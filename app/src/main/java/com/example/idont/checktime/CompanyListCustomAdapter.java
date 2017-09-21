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

import java.util.List;

/**
 * Created by iDont on 6/9/2560.
 */

public class CompanyListCustomAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private ViewHolder viewHolder;
    private List<CompanyListDataReceive> companyListDataReceives;
    private Context context;

    public CompanyListCustomAdapter(Activity activity, List<CompanyListDataReceive> companyListDataReceives) {
        layoutInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        this.companyListDataReceives = companyListDataReceives;
        context = activity;
    }

    private static class ViewHolder {
        ImageView company_photo;
        TextView company_name;
        TextView company_startTime;
        TextView company_finishTime;
        TextView textViewCompanyName;
        TextView textViewStartTime;
        TextView textViewFinishTime;
        ProgressBar progressBar;

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

            viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textViewCompanyName.setText(companyListDataReceive.getCompany_name());
        viewHolder.textViewStartTime.setText(companyListDataReceive.getStart_time());
        viewHolder.textViewFinishTime.setText(companyListDataReceive.getFinish_time());
        String logo_url = companyListDataReceive.getLogo_url();

        if (logo_url != null) {
            Glide.with(context)
                    .load(logo_url)
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
                    .into(viewHolder.company_photo);
        } else {
            viewHolder.progressBar.setVisibility(View.GONE);
        }

        return convertView;
    }
}

