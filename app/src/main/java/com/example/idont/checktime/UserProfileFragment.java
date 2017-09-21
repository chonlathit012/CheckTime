package com.example.idont.checktime;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment implements Test {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    StorageReference storageReference;

    String uid;
    String json = "";
    String jsonReceive = "";

    TextView textViewFirstname;
    TextView textViewLastname;
    TextView textViewEmail;
    TextView textViewAge;
    TextView textViewPhonenumber;
    ImageView imageView;

    public static UserProfileFragment newInstance() {
        UserProfileFragment fragment = new UserProfileFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewEmail = (TextView) view.findViewById(R.id.textViewEmail);
        textViewFirstname = (TextView) view.findViewById(R.id.textViewFirstname);
        textViewLastname = (TextView) view.findViewById(R.id.textViewLastname);
        textViewAge = (TextView) view.findViewById(R.id.textViewAge);
        textViewPhonenumber = (TextView) view.findViewById(R.id.textViewPhonenumber);
        imageView = (ImageView) view.findViewById(R.id.imageView);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        getUserData();

        Button buttonSignOut = (Button) view.findViewById(R.id.buttonSignOut);
        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to sign out?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ProgressDialog progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("Sign out..");
                        progressDialog.show();
                        firebaseAuth.signOut();
                        progressDialog.dismiss();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                        getActivity().finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

    }

    public void showUserData() {

        Gson gson = new Gson();
        UserProfileReceive userProfileReceive = gson.fromJson(jsonReceive, UserProfileReceive.class);

        String email = userProfileReceive.getData().getEmail();
        String firstname = userProfileReceive.getData().getFirst_name();
        String lastname = userProfileReceive.getData().getLast_name();
        String birthday = userProfileReceive.getData().getBirthday();
        String phonenumber = userProfileReceive.getData().getPhone_number();
        String photo_url = userProfileReceive.getData().getPhoto_url();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int yearUser = 0;

        int length = birthday.length();
        if (length == 8) {
            String bi = birthday.substring(4);
            yearUser = Integer.parseInt(bi);
        } else if (length == 9) {
            String bi = birthday.substring(5);
            yearUser = Integer.parseInt(bi);
        } else if (length == 10) {
            String bi = birthday.substring(6);
            yearUser = Integer.parseInt(bi);
        }

        if (photo_url != null) {
            Glide.with(getActivity())
                    .load(photo_url)
                    .into(imageView);
        }

        textViewFirstname.setText("Firstname : " + firstname);
        textViewLastname.setText("Lastname : " + lastname);
        textViewEmail.setText("Email : " + email);
        textViewAge.setText("Age : " + (year - yearUser));
        textViewPhonenumber.setText("Phone : " + phonenumber);

    }

    public void getUserData() {
        Gson gson = new Gson();
        UserProfileData userProfileData = new UserProfileData();
        userProfileData.setId(uid);

        UserProfileSend userProfileSend = new UserProfileSend();
        userProfileSend.setTarget("profile_data");
        userProfileSend.setData(userProfileData);

        json = gson.toJson(userProfileSend);

        new HttpTask(UserProfileFragment.this).execute(json);

    }

    @Override
    public void onPost(String s) {
        if (s.equals("No connection.")) {
            android.app.AlertDialog.Builder builder =
                    new android.app.AlertDialog.Builder(getActivity());
            builder.setMessage("No connection.");
            builder.setPositiveButton("Close app", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    getActivity().finishAffinity();
                    System.exit(0);
                }
            });
            builder.show();
        } else {
            jsonReceive = s;

            Gson gson = new Gson();
            CheckTitle checkTitle = gson.fromJson(jsonReceive, CheckTitle.class);

            String message = checkTitle.getMessage();

            switch (message) {
                case "Get profile data success.":
                    showUserData();
                    break;
                case "No data.":
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    break;
                case "No user.":
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getUserData();
    }
}
