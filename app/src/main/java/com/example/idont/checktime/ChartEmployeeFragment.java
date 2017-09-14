package com.example.idont.checktime;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import im.dacer.androidcharts.BarView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChartEmployeeFragment extends Fragment implements Test {

    int day = 30;
    BarView barView;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String uid;
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

        getTimeList();
//        randomSet(barView);



    }

    public void showTimeList() {
        Gson gson = new Gson();
        CalendarReceive calendarReceive = gson.fromJson(jsonReceive, CalendarReceive.class);
        List<CalendarDataReceive> listReceiveList = calendarReceive.getData().getTime_list();

        ArrayList<Integer> barDataList = new ArrayList<Integer>();
        ArrayList<String> dayNumbar = new ArrayList<String>();
        for (int i = 0; i < listReceiveList.size(); i++) {
            String start_time = listReceiveList.get(i).getStart_time();
            barDataList.add(i,(int) (Math.random() * 200) + 40);
            dayNumbar.add(String.valueOf(i + 1));

        }
        barView.setBottomTextList(dayNumbar);
        barView.setDataList(barDataList, 250);
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

    private void randomSet(BarView barView) {
//        day = (int)(Math.random()*20);
        ArrayList<String> test = new ArrayList<String>();
        for (int i = 0; i < day; i++) {
            test.add(String.valueOf(i + 1)); // chart number
        }
        barView.setBottomTextList(test);

        ArrayList<Integer> barDataList = new ArrayList<Integer>();
        for (int i = 0; i < day; i++) {
            barDataList.add((int) (Math.random() * 200) + 40);
        }
        barDataList.add(0,30); // time 7:00
        barDataList.add(1,120); // base time 8:30
        barDataList.add(2,60); // start time 7:30
        barDataList.add(3,240); // time 10:30
        barDataList.add(4,180); // time 9:30
        barView.setDataList(barDataList, 250);
    }

    @Override
    public void onPost(String s) {
        jsonReceive = s;

        Gson gson = new Gson();
        CheckTitle checkTitle = gson.fromJson(jsonReceive, CheckTitle.class);
        message = checkTitle.getMessage();

        switch (message) {
            case "Get time_list success.":
                showTimeList();
                break;
        }
    }
}
