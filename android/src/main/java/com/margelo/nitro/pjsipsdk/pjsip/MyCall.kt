package com.margelo.nitro.pjsipsdk.pjsip

import android.content.Context
import android.media.AudioManager
import android.os.Message
import android.util.Log
import com.margelo.nitro.NitroModules.Companion.applicationContext
import com.margelo.nitro.pjsipsdk.data.VIDEO_CAPTURE_DEVICE_ID
import com.margelo.nitro.pjsipsdk.manager.CallManager
import com.margelo.nitro.pjsipsdk.manager.SDKManager
//import com.margelo.nitro.pjsipsdk.manager.VideoManager
import com.margelo.nitro.pjsipsdk.model.CallState
import com.margelo.nitro.pjsipsdk.utils.safeCallInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.pjsip.pjsua2.Account
import org.pjsip.pjsua2.Call
import org.pjsip.pjsua2.CallInfo
import org.pjsip.pjsua2.CallOpParam
import org.pjsip.pjsua2.OnCallMediaEventParam
import org.pjsip.pjsua2.OnCallMediaStateParam
import org.pjsip.pjsua2.OnCallStateParam
import org.pjsip.pjsua2.VideoPreview
import org.pjsip.pjsua2.pjmedia_dir
import org.pjsip.pjsua2.pjmedia_event_type
import org.pjsip.pjsua2.pjmedia_type
import org.pjsip.pjsua2.pjsip_inv_state
import org.pjsip.pjsua2.pjsua2
import org.pjsip.pjsua2.pjsua_call_media_status

class MyCall(val acc: Account, val call_id: Int) : Call(acc, call_id) {
  private val _state = MutableStateFlow(CallState())
  val state: StateFlow<CallState> = _state.asStateFlow()
  fun current(): CallState = _state.value
  private fun update(transform: (CallState) -> CallState) {
    _state.value = transform(_state.value)
  }
  companion object {
    val TAG = "MyCall"
  }

  override fun onCallState(prm: OnCallStateParam?) {
    val ci : CallInfo = this.safeCallInfo ?: return
    update { it.copy(callInfo = ci) }
    SDKManager.callStateChangeCallBack(this)
  }

  override fun setHold(prm: CallOpParam?) {
    super.setHold(prm)
    update { it.copy(hold = !it.hold) }
  }

  override fun makeCall(dst_uri: String?, prm: CallOpParam?) {
    super.makeCall(dst_uri, prm)
  }
  override fun answer(prm: CallOpParam?) {
    super.answer(prm)
  }
  override fun hangup(prm: CallOpParam?) {
    super.hangup(prm)
  }

  override fun onCallMediaState(prm: OnCallMediaStateParam?) {
    val ci : CallInfo = this.safeCallInfo ?: return
    val sdkState = SDKManager.current()
    val adm = sdkState.ep.audDevManager()
    val cmiv = ci.media
    for (i in cmiv.indices) {
      val cmi = cmiv[i]
      if (cmi.type == pjmedia_type.PJMEDIA_TYPE_AUDIO &&
        cmi.status == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE)
      {
        Log.d(TAG, "Audio media[$i]: type=${cmi.type}, status=${cmi.status}, dir=${cmi.dir}")
        /* Connect ports */
        try {
          val am = getAudioMedia(i)
          am.adjustRxLevel(1.5f)
          am.adjustTxLevel(1.5f)
          adm.captureDevMedia.startTransmit(am)
          am.startTransmit(adm.playbackDevMedia)
        } catch (e: Exception) {
          Log.e(TAG, "Failed connecting media ports: ${e.message}")
        }
      } else if ((cmi.type == pjmedia_type.PJMEDIA_TYPE_VIDEO) &&
        (cmi.status == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE) &&
        (cmi.dir and pjmedia_dir.PJMEDIA_DIR_ENCODING) != 0)
      {
        // TODO: Handle remote video
      }
    }
  }

  override fun onCallMediaEvent(prm: OnCallMediaEventParam?) {
    if (prm!!.ev.type == pjmedia_event_type.PJMEDIA_EVENT_FMT_CHANGED) {
      val ci : CallInfo = this.safeCallInfo ?: return
      if (prm.medIdx < 0 || prm.medIdx >= ci.media.size)
        return

      /* Check if this event is from incoming video */
      val cmi = ci.media[prm.medIdx.toInt()]
      if (cmi.type != pjmedia_type.PJMEDIA_TYPE_VIDEO ||
        cmi.status != pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE ||
        cmi.videoIncomingWindowId == pjsua2.INVALID_ID)
        return

      /* Currently this is a new incoming video */
      val sdkState = SDKManager.current()
      println("Got remote video format change = " +prm.ev.data.fmtChanged.newWidth + "x" + prm.ev.data.fmtChanged.newHeight)
      // TODO: Handle remote video
    }
  }

}
