package com.doing.team.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.doing.team.DoingApplication;
import com.doing.team.R;

import java.util.List;

/**
 * Created by wangzhiheng on 2016/1/24.
 */
public class ContentListAdapter extends BaseAdapter {
    List<View> list;
    public ContentListAdapter(List<View> list){
        this.list = list;

    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return list.get(position);
    }
}
