package by.chemerisuk.cordova.firebase;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import by.chemerisuk.cordova.support.CordovaMethod;
import by.chemerisuk.cordova.support.ReflectiveCordovaPlugin;

import org.apache.cordova.CallbackContext;
import org.json.JSONException;
import org.json.JSONObject;


public class ContactPickerPlugin extends ReflectiveCordovaPlugin {
    private static final String TAG = "ContactPickerPlugin";

    @CordovaMethod
    private void open(JSONObject settings, CallbackContext callbackContext) throws JSONException {
        callbackContext.success();
    }
}
