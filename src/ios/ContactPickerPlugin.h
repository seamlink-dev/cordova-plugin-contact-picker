#import <Cordova/CDV.h>
#import <ContactsUI/ContactsUI.h>

@interface ContactPickerPlugin : CDVPlugin<CNContactPickerDelegate>

- (void)requestContact:(CDVInvokedUrlCommand*)command;

@property (nonatomic, copy) NSString *contactCallbackId;
@property (nonatomic, copy) NSString *lastCountry;

@end
