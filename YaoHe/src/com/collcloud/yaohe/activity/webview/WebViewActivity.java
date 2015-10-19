package com.collcloud.yaohe.activity.webview;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;

public class WebViewActivity extends BaseActivity implements OnClickListener {

	protected Handler mHandler;

	protected WebView mWebView;
	/** 传递要加载的url时的key */
	public static final String URL_TAG = "url";
	/** 传递要加载的title时的key */
	public static final String TITLE_TAG = "title";

	private static final int MESSAGE_BACK = 0;
	private static final int MESSAGE_EXIT = 1;
	private ImageView tv_actionbarback;
	private TextView tv_actionbartitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);

		String url = getIntent().getStringExtra(URL_TAG);
		// initHandler();
		initView();

		if (!TextUtils.isEmpty(url)) {
			mWebView.loadUrl(url);
		}
	}

	/**
	 * 初始化View
	 */
	private void initView() {
		tv_actionbarback = (ImageView) findViewById(R.id.tv_actionbarback);
		tv_actionbarback.setOnClickListener(this);
		tv_actionbartitle = (TextView) findViewById(R.id.tv_actionbartitle);
		tv_actionbartitle.setText(getIntent().getStringExtra(TITLE_TAG));
		mWebView = (WebView) findViewById(R.id.webView_);
		initWebView();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		// mWebView.setInitialScale(100);
		WebSettings settings = mWebView.getSettings();
		// 设置webview界面在拖动时, 显示放大和缩小的按钮
		settings.setBuiltInZoomControls(true);
		// 设置webview双击时, 放大和缩小的功能
		settings.setUseWideViewPort(true);
		// settings.setAllowFileAccess(true);// 设置允许访问文件数据
		// settings.setJavaScriptEnabled(true);// 设置支持javascript脚本
		// settings.setLoadsImagesAutomatically(true);// 自动加载图片
		// settings.setRenderPriority(RenderPriority.HIGH);// 提高渲染等级
		// settings.setBlockNetworkImage(true);// 把图片加载放在最后来加载渲染
		// settings.setUseWideViewPort(true);// 设置加载进来的页面自适应手机屏幕
		// settings.setLoadWithOverviewMode(true); // 设置加载进来的页面自适应手机屏幕
		// mWebView.clearCache(true);
		// mWebView.clearHistory();
		WebViewClient mWebClient = new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 1.加载 页面 url 的逻辑处理
				// if (!onHandleUrl(url)) {
				// return super.shouldOverrideUrlLoading(view, url);
				// }
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// 加载错误页面
				showToast("errorCode : " + errorCode + "  " + description);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// 开始加载页面
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}

			@Override
			public void onLoadResource(WebView view, String url) {
				// 5.加载 源码 和 资源
				super.onLoadResource(view, url);
			}

			@Override
			public void doUpdateVisitedHistory(WebView view, String url,
					boolean isReload) {
				// 6.更新访问历史记录
				super.doUpdateVisitedHistory(view, url, isReload);
			}

		};

		mWebView.setWebViewClient(mWebClient);

		// mWebView.setWebChromeClient(new WebChromeClient() {
		//
		// @Override
		// public void onProgressChanged(WebView view, int newProgress) {
		// // 3. 开始加载进度
		// super.onProgressChanged(view, newProgress);
		// }
		//
		// @Override
		// public boolean onJsAlert(WebView view, String url, String message,
		// JsResult result) {
		// return super.onJsAlert(view, url, message, result);
		// }
		//
		// @Override
		// public boolean onJsPrompt(WebView view, String url, String message,
		// String defaultValue, final JsPromptResult result) {
		// AlertDialog.Builder builder = new AlertDialog.Builder(
		// WebViewActivity.this);
		// builder.setTitle("Alert");
		// builder.setMessage(message);
		// builder.setPositiveButton(android.R.string.ok,
		// new AlertDialog.OnClickListener() {
		// public void onClick(DialogInterface dialog,
		// int which) {
		// result.confirm();
		// }
		// });
		// builder.setCancelable(false);
		// builder.create();
		// builder.show();
		// return true;
		// }
		//
		// @Override
		// public boolean onJsBeforeUnload(WebView view, String url,
		// String message, JsResult result) {
		// return super.onJsBeforeUnload(view, url, message, result);
		// }
		//
		// @Override
		// public void getVisitedHistory(ValueCallback<String[]> callback) {
		// super.getVisitedHistory(callback);
		// }
		//
		// @Override
		// public void onGeolocationPermissionsShowPrompt(String origin,
		// Callback callback) {
		// super.onGeolocationPermissionsShowPrompt(origin, callback);
		// }
		//
		// @Override
		// public void onReceivedTitle(WebView view, String title) {
		// // 7. 接收 title
		// if (!TextUtils.isEmpty(title))
		// setTitle(title);
		// ;
		// super.onReceivedTitle(view, title);
		// }
		//
		// @Override
		// public void onReceivedIcon(WebView view, Bitmap icon) {
		// // 接收 网站icon
		// super.onReceivedIcon(view, icon);
		// }
		//
		// });
	}

	private void initHandler() {
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				boolean isHandle = WebViewActivity.this.handleMessage(msg);
				if (!isHandle) {
					super.handleMessage(msg);
				}

			}

		};
	}

	/**
	 * 处理handler消息
	 * 
	 * @param msg
	 * @return
	 */
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MESSAGE_EXIT:
			finish();
			break;
		case MESSAGE_BACK:
			webGoBack();
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 返回键
		case R.id.ll_tv_actionbarback:
			finish();
		default:
			break;
		}
	}

	/**
	 * 处理跳转的url
	 * 
	 * @param url
	 * @return 是否处理
	 */
	public boolean onHandleUrl(String url) {
		Uri uri = Uri.parse(url);
		return false;
	}

	/**
	 * 调用js方法
	 * 
	 * @param method
	 *            方法名
	 * @param param
	 *            参数拼接的字符串
	 */
	public void callJSMethod(String method, String param) {
		mWebView.loadUrl("javascript:" + method + "(" + param + ")");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!webGoBack())
				finish();
		}
		return true;
	}

	/**
	 * 加载制定的Url
	 * 
	 * @param url
	 */
	public void webLoadUrl(String url) {
		if (mWebView != null) {
			Uri uri = Uri.parse(url);
			Builder builder = uri.buildUpon();
			builder.appendQueryParameter("plat", "android");
			// StringBuffer urlBuffer = new StringBuffer(url);
			// urlBuffer.append("&plat=android");
			mWebView.loadUrl(builder.build().toString());
		}
	}

	/**
	 * 页面后退
	 * 
	 * @return 是否成功
	 */
	private boolean webGoBack() {
		if (mWebView != null && mWebView.canGoBack()) {
			mWebView.goBack();
			return true;
		}
		return false;
	}

	/**
	 * 页面前进
	 * 
	 * @return 是否成功
	 */
	public boolean webGoForward() {
		if (mWebView != null && mWebView.canGoForward()) {
			mWebView.canGoForward();
			return true;
		}
		return false;
	}

	/**
	 * 重新加载
	 */
	public void webReload() {
		if (mWebView != null) {
			mWebView.reload();
		}
	}

	/**
	 * 添加JS对象
	 * 
	 * @param jsObject
	 * @param name
	 */
	public void addJSObject(Object jsObject, String name) {
		if (mWebView != null) {
			mWebView.addJavascriptInterface(jsObject, name);
		}
	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	protected void resetLayout() {
		RelativeLayout rel_webview = (RelativeLayout) findViewById(R.id.rl_webview_root);
		SupportDisplay.resetAllChildViewParam(rel_webview);

		LinearLayout layout = (LinearLayout) findViewById(R.id.ll_tv_actionbarback);
		layout.setOnClickListener(this);
	}

}
