package com.collcloud.yaohe.activity.register;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.login.LoginActivity;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.timertask.TimerTask.TimerCallBack;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.IntentUtil;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * @类说明 用户注册界面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月2日 下午3:02:18
 */
public class RegisterActivity extends BaseActivity implements OnClickListener,
		TimerCallBack {

	private static final String TAG = "Register";
	/** 用户协议 */
	private TextView tv_protocol;
	/** 注册标题 登录按钮 */
	private TextView register_tv_title, register_tv_login;
	/** 验证码按钮 */
	private TextView my_register_getvercode;
	/** 手机号 */
	private EditText user_phone;
	/** 验证码 */
	private EditText my_register_editinvcode;
	/** 密码 */
	private EditText my_register_editpassword;
	/** 进度条 */
	private Dialog reg_Dialog;
	/** 注册按钮 */
	private Button my_btnRegister;
	/** 返回按钮 */
	private LinearLayout ll_top_back;
	/** 注册协议 */
	private CheckBox cb_xieyi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		intialSource();
	}

	/**
	 * UI界面资源初始化
	 */
	private void intialSource() {

		this.ll_top_back = (LinearLayout) findViewById(R.id.ll_top_back);
		this.ll_top_back.setOnClickListener(this);

		register_tv_title = (TextView) findViewById(R.id.my_login_tv_top_title);
		register_tv_title.setText("注册");

		register_tv_login = (TextView) findViewById(R.id.my_login_top_tv_loginorregist);
		register_tv_login.setText("登录");
		register_tv_login.setOnClickListener(this);

		tv_protocol = (TextView) findViewById(R.id.my_register_tv_protocol);
		tv_protocol.setOnClickListener(this);
		tv_protocol.setText(Html.fromHtml("我已阅读并同意"
				+ "<font size=\"3\" color=\"red\">《吆喝用户协议》</font>"));
		// 获取验证码按钮
		this.my_register_getvercode = (TextView) findViewById(R.id.my_register_getvercode);
		//LEE注释
		//my_register_getvercode.setOnClickListener(this);

		this.user_phone = (EditText) findViewById(R.id.user_phone);
		this.my_register_editinvcode = (EditText) findViewById(R.id.my_register_editinvcode);
		this.my_register_editpassword = (EditText) findViewById(R.id.my_register_editpassword);

		this.my_btnRegister = (Button) findViewById(R.id.my_btnRegister);
		my_btnRegister.setOnClickListener(this);

		this.cb_xieyi = (CheckBox) findViewById(R.id.cb_xieyi);
		cb_xieyi.setOnClickListener(this);
	}

	/**
	 * 获取短信验证码
	 */
	public void getVerCode() {
		String user_phonenumber = user_phone.getText().toString();
		if (TextUtils.isEmpty(user_phonenumber)
				|| user_phonenumber.length() != 11) {
			showToast("请填写正确的手机号码");
			return;
		} else {
			accessNetRegGetSms();
			// 开始60秒到计时
			startcountdown();
			showToast("正在获取验证码，请稍候...");
		}
	}

	/**
	 * 创建进度条
	 */
	private void progressbar(Context context, int layout) {
		reg_Dialog = new AlertDialog.Builder(this).create();
		// mDialog.setOnKeyListener(keyListener);
		reg_Dialog.show();
		// 注意此处要放在show之后 否则会报异常
		reg_Dialog.setContentView(layout);
	}

	/**
	 * 输入验证
	 */
	private void register() {
		if (TextUtils.isEmpty(user_phone.getText().toString())) {
			showToast("手机号不能为空");
			return;
		} else if (Utils.strFromView(user_phone) != null
				&& Utils.strFromView(user_phone).length() <= 10) {
			showToast("手机号码不是11位");
			return;
		} 
		//LEE注释
//		else if (TextUtils.isEmpty(my_register_editinvcode.getText()
//				.toString())) {
//			showToast("验证码不能为空");
//			return;
//		}
		
		else if (TextUtils.isEmpty(my_register_editpassword.getText()
				.toString())) {
			showToast("密码不能为空");
			return;
		} else if (my_register_editpassword.getText().toString().length() < 6
				|| my_register_editpassword.getText().toString().length() > 32) {
			showToast("密码长度不是6到32位");
			return;
		} else if (!cb_xieyi.isChecked()) {
			showToast("您还未同意吆喝协议");
			return;
		}
		progressbar(RegisterActivity.this, R.layout.loading_progress);
		accessNetReg();
	}

	/**
	 * 验证码计时器
	 */
	CountDownTimer cdt = null;

	private void startcountdown() {
		my_register_getvercode.setClickable(false);
		my_register_getvercode.setSelected(true);
		cdt = new CountDownTimer(61 * 1000, 1 * 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				final int c = (int) millisUntilFinished / 1000;
				my_register_getvercode.setText("重新获取" + c + "s");
			}

			@Override
			public void onFinish() {
				my_register_getvercode.setText("获取验证码");
				my_register_getvercode.setClickable(true);
				my_register_getvercode.setSelected(false);
			}
		};
		cdt.start();
	}

	/**
	 * 访问网络进行注册
	 */
	private void accessNetReg() {

		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();

		// 获取用户名&密码&
		final String userName = user_phone.getText().toString();
		final String userPassword = my_register_editpassword.getText()
				.toString();
		String vCode = "";//my_register_editinvcode.getText().toString();

		// 封装post请求参数
		params.addBodyParameter("login_user", userName);
		params.addBodyParameter("login_pass", userPassword);
		params.addBodyParameter("code", vCode);

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.REGISTERURL,
				params, new RequestCallBack<String>() {
					// 网络返回字符串
					String responseInfo = null;
					String responsedate = null;

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						CCLog.v(TAG, "网络访问失败");
						reg_Dialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						CCLog.v(TAG, "网络访问成功");
						if (arg0.result  != null) {
							CCLog.i(TAG, arg0.result);
						}
						JSONObject object, object2, object3;
						String code = null;
						String responseMsg = null;
						String memberID = null;
						try {
							object = new JSONObject(arg0.result);
							// 获取一个返回的字符串
							responseInfo = object.getString("status");
							object2 = new JSONObject(responseInfo);
							code = object2.getString("code");
							responseMsg = object2.getString("message");

							responsedate = object.optString("data");
							object3 = new JSONObject(responsedate);
							memberID = object3.optString("member_id");

						} catch (JSONException e) {
							e.printStackTrace();
						}
						// 表示注册成功了
						if (code.equals("0")) {
							reg_Dialog.dismiss();

							mLoginDataManager.setLoginState("0");
							mLoginDataManager.setUserType("0");
							mLoginDataManager.setMemberId(memberID);
							mLoginDataManager.setUserPhone(userName);
							showToast("恭喜您，注册成功。");
							Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
							baseStartActivity(intent);
							RegisterActivity.this.finish();
						} else {// 注册失败了
							showToast(responseMsg);
							reg_Dialog.dismiss();
						}

					}
				});
	}

	/**
	 * 访问网络获取验证码
	 */
	private void accessNetRegGetSms() {

		HttpUtils http2 = new HttpUtils();
		RequestParams params2 = new RequestParams();

		// 封装用户名=手机号
		params2.addBodyParameter("login_user", user_phone.getText().toString());

		// 发送请求获取验证码
		http2.send(HttpRequest.HttpMethod.POST, ContantsValues.GETVCODE,
				params2, new RequestCallBack<String>() {

					JSONObject object3, object4;
					String responseSms, code2, responseMsg2;

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// 网络以及其他原因获取验证码失败
						showToast("获取验证码失败,60s后重试!");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						CCLog.v(TAG, "网络访问成功了");
						try {
							// 获取状态码 0 or 1
							object3 = new JSONObject(arg0.result);
							if (arg0.result != null) {
								CCLog.i(TAG, arg0.result);
							}
							responseSms = object3.getString("status");

							object4 = new JSONObject(responseSms);
							code2 = object4.getString("code");
							CCLog.v(TAG, code2);

							responseMsg2 = object4.getString("message");
							CCLog.v(TAG, responseMsg2);
						} catch (JSONException e) {
							// 异常信息
							CCLog.i(TAG, e.toString());
							e.printStackTrace();
						}

						// 验证码发送不成功
						if (code2.equals("1")) {
							showToast(responseMsg2);
						}
					}
				});
	}

	/**
	 * UI界面动作事件
	 */
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {

		case R.id.my_register_tv_protocol:
			// 点击用户协议跳转到h5页面去
			IntentUtil.toWebView(RegisterActivity.this, "注册协议",
					ContantsValues.HOST+"xieyi.html");
			break;

		// 点击登录按钮
		case R.id.my_login_top_tv_loginorregist:
			baseStartActivity(new Intent(RegisterActivity.this,
					LoginActivity.class));
			break;

		// 点击获取验证码按钮
		case R.id.my_register_getvercode:
			getVerCode();
			break;

		// 点击注册按钮
		case R.id.my_btnRegister:
			register();
			break;

		case R.id.ll_top_back:

			RegisterActivity.this.finish();
			break;

		default:
			break;
		}
	}

	/**
	 * UI界面重新计算
	 */
	@Override
	protected void resetLayout() {
		RelativeLayout rl_register_root = (RelativeLayout) findViewById(R.id.rl_register_root);
		SupportDisplay.resetAllChildViewParam(rl_register_root);
	}

	/**
	 * 唤醒等待中线程
	 */
	@Override
	public void notify(int currentCount) {
		// 唤醒等待中的线程
		if (currentCount > 0) {
			my_register_getvercode.setClickable(false);
			my_register_getvercode.setText("重新获取" + currentCount + "s");
		} else {
			my_register_getvercode.setClickable(true);
			my_register_getvercode.setText("获取验证码");
			my_register_getvercode.setSelected(false);
		}

	}

}
