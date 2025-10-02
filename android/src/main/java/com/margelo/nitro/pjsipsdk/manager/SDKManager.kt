package com.margelo.nitro.pjsipsdk.manager

import android.content.Context
import android.content.Context.CAMERA_SERVICE
import android.hardware.camera2.CameraManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import com.facebook.react.bridge.ReactApplicationContext
import com.margelo.nitro.NitroModules.Companion.applicationContext
import com.margelo.nitro.pjsipsdk.AccountConfigData
import com.margelo.nitro.pjsipsdk.Transport
import com.margelo.nitro.pjsipsdk.data.SIP_LISTENING_PORT
import com.margelo.nitro.pjsipsdk.data.VIDEO_CAPTURE_DEVICE_ID
import com.margelo.nitro.pjsipsdk.model.SDKState
import com.margelo.nitro.pjsipsdk.pjsip.MyAccount
import com.margelo.nitro.pjsipsdk.pjsip.MyCall
import com.margelo.nitro.pjsipsdk.utils.safeAccountInfo
import com.margelo.nitro.pjsipsdk.utils.safeCallInfo
import kotlinx.coroutines.android.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.pjsip.PjCameraInfo2
import org.pjsip.pjsua2.AccountConfig
import org.pjsip.pjsua2.AuthCredInfo
import org.pjsip.pjsua2.CallInfo
import org.pjsip.pjsua2.CallOpParam
import org.pjsip.pjsua2.StringVector
import org.pjsip.pjsua2.TransportConfig
import org.pjsip.pjsua2.VideoPreview
import org.pjsip.pjsua2.pj_log_decoration
import org.pjsip.pjsua2.pj_qos_type
import org.pjsip.pjsua2.pjsip_inv_state
import org.pjsip.pjsua2.pjsip_transport_type_e
import org.pjsip.pjsua2.pjsua_state

object PjsipThread {
  private val handlerThread = HandlerThread("PJSIP-Thread").apply { start() }
  val handler = Handler(handlerThread.looper)
  val dispatcher = handler.asCoroutineDispatcher()

  suspend fun <T> runOnPjsipThread(block: suspend () -> T): T =
    withContext(dispatcher) {
      val ep = SDKManager.current().ep
      val state = ep.libGetState()
      try {
        if (state >= pjsua_state.PJSUA_STATE_INIT && !ep.libIsThreadRegistered()) {
          ep.libRegisterThread(handlerThread.name)
        }
      } catch (_: Exception) {}
      block()
    }
}





object SDKManager {
  val TAG = "SDKManager"

  /** State Management */
  private val _state = MutableStateFlow(SDKState())
  val state: StateFlow<SDKState> = _state.asStateFlow()
  fun current(): SDKState = _state.value
  private fun update(transform: (SDKState) -> SDKState) {
    _state.value = transform(_state.value)
  }

  fun setPreviewStarted(started: Boolean) {
    update { it.copy(previewStarted = started) }
  }

  fun reset() {
    _state.value = SDKState()
  }


  init {
    setupEpConfig()

    setupLogger()
  }


  /**
   * First Setup Method
   */
  suspend fun firstSetup(context: ReactApplicationContext): Boolean {
    try {
      Log.d(TAG, "firstSetup")
      val activity = context.currentActivity
      if (activity == null) {
        Log.e(TAG, "Activity null")
        return false
      }

      /* Check permissions */
      PermissionManager.requestPermissions(activity, context).apply {
        if (!cameraGranted || !micGranted) {
          Log.e(TAG, "Permission Rejected")
          return false
        }
      }

      /* Set Camera Manager for PJMEDIA video */
      (activity.getSystemService(CAMERA_SERVICE) as CameraManager).let {
        PjCameraInfo2.SetCameraManager(it)
      }

      initPJSUA2()
      val transportIds = mapOf(
        Transport.TCP to createTransport(Transport.TCP),
        Transport.UDP to createTransport(Transport.UDP),
        Transport.TLS to createTransport(Transport.TLS)
      )
      update { it.copy(transportId = transportIds) }
      startPJSUA2()

      return true
    } catch (e: Exception) {
      Log.e(TAG, e.message.toString())
      return false
    }
  }

  /**
   * Create Account Method
   */
  suspend fun createAccount(config: AccountConfigData): MyAccount? = PjsipThread.runOnPjsipThread {
    Log.d(TAG, "createAccount: $config")
    try {
      if (current().ep.libGetState() != pjsua_state.PJSUA_STATE_RUNNING) {
        Log.d(TAG, "Lib not running")
        return@runOnPjsipThread null
      }

      val preAcc = current().acc
      if (preAcc != null) {
        Log.d(TAG, "Account existed!")
        return@runOnPjsipThread preAcc
      }

      val acc = MyAccount()
      val accIdUri = "sip:${config.username}@${config.domain}"
      val accRegistrar = "sip:${config.domain}"
      val accUser = config.username
      val accPasswd = config.password

      val transportIds = current().transportId
      val targetTransportId = when (config.transport) {
        Transport.TCP -> transportIds[Transport.TCP]
        Transport.UDP -> transportIds[Transport.UDP]
        Transport.TLS -> transportIds[Transport.TLS]
        else -> transportIds[Transport.TCP]
      }
      val accCfg = AccountConfig().apply {
        if (targetTransportId != null){
          sipConfig.transportId = targetTransportId
          sipConfig.proxies = StringVector(listOf("$accRegistrar;transport=tcp"))
        }
        mediaConfig.transportConfig.qosType = pj_qos_type.PJ_QOS_TYPE_VOICE
        natConfig.iceEnabled = true
        natConfig.turnEnabled = false
        idUri = accIdUri
        regConfig.registrarUri = accRegistrar
        sipConfig.authCreds.add(AuthCredInfo("digest", "*", accUser, 0, accPasswd))
        videoConfig.autoShowIncoming = true
        videoConfig.autoTransmitOutgoing = true
        videoConfig.defaultCaptureDevice = VIDEO_CAPTURE_DEVICE_ID
      }
      acc.create(accCfg, true)
      update { it.copy(acc = acc) }

      return@runOnPjsipThread acc
    } catch (e: Exception) {
      Log.e(TAG, e.message.toString())
      return@runOnPjsipThread null
    }
  }

  /**
   * Remove Account Method
   */
  suspend fun destroyAccount(accountID: Int) = PjsipThread.runOnPjsipThread {
    Log.d(TAG, "destroyAccount")
    try {
      val ep = current().ep
      if (ep.libGetState() != pjsua_state.PJSUA_STATE_RUNNING) {
        Log.e(TAG, "Lib not running")
        return@runOnPjsipThread
      }
      ep.hangupAllCalls()
       current().acc?.apply {
         shutdown()
         delete()
       }
       update { it.copy(acc = null) }
    } catch (e: Exception) {
      Log.e(TAG, e.message.toString())
    }
  }

  /**
   * Make Call Method
   */
  suspend fun makeCall(accountID: Double, uri: String): MyCall? = PjsipThread.runOnPjsipThread {
    val ep = current().ep
    val acc = current().acc ?: return@runOnPjsipThread null
    val callManager = CallManager
    Log.d(TAG, "makeCall")

    if (ep.libGetState() != pjsua_state.PJSUA_STATE_RUNNING) {
      Log.e(TAG, "Lib not running")
      return@runOnPjsipThread null
    }

    // Tạo URI đích
    val domain = acc.safeAccountInfo?.uri?.substringAfter("@") ?: return@runOnPjsipThread null
    val dstUri = "sip:$uri@$domain"

    try {
      if (callManager.current.activeCall == null) {

        prepareAudioDevices()

        // Tạo và thực hiện cuộc gọi
        val call = MyCall(acc, -1)
        val prm = CallOpParam(true)
        call.makeCall(dstUri, prm)
        callManager.addCall(call)

        return@runOnPjsipThread call
      } else {
        ep.hangupAllCalls()
        return@runOnPjsipThread null
      }
    } catch (e: Exception) {
      Log.e(TAG, e.message.toString())
      return@runOnPjsipThread null
    }
  }

  /**
   * End Call Method
   */
  suspend fun endCall(callID: Int): Boolean = PjsipThread.runOnPjsipThread {
    Log.d(TAG, "endCall")
    val call = CallManager.findCall(callID) ?: return@runOnPjsipThread false
    call.let {
      it.hangup(CallOpParam())
      CallManager.removeCall(it.id)
    }
    return@runOnPjsipThread true
  }


  /**
   * All Call Method
   */
  fun callStateChangeCallBack(call: MyCall) {
    val context = applicationContext ?: return
    val ci = call.safeCallInfo ?: return
    val ep = current().ep
    try {
      val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

      ep.utilLogWrite(1, "MyCall", "Call state changed to: " + ci.stateText)
      ep.utilLogWrite(1, "MyCall", call.dump(true, ""))

      when (ci.state) {
        // ✅ Bắt đầu call
        pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED -> {
          audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
          audioManager.isMicrophoneMute = false
          setSpeakerphone(context, true)

        }

        // ✅ Kết thúc call
        pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED -> {
          CallManager.removeCall(ci.id)
          audioManager.mode = AudioManager.MODE_NORMAL
          setSpeakerphone(context, false)

  //        VideoManager.disconnectCall()
  //        if (current().previewStarted) {
  //          try {
  //            VideoPreview(VIDEO_CAPTURE_DEVICE_ID).stop()
  //          } catch (e: Exception) {
  //            Log.e(MyCall.Companion.TAG, "Failed stopping video preview: ${e.message}")
  //          }
  //          setPreviewStarted(false)
  //        }
        }
      }
    } catch (e: Exception) {
      Log.e(TAG, e.message.toString())
    }
  }

  private fun setSpeakerphone(context: Context, enable: Boolean) {
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      // Android 12+
      if (enable) {
        val devices = audioManager.availableCommunicationDevices
        val preferredDevice = devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_SCO }
          ?: devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP }
          ?: devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BUILTIN_EARPIECE }
          ?: devices.firstOrNull { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }

        preferredDevice?.let { audioManager.setCommunicationDevice(it) }
      } else {
        audioManager.clearCommunicationDevice()
      }
    } else {
        audioManager.isSpeakerphoneOn = enable
    }
  }




  /**
   * Create & init PJSUA2
   */
  private suspend fun initPJSUA2 () = PjsipThread.runOnPjsipThread {
    try {
      Log.d(TAG, "initPJSUA2")
      val ep = current().ep
      if (ep.libGetState() > pjsua_state.PJSUA_STATE_NULL) {
        Log.e(TAG, "Lib init-ed")
        return@runOnPjsipThread
      } else if (ep.libGetState() == pjsua_state.PJSUA_STATE_NULL) {
        val epConfig = current().epConfig
        ep.apply {
          libCreate()
          libInit(epConfig)
        }
      }
    } catch (e: Exception) {
      Log.e(TAG, e.message.toString())
    }
  }

  /**
   *  Start PJSUA2
   */
  private suspend fun startPJSUA2 () = PjsipThread.runOnPjsipThread {
    try {
      Log.d(TAG, "startPJSUA2")
      val ep = current().ep
      if (ep.libGetState() >= pjsua_state.PJSUA_STATE_RUNNING) {
        Log.e(TAG, "Lib is running")
        return@runOnPjsipThread
      } else if (ep.libGetState() < pjsua_state.PJSUA_STATE_INIT) {
        Log.e(TAG, "Lib not init")
        return@runOnPjsipThread
      } else if (ep.libGetState() == pjsua_state.PJSUA_STATE_INIT) {
        ep.apply {
//          codecSetPriority("PCMU/8000/1", 255.toShort())
//          codecSetPriority("PCMA/8000/1", 254.toShort())
//          codecSetPriority("GSM/8000/1", 200.toShort())
//          codecSetPriority("speex/8000/1", 180.toShort())
//          codecEnum2().forEach {
//            codecSetPriority(it.codecId, it.priority) // enable all
//          }
          libStart()
        }
      }
    } catch (e: Exception) {
      Log.e(TAG, e.message.toString())
    }
  }

  /**
   * Restart PJSUA2
   */
  private suspend fun restartPJSUA2 () = PjsipThread.runOnPjsipThread {
    try {
      Log.d(TAG, "restartPJSUA2")
      val epConfig = current().epConfig
      current().ep.apply {
        hangupAllCalls()
        libDestroy()
        libInit(epConfig)
      }
    } catch (e: Exception) {
      Log.e(TAG, e.message.toString())
    }
  }

  /**
   * Set Transport Method
   */
  private suspend fun createTransport (transport: Transport): Int? = PjsipThread.runOnPjsipThread{
    Log.d(TAG, "createTransport")
    val ep = current().ep
    if (ep.libGetState() < pjsua_state.PJSUA_STATE_INIT) {
      Log.e(TAG, "Lib not init")
      return@runOnPjsipThread null
    }
    val sipTpConfig = TransportConfig()

    sipTpConfig.qosType = pj_qos_type.PJ_QOS_TYPE_VOICE
    sipTpConfig.port = SIP_LISTENING_PORT.toLong()
    val transportType = when(transport) {
      Transport.TCP -> pjsip_transport_type_e.PJSIP_TRANSPORT_TCP
      Transport.TLS -> pjsip_transport_type_e.PJSIP_TRANSPORT_TLS
      Transport.UDP -> pjsip_transport_type_e.PJSIP_TRANSPORT_UDP
    }
    return@runOnPjsipThread ep.transportCreate(transportType, sipTpConfig)
  }

  /**
   * Setup Logger
   */
  private fun setupLogger() {
    Log.d(TAG, "setupLogger")
    val epConfig = current().epConfig
    val logWriter = current().logWriter
    epConfig.logConfig.apply {
      level = 1
      consoleLevel = 1
      writer = logWriter
      decor = decor and
        (pj_log_decoration.PJ_LOG_HAS_CR or
          pj_log_decoration.PJ_LOG_HAS_NEWLINE).inv().toLong()
    }
  }

  /**
   * Setup EP Config
   */
  private fun setupEpConfig() {
    Log.d(TAG, "setupEpConfig")
    val ep = current().ep
    current().epConfig.apply {
      uaConfig.apply {
        userAgent = "android.WF4biz.Worldfone.vn ("+ ep.libVersion().full +") - omnicxm_android"
        stunServer = StringVector().apply {
          add("stun.l.google.com:19302")
        }
        maxCalls = 1
      }
      medConfig.apply {
        hasIoqueue = true
        hasIoqueue = true
        clockRate = 8000
        quality = 4
        ecOptions = 1
        ecTailLen = 200
        threadCnt = 2
        noVad = true
      }
    }
  }

  /**
   * Prepare Audio Devices
   */
  private fun prepareAudioDevices() {
    val adm = current().ep.audDevManager()

//    adm.setNullDev()
    adm.inputLatency = 0
    adm.captureDev = -1
    adm.playbackDev = -1

    // (tùy chọn) log danh sách thiết bị
    adm.enumDev2().forEachIndexed { i, info ->
      Log.d(TAG, "AudioDev[$i]: ${info.name}, in=${info.inputCount}, out=${info.outputCount}")
    }
  }
}
