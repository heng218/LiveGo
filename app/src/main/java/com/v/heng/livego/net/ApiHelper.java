package com.v.heng.livego.net;

import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.v.heng.livego.base.Constant;
import com.v.heng.livego.base.MyApplication;
import com.v.heng.livego.bean.LiveInfo;
import com.v.heng.livego.dao.LiveInfoDao;
import com.v.heng.livego.utils.ThreadPoolManager;

import java.util.List;

/**
 * Created by heng on 2015/10/10.
 * 邮箱：252764480@qq.com
 */
public class ApiHelper {

    public static final int DATA_FAIL = 00;
    public static final int DATA_SUCCESS = 01;
    public static final int DATA_EMPTY = 02;


    public static void getLiveAddressByLivePage(final Dialog dialog, final Handler handler,  final LiveInfo liveInfo) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                final Message msg = new Message();
                LiveInfo liveInfo_new = LiveInfoDao.getLiveAddressByLivePage(liveInfo);
                if (liveInfo_new == null) {
                    msg.what = DATA_EMPTY;
                } else {
                    msg.what = DATA_SUCCESS;
                    msg.obj = liveInfo_new;
                }

                Handler1 handler1 = new Handler1(Looper.getMainLooper(), handler);
                handler1.sendMessage(msg);
            }
        });
    }

    public static void getLiveInfos_game(final Handler handler, final int page, final String game) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                final Message msg = new Message();
                List<LiveInfo> liveInfos = LiveInfoDao.getLiveInfosByLaoyuegou_game(game, page);
                if (liveInfos == null || liveInfos.size() == 0) {
                    msg.what = DATA_EMPTY;
                } else {
                    msg.what = DATA_SUCCESS;
                    msg.obj = liveInfos;
                }

                Handler1 handler1 = new Handler1(Looper.getMainLooper(), handler);
                handler1.sendMessage(msg);
            }
        });
    }

    /**
     * 得到直播信息
     * bg 平台
     * @param handler
     * @param page
     * @param platform
     */
    public static void getLiveInfos_platform(final Handler handler, final int page, final String platform) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                final Message msg = new Message();
                List<LiveInfo> liveInfos = null;
                if("龙珠".equals(platform)) {
                    liveInfos = LiveInfoDao.getLongzhuLiveInfos(page);
                } else if("战旗".equals(platform)) {
                    liveInfos = LiveInfoDao.getZhanqiLiveInfos(page);
                } else if("熊猫".equals(platform)) {
                    liveInfos = LiveInfoDao.getPandaLiveInfos(page);
                } else {
                    liveInfos = LiveInfoDao.getLiveInfosByLaoyuegou_platform(platform, page);
                }

                if (liveInfos == null || liveInfos.size() == 0) {
                    msg.what = DATA_EMPTY;
                } else {
                    msg.what = DATA_SUCCESS;
                    msg.obj = liveInfos;
                }

                Handler1 handler1 = new Handler1(Looper.getMainLooper(), handler);
                handler1.sendMessage(msg);
            }
        });
    }

    public static void getLiveInfos_all(final Handler handler, final int page) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                final Message msg = new Message();
                List<LiveInfo> liveInfos = LiveInfoDao.getAllLiveInfosByLaoyuegou(page);
                if (liveInfos == null || liveInfos.size() == 0) {
                    msg.what = DATA_EMPTY;
                } else {
                    msg.what = DATA_SUCCESS;
                    msg.obj = liveInfos;
                }

                Handler1 handler1 = new Handler1(Looper.getMainLooper(), handler);
                handler1.sendMessage(msg);
            }
        });
    }

    private static class Handler1 extends Handler {
        private Handler mainhandler;

        private Handler1(Looper looper , Handler mainHandler) {
            super(looper);
            this.mainhandler = mainHandler;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(! NetUtils.getNetwork(MyApplication.context)) {
                Toast.makeText(MyApplication.context,"请检查您的网络", 1).show();
            }else if(! Constant.isOpenWifi) {
                Toast.makeText(MyApplication.context,"您当前使用手机流量", 1).show();
            }

            //2  收到消息后可再发消息到主线程
            Message message = new Message();
            message.what = msg.what;
            message.obj = msg.obj;
            mainhandler.sendMessage(message);
        }
    }




}
