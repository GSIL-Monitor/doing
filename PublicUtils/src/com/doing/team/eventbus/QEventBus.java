package com.doing.team.eventbus;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;

import com.doing.team._public.util.ApkUtil;
import com.qihoo.haosou.msearchpublic.util.LogUtils;

import de.greenrobot.event.EventBus;

public class QEventBus
{
	private EventBus eventBus;
	
	private static Map<String, QEventBus> mapEventBus = new HashMap<String, QEventBus>();
	private static final Object lock = new Object();
	
    public static final int EVENTBUS_PRIORITY_HIGH 	= 100;
    public static final int EVENTBUS_PRIORITY_NORMAL= 50;
    public static final int EVENTBUS_PRIORITY_LOW 	= 10;

    static private Boolean isProductPack = null;
    static private boolean isProductionPackage() {
        if (isProductPack==null) {
            isProductPack = true;
            try {
                do {
                    Class appClass = Class.forName("com.doing.team.DoingApplication");
                    if (appClass == null) {
                        break;
                    }
                    Method getInstanceMethod = appClass.getMethod("getInstance");
                    if (getInstanceMethod == null) {
                        break;
                    }
                    Object appObj = getInstanceMethod.invoke(null);
                    if (appObj == null) {
                        break;
                    }
                    Method getAppContextMethod = appClass.getMethod("getApplicationContext");
                    if (getAppContextMethod == null) {
                        break;
                    }
                    Context context = (Context) getAppContextMethod.invoke(appObj);
                    if (context == null) {
                        break;
                    }
                    isProductPack = ApkUtil.isProductionPackage(context);
                } while (false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (isProductPack==null) {
            return true;
        }

        return isProductPack;
    }

    public QEventBus()
    {
        eventBus = EventBus.builder().logNoSubscriberMessages(false).sendNoSubscriberEvent(false).throwSubscriberException(!isProductionPackage()).build();
    }
    
	public static QEventBus getEventBus(String TAG)
	{
		synchronized(lock) 
		{
			if (TextUtils.isEmpty(TAG))
			{
				TAG = "default";
			}
			
			if(!mapEventBus.containsKey(TAG))
			{
				mapEventBus.put(TAG, new QEventBus());
			}
			
			return mapEventBus.get(TAG);
		}
	}
	
	public static QEventBus getEventBus()
	{
		return getEventBus(null);
	}

	public void register(Object subscriber)
	{
		register(subscriber, EVENTBUS_PRIORITY_NORMAL);
	}

	public void register(Object subscriber, int priority)
    {
        try {
            if (!isProductionPackage()) {
                LogUtils.d("QEventBus", "register [" + subscriber.getClass().toString() + "]");
            }

            eventBus.register(subscriber, priority);
        } catch (Exception e) {
            //捕获类似问题于Subscriber class com.qihoo.haosou.h.q already registered to event class com.qihoo.haosou.activity.q
            LogUtils.e(e);
        }
    }

	public void registerSticky(Object subscriber)
	{
		registerSticky(subscriber, EVENTBUS_PRIORITY_NORMAL);
	}
	
	public void registerSticky(Object subscriber, int priority)
    {
		try {
            if (!isProductionPackage()) {
                LogUtils.d("QEventBus", "registerSticky [" + subscriber.getClass().toString() + "]");
            }
		    eventBus.registerSticky(subscriber, priority);
        } catch (Exception e) {
            //捕获类似问题于Subscriber class com.qihoo.haosou.h.q already registered to event class com.qihoo.haosou.activity.q
            LogUtils.e(e);
        }
    }
	
    public boolean isRegistered(Object subscriber)
    {
    	return eventBus.isRegistered(subscriber);
    }

    public void unregister(Object subscriber)
    {
    	try {
            if (!isProductionPackage()) {
                LogUtils.d("QEventBus", "unregister [" + subscriber.getClass().toString() + "]");
            }
    	    eventBus.unregister(subscriber);
        } catch (Exception e) {
            //捕获类似问题于Subscriber class com.qihoo.haosou.h.q already registered to event class com.qihoo.haosou.activity.q
            LogUtils.e(e);
        }
    }
    
    public void post(Object event)
    {
        if (!isProductionPackage()) {
            LogUtils.d("QEventBus", "post [" + event.getClass().toString() + "]");
        }
        eventBus.post(event);
    }
    
    public void cancelEventDelivery(Object event)
    {
    	eventBus.cancelEventDelivery(eventBus);
    }
    
    public void postSticky(Object event)
    {
        if (!isProductionPackage()) {
            LogUtils.d("QEventBus", "postSticky [" + event.getClass().toString() + "]");
        }
    	eventBus.postSticky(event);
    }
    
    public <T> T getStickyEvent(Class<T> eventType)
    {
    	return eventBus.getStickyEvent(eventType);
    }
    
    public <T> T removeStickyEvent(Class<T> eventType) 
    {
    	return eventBus.removeStickyEvent(eventType);
    }
    
    public boolean removeStickyEvent(Object event)
    {
    	return eventBus.removeStickyEvent(event);
    }
    
    public void removeAllStickyEvents()
    {
    	eventBus.removeAllStickyEvents();
    }
    
    public boolean hasSubscriberForEvent(Class<?> eventClass)
    {
    	return eventBus.hasSubscriberForEvent(eventClass);
    }
}
