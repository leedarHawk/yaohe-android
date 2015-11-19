package com.collcloud.yaohe.activity.details.huodong;

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
import com.collcloud.yaohe.api.ApiAccessErrorManager;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.api.info.DetailsActivityInfo;
import com.collcloud.yaohe.api.info.DetailsActivityInfo.HuoDong;
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
 * 活动信息详情内容
 * 
 * @ClassName HuoDongDetailsActivity
 * @Description
 * @author CollCloud_小米
 */
public class HuoDongDetailsActivity extends BaseActivity implements
		OnClickListener {

	/**
	 * 活动相关图片显示
	 */
	private ViewPager mPager = null;
	private ViewGroup mCirleVGroup = null;
	/**
	 * 图片显示适配器
	 */
	private HomePageAdapter mAdapter = null;

	/**
	 * 详情图片url列表
	 */
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

	private RelativeLayout mRlImgsLayout = null;
	/**
	 * 活动详情ID
	 */
	private String mStrActivityID = null;
	/**
	 * 活动详情内容
	 */
	private DetailsActivityInfo mActivityInfo;
	private HuoDong mHuoDongInfo;
	/**
	 * 活动详情标题
	 */
	private TextView mTvHuodongTitle;
	/**
	 * 活动详情内容
	 */
	private TextView mTvHuodongContent;
	/**
	 * 活动开始时间-结束时间
	 */
	private TextView mTvHuodongDate;
	/**
	 * 活动活动地点
	 */
	private TextView mTvHuodongAddress;
	/**
	 * 活动时间
	 */
	private TextView mTvHuodongTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details_huodong);

		mStrActivityID = getStringExtra(IntentKeyNames.KEY_DETAILS_SERVICE_ID);
		mStrShopID = getStringExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID);
		mStrMemberId = getStringExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID);
		mStrCallId = getStringExtra(IntentKeyNames.KEY_CALL_ID);
		CCLog.i("活动详情:", "活动ID " + mStrActivityID + " shop_id: " + mStrShopID
				+ " member_id: " + mStrMemberId);
		initView();
		// 关注店铺状态信息查询
		isFollow(mLoginDataManager.getMemberId(), mStrShopID,
				ContantsValues.SHOP_FOLLOW_STATUS_URL);
		getActivityDetail();
		

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
//												if (errorJsonObject.has("data")) {
//													mBaseIsFollow = errorJsonObject.optBoolean("data");
//												}
												
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
	 * 获取活动详情内容
	 * 
	 * @Title getActivityDetail
	 */
	private void getActivityDetail() {

		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", mStrActivityID);

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
							//
							responseErrorInfo(responseInfo);

							if (jsonObject.has("data")) {
								mActivityInfo = GsonUtils.json2Bean(
										responseInfo.result,
										DetailsActivityInfo.class);
								if (mActivityInfo != null
										&& mActivityInfo.data != null) {
									mHuoDongInfo = mActivityInfo.data;
									if (mHuoDongInfo.shop_id != null) {
										mStrShopID = mHuoDongInfo.shop_id;
									}
									if (mHuoDongInfo.shop_name != null) {
										mTvTuiJianName
												.setText(mHuoDongInfo.shop_name);
									}
									if (mHuoDongInfo.shop_address != null) {
										mTvTuiJianAddress
												.setText(mHuoDongInfo.shop_address);
									}
									if (mHuoDongInfo.shop_fans_num != null) {
										mTvTuijianFans
												.setText(mHuoDongInfo.shop_fans_num
														+ " 粉丝");
										fansCount = Integer.parseInt(mHuoDongInfo.shop_fans_num);
									}
									if (mHuoDongInfo.shop_subscribe_tel != null) {
										mStrTuiJianTel = mHuoDongInfo.shop_subscribe_tel;
										mStrTuiJianKefu = mHuoDongInfo.shop_subscribe_tel;
									}
									if (mHuoDongInfo.zan_num != null) {
										mTvBaseBottomZan
												.setText(mHuoDongInfo.zan_num);
									}
									if (mHuoDongInfo.comment_num != null) {
										mTvBaseBottomPinLun
												.setText(mHuoDongInfo.comment_num);
									}
									if (mHuoDongInfo.collection_num != null) {
										mTvBaseBottomShouCang
												.setText(mHuoDongInfo.collection_num);
									}
									if (mHuoDongInfo.title != null) {
										mTvHuodongTitle
												.setText(mHuoDongInfo.title);
									}
									if (mHuoDongInfo.content != null) {
										mTvHuodongContent
												.setText(mHuoDongInfo.content);
									}
									if (mHuoDongInfo.address != null) {
										mTvHuodongAddress
												.setText(mHuoDongInfo.address);
									}
									if (mHuoDongInfo.start_date != null) {
										startDate = mHuoDongInfo.start_date;
										date.append(startDate + " 至 ");
									}
									if (mHuoDongInfo.end_date != null) {
										endDate = mHuoDongInfo.end_date;
										date.append(endDate);
									}
									if (!Utils.isStringEmpty(date.toString())) {
										mTvHuodongDate.setText(date.toString());
									}
									if (mHuoDongInfo.addtime != null) {
										if (mHuoDongInfo.addtime.length() == 10) {

											// 确保long型日期/时间值是正确的，比如检测长度，是否少了最后的毫秒数
											mHuoDongInfo.addtime = mHuoDongInfo.addtime
													+ "000";
											SimpleDateFormat df = new SimpleDateFormat(
													"yyyy-MM-dd HH:mm");
											Date times = new Date(
													Long.valueOf(mHuoDongInfo.addtime));
											mTvHuodongTime.setText(df
													.format(times));
										} else {
											mTvHuodongTime
													.setText(mHuoDongInfo.addtime);
										}
									}
									if (!Utils.isStringEmpty(mHuoDongInfo.img1)) {
										mImgPath.add(URLs.IMG_PRE
												+ mHuoDongInfo.img1);
									}
									if (!Utils.isStringEmpty(mHuoDongInfo.img2)) {
										mImgPath.add(URLs.IMG_PRE
												+ mHuoDongInfo.img2);
									}
									if (!Utils.isStringEmpty(mHuoDongInfo.img3)) {
										mImgPath.add(URLs.IMG_PRE
												+ mHuoDongInfo.img3);
									}
									if (!Utils.isStringEmpty(mHuoDongInfo.img4)) {
										mImgPath.add(URLs.IMG_PRE
												+ mHuoDongInfo.img4);
									}
									if (!Utils.isStringEmpty(mHuoDongInfo.img5)) {
										mImgPath.add(URLs.IMG_PRE
												+ mHuoDongInfo.img5);
									}
									if (!Utils.isStringEmpty(mHuoDongInfo.img6)) {
										mImgPath.add(URLs.IMG_PRE
												+ mHuoDongInfo.img6);
									}
									if (mImgPath.size() > 0) {
										mRlImgsLayout
												.setVisibility(View.VISIBLE);
										// 设定活动详情图片信息
										setHuodongImgInfo();
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
						UIHelper.ToastMessage(HuoDongDetailsActivity.this,
								R.string.response_data_invalid);
					}
				});

	}

	@Override
	protected void resetLayout() {
		RelativeLayout rlLayout = (RelativeLayout) this
				.findViewById(R.id.rl_activity_details_huodong_root);
		SupportDisplay.resetAllChildViewParam(rlLayout);
		// 头部信息
		initTopTitle();
		setTopTitleWithShare("信息详情");
		// 底部收藏，评论等
		initBottomPinlunShoucang();
		// 配置分享
		initSharePlatforms();

		mPager = (ViewPager) this
				.findViewById(R.id.vp_huodong_page_adapter_pic);
		mCirleVGroup = (LinearLayout) this
				.findViewById(R.id.ll_huodong_page_adapter_circle_img);
		mRlImgsLayout = (RelativeLayout) findViewById(R.id.rl_huodong_page_adapter_pic);
		// 活动标题
		mTvHuodongTitle = (TextView) findViewById(R.id.tv_details_type_huodong_title);
		// 活动内容
		mTvHuodongContent = (TextView) findViewById(R.id.tv_details_huodong_content);
		// 活动开始时间-结束时间
		mTvHuodongDate = (TextView) findViewById(R.id.tv_details_huodong_date);
		// 活动地点
		mTvHuodongAddress = (TextView) findViewById(R.id.tv_details_huodong_didian);
		// 活动时间
		mTvHuodongTime = (TextView) findViewById(R.id.tv_details_huodong_type_time);

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

		// 设定活动图片
		setHuodongImgInfo();

	}

	/**
	 * 设定活动详情图片信息
	 * 
	 * @Title setHuodongImgInfo
	 */
	private void setHuodongImgInfo() {
		mAdapter = new HomePageAdapter(HuoDongDetailsActivity.this, mImgPath);
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
					// TODO Auto-generated method stub
					Intent intent = new Intent(HuoDongDetailsActivity.this,
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
			intent.setClass(HuoDongDetailsActivity.this,
					ShowGeocoderActivity.class);
			intent.putExtra(IntentKeyNames.KEY_GEOCODER_LOCATION,
					amapGencodeInfo);
			baseStartActivity(intent);
			break;
		case R.id.rl_details_tuijian_shop_name: // 店铺名称点击
			intent.setClass(HuoDongDetailsActivity.this,
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
				UIHelper.ToastMessage(HuoDongDetailsActivity.this,
						getResources().getString(R.string.network_disabled));
				return;
			}
			if (!mLoginDataManager.getLoginState().equals("1")) {
				toastNotLogin(HuoDongDetailsActivity.this);
			} else {
				this.onClickFollowBtn();
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
			UIHelper.ToastMessage(HuoDongDetailsActivity.this, "您还没登录，请先登录。");
		} else {

			Intent intent = new Intent(HuoDongDetailsActivity.this,
					DetailsCallPinlunActivity.class);
			Bundle bundle = new Bundle();
			intent.putExtra(IntentKeyNames.KEY_CALL_DETAILS_COMMENT_ID, mStrCallId);
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
			UIHelper.ToastMessage(HuoDongDetailsActivity.this, "您还没登录，请先登录。");
		} else {
			callCollectionApi(mLoginDataManager.getMemberId(), mStrCallId,
					"收藏成功。");
			if (mIsCollectionSuccess) {

				ApiAccess.showProgressDialog(HuoDongDetailsActivity.this,
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

		ApiAccess.showProgressDialog(HuoDongDetailsActivity.this, "卖力点赞中..",
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
		Intent intent = new Intent(HuoDongDetailsActivity.this,
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
