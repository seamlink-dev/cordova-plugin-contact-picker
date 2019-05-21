#import "ContactPickerPlugin.h"

@implementation ContactPickerPlugin

- (void)open:(CDVInvokedUrlCommand *)command {
    NSDictionary* settings = [command.arguments objectAtIndex:0];

    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

@end
