package com.example.idont.checktime;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TimePicker;
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

public class SettingManagerFragment extends Fragment implements Test {

    private static final int GALLERY_INTENT = 2;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    StorageReference storageReference;

    EditText editTextCompanyName;
    TextInputLayout textInputEditTextCompanyName;

    CoordinatorLayout coordinatorLayout;

    Button buttonStartTime;
    Button buttonFinishTime;
    Button buttonSave;
    Button buttonSelectPhoto;

    ImageView imageView;

    Calendar calendar;
    int hour, minute;
    Bitmap bitmap = null;

    Uri selectedImage;
    ProgressDialog progressDialog;
    ProgressBar progressBar;

    String json = "";
    String jsonReceive = "";
    String uid;
    String company_id;
    String logo_url = "1";

    public static SettingManagerFragment newInstance() {
        SettingManagerFragment fragment = new SettingManagerFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting_manager, container, false);
    }

    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(getActivity());
        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id
                .coordinatorLayout);

        editTextCompanyName = (EditText) view.findViewById(R.id.editCompanyName);
        textInputEditTextCompanyName = (TextInputLayout) view.findViewById(R.id.input_editCompanyName);

        buttonSave = (Button) view.findViewById(R.id.buttonSave);
        buttonStartTime = (Button) view.findViewById(R.id.buttonSelectTimeStart);
        buttonFinishTime = (Button) view.findViewById(R.id.buttonSelectTimeFinish);
        buttonSelectPhoto = (Button) view.findViewById(R.id.buttonSelectPhoto);
        imageView = (ImageView) view.findViewById(R.id.imageView);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForm();

                String companyName = editTextCompanyName.getText().toString().trim();

                if (!companyName.isEmpty()) {
                    editComapanyData();
                }
            }
        });

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

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
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

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
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

        editTextCompanyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateCompanyName();
            }
        });

        getCompanyId();

    }

    public void gson() {
        Gson gson = new Gson();

        EditCompanyData editCompanyData = new EditCompanyData();
        editCompanyData.setCompany_id(company_id);
        editCompanyData.setCompany_name(editTextCompanyName.getText().toString());
        editCompanyData.setStart_time(buttonStartTime.getText().toString());
        editCompanyData.setFinish_time(buttonFinishTime.getText().toString());
        editCompanyData.setLogo_url(logo_url);

        EditCompanySend editCompanySend = new EditCompanySend();
        editCompanySend.setTarget("edit_company_data");
        editCompanySend.setData(editCompanyData);

        json = gson.toJson(editCompanySend);

        new HttpTask(SettingManagerFragment.this).execute(json);
    }

    public void editComapanyData() {
        if (selectedImage != null) {

            progressDialog.setMessage("Uploading....");
            progressDialog.show();

            StorageReference imageRef = storageReference.child("logoCompany/" + company_id); // id of user

            imageRef.putFile(selectedImage).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Update failed.", Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    logo_url = taskSnapshot.getDownloadUrl().toString();
                    gson();
                }
            });
        } else {
            gson();
        }
    }

    public void getCompanyId() {
        Gson gson = new Gson();
        GetCompanyIdData getCompanyIdData = new GetCompanyIdData();
        getCompanyIdData.setId(uid);

        GetCompanyIdSend getCompanyIdSend = new GetCompanyIdSend();
        getCompanyIdSend.setTarget("get_company_id");
        getCompanyIdSend.setData(getCompanyIdData);

        json = gson.toJson(getCompanyIdSend);

        new HttpTask(SettingManagerFragment.this).execute(json);
    }

    public void showCompanyId() {
        Gson gson = new Gson();
        GetCompanyIdReceive getCompanyIdReceive = gson.fromJson(jsonReceive, GetCompanyIdReceive.class);

        company_id = getCompanyIdReceive.getData().getCompany_id();

        getCompanyData();
    }

    public void getCompanyData() {
        Gson gson = new Gson();
        SettingManagerData settingManagerData = new SettingManagerData();
        settingManagerData.setCompany_id(company_id);

        SettingManagetSend settingManagetSend = new SettingManagetSend();
        settingManagetSend.setTarget("company_data");
        settingManagetSend.setData(settingManagerData);

        json = gson.toJson(settingManagetSend);

        new HttpTask(SettingManagerFragment.this).execute(json);
    }

    public void showCompanyData() {
        Gson gson = new Gson();
        SettingManagerReceive settingManagerReceive = gson.fromJson(jsonReceive, SettingManagerReceive.class);

        String company_name = settingManagerReceive.getData().getCompany_name();
        String start_time = settingManagerReceive.getData().getStart_time();
        String finish_time = settingManagerReceive.getData().getFinish_time();
        String logo_url = settingManagerReceive.getData().getLogo_url();

        if (logo_url != null) {
            Glide.with(getActivity())
                    .load(logo_url)
                    .error(R.drawable.alert)
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

        editTextCompanyName.setText(company_name);
        buttonStartTime.setText(start_time);
        buttonFinishTime.setText(finish_time);
    }

    private boolean validateCompanyName() {
        String companyName = editTextCompanyName.getText().toString().trim();
        if (companyName.isEmpty()) {
            textInputEditTextCompanyName.setError(getString(R.string.err_msg_company_name));
            requestFocus(editTextCompanyName);
            return false;
        } else {
            textInputEditTextCompanyName.setErrorEnabled(false);
        }
        return true;
    }

    public void checkForm() {
        if (!validateCompanyName()) {
            return;
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
                case "Get company_id success.":
                    showCompanyId();
                    break;
                case "No company.":
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    break;
                case "No user.":
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    break;
                case "Get company_data success.":
                    showCompanyData();
                    break;
                case "No company data.":
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    break;
                case "company name already exist.":
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    break;
                case "update successful.":
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    break;
                case "update failed.":
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if (requestCode == GALLERY_INTENT && resultCode == Activity.RESULT_OK) {
            selectedImage = data.getData();
            bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
