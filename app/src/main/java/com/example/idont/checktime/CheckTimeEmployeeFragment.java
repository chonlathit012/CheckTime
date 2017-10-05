package com.example.idont.checktime;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckTimeEmployeeFragment extends Fragment implements Test {

    private static final int CAMERA_REQUEST = 1888;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    ProgressDialog progressDialog;
    StorageReference storageReference;

    Button buttonCheckTime;

    Uri cameraPicture;

    String json = "";
    String jsonReceive = "";
    String uid;
    String message;
    String start_time;
    String finish_time;
//    String wifi = "58:8d:09:e2:a5:81";
    String wifi = "aa:5b:78:6f:49:a2";
    String checkWifi;
    String stringDate;

    int state = 0;

    double lat = 0.0;
    double lng = 0.0;

    Bitmap photo = null;

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

        progressDialog = new ProgressDialog(getActivity());
        storageReference = FirebaseStorage.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        buttonCheckTime = (Button) view.findViewById(R.id.buttonCheckTime);

        buttonCheckTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
                } else {
                    detectWifi();
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
                                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
//                                checkIn();
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
                                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
//                                checkOut();
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
            }
        });

        getStateButton();
        getDate();
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
            buttonCheckTime.setBackgroundResource(R.drawable.button_red);
            buttonCheckTime.setText("CHECK OUT");
            state = 1;
        } else {
            buttonCheckTime.setBackgroundResource(R.drawable.button_grey);
            buttonCheckTime.setText("Wait Tomorrow");
            state = 2;
        }
    }

    public void detectWifi() {

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            cameraPicture = getImageUri(getActivity().getApplicationContext(), photo);

            if (state == 0) {
                progressDialog.setMessage("Checktime....");
                progressDialog.show();

                StorageReference imageRef = storageReference.child("checktime/checkin/" + uid + "/" + stringDate);

                imageRef.putFile(cameraPicture).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "check in failed.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        checkIn();
                    }
                });
            } else if (state == 1) {
                progressDialog.setMessage("Checktime....");
                progressDialog.show();

                StorageReference imageRef = storageReference.child("checktime/checkout/" + uid + "/" + stringDate);

                imageRef.putFile(cameraPicture).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "check out failed.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        checkOut();
                    }
                });
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void getDate() {
        Date date = new Date();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        stringDate = formatDate.format(date);

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
            message = checkTitle.getMessage();

            switch (message) {
                case "Get state_Button success.":
                    checkStateButton();
                    break;
                case "No time of user.":
                    buttonCheckTime.setBackgroundResource(R.drawable.button_green);
                    buttonCheckTime.setText("CHECK IN");
                    state = 0;
                    break;
                case "No time of today.":
                    buttonCheckTime.setBackgroundResource(R.drawable.button_green);
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
}
