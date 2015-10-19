package com.collcloud.yaohe.activity.jubao;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.api.ApiAccess;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.GlobalConstant;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.url.ContantsValues;

/**
 * 举报功能页面
 * 
 * @ClassName JuBaoActivity
 * @Description 举报功能，包含举报成功后的页面显示
 * @author CollCloud_小米
 */
public class JuBaoActivity extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener {

	/**
	 * 返回按钮
	 */
	private LinearLayout mLlBack;
	/**
	 * 文字标题
	 */
	private TextView mTvTitle;
	/**
	 * 提交组件
	 */
	private LinearLayout mLlSub;
	/**
	 * 要提交的举报类型（色情，谣言，政治，网站钓鱼等）
	 */
	private String mStrSubString;
	/**
	 * 补充说明的编辑组件
	 */
	private EditText mEtExtra;

	/**
	 * 举报选择，补充说明页
	 */
	private LinearLayout mLlJuBaoEdit;
	/**
	 * 举报成功后的页面
	 */
	private LinearLayout mLlJuBaoSuccess;
	/**
	 * 举报选择列表组
	 */
	private RadioGroup mRgJuBaoChoice;
	/**
	 * 要举报的店铺id
	 */
	private String mStrShopID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_jubao_edit);
		mStrShopID = getStringExtra(IntentKeyNames.KEY_ADD_REPORT_SHOP_ID);
	}

	@Override
	protected void resetLayout() {
		LinearLayout layout = (LinearLayout) this
				.findViewById(R.id.ll_activity_jubao_viewgroup);
		SupportDisplay.resetAllChildViewParam(layout);

		mLlJuBaoEdit = (LinearLayout) this
				.findViewById(R.id.ll_activity_jubao_con);
		mLlJuBaoSuccess = (LinearLayout) this
				.findViewById(R.id.ll_activity_jubao_success);

		mLlBack = (LinearLayout) this
				.findViewById(R.id.ll_commonn_sub_title_back);
		mTvTitle = (TextView) this.findViewById(R.id.tv_commonn_back_sub_title);
		mLlSub = (LinearLayout) this.findViewById(R.id.ll_commonn_sub_btn);
		mEtExtra = (EditText) this.findViewById(R.id.et_add_jubao_content);

		mRgJuBaoChoice = (RadioGroup) this
				.findViewById(R.id.rg_jubao_check_reason);
		mLlBack.setOnClickListener(this);
		mLlSub.setOnClickListener(this);
		mRgJuBaoChoice.setOnCheckedChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_commonn_sub_title_back:
			this.finish();
			break;
		case R.id.ll_commonn_sub_btn:
			subReport();
			break;

		default:
			break;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * 提交举报内容
	 * 
	 * @Title subReport
	 */
	private void subReport() {
		String extraStr = Utils.strFromView(mEtExtra);
		if (Utils.isStringEmpty(extraStr)) {
			extraStr = GlobalConstant.EMPTY_STRING;
		}
		ApiAccess.showProgressDialog(JuBaoActivity.this, "提交中...",
				R.style.progress_dialog);
		addReportApi(mLoginDataManager.getMemberId(), mStrShopID,
				mStrSubString, extraStr, ContantsValues.ADD_REPORT_URL);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				mLlJuBaoEdit.setVisibility(View.GONE);
				mLlJuBaoSuccess.setVisibility(View.VISIBLE);
				mTvTitle.setText("举报成功");
				mLlSub.setVisibility(View.GONE);
				ApiAccess.dismissProgressDialog();
			}
		}, 1000);

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		mStrSubString = GlobalConstant.VALID_VALUE;
		switch (checkedId) {
		case R.id.rbtn_jubao_check_reason_1:
			mStrSubString = "0";
			break;
		case R.id.rbtn_jubao_check_reason_2:
			mStrSubString = "1";
			break;
		case R.id.rbtn_jubao_check_reason_3:
			mStrSubString = "2";
			break;
		case R.id.rbtn_jubao_check_reason_4:
			mStrSubString = "3";
			break;
		case R.id.rbtn_jubao_check_reason_5:
			mStrSubString = "4";
			break;
		default:
			mStrSubString = GlobalConstant.VALID_VALUE;
			break;
		}

	}

}
