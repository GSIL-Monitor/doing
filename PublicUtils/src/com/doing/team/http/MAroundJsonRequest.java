package com.doing.team.http;

import java.util.Map;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;

import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.doing.team.http.MAroundRequestOption.HttpMethod;

public class MAroundJsonRequest extends JsonObjectRequest {
    RetryPolicy retryPolicy;
	
	private MAroundRequestOption mOption; 
	public MAroundJsonRequest(HttpMethod method, String url, JSONObject jsonRequest,
			Listener<JSONObject> listener, ErrorListener errorListener) {
		super(method.getvalue(), url, jsonRequest, listener, errorListener);
		mOption = new MAroundRequestOption();
        retryPolicy = new DefaultRetryPolicy(15000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

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

    @Override
    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }
}
