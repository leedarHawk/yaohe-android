package com.collcloud.yaohe.activity.person.shoucang.person;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.my.MineActivity;
import com.collcloud.yaohe.activity.person.shoucang.PersonShouCangActivity;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * @类说明  我的收藏(收藏的个人)
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月2日 下午3:02:18
 */
public class PersonSCPersonActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "ShouCangPerson";
	/**标题信息*/
	private TextView tv_actionbartitle;
	/**页面返回*/
	private ImageView tv_actionbarback;
	/**个人*/
	private TextView tv_sc_person_mu;
	/**商家*/
	private TextView tv_sc_bus_mu_;
	/**进度条*/
	private Dialog scPer_Dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_scperson);
		intialSource();
		//绑定适配器 ShouCangPersonAdapter
	}
	
	/**
	 * 访问网络获取收藏的个人
	 */
	private void accessNetGetBusinessData(){
		
		HttpUtils http=new HttpUtils();
		RequestParams params = new RequestParams();
		
		//封装post请求参数
/*		params.addBodyParameter("login_user",userName);
		params.addBodyParameter("login_pass",userPassword);
		params.addBodyParameter("code",vCode);*/
		
		http.send(HttpRequest.HttpMethod.POST,"url",params,new RequestCallBack<String>() {
			//网络返回字符串
			String responseInfo=null;
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				CCLog.v(TAG, "获取收藏的商家时网络出错了");
				scPer_Dialog.dismiss();
                showToast("数据加载失败了");
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				CCLog.v(TAG, "获取收藏的商家成功");
				CCLog.v(TAG, arg0.result);
				JSONObject object,object2;
				String code=null;
				String responseMsg=null;
				try {
					object=new JSONObject(arg0.result);
					//获取一个返回的字符串
					responseInfo=object.getString("status");
					object2=new JSONObject(responseInfo);
					
					code=object2.getString("code");
					responseMsg=object2.getString("message");
					
				} catch (JSONException e) {
					CCLog.v(TAG,e.toString());
					e.printStackTrace();
				}
				//数据获取陈宫
				if(code.equals("0")){
					scPer_Dialog.dismiss();
					

				}else{//失败了
					showToast(responseMsg);
					scPer_Dialog.dismiss();
				}			
			}
		});
	}

	/**
	 * UI界面资源初始化
	 */
	private void intialSource(){
		//其他
		this.tv_actionbarback=(ImageView) findViewById(R.id.tv_actionbarback);
		
		tv_actionbarback.setOnClickListener(this);
		
		this.tv_actionbartitle=(TextView) findViewById(R.id.tv_actionbartitle);
		
		tv_actionbartitle.setText("我的收藏");
		
		//this.xlistview_sc=(XListView) findViewById(R.id.xlv_sc_business_);
		
		this.tv_sc_person_mu=(TextView) findViewById(R.id.tv_sc_person_mu);
		
		tv_sc_person_mu.setOnClickListener(this);
		
		this.tv_sc_bus_mu_=(TextView) findViewById(R.id.tv_sc_bus_mu_);
		
		tv_sc_bus_mu_.setOnClickListener(this);		
	}
	
	/**
	 * UI界面动作事件
	 */
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {
		
		case R.id.tv_actionbarback:
			CCLog.v(TAG,"点击了");
			startActivity(new Intent(PersonSCPersonActivity.this,MineActivity.class));
			finish();
			break;
		case R.id.tv_sc_person_mu:
			CCLog.v(TAG,"点击了PersonSCPersonActivity");
			//startActivity(new Intent(PersonSCPersonActivity.this,PersonSCPersonActivity.class));
			break;
		case R.id.tv_sc_bus_mu_:
			CCLog.v(TAG,"点击了PersonShouCangActivity");
			startActivity(new Intent(PersonSCPersonActivity.this,PersonShouCangActivity.class));
			break;
		default:
			break;
		}
	}
	
	/**
	 * UI界面适配
	 */
	@Override
	protected void resetLayout() {

		RelativeLayout rel_persc_=(RelativeLayout) findViewById(R.id.rl_per_sc_per_root);
		SupportDisplay.resetAllChildViewParam(rel_persc_);
	}

}
