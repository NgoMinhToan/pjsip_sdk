import { NitroModules } from 'react-native-nitro-modules';
import { type AccountConfigData, type PjsipSdk } from './PjsipSdk.nitro';
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
