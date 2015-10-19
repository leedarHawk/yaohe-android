package com.collcloud.yaohe.activity.person.shopdianping;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.person.message.PersonMsgActivity;

public class PerShopDianPingActivity extends Activity implements OnClickListener{
	
	/**页面标题*/
	private TextView tv_actionbartitle;
	/**页面返回按钮*/
	private ImageView tv_actionbarback;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_per_shop_dian_ping);
		intialSource();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.per_shop_dian_ping, menu);
		return true;
	}
	
	private void intialSource(){
		//其他
		this.tv_actionbarback=(ImageView) findViewById(R.id.tv_actionbarback);
		tv_actionbarback.setOnClickListener(this);
		this.tv_actionbartitle=(TextView) findViewById(R.id.tv_actionbartitle);
		tv_actionbartitle.setText("评论");	
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.tv_actionbarback:
			startActivity(new Intent(PerShopDianPingActivity.this,PersonMsgActivity.class));
			finish();
			break;
		default:
			break;
	 }
		
	}
}
