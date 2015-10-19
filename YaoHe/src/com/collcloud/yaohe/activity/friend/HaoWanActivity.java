package com.collcloud.yaohe.activity.friend;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.CommonActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.Utils;

public class HaoWanActivity extends CommonActivity implements OnClickListener {

	// 3个小金蛋
	private LinearLayout mLlShowEgg;
	private ImageView mIvEgg1, mIvEgg2, mIvEgg3;
	private ImageView image;
	private ImageView mResultImg;

	private AssetManager assets = null;
	private String[] images = null;
	private int currentImg = 0;
	// 定义一个负责更新图片的Handler
	private Handler handler = null;
	private Thread thread = null;

	private List<String> mEggsImgs = new ArrayList<String>();
	private List<String> mResultImgs = new ArrayList<String>();
	private int currentEggImg = 0;
	private boolean mIsRunOnCreate = false;
	private boolean mIsCanClick = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_haowan_info);
		setFooterType(3);
		super.onCreate(savedInstanceState);
		CCLog.i("haowan ", "onCreate ");
		mIsRunOnCreate = true;
		// 初始化视图
		onInitView();
		// 获取assets下图片
		images = getImages();
		displayAssets();

		startResultAnim(0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setFooterType(3);
	}

	@SuppressLint("HandlerLeak")
	private void onInitView() {
		handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				// 表明消息是该程序发出的
				if (msg.what == 0x110) {

					// 展示下一张图片
					dispalyEggImage();
				} else if (msg.what == 0x111) {
					// 展示敲碎金蛋，播放结果图片
					dispalyResultImage();
				}
			};
		};
	}

	@Override
	public void onClick(View v) {
		if (Utils.isFastDoubleClick()) {
			return;
		}
		switch (v.getId()) {
		case R.id.iv_eggs_img1:
		case R.id.iv_eggs_img2:
		case R.id.iv_eggs_img3:
			stopResultAnim();
			startResultAnim(1);
			break;

		case R.id.image_show_anim:
			// if (mIsCanClick) {
			stopResultAnim();
			mLlShowEgg.setVisibility(View.VISIBLE);
			image.setVisibility(View.GONE);
			// }
			break;

		default:
			break;
		}

	}

	private void startResultAnim(final int type) {
		if (thread == null) {
			thread = new Thread() {
				@Override
				public void run() {
					Thread curThread = Thread.currentThread();
					while (thread != null && thread == curThread) {
						switch (type) {
						case 0:
							try {
								Thread.sleep(100);
								Message msg = new Message();
								msg.what = 0x110;
								handler.sendMessage(msg);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							break;
						case 1:
							try {
								Thread.sleep(100);
								Message msg = new Message();
								msg.what = 0x111;
								handler.sendMessage(msg);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							break;

						default:
							break;
						}

					}
				}
			};
			thread.start();
		} else {
			stopResultAnim();
		}
	}

	private void stopResultAnim() {

		if (thread != null) {
			Thread temp = thread;
			thread = null;
			temp.interrupt();
		} else {
			return;
		}

	}

	// 展示assets内容
	private void displayAssets() {
		int length = images.length;

		if (mEggsImgs != null && mEggsImgs.size() > 0) {
			mEggsImgs.clear();
		}

		if (mResultImgs != null && mResultImgs.size() > 0) {
			mResultImgs.clear();
		}
		mEggsImgs = new ArrayList<String>();
		mResultImgs = new ArrayList<String>();
		for (int i = 0; i < length; i++) {
			if (images[i].startsWith("egg_")) {

				mEggsImgs.add(images[i]);
			} else if (images[i].startsWith("game0_1_")) {
				mResultImgs.add(images[i]);
			}
		}
		CCLog.i("锤子动画的总大小：", " " + mResultImgs.size());

	}

	private void dispalyEggImage() {
		image.setVisibility(View.VISIBLE);
		mLlShowEgg.setVisibility(View.GONE);
		// 如果发生数组越界
		// if (currentEggImg >= mEggsImgs.size()) {
		// currentEggImg = 0;
		// }
		if (currentEggImg == mEggsImgs.size()) {
			image.setVisibility(View.GONE);
			mLlShowEgg.setVisibility(View.VISIBLE);
			currentEggImg = 0;
			stopResultAnim();
			return;
		}
		// 备注1
		// 找到下一个图片文件
		while (!mEggsImgs.get(currentEggImg).endsWith(".png")
				&& !mEggsImgs.get(currentEggImg).endsWith(".jpg")
				&& !mEggsImgs.get(currentEggImg).endsWith(".gif")) {
			currentEggImg++;
			// 如果已发生数组越界
			if (currentEggImg >= mEggsImgs.size()) {
				currentEggImg = 0;
			}
		}

		InputStream assetFile = null;
		try {
			// 打开指定资源对应的输入流
			assetFile = assets.open(mEggsImgs.get(currentEggImg++));
		} catch (IOException e) {
			e.printStackTrace();
		}

		BitmapDrawable bitmapDrawable = (BitmapDrawable) image.getDrawable();
		// 备注2
		// 如果图片还未回收，先强制回收该图片
		if (bitmapDrawable != null && !bitmapDrawable.getBitmap().isRecycled()) {
			bitmapDrawable.getBitmap().recycle();
		}
		// 改变ImageView显示的图片
		image.setImageBitmap(BitmapFactory.decodeStream(assetFile));
	}

	private void dispalyResultImage() {
		CCLog.i("数组图片 ", currentImg + " ");
		// 如果发生数组越界
		// if (currentImg >= mResultImgs.size()) {
		// currentImg = 0;
		// }
		if (currentImg >= mResultImgs.size()) {
			stopResultAnim();
			currentImg = 0;
			image.setVisibility(View.GONE);
			mResultImg.setVisibility(View.VISIBLE);
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					showCoupon();
					mResultImg.setVisibility(View.GONE);
					mLlShowEgg.setVisibility(View.VISIBLE);
				}
			});
			return;
		} else {
			image.setVisibility(View.VISIBLE);
			mLlShowEgg.setVisibility(View.GONE);
			// 备注1
			// 找到下一个图片文件
			// while (!mResultImgs.get(currentImg).endsWith(".png")
			// && !mResultImgs.get(currentImg).endsWith(".jpg")
			// && !mResultImgs.get(currentImg).endsWith(".gif")) {
			// currentImg++;
			// // 如果已发生数组越界
			// if (currentImg >= mResultImgs.size()) {
			// currentImg = 0;
			// }
			// }

			InputStream assetFile = null;
			try {
				// 打开指定资源对应的输入流
				assetFile = assets.open(mResultImgs.get(currentImg++));
			} catch (IOException e) {
				e.printStackTrace();
			}

			BitmapDrawable bitmapDrawable = (BitmapDrawable) image
					.getDrawable();
			// 备注2
			// 如果图片还未回收，先强制回收该图片
			if (bitmapDrawable != null
					&& !bitmapDrawable.getBitmap().isRecycled()) {
				bitmapDrawable.getBitmap().recycle();
			}
			// 改变ImageView显示的图片
			image.setImageBitmap(BitmapFactory.decodeStream(assetFile));

		}
	}

	// 展示下一张图片
	// private void dispalyNextImage() {
	// // 如果发生数组越界
	// if (currentImg >= images.length) {
	// currentImg = 0;
	// }
	// // 备注1
	// // 找到下一个图片文件
	// while (!images[currentImg].endsWith(".png")
	// && !images[currentImg].endsWith(".jpg")
	// && !images[currentImg].endsWith(".gif")) {
	// currentImg++;
	// // 如果已发生数组越界
	// if (currentImg >= images.length) {
	// currentImg = 0;
	// }
	// }
	//
	// InputStream assetFile = null;
	// try {
	// // 打开指定资源对应的输入流
	// assetFile = assets.open(images[currentImg++]);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// BitmapDrawable bitmapDrawable = (BitmapDrawable) image.getDrawable();
	// // 备注2
	// // 如果图片还未回收，先强制回收该图片
	// if (bitmapDrawable != null && !bitmapDrawable.getBitmap().isRecycled()) {
	// bitmapDrawable.getBitmap().recycle();
	// }
	// // 改变ImageView显示的图片
	// image.setImageBitmap(BitmapFactory.decodeStream(assetFile));
	// }

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
		mResultImg.setBackgroundResource(R.drawable.bg_yaohe_haowan_09);
		mResultImg.startAnimation(animaton);
	}

	@Override
	protected void resetLayout() {

		RelativeLayout rlLayout = (RelativeLayout) findViewById(R.id.ll_activity_haowan_eggs_root);
		SupportDisplay.resetAllChildViewParam(rlLayout);

		mIvEgg1 = (ImageView) findViewById(R.id.iv_eggs_img1);
		mIvEgg2 = (ImageView) findViewById(R.id.iv_eggs_img2);
		mIvEgg3 = (ImageView) findViewById(R.id.iv_eggs_img3);
		mLlShowEgg = (LinearLayout) findViewById(R.id.ll_image_show_eggs_);
		image = (ImageView) findViewById(R.id.image_show_anim);
		mResultImg = (ImageView) findViewById(R.id.image_show_result_anim);
		mIvEgg1.setOnClickListener(this);
		mIvEgg2.setOnClickListener(this);
		mIvEgg3.setOnClickListener(this);
		image.setOnClickListener(this);
	}

	@SuppressWarnings("finally")
	private String[] getImages() {
		String[] tempImages = null;
		try {
			assets = getAssets();
			// 获取/assets/目录下所有文件
			if (null != assets) {
				tempImages = assets.list("");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			return tempImages;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mIsRunOnCreate = false;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		CCLog.i("HaoWan onNewIntent");

		if (!mIsRunOnCreate) {
			// 初始化视图
			onInitView();
			// 获取assets下图片
			images = getImages();
			displayAssets();
			startResultAnim(0);
		}
	}
}
