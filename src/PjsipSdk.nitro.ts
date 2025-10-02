import type { HybridObject } from 'react-native-nitro-modules';
import type { SIPAccount } from './SIPAccount.nitro';
import type { SIPCall } from './SIPCall.nitro';

export interface PjsipSdk
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  multiply(a: number, b: number): number;

  firstSetup(): Promise<boolean>;

  requestPermission(): Promise<boolean>;

  createAccount(config: AccountConfigData): Promise<SIPAccount | undefined>;
  removeAccount(accountID: number): Promise<void>;
  getAccounts(): SIPAccount[];
  getAccount(accountID: number): SIPAccount | undefined;

  // Call action
  makeCall(accountID: number, uri: string): Promise<SIPCall | undefined>;
  endCall(callID: number): Promise<boolean>;
  answerCall(callID: number): Promise<void>;
  referCall(callID: number, uri: string): Promise<void>;
  holdCall(callID: number): Promise<void>;
  unHoldCall(callID: number): Promise<void>;
  toggleHold(callID: number): Promise<void>;
  useSpeaker(callID: number): Promise<void>;
  useEarpiece(callID: number): Promise<void>;
  toggleSpeaker(callID: number): Promise<void>;
  muteCall(callID: number): Promise<void>;
  unMuteCall(callID: number): Promise<void>;
  dtmfCall(callID: number): Promise<void>;

  // Call
  getCall(callID: number): SIPCall | undefined;
}

export enum Transport {
  TCP,
  UDP,
  TLS,
}

export type AccountConfigData = {
  username: string;
  password: string;
  domain: string;
  transport?: Transport;
};
