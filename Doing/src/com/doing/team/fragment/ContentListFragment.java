package com.doing.team.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.doing.team.R;
import com.doing.team.pulltorefresh.PullToRefreshListView;

public class ContentListFragment extends BaseFragment{
    private View mView;
    private PullToRefreshListView mlist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.content_list_fragment, container, false);
        mlist = (PullToRefreshListView)mView.findViewById(R.id.content_list);

        return mView;
    }
}
