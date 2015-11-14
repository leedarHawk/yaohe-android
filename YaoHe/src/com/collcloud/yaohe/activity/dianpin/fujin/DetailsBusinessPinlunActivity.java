package com.collcloud.yaohe.activity.dianpin.fujin;

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
import com.collcloud.yaohe.activity.login.LoginActivity;
import com.collcloud.yaohe.api.ApiAccessErrorManager;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.api.info.DetailBusinessCommentInfo;
import com.collcloud.yaohe.api.info.DetailBusinessCommentInfo.BusinessCommentInfo;
import com.collcloud.yaohe.api.info.DetailsCallCommentInfo.CallCommentInfo;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.GlobalVariable;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.adapter.HomeBusinessCommentAdapter;
import com.collcloud.yaohe.ui.adapter.HomeBusinessCommentAdapter.CommentHuifuListener;
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

/**
 * 店铺列表内容
 * 
 * @ClassName DetailsBusinessPinlunActivity
 * @Description
 * @author CollCloud_小米
 */
public class DetailsBusinessPinlunActivity extends BaseActivity implements
		OnClickListener {

	private LinearLayout mLlEmpty;
	private TextView mTvTips;
	/**
	 * 主页面显示内容的List
	 */
	private SingleLayoutListView mLvPullToRefreshView;
	private HomeBusinessCommentAdapter mAdapter;
	private TextView mTvSend;
	private EditText mEtPinLun;
	private String mStrShopID;
	private DetailBusinessCommentInfo mDetailsBusinessCommentInfo;
	private List<BusinessCommentInfo> mList = new ArrayList<BusinessCommentInfo>();
	private int mTotalCount = 0;
	
	private String tag = DetailsBusinessPinlunActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details_call_dianping);
		mStrShopID = getStringExtra(IntentKeyNames.KEY_SHOP_COMMENT_ID);
		getBusinessCommentInfo();
		// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		// Date times = new Date(Long.valueOf(System.currentTimeMillis()));
		// String time = df.format(times);
		// CCLog.e("时间格式化：",time);
	}

	/**
	 * 获取评论内容
	 * 
	 * @Title getBusinessCommentInfo
	 */
	private void getBusinessCommentInfo() {
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("shop_id", mStrShopID);
		CCLog.i("请求店铺评论列表：", "shop_id=" + mStrShopID);
		String url = ContantsValues.BUSINESS_DETAILS_COMMENT_URL+"&shop_id="+mStrShopID;
		CCLog.d(tag, "shop comment url :"+url);
		http.send(HttpRequest.HttpMethod.POST,
				url, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						JSONObject jsonObject;
						try {
							mLvPullToRefreshView.onRefreshComplete();
							jsonObject = new JSONObject(responseInfo.result);
							responseErrorInfo(responseInfo);
							if (responseInfo.result != null) {
								CCLog.i("店铺评论列表详情：", responseInfo.result);
							}
							if (jsonObject != null && jsonObject.has("data")) {
								mDetailsBusinessCommentInfo = GsonUtils
										.json2Bean(responseInfo.result,
												DetailBusinessCommentInfo.class);
								if (mDetailsBusinessCommentInfo.data != null
										&& mDetailsBusinessCommentInfo.data
												.size() > 0) {
									if (mList != null && mList.size() > 0) {
										mList.clear();
									}
									//mList = new ArrayList<BusinessCommentInfo>();
									if (mDetailsBusinessCommentInfo.data.size() == 1) {
										if (Utils
												.isStringEmpty(mDetailsBusinessCommentInfo.data
														.get(0).id)
												&& Utils.isStringEmpty(mDetailsBusinessCommentInfo.data
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
									mTotalCount = mDetailsBusinessCommentInfo.data
											.size();
									for (int j = 0; j < mDetailsBusinessCommentInfo.data
											.size(); j++) {

										BusinessCommentInfo commentInfo = new BusinessCommentInfo();
										commentInfo.totalStar = GlobalVariable.sShopStar;
										if (mDetailsBusinessCommentInfo.data
												.get(j).id != null) {
											commentInfo.id = mDetailsBusinessCommentInfo.data
													.get(j).id;
										}
										if (mDetailsBusinessCommentInfo.data
												.get(j).shop_id != null) {
											commentInfo.shop_id = mDetailsBusinessCommentInfo.data
													.get(j).shop_id;
										}
										if (mDetailsBusinessCommentInfo.data
												.get(j).member_id != null) {
											commentInfo.member_id = mDetailsBusinessCommentInfo.data
													.get(j).member_id;
										}
										if (mDetailsBusinessCommentInfo.data
												.get(j).star != null) {
											commentInfo.star = mDetailsBusinessCommentInfo.data
													.get(j).star;
										}
										if (mDetailsBusinessCommentInfo.data
												.get(j).content != null) {
											commentInfo.content = mDetailsBusinessCommentInfo.data
													.get(j).content;
										}
										if (mDetailsBusinessCommentInfo.data
												.get(j).is_anonymous != null) {
											commentInfo.is_anonymous = mDetailsBusinessCommentInfo.data
													.get(j).is_anonymous;
										}
										if (mDetailsBusinessCommentInfo.data
												.get(j).addtime != null) {
											commentInfo.addtime = mDetailsBusinessCommentInfo.data
													.get(j).addtime;
										}
										if (mDetailsBusinessCommentInfo.data
												.get(j).title != null) {
											commentInfo.title = mDetailsBusinessCommentInfo.data
													.get(j).title;
										}
										if (mDetailsBusinessCommentInfo.data
												.get(j).type != null) {
											commentInfo.type = mDetailsBusinessCommentInfo.data
													.get(j).type;
										}
										if (mDetailsBusinessCommentInfo.data
												.get(j).nickname != null) {
											commentInfo.nickname = mDetailsBusinessCommentInfo.data
													.get(j).nickname;
										}
										if (!Utils
												.isStringEmpty(mDetailsBusinessCommentInfo.data
														.get(j).face)) {
											commentInfo.face = URLs.IMG_PRE
													+ mDetailsBusinessCommentInfo.data
															.get(j).face;
										}
										
										if (mDetailsBusinessCommentInfo.data.get(j).parentid != null) {
											commentInfo.parentid = mDetailsBusinessCommentInfo.data
													.get(j).parentid;
										}
										if (mDetailsBusinessCommentInfo.data.get(j).answerName != null) {
											commentInfo.answerName = mDetailsBusinessCommentInfo.data
													.get(j).answerName;
										}

										mList.add(commentInfo);

									}
									setTopOnlyBackTitle("评论详情"+"（"+mTotalCount+"）");
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
							}

						} catch (JSONException e) {
							e.printStackTrace();
							setTopOnlyBackTitle("评论详情");
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						UIHelper.ToastMessage(
								DetailsBusinessPinlunActivity.this,
								R.string.response_data_invalid);
					}
				});

	}
	
	
	
	/**
	 * 根据评论id删除评论
	 * @param commentId
	 */
	private void delComment(final String commentId) {
		
		ApiAccess.showProgressDialog(this, "正在删除评论..",
				R.style.progress_dialog);
		String url = ContantsValues.DELSHOP_COMMENT_URL+"&id="+commentId;
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
												BusinessCommentInfo tmpc = null;
												for(BusinessCommentInfo bc : mList) {
													if(bc.id.equals(commentId) ) {
														tmpc = bc;
														break;
													}
												}
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
	

	/**
	 * 设定评论列表信息
	 * 
	 * @Title setCommentListInfo
	 */
	private void setCommentListInfo() {
		if(mAdapter == null) {
			mAdapter = new HomeBusinessCommentAdapter(
					DetailsBusinessPinlunActivity.this, mList,mLoginDataManager.getMemberId(),new CommentHuifuListener() {
						@Override
						public void onCommentForHuifu(BusinessCommentInfo callCommentInfo) {
							gotoShopCommentActivity(callCommentInfo.id, "1");
						}

						@Override
						public void onDelComment(
								BusinessCommentInfo businessCommentInfo) {
							if (!mLoginDataManager.getLoginState().equals("1")) {
								UIHelper.ToastMessage(DetailsBusinessPinlunActivity.this, "您还没登录，请先登录。");
								Intent intent = new Intent(mBaseActivity,
										LoginActivity.class);
								mBaseActivity.baseStartActivity(intent);
							} else {
								delComment(businessCommentInfo.id);
							}
							
						}
					});
			mLvPullToRefreshView.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}
		setTopOnlyBackTitle("评论详情"+"（"+mList.size()+"）");

	}

	@Override
	protected void resetLayout() {
		RelativeLayout rlLayout = (RelativeLayout) findViewById(R.id.rl_activity_details_call_dianpin);
		SupportDisplay.resetAllChildViewParam(rlLayout);

		initTopOnlyBackTitle();
		setTopOnlyBackTitle("评论详情"+"（"+mTotalCount+"）");
		mLvPullToRefreshView = (SingleLayoutListView) findViewById(R.id.lv_activity_details_call_dianpin);
		mTvSend = (TextView) findViewById(R.id.tv_activity_call_pinglun_send);
		mEtPinLun = (EditText) findViewById(R.id.et_activity_call_pinglun_);
		mTvSend.setOnClickListener(this);

		mLlEmpty = (LinearLayout) findViewById(R.id.ll_activity_details_comment_empty);
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
				//loadData(0);
				getBusinessCommentInfo();
			}
		});
		mBtnIsCancelButton = true;
		mTopCancel = true;

	}

//	/**
//	 * 加载数据啦~
//	 */
//	public void loadData(final int type) {
//		new Thread() {
//			@Override
//			public void run() {
//				switch (type) {
//				case 0:
//					Message _Msg = mUiHandler.obtainMessage(0);
//					mUiHandler.sendMessage(_Msg);
//					CCLog.i("关注商家  —— 可以加载数据了。。");
//					break;
//				case 1:
//					getBusinessCommentInfo();
//					break;
//				}
//
//				try {
//					Thread.sleep(1500);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				if (type == 0) { // 下拉刷新
//					// Collections.reverse(mList); //逆序
//					Message _Msg = mUiHandler.obtainMessage(1);
//					mUiHandler.sendMessage(_Msg);
//				}
//			}
//		}.start();
//	}

	@SuppressLint("HandlerLeak")
	Handler mUiHandler = new Handler() {
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				getBusinessCommentInfo();
				break;

			case 1:
				UIHelper.ToastMessage(mBaseActivity, "数据已全部加载，没有更多了。");
				mLvPullToRefreshView.onRefreshComplete(); // 下拉刷新完成
				break;
			case 2:
				ApiAccess.dismissProgressDialog();
				if (mList != null && mList.size() > 0) {
					mAdapter = new HomeBusinessCommentAdapter(
							DetailsBusinessPinlunActivity.this, mList);
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
			gotoShopCommentActivity("0","0");
			break;

		default:
			break;
		}

	}
	
	/**
	 * 
	 * @param parentId被回复的评论id号
	 */
	private void gotoShopCommentActivity(String parentId,String shopCommentType) {
		if (!mLoginDataManager.getLoginState().equals("1")) {
			UIHelper.ToastMessage(this, "您还没登录，请先登录。");
			Intent intent = new Intent(mBaseActivity,
					LoginActivity.class);
			mBaseActivity.baseStartActivity(intent);
		} else {
			Intent intent = new Intent();
			intent.setClass(DetailsBusinessPinlunActivity.this,
					ShopCommentActivity.class);
			intent.putExtra(IntentKeyNames.KEY_SHOP_COMMENT_ID, mStrShopID);
			intent.putExtra("shopCommentType", shopCommentType);
			intent.putExtra("parentId", parentId);
			startActivityForResult(intent, 20);
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 20 && data != null) {
			Bundle b = data.getExtras(); // data为B中回传的Intent
			if (!Utils.isStringEmpty(b.getString("BusinessShopID"))) {
				mStrShopID = b.getString("BusinessShopID");
				CCLog.i("onActivityResult", " shop_id = " + mStrShopID);
			}
		}
		//loadData(0);
		getBusinessCommentInfo();

	}

	/**
	 * 店铺点评
	 * 
	 * @Title commentApi
	 * @Description
	 * @param memberID
	 *            会员ID(post提交)
	 * @param shopId
	 *            店铺ID(post提交)
	 * @param star
	 *            几颗星
	 * @param content
	 *            内容
	 * @param isAnonymous
	 *            是否匿名 0不匿名1匿名
	 * @param type
	 *            0评论 1回复
	 * @param url
	 *            请求地址
	 * @param message
	 * @return void
	 */
//	private void commentApi(String memberID, String shopId, String star,
//			String content, String isAnonymous, String type, String url,
//			final String message) {
//
//		HttpUtils http = new HttpUtils();
//		RequestParams params = new RequestParams();
//		params.addBodyParameter("member_id", memberID);
//		params.addBodyParameter("shop_id", shopId);
//		params.addBodyParameter("star", star);
//		params.addBodyParameter("content", content);
//		params.addBodyParameter("is_anonymous", isAnonymous);
//		params.addBodyParameter("type", type);
//		CCLog.i("店铺点评参数：", "member_id= " + memberID + " shop_id=" + shopId
//				+ " star=" + star + " content=" + content + " is_anonymous="
//				+ isAnonymous);
//
//		http.send(HttpRequest.HttpMethod.POST, url, params,
//				new RequestCallBack<String>() {
//
//					@Override
//					public void onSuccess(ResponseInfo<String> responseInfo) {
//						if (!Utils.isStringEmpty(responseInfo.result)) {
//							if (responseInfo.result.contains("status")) {
//								if (responseInfo.result != null) {
//									CCLog.i("店铺点评信息：", " "
//											+ responseInfo.result);
//								}
//								try {
//									// 数据处理
//									JSONObject errorJsonObject = new JSONObject(
//											responseInfo.result);
//									if (errorJsonObject.has("status")) {
//										JSONObject statusObject = errorJsonObject
//												.optJSONObject("status");
//										int code = statusObject.optInt("code");
//										if (code == 1) {
//											String strErrorMsg = statusObject
//													.getString("message");
//											UIHelper.ToastMessage(
//													mBaseActivity, strErrorMsg);
//										} else {
//											ApiAccess.showProgressDialog(
//													mBaseActivity, "提交中..");
//
//											new Handler().postDelayed(
//													new Runnable() {
//														@Override
//														public void run() {
//															if (message != null) {
//																UIHelper.ToastMessage(
//																		mBaseActivity,
//																		message);
//															}
//															mEtPinLun
//																	.setText("");
//															ApiAccess
//																	.dismissProgressDialog();
//															loadData(0);
//														}
//													}, 1000);
//										}
//									}
//								} catch (Exception e) {
//									String errorMsg = ApiAccessErrorManager
//											.getMessage(5, mBaseActivity);
//									UIHelper.ToastMessage(mBaseActivity,
//											errorMsg);
//								}
//
//							}
//						}
//					}
//
//					@Override
//					public void onFailure(HttpException error, String msg) {
//						UIHelper.ToastMessage(mBaseActivity,
//								R.string.response_data_invalid);
//					}
//				});
//	}

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
		// Intent intent = new Intent();
		// Bundle bundle = new Bundle();
		// intent.putExtra(IntentKeyNames.KEY_COMMENT_NUM, mTotalCount);
		// intent.putExtras(bundle);
		// setResult(11, intent);
		DetailsBusinessPinlunActivity.this.finish();
	}

}