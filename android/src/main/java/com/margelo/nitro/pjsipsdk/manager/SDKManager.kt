package com.margelo.nitro.pjsipsdk.manager

import android.content.Context.CAMERA_SERVICE
import android.hardware.camera2.CameraManager
import com.facebook.react.bridge.ReactApplicationContext
import com.margelo.nitro.pjsipsdk.Transport
import com.margelo.nitro.pjsipsdk.model.SDKState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.pjsip.PjCameraInfo2
import org.pjsip.pjsua2.pjsip_transport_type_e

object SDKManager {
  // private mutable state
  private val _state = MutableStateFlow(SDKState())
  // public immutable state (read-only)
  val state: StateFlow<SDKState> = _state.asStateFlow()

  /** Lấy state hiện tại */
  fun current(): SDKState = _state.value

  /** Update toàn bộ state */
  private fun update(transform: (SDKState) -> SDKState) {
    _state.value = transform(_state.value)
  }

  fun setPreviewStarted(started: Boolean) {
    update { it.copy(previewStarted = started) }
  }

  fun reset() {
    _state.value = SDKState()
  }




  // Call Handler
  suspend fun firstSetup(context: ReactApplicationContext): Boolean {
    try {
      val activity = context.currentActivity
      if (activity == null) return false

      /* Check permissions */
      val permissions = PermissionManager.requestPermissions(activity)
      if (!permissions.cameraGranted || !permissions.micGranted) return false

      /* Set Camera Manager for PJMEDIA video */
      val cm = activity.getSystemService(CAMERA_SERVICE) as CameraManager
      PjCameraInfo2.SetCameraManager(cm)

      return true
    } catch (e: Exception) {
      e.printStackTrace()
      return false
    }
  }

  fun getTransport(transportID: Int?): Transport? {
    if (transportID == null) return null
    val transportType = current().ep.transportGetInfo(transportID)?.type
    return when (transportType) {
      pjsip_transport_type_e.PJSIP_TRANSPORT_UDP -> Transport.UDP
      pjsip_transport_type_e.PJSIP_TRANSPORT_TLS -> Transport.TLS
      else -> null
    }
  }
}
