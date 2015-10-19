package com.collcloud.yaohe.activity.friend.award;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.entity.Award;
import com.collcloud.yaohe.ui.adapter.AwardListAdapter;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * @类说明 奖品列表页面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月2日 下午3:02:18
 */
public class AwardActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "AWARD";
	/** 返回按钮 */
	private LinearLayout ll_top_back;
	/** 标题 */
	private TextView login_tv_title;
	/** 保存按钮 */
	private TextView tv_save_nickname;
	/** 数据加载进度条 */
	private Dialog mDialog;
	/** 数据列表 */
	private ListView mListview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_award);
		initalSource();
		
		progressbar(this, R.layout.loading_progress);
		accessNetGetBusiness();
	}

	/**
	 * 初始化资源
	 */
	private void initalSource() {

		this.ll_top_back = (LinearLayout) findViewById(R.id.ll_top_back);
		this.ll_top_back.setOnClickListener(this);

		this.login_tv_title = (TextView) findViewById(R.id.my_login_tv_top_title);
		this.login_tv_title.setText("中奖纪录");

		this.tv_save_nickname = (TextView) findViewById(R.id.my_login_top_tv_loginorregist);
		this.tv_save_nickname.setText("");

		mListview = (ListView) findViewById(R.id.lv_award_list);
	}

	@Override
	protected void resetLayout() {
		// 页面适配
		RelativeLayout rl_award_root = (RelativeLayout) findViewById(R.id.rl_award_root);
		SupportDisplay.resetAllChildViewParam(rl_award_root);
	}

	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {

		case R.id.ll_top_back:
			AwardActivity.this.finish();
			break;

		default:
			break;
		}
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
	 * 获取可使用的中奖数据
	 */
	private void accessNetGetBusiness() {

		HttpUtils http = new HttpUtils();
		// 用来封装参数
		RequestParams params = new RequestParams();

		// 用户ID
		CCLog.v(TAG, "当前用户的ID>>>>>" + mLoginDataManager.getMemberId());
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());
		params.addBodyParameter("page", "1");
		

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.ZHONGJIANGJILU, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {

						mDialog.dismiss();
						showToast("网络访问失败,检查网络设置");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {

						CCLog.v(TAG, "网络获取中奖数据成功");
						CCLog.v(TAG, arg0.result);
						JSONObject object, object2;
						// 网络返回商圈结果状态信息
						String responseInfo = "";
						// 网络访问商圈状态码
						String code = "";

						try {

							object = new JSONObject(arg0.result);

							responseInfo = object.getString("status");

							object2 = new JSONObject(responseInfo);

							code = object2.getString("code");

							if (code.equals("0")) {

								CCLog.v(TAG, "+" + code);

								JSONArray jsoArray = object
										.optJSONArray("data");
								final int awardItems = jsoArray.length();

								CCLog.v(TAG, jsoArray.toString());

								if (awardItems > 1) {

									/** 中奖数据集合 */
									ArrayList<Award> awardList = new ArrayList<Award>();

									for (int i = 0; i < awardItems; i++) {

										Award awardItem = new Award();

										JSONObject guanzhuObject = jsoArray
												.getJSONObject(i);

										awardItem.id = guanzhuObject
												.optString("id");
										CCLog.e(TAG, "中奖品id" + awardItem.id);
										awardItem.card_number = guanzhuObject
												.optString("card_number");
										awardItem.ptitle = guanzhuObject
												.optString("ptitle");

										awardList.add(awardItem);

									}

									mListview.setAdapter(new AwardListAdapter(
											AwardActivity.this, awardList));

									mDialog.dismiss();
								} else {
									mDialog.dismiss();
									showToast("您还没有中奖纪录");
								}

							} else {// 数据为空的时候

								CCLog.v(TAG, "加载的中奖数据为空");
								mDialog.dismiss();
								showToast("您还没有中奖纪录商家");
							}

						} catch (JSONException e) {
							mDialog.dismiss();
							CCLog.v(TAG, "中奖纪录解析异常" + e.toString());
							e.printStackTrace();
						}
					}
				});
	}

}
