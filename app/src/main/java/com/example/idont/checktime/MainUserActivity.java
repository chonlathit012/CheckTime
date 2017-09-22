package com.example.idont.checktime;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainUserActivity extends AppCompatActivity {
    Toolbar toolbar;
    int state = 1;
    Menu menutest;

    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        toolbar = (Toolbar) findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");

        ActivityCompat.requestPermissions(MainUserActivity.this, new String[]{
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int editUser = bundle.getInt("User profile updated.");
            int createCompany = bundle.getInt("Created data success");

            if (editUser == 1){
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout,"User profile updated.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
            if (createCompany == 1){
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout,"Created data success.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.profile:
                        selectedFragment = UserProfileFragment.newInstance();
                        getSupportActionBar().setTitle("Profile");
                        state = 1;
                        menutest.clear();
                        getMenuInflater().inflate(R.menu.toolbar_user, menutest);
                        break;
                    case R.id.company:
                        selectedFragment = CompanyListFragment.newInstance();
                        getSupportActionBar().setTitle("Company");
                        state = 0;
                        menutest.clear();
                        getMenuInflater().inflate(R.menu.toolbar_create_company, menutest);
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
                return true;
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, UserProfileFragment.newInstance());
        transaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menutest = menu;
        menu.clear();
        if (state == 1) {
            getMenuInflater().inflate(R.menu.toolbar_user, menu);
        }
        if (state == 0) {
            getMenuInflater().inflate(R.menu.toolbar_create_company, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.edit) {
            startActivity(new Intent(MainUserActivity.this, EditUserProfileActivity.class));
        }
        if (id == R.id.create) {
            startActivity(new Intent(MainUserActivity.this, CreateCompanyActivity.class));
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
                        MainUserActivity.super.onBackPressed();
                    }
                }).create().show();
    }
}
