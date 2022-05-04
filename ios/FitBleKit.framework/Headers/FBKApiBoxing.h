/********************************************************************************
 * 文件名称：FBKApiHubConfig.h
 * 内容摘要：Hub Config API
 * 版本编号：1.0.1
 * 创建日期：2018年07月04日
 ********************************************************************************/

#import "FBKApiBsaeMethod.h"

@protocol FBKApiBoxingDelegate <NSObject>

// 硬件版本号
- (void)armBandVersion:(NSString *)version andDevice:(id)bleDevice;

// 实时拳击
- (void)realtimeFistInfo:(NSDictionary *)fistDic andDevice:(id)bleDevice;

// 大数据
- (void)armBoxingDetailData:(NSDictionary *)dataDic andDevice:(id)bleDevice;

@end


@interface FBKApiBoxing : FBKApiBsaeMethod

// 协议
@property(assign,nonatomic)id <FBKApiBoxingDelegate> delegate;

// 设置时间
- (void)setBoxingTime;

// 获取所有历史数据
- (void)getBoxingTotalHistory;

@end

