package com.margelo.nitro.pjsipsdk.model

import com.margelo.nitro.pjsipsdk.Transport
import org.pjsip.pjsua2.AccountConfig
import org.pjsip.pjsua2.AccountInfo

data class AccountState (
  val id: Int? = null,
  val accountInfo: AccountInfo? = null,
  val accountConfig: AccountConfig? = null
)
