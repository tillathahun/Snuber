package com.mylesspencertyler.snuber.activity;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.mylesspencertyler.snuber.R;
import com.mylesspencertyler.snuber.fragment.LoginMainFragment;
import com.mylesspencertyler.snuber.utils.Utils;

public class LoginActivity extends FragmentActivity {

    public static final String PREF_USER_FIRST_TIME = "user_first_time";
    public static final String PREF_USER_LOGGED_IN = "user_logged_in";
    public static final String PREF_USER_IS_DRIVER = "user_is_driver";
    public static final String PREF_APP_FIREBASE_TOKEN = "user_firebase_token";

    boolean isUserFirstTime, isUserLoggedIn, isUserDriver;

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Determine if the app has been opened before. If it hasn't, open the PagerActivity
         */
        isUserFirstTime = Boolean.valueOf(Utils.readSharedSetting(LoginActivity.this, PREF_USER_FIRST_TIME, "true"));

        if (isUserFirstTime) {
            Intent introIntent = new Intent(LoginActivity.this, PagerActivity.class);
            introIntent.putExtra(PREF_USER_FIRST_TIME, isUserFirstTime);
            startActivity(introIntent);
        }

        Log.e("UNGBUNG", "THIS IS BEING CALLED");
        isUserLoggedIn = Boolean.valueOf(Utils.readSharedSetting(LoginActivity.this, PREF_USER_LOGGED_IN, "false"));

        if(isUserLoggedIn) {
            //check if user is a driver or a student, send them to the appropriate place.
            isUserDriver = Boolean.valueOf(Utils.readSharedSetting(LoginActivity.this, PREF_USER_IS_DRIVER, "false"));
            if(isUserDriver) {
                //send to driver page
                Intent driverIntent = new Intent(this, DriverActivity.class);
                startActivity(driverIntent);
            } else {
                //send to the student page
                Intent studentIntent = new Intent(this, StudentActivity.class);
                startActivity(studentIntent);
            }

        } else {
            setContentView(R.layout.activity_login);

            // Check that the activity is using the layout version with the fragment_container FrameLayout
            if (findViewById(R.id.fragment_container) != null) {
                // if we are being restored from a previous state, then we dont need to do anything and should
                // return or else we could end up with overlapping fragments.
                if (savedInstanceState != null)
                    return;

                // Create an instance of editorFrag
                LoginMainFragment loginFragment = new LoginMainFragment();

                // add fragment to the fragment container layout
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, loginFragment).commit();
            }
        }
    }
}
