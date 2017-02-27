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

public class LoginMainFragment extends Fragment implements View.OnClickListener {

    private Button signupButton;
    private Button loginButton;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login_main, container, false);

        signupButton = (Button) view.findViewById(R.id.btn_signup);
        signupButton.setOnClickListener(this);

        loginButton = (Button) view.findViewById(R.id.btn_login);
        loginButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signup:
                switchFragment(LoginType.SIGNUP);
                break;
            case R.id.btn_login:
                switchFragment(LoginType.LOGIN);
                break;
        }
    }

    private void switchFragment(LoginType loginType) {
        switch (loginType) {
            case SIGNUP:
                final FragmentTransaction ftStudent = getFragmentManager().beginTransaction();
                ftStudent.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

                ftStudent.replace(R.id.fragment_container, new SignupFragment(), "StudentFragmentTag");
                ftStudent.addToBackStack(null);
                ftStudent.commit();
                break;
            case LOGIN:
                final FragmentTransaction ftDriver = getFragmentManager().beginTransaction();
                ftDriver.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);

                ftDriver.replace(R.id.fragment_container, new LoginViewFragment(), "DriverFragmentTag");
                ftDriver.addToBackStack(null);
                ftDriver.commit();
                break;
        }
    }
}
