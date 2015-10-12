package com.doing.team.http;

import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.ImageRequest;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class MAroundImageRequest extends ImageRequest {

	private MAroundRequestOption mOption;
	public MAroundImageRequest(String url, Listener<Bitmap> listener,
			int maxWidth, int maxHeight, Config decodeConfig,
			ErrorListener errorListener) {
		super(url, listener, maxWidth, maxHeight, decodeConfig, errorListener);
		mOption = new MAroundRequestOption();
	}
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		
		Map<String, String> parent=super.getHeaders();
		return mOption.getHeaders(parent);
	}
	@Override 
	protected Map<String, String> getParams() throws AuthFailureError{
	
		Map<String, String> parent=super.getParams();
		return mOption.getParams(parent);
	}
	public void Addheader(String key,String value){
		mOption.Addheader(key, value);
	}
	public void AddNormalParam(String key,String value){
		mOption.Addheader(key, value);
	}
	

}
