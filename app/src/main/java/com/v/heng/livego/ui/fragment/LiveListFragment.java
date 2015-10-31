package com.v.heng.livego.ui.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.v.heng.library.xlistview.XListView;
import com.v.heng.livego.R;
import com.v.heng.livego.adapter.LiveListAdapter;
import com.v.heng.livego.bean.LiveInfo;
import com.v.heng.livego.net.ApiHelper;
import com.v.heng.livego.utils.Utils;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;

import java.util.ArrayList;
import java.util.List;

public class LiveListFragment extends Fragment {

	private View rootView;

//	private GridView gridView;
//	private LiveListAdapter adapter;

	private XListView xListView;
	private LiveListAdapter xListViewAdapter;
	private List<LiveInfo> list = new ArrayList<LiveInfo>() ;
	private int xListView_page = 1;
	private int xListView_totalDataCount = 0;

	public enum RequestSort {
		all, platform, game
	}
	public RequestSort requestSort = RequestSort.all;
	public String requesText = "";


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 初始化 平台 或 游戏
		setData();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		System.out.println("LiveListFragment onCreateView:");

		rootView = inflater.inflate(R.layout.fragment_livelist, null);

		initViews();

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		initAds();
	}

	public void initViews() {
//		gridView = (GridView) rootView.findViewById(R.id.gridView);
//		adapter = new LiveListAdapter(getContext());
//		gridView.setAdapter(adapter);

		xListView = (XListView)rootView.findViewById(R.id.xListView);
		xListViewAdapter = new LiveListAdapter(getContext(), null);
		xListView.setAdapter(xListViewAdapter);
		xListView.setPullLoadEnable(true);
		xListView.setPullRefreshEnable(true);
		xListView.setXListViewListener(new XListView.IXListViewListener() {
			@Override
			public void onRefresh() {
				getData(true);
			}

			@Override
			public void onLoadMore() {
				getData(false);
			}
		});
		xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			}
		});

		xListView.startPullRefresh();
	}


	public void initAds() {
		if("youmi".equals(Utils.getMetaData(getContext(), "AD_CHANNEL"))) {
			// 实例化广告条
			AdView adView = new AdView(getContext(), AdSize.FIT_SCREEN);
			// 获取要嵌入广告条的布局
			LinearLayout adLayout=(LinearLayout)rootView.findViewById(R.id.adLayout);
			// 将广告条加入到布局中
			adLayout.addView(adView);
		}
	}

	public void setData() {
		Bundle bundle = getArguments();
		if(bundle != null) {
			String platform = bundle.getString("platform");
			String game = bundle.getString("game");

			if(!TextUtils.isEmpty(platform)) {
				this.requestSort = RequestSort.platform;
				this.requesText = platform;
			} else if(!TextUtils.isEmpty(game)) {
				this.requestSort = RequestSort.game;
				this.requesText = game;
			}
		}
	}

	public void getData(boolean isRefresh) {
		if (isRefresh) {
			xListView_page = 1;
			xListView_totalDataCount = 0;
			list.clear();
			xListViewAdapter.setLiveInfos(null);
			xListViewAdapter.notifyDataSetChanged();
		} else if (xListViewAdapter.getCount() >= xListView_totalDataCount) {
			xListView.setTotalDataCount(xListViewAdapter.getCount());
			xListView.notifyFootViewTextChange();
			return;
		}

		// 请求
		switch (requestSort) {
			case all:
				ApiHelper.getLiveInfos_all(handler, xListView_page);
				break;
			case platform:
				ApiHelper.getLiveInfos_platform(handler,xListView_page, requesText);
				break;
			case game:
				ApiHelper.getLiveInfos_game(handler,xListView_page, requesText);
				break;
			default:
				break;
		}
	}




	public android.os.Handler handler = new android.os.Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case ApiHelper.DATA_SUCCESS:
					ArrayList<LiveInfo> newLiveInfos = (ArrayList<LiveInfo>) msg.obj;

					xListView_page++;
//					xListView_totalDataCount = Integer.MAX_VALUE;
					xListView_totalDataCount = 50;
					list.addAll(newLiveInfos);

					xListViewAdapter.setLiveInfos(list);
					xListViewAdapter.notifyDataSetChanged();
					xListView.setTotalDataCount(xListView_totalDataCount);
					xListView.notifyFootViewTextChange();

					break;
				case ApiHelper.DATA_EMPTY:
					xListView_totalDataCount = list.size();

					xListViewAdapter.setLiveInfos(list);
					xListViewAdapter.notifyDataSetChanged();
					xListView.setTotalDataCount(xListView_totalDataCount);
					xListView.notifyFootViewTextChange();
					break;
				default:
					break;
			}
		}
	};



}
