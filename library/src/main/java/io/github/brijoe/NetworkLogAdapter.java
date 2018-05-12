package io.github.brijoe;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


class NetworkLogAdapter extends BaseAdapter {

    private List<NetworkLog> networkLogList;

    private SimpleDateFormat dateFormat;

    private Context mContext;


    public NetworkLogAdapter(Context context, List<NetworkLog> networkLogs) {
        this.mContext=context;
        this.networkLogList = networkLogs;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    @Override
    public int getCount() {
        return networkLogList == null ? 0 : networkLogList.size();
    }

    @Override
    public Object getItem(int position) {
        return networkLogList==null?null:networkLogList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return networkLogList==null?null:networkLogList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MyViewHolder holder;
        NetworkLog networkLog = (NetworkLog) getItem(position);
        if(convertView==null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_log, null);
            holder = new MyViewHolder();
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.url = (TextView) convertView.findViewById(R.id.url);
            holder.code = (TextView) convertView.findViewById(R.id.code);
            holder.status = (ImageView) convertView.findViewById(R.id.code_img);
            convertView.setTag(holder);
        }
        else {
            holder= (MyViewHolder) convertView.getTag();
        }
        holder.date.setText(dateFormat.format(new Date(networkLog.getDate())));
        holder.url.setText("[" + networkLog.getRequestType() + "] " + networkLog.getUrl());
        holder.code.setText(networkLog.getResponseCode());
        if (networkLog.getResponseCode().startsWith("2")) {
            holder.status.setBackgroundColor(Color.GREEN);
            holder.code.setTextColor(Color.GREEN);
        } else if (networkLog.getResponseCode().startsWith("4")) {
            holder.status.setBackgroundColor(Color.parseColor("#ffa500"));
            holder.code.setTextColor(Color.parseColor("#ffa500"));
        } else if (networkLog.getResponseCode().startsWith("5")) {
            holder.status.setBackgroundColor(Color.RED);
            holder.code.setTextColor(Color.RED);
        }
        return   convertView;
    }

    private class MyViewHolder  {
          TextView date, url, code;
          ImageView status;

    }

    public void flush(List<NetworkLog> networkLogs){
        if(networkLogs==null)
            return;
        networkLogList.clear();
        networkLogList.addAll(networkLogs);
        notifyDataSetChanged();
    }

    public void clear() {
        if(networkLogList==null)
            return;
        networkLogList.clear();
        notifyDataSetChanged();
    }
}
