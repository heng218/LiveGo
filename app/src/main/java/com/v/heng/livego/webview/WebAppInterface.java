package com.v.heng.livego.webview;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

public class WebAppInterface {

	private WebView webView;
	private Context context;
	private Handler handler;
	
	public WebAppInterface(WebView webView, Context context, Handler handler) {
		this.webView = webView;
		this.context = context;
		this.handler = handler;
	}
	
	
//	@JavascriptInterface
//	public void getUserLoginStatus(){
//		handler.post(new Runnable() {
//			public void run() {
//		  		boolean isLogin = ((BaseActivity)context).mSettings.getBoolean(Constants.ISLOGIN, false);
//				JSONObject json = new JSONObject();
//				try {
//					json.put("username", ((BaseActivity)context).mSettings.getString(Constants.BINDPHONENUM, ""));
//					if(isLogin){
//						json.put("status", "1");
//					}else{
//						json.put("status", "0");
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//				
//				webView.loadUrl("javascript:getLoginStatuBack('" + json.toString()+ "')");
//			}
//		});
//	}
	
//	@JavascriptInterface
//	public void getUserInfo() {
//		handler.post(new Runnable() {
//			public void run() {
//				UserInfo userInfo = JfpalApplication.userInfo;
//				JSONObject userObject = new JSONObject();
//				if (userInfo != null) {
//					try {
//						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//
//						String username = ((BaseActivity)context).mSettings.getString(Constants.BINDPHONENUM, "");
//						String md5_2 = Utils.md5(Utils.md5(username + "+" + sdf.format(new Date()) + "+yjklicai"));
//						userObject.put("username", username);
//						userObject.put("md5", md5_2);
//						
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//				webView.loadUrl("javascript:getUserInfoBack('" + userObject.toString() + "')");
//			}
//		});
//	}
	
//	@JavascriptInterface
//	public void getSeriorUserInfo(final String username) {
//		handler.post(new Runnable() {
//			public void run() {
//				if(((BaseActivity)context).mSettings.getString(Constants.BINDPHONENUM, "").equals(username)) {
//					
//					UserInfo userInfo = JfpalApplication.userInfo;
//					JSONObject userObject = new JSONObject();
//					if (userInfo != null) {
//						try {
//							userObject.put("mobileNo", username);
//							userObject.put("name", userInfo.getRealName());
//							userObject.put("sex", userInfo.getGender());
//							userObject.put("email", userInfo.getEmail());
//							userObject.put("IdCardNo", userInfo.getCertType()); // I not know why
//							userObject.put("userLevel", userInfo.getAuthFlag());
//							userObject.put("appuser", Constants.APPUSER);
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//					}
//					webView.loadUrl("javascript:getSeriorUserInfoBack('" + userObject.toString() + "')");
//				}
//			}
//		});
//	}
	
//	@JavascriptInterface
//	public void makeOrder(final String order) {
////		System.out.println("order: " + order);
//		handler.post(new Runnable() {
//			public void run() {
//				if (order != null) {
//					try {
//						JSONObject jsonObject = new JSONObject(order);
//						String amt = jsonObject.getString("amt");
//						String orderId = jsonObject.getString("orderId");
//						String productNo = jsonObject.getString("productNo");
//						String appuser = jsonObject.getString("appuser");
//						String username = jsonObject.getString("username");
//						String secretkey = jsonObject.getString("secretkey");
//						
//						// Md5 校验
//						String yjkey = "HY2RqQAspPAxQTt5nahGyw==";
//						if(Utils.md5(appuser + amt + username + orderId + productNo + yjkey).equals(secretkey) ) {
//							// 请求订单
//							((BaseActivity)context).payInfo.setDoAction("SubmitOrder");
//							((BaseActivity)context).payInfo.setTransactAmount(MoneyEncoder.getPrice(amt));
//							((BaseActivity)context).payInfo.setMerchantId(Constants.MERCHANT_TYPE_YJWEALTH);
//							((BaseActivity)context).payInfo.setProductId(Constants.PRODUCT_TYPE_YJWEALTH);
//							((BaseActivity)context).payInfo.setBlesstype(productNo);
//							((BaseActivity)context).payInfo.setOrderDesc(orderId);
//							Intent intent = new Intent();
//							intent.setClass(context, CommonPayMethodActivity.class);
//							intent.putExtra(Constants.PAYINFO, ((BaseActivity)context).payInfo);
//							((Activity)context).startActivityForResult(intent, 0);
//						} else  { //检验失败
//							((BaseActivity)context).showToast("检验失败，请稍候重试");
//						}
//					} catch (Exception e) {
//						System.out.println(e);
//					}
//				}
//			}
//		});
//	}
	
	@JavascriptInterface
	public void goHome() {
		handler.post(new Runnable() {
			public void run() {
				((Activity)context).finish();
			}
		});
	}
	
}
