import { useRef } from 'react';
import { Button, StyleSheet, Text, View } from 'react-native';
import {
  createAccount,
  endCall,
  firstSetup,
  makeCall,
  multiply,
  removeAccount,
  requestPermission,
} from 'react-native-pjsip_sdk';
import type { SIPAccount } from '../../src/SIPAccount.nitro';
import type { SIPCall } from '../../src/SIPCall.nitro';

const result = multiply(3, 7);

export default function App() {
  const account = useRef<SIPAccount>(null);
  const call = useRef<SIPCall>(null);
  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
      <Button title="firstSetup" onPress={() => firstSetup()} />
      <Button
        title="requestPermission"
        onPress={() =>
          requestPermission().then((res) =>
            console.log('requestPermission:', res)
          )
        }
      />
      <Button
        title="createAccount"
        onPress={() =>
          createAccount({
            domain: 'C0384.talk.worldfone.cloud',
            password: '72419633',
            username: 'C03841717',
          }).then((res) => {
            console.log('createAccount:', res);
            account.current = res ?? null;
          })
        }
      />
      <Button
        title="deleteAccount"
        onPress={() => {
          const id = account.current?.id;
          if (id !== undefined) {
            removeAccount(id).then(() => {
              account.current = null;
              console.log('deleteAccount:', id, 'done');
            });
          }
        }}
      />
      <Button
        title="makeCall"
        onPress={() => {
          const id = account.current?.id;
          if (id !== undefined) {
            makeCall(0, '3107').then((res) => {
              call.current = res ?? null;
              console.log('makeCall:', call.current);
            });
          }
        }}
      />
      <Button
        title="endCall"
        onPress={() => {
          const id = call.current?.id;
          if (id !== undefined) {
            endCall(id).then((res) => {
              call.current = null;
              console.log('makeCall:', res);
            });
          }
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
