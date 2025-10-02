package com.margelo.nitro.pjsipsdk

import android.util.Log
import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.NitroModules.Companion.applicationContext
import com.margelo.nitro.core.Promise
import com.margelo.nitro.pjsipsdk.manager.PermissionManager
import com.margelo.nitro.pjsipsdk.manager.SDKManager

@DoNotStrip
class PjsipSdk() : HybridPjsipSdkSpec() {
  val sdkManager = SDKManager

  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }

  override fun firstSetup(): Promise<Boolean> {
    val context = applicationContext
    return Promise.async {
      if (context == null) {
        throw Exception("Context is null")
      }
     return@async sdkManager.firstSetup(context)
    }
  }

  override fun requestPermission(): Promise<Boolean> {
    Log.d("PjsipSdk", "start function")
    val context = applicationContext
    return Promise.async {
      if (context == null) {
        throw Exception("Context is null")
      }
      val activity = context.currentActivity
      if (activity == null) {
        return@async false
      }
      val permission = PermissionManager.requestPermissions(context.currentActivity,context )
      return@async permission.cameraGranted && permission.micGranted
    }.catch { e ->
      e.printStackTrace()
    }
  }

  override fun createAccount(config: AccountConfigData): Promise<HybridSIPAccountSpec?> {
    return Promise.async {
      val acc = sdkManager.createAccount(config)
      return@async if (acc != null) HybridSIPAccount(acc) else null
    }
  }

  override fun removeAccount(accountID: Double): Promise<Unit> {
    return Promise.async {
      return@async sdkManager.destroyAccount(accountID.toInt())
    }
  }

  override fun getAccounts(): Array<HybridSIPAccountSpec> {
    val acc = sdkManager.current().acc
    return if (acc != null) arrayOf(HybridSIPAccount(acc)) else emptyArray()
  }

  override fun getAccount(accountID: Double): HybridSIPAccountSpec? {
    val acc = sdkManager.current().acc
    return if (acc != null) HybridSIPAccount(acc) else null
  }

  override fun makeCall(
    accountID: Double,
    uri: String
  ): Promise<HybridSIPCallSpec?> {
    return Promise.async {
      val call = sdkManager.makeCall(accountID, uri)
      return@async if (call != null) HybridSIPCall(call) else null
    }
  }

  override fun endCall(callID: Double): Promise<Boolean> {
    return Promise.async {
      return@async sdkManager.endCall(callID.toInt())
    }
  }

  override fun answerCall(callID: Double): Promise<Unit> {
    TODO("Not yet implemented")
  }

  override fun referCall(
    callID: Double,
    uri: String
  ): Promise<Unit> {
    TODO("Not yet implemented")
  }

  override fun holdCall(callID: Double): Promise<Unit> {
    TODO("Not yet implemented")
  }

  override fun unHoldCall(callID: Double): Promise<Unit> {
    TODO("Not yet implemented")
  }

  override fun toggleHold(callID: Double): Promise<Unit> {
    TODO("Not yet implemented")
  }

  override fun useSpeaker(callID: Double): Promise<Unit> {
    TODO("Not yet implemented")
  }

  override fun useEarpiece(callID: Double): Promise<Unit> {
    TODO("Not yet implemented")
  }

  override fun toggleSpeaker(callID: Double): Promise<Unit> {
    TODO("Not yet implemented")
  }

  override fun muteCall(callID: Double): Promise<Unit> {
    TODO("Not yet implemented")
  }

  override fun unMuteCall(callID: Double): Promise<Unit> {
    TODO("Not yet implemented")
  }

  override fun dtmfCall(callID: Double): Promise<Unit> {
    TODO("Not yet implemented")
  }

  override fun getCall(callID: Double): HybridSIPCallSpec {
    TODO("Not yet implemented")
  }
}
