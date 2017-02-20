package com.mylesspencertyler.snuber.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mylesspencertyler.snuber.R;
import com.mylesspencertyler.snuber.utils.Utils;

public class LoginActivity extends AppCompatActivity {

    public static final String PREF_USER_FIRST_TIME = "user_first_time";
    boolean isUserFirstTime;


    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

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

        setContentView(R.layout.activity_login);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        /*
         * If back pressed, close the app, don't go back to the PagerActivity
         */
        finish();
    }
}
