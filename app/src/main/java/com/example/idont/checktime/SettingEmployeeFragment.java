package com.example.idont.checktime;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingEmployeeFragment extends Fragment implements Test {

    private static final String[] TIMES = {"none", "before 5 min", "before 10 min", "before 15 min"};

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    StorageReference storageReference;

    SharedPreferences sharedPreferences;
    ProgressBar progressBar;

    String uid;
    String company_id;
    String json = "";
    String jsonReceive = "";
    String selectTime;

    TextView textViewFirstname;
    TextView textViewLastname;
    TextView textViewEmail;
    TextView textViewAge;
    TextView textViewPhonenumber;

    Button buttonSelectTime;
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

    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String stringValue = sharedPreferences.getString("Time", "Select time");

        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        textViewEmail = (TextView) view.findViewById(R.id.textViewEmail);
        textViewFirstname = (TextView) view.findViewById(R.id.textViewFirstname);
        textViewLastname = (TextView) view.findViewById(R.id.textViewLastname);
        textViewAge = (TextView) view.findViewById(R.id.textViewAge);
        textViewPhonenumber = (TextView) view.findViewById(R.id.textViewPhonenumber);

        buttonSelectTime = (Button) view.findViewById(R.id.buttonSelectTime);
        imageView = (ImageView) view.findViewById(R.id.imageView);

        buttonSelectTime.setText(stringValue);

        getUserData();

        buttonSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                builder.setTitle("Select notification time");
                builder.setSingleChoiceItems(TIMES, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TIMES[which].equals("none")) {
                            selectTime = "Select time";
                        } else {
                            selectTime = TIMES[which];
                        }
                    }
                });
                builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("Time", selectTime);
                        editor.apply();

                        getCompanyId();

                        buttonSelectTime.setText(selectTime);
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("cancel", null);
                builder.create();
                builder.show();
            }
        });

    }

    public void notification(String start, String finish) {
        AlarmManager manager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Date dat = new Date();
        Calendar start_time = Calendar.getInstance();
        Calendar finish_time = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();

        cal_now.setTime(dat);

        SimpleDateFormat formatDateStart = new SimpleDateFormat("HH:mm");
        Date dateNewStart;
        Date dateNewFinish;
        String startTime = "";
        String finishTime = "";
        String startTimeM = "";
        String finishTimeM = "";
        try {
            dateNewStart = formatDateStart.parse(start);
            dateNewFinish = formatDateStart.parse(finish);
            SimpleDateFormat formaterTime = new SimpleDateFormat("HH");
            SimpleDateFormat formaterTimeMinute = new SimpleDateFormat("mm");
            startTime = formaterTime.format(dateNewStart);
            startTimeM = formaterTimeMinute.format(dateNewStart);
            finishTime = formaterTime.format(dateNewFinish);
            finishTimeM = formaterTimeMinute.format(dateNewFinish);

            switch (startTime) {
                case "07":
                    startTime = "7";
                    break;
                case "08":
                    startTime = "8";
                    break;
                case "09":
                    startTime = "9";
                    break;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (!selectTime.equals("Select time")) {
            int startMinute = Integer.parseInt(startTimeM);
            int finishMinute = Integer.parseInt(finishTimeM);

            if (selectTime.equals("before 5 min")) {
                startMinute = startMinute - 5;
                finishMinute = finishMinute - 5;
            } else if (selectTime.equals("before 10 min")) {
                startMinute = startMinute - 10;
                finishMinute = finishMinute - 10;
            } else if (selectTime.equals("before 15 min")) {
                startMinute = startMinute - 15;
                finishMinute = finishMinute - 15;
            }

            start_time.setTime(dat);
            start_time.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime));
            start_time.set(Calendar.MINUTE, startMinute);
            start_time.set(Calendar.SECOND, 0);

            finish_time.setTime(dat);
            finish_time.set(Calendar.HOUR_OF_DAY, Integer.parseInt(finishTime));
            finish_time.set(Calendar.MINUTE, finishMinute);
            finish_time.set(Calendar.SECOND, 0);

            if (start_time.before(cal_now)) {
                start_time.add(Calendar.DATE, 1);
            }

            if (finish_time.before(cal_now)) {
                finish_time.add(Calendar.DATE, 1);
            }

            Intent intentStart = new Intent(getActivity(), AlarmStart.class);
            Intent intentFinish = new Intent(getActivity(), AlarmFinish.class);

            manager.set(AlarmManager.RTC_WAKEUP, start_time.getTimeInMillis(),
                    PendingIntent.getBroadcast(getActivity(), 0, intentStart, 0));
            manager.set(AlarmManager.RTC_WAKEUP, finish_time.getTimeInMillis(),
                    PendingIntent.getBroadcast(getActivity(), 0, intentFinish, 0));
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

        new HttpTask(SettingEmployeeFragment.this).execute(json);
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

        new HttpTask(SettingEmployeeFragment.this).execute(json);
    }

    public void showCompanyData() {
        Gson gson = new Gson();
        SettingManagerReceive settingManagerReceive = gson.fromJson(jsonReceive, SettingManagerReceive.class);

        String start_time = settingManagerReceive.getData().getStart_time();
        String finish_time = settingManagerReceive.getData().getFinish_time();

        notification(start_time, finish_time);

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

        new HttpTask(SettingEmployeeFragment.this).execute(json);

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
                case "Get company_id success.":
                    showCompanyId();
                    break;
                case "No company.":
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    break;
                case "Get company_data success.":
                    showCompanyData();
                    break;
                case "No company data.":
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
