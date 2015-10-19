package com.collcloud.yaohe.activity.dianpin.fujin;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.adapter.FuJinPinLunAdapter;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.ui.view.SingleLayoutListView;

/**
 * 附近推荐的商圈-消息评价详情页面
 * 
 * @ClassName FuJinXiaoXiActivity
 * @Description 发消息聊天
 */
public class FuJinXiaoXiActivity extends BaseActivity implements
		OnClickListener {

	/** 标题返回 */
	private LinearLayout mLlBack;
	/** 标题文字 */
	private TextView mTvTitle;

	/** 评论列表 */
	private SingleLayoutListView mLvPinLun;
	/** 评论内容适配器 */
	private FuJinPinLunAdapter mPinLunAdapter = null;/**
	 * 数据项为空时，显示提示文字
	 */
	private LinearLayout mLlMessage;
	private LinearLayout mLlEmpty;
	private TextView mTvTips;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fujin_dianping);
		init();
	}

	private void init() {
		mTvTitle.setText("信息");
		mTvTips.setText("该商家已返回母星\n\n暂时失联中...");
	}

	@Override
	protected void resetLayout() {
		RelativeLayout rlLayout = (RelativeLayout) this
				.findViewById(R.id.rl_activity_details_fujin_dianping_root);
		SupportDisplay.resetAllChildViewParam(rlLayout);
		mLlBack = (LinearLayout) this
				.findViewById(R.id.ll_commonn_text_title_back);
		mTvTitle = (TextView) this
				.findViewById(R.id.tv_commonn_back_title_text);
		mLlBack.setOnClickListener(this);
		mLvPinLun = (SingleLayoutListView) this
				.findViewById(R.id.lv_fujin_dianping_content);

		mPinLunAdapter = new FuJinPinLunAdapter(FuJinXiaoXiActivity.this);
		mLvPinLun.setAdapter(mPinLunAdapter);
		Utils.resetListViewHeightBasedOnChildren(mLvPinLun);
		
		mLlMessage = (LinearLayout) findViewById(R.id.ll_nearby_fragment_xiaoxi);
		mLlEmpty = (LinearLayout) findViewById(R.id.ll_nearby_fragment_empty);
		mTvTips = (TextView) findViewById(R.id.tv_yaohe_no_data_text);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_commonn_text_title_back:
			this.finish();
			break;

		default:
			break;
		}

	}

}
