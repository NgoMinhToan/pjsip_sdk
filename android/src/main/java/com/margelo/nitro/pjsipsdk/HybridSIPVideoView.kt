package com.margelo.nitro.pjsipsdk

import android.view.SurfaceView
import android.view.View
import com.facebook.jni.HybridData
import com.facebook.react.uimanager.ThemedReactContext
import com.margelo.nitro.pjsipsdk.manager.SDKManager
import com.margelo.nitro.pjsipsdk.manager.VideoManager

class HybridSIPVideoView(val context: ThemedReactContext) : HybridSIPVideoViewSpec() {
  // Props
  override var enableFlash: Boolean = false
  override var width: Double? = null
  override var height: Double? = null
  override var type: SIPVideoViewType = SIPVideoViewType.REMOTE

  // View
  override val view: View = View(context)

  override fun updateNative(hybridData: HybridData) {
    super.updateNative(hybridData)
  }

  override fun beforeUpdate() {
    super.beforeUpdate()
    val sv = view as SurfaceView
    val state = VideoManager.current()
    when (type) {
      SIPVideoViewType.LOCAL -> {
        state.localVideoHandler.attach(sv.holder)
      }
      SIPVideoViewType.REMOTE -> {
        state.remoteVideoHandler.attach(sv.holder)
      }
    }
  }
  override fun afterUpdate() {
    super.afterUpdate()
  }
}
