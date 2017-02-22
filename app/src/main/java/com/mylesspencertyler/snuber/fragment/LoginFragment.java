package com.mylesspencertyler.snuber.fragment;

import com.mylesspencertyler.snuber.R;
import com.mylesspencertyler.snuber.utils.LoginType;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by tyler on 2/22/2017.
 */

public class LoginFragment extends Fragment implements View.OnClickListener {

    private Button studentButton;
    private Button driverButton;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);

        studentButton = (Button) view.findViewById(R.id.btn_student);
        studentButton.setOnClickListener(this);

        driverButton = (Button) view.findViewById(R.id.btn_driver);
        driverButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_student:
                switchFragment(LoginType.STUDENT);
                break;
            case R.id.btn_driver:
                switchFragment(LoginType.DRIVER);
                break;
        }
    }

    private void switchFragment(LoginType loginType) {
        switch (loginType) {
            case STUDENT:
                final FragmentTransaction ftStudent = getFragmentManager().beginTransaction();
                ftStudent.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

                ftStudent.replace(R.id.fragment_container, new StudentFragment(), "StudentFragmentTag");
                ftStudent.addToBackStack(null);
                ftStudent.commit();
                break;
            case DRIVER:
                final FragmentTransaction ftDriver = getFragmentManager().beginTransaction();
                ftDriver.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

                ftDriver.replace(R.id.fragment_container, new DriverFragment(), "DriverFragmentTag");
                ftDriver.addToBackStack(null);
                ftDriver.commit();
                break;
        }
    }
}
