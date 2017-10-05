package com.example.idont.checktime;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import im.dacer.androidcharts.BarView;

/**
 * A simple {@link Fragment} subclass.
 */

public class ChartEmployeeFragment extends Fragment implements Test {

    BarView barView;

    ProgressBar progressBar;

    View viewLine;
    ImageView imageView;

    TextView textView07;
    TextView textView08;
    TextView textView10;
    TextView textViewDay;
    TextView textViewTime;


    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String uid;
    String company_id;
    String json = "";
    String jsonReceive = "";
    String message;

    public static ChartEmployeeFragment newInstance() {
        ChartEmployeeFragment fragment = new ChartEmployeeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chart_employee, container, false);
    }

    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        barView = (BarView) view.findViewById(R.id.bar_view);

        viewLine = (View) view.findViewById(R.id.view0830);
        imageView = (ImageView) view.findViewById(R.id.imageView);

        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        textView07 = (TextView) view.findViewById(R.id.textView0700);
        textView08 = (TextView) view.findViewById(R.id.textView0830);
        textView10 = (TextView) view.findViewById(R.id.textView1030);
        textViewTime = (TextView) view.findViewById(R.id.texttime);
        textViewDay = (TextView) view.findViewById(R.id.textday);

        getTimeList();

    }

    public void showTimeList() {
        Gson gson = new Gson();
        CalendarReceive calendarReceive = gson.fromJson(jsonReceive, CalendarReceive.class);
        List<CalendarDataReceive> listReceiveList = calendarReceive.getData().getTime_list();
        String company_start_time = calendarReceive.getData().getCompany_start_time();

        if (listReceiveList.size() != 0) {


            SimpleDateFormat formatCompanyTime = new SimpleDateFormat("HH:mm");
            Date companyTime;
            String companyHour = null;
            String companyMinute = null;
            SimpleDateFormat formaterHourCom = null;
            SimpleDateFormat formaterMinuteCom = null;
            try {
                companyTime = formatCompanyTime.parse(company_start_time);
                formaterHourCom = new SimpleDateFormat("HH");
                formaterMinuteCom = new SimpleDateFormat("mm");

                companyHour = formaterHourCom.format(companyTime);
                companyMinute = formaterMinuteCom.format(companyTime);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            textView08.setText(companyHour + ":" + companyMinute);
            textView07.setText(String.valueOf(Integer.parseInt(companyHour) - 1) + ":30");
            textView10.setText(String.valueOf(Integer.parseInt(companyHour) + 2) + ":30");
            textViewTime.setText("Time");
            textViewDay.setText("Day");
            viewLine.setBackgroundColor(Color.LTGRAY);

            ArrayList<Integer> barDataList = new ArrayList<Integer>();
            ArrayList<String> dayNumbar = new ArrayList<String>();
            for (int i = 0; i < listReceiveList.size(); i++) {
                String start_time = listReceiveList.get(i).getStart_time();
                SimpleDateFormat formatDateStart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date dateNewStart;
                String newDate = null;
                String newHour = null;
                String newMinute = null;
                try {
                    dateNewStart = formatDateStart.parse(start_time);
                    SimpleDateFormat formater = new SimpleDateFormat("dd");
                    SimpleDateFormat formaterHour = new SimpleDateFormat("HH");
                    SimpleDateFormat formaterMinute = new SimpleDateFormat("mm");

                    newDate = formater.format(dateNewStart);
                    newHour = formaterHour.format(dateNewStart);
                    newMinute = formaterMinute.format(dateNewStart);

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                int hour = Integer.parseInt(newHour);
                int hourCompany = Integer.parseInt(companyHour) - 1;

                if (String.valueOf(hour).equals(String.valueOf(Integer.parseInt(companyHour) - 1))) {
                    barDataList.add(i, 30 + Integer.parseInt(newMinute));

                } else if (String.valueOf(hour).equals(String.valueOf(Integer.parseInt(companyHour)))) {
                    barDataList.add(i, 90 + Integer.parseInt(newMinute));

                } else if (String.valueOf(hour).equals(String.valueOf(Integer.parseInt(companyHour) + 1))) {
                    barDataList.add(i, 150 + Integer.parseInt(newMinute));

                } else if (String.valueOf(hour).equals(String.valueOf(Integer.parseInt(companyHour) + 2))) {
                    barDataList.add(i, 210 + Integer.parseInt(newMinute));

                } else if (String.valueOf(hour).equals(String.valueOf(Integer.parseInt(companyHour) - 2))) {
                    barDataList.add(i, Integer.parseInt(newMinute));

                } else if (hour < hourCompany) {
                    barDataList.add(i, 0);
                } else {
                    barDataList.add(i, 250);
                }

                dayNumbar.add(String.valueOf(newDate));

            }

            progressBar.setVisibility(View.GONE);
            barView.setBottomTextList(dayNumbar);
            barView.setDataList(barDataList, 250);
        } else {
            imageView.setImageResource(R.drawable.no_icon);
            progressBar.setVisibility(View.GONE);
        }
    }

    public void getTimeList() {
        Gson gson = new Gson();
        CalendarData calendarData = new CalendarData();
        calendarData.setId(uid);

        CalendarSend calendarSend = new CalendarSend();
        calendarSend.setTarget("chart");
        calendarSend.setData(calendarData);

        json = gson.toJson(calendarSend);

        new HttpTask(ChartEmployeeFragment.this).execute(json);

    }

    @Override
    public void onPost(String s) {
        if (s.equals("No connection.")) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());
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
                case "Get time_list success.":
                    showTimeList();
                    break;
                case "No time_list.":
                    imageView.setImageResource(R.drawable.no_icon);
                    progressBar.setVisibility(View.GONE);
                    break;
            }
        }

    }

}
