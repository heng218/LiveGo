package com.v.heng.livego.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * 文件操作工具包
 * 
 * @author heng
 */
public class FileUtils {
	private static final String TAG = "FileUtils";

	// /*
	// * 读~(非)加密文件
	// */
	// public static void readFile_NotOperation(String fullPath, String toPath) {
	//
	// File file = new File(fullPath);
	// if (!file.exists()) {
	// return;
	// }
	//
	// FileInputStream fis = null;
	// ByteArrayOutputStream baos = null;
	// try {
	// fis = new FileInputStream(fullPath);
	// baos = new ByteArrayOutputStream();
	//
	// byte[] buffer = new byte[1];
	// while ((fis.read(buffer)) != -1) {
	// baos.write(~buffer[0]);
	// }
	//
	// System.out.println("baos.toString() : " + baos.toString());
	//
	// fis.close();
	// } catch (FileNotFoundException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// } finally {
	// try {
	// fis.close();
	// baos.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// /*
	// * 写~(非)加密文件
	// */
	// public static void writeFile_NotOperation(String content, String path,
	// String filename) {
	//
	// if (!checkSDCardExists()) {
	// return;
	// }
	//
	// File dir = new File(path);
	// if (!dir.exists()) {
	// dir.mkdirs();
	// }
	// FileOutputStream fos = null;
	// try {
	// fos = new FileOutputStream(path + filename, false); // 追加写
	//
	// // 非运算加密
	// byte[] b = content.toString().getBytes();
	// for (int i = 0; i < b.length; i++) {
	// byte oneByte = b[i];
	// fos.write(~oneByte);
	// }
	//
	// fos.close();
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } finally {
	// try {
	// fos.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// }

	public static boolean uploadFileBySocket(String host, String _url, String path) throws Exception {
		String BOUNDARY = "---------------------------7db1c523809b2";// 数据分割线

		// 根据path找到SDCard中的文件
		File file = new File(path);
		// 组装表单字段和文件之前的数据
		StringBuilder sb = new StringBuilder();

		// sb.append("--" + BOUNDARY + "\r\n");
		// sb.append("Content-Disposition: form-data; name=\"username\"" + "\r\n");
		// sb.append("\r\n");
		// sb.append(username + "\r\n");
		//
		// sb.append("--" + BOUNDARY + "\r\n");
		// sb.append("Content-Disposition: form-data; name=\"password\"" + "\r\n");
		// sb.append("\r\n");
		// sb.append(password + "\r\n");
		//
		// sb.append("--" + BOUNDARY + "\r\n");
		// sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + path + "\"" + "\r\n");
		// sb.append("Content-Type: image/pjpeg" + "\r\n");
		// sb.append("\r\n");

		// 文件之前的数据
		byte[] before = sb.toString().getBytes("UTF-8");
		// 文件之后的数据
		byte[] after = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("UTF-8");

		URL url = new URL(_url);

		// 由于HttpURLConnection中会缓存数据, 上传较大文件时会导致内存溢出, 所以我们使用Socket传输
		Socket socket = new Socket(url.getHost(), url.getPort());
		OutputStream out = socket.getOutputStream();
		PrintStream ps = new PrintStream(out, true, "UTF-8");

		// 写出请求头
		ps.println("POST /14_Web/servlet/LoginServlet HTTP/1.1");
		ps.println("Content-Type: multipart/form-data; boundary=" + BOUNDARY);
		ps.println("Content-Length: " + String.valueOf(before.length + file.length() + after.length));
		ps.println("Host: " + host);

		InputStream in = new FileInputStream(file);

		// 写出数据
		out.write(before);

		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) != -1) {
			out.write(buf, 0, len);
		}
		out.write(after);

		in.close();
		out.close();
		socket.close();

		return true;
	}

	/**
	 * 写异常日志到文件
	 * 
	 * @param buffer
	 * @param folder
	 * @param fileName
	 * @return
	 */
	public static boolean writeCrashLogToFile(Context context, byte[] buffer, String folder, String fileName) {
		boolean writeSucc = false;

		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

		String folderPath = "";
		if (sdCardExist) {
			folderPath = folder;
		} else {
			folderPath = context.getFilesDir().getAbsolutePath();
		}

		File fileDir = new File(folderPath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		File file = new File(folderPath + fileName);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file, false);
			out.write(buffer);
			writeSucc = true;
		} catch (Exception e) {
			Log.e(TAG, "writeCrashLogToFile", e);
		} finally {
			try {
				out.close();
			} catch (IOException e) {
			}
		}
		return writeSucc;
	}

	/**
	 * 写文本文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
	 * 
	 * @param context
	 */
	public static void write(Context context, String fileName, String content) {
		if (content == null)
			content = "";
		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			fos.write(content.getBytes());
		} catch (Exception e) {
			Log.e(TAG, "write", e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 读取文本文件
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static String read(Context context, String fileName) {
		try {
			FileInputStream in = context.openFileInput(fileName);
			return readInStream(in);
		} catch (Exception e) {
			Log.e(TAG, "read", e);
		}
		return "";
	}

	public static String readInStream(InputStream inStream) {
		ByteArrayOutputStream outStream = null;
		try {
			outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[512];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, length);
			}

			return outStream.toString();
		} catch (IOException e) {
			Log.e("FileUtils", "readInStream", e);
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
				if (outStream != null) {
					outStream.close();
				}
			} catch (IOException e) {
			}
		}
		return null;
	}

	public static byte[] readInStream_btyes(InputStream inStream) {
		ByteArrayOutputStream outStream = null;
		try {
			outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[512];
			int length = -1;
			while ((length = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, length);
			}

			return outStream.toByteArray();
		} catch (IOException e) {
			Log.e("FileUtils", "readInStream", e);
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
				if (outStream != null) {
					outStream.close();
				}
			} catch (IOException e) {
			}
		}
		return null;
	}

	public static File createFile(String folderPath, String fileName) {
		File destDir = new File(folderPath);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		return new File(folderPath, fileName + fileName);
	}

	/**
	 * 向手机写图片
	 * 
	 * @param buffer
	 * @param folder
	 * @param fileName
	 * @return
	 */
	public static boolean writeFile(byte[] buffer, String folder, String fileName) {
		boolean writeSucc = false;

		File fileDir = new File(folder);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		File file = new File(folder + fileName);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file, false);
			out.write(buffer);
			out.flush();
			writeSucc = true;
		} catch (Exception e) {
			Log.e(TAG, "写文件失败" + folder, e);
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
			}
		}
		return writeSucc;
	}

	public static boolean writeFile(String data, String folder, String fileName) {
		boolean writeSucc = false;

		File fileDir = new File(folder);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		File file = new File(folder + fileName);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file, false);
			out.write(data.getBytes());
			out.flush();
			writeSucc = true;
		} catch (Exception e) {
			Log.e(TAG, "writeFile", e);
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return writeSucc;
	}

	/**
	 * 根据文件绝对路径获取文件名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileName(String filePath) {
		if (StringUtils.isEmpty(filePath))
			return "";
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
	}

	/**
	 * 根据文件的绝对路径获取文件名但不包含扩展名
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getFileNameNoFormat(String filePath) {
		if (StringUtils.isEmpty(filePath)) {
			return "";
		}
		int point = filePath.lastIndexOf('.');
		return filePath.substring(filePath.lastIndexOf(File.separator) + 1, point);
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileFormat(String fileName) {
		if (StringUtils.isEmpty(fileName))
			return "";

		int point = fileName.lastIndexOf('.');
		return fileName.substring(point + 1);
	}

	/**
	 * 获取文件大小
	 * 
	 * @param filePath
	 * @return
	 */
	public static long getFileSize(String filePath) {
		long size = 0;

		File file = new File(filePath);
		if (file != null && file.exists()) {
			size = file.length();
		}
		return size;
	}

	/**
	 * 获取文件大小
	 * 
	 * @param size
	 *            字节
	 * @return
	 */
	public static String getFileSize(long size) {
		if (size <= 0)
			return "0";
		java.text.DecimalFormat df = new java.text.DecimalFormat("##.##");
		float temp = (float) size / 1024;
		if (temp >= 1024) {
			return df.format(temp / 1024) + "M";
		} else {
			return df.format(temp) + "K";
		}
	}

	/**
	 * 转换文件大小
	 * 
	 * @param fileS
	 * @return B/KB/MB/GB
	 */
	public static String formatFileSize(long fileS) {
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 获取目录文件大小
	 * 
	 * @param dir
	 * @return
	 */
	public static long getDirSize(File dir) {
		if (dir == null) {
			return 0;
		}
		if (!dir.isDirectory()) {
			return 0;
		}
		long dirSize = 0;
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				dirSize += file.length();
			} else if (file.isDirectory()) {
				dirSize += file.length();
				dirSize += getDirSize(file); // 递归调用继续统计
			}
		}
		return dirSize;
	}

	/**
	 * 获取目录文件个数
	 * 
	 * @param
	 * @return
	 */
	public long getFileList(File dir) {
		long count = 0;
		File[] files = dir.listFiles();
		count = files.length;
		for (File file : files) {
			if (file.isDirectory()) {
				count = count + getFileList(file);// 递归
				count--;
			}
		}
		return count;
	}

	public static boolean checkSDCardExists() {
		if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED)
			return true;
		return false;
	}

	/**
	 * 检查文件是否存在
	 * 
	 * @param name
	 * @return
	 */
	public static boolean checkFileExists(String name) {
		boolean status;
		if (!name.equals("")) {
			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + name);
			status = newPath.exists();
		} else {
			status = false;
		}
		return status;

	}

	/**
	 * 计算SD卡的剩余空间
	 * 
	 * @return 返回-1，说明没有安装sd卡 单位 byte
	 */
	public static long getFreeDiskSpace() {
		String status = Environment.getExternalStorageState();
		long freeSpace = 0;
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				File path = Environment.getExternalStorageDirectory();
				StatFs stat = new StatFs(path.getPath());
				long blockSize = stat.getBlockSize();
				long availableBlocks = stat.getAvailableBlocks();
				// freeSpace = availableBlocks * blockSize / 1024;
				freeSpace = availableBlocks * blockSize;
			} catch (Exception e) {
				Log.e(TAG, "getFreeDiskSpace", e);
			}
		} else {
			return -1;
		}
		return (freeSpace);
	}

	/**
	 * 新建目录
	 * 
	 * @param directoryName
	 * @return
	 */
	public static boolean createDirectory(String directoryName) {
		boolean status;
		if (!directoryName.equals("")) {
			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + directoryName);
			status = newPath.mkdir();
			status = true;
		} else
			status = false;
		return status;
	}

	/**
	 * 根据文件绝对路径文件创建目录
	 * 
	 * @return
	 */
	public static boolean createDirs(String filePath) {
		if (filePath == null) {
			return false;
		}

		if (new File(filePath).exists())
			return true;

		String path = filePath.substring(0, filePath.lastIndexOf(File.separator));
		File file = new File(path);
		if (file.exists()) {
			return true;
		}
		file.mkdirs();
		return true;
	}

	/**
	 * 检查是否安装SD卡
	 * 
	 * @return
	 */
	public static boolean checkSaveLocationExists() {
		String sDCardStatus = Environment.getExternalStorageState();
		boolean status;
		if (sDCardStatus.equals(Environment.MEDIA_MOUNTED)) {
			status = true;
		} else
			status = false;
		return status;
	}

	/**
	 * 删除目录下的文件，目录保留
	 * 
	 * @param path
	 * @return
	 */
	public static void deleteFiles(String path) {
		if (path == null)
			return;
		File file = new File(path);
		if (file.exists()) {
			deleteFiles(file);
		}
	}

	public static void deleteFiles(File file) {
		if (file.listFiles() != null) {
			for (File f : file.listFiles()) {
				if (f.isDirectory()) {
					deleteFiles(f);
				} else {
					f.delete();
				}
			}
		}
	}

	/**
	 * 删除目录(包括：目录里的所有文件)
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean deleteDirectory(String fileName) {
		boolean status;
		SecurityManager checker = new SecurityManager();

		if (!fileName.equals("")) {

			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + fileName);
			checker.checkDelete(newPath.toString());
			if (newPath.isDirectory()) {
				String[] listfile = newPath.list();
				// delete all files within the specified directory and then
				// delete the directory
				try {
					for (int i = 0; i < listfile.length; i++) {
						File deletedFile = new File(newPath.toString() + "/" + listfile[i].toString());
						deletedFile.delete();
					}
					newPath.delete();
					Log.i("DirectoryManager deleteDirectory", fileName);
					status = true;
				} catch (Exception e) {
					Log.e(TAG, "deleteDirectory", e);
					status = false;
				}

			} else
				status = false;
		} else
			status = false;
		return status;
	}

	/**
	 * 删除文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean deleteFile(String fileName) {
		boolean status;
		SecurityManager checker = new SecurityManager();

		if (!fileName.equals("")) {

			File path = Environment.getExternalStorageDirectory();
			File newPath = new File(path.toString() + fileName);
			checker.checkDelete(newPath.toString());
			if (newPath.isFile()) {
				try {
					Log.i("DirectoryManager deleteFile", fileName);
					newPath.delete();
					status = true;
				} catch (SecurityException se) {
					Log.e(TAG, "deleteFile", se);
					status = false;
				}
			} else
				status = false;
		} else
			status = false;
		return status;
	}

	/**
	 * 
	 * getMIMEType: Get the MIME Types from the file name.
	 * 
	 *            The name of the file.
	 * @return mimetype the MIME Type of the file.
	 * @throws
	 */
	public static String getMIMEType(File file) {
		String type = "*/*";

		String name = file.getName();
		int dotIndex = name.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}

		String end = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();
		if (end.equals(""))
			return type;
		for (int i = 0; i < MIME_MapTable.length; i++) {
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}

		return type;
	}

	/**
	 * 打开各类文件
	 * 
	 * @param context
	 * @param f
	 */
	public static void openFile(Context context, File f) {
		try {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(Intent.ACTION_VIEW);
			String type = getMIMEType(f);
			intent.setDataAndType(Uri.fromFile(f), type);
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The Table of MIME Types
	 */
	public static final String[][] MIME_MapTable = {
			// {后缀名， MIME类型}
			{ "3gp", "video/3gpp" }, { "aab", "application/x-authoware-bin" }, { "aam", "application/x-authoware-map" },
			{ "aas", "application/x-authoware-seg" }, { "ai", "application/postscript" }, { "aif", "audio/x-aiff" }, { "aifc", "audio/x-aiff" },
			{ "aiff", "audio/x-aiff" }, { "als", "audio/X-Alpha5" }, { "amc", "application/x-mpeg" }, { "ani", "application/octet-stream" },
			{ "apk", "application/vnd.android.package-archive" }, { "asc", "text/plain" }, { "asd", "application/astound" }, { "asf", "video/x-ms-asf" },
			{ "asn", "application/astound" }, { "asp", "application/x-asap" }, { "asx", "video/x-ms-asf" }, { "au", "audio/basic" },
			{ "avb", "application/octet-stream" }, { "avi", "video/x-msvideo" }, { "awb", "audio/amr-wb" }, { "bcpio", "application/x-bcpio" },
			{ "bin", "application/octet-stream" }, { "bld", "application/bld" }, { "bld2", "application/bld2" }, { "bmp", "image/bmp" },
			{ "bpk", "application/octet-stream" }, { "bz2", "application/x-bzip2" }, { "c", "text/x-csrc" }, { "cpp", "text/x-c++src" },
			{ "cal", "image/x-cals" }, { "ccn", "application/x-cnc" }, { "cco", "application/x-cocoa" }, { "cdf", "application/x-netcdf" },
			{ "cgi", "magnus-internal/cgi" }, { "chat", "application/x-chat" }, { "class", "application/octet-stream" }, { "clp", "application/x-msclip" },
			{ "cmx", "application/x-cmx" }, { "co", "application/x-cult3d-object" }, { "cod", "image/cis-cod" }, { "cpio", "application/x-cpio" },
			{ "cpt", "application/mac-compactpro" }, { "crd", "application/x-mscardfile" }, { "csh", "application/x-csh" }, { "csm", "chemical/x-csml" },
			{ "csml", "chemical/x-csml" }, { "css", "text/css" }, { "cur", "application/octet-stream" }, { "dcm", "x-lml/x-evm" },
			{ "dcr", "application/x-director" }, { "dcx", "image/x-dcx" }, { "dhtml", "text/html" }, { "dir", "application/x-director" },
			{ "dll", "application/octet-stream" }, { "dmg", "application/octet-stream" }, { "dms", "application/octet-stream" },
			{ "doc", "application/msword" }, { "dot", "application/x-dot" }, { "dvi", "application/x-dvi" }, { "dwf", "drawing/x-dwf" },
			{ "dwg", "application/x-autocad" }, { "dxf", "application/x-autocad" }, { "dxr", "application/x-director" },
			{ "ebk", "application/x-expandedbook" }, { "emb", "chemical/x-embl-dl-nucleotide" }, { "embl", "chemical/x-embl-dl-nucleotide" },
			{ "eps", "application/postscript" }, { "eri", "image/x-eri" }, { "es", "audio/echospeech" }, { "esl", "audio/echospeech" },
			{ "etc", "application/x-earthtime" }, { "etx", "text/x-setext" }, { "evm", "x-lml/x-evm" }, { "evy", "application/x-envoy" },
			{ "exe", "application/octet-stream" }, { "fh4", "image/x-freehand" }, { "fh5", "image/x-freehand" }, { "fhc", "image/x-freehand" },
			{ "fif", "image/fif" }, { "fm", "application/x-maker" }, { "fpx", "image/x-fpx" }, { "fvi", "video/isivideo" },
			{ "gau", "chemical/x-gaussian-input" }, { "gca", "application/x-gca-compressed" }, { "gdb", "x-lml/x-gdb" }, { "gif", "image/gif" },
			{ "gps", "application/x-gps" }, { "gtar", "application/x-gtar" }, { "gz", "application/x-gzip" }, { "h", "text/x-chdr" },
			{ "hdf", "application/x-hdf" }, { "hdm", "text/x-hdml" }, { "hdml", "text/x-hdml" }, { "hlp", "application/winhlp" },
			{ "hqx", "application/mac-binhex40" }, { "htm", "text/html" }, { "html", "text/html" }, { "hts", "text/html" },
			{ "ice", "x-conference/x-cooltalk" }, { "ico", "application/octet-stream" }, { "ief", "image/ief" }, { "ifm", "image/gif" },
			{ "ifs", "image/ifs" }, { "imy", "audio/melody" }, { "ins", "application/x-NET-Install" }, { "ips", "application/x-ipscript" },
			{ "ipx", "application/x-ipix" }, { "it", "audio/x-mod" }, { "itz", "audio/x-mod" }, { "ivr", "i-world/i-vrml" }, { "j2k", "image/j2k" },
			{ "jad", "text/vnd.sun.j2me.app-descriptor" }, { "jam", "application/x-jam" }, { "java", "application/x-java" },
			{ "jar", "application/java-archive" }, { "jnlp", "application/x-java-jnlp-file" }, { "jpe", "image/jpeg" }, { "jpeg", "image/jpeg" },
			{ "jpg", "image/jpeg" }, { "jpz", "image/jpeg" }, { "js", "application/x-javascript" }, { "jwc", "application/jwc" },
			{ "kjx", "application/x-kjx" }, { "lak", "x-lml/x-lak" }, { "latex", "application/x-latex" }, { "lcc", "application/fastman" },
			{ "lcl", "application/x-digitalloca" }, { "lcr", "application/x-digitalloca" }, { "lgh", "application/lgh" },
			{ "lha", "application/octet-stream" }, { "lml", "x-lml/x-lml" }, { "lmlpack", "x-lml/x-lmlpack" }, { "lsf", "video/x-ms-asf" },
			{ "lsx", "video/x-ms-asf" }, { "lzh", "application/x-lzh" }, { "m13", "application/x-msmediaview" }, { "m14", "application/x-msmediaview" },
			{ "m15", "audio/x-mod" }, { "m3u", "audio/x-mpegurl" }, { "m3url", "audio/x-mpegurl" }, { "ma1", "audio/ma1" }, { "ma2", "audio/ma2" },
			{ "ma3", "audio/ma3" }, { "ma5", "audio/ma5" }, { "man", "application/x-troff-man" }, { "map", "magnus-internal/imagemap" },
			{ "mbd", "application/mbedlet" }, { "mct", "application/x-mascot" }, { "mdb", "application/x-msaccess" }, { "mdz", "audio/x-mod" },
			{ "me", "application/x-troff-me" }, { "mel", "text/x-vmel" }, { "mi", "application/x-mif" }, { "mid", "audio/midi" }, { "midi", "audio/midi" },
			{ "mif", "application/x-mif" }, { "mil", "image/x-cals" }, { "mio", "audio/x-mio" }, { "mmf", "application/x-skt-lbs" }, { "mng", "video/x-mng" },
			{ "mny", "application/x-msmoney" }, { "moc", "application/x-mocha" }, { "mocha", "application/x-mocha" }, { "mod", "audio/x-mod" },
			{ "mof", "application/x-yumekara" }, { "mol", "chemical/x-mdl-molfile" }, { "mop", "chemical/x-mopac-input" }, { "mov", "video/quicktime" },
			{ "movie", "video/x-sgi-movie" }, { "mp2", "audio/x-mpeg" }, { "mp3", "audio/x-mpeg" }, { "mp4", "video/mp4" },
			{ "mpc", "application/vnd.mpohun.certificate" }, { "mpe", "video/mpeg" }, { "mpeg", "video/mpeg" }, { "mpg", "video/mpeg" },
			{ "mpg4", "video/mp4" }, { "mpga", "audio/mpeg" }, { "mpn", "application/vnd.mophun.application" }, { "mpp", "application/vnd.ms-project" },
			{ "mps", "application/x-mapserver" }, { "mrl", "text/x-mrml" }, { "mrm", "application/x-mrm" }, { "ms", "application/x-troff-ms" },
			{ "mts", "application/metastream" }, { "mtx", "application/metastream" }, { "mtz", "application/metastream" }, { "mzv", "application/metastream" },
			{ "nar", "application/zip" }, { "nbmp", "image/nbmp" }, { "nc", "application/x-netcdf" }, { "ndb", "x-lml/x-ndb" }, { "ndwn", "application/ndwn" },
			{ "nif", "application/x-nif" }, { "nmz", "application/x-scream" }, { "nokia-op-logo", "image/vnd.nok-oplogo-color" },
			{ "npx", "application/x-netfpx" }, { "nsnd", "audio/nsnd" }, { "nva", "application/x-neva1" }, { "oda", "application/oda" },
			{ "oom", "application/x-AtlasMate-Plugin" }, { "pac", "audio/x-pac" }, { "pae", "audio/x-epac" }, { "pan", "application/x-pan" },
			{ "pbm", "image/x-portable-bitmap" }, { "pcx", "image/x-pcx" }, { "pda", "image/x-pda" }, { "pdb", "chemical/x-pdb" },
			{ "pdf", "application/pdf" }, { "pfr", "application/font-tdpfr" }, { "pgm", "image/x-portable-graymap" }, { "pict", "image/x-pict" },
			{ "pm", "application/x-perl" }, { "pmd", "application/x-pmd" }, { "png", "image/png" }, { "pnm", "image/x-portable-anymap" },
			{ "pnz", "image/png" }, { "pot", "application/vnd.ms-powerpoint" }, { "ppm", "image/x-portable-pixmap" },
			{ "pps", "application/vnd.ms-powerpoint" }, { "ppt", "application/vnd.ms-powerpoint" }, { "pqf", "application/x-cprplayer" },
			{ "pqi", "application/cprplayer" }, { "prc", "application/x-prc" }, { "proxy", "application/x-ns-proxy-autoconfig" },
			{ "ps", "application/postscript" }, { "ptlk", "application/listenup" }, { "pub", "application/x-mspublisher" }, { "pvx", "video/x-pv-pvx" },
			{ "qcp", "audio/vnd.qcelp" }, { "qt", "video/quicktime" }, { "qti", "image/x-quicktime" }, { "qtif", "image/x-quicktime" },
			{ "r3t", "text/vnd.rn-realtext3d" }, { "ra", "audio/x-pn-realaudio" }, { "ram", "audio/x-pn-realaudio" },
			{ "rar", "application/x-rar-compressed" }, { "ras", "image/x-cmu-raster" }, { "rdf", "application/rdf+xml" }, { "rf", "image/vnd.rn-realflash" },
			{ "rgb", "image/x-rgb" }, { "rlf", "application/x-richlink" }, { "rm", "audio/x-pn-realaudio" }, { "rmf", "audio/x-rmf" },
			{ "rmm", "audio/x-pn-realaudio" }, { "rmvb", "audio/x-pn-realaudio" }, { "rnx", "application/vnd.rn-realplayer" },
			{ "roff", "application/x-troff" }, { "rp", "image/vnd.rn-realpix" }, { "rpm", "audio/x-pn-realaudio-plugin" }, { "rt", "text/vnd.rn-realtext" },
			{ "rte", "x-lml/x-gps" }, { "rtf", "application/rtf" }, { "rtg", "application/metastream" }, { "rtx", "text/richtext" },
			{ "rv", "video/vnd.rn-realvideo" }, { "rwc", "application/x-rogerwilco" }, { "s3m", "audio/x-mod" }, { "s3z", "audio/x-mod" },
			{ "sca", "application/x-supercard" }, { "scd", "application/x-msschedule" }, { "sdf", "application/e-score" }, { "sea", "application/x-stuffit" },
			{ "sgm", "text/x-sgml" }, { "sgml", "text/x-sgml" }, { "sh", "application/x-sh" }, { "shar", "application/x-shar" },
			{ "shtml", "magnus-internal/parsed-html" }, { "shw", "application/presentations" }, { "si6", "image/si6" }, { "si7", "image/vnd.stiwap.sis" },
			{ "si9", "image/vnd.lgtwap.sis" }, { "sis", "application/vnd.symbian.install" }, { "sit", "application/x-stuffit" },
			{ "skd", "application/x-Koan" }, { "skm", "application/x-Koan" }, { "skp", "application/x-Koan" }, { "skt", "application/x-Koan" },
			{ "slc", "application/x-salsa" }, { "smd", "audio/x-smd" }, { "smi", "application/smil" }, { "smil", "application/smil" },
			{ "smp", "application/studiom" }, { "smz", "audio/x-smd" }, { "snd", "audio/basic" }, { "spc", "text/x-speech" },
			{ "spl", "application/futuresplash" }, { "spr", "application/x-sprite" }, { "sprite", "application/x-sprite" }, { "spt", "application/x-spt" },
			{ "src", "application/x-wais-source" }, { "stk", "application/hyperstudio" }, { "stm", "audio/x-mod" }, { "sv4cpio", "application/x-sv4cpio" },
			{ "sv4crc", "application/x-sv4crc" }, { "svf", "image/vnd" }, { "svg", "image/svg-xml" }, { "svh", "image/svh" }, { "svr", "x-world/x-svr" },
			{ "swf", "application/x-shockwave-flash" }, { "swfl", "application/x-shockwave-flash" }, { "t", "application/x-troff" },
			{ "tad", "application/octet-stream" }, { "talk", "text/x-speech" }, { "tar", "application/x-tar" }, { "taz", "application/x-tar" },
			{ "tbp", "application/x-timbuktu" }, { "tbt", "application/x-timbuktu" }, { "tcl", "application/x-tcl" }, { "tex", "application/x-tex" },
			{ "texi", "application/x-texinfo" }, { "texinfo", "application/x-texinfo" }, { "tgz", "application/x-tar" }, { "thm", "application/vnd.eri.thm" },
			{ "tif", "image/tiff" }, { "tiff", "image/tiff" }, { "tki", "application/x-tkined" }, { "tkined", "application/x-tkined" },
			{ "toc", "application/toc" }, { "toy", "image/toy" }, { "tr", "application/x-troff" }, { "trk", "x-lml/x-gps" },
			{ "trm", "application/x-msterminal" }, { "tsi", "audio/tsplayer" }, { "tsp", "application/dsptype" }, { "tsv", "text/tab-separated-values" },
			{ "tsv", "text/tab-separated-values" }, { "ttf", "application/octet-stream" }, { "ttz", "application/t-time" }, { "txt", "text/plain" },
			{ "ult", "audio/x-mod" }, { "ustar", "application/x-ustar" }, { "uu", "application/x-uuencode" }, { "uue", "application/x-uuencode" },
			{ "vcd", "application/x-cdlink" }, { "vcf", "text/x-vcard" }, { "vdo", "video/vdo" }, { "vib", "audio/vib" }, { "viv", "video/vivo" },
			{ "vivo", "video/vivo" }, { "vmd", "application/vocaltec-media-desc" }, { "vmf", "application/vocaltec-media-file" },
			{ "vmi", "application/x-dreamcast-vms-info" }, { "vms", "application/x-dreamcast-vms" }, { "vox", "audio/voxware" },
			{ "vqe", "audio/x-twinvq-plugin" }, { "vqf", "audio/x-twinvq" }, { "vql", "audio/x-twinvq" }, { "vre", "x-world/x-vream" },
			{ "vrml", "x-world/x-vrml" }, { "vrt", "x-world/x-vrt" }, { "vrw", "x-world/x-vream" }, { "vts", "workbook/formulaone" }, { "wav", "audio/x-wav" },
			{ "wax", "audio/x-ms-wax" }, { "wbmp", "image/vnd.wap.wbmp" }, { "web", "application/vnd.xara" }, { "wi", "image/wavelet" },
			{ "wis", "application/x-InstallShield" }, { "wm", "video/x-ms-wm" }, { "wma", "audio/x-ms-wma" }, { "wmd", "application/x-ms-wmd" },
			{ "wmf", "application/x-msmetafile" }, { "wml", "text/vnd.wap.wml" }, { "wmlc", "application/vnd.wap.wmlc" }, { "wmls", "text/vnd.wap.wmlscript" },
			{ "wmlsc", "application/vnd.wap.wmlscriptc" }, { "wmlscript", "text/vnd.wap.wmlscript" }, { "wmv", "audio/x-ms-wmv" }, { "wmx", "video/x-ms-wmx" },
			{ "wmz", "application/x-ms-wmz" }, { "wpng", "image/x-up-wpng" }, { "wpt", "x-lml/x-gps" }, { "wri", "application/x-mswrite" },
			{ "wrl", "x-world/x-vrml" }, { "wrz", "x-world/x-vrml" }, { "ws", "text/vnd.wap.wmlscript" }, { "wsc", "application/vnd.wap.wmlscriptc" },
			{ "wv", "video/wavelet" }, { "wvx", "video/x-ms-wvx" }, { "wxl", "application/x-wxl" }, { "x-gzip", "application/x-gzip" },
			{ "xar", "application/vnd.xara" }, { "xbm", "image/x-xbitmap" }, { "xdm", "application/x-xdma" }, { "xdma", "application/x-xdma" },
			{ "xdw", "application/vnd.fujixerox.docuworks" }, { "xht", "application/xhtml+xml" }, { "xhtm", "application/xhtml+xml" },
			{ "xhtml", "application/xhtml+xml" }, { "xla", "application/vnd.ms-excel" }, { "xlc", "application/vnd.ms-excel" },
			{ "xll", "application/x-excel" }, { "xlm", "application/vnd.ms-excel" }, { "xls", "application/vnd.ms-excel" },
			{ "xlt", "application/vnd.ms-excel" }, { "xlw", "application/vnd.ms-excel" }, { "xm", "audio/x-mod" }, { "xml", "text/xml" },
			{ "xmz", "audio/x-mod" }, { "xpi", "application/x-xpinstall" }, { "xpm", "image/x-xpixmap" }, { "xsit", "text/xml" }, { "xsl", "text/xml" },
			{ "xul", "text/xul" }, { "xwd", "image/x-xwindowdump" }, { "xyz", "chemical/x-pdb" }, { "yz1", "application/x-yz1" },
			{ "z", "application/x-compress" }, { "zac", "application/x-zaurus-zac" }, { "zip", "application/zip	" }, };

	/**
	 * The Table of ICON Types
	 */
	public static final String[][] ICON_MapTable = {
			// {后缀名， ICON类型}
			{ "3gp", "icon_video" },
			{ "asf", "icon_video" },
			{ "asx", "icon_video" },
			{ "avi", "icon_video" },
			{ "fvi", "icon_video" },
			{ "lsf", "icon_video" },
			{ "lsx", "icon_video" },

			{ "m15", "icon_audio" },
			{ "awb", "icon_audio" },
			{ "au", "icon_audio" },
			{ "aif", "icon_audio" },
			{ "aifc", "icon_audio" },
			{ "aiff", "icon_audio" },
			{ "als", "icon_audio" },
			{ "es", "icon_audio" },
			{ "esl", "icon_audio" },
			{ "imy", "icon_audio" },
			{ "it", "icon_audio" },
			{ "itz", "icon_audio" },
			{ "m3u", "icon_audio" },
			{ "m3url", "icon_audio" },
			{ "ma1", "icon_audio" },
			{ "ma2", "icon_audio" },
			{ "ma3", "icon_audio" },
			{ "ma5", "icon_audio" },
			{ "mdz", "icon_audio" },
			{ "mid", "icon_audio" },
			{ "midi", "icon_audio" },

			{ "ief", "icon_image" },
			{ "ifs", "icon_image" },
			{ "j2k", "icon_image" },
			{ "jpe", "icon_image" },
			{ "jpeg", "icon_image" },
			{ "jpg", "icon_image" },
			{ "jpz", "icon_image" },

			{ "doc", "icon_doc" },
			{ "dot", "icon_doc" },

			{ "apk", "icon_apk" },

			{ "mil", "icon_image" },
			{ "mio", "icon_audio" },
			{ "mng", "icon_video" },
			{ "mod", "icon_audio" },
			{ "mov", "icon_video" },
			{ "movie", "icon_video" },
			{ "mp2", "icon_audio" },
			{ "mp3", "icon_audio" },
			{ "mp4", "icon_video" },
			{ "mpe", "icon_video" },
			{ "mpeg", "icon_video" },
			{ "mpg", "icon_video" },
			{ "mpg4", "icon_video" },
			{ "mpga", "icon_audio" },
			{ "nar", "icon_rar" },
			{ "nbmp", "icon_image" },
			{ "nsnd", "icon_audio" },
			{ "pac", "icon_audio" },
			{ "pae", "icon_audio" },
			// {"pbm", "icon_bmp"},
			{ "pcx", "icon_image" },
			{ "pda", "icon_image" },
			{ "pdf", "icon_pdf" },
			{ "pgm", "icon_image" },
			{ "pict", "icon_image" },
			{ "png", "icon_image" },
			{ "pnm", "icon_image" },
			{ "pnz", "icon_png" },
			{ "pot", "icon_ppt" },
			{ "ppm", "icon_image" },
			{ "pps", "icon_ppt" },
			{ "ppt", "icon_ppt" },
			{ "pvx", "icon_video" },
			{ "qcp", "icon_audio" },
			{ "qt", "icon_video" },
			{ "qti", "icon_image" },
			{ "qtif", "icon_image" },
			{ "ra", "icon_audio" },
			{ "ram", "icon_audio" },
			{ "rar", "icon_rar" },
			{ "ras", "icon_image" },
			{ "rf", "icon_image" },
			{ "rgb", "icon_image" },
			{ "rm", "icon_audio" },
			{ "rmf", "icon_audio" },
			{ "rmm", "icon_audio" },
			{ "rmvb", "icon_audio" },
			{ "rp", "icon_image" },
			{ "rpm", "icon_audio" },
			{ "rv", "icon_video" },
			{ "s3m", "icon_audio" },
			{ "s3z", "icon_audio" },
			{ "shtml", "icon_html" },
			{ "si6", "icon_image" },
			{ "si7", "icon_image" },
			{ "si9", "icon_image" },
			{ "smd", "icon_audio" },
			{ "smz", "icon_audio" },
			{ "snd", "icon_audio" },
			{ "stm", "icon_audio" },
			{ "svf", "icon_image" },
			{ "svg", "icon_image" },
			{ "svh", "icon_image" },
			{ "swf", "icon_flash" },
			{ "swfl", "icon_flash" },
			{ "tar", "icon_rar" },
			{ "taz", "icon_rar" },
			{ "tgz", "icon_rar" },
			{ "toy", "icon_image" },
			{ "tsi", "icon_audio" },
			{ "txt", "icon_doc" },
			{ "ult", "icon_audio" },
			{ "asc", "icon_doc" },
			{ "vox", "icon_audio" },
			{ "vqe", "icon_audio" },
			{ "vqf", "icon_audio" },
			{ "vql", "icon_audio" },
			{ "wav", "icon_audio" },
			{ "wax", "icon_audio" },
			{ "wi", "icon_image" },
			{ "wm", "icon_video" },
			{ "wma", "icon_audio" },
			{ "wmv", "icon_video" },
			{ "wmx", "icon_video" },
			{ "wpng", "icon_image" },
			{ "wv", "icon_video" },
			{ "wvx", "icon_video" },
			{ "xlm", "icon_xls" },
			{ "xls", "icon_xls" },
			{ "xlt", "icon_xls" },
			{ "xlw", "icon_xls" },
			{ "xm", "icon_audio" },
			// {"xml", "icon_xml"},
			{ "xmz", "icon_audio" },
			{ "xpm", "icon_image" },
			// {"xsit", "icon_xml"},
			// {"xsl", "icon_xml"},
			// {"xul", "text/xul"},
			{ "xwd", "icon_image" },
			{ "zip", "icon_rar" },

			{ "aab", "application/x-authoware-bin" },
			{ "aam", "application/x-authoware-map" },
			{ "aas", "application/x-authoware-seg" },
			{ "ai", "application/postscript" },
			{ "amc", "application/x-mpeg" },
			{ "ani", "application/octet-stream" },
			// {"apk", "application/vnd.android.package-archive"},

			{ "asd", "application/astound" },
			{ "asn", "application/astound" },
			{ "asp", "application/x-asap" },
			{ "avb", "application/octet-stream" },
			{ "bcpio", "application/x-bcpio" },
			{ "bin", "application/octet-stream" },
			{ "bld", "application/bld" },
			{ "bld2", "application/bld2" },
			// {"bmp", "icon_bmp"},
			{ "bpk", "application/octet-stream" },
			// {"bz2", "icon_archive"},
			// {"c", "icon_c"},
			// {"cpp", "icon_cpp"},
			{ "cal", "image/x-cals" },
			{ "ccn", "application/x-cnc" },
			{ "cco", "application/x-cocoa" },
			{ "cdf", "application/x-netcdf" },
			{ "cgi", "magnus-internal/cgi" },
			{ "chat", "application/x-chat" },
			{ "class", "application/octet-stream" },
			{ "clp", "application/x-msclip" },
			{ "cmx", "application/x-cmx" },
			{ "co", "application/x-cult3d-object" },
			{ "cod", "image/cis-cod" },
			{ "cpio", "application/x-cpio" },
			{ "cpt", "application/mac-compactpro" },
			{ "crd", "application/x-mscardfile" },
			{ "csh", "application/x-csh" },
			{ "csm", "chemical/x-csml" },
			{ "csml", "chemical/x-csml" },
			// {"css", "icon_css"},
			{ "cur", "application/octet-stream" },
			{ "dcm", "x-lml/x-evm" },
			{ "dcr", "application/x-director" },
			{ "dcx", "image/x-dcx" },
			// {"dhtml", "icon_html"},
			{ "dir", "application/x-director" },
			{ "dll", "application/octet-stream" },
			{ "dmg", "application/octet-stream" },
			{ "dms", "application/octet-stream" },
			{ "dvi", "application/x-dvi" },
			{ "dwf", "drawing/x-dwf" },
			{ "dwg", "application/x-autocad" },
			{ "dxf", "application/x-autocad" },
			{ "dxr", "application/x-director" },
			{ "ebk", "application/x-expandedbook" },
			{ "emb", "chemical/x-embl-dl-nucleotide" },
			{ "embl", "chemical/x-embl-dl-nucleotide" },
			{ "eps", "application/postscript" },
			{ "eri", "image/x-eri" },
			{ "etc", "application/x-earthtime" },
			{ "etx", "text/x-setext" },
			{ "evm", "x-lml/x-evm" },
			{ "evy", "application/x-envoy" },
			{ "exe", "application/octet-stream" },
			{ "fh4", "image/x-freehand" },
			{ "fh5", "image/x-freehand" },
			{ "fhc", "image/x-freehand" },
			{ "fif", "image/fif" },
			{ "fm", "application/x-maker" },
			{ "fpx", "image/x-fpx" },
			{ "gau", "chemical/x-gaussian-input" },
			{ "gca", "application/x-gca-compressed" },
			{ "gdb", "x-lml/x-gdb" },
			// {"gif", "icon_gif"},
			{ "gps", "application/x-gps" },
			{ "gtar", "application/x-gtar" },
			// {"gz", "icon_gzip"},
			// {"h", "icon_c_h"},
			{ "hdf", "application/x-hdf" },
			{ "hdm", "text/x-hdml" },
			{ "hdml", "text/x-hdml" },
			{ "hlp", "application/winhlp" },
			{ "hqx", "application/mac-binhex40" },
			{ "htm", "icon_html" },
			// {"html", "icon_html"},
			// {"hts", "icon_html"},
			{ "ice", "x-conference/x-cooltalk" },
			// {"ico", "icon_ico"},

			// {"ifm", "icon_gif"},

			{ "ins", "application/x-NET-Install" },
			{ "ips", "application/x-ipscript" },
			{ "ipx", "application/x-ipix" },

			{ "ivr", "i-world/i-vrml" },

			{ "jad", "text/vnd.sun.j2me.app-descriptor" },
			{ "jam", "application/x-jam" },
			// {"java", "icon_java"},
			// {"jar", "icon_java"},
			// {"jnlp", "icon_java"},

			{ "js", "icon_javascript" }, { "jwc", "application/jwc" }, { "kjx", "application/x-kjx" }, { "lak", "x-lml/x-lak" },
			{ "latex", "application/x-latex" }, { "lcc", "application/fastman" }, { "lcl", "application/x-digitalloca" },
			{ "lcr", "application/x-digitalloca" }, { "lgh", "application/lgh" }, { "lha", "application/octet-stream" }, { "lml", "x-lml/x-lml" },
			{ "lmlpack", "x-lml/x-lmlpack" },

			{ "lzh", "application/x-lzh" }, { "m13", "application/x-msmediaview" }, { "m14", "application/x-msmediaview" },

			{ "man", "application/x-troff-man" }, { "map", "magnus-internal/imagemap" }, { "mbd", "application/mbedlet" }, { "mct", "application/x-mascot" },
			{ "mdb", "application/x-msaccess" },

			{ "me", "application/x-troff-me" }, { "mel", "text/x-vmel" }, { "mi", "application/x-mif" },

			{ "mif", "application/x-mif" },

			{ "mmf", "application/x-skt-lbs" },

			{ "mny", "application/x-msmoney" }, { "moc", "application/x-mocha" }, { "mocha", "application/x-mocha" },

			{ "mof", "application/x-yumekara" }, { "mol", "chemical/x-mdl-molfile" }, { "mop", "chemical/x-mopac-input" },

			{ "mpc", "application/vnd.mpohun.certificate" },

			{ "mpn", "application/vnd.mophun.application" }, { "mpp", "application/vnd.ms-project" }, { "mps", "application/x-mapserver" },
			{ "mrl", "text/x-mrml" }, { "mrm", "application/x-mrm" }, { "ms", "application/x-troff-ms" }, { "mts", "application/metastream" },
			{ "mtx", "application/metastream" }, { "mtz", "application/metastream" }, { "mzv", "application/metastream" },

			{ "nc", "application/x-netcdf" }, { "ndb", "x-lml/x-ndb" }, { "ndwn", "application/ndwn" }, { "nif", "application/x-nif" },
			{ "nmz", "application/x-scream" }, { "nokia-op-logo", "icon_image" }, { "npx", "application/x-netfpx" },

			{ "nva", "application/x-neva1" }, { "oda", "application/oda" }, { "oom", "application/x-AtlasMate-Plugin" },

			{ "pan", "application/x-pan" },

			{ "pfr", "application/font-tdpfr" },

			{ "pdb", "chemical/x-pdb" },

			{ "pm", "application/x-perl" }, { "pmd", "application/x-pmd" },

			{ "pqf", "application/x-cprplayer" }, { "pqi", "application/cprplayer" }, { "prc", "application/x-prc" },
			{ "proxy", "application/x-ns-proxy-autoconfig" }, { "ps", "application/postscript" }, { "ptlk", "application/listenup" },
			{ "pub", "application/x-mspublisher" },

			{ "r3t", "text/vnd.rn-realtext3d" },

			{ "rdf", "application/rdf+xml" },

			{ "rlf", "application/x-richlink" },

			{ "rnx", "application/vnd.rn-realplayer" }, { "roff", "application/x-troff" },

			{ "rt", "text/vnd.rn-realtext" }, { "rte", "x-lml/x-gps" }, { "rtf", "icon_text_richtext" }, { "rtg", "application/metastream" },
			{ "rtx", "text/richtext" },

			{ "rwc", "application/x-rogerwilco" },

			{ "sca", "application/x-supercard" }, { "scd", "application/x-msschedule" }, { "sdf", "application/e-score" }, { "sea", "application/x-stuffit" },
			{ "sgm", "text/x-sgml" }, { "sgml", "text/x-sgml" }, { "sh", "application/x-sh" }, { "shar", "application/x-shar" },

			{ "shw", "application/presentations" },

			{ "sis", "application/vnd.symbian.install" }, { "sit", "application/x-stuffit" }, { "skd", "application/x-Koan" }, { "skm", "application/x-Koan" },
			{ "skp", "application/x-Koan" }, { "skt", "application/x-Koan" }, { "slc", "application/x-salsa" },

			{ "smi", "application/smil" }, { "smil", "application/smil" }, { "smp", "application/studiom" },

			{ "spc", "text/x-speech" }, { "spl", "application/futuresplash" }, { "spr", "application/x-sprite" }, { "sprite", "application/x-sprite" },
			{ "spt", "application/x-spt" }, { "src", "application/x-wais-source" },
			{ "stk", "application/hyperstudio" },

			{ "sv4cpio", "application/x-sv4cpio" },
			{ "sv4crc", "application/x-sv4crc" },

			{ "svr", "x-world/x-svr" },

			{ "t", "application/x-troff" },
			{ "tad", "application/octet-stream" },
			{ "talk", "text/x-speech" },

			{ "tbp", "application/x-timbuktu" },
			{ "tbt", "application/x-timbuktu" },
			{ "tcl", "application/x-tcl" },
			{ "tex", "application/x-tex" },
			{ "texi", "application/x-texinfo" },
			{ "texinfo", "application/x-texinfo" },

			{ "thm", "application/vnd.eri.thm" },
			// {"tif", "icon_tiff"},
			// {"tiff", "icon_tiff"},
			{ "tki", "application/x-tkined" }, { "tkined", "application/x-tkined" }, { "toc", "application/toc" },

			{ "tr", "application/x-troff" }, { "trk", "x-lml/x-gps" }, { "trm", "application/x-msterminal" },

			{ "tsp", "application/dsptype" }, { "tsv", "text/tab-separated-values" }, { "tsv", "text/tab-separated-values" },
			{ "ttf", "application/octet-stream" }, { "ttz", "application/t-time" },

			{ "ustar", "application/x-ustar" }, { "uu", "application/x-uuencode" }, { "uue", "application/x-uuencode" }, { "vcd", "application/x-cdlink" },
			{ "vcf", "text/x-vcard" }, { "vmd", "application/vocaltec-media-desc" }, { "vmf", "application/vocaltec-media-file" },
			{ "vmi", "application/x-dreamcast-vms-info" }, { "vms", "application/x-dreamcast-vms" },

			{ "vre", "x-world/x-vream" }, { "vrml", "x-world/x-vrml" },
			{ "vrt", "x-world/x-vrt" },
			{ "vrw", "x-world/x-vream" },
			{ "vts", "workbook/formulaone" },

			// {"wbmp", "icon_bmp"},
			{ "web", "application/vnd.xara" },

			{ "wis", "application/x-InstallShield" },

			{ "wmd", "application/x-ms-wmd" }, { "wmf", "application/x-msmetafile" }, { "wml", "text/vnd.wap.wml" }, { "wmlc", "application/vnd.wap.wmlc" },
			{ "wmls", "text/vnd.wap.wmlscript" }, { "wmlsc", "application/vnd.wap.wmlscriptc" }, { "wmlscript", "text/vnd.wap.wmlscript" },

			{ "wmz", "application/x-ms-wmz" },

			{ "wpt", "x-lml/x-gps" }, { "wri", "application/x-mswrite" }, { "wrl", "x-world/x-vrml" }, { "wrz", "x-world/x-vrml" },
			{ "ws", "text/vnd.wap.wmlscript" }, { "wsc", "application/vnd.wap.wmlscriptc" },

			{ "wxl", "application/x-wxl" }, { "x-gzip", "application/x-gzip" }, { "xar", "application/vnd.xara" },
			// {"xbm", "icon_bmp"},
			{ "xdm", "application/x-xdma" }, { "xdma", "application/x-xdma" }, { "xdw", "application/vnd.fujixerox.docuworks" }, { "xht", "icon_xhtml_xml" },
			{ "xhtm", "icon_xhtml_xml" }, { "xhtml", "icon_xhtml_xml" },

			{ "xll", "application/x-excel" },

			{ "xpi", "application/x-xpinstall" },

			{ "xyz", "chemical/x-pdb" }, { "yz1", "application/x-yz1" }, { "z", "application/x-compress" }, { "zac", "application/x-zaurus-zac" }, };
}