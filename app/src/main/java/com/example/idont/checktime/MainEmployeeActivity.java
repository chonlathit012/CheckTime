package com.example.idont.checktime;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

public class MainEmployeeActivity extends AppCompatActivity implements Test {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String uid;
    String json;
    String jsonReceive;

    Menu menutest;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_employee);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        toolbar = (Toolbar) findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chart");

        ActivityCompat.requestPermissions(MainEmployeeActivity.this,new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.chart:
                        selectedFragment = ChartEmployeeFragment.newInstance();
                        getSupportActionBar().setTitle("Chart");
                        menutest.clear();
                        break;
                    case R.id.time:
                        selectedFragment = CheckTimeEmployeeFragment.newInstance();
                        getSupportActionBar().setTitle("Time");
                        menutest.clear();
                        break;
                    case R.id.calendar:
                        selectedFragment = CalendarEmplouyeeFragment.newInstance();
                        getSupportActionBar().setTitle("Calendar");
                        menutest.clear();
                        break;
                    case R.id.setting:
                        selectedFragment = SettingEmployeeFragment.newInstance();
                        getSupportActionBar().setTitle("Setting");
                        menutest.clear();
                        getMenuInflater().inflate(R.menu.toolbar_setting_employee, menutest);
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
                return true;
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, ChartEmployeeFragment.newInstance());
        transaction.commit();
    }

    public void resignDataEmployee() {
        Gson gson = new Gson();
        ResignData resignData = new ResignData();
        resignData.setId(uid);

        ResignSend resignSend = new ResignSend();
        resignSend.setTarget("resign_employee");
        resignSend.setData(resignData);

        json = gson.toJson(resignSend);

        new HttpTask(MainEmployeeActivity.this).execute(json);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menutest = menu;
        menu.clear();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.edit_profile) {
            startActivity(new Intent(MainEmployeeActivity.this, EditUserProfileActivity.class));
        }

        if (id == R.id.resign) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(MainEmployeeActivity.this);
            builder.setTitle("Resign !!");
            builder.setMessage("Are you sure you want to resign?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    resignDataEmployee();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.show();
        }

        if (id == R.id.signout) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(MainEmployeeActivity.this);
            builder.setMessage("Are you sure you want to sign out?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ProgressDialog progressDialog = new ProgressDialog(MainEmployeeActivity.this);
                    progressDialog.setMessage("Sign out..");
                    progressDialog.show();
                    firebaseAuth.signOut();
                    progressDialog.dismiss();
                    startActivity(new Intent(MainEmployeeActivity.this, LoginActivity.class));
                    finish();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.logo)
                .setTitle("Exit application?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        MainEmployeeActivity.super.onBackPressed();
                    }
                }).create().show();
    }

    @Override
    public void onPost(String s) {
        jsonReceive = s;

        Gson gson = new Gson();
        CheckTitle checkTitle = gson.fromJson(jsonReceive, CheckTitle.class);

        String message = checkTitle.getMessage();

        switch (message) {
            case "Resign success.":
                firebaseAuth.signOut();
                startActivity(new Intent(MainEmployeeActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }
}
