#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(GooglePlacesSdk, NSObject)

RCT_EXTERN_METHOD(initialize: (NSString *)apiKey)
RCT_EXTERN_METHOD(fetchPredictions: (NSString *)query
                  filterOptions: (NSDictionary *)filterOptions
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(searchByText: (NSString *)query
                  filterOptions: (NSDictionary *)filterOptions
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(fetchPlaceByID: (NSString *)placeID
                  fields: (NSArray*)fields
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(fetchPlaceByID: (NSString *)placeID
                  fields: (NSArray*)fields
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(startNewSession:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(clearSession:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(searchNearby:  (NSDictionary *)options
                  includedTypes: (NSArray*)includedTypes
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)


+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

@end
