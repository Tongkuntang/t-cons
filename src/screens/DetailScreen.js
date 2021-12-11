/* eslint-disable react-native/no-inline-styles */
import { FontAwesome5 } from "@expo/vector-icons";
import {
  Button,
  Center,
  Container,
  Flex,
  Heading,
  Image,
  Text,
  useToast,
} from "native-base";
import React, { useEffect, useState } from "react";
import { Linking, Platform } from "react-native";
import PagerView from "react-native-pager-view";

const DetailScreen = ({ route }) => {
  const { id } = route.params;
  const toast = useToast();

  const [storeDetail, setStoreDetail] = useState(null);
  const [, setIsloading] = useState(false);

  // function
  const getStoreDetail = async () => {
    try {
      setIsloading(true);

      setStoreDetail({
        id: 1,
        name: "ก.รุ่งเจริญ",
        images: [
          "https://wallpaperaccess.com/full/317501.jpg",
          "https://wallpaperaccess.com/full/317501.jpg",
          "https://wallpaperaccess.com/full/317501.jpg",
        ],
        detail: "detail",
        adress: {
          text: "สุพรรณ",
          coords: {
            latitude: 14.4614911,
            longitude: 100.1702438,
          },
          contact: "0824323453",
        },
      });
    } catch (err) {
      console.error(err);
      toast.show({
        title: "มีบางอย่างผิดพลาด กรุณาลองใหม่",
        status: "error",
      });
    } finally {
      setIsloading(false);
    }
  };
  const openMap = () => {
    const {
      adress: {
        coords: { latitude, longitude },
      },
    } = storeDetail;

    const scheme = Platform.select({
      ios: "maps:0,0?q=",
      android: "geo:0,0?q=",
    });
    const latLng = `${latitude},${longitude}`;
    const label = storeDetail?.name;
    const url = Platform.select({
      ios: `${scheme}${label}@${latLng}`,
      android: `${scheme}${latLng}(${label})`,
    });

    Linking.openURL(url);
  };

  // hook
  useEffect(() => {
    getStoreDetail();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <Flex flex="1">
      <Center marginY="4">
        <Container>
          <Heading>{storeDetail?.name}</Heading>
        </Container>
      </Center>
      <Flex paddingX={4} height="200" position="relative" shadow="4">
        <PagerView style={{ flex: 1 }} initialPage={0}>
          {storeDetail?.images?.map((image, index) => {
            return (
              <Image
                key={index}
                source={{
                  uri: image,
                }}
                alt="store image"
                width="100%"
                height="100%"
                pageMargin="2"
                showPageIndicator
                overScrollMode
                resizeMode="cover"
                borderRadius="lg"
              />
            );
          })}
        </PagerView>
      </Flex>
      <Flex
        margin={4}
        padding={4}
        background="white"
        borderRadius="lg"
        shadow="4"
        flexDirection="row"
      >
        <Flex flex="1">
          <Text fontSize="md">รายละเอียดร้านค้า: {storeDetail?.detail}</Text>
          <Text fontSize="md">สถานที่ตั้ง: {storeDetail?.adress?.text}</Text>
          <Text fontSize="md">ติดต่อ: {storeDetail?.adress?.contact}</Text>
        </Flex>
        <Flex>
          <Button
            height="60"
            width="60"
            borderRadius="30"
            shadow="4"
            background="primary.400"
            onPress={openMap}
            startIcon={
              <FontAwesome5 name="map-marked-alt" size={24} color="white" />
            }
          />
        </Flex>
      </Flex>
    </Flex>
  );
};

export default DetailScreen;
