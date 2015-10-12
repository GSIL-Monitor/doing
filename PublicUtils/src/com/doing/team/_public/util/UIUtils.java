package com.doing.team._public.util;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class UIUtils {

    /**
     * 自定义Toast
     *
     * @param duration
     *            Toast.LENGTH_LONG or Toast.LENGTH_SHORT or seconds
     * */
    public static void showToast(Context context, int stringId, int duration) {
        showToast(context, context.getString(stringId), duration);
    }

    /**
     * 自定义Toast
     *
     * @param duration
     *            Toast.LENGTH_LONG or Toast.LENGTH_SHORT or seconds
     * */
    public static void showToast(Context context, String text, int duration) {
        Toast.makeText(context, text, duration).show();
    }

    /**
     * 动态替换文本并改变文本颜色
     * 仅限制一种颜色
     */
    public static SpannableStringBuilder getSpannableStringBuilder(Context context, int resStrId, int resColorId,
            String arg1) {
        return getSpannableStringBuilder(context, context.getString(resStrId, arg1), resColorId, arg1);
    }

    /**
     * 动态替换文本并改变文本颜色
     * 仅限制一种颜色
     */
    public static SpannableStringBuilder getSpannableStringBuilder(Context context, int resStrId, int resColorId,
            String arg1, String arg2) {
        return getSpannableStringBuilder(context, context.getString(resStrId, arg1, arg2), resColorId, arg1, arg2);
    }

    /**
     * 动态替换文本并改变文本颜色
     * 仅限制一种颜色
     */
    public static SpannableStringBuilder getSpannableStringBuilder(Context context, String resStr, int resColorId,
            String... args) {

        SpannableStringBuilder spanStr = new SpannableStringBuilder(resStr);
        int i = -1;
        for (String arg : args) {
            i = resStr.indexOf(arg, i + 1);
            spanStr.setSpan(new ForegroundColorSpan(context.getResources().getColor(resColorId)), i, i + arg.length(),
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return spanStr;
    }

    /**
     * 动态替换文本并改变文本颜色
     */
    public static SpannableStringBuilder changeLastColor(Context context, String text, int resColorId, String arg) {

        SpannableStringBuilder spanStr = new SpannableStringBuilder(text);
        int argIndex = text.lastIndexOf(arg);
        spanStr.setSpan(new ForegroundColorSpan(context.getResources().getColor(resColorId)), argIndex,
                argIndex + arg.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return spanStr;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    
    /**
     * 将字符串改为全角
     */
    public static String ToDBC(String input) {
    	
    	if (TextUtils.isEmpty(input)) {
			return "";
		}
    	   char[] c = input.toCharArray();
    	   for (int i = 0; i< c.length; i++) {
    	       if (c[i] == 12288) {
    	         c[i] = (char) 32;
    	         continue;
    	       }if (c[i]> 65280&& c[i]< 65375)
    	          c[i] = (char) (c[i] - 65248);
    	       }
    	   return new String(c);
    	}
    
    
    /**
     * 去除特殊字符或将所有中文标号替换为英文标号
     */
    public static String StringFilter(String str) throws PatternSyntaxException{
        str=str.replaceAll("【","[").replaceAll("】","]").replaceAll("！","!");//替换中文标号
        String regEx="[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
     return m.replaceAll("").trim();
    }
    
    public static String getFileData(Context context,String fileName){
        
        FileInputStream inStream= null;       
        try{
            inStream = context.openFileInput(fileName);
        }catch(FileNotFoundException e){
            return null;
        }
        
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();//输出到内存
        int len=0;
        byte[] buffer = new byte[1024];
        try {
            while((len=inStream.read(buffer))!=-1){
            outStream.write(buffer, 0, len);//
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        byte[] content_byte = outStream.toByteArray();
        String content = new String(content_byte);
        return content;
    }
}
