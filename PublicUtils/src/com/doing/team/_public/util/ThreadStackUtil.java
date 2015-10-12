package com.doing.team._public.util;

public class ThreadStackUtil {

	public static void getCurrentClassName(){
		Thread.currentThread().getStackTrace()[3].getClassName();
	}
	
}
