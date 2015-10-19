package com.collcloud.yaohe.activity.business.service;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.business.BusinessActivity;
import com.collcloud.yaohe.activity.business.fahuiyuanka.BusinessFHYKActivity;
import com.collcloud.yaohe.activity.business.fahuodong.BusinessFHDActivity;
import com.collcloud.yaohe.activity.business.faquan.BusinessFQActivity;
import com.collcloud.yaohe.activity.business.faxinpin.BusinessFXPActivity;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;

/**
 * @类说明 四大服务界面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月12日 下午3:02:18
 */
public class BusinessServiceActivity extends BaseActivity implements
		OnClickListener {

	/** 返回按钮 */
	private LinearLayout ll_tv_actionbarback;
	/** 标题信息 */
	private TextView tv_actionbartitle;
	/** 发券 */
	private RelativeLayout rl_fw_fq;
	/** 发会员卡 */
	private RelativeLayout rl_fw_fhyk;
	/** 发新品 */
	private RelativeLayout rl_fw_xp;
	/** 发活动 */
	private RelativeLayout rl_fw_fhd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_business_service);

		intialSource();
	}

	/**
	 * UI资源初始化
	 */
	private void intialSource() {
		this.ll_tv_actionbarback = (LinearLayout) findViewById(R.id.ll_tv_actionbarback);
		ll_tv_actionbarback.setOnClickListener(this);

		this.tv_actionbartitle = (TextView) findViewById(R.id.tv_actionbartitle);
		tv_actionbartitle.setText("服务");

		this.rl_fw_fq = (RelativeLayout) findViewById(R.id.rl_fw_fq);
		rl_fw_fq.setOnClickListener(this);

		this.rl_fw_fhyk = (RelativeLayout) findViewById(R.id.rl_fw_fhyk);
		rl_fw_fhyk.setOnClickListener(this);

		this.rl_fw_xp = (RelativeLayout) findViewById(R.id.rl_fw_xp);
		rl_fw_xp.setOnClickListener(this);

		this.rl_fw_fhd = (RelativeLayout) findViewById(R.id.rl_fw_fhd);
		rl_fw_fhd.setOnClickListener(this);
	}

	/**
	 * 页面适配
	 */
	@Override
	protected void resetLayout() {
		RelativeLayout rel_fw = (RelativeLayout) findViewById(R.id.rl_fw_root);
		SupportDisplay.resetAllChildViewParam(rel_fw);
	}

	/**
	 * UI界面组件点击事件
	 */
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		case R.id.ll_tv_actionbarback:
			baseStartActivity(new Intent(BusinessServiceActivity.this,
					BusinessActivity.class));
			finish();
			break;
		case R.id.rl_fw_fq:
			baseStartActivity(new Intent(BusinessServiceActivity.this,
					BusinessFQActivity.class));

			break;
		case R.id.rl_fw_fhyk:
			baseStartActivity(new Intent(BusinessServiceActivity.this,
					BusinessFHYKActivity.class));

			break;
		case R.id.rl_fw_xp:
			baseStartActivity(new Intent(BusinessServiceActivity.this,
					BusinessFXPActivity.class));

			break;
		case R.id.rl_fw_fhd:
			baseStartActivity(new Intent(BusinessServiceActivity.this,
					BusinessFHDActivity.class));

			break;
		default:
			break;
		}
	}

}
