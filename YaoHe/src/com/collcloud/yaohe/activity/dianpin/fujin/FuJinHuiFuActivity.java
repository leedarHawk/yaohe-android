package com.collcloud.yaohe.activity.dianpin.fujin;

import android.os.Bundle;
import android.widget.RelativeLayout;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;

/**
 * 【附近】点评详细-回复页面
 * 
 * @ClassName FuJinHuiFuActivity
 * @Description 回复点评
 * @author CollCloud_小米
 */
public class FuJinHuiFuActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fujin_huifu);
	}

	@Override
	protected void resetLayout() {
		// TODO Auto-generated method stub
		RelativeLayout rlLayout = (RelativeLayout) this
				.findViewById(R.id.rl_activity_fujin_new_huifu_root);
		SupportDisplay.resetAllChildViewParam(rlLayout);

	}

}
