package com.example.idont.checktime;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

public class LoadActivity extends AppCompatActivity implements Test {

    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    Handler handler;
    Runnable runnable;

    CoordinatorLayout coordinatorLayout;

    String jsonReceive = "";
    String json = "";
    String uid;
    String role_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                getRoleData();
            }
        };

    }

    public void getRoleData() {
        Gson gson = new Gson();
        CheckRoleData checkRoleData = new CheckRoleData();
        checkRoleData.setId(uid);

        CheckRoleSend checkRoleSend = new CheckRoleSend();
        checkRoleSend.setTarget("check_employee_role");
        checkRoleSend.setData(checkRoleData);

        json = gson.toJson(checkRoleSend);

        new HttpTask(LoadActivity.this).execute(json);

    }

    public void showData() {

        Gson gson = new Gson();
        CheckRoleReceive checkRoleReceive = gson.fromJson(jsonReceive, CheckRoleReceive.class);

        role_id = checkRoleReceive.getData().getRole_id();

        delay(role_id);

    }

    public void delay(final String role_id) {

        if (role_id.equals("1")) {
            startActivity(new Intent(LoadActivity.this, MainUserActivity.class));
            finish();
        } else if (role_id.equals("2")) {
            startActivity(new Intent(LoadActivity.this, MainEmployeeActivity.class));
            finish();
        } else if (role_id.equals("3")) {
            startActivity(new Intent(LoadActivity.this, MainManagerActivity.class));
            finish();
        }
    }

    @Override
    public void onPost(String s) {
        if (s.equals("No connection.")) {
//            Snackbar snackbar = Snackbar
//                    .make(coordinatorLayout, s, Snackbar.LENGTH_INDEFINITE).setAction("close app", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            finishAffinity();
//                            System.exit(0);
//                        }
//                    }).setActionTextColor(Color.rgb(129, 186, 219));
//            snackbar.show();

            android.app.AlertDialog.Builder builder =
                    new android.app.AlertDialog.Builder(this);
            builder.setMessage("No connection.");
            builder.setPositiveButton("Close app", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finishAffinity();
                    System.exit(0);
                }
            });
            builder.show();

        } else {
            jsonReceive = s;
            showData();
        }
    }

    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 4000);
    }

    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }
}
