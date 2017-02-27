package com.mylesspencertyler.snuber.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

import com.mylesspencertyler.snuber.R;
import com.mylesspencertyler.snuber.utils.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tyler on 2/22/2017.
 */

public class StudentFragment extends Fragment implements View.OnClickListener {

    private EditText editText_Name;
    private EditText editText_Email;
    private EditText editText_Pass;
    private EditText editText_PassConfirm;
    private Button nextButton;
    private View view;

    private boolean validName;
    private boolean validEmail;
    private boolean validPass;
    private boolean validPasswordConfirm;

    public static final String PREF_USERNAME_STUDENT = "username_student";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_student, container, false);

        editText_Name = (EditText) view.findViewById(R.id.studentName_editText);
        editText_Email = (EditText) view.findViewById(R.id.studentEmail_editText);
        editText_Pass = (EditText) view.findViewById(R.id.studentPassword_editText);
        editText_PassConfirm = (EditText) view.findViewById(R.id.studentPasswordConfirm_editText);

        editText_Name.setFocusableInTouchMode(true);
        editText_Name.requestFocus();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        editText_Name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (actionId == EditorInfo.IME_ACTION_NEXT))) {
                    Log.i("Student Fragment","Next pressed");
                    final String name = editText_Name.getText().toString();
                    if(isValidName(name)){
                        editText_Email.setFocusableInTouchMode(true);
                        editText_Email.requestFocus();

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
                return false;
            }
        });

        editText_Email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (actionId == EditorInfo.IME_ACTION_NEXT))) {
                    Log.i("Student Fragment","Next pressed");
                    final String email = editText_Email.getText().toString();
                    if(isValidEmail(email)) {
                        editText_Pass.setFocusableInTouchMode(true);
                        editText_Pass.requestFocus();

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
                return false;
            }
        });

        editText_Pass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (actionId == EditorInfo.IME_ACTION_NEXT))) {
                    Log.i("Student Fragment","Next pressed");
                    final String pass = editText_Pass.getText().toString();
                    if(isValidPassword(pass)) {
                        editText_PassConfirm.setFocusableInTouchMode(true);
                        editText_PassConfirm.requestFocus();

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                }
                return false;
            }
        });

        nextButton = (Button) view.findViewById(R.id.btn_student_next);
        nextButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        if(areAllValid()){
            //Utils.saveSharedSetting(getActivity(), PREF_USERNAME_STUDENT, editText_Name.getText().toString());

            final FragmentTransaction ftStudent = getFragmentManager().beginTransaction();
            ftStudent.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

            Bundle args = new Bundle();
            UserImageFragment userImageFragment = new UserImageFragment();
            args.putString("name", editText_Name.getText().toString());
            args.putString("email", editText_Email.getText().toString());
            args.putString("password", editText_Pass.getText().toString());
            userImageFragment.setArguments(args);

            ftStudent.replace(R.id.fragment_container, userImageFragment, "UserImageFragmentTag");
            ftStudent.addToBackStack(null);
            ftStudent.commit();
        } else {
            Toast toast = Toast.makeText(getActivity(), "Invalid Login Information", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private boolean areAllValid() {
        final String name = editText_Name.getText().toString();
        final String email = editText_Email.getText().toString();
        final String pass = editText_Pass.getText().toString();
        final String passConfirm = editText_PassConfirm.getText().toString();

        validName = isValidName(name);
        validEmail = isValidEmail(email);
        validPass = isValidPassword(pass);
        validPasswordConfirm = isValidPasswordConfirm(pass, passConfirm);

        return validName && validEmail && validPass && validPasswordConfirm;
    }

    private boolean isValidName(String name) {
        if(name.length() > 0) {
            return true;
        } else {
            editText_Name.setError("Invalid Name");
        }
        return false;
    }

    //This is a bit hardcoded, but that's okay for now
    private boolean isValidEmail(String email) {
//        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
//                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

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
            editText_Pass.setError("Password must be at least 5 characters long");
        }
        return false;
    }


    private boolean isValidPasswordConfirm(String password, String passwordConfirm) {
        if(!password.equals(passwordConfirm)){
            editText_PassConfirm.setError("Emails do not match");
        } else {
            return true;
        }
        return false;
    }
}
