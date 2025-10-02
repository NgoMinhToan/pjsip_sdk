import { type HybridObject } from 'react-native-nitro-modules';

export type CALL_EVENT =
  | 'ON_CALL_STATE'
  | 'ON_CALL_MEDIA_STATE'
  | 'ON_CALL_MEDIA_EVENT';
export type ACCOUNT_EVENT = 'INCOMING_CALL';

type EventSubscription = {
  unsubscribe: () => void;
};

export interface SIPEventEmitter
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  onCallStateEvent(listener: (payload?: string) => void): EventSubscription;
  onCallMediaStateEvent(
    listener: (payload?: string) => void
  ): EventSubscription;
  onCallMediaEvent(listener: (payload?: string) => void): EventSubscription;
  onIncomingCallEvent(listener: (payload?: string) => void): EventSubscription;
}
