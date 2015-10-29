package com.v.heng.livego.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.v.heng.livego.utils.LogUtil;

public class MyWebView extends WebView {

	private Context context;
	private ProgressBar progressBar;
	private TextView textView;
	private String failingUrl;
	
	public static ValueCallback<Uri> mUploadMessage;
	public final static int FILECHOOSER_RESULTCODE = 1;
	
	public MyWebView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		init();
	}

	public MyWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}


	public void init() {
		this.getSettings().setJavaScriptEnabled(true);
		this.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		this.getSettings().setDomStorageEnabled(true);
		this.getSettings().setAllowFileAccess(true);
		
		this.getSettings().setPluginState(PluginState.ON);
		this.setVerticalScrollBarEnabled(false);  //取消Vertical ScrollBar显示
		this.setHorizontalScrollBarEnabled(false); //取消Horizontal ScrollBar显示
		
		// this.addJavascriptInterface(new WebAppInterface(webview, this,
		// mHandler), "yjpay");
		this.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				System.out.println("errorCode: " + errorCode);
				System.out.println("description: " + description);
				System.out.println("failingUrl: " + failingUrl);
				
				MyWebView.this.failingUrl = failingUrl;
				if(textView == null) {
					textView = new TextView(context);
					textView.setText("网络异常，请稍候再试");
					textView.setTextSize(18);
					textView.setBackgroundColor(Color.WHITE);
					textView.setGravity(Gravity.CENTER);
				}
				view.addView(textView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				
//				view.stopLoading();
//				view.loadUrl("file:///android_asset/error.html");
			}
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				LogUtil.logERROR(getClass(), "shouldOverrideUrlLoading: url: " + url);
				if(url.startsWith("tel:")) {
					try {
						Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));  
		                context.startActivity(intent);  
					} catch (Exception e) {
						System.out.println(e);
					}
					return true;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
				LogUtil.logERROR(getClass(), "shouldInterceptRequest: url: " + url);

				return super.shouldInterceptRequest(view, url);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				LogUtil.logERROR(getClass(), "onPageFinished: url: " + url);
				super.onPageFinished(view, url);
			}
		});
		this.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				if (newProgress == 100) {
					if(failingUrl != null && !failingUrl.equals(view.getUrl())) { // onError
						view.removeView(textView);
					}
					if (progressBar != null) {
						progressBar.setVisibility(View.GONE);
					}
				} else {
					if (progressBar == null) {
						progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
						progressBar.setMax(100);
						progressBar.setProgress(0);
						MyWebView.this.addView(progressBar, LayoutParams.FILL_PARENT, 7);
					}
					if (!progressBar.isShown()) {
						progressBar.setVisibility(View.VISIBLE);
					}
					progressBar.setProgress(newProgress);
				}
			}

			// For Android 3.0+
			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("image/*");
				((Activity)context).startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);

			}
			// For Android 3.0+
			public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("*/*");
				((Activity)context).startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
			}
			// For Android 4.1
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
				mUploadMessage = uploadMsg;
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.addCategory(Intent.CATEGORY_OPENABLE);
				i.setType("image/*");
				((Activity)context).startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
			}

			@Override
			public void onShowCustomView(View view, CustomViewCallback callback) {
				// TODO Auto-generated method stub
			}

			// @Override
			// public boolean onJsConfirm(WebView view, String url, String
			// message, JsResult result) {
			// Toast.makeText(context, "onJsConfirm", Toast.LENGTH_LONG).show();
			// return true;
			// }
			//
			// @Override
			// public boolean onJsAlert(WebView view, String url, String
			// message, final JsResult result) {
			// try {
			// new
			// AlertDialog.Builder(context).setTitle("温馨提示").setMessage(message)
			// .setPositiveButton("确定", new AlertDialog.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// result.confirm();
			// dialog.dismiss();
			// }
			// }).show();
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// return true;
			// }
		});
	}

}
