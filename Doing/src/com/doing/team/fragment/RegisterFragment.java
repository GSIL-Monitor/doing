package com.doing.team.fragment;

import com.doing.team.R;
import com.doing.team.eventbus.QEventBus;
import com.doing.team.eventdefs.ApplicationEvents;
import com.qihoo.haosou.msearchpublic.util.LogUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterFragment extends BaseFragment {
    private View mView;
    private long mExitTime = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        QEventBus.getEventBus().register(this);
        mView = inflater.inflate(R.layout.reigster_container_fragment, container, false);
        mView.setOnClickListener(null);
        registerFragments();
        switchToFragment(RegisterGenderFragment.class, false);
        return mView;
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        QEventBus.getEventBus().unregister(this);
    }
    /**
     * 切换fragment
     * @param event
     */
    public void onEventMainThread(ApplicationEvents.SwitchFragmentToRegister event) {
        switchToFragment(event.fragmentClass, event.addToBackStack);
    }
    @Override
    public boolean onBackPressed() {
        Fragment currentFragment = getCurrentFragment();
        if (currentFragment != null) {
            if (currentFragment instanceof RegisterInfoFragment) {

                switchToFragment(RegisterGenderFragment.class, false);
                return true;
            } else if (currentFragment instanceof RegisterGenderFragment) {

                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Toast.makeText(getActivity(), R.string.enter_again_and_exit, Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();
                } else {
                    QEventBus.getEventBus().post(new ApplicationEvents.ExitApplication());
                }
                return true;

            }
        }

        return false;
    }


    private void registerFragments() {
        registerFragment(RegisterGenderFragment.class, R.id.fragment_register_container);
        registerFragment(RegisterInfoFragment.class, R.id.fragment_register_container);
    }

    private class FragmentInfo {
        public Class<? extends Fragment> fragmentClass;
        public int idContainer;

        public FragmentInfo(Class<? extends Fragment> fragmentClass,
                            int idContainer) {
            this.fragmentClass = fragmentClass;
            this.idContainer = idContainer;
        }
    }

    private Map<String, FragmentInfo> mapFragmentInfo = new HashMap<String, FragmentInfo>();

    private boolean registerFragment(Class<? extends Fragment> fragmentClass,
                                     int idContainer) {
        if (fragmentClass == null)
            return false;

        if (mapFragmentInfo.containsKey(fragmentClass.getName()))
            return false;
        mapFragmentInfo.put(fragmentClass.getName(), new FragmentInfo(
                fragmentClass, idContainer));
        return true;
    }

    @SuppressWarnings("unchecked")
    public boolean switchToFragment(Class<? extends Fragment> fragmentClass,
                                    boolean addToBackStack) {
        if (fragmentClass == null)
            return false;

        if (!mapFragmentInfo.containsKey(fragmentClass.getName()))
            return false;

        FragmentInfo info = mapFragmentInfo.get(fragmentClass.getName());
        if (info == null || info.fragmentClass == null)
            return false;

        Fragment fragment = getChildFragmentManager().findFragmentByTag(
                fragmentClass.getName());
        if (fragment == null) {
            try {
                fragment = info.fragmentClass.getConstructor().newInstance();
                getChildFragmentManager()
                        .beginTransaction()
                        .add(info.idContainer, fragment,
                                fragmentClass.getName())
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                LogUtils.e(e);
                return false;
            }
        }

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment iter : fragments) {
                if (iter == null || iter == fragment || iter.isHidden())
                    continue;

                if (iter instanceof BaseFragment)
                    ((BaseFragment) fragment)
                            .setPrevFragmentClass((Class<? extends BaseFragment>) iter
                                    .getClass());

                if (iter == fragment)
                    return true;

                ft.hide(iter);
            }
        }

        ft.show(fragment);

        if (addToBackStack)
            ft.addToBackStack(null);

        ft.commitAllowingStateLoss();
        return true;
    }

    public Fragment getCurrentFragment() {
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment iter : fragments) {
                if (iter == null || iter.isHidden())
                    continue;

                return iter;
            }
        }
        return null;
    }
}
