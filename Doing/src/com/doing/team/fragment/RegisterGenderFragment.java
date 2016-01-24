package com.doing.team.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.doing.team.DoingApplication;
import com.doing.team.R;
import com.doing.team.bean.UserInfo;
import com.doing.team.eventbus.QEventBus;
import com.doing.team.eventdefs.ApplicationEvents;

public class RegisterGenderFragment extends BaseFragment implements View.OnClickListener {
    private View mView;
    private TextView female;
    private TextView male;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.reigster_gender_fragment, container, false);
        mView.setOnClickListener(null);
        female = (TextView) mView.findViewById(R.id.gender_female);
        male = (TextView) mView.findViewById(R.id.gender_male);
        female.setOnClickListener(this);
        male.setOnClickListener(this);
        return mView;
    }

    @Override
    public void onClick(View v) {
        UserInfo mUserInfo = DoingApplication.getInstance().getUserInfo();
        switch (v.getId()) {
            case R.id.gender_female:
                if (mUserInfo != null) {
                    mUserInfo.gender = 0;
                } else {
                    mUserInfo = new UserInfo();
                    mUserInfo.gender = 0;
                }
                break;
            case R.id.gender_male:
                if (mUserInfo != null) {
                    mUserInfo.gender = 1;
                } else {
                    mUserInfo = new UserInfo();
                    mUserInfo.gender = 1;
                }
                break;
        }
        DoingApplication.getInstance().saveUserinfo(mUserInfo);
        QEventBus.getEventBus().post(new ApplicationEvents.SwitchFragmentToRegisterGender(RegisterInfoFragment.class, false));
    }
}
