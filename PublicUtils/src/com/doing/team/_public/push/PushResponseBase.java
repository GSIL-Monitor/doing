package com.doing.team._public.push;

public class PushResponseBase {
	private String id;
	private String pushid;
	private String ver;
	private String from;
	private String module;
	private String time;
	private String type;
	private String query;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getPushid() {
		return pushid;
	}
	public void setPushid(String pushid) {
		this.pushid = pushid;
	}
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getModule() {
		return module;
	}
	public void setModule(String module) {
		this.module = module;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getQuery() {
	    return query;
	}
	public void setQuery(String query) {
	    this.query = query;
	}
	public class PushResponse<T> extends PushResponseBase{
		private T msg;

        public PushResponse() {
        }

        public T getMsg() {
			return msg;
		}
		public void setMsg(T msg) {
			this.msg = msg;
		}
	}
	@Override
	public String toString() {
		return "PushResponseBase [id=" + id + ", pushid=" + pushid + ", ver=" + ver + ", from=" + from
				+ ", module=" + module + ", time=" + time + ", type=" + type
				+ ", query=" + query + "]";
	}

}
