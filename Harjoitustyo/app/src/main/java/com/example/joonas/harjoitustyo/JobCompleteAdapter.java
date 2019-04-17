package com.example.joonas.harjoitustyo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joonas on 05/02/2016.
 */
public class JobCompleteAdapter extends ArrayAdapter<Job> {

    public JobCompleteAdapter(Context context, int resource, List<Job> list) {
        super(context, resource, list);
    }

    static class DataHandler
    {
        TextView id, selite, suoritteet,tunnit, tila;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;

        DataHandler handler;

        if (convertView == null)
        {

            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.job_complete_row_layout, null/*parent, false*/);
            handler = new DataHandler();
            handler.id = (TextView) row.findViewById(R.id.txt_job_id2);
            handler.selite = (TextView) row.findViewById(R.id.txt_job_selite);
            handler.suoritteet = (TextView) row.findViewById(R.id.txt_job_suoritteet);
            handler.tunnit = (TextView) row.findViewById(R.id.txt_job_tunnit);
            handler.tila = (TextView) row.findViewById(R.id.txt_job_status2);
            row.setTag(handler);

        }
        else
        {
            row = convertView;
            handler = (DataHandler)row.getTag();
        }

        handler.id.setText(getItem(position).getJob_id());
        handler.selite.setText(getItem(position).getSelite());
        handler.suoritteet.setText(getItem(position).getSuoritteet());
        handler.tunnit.setText(getItem(position).getTunnit());
        handler.tila.setText(getItem(position).getStatus());
        return row;
    }
}
