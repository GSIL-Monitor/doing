package com.doing.team.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.doing.team.DoingApplication;
import com.doing.team.R;
import com.doing.team.activity.ContentDetailActivity;
import com.doing.team.activity.PublishActivity;
import com.doing.team.adapter.ContentListAdapter;
import com.doing.team.bean.ContentListItemInfo;
import com.doing.team.properties.Constant;
import com.doing.team.pulltorefresh.PullToRefreshBase;
import com.doing.team.pulltorefresh.PullToRefreshListView;
import com.qihoo.haosou.msearchpublic.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ContentListFragment extends BaseFragment implements View.OnClickListener,PullToRefreshBase.OnRefreshListener2{
    private long mExitTime = 0;
    private View mView;
    private PullToRefreshListView refreshList;
    private ListView mListView;
    private ImageView myNewsImage;
    private ImageView publishCameraImage;
    private ImageView personalImage;
    private List<ContentListItemInfo> infos;
    private ContentListAdapter listAdapter;

    ContentListItemInfo info1=new ContentListItemInfo();
    ContentListItemInfo info2=new ContentListItemInfo();
    ContentListItemInfo info3=new ContentListItemInfo();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.content_list_fragment, container, false);
        refreshList = (PullToRefreshListView)mView.findViewById(R.id.content_list);
        refreshList.setMode(PullToRefreshBase.Mode.BOTH);
        refreshList.setOnRefreshListener(this);
        mListView = refreshList.getRefreshableView();
        myNewsImage = (ImageView)mView.findViewById(R.id.my_news_imag);
        publishCameraImage = (ImageView)mView.findViewById(R.id.publish_camera_imag);
        personalImage = (ImageView)mView.findViewById(R.id.personal_imag);
        myNewsImage.setOnClickListener(this);
        personalImage.setOnClickListener(this);
        publishCameraImage.setOnClickListener(this);


        infos = new ArrayList<ContentListItemInfo>();
        info1.imageTypy = Constant.DOUBLE_IMAGE;
        info2.imageTypy = Constant.VERTICAL_IMAGE;
        info3.imageTypy = Constant.HORIZONTAL_IMAGE;
        infos.add(info1);
//        infos.add(info2);
//        infos.add(info3);

        listAdapter = new ContentListAdapter(getActivity());
        listAdapter.setListInfos(infos);
        mListView.setAdapter(listAdapter);
        return mView;
    }

    @Override
    public boolean onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(getActivity(), R.string.enter_again_and_exit, Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            getActivity().finish();
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.my_news_imag:
                break;
            case R.id.publish_camera_imag:
                getActivity().startActivity(new Intent(getActivity(),PublishActivity.class));
                break;
            case R.id.personal_imag:
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        Log.i("wzh", "dowm");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                info1.imageTypy = Constant.DOUBLE_IMAGE;
                infos.add(0, info1);
                listAdapter.setListInfos(infos);
                refreshView.onRefreshComplete();
            }
        }, 500);

    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
        Log.i("wzh", "up");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                info3.imageTypy = Constant.HORIZONTAL_IMAGE;
                infos.add(info3);
                listAdapter.setListInfos(infos);
                refreshView.onRefreshComplete();
            }
        }, 500);
    }

    private Handler handler = new Handler();
}
