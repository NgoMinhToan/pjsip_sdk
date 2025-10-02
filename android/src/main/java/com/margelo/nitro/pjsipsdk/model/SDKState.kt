package com.margelo.nitro.pjsipsdk.model

import com.margelo.nitro.pjsipsdk.Transport
import com.margelo.nitro.pjsipsdk.pjsip.MyAccount
import com.margelo.nitro.pjsipsdk.pjsip.MyLogWriter
import org.pjsip.pjsua2.Endpoint
import org.pjsip.pjsua2.EpConfig

data class SDKState(
  val logWriter: MyLogWriter = MyLogWriter(),
  val ep: Endpoint = Endpoint(),
  val acc: MyAccount? = null,
  val previewStarted: Boolean = false,
  val epConfig: EpConfig = EpConfig(),
  val transportId: Map<Transport, Int?> = mapOf()
)
