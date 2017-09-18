package com.example.idont.checktime;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.desai.vatsal.mydynamiccalendar.MyDynamicCalendar;
import com.desai.vatsal.mydynamiccalendar.OnDateClickListener;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CalendarActivity extends AppCompatActivity implements Test {

    MyDynamicCalendar myCalendar;

    String uid;
    String json = "";
    String jsonReceive = "";
    String message;
    String employee_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            employee_id = bundle.getString("employee_id");
        }

        myCalendar = (MyDynamicCalendar) findViewById(R.id.myCalendar);

        myCalendar.deleteAllEvent();

        myCalendar.showMonthView();

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

        myCalendar.setHolidayCellBackgroundColor("#aad4ed");
        myCalendar.setHolidayCellTextColor("#000000");

        addHoliday();

        myCalendar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onClick(Date date) {

                Log.e("date", new Date().toString());
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(CalendarActivity.this);

                Gson gson = new Gson();
                CalendarReceive calendarReceive = gson.fromJson(jsonReceive, CalendarReceive.class);

                message = calendarReceive.getMessage();

                if (!message.equals("No time_list.")) {

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
        calendarData.setId(employee_id);

        CalendarSend calendarSend = new CalendarSend();
        calendarSend.setTarget("calendar");
        calendarSend.setData(calendarData);

        json = gson.toJson(calendarSend);

        new HttpTask(CalendarActivity.this).execute(json);

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

    public void addHoliday(){
        myCalendar.addHoliday("1-1-2017");
        myCalendar.addHoliday("28-1-2017");
        myCalendar.addHoliday("11-2-2017");
        myCalendar.addHoliday("13-2-2017");
        myCalendar.addHoliday("6-4-2017");
        myCalendar.addHoliday("13-4-2017");
        myCalendar.addHoliday("1-5-2017");
        myCalendar.addHoliday("5-5-2017");
        myCalendar.addHoliday("10-5-2017");
        myCalendar.addHoliday("8-7-2017");
        myCalendar.addHoliday("9-7-2017");
        myCalendar.addHoliday("10-7-2017");
        myCalendar.addHoliday("12-8-2017");
        myCalendar.addHoliday("14-8-2017");
        myCalendar.addHoliday("23-10-2017");
        myCalendar.addHoliday("5-12-2017");
        myCalendar.addHoliday("10-12-2017");
        myCalendar.addHoliday("11-12-2017");
        myCalendar.addHoliday("31-12-2017");
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
