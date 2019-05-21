package by.chemerisuk.cordova;

import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds;

import by.chemerisuk.cordova.support.CordovaMethod;
import by.chemerisuk.cordova.support.ReflectiveCordovaPlugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.app.Activity.RESULT_OK;

public class ContactPickerPlugin extends ReflectiveCordovaPlugin {
    private static final String TAG = "ContactPickerPlugin";
    private static final int SELECT_PHONE_NUMBER = 123344;
    private static final String[] CONTACT_PROJECTION = {CommonDataKinds.Phone.DISPLAY_NAME, CommonDataKinds.Phone.NORMALIZED_NUMBER};

    private CallbackContext contactCallback;

    @CordovaMethod
    private void requestContact(JSONObject settings, CallbackContext callbackContext) throws JSONException {
        if (this.contactCallback != null) {
            callbackContext.error("Only single contact request is allowed");
        } else {
            this.contactCallback = callbackContext;

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(CommonDataKinds.Phone.CONTENT_TYPE);
            this.cordova.startActivityForResult(this, intent, SELECT_PHONE_NUMBER);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == SELECT_PHONE_NUMBER && this.contactCallback != null) {
            if (resultCode != RESULT_OK) {
                this.contactCallback.sendPluginResult(
                    new PluginResult(PluginResult.Status.OK, (String)null)
                );
            } else {
                // Get the URI and query the content provider for the phone number
                Cursor cursor = this.cordova.getActivity().getContentResolver().query(intent.getData(), CONTACT_PROJECTION, null, null, null);
                // If the cursor returned is valid, get the phone number
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(CONTACT_PROJECTION[0]);
                    int numberIndex = cursor.getColumnIndex(CONTACT_PROJECTION[1]);
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
}
