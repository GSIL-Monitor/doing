package com.doing.team._public.push;

public interface PushMessageListener {
	public void OnMessage(PushResponseBase base,String json);
}
