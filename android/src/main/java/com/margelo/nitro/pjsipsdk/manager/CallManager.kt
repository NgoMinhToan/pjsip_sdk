package com.margelo.nitro.pjsipsdk.manager

import android.util.Log
import com.margelo.nitro.pjsipsdk.pjsip.MyCall
import com.margelo.nitro.pjsipsdk.model.CallManagerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object CallManager {
  private val _state = MutableStateFlow<CallManagerState>(CallManagerState())
  val state: StateFlow<CallManagerState> = _state.asStateFlow()

  val current: CallManagerState
    get() = _state.value

  fun addCall(call: MyCall) {
    _state.update { it.copy(
      calls = it.calls + (call.id to call)
    ) }

    if (current.activeCall == null) {
      setCallActive(call)
    }
  }

  fun removeCall(callId: Int) {
    _state.update { it.copy(
      calls = it.calls - callId
    ) }

    if (current.activeCall?.id == callId) {
      setCallActive(null)
    }
  }

  fun setCallActive(call: MyCall?) {
    _state.update { it.copy(
      activeCall = call
    ) }
  }

  fun updateCall(call: MyCall) {
    _state.update { it.copy(
      calls = it.calls + (call.id to call)
    ) }
  }

  fun findCall(callId: Int): MyCall? {
    Log.d("CallManager", "findCall: $callId")
    Log.d("CallManager", "calls: ${_state.value.calls}")
    return _state.value.calls[callId]
  }
}
