/*
 * Copyright Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tkosen.com.gcm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;

/**
 * This service listens for messages from GCM, makes them usable for this application and then
 * sends them to their destination.
 */
public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        if (from == null) {
            Log.w(TAG, "Couldn't determine origin of message. Skipping.");
            return;
        }

        try {
            digestData(data);
        } catch (JSONException e) {
            Log.e(TAG, "onMessageReceived: Could not digest data", e);
        }
    }

    private void digestData(Bundle data) throws JSONException {
        final String action = data.getString("action");
        Log.d(TAG, "Action: " + action);
        if (action == null) {
            Log.w(TAG, "onMessageReceived: Action was null, skipping further processing.");
            return;
        }
        Intent broadcastIntent = new Intent(action);
        switch (action) {
            case GcmAction.GCM_RECEIVED:
                final String code = data.getString("alpha2Code");
                broadcastIntent.putExtra(IntentExtras.NEW_LOCATION, code);
                break;
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

}
