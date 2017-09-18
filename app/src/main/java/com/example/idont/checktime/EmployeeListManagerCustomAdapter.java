package com.example.idont.checktime;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by iDont on 8/9/2560.
 */

public class EmployeeListManagerCustomAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private ViewHolder viewHolder;
    private List<EmployeeListManagerDataReceive> employeeListManagerDataReceives;
    private Context context;

    StorageReference storageReference;

    ArrayList<EmployeeListManagerDataReceive> arraylist;

    public EmployeeListManagerCustomAdapter(Activity activity, List<EmployeeListManagerDataReceive> employeeListManagerDataReceives) {
        layoutInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        this.employeeListManagerDataReceives = employeeListManagerDataReceives;
        arraylist = new ArrayList<EmployeeListManagerDataReceive>();
        arraylist.addAll(employeeListManagerDataReceives);
        context = activity;
        storageReference = FirebaseStorage.getInstance().getReference();
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
        String photo_url = employeeListManagerDataReceive.getPhoto_url();
        String uid = employeeListManagerDataReceive.getId();

        if (photo_url != null) {
            Glide.with(context)
                    .load(photo_url)
                    .into(viewHolder.profile_photo);
        }

        return convertView;
    }

    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());

        employeeListManagerDataReceives.clear();
        if (charText.length() == 0) {
            employeeListManagerDataReceives.addAll(arraylist);

        } else {
            for (EmployeeListManagerDataReceive postDetail : arraylist) {
                if (charText.length() != 0 && postDetail.getDisplay_name().toLowerCase(Locale.getDefault()).contains(charText)) {
                    employeeListManagerDataReceives.add(postDetail);
                }
            }
        }
        notifyDataSetChanged();
    }

}
