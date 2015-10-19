package com.collcloud.yaohe.activity.business.createservice;

import android.os.Bundle;
import android.view.Menu;
import android.widget.RelativeLayout;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;

/**
 * @类说明  我的(商家)创建服务页面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月12日 下午5:02:18
 */
public class BusinessCreateServiceActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_business_create_service);
	}
	
	


	/**
	 * 页面适配
	 */
	@Override
	protected void resetLayout() {

		RelativeLayout rel_cjfw = (RelativeLayout) findViewById(R.id.rl_create_servers_root);
		SupportDisplay.resetAllChildViewParam(rel_cjfw);
	}

}
