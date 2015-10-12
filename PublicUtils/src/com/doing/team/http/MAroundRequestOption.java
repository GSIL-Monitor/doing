package com.doing.team.http;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;

public class MAroundRequestOption {
	
	public enum HttpMethod{
		DEPRECATED_GET_OR_POST(-1),GET(0),POST(1),PUT(2),DELETE(3),HEAD(4),OPTIONS(5),TRACE(6),PATCH(7);
		private int value;
		private HttpMethod(int value){
			this.value=value;
		}
		public int getvalue(){
			return value;
		}
	}
	private Map<String, String> mQihooHeader=new HashMap<String, String>();
	private Map<String, String> mQihooPostParam=new HashMap<String, String>();
	
	public MAroundRequestOption() {
		mQihooHeader.put("User-Agent", "QihooMSearch mso_app");
		mQihooHeader.put("Referer", "http://m.so.com");
	}
	public Map<String, String> getHeaders(Map<String, String> parent) throws AuthFailureError {
		Map<String, String> mapAll=new HashMap<String, String>();
		if(parent !=null && parent.size()>0){
			mapAll.putAll(parent);
		}
		if(mQihooHeader!=null && mQihooHeader.size()>0){
			mapAll.putAll(mQihooHeader);
		}
		return mapAll;
	}
	protected Map<String, String> getParams(Map<String, String> parent) throws AuthFailureError{
		Map<String, String> mapAll=new HashMap<String, String>();
		if(parent !=null && parent.size()>0){
			mapAll.putAll(parent);
		}
		if(mQihooPostParam!=null && mQihooPostParam.size()>0){
			mapAll.putAll(mQihooPostParam);
		}
		return mapAll;
	}
	public void Addheader(String key,String value){
		mQihooHeader.put(key, value);
	}
	public void AddNormalParam(String key,String value){
		mQihooPostParam.put(key, value);
	}
}
