package com.example.idont.checktime;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingEmployeeFragment extends Fragment implements Test {

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

    public static SettingEmployeeFragment newInstance() {
        SettingEmployeeFragment fragment = new SettingEmployeeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting_employee, container, false);
    }

    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        textViewEmail = (TextView) view.findViewById(R.id.textViewEmail);
        textViewFirstname = (TextView) view.findViewById(R.id.textViewFirstname);
        textViewLastname = (TextView) view.findViewById(R.id.textViewLastname);
        textViewAge = (TextView) view.findViewById(R.id.textViewAge);
        textViewPhonenumber = (TextView) view.findViewById(R.id.textViewPhonenumber);
        imageView = (ImageView) view.findViewById(R.id.imageView);

        getUserData();

    }

    public void showUserData() {

        Gson gson = new Gson();
        UserProfileReceive userProfileReceive = gson.fromJson(jsonReceive, UserProfileReceive.class);

        String email = userProfileReceive.getData().getEmail();
        String firstname = userProfileReceive.getData().getFirst_name();
        String lastname = userProfileReceive.getData().getLast_name();
        String birthday = userProfileReceive.getData().getBirthday();
        String phonenumber = userProfileReceive.getData().getPhone_number();

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

        StorageReference imageRef = storageReference.child("userProfile/"+uid); // id of user

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getActivity())
                        .load(uri)
                        .into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
//                Toast.makeText(EditUserProfileActivity.this, "Download failed.", Toast.LENGTH_SHORT).show();
            }
        });

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

        new HttpTask(SettingEmployeeFragment.this).execute(json);

    }

    @Override
    public void onPost(String s) {
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
