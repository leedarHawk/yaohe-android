package com.collcloud.yaohe.activity.business.myfans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.collcloud.swipe.view.XListView;
import com.collcloud.swipe.view.XListView.IXListViewListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.business.myfans.BusinessFansAdapter.OnSendMsgListener;
import com.collcloud.yaohe.activity.chat.ChattingActivity;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.entity.MyFansList;
import com.collcloud.yaohe.staticvalue.Staticvalue;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.L;

/**
 * @类说明 商家的所有粉丝列表页面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月2日 下午3:02:18
 */
public class BusinessMyFansActivity extends BaseActivity implements
		OnClickListener {

	private static final String TAG = "BusinessMyFans";
	/** 标题信息 */
	private TextView tv_actionbartitle;
	private BusinessFansAdapter mAdapter;
	/** 粉丝mListView */
	private XListView mListView;
	/** 进度条 */
	private Dialog myfans_Dialog;
	/** 创建缓存 */
	private static final String TEST_FILE_NAME = "Universal Image Loader @#&=+-_.,!()~'%20.png";
	/** 图片加载 */
	DisplayImageOptions mfans_options;
	/** 图片imageLoader初始化 */
	private ImageLoader imageLoader = ImageLoader.getInstance();
	/** 用来模拟异步获取数据 */
	private Handler handler;
	private int mPage = 0;
	private String mStrMemberID ;
	//是否来自 主页 “我的” 页面点击
	private boolean fromBusinessActivity = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_business_my_fans);

		/** 图片imageLoader初始化 */
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		mStrMemberID = getStringExtra("BusinessFansMember");
		fromBusinessActivity = this.getIntent().getBooleanExtra("fromBusinessActivity", false);
		
		progressbar(BusinessMyFansActivity.this, R.layout.loading_progress);
		intialSource();
		

		handler = new Handler();
		// 设置xlistview可以加载、刷新
		mListView.setPullLoadEnable(true);
		// 设置xlistview可以刷新
		mListView.setPullRefreshEnable(true);

		mListView.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {

				mPage = mPage + 1;
				accessNetGetData();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						mListView.stopRefresh();
						showToast("数据已是最新。");
					}
				}, 1000);

			}

			@Override
			public void onLoadMore() {

				mPage = mPage + 1;
				accessNetGetData();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						mListView.stopLoadMore();
						mListView.setPullLoadEnable(false);
						showToast("数据已加载完毕。");
					}
				}, 1000);

			}
		});

		// 创建本地缓存
		File testImageOnSdCard = new File("/mnt/sdcard", TEST_FILE_NAME);
		if (!testImageOnSdCard.exists()) {
			copyTestImageToSdCard(testImageOnSdCard);
		}
		mfans_options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icon_loading_default_small)
				.showImageForEmptyUri(R.drawable.icon_loading_default_small)
				.showImageOnFail(R.drawable.icon_loading_default_small)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.build();
		Staticvalue.OPTIONS = mfans_options;

		accessNetGetData();

	}

	/**
	 * UI界面资源初始化
	 */
	private void intialSource() {

		this.mListView = (XListView) findViewById(R.id.xListView_fans);

		this.tv_actionbartitle = (TextView) findViewById(R.id.tv_actionbartitle);
		if (mLoginDataManager.getMemberId().equals(mStrMemberID)) {
			tv_actionbartitle.setText("我的粉丝");
		} else {
			tv_actionbartitle.setText("粉丝列表");
		}
	}

	/**
	 * 加载进度条
	 */
	private void progressbar(Context context, int layout) {
		myfans_Dialog = new AlertDialog.Builder(this).create();
		// mDialog.setOnKeyListener(keyListener);
		myfans_Dialog.show();
		// 注意此处要放在show之后 否则会报异常
		myfans_Dialog.setContentView(layout);
	}

	/**
	 * 获取粉丝数据
	 */
	private void accessNetGetData() {

		HttpUtils http = new HttpUtils();
		// 用来封装参数
		RequestParams params = new RequestParams();
		// 用户ID
		CCLog.v(TAG, "当前用户的ID>>>>>" + mLoginDataManager.getMemberId());
		if(fromBusinessActivity) {
			params.addBodyParameter("member_id", mLoginDataManager.getMemberId());
		} else {
			params.addBodyParameter("member_id", mStrMemberID);
		}
		params.addBodyParameter("page", "1");
		
		http.send(HttpRequest.HttpMethod.POST, ContantsValues.MYFS, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						myfans_Dialog.dismiss();
						showToast("获取我的粉丝时出错了");
						CCLog.e(TAG, "获取我的粉丝时出错了");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						CCLog.v(TAG, "网络获取我的粉丝数据成功");
						CCLog.v(TAG, arg0.result);
						JSONObject object, object2;
						// 网络返回发布我的粉丝状态信息
						String responseInfo = "";
						// 网络发布我的粉丝状态码
						String code = "";
						// 网络发布我的粉丝返回消息
						String responseMsg = "";

						try {
							object = new JSONObject(arg0.result);
							responseInfo = object.getString("status");
							object2 = new JSONObject(responseInfo);

							code = object2.getString("code");
							responseMsg = object2.getString("message");

							if (code.equals("0")) {

								JSONArray jsoArray = object.optJSONArray("data");
								final int itemFSCount = jsoArray.length();

								if (itemFSCount > 0) {
									if (itemFSCount == 1) {
										JSONObject yhObject = jsoArray.getJSONObject(0);
										if (Utils.isStringEmpty(yhObject.optString("member_id"))) {
											myfans_Dialog.dismiss();
											showToast("您还没有粉丝");
											return;
										}

									}
									if (itemFSCount >9) {
										mListView.setPullLoadEnable(true);
									}else {
										mListView.setPullLoadEnable(false);
									}

									/** 关注商家数据集合 */
									ArrayList<MyFansList> myFsList = new ArrayList<MyFansList>();

									for (int i = 0; i < itemFSCount; i++) {

										MyFansList fsItem = new MyFansList();

										JSONObject yhObject = jsoArray
												.getJSONObject(i);

										fsItem.member_id = yhObject
												.optString("member_id");
										fsItem.nickname = yhObject
												.optString("nickname");

										fsItem.face = yhObject
												.optString("face");

										fsItem.login_user = yhObject
												.optString("login_user");

										myFsList.add(fsItem);

									}
									setFansData(myFsList);
									myfans_Dialog.dismiss();

								} else {
									myfans_Dialog.dismiss();

									showToast("您还没有粉丝");
								}

							} else {
								myfans_Dialog.dismiss();
								showToast("您还没有粉丝");
							}

						} catch (JSONException e) {
							myfans_Dialog.dismiss();
							CCLog.v(TAG, "获取我的粉丝数据时异常" + e.toString());
							e.printStackTrace();
						}
					}

				});
	}

	private void setFansData(ArrayList<MyFansList> myFsList) {
		mAdapter = new BusinessFansAdapter(BusinessMyFansActivity.this,
				myFsList);
		mListView.setAdapter(mAdapter);
		if (mAdapter != null) {
			mAdapter.setOnCollectionItemListerner(new OnSendMsgListener() {

				@Override
				public void onSendMsgListener(int position, String member_id,
						String nickname) {
					Intent intent = new Intent(BusinessMyFansActivity.this,
							ChattingActivity.class);
					intent.putExtra("ACCOUNTTO", member_id);
					intent.putExtra("NICKNAME", nickname);
					baseStartActivity(intent);
				}
			});
		}
	}

	/**
	 * UI界面动作事件
	 */
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {

		case R.id.ll_tv_actionbarback:
			finish();
			break;

		default:
			break;
		}
	}

	/**
	 * imageloader
	 * 
	 * @说明 图片写入缓存
	 * @param testImageOnSdCard
	 */
	private void copyTestImageToSdCard(final File testImageOnSdCard) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					InputStream is = getAssets().open(TEST_FILE_NAME);
					FileOutputStream fos = new FileOutputStream(
							testImageOnSdCard);
					byte[] buffer = new byte[8192];
					int read;
					try {
						while ((read = is.read(buffer)) != -1) {
							fos.write(buffer, 0, read);
						}
					} finally {
						fos.flush();
						fos.close();
						is.close();
					}
				} catch (IOException e) {
					L.w("Can't copy test image onto SD card");
				}
			}
		}).start();
	}

	/**
	 * 页面适配
	 */
	@Override
	protected void resetLayout() {

		LinearLayout ll_myfans = (LinearLayout) findViewById(R.id.ll_myfans_root);
		SupportDisplay.resetAllChildViewParam(ll_myfans);

		LinearLayout layout = (LinearLayout) findViewById(R.id.ll_tv_actionbarback);
		layout.setOnClickListener(this);
	}

}
