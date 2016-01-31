package com.doing.team.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.doing.team.R;
import com.doing.team.adapter.ContentListAdapter;
import com.doing.team.properties.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzhiheng on 2016/1/24.
 */
public class ContentDetailActivity extends Activity implements View.OnClickListener{
    private View back;
    private View report;
    private ListView listView;
    private TextView comments;
    private EditText replyText;
    private TextView commit;
    private ContentListAdapter listAdapter;
    private int imageType;
    private List<View> list;
    private View horizontalImageView;
    private View verticalImageView;
    private View doubleImageView;
    private LinearLayout contentDistanceLayout;
    private TextView contentDistance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_detail_activity);
        imageType = getIntent().getIntExtra(Constant.IMAGE_TYPE, 0);
        horizontalImageView = findViewById(R.id.content_ditail_horizontal);
        verticalImageView = findViewById(R.id.content_ditail_vertical);
        doubleImageView = findViewById(R.id.content_ditail_double);
        back = findViewById(R.id.content_detail_back);
        report = findViewById(R.id.content_detail_report);
        contentDistanceLayout = (LinearLayout) findViewById(R.id.content_distance_layout);
        contentDistance = (TextView) findViewById(R.id.content_distance);
        listView = (ListView) findViewById(R.id.content_detail_comment_detail);
        comments = (TextView) findViewById(R.id.content_detail_comments);
        replyText = (EditText) findViewById(R.id.content_detail_reply_text);
        commit = (TextView) findViewById(R.id.content_detail_reply_commit);
        contentDistanceLayout.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        commit.setOnClickListener(this);
        list = new ArrayList<View>();
        View view = View.inflate(this,R.layout.content_list_item_title,null);
        list.add(view);
        listAdapter = new ContentListAdapter(list);
        listView.setAdapter(listAdapter);
        switch (imageType){
            case Constant.HORIZONTAL_IMAGE:
                horizontalImageView.setVisibility(View.VISIBLE);
                verticalImageView.setVisibility(View.GONE);
                doubleImageView.setVisibility(View.GONE);
                break;
            case Constant.VERTICAL_IMAGE:
                verticalImageView.setVisibility(View.VISIBLE);
                horizontalImageView.setVisibility(View.GONE);
                doubleImageView.setVisibility(View.GONE);
                break;
            case Constant.DOUBLE_IMAGE:
                doubleImageView.setVisibility(View.VISIBLE);
                verticalImageView.setVisibility(View.GONE);
                horizontalImageView.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.content_detail_back:
                finish();
                break;
            case R.id.content_detail_reply_commit:
                View view = View.inflate(this,R.layout.content_list_item_title,null);
                TextView tv = (TextView) view.findViewById(R.id.content_title);
                tv.setText(getResources().getString(R.string.doing) + replyText.getText());
                list.add(view);
                listAdapter.notifyDataSetChanged();
                comments.setText(list.size()+"");
                replyText.setText("");
                break;
        }
    }
}
