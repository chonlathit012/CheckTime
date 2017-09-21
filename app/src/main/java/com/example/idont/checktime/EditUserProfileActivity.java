package com.example.idont.checktime;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Calendar;

public class EditUserProfileActivity extends AppCompatActivity implements Test {

    private static final int GALLERY_INTENT = 2;

    Toolbar toolbar;
    ProgressDialog progressDialog;
    ProgressBar progressBar;

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
    String url_photo = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
        setupUI(findViewById(R.id.layout));

        progressDialog = new ProgressDialog(this);
        progressBar = (ProgressBar) findViewById(R.id.progress);

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

        editTextFirstname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateFirstname();
            }
        });

        editTextLastname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateLastname();
            }
        });
        editTextPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validatePhonenuumber();
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
                    url_photo = taskSnapshot.getDownloadUrl().toString();
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
        editProfileData.setPhoto_url(url_photo);

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
        String photo_url = userProfileReceive.getData().getPhoto_url();

        if (photo_url != null) {
            Glide.with(EditUserProfileActivity.this)
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
        }

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

    public void checkForm() {
        if (!validateFirstname()) {
            return;
        } else if (!validateLastname()) {
            return;
        } else if (!validatePhonenuumber()) {
            return;
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateFirstname() {
        String firstName = editTextFirstname.getText().toString().trim();
        if (firstName.isEmpty()) {
            textInputLayoutFirstname.setError(getString(R.string.err_msg_firstname));
            requestFocus(editTextFirstname);
            return false;
        } else {
            textInputLayoutFirstname.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateLastname() {
        String lastname = editTextLastname.getText().toString().trim();
        if (lastname.isEmpty()) {
            textInputLayoutLastname.setError(getString(R.string.err_msg_lastname));
            requestFocus(editTextLastname);
            return false;
        } else {
            textInputLayoutLastname.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePhonenuumber() {
        String phoneNumber = editTextPhoneNumber.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            textInputLayoutPhonenumber.setError(getString(R.string.err_msg_phone_number));
            requestFocus(editTextPhoneNumber);
            return false;
        } else if (phoneNumber.length() < 9) {
            textInputLayoutPhonenumber.setError(getString(R.string.err_msg_phone_less));
            requestFocus(editTextPhoneNumber);
            return false;
        } else {
            textInputLayoutPhonenumber.setErrorEnabled(false);
        }
        return true;
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
            checkForm();

            String firstName = editTextFirstname.getText().toString().trim();
            String lastName = editTextLastname.getText().toString().trim();
            String phoneNumber = editTextPhoneNumber.getText().toString().trim();

            if (!firstName.isEmpty() && !lastName.isEmpty() && !phoneNumber.isEmpty() && phoneNumber.length() > 8) {
                if (!buttonDate.getText().toString().equals("Select Date")) {
                    editProfile();
                } else {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Please select birthday", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        }
        return super.onOptionsItemSelected(item);
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
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(EditUserProfileActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
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
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

}
