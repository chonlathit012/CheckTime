package com.example.idont.checktime;

import android.*;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckTimeEmployeeFragment extends Fragment implements Test {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    Button buttonCheckTime;

    String json = "";
    String jsonReceive = "";
    String uid;
    String message;
    String start_time;
    String finish_time;
    String wifi = "58:8d:09:e2:a5:81";
    String checkWifi;

    int state = 0;

    double lat = 0.0;
    double lng = 0.0;

    WifiManager wifiManager;
    List<ScanResult> wifiList;
    Data[] data;

    public static CheckTimeEmployeeFragment newInstance() {
        CheckTimeEmployeeFragment fragment = new CheckTimeEmployeeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_check_time_employee, container, false);
    }

    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        buttonCheckTime = (Button) view.findViewById(R.id.buttonCheckTime);

        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 123);

        buttonCheckTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GpsTracker gpsTracker = new GpsTracker(getActivity());
                Location location = gpsTracker.getLocation();
                if (location != null) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                }

                if (checkWifi != null || (lat < 13.72238400 && lat > 13.7215001 && lng > 100.5068001 && lng < 100.5073648)) {
                    if (state == 0) {
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(getActivity());
                        builder.setMessage("Are you sure you want to check in?");
                        builder.setPositiveButton("Check in", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                checkIn();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                    } else if (state == 1) {
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(getActivity());
                        builder.setMessage("Are you sure you want to check out?");
                        builder.setPositiveButton("Check out", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                checkOut();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.show();
                    }
                } else {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(getActivity());
                    builder.setMessage("Can't checktime Please try again.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
                    builder.show();
                }
            }
        });

        getStateButton();
        detectWifi();
    }

    public void checkIn() {
        Gson gson = new Gson();
        CheckInSend checkInSend = new CheckInSend();
        checkInSend.setTarget("check_in");
        checkInSend.setId(uid);

        json = gson.toJson(checkInSend);

        new HttpTask(CheckTimeEmployeeFragment.this).execute(json);
    }

    public void checkOut() {
        Gson gson = new Gson();
        CheckOutSend checkOutSend = new CheckOutSend();
        checkOutSend.setTarget("check_out");
        checkOutSend.setId(uid);

        json = gson.toJson(checkOutSend);

        new HttpTask(CheckTimeEmployeeFragment.this).execute(json);
    }

    public void getStateButton() {
        Gson gson = new Gson();
        StateButtonData stateButtonData = new StateButtonData();
        stateButtonData.setId(uid);

        StateButtonSend stateButtonSend = new StateButtonSend();
        stateButtonSend.setTarget("state_button");
        stateButtonSend.setData(stateButtonData);

        json = gson.toJson(stateButtonSend);

        new HttpTask(CheckTimeEmployeeFragment.this).execute(json);
    }

    public void checkStateButton() {
        Gson gson = new Gson();
        StateButtonReceive stateButtonReceive = gson.fromJson(jsonReceive, StateButtonReceive.class);
        finish_time = stateButtonReceive.getData().getFinish_time();
        start_time = stateButtonReceive.getData().getStart_time();

        if (finish_time == null) {
            buttonCheckTime.setBackgroundResource(R.color.btn_red_color);
            buttonCheckTime.setText("CHECK OUT");
            state = 1;
        } else {
            buttonCheckTime.setBackgroundResource(R.color.btn_gray_color);
            buttonCheckTime.setText("Wait Tomorrow");
            state = 2;
        }
    }

    public void detectWifi() {

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "Permission failed.", Toast.LENGTH_SHORT).show();
        }

        wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        wifiList = wifiManager.getScanResults();

        for (int i = 0; i < wifiList.size(); i++) {
            String item = wifiList.get(i).toString();
            String[] string = item.split(",");
            String bssid_string = string[1];
            String bssid = bssid_string.split(": ")[1];
            if (bssid.equals(wifi)) {
                checkWifi = wifi;
                break;
            }
        }
    }

    @Override
    public void onPost(String s) {
        jsonReceive = s;

        Gson gson = new Gson();
        CheckTitle checkTitle = gson.fromJson(jsonReceive, CheckTitle.class);
        message = checkTitle.getMessage();

        switch (message) {
            case "Get state_Button success.":
                checkStateButton();
                break;
            case "No time of user.":
                buttonCheckTime.setBackgroundResource(R.color.btn_green_color);
                buttonCheckTime.setText("CHECK IN");
                state = 0;
                break;
            case "No time of today.":
                buttonCheckTime.setBackgroundResource(R.color.btn_green_color);
                buttonCheckTime.setText("CHECK IN");
                state = 0;
                break;
            case "Check in success.":
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                getStateButton();
                break;
            case "Check in failed.":
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                break;
            case "Wait tomorrow.":
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                break;
            case "Check out success.":
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                getStateButton();
                break;
        }
    }
}
