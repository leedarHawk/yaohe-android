package com.collcloud.yaohe.activity.forgetpassword;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.login.LoginActivity;
import com.collcloud.yaohe.activity.resetpassword.ResetPasswordActivity;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.staticvalue.Staticvalue;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * @category 忘记密码页面
 * @author 赵洋洋
 * 
 */
public class ForgetPasswordActivity extends BaseActivity implements
		OnClickListener {

	/** 标题 */
	private TextView tv_actionbartitle;
	/** 返回 */
	private ImageView tv_actionbarback;
	/** 下一步按钮 */
	private Button btn_FindPassWord_next;
	/** 输入手机号码 */
	private EditText edt_userphonenum_fp;
	/** 获取验证码按钮 */
	private Button btnGetVerCode_fg_fp;
	/** 输入验证码 */
	private EditText edt_InvCode_fp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_password);
		intialSource();
	}

	private void intialSource() {
		this.tv_actionbartitle = (TextView) findViewById(R.id.tv_actionbartitle);
		tv_actionbartitle.setText("重置密码");
		this.tv_actionbarback = (ImageView) findViewById(R.id.tv_actionbarback);
		tv_actionbarback.setOnClickListener(this);
		LinearLayout layout = (LinearLayout) findViewById(R.id.ll_tv_actionbarback);
		layout.setOnClickListener(this);

		this.btn_FindPassWord_next = (Button) findViewById(R.id.btn_FindPassWord_next);
		btn_FindPassWord_next.setOnClickListener(this);
		this.edt_userphonenum_fp = (EditText) findViewById(R.id.edt_userphonenum_fp);
		this.btnGetVerCode_fg_fp = (Button) findViewById(R.id.btnGetVerCode_fg_fp);
		btnGetVerCode_fg_fp.setOnClickListener(this);
		this.edt_InvCode_fp = (EditText) findViewById(R.id.edt_InvCode_fp);
	}

	private void goNext() {
		// 点击下一步，首先验证输入手机号和验证码是否正确
		verificationOfInput();
	}

	/**
	 * 检验输入
	 */
	private void verificationOfInput() {
		if (TextUtils.isEmpty(edt_userphonenum_fp.getText().toString())) {
			showToast("手机号不能为空");
			return;
		}
		if (edt_userphonenum_fp.getText().toString().length() < 11) {
			showToast("手机号码不是11位");
			return;
		}
		if (TextUtils.isEmpty(edt_InvCode_fp.getText().toString())) {
			showToast("验证码不能为空");
			return;
		}
		validatecode();

	}

	/**
	 * 验证验证码
	 */
	private void validatecode() {

		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		// 封装post请求参数
		CCLog.v("要找回的手机号", Utils.strFromView(edt_userphonenum_fp));
		params.addBodyParameter("login_user",
				Utils.strFromView(edt_userphonenum_fp));

		CCLog.v("验证码", Utils.strFromView(edt_InvCode_fp));
		params.addBodyParameter("code", Utils.strFromView(edt_InvCode_fp));

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.RESETPASSWORD,
				params, new RequestCallBack<String>() {
					// 网络返回字符串
					String responseInfo = null;

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						CCLog.v("网络访问失败");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						JSONObject object, object2;
						String code = null;
						String responseMsg = "";
						try {
							object = new JSONObject(arg0.result);
							// 获取一个返回的字符串
							responseInfo = object.getString("status");
							object2 = new JSONObject(responseInfo);
							code = object2.getString("code");
							responseMsg = object2.getString("message");

						} catch (JSONException e) {
							e.printStackTrace();
						}
						// 表示验证码发送成功了
						if (code.equals("0")) {
							Staticvalue.varcode = Utils
									.strFromView(edt_InvCode_fp);
							Staticvalue.forgetUserPhone = Utils
									.strFromView(edt_userphonenum_fp);
							baseStartActivity(new Intent(
									ForgetPasswordActivity.this,
									ResetPasswordActivity.class));
						} else {
							showToast(responseMsg);
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
		params2.addBodyParameter("login_user", edt_userphonenum_fp.getText()
				.toString());

		// 发送请求获取验证码
		http2.send(HttpRequest.HttpMethod.POST,
				ContantsValues.RESETPASSWORDCODE, params2,
				new RequestCallBack<String>() {

					JSONObject object3, object4;
					String responseSms, code2, responseMsg2;

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// 网络以及其他原因获取验证码失败
						showToast("获取验证码失败,60s后重试!");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						try {
							// 获取状态码 0 or 1
							object3 = new JSONObject(arg0.result);

							responseSms = object3.getString("status");

							object4 = new JSONObject(responseSms);
							code2 = object4.getString("code");

							responseMsg2 = object4.getString("message");
						} catch (JSONException e) {
							// 异常信息
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
	 * 获取短信验证码
	 */
	public void getVerCode() {
		String user_phonenumber = edt_userphonenum_fp.getText().toString();
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
	 * 验证码计时器
	 */
	CountDownTimer cdt = null;

	/**
	 * 开始计时
	 */
	private void startcountdown() {
		btnGetVerCode_fg_fp.setClickable(false);
		btnGetVerCode_fg_fp.setSelected(true);
		cdt = new CountDownTimer(61 * 1000, 1 * 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				final int c = (int) millisUntilFinished / 1000;
				btnGetVerCode_fg_fp.setText("重新获取" + c + "s");
			}

			@Override
			public void onFinish() {
				btnGetVerCode_fg_fp.setText("获取验证码");
				btnGetVerCode_fg_fp.setClickable(true);
				btnGetVerCode_fg_fp.setSelected(false);
			}
		};
		cdt.start();
	}

	/**
	 * UI界面点击事件
	 */
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		/** 返回按钮 */
		case R.id.ll_tv_actionbarback:
			startActivity(new Intent(ForgetPasswordActivity.this,
					LoginActivity.class));
			finish();
			break;
		/** 获取验证码按钮 */
		case R.id.btnGetVerCode_fg_fp:
			getVerCode();
			break;
		/** 下一步安扭 */
		case R.id.btn_FindPassWord_next:
			goNext();
			break;
		default:
			break;
		}
	}

	@Override
	protected void resetLayout() {

		LinearLayout fp_root = (LinearLayout) findViewById(R.id.ll_fp_root);
		SupportDisplay.resetAllChildViewParam(fp_root);
	}

}
