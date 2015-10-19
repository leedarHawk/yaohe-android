package com.collcloud.yaohe.activity.business.myyaohe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.swu.pulltorefresh.RefreshTime;
import cn.swu.pulltorefreshswipemenulistviewsample.PullToRefreshSwipeMenuListView;
import cn.swu.pulltorefreshswipemenulistviewsample.PullToRefreshSwipeMenuListView.IXListViewListener;
import cn.swu.pulltorefreshswipemenulistviewsample.PullToRefreshSwipeMenuListView.OnMenuItemClickListener;
import cn.swu.pulltorefreshswipemenulistviewsample.PullToRefreshSwipeMenuListView.OnSwipeListener;
import cn.swu.swipemenulistview.SwipeMenu;
import cn.swu.swipemenulistview.SwipeMenuCreator;
import cn.swu.swipemenulistview.SwipeMenuItem;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.business.BusinessActivity;
import com.collcloud.yaohe.activity.business.fayaohe.BusinessFaYaoHeActivity;
import com.collcloud.yaohe.activity.details.huodong.HuoDongDetailsActivity;
import com.collcloud.yaohe.activity.details.vip.VipCardDetailsActivity;
import com.collcloud.yaohe.activity.details.xinpin.XinPinDetailsActivity;
import com.collcloud.yaohe.activity.details.yaohela.YaoHeLaDetailsActivity;
import com.collcloud.yaohe.activity.details.youhui.YouHuiDetailsActivity;
import com.collcloud.yaohe.api.ApiAccessErrorManager;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.entity.FourService;
import com.collcloud.yaohe.staticvalue.Staticvalue;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.UIHelper;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.L;

/**
 * @类说明 我的吆喝界面
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月12日 下午3:02:18
 */
public class MyYaoHeActivity extends BaseActivity implements OnClickListener {

	private static final String TAG = "MYYAOHE";

	LinearLayout todolayout;
	LinearLayout titleLayout;
	/** 当前数据条目 */
	private int curCount = 0;
	private int mPage = 0;
	/** 吆喝mListView */
	// private XListView mListView;
	//private SwipeListView mSwipeListView;
	private PullToRefreshSwipeMenuListView mListView;
	/** 标题信息 */
	private TextView tv_title;
	/** 发送按钮 */
	private TextView tv_do;
	/** 进度条 */
	private Dialog myh_Dialog;
	/** 用来模拟异步获取数据 */
	private ArrayList<FourService> mYaoHeList;
	// private BusinessMyYaoHeAdapter mAdapter;
	private MyYaoHeAdapter mAdapter;

	/** 图片加载 */
	DisplayImageOptions myh_options;
	/** 创建缓存 */
	private static final String TEST_FILE_NAME = "Universal Image Loader @#&=+-_.,!()~'%20.png";
	/** 图片imageLoader初始化 */
	private ImageLoader imageLoader = ImageLoader.getInstance();

	private LinearLayout mLlEmpty;
	private TextView mTvEmptyTips;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_yao_he);
		/** 图片imageLoader初始化 */
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));

		intialSource();

		// 创建本地缓存
		File testImageOnSdCard = new File("/mnt/sdcard", TEST_FILE_NAME);
		if (!testImageOnSdCard.exists()) {
			copyTestImageToSdCard(testImageOnSdCard);
		}
		myh_options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icon_yaohe_loading_default)
				.showImageForEmptyUri(R.drawable.icon_yaohe_loading_default)
				.showImageOnFail(R.drawable.icon_yaohe_loading_default)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.build();
		Staticvalue.OPTIONS = myh_options;

		accessNetGetData(true,false);

		// // 设置xlistview可以加载、刷新
		// mListView.setPullLoadEnable(false);
		//
		// // 设置xlistview可以刷新
		// mListView.setPullRefreshEnable(true);
		//
		// mListView.setXListViewListener(new IXListViewListener() {
		//
		// @Override
		// public void onRefresh() {
		// mPage = mPage + 1;
		// accessNetGetData();
		// handler.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// mListView.stopRefresh();
		// showToast("数据已是最新。");
		// }
		// }, 1000);
		//
		// }
		//
		// @Override
		// public void onLoadMore() {
		// mPage = mPage + 1;
		// accessNetGetData();
		// handler.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// mListView.stopLoadMore();
		// mListView.setPullLoadEnable(false);
		// showToast("数据已加载完毕。");
		// }
		// }, 1000);
		// }
		// });

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPage = 0;
	}

	/**
	 * 获取数据
	 */
	private void accessNetGetData(boolean showLoadingBar,boolean loadMore) {
		if(showLoadingBar) {
			progressbar(MyYaoHeActivity.this, R.layout.loading_progress);
		}
		
		HttpUtils http = new HttpUtils();
		// 用来封装参数
		RequestParams params = new RequestParams();
		// 用户ID
		CCLog.v(TAG, "当前用户的ID>>>>>" + mLoginDataManager.getMemberId());
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());
		// params.addBodyParameter("page", "2");

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.YYFW, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						CCLog.v(TAG, "获取已发送的吆喝时网络出错了");
						hideProgressbar();
						showToast("数据加载失败了");
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						hideProgressbar();
						CCLog.v(TAG, "网络获取吆喝数据成功");
						CCLog.v(TAG, arg0.result);
						JSONObject object, object2;
						// 网络返回发布吆喝状态信息
						String responseInfo = "";
						// 网络发布吆喝状态码
						String code = "";
						// 网络发布吆喝返回消息
						String responseMsg = "";

						try {
							object = new JSONObject(arg0.result);
							responseInfo = object.getString("status");
							object2 = new JSONObject(responseInfo);

							code = object2.getString("code");
							responseMsg = object2.getString("message");

							if (code.equals("0")) {

								JSONArray jsoArray = object
										.optJSONArray("data");
								final int itemYhCount = jsoArray.length();

								if (itemYhCount > 0) {
									if (itemYhCount == 1) {
										JSONObject yhObject = jsoArray
												.getJSONObject(0);
										if (Utils.isStringEmpty(yhObject
												.optString("id"))) {
											mLlEmpty.setVisibility(View.VISIBLE);
											mListView
													.setVisibility(View.GONE);
											return;
										} else {
											mListView
													.setVisibility(View.VISIBLE);
											mLlEmpty.setVisibility(View.GONE);
										}
									}

									// if (itemYhCount > 9) {
									// mListView.setPullLoadEnable(true);
									// } else {
									// mListView.setPullLoadEnable(false);
									// }
									tv_title.setText("吆喝(" + itemYhCount + ")");
									List<FourService> yaoHeList = parseJSONArray(jsoArray);
									setYaoHeData(yaoHeList);
								} else {
									onLoad();
									showToast("您还没有发布吆喝");
								}
							}

						} catch (JSONException e) {
							onLoad();
							CCLog.v(TAG, "获取吆喝数据时异常" + e.toString());
							e.printStackTrace();
						}

					}

				});
	}

	/**
	 * 我的吆喝Json解析
	 */
	public ArrayList<FourService> parseJSONArray(JSONArray response) {

		if (mYaoHeList != null && mYaoHeList.size() > 0) {
			mYaoHeList.clear();
		}
		mYaoHeList = new ArrayList<FourService>();

		FourService fourservice = null;

		for (int i = 0; i < response.length(); i++) {

			fourservice = new FourService();
			JSONObject object = null;
			try {
				object = response.getJSONObject(i);

				fourservice.id = object.optString("id");
				fourservice.member_id = object.optString("member_id");
				fourservice.service_id = object.optString("service_id");
				fourservice.type = object.optString("type");

				fourservice.title = object.optString("title");
				fourservice.content = object.optString("content");
				fourservice.addtime = object.optString("addtime");

				fourservice.city_id = object.optString("city_id");
				fourservice.province_id = object.optString("province_id");
				fourservice.area_id = object.optString("area_id");

				fourservice.collection_num = object.optString("collection_num");
				fourservice.comment_num = object.optString("comment_num");
				fourservice.zan_num = object.optString("zan_num");

				if (!Utils.isStringEmpty(object.optString("img"))) {
					fourservice.img = URLs.IMG_PRE + object.optString("img");
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
			mYaoHeList.add(fourservice);
		}
		return mYaoHeList;
	}

	private void setYaoHeData(List<FourService> list) {
		onLoad();
		if (mAdapter != null) {
			mAdapter.refresh(list);
		} else {
			mAdapter = new MyYaoHeAdapter(MyYaoHeActivity.this, list,
					myh_options, 0);
			mListView.setAdapter(mAdapter);
		}
		//initListener();
	}

	private void initListener() {
//		mAdapter.setOnRightItemClickListener(new MyYaoHeAdapter.OnRightClickListener() {
//
//			@Override
//			public void onRightItemClick(View v, int position) {
//				deleteService(position);
//			}
//		});
//
//		if (mAdapter != null) {
//			mAdapter.setOnMyYaoheListerner(new OnMyYaoheListener() {
//
//				@Override
//				public void onMyYaoheClick(int position, String type,
//						String callID, String member_id, String serviceId) {
//					onServiceItemClick(type, callID, member_id, serviceId);
//				}
//			});
//		}
	}

	private void deleteService(int position) {
		if (mYaoHeList != null && mYaoHeList.size() > 0) {
			String callID = mYaoHeList.get(position).id;

			String url = ContantsValues.BUSINESS_DELETE_CALL;
			deleteBusinessService(callID, url,position);
		}
	}

	private void onServiceItemClick(String type, String callID,
			String memberID, String serviceID) {
		Intent intent = new Intent();
		intent.putExtra(IntentKeyNames.KEY_DETAILS_SERVICE_ID, serviceID);
		intent.putExtra(IntentKeyNames.KEY_CALL_ID, callID);
		intent.putExtra(IntentKeyNames.KEY_SHOP_MEMBER_ID, memberID);
		if (type.equals("0")) {
			intent.setClass(MyYaoHeActivity.this, YouHuiDetailsActivity.class);
			baseStartActivity(intent);
		} else if (type.equals("1")) {
			intent.setClass(MyYaoHeActivity.this, VipCardDetailsActivity.class);
			baseStartActivity(intent);

		} else if (type.equals("2")) {
			intent.setClass(MyYaoHeActivity.this, HuoDongDetailsActivity.class);
			baseStartActivity(intent);

		} else if (type.equals("3")) {
			intent.setClass(MyYaoHeActivity.this, XinPinDetailsActivity.class);
			baseStartActivity(intent);

		} else {
			intent.setClass(MyYaoHeActivity.this, YaoHeLaDetailsActivity.class);
			baseStartActivity(intent);
		}
	}

	/**
	 * UI界面动作事件
	 */
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {

		case R.id.ll_common_top_back:
			baseStartActivity(new Intent(MyYaoHeActivity.this,
					BusinessActivity.class));
			finish();
			break;
		case R.id.ll_tv_do:
			baseStartActivity(new Intent(MyYaoHeActivity.this,
					BusinessFaYaoHeActivity.class));
			break;

		default:
			break;
		}
	}

	/**
	 * UI界面资源初始化
	 */
	public void intialSource() {

		// this.mListView = (XListView) findViewById(R.id.xListView_myh_list);
		initListView();
		

		this.tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("吆喝");

		this.tv_do = (TextView) findViewById(R.id.tv_do);
		tv_do.setText("发布");
	}
	
	int count=0;
	@SuppressLint("ShowToast")
	private void initListView() {
		
		mListView = (PullToRefreshSwipeMenuListView) findViewById(R.id.slv_person_my_yaohe);
		mListView.setPullRefreshEnable(true);
	    mListView.setPullLoadEnable(true);
	    mListView.setXListViewListener(new IXListViewListener() {
			
			@Override
			public void onRefresh() {
				mListView.setPullLoadEnable(true);
				count =0;
				accessNetGetData(false,false);
			}
			
			@Override
			public void onLoadMore() {
				count++;
				if(count>3) {
					mListView.setPullLoadEnable(false);
					showToast("没有更多数据了");
				}
				accessNetGetData(false,true);
			}
		});
		
		
		// step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
//                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
//                // set item background
//                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
//                // set item width
//                openItem.setWidth(dp2px(90));
//                // set item title
//                openItem.setTitle("打开");
//                // set item title fontsize
//                openItem.setTitleSize(18);
//                // set item title font color
//                openItem.setTitleColor(Color.WHITE);
//                // add to menu
//                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        mListView.setMenuCreator(creator);
        // step 2. listener item click event
        mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                //ApplicationInfo item = mAppList.get(position);
                //FourService item = mYaoHeList.get(position);
                switch (index) {
                case 0:
                    // open
                    //open(item);
                	
                    // delete
                	deleteService(position);
                    break;
//                case 1:
//                    // delete
//                	deleteService(position);
//                    break;
                }
            }
        });

//        mListView.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view,
//					int position, long id) {
//				Toast.makeText(MyYaoHeActivity.this, "position:"+position, Toast.LENGTH_SHORT);
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) {
//				
//			}
//		});
        // set SwipeListener
        mListView.setOnSwipeListener(new OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }

			public void onSwipeClick(int position) {
				try {
					FourService serviceInfo = mYaoHeList.get(position-1);
					final String type = serviceInfo.type;
					final String memberID = serviceInfo.member_id;
					final String callID = serviceInfo.id;
					final String service_id = serviceInfo.service_id;
					onServiceItemClick(type, callID, memberID, service_id);
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			}
        });
	}
	
    private void onLoad() {
        mListView.setRefreshTime(RefreshTime.getRefreshTime(getApplicationContext()));
        mListView.stopRefresh();

        mListView.stopLoadMore();

    }
	
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

	/**
	 * 页面适配
	 */
	@Override
	protected void resetLayout() {
		LinearLayout ll_myyh = (LinearLayout) this
				.findViewById(R.id.ll_myyaohe_root);
		SupportDisplay.resetAllChildViewParam(ll_myyh);
		titleLayout = (LinearLayout) this.findViewById(R.id.ll_common_top_back);
		titleLayout.setOnClickListener(this);
		todolayout = (LinearLayout) this.findViewById(R.id.ll_tv_do);
		todolayout.setOnClickListener(this);

		mLlEmpty = (LinearLayout) this
				.findViewById(R.id.ll_business_my_yaohe_empty);
		mLlEmpty.setVisibility(View.GONE);
		mTvEmptyTips = (TextView) this.findViewById(R.id.tv_yaohe_no_data_text);
		mTvEmptyTips.setText("您还没有发布吆喝呢");
		mTvEmptyTips.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				baseStartActivity(new Intent(MyYaoHeActivity.this,
						BusinessFaYaoHeActivity.class));
			}
		});
	}

	/**
	 * imageloader
	 * 
	 * @说明 图片写入缓存
	 * @param testImageOnSdCard
	 */
	private void copyTestImageToSdCard(final File testImageOnSdCard) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					InputStream is = getAssets().open(TEST_FILE_NAME);
					FileOutputStream fos = new FileOutputStream(
							testImageOnSdCard);
					byte[] buffer = new byte[8192];
					int read;
					try {
						while ((read = is.read(buffer)) != -1) {
							fos.write(buffer, 0, read);
						}
					} finally {
						fos.flush();
						fos.close();
						is.close();
					}
				} catch (IOException e) {
					L.w("Can't copy test image onto SD card");
				}
			}
		}).start();
	}

	/**
	 * 加载进度条
	 */
	private void progressbar(Context context, int layout) {
		myh_Dialog = new AlertDialog.Builder(this).create();
		// mDialog.setOnKeyListener(keyListener);
		myh_Dialog.show();
		// 注意此处要放在show之后 否则会报异常
		myh_Dialog.setContentView(layout);
	}
	
	private void hideProgressbar() {
		if(myh_Dialog != null) {
			myh_Dialog.dismiss();
		}
	}

	/**
	 * 删除优惠券，会员卡，新品和活动
	 */
	private void deleteBusinessService(String serviceID, String url,final int position) {
		progressbar(MyYaoHeActivity.this, R.layout.loading_progress);
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId());
		params.addBodyParameter("id", serviceID);
		CCLog.i("删除吆喝参数：", "member_id=" + mLoginDataManager.getMemberId()
				+ " id=" + serviceID);

		http.send(HttpRequest.HttpMethod.POST, url, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						hideProgressbar();
						if (!Utils.isStringEmpty(responseInfo.result)) {
							if (responseInfo.result.contains("status")) {
								try {
									// 数据处理
									JSONObject errorJsonObject = new JSONObject(
											responseInfo.result);
									if (responseInfo.result != null) {
										CCLog.i("删除吆喝状态信息：",
												responseInfo.result + " ");
									}
									if (errorJsonObject.has("status")) {
										JSONObject statusObject = errorJsonObject
												.optJSONObject("status");
										if (statusObject.has("code")) {
											int code = statusObject
													.optInt("code");
											if (code == 1) {
												String strErrorMsg = statusObject
														.optString("message");
												UIHelper.ToastMessage(
														mBaseActivity,
														strErrorMsg);
											} else {
												showToast("删除成功");
												mYaoHeList.remove(position);
												setYaoHeData(mYaoHeList);
												//accessNetGetData(false);
											}
										}
									}
								} catch (Exception e) {
									String errorMsg = ApiAccessErrorManager
											.getMessage(5, mBaseActivity);
									UIHelper.ToastMessage(mBaseActivity,
											errorMsg);
									hideProgressbar();
								}

							}
						}
					}

					@Override
					public void onFailure(HttpException error, String msg) {
						UIHelper.ToastMessage(mBaseActivity,
								R.string.response_data_invalid);
						hideProgressbar();
					}
				});

	}
}
