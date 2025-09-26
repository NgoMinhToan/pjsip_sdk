package com.margelo.nitro.pjsipsdk.pjsip

import com.margelo.nitro.pjsipsdk.data.VIDEO_CAPTURE_DEVICE_ID
import com.margelo.nitro.pjsipsdk.manager.CallManager
import com.margelo.nitro.pjsipsdk.manager.SDKManager
import com.margelo.nitro.pjsipsdk.manager.VideoManager
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

  override fun onCallState(prm: OnCallStateParam?) {
    val ci : CallInfo = this.safeCallInfo ?: return
    update { it.copy(callInfo = ci) }
    val sdkState = SDKManager.current()
    sdkState.ep.utilLogWrite(3, "MyCall", "Call state changed to: " + ci.stateText)

    update { it.copy(callInfo = ci) }

    if (ci.state == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
      CallManager.removeCall(call_id)
      VideoManager.disconnectCall()
      if (sdkState.previewStarted) {
        try {
          VideoPreview(VIDEO_CAPTURE_DEVICE_ID).stop()
        } catch (e: Exception) {
          println("Failed stopping video preview" + e.message)
        }
        SDKManager.setPreviewStarted(false)
      }
      sdkState.ep.utilLogWrite(3, "MyCall", this.dump(true, ""))
    }
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
    val cmiv = ci.media
    for (i in cmiv.indices) {
      val cmi = cmiv[i]
      if (cmi.type == pjmedia_type.PJMEDIA_TYPE_AUDIO &&
        (cmi.status == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE ||
          cmi.status == pjsua_call_media_status.PJSUA_CALL_MEDIA_REMOTE_HOLD))
      {
        /* Connect ports */
        try {
          val am = getAudioMedia(i)
          sdkState.ep.audDevManager().captureDevMedia.startTransmit(am)
          am.startTransmit(sdkState.ep.audDevManager().playbackDevMedia)
        } catch (e: Exception) {
          println("Failed connecting media ports" + e.message)
        }
      } else if ((cmi.type == pjmedia_type.PJMEDIA_TYPE_VIDEO) &&
        (cmi.status == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE) &&
        (cmi.dir and pjmedia_dir.PJMEDIA_DIR_ENCODING) != 0)
      {
        // TODO: Handle local view
//        val m = Message.obtain(sdkState.uiHandler, MSG_SHOW_LOCAL_VIDEO, cmi)
//        m.sendToTarget()
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
//      val m = Message.obtain(sdkState.uiHandler, MSG_SHOW_REMOTE_VIDEO, cmi)
//      m.sendToTarget()
    }
  }

}
