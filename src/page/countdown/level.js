import React, { useEffect, useRef, useState } from "react";
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
} from "react-native";
import { useRecoilState } from "recoil";
import { getActionUser } from "../../action/actionauth";
import { getAllbanner, getBanNer } from "../../action/actionbanner";
import { autolize_Lv, nextautolize_Lv } from "../../json/utils";
import {
  LvState,
  tokenState,
  userState,
} from "../../reducer/reducer/reducer/Atom";
import Carousel from "react-native-snap-carousel";
const { width, height } = Dimensions.get("window");
export default function level() {
  const [user, setUser] = useState(null);
  const [token, setToken] = useRecoilState(tokenState);
  const carouselRef = useRef();
  const [lvstate, setlvstate] = useRecoilState(LvState);
  const [banner, setbanner] = useState([]);
  const [banner1, setbanner1] = useState([]);
  async function allbanner() {
    const getbanner = await getAllbanner(token);
    const page = getbanner.data[2].page;
    // console.log("36", page);
    // setbanner(getbanner.data);
    const getbanner1 = await getBanNer({ token, page: page });
    setbanner1(getbanner1.data[0].img_list);
    // console.log("39", getbanner1.data[0].img_list);
  }
  useEffect(() => {
    allbanner();
  }, [token]);
  useEffect(() => {
    getUser();
  }, []);

  async function getUser() {
    const useracc = await getActionUser(token);

    setUser(useracc.data);
  }

  if (user == null) {
    return <View />;
  }

  return (
    <View>
      <View style={styles.view}>
        <View>
          <Text style={styles.textlv}>
            Lv
            <Text style={styles.text}>
              {autolize_Lv(parseInt(user.user_accounts.total_distance)).lv > 60
                ? lvstate.length == 0
                  ? 60
                  : lvstate[0].status == true
                  ? autolize_Lv(parseInt(user.user_accounts.total_distance)).lv
                  : 60
                : autolize_Lv(parseInt(user.user_accounts.total_distance)).lv}
            </Text>
          </Text>
          <Text style={styles.textrank}>
            Rank :{" "}
            {autolize_Lv(parseInt(user.user_accounts.total_distance)).lv > 60
              ? lvstate.length == 0
                ? "D"
                : lvstate[0].status == true
                ? autolize_Lv(parseInt(user.user_accounts.total_distance)).rank
                : "D"
              : "D"}{" "}
            Class
          </Text>
        </View>
        <Image
          // resizeMode={"stretch"}
          source={
            autolize_Lv(parseInt(user.user_accounts.total_distance)).lv > 60
              ? lvstate.length == 0
                ? {
                    uri: "https://ssr-project.s3.ap-southeast-1.amazonaws.com/rank/D/50.png",
                  }
                : lvstate[0].status == true
                ? autolize_Lv(parseInt(user.user_accounts.total_distance)).grid
                : {
                    uri: "https://ssr-project.s3.ap-southeast-1.amazonaws.com/rank/D/50.png",
                  }
              : autolize_Lv(parseInt(user.user_accounts.total_distance)).grid
          }
          style={{ width: 90, height: 98 }}
        />
      </View>
      <View style={styles.linelevel}>
        <View
          style={[
            styles.linelevelrank,
            {
              width:
                width *
                ((parseInt(user.user_accounts.total_distance) /
                  nextautolize_Lv(parseInt(user.user_accounts.total_distance))
                    .exp) *
                  0.93),
            },
          ]}
        />
      </View>
      <View style={{ paddingHorizontal: 15, marginTop: 5 }}>
        <Text style={styles.textpoint}>
          {(parseInt(user.user_accounts.total_distance) / 1000).toFixed(2)}/
          {nextautolize_Lv(parseInt(user.user_accounts.total_distance)).exp /
            1000}{" "}
          km
        </Text>
      </View>
    </View>
  );
}
const styles = StyleSheet.create({
  view: {
    flexDirection: "row",
    justifyContent: "space-between",
    paddingHorizontal: 20,
    marginTop: 20,
  },
  textlv: {
    fontFamily: "Prompt-Regular",
    fontSize: 50,
    color: "#FCC81D",
    marginRight: 10,
  },
  text: {
    fontFamily: "Prompt-Regular",
    fontSize: 50,
    color: "#393939",
  },
  textrank: {
    fontFamily: "Prompt-Regular",
    fontSize: 33,
    color: "#393939",
  },
  linelevel: {
    width: width * 0.93,
    height: 5,
    backgroundColor: "#000000",
    marginTop: 30,
    marginHorizontal: 10,
    alignSelf: "center",
  },
  linelevelrank: {
    width: width * 0.4,
    height: 4.5,
    backgroundColor: "#F8CA36",
    alignSelf: "flex-start",
  },
  textpoint: {
    fontFamily: "Prompt-Regular",
    fontSize: 24,
    color: "#393939",
  },
  imgsup: {
    width: width,
    height: height * 0.2,
    marginTop: 20,
  },
});