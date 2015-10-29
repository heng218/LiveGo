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

    public static String[] UserAgent = {
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11",
            "Mozilla/5.0 (Linux; U; Android 2.2; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.2",
            "Mozilla/5.0 (iPad; U; CPU OS 3_2_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B500 Safari/531.21.11",
            "Mozilla/5.0 (SymbianOS/9.4; Series60/5.0 NokiaN97-1/20.0.019; Profile/MIDP-2.1 Configuration/CLDC-1.1) AppleWebKit/525 (KHTML, like Gecko) BrowserNG/7.1.18121",
            //http://blog.csdn.net/yjflinchong
            "Nokia5700AP23.01/SymbianOS/9.1 Series60/3.0",
            "UCWEB7.0.2.37/28/998",
            "NOKIA5700/UCWEB7.0.2.37/28/977",
            "Openwave/UCWEB7.0.2.37/28/978",
            "Mozilla/4.0 (compatible; MSIE 6.0; ) Opera/UCWEB7.0.2.37/28/989"
    };

    public static byte[] getByteArrayFromNetwork(String urlStirng, String userAgent) {
        try {
            URL url = new URL(urlStirng);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setRequestProperty("User-Agent", userAgent);
            conn.connect();
            InputStream inStream = conn.getInputStream();
            byte[] b = FileUtils.readInStream_btyes(inStream);
            return b;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

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
