package com.doing.team._public.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.text.format.Time;

public class DateUtils {

	public static String getTime1(){
        long time=System.currentTimeMillis();  
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());  
        Date d1=new Date(time);   
        return format.format(d1); 
    }  
	public static String getTime2(){  
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss",Locale.getDefault());  
        return format.format(new Date());     
    }  
	
	public static String getTime3(){  
	    Calendar calendar = Calendar.getInstance();  
	    String created = calendar.get(Calendar.YEAR) + "年"  
	            + (calendar.get(Calendar.MONTH)+1) + "月"//从0计算  
	            + calendar.get(Calendar.DAY_OF_MONTH) + "日"  
	            + calendar.get(Calendar.HOUR_OF_DAY) + "时"  
	            + calendar.get(Calendar.MINUTE) + "分"+calendar.get(Calendar.SECOND)+"s";  
	    return created;
	   } 
	public static String getTime4(){  
        Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。  
        t.setToNow(); // 取得系统时间。  
        return t.year+"年 "+(t.month+1)+"月 "+t.monthDay+"日 "+t.hour+"h "+t.minute+"m "+t.second;    
    }  
	
	public static String getTime5(){  
		Calendar calendar = Calendar.getInstance();  
        int day = calendar.get(Calendar.DAY_OF_WEEK);  
        String today = null;  
        if (day == 2) {  
            today = "Monday";  
        } else if (day == 3) {  
            today = "Tuesday";  
        } else if (day == 4) {  
            today = "Wednesday";  
        } else if (day == 5) {  
            today = "Thursday";  
        } else if (day == 6) {  
            today = "Friday";  
        } else if (day == 7) {  
            today = "Saturday";  
        } else if (day == 1) {  
            today = "Sunday";  
        } 
        return today;
    }  
}
