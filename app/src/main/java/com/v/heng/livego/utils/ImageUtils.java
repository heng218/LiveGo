package com.v.heng.livego.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.DisplayMetrics;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 图片操作工具包
 */
public class ImageUtils {

	public final static String SDCARD_MNT = "/mnt/sdcard";
	public final static String SDCARD = "/sdcard";

	/** 请求相册 */
	public static final int REQUEST_CODE_GETIMAGE_BYSDCARD = 0;
	/** 请求相机 */
	public static final int REQUEST_CODE_GETIMAGE_BYCAMERA = 1;
	/** 请求裁剪 */
	public static final int REQUEST_CODE_GETIMAGE_BYCROP = 2;

	/**
	 * 写图片文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
	 * 
	 * @throws IOException
	 */
	public static void saveImage(Context context, String fileName, Bitmap bitmap) throws IOException {
		saveImage(context, fileName, bitmap, 100);
	}

	public static void saveImage(Context context, String fileName, Bitmap bitmap, int quality) throws IOException {
		if (bitmap == null || fileName == null || context == null)
			return;

		FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, quality, stream);
		byte[] bytes = stream.toByteArray();
		fos.write(bytes);
		fos.close();
	}

	/**
	 * 写图片文件到SD卡
	 * 
	 * @throws IOException
	 */
	public static void saveImageToSD(String filePath, Bitmap bitmap, int quality) throws IOException {
		if (bitmap != null) {
			FileOutputStream fos = new FileOutputStream(filePath);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, quality, stream);
			byte[] bytes = stream.toByteArray();
			fos.write(bytes);
			fos.close();
		}
	}

	/**
	 * 获取bitmap
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	public static Bitmap getBitmap(Context context, String fileName) {
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			fis = context.openFileInput(fileName);
			bitmap = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return bitmap;
	}

	/**
	 * 获取bitmap
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap getBitmapByPath(String filePath) {
		return getBitmapByPath(filePath, null);
	}

	public static Bitmap getBitmapByPath(String filePath, BitmapFactory.Options opts) {
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			File file = new File(filePath);
			fis = new FileInputStream(file);
			bitmap = BitmapFactory.decodeStream(fis, null, opts);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return bitmap;
	}

	/**
	 * 获取bitmap
	 * 
	 * @param file
	 * @return
	 */
	public static Bitmap getBitmapByFile(File file) {
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			fis = new FileInputStream(file);
			bitmap = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return bitmap;
	}

	/**
	 * 使用当前时间戳拼接一个唯一的文件名
	 * 
	 * @return
	 */
	public static String getTempFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SS", Locale.CHINESE);
		String fileName = format.format(new Timestamp(System.currentTimeMillis()));
		return fileName;
	}

	/**
	 * 获取照相机使用的目录
	 * 
	 * @return
	 */
	public static String getCamerPath() {
		return Environment.getExternalStorageDirectory() + File.separator + "FounderNews" + File.separator;
	}

	/**
	 * 判断当前Url是否标准的content://样式，如果不是，则返回绝对路径
	 * 
	 * @return
	 */
	public static String getAbsolutePathFromNoStandardUri(Uri mUri) {
		String filePath = null;

		String mUriString = mUri.toString();
		mUriString = Uri.decode(mUriString);

		String pre1 = "file://" + SDCARD + File.separator;
		String pre2 = "file://" + SDCARD_MNT + File.separator;

		if (mUriString.startsWith(pre1)) {
			filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + mUriString.substring(pre1.length());
		} else if (mUriString.startsWith(pre2)) {
			filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + mUriString.substring(pre2.length());
		}
		return filePath;
	}

	/**
	 * 通过uri获取文件的绝对路径
	 * 
	 * @param uri
	 * @return
	 */
	public static String getAbsoluteImagePath(Activity context, Uri uri) {
		String imagePath = "";
		String[] proj = { Images.Media.DATA };
		Cursor cursor = context.managedQuery(uri, proj, // Which columns to
														// return
				null, // WHERE clause; which rows to return (all rows)
				null, // WHERE clause selection arguments (none)
				null); // Order-by clause (ascending by name)

		if (cursor != null) {
			int column_index = cursor.getColumnIndexOrThrow(Images.Media.DATA);
			if (cursor.getCount() > 0 && cursor.moveToFirst()) {
				imagePath = cursor.getString(column_index);
			}
		}

		return imagePath;
	}

	/**
	 * 获取图片缩略图 只有Android2.1以上版本支持
	 * 
	 * @param imgName
	 * @param kind
	 *            MediaStore.Images.Thumbnails.MICRO_KIND
	 * @return
	 */
	public static Bitmap loadImgThumbnail(Activity context, String imgName, int kind) {
		Bitmap bitmap = null;

		String[] proj = { Images.Media._ID, Images.Media.DISPLAY_NAME };

		Cursor cursor = context.managedQuery(Images.Media.EXTERNAL_CONTENT_URI, proj, Images.Media.DISPLAY_NAME + "='" + imgName + "'",
				null, null);

		if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
			ContentResolver crThumb = context.getContentResolver();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			bitmap =MediaStore.Images.Thumbnails.getThumbnail(crThumb,cursor.getInt(0),kind, options);
		}
		return bitmap;
	}

	public static Bitmap loadImgThumbnail(String filePath, int w, int h) {
		Bitmap bitmap = getBitmapByPath(filePath);
		return zoomBitmap(bitmap, w, h);
	}

	/**
	 * 获取SD卡中最新图片路径
	 * 
	 * @return
	 */
	public static String getLatestImage(Activity context) {
		String latestImage = null;
		String[] items = { Images.Media._ID, Images.Media.DATA };
		Cursor cursor = context.managedQuery(Images.Media.EXTERNAL_CONTENT_URI, items, null, null, Images.Media._ID + " desc");

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				latestImage = cursor.getString(1);
				break;
			}
		}

		return latestImage;
	}

	/**
	 * 计算缩放图片的宽高
	 * 
	 * @param img_size
	 * @param square_size
	 * @return
	 */
	public static int[] scaleImageSize(int[] img_size, int square_size) {
		if (img_size[0] <= square_size && img_size[1] <= square_size)
			return img_size;
		double ratio = square_size / (double) Math.max(img_size[0], img_size[1]);
		return new int[] { (int) (img_size[0] * ratio), (int) (img_size[1] * ratio) };
	}

	/**
	 * 创建缩略图
	 * 
	 * @param context
	 * @param largeImagePath
	 *            原始大图路径
	 * @param thumbfilePath
	 *            输出缩略图路径
	 * @param square_size
	 *            输出图片宽度
	 * @param quality
	 *            输出图片质量
	 * @throws IOException
	 */
	public static void createImageThumbnail(Context context, String largeImagePath, String thumbfilePath, int square_size, int quality) throws IOException {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 1;
		// 原始图片bitmap
		Bitmap cur_bitmap = getBitmapByPath(largeImagePath, opts);

		if (cur_bitmap == null)
			return;

		// 原始图片的高宽
		int[] cur_img_size = new int[] { cur_bitmap.getWidth(), cur_bitmap.getHeight() };
		// 计算原始图片缩放后的宽高
		int[] new_img_size = scaleImageSize(cur_img_size, square_size);
		// 生成缩放后的bitmap
		Bitmap thb_bitmap = zoomBitmap(cur_bitmap, new_img_size[0], new_img_size[1]);
		// 生成缩放后的图片文件
		saveImageToSD(thumbfilePath, thb_bitmap, quality);
	}

	/**
	 * 缩放图片（用比例）
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap zoom(Bitmap bitmap, float zf) {
		Matrix matrix = new Matrix();
		matrix.postScale(zf, zf);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	/**
	 * 缩放图片（用比例）
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap zoom(Bitmap bitmap, float wf, float hf) {
		Matrix matrix = new Matrix();
		matrix.postScale(wf, hf);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	public static Bitmap scaleBitmap(Bitmap bitmap) {
		// 获取这个图片的宽和高
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// 定义预转换成的图片的宽度和高度
		int newWidth = 200;
		int newHeight = 200;
		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		// 旋转图片 动作
		// matrix.postRotate(45);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		return resizedBitmap;
	}

	/**
	 * (缩放)重绘图片
	 * 
	 * @param context
	 *            Activity
	 * @param bitmap
	 * @return
	 */
	public static Bitmap reDrawBitMap(Activity context, Bitmap bitmap) {
		DisplayMetrics dm = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int rHeight = dm.heightPixels;
		int rWidth = dm.widthPixels;
		// float rHeight=dm.heightPixels/dm.density+0.5f;
		// float rWidth=dm.widthPixels/dm.density+0.5f;
		// int height=bitmap.getScaledHeight(dm);
		// int width = bitmap.getScaledWidth(dm);
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();
		float zoomScale;
		/** 方式1 **/
		// if(rWidth/rHeight>width/height){//以高为准
		// zoomScale=((float) rHeight) / height;
		// }else{
		// //if(rWidth/rHeight<width/height)//以宽为准
		// zoomScale=((float) rWidth) / width;
		// }
		/** 方式2 **/
		// if(width*1.5 >= height) {//以宽为准
		// if(width >= rWidth)
		// zoomScale = ((float) rWidth) / width;
		// else
		// zoomScale = 1.0f;
		// }else {//以高为准
		// if(height >= rHeight)
		// zoomScale = ((float) rHeight) / height;
		// else
		// zoomScale = 1.0f;
		// }
		/** 方式3 **/
		if (width >= rWidth)
			zoomScale = ((float) rWidth) / width;
		else
			zoomScale = 1.0f;
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 缩放图片动作
		matrix.postScale(zoomScale, zoomScale);
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * 将Drawable转化为Bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;

	}

	/**
	 * 获得圆角图片的方法
	 * 
	 * @param bitmap
	 * @param roundPx
	 *            一般设成14
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 获得带倒影的图片方法
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2, width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff,
				TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);

		return bitmapWithReflection;
	}

	/**
	 * 将bitmap转化为drawable
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Drawable bitmapToDrawable(Bitmap bitmap) {
		Drawable drawable = new BitmapDrawable(bitmap);
		return drawable;
	}

	/**
	 * 获取图片类型
	 * 
	 * @param file
	 * @return
	 */
	public static String getImageType(File file) {
		if (file == null || !file.exists()) {
			return null;
		}
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			String type = getImageType(in);
			return type;
		} catch (IOException e) {
			return null;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * detect bytes's image type by inputstream
	 * 
	 * @param in
	 * @return
	 * @see #getImageType(byte[])
	 */
	public static String getImageType(InputStream in) {
		if (in == null) {
			return null;
		}
		try {
			byte[] bytes = new byte[8];
			in.read(bytes);
			return getImageType(bytes);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * detect bytes's image type
	 * 
	 * @param bytes
	 *            2~8 byte at beginning of the image file
	 * @return image mimetype or null if the file is not image
	 */
	public static String getImageType(byte[] bytes) {
		if (isJPEG(bytes)) {
			return "image/jpeg";
		}
		if (isGIF(bytes)) {
			return "image/gif";
		}
		if (isPNG(bytes)) {
			return "image/png";
		}
		if (isBMP(bytes)) {
			return "application/x-bmp";
		}
		return null;
	}

	private static boolean isJPEG(byte[] b) {
		if (b.length < 2) {
			return false;
		}
		return (b[0] == (byte) 0xFF) && (b[1] == (byte) 0xD8);
	}

	private static boolean isGIF(byte[] b) {
		if (b.length < 6) {
			return false;
		}
		return b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8' && (b[4] == '7' || b[4] == '9') && b[5] == 'a';
	}

	private static boolean isPNG(byte[] b) {
		if (b.length < 8) {
			return false;
		}
		return (b[0] == (byte) 137 && b[1] == (byte) 80 && b[2] == (byte) 78 && b[3] == (byte) 71 && b[4] == (byte) 13 && b[5] == (byte) 10
				&& b[6] == (byte) 26 && b[7] == (byte) 10);
	}

	private static boolean isBMP(byte[] b) {
		if (b.length < 2) {
			return false;
		}
		return (b[0] == 0x42) && (b[1] == 0x4d);
	}

	public static Bitmap getNetBitmap(String strUrl) {
		Bitmap bitmap = null;

		try {
			URL url = new URL(strUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoInput(true);
			con.connect();
			InputStream in = con.getInputStream();
			bitmap = BitmapFactory.decodeStream(in);
			// FileOutputStream out = new FileOutputStream(file.getPath());
			// bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			// out.flush();
			// out.close();
			// in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
		return bitmap;
	}

	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// 源图片的高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			// 计算出实际宽高和目标宽高的比率
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
			// 一定都会大于等于目标的宽和高。
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	public static byte[] getPicThumbnail(String filePath) {

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
		int height = options.outHeight * 100 / options.outWidth;
		options.outWidth = 100;
		options.outHeight = height;
		/* 这样才能真正的返回一个Bitmap给你 */
		options.inJustDecodeBounds = false;
		// 默认是
		options.inPreferredConfig = Config.RGB_565;
		/* 下面两个字段需要组合使用 */
		options.inPurgeable = true;
		options.inInputShareable = true;
		// bmp = BitmapFactory.decodeFile(filePath, options);

		// Bitmap bitmap = ThumbnailUtils.extractThumbnail(bmp, 100, height);
		Bitmap bitmap = decodeThumbBitmapForFile(filePath, 100, 100);
		if (bitmap == null) {
			return new byte[0];
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 30, baos);
		return baos.toByteArray();
	}

	public static byte[] getThumbnailByData(byte[] data) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
		int height = options.outHeight * 100 / options.outWidth;
		options.outWidth = 100;
		options.outHeight = height;
		/* 这样才能真正的返回一个Bitmap给你 */
		options.inJustDecodeBounds = false;
		// 默认是
		options.inPreferredConfig = Config.ARGB_8888;
		/* 下面两个字段需要组合使用 */
		options.inPurgeable = true;
		options.inInputShareable = true;
		// bmp = BitmapFactory.decodeFile(filePath, options);

		// Bitmap bitmap = ThumbnailUtils.extractThumbnail(bmp, 100, height);
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
		if (bitmap == null) {
			return new byte[0];
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * 根据View(主要是ImageView)的宽和高来获取图片的缩略图
	 * 
	 * @param path
	 * @param viewWidth
	 * @param viewHeight
	 * @return
	 */
	public static Bitmap decodeThumbBitmapForFile(String path, int viewWidth, int viewHeight) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		// 设置为true,表示解析Bitmap对象，该对象不占内存
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		// 设置缩放比例
		options.inSampleSize = computeScale(options, viewWidth, viewHeight);

		// 设置为false,解析Bitmap对象加入到内存中
		options.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(path, options);
	}

	/**
	 * 根据View(主要是ImageView)的宽和高来计算Bitmap缩放比例。默认不缩放
	 * 
	 * @param options
	 */
	protected static int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight) {
		int inSampleSize = 1;
		if (viewWidth == 0 || viewWidth == 0) {
			return inSampleSize;
		}
		int bitmapWidth = options.outWidth;
		int bitmapHeight = options.outHeight;

		// 假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例
		if (bitmapWidth > viewWidth || bitmapHeight > viewWidth) {
			int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
			int heightScale = Math.round((float) bitmapHeight / (float) viewWidth);

			// 为了保证图片不缩放变形，我们取宽高比例最小的那个
			inSampleSize = widthScale < heightScale ? widthScale : heightScale;
		}
		return inSampleSize;
	}

	/**
	 * 扫描系统盘
	 * 
	 * @param filePath
	 * @param context
	 */
	public static void scanPhotos(String filePath, Context context) {
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
		// Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		// Uri uri = Uri.fromFile(new File(filePath));
		// intent.setData(uri);
		// context.sendBroadcast(intent);
	}

	public static void sdScan(String filePath, Context context) {
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + filePath)));
	}

	public static void broadcastFile(final Context context, final File file, final boolean is_new_picture, final boolean is_new_video) {
		// note that the new method means that the new folder shows up as a file when connected to a PC via MTP (at least tested on Windows 8)
		if (file.isDirectory()) {
			// context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file)));
			// // ACTION_MEDIA_MOUNTED no longer allowed on Android 4.4! Gives: SecurityException: Permission Denial: not allowed to send broadcast
			// // android.intent.action.MEDIA_MOUNTED
			// // note that we don't actually need to broadcast anything, the folder and contents appear straight away (both in Gallery on device, and on a PC
			// when
			// // connecting via MTP)
			// // also note that we definitely don't want to broadcast ACTION_MEDIA_SCANNER_SCAN_FILE or use scanFile() for folders, as this means the folder
			// shows
			// // up as a file on a PC via MTP (and isn't fixed by rebooting!)
		} else {
			//
			// // both of these work fine, but using MediaScannerConnection.scanFile() seems to be preferred over sending an intent
			// context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
			// // failed_to_scan = true; // set to true until scanned okay
			// MediaScannerConnection.scanFile(context, new String[] { file.getAbsolutePath() }, null, new MediaScannerConnection.OnScanCompletedListener() {
			// public void onScanCompleted(String path, Uri uri) {
			// if (is_new_picture) {
			// context.sendBroadcast(new Intent(Camera.ACTION_NEW_PICTURE, uri));
			// // for compatibility with some apps - apparently this is what used to be broadcast on Android?
			// context.sendBroadcast(new Intent("com.android.camera.NEW_PICTURE", uri));
			// //
			// if (true) // this code only used for debugging/logging
			// {
			// String[] CONTENT_PROJECTION = { Images.Media.DATA, Images.Media.DISPLAY_NAME, Images.Media.MIME_TYPE, Images.Media.SIZE };
			// Cursor c = context.getContentResolver().query(uri, CONTENT_PROJECTION, null, null, null);
			// if (c == null) {
			// } else if (!c.moveToFirst()) {
			// } else {
			// String file_path = c.getString(c.getColumnIndex(Images.Media.DATA));
			// String file_name = c.getString(c.getColumnIndex(Images.Media.DISPLAY_NAME));
			// String mime_type = c.getString(c.getColumnIndex(Images.Media.MIME_TYPE));
			// c.close();
			// // failed_to_scan = false;
			// }
			// }
			// } else if (is_new_video) {
			// context.sendBroadcast(new Intent(Camera.ACTION_NEW_VIDEO, uri));
			// // failed_to_scan = true;
			// } else {
			// // failed_to_scan = true;
			// }
			// }
			// });

			ContentValues values = new ContentValues();
			values.put(ImageColumns.TITLE, file.getName().substring(0, file.getName().lastIndexOf(".")));
			values.put(ImageColumns.DISPLAY_NAME, file.getName());
			values.put(ImageColumns.DATE_TAKEN, System.currentTimeMillis());
			values.put(ImageColumns.MIME_TYPE, "image/jpeg");
			values.put(ImageColumns.DATA, file.getAbsolutePath());
			// Location location = preview.getLocation();
			// if (location != null) {
			// values.put(ImageColumns.LATITUDE, location.getLatitude());
			// values.put(ImageColumns.LONGITUDE, location.getLongitude());
			// }
			try {
				context.getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values);
			} catch (Throwable e) {
				System.out.println(e);
				// Log.e(TAG, "Failed to write MediaStore" + th);
			}

		}
	}

	/**
	 * 删除媒体库 指定图片的信息
	 * 
	 * @param filePath
	 *            文件的绝对路径
	 * @param context
	 *            上下文
	 */

	public static void updateImagesMedia(final Context context, final String filePath) {
		try {
			context.getContentResolver().delete(Images.Media.EXTERNAL_CONTENT_URI, ImageColumns.DATA + "=?", new String[] { filePath });
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * 根据图片字节数组，对图片可能进行二次采样，不致于加载过大图片出现内存溢出
	 * 
	 * @param bytes
	 * @return
	 */
	public static Bitmap getBitmapByBytes(byte[] bytes) {

		// 对于图片的二次采样,主要得到图片的宽与高
		int width = 0;
		int height = 0;
		int sampleSize = 1; // 默认缩放为1
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 仅仅解码边缘区域
		// 如果指定了inJustDecodeBounds，decodeByteArray将返回为空
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
		// 得到宽与高
		height = options.outHeight;
		width = options.outWidth;

		// 图片实际的宽与高，根据默认最大大小值，得到图片实际的缩放比例
		// 假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例

		while ((height / sampleSize > 200) || (width / sampleSize > 200)) {
			sampleSize *= 2;
		}

		// 不再只加载图片实际边缘
		options.inJustDecodeBounds = false;
		// 并且制定缩放比例
		options.inSampleSize = sampleSize;
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
	}

	public static byte[] getThumbByBytes(byte[] data) {
		Bitmap bitmap = getBitmapByBytes(data);
		if (bitmap == null) {
			return null;
		}
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 100, bStream);
		byte[] bytes = bStream.toByteArray();
		return bytes;
	}

	/**
	 * 放大缩小图片
	 * 
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		Bitmap newbmp = null;
		if (bitmap != null) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			Matrix matrix = new Matrix();
			float scaleWidht = ((float) w / width);
			float scaleHeight = ((float) h / height);
			matrix.postScale(scaleWidht, scaleHeight);
			newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		}
		return newbmp;
	}

	public static byte[] compressImageFromFile(byte[] bytes) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;// 只读边,不读内容
		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 800f;//
		float ww = 480f;//
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置采样率

		newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

		bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, newOpts);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	// 设置图片透明度
	public static Bitmap getTransparentBitmap(Bitmap sourceImg, int number) {
		int[] argb = new int[sourceImg.getWidth() * sourceImg.getHeight()];

		sourceImg.getPixels(argb, 0, sourceImg.getWidth(), 0, 0, sourceImg

		.getWidth(), sourceImg.getHeight());// 获得图片的ARGB值

		number = number * 255 / 100;

		for (int i = 0; i < argb.length; i++) {

			argb[i] = (number << 24) | (argb[i] & 0x00FFFFFF);

		}

		sourceImg = Bitmap.createBitmap(argb, sourceImg.getWidth(), sourceImg

		.getHeight(), Config.ARGB_8888);

		return sourceImg;
	}

	// // 图片剪切
	// public static Bitmap cutBitmap(Bitmap mBitmap, Bitmap.Config config) {
	//
	// if (mBitmap == null) {
	// return mBitmap;
	// }
	// int width1 = mBitmap.getWidth();
	// int height1 = mBitmap.getHeight();
	// int top = 0;
	// int wh = 0;
	// Rect rect = null;
	// if (width1 < height1) {
	// top = (height1 - width1) / 2;
	// wh = width1;
	// rect = new Rect(0, top, wh, wh);
	// } else {
	// top = (width1 - height1) / 2;
	// wh = height1;
	// rect = new Rect(top, 0, wh, wh);
	// }
	// if (wh < 0) {
	// wh = 80;
	// rect = new Rect(top, 0, wh, wh);
	// }
	// int width = rect.width();
	// int height = rect.height();
	// if (width < 80) {
	// width = 80;
	// }
	// if (height < 80) {
	// height = 80;
	// }
	//
	// Bitmap croppedImage = Bitmap.createBitmap(width, height, config);
	// Canvas cvs = new Canvas(croppedImage);
	// Rect dr = new Rect(0, 0, width, height);
	// cvs.drawBitmap(mBitmap, rect, dr, null);
	// return croppedImage;
	// }
}
