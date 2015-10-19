package com.collcloud.yaohe.activity.person.youhui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.person.huiyuanka.PersonHYKActivity;
import com.collcloud.yaohe.activity.person.youhui.usableyouhuiquan.UsableYouHuiQuanActivity;
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
 * @类说明 我的(个人)优惠页面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月12日 下午5:02:18
 */
public class PersonYouhuiActivity extends BaseActivity implements
		OnClickListener {

	private static final String TAG = "MYH";
	/** 页面标提 */
	private TextView tv_actionbartitle;
	/** 页面返回 */
	private ImageView tv_actionbarback;
	/** 优惠券 */
	RelativeLayout rel_youhuiquan;
	/** 会员卡 */
	RelativeLayout rel_huiyuanka;
	/** 进度条 */
	private Dialog yhper_Dialog;
	/** 优惠券数量 */
	private String YHQnum = "0";
	/** 会员卡数量 */
	private String HYKnum = "0";
	/** 优惠券数量 */
	private TextView tv_youhuiquan_num;
	/** 会员卡数量 */
	private TextView tv_huiyuanka_num;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_youhui);

		progressbar(PersonYouhuiActivity.this, R.layout.loading_progress);

		intialSource();

		accessNetGetNum();
	}

	/**
	 * 获取优惠券&会员卡数量
	 */
	private void accessNetGetNum() {

		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();

		// 封装post请求参数
		// 用户ID
		CCLog.v(TAG, "当前用户的ID>>>>>" + mLoginDataManager.getMemberId());
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId()
				.toString());

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.YH, params,
				new RequestCallBack<String>() {
					// 网络返回字符串
					String responseInfo = null;
					String responseData = null;

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						CCLog.v(TAG, "获取优惠数量失败了");
						yhper_Dialog.dismiss();
						showToast("数据加载失败了");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						yhper_Dialog.dismiss();
						CCLog.v(TAG, "获取优惠数量成功");
						CCLog.v(TAG, arg0.result);
						JSONObject object, object2, object3;
						String code = null;
						String responseMsg = null;
						try {
							object = new JSONObject(arg0.result);
							// 获取一个返回的字符串
							responseInfo = object.getString("status");

							object2 = new JSONObject(responseInfo);

							code = object2.getString("code");
							responseMsg = object2.getString("message");

							// 数据获取成功
							if (code.equals("0")) {
								// 获取一个返回的数据值
								responseData = object.getString("data");

								object3 = new JSONObject(responseData);

								YHQnum = object3.getString("coupon_num");
								tv_youhuiquan_num.setText(YHQnum);

								HYKnum = object3.getString("card_num");
								tv_huiyuanka_num.setText(HYKnum);

							} else {// 失败了
								showToast(responseMsg);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				});
	}

	/**
	 * 加载进度条
	 */
	private void progressbar(Context context, int layout) {
		yhper_Dialog = new AlertDialog.Builder(this).create();
		// mDialog.setOnKeyListener(keyListener);
		yhper_Dialog.show();
		// 注意此处要放在show之后 否则会报异常
		yhper_Dialog.setContentView(layout);
	}

	/**
	 * UI界面资源初始化
	 */
	private void intialSource() {

		rel_youhuiquan = (RelativeLayout) findViewById(R.id.rel_youhuiquan);
		rel_youhuiquan.setOnClickListener(this);

		rel_huiyuanka = (RelativeLayout) findViewById(R.id.rel_huiyuanka);
		rel_huiyuanka.setOnClickListener(this);

		this.tv_actionbarback = (ImageView) findViewById(R.id.tv_actionbarback);
		tv_actionbarback.setOnClickListener(this);

		this.tv_actionbartitle = (TextView) findViewById(R.id.tv_actionbartitle);
		tv_actionbartitle.setText("我的优惠");

		this.tv_youhuiquan_num = (TextView) findViewById(R.id.tv_youhuiquan_num);
		tv_youhuiquan_num.setText(YHQnum);

		this.tv_huiyuanka_num = (TextView) findViewById(R.id.tv_huiyuanka_num);
		tv_huiyuanka_num.setText(HYKnum);

	}

	/**
	 * UI界面动作事件
	 */
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {

		case R.id.rel_youhuiquan:
			baseStartActivity(new Intent(PersonYouhuiActivity.this,
					UsableYouHuiQuanActivity.class));
			break;
		case R.id.rel_huiyuanka:
			baseStartActivity(new Intent(PersonYouhuiActivity.this,
					PersonHYKActivity.class));
			break;
		case R.id.ll_tv_actionbarback:
			this.finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 页面适配
	 */
	@Override
	protected void resetLayout() {

		RelativeLayout rel_myyhq = (RelativeLayout) findViewById(R.id.rl_yhq_root);
		SupportDisplay.resetAllChildViewParam(rel_myyhq);

		LinearLayout llLayout = (LinearLayout) findViewById(R.id.ll_tv_actionbarback);
		llLayout.setOnClickListener(this);
	}

}
