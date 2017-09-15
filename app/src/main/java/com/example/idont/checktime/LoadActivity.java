package com.example.idont.checktime;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LoadActivity extends AppCompatActivity implements Test{

    ProgressBar progressBar;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    CoordinatorLayout coordinatorLayout;

    String jsonReceive = "";
    String json = "";
    String uid;

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

        getRoleData();

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

        String role_id = checkRoleReceive.getData().getRole_id();

        delay(Integer.parseInt(role_id));

    }

    public void delay(final int role_id) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                }
                if (role_id == 1) {
                    startActivity(new Intent(LoadActivity.this, MainUserActivity.class));
                    finish();
                } else if (role_id == 2) {
                    startActivity(new Intent(LoadActivity.this, MainEmployeeActivity.class));
                    finish();
                } else if (role_id == 3) {
                    startActivity(new Intent(LoadActivity.this, MainManagerActivity.class));
                    finish();
                }
            }
        }).start();
    }

    @Override
    public void onPost(String s) {
        if (s.equals("No connection.")){
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout,s, Snackbar.LENGTH_INDEFINITE).setAction("close", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            }).setActionTextColor(Color.rgb(129,186,219));
            snackbar.show();
        } else {
            jsonReceive = s;
            showData();
        }
    }
}
