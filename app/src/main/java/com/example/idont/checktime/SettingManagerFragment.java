package com.example.idont.checktime;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.Calendar;

public class SettingManagerFragment extends Fragment implements Test {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    EditText editTextCompanyName;
    TextInputEditText textInputEditTextCompanyName;

    CoordinatorLayout coordinatorLayout;

    Button buttonStartTime;
    Button buttonFinishTime;
    Button buttonSave;

    Calendar calendar;
    int hour, minute;

    String json = "";
    String jsonReceive = "";
    String uid;
    String company_id;

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

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id
                .coordinatorLayout);

        editTextCompanyName = (EditText) view.findViewById(R.id.editCompanyName);
//        textInputEditTextCompanyName = (TextInputEditText) view.findViewById(R.id.input_editCompanyName);

        buttonSave = (Button) view.findViewById(R.id.buttonSave);
        buttonStartTime = (Button) view.findViewById(R.id.buttonSelectTimeStart);
        buttonFinishTime = (Button) view.findViewById(R.id.buttonSelectTimeFinish);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editComapanyData();
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

        getCompanyId();

    }

    public void editComapanyData() {
        Gson gson = new Gson();

        EditCompanyData editCompanyData = new EditCompanyData();
        editCompanyData.setCompany_id(company_id);
        editCompanyData.setCompany_name(editTextCompanyName.getText().toString());
        editCompanyData.setStart_time(buttonStartTime.getText().toString());
        editCompanyData.setFinish_time(buttonFinishTime.getText().toString());

        EditCompanySend editCompanySend = new EditCompanySend();
        editCompanySend.setTarget("edit_company_data");
        editCompanySend.setData(editCompanyData);

        json = gson.toJson(editCompanySend);

        new HttpTask(SettingManagerFragment.this).execute(json);
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

        editTextCompanyName.setText(company_name);
        buttonStartTime.setText(start_time);
        buttonFinishTime.setText(finish_time);
    }

    @Override
    public void onPost(String s) {
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
