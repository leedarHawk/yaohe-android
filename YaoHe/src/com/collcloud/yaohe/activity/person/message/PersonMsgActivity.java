package com.collcloud.yaohe.activity.person.message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.collcloud.frame.xlistview.XListView;
import com.collcloud.frame.xlistview.XListView.IXListViewListener;
import com.collcloud.frame.xlistview.XListView.OnSlidingDirectionListen;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.business.myfans.BusinessMyFansActivity;
import com.collcloud.yaohe.activity.chat.ChattingActivity;
import com.collcloud.yaohe.activity.person.pinglun.PersonPingLunActivity;
import com.collcloud.yaohe.activity.person.zan.PersonZanActivity;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.api.info.DetailsCallCommentInfo;
import com.collcloud.yaohe.api.info.MsgInfo;
import com.collcloud.yaohe.api.info.MsgInfoBase;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.GlobalConstant;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.adapter.MsgAdapter;
import com.collcloud.yaohe.ui.adapter.MsgAdapter.MsgClickListener;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.GsonUtils;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * @类说明 我的(个人信息)页面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月12日 下午5:02:18
 */
public class PersonMsgActivity extends BaseActivity implements OnClickListener {

	/** 页面标题 */
	private TextView tv_per_msg_title;
	/** 页面返回按钮 */
	private LinearLayout ll_tv_actionbarback;

	private TextView mTvCommentCount;
	private TextView mTvZanCount;
	private TextView mTvCommentTime;
	private TextView mTvZanTime;
	private RelativeLayout mRlComment;
	private RelativeLayout mRlZan;
	
	private String tag = PersonMsgActivity.class.getSimpleName();
	
	/**
	 * 主页面显示内容的List
	 */
	private XListView mLvPullToRefreshView;
	private MsgAdapter mAdapter;
	private List<MsgInfo> msgInfoList = new ArrayList<MsgInfo>();
	private LayoutInflater mLayoutInflater = null;
	private View headerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_msg);
		mLayoutInflater = LayoutInflater.from(this);
		// 初始化资源
		intialSource();
	}

	private void getSmsMsg() {

		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();

		CCLog.v(GlobalConstant.TAG,
				"当前用户的ID>>>>>" + mLoginDataManager.getMemberId());
		
		
		String url = ContantsValues.PERSON_MY_SMS_MSG+"&member_id="+mLoginDataManager.getMemberId();
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId()
				.toString());
		
		CCLog.d(tag, "get sms msg url :"+url);

		http.send(HttpRequest.HttpMethod.POST, url,
				params, new RequestCallBack<String>() {
					// 网络返回字符串
					String responseData = null;
					@Override
					public void onSuccess(ResponseInfo<String> responseInfos) {
						CCLog.v(GlobalConstant.TAG, "获取个人消息数量成功");
						CCLog.v(GlobalConstant.TAG, responseInfos.result);
						JSONObject object, object2, object3;
						String code = null;
						String responseMsg = null;
						try {
							object = new JSONObject(responseInfos.result);
							// 获取一个返回的字符串
							String responseInfo = object.getString("status");

							object2 = new JSONObject(responseInfo);

							code = object2.getString("code");


							// 数据获取成功
							if (code.equals("0")) {
								// 获取一个返回的数据值
//								responseData = object.getString("data");
//								object3 = new JSONObject(responseData);
								
								MsgInfoBase msgInfoBase = GsonUtils.json2Bean(
										responseInfos.result,
										MsgInfoBase.class);
								
								if (msgInfoBase.data != null
										&& msgInfoBase.data.size() > 0) {
									msgInfoList.clear();									
									for (int j = 0; j < msgInfoBase.data
											.size(); j++) {
										MsgInfo msgInfo = new MsgInfo();
										if(msgInfoBase.data.get(j).face !=null && !"".equals(msgInfoBase.data.get(j).face)) {
											msgInfo.face =  URLs.IMG_PRE+msgInfoBase.data.get(j).face;
										}
										
										
										msgInfo.lastSendtime = msgInfoBase.data.get(j).lastSendtime;
										msgInfo.nickname = msgInfoBase.data.get(j).nickname;
										msgInfo.noReadCount = msgInfoBase.data.get(j).noReadCount;
										msgInfo.member_id = msgInfoBase.data.get(j).member_id;
										msgInfoList.add(msgInfo);
									}
								}
								if(msgInfoList != null && msgInfoList.size()>0) {
									setMsgInfo();
								} else {
									setMsgInfo();
								}


							} else {// 失败了
								showToast(object2.getString("message"));
								setMsgInfo();
							}
							onLoad();

						} catch (JSONException e) {
							e.printStackTrace();
							onLoad();
						}
					}
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						onLoad();
					}
				});

	}
	
	
	/**
	 * 获取sms发送的消息
	 */
	public void getPerMsg() {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();

		CCLog.v(GlobalConstant.TAG,
				"当前用户的ID>>>>>" + mLoginDataManager.getMemberId());
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId()
				.toString());

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.PERSON_MY_MSG,
				params, new RequestCallBack<String>() {
					// 网络返回字符串
					String responseInfo = null;
					String responseData = null;

					@Override
					public void onFailure(HttpException arg0, String arg1) {
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						CCLog.v(GlobalConstant.TAG, "获取个人消息数量成功");
						CCLog.v(GlobalConstant.TAG, arg0.result);
						JSONObject object, object2, object3;
						String code = null;
						String responseMsg = null;
						String commentCount = null;
						String zanCount = null;
						try {
							object = new JSONObject(arg0.result);
							// 获取一个返回的字符串
							responseInfo = object.getString("status");

							object2 = new JSONObject(responseInfo);

							code = object2.getString("code");
							responseMsg = object2.getString("message");

							// 数据获取成功
							if (code.equals("0")) {
								// 获取一个返回的数据值
								responseData = object.getString("data");
								object3 = new JSONObject(responseData);

								commentCount = object3.optString("commentnum");
								if (Utils.isStringEmpty(commentCount)
										|| commentCount.equals("0")) {
									mTvCommentCount.setVisibility(View.GONE);
								} else {
									mTvCommentCount.setVisibility(View.VISIBLE);
									mTvCommentCount.setText(commentCount);
								}
								zanCount = object3.getString("zannum");
								if (Utils.isStringEmpty(zanCount)
										|| zanCount.equals("0")) {
									mTvZanCount.setVisibility(View.GONE);
								} else {
									mTvZanCount.setVisibility(View.VISIBLE);
									mTvZanCount.setText(zanCount);
								}

							} else {// 失败了
								showToast(responseMsg);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				});
	}

	/**
	 * UI界面资源初始化
	 */
	@SuppressLint("SimpleDateFormat")
	private void intialSource() {

		//添加头部
		headerView = mLayoutInflater.inflate(R.layout.activity_person_msg_listview_header,
				null);
		
		
		
		this.tv_per_msg_title = (TextView) findViewById(R.id.tv_actionbartitle);
		tv_per_msg_title.setText("消息");
		mTvZanCount = (TextView) headerView.findViewById(R.id.tv_business_zan_count);
		mTvCommentCount = (TextView) headerView.findViewById(R.id.tv_business_msg_count);
		mTvZanTime = (TextView) headerView.findViewById(R.id.tv_psg_zan_time);
		mTvCommentTime = (TextView) headerView.findViewById(R.id.tv_psg_comment_time);

		mRlComment = (RelativeLayout) headerView.findViewById(R.id.rl_person_msg_comment);
		mRlZan = (RelativeLayout) headerView.findViewById(R.id.rl_person_msg_zan);

		mRlZan.setOnClickListener(this);
		mRlComment.setOnClickListener(this);

		this.ll_tv_actionbarback = (LinearLayout) findViewById(R.id.ll_tv_actionbarback);
		ll_tv_actionbarback.setOnClickListener(this);

		// 默认提示日期 为当天日期
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		mTvCommentTime.setText(format.format(new Date()));
		mTvZanTime.setText(format.format(new Date()));
		
		
		
		initListView();
		
		

	}
	
	/**
	 * 初始化listview
	 */
	private void initListView() {
		//LEE //////////////////////
		mLvPullToRefreshView = (XListView)this.findViewById(R.id.msgList);
		
		mLvPullToRefreshView.setPullLoadEnable(false);
		mLvPullToRefreshView.setPullRefreshEnable(true);
		mLvPullToRefreshView.forbiddenLoadMore();
		mLvPullToRefreshView.addHeaderView(headerView);
		
		mLvPullToRefreshView.setXListViewListener(new IXListViewListener() {
			
			@Override
			public void onRefresh() {
				refresh();
			}
			
			@Override
			public void onLoadMore() {
				mLvPullToRefreshView.forbiddenLoadMore();
				//Toast.makeText(PersonMsgActivity.this, "数据已加载完毕", Toast.LENGTH_SHORT).show();
			}
		});
		mLvPullToRefreshView.setOnSlidingDirectionListen(new OnSlidingDirectionListen() {
			
			@Override
			public void onScrollUpWard(float value) {
			}
			
			@Override
			public void onScrollTop() {
			}
			
			@Override
			public void onScrollDownWard(float value) {
			}
			
			@Override
			public void onScrollBottom() {
				
			}
		});
	}
	
	
	public void refresh() {
		this.getPerMsg();
		this.getSmsMsg();
	}
	
	private void onLoad() {
		
		mLvPullToRefreshView.stopRefresh();
		mLvPullToRefreshView.stopLoadMore();
		mLvPullToRefreshView.setRefreshTime("");
		mLvPullToRefreshView.forbiddenLoadMore();
	}
	
	
	/**
	 * 设定首页推荐显示内容
	 */
	public void setMsgInfo() {
		
		ApiAccess.dismissProgressDialog();
		if(mAdapter !=null) {
			mAdapter.notifyDataSetChanged();
		} else {
			mAdapter = new MsgAdapter(mBaseActivity, msgInfoList,new MsgClickListener(){
				@Override
				public void onClick(MsgInfo msgInfo) {
					//进入信息列表
					gotoMsgList(msgInfo);
				}
				
			});
			mLvPullToRefreshView.setAdapter(mAdapter);
		}
		mLvPullToRefreshView.forbiddenLoadMore();
	}
	
	/**
	 * 进入信息列表页面
	 */
	private void gotoMsgList(MsgInfo msgInfo) {
		Intent intent = new Intent(PersonMsgActivity.this,
				ChattingActivity.class);
		intent.putExtra("ACCOUNTTO", msgInfo.member_id);
		intent.putExtra("NICKNAME", msgInfo.nickname);
		baseStartActivity(intent);
	}
	

	/**
	 * UI界面动作事件
	 */
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {

		case R.id.ll_tv_actionbarback:
			PersonMsgActivity.this.finish();
			break;

		case R.id.rl_person_msg_comment: // 评论
			Intent intent = new Intent(PersonMsgActivity.this,
					PersonPingLunActivity.class);
			baseStartActivity(intent);
			break;
		case R.id.rl_person_msg_zan: // 点赞
			Intent intent1 = new Intent(PersonMsgActivity.this,
					PersonZanActivity.class);
			baseStartActivity(intent1);
			break;

		default:
			break;
		}
	}

	@Override
	protected void resetLayout() {

		RelativeLayout ll_mypermsg = (RelativeLayout) findViewById(R.id.ll_permsg_root);
		SupportDisplay.resetAllChildViewParam(ll_mypermsg);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.getPerMsg();
		this.getSmsMsg();
	}

}
