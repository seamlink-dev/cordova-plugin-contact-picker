package by.chemerisuk.cordova;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds;

import by.chemerisuk.cordova.support.CordovaMethod;
import by.chemerisuk.cordova.support.ReflectiveCordovaPlugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class ContactPickerPlugin extends ReflectiveCordovaPlugin {
    private static final String TAG = "ContactPickerPlugin";
    private static final int SELECT_CONTACT = 123344;
    private static final String[] CONTACT_FIELDS_PROJECTION;
    private static final Map<String, String> CONTACT_FIELDS_MAP = new HashMap<String, String>();
    static {
        CONTACT_FIELDS_MAP.put(CommonDataKinds.Phone.DISPLAY_NAME, "displayName");
        CONTACT_FIELDS_MAP.put(CommonDataKinds.Phone.NORMALIZED_NUMBER, "phoneNumber");
        CONTACT_FIELDS_PROJECTION = CONTACT_FIELDS_MAP.keySet().toArray(new String[]{});
    }

    private CallbackContext contactCallback;

    @CordovaMethod
    private void requestContact(CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        if (this.contactCallback != null) {
            callbackContext.error("Only single contact request is allowed");
        } else {
            this.contactCallback = callbackContext;

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(CommonDataKinds.Phone.CONTENT_TYPE);
            cordova.startActivityForResult(this, Intent.createChooser(intent, "Select contact"), SELECT_CONTACT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == SELECT_CONTACT && this.contactCallback != null) {
             if (resultCode != Activity.RESULT_OK) {
                this.contactCallback.sendPluginResult(
                    new PluginResult(PluginResult.Status.OK, (String)null)
                );
            } else {
                // Get the URI and query the content provider for the phone number
                Cursor cursor = this.cordova.getActivity().getContentResolver().query(intent.getData(),
                        CONTACT_FIELDS_PROJECTION, null, null, null);
                // If the cursor returned is valid, get the phone number
                if (cursor != null && cursor.moveToFirst()) {
                    JSONObject result = new JSONObject();
                    try {
                        for (Map.Entry<String, String> entry : CONTACT_FIELDS_MAP.entrySet()) {
                            int columnIndex = cursor.getColumnIndex(entry.getKey());
                            result.put(entry.getValue(), cursor.getString(columnIndex));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    this.contactCallback.success(result);
                }
            }

            this.contactCallback = null;
        }
    }
}
