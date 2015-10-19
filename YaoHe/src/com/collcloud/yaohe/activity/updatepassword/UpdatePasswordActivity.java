package com.collcloud.yaohe.activity.updatepassword;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.api.ApiAccess;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * @类说明 修改密码界面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月2日 下午3:02:18
 */
public class UpdatePasswordActivity extends BaseActivity implements
		OnClickListener {

	private static final String TAG = "UPPassword";
	/** 返回按钮 */
	private LinearLayout ll_top_back;
	/** 标题 */
	private TextView my_login_tv_top_title;
	/** 注册按钮 */
	private TextView my_login_top_tv_loginorregist;
	/** 旧密码 */
	private EditText up_old_password;
	/** 新密码 */
	private EditText up_new_password;
	/** 进度条 */
	private Dialog upPass_mDialog;
	/** 确认密码 */
	private EditText edt_re_password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_password);
		// 初始化资源
		initalSource();
	}

	private void verifyInput() {
		if (TextUtils.isEmpty(up_old_password.getText().toString())) {
			showToast("请输入原密码");
			return;
		} else if (TextUtils.isEmpty(up_new_password.getText().toString())) {
			showToast("请输入新密码");
			return;
		} else if (up_new_password.getText().toString()
				.equals(up_old_password.getText().toString())) {
			showToast("新密码不能与原密码相同");
			return;
		}
		
		else if (up_new_password.getText().toString().length() < 6) {
			showToast("密码长度6-32位");
			return;
		} else if (TextUtils.isEmpty(edt_re_password.getText().toString())) {
			showToast("请再次输入新密码");
			return;
		} else if (!up_new_password.getText().toString()
				.equals(edt_re_password.getText().toString())) {
			showToast("两次密码不一致，请重新输入确认密码");
			return;
		}

		// progressbar(this, R.layout.loading_progress);
		accessNet();
	}

	/**
	 * 访问网络
	 * */
	private void accessNet() {
		ApiAccess.showProgressDialog(UpdatePasswordActivity.this, "修改密码中...",
				R.style.progress_dialog);
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();

		// 封装post请求参数
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());
		// 封装post请求参数
		params.addBodyParameter("old_pass", up_old_password.getText()
				.toString());
		params.addBodyParameter("new_pass", up_new_password.getText()
				.toString());

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.UPPASSWORD,
				params, new RequestCallBack<String>() {

					// 网络返回字符串状态值
					String responseInfo = "";
					// 网络返回字符串data
					String responseData = "";

					@Override
					public void onFailure(HttpException arg0, String arg1) {

						CCLog.v(TAG, "网络访问失败");
						// upPass_mDialog.dismiss();
						ApiAccess.dismissProgressDialog();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						ApiAccess.dismissProgressDialog();
						JSONObject object, object2;
						// 网络访问状态码
						String code = "";
						// 网络访问返回的消息
						String responseMsg = "";

						try {
							if (arg0.result != null) {
								CCLog.i("修改密码状态信息：", arg0.result + " ");
							}
							object = new JSONObject(arg0.result);
							responseInfo = object.getString("status");

							object2 = new JSONObject(responseInfo);
							code = object2.optString("code");
							responseMsg = object2.optString("message");

							if (code.equals("0")) {
								// 密码修改成功后保存 & userpassword
								mLoginDataManager
										.setUserPassword(up_new_password
												.getText().toString());
								showToast("密码修改成功，请牢记新密码!");
								UpdatePasswordActivity.this.finish();

							} else {
								showToast(responseMsg);
							}

						} catch (JSONException e) {
							// 抛出异常
							e.printStackTrace();
						}

						// upPass_mDialog.dismiss();
					}
				});
	}

	private void initalSource() {
		my_login_tv_top_title = (TextView) findViewById(R.id.my_login_tv_top_title);
		my_login_tv_top_title.setText("修改密码");

		my_login_top_tv_loginorregist = (TextView) findViewById(R.id.my_login_top_tv_loginorregist);
		my_login_top_tv_loginorregist.setText("保存");
		my_login_top_tv_loginorregist.setOnClickListener(this);

		this.ll_top_back = (LinearLayout) findViewById(R.id.ll_top_back);
		this.ll_top_back.setOnClickListener(this);

		this.up_old_password = (EditText) findViewById(R.id.edt_old_password);
		this.up_new_password = (EditText) findViewById(R.id.edt_new_password);
		this.edt_re_password = (EditText) findViewById(R.id.edt_re_password);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.ll_top_back:
			UpdatePasswordActivity.this.finish();
			break;
		case R.id.my_login_top_tv_loginorregist:
			// 保存动作
			verifyInput();
			break;
		default:
			break;
		}
	}

	/**
	 * 页面加载
	 * */
	private void progressbar(Context context, int layout) {
		upPass_mDialog = new AlertDialog.Builder(this).create();
		// mDialog.setOnKeyListener(keyListener);
		upPass_mDialog.show();
		// 注意此处要放在show之后 否则会报异常
		upPass_mDialog.setContentView(layout);
	}

	@Override
	protected void resetLayout() {

		RelativeLayout rl_uppassword = (RelativeLayout) findViewById(R.id.rl_uppassword_root);
		SupportDisplay.resetAllChildViewParam(rl_uppassword);
	}

}
