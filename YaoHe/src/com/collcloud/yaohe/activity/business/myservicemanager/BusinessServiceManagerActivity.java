package com.collcloud.yaohe.activity.business.myservicemanager;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.business.BusinessActivity;
import com.collcloud.yaohe.activity.business.service.BusinessServiceActivity;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.UIHelper;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class BusinessServiceManagerActivity extends BaseActivity implements
		OnClickListener {
	/**
	 * 服务 -- 优惠券
	 */
	private RelativeLayout mRlServiceCoupon;
	/**
	 * 服务 -- 会员卡
	 */
	private RelativeLayout mRlServiceCard;
	/**
	 * 服务 -- 活动
	 */
	private RelativeLayout mRlServiceActivity;
	/**
	 * 服务 -- 新品
	 */
	private RelativeLayout mRlServiceNewProduct;
	/**
	 * 优惠券、会员卡、活动、新品的数量显示
	 */
	private TextView mTvCouponCount, mTvCardCount, mTvActivityCount,
			mTvNewProductCount;
	private String mStrCounpnNum, mStrCardNum, mStrActivityNum,
			mStrNewProductNum;
	private TextView mTvTitle;
	/**
	 * 商家ID
	 */
	private String mStrShopID;
	/**
	 * 商家member_id
	 */
	private String mStrMemberID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_business_service_manager);
		mStrShopID = getStringExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID);
		mStrMemberID = getStringExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID);
		CCLog.i("商家个人详情:", "member_id :" + mStrMemberID + "shop_id : "
				+ mStrShopID);
		// 获取详情
		// getBusinessShopInfo();
	}

	/**
	 * 获取商家个人详情
	 * 
	 * @Title getBusinessShopInfo
	 */
	private void getBusinessShopInfo() {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", mStrShopID);
		//params.addBodyParameter("member_id", mStrMemberID);
		//修改member id为当前用户的ID,mStrMemberID为shop member ID
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());


		http.send(HttpRequest.HttpMethod.POST,
				ContantsValues.DETAILS_GET_BUSINESS_SHOP_INFO, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(responseInfo.result);
							responseErrorInfo(responseInfo);
							CCLog.i("商家个人详情：", responseInfo.result);
							if (jsonObject != null && jsonObject.has("data")) {
								JSONObject dataObject = jsonObject
										.optJSONObject("data");
								if (dataObject != null && dataObject.has("row")) {// 商家个人商铺简介
									JSONObject businessObject = dataObject
											.optJSONObject("row");
									// 商家个人商铺介绍
									businessShopDes(businessObject);
								}

							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						UIHelper.ToastMessage(
								BusinessServiceManagerActivity.this,
								R.string.response_data_invalid);
					}
				});

	}

	/**
	 * 商家个人商铺介绍
	 * 
	 * @Title businessShopDes
	 * @Description
	 */
	private void businessShopDes(JSONObject jsonObject) {

		if (jsonObject.has("counponnum")) {
			mStrCounpnNum = jsonObject.optString("counponnum");
			if (Integer.valueOf(mStrCounpnNum) > 0) {
				mTvCouponCount.setVisibility(View.VISIBLE);
				mTvCouponCount.setText(mStrCounpnNum);
			} else {
				mTvCouponCount.setVisibility(View.GONE);

			}
		}
		if (jsonObject.has("cardnum")) {
			mStrCardNum = jsonObject.optString("cardnum");
			if (Integer.valueOf(mStrCardNum) > 0) {
				mTvCardCount.setVisibility(View.VISIBLE);
				mTvCardCount.setText(mStrCardNum);
			} else {
				mTvCardCount.setVisibility(View.GONE);

			}
		}
		if (jsonObject.has("activitynum")) {
			mStrActivityNum = jsonObject.optString("activitynum");
			if (Integer.valueOf(mStrActivityNum) > 0) {
				mTvActivityCount.setVisibility(View.VISIBLE);
				mTvActivityCount.setText(mStrActivityNum);
			} else {
				mTvActivityCount.setVisibility(View.GONE);

			}
		}
		if (jsonObject.has("newproductnum")) {
			mStrNewProductNum = jsonObject.optString("newproductnum");
			if (Integer.valueOf(mStrNewProductNum) > 0) {
				mTvNewProductCount.setVisibility(View.VISIBLE);
				mTvNewProductCount.setText(mStrNewProductNum);
			} else {
				mTvNewProductCount.setVisibility(View.GONE);

			}
		}
	}

	@Override
	protected void resetLayout() {
		// TODO Auto-generated method stub
		LinearLayout layout = (LinearLayout) findViewById(R.id.ll_business_service_manager_root);
		SupportDisplay.resetAllChildViewParam(layout);

		LinearLayout llBack = (LinearLayout) findViewById(R.id.ll_common_top_back);
		llBack.setOnClickListener(this);

		mTvTitle = (TextView) findViewById(R.id.tv_title);
		mTvTitle.setText("全部");

		TextView tvCreate = (TextView) findViewById(R.id.tv_do);
		LinearLayout topDo = (LinearLayout) findViewById(R.id.ll_tv_do);
		tvCreate.setText("创建");
		topDo.setOnClickListener(this);

		// 商家服务列表区域 - 优惠券、会员卡、活动、新品
		mRlServiceCoupon = (RelativeLayout) findViewById(R.id.rl_business_youhuiquan);
		mRlServiceCard = (RelativeLayout) findViewById(R.id.rl_business_card);
		mRlServiceActivity = (RelativeLayout) findViewById(R.id.rl_business_huodong);
		mRlServiceNewProduct = (RelativeLayout) findViewById(R.id.rl_business_xinpin);
		// 优惠券、会员卡、活动、新品 数量
		mTvCouponCount = (TextView) findViewById(R.id.tv_business_youhuiquan_count);
		mTvActivityCount = (TextView) findViewById(R.id.tv_business_huodong_count);
		mTvCardCount = (TextView) findViewById(R.id.tv_business_card_count);
		mTvNewProductCount = (TextView) findViewById(R.id.tv_business_xinpin_count);
		mRlServiceCoupon.setOnClickListener(this);
		mRlServiceCard.setOnClickListener(this);
		mRlServiceActivity.setOnClickListener(this);
		mRlServiceNewProduct.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (Utils.isFastDoubleClick()) {
			return;
		}

		switch (v.getId()) {
		case R.id.ll_common_top_back:
			baseStartActivity(new Intent(BusinessServiceManagerActivity.this,
					BusinessActivity.class));
			finish();
			break;
		case R.id.ll_tv_do:
			// 跳转到四大服务总页面
			baseStartActivity(new Intent(BusinessServiceManagerActivity.this,
					BusinessServiceActivity.class));
			break;
		// 商家服务信息-点击查看详情
		case R.id.rl_business_youhuiquan:
			onServiceItemClick("0");
			break;
		case R.id.rl_business_card:
			onServiceItemClick("1");
			break;
		case R.id.rl_business_huodong:
			onServiceItemClick("2");
			break;
		case R.id.rl_business_xinpin:
			onServiceItemClick("3");
			break;
		default:
			break;
		}
	}

	private void onServiceItemClick(String type) {
		Intent intent = new Intent();
		intent.putExtra(IntentKeyNames.KEY_CALL_TYPE, type);
		intent.putExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID, mStrShopID);
		intent.putExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID, mStrMemberID);
		CCLog.i("商家服务详情:", " type :" + type);
		intent.setClass(BusinessServiceManagerActivity.this,
				ServiceTypeDetailsActivity.class);
		baseStartActivity(intent);
	}
}
