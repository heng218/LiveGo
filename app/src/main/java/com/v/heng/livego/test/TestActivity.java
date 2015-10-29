package com.v.heng.livego.test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.v.heng.livego.R;
import com.v.heng.livego.dao.LiveInfoDao;
import com.v.heng.livego.utils.LogUtil;
import com.v.heng.livego.webview.MyWebView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * Created by heng on 2015/10/21.
 * 邮箱：252764480@qq.com
 */
public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_detail);

        String htmlData = "";
        MyWebView webView = (MyWebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl("http://star.longzhu.com/lol2015?from=challcontent");

    }

    final class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d("WebView", "onPageStarted");
            super.onPageStarted(view, url, favicon);
        }
        public void onPageFinished(WebView view, String url) {
            Log.d("WebView","onPageFinished ");
            view.loadUrl("javascript:window.local_obj.showSource('<head>'+" +
                    "document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            super.onPageFinished(view, url);
        }
    }

    final class InJavaScriptLocalObj {
        public void showSource(String html) {
//            Log.d("HTML", html);
            Document doc = Jsoup.parse(html);
            Elements elements1 = doc.getElementsByTag("object");
            LogUtil.logERROR(LiveInfoDao.class, "elements1:  " + elements1);
        }
    }
}
