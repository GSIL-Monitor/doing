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
    private List<View> list;
    private ContentListAdapter listAdapter;

    View view1 = View.inflate(DoingApplication.getInstance(), R.layout.content_list_one_horizontal_image_item,null);
    View view2 = View.inflate(DoingApplication.getInstance(), R.layout.content_list_one_vertical_image_item,null);
    View view3 = View.inflate(DoingApplication.getInstance(), R.layout.content_list_double_image_item,null);
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


        list = new ArrayList<View>();
        view1.setTag(1);
        view2.setTag(2);
        view3.setTag(3);
        list.add(view1);
        list.add(view2);
        list.add(view3);
        listAdapter = new ContentListAdapter(list);
//        refreshList.setAdapter(listAdapter);
        mListView.setAdapter(listAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch ((Integer)view.getTag()){
                    case 1:
                        Intent intent1 = new Intent(getActivity(), ContentDetailActivity.class);
                        intent1.putExtra(Constant.IMAGE_TYPE,Constant.HORIZONTAL_IMAGE);
                        getActivity().startActivity(intent1);
                        break;
                    case 2:
                        Intent intent2 = new Intent(getActivity(), ContentDetailActivity.class);
                        intent2.putExtra(Constant.IMAGE_TYPE,Constant.VERTICAL_IMAGE);
                        getActivity().startActivity(intent2);
                        break;
                    case 3:
                        Intent intent3 = new Intent(getActivity(), ContentDetailActivity.class);
                        intent3.putExtra(Constant.IMAGE_TYPE,Constant.DOUBLE_IMAGE);
                        getActivity().startActivity(intent3);
                        break;
                }
            }
        });
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
        final View view = View.inflate(DoingApplication.getInstance(), R.layout.content_list_one_horizontal_image_item,null);
        view.setTag(1);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                list.add(0, view);
                listAdapter.notifyDataSetChanged();
                refreshView.onRefreshComplete();
            }
        }, 500);

    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
        Log.i("wzh", "up");
        final View view = View.inflate(DoingApplication.getInstance(), R.layout.content_list_one_vertical_image_item, null);
        view.setTag(2);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                list.add(view);
                listAdapter.notifyDataSetChanged();
                refreshView.onRefreshComplete();
            }
        }, 500);
    }

    private Handler handler = new Handler();
}
