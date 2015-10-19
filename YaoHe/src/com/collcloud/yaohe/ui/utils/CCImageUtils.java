package com.collcloud.yaohe.ui.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class CCImageUtils {
	/**
	 * 根据图片的比例改变ImageView的大小
	 * 
	 * @param context
	 * @param view
	 * @param rw
	 */
	public static void resizeImageView(Context context, ImageView view, int rw) {
		int w = view.getDrawable().getIntrinsicWidth();
		int h = view.getDrawable().getIntrinsicHeight();
		int rh = rw * h / w;

		LayoutParams params = view.getLayoutParams();
		params.width = rw;
		params.height = rh;
		view.setLayoutParams(params);
	}

	/**
	 * 检测图片url地址所指向的是否为视频文件
	 * 
	 * @param url
	 * @return
	 */
	public static boolean checkImageUrlIsVedio(String url) {
		String str = url.substring(url.length() - 4);
		String jpegStr = url.substring(url.length() - 5);

		return !(".jpg".equals(str) || ".png".equals(str) || ".jpeg".equals(jpegStr));
	}

	/**
	 * 获得屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/**
	 * 获得屏幕宽度
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}
}
