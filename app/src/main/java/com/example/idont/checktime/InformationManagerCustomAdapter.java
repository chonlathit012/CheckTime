package com.example.idont.checktime;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by iDont on 15/9/2560.
 */

public class InformationManagerCustomAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private ViewHolder viewHolder;
    private List<InformationListDataReceive> listDataReceives;
    private Context context;

    public InformationManagerCustomAdapter(Activity activity, List<InformationListDataReceive> listDataReceives) {
        layoutInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        this.listDataReceives = listDataReceives;
        context = activity;
    }

    private static class ViewHolder {
        ImageView profile_photo;
        TextView display_name;
        TextView time;
        ProgressBar progressBar;
    }

    @Override
    public int getCount() {
        int state = 0;
        if (listDataReceives.size() <= 3) {
            state = listDataReceives.size();
        } else {
            state = 3;
        }
        return state;
    }

    @Override
    public Object getItem(int position) {
        return listDataReceives.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InformationListDataReceive informationListDataReceive = listDataReceives.get(position);

        String start_time = informationListDataReceive.getStart_time();
        SimpleDateFormat formatDateStart = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateNewStart;
        String startTime = "";

        try {
            dateNewStart = formatDateStart.parse(start_time);
            SimpleDateFormat formaterTime = new SimpleDateFormat("HH:mm:ss");
            startTime = formaterTime.format(dateNewStart);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_late, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.profile_photo = (ImageView) convertView.findViewById(R.id.profile_photo);
            viewHolder.display_name = (TextView) convertView.findViewById(R.id.display_name);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);
            viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.display_name.setText(informationListDataReceive.getDisplay_name());
        viewHolder.time.setText("Start time : " + startTime);
        String photo_url = informationListDataReceive.getPhoto_url();

        if (photo_url != null) {
            Glide.with(context)
                    .load(photo_url)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                            return false; // important to return false so the error placeholder can be placed
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(viewHolder.profile_photo);
        } else {
            viewHolder.progressBar.setVisibility(View.GONE);
        }

        return convertView;
    }
}
