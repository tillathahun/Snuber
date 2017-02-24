package com.mylesspencertyler.snuber.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
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

import com.mylesspencertyler.snuber.R;
import com.mylesspencertyler.snuber.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * Created by tyler on 2/22/2017.
 */

public class DriverFragment extends Fragment implements View.OnClickListener {

    private static final String PREF_USERNAME_DRIVER = "username_driver";
    private EditText editText;
    private Button nextButton;
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_driver, container, false);

        editText = (EditText) view.findViewById(R.id.driverName_editText);

        editText.setFocusableInTouchMode(true);
        editText.requestFocus();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (actionId == EditorInfo.IME_ACTION_DONE))) {
                    Log.i("Driver Fragment","Enter pressed");
                }
                return false;
            }
        });

        nextButton = (Button) view.findViewById(R.id.btn_driver_next);
        nextButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {

        Utils.saveSharedSetting(getActivity(), PREF_USERNAME_DRIVER, editText.getText().toString());

        final FragmentTransaction ftStudent = getFragmentManager().beginTransaction();
        ftStudent.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

        ftStudent.replace(R.id.fragment_container, new UserImageFragment(), "UserImageFragmentTag");
        ftStudent.addToBackStack(null);
        ftStudent.commit();
    }
}
