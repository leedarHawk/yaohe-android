package com.collcloud.yaohe.ui.utils;

import android.content.Context;
import android.content.Intent;

import com.collcloud.yaohe.activity.webview.WebViewActivity;

/**
 * Activity跳转方法
 * @author 赵洋洋
 * @time 2015-7-1 下午3:36:33
 */
public class IntentUtil {
	/**
	 * 调用WebView
	 * @param context	上下文
	 * @param title		标题
	 * @param url		网址
	 */
	public static void toWebView(Context context, String title, String url){
		Intent mIntent = new Intent(context, WebViewActivity.class);
		mIntent.putExtra(WebViewActivity.TITLE_TAG, title);
		mIntent.putExtra(WebViewActivity.URL_TAG, url);
		context.startActivity(mIntent);
	}

	
}
