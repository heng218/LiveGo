package com.v.heng.livego.base;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.umeng.analytics.MobclickAgent;
import com.v.heng.livego.R;


public class MyApplication extends Application {

	private final static String TAG = MyApplication.class.getName();
	public static Context context;
	public static boolean isDebug = true;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		isDebug = "debug".equals(getResources().getString(R.string.dev_mode)) ? true : false;


//		System.out.println("MyApplication: " + "onCreate");

		//
//		startScreenBroadcastReceiver();
//		startHomeBroadcastReceiver();

		// umeng调试模式
		MobclickAgent.setDebugMode(true);
		// umeng捕获程序崩溃日志
		MobclickAgent.setCatchUncaughtExceptions(false);
		// 注册crashHandler
		CrashHandler.getInstance().init(getApplicationContext());
	}



	@Override
	public void onTerminate() {
		super.onTerminate();
	}





}
