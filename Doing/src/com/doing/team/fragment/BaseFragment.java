package com.doing.team.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import com.doing.team.eventbus.QEventBus;

public class BaseFragment extends Fragment
{
	public class BaseFragmentEvent
	{
		public Fragment from;
		public String fromName;
	}
	
	private void post(BaseFragmentEvent event)
	{
		String className = this.getClass().getName();
		if(!TextUtils.isEmpty(className))//className!=null && !className.isEmpty())
		{
			event.from = this;
			event.fromName = className;
			QEventBus.getEventBus(className).post(event);
		}
	}
	
	public class OnActivityCreated extends BaseFragmentEvent
	{
		public Bundle savedInstanceState;
		public OnActivityCreated(Bundle savedInstanceState)
		{
			this.savedInstanceState = savedInstanceState;
		}
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		post(new OnActivityCreated(savedInstanceState));
		super.onActivityCreated(savedInstanceState);
	}

	public class OnActivityResult extends BaseFragmentEvent
	{
		public int requestCode;
		public int resultCode; 
		public Intent data;
		public OnActivityResult(int requestCode, int resultCode, Intent data)
		{
			this.requestCode = requestCode;
			this.resultCode = resultCode;
			this.data = data;
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		post(new OnActivityResult(requestCode, resultCode, data));
		super.onActivityResult(requestCode, resultCode, data);
	}

	public class OnAttach extends BaseFragmentEvent
	{
		public Activity activity;
		public OnAttach(Activity activity)
		{
			this.activity = activity;
		}
	}
	@Override
	public void onAttach(Activity activity)
	{
		post(new OnAttach(activity));
		super.onAttach(activity);
	}

	public class OnConfigurationChanged extends BaseFragmentEvent
	{
		public Configuration newConfig;
		public OnConfigurationChanged(Configuration newConfig)
		{
			this.newConfig = newConfig;
		}
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		post(new OnConfigurationChanged(newConfig));
		super.onConfigurationChanged(newConfig);
	}

	public class OnContextItemSelected extends BaseFragmentEvent
	{
		public MenuItem item;
		public OnContextItemSelected(MenuItem item)
		{
			this.item = item;
		}
	}
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		post(new OnContextItemSelected(item));
		return super.onContextItemSelected(item);
	}

	public class OnCreate extends BaseFragmentEvent
	{
		public Bundle savedInstanceState;
		public OnCreate(Bundle savedInstanceState)
		{
			this.savedInstanceState = savedInstanceState;
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		post(new OnCreate(savedInstanceState));
		super.onCreate(savedInstanceState);
	}

	public class OnCreateAnimation extends BaseFragmentEvent
	{
		public int transit;
		public boolean enter;
		public int nextAnim;
		public OnCreateAnimation(int transit, boolean enter, int nextAnim)
		{
			this.transit = transit;
			this.enter = enter;
			this.nextAnim = nextAnim;
		}
	}
	@Override
	public Animation onCreateAnimation(int transit, boolean enter, int nextAnim)
	{
		post(new OnCreateAnimation(transit, enter, nextAnim));
		return super.onCreateAnimation(transit, enter, nextAnim);
	}

	public class OnCreateContextMenu extends BaseFragmentEvent
	{
		public ContextMenu menu;
		public View v;
		public ContextMenuInfo menuInfo;
		public OnCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
		{
			this.menu = menu;
			this.v = v;
			this.menuInfo = menuInfo;
		}
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		post(new OnCreateContextMenu(menu, v, menuInfo));
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	public class OnCreateOptionsMenu extends BaseFragmentEvent
	{
		public Menu menu;
		public MenuInflater inflater;
		public OnCreateOptionsMenu(Menu menu, MenuInflater inflater)
		{
			this.menu = menu;
			this.inflater = inflater;
		}
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		post(new OnCreateOptionsMenu(menu, inflater));
		super.onCreateOptionsMenu(menu, inflater);
	}

	public class OnCreateView extends BaseFragmentEvent
	{
		public LayoutInflater inflater;
		public ViewGroup container;
		public Bundle savedInstanceState;
		public OnCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			this.inflater = inflater;
			this.inflater = inflater;
			this.savedInstanceState = savedInstanceState;
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		post(new OnCreateView(inflater, container, savedInstanceState));
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public class OnDestroy extends BaseFragmentEvent
	{
	}
	@Override
	public void onDestroy()
	{
		post(new OnDestroy());
		super.onDestroy();
	}

	public class OnDestroyOptionsMenu extends BaseFragmentEvent
	{
	}
	@Override
	public void onDestroyOptionsMenu()
	{
		post(new OnDestroyOptionsMenu());
		super.onDestroyOptionsMenu();
	}

	public class OnDestroyView extends BaseFragmentEvent
	{
	}
	@Override
	public void onDestroyView()
	{
		post(new OnDestroyView());
		super.onDestroyView();
	}

	public class OnDetach extends BaseFragmentEvent
	{
	}
	@Override
	public void onDetach()
	{
		post(new OnDetach());
		super.onDetach();
	}

	public class OnHiddenChanged extends BaseFragmentEvent
	{
		public boolean hidden;
		public OnHiddenChanged(boolean hidden)
		{
			this.hidden = hidden;
		}
	}
	@Override
	public void onHiddenChanged(boolean hidden)
	{
		post(new OnHiddenChanged(hidden));
		super.onHiddenChanged(hidden);
	}

	public class OnInflate extends BaseFragmentEvent
	{
		public Activity activity;
		public AttributeSet attrs;
		public Bundle savedInstanceState;
		public OnInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState)
		{
			this.activity = activity;
			this.attrs = attrs;
			this.savedInstanceState = savedInstanceState;
		}
	}
	@Override
	public void onInflate(Activity activity, AttributeSet attrs,
			Bundle savedInstanceState)
	{
		post(new OnInflate(activity, attrs, savedInstanceState));
		super.onInflate(activity, attrs, savedInstanceState);
	}

	public class OnLowMemory extends BaseFragmentEvent
	{
	}
	@Override
	public void onLowMemory()
	{
		post(new OnLowMemory());
		super.onLowMemory();
	}

	public class OnOptionsItemSelected extends BaseFragmentEvent
	{
		public MenuItem item;
		public OnOptionsItemSelected(MenuItem item)
		{
			this.item = item;
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		post(new OnOptionsItemSelected(item));
		return super.onOptionsItemSelected(item);
	}

	public class OnOptionsMenuClosed extends BaseFragmentEvent
	{
		public Menu menu;
		public OnOptionsMenuClosed(Menu menu)
		{
			this.menu = menu;
		}
	}
	@Override
	public void onOptionsMenuClosed(Menu menu)
	{
		post(new OnOptionsMenuClosed(menu));
		super.onOptionsMenuClosed(menu);
	}

	public class OnPause extends BaseFragmentEvent
	{
	}
	@Override
	public void onPause()
	{
		post(new OnPause());
		super.onPause();
	}

	public class OnPrepareOptionsMenu extends BaseFragmentEvent
	{
		public Menu menu;
		public OnPrepareOptionsMenu(Menu menu)
		{
			this.menu = menu;
		}
	}
	@Override
	public void onPrepareOptionsMenu(Menu menu)
	{
		post(new OnPrepareOptionsMenu(menu));
		super.onPrepareOptionsMenu(menu);
	}

	public class OnResume extends BaseFragmentEvent
	{
	}
	@Override
	public void onResume()
	{
		post(new OnResume());
		super.onResume();
	}

	public class OnSaveInstanceState extends BaseFragmentEvent
	{
		public Bundle outState;
		public OnSaveInstanceState(Bundle outState)
		{
			this.outState = outState;
		}
	}
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		post(new OnSaveInstanceState(outState));
		super.onSaveInstanceState(outState);
	}

	public class OnStart extends BaseFragmentEvent
	{
	}
	@Override
	public void onStart()
	{
		post(new OnStart());
		super.onStart();
	}

	public class OnStop extends BaseFragmentEvent
	{
	}
	@Override
	public void onStop()
	{
		post(new OnStop());
		super.onStop();
	}

	public class OnViewCreated extends BaseFragmentEvent
	{
		public View view;
		public Bundle savedInstanceState;
		public OnViewCreated(View view, Bundle savedInstanceState)
		{
			this.view = view;
			this.savedInstanceState = savedInstanceState;
		}
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		post(new OnViewCreated(view, savedInstanceState));
		super.onViewCreated(view, savedInstanceState);
	}

	public class OnViewStateRestored extends BaseFragmentEvent
	{
		public Bundle savedInstanceState;
		public OnViewStateRestored(Bundle savedInstanceState)
		{
			this.savedInstanceState = savedInstanceState;
		}
	}
	@Override
	public void onViewStateRestored(Bundle savedInstanceState)
	{
		post(new OnViewStateRestored(savedInstanceState));
		super.onViewStateRestored(savedInstanceState);
	}
	
	public boolean onBackPressed()
	{
		return false;
	}
	
	private Class<? extends BaseFragment> mPrevFragmentClass = null;
	public void setPrevFragmentClass(Class<? extends BaseFragment> prevFragmentClass)
	{
		mPrevFragmentClass = prevFragmentClass;
	}
	
	public Class<? extends BaseFragment> getPrevFragmentClass()
	{
		return mPrevFragmentClass;
	}
}
