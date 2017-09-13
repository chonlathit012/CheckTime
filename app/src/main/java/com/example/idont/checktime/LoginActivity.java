package com.example.idont.checktime;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity implements Test {

    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    CoordinatorLayout coordinatorLayout;

    TextView textViewRegister;
    TextView textViewForgotPassword;
    EditText editTextEmail;
    EditText editTextPassword;
    TextInputLayout textInputLayoutEmail;
    TextInputLayout textInputLayoutPassword;
    Button buttonLogin;

    String uid = null;
    String json = "";
    String jsonReceive = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(LoginActivity.this, LoadActivity.class));
        }

        textViewRegister = (TextView) findViewById(R.id.textViewRegis);
        textViewForgotPassword = (TextView) findViewById(R.id.forgotPassword);
        editTextEmail = (EditText) findViewById(R.id.editEmail);
        editTextPassword = (EditText) findViewById(R.id.editPassword);
        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.input_editEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.input_editPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int name = bundle.getInt("Registered Successful");

            if (name == 1){
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout,"Registered Successful", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });

        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder =
                        new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Forgot password");
                LayoutInflater inflater = getLayoutInflater();

                View view = inflater.inflate(R.layout.dialog_forgot, null);
                builder.setView(view);
                final AlertDialog dialog = builder.create();
                dialog.show();

                final EditText editTextEmailForgot = (EditText) view.findViewById(R.id.editEmailForget);
                Button buttonConfirm = (Button) view.findViewById(R.id.buttonConfirm);
                Button buttonCancel = (Button) view.findViewById(R.id.buttonCancel);

                editTextEmailForgot.addTextChangedListener(new TextWatcher() {
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

                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                buttonConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = editTextEmailForgot.getText().toString().trim();
                        checkForm();
                        if (TextUtils.isEmpty(email)) {
                            Toast.makeText(LoginActivity.this, "Please enter your email",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                        progressDialog.setMessage("Please wait.. ");
                        progressDialog.show();

                        firebaseAuth.sendPasswordResetEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(LoginActivity.this, "Email sent. Please check your email.",
                                                    Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            dialog.dismiss();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Please try again.",
                                                    Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                    }
                });
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForm();
                userLogin();
            }
        });

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
    }

    public void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            return;
        }

        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            finish();
                            firebaseUser = firebaseAuth.getCurrentUser();
                            uid = firebaseUser.getUid();
                            gson();
                            Intent intent = new Intent(LoginActivity.this, LoadActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Email or password incorrect",
                                    Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    public void gson() {
        Gson gson = new Gson();

        UserProfileData userProfileData = new UserProfileData();
        userProfileData.setId(uid);

        UserProfileSend userProfileSend = new UserProfileSend();
        userProfileSend.setTarget("profile_data");
        userProfileSend.setData(userProfileData);

        json = gson.toJson(userProfileSend);

        new HttpTask(LoginActivity.this).execute(json);
    }

    public void checkForm() {
        if (!validateEmail()) {
            return;
        }
        if (!validatePassword()) {
            return;
        }
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

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        LoginActivity.super.onBackPressed();
                    }
                }).create().show();
    }

    @Override
    public void onPost(String s) {
        jsonReceive = s;
    }
}
