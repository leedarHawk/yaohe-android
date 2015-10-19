package com.collcloud.yaohe.activity.details.fujinshop;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.dianpin.fujin.AddNewDianpinActivity;
import com.collcloud.yaohe.activity.dianpin.fujin.FuJinXiaoXiActivity;
import com.collcloud.yaohe.activity.map.ShowGeocoderActivity;
import com.collcloud.yaohe.api.info.DetailsNearShopInfo;
import com.collcloud.yaohe.api.info.DetailsNearShopInfo.NearShopInfo;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.GlobalConstant;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.model.AmapGencodeInfo;
import com.collcloud.yaohe.ui.utils.GsonUtils;
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
 * 【附近】推荐商圈-详情列表
 * 
 * @ClassName FuJinFuwuDetailsActivity
 * @Description 详情信息
 * @author CollCloud_小米
 */
public class FuJinFuwuDetailsActivity extends BaseActivity implements
		OnClickListener {

	private LinearLayout mLlExtra;
	// ***********　推荐店铺信息 *********** //
	/**
	 * 附近商圈店铺的名称
	 */
	private TextView mTvTuiJianName = null;
	/**
	 * 附近商圈店铺简介
	 */
	private TextView mTvTuiJianContent = null;
	/**
	 * 附近商圈店铺的地址
	 */
	private TextView mTvTuiJianAddress = null;
	private TextView mTvTuiJianAddressImg = null;
	/**
	 * 附近商圈店铺的电话
	 */
	private TextView mTvTuiJianTel = null;
	private TextView mTvTuiJianTelImg = null;
	/**
	 * 附近商圈店铺关注按钮
	 */
	private TextView mTvTuiJianGuanZhu = null;
	/**
	 * 附近商圈店铺粉丝数量
	 */
	private TextView mTvTuijianFans = null;
	/**
	 * API获取的电话号码
	 */
	private String mStrTuiJianTel = null;
	/**
	 * API获取的关注状态
	 */
	private String mStrTuiJianGuanZhu = null;
	/**
	 * 分类
	 */
	private TextView mTvType = null;
	/**
	 * 店铺图片
	 */
	private ImageView mIvShopImg = null;
	/**
	 * 营业时间
	 */
	private TextView mTvTime = null;

	// ***********　推荐店铺信息 *********** //
	/**
	 * 店铺ID
	 */
	private String mStrShopID;
	/**
	 * 商铺详情内容获取
	 */
	private DetailsNearShopInfo mDetailsNearShopInfo;
	private NearShopInfo mShopInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details_fujin_fuwu);
		mStrShopID = getStringExtra(IntentKeyNames.KEY_NEAR_SHOP_ID);
		// 获取附近商圈详情
		// getNearShopInfo();
	}

	/**
	 * 获取附近商圈详情
	 * 
	 * @Title getNearShopInfo
	 */
	private void getNearShopInfo() {

		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", mStrShopID);

		// TODO url地址
		http.send(HttpRequest.HttpMethod.POST,
				ContantsValues.DETAILS_GET_ACTIVITY, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(responseInfo.result);
							String startDate = "";
							String endDate = "";
							StringBuffer date = new StringBuffer();
							if (jsonObject.has("data")) {
								mDetailsNearShopInfo = GsonUtils.json2Bean(
										responseInfo.result,
										DetailsNearShopInfo.class);
								if (mDetailsNearShopInfo != null
										&& mDetailsNearShopInfo.data != null) {
									mShopInfo = mDetailsNearShopInfo.data;
									if (mShopInfo.shop_id != null) {
										mStrShopID = mShopInfo.shop_id;
									}
									if (mShopInfo.shop_name != null) {
										mTvTuiJianName
												.setText(mShopInfo.shop_name);
									}
									if (mShopInfo.shop_address != null) {
										mTvTuiJianAddress
												.setText(mShopInfo.shop_address);
									}
									if (mShopInfo.shop_fans_num != null) {
										mTvTuijianFans
												.setText(mShopInfo.shop_fans_num
														+ " 粉丝");
									}
									if (mShopInfo.shop_subscribe_tel != null) {
										mTvTuiJianTel
												.setText(mShopInfo.shop_subscribe_tel);
										mStrTuiJianTel = mShopInfo.shop_subscribe_tel;
									}
									if (mShopInfo.type != null) {
										mTvType.setText(mShopInfo.type);
									}
									if (mShopInfo.content != null) {
										mTvTuiJianContent
												.setText(mShopInfo.content);
									}

								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						UIHelper.ToastMessage(FuJinFuwuDetailsActivity.this,
								R.string.response_data_invalid);
					}
				});

	}

	@Override
	protected void resetLayout() {
		RelativeLayout rlLayout = (RelativeLayout) this
				.findViewById(R.id.rl_activity_details_fujin_fuwu_root);
		SupportDisplay.resetAllChildViewParam(rlLayout);

		initTopOnlyBackTitle();
		setTopOnlyBackTitle("详细资料");
		initBottomXiaoxi();

		mLlExtra = (LinearLayout) findViewById(R.id.ll_details_fujin_fuwu_content);
		// 店铺图片
		mIvShopImg = (ImageView) findViewById(R.id.iv_details_fujin_fuwu_tuijian_img);
		// 店铺名称
		mTvTuiJianName = (TextView) findViewById(R.id.tv_details_tuijian_fuwu_name);
		// 店铺内容
		mTvTuiJianContent = (TextView) findViewById(R.id.tv_details_fujin_shop_descri);
		// 店铺地址
		mTvTuiJianAddress = (TextView) findViewById(R.id.tv_details_fujin_dizhi);
		mTvTuiJianAddress.setOnClickListener(this);
		mTvTuiJianAddressImg = (TextView) findViewById(R.id.tv_details_fujin_dizhi_img);
		mTvTuiJianAddressImg.setOnClickListener(this);

		// 店铺电话
		mTvTuiJianTel = (TextView) findViewById(R.id.tv_details_fujin_tel);
		mTvTuiJianTelImg = (TextView) findViewById(R.id.tv_details_fujin_tel_img);
		mTvTuiJianTel.setOnClickListener(this);
		mTvTuiJianTelImg.setOnClickListener(this);

		// 关注店铺
		mTvTuiJianGuanZhu = (TextView) findViewById(R.id.tv_details_tuijian_fuwu_guanzhu);
		mTvTuiJianGuanZhu.setOnClickListener(this);
		// 店铺粉丝数量
		mTvTuijianFans = (TextView) findViewById(R.id.tv_details_tuijian_fuwu_fans);
		// 店铺所属分类
		mTvType = (TextView) findViewById(R.id.tv_details_tuijian_fuwu_meishi);
		// 店铺营业时间
		mTvTime = (TextView) findViewById(R.id.tv_details_fujin_time);

		mTvTuiJianGuanZhu.setText(GlobalConstant.INVALID_VALUE);

	}

	@Override
	public void onClick(View v) {
		if (Utils.isFastDoubleClick()) {
			return;
		}
		Intent intent = new Intent();
		switch (v.getId()) {

		case R.id.tv_details_fujin_tel: // 拨打电话
		case R.id.tv_details_fujin_tel_img:
			telDialog("确定要拨打电话吗？\n" + mStrTuiJianTel, mStrTuiJianTel);

			break;

		case R.id.tv_details_fujin_dizhi: // 店铺地址定位
		case R.id.tv_details_fujin_dizhi_img:
			AmapGencodeInfo amapGencodeInfo = new AmapGencodeInfo();
			String queryName = GlobalConstant.EMPTY_STRING;
			String queryAddress = GlobalConstant.EMPTY_STRING;
			if (!Utils.isStringEmpty(Utils.strFromView(mTvTuiJianAddress))) {
				queryAddress = Utils.strFromView(mTvTuiJianAddress);
			}
			if (!Utils.isStringEmpty(Utils.strFromView(mTvTuiJianName))) {
				queryName = Utils.strFromView(mTvTuiJianName);
			}
			amapGencodeInfo.setmStrQueryAddress(queryAddress);
			amapGencodeInfo.setmStrQueryName(queryName);
			intent.setClass(FuJinFuwuDetailsActivity.this,
					ShowGeocoderActivity.class);
			intent.putExtra(IntentKeyNames.KEY_GEOCODER_LOCATION,
					amapGencodeInfo);
			baseStartActivity(intent);

			break;

		case R.id.tv_details_tuijian_fuwu_guanzhu: // 关注店铺
			if (Utils.strFromView(mTvTuiJianGuanZhu).equals(
					GlobalConstant.INVALID_VALUE)) {
				// 关注店铺
				shopFollowApi(mLoginDataManager.getMemberId(), mStrShopID,
						ContantsValues.SHOP_FOLLOW_URL, "关注成功");
				mTvTuiJianGuanZhu.setText(GlobalConstant.VALID_VALUE);
				mTvTuiJianGuanZhu
						.setBackgroundResource(R.drawable.icon_home_type_yiguanzhu);
			} else {
				mTvTuiJianGuanZhu.setText(GlobalConstant.INVALID_VALUE);
				mTvTuiJianGuanZhu
						.setBackgroundResource(R.drawable.icon_fujin_jiaguanzhu);
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 附近店铺点评
	 */
	@Override
	protected void nearByComment() {
		super.nearByComment();
		Intent intent = new Intent();
		intent.setClass(mBaseActivity, AddNewDianpinActivity.class);
		intent.putExtra(IntentKeyNames.KEY_NEAR_COMMENT_ID, mStrShopID);
		baseStartActivity(intent);
	}

	/**
	 * 附近店铺发消息
	 */
	@Override
	protected void nearByMessage() {
		super.nearByMessage();

		Intent intent = new Intent();
		intent.setClass(mBaseActivity, FuJinXiaoXiActivity.class);
		intent.putExtra(IntentKeyNames.KEY_NEAR_MESSAGE_SHOP_ID, mStrShopID);
		baseStartActivity(intent);
	}

}
