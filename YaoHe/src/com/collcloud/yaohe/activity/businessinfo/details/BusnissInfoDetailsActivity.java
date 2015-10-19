package com.collcloud.yaohe.activity.businessinfo.details;

import android.os.Bundle;
import android.view.Menu;
import android.widget.RelativeLayout;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;

/**
 * @类说明  我的(商家详细信息)页面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月12日 下午5:02:18
 */
public class BusnissInfoDetailsActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_busniss_info_details);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.busniss_info_details, menu);
		return true;
	}

	@Override
	protected void resetLayout() {
		// TODO Auto-generated method stub
		RelativeLayout rel_mysjxxxx = (RelativeLayout) findViewById(R.id.rl_businfo_detail_root);
		SupportDisplay.resetAllChildViewParam(rel_mysjxxxx);
	}

}
