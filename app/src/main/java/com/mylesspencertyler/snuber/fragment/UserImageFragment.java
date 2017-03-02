package com.mylesspencertyler.snuber.fragment;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.mylesspencertyler.snuber.R;
import com.mylesspencertyler.snuber.activity.LoginActivity;
import com.mylesspencertyler.snuber.activity.PagerActivity;
import com.mylesspencertyler.snuber.activity.StudentActivity;
import com.mylesspencertyler.snuber.utils.RoundedImageView;
import com.mylesspencertyler.snuber.utils.SnuberClient;
import com.mylesspencertyler.snuber.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_OK;

/**
 * Created by tyler on 2/24/2017.
 */

public class UserImageFragment extends Fragment implements View.OnClickListener {

    private View view;
    private Button chooseImage;
    private Button signupButton;
    private TextView profileName;
    private ImageView roundedImageView;
    private ImageView profileIcon;

    HashMap<String, String> hmap;

    private SignupListener signupListener;

    private Uri outputFileUri;

    private final int SELECT_PICTURE_REQUEST_CODE = 20;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_userimage, container, false);

        hmap = new HashMap<String, String>();

        hmap.put("name", getArguments().getString("name"));
        hmap.put("email", getArguments().getString("email"));
        hmap.put("password", getArguments().getString("password"));

        chooseImage = (Button) view.findViewById(R.id.btn_userimage);
        chooseImage.setOnClickListener(this);

        profileName = (TextView) view.findViewById(R.id.username_textView);
        profileName.setText(hmap.get("name"));

        roundedImageView = (ImageView) view.findViewById(R.id.profile_picture);
        roundedImageView.setVisibility(View.GONE);

        profileIcon = (ImageView) view.findViewById(R.id.section_img_user);

        signupButton = (Button) view.findViewById(R.id.btn_userimage_signup);

        return view;
    }

    @Override
    public void onClick(View v) {
       openImageIntent();
    }

    private void openImageIntent() {

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();
        final String fname = UUID.randomUUID().toString();
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        startActivityForResult(chooserIntent, SELECT_PICTURE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE_REQUEST_CODE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = outputFileUri;
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                }

                Bitmap uriBitmap = null;

                try {
                    uriBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                    Bitmap croppedBitmap = RoundedImageView.getCroppedBitmap(uriBitmap, (int)getActivity().getResources().getDimension(R.dimen.half_profile_height));
                    BitmapDrawable ob = new BitmapDrawable(getActivity().getResources(), croppedBitmap);
                    roundedImageView.setBackground(ob);
                    profileIcon.setVisibility(View.GONE);
                    roundedImageView.setVisibility(View.VISIBLE);

                    signupListener = new SignupListener(hmap, croppedBitmap);
                    signupButton.setOnClickListener(signupListener);
                    signupButton.setEnabled(true);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }


    private class SignupListener implements View.OnClickListener{

        private HashMap<String, String> hashMap;
        private Bitmap selectedImageBitmap;

        public SignupListener(HashMap hashMap, Bitmap selectedImageBitmap) {
            this.hashMap = hashMap;
            this.selectedImageBitmap = selectedImageBitmap;
        }

        /**
         * This this where we will make the upload of the new user to the server.
         * Also, set shared pref of logged in here.
         * @param v
         */
        // TODO: Finish onClick functionality
        @Override
        public void onClick(View v) {

            /**
             * Create new user in the database. If successful,
             *  - set the shared pref PREF_USER_LOGGED_IN = true;
             *  - set the shared pref PREF_USER_IS_DRIVER = response.isDriver;
             *  - send the user to the appropriate screen based on isDriver
             *
             *  If unsuccessful, toast and return user to the name text input
             */

            String name = this.hashMap.get("name");
            String firstName = "";
            String lastName = "";
            if(name.indexOf(' ') != -1) {
                firstName = name.substring(0, name.indexOf(' '));
                lastName = name.substring(name.indexOf(' ') + 1, name.length());
            }
            String email = this.hashMap.get("email");
            String username = email.substring(0, email.length() - 8);
            String password = this.hashMap.get("password");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            this.selectedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            ByteArrayInputStream image = new ByteArrayInputStream(stream.toByteArray());

            SnuberClient.register(firstName, lastName, email, username, password, image, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    // if the user is successfully created, save the shared preference that the user is logged in
                    try {
                        if(response.getBoolean("success")) {
                            Utils.saveSharedSetting(getActivity(), LoginActivity.PREF_USER_LOGGED_IN, "true");
                            Utils.saveSharedSetting(getActivity(), LoginActivity.PREF_USER_IS_DRIVER, "false");
                            Toast toast = Toast.makeText(getActivity(), "Registered!", Toast.LENGTH_SHORT);
                            toast.show();
                            Intent studentIntent = new Intent(getActivity(), StudentActivity.class);
                            startActivity(studentIntent);
                        } else {
                            Toast toast = Toast.makeText(getActivity(), "Registration Failed!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e("Snuber Networking", responseString);
                    Toast toast = Toast.makeText(getActivity(), "Registration Request Failed", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
    }
}
