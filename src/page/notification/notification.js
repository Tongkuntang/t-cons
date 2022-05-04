import React, { useState } from "react";
import {
  View,
  Text,
  SafeAreaView,
  Platform,
  StyleSheet,
  Image,
  Dimensions,
  TouchableOpacity,
  Modal,
  FlatList,
  ScrollView,
  Share,
} from "react-native";
import {
  FontAwesome5,
  Fontisto,
  FontAwesome,
  MaterialIcons,
  Ionicons,
  MaterialCommunityIcons,
} from "@expo/vector-icons";
import Headerevent from "../components/headerevent";
const { width, height } = Dimensions.get("window");

const DATA = [
  {
    title: "EVENT",
    imageURL: {
      uri: "https://ssr-project.s3.ap-southeast-1.amazonaws.com/icon_noti1.png",
    },
  },
  {
    title: "EVENT",
    imageURL: {
      uri: "https://ssr-project.s3.ap-southeast-1.amazonaws.com/icon_noti1.png",
    },
  },
  {
    title: "FRIEND",
    imageURL: {
      uri: "https://ssr-project.s3.ap-southeast-1.amazonaws.com/icon_noti2.png",
    },
  },
];

export default function notification({ navigation }) {
  return (
    <View style={styles.container}>
      <SafeAreaView />
      <View
        style={{
          marginTop: Platform.OS === "ios" ? 0 : 0,
        }}
      >
        <Headerevent item="อีเว้นทั้งหมด" navigation={navigation} />
      </View>
      <View>
        <FlatList
          data={DATA}
          style={{ backgroundColor: "white", width: width }}
          renderItem={({ item, index }) => {
            return (
              <TouchableOpacity
                style={{
                  width: width,
                  height: 80,
                  marginTop: 10,
                  paddingHorizontal: 10,
                  alignSelf: "center",
                  backgroundColor: "#FFFFFF",
                  justifyContent: "center",
                  borderBottomWidth: 1,
                  borderBottomColor: "#707070",
                }}
              >
                <View style={{ flexDirection: "row" }}>
                  <Image
                    source={item.imageURL}
                    style={{
                      width: 50,
                      height: 50,
                      alignSelf: "center",
                      resizeMode: "contain",
                      justifyContent: "center",
                    }}
                  />

                  <View
                    style={{
                      marginLeft: 10,
                      alignSelf: "center",
                      width: width * 0.8,
                    }}
                  >
                    <View
                      style={{
                        alignSelf: "flex-end",
                        flexDirection: "row",
                        justifyContent: "center",
                      }}
                    >
                      <Image
                        source={{
                          uri: "https://ssr-project.s3.ap-southeast-1.amazonaws.com/clock.png",
                        }}
                        style={{
                          width: 10,
                          height: 10,
                          resizeMode: "contain",
                          marginRight: 10,
                          alignSelf: "center",
                        }}
                      />
                      <Text
                        style={{
                          fontFamily: "Prompt-Regular",
                          fontSize: 10,
                          color: "#434343",
                          alignSelf: "center",
                        }}
                      >
                        12:00 AM
                      </Text>
                    </View>
                    <Text
                      style={{
                        fontSize: 14,
                        fontFamily: "Prompt-Regular",
                        color: "#434343",
                      }}
                    >
                      {item.title}
                    </Text>

                    <Text
                      style={{
                        fontFamily: "Prompt-Regular",
                        fontSize: 12,
                        color: "#434343",
                      }}
                    >
                      We have an Exciting Offers for you near to yo...
                    </Text>
                  </View>
                </View>
              </TouchableOpacity>
            );
          }}
        />
      </View>
    </View>
  );
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
  },
});
