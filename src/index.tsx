import { NitroModules } from 'react-native-nitro-modules';
import type { PjsipSdk } from './PjsipSdk.nitro';

const PjsipSdkHybridObject =
  NitroModules.createHybridObject<PjsipSdk>('PjsipSdk');

export function multiply(a: number, b: number): number {
  return PjsipSdkHybridObject.multiply(a, b);
}
