package com.doing.team.activity;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue.IdleHandler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.doing.team.eventdefs.ApplicationEvents;
import com.doing.team.fragment.BaseFragment;
import com.doing.team.fragment.HompageFragment;
import com.doing.team.fragment.RegisterFragment;
import com.doing.team.util.NetworkManager;
import com.doing.team.util.SharePreferenceHelper;
import com.qihoo.haosou.msearchpublic.util.LogUtils;
import com.doing.team.util.NetworkManager;
import com.doing.team.eventbus.QEventBus;
import com.doing.team.util._INetworkChange;
import com.doing.team.DoingApplication;
import com.doing.team.R;
import com.doing.team.eventdefs.ApplicationEvents.QueueIdleTask;

/**
 * 主界面，承载各fragment框架
 * @author wangzhiheng
 *
 */
public class DoingActivity extends BaseActivity implements _INetworkChange{
	 
	private ArrayList<Runnable> idleTaskList = new ArrayList<Runnable>();
	protected static final String GOTO_READ_SMS = "goto_read_sms";  
	private final String NO_SPLASH = "noSplash";  
	

    private boolean isAroundActivityShowing(Context context) {
        try {
            ActivityManager am = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

            return cn.getPackageName().equals(context.getPackageName());
        } catch (Throwable e) {
            e.printStackTrace();
            return true;
        }
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		QEventBus.getEventBus().register(this);
		setContentView(R.layout.activity_around);
		registerFragments();
		switchToFragment(HompageFragment.class,false);
		//处理系统回收的特殊情况，用于重启后避免开启splashFragment
        if(savedInstanceState!=null){
        	if(savedInstanceState.getBoolean(NO_SPLASH)){
        		Intent intent = getIntent();
                checkIntent(intent, false,true);
        	}
        }else {
        	Intent intent = getIntent();
            checkIntent(intent, false,false);
		}
        init();
        
	}
	
	
	
	
//	@Override
//	protected void onRestart()
//	{
//		super.onRestart(); 
//		if (getCurrentFragment() instanceof TabBaseFragment&& 
//				((TabBaseFragment)getCurrentFragment()).getCurrentFragment() instanceof TabHomePageFragment) {
//			ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
//			try {
//				//可能报空指针异常
//				ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//				if (cn!=null&&cn.equals(this.getComponentName())){
//					Constant.mIsHomeRelocate=true;
//					QEventBus.getEventBus().post(new GlobalEvents.UpdateLocation(true, false));
//				}
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//		}
//	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		 NetworkManager.getInstance(DoingApplication.getInstance()).unRegisterReceiver();
		QEventBus.getEventBus().unregister(this);
	}

	/**
	 * 初始化
	 */
	private void init(){
		

	    NetworkManager.getInstance(DoingApplication.getInstance()).addNetworkChangeListener(this); // 监听网络状态变化
		//初始化空闲线程Handler
		Looper.myQueue().addIdleHandler(new IdleHandler() {
            @Override
            public boolean queueIdle() {
	            while(idleTaskList.size()>0){
	                Runnable runnable = idleTaskList.get(0);
	                runnable.run();
	                idleTaskList.remove(0);
	            }
	            return true;
            }
        });
	}
	
	
	/**
	 * 注册到fragment管理器
	 */
	private void registerFragments() {
		registerFragment(HompageFragment.class,R.id.main_container);
        registerFragment(RegisterFragment.class, R.id.main_container);
    }
	
	
	/**
	 * 检查Intent事件
	 * @param intent
	 * @param isOnNewIntent
	 */
	private void checkIntent(Intent intent, boolean isOnNewIntent,boolean noSplash) {
        if (intent == null)
            return;
        if (noSplash) {
            gotoNextFragmnet();
        }else {
            gotoNextFragmnet();
        }
//        IntentChecker.getInstance().doCheck(intent, this, isOnNewIntent,noSplash);
    }
	private void gotoNextFragmnet(){
	    if (SharePreferenceHelper.isRegister()) {
            switchToFragment(HompageFragment.class, false);
        }else {
            switchToFragment(RegisterFragment.class, false);
            
        }
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		checkIntent(intent, true,false);
        //checkFirstBootCount(intent);
	}
	
	@Override
	public void onBackPressed()
	{
	    
        boolean fragmentHandled = false;
        Fragment currentFragment = getCurrentFragment();
        boolean needFilter = true;
        if(currentFragment!=null && currentFragment instanceof BaseFragment)
        {
        	fragmentHandled = ((BaseFragment)currentFragment).onBackPressed();   
        	if (((BaseFragment)currentFragment).getPrevFragmentClass()!=null&&((BaseFragment)currentFragment).getPrevFragmentClass().equals(currentFragment.getClass())) {
				needFilter=true;
			}
        }
        
        if(!fragmentHandled)
        {
        	super.onBackPressed();
        	//从push通知进入app的browser以后，按back返回tabbasefragment，所以加了下面一句，其他activity按back会崩溃
        	if(needFilter){
        	    QEventBus.getEventBus().post(new ApplicationEvents.GoHomeOrExit());
        	}
        }
	}

	
	
	
	/**
	 * 切换fragment
	 * @param event
	 */
	public void onEventMainThread(final ApplicationEvents.SwitchToFragment event) {
        switchToFragment(event.fragmentClass, event.addToBackStack);
    }

	public void onEventMainThread(ApplicationEvents.ExitApplication event) {
        finish();
    }
    
    
    /**
     * 空闲任务
     * @param event
     */
    public void onEventMainThread(QueueIdleTask event){
    	idleTaskList.add(event.runnable);
    }
    
    


	@Override
	public void onNetworkChanged(int type) {
		//   网络变成有网时， 重新去定位
		if (type != NetworkManager.TYPE_NONE) {
		
		}
		/*if(type == NetworkManager.TYPE_NONE){
			ToastUtils.show(AroundActivity.this, getResources().getString(R.string.net_errors));
			QEventBus.getEventBus().postSticky(new ApplicationEvents.OnShowErrorLocation(AroundActivity.this.getResources().getString(R.string.no_suggess)));
		}*/
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putBoolean(NO_SPLASH,true);
		super.onSaveInstanceState(outState);
	}
  
}
