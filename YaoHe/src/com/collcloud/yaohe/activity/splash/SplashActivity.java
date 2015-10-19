package com.collcloud.yaohe.activity.splash;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.RelativeLayout;

import com.collcloud.yaohe.MainActivity;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.utils.ADBCheck;
import com.umeng.analytics.MobclickAgent;

/**
 * 程序Splash画面
 * 
 * @ClassName SplashActivity
 * @Description Splash 淡出动画显示
 * @author CollCloud_小米
 */
public class SplashActivity extends BaseActivity {

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		final ContentResolver contentResolver = this.getContentResolver();
		final Handler mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1 && !ADBCheck.isADBEnabled(contentResolver)) {
					next();
				}
			}
		};

		// 1s後、次へ画面を進む
		Thread mThread = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					Message mMessage = new Message();
					mMessage.what = 1;
					mHandler.sendMessage(mMessage);
				} catch (InterruptedException e) {
				}
			}
		};
		mThread.start();

		// 友盟统计分析
		MobclickAgent.updateOnlineConfig( SplashActivity.this );
//		AnalyticsConfig.enableEncrypt(true);
		MobclickAgent.setDebugMode(false);
//      SDK在统计Fragment时，需要关闭Activity自带的页面统计，
//		然后在每个页面中重新集成页面统计的代码(包括调用了 onResume 和 onPause 的Activity)。
		MobclickAgent.openActivityDurationTrack(false);

//		MobclickAgent.setAutoLocation(true);
//		MobclickAgent.setSessionContinueMillis(1000);

	}

	private void next() {
		Intent intent = new Intent();
		intent.setClass(SplashActivity.this, MainActivity.class);
		baseStartActivity(intent);
		overridePendingTransition(R.anim.alpha_fade, R.anim.translate_hold);
		this.finish();

	}

	@Override
	protected void resetLayout() {
		RelativeLayout background = (RelativeLayout) findViewById(R.id.rl_splash_viewgroup);
		SupportDisplay.resetAllChildViewParam(background);
	}

}
