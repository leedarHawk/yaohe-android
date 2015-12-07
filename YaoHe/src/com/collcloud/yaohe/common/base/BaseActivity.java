package com.collcloud.yaohe.common.base;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.collcloud.swipe.interfaces.ILoginDataManager;
import com.collcloud.yaohe.MainActivity;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.api.ApiAccess;
import com.collcloud.yaohe.api.ApiAccessErrorManager;
import com.collcloud.yaohe.api.ResponseDataToUI;
import com.collcloud.yaohe.api.info.AreaListInfo;
import com.collcloud.yaohe.api.info.AreaListInfo.AreaList;
import com.collcloud.yaohe.api.info.ClassifyListInfo;
import com.collcloud.yaohe.api.info.ClassifyListInfo.Classify;
import com.collcloud.yaohe.api.info.DistrictListInfo;
import com.collcloud.yaohe.api.info.DetailBusinessCommentInfo.BusinessCommentInfo;
import com.collcloud.yaohe.api.info.DistrictListInfo.DistrictList;
import com.collcloud.yaohe.common.data.LoginDataManagerSPImpl;
import com.collcloud.yaohe.constants.CommonConstant;
import com.collcloud.yaohe.ui.adapter.HomePageAdapter;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.GsonUtils;
import com.collcloud.yaohe.ui.utils.UIHelper;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.ui.view.CustomShareBoard;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.SmsShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * 应用程序共通Activity基类
 * 
 * @ClassName BaseActivity
 * @Description 社会化组件分享，底部标签导航处理等
 * @author CollCloud_小米
 */
public abstract class BaseActivity extends FragmentActivity {

	private String tag = this.getClass().getSimpleName();
	protected BaseActivity mBaseActivity = null;
	public static boolean mIsRunFirst = false;

	protected AppApplacation mApplication = null;
	public boolean isLandscape = false;

	protected boolean mBtnIsCancelButton = false;
	protected boolean mBtnIsLogout = false;
	private long waitTime = 2000;
	private long touchTime = 0;

	// ******** DIALOG ******* //
	private Button mBtnOK;
	private Button mBtnOnlyOK;

	private LinearLayout mLlOnlyOneButton;
	private LinearLayout mLlTwoButtons;

	private Button mBtnCancel;
	private TextView mTvTitle;
	private TextView mTvText;
	private TextView mTvExtraMsg;

	// 【首页】详情等用到的底部功能组件 //
	/** 底部点赞 */
	protected LinearLayout mLlBaseBottomZan = null;
	protected TextView mTvBaseBottomZan = null;
	protected TextView mTvBaseBottomZanImg = null;
	/** 底部评论 */
	protected LinearLayout mLlBaseBottomPinLun = null;
	protected TextView mTvBaseBottomPinLun = null;
	protected TextView mTvBaseBottomPinLunImg = null;
	/** 底部收藏 */
	protected LinearLayout mLlBaseBottomShouCang = null;
	protected TextView mTvBaseBottomShouCang = null;
	protected TextView mTvBaseBottomShouCangImg = null;

	// 【附近】【商圈】用到的底部发消息，点评 //
	/** 底部评论 */
	protected LinearLayout mLlBaseBottomDianPing;
	/** 底部消息 */
	protected LinearLayout mLlBaseBottomXiaoXi;

	// 【附近】【商圈】用到的头部title //
	/** 标题返回 */
	protected LinearLayout mLlBaseTopOnlyBack;
	/** 标题文字 */
	protected TextView mTvBaseTopOnlyBackTitle;

	// 【附近】【商圈】用到的头部title //
	/** 头部返回按钮组件 */
	protected LinearLayout mLlBaseTopBack = null;
	/** 头部标题文字 */
	protected TextView mTvBaseTopTitle = null;
	/** 头部分享组件 */
	protected LinearLayout mLlBaseShare = null;
	/** 头部设定更多组件 */
	protected LinearLayout mLlBaseJuBao = null;
	//显示加载提示框
	public ApiAccess ApiAccess = new ApiAccess();

	protected final UMSocialService mController = UMServiceFactory
			.getUMSocialService(GlobalConstant.DESCRIPTOR);

	/** 吆喝APP 保存数据接口 */
	public ILoginDataManager mLoginDataManager = LoginDataManagerSPImpl
			.getInstace(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			isLandscape = true;
		} else {
			isLandscape = false;
		}
		mApplication = (AppApplacation) this.getApplication();
		//mApplication.add(this);
		mBaseActivity = this;
		SupportDisplay.initLayoutSetParams(BaseActivity.this);

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			isLandscape = true;
		} else {
			isLandscape = false;
		}
		SupportDisplay.initLayoutSetParams(BaseActivity.this);
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onResume() {
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			isLandscape = true;
		} else {
			isLandscape = false;
		}
		SupportDisplay.initLayoutSetParams(BaseActivity.this);

		super.onResume();
		MobclickAgent.onResume(this);
		// 配置需要分享的相关平台
		configPlatforms();
		// 设置分享的内容
		setShareContent();
		if (!mApplication.isNetworkConnected()) {
			UIHelper.ToastMessage(mBaseActivity,
					R.string.network_request_disabled);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * 子类Activity必须实现的方法，用于初始化控件，重新适配等操作
	 * 
	 * @Title resetLayout
	 * @Description 主要用于多屏幕适配时重新计算
	 * @param
	 * @return void
	 */
	protected abstract void resetLayout();

	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		resetLayout();
	}

	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		resetLayout();
	}

	public void baseStartActivity(Context packageContext, Class<?> cls) {
		Intent intent = new Intent(packageContext, cls);
		baseStartActivity(intent);
	}

	public void baseStartActivity(Intent intent) {
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		this.startActivity(intent);

	}

	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onKeyCodeBackListener();
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 按下安卓系统返回键的监听
	 * 
	 * @Title onKeyCodeBackListener
	 */
	private void onKeyCodeBackListener() {
		// 子类如果不是直接finish（），想自己处理的话，首先赋值mBtnIsCancelButton=true
		if (mBtnIsCancelButton) {
			// 子类Activity覆盖此方法可以拦截返回键监听，在自己的页面中处理操作
			onCancelButtonListener();

		} else if (mBtnIsLogout) {// 这个值在具体的主页面设定为true，可以从这点击两次退出，其他页面自行处理

			long currentTime = System.currentTimeMillis();
			if ((currentTime - touchTime) >= waitTime) {
				Toast.makeText(this, "请再按一次退出程序!!", Toast.LENGTH_SHORT).show();
				touchTime = currentTime;
			} else {
				if(this instanceof MainActivity) {
					android.os.Process.killProcess(android.os.Process.myPid());
					mApplication.finishAll();
				}
				
			}

		} else {// 其他情况，默认关闭页面
			finish();
		}

	}

	/**
	 * android系统返回键按下后，各页面独自处理方法
	 */
	protected void onCancelButtonListener() {
	}

	/** 控件抖动效果 */
	protected void shakeLayout(View view) {
		Animation shake = AnimationUtils.loadAnimation(this, R.anim.animlayout);
		if (view != null && view.getVisibility() == View.VISIBLE) {
			view.startAnimation(shake);
		}

	}

	protected String getStringExtra(String key) {
		String result = "";
		if (getIntent().getStringExtra(key) != null) {
			result = getIntent().getStringExtra(key);
		}
		return result;
	}

	public interface PressOKButtonListener {
		void onOKButtonPressed();
	}

	public interface PressCancelButtonListener {
		void onCancelButtonPressed();
	}

	public void showDialogOnlyOneButton(String okName, String title,
			String message, String extraMsg,
			final PressOKButtonListener pressOKButtonListener) {
		if (this.isFinishing()) {
			return;
		}
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.common_dialog_buttons);
		dialog.setCancelable(false);

		LinearLayout dialogBackground = (LinearLayout) dialog
				.findViewById(R.id.common_dialog_styles);
		SupportDisplay.resetAllChildViewParam(dialogBackground, isLandscape);

		mLlOnlyOneButton = (LinearLayout) dialog
				.findViewById(R.id.ll_dialog_common_only_one_button);
		mLlTwoButtons = (LinearLayout) dialog
				.findViewById(R.id.ll_dialog_common_has_two_buttons);

		mLlOnlyOneButton.setVisibility(View.VISIBLE);
		mLlTwoButtons.setVisibility(View.GONE);

		// dialog buttons and text
		mBtnOK = (Button) dialog.findViewById(R.id.btn_dialog_ok);
		mBtnOnlyOK = (Button) dialog.findViewById(R.id.btn_dialog_only_one_ok);
		mBtnCancel = (Button) dialog.findViewById(R.id.btn_dialog_cancel);
		mTvText = (TextView) dialog.findViewById(R.id.tv_dialogText);
		mTvExtraMsg = (TextView) dialog.findViewById(R.id.tv_dialogText_extra);
		mTvTitle = (TextView) dialog.findViewById(R.id.tv_dialogTitle);
		// OK button
		mBtnOnlyOK.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (pressOKButtonListener != null) {
					pressOKButtonListener.onOKButtonPressed();
				}
				dialog.dismiss();
			}
		});

		mBtnCancel.setVisibility(View.GONE);
		if (!Utils.isStringEmpty(title)) {
			mTvTitle.setText(title);
		} else {
			mTvTitle.setText(GlobalConstant.EMPTY_STRING);
		}
		if (!Utils.isStringEmpty(okName)) {
			mBtnOnlyOK.setText(okName);
		} else {
			mBtnOnlyOK.setText("确认");
		}
		if (!Utils.isStringEmpty(message)) {
			mTvText.setText(message);
			mTvText.setVisibility(View.VISIBLE);
		} else {
			mTvText.setText(GlobalConstant.EMPTY_STRING);
			mTvText.setVisibility(View.GONE);
		}
		if (!Utils.isStringEmpty(extraMsg)) {
			mTvExtraMsg.setVisibility(View.VISIBLE);
			mTvExtraMsg.setText(extraMsg);
		} else {
			mTvExtraMsg.setVisibility(View.GONE);
		}
		dialog.show();

	}

	/**
	 * 对话框样式-只有一个确认按钮，文字自定义
	 * 
	 * @Title showDialogOneBtton
	 * @Description 只有一个确认按钮的自定义对话框
	 * @param title
	 *            ： 对话框Title文字
	 * @param message
	 *            ：对话框内容文字
	 * @param extraMsg
	 *            ：对话框额外信息文字
	 * @param pressOKButtonListener
	 *            确认按钮的点击监听
	 */
	public void showDialogDefaultOneBtton(String title, String message,
			String extraMsg, final PressOKButtonListener pressOKButtonListener) {
		showDialogOnlyOneButton("确认", title, message, extraMsg,
				pressOKButtonListener);
	}

	public void showDialogWithButton(String title) {
		showDialogOnlyOneButton("确认", title, null, null, null);
	}

	public void showApiResponseError() {
		showDialogOnlyOneButton(
				"确认",
				mBaseActivity.getResources().getString(
						R.string.xml_parser_failed), null, null, null);
	}

	/**
	 * 对话框样式-默认确认和取消按钮，文字自定义
	 * 
	 * @Title showDialogOneBtton
	 * @Description 默认确认和取消按钮的自定义对话框
	 * @param title
	 *            ： 对话框Title文字
	 * @param message
	 *            ：对话框内容文字
	 * @param extraMsg
	 *            ：对话框额外信息文字
	 * @param pressOKButtonListener
	 *            确认按钮的点击监听
	 * @param pressCancelButtonListener
	 *            取消按钮的点击监听
	 */
	public void showDialogDefaultTwoBttons(String title, String message,
			String extraMsg, final PressOKButtonListener pressOKButtonListener,
			final PressCancelButtonListener pressCancelButtonListener) {
		showDialogWithButtons("取消", "确认", title, message, extraMsg,
				pressOKButtonListener, pressCancelButtonListener);
	}

	public void showDialogWithButtons(String cancelName, String okName,
			String title, String message, String extraMsg,
			final PressOKButtonListener pressOKButtonListener,
			final PressCancelButtonListener pressCancelButtonListener) {
		if (this.isFinishing()) {
			return;
		}
		// dialog style
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.common_dialog_buttons);
		dialog.setCancelable(false);

		// dialog resize
		LinearLayout dialogBackground = (LinearLayout) dialog
				.findViewById(R.id.common_dialog_styles);
		SupportDisplay.resetAllChildViewParam(dialogBackground, isLandscape);

		mLlOnlyOneButton = (LinearLayout) dialog
				.findViewById(R.id.ll_dialog_common_only_one_button);
		mLlTwoButtons = (LinearLayout) dialog
				.findViewById(R.id.ll_dialog_common_has_two_buttons);

		mLlOnlyOneButton.setVisibility(View.GONE);
		mLlTwoButtons.setVisibility(View.VISIBLE);

		// dialog buttons and text
		mBtnOK = (Button) dialog.findViewById(R.id.btn_dialog_ok);
		mBtnOnlyOK = (Button) dialog.findViewById(R.id.btn_dialog_only_one_ok);
		mBtnCancel = (Button) dialog.findViewById(R.id.btn_dialog_cancel);
		mTvText = (TextView) dialog.findViewById(R.id.tv_dialogText);
		mTvExtraMsg = (TextView) dialog.findViewById(R.id.tv_dialogText_extra);
		mTvTitle = (TextView) dialog.findViewById(R.id.tv_dialogTitle);
		// OK button
		mBtnOK.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (pressOKButtonListener != null) {
					pressOKButtonListener.onOKButtonPressed();
				}
				dialog.dismiss();
			}
		});

		// cancel button
		mBtnCancel.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (pressCancelButtonListener != null) {
					pressCancelButtonListener.onCancelButtonPressed();
				}
				dialog.dismiss();
			}
		});
		if (!Utils.isStringEmpty(message)) {
			mTvText.setText(message);
		} else {
			mTvText.setText(GlobalConstant.EMPTY_STRING);
		}
		if (!Utils.isStringEmpty(title)) {
			mTvTitle.setText(title);
		} else {
			mTvTitle.setText(GlobalConstant.EMPTY_STRING);
		}
		if (!Utils.isStringEmpty(okName)) {
			mBtnOK.setText(okName);
		} else {
			mBtnOK.setText("确认");
		}
		if (!Utils.isStringEmpty(cancelName)) {
			mBtnCancel.setText(cancelName);
		} else {
			mBtnCancel.setText("取消");
		}
		if (!Utils.isStringEmpty(extraMsg)) {
			mTvExtraMsg.setVisibility(View.VISIBLE);
			mTvExtraMsg.setText(extraMsg);
		} else {
			mTvExtraMsg.setVisibility(View.GONE);
		}
		dialog.show();

	}

	// ************　共通组件相关　*************** //
	// ********* 分割线 ***** 店铺收藏、店铺点赞 **** 分割线 ********** //
	/**
	 * 【首页】详情等用到的底部功能组件
	 * 
	 * @Title initBottomPinlunShoucang
	 * @Description 包含【点赞】【收藏】【评论】
	 */
	public void initBottomPinlunShoucang() {
		// 点赞
		mLlBaseBottomZan = (LinearLayout) this
				.findViewById(R.id.ll_common_bottom_details_dianzan_);
		mTvBaseBottomZan = (TextView) this
				.findViewById(R.id.tv_common_bottom_details_dianzan_);
		mTvBaseBottomZanImg = (TextView) this
				.findViewById(R.id.tv_common_bottom_details_dianzan_img);
		// 收藏
		mLlBaseBottomShouCang = (LinearLayout) this
				.findViewById(R.id.ll_common_bottom_details_shoucang_);
		mTvBaseBottomShouCang = (TextView) this
				.findViewById(R.id.tv_common_bottom_details_shoucang);
		mTvBaseBottomShouCangImg = (TextView) this
				.findViewById(R.id.tv_common_bottom_details_shoucang_img);
		// 评论
		mLlBaseBottomPinLun = (LinearLayout) this
				.findViewById(R.id.ll_common_bottom_details_pinlun_);
		mTvBaseBottomPinLun = (TextView) this
				.findViewById(R.id.tv_common_bottom_details_pinlun);
		mTvBaseBottomPinLunImg = (TextView) this
				.findViewById(R.id.tv_common_bottom_details_pinlun_img);

		mLlBaseBottomZan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// shopPraise();
				callPraise();
			}
		});
		mLlBaseBottomShouCang.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// shopCollection();
				callCollection();
			}
		});
		mLlBaseBottomPinLun.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// shopComment();
				callComment();
			}
		});
	}

	/**
	 * 店铺点赞
	 */
	protected void shopPraise() {
	}

	/**
	 * 店铺点评
	 */
	protected void shopComment() {
	}

	/**
	 * 店铺收藏
	 */
	protected void shopCollection() {
	}

	/**
	 * 吆喝点评
	 */
	protected void callComment() {
	}

	/**
	 * 吆喝点赞
	 */
	protected void callPraise() {
	}

	/**
	 * 吆喝收藏
	 */
	protected void callCollection() {
	}

	/**
	 * 店铺收藏、店铺点赞 共通API调用
	 * 
	 * @Title shopActionApi
	 * @Description 店铺收藏、店铺点赞 共通API调用
	 * @param memberID
	 *            会员ID
	 * @param shopID
	 *            店铺ID
	 * @param url
	 *            请求url地址
	 * @param message
	 *            自定义成功后的提示信息
	 */
	protected void shopActionApi(String memberID, String shopID, String url,
			final String message) {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", memberID);
		params.addBodyParameter("shop_id", shopID);

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
										int code = statusObject.optInt("code");
										if (code == 1) {
											String strErrorMsg = errorJsonObject
													.getString("message");
											UIHelper.ToastMessage(
													mBaseActivity, strErrorMsg);

										} else {
											UIHelper.ToastMessage(
													mBaseActivity, message);
										}
									}
								} catch (Exception e) {
									String errorMsg = ApiAccessErrorManager
											.getMessage(5, mBaseActivity);
									UIHelper.ToastMessage(mBaseActivity,
											errorMsg);
								}

							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						UIHelper.ToastMessage(mBaseActivity,
								R.string.response_data_invalid);
					}
				});
	}

	/**
	 * 店铺点评API 调用
	 * 
	 * @Title shopCommentApi
	 * @Description
	 * @param memberID
	 *            会员ID(post提交)
	 * @param shopID
	 *            店铺ID(post提交)
	 * @param star
	 *            几颗星
	 * @param content
	 *            内容
	 * @param isAnonymous
	 *            是否匿名 0不匿名1匿名
	 * @param type
	 *            0评论 1回复
	 * @param parentid
	 *            回复的评论ID
	 * @param url
	 *            post请求的url地址
	 * @param message
	 *            点评成功后的提示信息
	 */
	protected void shopCommentApi(String memberID, String shopID, String star,
			String content, String isAnonymous, String type, String parentid,
			String url, final String message) {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", memberID);
		params.addBodyParameter("shop_id", shopID);
		params.addBodyParameter("star", star);
		params.addBodyParameter("content", content);
		params.addBodyParameter("is_anonymous", isAnonymous);
		params.addBodyParameter("type", type);
		params.addBodyParameter("parentid", parentid);
		CCLog.i("店铺点评参数：", "member_id= " + memberID + " shop_id=" + shopID
				+ " star=" + star + " content=" + content + " is_anonymous="
				+ isAnonymous);

		http.send(HttpRequest.HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!Utils.isStringEmpty(responseInfo.result)) {
							if (responseInfo.result.contains("status")) {
								if (responseInfo.result != null) {
									CCLog.i("店铺点评信息：", " "
											+ responseInfo.result);
								}
								try {
									// 数据处理
									JSONObject errorJsonObject = new JSONObject(
											responseInfo.result);
									if (errorJsonObject.has("status")) {
										JSONObject statusObject = errorJsonObject
												.optJSONObject("status");
										int code = statusObject.optInt("code");
										if (code == 1) {
											String strErrorMsg = statusObject
													.getString("message");
											UIHelper.ToastMessage(
													mBaseActivity, strErrorMsg);
										} else {
											ApiAccess.showProgressDialog(
													mBaseActivity, "提交中..");

											new Handler().postDelayed(
													new Runnable() {
														@Override
														public void run() {
															UIHelper.ToastMessage(
																	mBaseActivity,
																	message);
															ApiAccess
																	.dismissProgressDialog();
															finish();
														}
													}, 1000);
										}
									}
								} catch (Exception e) {
									String errorMsg = ApiAccessErrorManager
											.getMessage(5, mBaseActivity);
									UIHelper.ToastMessage(mBaseActivity,
											errorMsg);
								}

							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						UIHelper.ToastMessage(mBaseActivity,
								R.string.response_data_invalid);
					}
				});
	}

	/**
	 * 吆喝点评API 调用
	 * 
	 * @Title shopCommentApi
	 * @Description
	 * @param memberID
	 *            会员ID(post提交)
	 * @param callID
	 *            吆喝ID(post提交)
	 * @param star
	 *            几颗星
	 * @param content
	 *            内容
	 * @param isAnonymous
	 *            是否匿名 0不匿名1匿名
	 * @param url
	 *            post请求的url地址
	 * @param message
	 *            点评成功后的提示信息
	 */
	protected void callCommentApi(String memberID, String callID, String star,
			String content, String isAnonymous, String strType, String url,
			final String message) {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", memberID);
		params.addBodyParameter("call_id", callID);
		params.addBodyParameter("content", content);
		params.addBodyParameter("is_anonymous", isAnonymous);
		params.addBodyParameter("type", strType);
		params.addBodyParameter("parentid", "");
		CCLog.i("BaseActivity 吆喝点评参数：", "member_id= " + memberID + " call_id=" + callID
				+ " star=" + star + " content=" + content + " is_anonymous="
				+ isAnonymous);

		http.send(HttpRequest.HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!Utils.isStringEmpty(responseInfo.result)) {
							if (responseInfo.result.contains("status")) {
								if (responseInfo.result != null) {
									CCLog.i("吆喝点评信息：", " "
											+ responseInfo.result);
								}
								try {
									// 数据处理
									JSONObject errorJsonObject = new JSONObject(
											responseInfo.result);
									if (errorJsonObject.has("status")) {
										JSONObject statusObject = errorJsonObject
												.optJSONObject("status");
										int code = statusObject.optInt("code");
										if (code == 1) {
											String strErrorMsg = errorJsonObject
													.getString("message");
											UIHelper.ToastMessage(
													mBaseActivity, strErrorMsg);
										} else {
											ApiAccess.showProgressDialog(
													mBaseActivity, "提交中..");

											new Handler().postDelayed(
													new Runnable() {
														@Override
														public void run() {
															if (message != null) {
																UIHelper.ToastMessage(
																		mBaseActivity,
																		message);
															}
															ApiAccess
																	.dismissProgressDialog();
															finish();
														}
													}, 1000);
										}
									}
								} catch (Exception e) {
									String errorMsg = ApiAccessErrorManager
											.getMessage(5, mBaseActivity);
									UIHelper.ToastMessage(mBaseActivity,
											errorMsg);
								}

							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						UIHelper.ToastMessage(mBaseActivity,
								R.string.response_data_invalid);
					}
				});
	}

	protected boolean mIsCollectionSuccess = false;

	/**
	 * 吆喝收藏 API调用
	 * 
	 * @Title shopActionApi
	 * @Description 吆喝收藏API调用
	 * @param memberID
	 *            会员ID
	 * @param callID
	 *            吆喝ID
	 * @param url
	 *            请求url地址
	 * @param message
	 *            自定义成功后的提示信息
	 */
	protected boolean callCollectionApi(String memberID, String callID,
			final String message) {
		String url = ContantsValues.CALL_FOLLOW_URL +"&member_id="+memberID+"&id="+callID;
		//String url = ContantsValues.CALL_FOLLOW_URL;
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", memberID);
		params.addBodyParameter("call_id", callID);
		CCLog.i("吆喝收藏点赞参数：", "member_id=" + memberID + " call_id=" + callID);

		http.send(HttpRequest.HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!Utils.isStringEmpty(responseInfo.result)) {
							if (responseInfo.result.contains("status")) {
								if (responseInfo.result != null) {
									CCLog.i("吆喝收藏点赞状态：", responseInfo.result
											+ " ");
								}
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
											if (code == 1) {
												mIsCollectionSuccess = false;
												String strErrorMsg = statusObject
														.optString("message");
												UIHelper.ToastMessage(
														mBaseActivity,
														strErrorMsg);

											} else {
												mIsCollectionSuccess = true;
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
									mIsCollectionSuccess = false;
									String errorMsg = ApiAccessErrorManager
											.getMessage(5, mBaseActivity);
									UIHelper.ToastMessage(mBaseActivity,
											errorMsg);
								}

							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						mIsCollectionSuccess = false;
						UIHelper.ToastMessage(mBaseActivity,
								R.string.response_data_invalid);
					}
				});
		return mIsCollectionSuccess;
	}

	protected boolean mIsZanSuccess = false;

	/**
	 * 吆喝点赞 API调用
	 * 
	 * @Title shopActionApi
	 * @Description 吆喝点赞 API调用
	 * @param memberID
	 *            会员ID
	 * @param callID
	 *            吆喝ID
	 * @param url
	 *            请求url地址
	 * @param message
	 *            自定义成功后的提示信息
	 */
	protected boolean callZanApi(String memberID, String callID,
			final String message) {
		String url = ContantsValues.CALL_PRAISE_URL;
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", memberID);
		params.addBodyParameter("call_id", callID);
		CCLog.i("吆喝收藏点赞参数：", "member_id=" + memberID + " call_id=" + callID);

		http.send(HttpRequest.HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!Utils.isStringEmpty(responseInfo.result)) {
							if (responseInfo.result.contains("status")) {
								if (responseInfo.result != null) {
									CCLog.i("吆喝收藏点赞状态：", responseInfo.result
											+ " ");
								}
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
											if (code == 1) {
												mIsZanSuccess = false;
												String strErrorMsg = statusObject
														.optString("message");
												UIHelper.ToastMessage(
														mBaseActivity,
														strErrorMsg);

											} else {
												mIsZanSuccess = true;
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
									mIsZanSuccess = false;
									String errorMsg = ApiAccessErrorManager
											.getMessage(5, mBaseActivity);
									UIHelper.ToastMessage(mBaseActivity,
											errorMsg);
								}

							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						mIsZanSuccess = false;
						UIHelper.ToastMessage(mBaseActivity,
								R.string.response_data_invalid);
					}
				});
		return mIsZanSuccess;
	}

	// ***************判断商铺是否已经关注***************** //
	// 是否可以关注
	protected boolean mBaseIsNotFollow = false;

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
												mBaseIsNotFollow = true;
												String strErrorMsg = statusObject
														.getString("message");
												UIHelper.ToastMessage(
														mBaseActivity,
														strErrorMsg);
											} else {
												mBaseIsNotFollow = false;
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

									mBaseIsNotFollow = true;
									String errorMsg = ApiAccessErrorManager
											.getMessage(5, mBaseActivity);
									UIHelper.ToastMessage(mBaseActivity,
											errorMsg);
								}

							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						UIHelper.ToastMessage(mBaseActivity,
								R.string.response_data_invalid);
					}
				});
	}

	/**
	 * 是否已经关注了。
	 */
	protected boolean mBaseIsFollow = false;

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
					}

					@Override
					public void onFailure(HttpException error, String msg) {
					}
				});
		return mBaseIsFollow;
	}

	// ********* 分割线 ***** 店铺收藏、店铺点赞 **** 分割线 ********** //

	// ***********　写入 举报 开始 *********** //
	/**
	 * 
	 * @Title addReportApi
	 * @Description
	 * @param memberID
	 *            会员ID(post传参)
	 * @param shopID
	 *            店铺ID(post传参)
	 * @param reason
	 *            举报原因(post传参)0色情 1谣言 2政治 3网站钓鱼 4诱导分享
	 * @param content
	 *            补充说明(post传参)
	 * @param terminal
	 *            终端(0 Android 1 Iphone)
	 * @param url
	 * @return void
	 */
	protected void addReportApi(String memberID, String shopID, String reason,
			String content, String url) {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", memberID);
		params.addBodyParameter("shop_id", shopID);
		params.addBodyParameter("reason", reason);
		params.addBodyParameter("content", content);
		params.addBodyParameter("terminal", "0");

		http.send(HttpRequest.HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!Utils.isStringEmpty(responseInfo.result)) {
							if (responseInfo.result != null) {
								CCLog.i("举报信息：", responseInfo.result + " ");
							}
							if (responseInfo.result.contains("status")) {
								try {
									// 数据处理
									JSONObject errorJsonObject = new JSONObject(
											responseInfo.result);
									if (errorJsonObject.has("status")) {
										JSONObject statusObject = errorJsonObject
												.optJSONObject("status");
										int code = statusObject.optInt("code");
										if (code == 1) {
											// String strErrorMsg =
											// errorJsonObject
											// .getString("message");
											// UIHelper.ToastMessage(
											// mBaseActivity, strErrorMsg);

										}
									}
								} catch (Exception e) {
									// String errorMsg = ApiAccessErrorManager
									// .getMessage(5, mBaseActivity);
									// UIHelper.ToastMessage(mBaseActivity,
									// errorMsg);
								}

							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						UIHelper.ToastMessage(mBaseActivity,
								R.string.response_data_invalid);
					}
				});
	}

	// ***********　写入 举报 结束 *********** //

	/**
	 * api返回结果异常信息处理
	 * 
	 * @Title responseErrorInfo
	 */
	protected void responseErrorInfo(ResponseInfo<String> responseData) {
		if (!Utils.isStringEmpty(responseData.result)) {
			if (responseData.result.contains("status")) {
				try {
					// 数据处理
					JSONObject errorJsonObject = new JSONObject(
							responseData.result);
					if (errorJsonObject.has("status")) {
						JSONObject statusObject = errorJsonObject
								.optJSONObject("status");
						int code = statusObject.optInt("code");
						if (code == 1) {
							String strErrorMsg = statusObject
									.optString("message");
							CCLog.e("BaseActivity结果:", "错误信息是： " + strErrorMsg);
							UIHelper.ToastMessage(mBaseActivity, strErrorMsg);
						} else {
							CCLog.e("BaseActivity结果:", "status ：0 正常。 ");
						}
					}
				} catch (Exception e) {
					String errorMsg = ApiAccessErrorManager.getMessage(5,
							mBaseActivity);
					UIHelper.ToastMessage(mBaseActivity, errorMsg);
				}
			}
		}
	}

	/**
	 * 【附近】【商圈】用到的底部发消息，点评
	 * 
	 * @Title initFuJinBottomXiaoxi
	 * @Description 包含【发消息】【点评】
	 */
	public void initBottomXiaoxi() {

		// 点评
		mLlBaseBottomDianPing = (LinearLayout) this
				.findViewById(R.id.ll_common_bottom_details_pinlun_);
		// 发消息
		mLlBaseBottomXiaoXi = (LinearLayout) this
				.findViewById(R.id.ll_common_bottom_fujin_details_xiaoxi_);
		mLlBaseBottomDianPing.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				nearByComment();
			}
		});
		mLlBaseBottomXiaoXi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				nearByMessage();
			}
		});
	}

	/**
	 * 附近商圈点评
	 */
	protected void nearByComment() {

	}

	/**
	 * 附近商圈发消息
	 */
	protected void nearByMessage() {

	}

	// ********* ***** 分割线 **** ********** //
	protected boolean mTopCancel = false;

	/**
	 * 【附近】【商圈】用到的头部title
	 * 
	 * @Title initTopOnlyBackTitle
	 * @Description 包含【返回】【标题】
	 */
	public void initTopOnlyBackTitle() {

		// 返回
		mLlBaseTopOnlyBack = (LinearLayout) this
				.findViewById(R.id.ll_commonn_text_title_back);
		// 标题文字
		mTvBaseTopOnlyBackTitle = (TextView) this
				.findViewById(R.id.tv_commonn_back_title_text);
		mLlBaseTopOnlyBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mTopCancel) {
					topCancelDefined();
				} else {
					finish();
				}
			}
		});
	}

	protected void topCancelDefined() {

	}

	// ********* ***** 分割线 **** ********** //
	/**
	 * 【附近】【商圈】用到的头部title (返回按钮，分享，举报等)
	 * 
	 * @Title initTopTitle
	 */
	public void initTopTitle() {
		// 返回
		mLlBaseTopBack = (LinearLayout) this
				.findViewById(R.id.ll_commonn_title_menu_back);
		// 标题文字
		mTvBaseTopTitle = (TextView) this
				.findViewById(R.id.tv_commonn_title_menu_text);
		// 分享
		mLlBaseShare = (LinearLayout) this
				.findViewById(R.id.ll_commonn_title_menu_share);
		// 更多设定
		mLlBaseJuBao = (LinearLayout) this
				.findViewById(R.id.ll_commonn_title_menu_more);
		mLlBaseTopBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});
		mLlBaseShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				postShare();
			}
		});
		mLlBaseJuBao.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addReport();
			}
		});
	}

	/**
	 * 写入举报
	 * 
	 * @Title addReport
	 */
	protected void addReport() {

	}

	/**
	 * 设置Title文字。（顶部带分享按钮）
	 * 
	 * @Title setTopTitleWithShare
	 * @param str
	 *            要设置的title文言
	 */
	public void setTopTitleWithShare(String str) {
		if (mTvBaseTopTitle != null && !Utils.isStringEmpty(str)) {
			mTvBaseTopTitle.setText(str);
		} else {
			mTvBaseTopTitle.setText("信息详情");
		}
	}

	/**
	 * 设置Title文字。（顶部只有返回按钮和Title）
	 * 
	 * @Title setTopTitleOnlyBack
	 * @param str
	 *            要设置的title文言
	 */
	public void setTopOnlyBackTitle(String str) {
		if (mTvBaseTopOnlyBackTitle != null && !Utils.isStringEmpty(str)) {
			mTvBaseTopOnlyBackTitle.setText(str);
		} else {
			mTvBaseTopOnlyBackTitle.setText("");
		}
	}

	protected void doOnApiAccessResponsedBase(ResponseDataToUI responseData) {
	}

	protected void doOnErrorResponseAction() {
	}

	
	/*
	protected ApiAccessResponseListener mApiResponseListener = new ApiAccessResponseListener() {

		@Override
		public void onApiAccessResponse(ResponseDataToUI responseData) {
			if (responseData == null) {
				String errorMsg = ApiAccessErrorManager.getMessage(
						ApiAccessErrorManager.RESPONSE_DATA_INVALID,
						BaseActivity.this);
				// showDialogWithButton(errorMsg);
				UIHelper.ToastMessage(BaseActivity.this, errorMsg);
				ApiAccess.dismissProgressDialog();
				doOnErrorResponseAction();
				return;
			}
			if (ApiAccessErrorManager.isNotOK(responseData.netWorkErrorCode)) {
				ApiAccess.dismissProgressDialog();
				if (!mApplication.isNetworkConnected()) {
					String errorMsg = ApiAccessErrorManager.getMessage(
							ApiAccessErrorManager.NETWORK_DISABLED,
							BaseActivity.this);
					UIHelper.ToastMessage(BaseActivity.this, errorMsg);
				} else {
					String errorMsg = ApiAccessErrorManager.getMessage(
							responseData.netWorkErrorCode, BaseActivity.this);
					UIHelper.ToastMessage(BaseActivity.this, errorMsg);
				}
				doOnErrorResponseAction();
				return;
			} else {
				if (!Utils.isStringEmpty(responseData.result)) {
					if (responseData.result.contains("status")) {
						try {
							// 数据处理
							JSONObject errorJsonObject = new JSONObject(
									responseData.result);
							if (errorJsonObject.has("status")) {
								JSONObject statusObject = errorJsonObject
										.optJSONObject("status");
								if (statusObject.has("code")) {
									int code = statusObject.optInt("code");
									if (code == 1) {
										String strErrorMsg = statusObject
												.optString("message");
										CCLog.e("BaseActivity 结果:", "错误信息是： "
												+ strErrorMsg);
										UIHelper.ToastMessage(mBaseActivity,
												strErrorMsg);
									} else {
										CCLog.e("BaseActivity 结果:",
												"status ：0 正常。 ");
									}
								}
							}
						} catch (Exception e) {
							String errorMsg = ApiAccessErrorManager.getMessage(
									5, BaseActivity.this);
							UIHelper.ToastMessage(mBaseActivity, errorMsg);
						}

						ApiAccess.dismissProgressDialog();
					}
				}
			}
			doOnApiAccessResponsedBase(responseData);
		};
	};
*/
	public void initSharePlatforms() {
		configPlatforms();
		setShareContent();
	}

	/**
	 * 配置分享平台参数</br>
	 */
	private void configPlatforms() {
		// 添加新浪SSO授权
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		mController.getConfig().closeToast();

		// 添加QQ、QZone平台
		addQQQZonePlatform();

		// 添加微信、微信朋友圈平台
		addWXPlatform();
	}

	/**
	 * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
	 *       image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
	 *       要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
	 *       : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
	 * @return
	 */
	private void addQQQZonePlatform() {
		// String appId = "100424468";
		// String appKey = "c7394704798a158208a74ab60104f0ba";
		// 添加QQ支持, 并且设置QQ分享内容的target url
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(mBaseActivity,
				GlobalConstant.QQ_APP_ID, GlobalConstant.QQ_APP_KEY);
		qqSsoHandler.setTargetUrl(GlobalConstant.QQ_SHARE_TARGET_URL);
		qqSsoHandler.addToSocialSDK();

		// 添加QZone平台
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(mBaseActivity,
				GlobalConstant.QQ_APP_ID, GlobalConstant.QQ_APP_KEY);
		qZoneSsoHandler.addToSocialSDK();
	}

	/**
	 * @功能描述 : 添加微信平台分享
	 * @return
	 */
	private void addWXPlatform() {
		// 注意：在微信授权的时候，必须传递appSecret
		// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
		// String appId = "wx967daebe835fbeac";
		// String appSecret = "5bb696d9ccd75a38c8a0bfe0675559b3";
		String appId = GlobalConstant.WECHAT_APP_ID;
		String appSecret = GlobalConstant.WECHAT_APP_KEY;
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(mBaseActivity, appId, appSecret);
		wxHandler.addToSocialSDK();
		wxHandler.showCompressToast(false);

		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(mBaseActivity, appId,
				appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
		wxCircleHandler.showCompressToast(false);
	}

	/**
	 * 根据不同的平台设置不同的分享内容</br>
	 */
	private void setShareContent() {
		mController.getConfig().closeToast();

		// 配置SSO
		// mController.getConfig().setSsoHandler(new SinaSsoHandler());
		// mController.getConfig().setSinaCallbackUrl("");

//		UMImage urlImage = new UMImage(mBaseActivity,
//				"http://www.umeng.com/images/pic/social/integrated_3.png");
		UMImage localImage = new UMImage(mBaseActivity, R.drawable.ic_launcher);
//		UMImage image = new UMImage(mBaseActivity,
//				BitmapFactory.decodeResource(getResources(),
//						R.drawable.ic_launcher));

		// QQ 空间SSO授权
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(mBaseActivity,
				GlobalConstant.QQ_APP_ID, GlobalConstant.QQ_APP_KEY);
		qZoneSsoHandler.addToSocialSDK();
		mController.setShareContent(GlobalConstant.QQ_SHARE_CONTENT);

		// 微信
		WeiXinShareContent weixinContent = new WeiXinShareContent();
		weixinContent.setShareContent(GlobalConstant.WECHAT_SHARE_CONTENT);
		weixinContent.setTitle(GlobalConstant.WECHAT_SHARE_TITLE);
		weixinContent.setTargetUrl(GlobalConstant.QQ_SHARE_TARGET_URL);
		weixinContent.setShareMedia(localImage);
		mController.setShareMedia(weixinContent);

		// 设置朋友圈分享的内容
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia.setShareContent(GlobalConstant.WECHAT_CIRCLE_SHARE_CONTENT);
		circleMedia.setTitle(GlobalConstant.WECHAT_CIRCLE_SHARE_TITLE);
		circleMedia.setShareMedia(localImage);
		// circleMedia.setShareMedia(uMusic);
		// circleMedia.setShareMedia(video);
		circleMedia.setTargetUrl(GlobalConstant.QQ_SHARE_TARGET_URL);
		mController.setShareMedia(circleMedia);

		UMImage qzoneImage = new UMImage(mBaseActivity,
				"http://www.umeng.com/images/pic/social/integrated_3.png");
		qzoneImage.setTargetUrl(GlobalConstant.QQ_SHARE_TARGET_URL);

		// 设置QQ空间分享内容
		QZoneShareContent qzone = new QZoneShareContent();
		qzone.setShareContent(GlobalConstant.QQZONE_SHARE_CONTENT);
		qzone.setTargetUrl(GlobalConstant.QQZONE_SHARE_TARGET_URL);
		qzone.setTitle(GlobalConstant.QQZONE_SHARE_TITLE);
		qzone.setShareMedia(localImage);
		// qzone.setShareMedia(uMusic);
		mController.setShareMedia(qzone);

		
		// image.setTitle("thumb title");
		// image.setThumb("http://www.umeng.com/images/pic/social/integrated_3.png");

		// QQ分享
		QQShareContent qqShareContent = new QQShareContent();
		qqShareContent.setShareContent(GlobalConstant.QQ_SHARE_CONTENT);
		qqShareContent.setTitle(GlobalConstant.QQ_SHARE_TITLE);
		qqShareContent.setShareMedia(localImage);
		qqShareContent.setTargetUrl(GlobalConstant.QQ_SHARE_TARGET_URL);
		mController.setShareMedia(qqShareContent);

		// 设置短信分享内容
		SmsShareContent sms = new SmsShareContent();
		sms.setShareContent(GlobalConstant.SMS_SHARE_CONTENT);
		// sms.setShareImage(urlImage);
		mController.setShareMedia(sms);

		SinaShareContent sinaContent = new SinaShareContent();
		sinaContent.setShareContent(GlobalConstant.SINA_SHARE_CONTENT);
		mController.setShareMedia(sinaContent);

	}

	/**
	 * 调用postShare分享。跳转至分享编辑页，然后再分享。</br> [注意]<li>
	 * 对于新浪，豆瓣，人人，腾讯微博跳转到分享编辑页，其他平台直接跳转到对应的客户端
	 */
	public void postShare() {
		final CustomShareBoard shareBoard = new CustomShareBoard(mBaseActivity);
		backgroundAlpha(0.5f);
		shareBoard.setOnDismissListener(new poponDismissListener());
		shareBoard.showAtLocation(mBaseActivity.getWindow().getDecorView(),
				Gravity.BOTTOM, 0, 0);
		shareBoard.mBtnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (shareBoard.isShowing()) {
					shareBoard.dismiss();
					backgroundAlpha(1f);
				}
			}
		});
	}

	// @Override
	// public void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// super.onActivityResult(requestCode, resultCode, data);
	//
	// // 根据requestCode获取对应的SsoHandler
	// // UMSsoHandler sinaHandler =
	// // mController.getConfig().getSsoHandler(requestCode);
	// // if (sinaHandler != null) {
	// // sinaHandler.authorizeCallBack(requestCode, resultCode, data);
	// // }
	// UMSsoHandler ssoHandler = SocializeConfig.getSocializeConfig()
	// .getSsoHandler(requestCode);
	// if (ssoHandler != null) {
	// ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	// }
	// }

	/**
	 * 设置添加屏幕的背景透明度
	 */
	public void backgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = bgAlpha;
		getWindow().setAttributes(lp);
	}

	/**
	 * 添加弹出的popWin关闭的事件，主要是为了将背景透明度改回来
	 */
	class poponDismissListener implements
			android.widget.PopupWindow.OnDismissListener {
		@Override
		public void onDismiss() {
			backgroundAlpha(1f);
		}
	}

	/**
	 * 拨打电话号码
	 * 
	 * @param 电话号码
	 */
	public void callPhone(String telNo) {
		if (!Utils.isStringEmpty(telNo)) {
			Intent intent = new Intent("android.intent.action.CALL",
					Uri.parse("tel:" + telNo));
			startActivity(intent);
		}
	}

	/**
	 * 创建一个系统对话框
	 * 
	 * @param message
	 *            要显示的Title文字
	 * @return AlertDialog
	 */
	public AlertDialog alertDialog(String message) {
		final AlertDialog ad;
		AlertDialog.Builder builder = new AlertDialog.Builder(mBaseActivity);

		builder.setMessage(message)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).setNegativeButton("取消", null);

		ad = builder.create();
		ad.show();
		return ad;
	}

	/**
	 * 提示拨打电话对话框
	 */
	public void telDialog(String message, final String telNo) {

		final AlertDialog ad;
		AlertDialog.Builder builder = new AlertDialog.Builder(mBaseActivity);

		builder.setMessage(message)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						callPhone(telNo);
					}
				}).setNegativeButton("取消", null);

		ad = builder.create();
		ad.show();

	}

	/**
	 * 公用提示框
	 * 
	 * @author 赵洋洋
	 * @Title setBottomTabChecked
	 * @Description
	 * @param @param index
	 * @return void
	 */
	public void showToast(final String info) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(getApplicationContext(), info,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	// ************** 获取城市的区域列表 ************* //
	
	/**
	 * 获取城市的区域列表
	 * 
	 * @Title getAreaList
	 * @param cityID
	 *            城市ID
	 * @return List<AreaList> 区域列表 （id / title ）
	 */
	public  List<AreaList> getAreaList(String cityID) {
			// 用来封装参数
			RequestParams params = new RequestParams();

			params.addBodyParameter("city_id", cityID);

			HttpUtils http = new HttpUtils();
			http.send(HttpMethod.POST, ContantsValues.CITYAREA, params,
					new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							List<AreaList> areaLists = new ArrayList<AreaList>();
							AreaListInfo areaListInfo;
							if (!Utils.isStringEmpty(responseInfo.result)) {
								if (responseInfo.result.contains("data")) {
									try {
										// 数据处理
										areaListInfo = GsonUtils.json2Bean(
												responseInfo.result,
												AreaListInfo.class);
										if (areaListInfo.data != null
												&& areaListInfo.data.size() > 0) {
											for (int i = 0; i < areaListInfo.data
													.size(); i++) {
												AreaList areaInfo = new AreaList();
												if (areaListInfo.data.get(i).title != null) {
													areaInfo.title = areaListInfo.data
															.get(i).title;
													CCLog.i("城市的区域列表： ",
															areaListInfo.data
																	.get(i).title);
												}
												if (areaListInfo.data.get(i).id != null) {
													areaInfo.id = areaListInfo.data
															.get(i).id;
													CCLog.i("城市的区域ID： ",
															areaListInfo.data
																	.get(i).id);
												}
												areaLists.add(areaInfo);
											}
										}
									} catch (Exception e) {
									}
								}
							}
							AppApplacation.sAreaList = areaLists;
							CCLog.d("areaCount", "arealists size is :"+areaLists.size());
							
						}

						@Override
						public void onFailure(HttpException arg0, String msg) {
						}
					});
		
		return null;
	}

	// ************** 获取区域所在的商圈列表 ************* //
	List<DistrictList> districts;

	/**
	 * 获取区域所在的商圈列表
	 * 
	 * @Title getDistrictList
	 * @Description
	 * @param areaID
	 *            区域ID
	 * @return List<DistrictList>
	 */
	public List<DistrictList> getDistrictList(String areaID) {
		districts = new ArrayList<DistrictList>();
		// 用来封装参数
		RequestParams params = new RequestParams();
		params.addBodyParameter("area_id", areaID);

		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.POST, ContantsValues.SHANGQUAN, params,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						DistrictListInfo districtListInfo;
						if (!Utils.isStringEmpty(responseInfo.result)) {
							if (responseInfo.result.contains("data")) {
								try {
									// 数据处理
									districtListInfo = GsonUtils.json2Bean(
											responseInfo.result,
											DistrictListInfo.class);
									if (districtListInfo.data != null
											&& districtListInfo.data.size() > 0) {
										for (int i = 0; i < districtListInfo.data
												.size(); i++) {
											DistrictList district = new DistrictList();
											if (districtListInfo.data.get(i).title != null) {
												district.title = districtListInfo.data
														.get(i).title;
												CCLog.i("区域所在的商圈列表",
														districtListInfo.data
																.get(i).title);
											}
											if (districtListInfo.data.get(i).id != null) {
												district.id = districtListInfo.data
														.get(i).id;
											}
											districts.add(district);
										}
									}
								} catch (Exception e) {

								}

							}
						}
					}

					@Override
					public void onFailure(HttpException arg0, String msg) {
					}
				});
		return districts;
	}

	ClassifyListInfo mClassifyListInfo;

	/**
	 * 获取一级分类列表
	 * 
	 * @Title getOneClassifyList
	 */
	public void getOneClassifyList(String parent_id) {
		if (AppApplacation.sOneClassFy != null
				&& AppApplacation.sOneClassFy.size() > 0) {

			CCLog.i("已保存一级分类列表。");
			ApiAccess.dismissProgressDialog();
		} else {

			HttpUtils http = new HttpUtils();
			RequestParams params = new RequestParams();
			params.addBodyParameter("parent_id", parent_id);
			http.send(HttpMethod.POST, ContantsValues.BUSINESSTYPE, params,
					new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							ApiAccess.dismissProgressDialog();
							if (!Utils.isStringEmpty(responseInfo.result)) {
								if (responseInfo.result.contains("data")) {
									try {
										// 数据处理
										mClassifyListInfo = GsonUtils
												.json2Bean(responseInfo.result,
														ClassifyListInfo.class);
										if (mClassifyListInfo.data != null
												&& mClassifyListInfo.data
														.size() > 0) {
											AppApplacation.sOneClassFy.clear();
											for (int i = 0; i < mClassifyListInfo.data
													.size(); i++) {
												Classify classifyInfo = new Classify();
												if (mClassifyListInfo.data
														.get(i).title != null) {
													classifyInfo.title = mClassifyListInfo.data
															.get(i).title;
													CCLog.i("一级分类： ",
															mClassifyListInfo.data
																	.get(i).title);
												}
												if (mClassifyListInfo.data
														.get(i).id != null) {
													classifyInfo.id = mClassifyListInfo.data
															.get(i).id;
												}
												AppApplacation.sOneClassFy
														.add(classifyInfo);
											}
										}
									} catch (Exception e) {
										if (e.getMessage() != null) {
											CCLog.e("MainActivity",
													"获取一级分类出现异常\n"
															+ e.getMessage()
																	.toString());
										}
										ApiAccess.dismissProgressDialog();
									}

								}
							}
						}

						@Override
						public void onFailure(HttpException arg0, String msg) {
							ApiAccess.dismissProgressDialog();
						}
					});

		}
	}

	// ***********　　判断用户未登录时，提示信息等一些处理　************ //
	public void toastNotLogin(Context context) {
		UIHelper.ToastMessage(context, "您还未登录，请登录后操作。");
	}

	// ==== 华丽的分割线 下面内容涉及viewpager底部圆点处理 ==== //
	protected ViewPager mPager;
	protected HomePageAdapter mPagerAdapter;
	private ViewGroup mCircleView = null;
	private ImageButton preBtn = null;
	private List<String> mCircleData = new ArrayList<String>();

	private boolean timeFlag = false;
	private boolean theadFlag = true;
	private Handler myHandler = null;

	/**
	 * 初始化viewpager共通调用的组件信息等
	 * 
	 * @return void
	 */
	protected void initViewPagerCircleView() {
		setTopImageCircleView();
		mPager.setCurrentItem(0);
		// myHandler = new myHandler();
		// Thread thread = new Thread(new MyThread());
		// thread.start();
	}

	/**
	 * 画面初期化时，相关处理
	 */
	private void setTopImageCircleView() {
		if (mCircleView != null) {
			mCircleView.removeAllViews();
		}
		for (int i = 0; i < mCircleData.size(); i++) {
			ImageButton btn = new ImageButton(mBaseActivity);
			LinearLayout.LayoutParams vl = new LinearLayout.LayoutParams(15, 15);
			vl.setMargins(2, 0, 2, 0);
			btn.setLayoutParams(vl);
			if (i == 0) {
				btn.setBackgroundResource(R.drawable.icon_home_pager_circle_on);
			} else {
				btn.setBackgroundResource(R.drawable.icon_home_pager_circle_off);
			}
			mCircleView.addView(btn);
		}
		preBtn = null;
		if (mPager.getChildCount() > 0) {
			mPager.setCurrentItem(0);
		}
	}

	protected class ImageViewPagerChangeListener implements
			OnPageChangeListener {
		public ImageViewPagerChangeListener() {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			if (arg0 == 1) {
				timeFlag = true;
			}
		}

		@Override
		public void onPageScrolled(int position, float offset, int offsetPixel) {

		}

		@Override
		public void onPageSelected(int index) {
			if (preBtn == null) {
				preBtn = (ImageButton) mCircleView.getChildAt(0);
			}
			if (preBtn != null) {
				preBtn.setBackgroundResource(R.drawable.icon_home_pager_circle_off);
			}
			ImageButton currentBt = (ImageButton) mCircleView.getChildAt(index
					% mCircleData.size());

			currentBt
					.setBackgroundResource(R.drawable.icon_home_pager_circle_on);
			preBtn = currentBt;
		}
	}

	private void sleep() throws InterruptedException {
		Thread.sleep(3000);
		if (timeFlag) {
			timeFlag = false;
			sleep();
		}
	}

	private class myHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (mPager != null) {
				int currentItem = mPager.getCurrentItem();
				mPager.setCurrentItem(currentItem + 1);
			}
		}
	}

	private class MyThread implements Runnable {

		@Override
		public void run() {
			while (theadFlag) {
				try {
					sleep();
					Message message = new Message();
					message.what = 1;
					myHandler.sendMessage(message);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public List<String> getmCircleData() {
		return mCircleData;
	}

	public void setmCircleData(List<String> mCircleData) {
		this.mCircleData = mCircleData;
	}

	public ViewGroup getmCircleView() {
		return mCircleView;
	}

	public void setmCircleView(ViewGroup mCircleView) {
		this.mCircleView = mCircleView;
	}

	public ViewPager getmPager() {
		return mPager;
	}

	public void setmPager(ViewPager mPager) {
		this.mPager = mPager;
	}
	
	/**
	 * 关注状态改变广播
	 * @param shopId 
	 * @param isCanceFollow
	 * @param doWhat
	 */
	public void sendBroadCast(String shopId,boolean isCanceFollow,int doWhat) {
		Intent intent = new Intent(CommonConstant.STATUS_BROADCAST_ACTION);
		intent.putExtra("doWhat", doWhat);
        intent.putExtra("shopId", shopId);
        intent.putExtra("isCanceFollow", isCanceFollow);
        sendBroadcast(intent);
	}
	
	/**
	 * 评论数改变广播
	 * @param shopId 
	 * @param isCanceFollow
	 * @param doWhat
	 */
	public void sendBroadCastForCommentCountChange(String callId,boolean isAddCommentCount) {
		Intent intent = new Intent(CommonConstant.STATUS_BROADCAST_ACTION);
		intent.putExtra("doWhat", CommonConstant.doWhat_change_comment_count);
        intent.putExtra("callId", callId);
        intent.putExtra("isAddCommentCount", isAddCommentCount);
        sendBroadcast(intent);
	}
	
	/**
	 * 点赞改变数量广播
	 * @param shopId 
	 * @param isCanceFollow
	 * @param doWhat
	 */
	public void sendBroadCastForZan(String callId) {
		Intent intent = new Intent(CommonConstant.STATUS_BROADCAST_ACTION);
		intent.putExtra("doWhat", CommonConstant.doWhat_change_zan_count);
        intent.putExtra("callId", callId);
        sendBroadcast(intent);
	}
	
	/**
	 * 收藏改变数量广播
	 * @param shopId 
	 * @param isAddShoucang 是否为添加收藏
	 * @param doWhat
	 */
	public void sendBroadCastForShouCang(String callId,boolean isAddShoucang) {
		Intent intent = new Intent(CommonConstant.STATUS_BROADCAST_ACTION);
		intent.putExtra("doWhat", CommonConstant.doWhat_change_shoucang_count);
        intent.putExtra("callId", callId);
        intent.putExtra("isAddShoucang", isAddShoucang);
        sendBroadcast(intent);
	}
	
	/**
	 * 
	 * @param shopId 商铺id
	 * @param starCount 商铺星星个数
	 */
	public void sendBroadCastForShopStar(String shopId,String starCount) {
		Intent intent = new Intent(CommonConstant.STATUS_BROADCAST_ACTION);
		intent.putExtra("doWhat", CommonConstant.doWhat_change_shop_start_count);
        intent.putExtra("starCount", starCount);
        intent.putExtra("shopId", shopId);
        sendBroadcast(intent);
	}
	
	
	
	/**
	 * LEE
	 * 获取店铺星星数量
	 * @param shopId 店铺id
	 */
	public void getShopStar(final String shopId) {
		

		String url = ContantsValues.SHOP_STARS_COUNT_URL+"&shop_id="+shopId;
		CCLog.d(tag, "get shop start url :" +url);
		
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("shop_id", shopId);
		http.send(HttpRequest.HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!Utils.isStringEmpty(responseInfo.result)) {
							if (responseInfo.result.contains("status")) {
								if (responseInfo.result != null) {
								}
								try {
									// 数据处理
									JSONObject errorJsonObject = new JSONObject(
											responseInfo.result);
									if (errorJsonObject.has("status")) {
										JSONObject statusObject = errorJsonObject
												.optJSONObject("status");
										int code = statusObject.optInt("code");
										if (code == 1) {
											String strErrorMsg = statusObject
													.getString("message");
											UIHelper.ToastMessage(
													mBaseActivity, strErrorMsg);
										} else {
											JSONObject dataObject = errorJsonObject
													.optJSONObject("data");
											String shop_Id = dataObject.optString("shop_id");
											String starCount = dataObject.optString("star");
											gainShopStarCount(shop_Id,starCount);

										}
									}
								} catch (Exception e) {
									String errorMsg = ApiAccessErrorManager
											.getMessage(5, mBaseActivity);
									UIHelper.ToastMessage(mBaseActivity,
											errorMsg);
								}

							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						UIHelper.ToastMessage(mBaseActivity,
								R.string.response_data_invalid);
					}
				});
	}
	
	/**
	 * 
	 * @param shopId 店铺id
	 * @param starCount 店铺星星数量
	 */
	public void gainShopStarCount(String shopId,String starCount) {
		CCLog.d(tag, "gainShopStarCount-->shopId:"+shopId);
		CCLog.d(tag, "gainShopStarCount-->starCount:"+starCount);
	}
	
	
	
}
