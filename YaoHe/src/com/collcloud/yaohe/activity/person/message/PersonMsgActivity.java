package com.collcloud.yaohe.activity.person.message;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.person.pinglun.PersonPingLunActivity;
import com.collcloud.yaohe.activity.person.zan.PersonZanActivity;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.GlobalConstant;
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
 * @类说明 我的(个人信息)页面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月12日 下午5:02:18
 */
public class PersonMsgActivity extends BaseActivity implements OnClickListener {

	/** 页面标题 */
	private TextView tv_per_msg_title;
	/** 页面返回按钮 */
	private LinearLayout ll_tv_actionbarback;

	private TextView mTvCommentCount;
	private TextView mTvZanCount;
	private TextView mTvCommentTime;
	private TextView mTvZanTime;
	private RelativeLayout mRlComment;
	private RelativeLayout mRlZan;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_msg);
		// 初始化资源
		intialSource();

		

	}

	private void getPerMsg() {

		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();

		CCLog.v(GlobalConstant.TAG,
				"当前用户的ID>>>>>" + mLoginDataManager.getMemberId());
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId()
				.toString());

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.PERSON_MY_MSG,
				params, new RequestCallBack<String>() {
					// 网络返回字符串
					String responseInfo = null;
					String responseData = null;

					@Override
					public void onFailure(HttpException arg0, String arg1) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						CCLog.v(GlobalConstant.TAG, "获取个人消息数量成功");
						CCLog.v(GlobalConstant.TAG, arg0.result);
						JSONObject object, object2, object3;
						String code = null;
						String responseMsg = null;
						String commentCount = null;
						String zanCount = null;
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

								commentCount = object3.optString("commentnum");
								if (Utils.isStringEmpty(commentCount)
										|| commentCount.equals("0")) {
									mTvCommentCount.setVisibility(View.GONE);
								} else {
									mTvCommentCount.setVisibility(View.VISIBLE);
									mTvCommentCount.setText(commentCount);
								}
								zanCount = object3.getString("zannum");
								if (Utils.isStringEmpty(zanCount)
										|| zanCount.equals("0")) {
									mTvZanCount.setVisibility(View.GONE);
								} else {
									mTvZanCount.setVisibility(View.VISIBLE);
									mTvZanCount.setText(zanCount);
								}

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
	 * UI界面资源初始化
	 */
	@SuppressLint("SimpleDateFormat")
	private void intialSource() {

		this.tv_per_msg_title = (TextView) findViewById(R.id.tv_actionbartitle);
		tv_per_msg_title.setText("消息");
		mTvZanCount = (TextView) findViewById(R.id.tv_business_zan_count);
		mTvCommentCount = (TextView) findViewById(R.id.tv_business_msg_count);
		mTvZanTime = (TextView) findViewById(R.id.tv_psg_zan_time);
		mTvCommentTime = (TextView) findViewById(R.id.tv_psg_comment_time);

		mRlComment = (RelativeLayout) findViewById(R.id.rl_person_msg_comment);
		mRlZan = (RelativeLayout) findViewById(R.id.rl_person_msg_zan);

		mRlZan.setOnClickListener(this);
		mRlComment.setOnClickListener(this);

		this.ll_tv_actionbarback = (LinearLayout) findViewById(R.id.ll_tv_actionbarback);
		ll_tv_actionbarback.setOnClickListener(this);

		// 默认提示日期 为当天日期
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		mTvCommentTime.setText(format.format(new Date()));
		mTvZanTime.setText(format.format(new Date()));

	}

	/**
	 * UI界面动作事件
	 */
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {

		case R.id.ll_tv_actionbarback:
			PersonMsgActivity.this.finish();
			break;

		case R.id.rl_person_msg_comment: // 评论
			Intent intent = new Intent(PersonMsgActivity.this,
					PersonPingLunActivity.class);
			baseStartActivity(intent);
			break;
		case R.id.rl_person_msg_zan: // 点赞
			Intent intent1 = new Intent(PersonMsgActivity.this,
					PersonZanActivity.class);
			baseStartActivity(intent1);
			break;

		default:
			break;
		}
	}

	@Override
	protected void resetLayout() {

		RelativeLayout ll_mypermsg = (RelativeLayout) findViewById(R.id.ll_permsg_root);
		SupportDisplay.resetAllChildViewParam(ll_mypermsg);
	}
	
	@Override
	protected void onResume() {
		getPerMsg();
		super.onResume();
	}

}
