package com.doing.team.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.doing.team.R;
import com.doing.team.eventbus.QEventBus;
import com.doing.team.eventdefs.ApplicationEvents;
import com.doing.team.fragment.BaseFragment;
import com.doing.team.util.WidgetUtils;
import com.qihoo.haosou.msearchpublic.util.LogUtils;

public class BaseActivity extends FragmentActivity {
    public static long lastPauseTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // this.setTheme(R.style.Theme_Daylight);
    }

    public class OnDestroy extends BaseActivityEvent {
    }

    @Override
    protected void onDestroy() {
        post(new OnDestroy());
        super.onDestroy();
    }

    public class OnResume extends BaseActivityEvent {
    }

    @Override
    protected void onResume() {
        post(new OnResume());
        super.onResume();
    }

    public class OnPause extends BaseActivityEvent {
    }

    @Override
    protected void onPause() {
        post(new OnPause());
        super.onPause();
        lastPauseTime = System.currentTimeMillis();
    }

    public class OnActivityResult extends BaseActivityEvent {
        public int requestCode;
        public int resultCode;
        public Intent data;

        public OnActivityResult(int requestCode, int resultCode, Intent data) {
            this.requestCode = requestCode;
            this.resultCode = resultCode;
            this.data = data;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        post(new OnActivityResult(requestCode, resultCode, data));
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class OnConfigurationChanged extends BaseActivityEvent {
        public Configuration newConfig;

        public OnConfigurationChanged(Configuration newConfig) {
            this.newConfig = newConfig;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        post(new OnConfigurationChanged(newConfig));
        super.onConfigurationChanged(newConfig);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public class BaseActivityEvent {
        public Activity from;
        public String fromName;
    }

    private void post(BaseActivityEvent event) {
        String className = this.getClass().getName();
        if (!TextUtils.isEmpty(className))// className!=null &&
                                          // !className.isEmpty())
        {
            event.from = this;
            event.fromName = className;
            QEventBus.getEventBus(className).post(event);
        }
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
            // 从push通知进入app的browser以后，按back返回tabbasefragment，所以加了下面一句，其他activity按back会崩溃
            // QEventBus.getEventBus().post(new
            // ApplicationEvents.GoHomeOrExit());
        }
    }

    private class FragmentInfo {
        public Class<? extends Fragment> fragmentClass;
        public int idContainer;

        public FragmentInfo(Class<? extends Fragment> fragmentClass, int idContainer) {
            this.fragmentClass = fragmentClass;
            this.idContainer = idContainer;
        }
    }

    private Map<String, FragmentInfo> mapFragmentInfo = new HashMap<String, FragmentInfo>();

    public boolean registerFragment(Class<? extends Fragment> fragmentClass, int idContainer) {
        if (fragmentClass == null)
            return false;

        if (mapFragmentInfo.containsKey(fragmentClass.getName()))
            return false;

        mapFragmentInfo.put(fragmentClass.getName(), new FragmentInfo(fragmentClass, idContainer));
        return true;
    }

    public boolean unregisterFragment(Class<? extends Fragment> fragmentClass) {
        if (fragmentClass == null)
            return false;

        if (!mapFragmentInfo.containsKey(fragmentClass.getName()))
            return false;

        mapFragmentInfo.remove(fragmentClass.getName());
        return true;
    }

    // Fragment切换时不销毁，提高切换Fragment的效率；
    // 因此需要注意切换Fragment时onResume、onPause、onDestroyView、onCreateView等消息不一定会触发
    @SuppressWarnings("unchecked")
    public synchronized boolean switchToFragment(final Class<? extends Fragment> fragmentClass,
           final boolean addToBackStack) {
        LogUtils.d("switchToFragment", "switchToFragment begin...");
        if (fragmentClass == null) {
            LogUtils.d("switchToFragment", "fragmentClass==null");
            return false;
        }

        if (!mapFragmentInfo.containsKey(fragmentClass.getName())) {
            LogUtils.d("switchToFragment", "!mapFragmentInfo.containsKey(fragmentClass.getName())");
            return false;
        }

        FragmentInfo info = mapFragmentInfo.get(fragmentClass.getName());
        if (info == null || info.fragmentClass == null) {
            LogUtils.d("switchToFragment", "info==null || info.fragmentClass==null");
            return false;
        }

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentClass.getName());
        if (fragment == null) {
            try {
                fragment = info.fragmentClass.getConstructor().newInstance();
                LogUtils.e("hpp_pl", "creat fragment:" + fragment.getClass());
                getSupportFragmentManager().beginTransaction()
                        .add(info.idContainer, fragment, fragmentClass.getName())
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                LogUtils.e(e);
                LogUtils.d("switchToFragment", "Exception");
                return false;
            }
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment iter : fragments) {
                if (iter == null || iter == fragment || iter.isHidden())
                    continue;

                if (iter instanceof BaseFragment)
                    ((BaseFragment) fragment)
                            .setPrevFragmentClass((Class<? extends BaseFragment>) iter.getClass());

                if (iter == fragment) {
                    LogUtils.d("switchToFragment", "iter==fragment");
                    return true;
                }

                ft.hide(iter);
            }
            ft.show(fragment);

            if (addToBackStack)
                ft.addToBackStack(null);

            ft.commitAllowingStateLoss();
        } else {
            new Handler().post(new Runnable() {

                @Override
                public void run() {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    List<Fragment> fragments = getSupportFragmentManager().getFragments();
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(
                            fragmentClass.getName());
                    if (fragments != null) {
                        for (Fragment iter : fragments) {
                            if (iter == null || iter == fragment || iter.isHidden())
                                continue;

                            if (iter instanceof BaseFragment)
                                ((BaseFragment) fragment)
                                        .setPrevFragmentClass((Class<? extends BaseFragment>) iter
                                                .getClass());

                            if (iter == fragment) {
                                LogUtils.d("switchToFragment", "iter==fragment");
                                break;
                            }

                            ft.hide(iter);

                        }
                        ft.show(fragment);

                        if (addToBackStack)
                            ft.addToBackStack(null);

                        ft.commitAllowingStateLoss();
                    }
                }
            });
        }

        

        LogUtils.d("switchToFragment", "end!");
        return true;
    }

    // 需要注意的是，getCurrentFragment和isHomeFragment都基于一个假设，即同一时刻，只有一个顶级Fragment处于显示状态，对于目前的App这个假设是合理的，
    // 但是后续如果有横屏或其他的需求导致的改动，则不应仍有以上假设
    public Fragment getCurrentFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment iter : fragments) {
                if (iter == null || iter.isHidden())
                    continue;

                return iter;
            }
        }
        return null;
    }

    // public boolean isHomeFragment() {
    // Fragment currentFragment = getCurrentFragment();
    // return currentFragment != null
    // && currentFragment instanceof TabBaseFragment;
    // }

    public void addFragment(Class<? extends Fragment> fragmentClass) {
        if (fragmentClass == null)
            return;

        if (!mapFragmentInfo.containsKey(fragmentClass.getName()))
            return;

        FragmentInfo info = mapFragmentInfo.get(fragmentClass.getName());
        if (info == null || info.fragmentClass == null)
            return;

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentClass.getName());
        if (fragment == null) {
            try {
                fragment = info.fragmentClass.getConstructor().newInstance();
                getSupportFragmentManager().beginTransaction()
                        .add(info.idContainer, fragment, fragmentClass.getName()).hide(fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                LogUtils.e(e);
            }
        }
    }

    public boolean isFragmentExist(Class<? extends Fragment> fragmentClass) {
        return getSupportFragmentManager().findFragmentByTag(fragmentClass.getName()) != null;
    }

    /**
     * 关闭软键盘
     * 
     * @param event
     */
    public void onEventMainThread(ApplicationEvents.CloseSoftInputMethod event) {
        WidgetUtils.hideSoftKeyboard(this);
    }

}
