package com.collcloud.yaohe.activity.dianpin.fujin;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
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

/**
 * 点评页面（吆喝、商家）
 * 
 * @ClassName ShopCommentActivity
 * @Description
 * @author CollCloud_小米
 */
public class ShopCommentActivity extends BaseActivity implements
		OnClickListener, android.widget.CompoundButton.OnCheckedChangeListener {
	/**
	 * 是否匿名选择
	 */
	private CheckBox mCbAnonymous = null;
	private TextView mTvAnonymous = null;
	/**
	 * 评分组件layout
	 */
	private RelativeLayout mRlRatingLayout;
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

	private String mStrType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_comment_info);
		mStrShopID = getStringExtra(IntentKeyNames.KEY_SHOP_COMMENT_ID);
		mStrCallID = getStringExtra(IntentKeyNames.KEY_CALL_COMMENT_ID);
		mStrType = getStringExtra("callCommentType");

		if (!Utils.isStringEmpty(mStrCallID)) {
			mRlRatingLayout.setVisibility(View.GONE);
		} else {
			mRlRatingLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void resetLayout() {
		RelativeLayout rlLayout = (RelativeLayout) this
				.findViewById(R.id.rl_activity_shop_comment_root);
		SupportDisplay.resetAllChildViewParam(rlLayout);

		mCbAnonymous = (CheckBox) findViewById(R.id.cb_shop_dianping_niming_tag);
		mEtComment = (EditText) findViewById(R.id.et_add_pinglun_content);
		mTvAnonymous = (TextView) findViewById(R.id.tv_shop_dianping_niming_text);
		mBtnCommit = (Button) findViewById(R.id.btn_shop_dianpin_fabu);
		mRatingBar = (RatingBar) findViewById(R.id.ratingBar_new_pinlun);
		mRlRatingLayout = (RelativeLayout) findViewById(R.id.rl_comment_ratingbar_viewgroup);
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
		case R.id.btn_shop_dianpin_fabu:
			mStrIsAnonymous = Utils.getCheckResult(mIsAnonymous);
			mStrContent = Utils.strFromView(mEtComment);
			if (Utils.isStringEmpty(mStrContent)) {
				UIHelper.ToastMessage(ShopCommentActivity.this, "内容不可为空。");
			} else if (!Utils.isStringEmpty(mStrShopID)) {
				// 店铺点评API调用
				shopCommentApi(mLoginDataManager.getMemberId(), mStrShopID,
						mStrStar, mStrContent, mStrIsAnonymous, "0", "0",
						ContantsValues.SHOP_COMMENT_URL, "点评成功。");
			} else if (!Utils.isStringEmpty(mStrCallID)) {
				// 吆喝点评API调用
				yaoheComment(mLoginDataManager.getMemberId(), mStrCallID,
						mStrStar, mStrContent, mStrIsAnonymous, mStrType,
						ContantsValues.CALL_COMMENT_URL, "点评成功。");
			}
			break;

		case R.id.tv_shop_dianping_niming_text:
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

	private void yaoheComment(String memberID, String callID, String star,
			String content, String isAnonymous, String strType, String url,
			final String message) {

		ApiAccess.showProgressDialog(mBaseActivity, "提交中..");
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", memberID);
		params.addBodyParameter("call_id", callID);
		params.addBodyParameter("content", content);
		params.addBodyParameter("is_anonymous", isAnonymous);
		params.addBodyParameter("type", strType);
		params.addBodyParameter("parentid", "");
		CCLog.i("吆喝点评参数：", "member_id= " + memberID + " call_id=" + callID
				+ " type=" + strType + " content=" + content + " is_anonymous="
				+ isAnonymous);

		http.send(HttpRequest.HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						ApiAccess.dismissProgressDialog();
						if (!Utils.isStringEmpty(responseInfo.result)) {
							if (responseInfo.result.contains("status")) {
								if (responseInfo.result != null) {
									CCLog.i("吆喝点评信息：", " "
											+ responseInfo.result);
								}
								try {
									// 数据处理
									JSONObject errorJsonObject = new JSONObject(
											responseInfo.result);
									if (errorJsonObject.has("status")) {
										JSONObject statusObject = errorJsonObject
												.optJSONObject("status");
										int code = statusObject.optInt("code");
										if (code == 1) {
											String strErrorMsg = errorJsonObject
													.getString("message");
											UIHelper.ToastMessage(
													mBaseActivity, strErrorMsg);
										} else {

											UIHelper.ToastMessage(
													ShopCommentActivity.this,
													message);
											cancelAction();
										}
									}
								} catch (Exception e) {
									String errorMsg = ApiAccessErrorManager
											.getMessage(5, mBaseActivity);
									UIHelper.ToastMessage(mBaseActivity,
											errorMsg);
								}

							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						ApiAccess.dismissProgressDialog();
						UIHelper.ToastMessage(mBaseActivity,
								R.string.response_data_invalid);
					}
				});
	}

	private void cancelAction() {
		Intent intent = new Intent();
		if (!Utils.isStringEmpty(mStrCallID)) {
			Bundle bundle = new Bundle();
			intent.putExtra("yaoheCallID", mStrCallID);
			intent.putExtras(bundle);
			setResult(11, intent);

		} else if (!Utils.isStringEmpty(mStrShopID)) {
			Bundle bundle = new Bundle();
			intent.putExtra("BusinessShopID", mStrShopID);
			intent.putExtras(bundle);
			setResult(22, intent);
		}
		ShopCommentActivity.this.finish();

	}
}