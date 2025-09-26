package com.margelo.nitro.pjsipsdk.utils

import org.pjsip.pjsua2.Account
import org.pjsip.pjsua2.AccountInfo

val Account.safeAccountInfo: AccountInfo?
  get() = try {
    this.info
  } catch (e: Exception) {
    println("Failed getting account info: $e")
    null
  }
