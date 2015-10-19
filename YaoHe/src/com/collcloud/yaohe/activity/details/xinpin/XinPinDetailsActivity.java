package com.collcloud.yaohe.activity.details.xinpin;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.details.fujinshop.DetailsBusinessInfoActivity;
import com.collcloud.yaohe.activity.details.yaohela.PicFullScreenShowActivity;
import com.collcloud.yaohe.activity.dianpin.fujin.DetailsCallPinlunActivity;
import com.collcloud.yaohe.activity.jubao.JuBaoActivity;
import com.collcloud.yaohe.activity.map.ShowGeocoderActivity;
import com.collcloud.yaohe.api.ApiAccess;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.api.info.DetailsNewProductInfo;
import com.collcloud.yaohe.api.info.DetailsNewProductInfo.NewProduct;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.GlobalConstant;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.adapter.HomePageAdapter;
import com.collcloud.yaohe.ui.adapter.HomePageAdapter.OnPagerItemClickListener;
import com.collcloud.yaohe.ui.model.AmapGencodeInfo;
import com.collcloud.yaohe.ui.utils.CCLog;
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
 * 新品发布 信息详情内容
 * 
 * @ClassName XinPinDetailsActivity
 * @Description
 * @author CollCloud_小米
 */
@SuppressWarnings("unused")
public class XinPinDetailsActivity extends BaseActivity implements
		OnClickListener {

	/** 新品相关图片 */
	private ViewPager mPager = null;
	private ViewGroup mCirleVGroup = null;
	private HomePageAdapter mAdapter = null;
	private List<String> mImgPath = new ArrayList<String>();

	// ***********　推荐店铺信息 *********** //
	/**
	 * 推荐商家的名称
	 */
	private TextView mTvTuiJianName = null;
	private RelativeLayout mRlTuiJianName = null;
	/**
	 * 推荐商家的地址
	 */
	private TextView mTvTuiJianAddress = null;
	/**
	 * 推荐商家的电话
	 */
	private RelativeLayout mRlTuiJianTel = null;
	/**
	 * 推荐商家的客服
	 */
	private RelativeLayout mRlTuiJianKefu = null;
	/**
	 * 推荐商家关注按钮
	 */
	private TextView mTvTuiJianGuanZhu = null;
	/**
	 * 推荐商家店铺粉丝数量
	 */
	private TextView mTvTuijianFans = null;
	/**
	 * API获取的电话号码
	 */
	private String mStrTuiJianTel = null;
	/**
	 * API获取的客服
	 */
	private String mStrTuiJianKefu = null;
	/**
	 * API获取的关注状态
	 */
	private String mStrTuiJianGuanZhu = null;
	private String mStrZan_Num;
	private String mStrComment_Num;
	private String mStrCollection_Num;
	// ***********　推荐店铺信息 *********** //

	// ************　 新品详情相关内容　************ //
	private RelativeLayout mRlImgsLayout = null;
	/**
	 * 新品详情ID
	 */
	private String mStrActivityID = null;
	/**
	 * 新品详情标题
	 */
	private TextView mTvProductTitle;
	/**
	 * 新品详情内容
	 */
	private TextView mTvProductContent;
	/**
	 * 新品价格
	 */
	private TextView mTvProductPrice;
	/**
	 * 新品开始时间-结束时间
	 */
	private TextView mTvProductDate;
	/**
	 * 新品新品地点
	 */
	private TextView mTvProductAddress;
	/**
	 * 新品时间
	 */
	private TextView mTvProductTime;
	/**
	 * 新品详情内容
	 */
	private DetailsNewProductInfo mNewProductInfo;
	private NewProduct mProductInfo;
	/**
	 * 新品详情ID
	 */
	private String mStrNewProductID = null;
	/**
	 * 店铺ID
	 */
	private String mStrShopID;
	/**
	 * 会员Id
	 */
	private String mStrMemberId;
	private String mStrCallId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details_xinpin);

		initView();
		// 设定新品详情图片信息
		setNewProductImgInfo();

		mStrNewProductID = getStringExtra(IntentKeyNames.KEY_DETAILS_SERVICE_ID);
		mStrMemberId = getStringExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID);
		mStrShopID = getStringExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID);
		mStrCallId = getStringExtra(IntentKeyNames.KEY_CALL_ID);
		CCLog.i("新品详情接收 ", "新品ID " + mStrNewProductID + " shop_id: "
				+ mStrShopID + " member_id: " + mStrMemberId);
		isFollow(mLoginDataManager.getMemberId(), mStrShopID,
				ContantsValues.SHOP_FOLLOW_URL);
		getNewProductDetail();
	}

	/**
	 * 画面初期化时，相关处理
	 */
	private void initView() {
		mTvTuiJianGuanZhu.setText(GlobalConstant.INVALID_VALUE);
		String strZanNum = getStringExtra(IntentKeyNames.KEY_ZAN_NUM);
		String strCollectionNum = getStringExtra(IntentKeyNames.KEY_COLLECTION_NUM);
		String strCommentNum = getStringExtra(IntentKeyNames.KEY_COMMENT_NUM);
		if (!Utils.isStringEmpty(strZanNum)) {
			mTvBaseBottomZan.setText(strZanNum);
		}
		if (!Utils.isStringEmpty(strCollectionNum)) {
			mTvBaseBottomShouCang.setText(strCollectionNum);
		}
		if (!Utils.isStringEmpty(strCommentNum)) {
			mTvBaseBottomPinLun.setText(strCommentNum);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (mBaseIsFollow) {
					mTvTuiJianGuanZhu.setText(GlobalConstant.VALID_VALUE);
					mTvTuiJianGuanZhu
							.setBackgroundResource(R.drawable.icon_home_type_yiguanzhu);
				} else {
					mTvTuiJianGuanZhu.setText(GlobalConstant.INVALID_VALUE);
					mTvTuiJianGuanZhu
							.setBackgroundResource(R.drawable.icon_home_type_weiguanzhu);
				}
			}
		}, 1000);
	}

	/**
	 * 获取新品详情内容
	 * 
	 * @Title v
	 */
	@SuppressLint("SimpleDateFormat")
	private void getNewProductDetail() {

		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", mStrNewProductID);

		http.send(HttpRequest.HttpMethod.POST,
				ContantsValues.DETAILS_GET_NEW_PRODUCT, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(responseInfo.result);
							String startDate = "";
							String endDate = "";
							StringBuffer date = new StringBuffer();
							responseErrorInfo(responseInfo);
							if (jsonObject.has("data")) {
								if (responseInfo.result != null) {
									CCLog.i("新品 ID " + mStrNewProductID
											+ "-->详情内容: ", responseInfo.result);
								}
								mNewProductInfo = GsonUtils.json2Bean(
										responseInfo.result,
										DetailsNewProductInfo.class);
								if (mNewProductInfo != null
										&& mNewProductInfo.data != null) {
									mProductInfo = mNewProductInfo.data;
									if (mProductInfo.shop_id != null) {
										mStrShopID = mProductInfo.shop_id;
									}
									if (mProductInfo.shop_name != null) {
										mTvTuiJianName
												.setText(mProductInfo.shop_name);
									}
									if (mProductInfo.shop_address != null) {
										mTvTuiJianAddress
												.setText(mProductInfo.shop_address);
									}
									if (mProductInfo.shop_fans_num != null) {
										mTvTuijianFans
												.setText(mProductInfo.shop_fans_num
														+ " 粉丝");
									}
									if (mProductInfo.shop_subscribe_tel != null) {
										mStrTuiJianTel = mProductInfo.shop_subscribe_tel;
										mStrTuiJianKefu = mProductInfo.shop_subscribe_tel;
									}

									if (mProductInfo.zan_num != null) {
										mStrZan_Num = mProductInfo.zan_num;
									}
									if (mProductInfo.comment_num != null) {
										mStrComment_Num = mProductInfo.comment_num;
									}
									if (mProductInfo.collection_num != null) {
										mStrCollection_Num = mProductInfo.collection_num;
									}
									if (mProductInfo.title != null) {
										mTvProductTitle
												.setText(mProductInfo.title);
									}
									if (mProductInfo.content != null) {
										mTvProductContent
												.setVisibility(View.VISIBLE);
										mTvProductContent
												.setText(mProductInfo.content);
									} else {
										mTvProductContent
												.setVisibility(View.GONE);
									}

									if (mProductInfo.price != null) {
										mTvProductPrice.setText("¥ "
												+ mProductInfo.price);
									}
									if (mProductInfo.addtime != null) {
										if (mProductInfo.addtime.length() == 10) {
											// 确保long型日期/时间值是正确的，比如检测长度，是否少了最后的毫秒数
											try {
												mProductInfo.addtime = mProductInfo.addtime
														+ "000";
												SimpleDateFormat df = new SimpleDateFormat(
														"yyyy-MM-dd HH:mm");
												Date times = new Date(
														Long.valueOf(mProductInfo.addtime));
												mTvProductTime.setText(df
														.format(times));
											} catch (NumberFormatException e) {
												mTvProductTime
														.setText(mProductInfo.addtime);
											}

										} else {
											mTvProductTime
													.setText(mProductInfo.addtime);
										}
									}
									if (mProductInfo.day != null) {
										mTvProductDate.setText(mProductInfo.day
												+ " 上市");
									}
									if (!Utils.isStringEmpty(mProductInfo.img1)) {
										mImgPath.add(URLs.IMG_PRE
												+ mProductInfo.img1);
									}
									if (!Utils.isStringEmpty(mProductInfo.img2)) {
										mImgPath.add(URLs.IMG_PRE
												+ mProductInfo.img2);
									}
									if (!Utils.isStringEmpty(mProductInfo.img3)) {
										mImgPath.add(URLs.IMG_PRE
												+ mProductInfo.img3);
									}
									if (!Utils.isStringEmpty(mProductInfo.img4)) {
										mImgPath.add(URLs.IMG_PRE
												+ mProductInfo.img4);
									}
									if (!Utils.isStringEmpty(mProductInfo.img5)) {
										mImgPath.add(URLs.IMG_PRE
												+ mProductInfo.img5);
									}
									if (!Utils.isStringEmpty(mProductInfo.img6)) {
										mImgPath.add(URLs.IMG_PRE
												+ mProductInfo.img6);
									}
									if (mImgPath.size() > 0) {
										mRlImgsLayout
												.setVisibility(View.VISIBLE);
										// 设定新品详情图片信息
										setNewProductImgInfo();
									} else {
										mRlImgsLayout.setVisibility(View.GONE);
									}

								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						UIHelper.ToastMessage(XinPinDetailsActivity.this,
								R.string.response_data_invalid);
					}
				});

	}

	/**
	 * 设定新品详情图片信息
	 * 
	 * @Title setNewProductImgInfo
	 */
	private void setNewProductImgInfo() {
		mAdapter = new HomePageAdapter(XinPinDetailsActivity.this, mImgPath);
		mPager.setAdapter(mAdapter);
		mPager.setOnPageChangeListener(new ImageViewPagerChangeListener());
		// viewpager图片轮播的圆点处理
		if (mImgPath != null && mImgPath.size() > 0) {
			setmPager(mPager);
			setmCircleView(mCirleVGroup);
			setmCircleData(mImgPath);
			initViewPagerCircleView();
		}
		if (mAdapter != null) {
			mAdapter.setOnPagerItemClick(new OnPagerItemClickListener() {

				@Override
				public void onPagerItemClick(int position) {
					Intent intent = new Intent(XinPinDetailsActivity.this,
							PicFullScreenShowActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("imgPath", (Serializable) mImgPath);
					bundle.putInt("startIndex", position);
					intent.putExtras(bundle);
					baseStartActivity(intent);
				}
			});
		}
	}

	@Override
	protected void resetLayout() {
		RelativeLayout rlLayout = (RelativeLayout) this
				.findViewById(R.id.rl_activity_details_xinpin_root);
		SupportDisplay.resetAllChildViewParam(rlLayout);

		mPager = (ViewPager) this.findViewById(R.id.vp_xinpin_page_adapter_pic);
		mCirleVGroup = (LinearLayout) this
				.findViewById(R.id.ll_v_page_adapter_circle_img);

		initTopTitle(); // 头部信息
		setTopTitleWithShare("信息详情");
		initBottomPinlunShoucang(); // 底部收藏，评论等
		initSharePlatforms();

		mRlImgsLayout = (RelativeLayout) findViewById(R.id.rl_xinpin_page_adapter_pic);
		// 新品标题
		mTvProductTitle = (TextView) findViewById(R.id.tv_details_type_xinpin_title);
		// 新品内容
		mTvProductContent = (TextView) findViewById(R.id.tv_details_xinpin_content);
		// 新品开始时间-结束时间
		mTvProductDate = (TextView) findViewById(R.id.tv_details_xinpin_date);
		// 新品价格
		mTvProductPrice = (TextView) findViewById(R.id.tv_details_xinpin_price);
		// 新品时间
		mTvProductTime = (TextView) findViewById(R.id.tv_details_type_time);

		// 店铺名称
		mTvTuiJianName = (TextView) findViewById(R.id.tv_details_tuijian_huodong_name);
		mRlTuiJianName = (RelativeLayout) findViewById(R.id.rl_details_tuijian_shop_name);
		mRlTuiJianName.setOnClickListener(this);
		// 店铺地址
		mTvTuiJianAddress = (TextView) findViewById(R.id.tv_details_tuijian_dizhi);
		mTvTuiJianAddress.setOnClickListener(this);
		// 店铺电话
		mRlTuiJianTel = (RelativeLayout) findViewById(R.id.rl_details_tuijian_dianhua);
		mRlTuiJianTel.setOnClickListener(this);
		// 客服
		mRlTuiJianKefu = (RelativeLayout) findViewById(R.id.rl_details_tuijian_kefu);
		mRlTuiJianKefu.setOnClickListener(this);
		// 关注店铺
		mTvTuiJianGuanZhu = (TextView) findViewById(R.id.tv_details_tuijian_guanzhu);
		mTvTuiJianGuanZhu.setOnClickListener(this);
		// 店铺粉丝数量
		mTvTuijianFans = (TextView) findViewById(R.id.tv_details_tuijian_huodong_fans);

	}

	@Override
	public void onClick(View v) {
		if (Utils.isFastDoubleClick()) {
			return;
		}
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.tv_details_tuijian_dizhi:
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
			intent.setClass(XinPinDetailsActivity.this,
					ShowGeocoderActivity.class);
			intent.putExtra(IntentKeyNames.KEY_GEOCODER_LOCATION,
					amapGencodeInfo);
			baseStartActivity(intent);
			break;
		case R.id.rl_details_tuijian_shop_name: // 店铺名称点击
			intent.setClass(XinPinDetailsActivity.this,
					DetailsBusinessInfoActivity.class);
			intent.putExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID, mStrShopID);
			intent.putExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID, mStrMemberId);
			baseStartActivity(intent);
			break;
		case R.id.rl_details_tuijian_dianhua: // 拨打电话
			telDialog("确定要拨打电话吗？\n" + mStrTuiJianTel, mStrTuiJianTel);
			break;
		case R.id.rl_details_tuijian_kefu: // 客服
			telDialog("确定要拨打电话吗？\n" + mStrTuiJianTel, mStrTuiJianTel);
			break;
		case R.id.tv_details_tuijian_guanzhu: // 关注
			if (!AppApplacation.getInstance().isNetworkConnected()) { // 网络检查
				UIHelper.ToastMessage(XinPinDetailsActivity.this,
						getResources().getString(R.string.network_disabled));
				return;
			}
			if (!mLoginDataManager.getLoginState().equals("1")) {
				toastNotLogin(XinPinDetailsActivity.this);
			} else {
				if (mStrShopID != null) {
					CCLog.i("点击关注 ，对应的shopID ：", " " + mStrShopID);
				}
				ApiAccess.showProgressDialog(XinPinDetailsActivity.this,
						"卖力关注中...");
				// 关注店铺
				shopFollowApi(mLoginDataManager.getMemberId(), mStrShopID,
						ContantsValues.SHOP_FOLLOW_URL, "关注成功");
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						ApiAccess.dismissProgressDialog();

						if (!mBaseIsNotFollow) {
							if (Utils.strFromView(mTvTuiJianGuanZhu).equals(
									GlobalConstant.INVALID_VALUE)) {
								mTvTuiJianGuanZhu
										.setText(GlobalConstant.VALID_VALUE);
								mTvTuiJianGuanZhu
										.setBackgroundResource(R.drawable.icon_home_type_yiguanzhu);

							} else {
								mTvTuiJianGuanZhu
										.setText(GlobalConstant.INVALID_VALUE);
								mTvTuiJianGuanZhu
										.setBackgroundResource(R.drawable.icon_home_type_weiguanzhu);
							}
						}

					}
				}, 1500);
				// new Handler().postDelayed(new Runnable() {
				//
				// @Override
				// public void run() {
				// ApiAccess.dismissProgressDialog();
				// // 关注店铺
				// shopFollowApi(mLoginDataManager.getMemberId(),
				// mStrShopID, ContantsValues.SHOP_FOLLOW_URL,
				// "关注成功");
				// mTvTuiJianGuanZhu.setText(GlobalConstant.VALID_VALUE);
				// mTvTuiJianGuanZhu
				// .setBackgroundResource(R.drawable.icon_home_type_yiguanzhu);
				//
				// }
				// }, 1500);
			}
			break;
		default:
			break;
		}

	}

	// *********** 店铺点评、店铺收藏、店铺点赞 *********** //
	/**
	 * 点评
	 */
	@Override
	protected void callComment() {
		super.callComment();

		if (!mLoginDataManager.getLoginState().equals("1")) {
			UIHelper.ToastMessage(XinPinDetailsActivity.this, "您还没登录，请先登录。");
		} else {
			Intent intent = new Intent(XinPinDetailsActivity.this,
					DetailsCallPinlunActivity.class);
			Bundle bundle = new Bundle();
			intent.putExtra(IntentKeyNames.KEY_CALL_DETAILS_COMMENT_ID,
					mStrCallId);
			intent.putExtra(IntentKeyNames.KEY_COMMENT_NUM,
					Utils.strFromView(mTvBaseBottomPinLun));
			intent.putExtras(bundle);
			startActivityForResult(intent, 10);
		}
	}

	/**
	 * 收藏
	 */
	@Override
	protected void callCollection() {
		super.callCollection();
		if (!mLoginDataManager.getLoginState().equals("1")) {
			UIHelper.ToastMessage(XinPinDetailsActivity.this, "您还没登录，请先登录。");
		} else {
			callCollectionApi(mLoginDataManager.getMemberId(), mStrCallId,
					"收藏成功。");
			if (mIsCollectionSuccess) {

				ApiAccess.showProgressDialog(XinPinDetailsActivity.this,
						"卖力收藏中..", R.style.progress_dialog);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						ApiAccess.dismissProgressDialog();
						if (!Utils.isStringEmpty(Utils
								.strFromView(mTvBaseBottomShouCang))) {
							int count = Integer.valueOf(Utils
									.strFromView(mTvBaseBottomShouCang)) + 1;
							mTvBaseBottomShouCang.setText(String.valueOf(count));
							mTvBaseBottomShouCang
									.setTextColor(getResources().getColor(
											R.color.common_text_color_chengse));
							mTvBaseBottomShouCangImg
									.setBackgroundResource(R.drawable.icon_home_item_shoucang_on);
						}

					}
				}, 500);
			}
			;
		}

	}

	/**
	 * 点赞
	 */
	@Override
	protected void callPraise() {
		super.callPraise();
		callZanApi(mLoginDataManager.getMemberId(), mStrCallId, "点赞成功。");

		ApiAccess.showProgressDialog(XinPinDetailsActivity.this, "卖力点赞中..",
				R.style.progress_dialog);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				ApiAccess.dismissProgressDialog();
				if (!Utils.isStringEmpty(Utils.strFromView(mTvBaseBottomZan))) {
					int count = Integer.valueOf(Utils
							.strFromView(mTvBaseBottomZan)) + 1;
					mTvBaseBottomZan.setText(String.valueOf(count));
					mTvBaseBottomZan.setTextColor(getResources().getColor(
							R.color.common_text_color_chengse));
					mTvBaseBottomZanImg
							.setBackgroundResource(R.drawable.icon_home_item_zan_on);

				}

			}
		}, 500);
	};

	/**
	 * 写入举报
	 */
	@Override
	protected void addReport() {
		super.addReport();
		Intent intent = new Intent(XinPinDetailsActivity.this,
				JuBaoActivity.class);
		intent.putExtra(IntentKeyNames.KEY_ADD_REPORT_SHOP_ID, mStrShopID);
		baseStartActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		int currentCount = 0;
		if (!Utils.isStringEmpty(Utils.strFromView(mTvBaseBottomPinLun))) {
			currentCount = Integer.valueOf(Utils
					.strFromView(mTvBaseBottomPinLun));
		}
		CCLog.e("resultCode : ", resultCode + " ");
		switch (resultCode) {
		case 11:
			Bundle b = intent.getExtras(); // data为B中回传的Intent
			int count = b.getInt(IntentKeyNames.KEY_COMMENT_NUM);// count即为回传的值
			CCLog.e("count : ", count + " ");
			if (count > currentCount) {

				mTvBaseBottomPinLunImg
						.setBackgroundResource(R.drawable.icon_bottom_item_comment_on);
				mTvBaseBottomPinLun.setText(String.valueOf(count));
				mTvBaseBottomPinLun.setTextColor(getResources().getColor(
						R.color.common_text_color_chengse));
			}
			break;

		default:
			mTvBaseBottomPinLunImg
					.setBackgroundResource(R.drawable.icon_bottom_item_comment_off);
			mTvBaseBottomPinLun.setTextColor(getResources().getColor(
					R.color.white));
			break;
		}
	}
}
