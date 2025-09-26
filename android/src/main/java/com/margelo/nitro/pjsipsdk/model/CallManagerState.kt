package com.margelo.nitro.pjsipsdk.model

import com.margelo.nitro.pjsipsdk.pjsip.MyCall

data class CallManagerState (
  val calls: Map<Int, MyCall> = emptyMap(),
  val activeCall: MyCall? = null,
)
