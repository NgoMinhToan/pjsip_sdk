package com.margelo.nitro.pjsipsdk.manager

import android.Manifest
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.margelo.nitro.core.Promise
import com.margelo.nitro.pjsipsdk.model.PermissionState
import com.margelo.nitro.pjsipsdk.model.SDKState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.jvm.Throws

object PermissionManager {
  private val _state = MutableStateFlow(PermissionState())
  // public immutable state (read-only)
  val state: StateFlow<PermissionState> = PermissionManager._state.asStateFlow()

  /** Lấy state hiện tại */
  fun current(): PermissionState = PermissionManager._state.value

  /** Update toàn bộ state */
  private fun update(transform: (PermissionState) -> PermissionState) {
    PermissionManager._state.value = transform(PermissionManager._state.value)
  }

  fun checkPermissions(context: Context): PermissionState {
    update { it.copy(
      micGranted = ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.RECORD_AUDIO
      ) == PermissionChecker.PERMISSION_GRANTED,
      cameraGranted = ContextCompat.checkSelfPermission(
        context,
      Manifest.permission.CAMERA
      ) == PermissionChecker.PERMISSION_GRANTED,
      )
    }
    return _state.value
  }

  /**
   * Gọi hàm này từ một ComponentActivity (hoặc Fragment)
   */
  suspend fun requestPermissions(activity: Activity?): PermissionState =
    suspendCoroutine { cont ->
      val compActivity = activity as? ComponentActivity
      if (compActivity == null) {
        Log.d("PermissionManager", "Activity is not a ComponentActivity")
        cont.resume(_state.value) // trả ngay state hiện tại
        return@suspendCoroutine
      }

      val launcher = compActivity.activityResultRegistry.register(
        "requestPermissions",
        ActivityResultContracts.RequestMultiplePermissions()
      ) { result ->
        update {
          it.copy(
            micGranted = result[Manifest.permission.RECORD_AUDIO] == true,
            cameraGranted = result[Manifest.permission.CAMERA] == true
          )
        }
        cont.resume(_state.value) // resume coroutine
      }

      launcher.launch(
        arrayOf(
          Manifest.permission.RECORD_AUDIO,
          Manifest.permission.CAMERA
        )
      )
    }
}
