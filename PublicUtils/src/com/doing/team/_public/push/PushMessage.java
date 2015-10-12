package com.doing.team._public.push;

import android.text.TextUtils;

public class PushMessage {
	public enum version{
		VER_START("0"),VER_ONE("1");
		private String name;
		private version(String name){
			this.name=name;
		}
		public static version GetVersion(String name){
			for(version ver:version.values()){
				if(ver.name.equalsIgnoreCase(name)){
					return ver;
				}
			}
			return VER_START;
		}
	}
	public enum module{
		MOD_START("NO"),MOD_MSEARCH("msearch"),
		MOD_MNOTIFY("mod_notifybar"),MOD_CONTROL("ctrl");
		private String name;
		private module(String name){
			this.name=name;
		}
		public static  module GetModule(String name){
			for(module mod:module.values()){
				if(mod.name.equalsIgnoreCase(name)){
					return mod;
				}
			}
			return MOD_START;
		}
		public String getName(){
			return this.name;
		}
	}
	public enum SEARCH_MSG_TYPE{
		TYPE_START("0"),TYPE_ONE("1");
		private String name;
		private SEARCH_MSG_TYPE(String name){
			this.name=name;
		}
		public static  SEARCH_MSG_TYPE GetMsgType(String name){
			for(SEARCH_MSG_TYPE mod:SEARCH_MSG_TYPE.values()){
				if(mod.name.equalsIgnoreCase(name)){
					return mod;
				}
			}
			return TYPE_START;
		}
	}
	public enum SEARCH_CTRL_MSG_TYPE{
		TYPE_NULL(""),TYPE_START_V5("v5");
		private String name;
		private SEARCH_CTRL_MSG_TYPE(String name){
			this.name=name;
		}
		public static  SEARCH_CTRL_MSG_TYPE GetMsgType(String name){
			if(TextUtils.isEmpty(name)){
				return TYPE_NULL;
			}
			for(SEARCH_CTRL_MSG_TYPE mod:SEARCH_CTRL_MSG_TYPE.values()){
				if(mod.name.equalsIgnoreCase(name)){
					return mod;
				}
			}
			return TYPE_NULL;
		}
		
	}

}
