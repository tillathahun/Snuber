package com.mylesspencertyler.snuber.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.*;
import com.mylesspencertyler.snuber.R;
import com.mylesspencertyler.snuber.activity.DriverActivity;
import com.mylesspencertyler.snuber.activity.LoginActivity;
import com.mylesspencertyler.snuber.activity.PagerActivity;
import com.mylesspencertyler.snuber.activity.StudentActivity;
import com.mylesspencertyler.snuber.utils.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

/**
 * Created by tyler on 2/22/2017.
 */

public class LoginViewFragment extends Fragment implements View.OnClickListener {

    private EditText editText_Email;
    private EditText editText_Password;
    private Button nextButton;
    private View view;

    private boolean validEmail;
    private boolean validPass;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_login_view, container, false);

        editText_Email = (EditText) view.findViewById(R.id.loginEmail_editText);
        editText_Password = (EditText) view.findViewById(R.id.loginPassword_editText);

        editText_Email.setFocusableInTouchMode(true);
        editText_Email.requestFocus();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        editText_Email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (actionId == EditorInfo.IME_ACTION_NEXT))) {
                    Log.i("Driver Fragment","Enter pressed");
                    final String email = editText_Email.getText().toString();
                    if(isValidEmail(email)) {
                        editText_Password.setFocusableInTouchMode(true);
                        editText_Password.requestFocus();

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
                return false;
            }
        });

        nextButton = (Button) view.findViewById(R.id.btn_driver_next);
        nextButton.setOnClickListener(this);

        return view;
    }

    // TODO: Finish onClick functionality
    @Override
    public void onClick(View v) {

        // check if fields are valid
        if(areAllValid()) {
            String email = editText_Email.getText().toString();
            String username = email.substring(0, email.length() - 8);
            String password = editText_Password.getText().toString();
            SnuberClient.login(username, password, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if(response.getBoolean("success")) {
                            if(response.getBoolean("is_driver")) {
                                Utils.saveSharedSetting(getActivity(), LoginActivity.PREF_USER_IS_DRIVER, "true");
                                Utils.saveSharedSetting(getActivity(), LoginActivity.PREF_USER_LOGGED_IN, "true");
                                // Redirect to driver page
                                Intent driverIntent = new Intent(getActivity(), DriverActivity.class);
                                startActivity(driverIntent);
                            } else {
                                // store refreshtoken in the server, call a snuber client function here that updates the firebase token
                                Utils.saveSharedSetting(getActivity(), LoginActivity.PREF_USER_IS_DRIVER, "false");
                                Utils.saveSharedSetting(getActivity(), LoginActivity.PREF_USER_LOGGED_IN, "true");
                                Intent studentIntent = new Intent(getActivity(), StudentActivity.class);
                                startActivity(studentIntent);
                            }
                            Toast toast = Toast.makeText(getActivity(), "Logged In!", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            Toast toast = Toast.makeText(getActivity(), "Error Logging In!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e("Snuber Networking", responseString);
                    Toast toast = Toast.makeText(getActivity(), "Login Request Failed", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });


            /**
             * If user exists, do 3 things:
             *  - set the shared pref PREF_USER_LOGGED_IN = true;
             *  - set the shared pref PREF_USER_IS_DRVIER = response.isDriver;
             *  - send the user to the appropriate screen based on isDriver
             *
             *  If the user doesn't exist, show an alert dialog/toast message and
             *  direct the user back to the email text input
             */
        } else {
            // show error toast if fields aren't valid
            Toast toast = Toast.makeText(getActivity(), "Invalid Login Information", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private boolean isValidEmail(String email) {

        String EMAIL_PATTERN = "^[a-zA-Z0-9\\.]+(@wpi.edu)$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);

        if(!matcher.matches()) {
            editText_Email.setError("Must enter valid WPI email");
        } else {
            return true;
        }
        return false;
    }

    private boolean isValidPassword(String password) {
        if(password.length() >= 5) {
            return true;
        } else {
            editText_Password.setError("Password must be at least 5 characters long");
        }
        return false;
    }

    private boolean areAllValid() {
        final String email = editText_Email.getText().toString();
        final String pass = editText_Password.getText().toString();

        validEmail = isValidEmail(email);
        validPass = isValidPassword(pass);

        return validEmail && validPass;
    }
}
