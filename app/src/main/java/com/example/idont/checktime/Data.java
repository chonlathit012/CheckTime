package com.example.idont.checktime;

/**
 * Created by iDont on 13/9/2560.
 */

public class Data {
    private String ssid;
    private String bssid;
    private String security;
    private String level;



    public Data(String ssid, String bssid, String security, String level) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.security = security;
        this.level = level;
    }

    public String getTitle() {
        return ssid;
    }

    public String getSecurity() {
        return security;
    }

    public String getLevel() {
        return level;
    }

    public String getBssid() {
        return bssid;
    }

}
