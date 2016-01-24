package com.doing.team.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue.IdleHandler;
import android.support.v4.app.Fragment;

import com.doing.team.eventdefs.ApplicationEvents;
import com.doing.team.fragment.BaseFragment;
import com.doing.team.fragment.ContentListFragment;
import com.doing.team.fragment.RegisterFragment;
import com.doing.team.util.NetworkManager;
import com.doing.team.util.SharePreferenceHelper;
import com.doing.team.eventbus.QEventBus;
import com.doing.team.util._INetworkChange;
import com.doing.team.DoingApplication;
import com.doing.team.R;
import com.doing.team.eventdefs.ApplicationEvents.QueueIdleTask;

/**
 * 主界面，承载各fragment框架
 *
 * @author wangzhiheng
 */
public class DoingActivity extends BaseActivity implements _INetworkChange {

    private ArrayList<Runnable> idleTaskList = new ArrayList<Runnable>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QEventBus.getEventBus().register(this);
        setContentView(R.layout.activity_doing);
        registerFragments();
        gotoNextFragmnet();
//        init();

    }


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
    private void init() {


        NetworkManager.getInstance(DoingApplication.getInstance()).addNetworkChangeListener(this); // 监听网络状态变化
        //初始化空闲线程Handler
        Looper.myQueue().addIdleHandler(new IdleHandler() {
            @Override
            public boolean queueIdle() {
                while (idleTaskList.size() > 0) {
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
        registerFragment(ContentListFragment.class, R.id.main_container);
        registerFragment(RegisterFragment.class, R.id.main_container);
    }


    /**
     * 检查Intent事件
     *
     * @param intent
     * @param isOnNewIntent
     */
    private void checkIntent(Intent intent, boolean isOnNewIntent, boolean noSplash) {
        if (intent == null)
            return;
        if (noSplash) {
            gotoNextFragmnet();
        } else {
            gotoNextFragmnet();
        }
//        IntentChecker.getInstance().doCheck(intent, this, isOnNewIntent,noSplash);
    }

    private void gotoNextFragmnet() {
        if (SharePreferenceHelper.isRegister()) {
            switchToFragment(ContentListFragment.class, false);
        } else {
            switchToFragment(RegisterFragment.class, false);

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkIntent(intent, true, false);
    }

    @Override
    public void onBackPressed() {

        boolean fragmentHandled = false;
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment != null && currentFragment instanceof BaseFragment) {
            fragmentHandled = ((BaseFragment) currentFragment).onBackPressed();
        }

        if (!fragmentHandled) {
            super.onBackPressed();
            QEventBus.getEventBus().post(new ApplicationEvents.GoHomeOrExit());
        }
    }


    /**
     * 切换fragment
     *
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
     *
     * @param event
     */
    public void onEventMainThread(QueueIdleTask event) {
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

}
