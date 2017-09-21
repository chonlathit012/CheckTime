package com.example.idont.checktime;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.Calendar;

public class EmployeeProfileActivity extends AppCompatActivity implements Test{

    Toolbar toolbar;

    StorageReference storageReference;
    ProgressBar progressBar;

    TextView textViewFirstname;
    TextView textViewLastname;
    TextView textViewEmail;
    TextView textViewAge;
    TextView textViewPhonenumber;

    Button buttonShowCalendar;

    ImageView imageView;

    String json = "";
    String jsonReceive = "";
    String employee_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile);

        storageReference = FirebaseStorage.getInstance().getReference();
        progressBar = (ProgressBar) findViewById(R.id.progress);

        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        textViewFirstname = (TextView) findViewById(R.id.textViewFirstname);
        textViewLastname = (TextView) findViewById(R.id.textViewLastname);
        textViewAge = (TextView) findViewById(R.id.textViewAge);
        textViewPhonenumber = (TextView) findViewById(R.id.textViewPhonenumber);

        buttonShowCalendar = (Button) findViewById(R.id.buttonShowCalendar);
        imageView = (ImageView) findViewById(R.id.imageView);

        toolbar = (Toolbar) findViewById(R.id.toolbar_edit_user);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Employee profile");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            employee_id = bundle.getString("employee_id");
        }

        getUserData();

        buttonShowCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployeeProfileActivity.this,CalendarActivity.class);
                intent.putExtra("employee_id",employee_id);
                startActivity(intent);
            }
        });

    }

    public void showUserData() {

        Gson gson = new Gson();
        UserProfileReceive userProfileReceive = gson.fromJson(jsonReceive, UserProfileReceive.class);

        String uid = userProfileReceive.getData().getId();
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
            Glide.with(EmployeeProfileActivity.this)
                    .load(photo_url)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false; // important to return false so the error placeholder can be placed
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageView);
        } else {
            progressBar.setVisibility(View.GONE);
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
        userProfileData.setId(employee_id);

        UserProfileSend userProfileSend = new UserProfileSend();
        userProfileSend.setTarget("profile_data");
        userProfileSend.setData(userProfileData);

        json = gson.toJson(userProfileSend);

        new HttpTask(EmployeeProfileActivity.this).execute(json);

    }

    @Override
    public void onPost(String s) {
        if (s.equals("No connection.")) {
            android.app.AlertDialog.Builder builder =
                    new android.app.AlertDialog.Builder(this);
            builder.setMessage("No connection.");
            builder.setPositiveButton("Close app", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finishAffinity();
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
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    break;
                case "No user.":
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }
}
