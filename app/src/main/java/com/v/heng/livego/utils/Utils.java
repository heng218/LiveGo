package com.v.heng.livego.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.v.heng.livego.base.Constant;

public class Utils {

    /**
     * 得到常用Dialog
     * @param context
     * @return
     */
    public static NiftyDialogBuilder getCommonDilog(Context context) {
        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
        dialogBuilder
                .withTitle("Modal Dialog")                                  //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")                                  //def
                .withDividerColor("#11000000")                              //def
                .withMessage("This is a modal Dialog.")                     //.withMessage(null)  no Msg
                .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                .withDialogColor("#FFE74C3C")                               //def  | withDialogColor(int resid)
//                .withIcon(context.getResources().getDrawable(R.drawable.icon))      //def
                .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                .withDuration(700)                                          //def
//                .withEffect(effect)                                         //def Effectstype.Slidetop
                .withButton1Text("OK")                                      //def gone
                .withButton2Text("Cancel")                                  //def gone
//                .setCustomView(R.layout.custom_view, v.getContext())         //.setCustomView(View or ResId,context)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "i'm btn1", Toast.LENGTH_SHORT).show();
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "i'm btn2", Toast.LENGTH_SHORT).show();
                    }
                });
        return dialogBuilder;
    }

    // 得到property文件URl
    public static String getUrl(Context cxt, String key) {
        if (key == null || "".equals(key)) {
            key = "http.api.url";
        }

        InputStream fis = null;
        try {
            fis = cxt.getResources().getAssets().open("pro.properties");
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Properties pro = new Properties();

        try {
            pro.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pro.getProperty(key);
    }

    // 得到Property对象
    private static Properties getProperties(Context cxt) {
        InputStream fis = null;
        try {
            fis = cxt.getAssets().open("pro.properties");
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Properties pro = new Properties();

        try {
            pro.load(fis);
            return pro;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // MD5加密
    public static String md5(String string) {
        if (string == null)
            return null;

        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    /**
     * 两点经纬度，计算他们之间的距离
     */
    public final static double EARTH_RADIUS = 6378137.0;

    public static double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
        double radLat1 = (lat_a * Math.PI / 180.0);
        double radLat2 = (lat_b * Math.PI / 180.0);
        double a = radLat1 - radLat2;
        double b = (lng_a - lng_b) * Math.PI / 180.0;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    /**
     * 得到指定半径内的最大最小 经纬度
     *
     * @param raidus 单位米 return minLat,minLng,maxLat,maxLng
     */
    public static double[] getAround(double lat, double lon, int raidus) {
        Double latitude = lat;
        Double longitude = lon;
        Double degree = (24901 * 1609) / 360.0;
        double raidusMile = raidus;

        Double dpmLat = 1 / degree;

        Double radiusLat = dpmLat * raidusMile;

        Double minLat = latitude - radiusLat;

        Double maxLat = latitude + radiusLat;

        Double mpdLng = degree * Math.cos(latitude * (Math.PI / 180));

        Double dpmLng = 1 / mpdLng;

        Double radiusLng = dpmLng * raidusMile;

        Double minLng = longitude - radiusLng;

        Double maxLng = longitude + radiusLng;

        return new double[]{minLat, minLng, maxLat, maxLng};
    }

    public static String getAppId(Context context) {
        return getMetaData(context, "appID");
    }

    public static String getMetaData(Context cxt, String key) {
        ApplicationInfo appInfo = null;
        try {
            appInfo = cxt.getPackageManager().getApplicationInfo(cxt.getPackageName(), PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        Bundle b = appInfo.metaData;
        String str = b.getString(key);
        if (str == null) {
            return b.getInt(key) + "";
        } else {
            return str;
        }
    }

    /**
     * @param context
     * @return
     */
    public static boolean getNetwork(Context context) {
        // Context context = context.getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetInfo == null) {

            return false;
        } else if (activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {

            return true;
        } else if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            Constant.isOpenWifi = true;
            return true;
        } else {
            return false;
        }
    }

    /**
     * SDK 版本
     *
     * @return
     */
    public static int getAndroidSDKVersion() {
        int version = 0;
        try {
            version = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            Log.e("utils", e.toString());
        }
        return version;
    }

    /**
     * app版本号
     *
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context) {
        int versionCode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionCode;
    }

    /**
     * app版本名字
     *
     * @param context
     * @return
     */
    public static String getAppVersionName(Context context) {
        String versionName = "1.0.1";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;

        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    public static String getIMEI(Context cxt) {
        TelephonyManager telephonyManager = (TelephonyManager) cxt.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        return imei;
    }

    /**
     * apk路径（可获得安装时apk的名字）
     *
     * @param context
     * @return
     */
    public static String getApkPath(Context context) {
        PackageManager pm = context.getPackageManager();
        ApplicationInfo ai = null;
        try {
            ai = pm.getApplicationInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        String apkPath = ai.sourceDir;
        return apkPath;
    }

    /**
     * 屏幕像素·密度
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getScreenMetrics(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);

        return metric;
    }

    public static long getSDAllSize() {
        // 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        // 获取所有数据块数
        long allBlocks = sf.getBlockCount();
        // 返回SD卡大小
        // return allBlocks * blockSize; //单位Byte
        // return (allBlocks * blockSize)/1024; //单位KB
        return (allBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    public static long getsdfree() {
        long sdcardfree = 0;
        String sDcString = Environment.getExternalStorageState();
        if (sDcString.equals(Environment.MEDIA_MOUNTED)) {
            File pathFile = Environment.getExternalStorageDirectory();
            try {
                StatFs statfs = new StatFs(pathFile.getPath());
                // 获取SDCard上BLOCK总数
                long nTotalBlocks = statfs.getBlockCount();
                // 获取SDCard上每个block的SIZE
                long nBlocSize = statfs.getBlockSize();
                // 获取可供程序使用的Block的数量
                long nAvailaBlock = statfs.getAvailableBlocks();
                // 获取剩下的所有Block的数量(包括预留的一般程序无法使用的块)
                long nFreeBlock = statfs.getFreeBlocks();
                // 计算SDCard 总容量大小MB
                long sdcardtotal = nTotalBlocks * nBlocSize / 1024 / 1024;
                // 计算 SDCard 剩余大小MB
                sdcardfree = nAvailaBlock * nBlocSize / 1024 / 1024;
            } catch (IllegalArgumentException e) {
                Log.e("LV", e.toString());
            }
        }
        return sdcardfree;
    }

    public static void toastMsg(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER, 0, 0);
        toast.show();
    }

//    /**
//     * 检查是否有更新
//     *
//     * @param context
//     */
//    public static void update(final Context context, final boolean isShow) {
//        UmengUpdateAgent.setUpdateAutoPopup(false);
//        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
//            @Override
//            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
//                switch (updateStatus) {
//                    case UpdateStatus.Yes: // has update
//                        Constant.isUpdate = true;
//                        UmengUpdateAgent.showUpdateDialog(context, updateInfo);
//                        break;
//                    case UpdateStatus.No: // has no update
//                        Constant.isUpdate = false;
//                        if (isShow) {
//                            Toast.makeText(context, "您已经是最新版", Toast.LENGTH_SHORT).show();
//                        }
//                        break;
//                    case UpdateStatus.NoneWifi: // none wifi
//                        if (isShow) {
//                            Toast.makeText(context, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
//                        }
//                        break;
//                    case UpdateStatus.Timeout: // time out
//                        if (isShow) {
//                            Toast.makeText(context, "连接超时", Toast.LENGTH_SHORT).show();
//                        }
//                        break;
//                }
//            }
//        });
//        UmengUpdateAgent.update(context);
//    }
}
