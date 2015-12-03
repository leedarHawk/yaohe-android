package com.collcloud.yaohe.activity.login;

import java.util.List;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.business.BusinessActivity;
import com.collcloud.yaohe.activity.forgetpassword.ForgetPasswordActivity;
import com.collcloud.yaohe.activity.my.MineActivity;
import com.collcloud.yaohe.activity.register.RegisterActivity;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.constants.CommonConstant;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECDevice.ECConnectState;
import com.yuntongxun.ecsdk.ECDevice.OnECDeviceConnectListener;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.OnChatReceiveListener;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.im.group.ECGroupNoticeMessage;

/**
 * @类说明 用户登录界面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月2日 下午3:02:18
 */
public class LoginActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "Login";
	/** 标题 */
	private TextView login_tv_title;
	/** 注册按钮 */
	private TextView login_tv_regist;
	/** 忘记密码 */
	private TextView my_login_forgetPassWord;
	/** 用户名 */
	private EditText my_login_et_editUserName;
	/** 清除用户名 */
	private ImageButton imgbtn_clear_username;
	/** 密码 */
	private EditText my_login_et_editPassWord;
	/** 清除密码 */
	private ImageButton imgbtn_clear_password;
	/** 登录按钮 */
	private Button my_login_btnLogin;
	/** 登录进度条 */
	private Dialog login_mDialog;
	/** 返回按钮 */
	private LinearLayout ll_top_back;
	/** IM聊天参数 */
	private ECInitParams params;
	/** IM聊天参数 */
	public static final String ACTION_SDK_CONNECT = "com.yuntongxun.Intent_Action_SDK_CONNECT";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// 初始化资源
		initalSource();
	}

	/**
	 * 登录方法
	 */
	private void login() {
		if (TextUtils.isEmpty(my_login_et_editUserName.getText().toString())) {
			showToast("用户名不能为空");
			return;
		} else if (Utils.strFromView(my_login_et_editUserName) != null
				&& Utils.strFromView(my_login_et_editUserName).length() <= 10) {
			showToast("手机号码不是11位");
			return;
		} else if (TextUtils.isEmpty(my_login_et_editPassWord.getText()
				.toString())) {
			showToast("密码不能为空");
			return;
		} else if (my_login_et_editPassWord.getText().toString().length() < 6
				|| my_login_et_editPassWord.getText().toString().length() > 32) {
			showToast("密码长度6-32位");
			return;
		}
		progressbar(this, R.layout.loading_progress);
		accessNet();
	}

	/**
	 * 初始化资源
	 */
	private void initalSource() {

		this.ll_top_back = (LinearLayout) findViewById(R.id.ll_top_back);
		this.ll_top_back.setOnClickListener(this);

		this.login_tv_title = (TextView) findViewById(R.id.my_login_tv_top_title);
		this.login_tv_title.setText("登录");

		this.login_tv_regist = (TextView) findViewById(R.id.my_login_top_tv_loginorregist);
		this.login_tv_regist.setText("注册");
		this.login_tv_regist.setOnClickListener(this);

		this.my_login_forgetPassWord = (TextView) findViewById(R.id.my_login_forgetPassWord);
		my_login_forgetPassWord.setOnClickListener(this);

		this.my_login_et_editUserName = (EditText) findViewById(R.id.my_login_et_editUserName);
		this.my_login_et_editPassWord = (EditText) findViewById(R.id.my_login_et_editPassWord);
		this.imgbtn_clear_username = (ImageButton) findViewById(R.id.imgbtn_clear_username);
		this.imgbtn_clear_password = (ImageButton) findViewById(R.id.imgbtn_clear_password);
		imgbtn_clear_username.setOnClickListener(this);
		imgbtn_clear_password.setOnClickListener(this);

		my_login_et_editUserName.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length()>0){
					imgbtn_clear_username.setVisibility(View.VISIBLE);
				}else{
					imgbtn_clear_username.setVisibility(View.GONE);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		my_login_et_editPassWord.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length()>0){
					imgbtn_clear_password.setVisibility(View.VISIBLE);
				}else {
					imgbtn_clear_password.setVisibility(View.GONE);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		this.my_login_btnLogin = (Button) findViewById(R.id.my_login_btnLogin);
		my_login_btnLogin.setOnClickListener(this);
		my_login_et_editUserName.setText(mLoginDataManager.getUserPhone());
	}

	/**
	 * UI界面组件点击事件
	 */
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {

		case R.id.ll_top_back:
			// 返回按钮
			LoginActivity.this.finish();
			break;

		case R.id.imgbtn_clear_username:
			// 清除用户名
			my_login_et_editUserName.setText("");
			break;
		case R.id.imgbtn_clear_password:
			// 清除密码
			my_login_et_editPassWord.setText("");
			break;

		case R.id.my_login_top_tv_loginorregist:
			// 注册按钮
			baseStartActivity(new Intent(LoginActivity.this,
					RegisterActivity.class));
			break;

		case R.id.my_login_forgetPassWord:
			// 忘记密码
			baseStartActivity(new Intent(LoginActivity.this,
					ForgetPasswordActivity.class));
			break;

		case R.id.my_login_btnLogin:
			// 点击登录按钮
			login();
			break;

		default:
			break;
		}
	}

	/**
	 * 访问网络
	 * */
	private void accessNet() {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();

		// 封装post请求参数
		params.addBodyParameter("login_user", my_login_et_editUserName
				.getText().toString());
		params.addBodyParameter("login_pass", my_login_et_editPassWord
				.getText().toString());

		CCLog.v(TAG, "用户名>>" + my_login_et_editUserName.getText().toString());
		CCLog.v(TAG, "密码>>" + my_login_et_editPassWord.getText().toString());

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.LOGIN, params,
				new RequestCallBack<String>() {

					// 网络返回字符串状态值
					String responseInfo = "";
					// 网络返回字符串data
					String responseData = "";

					@Override
					public void onFailure(HttpException arg0, String arg1) {

						CCLog.v(TAG, "网络访问失败");
						login_mDialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						CCLog.v(TAG, "网络访问成功");
						CCLog.v(TAG, arg0.result);

						JSONObject object, object2, object3;
						// 网络访问状态码
						String code = "";
						// 网络访问返回的消息
						String responseMsg = "";

						try {
							object = new JSONObject(arg0.result);
							responseInfo = object.getString("status");

							object2 = new JSONObject(responseInfo);
							code = object2.getString("code");
							responseMsg = object2.getString("message");

							if (code.equals("0")) {
								// 登录成功后保存 id & userphone & userpassword

								mLoginDataManager
										.setUserPhone(my_login_et_editUserName
												.getText().toString());
								mLoginDataManager
										.setUserPassword(my_login_et_editPassWord
												.getText().toString());

								mLoginDataManager.setLoginState("1");

								responseData = object.getString("data");

								object3 = new JSONObject(responseData);

								mLoginDataManager.setMemberId(object3
										.optString("id"));
								if (object3.getString("type").toString()
										.equals("1")) {
									CCLog.v(TAG, "我的身份判断为商家");

									mLoginDataManager.setBusinessState("1");

									mLoginDataManager.setUserType("1");

								} else {

									CCLog.v(TAG, "我的身份判断为个人");

									mLoginDataManager.setBusinessState("0");

									mLoginDataManager.setUserType("0");
								}

								if (mLoginDataManager.getUserType().equals("1")) {
									// 商家 跳转到商家主页面
									baseStartActivity(new Intent(
											LoginActivity.this,
											BusinessActivity.class));
									Intent intentLoginOut = new Intent(CommonConstant.PERSON_LOGINOUT_BROADCAST_ACTION);
									intentLoginOut.putExtra("exit", true);
									sendBroadcast(intentLoginOut);
									sendBroadcast(intentLoginOut);
									finish();
								} else {
									// 个人 跳转到个人主页面
									baseStartActivity(new Intent(
											LoginActivity.this,
											MineActivity.class));
									Intent intentLoginOut = new Intent(CommonConstant.BUSINESS_LOGINOUT_BROADCAST_ACTION);
									intentLoginOut.putExtra("exit", true);
									sendBroadcast(intentLoginOut);
									sendBroadcast(intentLoginOut);
									
									finish();
								}

								CCLog.v(TAG, "我的身份" + object3.getString("type"));
							} else {
								showToast(responseMsg);
							}

						} catch (JSONException e) {
							// 抛出异常
							CCLog.v(TAG, e.toString());
							e.printStackTrace();
						}

						login_mDialog.dismiss();
					}
				});
	}

	/**
	 * 页面加载
	 * */
	private void progressbar(Context context, int layout) {
		login_mDialog = new AlertDialog.Builder(this).create();
		// mDialog.setOnKeyListener(keyListener);
		login_mDialog.show();
		// 注意此处要放在show之后 否则会报异常
		login_mDialog.setContentView(layout);
	}

	private void initEcSdk() {
		// TODO 烧饼
		if (!ECDevice.isInitialized()) {
			ECDevice.initial(AppApplacation.getInstance()
					.getApplicationContext(), new ECDevice.InitListener() {

				@Override
				public void onInitialized() {
					// 初始化成功
					if (params == null || params.getInitParams() == null
							|| params.getInitParams().isEmpty()) {
						params = new ECInitParams();
					}
					// 重置
					params.reset();
					params.setUserid(mLoginDataManager.getUserPhone());
					params.setAppKey("aaf98f894f06f288014f0788d72901c8");
					params.setToken("34cea21364846c65119fc23913912df3");
					// 1代表用户名+密码登陆（可以强制上线，踢掉已经在线的设备）
					// 2代表自动重连注册（如果账号已经在其他设备登录则会提示异地登陆）
					// 3 LoginMode（FORCE_LOGIN AUTO）
					params.setMode(ECInitParams.LoginMode.AUTO);
					// 设置登陆验证模式（是否验证密码）
					params.setAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);

					// 第三步：验证参数是否正确，注册SDK
					if (!params.validate()) {

						Intent intent = new Intent(ACTION_SDK_CONNECT);
						intent.putExtra("error", -1);
						sendBroadcast(intent);
						return;
					}

					params.setOnDeviceConnectListener(new OnECDeviceConnectListener() {

						@Override
						public void onDisconnect(ECError arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onConnectState(ECConnectState state,
								ECError error) {

							if (state == ECDevice.ECConnectState.CONNECT_FAILED
									&& error.errorCode == SdkErrorCode.SDK_KICKED_OFF) {
								CCLog.v(TAG, "IM链接成功");
							}

							Intent intent = new Intent(ACTION_SDK_CONNECT);
							intent.putExtra("error", error.errorCode);
							sendBroadcast(intent);

						}

						@Override
						public void onConnect() {
							CCLog.v(TAG, "IM链接失败");
						}
					});

					params.setOnChatReceiveListener(new OnChatReceiveListener() {

						@Override
						public void onSoftVersion(String arg0, int arg1) {

						}

						@Override
						public void onServicePersonVersion(int arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onReceiveOfflineMessageCompletion() {
							// TODO Auto-generated method stub

						}

						// IM开发文档
						// http://www.yuntongxun.com/api/im/wiki#point_box
						@Override
						public void onReceiveOfflineMessage(List<ECMessage> arg0) {

							// CCLog.V(TAG,
							// "[onReceiveOfflineMessage] show notice false");
							// if (msgs != null && !msgs.isEmpty() &&
							// !isFirstSync)
							// isFirstSync = true;
							// for (ECMessage msg : msgs) {
							// ECTextMessageBody msg1 = (ECTextMessageBody) msg
							// .getBody();
							// ChatInfo ddd = new ChatInfo(account, msg1
							// .getMessage(),0);
							// arrayList.add(ddd);
							// baseAdapter.notifyDataSetChanged();
							//
							// }
						}

						@Override
						public void onReceiveDeskMessage(ECMessage arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onOfflineMessageCount(int arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public int onGetOfflineMessage() {
							// TODO Auto-generated method stub
							return 0;
						}

						@Override
						public void OnReceivedMessage(ECMessage arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void OnReceiveGroupNoticeMessage(
								ECGroupNoticeMessage arg0) {
							// TODO Auto-generated method stub

						}
					});
					ECDevice.login(params);
				}

				@Override
				public void onError(Exception arg0) {
					// SDK 初始化失败,可能有如下原因造成
					// 1、可能SDK已经处于初始化状态
					// 2、SDK所声明必要的权限未在清单文件（AndroidManifest.xml）里配置、
					// 或者未配置服务属性android:exported="false";
					// 3、当前手机设备系统版本低于ECSDK所支持的最低版本（当前ECSDK支持
					// Android Build.VERSION.SDK_INT 以及以上版本）
					CCLog.v(TAG, "IMSDK初始化失败");
				}
			});
		}
	}

	/**
	 * 页面适配重新计算
	 * */
	@Override
	protected void resetLayout() {

		RelativeLayout rel_login = (RelativeLayout) findViewById(R.id.rl_login_root);
		SupportDisplay.resetAllChildViewParam(rel_login);
	}

}
