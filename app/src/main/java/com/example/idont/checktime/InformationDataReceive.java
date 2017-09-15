package com.example.idont.checktime;

import java.util.List;

/**
 * Created by iDont on 15/9/2560.
 */

public class InformationDataReceive {
    private List<InformationListDataReceive> employee_list_late;
    private String employee_day_count;
    private String employee_total;

    public List<InformationListDataReceive> getEmployee_list_late() {
        return employee_list_late;
    }

    public void setEmployee_list_late(List<InformationListDataReceive> employee_list_late) {
        this.employee_list_late = employee_list_late;
    }

    public String getEmployee_day_count() {
        return employee_day_count;
    }

    public void setEmployee_day_count(String employee_day_count) {
        this.employee_day_count = employee_day_count;
    }

    public String getEmployee_total() {
        return employee_total;
    }

    public void setEmployee_total(String employee_total) {
        this.employee_total = employee_total;
    }
}
