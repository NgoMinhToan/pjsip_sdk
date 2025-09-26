import type { HybridObject } from 'react-native-nitro-modules';
import type { SIPAccount } from './SIPAccount.nitro';
import type { SIPCall } from './SIPCall.nitro';

export interface PjsipSdk
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  multiply(a: number, b: number): number;

  firstSetup(): Promise<boolean>;

  requestPermission(): Promise<boolean>;

  createAccount(config: AccountConfig): Promise<string>;
  removeAccount(accountId: string): void;
  getAccounts(): SIPAccount[];
  getAccount(accountId: string): SIPAccount;

  // Call action
  makeCall(accountId: string, uri: string): Promise<string>;
  endCall(callId: string): Promise<void>;
  answerCall(callId: string): Promise<void>;
  referCall(callId: string, uri: string): Promise<void>;
  holdCall(callId: string): Promise<void>;
  unHoldCall(callId: string): Promise<void>;
  toggleHold(callId: string): Promise<void>;
  useSpeaker(callId: string): Promise<void>;
  useEarpiece(callId: string): Promise<void>;
  toggleSpeaker(callId: string): Promise<void>;
  muteCall(callId: string): Promise<void>;
  unMuteCall(callId: string): Promise<void>;
  dtmfCall(callId: string): Promise<void>;

  // Call
  getCall(callId: string): SIPCall;
}

export enum Transport {
  UDP,
  TLS,
}

type AccountConfig = {
  username: string;
  password: string;
  domain: string;
  transport: Transport;
};
