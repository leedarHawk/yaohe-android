package com.collcloud.yaohe.activity.details.fujinshop;

import java.util.ArrayList;

import android.os.Bundle;

import com.collcloud.yaohe.api.info.DetailsBusinessShopInfo.BusinessCallInfo;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.ui.view.SingleLayoutListView;

/**
 * 商家发布的吆喝列表信息
 * 
 * @ClassName DetailsCallActivity
 * @Description
 * @author CollCloud_小米
 */
public class DetailsCallActivity extends BaseActivity {
	private SingleLayoutListView mLvCall;
	private ArrayList<BusinessCallInfo> mListCalls;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getIntent().getSerializableExtra(
				IntentKeyNames.KEY_BUSINES_CALL_DETAILS) != null) {
			mListCalls = (ArrayList<BusinessCallInfo>) getIntent()
					.getSerializableExtra(
							IntentKeyNames.KEY_BUSINES_CALL_DETAILS);
		}
	}

	@Override
	protected void resetLayout() {
		// TODO Auto-generated method stub

	}

}
