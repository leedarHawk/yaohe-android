package com.collcloud.yaohe.ui.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.TextPaint;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.GlobalConstant;
import com.collcloud.yaohe.ui.view.SingleLayoutListView;

/**
 * 工具类集合
 * 
 * @ClassName Utils
 * @Description 本程序需要的工具类集合
 * @author CollCloud_小米
 */
public class Utils {

	/**
	 * 文字太长了，点击显示全部
	 * 
	 * @param context
	 *            Toast形式表现
	 * @param textView
	 */
	public static void canShowComplete(final Context context,
			final TextView textView) {
		if (textView == null) {
			return;
		}

		textView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				TextPaint paint = textView.getPaint();
				float textWidth = Layout.getDesiredWidth(textView.getText(),
						paint);
				int textViewWidth = textView.getMeasuredWidth();

				if (textWidth > textViewWidth) {
					Toast toast = Toast.makeText(context, textView.getText(),
							Toast.LENGTH_SHORT);
					toast.show();
				}
			}
		});
	}

	public static int getStatusHeight(Activity activity) {
		int statusHeight = 0;
		Rect localRect = new Rect();
		activity.getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(localRect);
		statusHeight = localRect.top;
		if (0 == statusHeight) {
			Class<?> localClass;
			try {
				localClass = Class.forName("com.android.internal.R$dimen");
				Object localObject = localClass.newInstance();
				int i5 = Integer.parseInt(localClass
						.getField("status_bar_height").get(localObject)
						.toString());
				statusHeight = activity.getResources()
						.getDimensionPixelSize(i5);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
		return statusHeight;
	}

	/**
	 * EditText是否为空的判断
	 */
	public static boolean isViewEmpty(TextView view) {

		if (isStringEmpty(strFromView(view))) {
			return true;
		}
		return false;
	}

	/**
	 * 从TextView或者EditText组件中获得内容
	 */
	public static String strFromView(View view) {
		String strText = "";
		if (null != view) {
			if ((view instanceof TextView)) {
				strText = ((TextView) view).getText().toString().trim();
			} else if (view instanceof EditText) {
				strText = ((EditText) view).getText().toString().trim();
			}
		}
		return strText;
	}

	/**
	 * 两个string 是否相等
	 */
	public static boolean isEqual(String str1, String str2) {
		if (null == str2) {
			return false;
		}
		if (str1.equals(str2)) {
			return true;
		}
		return false;
	}

	/**
	 * String 是否为空判断
	 */
	public static boolean isStringEmpty(String str) {

		if (null == str || "".equals(str) || "null".equals(str)) {
			return true;
		}
		return false;
	}

	/**
	 * bitmap To base64
	 * 
	 * @param bitmap
	 * @return
	 */
	public static String bitmapToBase64(Bitmap bitmap) {

		String result = null;
		ByteArrayOutputStream byteArrayoutStream = null;
		try {
			if (bitmap != null) {
				byteArrayoutStream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
						byteArrayoutStream);

				byteArrayoutStream.flush();
				byteArrayoutStream.close();

				byte[] bitmapBytes = byteArrayoutStream.toByteArray();
				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);

			}
		} catch (IOException e) {
		} finally {
			try {
				if (byteArrayoutStream != null) {
					byteArrayoutStream.flush();
					byteArrayoutStream.close();
				}
			} catch (IOException e) {
			}
		}
		return result;
	}

	/**
	 * base64 To bitmap
	 */
	public static Bitmap base64ToBitmap(String base64Data) {
		Bitmap bitmap = null;
		try {
			byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		} catch (OutOfMemoryError e) {
		} catch (IllegalArgumentException e) {
		}

		return bitmap;
	}

	public static Bitmap stringToBitmap(String data) {
		Bitmap bitmap = null;
		try {
			byte[] bytes = data.getBytes("UTF-8");
			bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		} catch (OutOfMemoryError e) {
		} catch (IllegalArgumentException e) {
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	/**
	 * UTF-8格式化
	 */
	public static String strURLEncoder(String strValue) {
		String newValue = "";
		try {
			newValue = URLEncoder.encode(strValue, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		return newValue;
	}

	public static boolean isAppRunning(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfoList = activityManager
				.getRunningTasks(100);
		boolean isAppRunning = false;
		for (RunningTaskInfo runningTaskInfo : runningTaskInfoList) {
			if (runningTaskInfo.topActivity.getPackageName().equals(
					GlobalConstant.PACKAGE_NAME)
					|| runningTaskInfo.baseActivity.getPackageName().equals(
							GlobalConstant.PACKAGE_NAME)) {
				isAppRunning = true;
				break;
			}
		}
		return isAppRunning;
	}

	/**
	 * Returns a list of application processes that are running on the device
	 * 
	 * @return true : App running onForeground <br>
	 *         false : App not running onForeground <br>
	 */
	public static boolean isAppOnForeground(Context context) {
		boolean isAppOnForeground = false;
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null) {
			isAppOnForeground = false;
		}
		for (RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(GlobalConstant.PACKAGE_NAME)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				isAppOnForeground = true;
			}
		}

		return isAppOnForeground;
	}

	/**
	 * 
	 * drawable 对象转为 bitmap
	 * 
	 * @param drawable
	 * @return　Bitmap
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {

		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * 保存图片 bitmap
	 * 
	 * @param picName
	 *            　　file name
	 * @param bm
	 *            bitmap
	 */
	public static void saveBitmap(String picName, Bitmap bm) {
		File f = new File(GlobalConstant.DESIGN_DEFAULT_IMAGE_PATH);
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	/**
	 * 从文件路径下取得图片
	 * 
	 * @param filepath
	 * @return bitmap
	 */
	public static Bitmap getImageFromSDCard(String filepath) {
		File file = new File(filepath);
		if (file.exists()) {
			Bitmap bm = BitmapFactory.decodeFile(filepath);
			return bm;
		}
		return null;
	}

	/**
	 * format expires date
	 * 
	 * @param strExpires
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String formatExpires(String strExpires) {
		Date date = new Date(strExpires);
		return String.valueOf(date.getTime());
	}

	/**
	 * 判断数组是否越界
	 * 
	 * @param mCookieValues
	 * @return
	 */
	public static boolean isArrayIndexOutOfBounds(String[] mCookieValues) {
		return mCookieValues.length > 1;
	}

	/**
	 * 日期格式化
	 * 
	 * @param strNoticeDate
	 * @param oldFormat
	 * @param newFormat
	 * @return
	 */

	public static String formatDate(String strNoticeDate, String oldFormat,
			String newFormat) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(oldFormat);
			SimpleDateFormat sdf2 = new SimpleDateFormat(newFormat);
			strNoticeDate = sdf2.format(sdf.parse(strNoticeDate));
		} catch (ParseException e) {
		}
		return strNoticeDate;
	}

	private static long lastClickTime;

	/**
	 * 快速连击
	 */
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	/**
	 * 文件最后的修改时间
	 */
	public static String getFileLastModifiedTime(String filePath) {
		String path = filePath.toString();
		File f = new File(path);
		Calendar cal = Calendar.getInstance();
		long time = f.lastModified();
		SimpleDateFormat formatter = new SimpleDateFormat(
				GlobalConstant.PUSH_DATE_FOEMAT + " "
						+ GlobalConstant.NOTICE_TIME_FOEMAT);
		cal.setTimeInMillis(time);

		return formatter.format(cal.getTime());
	}

	/**
	 * 判断指定的文件名是否存在
	 */
	public static boolean fileIsExists(String filePath) {
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static Drawable bitmapToDrawable(Resources resources, Bitmap bitmap) {
		BitmapDrawable bitmapDrawable = new BitmapDrawable(resources, bitmap);
		Drawable drawable = bitmapDrawable.getCurrent();
		return drawable;
	}

	// use for camera
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static Point getScreenMetrics(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int w_screen = dm.widthPixels;
		int h_screen = dm.heightPixels;
		return new Point(w_screen, h_screen);
	}

	public static float getScreenRate(Context context) {
		Point P = getScreenMetrics(context);
		float H = P.y;
		float W = P.x;
		return (H / W);
	}

	public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree) {
		Matrix matrix = new Matrix();
		matrix.postRotate((float) rotateDegree);
		Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
				b.getHeight(), matrix, false);
		return rotaBitmap;
	}

	// use for camera
	public static byte[] compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		while (baos.toByteArray().length > 1024 * 500) {
			baos.reset();
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
			options -= 1;
		}
		return baos.toByteArray();
	}

	/**
	 * bytes to bitmap
	 */
	public static Bitmap bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	public static String getCheckResult(boolean isChecked) {
		String checkMsg = GlobalConstant.EMPTY_STRING;
		if (isChecked) {
			checkMsg = GlobalConstant.VALID_VALUE;
		} else {
			checkMsg = GlobalConstant.INVALID_VALUE;
		}
		return checkMsg;
	}

	public static void resetListViewHeightBasedOnChildren(
			SingleLayoutListView listView) {
		if (listView == null) {
			return;
		}
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	public static String resetCity(String lbsCity) {
		String reset = GlobalConstant.EMPTY_STRING;
		if (!isStringEmpty(lbsCity)) {
			if (lbsCity.contains("市")) {
				reset = lbsCity.replace("市", "");
			} else {
				reset = lbsCity;
			}
		}
		return reset;
	}

	public static String resetDistrict(String district) {
		String reset = GlobalConstant.EMPTY_STRING;
		if (!isStringEmpty(district)) {
			if (district.contains("区")) {
				reset = district.replace("区", "");
			} else {
				reset = district;
			}
		}
		return reset;
	}

	// 查询区域，商圈信息
	/**
	 * 通过区域ID- 查找对应的区域名称
	 * 
	 * @Title queryAreaName
	 * @Description
	 * @param 区域id
	 * @return 区域名称
	 */
	public static String queryAreaName(String areaId) {
		String area = "";
		if (Utils.isStringEmpty(areaId)) {
			return area;
		}
		if (AppApplacation.sAreaList != null
				&& AppApplacation.sAreaList.size() > 0) {
			for (int i = 0; i < AppApplacation.sAreaList.size(); i++) {

				if (areaId.equals(AppApplacation.sAreaList.get(i).id)) {
					area = AppApplacation.sAreaList.get(i).title;
					break;
				}
			}
		}
		return area;
	}

	/**
	 * 通过商圈ID- 查找对应的商圈名称
	 * 
	 * @Title queryDistrictName
	 * @Description
	 * @param 商圈id
	 * @return 商圈名称
	 */
	public static String queryDistrictName(String districtId) {
		String district = "";
		if (Utils.isStringEmpty(districtId)) {
			return district;
		}
		if (AppApplacation.sAreaList != null
				&& AppApplacation.sAreaList.size() > 0) {
			for (int i = 0; i < AppApplacation.sDistrictList.size(); i++) {
				if (districtId.equals(AppApplacation.sDistrictList.get(i).id)) {
					district = AppApplacation.sDistrictList.get(i).title;
					break;
				}
			}
		}
		return district;
	}
	/**
	 * [获取应用程序版本名称信息]
	 * 
	 * @param context
	 * @return 当前应用的版本名称
	 */
	public static String getVersionName(Context context) {
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionName;

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
