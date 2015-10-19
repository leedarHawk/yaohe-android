package com.collcloud.yaohe.activity.person;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.api.ApiAccess;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.GlobalConstant;
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

public class UpdataSexActivity extends BaseActivity implements OnClickListener {

	private ImageView mIvMan;
	private ImageView mIvWoman;
	private String mStrSexType;
	private int sexType; //0男1女

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_updata_sex_info);
		mStrSexType = getStringExtra(IntentKeyNames.KEY_NICKNAME);
		setSexInfo(mStrSexType);
	}

	@Override
	protected void resetLayout() {
		LinearLayout layout = (LinearLayout) findViewById(R.id.ll_updata_sex_info);
		SupportDisplay.resetAllChildViewParam(layout);
		RelativeLayout rlManLayout = (RelativeLayout) findViewById(R.id.rl_updata_sex_man);
		RelativeLayout rlWomanLayout = (RelativeLayout) findViewById(R.id.rl_updata_sex_women);
		rlManLayout.setOnClickListener(this);
		rlWomanLayout.setOnClickListener(this);
		mIvMan = (ImageView) findViewById(R.id.iv_updata_sex_man);
		mIvWoman = (ImageView) findViewById(R.id.iv_updata_sex_women);
		initTopOnlyBackTitle();
		setTopOnlyBackTitle("修改性别");
		mBtnIsCancelButton = true;
		mTopCancel = true;

	}

	private void setSexInfo(String sex) {
		if (!Utils.isStringEmpty(sex)) {
			if (sex.equals(GlobalConstant.VALID_VALUE)) {
				sexType = 1;
				mIvMan.setBackgroundResource(R.color.white);
				mIvWoman.setBackgroundResource(R.drawable.icon_checked_on);
			} else if (sex.equals(GlobalConstant.INVALID_VALUE)) {
				sexType = 0;
				mIvMan.setBackgroundResource(R.drawable.icon_checked_on);
				mIvWoman.setBackgroundResource(R.color.white);
			}
		} else {
			sexType = 0;
			mIvMan.setBackgroundResource(R.drawable.icon_checked_on);
			mIvWoman.setBackgroundResource(R.color.white);
		}
	}

	private String getSex() {
		String sex = "1";
		if (sexType == 1) {
			sex = "1";
		} else if (sexType == 0) {
			sex = "0";
		}
		return sex;
	}

	private void updataSex() {
		ApiAccess.showProgressDialog(UpdataSexActivity.this, "修改性别中...",
				R.style.progress_dialog);
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());
		params.addBodyParameter("sex", getSex());
		CCLog.e("member_id=" + mLoginDataManager.getMemberId() + "  sex= "
				+ getSex());

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.UPPSEX, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						ApiAccess.dismissProgressDialog();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {

						ApiAccess.dismissProgressDialog();

						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(responseInfo.result);
							if (responseInfo.result != null) {
								CCLog.i("修改性别状态：", responseInfo.result);
							}
							if (jsonObject.has("status")) {
								JSONObject statusObject = jsonObject
										.optJSONObject("status");
								int code = statusObject.optInt("code");
								if (code == 1) {
									String strErrorMsg = statusObject
											.optString("message");
									UIHelper.ToastMessage(
											UpdataSexActivity.this, strErrorMsg);
								} else {
									UIHelper.ToastMessage(
											UpdataSexActivity.this, "性别修改成功。");
									Intent intent = new Intent(
											UpdataSexActivity.this,
											PersonActivity.class);
									baseStartActivity(intent);
									UpdataSexActivity.this.finish();
								}
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				});

	}

	@Override
	public void onClick(View v) {

		if (Utils.isFastDoubleClick()) {
			return;
		}
		switch (v.getId()) {
		case R.id.rl_updata_sex_man:
			sexType = 0;
			mIvMan.setBackgroundResource(R.drawable.icon_checked_on);
			mIvWoman.setBackgroundResource(R.color.white);
			break;
		case R.id.rl_updata_sex_women:
			sexType = 1;
			mIvMan.setBackgroundResource(R.color.white);
			mIvWoman.setBackgroundResource(R.drawable.icon_checked_on);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onCancelButtonListener() {
		super.onCancelButtonListener();
		updataSex();
	}

	@Override
	protected void topCancelDefined() {
		super.topCancelDefined();
		updataSex();
	}
}
