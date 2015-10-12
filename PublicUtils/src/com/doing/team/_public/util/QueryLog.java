package com.doing.team._public.util;

import android.os.Environment;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by starkey on 14/12/29.
 */
public class QueryLog {
    private String query;
    private long startTime;
    private long totalTime = 0;
    private int count = 0;

    private  volatile static  QueryLog instance;
    public final static String QUERY_LOG_DIRECTOY = Environment.getExternalStorageDirectory() + "/360search/querylog.txt";

    public QueryLog() {
    }

    public static QueryLog getInstance() {
        if (instance == null) {
            synchronized (QueryLog.class) {
                instance = new QueryLog();
                File file = new File(QUERY_LOG_DIRECTOY);
                if (file.exists()) {
                    file.delete();
                }
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return instance;
    }

    public void enter(String query) {
        this.query = query;
        this.startTime = System.currentTimeMillis();
    }

    public void leave() {
        if(this.startTime==0)
            return;

        long duration = System.currentTimeMillis() - this.startTime;
        this.totalTime += duration;
        this.count++;

        StringBuffer logContent = new StringBuffer();
        logContent.append("query:").append(query).append(", duration:").append(duration).append(", average:").append(this.totalTime/this.count).append('\n');

        this.startTime = 0;
        this.query = "";

        try
        {
            FileWriter fw = new FileWriter(QUERY_LOG_DIRECTOY,true); //the true will append the new data
            fw.write(logContent.toString());//appends the string to the file
            fw.close();
        }
        catch(IOException ioe)
        {
            System.err.println("IOException: " + ioe.getMessage());
        }
    }
}
