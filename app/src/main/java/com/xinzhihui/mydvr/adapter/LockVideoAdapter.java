package com.xinzhihui.mydvr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xinzhihui.mydvr.R;
import com.xinzhihui.mydvr.utils.StringUtils;

import java.util.List;

/**
 * 锁视频Adapter
 * Created by Administrator on 2016/10/18.
 */

public class LockVideoAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mPathList;

    public LockVideoAdapter(List<String> pathList, Context context) {
        mPathList = pathList;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mPathList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPathList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LockVideoAdapter.ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_filelist, null);

            viewHolder.img = (ImageView) convertView.findViewById(R.id.img_file_list);
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_file_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (LockVideoAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.img.setBackgroundResource(R.drawable.icon_doc);
        viewHolder.title.setText(StringUtils.getPathSubName(mPathList.get(position)));
        return convertView;
    }

    public final class ViewHolder {
        public ImageView img;
        public TextView title;
        public TextView path;
    }
}
