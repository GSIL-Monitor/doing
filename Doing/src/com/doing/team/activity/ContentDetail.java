package com.doing.team.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.doing.team.R;
import com.doing.team.adapter.ContentListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzhiheng on 2016/1/24.
 */
public class ContentDetail extends Activity implements View.OnClickListener{
    private View back;
    private View report;
    private ListView listView;
    private TextView comments;
    private EditText replyText;
    private TextView commit;
    private ContentListAdapter listAdapter;
    private View view;
    private List<View> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_detail);
        back = findViewById(R.id.content_detail_back);
        report = findViewById(R.id.content_detail_report);
        listView = (ListView) findViewById(R.id.content_detail_comment_detail);
        comments = (TextView) findViewById(R.id.content_detail_comments);
        replyText = (EditText) findViewById(R.id.content_detail_reply_text);
        commit = (TextView) findViewById(R.id.content_detail_reply_commit);
        back.setOnClickListener(this);
        commit.setOnClickListener(this);

        list = new ArrayList<View>();
        view = View.inflate(this,R.layout.content_list_item_title,null);
        list.add(view);
        listAdapter = new ContentListAdapter(list);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.content_detail_back:
                finish();
                break;
            case R.id.content_detail_reply_commit:
                list.add(view);
                listAdapter.notifyDataSetChanged();
                break;
        }
    }
}
