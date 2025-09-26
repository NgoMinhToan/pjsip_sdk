package com.margelo.nitro.pjsipsdk.model

import org.pjsip.pjsua2.CallInfo

data class CallState (
  val constructionTime: Long = System.currentTimeMillis(),
  val hold: Boolean = false,
  val muted: Boolean = false,
  val speaker: Boolean = false,
  val callInfo: CallInfo? = null,
)
