package com.margelo.nitro.pjsipsdk.utils

import org.pjsip.pjsua2.Call
import org.pjsip.pjsua2.CallInfo

val Call.safeCallInfo: CallInfo?
  get() = try {
    this.info
  } catch (e: Exception) {
    println("Failed getting call info: $e")
    null
  }

val Call.remoteName: String?
  get() {
    val remoteUri = this.safeCallInfo?.remoteUri ?: return null
    val regex = Regex("\"([^\"]+)\" <sip:([^@]+)@")
    val match = regex.find(remoteUri)
    return match?.groups?.get(1)?.value
  }

val Call.remoteNumber: String?
  get() {
    val remoteUri = this.safeCallInfo?.remoteUri ?: return null
    // Ưu tiên parse kiểu `"Name" <sip:number@...>`
    val regex = Regex("\"([^\"]+)\" <sip:([^@]+)@")
    var match = regex.find(remoteUri)
    if (match != null) {
      return match.groups[2]?.value
    }
    // Nếu không match thì fallback kiểu `sip:number@...`
    match = Regex("sip:([^@]+)@").find(remoteUri)
    return match?.groups[1]?.value
  }
