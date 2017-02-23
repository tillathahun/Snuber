package com.mylesspencertyler.snuber.fragment;

import android.content.Context;
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

import com.mylesspencertyler.snuber.R;

/**
 * Created by tyler on 2/22/2017.
 */

public class DriverFragment extends Fragment {

    private EditText editText;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_driver, container, false);

        EditText editText = (EditText) view.findViewById(R.id.driverName_editText);

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

        return view;
    }
}
