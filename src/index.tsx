import { NitroModules } from 'react-native-nitro-modules';
import { type AccountConfigData, type PjsipSdk } from './PjsipSdk.nitro';
import type {
  ACCOUNT_EVENT,
  CALL_EVENT,
  SIPEventEmitter,
} from './SIPEventEmitter.nitro';
const PjsipSdkHybridObject =
  NitroModules.createHybridObject<PjsipSdk>('PjsipSdk');

export function multiply(a: number, b: number): number {
  return PjsipSdkHybridObject.multiply(a, b);
}

export async function firstSetup() {
  return await PjsipSdkHybridObject.firstSetup();
}

export async function requestPermission() {
  return await PjsipSdkHybridObject.requestPermission();
}

export async function createAccount(config: AccountConfigData) {
  return await PjsipSdkHybridObject.createAccount(config);
}

export async function removeAccount(id: number) {
  return await PjsipSdkHybridObject.removeAccount(id);
}

export async function makeCall(accountID: number, uri: string) {
  return PjsipSdkHybridObject.makeCall(accountID, uri);
}

export async function endCall(callID: number) {
  return PjsipSdkHybridObject.endCall(callID);
}

/**
 * EventEmitter
 */
const SIPEventEmitterHybridObject =
  NitroModules.createHybridObject<SIPEventEmitter>('SIPEventEmitter');

export function onCallEvent(
  type: CALL_EVENT,
  listener: (payload: any) => void
) {
  switch (type) {
    case 'ON_CALL_STATE':
      return SIPEventEmitterHybridObject.onCallStateEvent(listener);
    case 'ON_CALL_MEDIA_STATE':
      return SIPEventEmitterHybridObject.onCallMediaStateEvent(listener);
    case 'ON_CALL_MEDIA_EVENT':
      return SIPEventEmitterHybridObject.onCallMediaEvent(listener);
  }
}

export function onAccountEvent(
  type: ACCOUNT_EVENT,
  listener: (payload: any) => void
) {
  switch (type) {
    case 'INCOMING_CALL':
      return SIPEventEmitterHybridObject.onIncomingCallEvent(listener);
  }
}
