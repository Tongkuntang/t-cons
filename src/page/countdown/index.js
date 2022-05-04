import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  SafeAreaView,
  Platform,
  StyleSheet,
  Image,
  Dimensions,
  TouchableOpacity,
  FlatList,
  Modal,
  ScrollView,
  Alert,
  AppState,
  NativeAppEventEmitter,
  NativeEventEmitter,
  NativeModules,
} from "react-native";
import AppleHealthKit, {
  HealthValue,
  HealthKitPermissions,
} from "react-native-health";
const { Fitblekit } = NativeModules;
import GoogleFit, { Scopes, BucketUnit } from "react-native-google-fit";
import HeaDer from "../components/header";
import Enjoy from "./enjoy";
import Result from "./result";
import Point from "./point";
import Level from "./level";
import { LinearGradient } from "expo-linear-gradient";
import moment from "moment";
import { Gyroscope } from "expo-sensors";

import { useRef } from "react";
import { useRecoilState } from "recoil";
import {
  LvState,
  tokenState,
  userState,
} from "../../reducer/reducer/reducer/Atom";
import { authHistrory } from "../../action/actionhistrory";
import { useIsFocused } from "@react-navigation/native";
import { autolize_Lv, nextautolize_Lv } from "../../json/utils";
import { actionEditprofile } from "../../action/actionfriend";
import { actionEditwal, getActionUser } from "../../action/actionauth";
import { getAllbanner, getBanNer } from "../../action/actionbanner";
import Carousel from "react-native-snap-carousel";
import { getLV } from "../../action/actionLV";
const { width, height } = Dimensions.get("window");
import { timeformet } from "../components/test";

const permissions = {
  permissions: {
    read: [AppleHealthKit.Constants.Permissions.HeartRate],
    write: [AppleHealthKit.Constants.Permissions.HeartRate],
    read: [AppleHealthKit.Constants.Permissions.Steps],
    write: [AppleHealthKit.Constants.Permissions.Steps],
  },
};

export default function index({ navigation }) {
  const [token, setToken] = useRecoilState(tokenState);
  const [user, setUser] = useRecoilState(userState);
  const [put, setput] = useState(false);
  const [page, setpage] = useState(0);
  // const [mainCount, setmainCount] = useState(0);

  const [num, setnum] = useState(5);
  const [detail, setdetail] = useState(true);
  const [chick, setchick] = useState(false);
  const [DO, setDO] = useState(null);
  const [Visible, setVisible] = useState(false);
  const [clear, setClear] = useState(null);
  const [resume, setResume] = useState(false);
  const [lvstate, setlvstate] = useRecoilState(LvState);
  const [timecal, setTime] = useState(0);
  const [back, setBack] = useState(0);
  let counts = useRef();
  const eventEmitter = new NativeEventEmitter(Fitblekit);

  const isFocus = useIsFocused();
  let Clear = useRef();
  const appState = useRef(AppState.currentState);
  const [appStateVisible, setAppStateVisible] = useState(appState.current);

  useEffect(() => {
    let dates = null;
    let currentStepCount = 0;
    const subscription = AppState.addEventListener(
      "change",
      async (nextAppState) => {
        if (
          appState.current.match(/inactive|background/) &&
          nextAppState === "active"
        ) {
          if (Platform.OS == "ios") {
            let options = {
              startDate: new Date(dates).toISOString(),
              endDate: new Date().toISOString(),
              type: "Walking", // one of: ['Walking', 'StairClimbing', 'Running', 'Cycling', 'Workout']
            };
            let valuse = setInterval(() => {
              AppleHealthKit.getSamples(options, (err, results) => {
                if (err) {
                  return;
                }
                let number = 0;
                console.log(results);
                if (results.length != 0) {
                  clearInterval(counts.current);
                  results.map((test) => (number = number + test.quantity));

                  setState((val) => ({
                    ...val,
                    currentStepCount: val.currentStepCount + number * 1.5,
                  }));
                }
              });
            }, 1000);

            counts.current = valuse;
          }
          setTime((val) => val + (new Date().getTime() - dates) / 1000);
        } else {
          dates = new Date().getTime();
        }
        appState.current = nextAppState;
        setAppStateVisible(appState.current);
      }
    );
  }, []);

  const [state, setState] = useState({
    isPedometerAvailable: "checking",
    pastStepCount: 0,
    currentStepCount: 0,
  });

  const carouselRef = useRef();
  const [banner, setbanner] = useState([]);
  const [banner1, setbanner1] = useState([]);

  async function allbanner() {
    const getbanner = await getAllbanner(token);
    const page = getbanner.data[2].page;
    const page1 = getbanner.data[1].page;
    const getbanner1 = await getBanNer({ token, page: page });
    setbanner1(getbanner1.data[0].img_list);
    const getbanner2 = await getBanNer({ token, page: page1 });
    setbanner(getbanner2.data[0].img_list);
  }

  NativeAppEventEmitter.addListener("onSendData", onSendData);
  // console.log('subscribeEmitter', this.subscription)
  // }
  const onSendData = (event) => {
    console.log("HealthEmitterHandler.onSendData", event);
    const { workouts } = event;
    console.log(workouts);
    // this.sendToBackEnd(workouts);
  };

  useEffect(() => {
    allbanner();
    lvlist();
  }, [token]);

  const [LV, setLV] = useState([]);
  async function lvlist() {
    const get = await getLV(token);
    setLV(get.data);
  }

  useEffect(() => {
    let back = 0;
    let Check = 0;
    let mainCount = 0;
    const newDevice5 = eventEmitter.addListener(
      "EVENTFBKSTEP",
      (deviceDiscovered) => {
        if (mainCount == 0) {
          mainCount = deviceDiscovered;
        }
        if (Check != deviceDiscovered - mainCount) {
          setState((val) => ({
            ...val,
            currentStepCount: deviceDiscovered - mainCount,
          }));
          Check = deviceDiscovered - mainCount;
          console.log("ทำนะ");
        }
      }
    );

    if (Platform.OS == "android") {
      GoogleFit.observeSteps((callback) => {
        if (back != callback.steps) {
          back = callback.steps;
          setState((val) => ({
            ...val,
            currentStepCount: val.currentStepCount + callback.steps * 2.3,
          }));
        }
      });
    } else {
      AppleHealthKit.initHealthKit(permissions, (err, res) => {
        const sub2 = NativeAppEventEmitter.addListener(
          "healthKit:StepCount:new",
          (evt) => {
            console.log(evt);
          }
        );
      });
    }
  }, []);

  const [data, setdata] = useState(null);
  const d = Date(Date.now());
  const a = moment(d.toString()).format("YYYY-MM-DD");
  const [body, setbody] = useState({
    uid: user.id,
    Distance: null,
    date: "",
    info: {},
  });

  const walkingFactor = 0.57;
  const customerheight = user.height;
  const weight = user.weight;
  let _subscription = useRef();
  const strip = customerheight * 0.415; // ความสูง
  const CaloriesBurnedPerMile = walkingFactor * (weight * 2.2);
  const stepCountMile = 160934.4 / strip;
  const conversationFactor = CaloriesBurnedPerMile / stepCountMile;

  function count() {
    setTimeout(() => {
      setnum((connt) => connt - 1);
    }, 1000);
    setTimeout(() => {
      setnum((connt) => connt - 1);
    }, 2000);
    setTimeout(() => {
      setnum((connt) => connt - 1);
    }, 3000);
    setTimeout(() => {
      setnum((connt) => connt - 1);
    }, 4000);
    setTimeout(() => {
      setnum((connt) => connt - 1);
    }, 5000);
  }

  useEffect(() => {
    if (chick) {
      _subscribe();
    } else {
      _unsubscribe();
    }
    return () => {
      _unsubscribe();
    };
  }, [chick]);

  useEffect(() => {
    // requestPermissions();
  }, []);

  const [subscription, setSubscription] = useState(null);

  const _unsubscribe = () => {
    subscription && subscription.remove();
    setSubscription(null);
  };

  const _subscribe = async () => {
    console.log(await Gyroscope.isAvailableAsync());
    // let MagnitudePreviouss = 0;
    let Magnitude = 0;
    setSubscription(
      Gyroscope.addListener((gyroscopeData) => {
        Magnitude =
          Magnitude +
          Math.sqrt(
            gyroscopeData.x * gyroscopeData.x +
              gyroscopeData.y * gyroscopeData.y +
              gyroscopeData.z * gyroscopeData.z
          ) -
          0.012;

        if (Magnitude > 8) {
          Magnitude = 0;
          setState((val) => ({
            ...val,
            currentStepCount: val.currentStepCount + 1,
          }));
        }
      })
    );
  };

  useEffect(() => {
    getAlluser();
    Clear.current = setInterval(() => {
      setTime((val) => val + 1);
    }, 1000);
    setClear(Clear.current);

    if (!isFocus) {
      clearInterval(Clear.current);
    }
  }, [isFocus]);

  useEffect(() => {
    if (resume) {
      setResume(false);
      Clear.current = setInterval(() => {
        setTime((val) => val + 1);
      }, 1000);
      setClear(Clear.current);
    }
  }, [resume]);

  var h = timeformet(Math.floor(timecal / 3600));
  var m = timeformet(Math.floor((timecal % 3600) / 60));
  var s = timeformet(Math.floor((timecal % 3600) % 60));

  function formatTime(params) {
    let Ans = params.toString();
    if (Ans.length == 1) {
      return "0" + Ans;
    } else {
      return Ans;
    }
  }

  async function getAlluser() {
    const resposne = await getActionUser(token);
    setUser(resposne.data);
  }

  return (
    <View style={{ flex: 1 }}>
      <SafeAreaView />
      <Modal
        animationType="none"
        transparent={true}
        visible={put}
        onRequestClose={() => {
          setput(!put);
        }}
      >
        <View
          style={{
            flex: 1,
            backgroundColor: "#000000bb",
            justifyContent: "center",
          }}
        >
          <View
            style={{
              width: width * 0.8,
              // height: 150,
              alignSelf: "center",
              backgroundColor: "#fff",
              borderRadius: 10,
            }}
          >
            <Text
              style={{
                fontFamily: "Prompt-Bold",
                fontSize: 16,
                color: "#000",
                alignSelf: "center",
                marginTop: 35,
              }}
            >
              Are you sure ?
            </Text>
            <Text
              style={{
                fontFamily: "Prompt-Regular",
                fontSize: 16,
                color: "#000",
                alignSelf: "center",
                textAlign: "center",
              }}
            >
              Are you sure you went to discard this run ?
            </Text>
            <View
              style={{
                flexDirection: "row",
                justifyContent: "space-between",
                width: width * 0.6,
                alignSelf: "center",
                marginTop: 20,
                marginBottom: 25,
              }}
            >
              <TouchableOpacity
                onPress={async () => {
                  setput(!put);
                }}
                style={{
                  width: 100,
                  height: 30,
                  borderRadius: 10,
                  borderWidth: 1,
                  borderColor: "#000",
                  backgroundColor: "#000",
                  justifyContent: "center",
                }}
              >
                <Text
                  style={{
                    fontFamily: "Prompt-Regular",
                    fontSize: 14,
                    color: "#fff",
                    alignSelf: "center",
                  }}
                >
                  Cancel
                </Text>
              </TouchableOpacity>
              <TouchableOpacity
                onPress={async () => {
                  let datamain = {
                    ...body,
                    info: {
                      distance: (state.currentStepCount * strip) / 100000,
                      callery: state.currentStepCount * conversationFactor,
                      time: timecal,
                    },
                    Distance: (state.currentStepCount * strip) / 100000,
                    date: a,
                  };

                  const response = await authHistrory({
                    body: datamain,
                    token,
                  });

                  const response1 = await actionEditprofile({
                    body: {
                      id: user.id,
                      total_distance:
                        parseInt(user.user_accounts.total_distance) +
                        ((state.currentStepCount * strip) / 100000) * 1000,
                    },
                    token,
                  });

                  if (response1.status == 200) {
                    const getuser = await getActionUser(token);
                    setUser(getuser.data);
                  }
                  setdetail(false);
                  setResume(true);
                  setnum(5);
                  setput(false);
                  setState((val) => ({ ...state, currentStepCount: 0 }));
                }}
                style={{
                  width: 100,
                  height: 30,
                  borderRadius: 10,
                  borderWidth: 1,
                  backgroundColor: "#FFC300",
                  borderColor: "#444",
                  justifyContent: "center",
                }}
              >
                <Text
                  style={{
                    fontFamily: "Prompt-Regular",
                    fontSize: 14,
                    color: "#fff",
                    alignSelf: "center",
                  }}
                >
                  Confirm
                </Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>
      <View style={{}}>
        <HeaDer onPress={() => navigation.goBack()} navigation={navigation} />
        <ScrollView>
          <Modal
            animationType="none"
            transparent={true}
            visible={Visible}
            onRequestClose={() => {
              Alert.alert("Modal has been closed.");
              setVisible(!Visible);
            }}
          >
            <View
              style={{
                flex: 1,
                backgroundColor: "#000000bb",
                justifyContent: "center",
              }}
            >
              <LinearGradient
                colors={["#FCC81D", "#EFD98F", "#EEE6CB", "#C29709"]}
                style={styles.backgroundmodal}
              >
                <Text style={styles.texttopicmodal}>COMPLETED</Text>
                <Image
                  // resizeMode={"stretch"}
                  source={require("../../img/pooop.png")}
                  style={styles.imgmodal}
                />
                <View style={styles.modall}>
                  <Text style={styles.texttopic}>
                    lv {data != null && data.lv}
                  </Text>
                  <Text style={styles.texttopic1}>
                    {data != null && data.reward.coinlv} Gold
                  </Text>
                </View>
                <View style={styles.modall}>
                  <Text style={styles.texttopic}>Distance</Text>
                  <Text style={styles.texttopic1}>
                    {data != null && data.reward.coindis} Gold
                  </Text>
                </View>
                <View style={styles.modall}>
                  <Text style={styles.texttopic}>Calories</Text>
                  <Text style={styles.texttopic1}>
                    {data != null && data.reward.cal} kcl
                  </Text>
                </View>
                <TouchableOpacity
                  onPress={async () => {
                    setVisible(!Visible);
                  }}
                  style={styles.touchmodal}
                >
                  <Text style={styles.go}>ตกลง</Text>
                </TouchableOpacity>
              </LinearGradient>
            </View>
          </Modal>
          {detail == false && (
            <Result
              navigation={navigation}
              onPress={() => {
                setdetail(true);
                setpage(2);
              }}
            />
          )}
          {detail == false ? (
            <View></View>
          ) : num == 0 ? (
            (page == 0 && (
              <Carousel
                ref={carouselRef}
                data={banner}
                sliderWidth={1000}
                itemWidth={1000}
                autoplay={100}
                loop
                renderItem={({ item, index }) => {
                  // console.log("129", item);
                  return (
                    <View>
                      <Image
                        // resizeMode={"stretch"}
                        style={styles.imgsup}
                        source={{
                          uri: "https://api.sosorun.com/api/imaged/get/" + item,
                        }}
                      />
                    </View>
                  );
                }}
              />
            )) ||
            (page == 1 && <Enjoy />) ||
            (page == 2 && <Enjoy />)
          ) : (
            (page == 0 && <Enjoy />) ||
            (page == 1 && <Enjoy />) ||
            (page == 2 && <Enjoy />)
          )}
          {detail == true && (
            <View style={styles.viewpage}>
              <TouchableOpacity
                onPress={() => setpage(0)}
                style={{ justifyContent: "center" }}
              >
                <Text
                  style={[
                    styles.textpage,
                    { color: page == 0 ? "#FCC81D" : "#fff" },
                    {
                      borderColor: page == 0 ? "#FCC81D" : "#393939",
                    },
                  ]}
                >
                  Run
                </Text>
              </TouchableOpacity>
              <TouchableOpacity
                onPress={() => setpage(1)}
                style={{ justifyContent: "center" }}
              >
                <Text
                  style={[
                    styles.textpage,
                    { color: page == 1 ? "#FCC81D" : "#fff" },
                    {
                      borderColor: page == 1 ? "#FCC81D" : "#393939",
                    },
                  ]}
                >
                  Level
                </Text>
              </TouchableOpacity>
              <TouchableOpacity
                onPress={() => setpage(2)}
                style={{ justifyContent: "center" }}
              >
                <Text
                  style={[
                    styles.textpage,
                    { color: page == 2 ? "#FCC81D" : "#fff" },
                    {
                      borderColor: page == 2 ? "#FCC81D" : "#393939",
                    },
                  ]}
                >
                  Point
                </Text>
              </TouchableOpacity>
            </View>
          )}
          {detail == true &&
            page == 0 &&
            (num == 0 ? (
              <View>
                <Text style={styles.textRun}>Time</Text>
                <Text style={styles.numRum}>
                  {formatTime(h)}:{formatTime(m)}:{formatTime(s)}
                </Text>
                <View style={styles.line} />
                <Text style={styles.textRun}>Distance</Text>
                <Text style={styles.numRum1}>
                  {((state.currentStepCount * strip) / 100000).toFixed(2)}
                </Text>
                <View style={styles.line} />
                <View style={{ flexDirection: "row" }}>
                  <View style={styles.viewspeed}>
                    <Text style={styles.textRun}>Speed</Text>
                    <Text style={styles.numRum}>
                      {(state.currentStepCount / timecal).toFixed(2)}
                    </Text>
                  </View>
                  <View style={styles.viewcalories}>
                    <Text style={styles.textRun}>Calories</Text>
                    <Text style={styles.numRum}>
                      {(state.currentStepCount * conversationFactor).toFixed(2)}
                    </Text>
                  </View>
                </View>
                {chick ? (
                  <TouchableOpacity
                    onPress={() => {
                      clearInterval(Clear.current);
                      clearInterval(clear);
                      setchick((val) => !val);
                    }}
                    style={styles.bottompause}
                  >
                    <Text style={styles.textpause}>PAUSE</Text>
                  </TouchableOpacity>
                ) : (
                  <View
                    style={{
                      flexDirection: "row",
                      justifyContent: "space-between",
                      paddingHorizontal: 10,
                    }}
                  >
                    <TouchableOpacity
                      onPress={() => {
                        setResume(true);
                        setchick((val) => !val);
                      }}
                      style={styles.bottomresume}
                    >
                      <Text style={styles.textpause}>RESUME</Text>
                    </TouchableOpacity>
                    <TouchableOpacity
                      onPress={() => {
                        setput(true);
                      }}
                      style={styles.bottomresume}
                    >
                      <Text style={styles.textpause}>BREAK</Text>
                    </TouchableOpacity>
                  </View>
                )}
                <View style={styles.viewlv}>
                  <Text style={styles.textRun}>
                    Lv
                    {parseFloat(
                      nextautolize_Lv(
                        (parseFloat(user.user_accounts.total_distance / 1000) +
                          (state.currentStepCount * strip) / 100000) *
                          1000
                      ).lv
                    ) -
                      1 >
                    60
                      ? lvstate.length == 0
                        ? 60
                        : lvstate[0].status == true
                        ? parseFloat(
                            nextautolize_Lv(
                              (parseFloat(
                                user.user_accounts.total_distance / 1000
                              ) +
                                (state.currentStepCount * strip) / 100000) *
                                1000
                            ).lv
                          ) - 1
                        : 60
                      : parseFloat(
                          nextautolize_Lv(
                            (parseFloat(
                              user.user_accounts.total_distance / 1000
                            ) +
                              (state.currentStepCount * strip) / 100000) *
                              1000
                          ).lv
                        ) - 1}
                  </Text>
                  <Text style={styles.textRun}>
                    Lv
                    {nextautolize_Lv(
                      (parseFloat(user.user_accounts.total_distance / 1000) +
                        (state.currentStepCount * strip) / 100000) *
                        1000
                    ).lv > 60
                      ? lvstate.length == 0
                        ? 61
                        : lvstate[0].status == true
                        ? nextautolize_Lv(
                            (parseFloat(
                              user.user_accounts.total_distance / 1000
                            ) +
                              (state.currentStepCount * strip) / 100000) *
                              1000
                          ).lv
                        : 61
                      : parseFloat(
                          nextautolize_Lv(
                            (parseFloat(
                              user.user_accounts.total_distance / 1000
                            ) +
                              (state.currentStepCount * strip) / 100000) *
                              1000
                          ).lv
                        )}
                  </Text>
                </View>
                <View style={styles.linelevel}>
                  <View
                    style={{
                      width:
                        width *
                        ((((parseFloat(
                          user.user_accounts.total_distance / 1000
                        ) +
                          (state.currentStepCount * strip) / 100000) *
                          1000) /
                          nextautolize_Lv(
                            (parseFloat(
                              user.user_accounts.total_distance / 1000
                            ) +
                              (state.currentStepCount * strip) / 100000) *
                              1000
                          ).exp) *
                          0.93),
                      height: 4.5,
                      backgroundColor: "#F8CA36",
                      alignSelf: "flex-start",
                    }}
                  />
                </View>
                <View style={styles.viewlv}>
                  <Text style={styles.textRun}>
                    {(
                      user.user_accounts.total_distance / 1000 +
                      (state.currentStepCount * strip) / 100000
                    ).toFixed(2)}{" "}
                    km
                  </Text>
                  <Text style={styles.textRun}>
                    {nextautolize_Lv(
                      (parseFloat(user.user_accounts.total_distance / 1000) +
                        (state.currentStepCount * strip) / 100000) *
                        1000
                    ).exp / 1000}{" "}
                    km
                  </Text>
                </View>
              </View>
            ) : (
              <View style={{ padding: 20 }}>
                <Text style={styles.text}>Internal Timer Start</Text>
                <View style={styles.viewnumber}>
                  <Text style={styles.number}>{num}</Text>
                  <Text style={styles.sec}>sec</Text>
                </View>
                <TouchableOpacity
                  onPress={() => {
                    count();
                    setTimeout(() => {
                      setchick(true);
                      setTime(0);
                    }, 5000);
                  }}
                  style={styles.bottongo}
                >
                  {num == 5 ? (
                    <Text style={styles.go}>GO</Text>
                  ) : (
                    <TouchableOpacity
                      onPress={() => {
                        navigation.navigate("Home");
                        // setnum(5);
                      }}
                      style={{ justifyContent: "center" }}
                    >
                      <Text style={styles.go}>CANCEL</Text>
                    </TouchableOpacity>
                  )}
                </TouchableOpacity>
                <Text style={styles.time}>00:00:00</Text>
              </View>
            ))}
          {page == 1 && <Level />}

          {page == 2 && (
            <View>
              <View style={{ height: height * 0.3 }}>
                <FlatList
                  data={LV.filter(
                    (item) =>
                      user.user_accounts.storage.Lvlist.filter(
                        (items) => items == item.lv
                      ).length == 0
                  )}
                  // extraData={filter}
                  renderItem={({ item }) => {
                    return (
                      <Point
                        item={item}
                        onPress={async () => {
                          setdata(item);
                          setVisible(true);
                          const response = await actionEditprofile({
                            token,
                            body: {
                              id: user.id,
                              storage: {
                                ...user.user_accounts.storage,
                                Lvlist:
                                  user.user_accounts.storage.Lvlist.concat(
                                    item.lv
                                  ),
                              },
                            },
                          });
                          // console.log("response", response);
                          if (response.status == 200) {
                            const get = await getLV(token);
                            // console.log("get", get);
                            const getuser = await getActionUser(token);
                            // console.log("getuser", getuser);
                            setUser(getuser.data);
                          }
                          let gold =
                            parseInt(item.reward.coinlv) +
                            parseInt(item.reward.coindis);
                          let cal = item.reward.cal;
                          const response3 = await actionEditwal({
                            body: { gold: gold, cal: cal },
                            token,
                          });
                        }}
                      />
                    );
                  }}
                />
              </View>
            </View>
          )}
        </ScrollView>
      </View>
      {page != 0 && (
        <View
          style={{
            position: "absolute",
            width: width,
            height: height * 0.2,
            bottom: 0,
          }}
        >
          <Carousel
            ref={carouselRef}
            data={banner1}
            sliderWidth={Dimensions.get("window").width}
            itemWidth={Math.round(width * 1)}
            autoplay
            loop
            inactiveSlideScale={1}
            renderItem={({ item, index }) => {
              // console.log("129", item);
              return (
                <View>
                  <Image
                    style={{ width: width, height: height * 0.2 }}
                    source={{
                      uri: "https://api.sosorun.com/api/imaged/get/" + item,
                    }}
                    // style={{ backgroundColor: "red" }}
                    // style={{ marginLeft: -width * 0.22, marginTop: -width * 0.2 }}
                  />
                </View>
              );
            }}
          />
        </View>
      )}
    </View>
  );
}
const styles = StyleSheet.create({
  textpage: {
    fontFamily: "Prompt-Regular",
    fontSize: 16,
    marginLeft: 5,
    alignSelf: "center",
    borderBottomWidth: 1,
  },
  viewpage: {
    backgroundColor: "#393939",
    height: 30,
    flexDirection: "row",
    paddingHorizontal: 40,
    justifyContent: "space-between",
  },
  text: {
    fontFamily: "Prompt-Regular",
    fontSize: 24,
    color: "#000",
  },
  viewnumber: {
    flexDirection: "row",
    alignSelf: "center",
    marginVertical: 15,
  },
  number: {
    fontFamily: "Prompt-Regular",
    fontSize: 100,
    color: "#000",
    marginLeft: 50,
  },
  sec: {
    fontFamily: "Prompt-Regular",
    fontSize: 24,
    color: "#000",
    marginTop: 20,
    marginLeft: 10,
  },
  go: {
    fontFamily: "Prompt-Regular",
    fontSize: 24,
    color: "#fff",
    alignSelf: "center",
  },
  bottongo: {
    width: width * 0.4,
    height: 48,
    backgroundColor: "#393939",
    borderRadius: 5,
    justifyContent: "center",
    alignSelf: "center",
    marginBottom: 10,
  },
  time: {
    fontFamily: "Prompt-Regular",
    fontSize: 48,
    color: "#000",
    alignSelf: "center",
    marginTop: 10,
  },
  textRun: {
    fontFamily: "Prompt-Regular",
    fontSize: 16,
    color: "#000",
    alignSelf: "center",
    marginTop: 10,
  },

  viewlv: {
    flexDirection: "row",
    justifyContent: "space-between",
    paddingHorizontal: 10,
  },
  linelevel: {
    width: width * 0.93,
    height: 5,
    backgroundColor: "#000000",
    marginTop: 5,
    marginHorizontal: 10,
    alignSelf: "center",
  },
  linelevelrank: {
    width: width * 0.4,
    height: 4.5,
    backgroundColor: "#F8CA36",
    alignSelf: "flex-start",
  },
  bottompause: {
    width: width * 0.98,
    height: 55,
    backgroundColor: "#393939",
    borderRadius: 5,
    justifyContent: "center",
    alignSelf: "center",
    marginTop: 10,
  },
  textpause: {
    fontFamily: "Prompt-Regular",
    fontSize: 24,
    color: "#fff",
    alignSelf: "center",
  },
  bottomresume: {
    width: width * 0.46,
    height: 55,
    backgroundColor: "#393939",
    borderRadius: 5,
    justifyContent: "center",
    alignSelf: "center",
    marginTop: 10,
  },
  backgroundmodal: {
    width: width * 0.9,
    alignSelf: "center",
    backgroundColor: "red",
    marginTop: 30,
    paddingHorizontal: 20,
    borderRadius: 10,
  },
  texttopicmodal: {
    fontFamily: "Prompt-Regular",
    fontSize: 42,
    color: "#000",
    alignSelf: "center",
    marginTop: 10,
  },
  imgmodal: {
    width: 250,
    height: 250,
    alignSelf: "center",
    marginTop: 10,
  },
  modall: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginTop: 10,
  },
  texttopic: {
    fontFamily: "Prompt-Regular",
    fontSize: 20,
    color: "#000",
  },
  texttopic1: {
    fontFamily: "Prompt-Regular",
    fontSize: 20,
    color: "#036E35",
  },
  touchmodal: {
    width: width * 0.6,
    height: 48,
    justifyContent: "center",
    alignSelf: "center",
    marginVertical: 10,
    backgroundColor: "#393939",
    borderRadius: 5,
  },
  viewcalories: {
    width: width * 0.5,
    height: 80,
    borderLeftWidth: 0.25,
    borderLeftColor: "#707070",
    borderBottomWidth: 0.5,
    borderBottomColor: "#707070",
    justifyContent: "center",
  },
  textRun: {
    fontFamily: "Prompt-Regular",
    fontSize: 16,
    color: "#000",
    alignSelf: "center",
    marginTop: 5,
  },
  numRum: {
    fontFamily: "Prompt-Regular",
    fontSize: 20,
    color: "#000",
    alignSelf: "center",
    marginBottom: 5,
  },
  numRum1: {
    fontFamily: "Prompt-Regular",
    fontSize: 48,
    color: "#000",
    alignSelf: "center",
    marginBottom: 5,
  },
  line: {
    width: width,
    borderBottomWidth: 0.5,
    borderBottomColor: "#707070",
  },
  viewspeed: {
    width: width * 0.5,
    height: 80,
    borderRightWidth: 0.25,
    borderRightColor: "#707070",
    borderBottomWidth: 0.5,
    borderBottomColor: "#707070",
    justifyContent: "center",
  },
  bottompause: {
    width: width * 0.98,
    height: 55,
    backgroundColor: "#393939",
    borderRadius: 5,
    justifyContent: "center",
    alignSelf: "center",
    marginTop: 10,
  },
  textpause: {
    fontFamily: "Prompt-Regular",
    fontSize: 24,
    color: "#fff",
    alignSelf: "center",
  },
  imgsup: {
    width: width,
    height: height * 0.3,
    marginTop: 13,
    bottom: 0,
  },
  imgsup1: {
    width: width,
    height: height * 0.2,
    bottom: 0,
  },
});

// import React, { useEffect } from 'react';
// import * as Permissions from 'expo-permissions';
// import { NativeModules, Button,PermissionsAndroid } from 'react-native';
// const { Fitblekit } = NativeModules;

// const NewModuleButton = () => {

//   useEffect(() => {
//     // getLocationAsync();
//   },[]);

//   const onPress = () => {
//     Fitblekit.onScanStart('testName', 'testLocation',(e) => {

//     });
//     // Fitblekit.onTrack('testName', 'testLocation',(e) => {

//     // });
//   };

//   return (
//     <Button
//       title="Click to invoke your native module!"
//       color="#841584"
//       onPress={onPress}
//     />
//   );
// };

// export default NewModuleButton;
