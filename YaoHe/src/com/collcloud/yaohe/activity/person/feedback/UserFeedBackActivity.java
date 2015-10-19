package com.collcloud.yaohe.activity.person.feedback;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.business.BusinessActivity;
import com.collcloud.yaohe.activity.my.MineActivity;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.GlobalVariable;
import com.collcloud.yaohe.common.base.SupportDisplay;
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
 * @类说明 用户反馈
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月2日 下午3:02:18
 */
public class UserFeedBackActivity extends BaseActivity implements
		OnClickListener {

	private static final String TAG = "FEEDBACK";
	/** 返回 */
	private LinearLayout ll_common_top_back;
	/** 标题 */
	private TextView tv_title;
	/** 提交 */
	private TextView tv_do;
	/** 标题 */
	private EditText edt_input_feedback;
	/** 标题 */
	private EditText edt_input_fb_numb;
	/** 进度条 */
	private Dialog mDialog;
	/** 提交按鈕*/
	LinearLayout ll_tv_do;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_feed_back);

		intialSource();
	}

	/**
	 * 页面加载
	 * */
	private void progressbar(Context context, int layout) {
		mDialog = new AlertDialog.Builder(this).create();
		// mDialog.setOnKeyListener(keyListener);
		mDialog.show();
		// 注意此处要放在show之后 否则会报异常
		mDialog.setContentView(layout);
	}

	/**
	 * UI界面资源初始化
	 * */
	private void intialSource() {

		ll_common_top_back = (LinearLayout) findViewById(R.id.ll_common_top_back);
		ll_common_top_back.setOnClickListener(this);
		
		ll_tv_do = (LinearLayout) findViewById(R.id.ll_tv_do);
		ll_tv_do.setOnClickListener(this);

		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("意见反馈");
		tv_do = (TextView) findViewById(R.id.tv_do);
		tv_do.setText("提交");

		edt_input_feedback = (EditText) findViewById(R.id.edt_input_feedback);
		edt_input_fb_numb = (EditText) findViewById(R.id.edt_input_fb_numb);
		
		if (mLoginDataManager.getLoginState().equals("1")) {
			if (!Utils.isStringEmpty(mLoginDataManager.getUserPhone())) {
				edt_input_fb_numb.setText(mLoginDataManager.getUserPhone());
			}
		}
	}

	/**
	 * UI界面点击事件
	 * */
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {

		case R.id.ll_common_top_back:

			if (mLoginDataManager.getUserType().equals("0")) {
				startActivity(new Intent(UserFeedBackActivity.this,
						MineActivity.class));
				finish();
			} else {
				startActivity(new Intent(UserFeedBackActivity.this,
						BusinessActivity.class));
				finish();
			}
			break;

		case R.id.ll_tv_do:
			// 提交到
			verifyInput();
			break;
		default:
			break;
		}
	}

	/**
	 * 输入校验
	 */
	public void verifyInput() {
		if (TextUtils.isEmpty(edt_input_feedback.getText().toString())) {
			showToast("反馈信息不能为空");
			return;
		}
		if (Utils.isStringEmpty(Utils.strFromView(edt_input_fb_numb))) {
			showToast("请输入手机号，以便我们及时给您反馈");
			return;
		}
		progressbar(this, R.layout.loading_progress);
		accessNet();
	}

	/**
	 * 访问网络意见反馈
	 */
	private void accessNet() {

		HttpUtils http = new HttpUtils();

		// 用来封装参数
		RequestParams params = new RequestParams();

		// 用户ID
		CCLog.v(TAG, "当前用户的ID>>>>>" + mLoginDataManager.getMemberId());
		params.addBodyParameter("member_id",mLoginDataManager.getMemberId());
		CCLog.v(TAG, "所在城市的ID>>>>>" + GlobalVariable.sChoiceCityID);
		params.addBodyParameter("city_id", GlobalVariable.sChoiceCityID);
		// 店铺名
		CCLog.v(TAG, "电话>>>>>" + edt_input_fb_numb.getText().toString());
		params.addBodyParameter("tel", edt_input_fb_numb.getText().toString());
		// 反馈内容
		CCLog.v(TAG, "反馈内容>>>>>" + edt_input_feedback.getText().toString());
		params.addBodyParameter("content", edt_input_feedback.getText()
				.toString());
		// 二级分类
		CCLog.v(TAG, "反馈终端>>>>>" + "Android");
		params.addBodyParameter("terminal", "Android");

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.FEEDBACK, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

						CCLog.v(TAG, "网络意见反馈失败!");
						showToast("网络意见反馈失败");
						mDialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						mDialog.dismiss();

						CCLog.v(TAG, "网络意见反馈成功!");

						CCLog.v(TAG, arg0.result);

						JSONObject object, object2;
						// 网络返回商圈结果状态信息
						String responseInfo = "";
						// 网络访问商圈状态码
						String code = "";
						// 网络商圈返回消息
						String responseMsg = "";

						try {

							object = new JSONObject(arg0.result);

							responseInfo = object.getString("status");

							object2 = new JSONObject(responseInfo);

							code = object2.getString("code");

							responseMsg = object2.getString("message");

						} catch (JSONException e) {
							e.printStackTrace();
						}

						if (code.equals("0")) {// 请求成功

							CCLog.v(TAG, "+" + code);
							dialog();

							CCLog.v(TAG, "意见反馈成功.....");

						} else {// 返回提示信息

							CCLog.v(TAG, "意见反馈时出错啦");

							showToast(responseMsg);
						}

					}
				});
	}

	/**
	 * 弹出对话框
	 */
	protected void dialog() {

		AlertDialog.Builder builder = new Builder(UserFeedBackActivity.this);
		builder.setMessage("感谢您的宝贵意见!");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

				if (mLoginDataManager.getUserType().equals("0")) {
					startActivity(new Intent(UserFeedBackActivity.this,
							MineActivity.class));
					finish();
					arg0.dismiss();

				} else {

					startActivity(new Intent(UserFeedBackActivity.this,
							BusinessActivity.class));
					finish();
					arg0.dismiss();
				}
			}

		});

		builder.create().show();
	}

	/**
	 * 屏幕重新计算
	 */
	@Override
	protected void resetLayout() {

		RelativeLayout rel_userfb_root = (RelativeLayout) findViewById(R.id.rl_user_feedback_root);
		SupportDisplay.resetAllChildViewParam(rel_userfb_root);
	}

}
