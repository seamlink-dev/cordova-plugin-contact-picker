#import "ContactPickerPlugin.h"
#import "libPhoneNumber-iOS/NBPhoneNumberUtil.h"

@implementation ContactPickerPlugin {
    CNContactPickerViewController* _contactPickerController;
    CNContactFormatter* _contactFormatter;
    NBPhoneNumberUtil* _phoneUtil;
}

- (void)pluginInitialize {
    _phoneUtil = [[NBPhoneNumberUtil alloc] init];
    _contactFormatter = [[CNContactFormatter alloc] init];

    _contactPickerController = [[CNContactPickerViewController alloc] init];
    _contactPickerController.displayedPropertyKeys = @[CNContactPhoneNumbersKey];
    _contactPickerController.predicateForSelectionOfContact = [NSPredicate predicateWithFormat:@"phoneNumbers.@count >= 1"];
    _contactPickerController.delegate = self;
}

- (void)requestContact:(CDVInvokedUrlCommand *)command {
    if (self.contactCallbackId) {
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                                    messageAsString:@"Only single contact request is allowed"];
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    } else {
        [self.getTopPresentedViewController presentViewController:_contactPickerController animated:YES completion:nil];

        self.contactCallbackId = command.callbackId;

        NSDictionary* settings = [command.arguments objectAtIndex:0];
        self.lastCountry = settings[@"country"];
    }
}

- (void)contactPicker:(CNContactPickerViewController *)picker didSelectContactProperty:(nonnull CNContactProperty *)contactProperty {
    NSString* displayName = [_contactFormatter stringFromContact:contactProperty.contact];
    NSString* phoneNumberString = [contactProperty.value valueForKey:@"digits"];
    NSError *err = nil;
    NBPhoneNumber *parsedNumber = [_phoneUtil parse:phoneNumberString
                                      defaultRegion:self.lastCountry error:&err];
    if (!err) {
        NSString *phoneNumberNormalized = [_phoneUtil format:parsedNumber
                                                numberFormat:NBEPhoneNumberFormatE164 error:&err];
        if (!err) {
            phoneNumberString = phoneNumberNormalized;
        }
    }

    if (self.contactCallbackId) {
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:@{@"displayName": displayName, @"phoneNumber": phoneNumberString}];
        [self.commandDelegate sendPluginResult:result callbackId:self.contactCallbackId];
        self.contactCallbackId = nil;
    }
}

- (void)contactPickerDidCancel:(CNContactPickerViewController *)picker {
    [_contactPickerController dismissViewControllerAnimated:YES completion:nil];
    if (self.contactCallbackId) {
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:nil];
        [self.commandDelegate sendPluginResult:result callbackId:self.contactCallbackId];
        self.contactCallbackId = nil;
    }
}

-(UIViewController *)getTopPresentedViewController {
    UIViewController *presentingViewController = self.viewController;
    while(presentingViewController.presentedViewController != nil && ![presentingViewController.presentedViewController isBeingDismissed])
    {
        presentingViewController = presentingViewController.presentedViewController;
    }
    return presentingViewController;
}

@end
