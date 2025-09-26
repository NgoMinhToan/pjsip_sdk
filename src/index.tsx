import { getHostComponent, NitroModules } from 'react-native-nitro-modules';
import SIPVideoViewConfig from '../nitrogen/generated/shared/json/SIPVideoViewConfig.json';
import { type PjsipSdk } from './PjsipSdk.nitro';
import {
  type ACCOUNT_EVENT,
  type CALL_EVENT,
  type SIPEventEmitter,
} from './SIPEventEmitter.nitro';
import type {
  SIPVideoViewMethods,
  SIPVideoViewProps,
} from './SIPVideoView.nitro';

const PjsipSdkHybridObject =
  NitroModules.createHybridObject<PjsipSdk>('PjsipSdk');

export function multiply(a: number, b: number): number {
  return PjsipSdkHybridObject.multiply(a, b);
}

/**
 * SIPVideoView
 */
export const SIPVideoView = getHostComponent<
  SIPVideoViewProps,
  SIPVideoViewMethods
>('SIPVideoView', () => SIPVideoViewConfig);

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
