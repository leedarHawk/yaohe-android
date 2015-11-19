package com.collcloud.yaohe.activity.details.youhui;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.details.fujinshop.DetailsBusinessInfoActivity;
import com.collcloud.yaohe.activity.details.yaohela.PicFullScreenShowActivity;
import com.collcloud.yaohe.activity.details.yaohela.YaoHeLaDetailsActivity;
import com.collcloud.yaohe.activity.dianpin.fujin.DetailsCallPinlunActivity;
import com.collcloud.yaohe.activity.jubao.JuBaoActivity;
import com.collcloud.yaohe.activity.map.ShowGeocoderActivity;
import com.collcloud.yaohe.api.ApiAccess;
import com.collcloud.yaohe.api.ApiAccessErrorManager;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.api.info.DetailsCouponInfo;
import com.collcloud.yaohe.api.info.DetailsCouponInfo.Coupon;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.GlobalConstant;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.adapter.HomeYaoHeGridViewAdapter;
import com.collcloud.yaohe.ui.model.AmapGencodeInfo;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.GsonUtils;
import com.collcloud.yaohe.ui.utils.UIHelper;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.ui.view.MyGridView;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * 优惠券详情信息内容
 * 
 * @ClassName YouHuiDetailsActivity
 * @Description
 * @author CollCloud_小米
 */
public class YouHuiDetailsActivity extends BaseActivity implements
		OnItemClickListener, OnClickListener {
	/** 活动相关图片 */
	private MyGridView mGvList;
	private HomeYaoHeGridViewAdapter mGvAdapter;
	private List<String> mImgPaths = new ArrayList<String>();
	private LinearLayout mLlImgsLayout = null;

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
	private String mStrCouponId;
	private String mStrCallId;
	/**
	 * 会员Id
	 */
	private String mStrMemberId;
	/**
	 * API获取的电话号码
	 */
	private String mStrTuiJianTel = null;
	/**
	 * API获取的客服
	 */
	private String mStrTuiJianKefu = null;
	/**
	 * API获取的是否领取了优惠券
	 */
	private String mStrGive = null;

	// ***********　推荐店铺信息 *********** //
	/**
	 * 优惠券ID
	 */
	private String mStrCouponID = null;
	/**
	 * 优惠券详情内容
	 */
	private DetailsCouponInfo mCouponDetail;
	private Coupon mCouponInfo;

	// 优惠券详细信息
	/**
	 * 优惠券标题
	 */
	private TextView mTvCouponTitle;
	/**
	 * 优惠券使用范围
	 */
	private TextView mTvCouponContent;
	/**
	 * 优惠券时间
	 */
	private TextView mTvCouponTime;
	/**
	 * 优惠券发行量
	 */
	private TextView mTvNum;
	/**
	 * 0满减券 1满赠券 2代金券 3折扣券
	 */
	private TextView mTvType;
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
	 * 使用：多次
	 */
	private TextView mTvUse;
	/**
	 * 获取优惠券
	 */
	private Button mBtnGetCoupon;
	private int mRequestCount = 0;

	private String mStrZanNum;
	private String mStrCollectionNum;
	private String mStrCommentNum;
	//总发行量
	private int totalFaxingCount=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details_youhui);
		mStrCouponID = getStringExtra(IntentKeyNames.KEY_DETAILS_SERVICE_ID);
		mStrShopID = getStringExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID);
		mStrMemberId = getStringExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID);
		mStrCallId = getStringExtra(IntentKeyNames.KEY_CALL_ID);
		CCLog.i("接收", "优惠券 id : " + mStrCouponID + " shop_id: " + mStrShopID
				+ " member_id: " + mStrMemberId);
		// 关注店铺状态信息查询
		isFollow(mLoginDataManager.getMemberId(), mStrShopID,
				ContantsValues.SHOP_FOLLOW_STATUS_URL);
		initView();

		// 获取优惠券详情
		getCouponDetail();
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
//										if (errorJsonObject.has("data")) {
//											mBaseIsFollow = errorJsonObject.optBoolean("data");
//										}
										
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
	 * 画面初期化时，相关处理
	 */
	private void initView() {
		// 设定优惠券详情图片内容
		setCouponImgInfo();
		mTvTuiJianGuanZhu.setText(GlobalConstant.INVALID_VALUE);

		mStrZanNum = getStringExtra(IntentKeyNames.KEY_ZAN_NUM);
		mStrCollectionNum = getStringExtra(IntentKeyNames.KEY_COLLECTION_NUM);
		mStrCommentNum = getStringExtra(IntentKeyNames.KEY_COMMENT_NUM);
		if (!Utils.isStringEmpty(mStrZanNum)) {
			mTvBaseBottomZan.setText(mStrZanNum);
		}
		if (!Utils.isStringEmpty(mStrCollectionNum)) {
			mTvBaseBottomShouCang.setText(mStrCollectionNum);
		}
		if (!Utils.isStringEmpty(mStrCommentNum)) {
			mTvBaseBottomPinLun.setText(mStrCommentNum);
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

	/**
	 * 获取优惠券详情内容
	 * 
	 * @Title getCouponDetail
	 */
	private void getCouponDetail() {

		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", mStrCouponID);
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());

		http.send(HttpRequest.HttpMethod.POST,
				ContantsValues.DETAILS_GET_COUPON, params,
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
								if (responseInfo.result != null) {
									CCLog.i("优惠券ID " + mStrCouponID
											+ "-->详情内容: ", responseInfo.result);
								}
								mCouponDetail = GsonUtils.json2Bean(
										responseInfo.result,
										DetailsCouponInfo.class);
								if (mCouponDetail != null
										&& mCouponDetail.data != null) {
									mCouponInfo = mCouponDetail.data;
									if (mCouponInfo.give != null) {
										mStrGive = mCouponInfo.give;
										if (mStrGive.equals("0")) {
											mBtnGetCoupon.setText("获取优惠券");
											mBtnGetCoupon
													.setTextColor(getResources()
															.getColor(
																	R.color.common_text_color_chengse));
										} else if (mStrGive.equals("1")) {
											mBtnGetCoupon.setText("已获取优惠券");
											mBtnGetCoupon
													.setTextColor(getResources()
															.getColor(
																	R.color.common_home_text_color_fans_time));
										}
									}
									if (mCouponInfo.id != null) {
										mStrCouponId = mCouponInfo.id;
									}
									if (mCouponInfo.shop_id != null) {
										mStrShopID = mCouponInfo.shop_id;
									}
									if (mCouponInfo.shop_name != null) {
										mTvTuiJianName
												.setText(mCouponInfo.shop_name);
									}
									if (mCouponInfo.shop_address != null) {
										mTvTuiJianAddress
												.setText(mCouponInfo.shop_address);
									}
									if (mCouponInfo.shop_fans_num != null) {
										mTvTuijianFans
												.setText(mCouponInfo.shop_fans_num
														+ " 粉丝");
										fansCount = Integer.parseInt(mCouponInfo.shop_fans_num);
									}
									if (mCouponInfo.shop_subscribe_tel != null) {
										mStrTuiJianTel = mCouponInfo.shop_subscribe_tel;
										mStrTuiJianKefu = mCouponInfo.shop_subscribe_tel;
									}

									// 优惠券内容
									if (mCouponInfo.title != null) {
										mTvCouponTitle
												.setText(mCouponInfo.title);
									}

									if (mCouponInfo.zan_num != null) {
										mTvBaseBottomZan
												.setText(mCouponInfo.zan_num);
									}
									if (mCouponInfo.comment_num != null) {
										mTvBaseBottomPinLun
												.setText(mCouponInfo.comment_num);
									}
									if (mCouponInfo.collection_num != null) {
										mTvBaseBottomShouCang
												.setText(mCouponInfo.collection_num);
									}

									if (mCouponInfo.content != null) {
										mTvCouponContent
												.setText(mCouponInfo.content);
									}
									if (mCouponInfo.addtime != null) {
										if (mCouponInfo.addtime.length() == 10) {

											// 确保long型日期/时间值是正确的，比如检测长度，是否少了最后的毫秒数
											mCouponInfo.addtime = mCouponInfo.addtime
													+ "000";
											SimpleDateFormat df = new SimpleDateFormat(
													"yyyy-MM-dd HH:mm");
											Date times = new Date(
													Long.valueOf(mCouponInfo.addtime));
											mTvCouponTime.setText(df
													.format(times));
										}
									}
									if (mCouponInfo.type != null) {
										mTvType.setText(exchangeType(mCouponInfo.type));
									}
									if (mCouponInfo.valid_start != null) {
										startDate = mCouponInfo.valid_start;
										date.append(startDate + " 至 ");
									}
									if (mCouponInfo.valid_end != null) {
										endDate = mCouponInfo.valid_end;
										date.append(endDate);
									}
									if (!Utils.isStringEmpty(date.toString())) {
										mTvValid.setText(date.toString());
									}
									if (mCouponInfo.num != null) {
										mTvNum.setText(mCouponInfo.num);
										try {
											totalFaxingCount = Integer.parseInt(mCouponInfo.num);
											if("0".equals(mStrGive)) {
												try {
													if((Integer.valueOf(mCouponInfo.draw_num) >=totalFaxingCount)){
														mBtnGetCoupon.setText("来晚啦，优惠券获取完毕");
														mBtnGetCoupon
														.setTextColor(getResources()
																.getColor(
																		R.color.common_home_text_color_fans_time));
														mBtnGetCoupon.setEnabled(false);
													}
												} catch(Exception e) {
													e.printStackTrace();
												}
											}
										} catch(Exception e) {
											e.printStackTrace();
										}
										
									}
									if (mCouponInfo.use != null) {
										if (mCouponInfo.use.equals("0")) {
											mTvUse.setText("单次");
										}else if (mCouponInfo.use.equals("1")) {
											mTvUse.setText("多次");
										}
									}
									if (mCouponInfo.draw_num != null) {
										mTvHasValid.setText("已获取"
												+ mCouponInfo.draw_num + "次");
									}

									// 详情图片
									if (!Utils.isStringEmpty(mCouponInfo.img1)) {
										mImgPaths.add(URLs.IMG_PRE
												+ mCouponInfo.img1);
									}
									if (!Utils.isStringEmpty(mCouponInfo.img2)) {
										mImgPaths.add(URLs.IMG_PRE
												+ mCouponInfo.img2);
									}
									if (!Utils.isStringEmpty(mCouponInfo.img3)) {
										mImgPaths.add(URLs.IMG_PRE
												+ mCouponInfo.img3);
									}
									if (!Utils.isStringEmpty(mCouponInfo.img4)) {
										mImgPaths.add(URLs.IMG_PRE
												+ mCouponInfo.img4);
									}
									if (!Utils.isStringEmpty(mCouponInfo.img5)) {
										mImgPaths.add(URLs.IMG_PRE
												+ mCouponInfo.img5);
									}
									if (!Utils.isStringEmpty(mCouponInfo.img6)) {
										mImgPaths.add(URLs.IMG_PRE
												+ mCouponInfo.img6);
									}
									if (mImgPaths.size() > 0) {
										mLlImgsLayout
												.setVisibility(View.VISIBLE);
										// 设定详情图片信息
										setCouponImgInfo();
									} else {
										mLlImgsLayout.setVisibility(View.GONE);
									}

								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						UIHelper.ToastMessage(YouHuiDetailsActivity.this,
								R.string.response_data_invalid);
					}
				});

	}

	/**
	 * 设定优惠券详情图片内容
	 * 
	 * @Title setCouponImgInfo
	 */
	private void setCouponImgInfo() {
		mGvAdapter = new HomeYaoHeGridViewAdapter(YouHuiDetailsActivity.this,
				mImgPaths);
		mGvList.setAdapter(mGvAdapter);
	}

	@Override
	protected void resetLayout() {
		RelativeLayout rlLayout = (RelativeLayout) this
				.findViewById(R.id.rl_activity_details_youhui_root);
		SupportDisplay.resetAllChildViewParam(rlLayout);

		mGvList = (MyGridView) this
				.findViewById(R.id.gv_activity_details_youhuiquan);
		mGvList.setOnItemClickListener(this);
		mLlImgsLayout = (LinearLayout) findViewById(R.id.ll_activity_details_youhuiquan);

		initTopTitle(); // 头部信息
		setTopTitleWithShare("信息详情");
		initBottomPinlunShoucang(); // 底部收藏，评论等
		initSharePlatforms();

		// 优惠券标题
		mTvCouponTitle = (TextView) findViewById(R.id.tv_details_youhui_title);
		// 优惠券使用范围
		mTvCouponContent = (TextView) findViewById(R.id.tv_details_youhui_content);
		// 优惠券时间
		mTvCouponTime = (TextView) findViewById(R.id.tv_details_youhui_time);
		// 优惠券发行量
		mTvNum = (TextView) findViewById(R.id.tv_details_youhui_youxiaoqi);
		// 0满减券 1满赠券 2代金券 3折扣券
		mTvType = (TextView) findViewById(R.id.tv_details_youhui_zhekou);
		// 有效期
		mTvValid = (TextView) findViewById(R.id.tv_details_shiyong_youhui_fanwei);
		// 使用：多次、单次
		mTvUse = (TextView) findViewById(R.id.tv_details_youhui_yuyue);
		// 已获取数量
		mTvHasValid = (TextView) findViewById(R.id.tv_details_get_vip_youhui_count);
		// 规则提醒
		mTvGuiZe = (TextView) findViewById(R.id.tv_details_youhui_guize);
		// 获取优惠券
		mBtnGetCoupon = (Button) findViewById(R.id.btn_details_get_vip_youhui);
		mBtnGetCoupon.setOnClickListener(this);

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
	public void onItemClick(AdapterView<?> group, View view, int position,
			long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(YouHuiDetailsActivity.this,
				PicFullScreenShowActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("imgPath", (Serializable) mImgPaths);
		bundle.putInt("startIndex", position);
		intent.putExtras(bundle);
		baseStartActivity(intent);

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
			intent.setClass(YouHuiDetailsActivity.this,
					ShowGeocoderActivity.class);
			intent.putExtra(IntentKeyNames.KEY_GEOCODER_LOCATION,
					amapGencodeInfo);
			baseStartActivity(intent);
			break;

		case R.id.rl_details_tuijian_shop_name: // 店铺名称点击
			intent.setClass(YouHuiDetailsActivity.this,
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
				UIHelper.ToastMessage(YouHuiDetailsActivity.this,
						getResources().getString(R.string.network_disabled));
				return;
			}
			if (!mLoginDataManager.getLoginState().equals("1")) {
				toastNotLogin(YouHuiDetailsActivity.this);
			} else {
				onClickFollowBtn();
			}
			break;
		// 获取优惠券
		case R.id.btn_details_get_vip_youhui:
			if (!mLoginDataManager.getLoginState().equals("1")) {
				toastNotLogin(YouHuiDetailsActivity.this);
			} else if (mRequestCount >= 1) {
				UIHelper.ToastMessage(YouHuiDetailsActivity.this, "您已经领取过了。");
				return;
			} else {
				ApiAccess.showProgressDialog(YouHuiDetailsActivity.this,
						"获取优惠券中...");
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						ApiAccess.dismissProgressDialog();
						// 获取优惠券
						getCoupon();
					}
				}, 2000);
			}
			break;
		default:
			break;
		}

	}

	/**
	 * 获取优惠券
	 */
	private void getCoupon() {

		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", mStrCouponID);
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.SHOP_GET_COUPON,
				params, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {

						try {
							// 数据处理
							JSONObject jsonObject = new JSONObject(
									responseInfo.result);
							if (responseInfo.result != null) {
								CCLog.i("获取优惠券状态：", " " + responseInfo.result);
							}
							if (jsonObject.has("status")) {
								JSONObject statusObject = jsonObject
										.optJSONObject("status");
								int code = statusObject.optInt("code");
								if (code == 1) {
									String strErrorMsg = statusObject
											.optString("message");
									UIHelper.ToastMessage(
											YouHuiDetailsActivity.this,
											strErrorMsg);
								} else {
									mRequestCount++;
									UIHelper.ToastMessage(
											YouHuiDetailsActivity.this,
											"恭喜你，获取优惠券成功。");
									if (mCouponInfo != null
											&& mCouponInfo.draw_num != null) {
										mTvHasValid.setText("已获取"
												+ (Integer
														.valueOf(mCouponInfo.draw_num) + 1)
												+ "次");
									}
									mBtnGetCoupon.setText("已获取优惠券");
									mBtnGetCoupon
											.setTextColor(getResources()
													.getColor(
															R.color.common_home_text_color_fans_time));
								}
							}
						} catch (Exception e) {
						}

					}

					@Override
					public void onFailure(HttpException error, String msg) {
						UIHelper.ToastMessage(YouHuiDetailsActivity.this,
								R.string.response_data_invalid);
					}
				});

	}

	// *********** 点评、收藏、点赞 *********** //
	/**
	 * 点评
	 */
	@Override
	protected void callComment() {
		super.callComment();

		if (!mLoginDataManager.getLoginState().equals("1")) {
			UIHelper.ToastMessage(YouHuiDetailsActivity.this, "您还没登录，请先登录。");
		} else {
			Intent intent = new Intent(YouHuiDetailsActivity.this,
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
			UIHelper.ToastMessage(YouHuiDetailsActivity.this, "您还没登录，请先登录。");
		} else {
			callCollectionApi(mLoginDataManager.getMemberId(), mStrCallId,
					"收藏成功。");
			if (mIsCollectionSuccess) {

				ApiAccess.showProgressDialog(YouHuiDetailsActivity.this,
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

		ApiAccess.showProgressDialog(YouHuiDetailsActivity.this, "卖力点赞中..",
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
		Intent intent = new Intent(YouHuiDetailsActivity.this,
				JuBaoActivity.class);
		intent.putExtra(IntentKeyNames.KEY_ADD_REPORT_SHOP_ID, mStrShopID);
		baseStartActivity(intent);
	}

	/**
	 * 0满减券 1满赠券 2代金券 3折扣券
	 * 
	 * @Title exchangeType
	 * @param type
	 * @return 0满减券 1满赠券 2代金券 3折扣券
	 */
	private String exchangeType(String type) {
		String exchanged = "满减券";
		if (Utils.isStringEmpty(type)) {
			return exchanged;
		}
		if (type.equals("0")) {
			exchanged = "满减券";
		} else if (type.equals("1")) {
			exchanged = "满赠券";
		} else if (type.equals("2")) {
			exchanged = "代金券";
		} else if (type.equals("3")) {
			exchanged = "折扣券";
		}
		return exchanged;
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
