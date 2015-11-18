package com.collcloud.yaohe.activity.details.yaohela;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.details.fujinshop.DetailsBusinessInfoActivity;
import com.collcloud.yaohe.activity.details.huodong.HuoDongDetailsActivity;
import com.collcloud.yaohe.activity.details.vip.VipCardDetailsActivity;
import com.collcloud.yaohe.activity.details.xinpin.XinPinDetailsActivity;
import com.collcloud.yaohe.activity.details.youhui.YouHuiDetailsActivity;
import com.collcloud.yaohe.activity.jubao.JuBaoActivity;
import com.collcloud.yaohe.activity.map.ShowGeocoderActivity;
import com.collcloud.yaohe.api.ApiAccess;
import com.collcloud.yaohe.api.ApiAccessErrorManager;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.api.info.DetailsCallInfo;
import com.collcloud.yaohe.api.info.DetailsCallInfo.CallInfo;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.GlobalConstant;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.adapter.HomeYaoHeGridViewAdapter;
import com.collcloud.yaohe.ui.model.AmapGencodeInfo;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
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
 * 首页-吆喝啦详情页面
 * 
 * @ClassName YaoHeLaDetailsActivity
 * @author CollCloud_小米
 */
public class YaoHeLaDetailsActivity extends BaseActivity implements
		OnItemClickListener, OnClickListener {

	/** 活动相关图片 */
	private LinearLayout mLlImgsLayout;
	private MyGridView mGvList;
	private HomeYaoHeGridViewAdapter mGvAdapter;
	private List<String> mImgPaths = new ArrayList<String>();

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

	// ******* 吆喝详情信息 ****** //
	// ******* 引用服务内容相关 start******** //
	/**
	 * 引用服务区域
	 */
	private LinearLayout mLlServiceLayout = null;
	/**
	 * 引用服务的图片
	 */
	private ImageView mIvServiceImg = null;
	/**
	 * 引用服务的类型
	 */
	private ImageView mIvServiceImgTag = null;
	/**
	 * 引用服务的内容信息
	 */
	private TextView mTvServiceContent = null;
	/**
	 * 0券 1卡 2活动 3新品
	 */
	private String mStrType = null;
	/**
	 * 吆喝ID
	 */
	private String mStrServiceID = null;
	// ******* 引用服务内容相关 end******** //

	private TextView mTvTime = null;
	private TextView mTvContent = null;
	private DetailsCallInfo mDetailsCallInfo;
	private CallInfo mCallInfo;
	
	//粉丝个数
	private int fansCount=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details_yaohela);
		mStrType = getStringExtra(IntentKeyNames.KEY_CALL_TYPE);
		mStrServiceID = getStringExtra(IntentKeyNames.KEY_DETAILS_SERVICE_ID);
		mStrMemberId = getStringExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID);
		mStrShopID = getStringExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID);
		mStrCallId = getStringExtra(IntentKeyNames.KEY_CALL_ID);
		CCLog.i("吆喝啦详情 :", " 吆喝啦ID " + mStrCallId + " service_id "
				+ mStrServiceID + " shop_id: " + mStrShopID + " member_id: "
				+ mStrMemberId);
		// 关注店铺状态信息查询
		isFollow(mLoginDataManager.getMemberId(), mStrShopID,
				ContantsValues.SHOP_FOLLOW_STATUS_URL);
		initView();
		// 吆喝详情内容
		getCallDetail();
		// 设定引用服务信息
		setServiceInfo();
	}

	/**
	 * 画面初期化时，相关处理
	 */
	private void initView() {
		// 设置图片信息
		setCallImgsData();

		mTvTuiJianGuanZhu.setText(GlobalConstant.INVALID_VALUE);
	}
	
	
	
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
	 * 设定引用服务信息
	 * 
	 * @Title setServiceInfo
	 */
	private void setServiceInfo() {
		if (Utils.isStringEmpty(mStrType)) {
			mLlServiceLayout.setVisibility(View.GONE);
		} else {
			mLlServiceLayout.setVisibility(View.VISIBLE);
			if (mStrType.equals("4")) {
				mLlServiceLayout.setVisibility(View.GONE);
			} else {
				if (mStrType.equals("0")) {
					mIvServiceImgTag
							.setBackgroundResource(R.drawable.icon_home_type_youhuiquan);
				} else if (mStrType.equals("1")) {
					mIvServiceImgTag
							.setBackgroundResource(R.drawable.icon_home_type_huiyuanka);

				} else if (mStrType.equals("2")) {
					mIvServiceImgTag
							.setBackgroundResource(R.drawable.icon_home_type_huodong);

				} else if (mStrType.equals("3")) {
					mIvServiceImgTag
							.setBackgroundResource(R.drawable.icon_home_type_xinpin);

				}
			}

		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		isFollow(mLoginDataManager.getMemberId(), mStrShopID,
				ContantsValues.SHOP_FOLLOW_STATUS_URL);
	}

	/**
	 * 设置图片信息
	 * 
	 * @Title setCallImgsData
	 */
	private void setCallImgsData() {
		mGvAdapter = new HomeYaoHeGridViewAdapter(YaoHeLaDetailsActivity.this,
				mImgPaths);
		mGvList.setAdapter(mGvAdapter);
	}

	/**
	 * 获取吆喝啦详情内容
	 * 
	 * @Title getCallDetail
	 */
	@SuppressLint("SimpleDateFormat")
	private void getCallDetail() {   
		ApiAccess.showProgressDialog(this, "数据加载中..",
				R.style.progress_dialog);
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", mStrCallId);
		
		String url = ContantsValues.DETAILS_GET_CALL_INFO+"&id="+mStrCallId;

		http.send(HttpRequest.HttpMethod.POST,
				url, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						JSONObject jsonObject;
						ApiAccess.dismissProgressDialog();
						try {
							jsonObject = new JSONObject(responseInfo.result);
							responseErrorInfo(responseInfo);
							if (jsonObject.has("data")) {
								if (responseInfo.result != null) {
									CCLog.i("吆喝ID " + mStrCallId + "-->详情内容: ",
											responseInfo.result);
								}
								if (mImgPaths != null && mImgPaths.size() > 0) {
									mImgPaths.clear();
									mImgPaths = new ArrayList<String>();
								}
								mDetailsCallInfo = GsonUtils.json2Bean(
										responseInfo.result,
										DetailsCallInfo.class);
								if (mDetailsCallInfo != null
										&& mDetailsCallInfo.data != null) {
									mCallInfo = mDetailsCallInfo.data;
									if (mCallInfo.shop_id != null) {
										mStrShopID = mCallInfo.shop_id;
									}
									if (mCallInfo.shop_name != null) {
										mTvTuiJianName
												.setText(mCallInfo.shop_name);
									}
									if (mCallInfo.shop_address != null) {
										mTvTuiJianAddress
												.setText(mCallInfo.shop_address);
									}
									if (mCallInfo.shop_fans_num != null) {
										mTvTuijianFans
												.setText(mCallInfo.shop_fans_num
														+ " 粉丝");
										fansCount = Integer.parseInt(mCallInfo.shop_fans_num);
									}
									if (mCallInfo.shop_subscribe_tel != null) {
										mStrTuiJianTel = mCallInfo.shop_subscribe_tel;
									}
									if (mCallInfo.zan_num != null) {
										mTvBaseBottomZan
												.setText(mCallInfo.zan_num);
									}
									if (mCallInfo.comment_num != null) {
										mTvBaseBottomPinLun
												.setText(mCallInfo.comment_num);
									}
									if (mCallInfo.collection_num != null) {
										mTvBaseBottomShouCang
												.setText(mCallInfo.collection_num);
									}

									// 新品发布的时候没有标题，所以服务器返回的content这个字段只适用于优惠券，会员卡，活动
									// 新品的时候，字段里多了一个title，所以用这个显示。
									if (!Utils.isStringEmpty(mCallInfo.type)
											&& mCallInfo.type.equals("3")) {
										if (mCallInfo.title != null) {
											mTvContent.setText(mCallInfo.title);
										}
									} else {

										if (mCallInfo.content != null) {
											mTvContent
													.setText(mCallInfo.content);
										}
									}
									if (mCallInfo.addtime != null) {
										if (mCallInfo.addtime.length() == 10) {
											try {
												// 确保long型日期/时间值是正确的，比如检测长度，是否少了最后的毫秒数
												mCallInfo.addtime = mCallInfo.addtime
														+ "000";

												SimpleDateFormat df = new SimpleDateFormat(
														"yyyy-MM-dd HH:mm");
												Date times = new Date(
														Long.valueOf(mCallInfo.addtime));
												mTvTime.setText(df
														.format(times));
											} catch (NumberFormatException e) {
												mTvTime.setText(mCallInfo.addtime);
											}
										} else {
											mTvTime.setText(mCallInfo.addtime);
										}
									}
									// ****** 引用服务标题和图片 start*******//
									if (mCallInfo.s_title != null) {
										mTvServiceContent
												.setText(mCallInfo.s_title);
									}
									if (mCallInfo.type != null) {
										mStrType = mCallInfo.type;
									}
									if (!Utils.isStringEmpty(mCallInfo.s_img)) {
										String url = URLs.IMG_PRE
												+ mCallInfo.s_img;

										RequestQueue mQueue = Volley
												.newRequestQueue(YaoHeLaDetailsActivity.this);
										ImageLoader imageLoader = new ImageLoader(
												mQueue, new BitmapCache());
										ImageListener listener = ImageLoader
												.getImageListener(
														mIvServiceImg,
														R.drawable.icon_yaohe_loading_default,
														R.drawable.icon_yaohe_loading_default);
										imageLoader.get(url, listener,getResources().getDimensionPixelSize(R.dimen.max_list_width),getResources().getDimensionPixelSize(R.dimen.max_list_height));
									}
									// ****** 引用服务标题和图片 end *******//
									// 详情图片
									if (!Utils.isStringEmpty(mCallInfo.img1)) {
										mImgPaths.add(URLs.IMG_PRE
												+ mCallInfo.img1);
									}
									if (!Utils.isStringEmpty(mCallInfo.img2)) {
										mImgPaths.add(URLs.IMG_PRE
												+ mCallInfo.img2);
									}
									if (!Utils.isStringEmpty(mCallInfo.img3)) {
										mImgPaths.add(URLs.IMG_PRE
												+ mCallInfo.img3);
									}
									if (!Utils.isStringEmpty(mCallInfo.img4)) {
										mImgPaths.add(URLs.IMG_PRE
												+ mCallInfo.img4);
									}
									if (!Utils.isStringEmpty(mCallInfo.img5)) {
										mImgPaths.add(URLs.IMG_PRE
												+ mCallInfo.img5);
									}
									if (!Utils.isStringEmpty(mCallInfo.img6)) {
										mImgPaths.add(URLs.IMG_PRE
												+ mCallInfo.img6);
									}
									if (mImgPaths.size() > 0) {
										mLlImgsLayout
												.setVisibility(View.VISIBLE);
										// 设定详情图片信息
										setCallImgsData();
									} else {
										mLlImgsLayout.setVisibility(View.GONE);
									}

								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
							ApiAccess.dismissProgressDialog();
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						ApiAccess.dismissProgressDialog();
						UIHelper.ToastMessage(YaoHeLaDetailsActivity.this,
								R.string.response_data_invalid);
					}
				});

	}

	@Override
	protected void resetLayout() {
		RelativeLayout rlLayout = (RelativeLayout) this
				.findViewById(R.id.rl_activity_details_yaohela_root);
		SupportDisplay.resetAllChildViewParam(rlLayout);

		mGvList = (MyGridView) this
				.findViewById(R.id.gv_activity_details_yaohe);
		mLlImgsLayout = (LinearLayout) this
				.findViewById(R.id.ll_activity_details_yaohe);
		mGvList.setOnItemClickListener(this);

		initTopTitle(); // 头部信息
		setTopTitleWithShare("信息详情");
		initBottomPinlunShoucang(); // 底部收藏，评论等
		initSharePlatforms();

		// ********* 引用服务 ********** //
		mLlServiceLayout = (LinearLayout) findViewById(R.id.ll_yaohela_fuwu_image_content);
		mTvServiceContent = (TextView) findViewById(R.id.tv_details_yaohele_fuwu_text);
		mTvServiceContent.setOnClickListener(this);
		mIvServiceImg = (ImageView) findViewById(R.id.iv_details_yaohele_fuwu_image);
		mIvServiceImgTag = (ImageView) findViewById(R.id.iv_details_yaohele_fuwu_image_tag);
		// ********* 引用服务 ********** //
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

		// 详情内容
		mTvTime = (TextView) findViewById(R.id.tv_details_type_title);
		mTvContent = (TextView) findViewById(R.id.tv_details_content);

	}

	@Override
	public void onItemClick(AdapterView<?> group, View view, int position,
			long arg3) {
		Intent intent = new Intent(YaoHeLaDetailsActivity.this,
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
			intent.setClass(YaoHeLaDetailsActivity.this,
					ShowGeocoderActivity.class);
			intent.putExtra(IntentKeyNames.KEY_GEOCODER_LOCATION,
					amapGencodeInfo);
			baseStartActivity(intent);
			break;
		case R.id.rl_details_tuijian_shop_name: // 店铺名称点击
			intent.setClass(YaoHeLaDetailsActivity.this,
					DetailsBusinessInfoActivity.class);
			intent.putExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID, mStrShopID);
			intent.putExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID, mStrMemberId);
			baseStartActivity(intent);
			break;
		case R.id.rl_details_tuijian_dianhua: // 拨打电话
			telDialog("确定要拨打电话吗？\n" + mStrTuiJianTel, mStrTuiJianTel);
			break;
		case R.id.rl_details_tuijian_kefu: // 客服
			// TODO 客服信息
			telDialog("确定要拨打电话吗？\n" + mStrTuiJianTel, mStrTuiJianTel);
			// intent.setClass(YaoHeLaDetailsActivity.this,
			// ChattingActivity.class);
			// intent.putExtra("ACCOUNTTO", "");
			// intent.putExtra("NICKNAME", "昵称");
			// baseStartActivity(intent);
			break;
		case R.id.tv_details_tuijian_guanzhu: // 关注

			if (!AppApplacation.getInstance().isNetworkConnected()) { // 网络检查
				UIHelper.ToastMessage(YaoHeLaDetailsActivity.this,
						getResources().getString(R.string.network_disabled));
				return;
			}
			if (!mLoginDataManager.getLoginState().equals("1")) {
				toastNotLogin(YaoHeLaDetailsActivity.this);
			} else {
				onClickFollowBtn();
			}
			break;
		// 点击引用服务的内容
		case R.id.tv_details_yaohele_fuwu_text:
			doService();
			break;
		default:
			break;
		}

	}
	
	/**
	 *当点击关注按钮后
	 */
	private void onClickFollowBtn() {
		String text = mTvTuiJianGuanZhu.getText().toString();
		if(GlobalConstant.VALID_VALUE.equals(text)) {
			ApiAccess.showProgressDialog(YaoHeLaDetailsActivity.this,
					"正在取消关注中...");// 取消关注店铺
			cancelFollows(mStrShopID);
		} else {
			ApiAccess.showProgressDialog(YaoHeLaDetailsActivity.this,
					"卖力关注中...");// 关注店铺
			shopFollowApi(mLoginDataManager.getMemberId(), mStrShopID,
					ContantsValues.SHOP_FOLLOW_URL, "关注成功");
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		mStrType = getStringExtra(IntentKeyNames.KEY_CALL_TYPE);
		mStrServiceID = getStringExtra(IntentKeyNames.KEY_DETAILS_SERVICE_ID);
		mStrMemberId = getStringExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID);
		mStrShopID = getStringExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID);
		mStrCallId = getStringExtra(IntentKeyNames.KEY_CALL_ID);
		// 吆喝详情内容
		getCallDetail();
		// 设定引用服务信息
		setServiceInfo();
	}

	/**
	 * 引用服务点击事件
	 * 
	 * @Title doService
	 */
	private void doService() {
		Intent intent = new Intent();
		intent.putExtra(IntentKeyNames.KEY_DETAILS_SERVICE_ID, mStrServiceID);
		intent.putExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID, mStrShopID);
		intent.putExtra(IntentKeyNames.KEY_CALL_ID, mStrCallId);
		intent.putExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID, mStrMemberId);
		// 点赞，评论，收藏
		intent.putExtra(IntentKeyNames.KEY_ZAN_NUM,
				Utils.strFromView(mTvBaseBottomZan));
		intent.putExtra(IntentKeyNames.KEY_COLLECTION_NUM,
				Utils.strFromView(mTvBaseBottomShouCang));
		intent.putExtra(IntentKeyNames.KEY_COMMENT_NUM,
				Utils.strFromView(mTvBaseBottomPinLun));
		if (mStrType.equals("0")) {
			intent.setClass(YaoHeLaDetailsActivity.this,
					YouHuiDetailsActivity.class);
			baseStartActivity(intent);
		} else if (mStrType.equals("1")) {
			intent.setClass(YaoHeLaDetailsActivity.this,
					VipCardDetailsActivity.class);
			baseStartActivity(intent);

		} else if (mStrType.equals("2")) {
			intent.setClass(YaoHeLaDetailsActivity.this,
					HuoDongDetailsActivity.class);
			baseStartActivity(intent);

		} else if (mStrType.equals("3")) {
			intent.setClass(YaoHeLaDetailsActivity.this,
					XinPinDetailsActivity.class);
			baseStartActivity(intent);

		}
	}

	// *********** 店铺点评、店铺收藏、店铺点赞 *********** //
	/**
	 * 点评
	 */
	@Override
	protected void callComment() {
		super.callComment();

		Intent intent = new Intent(YaoHeLaDetailsActivity.this,
				YaoHeCommentActivity.class);
		intent.putExtra(IntentKeyNames.KEY_CALL_COMMENT_ID, mStrCallId);
		intent.putExtra("YaoHeType", mStrType);
		startActivityForResult(intent, 10);

		// Intent intent = new Intent(YaoHeLaDetailsActivity.this,
		// DetailsCallPinlunActivity.class);
		// Bundle bundle = new Bundle();
		// intent.putExtra(IntentKeyNames.KEY_CALL_DETAILS_COMMENT_ID,
		// mStrCallId);
		// intent.putExtra(IntentKeyNames.KEY_COMMENT_NUM,
		// Utils.strFromView(mTvBaseBottomPinLun));
		// intent.putExtras(bundle);
		// startActivityForResult(intent, 10);
	}

	/**
	 * 收藏
	 */
	@Override
	protected void callCollection() {
		super.callCollection();
		if (!mLoginDataManager.getLoginState().equals("1")) {
			UIHelper.ToastMessage(YaoHeLaDetailsActivity.this, "您还没登录，请先登录。");
		} else {
			callCollectionApi(mLoginDataManager.getMemberId(), mStrCallId,
					"收藏成功。");

			ApiAccess.showProgressDialog(YaoHeLaDetailsActivity.this,
					"卖力收藏中..", R.style.progress_dialog);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {

					ApiAccess.dismissProgressDialog();
					if (mIsCollectionSuccess) {
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
				}
			}, 500);
			;
		}

	}

	private int mPraise = 0;

	/**
	 * 点赞
	 */
	@Override
	protected void callPraise() {
		super.callPraise();
		if (mPraise > 0) {
			showToast("您已经点过赞了。");
			return;
		}
		callZanApi(mLoginDataManager.getMemberId(), mStrCallId, "点赞成功。");

		ApiAccess.showProgressDialog(YaoHeLaDetailsActivity.this, "卖力点赞中..",
				R.style.progress_dialog);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				ApiAccess.dismissProgressDialog();

				if (!Utils.isStringEmpty(Utils.strFromView(mTvBaseBottomZan))
						&& mIsZanSuccess) {
					int count = Integer.valueOf(Utils
							.strFromView(mTvBaseBottomZan)) + 1;
					mPraise = mPraise + 1;
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
		Intent intent = new Intent(YaoHeLaDetailsActivity.this,
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