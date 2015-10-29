package com.v.heng.livego.utils;

import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.v.heng.livego.base.MyApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;


/**
 * 日志工具包
 *
 */
public class LogUtil {

	public static int LOGLEVEL = android.util.Log.VERBOSE;
	public static boolean ERROR = LOGLEVEL <= 6;
	public static boolean WARN = LOGLEVEL <= 5;
	public static boolean INFO = LOGLEVEL <= 4;
	public static boolean DEBUG = LOGLEVEL <= 3;
	public static boolean VERBOSE = LOGLEVEL <= 2;
	
	
	
	public static void logVERBOSE(Class<?> cls , String msg) {
		if(VERBOSE && MyApplication.isDebug) {
			Log.v(cls.getSimpleName(), msg);
		}
	}
	
	public static void logDEBUG(Class<?> cls , String msg) {
		if(DEBUG && MyApplication.isDebug) {
			Log.d(cls.getSimpleName(), msg);
		}
	}
	
	public static void logINFO(Class<?> cls , String msg) {
		if(INFO && MyApplication.isDebug) {
			Log.i(cls.getSimpleName(), msg);
		}
	}
	
	public static void logWARN(Class<?> cls , String msg) {
		if(WARN && MyApplication.isDebug) {
			Log.w(cls.getSimpleName(), msg);
		}
	}
	
	public static void logERROR(Class<?> cls , String msg) {
		if(ERROR && MyApplication.isDebug) {
			Log.e(cls.getSimpleName(), msg);
		}
		MobclickAgent.reportError(MyApplication.context, msg);
	}
	
	public static void logERROR(Class<?> cls , Exception e) {
		if(ERROR && MyApplication.isDebug) {
			Log.e(cls.getSimpleName(), getDetailMessage(e));
		}
		MobclickAgent.reportError(MyApplication.context, getDetailMessage(e));
	}
	
	/**
	 * @param e
	 * @return
	 */
	public static String getDetailMessage(Exception e) {
		Writer writer = new StringWriter();  
		PrintWriter printWriter = new PrintWriter(writer);  
		e.printStackTrace(printWriter);
		return writer.toString();
	}
	
	
	
	/**
	 * 打印普通信息
	 * @param msg
	 */
	public static void printInfo(String msg){
//		System.out.println(msg);
		Log.i("i", msg);
	}
	
	public static void printInfo(Class<?> cls , String msg){
		System.out.println(cls.getSimpleName() + ": " + msg);
	}
	
	/**
	 * 打印错误信息
	 * @param msg
	 */
	public static void printError(String msg){
			Log.e("error", msg);
	}
	
	/**
	 * 功能：记录日志<br>
	 * @param savePathStr 保存日志路径
	 * @param saveFileNameS 保存日志文件名
	 * @param saveDataStr 保存日志数据
	 * @param saveTypeStr 保存类型，fals为覆盖保存，true为在原来文件后添加保存
	 */
	public static void recordLog(String savePathStr,String saveFileNameS,String saveDataStr,boolean saveTypeStr) {

		try {

			String savePath = savePathStr;
			String saveFileName = saveFileNameS;
			String saveData = saveDataStr;
			boolean saveType =saveTypeStr;

			// 准备需要保存的文件
			File saveFilePath = new File(savePath);
			if (!saveFilePath.exists()) {
				saveFilePath.mkdirs();
			}
			File saveFile = new File(savePath +File.separator+ saveFileName);
			if (!saveType && saveFile.exists()) {
				saveFile.delete();
				saveFile.createNewFile();
				// 保存结果到文件
				FileOutputStream fos = new FileOutputStream(saveFile, saveType);
				fos.write(saveData.getBytes());
				fos.close();
			} else if (saveType && saveFile.exists()) {
				//saveFile.createNewFile();
				if(saveFile.length()>1024*1024){//当文件大于1M时，删除文件，重新创建日志文件
					saveFile.delete();
					saveFile.createNewFile();
				}
				FileOutputStream fos = new FileOutputStream(saveFile, saveType);
				fos.write(saveData.getBytes());
				fos.close();
			}else if (saveType && !saveFile.exists()) {
				saveFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(saveFile, saveType);
				fos.write(saveData.getBytes());
				fos.close();
			}



		} catch (Exception e) {
			saveDataStr = e.getMessage();
			//recordLog(savePathStr, saveFileNameS, saveDataStr, saveTypeStr);
			
			e.printStackTrace();
		}


	}
}
