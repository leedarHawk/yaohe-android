package com.collcloud.yaohe.activity.my;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.login.LoginActivity;
import com.collcloud.yaohe.activity.person.PersonActivity;
import com.collcloud.yaohe.activity.person.feedback.UserFeedBackActivity;
import com.collcloud.yaohe.activity.person.guanzhubusiness.GuanZhuBusinessActivity;
import com.collcloud.yaohe.activity.person.message.PersonMsgActivity;
import com.collcloud.yaohe.activity.person.setting.PersonSettingActivity;
import com.collcloud.yaohe.activity.person.shenqingweishangjia.ShenQingWeiSJActivity;
import com.collcloud.yaohe.activity.person.shoucang.PersonShouCangActivity;
import com.collcloud.yaohe.activity.person.youhui.PersonYouhuiActivity;
import com.collcloud.yaohe.activity.register.RegisterActivity;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.api.info.UseBaseInfo.UseBase;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.CommonActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.constants.CommonConstant;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.ui.view.BorderScrollView;
import com.collcloud.yaohe.ui.view.BorderScrollView.OnBorderListener;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.meg7.widget.CustomShapeImageView;
import com.tencent.open.utils.Util;

/**
 * @类说明 我的(个人)页面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月12日 下午5:02:18
 */
public class MineActivity extends CommonActivity implements OnClickListener,OnBorderListener {
	private String tag = MineActivity.class.getSimpleName();
	/** 登录按钮 */
	private Button mine_btn_login;
	/** 注册按钮 */
	private Button mine_btn_register;
	/** 登录成功之后的用户头像可点击 */
	private CustomShapeImageView mine_per_photo;
	/** 上下滑动 */
	private BorderScrollView sv_mine;
	/** 我的消息 */
	private RelativeLayout rl_my_msg;
	/** 我的关注 */
	private RelativeLayout rl_my_focus;
	/** 我的收藏 */
	private RelativeLayout rl_my_scang;
	/** 我的优惠 */
	private RelativeLayout rl_my_yhui;
	/** 申请为商家 */
	private RelativeLayout rl_my_sqshangjia;
	/** 我的反馈 */
	private RelativeLayout rl_my_feedback_;
	/** 我的设置 */
	private RelativeLayout rl_my_seetingss_;
	private RelativeLayout rl_user_base_share;
	/** 登录成功后头部显示个人信息 */
	LinearLayout panelLogin;
	/** 未登录的时候显示登录&注册按钮 */
	LinearLayout panelNologin;
	/** 显示用户名 */
	private TextView mine_per_name;
	private UseBase mUserInfo = new UseBase();
	private static ImageLoader mImageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_my);
		setFooterType(4);
		super.onCreate(savedInstanceState);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
		// 初始化组件
		intialSource();
		// 配置分享
		initSharePlatforms();

		setLoginInfo();
		registLoginOutBroadCast();
	}

	/**
	 * 获取个人基本信息
	 */
	private void getUserBaseInfo() {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.USER_BASE_URL,
				params, new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(responseInfo.result);
							responseErrorInfo(responseInfo);
							if (responseInfo.result != null) {
								CCLog.i("个人基本详情：", responseInfo.result);
							}
							if (jsonObject != null && jsonObject.has("data")) {
								JSONObject dataObject = jsonObject
										.optJSONObject("data");
								if (dataObject.has("login_user")) {
									mUserInfo.login_user = dataObject
											.optString("login_user");
								}
								if (dataObject.has("nickname")) {
									mUserInfo.nickname = dataObject
											.optString("nickname");
									if (!Utils
											.isStringEmpty(mUserInfo.nickname)) {
										mine_per_name
												.setText(mUserInfo.nickname);
									} else if (!Utils
											.isStringEmpty(mUserInfo.login_user)) {
										mine_per_name
												.setText(mUserInfo.login_user);
									} else {
										mine_per_name.setText(mLoginDataManager
												.getUserPhone());
									}
								}
								if (dataObject.has("face")) {
									if (!Utils.isStringEmpty(dataObject
											.optString("face"))) {

										mUserInfo.face = URLs.IMG_PRE
												+ dataObject.optString("face");
										ImageListener listener = ImageLoader
												.getImageListener(
														mine_per_photo,
														R.drawable.icon_yaohe_default_logo,
														R.drawable.icon_yaohe_default_logo);
										mImageLoader.get(mUserInfo.face, listener, getResources().getDimensionPixelSize(R.dimen.photo_width), getResources().getDimensionPixelSize(R.dimen.photo_height));
									}
								}
								if (dataObject.has("sex")) {
									mUserInfo.sex = dataObject.optString("sex");
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

	private void setLoginInfo() {
		// 检查是否已经登录 1:已经登录 0:未登录
		if (!Util.isEmpty(mLoginDataManager.getLoginState())
				&& mLoginDataManager.getLoginState().equals("1")) {

			panelNologin.setVisibility(View.GONE);
			panelLogin.setVisibility(View.VISIBLE);
			getUserBaseInfo();
		} else {
			panelNologin.setVisibility(View.VISIBLE);
			panelLogin.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		setFooterType(4);
		setLoginInfo();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		// 检查是否已经登录 1:已经登录 0:未登录
		if (!Util.isEmpty(mLoginDataManager.getLoginState())
				&& mLoginDataManager.getLoginState().equals("1")) {

			panelNologin.setVisibility(View.GONE);
			panelLogin.setVisibility(View.VISIBLE);

		} else {
			panelNologin.setVisibility(View.VISIBLE);
			panelLogin.setVisibility(View.GONE);
		}
	}

	/**
	 * UI界面点击监听事件
	 */
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {

		// 登录按钮
		case R.id.mine_btn_login:
			baseStartActivity(new Intent(MineActivity.this, LoginActivity.class));
			break;

		// 注册按钮
		case R.id.mine_btn_register:
			baseStartActivity(new Intent(MineActivity.this,
					RegisterActivity.class));
			break;

		// 登录成功后点击个人用户头像跳转到我的——个人信息
		case R.id.mine_per_photo:
			Intent intent = new Intent(MineActivity.this, PersonActivity.class);
			baseStartActivity(intent);
			break;

		// 我的消息
		case R.id.rl_my_msg:

			if (mLoginDataManager.getLoginState() == ""
					|| mLoginDataManager.getLoginState().equals("0")) {
				dialogNoLogin();
			} else {

				baseStartActivity(new Intent(MineActivity.this,
						PersonMsgActivity.class));
			}
			break;

		// 我的关注
		case R.id.rl_my_focus:
			if (mLoginDataManager.getLoginState() == ""
					|| mLoginDataManager.getLoginState().equals("0")) {
				dialogNoLogin();
			} else {

				baseStartActivity(new Intent(MineActivity.this,
						GuanZhuBusinessActivity.class));
			}
			break;

		// 我的收藏
		case R.id.rl_my_scang:
			if (mLoginDataManager.getLoginState() == ""
					|| mLoginDataManager.getLoginState().equals("0")) {

				dialogNoLogin();
			} else {

				baseStartActivity(new Intent(MineActivity.this,
						PersonShouCangActivity.class));
			}
			break;

		// 我的优惠
		case R.id.rl_my_yhui:
			if (mLoginDataManager.getLoginState() == ""
					|| mLoginDataManager.getLoginState().equals("0")) {

				dialogNoLogin();
			} else {
				baseStartActivity(new Intent(MineActivity.this,
						PersonYouhuiActivity.class));
			}
			break;

		// 申请为商家，，是h5还是原生的待定
		case R.id.rl_my_sqshangjia:

			if (mLoginDataManager.getLoginState() == ""
					|| mLoginDataManager.getLoginState().equals("0")) {
				dialogNoLogin();
			} else {
				// 如果已经是商家了 申请为商家按钮不可点击
				if (mLoginDataManager.getUserType().equals("1")) {
					showToast("您已经是商家了，不可多次申请!");
					rl_my_sqshangjia.setClickable(false);
				}

				if (mLoginDataManager.getLoginState().equals("1")
						&& mLoginDataManager.getUserType().equals("0")) {
					baseStartActivity(new Intent(MineActivity.this,
							ShenQingWeiSJActivity.class));
				}
			}

			break;
		// 用户反馈
		case R.id.rl_my_feedback_:

			baseStartActivity(new Intent(MineActivity.this,
					UserFeedBackActivity.class));
			break;

		// 设置
		case R.id.rl_my_seetings:

			baseStartActivity(new Intent(MineActivity.this,
					PersonSettingActivity.class));
			break;
		case R.id.rl_user_base_share:
			postShare(); // 邀请好友
			break;

		default:
			break;
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		setLoginInfo();
	}

	/**
	 * 弹出对话框
	 */
	protected void dialogNoLogin() {

		AlertDialog.Builder builder = new Builder(MineActivity.this);
		builder.setMessage("您还没有登录!");
		builder.setTitle("提示");
		builder.setPositiveButton("马上登录",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						startActivity(new Intent(MineActivity.this,
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

	/**
	 * UI界面资源初始化
	 */
	public void intialSource() {

		mine_btn_login = (Button) findViewById(R.id.mine_btn_login);

		mine_btn_login.setOnClickListener(this);

		mine_btn_register = (Button) findViewById(R.id.mine_btn_register);

		mine_btn_register.setOnClickListener(this);

		mine_per_photo = (CustomShapeImageView) findViewById(R.id.mine_per_photo);

		mine_per_photo.setOnClickListener(this);

		this.sv_mine = (BorderScrollView) findViewById(R.id.sv_msg_gz_yh_sc_fb);
		sv_mine.setOnBorderListener(this);

		this.rl_my_msg = (RelativeLayout) findViewById(R.id.rl_my_msg);

		rl_my_msg.setOnClickListener(this);

		this.rl_my_focus = (RelativeLayout) findViewById(R.id.rl_my_focus);

		rl_my_focus.setOnClickListener(this);

		this.rl_my_scang = (RelativeLayout) findViewById(R.id.rl_my_scang);

		rl_my_scang.setOnClickListener(this);

		this.rl_my_yhui = (RelativeLayout) findViewById(R.id.rl_my_yhui);

		rl_my_yhui.setOnClickListener(this);

		this.rl_my_sqshangjia = (RelativeLayout) findViewById(R.id.rl_my_sqshangjia);

		rl_my_sqshangjia.setOnClickListener(this);

		this.rl_my_feedback_ = (RelativeLayout) findViewById(R.id.rl_my_feedback_);

		rl_my_feedback_.setOnClickListener(this);

		this.rl_my_seetingss_ = (RelativeLayout) findViewById(R.id.rl_my_seetings);

		rl_my_seetingss_.setOnClickListener(this);
		this.rl_user_base_share = (RelativeLayout) findViewById(R.id.rl_user_base_share);
		rl_user_base_share.setOnClickListener(this);

		this.panelLogin = (LinearLayout) findViewById(R.id.panelLogin);

		this.panelNologin = (LinearLayout) findViewById(R.id.panelNologin);

		this.mine_per_name = (TextView) findViewById(R.id.mine_per_name);
	}

	/**
	 * 页面适配
	 */
	@Override
	protected void resetLayout() {
		// 页面适配
		RelativeLayout rel_mine = (RelativeLayout) findViewById(R.id.rl_mine_root);
		SupportDisplay.resetAllChildViewParam(rel_mine);

	}

	@Override
	public void onBottom() {
		// TODO Auto-generated method stub	
	}

	@Override
	public void onTop() {
		// TODO Auto-generated method stub
	}
	
	LoginOutBroadCastReceiver  bc = null;
	private void registLoginOutBroadCast() {
		//生成广播处理   
		  bc = new LoginOutBroadCastReceiver();   
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(CommonConstant.PERSON_LOGINOUT_BROADCAST_ACTION);
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

}
