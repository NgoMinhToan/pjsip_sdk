import type { HybridObject } from 'react-native-nitro-modules';

export interface SIPCall
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  id: number;
  accountId: number;
  localContact: string | null;
  localUri: string | null;
  remoteContact: string | null;
  remoteUri: string | null;
  state: number | null;
  stateText: string | null;
  held: boolean;
  muted: boolean;
  speaker: boolean;
  connectDuration: number | null;
  totalDuration: number | null;
  remoteOfferer: boolean | null;
  remoteNumber: string | null;
  remoteName: string | null;
  lastStatusCode: number | null;
  lastReason: string | null;
  constructionTime: bigint;
}
