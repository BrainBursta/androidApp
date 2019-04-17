package com.example.joonas.harjoitustyo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joonas on 30/01/2016.
 */
public class JobAdapter extends ArrayAdapter<Job> {

    public JobAdapter(Context context, int resource, List<Job> list) {
        super(context, resource, list);
    }

    static class DataHandler
    {
        TextView id, kuvaus, deadline, tila;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;

        DataHandler handler;

        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.job_row_layout, null/*parent, false*/);
            handler = new DataHandler();
            handler.id = (TextView) row.findViewById(R.id.txt_job_id);
            handler.kuvaus = (TextView) row.findViewById(R.id.txt_job_desc);
            handler.deadline = (TextView) row.findViewById(R.id.txt_job_deadline);
            handler.tila = (TextView) row.findViewById(R.id.txt_job_status);
            row.setTag(handler);
        }
        else
        {
            row = convertView;
            handler = (DataHandler)row.getTag();
        }

        handler.id.setText(getItem(position).getJob_id());
        handler.kuvaus.setText(getItem(position).getDesc());
        handler.deadline.setText(getItem(position).getDeadline());
        handler.tila.setText(getItem(position).getStatus());
        return row;
    }

}
