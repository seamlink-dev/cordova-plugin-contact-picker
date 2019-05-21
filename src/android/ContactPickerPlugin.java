package by.chemerisuk.cordova;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds;
import android.util.Log;

import by.chemerisuk.cordova.support.CordovaMethod;
import by.chemerisuk.cordova.support.ReflectiveCordovaPlugin;

import org.apache.cordova.CallbackContext;
import org.json.JSONException;
import org.json.JSONObject;

import static android.app.Activity.RESULT_OK;

public class ContactPickerPlugin extends ReflectiveCordovaPlugin {
    private static final String TAG = "ContactPickerPlugin";
    private static final int SELECT_PHONE_NUMBER = 123344;

    private CallbackContext contactCallback;

    @CordovaMethod
    private void requestContact(JSONObject settings, CallbackContext callbackContext) throws JSONException {
        this.contactCallback = callbackContext;

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(CommonDataKinds.Phone.CONTENT_TYPE);
        this.cordova.startActivityForResult(this, intent, SELECT_PHONE_NUMBER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == SELECT_PHONE_NUMBER && resultCode == RESULT_OK) {
            // make sure callback exists
            if (this.contactCallback == null) return;
            // Get the URI and query the content provider for the phone number
            Uri contactUri = intent.getData();
            String[] projection = new String[] {CommonDataKinds.Phone.DISPLAY_NAME, CommonDataKinds.Phone.NORMALIZED_NUMBER};
            Cursor cursor = this.cordova.getActivity().getContentResolver().query(contactUri, projection, null, null, null);
            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(projection[0]);
                int numberIndex = cursor.getColumnIndex(projection[1]);
                JSONObject result = new JSONObject();
                try {
                    result.put("displayName", cursor.getString(nameIndex));
                    result.put("phoneNumber", cursor.getString(numberIndex));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                this.contactCallback.success(result);
                this.contactCallback = null;
            }
        }
    }
}
