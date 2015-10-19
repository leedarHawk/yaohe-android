package com.collcloud.yaohe.activity.friend;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.collcloud.yaohe.MainActivity;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.business.BusinessActivity;
import com.collcloud.yaohe.activity.friend.award.AwardActivity;
import com.collcloud.yaohe.activity.fujin.FuJinActivity;
import com.collcloud.yaohe.activity.my.MineActivity;
import com.collcloud.yaohe.common.base.CommonActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.ImageResize;
import com.collcloud.yaohe.ui.utils.UIHelper;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * 首页-好玩功能
 * 
 * @ClassName FriendHaowanActivity
 * @Description 好玩功能
 */
public class FriendHaowanActivity extends CommonActivity implements
		OnCheckedChangeListener, OnClickListener {
	private static final String TAG="HAOWAN";
	/** 气球 */
	private ImageView mIvShowCoupon;
	private Handler mHandler;

	/**
	 * 气球动画Image
	 */
	private ImageView mIvAnimation;
	private RelativeLayout mRlAnim;
	private Bitmap mBitmap0 = null;

	private Bitmap mBitmap1 = null;

	private Bitmap mBitmap2 = null;

	private Bitmap mBitmap3 = null;

	private Bitmap mBitmap4 = null;
	private Bitmap mBottomBg = null;
	// 点击次数
	private int mCount = 1;
	private Timer mTimer;

	private FrameLayout mFlFrame;
	/**奖品id*/
	private String awardID="";
	/**奖品活动*/
	private String awardAID="";
	/**奖品标题*/
	private String awardTITLE="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_friend_haowan);
		setFooterType(3);
		super.onCreate(savedInstanceState);
		mTimer = new Timer();
	}

	@Override
	protected void onResume() {
		super.onResume();

		setFooterType(3);
		if (mHandler == null) {
			mHandler = new Handler();
		}
	}

	@Override
	public void onClick(View v) {
		if (Utils.isFastDoubleClick()) {
			return;
		}
		switch (v.getId()) {

		// 点我有惊喜 按钮事件：
		case R.id.btn_activity_haowan_commit:
			// timerTask();
			mCount++;
			setEnjoyAnimation();
			break;
		// 点击整个屏幕，触发事件
//		case R.id.fl_haowan_screen_touch:
//			++mCount;
//			setEnjoyAnimation();
//			break;
		case R.id.iv_haowan_animation_container:
			mCount++;
			setEnjoyAnimation();
			break;

		default:
			break;
		}
	}

	/**
	 * 好玩动画效果
	 */
	@SuppressWarnings("deprecation")
	private void setEnjoyAnimation() {

		if (mCount >= 6) {

			mIvShowCoupon.setVisibility(View.VISIBLE);
			// 小气球图片显示
			mIvAnimation.setVisibility(View.GONE);
		} else {
			// 一上来，爆炸后的优惠券、未中奖图片默认隐藏
			mIvShowCoupon.setVisibility(View.GONE);
			// 小气球图片显示
			mIvAnimation.setVisibility(View.VISIBLE);
		}
		if (mCount > 0 && mCount <= 1) {
			mIvAnimation.setBackgroundDrawable(getAnim(0));
		} else if (mCount == 2) {
			mIvAnimation.setBackgroundDrawable(getAnim(1));
		} else if (mCount == 3) {
			mIvAnimation.setBackgroundDrawable(getAnim(2));
		} else if (mCount == 4) {
			mIvAnimation.setBackgroundDrawable(getAnim(3));
		} else if (mCount == 5) {
			mIvAnimation.setBackgroundDrawable(getAnim(4));
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO 此处缺少掉用接口，判断成功与否的判断，然后显示图片。
					// 爆炸后的优惠券、未中奖图片显示
					if(awardID==""){//未中奖
						mIvShowCoupon.setVisibility(View.VISIBLE);
						showCoupon();
						mCount = 0;
					}else{//中奖了
						dialog();
					}
				}
			},400);
		} else {
			mIvShowCoupon.setVisibility(View.GONE);
			UIHelper.ToastMessage(FriendHaowanActivity.this, "下次再试试吧。");
			Intent intent = new Intent(FriendHaowanActivity.this,
					FriendHaowanActivity.class);
			baseStartActivity(intent);
			mIvShowCoupon = (ImageView) findViewById(R.id.iv_haowan_qiqiu);
			mFlFrame = (FrameLayout) findViewById(R.id.fl_haowan_screen_touch);
			mFlFrame.setOnClickListener(this);

			// 气球动画显示的imageview
			mIvAnimation = (ImageView) findViewById(R.id.iv_haowan_animation_container);
			mIvAnimation.setOnClickListener(this);
		}
		// 动画控制设定
		AnimationDrawable animaition = (AnimationDrawable) mIvAnimation
				.getBackground();
		animaition.setOneShot(true);
		if (animaition.isRunning()) {
			animaition.stop();// stop
		}
		animaition.start();// start
	}

	private AnimationDrawable getAnim(int no) {
		AnimationDrawable animation = new AnimationDrawable();
		LayoutParams params = (LayoutParams) mIvAnimation.getLayoutParams();
		int width = params.width;
		int height = params.height;

		if ((width > 320 && width < 640) || (height > 480 && height < 960)) {
			width = 320;
			height = 480;
		}
		if (mBitmap0 == null) {
			mBitmap0 = ImageResize.decodeSampledBitmapFromResource(
					getResources(), R.drawable.bg_yaohe_haowan_01, width,
					height);
		}
		Drawable drawable = new BitmapDrawable(getResources(), mBitmap0);
		if (mBitmap1 == null) {
			mBitmap1 = ImageResize.decodeSampledBitmapFromResource(
					getResources(), R.drawable.bg_yaohe_haowan_02, width,
					height);
		}
		Drawable drawable2 = new BitmapDrawable(getResources(), mBitmap1);
		if (mBitmap2 == null) {
			mBitmap2 = ImageResize.decodeSampledBitmapFromResource(
					getResources(), R.drawable.bg_yaohe_haowan_03, width,
					height);
		}
		Drawable drawable3 = new BitmapDrawable(getResources(), mBitmap2);
		if (mBitmap3 == null) {
			mBitmap3 = ImageResize.decodeSampledBitmapFromResource(
					getResources(), R.drawable.bg_yaohe_haowan_04, width,
					height);
		}
		Drawable drawable4 = new BitmapDrawable(getResources(), mBitmap3);
		if (mBitmap4 == null) {
			mBitmap4 = ImageResize.decodeSampledBitmapFromResource(
					getResources(), R.drawable.bg_yaohe_haowan_00, width,
					height);
		}
		Drawable drawable5 = new BitmapDrawable(getResources(), mBitmap4);
		switch (no) {
		case 0:
			animation.addFrame(drawable, 100);
			break;
		case 1:
			animation.addFrame(drawable, 100);
			animation.addFrame(drawable2, 100);
			break;
		case 2:
			animation.addFrame(drawable, 100);
			animation.addFrame(drawable2, 100);
			animation.addFrame(drawable3, 100);
			break;
		case 3:
			animation.addFrame(drawable, 100);
			animation.addFrame(drawable2, 100);
			animation.addFrame(drawable3, 100);
			animation.addFrame(drawable4, 100);
			break;
		case 4:
			animation.addFrame(drawable, 100);
			animation.addFrame(drawable2, 100);
			animation.addFrame(drawable3, 100);
			animation.addFrame(drawable4, 100);
			animation.addFrame(drawable5, 100);
			break;
		default:
			break;
		}

		return animation;
	}

	/**
	 * 显示爆炸后中奖的优惠券或者未中奖图片信息
	 * 
	 * @Title showCoupon
	 */
	public void showCoupon() {
		ScaleAnimation animaton = new ScaleAnimation(0.2f, 1.0f, 0.2f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animaton.setDuration(2000);
		mIvShowCoupon.startAnimation(animaton);
	}

	private void timerTask() {
		mTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				Message message = new Message();
				message.what = 1;
				doActionHandler.sendMessage(message);

			}
		}, 200, 200);

	}

	private Handler doActionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int msgId = msg.what;
			switch (msgId) {
			case 1:
				// do some action
				CCLog.e("计数器", " " + mCount);
				if (mCount >= 10) {
					return;
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mTimer != null) {
			mTimer.cancel();
		}
	}

	@Override
	protected void resetLayout() {
		RelativeLayout rlLayout = (RelativeLayout) findViewById(R.id.rl_activity_haowan_viewgroup);
		SupportDisplay.resetAllChildViewParam(rlLayout);

		
		mIvShowCoupon = (ImageView) findViewById(R.id.iv_haowan_qiqiu);
		mFlFrame = (FrameLayout) findViewById(R.id.fl_haowan_screen_touch);
		mFlFrame.setOnClickListener(this);

		// 气球动画显示的imageview
		mIvAnimation = (ImageView) findViewById(R.id.iv_haowan_animation_container);
		mIvAnimation.setOnClickListener(this);
		mRlAnim = (RelativeLayout) findViewById(R.id.rl_haowan_animation_container);
		LayoutParams params = (LayoutParams) mRlAnim.getLayoutParams();
		int width = params.width;
		int height = params.height;

		if ((width > 320 && width < 640) || (height > 480 && height < 960)) {
			width = 320;
			height = 480;
		}
		mBottomBg = ImageResize.decodeSampledBitmapFromResource(getResources(),
				R.drawable.bg_yaohe_haowan_01, width, height);
		Drawable background = new BitmapDrawable(getResources(), mBottomBg);
		mRlAnim.setBackgroundDrawable(background);
		if (mHandler == null) {
			mHandler = new Handler();
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rbtn_tab_bottom_zhuye:
			Intent mainIntent = new Intent();
			mainIntent.setClass(FriendHaowanActivity.this, MainActivity.class);
			baseStartActivity(mainIntent);
			break;
		case R.id.rbtn_tab_bottom_fujin:
			Intent fujinIntent = new Intent();
			fujinIntent
					.setClass(FriendHaowanActivity.this, FuJinActivity.class);
			baseStartActivity(fujinIntent);
			break;
		case R.id.rbtn_tab_bottom_haowan:
			Intent haowanIntent = new Intent();
			haowanIntent.setClass(FriendHaowanActivity.this,
					FriendHaowanActivity.class);
			baseStartActivity(haowanIntent);
			break;
		case R.id.rbtn_tab_bottom_wode:

			if (mLoginDataManager.getUserType().equals("1")) {

				Intent myIntent = new Intent();
				myIntent.setClass(FriendHaowanActivity.this,
						BusinessActivity.class);
				startActivity(myIntent);

			} else {
				Intent myIntent = new Intent();
				myIntent.setClass(FriendHaowanActivity.this, MineActivity.class);
				startActivity(myIntent);

			}

			break;

		default:
			break;
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	class MyRunnable implements Runnable {
		@Override
		public void run() {
			setEnjoyAnimation();

		}
	}

	/**
	 * 点击按钮，自动播放一组动画
	 * 
	 * @Title autoAnimation
	 */
	private void autoAnimation() {
		mIvAnimation.setBackgroundResource(R.drawable.loading_animtion);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// 爆炸后的优惠券、未中奖图片显示
				mIvShowCoupon.setVisibility(View.VISIBLE);
				showCoupon();
				// 气球充气的图片隐藏
				mIvAnimation.setVisibility(View.GONE);
			}
		}, 800);
	}
	
	
	/**
	 * 获取中奖数据
	 */
	private void accessNet() {

		HttpUtils http = new HttpUtils();
		// 用来封装参数
		RequestParams params = new RequestParams();

		// 用户ID
		CCLog.v(TAG, "当前用户的ID>>>>>" + mLoginDataManager.getMemberId());
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.ZHONGJIANG, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

						showToast("网络访问失败");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						CCLog.v(TAG, "网络获取中奖数据成功");
						CCLog.v(TAG, arg0.result);
						JSONObject object, object2,object3;
						// 网络返回商圈结果状态信息
						String responseInfo = "";
						// 网络访问商圈状态码
						String code = "";

						try {

							object = new JSONObject(arg0.result);

							responseInfo = object.getString("status");

							object2 = new JSONObject(responseInfo);

							code = object2.getString("code");

							if (code.equals("0")) {//表示中奖

								CCLog.v(TAG, "+" + code);
 
								String dataString=object.optString("data");
								
								object3=new JSONObject(dataString);
								
								awardID=object3.optString("id");
								awardAID=object3.optString("aid");
								awardAID=object3.optString("title");
							
								} 

						} catch (JSONException e) {
							
							CCLog.v(TAG, "中奖数据解析异常" + e.toString());
							e.printStackTrace();
						}
					}
				});
	}
	
	
	/**
	 * 弹出对话框提示中奖了
	 */
	protected void dialog() {

		AlertDialog.Builder builder = new Builder(FriendHaowanActivity.this);
		builder.setMessage(awardAID);
		builder.setTitle(awardTITLE);
		builder.setIcon(R.drawable.tx);
		builder.setPositiveButton("立即领取", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

					startActivity(new Intent(FriendHaowanActivity.this,
							AwardActivity.class));
					finish();
					arg0.dismiss();

			}

		});

		builder.create().show();
	}
}
