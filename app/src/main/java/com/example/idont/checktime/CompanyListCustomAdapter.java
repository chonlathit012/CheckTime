package com.example.idont.checktime;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by iDont on 6/9/2560.
 */

public class CompanyListCustomAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private ViewHolder viewHolder;
    private List<CompanyListDataReceive> companyListDataReceives;

    public CompanyListCustomAdapter(Activity activity, List<CompanyListDataReceive> companyListDataReceives) {
        layoutInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        this.companyListDataReceives = companyListDataReceives;
    }

    private static class ViewHolder {
        ImageView company_photo;
        TextView company_name;
        TextView company_startTime;
        TextView company_finishTime;
        TextView textViewCompanyName;
        TextView textViewStartTime;
        TextView textViewFinishTime;

    }

    @Override
    public int getCount() {
        return companyListDataReceives.size();
    }

    @Override
    public Object getItem(int position) {
        return companyListDataReceives.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CompanyListDataReceive companyListDataReceive = companyListDataReceives.get(position);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_company, parent, false);
                       viewHolder = new ViewHolder();
            viewHolder.company_photo = (ImageView) convertView.findViewById(R.id.company_photo);
            viewHolder.company_name = (TextView) convertView.findViewById(R.id.company_name);
            viewHolder.company_startTime = (TextView) convertView.findViewById(R.id.start_time);
            viewHolder.company_finishTime = (TextView) convertView.findViewById(R.id.finish_time);

            viewHolder.textViewCompanyName = (TextView) convertView.findViewById(R.id.textViewCompanyName);
            viewHolder.textViewStartTime = (TextView) convertView.findViewById(R.id.textViewStartTime);
            viewHolder.textViewFinishTime = (TextView) convertView.findViewById(R.id.textViewFinishTime);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textViewCompanyName.setText(companyListDataReceive.getCompany_name());
        viewHolder.textViewStartTime.setText(companyListDataReceive.getStart_time());
        viewHolder.textViewFinishTime.setText(companyListDataReceive.getFinish_time());

        return convertView;
    }
}

