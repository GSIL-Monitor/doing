package com.doing.team.bean;

import android.net.Uri;

import com.doing.team.DoingApplication;
import com.doing.team._public.util.DeviceUtils;

public class UserInfo {
    public int gender;
    public String nick;
    public int age;
    public String profession;
    public Uri headImag;
    private Data data;

    public Data getData() {
        data = new Data();
        data.nickName = nick;
        data.age = age;
        data.occupation = profession;
        data.deviceCode = DeviceUtils.getVerifyId(DoingApplication.getInstance());
        data.sex = gender;
        return data;
    }

    public class Data {
        public String nickName;
        public String occupation;
        public String deviceCode;
        public int sex;
        public int age;
    }
}
