package io.github.brijoe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.brijoe.R;
import io.github.brijoe.bean.BlockInfo;


public class BlockLogAdapter extends BaseAdapter {

    private List<BlockInfo> blockList;

    private SimpleDateFormat dateFormat;

    private Context mContext;


    public BlockLogAdapter(Context context, List<BlockInfo> blockList) {
        this.mContext = context;
        this.blockList = blockList;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    @Override
    public int getCount() {
        return blockList == null ? 0 : blockList.size();
    }

    @Override
    public Object getItem(int position) {
        return blockList == null ? null : blockList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return blockList == null ? null : blockList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MyViewHolder holder;
        BlockInfo blockInfo = (BlockInfo) getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_block, null);
            holder = new MyViewHolder();
            holder.time = convertView.findViewById(R.id.tv_block_time);
            holder.count=convertView.findViewById(R.id.tv_block_count);
            convertView.setTag(holder);
        } else {
            holder = (MyViewHolder) convertView.getTag();
        }
        holder.time.setText(dateFormat.format(new Date(blockInfo.getTime())));
        holder.count.setText("搜集到[" + blockInfo.getTraceCount() + "]条堆栈信息 ");
        return convertView;
    }

    private class MyViewHolder {
        TextView time, count;
    }

    public void flush(List<BlockInfo> blockList) {
        if (blockList == null)
            return;
        this.blockList.clear();
        this.blockList.addAll(blockList);
        notifyDataSetChanged();
    }

    public void clear() {
        if (blockList == null)
            return;
        blockList.clear();
        notifyDataSetChanged();
    }
}
