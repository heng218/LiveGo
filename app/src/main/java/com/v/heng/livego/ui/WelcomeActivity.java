package com.v.heng.livego.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.v.heng.livego.R;
import com.v.heng.livego.base.AppManager;
import com.v.heng.livego.base.BaseActivity;
import com.v.heng.livego.base.MyApplication;
import com.v.heng.livego.utils.Utils;

import net.youmi.android.AdManager;
import net.youmi.android.spot.SpotManager;

/**
 * Created by heng on 2015/10/16.
 * 邮箱：252764480@qq.com
 */
public class WelcomeActivity extends BaseActivity {

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initAds();

        initView();

        if ("youmi".equals(Utils.getMetaData(this, "AD_CHANNEL"))) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(AppManager.getAppManager().getActivity(MainActivity.class) == null) {
                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }, 6000);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(AppManager.getAppManager().getActivity(MainActivity.class) == null) {
                        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }, 3000);
        }

    }

    public void initView() {

    }

    public void initAds() {
        if ("youmi".equals(Utils.getMetaData(this, "AD_CHANNEL"))) {
            // youmi
            AdManager.getInstance(this).init("bd2d565a8291e1af", "81c02f869e4be53f", MyApplication.isDebug); //init
            SpotManager.getInstance(this).loadSpotAds(); // 加载插屏广告
//            SpotManager.getInstance(this).loadSplashSpotAds(); // 加载开屏广告
//          SpotManager.getInstance(this).setSpotOrientation(SpotManager.ORIENTATION_LANDSCAPE); // 屏幕方向
            SpotManager.getInstance(this).setAnimationType(SpotManager.ANIM_ADVANCE);

            // 开屏
            SpotManager.getInstance(this).showSplashSpotAds(this, MainActivity.class);
//            SpotManager.getInstance(this).showSplashSpotAds(this, splashView,
//                    new SpotDialogListener() {
//
//                        @Override
//                        public void onShowSuccess() {
//                            Log.i("YoumiAdDemo", "开屏展示成功");
//                        }
//
//                        @Override
//                        public void onShowFailed() {
//                            Log.i("YoumiAdDemo", "开屏展示失败。");
//                        }
//
//                        @Override
//                        public void onSpotClosed() {
//                            Log.i("YoumiAdDemo", "开屏关闭。");
//                        }
//
//                        @Override
//                        public void onSpotClick(boolean isWebPath) {
//                            Log.i("YoumiAdDemo", "插屏点击");
//                        }
//                    });
        }

    }

}
