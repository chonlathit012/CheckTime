package com.example.idont.checktime;

/**
 * Created by iDont on 7/9/2560.
 */

public class SettingManagerReceive {
    private String status;
    private String message;
    private SettingManagerDataReceive data;

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

    public SettingManagerDataReceive getData() {
        return data;
    }

    public void setData(SettingManagerDataReceive data) {
        this.data = data;
    }
}
