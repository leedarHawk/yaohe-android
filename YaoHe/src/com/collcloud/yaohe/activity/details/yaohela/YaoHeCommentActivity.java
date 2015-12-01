package com.collcloud.yaohe.activity.details.yaohela;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.dianpin.fujin.ShopCommentActivity;
import com.collcloud.yaohe.activity.login.LoginActivity;
import com.collcloud.yaohe.api.ApiAccessErrorManager;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.api.info.DetailsCallCommentInfo;
import com.collcloud.yaohe.api.info.DetailsCallCommentInfo.CallCommentInfo;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.adapter.HomeCallDetailsAdapter;
import com.collcloud.yaohe.ui.adapter.HomeCallDetailsAdapter.CommentHuifuListener;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.GsonUtils;
import com.collcloud.yaohe.ui.utils.UIHelper;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.ui.view.SingleLayoutListView;
import com.collcloud.yaohe.ui.view.SingleLayoutListView.OnRefreshListener;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

public class YaoHeCommentActivity extends BaseActivity implements
		OnClickListener {

	private LinearLayout mLlEmpty;
	private TextView mTvTips;
	/**
	 * 主页面显示内容的List
	 */
	private SingleLayoutListView mLvPullToRefreshView;
	private HomeCallDetailsAdapter mAdapter;
	private TextView mTvSend;
	private EditText mEtPinLun;

	private String mStrCallID;
	private String mStrType;

	private DetailsCallCommentInfo mDetailsCallCommentInfo;
	private List<CallCommentInfo> mList = new ArrayList<CallCommentInfo>();
	private int mTotalCount = 0;
	
	private String tag = YaoHeCommentActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yaohe_comment_details);
		mStrCallID = getStringExtra(IntentKeyNames.KEY_CALL_COMMENT_ID);
		mStrType = getStringExtra("YaoHeType");
		ApiAccess.showProgressDialog(this, "数据加载中..",
				R.style.progress_dialog);
		getCallCommentInfo(mStrCallID);
		CCLog.i("吆喝点评 CallID = ",""+mStrCallID );
		// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		// Date times = new Date(Long.valueOf(System.currentTimeMillis()));
		// String time = df.format(times);
		// CCLog.e("时间格式化：",time);
	}
	


	/**
	 * 获取评论内容
	 * 
	 * @Title getCallCommentInfo
	 */
	private void getCallCommentInfo(String callID) {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("call_id", callID);
		String url = ContantsValues.CALL_DETAILS_COMMENT_URL + "&call_id=" + callID ;
		CCLog.d(tag, "url--->"+url);
		http.send(HttpRequest.HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						JSONObject jsonObject;
						ApiAccess.dismissProgressDialog();
						try {
							jsonObject = new JSONObject(responseInfo.result);
							responseErrorInfo(responseInfo);
							if (responseInfo.result != null) {
								CCLog.i("吆喝评论列表详情：", responseInfo.result);
							}
							if (jsonObject != null && jsonObject.has("data")) {
								mDetailsCallCommentInfo = GsonUtils.json2Bean(
										responseInfo.result,
										DetailsCallCommentInfo.class);
								if (mDetailsCallCommentInfo.data != null
										&& mDetailsCallCommentInfo.data.size() > 0) {
									mList.clear();
									if (mDetailsCallCommentInfo.data.size() == 1) {
										if (Utils
												.isStringEmpty(mDetailsCallCommentInfo.data
														.get(0).id)
												&& Utils.isStringEmpty(mDetailsCallCommentInfo.data
														.get(0).member_id)) {
											mLlEmpty.setVisibility(View.VISIBLE);
											mLvPullToRefreshView
													.setVisibility(View.GONE);
											return;
										} else {
											mLlEmpty.setVisibility(View.GONE);
											mLvPullToRefreshView
													.setVisibility(View.VISIBLE);
										}
									}
									mTotalCount = mDetailsCallCommentInfo.data
											.size();
									
									for (int j = 0; j < mDetailsCallCommentInfo.data
											.size(); j++) {

										CallCommentInfo commentInfo = new CallCommentInfo();
										if (mDetailsCallCommentInfo.data.get(j).id != null) {
											commentInfo.id = mDetailsCallCommentInfo.data
													.get(j).id;
										}
										if (mDetailsCallCommentInfo.data.get(j).member_id != null) {
											commentInfo.member_id = mDetailsCallCommentInfo.data
													.get(j).member_id;
										}
										if (mDetailsCallCommentInfo.data.get(j).content != null) {
											commentInfo.content = mDetailsCallCommentInfo.data
													.get(j).content;
										}
										if (mDetailsCallCommentInfo.data.get(j).nickname != null) {
											commentInfo.nickname = mDetailsCallCommentInfo.data
													.get(j).nickname;
										}
										if (mDetailsCallCommentInfo.data.get(j).is_anonymous != null) {
											commentInfo.is_anonymous = mDetailsCallCommentInfo.data
													.get(j).is_anonymous;
										}
										if (mDetailsCallCommentInfo.data.get(j).addtime != null) {
											commentInfo.addtime = mDetailsCallCommentInfo.data
													.get(j).addtime;
										}
										if (!Utils
												.isStringEmpty(mDetailsCallCommentInfo.data
														.get(j).face)) {
											commentInfo.face = URLs.IMG_PRE
													+ mDetailsCallCommentInfo.data
															.get(j).face;
										}
										
										if (mDetailsCallCommentInfo.data.get(j).parentid != null) {
											commentInfo.parentid = mDetailsCallCommentInfo.data
													.get(j).parentid;
										}
										if (mDetailsCallCommentInfo.data.get(j).answerName != null) {
											commentInfo.answerName = mDetailsCallCommentInfo.data
													.get(j).answerName;
										}

										mList.add(commentInfo);

									}

									if (mList.size() > 0) {
										mLvPullToRefreshView
												.setVisibility(View.VISIBLE);
										mLlEmpty.setVisibility(View.GONE);
										// 设定评论列表信息
										setCommentListInfo();
									} else {
										mLvPullToRefreshView
												.setVisibility(View.VISIBLE);
										mLlEmpty.setVisibility(View.GONE);
									}

								}
								setTopOnlyBackTitle("评论详情"+"（"+mTotalCount+"）");
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						UIHelper.ToastMessage(YaoHeCommentActivity.this,
								R.string.response_data_invalid);
						setTopOnlyBackTitle("评论详情");
						ApiAccess.dismissProgressDialog();
					}
				});

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		mStrCallID = getStringExtra(IntentKeyNames.KEY_CALL_COMMENT_ID);
		mStrType = getStringExtra("YaoHeType");
		getCallCommentInfo(mStrCallID);
	}

	/**
	 * 设定评论列表信息
	 * 
	 * @Title setCommentListInfo
	 */
	private void setCommentListInfo() {
		String memberId = mLoginDataManager.getMemberId();
		if(mAdapter ==null) {
			mAdapter = new HomeCallDetailsAdapter(YaoHeCommentActivity.this, mList,
					memberId,new CommentHuifuListener(){
						@Override
						public void onCommentForHuifu(
								CallCommentInfo callCommentInfo) {
							//回复评论
							CCLog.d(tag, "huifu pinglun .......>"+callCommentInfo.nickname);
							//huifuComment(callCommentInfo);
							gotoShopCommentActivity(callCommentInfo.id);
							
						}
						@Override
						public void onDelComment(CallCommentInfo callCommentInfo) {
							if (!mLoginDataManager.getLoginState().equals("1")) {
								UIHelper.ToastMessage(YaoHeCommentActivity.this, "您还没登录，请先登录。");
								Intent intent = new Intent(mBaseActivity,
										LoginActivity.class);
								mBaseActivity.baseStartActivity(intent);
							} else {
								delComment(callCommentInfo.id);
							}
							
						}
			});
			mLvPullToRefreshView.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}
		setTopOnlyBackTitle("评论详情"+"（"+mList.size()+"）");

	}
	
//	private void huifuComment(CallCommentInfo callCommentInfo) {
//		rlLayoutCallSend.setVisibility(View.VISIBLE);
//	}
//	
	
	
	//显示隐藏 回复输入框
	private RelativeLayout rlLayoutCallSend;
	@Override
	protected void resetLayout() {
		RelativeLayout rlLayout = (RelativeLayout) findViewById(R.id.rl_activity_details_yaohe_comment);
		rlLayoutCallSend = (RelativeLayout) findViewById(R.id.rl_activity_details_call_send);
		
		SupportDisplay.resetAllChildViewParam(rlLayout);

		initTopOnlyBackTitle();
		setTopOnlyBackTitle("评论详情"+"（"+mTotalCount+"）");
		mLvPullToRefreshView = (SingleLayoutListView) findViewById(R.id.lv_activity_yaohe_dianpin);
		mTvSend = (TextView) findViewById(R.id.tv_activity_yaohe_pinglun_send);
		mTvSend.setOnClickListener(this);
		// mEtPinLun = (EditText) findViewById(R.id.et_activity_call_pinglun_);
		// mTvSend.setOnClickListener(this);

		mLlEmpty = (LinearLayout) findViewById(R.id.ll_activity_yaohe_comment_empty);
		mTvTips = (TextView) findViewById(R.id.tv_yaohe_no_data_text);
		mTvTips.setText("还没有人评论。\n快来抢沙发吧。");

		mLvPullToRefreshView.setCanLoadMore(false);
		mLvPullToRefreshView.setCanRefresh(true);
		mLvPullToRefreshView.setAutoLoadMore(false);
		mLvPullToRefreshView.setMoveToFirstItemAfterRefresh(false);
		mLvPullToRefreshView.setDoRefreshOnUIChanged(false);

		mLvPullToRefreshView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				loadData(0);
			}
		});
		mBtnIsCancelButton = true;
		mTopCancel = true;

	}

	/**
	 * 加载数据啦~
	 */
	public void loadData(final int type) {
		new Thread() {
			@Override
			public void run() {
				switch (type) {
				case 0:
					Message _Msg = mUiHandler.obtainMessage(0);
					mUiHandler.sendMessage(_Msg);
					CCLog.i("关注商家  —— 可以加载数据了。。");
					break;
				case 1:
					getCallCommentInfo(mStrCallID);
					break;
				}

				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (type == 0) { // 下拉刷新
					// Collections.reverse(mList); //逆序
					Message _Msg = mUiHandler.obtainMessage(1);
					mUiHandler.sendMessage(_Msg);
				}
			}
		}.start();
	}

	@SuppressLint("HandlerLeak")
	Handler mUiHandler = new Handler() {
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				getCallCommentInfo(mStrCallID);
				break;

			case 1:
				UIHelper.ToastMessage(mBaseActivity, "数据已全部加载，没有更多了。");
				mLvPullToRefreshView.onRefreshComplete(); // 下拉刷新完成
				break;
			case 2:
				ApiAccess.dismissProgressDialog();
				if (mList != null && mList.size() > 0) {
					String memberId = mLoginDataManager.getMemberId();
					mAdapter = new HomeCallDetailsAdapter(
							YaoHeCommentActivity.this, mList, memberId);
					mLvPullToRefreshView.setAdapter(mAdapter);
				}
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {
		if (Utils.isFastDoubleClick()) {
			return;
		}
		switch (v.getId()) {
		case R.id.tv_activity_call_pinglun_send:
			String comment = "";
			if (Utils.isStringEmpty(Utils.strFromView(mEtPinLun))) {
				UIHelper.ToastMessage(YaoHeCommentActivity.this, "点评内容不可为空。");
			}  else {
				comment = Utils.strFromView(mEtPinLun);
				// 吆喝点评API调用
				commentApi(mLoginDataManager.getMemberId(), mStrCallID, "3",
						comment, "1", ContantsValues.CALL_COMMENT_URL, "点评成功。");
			}

			break;
		case R.id.tv_activity_yaohe_pinglun_send:
			gotoShopCommentActivity("");
			break;
		default:
			break;
		}

	}
	
	/**
	 * 
	 * @param parentId被回复的评论id号
	 */
	private void gotoShopCommentActivity(String parentId) {
		if (!mLoginDataManager.getLoginState().equals("1")) {
			UIHelper.ToastMessage(this, "您还没登录，请先登录。");
			Intent intent = new Intent(mBaseActivity,
					LoginActivity.class);
			mBaseActivity.baseStartActivity(intent);
		} else {
			Intent intent = new Intent();
			intent.setClass(YaoHeCommentActivity.this,
					ShopCommentActivity.class);
			intent.putExtra(IntentKeyNames.KEY_CALL_COMMENT_ID, mStrCallID);
			intent.putExtra("callCommentType", mStrType);
			intent.putExtra("parentId", parentId);
			startActivityForResult(intent, 10);
		}
		
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 10 && resultCode == 11&&data !=null) {
			Bundle b = data.getExtras(); // data为B中回传的Intent
			if (!Utils.isStringEmpty(b.getString("yaoheCallID"))) {
				String callID = b.getString("yaoheCallID");
				boolean commentSuccess = b.getBoolean("commentSuccess");
				if(commentSuccess) {
					sendBroadCastForCommentCountChange(callID,true);
				}
				
				getCallCommentInfo(callID);
			}
		}
	}
	
	/**
	 * 根据评论id删除评论
	 * @param commentId
	 */
	private void delComment(final String commentId) {
		ApiAccess.showProgressDialog(this, "正在删除评论..",
				R.style.progress_dialog);
		String url = ContantsValues.DELYAOHE_COMMENT_URL+"&id="+commentId;
		CCLog.d(tag, "del comment url :" +url);
		
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("id", commentId);
		http.send(HttpRequest.HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!Utils.isStringEmpty(responseInfo.result)) {
							if (responseInfo.result.contains("status")) {
								if (responseInfo.result != null) {
									CCLog.i("删除点评信息：", " "
											+ responseInfo.result);
								}
								try {
									// 数据处理
									JSONObject errorJsonObject = new JSONObject(
											responseInfo.result);
									if (errorJsonObject.has("status")) {
										JSONObject statusObject = errorJsonObject
												.optJSONObject("status");
										int code = statusObject.optInt("code");
										if (code == 1) {
											String strErrorMsg = statusObject
													.getString("message");
											UIHelper.ToastMessage(
													mBaseActivity, strErrorMsg);
										} else {
											boolean delResult = errorJsonObject.optBoolean("data", false);
											if(!delResult) {
												Toast.makeText(mBaseActivity, "删除失败", Toast.LENGTH_SHORT).show();
											} else {
												Toast.makeText(mBaseActivity, "删除成功", Toast.LENGTH_SHORT).show();
												CallCommentInfo tmpc = null;
												for(CallCommentInfo cc : mList) {
													if(cc.id.equals(commentId) ) {
														tmpc = cc;
														break;
													}
												}
												sendBroadCastForCommentCountChange(mStrCallID,false);
												mList.remove(tmpc);
												
												setCommentListInfo();
											}
										}
									}
								} catch (Exception e) {
									ApiAccess.dismissProgressDialog();
									String errorMsg = ApiAccessErrorManager
											.getMessage(5, mBaseActivity);
									UIHelper.ToastMessage(mBaseActivity,
											errorMsg);
								}

							}
						}
						ApiAccess.dismissProgressDialog();
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						UIHelper.ToastMessage(mBaseActivity,
								R.string.response_data_invalid);
						ApiAccess.dismissProgressDialog();
					}
				});
	}

	private void commentApi(String memberID, String callID, String star,
			String content, String isAnonymous, String url, final String message) {

		// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		// Date times = new Date(Long.valueOf(System.currentTimeMillis()));
		// String time = df.format(times);
		// CCLog.e("时间格式化：",time);

		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", memberID);
		params.addBodyParameter("call_id", callID);
		params.addBodyParameter("star", star);
		params.addBodyParameter("content", content);
		params.addBodyParameter("is_anonymous", isAnonymous);
		CCLog.i("YaoHeCommentActivity 吆喝点评参数：", "member_id= " + memberID + " call_id=" + callID
				+ " star=" + star + " content=" + content + " is_anonymous="
				+ isAnonymous);

		http.send(HttpRequest.HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (!Utils.isStringEmpty(responseInfo.result)) {
							if (responseInfo.result.contains("status")) {
								if (responseInfo.result != null) {
									CCLog.i("吆喝点评信息：", " "
											+ responseInfo.result);
								}
								try {
									// 数据处理
									JSONObject errorJsonObject = new JSONObject(
											responseInfo.result);
									if (errorJsonObject.has("status")) {
										JSONObject statusObject = errorJsonObject
												.optJSONObject("status");
										int code = statusObject.optInt("code");
										if (code == 1) {
											String strErrorMsg = statusObject
													.getString("message");
											UIHelper.ToastMessage(
													mBaseActivity, strErrorMsg);
										} else {
											ApiAccess.showProgressDialog(
													mBaseActivity, "提交中..");

											new Handler().postDelayed(
													new Runnable() {
														@Override
														public void run() {
															if (message != null) {
																UIHelper.ToastMessage(
																		mBaseActivity,
																		message);
															}
															mEtPinLun
																	.setText("");
															ApiAccess
																	.dismissProgressDialog();
															loadData(0);
														}
													}, 1000);
										}
									}
								} catch (Exception e) {
									String errorMsg = ApiAccessErrorManager
											.getMessage(5, mBaseActivity);
									UIHelper.ToastMessage(mBaseActivity,
											errorMsg);
								}

							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						UIHelper.ToastMessage(mBaseActivity,
								R.string.response_data_invalid);
					}
				});
	}

	@Override
	protected void onCancelButtonListener() {
		super.onCancelButtonListener();
		cancelDefinedAction();
	}

	@Override
	protected void topCancelDefined() {
		super.topCancelDefined();
		cancelDefinedAction();
	}

	/**
	 * 返回前个页面传递的信息
	 * 
	 * @Title cancelDefinedAction
	 */
	private void cancelDefinedAction() {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.putExtra(IntentKeyNames.KEY_COMMENT_NUM, mTotalCount);
		intent.putExtras(bundle);
		setResult(11, intent);
		YaoHeCommentActivity.this.finish();
	}

}