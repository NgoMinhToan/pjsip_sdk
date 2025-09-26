import type {
  HybridView,
  HybridViewProps,
  HybridViewMethods,
} from 'react-native-nitro-modules';

enum SIPVideoViewType {
  LOCAL,
  REMOTE,
}
export interface SIPVideoViewProps extends HybridViewProps {
  enableFlash: boolean;
  width?: number;
  height?: number;
  type: SIPVideoViewType;
}
export interface SIPVideoViewMethods extends HybridViewMethods {}

export type SIPVideoView = HybridView<SIPVideoViewProps, SIPVideoViewMethods>;
