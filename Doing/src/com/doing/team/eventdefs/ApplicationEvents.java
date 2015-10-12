package com.doing.team.eventdefs;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * 主程序中eventbus事件
 * @author wangzefeng
 */
public class ApplicationEvents {
	/**
	 * v5下拉数据成功，包括上门信息和分类信息
	 */
	public static class PullDataDone{
		public static class SortData{
			
		}
		public static class DoorData{
			
		}
		
	}
	/**
	 * 显示定位失败信息
	 *
	 */
	public static class OnShowErrorLocation {
        public String error;

		public OnShowErrorLocation(String error) {
			super();
			this.error = error;
		}
       
	}
	/**
	 *  更新周边的住宅信息
	 *
	 */
	
	public static class UpdateAround {
	}
	/**
	 * 
	 * @author wangyan-pd
	 * 显示小区的sugss
	 */
	public static class ShowResidenceSugess {
		
	}

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
	 * 
	 * 切换到搜索商家页面
	 */
	public static class ShowLocationFloatFragment {
		
		   	public String query;
            public Context mContext;
		   	public ShowLocationFloatFragment(){
		   		this.query = "";
		   	}
			public ShowLocationFloatFragment(String query) {
				this.query = query;
			}
			public ShowLocationFloatFragment(Context context,String query) {
			    this.mContext = context;
			    this.query = query;
            }
		
	}
	
	public static class ShowOrderFragment {
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

/*	//暂时用不到这个策略
	public static enum PausedByWhat {
		AROUND, OTHER
	}*/

	/**
	 * 后退（效果同按Back键）
	 */
	public static class GoBack {
	}
	
	/**
     * 后退（效果同按Back键）
     */
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
	public static class SwitchFragmentToRegister {
		public Class<? extends Fragment> fragmentClass;
		public boolean addToBackStack;

		public SwitchFragmentToRegister(Class<? extends Fragment> fragmentClass,
								boolean addToBackStack) {
			this.fragmentClass = fragmentClass;
			this.addToBackStack = addToBackStack;
		}
	}

	/**
	 * 切换到主fragment后，显示第一个tab“首页”
	 */
	public static class ShowTabHomePage {
	}

	/**
	 * 退出程序
	 */
	public static class ExitApplication {
	}

	public static class OnSearchBtnClick{
		 
	}
	public static class GetOrderTimeStamp{
        
    }
	public static class ShowRedDotForNewOrder{
        public Boolean show;
        public long time;
        public ShowRedDotForNewOrder(Boolean show,long time){
            this.show = show;
            this.time = time;
        }
    }
	/**
	 * 
	 *  获取商家地点的关联词
	 *
	 */
	
	public static class GetRelativeQuery{
		public String query;
	public GetRelativeQuery(String query) {
			this.query = query;
		}
		
		
	}
	public static class SosLoad{
        public boolean store;
    public SosLoad(boolean store) {
            this.store = store;
        }
        
        
    }
	public static class UpdateApk {
		public final static int EVENT_NO_UPDATE = 0;
		public final static int EVENT_TIMEOUT = 1;
		public final static int EVENT_ERROR = 2;
		public final static int EVENT_UPDATE_NOTICE = 3;
		public final static int EVENT_INSTALL_NOTICE = 4;
		public int event;
		public Intent intent;
		public UpdateApk(int event, Intent intent) {
			this.event = event;
			this.intent = intent;
		}
	}
		
	public static class BannerDataReset{
	    public String from;
        public BannerDataReset(String from) {
            this.from = from;
        }
	}
	
	public static class ShowLoadingView {
    	public boolean show;
    	public ShowLoadingView(boolean show) {
    		this.show = show;
    	}
    }
	
	public static class ShowLoadFailView {
    	public boolean show;
    	public boolean isFail;
		public ShowLoadFailView(boolean show, boolean isFail) {
			super();
			this.show = show;
			this.isFail = isFail;
		}
    	
    }
 
	/**
     * 这部分是去选择城市了，带着城市信息cityname 和 citycode 传递给aroundactivity
     *  继承baseItem是因为在HistoryandSugAdapter里 onitemclick事件啊必须有处理
     *  好恶心啊，这里。主要是为了点击历史记录能够正常跳转查询，里面包含经纬度，城市名字和citycode
     *
     */
    public static class GetSendAddress {
    	//public BaseItem baseItem;
    }
    
    public static class MovieTab {
        public int num;
        public MovieTab(int num) {
            this.num = num;
        }
    }
    
    public static class TabPageIndicator {
        public int num;
        public TabPageIndicator(int num) {
            this.num = num;
        }
    }
    
    public static class ShareResponse {
        public static final int ERR_OK = 0;
        public static final int ERR_COMM = -1;
        public static final int ERR_USER_CANCEL = -2;
        public static final int ERR_SENT_FAILED = -3;
        public static final int ERR_AUTH_DENIED = -4;
        public static final int ERR_UNSUPPORT = -5;
        public String type;
        public int code;

        public ShareResponse(String type, int code) {
            this.type = type;
            this.code = code;
        }
    }

}