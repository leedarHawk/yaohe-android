package com.collcloud.yaohe.upnickname;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.person.PersonActivity;
import com.collcloud.yaohe.api.ApiAccess;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.staticvalue.Staticvalue;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.UIHelper;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class UpNicknameActivity extends BaseActivity implements OnClickListener {

	/** 返回按钮 */
	private LinearLayout ll_top_back;
	/** 标题 */
	private TextView login_tv_title;
	/** 保存按钮 */
	private TextView tv_save_nickname;
	/** 清除按钮 */
	private ImageButton imbt_clern;
	/** 昵称 */
	private EditText edt_newnickname;
	/** 进度条 */
	private Dialog m_Dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_up_nickname);

		initalSource();

	}

	/**
	 * 访问网络进行修改昵称
	 */
	private void accessNetReg() {
		ApiAccess.showProgressDialog(UpNicknameActivity.this, "修改昵称中...",
				R.style.progress_dialog);
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();

		// 封装post请求参数
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());
		params.addBodyParameter("nickname", Utils.strFromView(edt_newnickname));

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.UPNICKNAME,
				params, new RequestCallBack<String>() {
					// 网络返回字符串
					String responseInfo = null;

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// m_Dialog.dismiss();
						ApiAccess.dismissProgressDialog();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						ApiAccess.dismissProgressDialog();
						JSONObject object, object2;
						String code = null;
						String responseMsg = null;
						try {
							object = new JSONObject(arg0.result);
							if (arg0.result != null) {
								CCLog.i("修改昵称状态信息：", arg0.result);
							}
							// 获取一个返回的字符串
							responseInfo = object.getString("status");
							object2 = new JSONObject(responseInfo);
							code = object2.getString("code");
							responseMsg = object2.getString("message");

						} catch (JSONException e) {
							e.printStackTrace();
						}
						// 表示修改昵称成功了
						if (code.equals("0")) {
							// m_Dialog.dismiss();
							mLoginDataManager.setUserNickname(Utils
									.strFromView(edt_newnickname));
							UIHelper.ToastMessage(UpNicknameActivity.this,
									"昵称修改成功。");
							Intent intent = new Intent(UpNicknameActivity.this,
									PersonActivity.class);
							baseStartActivity(intent);
							UpNicknameActivity.this.finish();

						} else {// 昵称修改失败了
							showToast(responseMsg);
							// m_Dialog.dismiss();
						}

					}
				});
	}

	/**
	 * 初始化资源
	 */
	private void initalSource() {

		this.ll_top_back = (LinearLayout) findViewById(R.id.ll_top_back);
		this.ll_top_back.setOnClickListener(this);

		this.imbt_clern = (ImageButton) findViewById(R.id.imbt_clern);
		this.imbt_clern.setOnClickListener(this);

		this.login_tv_title = (TextView) findViewById(R.id.my_login_tv_top_title);
		this.login_tv_title.setText("修改昵称");

		this.tv_save_nickname = (TextView) findViewById(R.id.my_login_top_tv_loginorregist);
		this.tv_save_nickname.setText("保存");
		this.tv_save_nickname.setOnClickListener(this);

		this.edt_newnickname = (EditText) findViewById(R.id.edt_newnickname);
		if (!Utils.isStringEmpty(Staticvalue.sNickName)) {
			edt_newnickname.setText(Staticvalue.sNickName);
		}

	}

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {

		case R.id.imbt_clern:
			edt_newnickname.setText("");
			break;
		case R.id.my_login_top_tv_loginorregist:

			verifyInput();
			break;

		case R.id.ll_top_back:
			finish();
			break;

		default:
			break;
		}

	}

	/**
	 * UI界面输入检验
	 */
	private void verifyInput() {

		if (TextUtils.isEmpty(edt_newnickname.getText().toString())) {
			showToast("请填写昵称");
			return;
		}
		// progressbar(this, R.layout.loading_progress);
		accessNetReg();
	}

	/**
	 * 页面加载
	 * */
	private void progressbar(Context context, int layout) {
		m_Dialog = new AlertDialog.Builder(this).create();
		// mDialog.setOnKeyListener(keyListener);
		m_Dialog.show();
		// 注意此处要放在show之后 否则会报异常
		m_Dialog.setContentView(layout);
	}

	@Override
	protected void resetLayout() {

		// 页面适配
		RelativeLayout rl_up_nickname_root = (RelativeLayout) findViewById(R.id.rl_up_nickname_root);
		SupportDisplay.resetAllChildViewParam(rl_up_nickname_root);
	}

}
