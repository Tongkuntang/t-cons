export default {
  name: "t-cons",
  slug: "t-cons",
  version: "1.0.0",
  orientation: "portrait",
  icon: "./assets/icon.png",
  splash: {
    image: "./assets/splash.png",
    resizeMode: "contain",
    backgroundColor: "#ffffff",
  },
  updates: {
    fallbackToCacheTimeout: 0,
  },
  assetBundlePatterns: ["**/*"],
  ios: {
    supportsTablet: true,
  },
  android: {
    adaptiveIcon: {
      foregroundImage: "./assets/adaptive-icon.png",
      backgroundColor: "#FFFFFF",
    },
    package: "com.witeseb.t_cons",
    versionCode: 1,
  },
  web: {
    favicon: "./assets/favicon.png",
  },
  extra: {
    GOOGLE_MAP_API_KEY: "AIzaSyC04B8E84MbzmNhZAnvUtPbTSom6aYJmOw",
  },
};
