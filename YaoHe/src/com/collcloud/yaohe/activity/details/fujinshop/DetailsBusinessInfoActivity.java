package com.collcloud.yaohe.activity.details.fujinshop;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.business.myfans.BusinessMyFansActivity;
import com.collcloud.yaohe.activity.details.yaohela.YaoHeLaDetailsActivity;
import com.collcloud.yaohe.activity.dianpin.fujin.DetailsBusinessPinlunActivity;
import com.collcloud.yaohe.activity.dianpin.fujin.FuJinXiaoXiActivity;
import com.collcloud.yaohe.activity.map.ShowGeocoderActivity;
import com.collcloud.yaohe.api.ApiAccess;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.api.info.DetailsBusinessShopInfo.BusinessCallInfo;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.GlobalConstant;
import com.collcloud.yaohe.common.base.GlobalVariable;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.adapter.HomeBusinessCallAdapter;
import com.collcloud.yaohe.ui.adapter.HomeBusinessCallAdapter.OnBusinessInfoListener;
import com.collcloud.yaohe.ui.model.AmapGencodeInfo;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.UIHelper;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.ui.view.MyListView;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * 商家信息详情
 * 
 * @ClassName DetailsBusinessInfoActivity
 * @Description
 * @author CollCloud_小米
 */
public class DetailsBusinessInfoActivity extends BaseActivity implements
		OnClickListener {
	private String gittest;

	/**
	 * 商家发布的吆喝列表区域
	 */
	private LinearLayout mLlCall;
	private LinearLayout mLlBottomXi;
	private ScrollView mScrollView;

	// ***********　商家店铺信息 start *********** //
	/**
	 * 店铺的名称
	 */
	private TextView mTvShopName = null;
	/**
	 * 店铺简介
	 */
	private TextView mTvShopContent = null;
	/**
	 * 店铺的地址
	 */
	private TextView mTvShopAddress = null;
	private TextView mTvShopAddressImg = null;
	/**
	 * 店铺的电话
	 */
	private TextView mTvShopTel = null;
	private TextView mTvShopTelImg = null;
	/**
	 * 店铺关注按钮
	 */
	private TextView mTvShopGuanZhu = null;
	/**
	 * 店铺粉丝数量
	 */
	private TextView mTvShopFans = null;
	/**
	 * API获取的电话号码
	 */
	private String mStrTuiJianTel = null;
	/**
	 * API获取的关注状态
	 */
	private String mStrGuanZhuStatus = null;
	/**
	 * 商家星级
	 */
	private String mStrStar = null;
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
	private TextView mTvBusinessTime = null;
	private TextView mTvStar1, mTvStar2, mTvStar3, mTvStar4, mTvStar5;
	private String mStrCounpnNum, mStrCardNum, mStrActivityNum,
			mStrNewProductNum;

	// ***********　商家店铺信息 end *********** //
	/**
	 * 商家ID
	 */
	private String mStrShopID;
	/**
	 * 商家member_id
	 */
	private String mStrMemberID;
	private String mStrFace;

	// ************　服务内容　start　************** //
	/**
	 * 更多服务
	 */
	private Button mBtnServiceMore;
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
	private TextView mTvCoupon, mTvCard, mTvActivity, mTvNewProduct;
	// ************　服务内容　ｅｎｄ　************** //
	/**
	 * 商家吆喝列表
	 */
	private ArrayList<BusinessCallInfo> mListCalls;
	/**
	 * 商家吆喝服务的展示列表
	 */
	private MyListView mLvCall;
	/**
	 * 创建吆喝的展示列表适配器
	 */
	private HomeBusinessCallAdapter mCallAdapter;
	/**
	 * 更多吆喝
	 */
	private Button mBtnCallMore;
	private static ImageLoader mImageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details_fujin_fuwu);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
		// 设定商家头像
		ImageListener listener = ImageLoader.getImageListener(mIvShopImg,
				R.drawable.icon_yaohe_default_logo,
				R.drawable.icon_yaohe_default_logo);
		//mImageLoader.get(url, listener,getResources().getDimensionPixelSize(R.dimen.photo_max_middle_width),getResources().getDimensionPixelSize(R.dimen.photo_max_middle_width));

		mStrShopID = getStringExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID);
		mStrMemberID = getStringExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID);
		mStrFace = getStringExtra(IntentKeyNames.KEY_SHOP_MEMBER_FACE);
		CCLog.i("商家个人详情:", "member_id :" + mStrMemberID + "shop_id : "
				+ mStrShopID);
		// if (!Utils.isStringEmpty(mStrFace)) {
		// mImageLoader.get(mStrFace, listener);
		// }
		setFollosStatus();
		// 关注店铺状态信息查询
		isFollow(mLoginDataManager.getMemberId(), mStrShopID,	ContantsValues.SHOP_FOLLOW_STATUS_URL);
		CCLog.i("我就只看个店铺信息，怎么给我加关注啊！！！！！！！！");
		// 获取详情
		getBusinessShopInfo();
		mScrollView.postDelayed(new Runnable() {

			@Override
			public void run() {
				mScrollView.smoothScrollBy(0, -1000);
			}
		}, 1000);
	}

	private void setFollosStatus() {
		if (mStrMemberID.equals(mLoginDataManager.getMemberId())) {
			mTvShopGuanZhu.setVisibility(View.GONE);
			mLlBottomXi.setVisibility(View.GONE);
		} else {
			mTvShopGuanZhu.setVisibility(View.VISIBLE);
			mLlBottomXi.setVisibility(View.VISIBLE);

		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mCallAdapter != null) {
			mCallAdapter
					.setOnHomeItemClickListener(new OnBusinessInfoListener() {

						@Override
						public void onBusinessInfoClick(int position,
								String type, String callId, String serviceId,
								String memberId) {

							onItemSelectAction(serviceId, type, callId,
									memberId);

						}
					});
		}

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (mBaseIsFollow) {
					mTvShopGuanZhu.setText(GlobalConstant.VALID_VALUE);
					mTvShopGuanZhu
							.setBackgroundResource(R.drawable.icon_home_type_yiguanzhu);
				} else {
					mTvShopGuanZhu.setText(GlobalConstant.INVALID_VALUE);
					mTvShopGuanZhu
							.setBackgroundResource(R.drawable.icon_fujin_jiaguanzhu);
				}
			}
		}, 1000);
	}

	private void onItemSelectAction(String serviceId, String type,
			String callId, String memberId) {
		Intent intent = new Intent();
		intent.putExtra(IntentKeyNames.KEY_CALL_ID, callId);
		intent.putExtra(IntentKeyNames.KEY_CALL_TYPE, type);
		intent.putExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID, memberId);
		intent.putExtra(IntentKeyNames.KEY_DETAILS_SERVICE_ID, serviceId);
		intent.setClass(DetailsBusinessInfoActivity.this,
				YaoHeLaDetailsActivity.class);
		baseStartActivity(intent);
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
		String url = ContantsValues.DETAILS_GET_BUSINESS_SHOP_INFO+"&id="+mStrShopID+"&member_id="+ mLoginDataManager.getMemberId();
		http.send(HttpRequest.HttpMethod.POST,
				url, params,
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
								if (dataObject != null
										&& dataObject.optJSONArray("call") != null) {
									mLlCall.setVisibility(View.VISIBLE);

									JSONArray callArray = dataObject
											.optJSONArray("call");
									businessCallList(callArray);
									refreshCallData(mListCalls);

								} else {
									mLlCall.setVisibility(View.GONE);

								}
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						UIHelper.ToastMessage(DetailsBusinessInfoActivity.this,
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
		if (jsonObject.has("full_name")) {
			mTvShopName.setText(jsonObject.optString("full_name"));
		}
		if (jsonObject.has("content")) {
			mTvShopContent.setText(Html.fromHtml(jsonObject
					.optString("content")));
		}
		if (jsonObject.has("type")) {
			mTvType.setText(jsonObject.optString("type"));
		}
		if (jsonObject.has("address")) {
			mTvShopAddress.setText(jsonObject.optString("address"));
		}
		if (jsonObject.has("business_time")) {
			mTvBusinessTime.setText(jsonObject.optString("business_time"));
		}
		if (jsonObject.has("fans_num")) {
			mTvShopFans.setText(jsonObject.optString("fans_num") + " 粉丝");
		}
		if (jsonObject.has("subscribe_tel")) {
			mTvShopTel.setText(jsonObject.optString("subscribe_tel"));
			mStrTuiJianTel = jsonObject.optString("subscribe_tel");
		}
		if (jsonObject.has("star")) {
			mStrStar = jsonObject.optString("star");
			GlobalVariable.sShopStar = mStrStar;
			setShopStar();
		}
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
		mTvShopGuanZhu.setText(GlobalConstant.INVALID_VALUE);

	}

	/**
	 * 获取商家创建的吆喝列表
	 * 
	 * @Title businessCallList
	 * @Description
	 * @param @param callArray
	 * @return void
	 */
	private void businessCallList(JSONArray callArray) {
		if (callArray != null && callArray.length() > 0) {
			mListCalls = new ArrayList<BusinessCallInfo>();
			mBtnCallMore.setVisibility(View.GONE);
			// TODO 更多
			// if (callArray.length() > 3) {
			// mBtnCallMore.setVisibility(View.VISIBLE);
			// } else {
			// mBtnCallMore.setVisibility(View.GONE);
			// }

			if (callArray.length() == 1) {
				JSONObject callObject = callArray.optJSONObject(0);
				if (callObject!=null) {
					if (Utils.isStringEmpty(callObject.optString("id"))) {
						mLlCall.setVisibility(View.GONE);
						return;
					} else {
						mLlCall.setVisibility(View.VISIBLE);
					}
				}
			}
			for (int i = 0; i < callArray.length(); i++) {
				JSONObject callObject = callArray.optJSONObject(i);
				if (callObject!=null) {
					BusinessCallInfo callInfo = new BusinessCallInfo();

					if (callObject.has("id")) {
						callInfo.id = callObject.optString("id");
					}
					if (callObject.has("member_id")) {
						callInfo.member_id = callObject.optString("member_id");
					}
					if (callObject.has("service_id")) {
						callInfo.service_id = callObject.optString("service_id");
					}
					if (callObject.has("type")) {
						callInfo.type = callObject.optString("type");
					}
					if (callObject.has("title")) {
						callInfo.title = callObject.optString("title");
					}
					if (callObject.has("addtime")) {
						callInfo.addtime = callObject.optString("addtime");
					}
					if (callObject.has("city_id")) {
						callInfo.city_id = callObject.optString("city_id");
					}
					if (callObject.has("zan_num")) {
						callInfo.zan_num = callObject.optString("zan_num");
					}
					if (callObject.has("comment_num")) {
						callInfo.comment_num = callObject.optString("comment_num");
					}
					if (callObject.has("collection_num")) {
						callInfo.collection_num = callObject
								.optString("collection_num");
					}
					if (callObject.has("one_id")) {
						callInfo.one_id = callObject.optString("one_id");
					}
					if (callObject.has("industry_class_id")) {
						callInfo.industry_class_id = callObject
								.optString("industry_class_id");
					}
					if (callObject.has("province_id")) {
						callInfo.province_id = callObject.optString("province_id");
					}
					if (callObject.has("area_id")) {
						callInfo.area_id = callObject.optString("area_id");
					}
					if (callObject.has("district_id")) {
						callInfo.district_id = callObject.optString("district_id");
					}
					if (callObject.has("img")) {
						if (!Utils.isStringEmpty(callObject.optString("img"))) {
							callInfo.img = URLs.IMG_PRE
									+ callObject.optString("img");
						}
					}
					if (callObject.has("content")) {
						callInfo.content = callObject.optString("content");
					}
					mListCalls.add(callInfo);
				}
			}
		}

	}

	/**
	 * 设定吆喝列表数据
	 * 
	 * @Title setCallData
	 */
	private void setCallData() {
		mCallAdapter = new HomeBusinessCallAdapter(
				DetailsBusinessInfoActivity.this, mListCalls);
		mLvCall.setAdapter(mCallAdapter);

	}

	/**
	 * 刷新吆喝列表数据
	 * 
	 * @Title refreshServiceData
	 */
	private void refreshCallData(List<BusinessCallInfo> data) {
		if (data != null && data.size() > 0) {
			if (mCallAdapter != null) {
				mCallAdapter.refresh(data);
			} else {
				setCallData();
			}
		}
	}

	@Override
	protected void resetLayout() {
		RelativeLayout rlLayout = (RelativeLayout) this
				.findViewById(R.id.rl_activity_details_fujin_fuwu_root);
		SupportDisplay.resetAllChildViewParam(rlLayout);

		initTopOnlyBackTitle();
		setTopOnlyBackTitle("详细资料");
		initBottomXiaoxi();

		mLlBottomXi = (LinearLayout) this
				.findViewById(R.id.ll_details_fujin_bottom_xiaoxi);
		mScrollView = (ScrollView) this.findViewById(R.id.sv_business_info_);
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

		// 商家吆喝列表区域
		mLlCall = (LinearLayout) findViewById(R.id.ll_details_yaohe_content);
		mLvCall = (MyListView) findViewById(R.id.lv_details_fujin_yaohe_content);
		mBtnCallMore = (Button) findViewById(R.id.btn_details_yaohe_content_more);
		mBtnCallMore.setOnClickListener(this);

		// ************* 商家信息 ************* //
		// 店铺图片
		mIvShopImg = (ImageView) findViewById(R.id.iv_details_fujin_fuwu_tuijian_img);
		// 店铺名称
		mTvShopName = (TextView) findViewById(R.id.tv_details_tuijian_fuwu_name);
		// 店铺内容
		mTvShopContent = (TextView) findViewById(R.id.tv_details_fujin_shop_descri);
		// 店铺地址
		mTvShopAddress = (TextView) findViewById(R.id.tv_details_fujin_dizhi);
		mTvShopAddress.setOnClickListener(this);
		mTvShopAddressImg = (TextView) findViewById(R.id.tv_details_fujin_dizhi_img);
		mTvShopAddressImg.setOnClickListener(this);

		// 店铺电话
		mTvShopTel = (TextView) findViewById(R.id.tv_details_fujin_tel);
		mTvShopTelImg = (TextView) findViewById(R.id.tv_details_fujin_tel_img);
		mTvShopTel.setOnClickListener(this);
		mTvShopTelImg.setOnClickListener(this);

		// 关注店铺
		mTvShopGuanZhu = (TextView) findViewById(R.id.tv_details_tuijian_fuwu_guanzhu);
		mTvShopGuanZhu.setOnClickListener(this);
		// 店铺粉丝数量
		mTvShopFans = (TextView) findViewById(R.id.tv_details_tuijian_fuwu_fans);
		// 店铺所属分类
		mTvType = (TextView) findViewById(R.id.tv_details_tuijian_fuwu_meishi);
		// 店铺营业时间
		mTvBusinessTime = (TextView) findViewById(R.id.tv_details_fujin_time);
		mTvShopFans.setOnClickListener(this);

		mTvStar1 = (TextView) findViewById(R.id.tv_item_fujin_fuwu_pingjia1);
		mTvStar2 = (TextView) findViewById(R.id.tv_item_fujin_fuwu_pingjia2);
		mTvStar3 = (TextView) findViewById(R.id.tv_item_fujin_fuwu_pingjia3);
		mTvStar4 = (TextView) findViewById(R.id.tv_item_fujin_fuwu_pingjia4);
		mTvStar5 = (TextView) findViewById(R.id.tv_item_fujin_fuwu_pingjia5);

		mTvNewProduct = (TextView) findViewById(R.id.tv_business_service_xinpin_img);
		mTvActivity = (TextView) findViewById(R.id.tv_business_service_huodong_img);
		mTvCard = (TextView) findViewById(R.id.tv_business_service_card_img);
		mTvCoupon = (TextView) findViewById(R.id.tv_business_service_youhuiquan_img);

		mTvNewProduct.setOnClickListener(this);
		mTvActivity.setOnClickListener(this);
		mTvCard.setOnClickListener(this);
		mTvCoupon.setOnClickListener(this);
		// TODO 待做，关注状态
		mTvShopGuanZhu.setText(GlobalConstant.INVALID_VALUE);

		// 设定吆喝列表数据
		setCallData();

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
			if (!Utils.isStringEmpty(Utils.strFromView(mTvShopAddress))) {
				queryAddress = Utils.strFromView(mTvShopAddress);
			}
			if (!Utils.isStringEmpty(Utils.strFromView(mTvShopName))) {
				queryName = Utils.strFromView(mTvShopName);
			}
			amapGencodeInfo.setmStrQueryAddress(queryAddress);
			amapGencodeInfo.setmStrQueryName(queryName);
			intent.setClass(DetailsBusinessInfoActivity.this,
					ShowGeocoderActivity.class);
			intent.putExtra(IntentKeyNames.KEY_GEOCODER_LOCATION,
					amapGencodeInfo);
			baseStartActivity(intent);

			break;

		case R.id.tv_details_tuijian_fuwu_guanzhu: // 关注店铺
			if (!AppApplacation.getInstance().isNetworkConnected()) { // 网络检查
				UIHelper.ToastMessage(DetailsBusinessInfoActivity.this,
						getResources().getString(R.string.network_disabled));

			} else if (!mLoginDataManager.getLoginState().equals("1")) {
				UIHelper.ToastMessage(DetailsBusinessInfoActivity.this,
						"您还没登录，请先登录。");
			} else {
				ApiAccess.showProgressDialog(DetailsBusinessInfoActivity.this,
						"卖力关注中...");// 关注店铺
				shopFollowApi(mLoginDataManager.getMemberId(), mStrShopID,
						ContantsValues.SHOP_FOLLOW_URL, "关注成功");
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						ApiAccess.dismissProgressDialog();
						if (!mBaseIsNotFollow) {
							if (Utils.strFromView(mTvShopGuanZhu).equals(
									GlobalConstant.INVALID_VALUE)) {
								mTvShopGuanZhu
										.setText(GlobalConstant.VALID_VALUE);
								mTvShopGuanZhu
										.setBackgroundResource(R.drawable.icon_home_type_yiguanzhu);
							} else {
								mTvShopGuanZhu
										.setText(GlobalConstant.INVALID_VALUE);
								mTvShopGuanZhu
										.setBackgroundResource(R.drawable.icon_fujin_jiaguanzhu);
							}
						}
					}
				}, 1500);

			}

			break;

		// 商家服务信息-点击查看详情
		case R.id.rl_business_youhuiquan:
		case R.id.tv_business_service_youhuiquan_img:
			onServiceItemClick("0");
			break;
		case R.id.rl_business_card:
		case R.id.tv_business_service_card_img:
			onServiceItemClick("1");
			break;
		case R.id.rl_business_huodong:
		case R.id.tv_business_service_huodong_img:
			onServiceItemClick("2");
			break;
		case R.id.rl_business_xinpin:
		case R.id.tv_business_service_xinpin_img:
			onServiceItemClick("3");
			break;

		case R.id.btn_details_yaohe_content_more:
			UIHelper.ToastMessage(DetailsBusinessInfoActivity.this, "更多吆喝");
			break;
		case R.id.tv_details_tuijian_fuwu_fans: // 粉丝数
			intent.setClass(DetailsBusinessInfoActivity.this,
					BusinessMyFansActivity.class);
			intent.putExtra("BusinessFansMember", mStrMemberID);
			baseStartActivity(intent);
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
		intent.setClass(DetailsBusinessInfoActivity.this,
				DetailsServiceActivity.class);
		baseStartActivity(intent);
	}

	/**
	 * 附近店铺点评
	 */
	@Override
	protected void nearByComment() {
		super.nearByComment();
		Intent intent = new Intent();
		intent.setClass(DetailsBusinessInfoActivity.this,
				DetailsBusinessPinlunActivity.class);
		Bundle bundle = new Bundle();
		intent.putExtra(IntentKeyNames.KEY_SHOP_COMMENT_ID, mStrShopID);
		intent.putExtras(bundle);
		baseStartActivity(intent);
	}

	/**
	 * 附近店铺发消息
	 */
	@Override
	protected void nearByMessage() {
		super.nearByMessage();
		if (mLoginDataManager.getMemberId().equals(mStrMemberID)) {
			Intent intent = new Intent();
			intent.setClass(DetailsBusinessInfoActivity.this,
					BusinessMyFansActivity.class);
			baseStartActivity(intent);
		} else {

			Intent intent = new Intent();
			intent.setClass(DetailsBusinessInfoActivity.this,
					FuJinXiaoXiActivity.class);
			intent.putExtra(IntentKeyNames.KEY_NEAR_MESSAGE_SHOP_ID, mStrShopID);
			baseStartActivity(intent);
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		mStrShopID = getStringExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID);
		mStrMemberID = getStringExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID);
		CCLog.i("商家个人详情:", "member_id :" + mStrMemberID + "shop_id : "
				+ mStrShopID);
	}

	private void setShopStar() {
		if (!Utils.isStringEmpty(mStrStar)) {
			if (mStrStar.equals("1")) {
				mTvStar1.setBackgroundResource(R.drawable.icon_fujin_xing_red);
				mTvStar2.setBackgroundResource(R.drawable.icon_fujin_xing_gray);
				mTvStar3.setBackgroundResource(R.drawable.icon_fujin_xing_gray);
				mTvStar4.setBackgroundResource(R.drawable.icon_fujin_xing_gray);
				mTvStar5.setBackgroundResource(R.drawable.icon_fujin_xing_gray);
			} else if (mStrStar.equals("2")) {
				mTvStar1.setBackgroundResource(R.drawable.icon_fujin_xing_red);
				mTvStar2.setBackgroundResource(R.drawable.icon_fujin_xing_red);
				mTvStar3.setBackgroundResource(R.drawable.icon_fujin_xing_gray);
				mTvStar4.setBackgroundResource(R.drawable.icon_fujin_xing_gray);
				mTvStar5.setBackgroundResource(R.drawable.icon_fujin_xing_gray);

			} else if (mStrStar.equals("3")) {
				mTvStar1.setBackgroundResource(R.drawable.icon_fujin_xing_red);
				mTvStar2.setBackgroundResource(R.drawable.icon_fujin_xing_red);
				mTvStar3.setBackgroundResource(R.drawable.icon_fujin_xing_red);
				mTvStar4.setBackgroundResource(R.drawable.icon_fujin_xing_gray);
				mTvStar5.setBackgroundResource(R.drawable.icon_fujin_xing_gray);

			} else if (mStrStar.equals("4")) {
				mTvStar1.setBackgroundResource(R.drawable.icon_fujin_xing_red);
				mTvStar2.setBackgroundResource(R.drawable.icon_fujin_xing_red);
				mTvStar3.setBackgroundResource(R.drawable.icon_fujin_xing_red);
				mTvStar4.setBackgroundResource(R.drawable.icon_fujin_xing_red);
				mTvStar5.setBackgroundResource(R.drawable.icon_fujin_xing_gray);

			} else if (mStrStar.equals("5")) {
				mTvStar1.setBackgroundResource(R.drawable.icon_fujin_xing_red);
				mTvStar2.setBackgroundResource(R.drawable.icon_fujin_xing_red);
				mTvStar3.setBackgroundResource(R.drawable.icon_fujin_xing_red);
				mTvStar4.setBackgroundResource(R.drawable.icon_fujin_xing_red);
				mTvStar5.setBackgroundResource(R.drawable.icon_fujin_xing_red);

			}
		}
	}

}
