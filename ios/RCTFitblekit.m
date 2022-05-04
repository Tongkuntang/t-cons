#import "RCTFit.h"
#import <UIKit/UIKit.h>

@interface Fitblekit ()<RCTBridgeModule,FBKApiScanDevicesDelegate> {
    FBKApiScanDevices *m_scanBlue;
    UITableView       *m_searchEquTableView;
    NSMutableArray<FBKApiScanDevicesDelegate>   *m_searchEquArray;
    NSMutableArray<FBKApiScanDevicesDelegate>   *m_chooseArray;
    RCTResponseSenderBlock calls;
}

@end

@implementation Fitblekit

//export the name of the native module as 'Device' since no explicit name is mentioned
RCT_EXPORT_MODULE(Fitblekit);

- (NSArray<NSString *> *)supportedEvents {
  return @[@"CONNECT"];
}

//exports a method getDeviceName to javascript
RCT_EXPORT_METHOD(getDeviceName:(RCTResponseSenderBlock)callback){
 @try{
   NSString *deviceName = [[UIDevice currentDevice] name];
   callback(@[[NSNull null], deviceName]);
 }
 @catch(NSException *exception){
   callback(@[exception.reason, [NSNull null]]);
 }
}



RCT_EXPORT_METHOD(onScanStart:(RCTResponseSenderBlock)callback){
 @try{
//   UITableView       *m_searchEquTableView;
   NSMutableArray<FBKApiScanDevicesDelegate> *m_searchEquArray;
   NSMutableArray<FBKApiScanDevicesDelegate> *m_chooseArray;
//   FBKApiOldBand  *m_oldBandApi;
   m_chooseArray = [[NSMutableArray<FBKApiScanDevicesDelegate> alloc] init];
   m_searchEquArray = [[NSMutableArray<FBKApiScanDevicesDelegate> alloc] init];
   m_scanBlue = [[FBKApiScanDevices alloc] init];
   m_scanBlue.delegate = self;

//   m_oldBandApi = [[FBKApiOldBand alloc] init];
//   m_oldBandApi.delegate = self;
//   m_oldBandApi.dataSource = self;

//   NSString *deviceId= @"18B8A18E-DDC7-4F46-AB06-F731EA803B42";
//   [m_oldBandApi startConnectBleApi:deviceId andIdType:DeviceIdUUID];
   
   callback(@[[NSNull null], m_searchEquArray]);
 }
 @catch(NSException *exception){
   callback(@[exception.reason, [NSNull null]]);
 }
}

- (void)getDeviceList:(NSArray *)deviceList {
    [m_searchEquArray removeAllObjects];
    [m_searchEquArray addObjectsFromArray:deviceList];
    [self sendEventWithName:@"CONNECT" body:@{@"name": deviceList}];
}

- (void)phoneBleStatus:(BOOL)isPoweredOn {
  if (isPoweredOn) {
      [m_scanBlue startScanBleApi:nil isRealTimeDevice:NO withRssi:70];
  }
}

@end

