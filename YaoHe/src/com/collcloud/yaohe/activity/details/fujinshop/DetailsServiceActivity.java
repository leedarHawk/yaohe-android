package com.collcloud.yaohe.activity.details.fujinshop;

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
import com.collcloud.yaohe.activity.details.huodong.HuoDongDetailsActivity;
import com.collcloud.yaohe.activity.details.vip.VipCardDetailsActivity;
import com.collcloud.yaohe.activity.details.xinpin.XinPinDetailsActivity;
import com.collcloud.yaohe.activity.details.youhui.YouHuiDetailsActivity;
import com.collcloud.yaohe.activity.person.youhuiquan.detail.YouHuiQuanDetailActivity;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.api.info.DetailsBusinessShopInfo.BusinessServiceInfo;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.adapter.HomeBusinessServiceAdapter;
import com.collcloud.yaohe.ui.adapter.HomeBusinessServiceAdapter.OnBusinessServiceListener;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.UIHelper;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.ui.view.SingleLayoutListView;
import com.collcloud.yaohe.ui.view.SingleLayoutListView.OnLoadMoreListener;
import com.collcloud.yaohe.ui.view.SingleLayoutListView.OnRefreshListener;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * 商家基本信息-- 创建服务一览
 * 
 * @ClassName DetailsServiceActivity
 * @Description
 * @author CollCloud_小米
 */
public class DetailsServiceActivity extends BaseActivity implements Callback {
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
	private SingleLayoutListView mLvService;
	/**
	 * 商家服务列表
	 */
	private ArrayList<BusinessServiceInfo> mListService;
	/**
	 * 创建服务的展示列表适配器
	 */
	private HomeBusinessServiceAdapter mServiceAdapter;
	private Handler mHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_business_service_details);
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

		http.send(HttpRequest.HttpMethod.POST,
				ContantsValues.DETAILS_GET_BUSINESS_SEVICE_INFO, params,
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
						UIHelper.ToastMessage(DetailsServiceActivity.this,
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
					mLvService.setVisibility(View.GONE);
					mLlEmpty.setVisibility(View.VISIBLE);
					setEmptyTips();
					return;
				} else {
					mLvService.setVisibility(View.VISIBLE);
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
			mLvService.setVisibility(View.GONE);
			mLlEmpty.setVisibility(View.VISIBLE);
			setEmptyTips();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		initClickListener();
	}

	private void initClickListener() {
		if (mServiceAdapter != null) {
			mServiceAdapter
					.setOnServiceListerner(new OnBusinessServiceListener() {

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
								intent.setClass(DetailsServiceActivity.this,
										YouHuiDetailsActivity.class);
								baseStartActivity(intent);
							} else if (type.equals("1")) {
								intent.setClass(DetailsServiceActivity.this,
										VipCardDetailsActivity.class);
								baseStartActivity(intent);

							} else if (type.equals("2")) {
								intent.setClass(DetailsServiceActivity.this,
										HuoDongDetailsActivity.class);
								baseStartActivity(intent);

							} else if (type.equals("3")) {
								intent.setClass(DetailsServiceActivity.this,
										XinPinDetailsActivity.class);
								baseStartActivity(intent);

							}

						}
					});
		}
	}

	/**
	 * 设定服务列表数据
	 * 
	 * @Title setServiceData
	 */
	private void setServiceData() {
		mServiceAdapter = new HomeBusinessServiceAdapter(
				DetailsServiceActivity.this, mListService);
		mLvService.setAdapter(mServiceAdapter);
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
		LinearLayout layout = (LinearLayout) findViewById(R.id.ll_activity_business_service_root);
		SupportDisplay.resetAllChildViewParam(layout);
		mLvService = (SingleLayoutListView) findViewById(R.id.lv_activity_details_business_service);
		mLlEmpty = (LinearLayout) findViewById(R.id.ll_activity_details_business_service_empty);
		mTvEmptyTips = (TextView) findViewById(R.id.tv_yaohe_no_data_text);
		mHandler = new Handler(DetailsServiceActivity.this);

		initTopOnlyBackTitle();
		setTopOnlyBackTitle("服务列表");

		// TODO 设定提示文字
		mTvEmptyTips.setText("空空如也");

		mLvService.setCanLoadMore(false);
		mLvService.setCanRefresh(true);
		mLvService.setAutoLoadMore(false);
		mLvService.setMoveToFirstItemAfterRefresh(false);
		mLvService.setDoRefreshOnUIChanged(false);

		mLvService.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadData(0);
			}
		});
//		mLvService.setOnLoadListener(new OnLoadMoreListener() {
//			@Override
//			public void onLoadMore() {
//				loadData(1);
//			}
//		});

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
			mLvService.onRefreshComplete(); // 下拉刷新完成
			break;
		case 2:
			// 获取服务列表详情
			getBusinessServiceInfo("2");
			break;
		case 3:
			UIHelper.ToastMessage(mBaseActivity, "数据加载完成。");
			mLvService.onLoadMoreComplete();
			break;
		default:
			break;
		}
		return false;
	}

}
