package com.collcloud.yaohe.activity.business;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.business.myfans.BusinessMyFansActivity;
import com.collcloud.yaohe.activity.business.myservicemanager.BusinessServiceManagerActivity;
import com.collcloud.yaohe.activity.business.myyaohe.MyYaoHeActivity;
import com.collcloud.yaohe.activity.businessinfo.BusinessInfoActivity;
import com.collcloud.yaohe.activity.login.LoginActivity;
import com.collcloud.yaohe.activity.my.QRCodeActivity;
import com.collcloud.yaohe.activity.person.feedback.UserFeedBackActivity;
import com.collcloud.yaohe.activity.person.message.PersonMsgActivity;
import com.collcloud.yaohe.activity.person.setting.PersonSettingActivity;
import com.collcloud.yaohe.activity.register.RegisterActivity;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.CommonActivity;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.constants.CommonConstant;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.meg7.widget.CustomShapeImageView;

/**
 * @类说明 商家主界面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月12日 下午3:02:18
 */
public class BusinessActivity extends CommonActivity implements OnClickListener {
	private String tag = BusinessActivity.class.getSimpleName();
	/** 头部—我的吆喝 */
	private RelativeLayout rela_business_myyaohe;
	/** 头部—我的粉丝 */
	private RelativeLayout rela_business_myfans;
	/** 头部—我的消息 */
	private RelativeLayout rela_business_mymsg;
	/** 头部—服务管理 */
	private RelativeLayout rela_business_myserver;
	/** 意见反馈 */
	private RelativeLayout rl_fwgl_feedback;
	/** 邀请好友 */
	private RelativeLayout mRlShare;
	/** 设置 */
	private RelativeLayout rl_fwgl_seeting;
	/** 未登录 */
	private LinearLayout business_panelNologin;
	/** 已登录 */
	private LinearLayout lay_business_top;
	private FrameLayout fl_business_top;
	/** 登录按钮 */
	private Button business_btn_login;
	/** 注册按钮 */
	private Button business_btn_register;
	/** 点击头像 */
	private CustomShapeImageView im_business_photo;
	/** 店铺名 */
	private TextView tv_business_name;
	/** 店铺类型 */
	private TextView tv_business_type;
	private TextView mTvStar1;
	private TextView mTvStar2;
	private TextView mTvStar3;
	private TextView mTvStar4;
	private TextView mTvStar5;
	private static ImageLoader mImageLoader;
	private ImageView mIvErWeima;
	private String mStrFace;
	private String mStrShopName;
	private String mStrShopType;
	private String mStrShopId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_business);
		setFooterType(4);
		super.onCreate(savedInstanceState);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());

		setLoginPattern();

		// 配置分享
		initSharePlatforms();
		//getShopBaseInfo();
		registLoginOutBroadCast();
	}

	private void setLoginPattern() {
		if (mLoginDataManager.getLoginState() == ""
				|| mLoginDataManager.getLoginState().equals("0")) {

			business_panelNologin.setVisibility(View.VISIBLE);
			fl_business_top.setVisibility(View.GONE);

		} else {

			business_panelNologin.setVisibility(View.GONE);
			fl_business_top.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 获取个人基本信息
	 */
	private void getShopBaseInfo() {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.SHOP_BASE_URL,
				params, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(responseInfo.result);
							responseErrorInfo(responseInfo);
							if (responseInfo.result != null) {
								CCLog.i("商家基本详情：", responseInfo.result);
							}
							if (jsonObject != null && jsonObject.has("data")) {
								JSONObject dataObject = jsonObject
										.optJSONObject("data");
								if (dataObject.has("title")) {
									tv_business_name.setText(dataObject
											.optString("title"));
									mStrShopName = dataObject
											.optString("title");
								}
								if (dataObject.has("_class")) {
									tv_business_type.setText(dataObject
											.optString("_class"));
									mStrShopType = dataObject
											.optString("_class");
								}
								if (dataObject.has("shop_id")) {
									mStrShopId = dataObject
											.optString("shop_id");
								}
//								if (dataObject.has("face")) {
//									if (!Utils.isStringEmpty(dataObject
//											.optString("face"))) {
//
//										String url = URLs.IMG_PRE
//												+ dataObject.optString("face");
//										mStrFace = url;
//										ImageListener listener = ImageLoader
//												.getImageListener(
//														im_business_photo,
//														R.drawable.icon_yaohe_default_logo,
//														R.drawable.icon_yaohe_default_logo);
//										mImageLoader.get(url, listener, getResources().getDimensionPixelSize(R.dimen.photo_width), getResources().getDimensionPixelSize(R.dimen.photo_height));
//
//									}
//								} else {
//									im_business_photo.setImageDrawable(null);
//									//im_business_photo.setimage
//								}
								
								
								String url = URLs.IMG_PRE
										+ dataObject.optString("face");
								mStrFace = url;
								ImageListener listener = ImageLoader
										.getImageListener(
												im_business_photo,
												R.drawable.icon_yaohe_default_logo,
												R.drawable.icon_yaohe_default_logo);
								mImageLoader.get(url, listener, getResources().getDimensionPixelSize(R.dimen.photo_width), getResources().getDimensionPixelSize(R.dimen.photo_height));
								
								
								
								
								
								if (dataObject.has("star")) {
									String stars = dataObject.optString("star");
									setStarInfo(stars);
								}

							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						if (msg != null) {
							CCLog.e("个人信息获取异常");
						}
					}
				});

	}

	@Override
	protected void onResume() {
		super.onResume();
		setFooterType(4);
		setLoginPattern();
		getShopBaseInfo();
	}

	/**
	 * UI界面资源初始化
	 */
	private void intialSource() {

		rela_business_myyaohe = (RelativeLayout) findViewById(R.id.rela_business_myyaohe);
		rela_business_myyaohe.setOnClickListener(this);

		rela_business_myfans = (RelativeLayout) findViewById(R.id.rela_business_myfans);
		rela_business_myfans.setOnClickListener(this);

		rela_business_mymsg = (RelativeLayout) findViewById(R.id.rela_business_mymsg);
		rela_business_mymsg.setOnClickListener(this);

		rela_business_myserver = (RelativeLayout) findViewById(R.id.rela_business_myserver);
		rela_business_myserver.setOnClickListener(this);

		rl_fwgl_feedback = (RelativeLayout) findViewById(R.id.rl_fwgl_feedback);
		rl_fwgl_feedback.setOnClickListener(this);

		rl_fwgl_seeting = (RelativeLayout) findViewById(R.id.rl_fwgl_seeting);
		rl_fwgl_seeting.setOnClickListener(this);

		business_panelNologin = (LinearLayout) findViewById(R.id.business_panelNologin);

		lay_business_top = (LinearLayout) findViewById(R.id.lay_business_top);
		fl_business_top = (FrameLayout) findViewById(R.id.fl_business_top);

		this.business_btn_login = (Button) findViewById(R.id.business_btn_login);
		business_btn_login.setOnClickListener(this);

		this.business_btn_register = (Button) findViewById(R.id.business_btn_register);
		business_btn_register.setOnClickListener(this);
		mRlShare = (RelativeLayout) findViewById(R.id.rl_fwgl_share);
		mRlShare.setOnClickListener(this);

		this.im_business_photo = (CustomShapeImageView) findViewById(R.id.im_business_photo);
		im_business_photo.setOnClickListener(this);

		this.tv_business_name = (TextView) findViewById(R.id.tv_business_name);
		tv_business_name.setOnClickListener(this);

		this.tv_business_type = (TextView) findViewById(R.id.tv_business_type);
		mTvStar1 = (TextView) findViewById(R.id.tv_business_star1);
		mTvStar2 = (TextView) findViewById(R.id.tv_business_star2);
		mTvStar3 = (TextView) findViewById(R.id.tv_business_star3);
		mTvStar4 = (TextView) findViewById(R.id.tv_business_star4);
		mTvStar5 = (TextView) findViewById(R.id.tv_business_star5);
		mIvErWeima = (ImageView) findViewById(R.id.iv_business_erweima);
		mIvErWeima.setOnClickListener(this);
	}

	private void setStarInfo(String star) {
		if (!Utils.isStringEmpty(star)) {
			if (star.equals("1")) {
				mTvStar1.setBackgroundResource(R.drawable.red_star);
				mTvStar2.setBackgroundResource(R.drawable.gray_star);
				mTvStar3.setBackgroundResource(R.drawable.gray_star);
				mTvStar4.setBackgroundResource(R.drawable.gray_star);
				mTvStar5.setBackgroundResource(R.drawable.gray_star);
			} else if (star.equals("2")) {
				mTvStar1.setBackgroundResource(R.drawable.red_star);
				mTvStar2.setBackgroundResource(R.drawable.red_star);
				mTvStar3.setBackgroundResource(R.drawable.gray_star);
				mTvStar4.setBackgroundResource(R.drawable.gray_star);
				mTvStar5.setBackgroundResource(R.drawable.gray_star);

			} else if (star.equals("3")) {
				mTvStar1.setBackgroundResource(R.drawable.red_star);
				mTvStar2.setBackgroundResource(R.drawable.red_star);
				mTvStar3.setBackgroundResource(R.drawable.red_star);
				mTvStar4.setBackgroundResource(R.drawable.gray_star);
				mTvStar5.setBackgroundResource(R.drawable.gray_star);

			} else if (star.equals("4")) {
				mTvStar1.setBackgroundResource(R.drawable.red_star);
				mTvStar2.setBackgroundResource(R.drawable.red_star);
				mTvStar3.setBackgroundResource(R.drawable.red_star);
				mTvStar4.setBackgroundResource(R.drawable.red_star);
				mTvStar5.setBackgroundResource(R.drawable.gray_star);

			} else if (star.equals("5")) {
				mTvStar1.setBackgroundResource(R.drawable.red_star);
				mTvStar2.setBackgroundResource(R.drawable.red_star);
				mTvStar3.setBackgroundResource(R.drawable.red_star);
				mTvStar4.setBackgroundResource(R.drawable.red_star);
				mTvStar5.setBackgroundResource(R.drawable.red_star);

			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		setLoginPattern();
		getShopBaseInfo();
	}

	/**
	 * 页面适配
	 */
	@Override
	protected void resetLayout() {
		// 页面适配
		RelativeLayout rel_business_zy = (RelativeLayout) findViewById(R.id.rl_business_zy);
		SupportDisplay.resetAllChildViewParam(rel_business_zy);

		intialSource();
	}

	/**
	 * UI界面单击事件
	 */
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {

		case R.id.rela_business_myyaohe:
			if (mLoginDataManager.getLoginState() == ""
					|| mLoginDataManager.getLoginState().equals("0")) {
				dialogNoLogin();
			} else {
//				baseStartActivity(new Intent(BusinessActivity.this,
//						MyYaoHeActivity.class));
				Intent intent = new Intent(BusinessActivity.this,
						MyYaoHeActivity.class);
				intent.putExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID, mLoginDataManager.getMemberId());
				baseStartActivity(intent);
			}
			break;
		case R.id.rela_business_myfans:
			if (mLoginDataManager.getLoginState() == ""
					|| mLoginDataManager.getLoginState().equals("0")) {
				dialogNoLogin();
			} else {
				Intent intent = new Intent(BusinessActivity.this,
						BusinessMyFansActivity.class);
				intent.putExtra("fromBusinessActivity", true);
				baseStartActivity(intent);
			}
			break;
		case R.id.rela_business_mymsg:
			if (mLoginDataManager.getLoginState() == ""
					|| mLoginDataManager.getLoginState().equals("0")) {
				dialogNoLogin();
			} else {
				// baseStartActivity(new Intent(BusinessActivity.this,
				// BusinessMyMsgActivity.class));
				baseStartActivity(new Intent(BusinessActivity.this,
						PersonMsgActivity.class));
			}
			break;
		case R.id.rela_business_myserver:
			if (mLoginDataManager.getLoginState() == ""
					|| mLoginDataManager.getLoginState().equals("0")) {
				dialogNoLogin();
			} else {
				Intent serviceIntent = new Intent(BusinessActivity.this,
						BusinessServiceManagerActivity.class);
				serviceIntent.putExtra(IntentKeyNames.KEY_DETAILS_SHOP_ID,
						mStrShopId);
				serviceIntent.putExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID,
						mLoginDataManager.getMemberId());
				baseStartActivity(serviceIntent);
				// baseStartActivity(new Intent(BusinessActivity.this,
				// BusinessFWGLActivity.class));
			}
			break;

		case R.id.rl_fwgl_feedback:
			Intent feedBackIntent = new Intent();
			feedBackIntent.setClass(BusinessActivity.this,
					UserFeedBackActivity.class);
			baseStartActivity(feedBackIntent);
			break;

		case R.id.rl_fwgl_seeting:

			Intent seetingIntent = new Intent();
			seetingIntent.setClass(BusinessActivity.this,
					PersonSettingActivity.class);
			baseStartActivity(seetingIntent);
			break;

		case R.id.business_btn_login:
			baseStartActivity(new Intent(BusinessActivity.this,
					LoginActivity.class));
			break;
		case R.id.business_btn_register:
			baseStartActivity(new Intent(BusinessActivity.this,
					RegisterActivity.class));
			break;
		case R.id.rl_fwgl_share:
			postShare(); // 邀请好友
			break;

		case R.id.im_business_photo:
		case R.id.tv_business_name:
			baseStartActivity(new Intent(BusinessActivity.this,
					BusinessInfoActivity.class));
			break;
		case R.id.iv_business_erweima:
			Intent erweima = new Intent(BusinessActivity.this,
					QRCodeActivity.class);
			erweima.putExtra("userface", mStrFace);
			erweima.putExtra("shopName", mStrShopName);
			erweima.putExtra("shopType", mStrShopType);
			baseStartActivity(erweima);
			break;

		default:
			break;
		}

	}

	/**
	 * 弹出对话框
	 */
	protected void dialogNoLogin() {

		AlertDialog.Builder builder = new Builder(BusinessActivity.this);
		builder.setMessage("您还没有登录!");
		builder.setTitle("提示");
		builder.setPositiveButton("马上登录",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						startActivity(new Intent(BusinessActivity.this,
								LoginActivity.class));
						finish();
						arg0.dismiss();
					}

				});

		builder.setNegativeButton("稍后再说",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {

						arg0.dismiss();
					}
				});
		builder.create().show();
	}
	LoginOutBroadCastReceiver  bc = null;
	private void registLoginOutBroadCast() {
		//生成广播处理   
		  bc = new LoginOutBroadCastReceiver();   
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(CommonConstant.BUSINESS_LOGINOUT_BROADCAST_ACTION);
		registerReceiver(bc, intentFilter);
	}
	
	 class LoginOutBroadCastReceiver extends BroadcastReceiver {   
		    @Override  
		    public void onReceive(Context context, Intent intent) {
		    	if(intent.getBooleanExtra("exit", false)) {
		    		CCLog.d(tag, "LoginOutBroadCastReceiver............");
			    	finish();
		    	}
		    	
		    }
	 }
	 @Override
	protected void onDestroy() {
		 try {
			 if(bc != null) {
				 CCLog.d(tag, "onDestroy.......");
				 this.unregisterReceiver(bc); 
			 }
		 } catch(Exception e) {
			 e.printStackTrace();
		 }
		 
		 
		super.onDestroy();
		
	}
}
