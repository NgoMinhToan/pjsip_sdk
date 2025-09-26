package com.margelo.nitro.pjsipsdk.model

import com.margelo.nitro.pjsipsdk.HybridSIPEventEmitter
import com.margelo.nitro.pjsipsdk.pjsip.MyAccount
import com.margelo.nitro.pjsipsdk.pjsip.MyLogWriter
import org.pjsip.pjsua2.Endpoint

data class SDKState(
  val logWriter: MyLogWriter? = null,
  val ep: Endpoint = Endpoint(),
  val acc: MyAccount = MyAccount(),
  val previewStarted: Boolean = false,
  val emitter: HybridSIPEventEmitter = HybridSIPEventEmitter(),
)
