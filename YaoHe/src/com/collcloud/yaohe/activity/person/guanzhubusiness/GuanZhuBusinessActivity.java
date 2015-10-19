package com.collcloud.yaohe.activity.person.guanzhubusiness;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.MainActivity;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.details.fujinshop.DetailsBusinessInfoActivity;
import com.collcloud.yaohe.activity.my.MineActivity;
import com.collcloud.yaohe.api.ApiAccess;
import com.collcloud.yaohe.api.ApiAccessErrorManager;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.entity.GuanZhunBusiList.GuanZhunBusi;
import com.collcloud.yaohe.ui.adapter.PersonGuanzhuAdapter;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.UIHelper;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.ui.view.SwipeListView;
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
 * @类说明 关注商家
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月2日 下午3:02:18
 */
public class GuanZhuBusinessActivity extends BaseActivity implements
		OnClickListener {

	private static final String TAG = "我的关注";
	/** 标题信息 */
	private TextView tv_actionbartitle;
	private SwipeListView mSwipeListView;
	/** 图片加载 */
	DisplayImageOptions options;
	/** 图片缓存测试图片 */
	private static final String TEST_FILE_NAME = "Universal Image Loader @#&=+-_.,!()~'%20.png";
	private List<GuanZhunBusi> mFollowData;
	private PersonFollowAdapter mFollowAdapter;
	/**
	 * 列表内容为空，显示提示内容
	 */
	private LinearLayout mLlEmpty = null;
	/**
	 * 提示文字
	 */
	private TextView mTvTips = null;
	/** 图片imageLoader初始化 */
	private ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guan_zhu_business);

		// 初始化资源
		initalSource();

		getFollowList();
	}

	/**
	 * 获取可使用的我的关注数据
	 */
	private void getFollowList() {

		HttpUtils http = new HttpUtils();
		// 用来封装参数
		RequestParams params = new RequestParams();

		// 用户ID
		CCLog.v(TAG, "当前用户的ID>>>>>" + mLoginDataManager.getMemberId());
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.MYGZ, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						CCLog.v(TAG, "网络获取我的关注数据成功");
						CCLog.v(TAG, arg0.result);
						JSONObject object, object2;
						// 网络返回商圈结果状态信息
						String responseInfo = "";
						// 网络访问商圈状态码
						String code = "";

						try {

							object = new JSONObject(arg0.result);
							responseInfo = object.getString("status");
							object2 = new JSONObject(responseInfo);
							code = object2.getString("code");

							if (code.equals("0")) {
								JSONArray jsoArray = object
										.optJSONArray("data");
								final int numberOfItemsInResp = jsoArray
										.length();

								if (jsoArray.length() > 0) {
									CCLog.v("关注商家信息：", jsoArray.toString());
								}
								if (mFollowData != null
										&& mFollowData.size() > 0) {
									mFollowData.clear();
								}
								mFollowData = new ArrayList<GuanZhunBusi>();
								if (numberOfItemsInResp > 0) {
									if (numberOfItemsInResp == 1) {
										JSONObject yhObject = jsoArray
												.getJSONObject(0);
										if (Utils.isStringEmpty(yhObject
												.optString("id"))) {
											mLlEmpty.setVisibility(View.VISIBLE);
											mSwipeListView
													.setVisibility(View.GONE);
											return;
										} else {
											mSwipeListView
													.setVisibility(View.VISIBLE);
											mLlEmpty.setVisibility(View.GONE);
										}
									}
									/** 关注商家数据集合 */
									for (int i = 0; i < numberOfItemsInResp; i++) {
										GuanZhunBusi guanzhuInfo = new GuanZhunBusi();
										JSONObject guanzhuObject = jsoArray
												.getJSONObject(i);

										guanzhuInfo.id = guanzhuObject
												.optString("id");
										String face = guanzhuObject
												.optString("face");
										CCLog.i("face " + i, face + " ");
										if (!Utils.isStringEmpty(face)
												&& !face.equals("null")) {
											guanzhuInfo.face = URLs.IMG_PRE
													+ guanzhuObject
															.getString("face");
										}
										guanzhuInfo.one_id = guanzhuObject
												.optString("one_id");
										guanzhuInfo.industry_class_id = guanzhuObject
												.optString("industry_class_id");
										guanzhuInfo.username = guanzhuObject
												.optString("username");
										guanzhuInfo.title = guanzhuObject
												.optString("title");
										guanzhuInfo.member_id = guanzhuObject
												.optString("member_id");
										guanzhuInfo.class_title = guanzhuObject
												.optString("class_title");

										mFollowData.add(guanzhuInfo);

									}

									// 设置数据
									setFollowData();

								} else {
									showToast("您还没有关注任何商家");
								}

							} else {// 数据为空的时候

								CCLog.v(TAG, "加载的关注数据为空");
								showToast("您还没有关注任何商家");
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

				});
	}

	/**
	 * 设置数据信息
	 * 
	 * @Title setFollowData
	 */
	private void setFollowData() {

		if (mFollowAdapter != null) {
			mFollowAdapter.refreshData(mFollowData);
		} else {
			mFollowAdapter = new PersonFollowAdapter(
					GuanZhuBusinessActivity.this, mFollowData,
					mSwipeListView.getRightViewWidth());
			mSwipeListView.setAdapter(mFollowAdapter);
		}

		initListener();
	}

	private void initListener() {
		mFollowAdapter
				.setOnRightItemClickListener(new PersonFollowAdapter.onRightItemClickListener() {

					@Override
					public void onRightItemClick(View v, int position) {
						cancelFollow(position, mFollowData.get(position).id);
					}
				});

		mSwipeListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						Intent intent = new Intent(
								GuanZhuBusinessActivity.this,
								DetailsBusinessInfoActivity.class);
						if (mFollowData != null && mFollowData.size() > 0) {

							intent.putExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID,
									mFollowData.get(position).id);
							intent.putExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID,
									mFollowData.get(position).member_id);
						}
						baseStartActivity(intent);

					}
				});
	}

	/**
	 * 取消关注
	 */
	private void cancelFollow(final int position, String shopID) {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());
		params.addBodyParameter("id", shopID);
		CCLog.i("取消关注参数：", "member_id=" + mLoginDataManager.getMemberId()
				+ " id=" + shopID);

		http.send(HttpRequest.HttpMethod.POST,
				ContantsValues.CANCEL_FOLLOW_URL, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!Utils.isStringEmpty(responseInfo.result)) {
							if (responseInfo.result.contains("status")) {
								try {
									// 数据处理
									JSONObject errorJsonObject = new JSONObject(
											responseInfo.result);
									if (responseInfo.result != null) {
										CCLog.i("取消关注状态信息：",
												responseInfo.result + " ");
									}
									if (errorJsonObject.has("status")) {
										JSONObject statusObject = errorJsonObject
												.optJSONObject("status");
										if (statusObject.has("code")) {
											int code = statusObject
													.optInt("code");
											if (code == 1) {
												String strErrorMsg = statusObject
														.optString("message");
												UIHelper.ToastMessage(
														mBaseActivity,
														strErrorMsg);
											} else {
												showToast("取消关注成功");
												mSwipeListView.hideRightView();
												// updataUI(position);
												getFollowList();
											}
										}
									}
								} catch (Exception e) {
									String errorMsg = ApiAccessErrorManager
											.getMessage(5, mBaseActivity);
									UIHelper.ToastMessage(mBaseActivity,
											errorMsg);
								}

							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						UIHelper.ToastMessage(mBaseActivity,
								R.string.response_data_invalid);
					}
				});

	}

	/**
	 * UI界面资源初始化
	 */
	private void initalSource() {
		// 刷新相关
		mSwipeListView = (SwipeListView) this
				.findViewById(R.id.xlv_person_guanzhu);
		// 空内容提示
		mLlEmpty = (LinearLayout) findViewById(R.id.ll_person_guanzhu_shop_empty);
		mTvTips = (TextView) findViewById(R.id.tv_fragment_extra_text_link);
		mTvTips.setText(Html.fromHtml("去"
				+ "<font size=\"14\" color=\"#23a3fc\">首页推荐</font>" + "看看吧"));
		mTvTips.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent fujinIntent = new Intent();
				fujinIntent.setClass(GuanZhuBusinessActivity.this,
						MainActivity.class);
				baseStartActivity(fujinIntent);
				GuanZhuBusinessActivity.this.finish();
			}
		});

		// 其他
		tv_actionbartitle = (TextView) findViewById(R.id.tv_actionbartitle);
		tv_actionbartitle.setText("关注商家");

		/** 图片imageLoader初始化 */
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		// 创建本地缓存
		File testImageOnSdCard = new File("/mnt/sdcard", TEST_FILE_NAME);
		if (!testImageOnSdCard.exists()) {
			copyTestImageToSdCard(testImageOnSdCard);
		}
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icon_yaohe_loading_default)
				.showImageForEmptyUri(R.drawable.icon_yaohe_loading_default)
				.showImageOnFail(R.drawable.icon_yaohe_loading_default)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.build();

	}

	@SuppressLint("HandlerLeak")
	Handler mUiHandler = new Handler() {
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				// 获取首页关注商家列表
				getFollowList();
				break;

			case 1:
				UIHelper.ToastMessage(GuanZhuBusinessActivity.this,
						"数据已全部加载，没有更多了。");
				break;
			case 2:
				ApiAccess.dismissProgressDialog();
				if (mFollowData != null && mFollowData.size() > 0) {
					mSwipeListView.setVisibility(View.VISIBLE);
					mLlEmpty.setVisibility(View.GONE);
					setFollowData();

				}
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 加载数据啦~
	 */
	public void loadData(final int type) {
		new Thread() {
			@Override
			public void run() {
				switch (type) {
				case 0:
					Message _Msg = mUiHandler.obtainMessage(0);
					mUiHandler.sendMessage(_Msg);
					CCLog.i("关注商家  —— 可以加载数据了。。");
					break;
				case 1:
					getFollowList();
					break;
				}

				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (type == 0) { // 下拉刷新
					// Collections.reverse(mList); //逆序
					Message _Msg = mUiHandler.obtainMessage(1);
					mUiHandler.sendMessage(_Msg);
				}
			}
		}.start();
	}

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {

		case R.id.ll_tv_actionbarback:
			baseStartActivity(new Intent(GuanZhuBusinessActivity.this,
					MineActivity.class));
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

	@Override
	protected void resetLayout() {

		RelativeLayout rel_mygzsj = (RelativeLayout) findViewById(R.id.rl_gzsj_root);
		SupportDisplay.resetAllChildViewParam(rel_mygzsj);
		LinearLayout layout = (LinearLayout) findViewById(R.id.ll_tv_actionbarback);
		layout.setOnClickListener(this);

	}

}
