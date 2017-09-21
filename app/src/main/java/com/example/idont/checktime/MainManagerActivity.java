package com.example.idont.checktime;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainManagerActivity extends AppCompatActivity {

    Toolbar toolbar;
    Menu menutest;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_manager);

        toolbar = (Toolbar) findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Information");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.information:
                        selectedFragment = InformaitionManagerFragment.newInstance();
                        getSupportActionBar().setTitle("Information");
                        menutest.clear();
                        break;
                    case R.id.list:
                        selectedFragment = EmployeeListManagerFragment.newInstance();
                        getSupportActionBar().setTitle("Employee List");
                        menutest.clear();
                        break;
                    case R.id.setting:
                        selectedFragment = SettingManagerFragment.newInstance();
                        getSupportActionBar().setTitle("Setting");
                        menutest.clear();
                        getMenuInflater().inflate(R.menu.toolbar_setting_manager, menutest);
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
                return true;
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, InformaitionManagerFragment.newInstance());
        transaction.commit();
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
            startActivity(new Intent(MainManagerActivity.this, EditUserProfileActivity.class));
        }
        if (id == R.id.signout) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(MainManagerActivity.this);
            builder.setMessage("Are you sure you want to sign out?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ProgressDialog progressDialog = new ProgressDialog(MainManagerActivity.this);
                    progressDialog.setMessage("Sign out..");
                    progressDialog.show();
                    firebaseAuth.signOut();
                    progressDialog.dismiss();
                    startActivity(new Intent(MainManagerActivity.this, LoginActivity.class));
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
                        MainManagerActivity.super.onBackPressed();
                    }
                }).create().show();
    }
}
