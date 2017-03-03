package com.mylesspencertyler.snuber.utils;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.mylesspencertyler.snuber.activity.LoginActivity;

/**
 * Created by tyler on 3/2/2017.
 */

public class FirebaseHelper extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FirebaseHelper", "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String refreshedToken) {
        if(Boolean.valueOf(Utils.readSharedSetting(this, LoginActivity.PREF_USER_LOGGED_IN, "false"))) {
            // store this in the server, call a snuber client function here that updates the firebase token

            // save to shared pref
            Utils.saveSharedSetting(this, LoginActivity.PREF_APP_FIREBASE_TOKEN, refreshedToken);
        } else {
            // want to save this to shared pref
            Utils.saveSharedSetting(this, LoginActivity.PREF_APP_FIREBASE_TOKEN, refreshedToken);
        }
    }
}
