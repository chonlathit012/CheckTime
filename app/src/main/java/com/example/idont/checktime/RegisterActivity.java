package com.example.idont.checktime;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

public class RegisterActivity extends AppCompatActivity implements Test {

    Calendar calendar;
    int year, month, day;

    Button buttonRegis;
    Button buttonDate;

    TextView textViewBack;
    EditText editTextEmail;
    EditText editTextPassword;
    EditText editTextRePassword;
    EditText editTextFirstname;
    EditText editTextLastname;
    EditText editTextPhoneNumber;
    TextInputLayout textInputLayoutEmail;
    TextInputLayout textInputLayoutPassword;
    TextInputLayout textInputLayoutRePassword;
    TextInputLayout textInputLayoutFirstname;
    TextInputLayout textInputLayoutLastname;
    TextInputLayout textInputLayoutPhonenumber;

    CoordinatorLayout coordinatorLayout;

    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String uid = null;
    String json = "";
    String jsonReceive = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        buttonRegis = (Button) findViewById(R.id.buttonRegister);
        buttonDate = (Button) findViewById(R.id.buttonSelectDate);

        textViewBack = (TextView) findViewById(R.id.textViewBack);
        editTextEmail = (EditText) findViewById(R.id.editEmail);
        editTextPassword = (EditText) findViewById(R.id.editPassword);
        editTextRePassword = (EditText) findViewById(R.id.editRePassword);
        editTextFirstname = (EditText) findViewById(R.id.editFirstname);
        editTextLastname = (EditText) findViewById(R.id.editLastname);
        editTextPhoneNumber = (EditText) findViewById(R.id.editPhonenumber);
        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.input_editEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.input_editPassword);
        textInputLayoutRePassword = (TextInputLayout) findViewById(R.id.input_editRePassword);
        textInputLayoutFirstname = (TextInputLayout) findViewById(R.id.input_editFirstname);
        textInputLayoutLastname = (TextInputLayout) findViewById(R.id.input_editLastname);
        textInputLayoutPhonenumber = (TextInputLayout) findViewById(R.id.input_editPhonenumber);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        buttonDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
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

        textViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        buttonRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForm();

                String rePassword = editTextRePassword.getText().toString().trim();
                String firstName = editTextFirstname.getText().toString().trim();
                String lastName = editTextLastname.getText().toString().trim();
                String phoneNumber = editTextPhoneNumber.getText().toString().trim();

                if (!rePassword.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty() && !phoneNumber.isEmpty()
                        && phoneNumber.length() > 9) {
                    if (!buttonDate.getText().toString().equals("Select Date")) {
                        regisUser();
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Please select birthday", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            }
        });

        textChangedListener();
    }

    public void gson() {
        Gson gson = new Gson();

        RegisterData registerData = new RegisterData();
        registerData.setId(uid);
        registerData.setEmail(editTextEmail.getText().toString());
        registerData.setPassword(editTextPassword.getText().toString());
        registerData.setFirstname(editTextFirstname.getText().toString());
        registerData.setLastname(editTextLastname.getText().toString());
        registerData.setPhonenumber(editTextPhoneNumber.getText().toString());
        registerData.setBirthday(buttonDate.getText().toString());

        RegisterSend registerSend = new RegisterSend();
        registerSend.setTarget("register");
        registerSend.setRegisterData(registerData);

        json = gson.toJson(registerSend);

        new HttpTask(RegisterActivity.this).execute(json);
    }

    public void regisUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            return;
        }

        progressDialog.setMessage("Registering ...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            finish();
                            firebaseUser = firebaseAuth.getCurrentUser();
                            uid = firebaseUser.getUid();
                            firebaseAuth.signOut();
                            gson();

                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            intent.putExtra("Registered Successful", 1);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar snackbar = Snackbar
                                    .make(coordinatorLayout, "Email is already", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    public void checkForm() {
        if (!validateEmail()) {
            return;
        } else if (!validatePassword()) {
            return;
        } else if (!validateRePassword()) {
            return;
        } else if (!validateFirstname()) {
            return;
        } else if (!validateLastname()) {
            return;
        } else if (!validatePhonenuumber()) {
            return;
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

    private boolean validateEmail() {
        String email = editTextEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            textInputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(editTextEmail);
            return false;
        } else {
            textInputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        String password = editTextPassword.getText().toString().trim();
        if (password.isEmpty()) {
            textInputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(editTextPassword);
            return false;
        } else if (password.length() < 6) {
            textInputLayoutPassword.setError(getString(R.string.err_msg_passwordless));
            requestFocus(editTextPassword);
            return false;
        } else {
            textInputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateRePassword() {
        String password = editTextPassword.getText().toString().trim();
        String repassword = editTextRePassword.getText().toString().trim();
        if (repassword.isEmpty()) {
            textInputLayoutRePassword.setError(getString(R.string.err_msg_password));
            requestFocus(editTextRePassword);
            return false;
        } else if (repassword.length() < 6) {
            textInputLayoutRePassword.setError(getString(R.string.err_msg_passwordless));
            requestFocus(editTextRePassword);
            return false;
        } else if (!password.equals(repassword)) {
            textInputLayoutRePassword.setError(getString(R.string.err_msg_repassword));
            requestFocus(editTextRePassword);
            return false;
        } else {
            textInputLayoutRePassword.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void textChangedListener() {
        editTextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEmail();
            }
        });

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validatePassword();
            }
        });

        editTextRePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateRePassword();
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

    @Override
    public void onPost(String s) {
        jsonReceive = s;

    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
