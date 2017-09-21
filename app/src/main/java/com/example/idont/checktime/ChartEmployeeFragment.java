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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.w3c.dom.Text;

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

    BarView barView;

    View viewLine;
    ImageView imageView;

    TextView textView07;
    TextView textView08;
    TextView textView10;

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

        viewLine = (View) view.findViewById(R.id.view0830);
        imageView = (ImageView) view.findViewById(R.id.imageView);

        textView07 = (TextView) view.findViewById(R.id.textView0700);
        textView08 = (TextView) view.findViewById(R.id.textView0830);
        textView10 = (TextView) view.findViewById(R.id.textView1030);

        getTimeList();

    }

    public void showTimeList() {

        textView07.setText(R.string.txt_time_700);
        textView08.setText(R.string.txt_basetime);
        textView10.setText(R.string.txt_time_1030);
        viewLine.setBackgroundColor(Color.LTGRAY);

        Gson gson = new Gson();
        CalendarReceive calendarReceive = gson.fromJson(jsonReceive, CalendarReceive.class);
        List<CalendarDataReceive> listReceiveList = calendarReceive.getData().getTime_list();

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

            if (newHour.equals("07")) {
                barDataList.add(i, 30 + Integer.parseInt(newMinute));

            } else if (newHour.equals("08")) {
                barDataList.add(i, 90 + Integer.parseInt(newMinute));

            } else if (newHour.equals("09")) {
                barDataList.add(i, 150 + Integer.parseInt(newMinute));

            } else if (newHour.equals("10")) {
                barDataList.add(i, 210 + Integer.parseInt(newMinute));

            } else {
                barDataList.add(i, 250);
            }

            dayNumbar.add(String.valueOf(newDate));

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
                    break;
            }
        }

    }
}
