package com.margelo.nitro.pjsipsdk.manager

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.facebook.react.bridge.ReactApplicationContext
import com.margelo.nitro.pjsipsdk.model.PermissionState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

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
  private const val REQUEST_CODE = 2468


  private var pending: CompletableDeferred<PermissionState>? = null

  suspend fun requestPermissions(activity: Activity?, context: ReactApplicationContext): PermissionState {
    Log.d("PermissionManager", "requestPermissions")

    val current = checkPermissions(context)
    if (activity == null) {
      Log.w("PermissionManager", "Activity is null")
      return current
    }

    if (current.micGranted && current.cameraGranted) {
      Log.d("PermissionManager", "All permissions already granted")
      return current
    }

    pending?.cancel()

    val deferred = CompletableDeferred<PermissionState>()
    pending = deferred

    ActivityCompat.requestPermissions(
      activity,
      arrayOf(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
      ),
      REQUEST_CODE
    )

    // Đợi callback từ MainActivity
    return deferred.await()
  }

  /**
   * Gọi từ MainActivity khi nhận được callback
   */
  fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray,
    activity: Activity,
  ) {
    if (requestCode != REQUEST_CODE) return
    Log.d("PermissionManager", "onRequestPermissionsResult called")

    val current = checkPermissions(activity)
    val allGranted = current.micGranted && current.cameraGranted

    if (!allGranted) {
      Log.w("PermissionManager", "Permissions not granted fully: $current")

      // ⚠️ Kiểm tra nếu user chọn “Don’t ask again” → show dialog hướng dẫn
      val micRationale = ActivityCompat.shouldShowRequestPermissionRationale(
        activity,
        Manifest.permission.RECORD_AUDIO
      )
      val camRationale = ActivityCompat.shouldShowRequestPermissionRationale(
        activity,
        Manifest.permission.CAMERA
      )

      if (!micRationale || !camRationale) {
        showSettingsDialog(activity)
      } else {
        showRationaleDialog(activity)
      }
    }
    pending?.complete(current)
    pending = null
  }

  private fun showRationaleDialog(activity: Activity) {
    AlertDialog.Builder(activity)
      .setTitle("Yêu cầu quyền truy cập")
      .setMessage("Ứng dụng cần quyền micro và camera để hoạt động. Vui lòng cấp quyền để tiếp tục.")
      .setPositiveButton("Thử lại") { dialog, _ ->
        dialog.dismiss()
        // Gọi lại xin quyền
        ActivityCompat.requestPermissions(
          activity,
          arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
          ),
          REQUEST_CODE
        )
      }
      .setNegativeButton("Hủy", null)
      .show()
  }

  private fun showSettingsDialog(activity: Activity) {
    AlertDialog.Builder(activity)
      .setTitle("Cần cấp quyền thủ công")
      .setMessage("Bạn đã từ chối quyền và chọn 'Không hỏi lại'. Vui lòng mở cài đặt để cấp quyền thủ công.")
      .setPositiveButton("Mở cài đặt") { dialog, _ ->
        dialog.dismiss()
        openAppSettings(activity)
      }
      .setNegativeButton("Hủy", null)
      .show()
  }

  private fun openAppSettings(activity: Activity) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", activity.packageName, null)
    intent.data = uri
    activity.startActivity(intent)
  }

}
