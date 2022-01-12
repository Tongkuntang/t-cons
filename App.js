import { NavigationContainer } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import * as SplashScreen from "expo-splash-screen";
import { extendTheme, NativeBaseProvider } from "native-base";
import React, { useEffect, useState } from "react";
import { DetailScreen, HomeScreen, ListScreen } from "./src/screens";

const Stack = createNativeStackNavigator();
const theme = extendTheme({
  fontConfig: {
    Roboto: {
      100: {
        normal: "Roboto-Light",
        italic: "Roboto-LightItalic",
      },
      200: {
        normal: "Roboto-Light",
        italic: "Roboto-LightItalic",
      },
      300: {
        normal: "Roboto-Light",
        italic: "Roboto-LightItalic",
      },
      400: {
        normal: "Roboto-Regular",
        italic: "Roboto-Italic",
      },
      500: {
        normal: "Roboto-Medium",
      },
      600: {
        normal: "Roboto-Medium",
        italic: "Roboto-MediumItalic",
      },
    },
  },
  colors: {
    brand: {
      900: "#24275C",
    },
  },
});

const App = () => {
  const [appIsReady, setAppIsReady] = useState(false);

  useEffect(() => {
    async function prepare() {
      try {
        await SplashScreen.preventAutoHideAsync();
        await new Promise((resolve) => setTimeout(resolve, 2000));
      } catch (e) {
        console.log(e);
      } finally {
        setAppIsReady(true);
      }
    }

    prepare();
  }, []);

  useEffect(async () => {
    if (appIsReady) {
      await SplashScreen.hideAsync();
    }
  }, [appIsReady]);

  return (
    <NativeBaseProvider theme={theme}>
      <NavigationContainer>
        <Stack.Navigator initialRouteName="Home">
          <Stack.Screen
            component={HomeScreen}
            name="Home"
            options={{ title: "เลือกบริเวณต้องการค้นหา" }}
          />
          <Stack.Screen
            component={ListScreen}
            name="List"
            options={{ title: "ร้านวัสดุก่อสร้างใกล้เคียง" }}
          />
          <Stack.Screen
            component={DetailScreen}
            name="Detail"
            options={{ title: "รายละเอียดร้านค้า" }}
          />
        </Stack.Navigator>
      </NavigationContainer>
    </NativeBaseProvider>
  );
};

export default App;
