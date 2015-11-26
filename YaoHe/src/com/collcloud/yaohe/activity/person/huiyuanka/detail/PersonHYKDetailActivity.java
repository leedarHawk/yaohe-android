package com.collcloud.yaohe.activity.person.huiyuanka.detail;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.person.huiyuanka.PersonHYKActivity;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.entity.HykUseTimeList;
import com.collcloud.yaohe.ui.adapter.HYKDetailUseTimeAdapter;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * @类说明 我的(个人)会员卡详情页面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月12日 下午5:02:18
 */
public class PersonHYKDetailActivity extends BaseActivity implements OnClickListener{
	
	private static final String TAG = "HYKDETAIL";
	/** 返回按钮 */
	ImageView iv_top_menu_back;
	/** 标题信息 */
	TextView tv_commonn_title_menu_text;
	/** 分享按钮 */
	ImageView iv_top_title_menu_share;
	/** 消费按钮 */
	ImageView iv_top_title_menu_shoucang;
	/** 数据加载进度条 */
	private Dialog mDialog;
	/** 使用时间数据集合 */
	List<HykUseTimeList> hykusetimeList;
	/** 底部使用次数listview */
	private ListView lv_hykusetime_list;
	/**会员卡标题 */
	private TextView tv_hyk_detail_world;
	/**会员卡串码 */
	private TextView tv_hyk_yhm_num;
	/** 获取的会员卡标题 */
	private String title;// 会员卡标题
	/** 获取的会员卡内容 */
	private String card_number;// 会员卡串码

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_hykdetail);
		
		intialSource();
		
		progressbar(PersonHYKDetailActivity.this, R.layout.loading_progress);

		Bundle bundle = this.getIntent().getExtras();  
		String selectedhykid=bundle.getString("selectedhykid");
		String selectedsourid=bundle.getString("selectedsourid");	
		
		accessNetGetHykDetail(selectedhykid,selectedsourid);
	}
	
	/**
	 * 获取不可使用的会员卡数据
	 */
	private void accessNetGetHykDetail(String card_id,String member_card_id){
		
		HttpUtils http=new HttpUtils();
		
		//用来封装参数
		RequestParams params=new RequestParams();
		
		//选中会员卡ID
		CCLog.v(TAG, "选中会员卡ID>>>>>"+card_id);
		params.addBodyParameter("card_id",card_id);
		//选中会员卡对应商家ID
		CCLog.v(TAG, "该会员卡的发放者ID>>>>>"+member_card_id);
		params.addBodyParameter("member_card_id",member_card_id);

		http.send(HttpRequest.HttpMethod.GET,ContantsValues.MYCARDDETAIL,params, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException arg0, String arg1) {

				mDialog.dismiss();
				showToast("网络访问失败,检查网络设置");
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				mDialog.dismiss();
				CCLog.v(TAG, "网络获取会员卡数据成功");		
				CCLog.v(TAG, arg0.result);
				JSONObject object,object2;
				
				//网络返回会员卡结果状态信息
				String responseInfo="";	
				//网络返回data数据
				String responseData="";
							
				//网络访问会员卡状态码
				String code="";
				//网络会员卡返回消息
				String responseMsg="";
				try {
					
					object=new JSONObject(arg0.result);
					responseInfo=object.getString("status");
					object2=new JSONObject(responseInfo);
					
					code=object2.getString("code");
					responseMsg=object2.getString("message");
					
					if(code.equals("0")){
						
						String responseCoupon=null;
						responseData=object.getString("data");//包括："card": "list":
						JSONObject object3,object4;
						
						object3=new JSONObject(responseData);
						responseCoupon=object3.getString("card");
						object4=new JSONObject(responseCoupon);						
						
						title=object4.optString("title");
						tv_hyk_detail_world.setText(title);
						
						card_number=object4.optString("card_number");
						tv_hyk_yhm_num.setText(card_number);
						
						// 数据获取成功
						if (code.equals("0")) {

							JSONArray jsoArray = object3
									.optJSONArray("list");
							final int itemCount = jsoArray.length();

							if (itemCount > 0) {
								/** 关注商家数据集合 */
								ArrayList<HykUseTimeList> hykdetailList = new ArrayList<HykUseTimeList>();

								for (int i = 0; i < itemCount; i++) {

									HykUseTimeList hykdetaiItem = new HykUseTimeList();

									JSONObject scObject = jsoArray
											.getJSONObject(i);

									hykdetaiItem.id = scObject.optString("id");
									hykdetaiItem.ply_time=scObject.optString("ply_time");
									
									hykdetailList.add(hykdetaiItem);
								}
							
								//底部使用次数适配器
								lv_hykusetime_list.setAdapter(new HYKDetailUseTimeAdapter(
										PersonHYKDetailActivity.this, hykdetailList));
											
							} else {

								showToast("该会员卡没有使用记录");
							}

						}
						
					}else{
						showToast(responseMsg);
					}	
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * 页面资源初始化
	 */
	private void intialSource() {
		// 返回
		this.iv_top_menu_back = (ImageView) findViewById(R.id.iv_top_menu_back);
		iv_top_menu_back.setOnClickListener(this);
		// 标题
		this.tv_commonn_title_menu_text = (TextView) findViewById(R.id.tv_commonn_title_menu_text);
		tv_commonn_title_menu_text.setText("会员卡详情");
		// 分享
		this.iv_top_title_menu_share = (ImageView) findViewById(R.id.iv_top_title_menu_share);
		iv_top_title_menu_share.setVisibility(View.INVISIBLE);
		// 消费写陈了shoucang
		this.iv_top_title_menu_shoucang = (ImageView) findViewById(R.id.iv_top_title_menu_shoucang);
		iv_top_title_menu_shoucang.setOnClickListener(this);

		this.lv_hykusetime_list = (ListView) findViewById(R.id.lv_hykusetime_list);
		
		this.tv_hyk_detail_world = (TextView) findViewById(R.id.tv_hyk_detail_world);
		
		this.tv_hyk_yhm_num = (TextView) findViewById(R.id.tv_hyk_yhm_num);
		
		
	}

	/**
	 * UI界面动作事件
	 */
	@Override
	public void onClick(View arg0) {
		
		switch (arg0.getId()) {
		
		case R.id.iv_top_menu_back:
			
			finish();
			break;

		case R.id.iv_top_title_menu_share:
			break;

		case R.id.iv_top_title_menu_shoucang:
			break;

		default:
			break;
		}
	}
	
	/**
	 * 数据加载
	 * */
	private void progressbar(Context context, int layout) {
		mDialog = new AlertDialog.Builder(this).create();
		// mDialog.setOnKeyListener(keyListener);
		mDialog.show();
		// 注意此处要放在show之后 否则会报异常
		mDialog.setContentView(layout);
	}
	
	/**
	 *  页面适配
	 */
	@Override
	protected void resetLayout() {
		
		RelativeLayout rel_myhyk_detail = (RelativeLayout) findViewById(R.id.rl_hykdetail_root);
		SupportDisplay.resetAllChildViewParam(rel_myhyk_detail);
	}

}
