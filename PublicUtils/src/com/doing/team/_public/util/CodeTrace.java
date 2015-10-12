package com.doing.team._public.util;

import com.qihoo.haosou.msearchpublic.util.Log;


public class CodeTrace {
	
	public static final String TAG = "CodeTrace";
	
	private long beginTime;
	private long endTime;
	private StackTraceElement beginStackTraceElement;
	private StackTraceElement endStackTraceElement;
	
	private CodeTrace(){
	}
	
	private static boolean enable(){
		return Log.getDebugLevel() == Log.LEVEL_DEBUG;
		//return false;
	}
	
	public static CodeTrace newInstance(){
		return new CodeTrace();
	}
	
	public long begin(){
		if(!enable())
			return 0;
		beginTime = System.currentTimeMillis();
		beginStackTraceElement = Thread.currentThread().getStackTrace()[3];
//		this.fileName = e.getFileName();
//		this.methodName = e.getMethodName();
//		this.startLineNumber = e.getLineNumber();
		return beginTime;
	}

    public static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[5];
    }
	
	public static CodeTrace beginTrace(){
		if(!enable())
			return new CodeTrace();
		CodeTrace obj = new CodeTrace();
		obj.beginTime = System.currentTimeMillis();
		obj.beginStackTraceElement = Thread.currentThread().getStackTrace()[3];
		return obj;
	}
	
	public long end(){
		if(!enable())
			return 0;
		endTime = System.currentTimeMillis();
		this.endStackTraceElement = Thread.currentThread().getStackTrace()[3];
		return endTime;
	}
	
	public void endp(){
		if(!enable())
			return;
		endTime = System.currentTimeMillis();
		this.endStackTraceElement = Thread.currentThread().getStackTrace()[3];
		printf("");
	}
	
	public void endp(String flag){
		if(!enable())
			return;
		endTime = System.currentTimeMillis();
		this.endStackTraceElement = Thread.currentThread().getStackTrace()[3];
		printf(flag);
	}
	
	public long getCpuTime(){
		return endTime - beginTime;
	}

//	MainActivity.java::OnCreate[10,20]
//	MainActivity.java[OnCreate::10,OnStop::20]
//	MainActivity.java[OnCreate,10],ABC.java[OnStop,20]

	public void printf(){
		printf("");
	}
		
	public void printf(String flag){
		if(!enable())
			return;
		String out = "No supported";
		if(beginStackTraceElement.getFileName().equals(endStackTraceElement.getFileName()) &&
				beginStackTraceElement.getMethodName().equals(endStackTraceElement.getMethodName())
				){
			out = String.format("%s::%s[%d,%d]",beginStackTraceElement.getFileName(),beginStackTraceElement.getMethodName(),
					beginStackTraceElement.getLineNumber(),endStackTraceElement.getLineNumber()
					);
		}else if(beginStackTraceElement.getFileName().equals(endStackTraceElement.getFileName())){
			out = String.format("%s[%s:%d,%s:%d]",beginStackTraceElement.getFileName(),beginStackTraceElement.getMethodName(),
					beginStackTraceElement.getLineNumber(),endStackTraceElement.getMethodName(),endStackTraceElement.getLineNumber()
					);
		}else{
			out = String.format("%s[%s:%d] - %s[%s:%d]",beginStackTraceElement.getFileName(),beginStackTraceElement.getMethodName(),
					beginStackTraceElement.getLineNumber(),endStackTraceElement.getFileName(),endStackTraceElement.getMethodName(),endStackTraceElement.getLineNumber()
					);
		}
		if(flag != null && !"".equals(flag.trim()))
			Log.i(TAG, flag + " - " + out+" excel time "+getCpuTime() + "ms");
		else
			Log.i(TAG, out+" excel time "+getCpuTime() + "ms");
			
	}
	
}
