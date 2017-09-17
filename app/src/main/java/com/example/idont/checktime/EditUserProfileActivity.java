package com.example.idont.checktime;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

public class EditUserProfileActivity extends AppCompatActivity implements Test {

    private static final int GALLERY_INTENT = 2;

    Toolbar toolbar;
    ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    StorageReference storageReference;

    Calendar calendar;
    int year, month, day;

    ImageView imageView;
    ImageView imageView2;
    Uri selectedImage;
    Bitmap bitmap = null;

    CoordinatorLayout coordinatorLayout;

    Button buttonDate;
    Button buttonSelectPhoto;

    EditText editTextFirstname;
    EditText editTextLastname;
    EditText editTextPhoneNumber;
    TextInputLayout textInputLayoutFirstname;
    TextInputLayout textInputLayoutLastname;
    TextInputLayout textInputLayoutPhonenumber;

    String json = "";
    String jsonReceive = "";
    String uid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        progressDialog = new ProgressDialog(this);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        toolbar = (Toolbar) findViewById(R.id.toolbar_edit_user);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Profile");

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView2 = (ImageView) findViewById(R.id.imageView2);

        buttonDate = (Button) findViewById(R.id.buttonSelectDate);
        buttonSelectPhoto = (Button) findViewById(R.id.buttonSelectPhoto);

        editTextFirstname = (EditText) findViewById(R.id.editFirstname);
        editTextLastname = (EditText) findViewById(R.id.editLastname);
        editTextPhoneNumber = (EditText) findViewById(R.id.editPhonenumber);

        textInputLayoutFirstname = (TextInputLayout) findViewById(R.id.input_editFirstname);
        textInputLayoutLastname = (TextInputLayout) findViewById(R.id.input_editLastname);
        textInputLayoutPhonenumber = (TextInputLayout) findViewById(R.id.input_editPhonenumber);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        getUserData();

        buttonSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        buttonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(EditUserProfileActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                buttonDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

    }

    public void editProfile() {

        if (selectedImage != null) {

            progressDialog.setMessage("Uploading....");
            progressDialog.show();

            StorageReference imageRef = storageReference.child("userProfile/" + uid); // id of user

            imageRef.putFile(selectedImage).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.dismiss();
                    Toast.makeText(EditUserProfileActivity.this, "Profile update failed.", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(EditUserProfileActivity.this, "User profile updated.", Toast.LENGTH_SHORT).show();
                    finish();
                    gson();
                }
            });
        } else {
            gson();
            Toast.makeText(EditUserProfileActivity.this, "User profile updated.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void gson() {
        Gson gson = new Gson();

        EditProfileData editProfileData = new EditProfileData();
        editProfileData.setId(uid);
        editProfileData.setFirst_name(editTextFirstname.getText().toString());
        editProfileData.setLast_name(editTextLastname.getText().toString());
        editProfileData.setPhone_number(editTextPhoneNumber.getText().toString());
        editProfileData.setBirthday(buttonDate.getText().toString());

        EditProfileSend editProfileSend = new EditProfileSend();
        editProfileSend.setTarget("edit_profile_data");
        editProfileSend.setData(editProfileData);

        json = gson.toJson(editProfileSend);

        new HttpTask(EditUserProfileActivity.this).execute(json);
    }

    public void showData() {
        Gson gson = new Gson();
        UserProfileReceive userProfileReceive = gson.fromJson(jsonReceive, UserProfileReceive.class);

        String firstname = userProfileReceive.getData().getFirst_name();
        String lastname = userProfileReceive.getData().getLast_name();
        String birthday = userProfileReceive.getData().getBirthday();
        String phonenumber = userProfileReceive.getData().getPhone_number();

        StorageReference imageRef = storageReference.child("userProfile/"+uid); // id of user

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(EditUserProfileActivity.this)
                        .load(uri)
                        .into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
//                Toast.makeText(EditUserProfileActivity.this, "Download failed.", Toast.LENGTH_SHORT).show();
            }
        });

        editTextFirstname.setText(firstname);
        editTextLastname.setText(lastname);
        editTextPhoneNumber.setText(phonenumber);
        buttonDate.setText(birthday);

    }

    public void getUserData() {
        Gson gson = new Gson();
        UserProfileData userProfileData = new UserProfileData();
        userProfileData.setId(uid);

        UserProfileSend userProfileSend = new UserProfileSend();
        userProfileSend.setTarget("profile_data");
        userProfileSend.setData(userProfileData);

        json = gson.toJson(userProfileSend);

        new HttpTask(EditUserProfileActivity.this).execute(json);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_edit_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save) {
            editProfile();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPost(String s) {
        jsonReceive = s;

        Gson gson = new Gson();
        CheckTitle checkTitle = gson.fromJson(jsonReceive, CheckTitle.class);

        String message = checkTitle.getMessage();

        switch (message) {
            case "Get profile data success.":
                showData();
                break;
            case "Update profile success.":
                break;
            case "No data.":
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                break;
            case "No id.":
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                break;
            case "No user.":
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                break;
            case "Update profile failed.":
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if (requestCode == GALLERY_INTENT && resultCode == Activity.RESULT_OK) {
            selectedImage = data.getData();
            bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        imageView.setImageBitmap(bitmap);

    }
}
