package com.margelo.nitro.pjsipsdk

import com.margelo.nitro.pjsipsdk.manager.SDKManager
import com.margelo.nitro.pjsipsdk.pjsip.MyAccount
import com.margelo.nitro.pjsipsdk.utils.safeAccountInfo

class HybridSIPAccount(val account: MyAccount): HybridSIPAccountSpec() {
  override var id: Double
    get() = account.current().id?.toDouble() ?: account.id.toDouble()
    set(value) {}
  override var uri: String?
    get() = account.current().accountInfo?.uri ?: account.safeAccountInfo?.uri
    set(value) {}
  override var domain: String?
    get() = account.current().accountConfig?.regConfig?.registrarUri
    set(value) {}
  override var proxy: Array<String>
    get() = account.current().accountConfig
      ?.sipConfig
      ?.proxies
      ?.let { vec -> Array(vec.size) { i -> vec[i] } }
      ?: emptyArray()
    set(value) {}
  override var contactParams: String?
    get() = account.current().accountConfig?.regConfig?.contactParams
    set(value) {}
  override var contactUriParams: String?
    get() = account.current().accountConfig?.regConfig?.contactUriParams
    set(value) {}
  override var regServer: String?
    get() = account.current().accountConfig?.regConfig?.registrarUri
    set(value) {}
  override var regTimeout: Double?
    get() = account.current().accountConfig?.regConfig?.timeoutSec?.toDouble()
    set(value) {}
  override var regContactParams: String?
    get() = account.current().accountConfig?.regConfig?.contactParams
    set(value) {}
  override var regHeaders: Map<String,String>
    get() = account.current().accountConfig?.regConfig?.headers
      ?.fold(emptyMap()) { header, vec -> header + (vec.hName to vec.hValue) }
      ?: emptyMap()
    set(value) {}
}
