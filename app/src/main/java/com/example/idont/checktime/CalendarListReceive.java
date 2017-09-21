package com.example.idont.checktime;

import java.util.List;

/**
 * Created by iDont on 11/9/2560.
 */

public class CalendarListReceive {
    private String company_start_time;
    private List<CalendarDataReceive> time_list;

    public String getCompany_start_time() {
        return company_start_time;
    }

    public void setCompany_start_time(String company_start_time) {
        this.company_start_time = company_start_time;
    }

    public List<CalendarDataReceive> getTime_list() {
        return time_list;
    }

    public void setTiume_list(List<CalendarDataReceive> time_list) {
        this.time_list = time_list;
    }
}
