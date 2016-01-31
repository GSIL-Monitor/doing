package com.doing.team.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.doing.team.DoingApplication;
import com.doing.team.R;
import com.doing.team.activity.ContentDetailActivity;
import com.doing.team.activity.ZoomImageActivity;
import com.doing.team.bean.ContentListItemInfo;
import com.doing.team.properties.Constant;
import com.doing.team.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzhiheng on 2016/1/24.
 */
public class ContentListAdapter extends BaseAdapter {
    List<ContentListItemInfo> list;
    final private Context context;

    public ContentListAdapter(Context context) {
        list = new ArrayList<ContentListItemInfo>();
        this.context = context;
    }

    public void setListInfos(List<ContentListItemInfo> list) {
        if (list != null) {
            this.list = list;
            notifyDataSetChanged();
        }
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
    public int getItemViewType(int position) {
        return list.get(position).imageTypy;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            switch (getItemViewType(position)) {
                case Constant.HORIZONTAL_IMAGE:
                    convertView = View.inflate(DoingApplication.getInstance(), R.layout.content_list_one_horizontal_image_item, null);
                    viewHolder.image = (ImageView) convertView.findViewById(R.id.content_image);
                    break;
                case Constant.DOUBLE_IMAGE:
                    convertView = View.inflate(DoingApplication.getInstance(), R.layout.content_list_double_image_item, null);
                    viewHolder.image1 = (ImageView) convertView.findViewById(R.id.content_image1);
                    viewHolder.image2 = (ImageView) convertView.findViewById(R.id.content_image2);
                    break;
                case Constant.VERTICAL_IMAGE:
                    convertView = View.inflate(DoingApplication.getInstance(), R.layout.content_list_one_vertical_image_item, null);
                    viewHolder.image = (ImageView) convertView.findViewById(R.id.content_image);
                    break;
                default:
                    convertView = View.inflate(DoingApplication.getInstance(), R.layout.content_list_one_horizontal_image_item, null);
            }
            viewHolder.headImage = (CircleImageView) convertView.findViewById(R.id.user_head_image);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        switch (getItemViewType(position)) {
            case Constant.HORIZONTAL_IMAGE:
            case Constant.VERTICAL_IMAGE:
                viewHolder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, ZoomImageActivity.class));
                    }
                });
                break;
            case Constant.DOUBLE_IMAGE:
                viewHolder.image1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, ZoomImageActivity.class));
                    }
                });
                viewHolder.image2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, ZoomImageActivity.class));
                    }
                });
                break;
        }
        viewHolder.headImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ContentDetailActivity.class);
                switch (list.get(position).imageTypy) {
                    case Constant.HORIZONTAL_IMAGE:
                        intent.putExtra(Constant.IMAGE_TYPE, Constant.HORIZONTAL_IMAGE);
                        break;
                    case Constant.VERTICAL_IMAGE:
                        intent.putExtra(Constant.IMAGE_TYPE, Constant.VERTICAL_IMAGE);
                        break;
                    case Constant.DOUBLE_IMAGE:
                        intent.putExtra(Constant.IMAGE_TYPE, Constant.DOUBLE_IMAGE);
                        break;
                }
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    class ViewHolder {
        public CircleImageView headImage;
        public ImageView image;
        public ImageView image1;
        public ImageView image2;
    }
}
