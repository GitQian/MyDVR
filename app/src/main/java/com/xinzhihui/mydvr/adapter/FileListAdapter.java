package com.xinzhihui.mydvr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xinzhihui.mydvr.R;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/10/1.
 */
public class FileListAdapter extends BaseAdapter {

    private List<Map<String, Object>> fileList;
    private Context context;

    public FileListAdapter(List<Map<String, Object>> fileList, Context context) {
        this.fileList = fileList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_filelist, null);

            holder.img = (ImageView) convertView.findViewById(R.id.img_file_list);
            holder.title = (TextView) convertView.findViewById(R.id.tv_file_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.img.setBackgroundResource((int) fileList.get(position).get("img"));
        holder.title.setText((String) fileList.get(position).get("title"));
        return convertView;
    }

    public final class ViewHolder {
        public ImageView img;
        public TextView title;
        public TextView path;
    }
}
