package com.example.idont.checktime;

/**
 * Created by iDont on 10/9/2560.
 */

public class StateButtonReceive {
    private String status;
    private String message;
    private StateButtonDataReceive data;

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

    public StateButtonDataReceive getData() {
        return data;
    }

    public void setData(StateButtonDataReceive data) {
        this.data = data;
    }
}
