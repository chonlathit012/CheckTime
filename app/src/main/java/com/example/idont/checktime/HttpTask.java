package com.example.idont.checktime;

import android.app.Activity;
import android.os.AsyncTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by iDont on 30/8/2560.
 */

public class HttpTask extends AsyncTask<String, Void, String> {

    private HttpURLConnection con;
    private String result = "";
    private Test test = null;

    HttpTask(Test test) {
        this.test = test;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {

        test.onPost(s);
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            String URL = "http://172.20.10.3/project/main.php"; //hotspot
//            String URL = "http://192.168.1.210/project/main.php"; //home wifi
            URL url = new URL(URL);

            con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(10000);
            con.setConnectTimeout(15000);
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
            paramsList.add(new BasicNameValuePair("json", params[0]));

            OutputStream os = con.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(paramsList));
            writer.flush();
            writer.close();
            os.close();

            int HttpResult = con.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"));
                result = bufferedReader.readLine();
                bufferedReader.close();
            }

        } catch (Exception e) {
            result = "No connection.";
            return result;
        } finally {
            con.disconnect();
        }
        return result;
    }


    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}

