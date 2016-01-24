package com.doing.team.eventdefs;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * 主程序中eventbus事件
 * @author wangzhiheng
 */
public class ApplicationEvents {

	/**
	 * 添加IdleHandler任务
	 */
	public static class QueueIdleTask {
		public Runnable runnable;

		public QueueIdleTask(Runnable runnable) {
			this.runnable = runnable;
		}
	}

	/**
	 * 切换到浏览器界面
	 */
	public static class ShowBrowserFragment {
	}

	/**
	 * 回到首页
	 */
	public static class GoHome {
	}

	/**
	 * 回退到首页或退出
	 * （如果从HomePage进入，则回退到HomePage，如果通过Intent直接进入到某个页，则直接退出）
	 */
	public static class GoHomeOrExit {
	}

	public static enum BackToWhat {
		HOME, EXIT
	}


	/**
	 * 后退（效果同按Back键）
	 */
	public static class GoBack {
	}
	
    public static class LoginSuccess {
        public boolean hasLogin = false;
        public String username;
        public String avatorurl;
        
        public LoginSuccess(boolean value,String name,String url) {
            this.hasLogin = value;
            this.username = name;
            this.avatorurl = url;
        }
    }
    
    /**
     * 获取access_token
     */
    public static class OaurthAccessToken{
    }

	/**
	 * 关闭软键盘
	 */
	public static class CloseSoftInputMethod {

	}

	/**
	 * 切换主Fragment
	 */
	public static class SwitchToFragment {
		public Class<? extends Fragment> fragmentClass;
		public boolean addToBackStack;

		public SwitchToFragment(Class<? extends Fragment> fragmentClass,
				boolean addToBackStack) {
			this.fragmentClass = fragmentClass;
			this.addToBackStack = addToBackStack;
		}
	}

	/**
	 * 切换注册页Fragment
	 */
	public static class SwitchFragmentToRegisterGender {
		public Class<? extends Fragment> fragmentClass;
		public boolean addToBackStack;

		public SwitchFragmentToRegisterGender(Class<? extends Fragment> fragmentClass,
											  boolean addToBackStack) {
			this.fragmentClass = fragmentClass;
			this.addToBackStack = addToBackStack;
		}
	}


	/**
	 * 退出程序
	 */
	public static class ExitApplication {
	}


}