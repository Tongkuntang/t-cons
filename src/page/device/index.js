import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  SafeAreaView,
  Platform,
  StyleSheet,
  Image,
  Dimensions,
  TouchableOpacity,
  ScrollView,
  NativeModules,
  NativeEventEmitter,
  Modal,
  Linking,
} from "react-native";
const { Fitblekit } = NativeModules;
import { useRecoilState } from "recoil";
import { deviceIndex } from "../../reducer/reducer/reducer/Atom";
import Header from "../components/header";
import { TextInput } from "react-native-gesture-handler";
const { width, height } = Dimensions.get("window");
// import { GarminConnect } from "garmin-connect/dist";
// const GCClient = new GarminConnect();

export default function index({ navigation }) {
  const [modals, setModal] = useState(false);
  const peripherals = new Map();
  const [list, setList] = useState([]);
  const eventEmitter = new NativeEventEmitter(Fitblekit);
  const [devicsI, setDeviceI] = useRecoilState(deviceIndex);
  const [bodygarmin, setbodygarmin] = useState({
    username: "my.email@example.com",
    password: "MySecretPassword",
  });

  const renderItem = (item) => {
    const color = item.connected ? "green" : "#fff";
    return (
      <TouchableHighlight onPress={() => testPeripheral(item)}>
        <View style={[styles.row, { backgroundColor: color }]}>
          <Text
            style={{
              fontSize: 12,
              textAlign: "center",
              color: "#333333",
              padding: 10,
            }}
          >
            {item.name}
          </Text>
          <Text
            style={{
              fontSize: 10,
              textAlign: "center",
              color: "#333333",
              padding: 2,
            }}
          >
            RSSI: {item.rssi}
          </Text>
          <Text
            style={{
              fontSize: 8,
              textAlign: "center",
              color: "#333333",
              padding: 2,
              paddingBottom: 20,
            }}
          >
            {item.id}
          </Text>
        </View>
      </TouchableHighlight>
    );
  };

  useEffect(() => {
    try {
      if (Platform.OS == "android") {
        const newDevice = eventEmitter.addListener(
          "EVENTFBK",
          (deviceDiscovered) => {
            console.log(deviceDiscovered);
            const ress = deviceDiscovered.split(",");
            if (ress.length > 0) {
              Fitblekit.onConnect("SENIOR", "APPROVE", (e) => {
                console.log(e);
              });
            }
            // try {
            //   Fitblekit.onScanStop();
            // } catch (error) {}
          }
        );

        const newDevice1 = eventEmitter.addListener(
          "EVENTFBKSTEP",
          (deviceDiscovered) => {
            console.log(
              "EVENTFBKSTEP",
              deviceDiscovered
                .replace(/=/g, ":")
                .replace(/, calories:/g, `", calories:`)
                .replace(/createTime:/g, `createTime:"`)
            );
            // setDeviceList(deviceList => [...deviceList,deviceDiscovered])
          }
        );

        const newDevice2 = eventEmitter.addListener(
          "CONNECT",
          (deviceDiscovered) => {
            console.log("CONNECT", deviceDiscovered);
            // setDeviceList(deviceList => [...deviceList,deviceDiscovered])
          }
        );

        const newDevice3 = eventEmitter.addListener(
          "ERRORBLE",
          (deviceDiscovered) => {
            console.log("ERRORBLE", deviceDiscovered);
            if (deviceDiscovered == "BleConnected") {
              setDeviceI(1);
            }

            // setDeviceList(deviceList => [...deviceList,deviceDiscovered])
          }
        );

        const newDevice4 = eventEmitter.addListener(
          "batteryPower",
          (deviceDiscovered) => {
            console.log("batteryPower", deviceDiscovered);
            // setDeviceList(deviceList => [...deviceList,deviceDiscovered])
          }
        );

        const newDevice5 = eventEmitter.addListener(
          "EVENTFBKSTEP1",
          (deviceDiscovered) => {
            console.log("EVENTFBKSTEP1", deviceDiscovered);
            // setDeviceList(deviceList => [...deviceList,deviceDiscovered])
          }
        );

        const newDevice6 = eventEmitter.addListener(
          "EVENTFBKSTEP2",
          (deviceDiscovered) => {
            console.log("EVENTFBKSTEP2", deviceDiscovered);
            // setDeviceList(deviceList => [...deviceList,deviceDiscovered])
          }
        );

        const batteryPower = eventEmitter.addListener(
          "batteryPower",
          (deviceDiscovered) => {
            console.log("batteryPower", deviceDiscovered);
            // setDeviceList(deviceList => [...deviceList,deviceDiscovered])
          }
        );

        return () => {
          newDevice.remove();
          newDevice1.remove();
          newDevice2.remove();
          newDevice3.remove();
          newDevice4.remove();
          newDevice5.remove();
          newDevice6.remove();
          batteryPower.remove();
        };
      } else {
        const newDevice2 = eventEmitter.addListener(
          "CONNECT",
          (deviceDiscovered) => {
            console.log(
              deviceDiscovered.name.filter((item) => {
                return item.localName.includes("MOVE");
              })
            );
          }
        );
        return () => {
          newDevice2.remove();
        };
      }
    } catch (error) {}
  }, []);

  useEffect(() => {
    if (list.length > 0) {
      testPeripheral(list[3]);
    }
  }, [list]);

  return (
    <View style={styles.contalner}>
      <SafeAreaView />
      <Modal transparent={true} visible={modals} style={{ flex: 1 }}>
        <View
          style={{
            flex: 1,
            backgroundColor: "#00000090",
            justifyContent: "center",
            alignItems: "center",
          }}
        >
          <View
            style={{
              width: width * 0.9,
              height: width * 0.9,
              backgroundColor: "#fff",
              borderRadius: 10,
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <Text
              style={{
                fontFamily: "Prompt-Regular",
                fontSize: 19,
                marginBottom: 15,
              }}
            >
              GARMIN
            </Text>
            <TextInput
              style={{
                width: width * 0.8,
                height: 45,
                borderWidth: 1,
                borderRadius: 5,
                paddingHorizontal: 10,
                marginBottom: 15,
                fontFamily: "Prompt-Regular",
              }}
              defaultValue={bodygarmin.username}
              onChangeText={(text) => {
                setbodygarmin((val) => ({
                  ...val,
                  username: text,
                }));
              }}
            />
            <TextInput
              style={{
                width: width * 0.8,
                height: 45,
                borderWidth: 1,
                borderRadius: 5,
                paddingHorizontal: 10,
              }}
              secureTextEntrys
              defaultValue={bodygarmin.password}
              onChangeText={(text) => {
                setbodygarmin((val) => ({
                  ...val,
                  password: text,
                }));
              }}
            />
            <TouchableOpacity
              onPress={async () => {
                await GCClient.login(
                  "my.email@example.com",
                  "MySecretPassword"
                );
                const userInfo = await GCClient.getUserInfo();
              }}
              style={{
                backgroundColor: "#000",
                width: width * 0.3,
                height: 45,
                borderRadius: 5,
                alignItems: "center",
                justifyContent: "center",
                marginTop: 13,
              }}
            >
              <Text
                style={{
                  fontFamily: "Prompt-Regular",
                  fontSize: 19,

                  color: "#fff",
                }}
              >
                Connect
              </Text>
            </TouchableOpacity>
            <TouchableOpacity
              onPress={() => {
                setModal(false);
              }}
              style={{
                backgroundColor: "#fff",
                width: width * 0.3,
                height: 45,
                borderRadius: 5,
                alignItems: "center",
                justifyContent: "center",
                marginTop: 13,
              }}
            >
              <Text
                style={{
                  fontFamily: "Prompt-Regular",
                  fontSize: 19,
                  color: "#000",
                }}
              >
                Close
              </Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
      <View
        style={{
          marginTop: Platform.OS === "ios" ? 0 : 0,
        }}
      >
        <ScrollView>
          <Header
            navigation={navigation}
            onPress={() => {
              navigation.goBack();
            }}
          />
          <Text style={styles.texthead}>SOSORUN SMART GEAR</Text>
          {/* <TouchableOpacity
            onPress={() => navigation.goBack()}
            style={styles.touch}
          >
            <Image
              source={{
                uri: "https://ssr-project.s3.ap-southeast-1.amazonaws.com/111.png",
              }}
              style={styles.imgsoso}
            />
            <Text style={styles.textdevice}>SOSORUN POD V1</Text>
          </TouchableOpacity> */}
          <View style={styles.line} />
          {/* <TouchableOpacity
            onPress={() => {
              Fitblekit.onScanStart((e, i) => {
                // console.log(e);
                // console.log(i);
              });
            }}
            style={styles.touch}
          >
            <Image
              source={{
                uri: "https://ssr-project.s3.ap-southeast-1.amazonaws.com/112.png",
              }}
              style={styles.imgsoso}
            />
            <Text style={styles.textdevice}>SOSORUN POD V2</Text>
            {devicsI == 1 && (
              <MaterialIcons
                style={{ position: "absolute", right: 25, top: 25 }}
                name="check"
                size={32}
                color="#55AB68"
              />
            )}
          </TouchableOpacity> */}
          {/* <View style={styles.line} /> */}
          {/* <Text style={styles.texthead}>Other Devices</Text> */}
          <TouchableOpacity
            onPress={() => {
              setModal(true);
            }}
            style={styles.touch}
          >
            <Image
              source={{
                uri: "https://ssr-project.s3.ap-southeast-1.amazonaws.com/113.png",
              }}
              style={styles.imgsoso1}
            />
            <Text style={styles.textdevice}>GARMIN</Text>
          </TouchableOpacity>
          <View style={styles.line} />
          <TouchableOpacity
            onPress={() => {
              Linking.openURL(
                "https://oauth-login.cloud.huawei.com/oauth2/v3/authorize?response_type=code&state=state_parameter_passthrough_value&client_id=101489619&redirect_uri=http%3A%2F%2Fwww.example.com&scope=openid+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Fheightweight.read+https%3A%2F%2Fwww.huawei.com%2Fhealthkit%2Fcalories.read&access_type=offline&display=touch"
              );
            }}
            style={styles.touch}
          >
            <Image
              source={{
                uri: "https://ssr-project.s3.ap-southeast-1.amazonaws.com/114.png",
              }}
              style={styles.imgsoso2}
            />
            <Text style={styles.textdevice}>HUAWEI</Text>
          </TouchableOpacity>
          <View style={styles.line} />
          {/* <TouchableOpacity style={styles.touch}>
            <Image
              source={{
                uri: "https://ssr-project.s3.ap-southeast-1.amazonaws.com/115.png",
              }}
              style={styles.imgsoso2}
            />
            <Text style={styles.textdevice}>XIAOMI</Text>
          </TouchableOpacity>
          <View style={styles.line} /> */}
        </ScrollView>
      </View>
    </View>
  );
}
const styles = StyleSheet.create({
  contalner: {
    flex: 1,
  },
  texthead: {
    fontFamily: "Prompt-Regular",
    fontSize: 14,
    color: "#717171",
    marginLeft: 20,
    marginTop: 20,
  },
  textdevice: {
    fontFamily: "Prompt-Regular",
    fontSize: 14,
    color: "#717171",
    marginLeft: 20,
    alignSelf: "center",
  },
  touch: {
    marginLeft: 20,
    marginTop: 20,
    flexDirection: "row",
  },
  imgsoso: {
    width: 68,
    height: 84,
    alignSelf: "center",
  },
  line: {
    width: width,
    borderBottomWidth: 1,
    borderBottomColor: "#44444450",
    marginTop: 20,
  },
  imgsoso1: {
    width: 47,
    height: 57,
    alignSelf: "center",
  },
  imgsoso2: {
    width: 31,
    height: 38,
    alignSelf: "center",
  },
});
