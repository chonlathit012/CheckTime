package com.example.idont.checktime;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

public class CreateCompanyActivity extends AppCompatActivity implements Test {

    Toolbar toolbar;

    private static final int GALLERY_INTENT = 2;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    StorageReference storageReference;

    ProgressDialog progressDialog;

    Calendar calendar;
    int hour, minute;
    Uri selectedImage;
    Bitmap bitmap = null;

    String uid = null;
    String json = "";
    String jsonReceive = "";
    String logo_url = "1";

    TextView textViewDisplayName;

    Button buttonStartTime;
    Button buttonFinishTime;
    Button buttonSelectPhoto;

    ImageView imageView;

    EditText editTextCompanyName;
    TextInputLayout textInputLayoutCompanyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_company);

        progressDialog = new ProgressDialog(this);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        toolbar = (Toolbar) findViewById(R.id.toolbar_edit_user);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Company");

        textViewDisplayName = (TextView) findViewById(R.id.textDisplayName);

        buttonStartTime = (Button) findViewById(R.id.buttonSelectTimeStart);
        buttonFinishTime = (Button) findViewById(R.id.buttonSelectTimeFinish);
        buttonSelectPhoto = (Button) findViewById(R.id.buttonSelectPhoto);

        imageView = (ImageView) findViewById(R.id.imageView);

        editTextCompanyName = (EditText) findViewById(R.id.editCompanyName);
        textInputLayoutCompanyName = (TextInputLayout) findViewById(R.id.input_editCompanyName);

        buttonSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        buttonStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calendar = Calendar.getInstance();
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateCompanyActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hour,
                                                  int minute) {
                                view.setIs24HourView(true);
                                if (minute > 10) {
                                    buttonStartTime.setText(hour + ":" + minute);
                                } else {
                                    buttonStartTime.setText(hour + ":0" + minute);
                                }
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        buttonFinishTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                hour = calendar.get(Calendar.HOUR_OF_DAY);
                minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateCompanyActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hour,
                                                  int minute) {
                                view.setIs24HourView(true);
                                if (minute > 10) {
                                    buttonFinishTime.setText(hour + ":" + minute);
                                } else {
                                    buttonFinishTime.setText(hour + ":0" + minute);
                                }

                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        getDisplayName();
    }

    public void showDisplayName() {
        Gson gson = new Gson();
        DisplayNameReceive displayNameReceive = gson.fromJson(jsonReceive, DisplayNameReceive.class);
        String display_name = displayNameReceive.getData().getDisplay_name();

        textViewDisplayName.setText("Display name : " + display_name);
    }

    public void getDisplayName() {
        Gson gson = new Gson();
        DisplayNameData displayNameData = new DisplayNameData();
        displayNameData.setId(uid);

        DisplayNameSend displayNameSend = new DisplayNameSend();
        displayNameSend.setTarget("show_display_name");
        displayNameSend.setData(displayNameData);

        json = gson.toJson(displayNameSend);

        new HttpTask(CreateCompanyActivity.this).execute(json);
    }

    public void imageUpload() {

        if (selectedImage != null) {

            progressDialog.setMessage("Uploading....");
            progressDialog.show();

            StorageReference imageRef = storageReference.child("logoCompany/" + uid); // id of user

            imageRef.putFile(selectedImage).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.dismiss();
                    Toast.makeText(CreateCompanyActivity.this, "Create failed.", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    finish();
                    logo_url = taskSnapshot.getDownloadUrl().toString();
                    createCompany();
                }
            });
        } else {
            createCompany();
            finish();
        }
    }

    public void createCompany() {
        Gson gson = new Gson();

        CreateCompanyData createCompanyData = new CreateCompanyData();
        createCompanyData.setId(uid);
        createCompanyData.setCompany_name(editTextCompanyName.getText().toString());
        createCompanyData.setStart_time(buttonStartTime.getText().toString());
        createCompanyData.setFinish_time(buttonFinishTime.getText().toString());
        createCompanyData.setLogo_url(logo_url);

        CreateCompanySend createCompanySend = new CreateCompanySend();
        createCompanySend.setTarget("create_company");
        createCompanySend.setData(createCompanyData);

        json = gson.toJson(createCompanySend);

        new HttpTask(CreateCompanyActivity.this).execute(json);
    }

    @Override
    public void onPost(String s) {
        jsonReceive = s;

        Gson gson = new Gson();
        CheckTitle checkTitle = gson.fromJson(jsonReceive, CheckTitle.class);

        String message = checkTitle.getMessage();
        switch (message) {
            case "Created data success.":
                Intent intent = new Intent(CreateCompanyActivity.this, LoadActivity.class);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
                break;
            case "Company name already exist.":
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                break;
            default:
                showDisplayName();
        }
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
            String company_name = editTextCompanyName.getText().toString();
            String start_time = buttonStartTime.getText().toString();
            String finish_time = buttonFinishTime.getText().toString();

            if (company_name.isEmpty() || start_time.equals("Select Time") || finish_time.equals("Select Time")) {
                Toast.makeText(this, "Please enter data.", Toast.LENGTH_SHORT).show();
            } else{
                imageUpload();
            }
        }
        return super.onOptionsItemSelected(item);
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
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
