package com.collcloud.yaohe.activity.business.myservicemanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.business.BusinessActivity;
import com.collcloud.yaohe.activity.business.myservicemanager.FWGLAdapter.OnFWGLServiceListener;
import com.collcloud.yaohe.activity.business.service.BusinessServiceActivity;
import com.collcloud.yaohe.activity.details.huodong.HuoDongDetailsActivity;
import com.collcloud.yaohe.activity.details.vip.VipCardDetailsActivity;
import com.collcloud.yaohe.activity.details.xinpin.XinPinDetailsActivity;
import com.collcloud.yaohe.activity.details.yaohela.YaoHeLaDetailsActivity;
import com.collcloud.yaohe.activity.details.youhui.YouHuiDetailsActivity;
import com.collcloud.yaohe.api.ApiAccessErrorManager;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.entity.FourService;
import com.collcloud.yaohe.staticvalue.Staticvalue;
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
 * @类说明 商家服务管理页面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月12日 下午3:02:18
 */
public class BusinessFWGLActivity extends BaseActivity implements
		OnClickListener {

	private static final String TAG = "FWGL";
	/** 返回按钮 */
	private LinearLayout ll_common_top_back;
	private int mPage = 0;
	/** 标题信息 */
	private TextView tv_title;
	/** 发送按钮 */
	private TextView tv_do;
	/** 获取四大服务进度条 */
	private Dialog fwgl_mDialog;
	/** listview */
	// private XListView xListView_fwgl;
	/** 用来模拟异步获取数据 */
	private Handler handler;
	private SwipeListView mSwipeListView;
	private FWGLAdapter mAdapter;

	/** 图片加载 */
	DisplayImageOptions mfw_options;
	/** 创建缓存 */
	private static final String TEST_FILE_NAME = "Universal Image Loader @#&=+-_.,!()~'%20.png";
	/** 图片imageLoader初始化 */
	private ImageLoader imageLoader = ImageLoader.getInstance();

	private LinearLayout mLlEmpty;
	private TextView mTvEmptyTips;
	private ArrayList<FourService> mServiceList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_business_fwgl);
		/** 图片imageLoader初始化 */
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));

		intialSource();

		// 创建本地缓存
		File testImageOnSdCard = new File("/mnt/sdcard", TEST_FILE_NAME);
		if (!testImageOnSdCard.exists()) {
			copyTestImageToSdCard(testImageOnSdCard);
		}
		mfw_options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icon_yaohe_loading_default)
				.showImageForEmptyUri(R.drawable.icon_yaohe_loading_default)
				.showImageOnFail(R.drawable.icon_yaohe_loading_default)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.build();
		Staticvalue.OPTIONS = mfw_options;

		accessNetGetService();

		handler = new Handler();
		// // 设置xlistview可以加载、刷新
		// xListView_fwgl.setPullLoadEnable(true);
		// // 设置xlistview可以刷新
		// xListView_fwgl.setPullRefreshEnable(true);
		//
		// xListView_fwgl.setXListViewListener(new IXListViewListener() {
		//
		// @Override
		// public void onRefresh() {
		// accessNetGetService();
		// handler.postDelayed(new Runnable() {
		// @Override
		// public void run() {
		// showToast("数据已是最新。");
		// xListView_fwgl.stopRefresh();
		// }
		// }, 1000);
		// }
		//
		// @Override
		// public void onLoadMore() {
		// accessNetGetService();
		// handler.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// xListView_fwgl.setPullLoadEnable(false);
		// xListView_fwgl.stopLoadMore();
		// showToast("数据已加载完毕。");
		// }
		// }, 1000);
		// }
		// });
	}

	/**
	 * 访问网络获取四大类服务
	 */
	private void accessNetGetService() {
		progressbar(BusinessFWGLActivity.this, R.layout.loading_progress);
		HttpUtils http = new HttpUtils();
		// 用来封装参数
		RequestParams params = new RequestParams();

		// 用户ID
		CCLog.v(TAG, "当前用户的ID>>>>>" + mLoginDataManager.getMemberId());
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.YYFW, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

						CCLog.v(TAG, "网络发送请求服务失败");
						fwgl_mDialog.dismiss();
						showToast("请求数据信息失败了。");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						fwgl_mDialog.dismiss();
						CCLog.v(TAG, "网络请求服务数据成功");
						CCLog.v(TAG, arg0.result);
						JSONObject object, object2;
						// 网络返回四大服务结果状态信息
						String responseInfo = "";
						// 网络访问四大服务状态码
						String code = "";
						// 网络四大服务返回消息
						String responseMsg = "";

						try {

							object = new JSONObject(arg0.result);
							responseInfo = object.getString("status");
							object2 = new JSONObject(responseInfo);

							code = object2.getString("code");
							responseMsg = object2.getString("message");

							if (code.equals("0")) {// 请求成功

								JSONArray jsoArray = object
										.optJSONArray("data");
								final int itemFWCount = jsoArray.length();

								if (itemFWCount > 0) {
									if (itemFWCount == 1) {
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
									// if (itemFWCount > 9) {
									// xListView_fwgl.setPullLoadEnable(true);
									// } else {
									// xListView_fwgl.setPullLoadEnable(false);
									// }
									tv_title.setText("全部(" + itemFWCount + ")");
									CCLog.v(TAG, "正在解析四大服务数据");
									List<FourService> mFourServiceList = parseJSONArray(jsoArray);
									CCLog.v(TAG, "四大服务数据解析完毕");

									setListData(mFourServiceList);
								}

							} else {// 返回提示信息

								CCLog.v(TAG, "获取四大服务出错啦");
								showToast(responseMsg);
							}

						} catch (JSONException e) {
							fwgl_mDialog.dismiss();
							e.printStackTrace();
						}
					}

				});
	}

	private void setListData(List<FourService> mFourServiceList) {
		if (mAdapter != null) {
			mAdapter.refresh(mFourServiceList);
		} else {
			mAdapter = new FWGLAdapter(BusinessFWGLActivity.this,
					mFourServiceList, mfw_options,
					mSwipeListView.getRightViewWidth());
			mSwipeListView.setAdapter(mAdapter);
		}

		initListener();
	}

	private void initListener() {
		mAdapter.setOnRightClickListener(new FWGLAdapter.onRightClickListener() {

			@Override
			public void onRightItemClick(View v, int position) {
				deleteService(position);
			}
		});

		if (mAdapter != null) {
			mAdapter.setOnServiceListerner(new OnFWGLServiceListener() {

				@Override
				public void onServiceClick(int position, String type,
						String callID, String member_id, String serviceId) {
					onServiceItemClick(type, callID, member_id, serviceId);
				}
			});
		}
	}

	/**
	 * 引用服务点击事件
	 * 
	 * @Title doService
	 */
	private void onServiceItemClick(String type, String callID,
			String memberID, String serviceID) {
		Intent intent = new Intent();
		intent.putExtra(IntentKeyNames.KEY_DETAILS_SERVICE_ID, serviceID);
		intent.putExtra(IntentKeyNames.KEY_CALL_ID, callID);
		intent.putExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID, memberID);
		if (type.equals("0")) {
			intent.setClass(BusinessFWGLActivity.this,
					YouHuiDetailsActivity.class);
			baseStartActivity(intent);
		} else if (type.equals("1")) {
			intent.setClass(BusinessFWGLActivity.this,
					VipCardDetailsActivity.class);
			baseStartActivity(intent);

		} else if (type.equals("2")) {
			intent.setClass(BusinessFWGLActivity.this,
					HuoDongDetailsActivity.class);
			baseStartActivity(intent);

		} else if (type.equals("3")) {
			intent.setClass(BusinessFWGLActivity.this,
					XinPinDetailsActivity.class);
			baseStartActivity(intent);

		} else {
			intent.setClass(BusinessFWGLActivity.this,
					YaoHeLaDetailsActivity.class);
			baseStartActivity(intent);
		}
	}

	private void deleteService(int position) {
		if (mServiceList != null && mServiceList.size() > 0) {
			String type = mServiceList.get(position).type;
			String serviceID = mServiceList.get(position).id;
			String url = "";

			url = ContantsValues.BUSINESS_DELETE_CALL;
			deleteBusinessService(serviceID, url);
//			if (type.equals("0")) {
//				url = ContantsValues.BUSINESS_DELETE_COUPON;
//				deleteBusinessService(serviceID, url);
//			} else if (type.equals("1")) {
//				url = ContantsValues.BUSINESS_DELETE_CARD;
//				deleteBusinessService(serviceID, url);
//			} else if (type.equals("2")) {
//				url = ContantsValues.BUSINESS_DELETE_ACTTIVITY;
//				deleteBusinessService(serviceID, url);
//			} else if (type.equals("3")) {
//				url = ContantsValues.BUSINESS_DELETE_NEW;
//				deleteBusinessService(serviceID, url);
//			} else if (type.equals("4")) {
//				url = ContantsValues.BUSINESS_DELETE_CALL;
//				deleteBusinessService(serviceID, url);
//			}

		}
	}

	/**
	 * 四大服务Gson解析
	 */
	public ArrayList<FourService> parseJSONArray(JSONArray response) {

		if (mServiceList != null && mServiceList.size() > 0) {
			mServiceList.clear();
		}
		mServiceList = new ArrayList<FourService>();

		FourService fourservice = null;

		for (int i = 0; i < response.length(); i++) {

			fourservice = new FourService();
			JSONObject object = null;
			try {
				object = response.getJSONObject(i);

				fourservice.id = object.optString("id");
				fourservice.member_id = object.optString("member_id");
				fourservice.service_id = object.optString("service_id");
				fourservice.type = object.optString("type");

				fourservice.title = object.optString("title");
				fourservice.content = object.optString("content");
				fourservice.addtime = object.optString("addtime");

				fourservice.city_id = object.optString("city_id");
				fourservice.province_id = object.optString("province_id");
				fourservice.area_id = object.optString("area_id");

				fourservice.collection_num = object.optString("collection_num");
				fourservice.comment_num = object.optString("comment_num");
				fourservice.zan_num = object.optString("zan_num");

				if (!Utils.isStringEmpty(object.optString("img"))) {
					fourservice.img = URLs.IMG_PRE + object.optString("img");
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			mServiceList.add(fourservice);
		}
		return mServiceList;
	}

	/**
	 * 页面加载
	 * */
	private void progressbar(Context context, int layout) {
		fwgl_mDialog = new AlertDialog.Builder(this).create();
		// mDialog.setOnKeyListener(keyListener);
		fwgl_mDialog.show();
		// 注意此处要放在show之后 否则会报异常
		fwgl_mDialog.setContentView(layout);
	}

	/**
	 * UI界面动作事件
	 */
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {

		case R.id.ll_tv_do:
			// 跳转到四大服务总页面
			baseStartActivity(new Intent(BusinessFWGLActivity.this,
					BusinessServiceActivity.class));
			break;

		case R.id.ll_common_top_back: // 返回
			baseStartActivity(new Intent(BusinessFWGLActivity.this,
					BusinessActivity.class));
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

		RelativeLayout rl_myfwgl = (RelativeLayout) findViewById(R.id.rl_fwgl_root);
		SupportDisplay.resetAllChildViewParam(rl_myfwgl);
		mLlEmpty = (LinearLayout) this
				.findViewById(R.id.ll_business_fugl_empty);
		mLlEmpty.setVisibility(View.GONE);
		mTvEmptyTips = (TextView) this.findViewById(R.id.tv_yaohe_no_data_text);
		mTvEmptyTips.setText("您还没有创建服务呢");
		mTvEmptyTips.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				baseStartActivity(new Intent(BusinessFWGLActivity.this,
						BusinessServiceActivity.class));
			}
		});
	}

	/**
	 * 资源初始化
	 */
	private void intialSource() {
		this.ll_common_top_back = (LinearLayout) findViewById(R.id.ll_common_top_back);
		ll_common_top_back.setOnClickListener(this);

		this.tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("全部");

		this.tv_do = (TextView) findViewById(R.id.tv_do);
		LinearLayout topDo = (LinearLayout) findViewById(R.id.ll_tv_do);
		tv_do.setText("创建");
		topDo.setOnClickListener(this);
		// this.xListView_fwgl = (XListView) findViewById(R.id.xListView_fwgl);
		mSwipeListView = (SwipeListView) findViewById(R.id.slv_person_service_manager);
	}

	/**
	 * 删除优惠券，会员卡，新品和活动
	 */
	private void deleteBusinessService(String serviceID, String url) {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());
		params.addBodyParameter("id", serviceID);
		CCLog.i("删除服务参数：", "member_id=" + mLoginDataManager.getMemberId()
				+ " id=" + serviceID);

		http.send(HttpRequest.HttpMethod.POST, url, params,
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
										CCLog.i("删除服务状态信息：",
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
												showToast("删除成功");
												mSwipeListView.hideRightView();
												accessNetGetService();
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
}
