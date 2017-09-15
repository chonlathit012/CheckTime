package com.example.idont.checktime;

/**
 * Created by iDont on 15/9/2560.
 */

public class InformationReceive {
    private String status;
    private String message;
    private InformationDataReceive data;

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

    public InformationDataReceive getData() {
        return data;
    }

    public void setData(InformationDataReceive data) {
        this.data = data;
    }
}
