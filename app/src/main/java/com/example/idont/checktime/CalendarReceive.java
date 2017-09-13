package com.example.idont.checktime;

import java.util.List;

/**
 * Created by iDont on 11/9/2560.
 */

public class CalendarReceive {
    private String status;
    private String message;
    private CalendarListReceive data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CalendarListReceive getData() {
        return data;
    }

    public void setData(CalendarListReceive data) {
        this.data = data;
    }
}
