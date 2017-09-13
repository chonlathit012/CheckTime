package com.example.idont.checktime;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.desai.vatsal.mydynamiccalendar.MyDynamicCalendar;
import com.desai.vatsal.mydynamiccalendar.OnDateClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarEmplouyeeFragment extends Fragment implements Test {

    MyDynamicCalendar myCalendar;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    String uid;
    String json = "";
    String jsonReceive = "";
    String message;

    public static CalendarEmplouyeeFragment newInstance() {
        CalendarEmplouyeeFragment fragment = new CalendarEmplouyeeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar_emplouyee, container, false);
    }

    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        myCalendar = (MyDynamicCalendar) view.findViewById(R.id.myCalendar);

//        myCalendar.showMonthView();

        myCalendar.setCalendarBackgroundColor("#fafafa");

        myCalendar.setHeaderBackgroundColor("#ffffff");
        myCalendar.setHeaderTextColor("#000000");

        myCalendar.setNextPreviousIndicatorColor("#000000");

        myCalendar.setWeekDayLayoutBackgroundColor("#ddd9d9");
        myCalendar.setWeekDayLayoutTextColor("#000000");

        myCalendar.isSaturdayOff(false, "#ffffff", "#ff0000");
        myCalendar.isSundayOff(true, "#ffffff", "#f40404"); // sunday background and text color

        myCalendar.setExtraDatesOfMonthBackgroundColor("#ffffff");
        myCalendar.setExtraDatesOfMonthTextColor("#d6cfcf");

        myCalendar.setDatesOfMonthBackgroundColor("#ffffff");
        myCalendar.setDatesOfMonthTextColor("#000000");

        myCalendar.setCurrentDateBackgroundColor("#ed1547");
        myCalendar.setCurrentDateTextColor("#ffffff");

        myCalendar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onClick(Date date) {

                Log.e("date", new Date().toString());
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());

                Gson gson = new Gson();
                CalendarReceive calendarReceive = gson.fromJson(jsonReceive, CalendarReceive.class);
                List<CalendarDataReceive> listReceiveList = calendarReceive.getData().getTime_list();

                for (int i = 0; i < listReceiveList.size(); i++) {
                    String start_time = listReceiveList.get(i).getStart_time();
                    String finish_time = listReceiveList.get(i).getFinish_time();
                    SimpleDateFormat formatDateStart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SimpleDateFormat formatDateFinish = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date dateNewStart;
                    Date dateNewFinish;
                    String newDate;
                    String dateLocal;
                    String startTime = "";
                    String finishTime = "";
                    try {
                        dateNewStart = formatDateStart.parse(start_time);
                        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
                        SimpleDateFormat formaterTime = new SimpleDateFormat("HH:mm:ss");
                        newDate = formater.format(dateNewStart);
                        dateLocal = formater.format(date);
                        startTime = formaterTime.format(dateNewStart);
                        if (finish_time != null) {
                            dateNewFinish = formatDateFinish.parse(finish_time);
                            finishTime = formaterTime.format(dateNewFinish);

                            if (dateLocal.equals(newDate)) {
                                builder.setMessage(("Date : " + newDate) +
                                        "\nStart time : " + startTime +
                                        "\nFinish time : " + finishTime);
                                builder.setPositiveButton("Done", null);
                                builder.show();
                            }
                        } else {
                            if (dateLocal.equals(newDate)) {
                                builder.setMessage(("Date : " + newDate) +
                                        "\nStart time : " + startTime +
                                        "\nFinish time : null ");
                                builder.setPositiveButton("Done", null);
                                builder.show();
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onLongClick(Date date) {

            }
        });

        getTimeList();

    }

    public void getTimeList() {
        Gson gson = new Gson();
        CalendarData calendarData = new CalendarData();
        calendarData.setId(uid);

        CalendarSend calendarSend = new CalendarSend();
        calendarSend.setTarget("calendar");
        calendarSend.setData(calendarData);

        json = gson.toJson(calendarSend);

        new HttpTask(CalendarEmplouyeeFragment.this).execute(json);

    }

    public void showTimeList() {
        Gson gson = new Gson();
        CalendarReceive calendarReceive = gson.fromJson(jsonReceive, CalendarReceive.class);
        List<CalendarDataReceive> listReceiveList = calendarReceive.getData().getTime_list();

        for (int i = 0; i < listReceiveList.size(); i++) {
            String test = listReceiveList.get(i).getStart_time();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            String newDate = "";
            try {
                date = format.parse(test);
                SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy");
                newDate = formater.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                myCalendar.addEvent(newDate, "12:00", " ", " ");
                myCalendar.showMonthView();
            }
        }
    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());

        // Time format (hours)
//                kk = Hours in 1-24 format
//                hh= hours in 1-12 format
//                KK= hours in 0-11 format
//                HH= hours in 0-23 format
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
