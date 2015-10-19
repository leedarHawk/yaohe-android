package com.collcloud.yaohe.activity.resetpassword;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.forgetpassword.ForgetPasswordActivity;
import com.collcloud.yaohe.activity.login.LoginActivity;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.staticvalue.Staticvalue;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * @category 重置密码页面
 * @author 赵洋洋
 * 
 */
public class ResetPasswordActivity extends BaseActivity implements
		OnClickListener {

	private static final String TAG = "RESETPASSWORD";
	/** 标题 */
	private TextView tv_actionbartitle;
	/** 提交按钮 */
	private Button reset_PassWord_rs;
	/** 输入新密码 */
	private EditText edt_rs_PassWord;
	/** 确认新密码 */
	private EditText edt_ConfimPassWord;
	/** 返回按钮 */
	/** 提交按钮进度条 */
	private Dialog submit_mDialog;
	/** 返回按钮区域 */
	private LinearLayout ll_tv_actionbarback;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_password);
		intialSource();
	}

	/**
	 * 检验输入
	 */
	private void verificationOfInput() {
		if (TextUtils.isEmpty(edt_rs_PassWord.getText().toString())) {
			showToast("新密码不能为空");
			return;
		}
		if (edt_rs_PassWord.getText().toString().length() < 6) {
			showToast("新密码长度不能少于6位");
			return;
		}
		if (TextUtils.isEmpty(edt_ConfimPassWord.getText().toString())) {
			showToast("确认密码不能为空");
			return;
		}
		if (!edt_rs_PassWord.getText().toString()
				.equals(edt_ConfimPassWord.getText().toString())) {
			showToast("两次输入的密码不一致");
			return;
		}

		accessNet();
	}

	/**
	 * 初始化资源
	 */
	private void intialSource() {
		this.tv_actionbartitle = (TextView) findViewById(R.id.tv_actionbartitle);
		tv_actionbartitle.setText("重置密码");

		this.reset_PassWord_rs = (Button) findViewById(R.id.reset_PassWord_rs);
		reset_PassWord_rs.setOnClickListener(this);
		this.edt_rs_PassWord = (EditText) findViewById(R.id.edt_rs_PassWord);
		this.edt_ConfimPassWord = (EditText) findViewById(R.id.edt_ConfimPassWord);

		this.ll_tv_actionbarback = (LinearLayout) findViewById(R.id.ll_tv_actionbarback);
		ll_tv_actionbarback.setOnClickListener(this);
	}

	/**
	 * 提交时访问网络
	 * */
	private void progressbar(Context context, int layout) {
		submit_mDialog = new AlertDialog.Builder(this).create();
		// mDialog.setOnKeyListener(keyListener);
		submit_mDialog.show();
		// 注意此处要放在show之后 否则会报异常
		submit_mDialog.setContentView(layout);
	}

	@Override
	protected void resetLayout() {
		LinearLayout ll_reset = (LinearLayout) findViewById(R.id.ll_reset_root);
		SupportDisplay.resetAllChildViewParam(ll_reset);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		/** 返回按钮 */
		case R.id.ll_tv_actionbarback:
			baseStartActivity(new Intent(ResetPasswordActivity.this,
					ForgetPasswordActivity.class));
			finish();
			break;
		/** 提交按钮 */
		case R.id.reset_PassWord_rs:
			verificationOfInput();

		default:
			break;
		}
	}

	/**
	 * 访问网络进行密码重置
	 */
	private void accessNet() {
		progressbar(ResetPasswordActivity.this, R.layout.loading_progress);
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();

		// 封装post请求参数
		CCLog.v(TAG, "要找回的手机号" + Staticvalue.forgetUserPhone);
		params.addBodyParameter("login_user", Staticvalue.forgetUserPhone);

		CCLog.v(TAG, "验证码" + Staticvalue.varcode);
		params.addBodyParameter("code", Staticvalue.varcode);
		CCLog.v(TAG, "新密码" + edt_rs_PassWord.getText().toString());
		params.addBodyParameter("login_pass", edt_rs_PassWord.getText()
				.toString());

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.RESET_NEW_PASS,
				params, new RequestCallBack<String>() {
					// 网络返回字符串
					String responseInfo = null;

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						CCLog.v(TAG, "网络访问失败");
						submit_mDialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						CCLog.v(TAG, "网络访问成功");
						CCLog.v(TAG, arg0.result);
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
							submit_mDialog.dismiss();
							// 保存替换原始密码
							mLoginDataManager.setUserPassword(edt_rs_PassWord
									.getText().toString());
							showToast("新密码设定成功，请牢记。");

							baseStartActivity(new Intent(
									ResetPasswordActivity.this,
									LoginActivity.class));
							ResetPasswordActivity.this.finish();
						} else {// 重置密码失败了

							submit_mDialog.dismiss();
							showToast(responseMsg);
						}

					}
				});
	}

}
