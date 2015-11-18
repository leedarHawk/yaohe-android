package com.collcloud.yaohe.activity.details.vip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.details.fujinshop.DetailsBusinessInfoActivity;
import com.collcloud.yaohe.activity.details.yaohela.PicFullScreenShowActivity;
import com.collcloud.yaohe.activity.dianpin.fujin.DetailsCallPinlunActivity;
import com.collcloud.yaohe.activity.jubao.JuBaoActivity;
import com.collcloud.yaohe.activity.map.ShowGeocoderActivity;
import com.collcloud.yaohe.api.ApiAccess;
import com.collcloud.yaohe.api.ApiAccessErrorManager;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.api.info.DetailsCardInfo;
import com.collcloud.yaohe.api.info.DetailsCardInfo.Card;
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
 * Vip 会员卡信息详情内容
 * 
 * @ClassName VipCardDetailsActivity
 * @Description
 * @author CollCloud_小米
 */
public class VipCardDetailsActivity extends BaseActivity implements
		android.view.View.OnClickListener {

	/** 活动相关图片 */
	private ViewPager mPager = null;
	private ViewGroup mCirleVGroup = null;
	private HomePageAdapter mAdapter = null;
	private List<String> mImgPath = new ArrayList<String>();
	private RelativeLayout mRlImgsLayout = null;

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
	 * 店铺ID
	 */
	private String mStrShopID;
	/**
	 * 会员Id
	 */
	private String mStrMemberId;
	private String mStrCallId;
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

	// ***********　推荐店铺信息 *********** //

	/**
	 * 会员卡详情ID
	 */
	private String mStrCardID = null;
	/**
	 * 会员卡详情内容
	 */
	private DetailsCardInfo mCardDetails;
	private Card mCardInfo;

	// 会员卡详细信息
	/**
	 * 会员卡发行量
	 */
	private TextView mTvNum;
	/**
	 * 会员卡使用范围
	 */
	private TextView mTvContent;
	/**
	 * 有效期
	 */
	private TextView mTvValid;
	/**
	 * 已获取数量
	 */
	private TextView mTvHasValid;
	/**
	 * 预约时间
	 */
	private TextView mTvYuYue;
	/**
	 * 规则提醒
	 */
	private TextView mTvGuiZe;
	/**
	 * 获取会员卡
	 */
	private Button mBtnGetCard;
	private int mRequestCount = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details_card);
		// 会员卡ID
		mStrCardID = getStringExtra(IntentKeyNames.KEY_DETAILS_SERVICE_ID);
		mStrShopID = getStringExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID);
		mStrMemberId = getStringExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID);
		mStrCallId = getStringExtra(IntentKeyNames.KEY_CALL_ID);
		CCLog.i("接收会员卡 id :", " " + mStrCardID + " shop_id: " + mStrShopID
				+ " member_id: " + mStrMemberId);
		// 关注店铺状态信息查询
		isFollow(mLoginDataManager.getMemberId(), mStrShopID,
				ContantsValues.SHOP_FOLLOW_STATUS_URL);
		getCardDetail();
		initView();
		setCardImgInfo();

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
//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				if (mBaseIsFollow) {
//					mTvTuiJianGuanZhu.setText(GlobalConstant.VALID_VALUE);
//					mTvTuiJianGuanZhu
//							.setBackgroundResource(R.drawable.icon_home_type_yiguanzhu);
//				} else {
//					mTvTuiJianGuanZhu.setText(GlobalConstant.INVALID_VALUE);
//					mTvTuiJianGuanZhu
//							.setBackgroundResource(R.drawable.icon_home_type_weiguanzhu);
//				}
//			}
//		}, 1000);
	}
	
	
		//粉丝个数
		private int fansCount=0;
		
		/**
		 * 检测是否关注
		 */
		public boolean isFollow(String memberID, String id, String url) {
			HttpUtils http = new HttpUtils();
			RequestParams params = new RequestParams();
			params.addBodyParameter("member_id", memberID);
			params.addBodyParameter("id", id);
			url = url+"&member_id="+memberID+"&id="+id;
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
										if (errorJsonObject.has("status")) {
											JSONObject statusObject = errorJsonObject
													.optJSONObject("status");
											if (statusObject.has("code")) {
												int code = statusObject
														.optInt("code");

												CCLog.i("code：", code + " ");
												if (code == 1) {
													String strErrorMsg = statusObject
															.optString("message");
													if (strErrorMsg
															.contains("已经关注")) {
														mBaseIsFollow = true;
													}
												} else {
													mBaseIsFollow = false;
												}
											}
											//直接获取是否已经关注状态
											//mBaseIsFollow = errorJsonObject.optBoolean("data");
											CCLog.i("code：", errorJsonObject.optBoolean("data") + " ");
											CCLog.i("isFollow：", mBaseIsFollow + " ");
											
										}
									} catch (Exception e) {
										mBaseIsFollow = false;
									}
								}
							}
							setFollowStatus();
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							setFollowStatus();
						}
					});
			return mBaseIsFollow;
		}
		
		/**
		 * 设置关注状态
		 */
		private void setFollowStatus() {
			if (mBaseIsFollow) {
				mTvTuiJianGuanZhu.setText(GlobalConstant.VALID_VALUE);
				mTvTuiJianGuanZhu
						.setBackgroundResource(R.drawable.icon_home_type_yiguanzhu);
			} else {
				mTvTuiJianGuanZhu.setText(GlobalConstant.INVALID_VALUE);
				mTvTuiJianGuanZhu
						.setBackgroundResource(R.drawable.icon_fujin_jiaguanzhu);
			}
		}
		
		/**
		 * 取消关注
		 */
		private void cancelFollows(String shopID) {
			String url = ContantsValues.CANCEL_FOLLOWS + "&member_id=" + mLoginDataManager.getMemberId() + "&id=" + shopID;
			HttpUtils http = new HttpUtils();
			RequestParams params = new RequestParams();
			params.addBodyParameter("member_id", mLoginDataManager.getMemberId());
			params.addBodyParameter("id", shopID);
			CCLog.i("取消关注参数：", "member_id=" + mLoginDataManager.getMemberId()
					+ " id=" + shopID);

			http.send(HttpRequest.HttpMethod.POST, url, params,
					new RequestCallBack<String>() {

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							ApiAccess.dismissProgressDialog();
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
													UIHelper.ToastMessage(
															mBaseActivity, "取消关注成功");
													mBaseIsFollow=false;
													fansCount--;
													mTvTuijianFans.setText( fansCount+ " 粉丝");
													setFollowStatus();
													
												}
											}
										}
									} catch (Exception e) {
										String errorMsg = ApiAccessErrorManager
												.getMessage(5, mBaseActivity);
										UIHelper.ToastMessage(mBaseActivity,
												errorMsg);
										setFollowStatus();
										
									}

								}
							}
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							ApiAccess.dismissProgressDialog();
							UIHelper.ToastMessage(mBaseActivity,
									R.string.response_data_invalid);
						}
					});

		}
		
		/**
		 * 会员加店铺关注
		 * 
		 * @Title shopFollowApi
		 * @Description 会员加店铺关注API调用
		 * @param memberID
		 *            会员ID(post提交)
		 * @param id
		 *            商家ID(post提交)
		 * @param url
		 *            请求url地址
		 * @param message
		 *            自定义成功后的提示信息
		 */
		public void shopFollowApi(String memberID, String id, String url,
				final String message) {
			mBaseIsNotFollow = false;
			HttpUtils http = new HttpUtils();
			RequestParams params = new RequestParams();
			params.addBodyParameter("member_id", memberID);
			params.addBodyParameter("id", id);

			http.send(HttpRequest.HttpMethod.POST, url, params,
					new RequestCallBack<String>() {

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							ApiAccess.dismissProgressDialog();
							if (!Utils.isStringEmpty(responseInfo.result)) {
								if (responseInfo.result.contains("status")) {
									try {
										// 数据处理
										JSONObject errorJsonObject = new JSONObject(
												responseInfo.result);
										if (responseInfo.result != null) {
											CCLog.i("BaseActivity关注状态信息：",
													responseInfo.result + " ");
										}
										if (errorJsonObject.has("status")) {
											JSONObject statusObject = errorJsonObject
													.optJSONObject("status");
											if (statusObject.has("code")) {
												int code = statusObject
														.optInt("code");
												if (code == 1) {
													mBaseIsFollow = false;
													String strErrorMsg = statusObject
															.getString("message");
													UIHelper.ToastMessage(
															mBaseActivity,
															strErrorMsg);
												} else {
													mBaseIsFollow = true;
													fansCount++;
													mTvTuijianFans.setText( fansCount+ " 粉丝");
													if (!Utils
															.isStringEmpty(message)) {
														UIHelper.ToastMessage(
																mBaseActivity,
																message);
													}
												}
											}
										}
									} catch (Exception e) {

										mBaseIsFollow = false;
										String errorMsg = ApiAccessErrorManager
												.getMessage(5, mBaseActivity);
										UIHelper.ToastMessage(mBaseActivity,
												errorMsg);
									}

								}
							}
							setFollowStatus();
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							UIHelper.ToastMessage(mBaseActivity,
									R.string.response_data_invalid);
							ApiAccess.dismissProgressDialog();
							mBaseIsFollow = false;
							setFollowStatus();
						}
					});
		}
		
		
		
		/**
		 *当点击关注按钮后
		 */
		private void onClickFollowBtn() {
			String text = mTvTuiJianGuanZhu.getText().toString();
			if(GlobalConstant.VALID_VALUE.equals(text)) {
				ApiAccess.showProgressDialog(this,
						"正在取消关注中...");// 取消关注店铺
				cancelFollows(mStrShopID);
			} else {
				ApiAccess.showProgressDialog(this,
						"卖力关注中...");// 关注店铺
				shopFollowApi(mLoginDataManager.getMemberId(), mStrShopID,
						ContantsValues.SHOP_FOLLOW_URL, "关注成功");
			}
		}
		

	/**
	 * 获取会员卡活动详情内容
	 * 
	 * @Title getCardDetail
	 */
	private void getCardDetail() {

		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", mStrCardID);
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.DETAILS_GET_CARD,
				params, new RequestCallBack<String>() {

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
								mCardDetails = GsonUtils.json2Bean(
										responseInfo.result,
										DetailsCardInfo.class);
								if (responseInfo.result != null) {
									CCLog.i("会员卡ID " + mStrCardID + "-->详情内容: ",
											responseInfo.result);
								}
								if (mCardDetails != null
										&& mCardDetails.data != null) {
									mCardInfo = mCardDetails.data;
									if (mCardInfo.give != null) {
										String strGive = mCardInfo.give;
										if (strGive.equals("0")) {
											mBtnGetCard.setText("获取会员卡");
											mBtnGetCard
													.setTextColor(getResources()
															.getColor(
																	R.color.common_text_color_chengse));
										} else if (strGive.equals("1")) {
											mBtnGetCard.setText("已获取会员卡");
											mBtnGetCard
													.setTextColor(getResources()
															.getColor(
																	R.color.common_home_text_color_fans_time));
										}
									}
									if (mCardInfo.shop_name != null) {
										mTvTuiJianName
												.setText(mCardInfo.shop_name);
									}
									if (mCardInfo.shop_address != null) {
										mTvTuiJianAddress
												.setText(mCardInfo.shop_address);
									}
									if (mCardInfo.shop_fans_num != null) {
										mTvTuijianFans
												.setText(mCardInfo.shop_fans_num
														+ " 粉丝");
										fansCount = Integer.parseInt(mCardInfo.shop_fans_num);
									}
									if (mCardInfo.shop_subscribe_tel != null) {
										mStrTuiJianTel = mCardInfo.shop_subscribe_tel;
										mStrTuiJianKefu = mCardInfo.shop_subscribe_tel;
									}
									if (mCardInfo.zan_num != null) {
										mTvBaseBottomZan
												.setText(mCardInfo.zan_num);
									}
									if (mCardInfo.comment_num != null) {
										mTvBaseBottomPinLun
												.setText(mCardInfo.comment_num);
									}
									if (mCardInfo.collection_num != null) {
										mTvBaseBottomShouCang
												.setText(mCardInfo.collection_num);
									}
									// 会员卡详情，使用范围
									if (mCardInfo.content != null) {
										mTvContent.setText(mCardInfo.content);
									}
									if (mCardInfo.num != null) {
										mTvNum.setText(mCardInfo.num);
										
										try {
											int totalFaxingCount = Integer.parseInt(mCardInfo.num);
											if("0".equals(mCardInfo.give)) {
												try {
													if((Integer.valueOf(mCardInfo.draw_num) >=totalFaxingCount)){
														mBtnGetCard.setText("来晚啦，会员卡获取完毕");
														mBtnGetCard
														.setTextColor(getResources()
																.getColor(
																		R.color.common_home_text_color_fans_time));
														mBtnGetCard.setEnabled(false);
													}
												} catch(Exception e) {
													e.printStackTrace();
												}
											}
										} catch(Exception e) {
											e.printStackTrace();
										}
										
										
										
										
										
										
									}
									if (mCardInfo.shop_id != null) {
										mStrShopID = mCardInfo.shop_id;
									}
									if (mCardInfo.draw_num != null) {
										mTvHasValid.setText("已获取"
												+ mCardInfo.draw_num + "次");
									}
									if (mCardInfo.valid_start != null) {
										startDate = mCardInfo.valid_start;
										date.append(startDate + " 至 ");
									}
									if (mCardInfo.valid_end != null) {
										endDate = mCardInfo.valid_end;
										date.append(endDate);
									}
									if (!Utils.isStringEmpty(date.toString())) {
										mTvValid.setText(date.toString());
									}
									if (!Utils.isStringEmpty(mCardInfo.img1)) {
										mImgPath.add(URLs.IMG_PRE
												+ mCardInfo.img1);
									}
									if (!Utils.isStringEmpty(mCardInfo.img2)) {
										mImgPath.add(URLs.IMG_PRE
												+ mCardInfo.img2);
									}
									if (!Utils.isStringEmpty(mCardInfo.img3)) {
										mImgPath.add(URLs.IMG_PRE
												+ mCardInfo.img3);
									}
									if (!Utils.isStringEmpty(mCardInfo.img4)) {
										mImgPath.add(URLs.IMG_PRE
												+ mCardInfo.img4);
									}
									if (!Utils.isStringEmpty(mCardInfo.img5)) {
										mImgPath.add(URLs.IMG_PRE
												+ mCardInfo.img5);
									}
									if (!Utils.isStringEmpty(mCardInfo.img6)) {
										mImgPath.add(URLs.IMG_PRE
												+ mCardInfo.img6);
									}
									if (mImgPath.size() > 0) {
										mRlImgsLayout
												.setVisibility(View.VISIBLE);
										// 设定会员卡详情图片信息
										setCardImgInfo();
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
						UIHelper.ToastMessage(VipCardDetailsActivity.this,
								R.string.response_data_invalid);
					}
				});

	}

	/**
	 * 设定会员卡详情图片信息
	 * 
	 * @Title setCardImgInfo
	 */
	private void setCardImgInfo() {
		mAdapter = new HomePageAdapter(VipCardDetailsActivity.this, mImgPath);
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
					Intent intent = new Intent(VipCardDetailsActivity.this,
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
			intent.setClass(VipCardDetailsActivity.this,
					ShowGeocoderActivity.class);
			intent.putExtra(IntentKeyNames.KEY_GEOCODER_LOCATION,
					amapGencodeInfo);
			baseStartActivity(intent);
			break;
		case R.id.rl_details_tuijian_shop_name: // 店铺名称点击
			intent.setClass(VipCardDetailsActivity.this,
					DetailsBusinessInfoActivity.class);
			intent.putExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID, mStrShopID);
			intent.putExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID, mStrMemberId);
			baseStartActivity(intent);
			break;
		case R.id.rl_details_tuijian_dianhua: // 拨打电话
			telDialog("确定要拨打电话吗？\n" + mStrTuiJianTel, mStrTuiJianTel);
			break;
		case R.id.rl_details_tuijian_kefu: // 客服

			telDialog("确定要拨打电话吗？\n" + mStrTuiJianKefu, mStrTuiJianKefu);
			break;
		case R.id.tv_details_tuijian_guanzhu: // 关注

			if (!AppApplacation.getInstance().isNetworkConnected()) { // 网络检查
				UIHelper.ToastMessage(VipCardDetailsActivity.this,
						getResources().getString(R.string.network_disabled));
				return;
			}
			if (!mLoginDataManager.getLoginState().equals("1")) {
				toastNotLogin(VipCardDetailsActivity.this);
			} else {
				this.onClickFollowBtn();
			}
			break;
		case R.id.btn_details_get_vip_card:
			if (!mLoginDataManager.getLoginState().equals("1")) {
				toastNotLogin(VipCardDetailsActivity.this);
			} else if (mRequestCount >= 1) {
				UIHelper.ToastMessage(VipCardDetailsActivity.this, "您已经领取过了。");
				return;
			} else {
				ApiAccess.showProgressDialog(VipCardDetailsActivity.this,
						"获取会员卡中...");
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						ApiAccess.dismissProgressDialog();
						// 获取会员卡
						getVipCard();
					}
				}, 2000);
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 获取会员卡
	 */
	private void getVipCard() {

		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", mStrCardID);
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());
		String url = ContantsValues.SHOP_GET_CARD+"&id="+mStrCardID+"&member_id="+mLoginDataManager.getMemberId();
		http.send(HttpRequest.HttpMethod.POST, url,
				params, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						JSONObject jsonObject;
						try {
							if (responseInfo.result != null) {
								CCLog.i("获取会员卡状态：", " " + responseInfo.result);
							}
							// 数据处理
							jsonObject = new JSONObject(responseInfo.result);
							if (jsonObject.has("status")) {
								JSONObject statusObject = jsonObject
										.optJSONObject("status");

								int code = statusObject.optInt("code");
								if (code == 1) {
									String strErrorMsg = statusObject
											.getString("message");
									UIHelper.ToastMessage(
											VipCardDetailsActivity.this,
											strErrorMsg);
								} else {
									mRequestCount++;
									UIHelper.ToastMessage(
											VipCardDetailsActivity.this,
											"恭喜你，获取会员卡成功。");
									mBtnGetCard.setText("已获取会员卡");
									mBtnGetCard
											.setTextColor(getResources()
													.getColor(
															R.color.common_home_text_color_fans_time));

									if (mCardInfo != null
											&& mCardInfo.draw_num != null) {

										mTvHasValid.setText("已获取"
												+ (Integer
														.valueOf(mCardInfo.draw_num) + 1)
												+ "次");
									}
								}
							}
						} catch (Exception e) {
						}

					}

					@Override
					public void onFailure(HttpException error, String msg) {
						ApiAccess.dismissProgressDialog();
						UIHelper.ToastMessage(VipCardDetailsActivity.this,
								R.string.response_data_invalid);
					}
				});

	}

	@Override
	protected void resetLayout() {
		RelativeLayout rlLayout = (RelativeLayout) this
				.findViewById(R.id.rl_activity_details_card_root);
		SupportDisplay.resetAllChildViewParam(rlLayout);
		initTopTitle(); // 头部信息
		setTopTitleWithShare("信息详情");
		initBottomPinlunShoucang(); // 底部收藏，评论等
		initSharePlatforms();

		// 会员卡发行量
		mTvNum = (TextView) findViewById(R.id.tv_details_card_date);
		// 使用范围
		mTvContent = (TextView) findViewById(R.id.tv_details_shiyong_fanwei);
		// 有效期
		mTvValid = (TextView) findViewById(R.id.tv_details_card_youxiaoqi);
		// 已获取数量
		mTvHasValid = (TextView) findViewById(R.id.tv_activity_vipcard_yihuoqu);
		// 预约时间
		mTvYuYue = (TextView) findViewById(R.id.tv_details_card_yuyue);
		// 规则提醒
		mTvGuiZe = (TextView) findViewById(R.id.tv_details_card_guize);
		// 获取会员卡
		mBtnGetCard = (Button) findViewById(R.id.btn_details_get_vip_card);
		mBtnGetCard.setOnClickListener(this);
		mPager = (ViewPager) this.findViewById(R.id.vp_card_page_adapter_pic);
		mCirleVGroup = (ViewGroup) this
				.findViewById(R.id.ll_v_page_adapter_circle_img);
		mRlImgsLayout = (RelativeLayout) findViewById(R.id.rl_card_page_adapter_pic);

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

	// *********** 店铺点评、店铺收藏、店铺点赞 *********** //
	/**
	 * 点评
	 */
	@Override
	protected void callComment() {
		super.callComment();

		if (!mLoginDataManager.getLoginState().equals("1")) {
			UIHelper.ToastMessage(VipCardDetailsActivity.this, "您还没登录，请先登录。");
		} else {
			Intent intent = new Intent(VipCardDetailsActivity.this,
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
			UIHelper.ToastMessage(VipCardDetailsActivity.this, "您还没登录，请先登录。");
		} else {
			callCollectionApi(mLoginDataManager.getMemberId(), mStrCallId,
					"收藏成功。");
			if (mIsCollectionSuccess) {

				ApiAccess.showProgressDialog(VipCardDetailsActivity.this,
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

		ApiAccess.showProgressDialog(VipCardDetailsActivity.this, "卖力点赞中..",
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
		Intent intent = new Intent(VipCardDetailsActivity.this,
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
