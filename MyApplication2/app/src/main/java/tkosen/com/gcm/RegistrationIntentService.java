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

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import tkosen.com.map.R;


/**
 * Deal with registration of the user with the GCM instance.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    /**
     * Indicates that the token has been sent to the server.
     */
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    /**
     * Indicates that the registration with the server is complete.
     */
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String TOKEN = "token";


    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        try {
            // Just in case that onHandleIntent has been triggered several times in short
            // succession.
            synchronized (TAG) {
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.d(TAG, "GCM registration token: " + token);

                // Register to the server and subscribe to the topic of interest.
                Log.d(TAG,"Token arrived : " + token);
                // The list of topics we can subscribe to is being implemented within the server.
                GcmPubSub.getInstance(this).subscribe(token, "/topics/newclient", null);

                final SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(SENT_TOKEN_TO_SERVER, true);
                editor.putString(TOKEN, token);
                editor.apply();
            }
        } catch (IOException e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).apply();

        }
        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

}
