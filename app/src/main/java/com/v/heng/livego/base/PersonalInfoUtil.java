package com.v.heng.livego.base;


import android.content.Context;
import android.content.SharedPreferences;


public class PersonalInfoUtil {
	// 是否登陆
	public static boolean isLoginSuccess = false;
	// 用户uID
	public static String uID = "";
	/** 测试账号 */
	public static String username = "";
	
	
	
	
	

//	/**
//	 * 得到本地code
//	 *
//	 * @param context
//	 * @return
//	 */
//	public static int getVersionsCode(Context context) {
//		SharedPreferences sharedPreferences = context.getSharedPreferences(Context. Constant.preferences_versions, Context.MODE_PRIVATE);
//		int versionsCode = sharedPreferences.getInt(Constant.preferences_versions_code, -1000);
//		return versionsCode;
//	}

	/**
	 * 
	 * @param context
	 * @param code
	 */
	public static void setVersionsCode(Context context, int code) {
		SharedPreferences.Editor editor = context.getSharedPreferences(Constant.preferences_versions, Context.MODE_PRIVATE).edit();
		editor.putInt(Constant.preferences_versions_code, code);
		editor.commit();
	}
	
	
	/**
	 * 设置加密文件更新标识
	 * 
	 * @param context
	 * @param flag
	 */
	public static void setEncryptFlag(Context context, boolean flag) {
		SharedPreferences.Editor editor = context.getSharedPreferences(Constant.preferences_encrypt_flag, Context.MODE_PRIVATE).edit();
		editor.putBoolean(Constant.preferences_encrypt_flag_code, flag);
		boolean result = editor.commit();
		if(result)
		{
			System.out.println("save EncryptFlag success");
		}
	}

	/**
	 * 获取加密文件更新标识
	 * 
	 * @param context
	 * @return
	 */
	public static boolean getEncryptFlag(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.preferences_encrypt_flag, Context.MODE_PRIVATE);
		boolean result = sharedPreferences.getBoolean(Constant.preferences_encrypt_flag, false);
		return result;
	}



	

	
//	/**
//	 * DB 中得到  用户信息
//	 * @return
//	 */
//	public static UserInfo getUserInfo(Context context) {
//		UserInfoDao userInfoDao = new UserInfoDao(context);
//		return userInfoDao.getUserInfo();
//	}
	






	
	
	
	
	
	
	
	
}
