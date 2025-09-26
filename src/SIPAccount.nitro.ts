import type { HybridObject } from 'react-native-nitro-modules';
import type { Transport } from './PjsipSdk.nitro';

export interface SIPAccount
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  id: number;
  uri: string | null;
  domain: string | null;
  proxy: string[];
  transport?: Transport;
  contactParams?: string;
  contactUriParams?: string;
  regServer?: string;
  regTimeout?: number;
  regContactParams?: string;
  regHeaders: Record<string, string>;
}
