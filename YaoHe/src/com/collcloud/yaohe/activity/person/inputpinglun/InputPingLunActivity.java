package com.collcloud.yaohe.activity.person.inputpinglun;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.person.shopdianping.PerShopDianPingActivity;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;

/**
 * @类说明  我的(个人输入评论)优惠页面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月12日 下午5:02:18
 */
public class InputPingLunActivity extends BaseActivity implements OnClickListener{
	/**文本编辑框*/
	private EditText edt_inputpinglun;
	/**返回*/
	private TextView tv_back;
	/**页面标题*/
	private TextView tv_title;
	/**发送*/
	private TextView tv_send;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input_ping_lun);
		
		intialSource();
		showSoftKeyboard();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.input_ping_lun, menu);
		return true;
	}
	
	//初始化资源
	private void intialSource(){
		edt_inputpinglun=(EditText) findViewById(R.id.edt_inputpinglun);
		tv_back=(TextView) findViewById(R.id.tv_back);
		tv_back.setOnClickListener(this);
		tv_title=(TextView) findViewById(R.id.tv_title);
		tv_title.setText("回复评论");
		tv_send=(TextView) findViewById(R.id.tv_do);
		tv_send.setText("发送");
		tv_send.setOnClickListener(this);
	}
	//弹屏出现软键盘
	private void showSoftKeyboard(){
		edt_inputpinglun.setFocusable(true);  
		edt_inputpinglun.setFocusableInTouchMode(true);  
		edt_inputpinglun.requestFocus();  
		InputMethodManager inputManager =(InputMethodManager)edt_inputpinglun.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
		inputManager.showSoftInput(edt_inputpinglun, 0); 
	}

	@Override
	public void onClick(View arg0) {
		// 这儿应当做判断，具体跳转到那个界面，所有书写评论的地方都可以调用这一个界面
		switch (arg0.getId()) {
		case R.id.tv_back:
			startActivity(new Intent(InputPingLunActivity.this,PerShopDianPingActivity.class));
			finish();
			break;
		case R.id.tv_do:
			//根据来源不同保存到不同的地方
			startActivity(new Intent(InputPingLunActivity.this,PerShopDianPingActivity.class));
			finish();
			break;
		default:
			break;
		}
	}
	
	/**
	 *  页面适配
	 */
	@Override
	protected void resetLayout() {
		// TODO Auto-generated method stub  
		RelativeLayout rel_mysrpl= (RelativeLayout) findViewById(R.id.rl_srpl_root);
		SupportDisplay.resetAllChildViewParam(rel_mysrpl);
	}

}
