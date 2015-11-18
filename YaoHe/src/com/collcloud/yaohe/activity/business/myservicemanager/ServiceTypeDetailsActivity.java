package com.collcloud.yaohe.activity.business.myservicemanager;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.business.myservicemanager.BusinessServiceManagerAdapter.OnBusinessServiceManagerListener;
import com.collcloud.yaohe.activity.business.myservicemanager.BusinessServiceManagerAdapter.OnRightClickListener;
import com.collcloud.yaohe.activity.details.huodong.HuoDongDetailsActivity;
import com.collcloud.yaohe.activity.details.vip.VipCardDetailsActivity;
import com.collcloud.yaohe.activity.details.xinpin.XinPinDetailsActivity;
import com.collcloud.yaohe.activity.details.youhui.YouHuiDetailsActivity;
import com.collcloud.yaohe.api.ApiAccessErrorManager;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.api.info.DetailsBusinessShopInfo.BusinessServiceInfo;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
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

/**
 * 
 * @description LEE服务列表信息
 * @version 1.0
 * @author LEE
 * @date 2015年10月17日 下午8:09:20 
 * @update 2015年10月17日 下午8:09:20
 */
public class ServiceTypeDetailsActivity extends BaseActivity implements
		Callback {
	
	private String TestgitCommitn;
	/**
	 * 商家ID
	 */
	private String mStrShopID;
	/**
	 * 会员ID
	 */
	private String mStrMemberID;
	/**
	 * 类型(post提交)0优惠券 1会员卡 2活动 3新品
	 */
	private String mStrType;
	/**
	 * 内容为空时，提示文字信息
	 */
	private LinearLayout mLlEmpty;
	/**
	 * 提示文字
	 */
	private TextView mTvEmptyTips;
	/**
	 * 商家创建服务的展示列表
	 */
	// private SingleLayoutListView mLvService;
	private SwipeListView mSLvService;
	/**
	 * 商家服务列表
	 */
	private ArrayList<BusinessServiceInfo> mListService;
	/**
	 * 创建服务的展示列表适配器
	 */
	private BusinessServiceManagerAdapter mServiceAdapter;
	private Handler mHandler;
	
	private String tag = ServiceTypeDetailsActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_business_service_manager_details);
		mStrShopID = getStringExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID);
		mStrMemberID = getStringExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID);
		mStrType = getStringExtra(IntentKeyNames.KEY_CALL_TYPE);
		CCLog.i("商家服务列表详情:", "shop_id : " + mStrShopID + " type :" + mStrType);
		// 获取服务列表详情
		getBusinessServiceInfo("1");
	}

	/**
	 * 获取服务列表详情
	 */
	private void getBusinessServiceInfo(String page) {

		CCLog.i(" getBusinessServiceInfo ：类型 ", mStrType);
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("shop_id", mStrShopID);
		params.addBodyParameter("type", mStrType);
		params.addBodyParameter("page", page);
		String url = ContantsValues.DETAILS_GET_BUSINESS_SEVICE_INFO+"&shop_id="+mStrShopID+"&type="+mStrType+"&page=1";
		CCLog.d(tag, "url:"+url);
		http.send(HttpRequest.HttpMethod.POST,
				url, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(responseInfo.result);
							responseErrorInfo(responseInfo);
							CCLog.i("商家服务详情：", responseInfo.result);
							if (jsonObject != null
									&& jsonObject.optJSONArray("data") != null) {
								JSONArray serviceArray = jsonObject
										.optJSONArray("data");
								businessServiceList(serviceArray);
								refreshServiceData(mListService);

							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						UIHelper.ToastMessage(ServiceTypeDetailsActivity.this,
								R.string.response_data_invalid);
					}
				});

	}

	/**
	 * 获取商家创建的服务列表
	 * 
	 * @Title businessServiceList
	 */
	private void businessServiceList(JSONArray serviceArray) {
		if (serviceArray != null && serviceArray.length() > 0) {
			if (serviceArray.length() == 1) {
				JSONObject serObject = serviceArray.optJSONObject(0);
				if (Utils.isStringEmpty(serObject.optString("id"))) {
					mSLvService.setVisibility(View.GONE);
					mLlEmpty.setVisibility(View.VISIBLE);
					setEmptyTips();
					return;
				} else {
					mSLvService.setVisibility(View.VISIBLE);
					mLlEmpty.setVisibility(View.GONE);
				}
			}
			mListService = new ArrayList<BusinessServiceInfo>();
			for (int i = 0; i < serviceArray.length(); i++) {
				JSONObject serviceObject = serviceArray.optJSONObject(i);
				BusinessServiceInfo serviceInfo = new BusinessServiceInfo();

				if (serviceObject.has("id")) {
					serviceInfo.id = serviceObject.optString("id");
				}
				serviceInfo.member_id = mStrMemberID;
				if (serviceObject.has("title")) {
					serviceInfo.title = serviceObject.optString("title");
				}
				if (serviceObject.has("addtime")) {
					serviceInfo.addtime = serviceObject.optString("addtime");
				}
				if (serviceObject.has("img")) {
					if (!Utils.isStringEmpty(serviceObject.optString("img"))) {
						serviceInfo.img = URLs.IMG_PRE
								+ serviceObject.optString("img");
					}
				}
				if (mStrType != null) {
					serviceInfo.type = mStrType;
				}
				mListService.add(serviceInfo);
			}
		} else {
			mSLvService.setVisibility(View.GONE);
			mLlEmpty.setVisibility(View.VISIBLE);
			setEmptyTips();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	private void initClickListener() {
	
		if (mServiceAdapter != null) {
			mServiceAdapter.setOnRightItemClickListener(new OnRightClickListener() {

				@Override
				public void onRightItemClick(View v, int position) {
					// TODO Auto-generated method stub
					deleteService(position);
				}
			});
			mServiceAdapter
					.setOnServiceListerner(new OnBusinessServiceManagerListener() {

						@Override
						public void onServiceClick(int position, String type,
								String member_id, String serviceId) {
							// TODO Auto-generated method stub
							Intent intent = new Intent();
							intent.putExtra(
									IntentKeyNames.KEY_DETAILS_SERVICE_ID,
									serviceId);
							intent.putExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID,
									mStrShopID);
							intent.putExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID,
									member_id);
							if (type.equals("0")) {
								intent.setClass(
										ServiceTypeDetailsActivity.this,
										YouHuiDetailsActivity.class);
								baseStartActivity(intent);
							} else if (type.equals("1")) {
								intent.setClass(
										ServiceTypeDetailsActivity.this,
										VipCardDetailsActivity.class);
								baseStartActivity(intent);

							} else if (type.equals("2")) {
								intent.setClass(
										ServiceTypeDetailsActivity.this,
										HuoDongDetailsActivity.class);
								baseStartActivity(intent);

							} else if (type.equals("3")) {
								intent.setClass(
										ServiceTypeDetailsActivity.this,
										XinPinDetailsActivity.class);
								baseStartActivity(intent);

							}

						}
					});
		}
	}

	private void deleteService(int position) {
		if (mListService != null && mListService.size() > 0) {
			String serviceID = mListService.get(position).id;

			String url ="";
			if (mStrType.equals("0")) {
				url = ContantsValues.BUSINESS_DELETE_COUPON;
				deleteBusinessService(serviceID, url);
			} else if (mStrType.equals("1")) {
				url = ContantsValues.BUSINESS_DELETE_CARD;
				deleteBusinessService(serviceID, url);
			} else if (mStrType.equals("2")) {
				url = ContantsValues.BUSINESS_DELETE_ACTTIVITY;
				deleteBusinessService(serviceID, url);
			} else if (mStrType.equals("3")) {
				url = ContantsValues.BUSINESS_DELETE_NEW;
				deleteBusinessService(serviceID, url);
			} 
			deleteBusinessService(serviceID, url);
		}
	}
	
	// test git commit;;;;

	/**
	 * 设定服务列表数据
	 * 
	 * @Title setServiceData
	 */
	private void setServiceData() {
		mServiceAdapter = new BusinessServiceManagerAdapter(
				ServiceTypeDetailsActivity.this, mListService,
				mSLvService.getRightViewWidth());
		mSLvService.setAdapter(mServiceAdapter);
		initClickListener();
	}

	/**
	 * 刷新服务列表数据
	 * 
	 * @Title refreshServiceData
	 */
	private void refreshServiceData(List<BusinessServiceInfo> data) {
		if (data != null && data.size() > 0) {
			if (mServiceAdapter != null) {
				mServiceAdapter.refresh(data);
			} else {
				setServiceData();
			}
		}
		initClickListener();
	}

	private void setEmptyTips() {
		if (mStrType != null) {

			if ("0".equals(mStrType)) {
				mTvEmptyTips.setText("空空如也\n\n暂无优惠券信息");
			} else if ("1".equals(mStrType)) {
				mTvEmptyTips.setText("空空如也\n\n暂无会员卡信息");
			} else if ("2".equals(mStrType)) {
				mTvEmptyTips.setText("空空如也\n\n暂无活动信息");
			} else if ("3".equals(mStrType)) {
				mTvEmptyTips.setText("空空如也\n\n暂无新品信息");
			} else {
				mTvEmptyTips.setText("空空如也");
			}
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		mStrShopID = getStringExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID);
		mStrType = getStringExtra(IntentKeyNames.KEY_CALL_TYPE);
	}

	@Override
	protected void resetLayout() {
		LinearLayout layout = (LinearLayout) findViewById(R.id.ll_activity_business_service_manager_root);
		SupportDisplay.resetAllChildViewParam(layout);
		
		mSLvService = (SwipeListView) findViewById(R.id.slv_activity_details_business_service);
		mLlEmpty = (LinearLayout) findViewById(R.id.ll_activity_details_business_service_empty);
		mTvEmptyTips = (TextView) findViewById(R.id.tv_yaohe_no_data_text);
		
		mHandler = new Handler(ServiceTypeDetailsActivity.this);

		initTopOnlyBackTitle();
		setTopOnlyBackTitle("服务列表");

		// TODO 设定提示文字
		mTvEmptyTips.setText("空空如也");

		// mLvService.setCanLoadMore(false);
		// mLvService.setCanRefresh(true);
		// mLvService.setAutoLoadMore(false);
		// mLvService.setMoveToFirstItemAfterRefresh(false);
		// mLvService.setDoRefreshOnUIChanged(false);
		//
		// mLvService.setOnRefreshListener(new OnRefreshListener() {
		// @Override
		// public void onRefresh() {
		// loadData(0);
		// }
		// });
		// mLvService.setOnLoadListener(new OnLoadMoreListener() {
		// @Override
		// public void onLoadMore() {
		// loadData(1);
		// }
		// });

	}

	/**
	 * 加载数据啦~
	 */
	public void loadData(final int type) {
		new Thread() {
			@Override
			public void run() {
				switch (type) {
				case 0:
					Message _Msg = mHandler.obtainMessage(0);
					mHandler.sendMessage(_Msg);
					break;
				case 1:
					Message _Msg2 = mHandler.obtainMessage(2);
					mHandler.sendMessage(_Msg2);
					break;
				}

				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (type == 0) { // 下拉刷新
					// Collections.reverse(mList); //逆序
					Message _Msg = mHandler.obtainMessage(1);
					mHandler.sendMessage(_Msg);
				} else if (type == 1) {
					Message _Msg = mHandler.obtainMessage(4);
					mHandler.sendMessage(_Msg);
				}
			}
		}.start();
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case 0:
			// 获取服务列表详情
			getBusinessServiceInfo("1");
			break;

		case 1:
			UIHelper.ToastMessage(mBaseActivity, "数据已全部加载，没有更多了。");
			// mLvService.onRefreshComplete(); // 下拉刷新完成
			break;
		case 2:
			// 获取服务列表详情
			getBusinessServiceInfo("2");
			break;
		case 3:
			UIHelper.ToastMessage(mBaseActivity, "数据加载完成。");
			// mLvService.onLoadMoreComplete();
			break;
		default:
			break;
		}
		return false;
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
												mSLvService.hideRightView();
												getBusinessServiceInfo("1");
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