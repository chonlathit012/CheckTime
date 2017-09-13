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
 * Created by iDont on 8/9/2560.
 */

public class EmployeeListManagerCustomAdapter extends BaseAdapter{
    private LayoutInflater layoutInflater;
    private ViewHolder viewHolder;
    private List<EmployeeListManagerDataReceive> employeeListManagerDataReceives;

    public EmployeeListManagerCustomAdapter(Activity activity, List<EmployeeListManagerDataReceive> employeeListManagerDataReceives) {
        layoutInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        this.employeeListManagerDataReceives = employeeListManagerDataReceives;
    }

    private static class ViewHolder {
        ImageView profile_photo;
        TextView display_name;
    }

    @Override
    public int getCount() {
        return employeeListManagerDataReceives.size();
    }

    @Override
    public Object getItem(int position) {
        return employeeListManagerDataReceives.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EmployeeListManagerDataReceive employeeListManagerDataReceive = employeeListManagerDataReceives.get(position);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_employee, parent, false);
                viewHolder = new ViewHolder();

            viewHolder.profile_photo = (ImageView) convertView.findViewById(R.id.profile_photo);
            viewHolder.display_name = (TextView) convertView.findViewById(R.id.display_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.display_name.setText(employeeListManagerDataReceive.getDisplay_name());

        return convertView;
    }

}
