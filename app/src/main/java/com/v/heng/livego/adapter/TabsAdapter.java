package com.v.heng.livego.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;

public class TabsAdapter extends FragmentPagerAdapter implements  ViewPager.OnPageChangeListener {
	private final Context mContext;
	private final ViewPager mViewPager;
	public final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

	static final class TabInfo {
		private final Class<?> clss;
		private final Bundle args;

		TabInfo(Class<?> _class, Bundle _args) {
			clss = _class;
			args = _args;
		}
	}

	public TabsAdapter(FragmentActivity activity, ViewPager pager) {
		super(activity.getSupportFragmentManager());
		mContext = activity;
		mViewPager = pager;
		mViewPager.setAdapter(this);
		mViewPager.setOnPageChangeListener(this);
	}

	public void addTab(android.app.ActionBar.Tab tab, Class<?> clss, Bundle args) {
		TabInfo info = new TabInfo(clss, args);
//		tab.setTag(info);
//		tab.setTabListener((TabListener) this);
		mTabs.add(info);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if(mTabs == null || mTabs.size() == 0) {
			return 0;
		}
		return mTabs.size();
	}

	@Override
	public Fragment getItem(int position) {
		TabInfo info = mTabs.get(position);
		return Fragment.instantiate(mContext, info.clss.getName(), info.args);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//		System.out.println("position: positionOffset: positionOffsetPixels: " + position + " " + positionOffset + " " + positionOffsetPixels);
		
		
		
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
//		 mActionBar.setSelectedNavigationItem(position);
		
		
//		int count = ((MainActivity)mContext).getSupportFragmentManager().getFragments() == null ? 0 : ((MainActivity)mContext).getSupportFragmentManager().getFragments().size();
//		System.out.println("fragment: count: " + count );
	}

	

	
}
