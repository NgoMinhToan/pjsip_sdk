package com.margelo.nitro.pjsipsdk.pjsip

import com.margelo.nitro.pjsipsdk.manager.CallManager
import com.margelo.nitro.pjsipsdk.manager.SDKManager
import com.margelo.nitro.pjsipsdk.model.AccountState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.pjsip.pjsua2.Account
import org.pjsip.pjsua2.AccountConfig
import org.pjsip.pjsua2.CallOpParam
import org.pjsip.pjsua2.OnIncomingCallParam
import org.pjsip.pjsua2.pjsip_status_code

class MyAccount: Account() {
  private val _state = MutableStateFlow(AccountState())
  val state: StateFlow<AccountState> = _state.asStateFlow()
  fun current(): AccountState = _state.value
  private fun update(transform: (AccountState) -> AccountState) {
    _state.value = transform(_state.value)
  }

  override fun create(cfg: AccountConfig?) {
    super.create(cfg)
    if (cfg != null) {
      update { it.copy(accountConfig = cfg) }
    }
  }
  override fun onIncomingCall(prm: OnIncomingCallParam) {
    /* Auto answer with 200 for incoming calls  */
    val sdkState = SDKManager.current()
    val callManagerState = CallManager.current
    val call = MyCall(sdkState.acc, prm.callId)
    val ansPrm = CallOpParam(true)
    ansPrm.statusCode = if (callManagerState.activeCall == null) pjsip_status_code.PJSIP_SC_OK else pjsip_status_code.PJSIP_SC_BUSY_HERE
    try {
      call.answer(ansPrm)
      CallManager.addCall(call)
    } catch (e: Exception) {
      println(e)
    }
  }

}
