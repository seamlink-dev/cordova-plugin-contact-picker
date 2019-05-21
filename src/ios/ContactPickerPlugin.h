#import <Cordova/CDV.h>

@interface ContactPickerPlugin : CDVPlugin

- (void)requestContact:(CDVInvokedUrlCommand*)command;

@end
