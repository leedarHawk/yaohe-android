package com.collcloud.yaohe.activity.dianpin.fujin;

import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.api.ApiAccess;
import com.collcloud.yaohe.api.ApiAccessErrorManager;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.utils.UIHelper;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * 添加新评论
 * 
 * @ClassName AddNewDianpinActivity
 * @Description
 * @author CollCloud_小米
 */
public class AddNewDianpinActivity extends BaseActivity implements
		OnClickListener, android.widget.CompoundButton.OnCheckedChangeListener {
	/**
	 * 是否匿名选择
	 */
	private CheckBox mCbAnonymous = null;
	private TextView mTvAnonymous = null;
	/**
	 * 发布按钮
	 */
	private Button mBtnCommit = null;
	/**
	 * 编辑内容
	 */
	private EditText mEtComment = null;
	/**
	 * 评分控件
	 */
	private RatingBar mRatingBar;
	/**
	 * 商铺ID
	 */
	private String mStrShopID = null;
	/**
	 * 吆喝ID
	 */
	private String mStrCallID = null;
	private String mStrStar = "2";
	/**
	 * 点评内容
	 */
	private String mStrContent = null;
	/**
	 * 是否匿名 （0 、 1 ）
	 */
	private String mStrIsAnonymous;
	private boolean mIsAnonymous = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fujin_add_dianping);
		mStrShopID = getStringExtra(IntentKeyNames.KEY_NEAR_COMMENT_ID);
	}

	@Override
	protected void resetLayout() {
		RelativeLayout rlLayout = (RelativeLayout) this
				.findViewById(R.id.rl_activity_fujin_new_dianping_root);
		SupportDisplay.resetAllChildViewParam(rlLayout);

		mCbAnonymous = (CheckBox) findViewById(R.id.cb_fujin_dianping_niming_tag);
		mEtComment = (EditText) findViewById(R.id.et_near_add_pinglun_content);
		mTvAnonymous = (TextView) findViewById(R.id.tv_dianping_niming_text);
		mBtnCommit = (Button) findViewById(R.id.btn_near_dianpin_fabu);
		mRatingBar = (RatingBar) findViewById(R.id.ratingBar_near_new_pinlun);
		mTvAnonymous.setOnClickListener(this);
		mBtnCommit.setOnClickListener(this);
		mCbAnonymous.setOnCheckedChangeListener(this);
		mRatingBar.setOnRatingBarChangeListener(new RatingBarChangeListener());

		mBtnIsCancelButton = true;
		initTopOnlyBackTitle();
		setTopOnlyBackTitle("添加点评");

	}

	@Override
	protected void onCancelButtonListener() {
		super.onCancelButtonListener();
		this.finish();
	}

	class RatingBarChangeListener implements OnRatingBarChangeListener {
		@Override
		public void onRatingChanged(RatingBar ratingBar, float rating,
				boolean fromUser) {
			mStrStar = String.valueOf(Math.round(rating));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_near_dianpin_fabu:
			mStrIsAnonymous = Utils.getCheckResult(mIsAnonymous);
			mStrContent = Utils.strFromView(mEtComment);
			// 店铺点评API调用
			shopCommentApi(mLoginDataManager.getMemberId(), mStrShopID,
					mStrStar, mStrContent, mStrIsAnonymous, "0", "0",
					ContantsValues.SHOP_COMMENT_URL, "点评成功。");
			break;

		case R.id.tv_dianping_niming_text:
			if (mIsAnonymous) {
				mCbAnonymous.setChecked(false);
			} else {
				mCbAnonymous.setChecked(true);
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		mIsAnonymous = isChecked;
	}

	/**
	 * 商铺点评API 调用
	 * 
	 * @Title nearShopCommentApi
	 * @Description
	 * @param memberID
	 *            会员ID(post提交)
	 * @param callID
	 *            吆喝ID(post提交)
	 * @param star
	 *            几颗星
	 * @param content
	 *            内容
	 * @param isAnonymous
	 *            是否匿名 0不匿名1匿名
	 * @param url
	 *            post请求的url地址
	 * @param message
	 *            点评成功后的提示信息
	 */
	private void nearShopCommentApi(String memberID, String callID,
			String star, String content, String isAnonymous, String url,
			final String message) {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", memberID);
		params.addBodyParameter("call_id", callID);
		params.addBodyParameter("star", star);
		params.addBodyParameter("content", content);
		params.addBodyParameter("is_anonymous", isAnonymous);

		http.send(HttpRequest.HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!Utils.isStringEmpty(responseInfo.result)) {
							if (responseInfo.result.contains("status")) {
								try {
									// 数据处理
									JSONObject errorJsonObject = new JSONObject(
											responseInfo.result);
									if (errorJsonObject.has("status")) {
										JSONObject statusJsonObject = errorJsonObject
												.optJSONObject("status");
										int code = statusJsonObject
												.optInt("code");
										if (code == 1) {
											String strErrorMsg = statusJsonObject
													.getString("message");
											UIHelper.ToastMessage(
													AddNewDianpinActivity.this,
													strErrorMsg);
										} else {
											ApiAccess.showProgressDialog(
													AddNewDianpinActivity.this,
													"提交中..");

											new Handler().postDelayed(
													new Runnable() {
														@Override
														public void run() {
															if (message != null) {
																UIHelper.ToastMessage(
																		AddNewDianpinActivity.this,
																		message);
															}
															ApiAccess
																	.dismissProgressDialog();
															finish();
														}
													}, 1000);
										}
									}
								} catch (Exception e) {
									String errorMsg = ApiAccessErrorManager
											.getMessage(5,
													AddNewDianpinActivity.this);
									UIHelper.ToastMessage(
											AddNewDianpinActivity.this,
											errorMsg);
								}

							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						UIHelper.ToastMessage(mBaseActivity,
								R.string.response_data_invalid);
					}
				});
	}

}
