package com.v.heng.livego.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.v.heng.livego.base.Constant;
import com.v.heng.livego.utils.FileUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by heng on 2015/10/24.
 * 邮箱：252764480@qq.com
 */
public class NetUtils {


    public static byte[] getByteArrayFromNetwork(String urlStirng) {
        try {

            URL url = new URL(urlStirng);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream inStream = conn.getInputStream();
            byte[] b = FileUtils.readInStream_btyes(inStream);
            return b;
        } catch (Exception e) {
            System.out.println(e);
            return null;
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
}
