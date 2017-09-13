package com.example.idont.checktime;

import java.util.List;

/**
 * Created by iDont on 4/9/2560.
 */

public class CompanyListReceive {
    private String status;
    private String message;
    private CompanyListData data;

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

    public CompanyListData getData() {
        return data;
    }

    public void setData(CompanyListData data) {
        this.data = data;
    }
}
