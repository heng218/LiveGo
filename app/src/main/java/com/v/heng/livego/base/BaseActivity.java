package com.v.heng.livego.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.v.heng.livego.R;
import com.v.heng.livego.view.CommonProgressDialog;
import com.v.heng.livego.view.MyToast;

import java.util.Timer;
import java.util.TimerTask;


public class BaseActivity extends FragmentActivity implements View.OnClickListener {
    private static final String TAG = "BaseActivity";
    // private MyApplication myApp;
    private boolean isExit = false;

    LinearLayout baseLayout;
    public View titleBarView;
    LayoutParams params_warp = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    LayoutParams params_fill = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    public TextView leftTv, rightTv;


    protected boolean isClicking = false; // button 点击


    private Dialog progressDialog;
    private MyToast myToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 添加Activity到堆栈
        AppManager.getAppManager().addActivity(this);
//         myApp = (MyApplication) getApplication();

        initTitleView();

    }

    private void initTitleView() {
        titleBarView = LayoutInflater.from(this).inflate(R.layout.title_bar, null);

        leftTv = (TextView) titleBarView.findViewById(R.id.leftTv);
        leftTv.setOnClickListener(this);

        rightTv = (TextView) titleBarView.findViewById(R.id.rightTv);
        rightTv.setOnClickListener(this);
        rightTv.setVisibility(View.GONE);

        baseLayout = new LinearLayout(this);
        baseLayout.setOrientation(LinearLayout.VERTICAL);
        baseLayout.addView(titleBarView, params_warp);
        setContentView(baseLayout);
    }


    /**
     * title menu finish
     */

    public void setBaseContentView(int resId) {
        setBaseContentView(LayoutInflater.from(this).inflate(resId, null));
    }

    public void setBaseContentView(View contentView) {
        baseLayout.addView(contentView, params_fill);
    }

    public TextView getLeftTv() {
        return leftTv;
    }

    public TextView getRightTv() {
        return rightTv;
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        // 结束Activity&从堆栈中移除
        AppManager.getAppManager().finishActivity(this);
        super.onDestroy();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
         overridePendingTransition(R.anim.left_scale_a, R.anim.alpha_a);
    }

    public void startActivity(Intent intent, int enterAnim, int exitAnim) {
        super.startActivity(intent);
        // overridePendingTransition(enterAnim, exitAnim);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
//         overridePendingTransition(R.anim.left_scale_a, R.anim.alpha_a);
    }

    @Override
    public void finish() {
        super.finish();
         overridePendingTransition(R.anim.alpha_b, R.anim.left_scale_b);
    }

    public void finish(int enterAnim, int exitAnim) {
        super.finish();
        // overridePendingTransition(enterAnim, exitAnim);
    }

    /**
     * 点两次退出
     *
     * @return
     */
    public synchronized boolean exit() {
        if (isExit == false) {
            Toast.makeText(this, "再点一次退出", Toast.LENGTH_SHORT).show();
//            new MyToast(this, "再点一次退出", Toast.LENGTH_SHORT).show();
            isExit = true;

            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            };
            timer.schedule(timerTask, 1500);

            return false;

        } else {
            return true;
        }
    }


    /**
     * Toast 提示
     *
     * @param content
     * @param duration
     * @return
     */
    public boolean showToast(String content, int duration) {
        if (myToast == null) {
            myToast = new MyToast(this, content, duration);
            myToast.show();
        } else {
            myToast.setText(content, duration);
            myToast.show();
        }
        return true;
    }

    /**
     * 显示等待对话框
     *
     * @return
     */
    protected Dialog showProgressDialog(Context context, String message, boolean onBackCancel) {
        if (progressDialog == null) {
            progressDialog = new CommonProgressDialog(this, message, onBackCancel);
        }
        progressDialog.show();
        return progressDialog;
    }


    @Override
    public void onClick(View v) {
        if (isClicking || v == null) {
            return;
        } else {
            isClicking = true;
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isClicking = false;
            }
        }, 2000);

        MyAnimation.scaleOnBtnClick(v);

        switch (v.getId()) {
            case R.id.leftTv:
                finish();
                break;
            default:
                break;
        }
    }



    private Handler handler = new Handler();


}
