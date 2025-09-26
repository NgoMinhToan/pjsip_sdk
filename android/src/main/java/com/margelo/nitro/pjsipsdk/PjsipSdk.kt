package com.margelo.nitro.pjsipsdk

import com.facebook.proguard.annotations.DoNotStrip
import com.facebook.react.bridge.ReactApplicationContext
import com.margelo.nitro.core.Promise
import com.margelo.nitro.pjsipsdk.manager.PermissionManager
import com.margelo.nitro.pjsipsdk.manager.SDKManager

@DoNotStrip
class PjsipSdk(val context: ReactApplicationContext) : HybridPjsipSdkSpec() {
  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }

  override fun firstSetup(): Promise<Boolean> {
    return Promise.async {
     return@async SDKManager.firstSetup(context)
    }
  }

  override fun requestPermission(): Promise<Boolean> {
    return Promise.async {
      val activity = context.currentActivity
      if (activity == null) {
        return@async false
      }
      val permission = PermissionManager.requestPermissions(context.currentActivity)
      return@async permission.cameraGranted && permission.micGranted
    }
  }

  override fun createAccount(config: AccountConfig): Promise<String> {
    TODO("Not yet implemented")
  }

  override fun removeAccount(accountId: String) {
    TODO("Not yet implemented")
  }

  override fun getAccounts(): Array<HybridSIPAccountSpec> {
    return emptyArray()
  }

  override fun getAccount(accountId: String): HybridSIPAccountSpec {
    TODO("Not yet implemented")
  }

  override fun makeCall(
    accountId: String,
    uri: String
  ): Promise<String> {
    TODO("Not yet implemented")
  }

  override fun endCall(callId: String): Promise<Unit> {
    TODO("Not yet implemented")
  }

  override fun answerCall(callId: String): Promise<Unit> {
    TODO("Not yet implemented")
  }

  override fun referCall(
    callId: String,
    uri: String
  ): Promise<Unit> {
    TODO("Not yet implemented")
  }

  override fun holdCall(callId: String): Promise<Unit> {
    TODO("Not yet implemented")
  }

  override fun unHoldCall(callId: String): Promise<Unit> {
    TODO("Not yet implemented")
  }

  override fun toggleHold(callId: String): Promise<Unit> {
    TODO("Not yet implemented")
  }

  override fun useSpeaker(callId: String): Promise<Unit> {
    TODO("Not yet implemented")
  }

  override fun useEarpiece(callId: String): Promise<Unit> {
    TODO("Not yet implemented")
  }

  override fun toggleSpeaker(callId: String): Promise<Unit> {
    TODO("Not yet implemented")
  }

  override fun muteCall(callId: String): Promise<Unit> {
    TODO("Not yet implemented")
  }

  override fun unMuteCall(callId: String): Promise<Unit> {
    TODO("Not yet implemented")
  }

  override fun dtmfCall(callId: String): Promise<Unit> {
    TODO("Not yet implemented")
  }

  override fun getCall(callId: String): HybridSIPCallSpec {
    TODO("Not yet implemented")
  }
}
