package com.margelo.nitro.pjsipsdk

import com.margelo.nitro.pjsipsdk.pjsip.MyCall
import com.margelo.nitro.pjsipsdk.utils.remoteName
import com.margelo.nitro.pjsipsdk.utils.remoteNumber
import com.margelo.nitro.pjsipsdk.utils.safeCallInfo

class HybridSIPCall (val call: MyCall): HybridSIPCallSpec() {
  override var id: Double
    get() = call.call_id.toDouble()
    set(value) {}
  override var accountId: Double
    get() = call.acc.id.toDouble()
    set(value) {}
  override var localContact: String?
    get() = call.safeCallInfo?.localContact
    set(value) {}
  override var localUri: String?
    get() = call.safeCallInfo?.localUri
    set(value) {}
  override var remoteContact: String?
    get() = call.safeCallInfo?.remoteContact
    set(value) {}
  override var remoteUri: String?
    get() = call.safeCallInfo?.remoteUri
    set(value) {}
  override var state: Double?
    get() = call.safeCallInfo?.state?.toDouble()
    set(value) {}
  override var stateText: String?
    get() = call.safeCallInfo?.stateText
    set(value) {}
  override var held: Boolean
    get() = call.current().hold
    set(value) {}
  override var muted: Boolean
    get() = call.current().muted
    set(value) {}
  override var speaker: Boolean
    get() = call.current().speaker
    set(value) {}
  override var connectDuration: Double?
    get() = call.safeCallInfo?.connectDuration?.msec?.toDouble()
    set(value) {}
  override var totalDuration: Double?
    get() = call.safeCallInfo?.totalDuration?.msec?.toDouble()
    set(value) {}
  override var remoteOfferer: Boolean?
    get() = call.safeCallInfo?.remOfferer
    set(value) {}
  override var remoteNumber: String?
    get() = call.remoteNumber
    set(value) {}
  override var remoteName: String?
    get() = call.remoteName
    set(value) {}
  override var lastStatusCode: Double?
    get() = call.safeCallInfo?.lastStatusCode?.toDouble()
    set(value) {}
  override var lastReason: String?
    get() = call.safeCallInfo?.lastReason
    set(value) {}
  override var constructionTime: Long
    get() = call.current().constructionTime
    set(value) {}
}
