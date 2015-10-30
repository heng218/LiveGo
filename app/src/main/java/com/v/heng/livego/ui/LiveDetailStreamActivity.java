package com.v.heng.livego.ui;

import android.app.Dialog;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.v.heng.livego.R;
import com.v.heng.livego.base.BaseActivity;
import com.v.heng.livego.bean.LiveInfo;
import com.v.heng.livego.net.ApiHelper;
import com.v.heng.livego.utils.LogUtil;
import com.v.heng.livego.utils.Utils;

import net.youmi.android.spot.SpotManager;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

public class LiveDetailStreamActivity extends BaseActivity implements MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener, SurfaceHolder.Callback {


    private LiveInfo liveInfo;
    private Dialog progressDialog;

    private int mVideoWidth;
    private int mVideoHeight;
    private MediaPlayer mMediaPlayer;
    private SurfaceView mPreview;
    private SurfaceHolder holder;
    private String path;
    private Bundle extras;
    private static final String MEDIA = "media";
    private static final int LOCAL_AUDIO = 1;
    private static final int STREAM_AUDIO = 2;
    private static final int RESOURCES_AUDIO = 3;
    private static final int LOCAL_VIDEO = 4;
    private static final int STREAM_VIDEO = 5;
    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentView(R.layout.activity_live_detail_stream);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  //设置屏幕常亮

        liveInfo = (LiveInfo) getIntent().getSerializableExtra("LiveInfo");

        initTitle();

        initViews();

//        initData();

    }

    @Override
    protected void onResume() {
        super.onResume();

        initAds();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaPlayer();
        doCleanUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
        doCleanUp();
    }

    public void initTitle() {
        titleBarView.setVisibility(View.GONE);
    }

    public void initViews() {
        Vitamio.initialize(LiveDetailStreamActivity.this);
        mPreview = (SurfaceView) findViewById(R.id.surface);
        holder = mPreview.getHolder();
        holder.addCallback(this);
        holder.setFormat(PixelFormat.RGBA_8888);
        extras = getIntent().getExtras();

        progressDialog = showProgressDialog(this,"",false);
    }


    public void initData() {
//        if ("龙珠".equals(liveInfo.getLivePlatform())
//                || "战旗".equals(liveInfo.getLivePlatform())
//                || "熊猫".equals(liveInfo.getLivePlatform())) {
//        } else {
//            Dialog dialog = showProgressDialog(this, "", false);
//            ApiHelper.getLiveAddressByLivePage(dialog, handler, liveInfo);
//        }
    }

    public void initAds() {
        if (Utils.getMetaData(this, "AD_CHANNEL").equals("youmi")) {
            // youmi
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SpotManager.getInstance(LiveDetailStreamActivity.this).setSpotOrientation(SpotManager.ORIENTATION_LANDSCAPE); // 屏幕方向
                    SpotManager.getInstance(LiveDetailStreamActivity.this).showSpotAds(LiveDetailStreamActivity.this);
                }
            }, 1000);
        }
    }

    private void playVideo(Integer Media) {
        doCleanUp();
        try {
            switch (Media) {
                case LOCAL_VIDEO:
                /*
                 * TODO: Set the path variable to a local media file path.
				 */
                    path = "/mnt/sdcard/video/OCOC.mp4";
                    if (path == "") {
                        // Tell the user to provide a media file URL.
                        Toast.makeText(this, "Please edit MediaPlayerDemo_Video Activity, " + "and set the path variable to your media file path." + " Your media file must be stored on sdcard.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    break;
                case STREAM_VIDEO:
                /*
				 * TODO: Set path variable to progressive streamable mp4 or
				 * 3gpp format URL. Http protocol should be used.
				 * Mediaplayer can only play "progressive streamable
				 * contents" which basically means: 1. the movie atom has to
				 * precede all the media data atoms. 2. The clip has to be
				 * reasonably interleaved.
				 *
				 */
                    path = liveInfo.getLiveStream();
                    if (path == "") {
                        Toast.makeText(this, "Please edit MediaPlayerDemo_Video Activity," + " and set the path variable to your media file URL.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    break;
            }

            // Create a new media player and set the listeners
            mMediaPlayer = new MediaPlayer(this);
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.setDisplay(holder);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnBufferingUpdateListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnVideoSizeChangedListener(this);
            setVolumeControlStream(AudioManager.STREAM_MUSIC);

        } catch (Exception e) {
            LogUtil.logERROR(getClass(), e);
        }
    }

    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    private void doCleanUp() {
        mVideoWidth = 0;
        mVideoHeight = 0;
        mIsVideoReadyToBePlayed = false;
        mIsVideoSizeKnown = false;
    }

    private void startVideoPlayback() {
        holder.setFixedSize(mVideoWidth, mVideoHeight);
        mMediaPlayer.start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ApiHelper.DATA_EMPTY:
                    break;
                case ApiHelper.DATA_SUCCESS:
                    //
                    liveInfo = (LiveInfo) msg.obj;
                    break;
            }
        }
    };


    @Override
    public void onBackPressed() {
        if (Utils.getMetaData(this, "AD_CHANNEL").equals("youmi")) {
            // youmi
            // 如果有需要，可以点击后退关闭插播广告。
            if (!SpotManager.getInstance(this).disMiss()) {
                // 弹出退出窗口，可以使用自定义退屏弹出和回退动画,参照demo,若不使用动画，传入-1
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        playVideo(STREAM_VIDEO);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

    /**
     * Called to update status in buffering a media stream. Buffering is storing
     * data in memory while caching on external storage.
     *
     * @param mp      the MediaPlayer the update pertains to
     * @param percent the percentage (0-100) of the buffer that has been filled thus
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
//        LogUtil.logDEBUG(getClass(), "onBufferingUpdate: " + percent);
        if(progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * Called when the end of a media source is reached during playback.
     *
     * @param mp the MediaPlayer that reached the end of the file
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        LogUtil.logDEBUG(getClass(), "onCompletion: "  );
    }

    /**
     * Called when the media file is ready for playback.
     *
     * @param mp the MediaPlayer that is ready for playback
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        LogUtil.logDEBUG(getClass(), "onPrepared: "  );
        mIsVideoReadyToBePlayed = true;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            mMediaPlayer.setBufferSize(512 * 1024);
            startVideoPlayback();
        }
    }

    /**
     * Called to indicate the video size
     *
     * @param mp     the MediaPlayer associated with this callback
     * @param width  the width of the video
     * @param height the height of the video
     */
    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        LogUtil.logDEBUG(getClass(), "onVideoSizeChanged: "  );
        if (width == 0 || height == 0) {
            return;
        }
        mIsVideoSizeKnown = true;
        mVideoWidth = width;
        mVideoHeight = height;
        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
            startVideoPlayback();
        }
    }
}
