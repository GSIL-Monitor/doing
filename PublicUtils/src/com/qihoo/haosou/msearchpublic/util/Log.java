package com.qihoo.haosou.msearchpublic.util;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
/**
 * note :此文件存放的目录需与liblogutils.so文件中路径一致，要么就得重新改写liblogutils.so并编译。先这样吧。。
 *  by wangzefeng
 *
 */
public class Log {
	
	//只有大于或等于当前日志等级的日志信息才会被记录
	//当前日志等级为LEVEL_NONE时，任何日志都不会被记录
	//LEVEL_VERBOSE 等级的日志可在任何地方添加，比较随意，日志优先级最低
	//LEVEL_DEBUG 	等级的日志可在任何有调试需求的地方添加
	//LEVEL_INFO  	等级的日志记录一般的执行信息或者进入一个模块退出一个模块时使用
	//LEVEL_WARN  	等级的日志在警告时使用
	//LEVEL_ERROR 	等级的日志在发生错误时使用
	public static final int LEVEL_NONE = 999;
	public static final int LEVEL_VERBOSE = 0;
	public static final int LEVEL_DEBUG = 1;
	public static final int LEVEL_INFO = 2;
	public static final int LEVEL_WARN = 3;
	public static final int LEVEL_ERROR = 4;
	

	//默认日志级别
	public static final int DEFAULT_DEBUG_LEVEL = LEVEL_NONE;//LEVEL_DEBUG;// LEVEL_ERROR;
	
	//是否输出到LogCat
	public static final boolean USE_LOGCAT = true;
	
	//单个日志文件默认大小
	public static final int DEFAULT_LOG_FILE_MAX_SIZE = 1024*1024*100;
	

	public native static boolean nativeInit(boolean useLogcat,int debugLevel,int maxSize);
	public native static void nativeLog(int level,String tag,String msg);
	public native static void nativeSetDebugLevel(int debugLevel);
	public native static void nativeSetOutFile(String outFile);
	

	
	private static int DebugLevel = DEFAULT_DEBUG_LEVEL;
	
	
//	public static void log(int level,String tag,String msg){
//		if(level >= DebugLevel){
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//			sdf.format(new Date());
////			String pre = 
//			nativeLog(level,tag,msg);
//		}
//	}
	

	public static boolean isDebug(){
		return DebugLevel <= Log.LEVEL_DEBUG;
	}

	public static int getDebugLevel(){
		return DebugLevel;
	}
	
	private static void initParams(){
		try{
			nativeInit(USE_LOGCAT,DEFAULT_DEBUG_LEVEL,DEFAULT_LOG_FILE_MAX_SIZE);
		}catch(Throwable thr){
			
		}
		DebugLevel = DEFAULT_DEBUG_LEVEL;
	}
	
	public static void init(){
		System.loadLibrary("logutil");
		initParams();
	}
	
	public static void setOutFile(String logFile){
		if(logFile != null){
			new File(logFile).getParentFile().mkdirs();
			try{
				nativeSetOutFile(logFile);
			}catch(Throwable thr){
				
			}
		}
	}
	
	public static void init(String libPath){
		System.load(libPath);
		initParams();
	}
	
	public static void stop(){
		try{
			nativeSetDebugLevel(LEVEL_NONE);
		}catch(Throwable thr){
			
		}
	}
	
	public static void setDebugLevel(int debugLevel){
		try{
			nativeSetDebugLevel(debugLevel);
		}catch(Throwable thr){
			
		}
		DebugLevel = debugLevel;
	}

	public static void callNativeLog(int level,String tag,String msg){
		try {
			nativeLog(level,tag,msg);
		} catch (Throwable thr) {
			// TODO: handle exception
		}
	}
	
	public static void v(String tag,String msg){
		try {
			callNativeLog(LEVEL_VERBOSE,tag,msg);
		} catch (Throwable thr) {
			// TODO: handle exception
		}
	}

	public static void i(String tag,String msg){
		callNativeLog(LEVEL_INFO,tag,msg);
	}

	public static void d(String tag,String msg){
		callNativeLog(LEVEL_DEBUG,tag,msg);
	}

	public static void w(String tag,String msg){
		callNativeLog(LEVEL_WARN,tag,msg);
	}

	public static void e(String tag,String msg){
		callNativeLog(LEVEL_ERROR,tag,msg);
	}

	public static void v(String msg){
		callNativeLog(LEVEL_VERBOSE,genTag(),msg);
	}

	public static void i(String msg){
		callNativeLog(LEVEL_INFO,genTag(),msg);
	}

	public static void d(String msg){
		callNativeLog(LEVEL_DEBUG,genTag(),msg);
	}

	public static void w(String msg){
		callNativeLog(LEVEL_WARN,genTag(),msg);
	}

	public static void e(String msg){
		callNativeLog(LEVEL_ERROR,genTag(),msg);
	}
	
	
//	public static void e(String tag,Exception exception){
//		native_log(LEVEL_ERROR,tag,getStackTraceString(exception));
//	}

	public static void e(String tag,Throwable tr){
		if(DebugLevel <= LEVEL_ERROR)
			callNativeLog(LEVEL_ERROR,tag,getStackTraceString(tr));
	}

	public static void e(Throwable tr){
		if(DebugLevel <= LEVEL_ERROR)
			callNativeLog(LEVEL_ERROR,genTag(),getStackTraceString(tr));
	}

	public static void e(String tag,String msg,Throwable tr){
		if(DebugLevel <= LEVEL_ERROR)
			callNativeLog(LEVEL_ERROR,tag,msg+"\n"+getStackTraceString(tr));
	}
	
	public static String getStackTraceString(Throwable tr) {
		if (tr == null) {
			return "";
		}

		Throwable t = tr;
		while (t != null) {
		        if (t instanceof UnknownHostException) {
		           return "";
			}
			t = t.getCause();
		}
		
		StringWriter sw = new StringWriter();
		
		PrintWriter pw = new PrintWriter(sw, false);
		tr.printStackTrace(pw);
		pw.flush();
		return sw.toString();
	}
	

    private static String genTag() {
    	StackTraceElement element = Thread.currentThread().getStackTrace()[4];
        String tag = "%s[%s, %d]";
        String callerClazzName = element.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, element.getMethodName(), element.getLineNumber());
        return tag;
    }

	

}
