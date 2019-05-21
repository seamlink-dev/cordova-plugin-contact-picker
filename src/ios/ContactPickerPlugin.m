#import "ContactPickerPlugin.h"
#import "libPhoneNumber-iOS/NBPhoneNumberUtil.h"

@implementation ContactPickerPlugin {
    CNContactPickerViewController *_contactPickerController;
    NBPhoneNumberUtil *_phoneUtil;
}

- (void)pluginInitialize {
    _phoneUtil = [[NBPhoneNumberUtil alloc] init];
    _contactPickerController = [[CNContactPickerViewController alloc]init];
    _contactPickerController.predicateForSelectionOfContact = [NSPredicate predicateWithFormat:@"phoneNumbers.@count >= 1"];
    _contactPickerController.delegate = self;
}

- (void)requestContact:(CDVInvokedUrlCommand *)command {
    if (self.contactCallbackId) {
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                                    messageAsString:@"Only single contact request is allowed"];
        [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
        return;
    }

    [self.getTopPresentedViewController presentViewController:_contactPickerController animated:YES completion:nil];

    self.contactCallbackId = command.callbackId;

    NSDictionary* settings = [command.arguments objectAtIndex:0];
    self.lastCountry = settings[@"country"];
}

- (void)contactPicker:(CNContactPickerViewController *)picker didSelectContact:(nonnull CNContact *)contact {
    NSString* displayName = contact.givenName;
    NSString* lastName = contact.familyName;
    if (lastName) {
        displayName = [NSString stringWithFormat:@"%@ %@", displayName, lastName];
        displayName = [displayName stringByTrimmingCharactersInSet: [NSCharacterSet whitespaceAndNewlineCharacterSet]];
    }

    NSMutableArray *phoneNumbers = [[NSMutableArray alloc] init];
    for (int j = 0, k = (int)[contact.phoneNumbers count]; j < k; ++j) {
        CNPhoneNumber *phoneNumber = [[contact.phoneNumbers objectAtIndex:j] valueForKey:@"value"];
        NSString* phoneNumberString = [phoneNumber valueForKey:@"digits"];

        NSError *err = nil;
        NBPhoneNumber *myNumber = [_phoneUtil parse:phoneNumberString defaultRegion:self.lastCountry error:&err];
        if (!err) {
            NSString *phoneNumberNormalized = [_phoneUtil format:myNumber
                                                    numberFormat:NBEPhoneNumberFormatE164 error:&err];
            if (!err) {
                phoneNumberString = phoneNumberNormalized;
            }
        }

        [phoneNumbers addObject:phoneNumberString];
    }

    if (self.contactCallbackId) {
        CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:@{@"displayName": displayName, @"phoneNumbers": phoneNumbers}];
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
