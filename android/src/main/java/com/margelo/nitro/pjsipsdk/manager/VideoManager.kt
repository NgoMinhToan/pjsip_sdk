package com.margelo.nitro.pjsipsdk.manager

import android.view.SurfaceView
import android.view.View
import android.widget.RelativeLayout
import com.margelo.nitro.pjsipsdk.R
import com.margelo.nitro.pjsipsdk.data.VIDEO_CAPTURE_DEVICE_ID
import com.margelo.nitro.pjsipsdk.model.VideoHandlerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.pjsip.pjsua2.VideoPreview
import org.pjsip.pjsua2.VideoPreviewOpParam

object VideoManager {
  // private mutable state
  private val _state = MutableStateFlow(VideoHandlerState())
  // public immutable state (read-only)
  val state: StateFlow<VideoHandlerState> = _state.asStateFlow()

  /** Lấy state hiện tại */
  fun current(): VideoHandlerState = _state.value

  fun reset() {
    _state.value = VideoHandlerState()
  }

  fun disconnectCall() {
    current().localVideoHandler.resetVideoWindow()
    current().remoteVideoHandler.resetVideoWindow()
  }

  fun showLocalVideo() {
    try {
      /* Start preview, position it in the bottom right of the screen */
      val vp = VideoPreview(VIDEO_CAPTURE_DEVICE_ID)
      vp.start(VideoPreviewOpParam())

      val vwi = vp.videoWindow.info
      var w = vwi.size.w.toInt()
      var h = vwi.size.h.toInt()

      /* Adjust width to match the parent layout */
      /* Also adjust height to match the parent layout */
      /* Resize the preview surface */

      /* Link the video window to the surface view */
      current().localVideoHandler.setVideoWindow(vp.videoWindow)
    } catch (e: Exception) {
      println("Failed showing local video" + e.message)
    }
  }
}
