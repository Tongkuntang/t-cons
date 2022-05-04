import React from "react";
import { View, Text } from "react-native";

export default function index({ navigation, route }) {
  const dataEV = route.params.dataEV;
  function onNavigationStateChange(params) {
    console.log(params);
  }
  return (
    <WebView
      source={{
        // uri: url,
        uri: `AmountU: AmountU,
            html: <!DOCTYPE html>
            <html>
            <head>
            <meta charset="utf-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1.0" />
            <title>Payment Demo</title>
            </head>
            <body>
            <form id="payment-form" action="https://sandbox-cdnv3.chillpay.co/Payment/" method="post" role="form" class="form-horizontal">
            <modernpay:widget id="modernpay-widget-container"
            data-merchantid="M032077" data-amount="${AmountU.Amount}" data-orderno="${AmountU.routeno}" data-customerid="${Doctor}"
            data-mobileno="${user.telNo}" data-clientip="49.228.104.160" data-routeno="1" data-currency="764"
            data-description="${AmountU.item}" data-apikey="MFSufQZ8jftF8XjwH9sBV5wd9W6RXsER9ZgtIuY7alf8u3HbSYAt33vtORoh3dOz">
            </modernpay:widget>
            <button type="submit" id="btnSubmit" value="Submit" class="btn">Payment</button>
            </form>
            <script async src="https://sandbox-cdnv3.chillpay.co/js/widgets.js?v=1.00" charset="utf-8"></script>
            </body>
            </html>,
            `,
      }}
      onNavigationStateChange={onNavigationStateChange}
      startInLoadingState
      scalesPageToFit
      javaScriptEnabled
      style={{ flex: 1 }}
    />
  );
}
