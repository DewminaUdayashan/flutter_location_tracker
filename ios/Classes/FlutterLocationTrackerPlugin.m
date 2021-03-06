#import "FlutterLocationTrackerPlugin.h"
#if __has_include(<flutter_location_tracker/flutter_location_tracker-Swift.h>)
#import <flutter_location_tracker/flutter_location_tracker-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_location_tracker-Swift.h"
#endif

@implementation FlutterLocationTrackerPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterLocationTrackerPlugin registerWithRegistrar:registrar];
}
@end
