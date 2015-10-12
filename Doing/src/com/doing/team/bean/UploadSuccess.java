package com.doing.team.bean;

/**
 * Created by wangzhiheng on 2015/10/12.
 */
public class UploadSuccess {
    public int status;
    public String statusText;
    public Data data;
    class Data{
        public String userId;
        public String password ;
        public String token ;
        public int expire  ;
    }
}
