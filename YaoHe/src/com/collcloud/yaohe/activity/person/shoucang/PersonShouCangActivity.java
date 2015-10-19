package com.collcloud.yaohe.activity.person.shoucang;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.swu.pulltorefresh.RefreshTime;
import cn.swu.pulltorefreshswipemenulistviewsample.PullToRefreshSwipeMenuListView;
import cn.swu.pulltorefreshswipemenulistviewsample.PullToRefreshSwipeMenuListView.IXListViewListener;
import cn.swu.pulltorefreshswipemenulistviewsample.PullToRefreshSwipeMenuListView.OnMenuItemClickListener;
import cn.swu.pulltorefreshswipemenulistviewsample.PullToRefreshSwipeMenuListView.OnSwipeListener;
import cn.swu.swipemenulistview.SwipeMenu;
import cn.swu.swipemenulistview.SwipeMenuCreator;
import cn.swu.swipemenulistview.SwipeMenuItem;

import com.collcloud.yaohe.MainActivity;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.details.huodong.HuoDongDetailsActivity;
import com.collcloud.yaohe.activity.details.vip.VipCardDetailsActivity;
import com.collcloud.yaohe.activity.details.xinpin.XinPinDetailsActivity;
import com.collcloud.yaohe.activity.details.yaohela.YaoHeLaDetailsActivity;
import com.collcloud.yaohe.activity.details.youhui.YouHuiDetailsActivity;
import com.collcloud.yaohe.api.URLs;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.entity.SCInfoList;
import com.collcloud.yaohe.ui.adapter.ShouCangBusinessAdapter;
import com.collcloud.yaohe.ui.adapter.ShouCangBusinessAdapter.OnPersonCollectionListener;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.Utils;
import com.collcloud.yaohe.ui.view.SelectPicPopupWindow;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.L;

/**
 * @类说明 我的收藏(收藏的商家)
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月2日 下午3:02:18
 */
public class PersonShouCangActivity extends BaseActivity implements
		OnClickListener {

	private static final String TAG = "PersonShouCang";
	/** 标题信息 */
	private TextView tv_actionbartitle;
	/** 进度条 */
	private Dialog scBusin_Dialog;
	/** 数据列表 */
	private PullToRefreshSwipeMenuListView mListView;
	//private XListView mXListView;
	/** 模拟数据加载 */
	//private Handler handler;
	/** 图片加载 */
	private ShouCangBusinessAdapter mAdapter;
	/** 创建缓存 */
	private static final String TEST_FILE_NAME = "Universal Image Loader @#&=+-_.,!()~'%20.png";
	/** 图片imageLoader初始化 */
	private ImageLoader imageLoader = ImageLoader.getInstance();
	//private int mPos ;
	private ArrayList<SCInfoList> mListCollections = new ArrayList<SCInfoList>();
	// ******** Tips *******//
	private LinearLayout mLlEmpty;
	private TextView mTvEmptyTips;
	private SelectPicPopupWindow mPopupWindow;
	private int mDeleteID = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_shou_cang);
		/** 图片imageLoader初始化 */
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));

		intialSource();

		progressbar(PersonShouCangActivity.this, R.layout.loading_progress);
		accessNetGetBusinessData();

	}

	// 为弹出窗口实现监听类
//	private OnClickListener itemsOnClick = new OnClickListener() {
//
//		public void onClick(View v) {
//			mPopupWindow.dismiss();
//			switch (v.getId()) {
//			case R.id.btn_take_photo:
//				if (mDeleteID != -1) {
//					deleteShoucang(String.valueOf(mDeleteID));
////					mListCollections.remove(mPos);
////					mAdapter.refreshData(mListCollections);
//				}
//				break;
//			default:
//				break;
//			}
//		}
//	};

	/**
	 * 加载进度条
	 */
	private void progressbar(Context context, int layout) {
		scBusin_Dialog = new AlertDialog.Builder(this).create();
		// mDialog.setOnKeyListener(keyListener);
		scBusin_Dialog.show();
		// 注意此处要放在show之后 否则会报异常
		scBusin_Dialog.setContentView(layout);
	}

	/**
	 * 访问网络获取收藏的商家
	 */
	private void accessNetGetBusinessData() {

		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();

		// 封装post请求参数
		// 用户ID
		CCLog.v(TAG, "当前用户的ID>>>>>" + mLoginDataManager.getMemberId());
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId()
				.toString());

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.SC, params,
				new RequestCallBack<String>() {
					// 网络返回字符串
					String responseInfo = null;

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						CCLog.v(TAG, "获取收藏的商家时网络出错了");
						onLoad();
						scBusin_Dialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						scBusin_Dialog.dismiss();
						onLoad();
						CCLog.v(TAG, "获取收藏的商家成功");
						CCLog.v(TAG, arg0.result);
						JSONObject object, object2;
						String code = null;
						try {
							object = new JSONObject(arg0.result);
							// 获取一个返回的字符串
							responseInfo = object.getString("status");

							object2 = new JSONObject(responseInfo);

							code = object2.getString("code");

							// 数据获取成功
							if (code.equals("0")) {

								JSONArray jsoArray = object
										.optJSONArray("data");
								final int itemCount = jsoArray.length();
								if (mListCollections != null
										&& mListCollections.size() > 0) {
									mListCollections.clear();
								}
								mListCollections = new ArrayList<SCInfoList>();
								if (itemCount > 0) {
									if (itemCount == 1) {
										JSONObject yhObject = jsoArray
												.getJSONObject(0);
										if (Utils.isStringEmpty(yhObject
												.optString("id"))) {
											mLlEmpty.setVisibility(View.VISIBLE);
											mListView.setVisibility(View.GONE);
											return;
										} else {
											mListView
													.setVisibility(View.VISIBLE);
											mLlEmpty.setVisibility(View.GONE);
										}
									}
									for (int i = 0; i < itemCount; i++) {

										SCInfoList scItem = new SCInfoList();
										JSONObject scObject = jsoArray
												.getJSONObject(i);
										scItem.id = scObject.optString("id");
										scItem.service_id = scObject
												.optString("service_id");

										scItem.member_id = scObject
												.optString("member_id");

										scItem.content = scObject
												.optString("content");

										if (!Utils.isStringEmpty(scObject
												.optString("img1"))) {
											scItem.img1 = URLs.IMG_PRE
													+ scObject
															.optString("img1");
										}
										scItem.type = scObject
												.optString("type");
										scItem.addtime = scObject
												.optString("addtime");
										scItem.shop_name = scObject
												.optString("shop_name");
										mListCollections.add(scItem);
									}

									// 绑定适配器
									setCollectionData(mListCollections);
								} else {
									showToast("您还没有收藏任何商家");
								}

							}

						} catch (JSONException e) {
							onLoad();
							e.printStackTrace();
						}

					}

				});
	}

	private void setCollectionData(ArrayList<SCInfoList> scList) {
		if (mAdapter == null) {
			mAdapter = new ShouCangBusinessAdapter(PersonShouCangActivity.this,
					scList);
			mListView.setAdapter(mAdapter);

		} else {
			mAdapter.refreshData(scList);
		}
		//initListeners();
	}

//	private void initListeners() {
//		if (mAdapter != null) {
//			mAdapter.setOnCollectionItemListerner(new OnPersonCollectionListener() {
//
//				@Override
//				public void onLongCollectionClick(int position, String deleteID) {
//					if (!Utils.isStringEmpty(deleteID)) {
//						mDeleteID = Integer.valueOf(deleteID);
//					}
//					mPos = position;
//					mPopupWindow = new SelectPicPopupWindow(
//							PersonShouCangActivity.this, itemsOnClick);
//
//					mPopupWindow.setBottomOneText("取消收藏");
//					mPopupWindow.setOnlyOneButton();
//					// 显示窗口
//					mPopupWindow.showAtLocation(PersonShouCangActivity.this
//							.findViewById(R.id.mysc_rl_root), Gravity.BOTTOM
//							| Gravity.CENTER_HORIZONTAL, 0, 0);
//				}
//			});
//		}
//	}

	/**
	 * 内容为空提示对话框
	 */
	private void dialog() {

		AlertDialog.Builder builder = new Builder(PersonShouCangActivity.this);
		builder.setTitle("提示");
		builder.setMessage("您确定要删除吗？");
		builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// deleteShoucang();

			}
		});

		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

				arg0.dismiss();
			}
		});
		builder.create().show();
	}

	private void deleteShoucang(String detID,final int position) {
		progressbar(PersonShouCangActivity.this, R.layout.loading_progress);
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();

		// 封装post请求参数
		// 用户ID
		CCLog.v(TAG, "当前用户的ID>>>>>" + mLoginDataManager.getMemberId());
		params.addBodyParameter("member_id", mLoginDataManager.getMemberId()
				.toString());
		CCLog.v(TAG, "收藏ID>>>>>" + detID);
		params.addBodyParameter("id", detID);

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.DELETE_SHOUCANG,
				params, new RequestCallBack<String>() {
					// 网络返回字符串
					String responseInfo = null;

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						CCLog.v(TAG, "删除收藏时出错了");
						scBusin_Dialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						scBusin_Dialog.dismiss();
						CCLog.v(TAG, "删除收藏成功");
						CCLog.v(TAG, arg0.result);
						JSONObject object, object2;
						String code = null;
						String responseMsg = null;
						try {
							object = new JSONObject(arg0.result);
							// 获取一个返回的字符串
							responseInfo = object.getString("status");

							object2 = new JSONObject(responseInfo);

							code = object2.getString("code");
							responseMsg = object2.getString("message");

							// 数据获取成功
							if (code.equals("0")) {
								showToast("删除收藏成功");
								mListCollections.remove(position);
								mAdapter.refreshData(mListCollections);
								
							} else {
								showToast(responseMsg);
							}

						} catch (JSONException e) {
							e.printStackTrace();
							scBusin_Dialog.dismiss();
						}

					}

				});
	}

	/**
	 * UI界面资源初始化
	 */
	private void intialSource() {

		this.tv_actionbartitle = (TextView) findViewById(R.id.tv_actionbartitle);
		tv_actionbartitle.setText("我的收藏");

		//this.mXListView = (XListView) findViewById(R.id.xlv_sc_business_);

		// 创建本地缓存
		File testImageOnSdCard = new File("/mnt/sdcard", TEST_FILE_NAME);
		if (!testImageOnSdCard.exists()) {
			copyTestImageToSdCard(testImageOnSdCard);
		}
		
		this.initListView();
		/**
		handler = new Handler();
		// 设置xlistview可以加载、刷新
		mXListView.setPullLoadEnable(false);
		// 设置xlistview可以刷新
		mXListView.setPullRefreshEnable(true);

		mXListView.setXListViewListener(new IXListViewListener() {

			@Override
			public void onRefresh() {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						mXListView.stopRefresh();
						showToast("刷新成功");
					}
				}, 1000);
			}

			@Override
			public void onLoadMore() {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {

						mXListView.stopLoadMore();
						showToast("数据加载成功");
					}
				}, 1000);
			}
		});
		mXListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				String type = null;
				String serviceID = null;
				String callID = null;
				if (mListCollections.get(position-1).type != null) {
					type = mListCollections.get(position - 1).type;
					serviceID = mListCollections.get(position - 1).service_id;
					callID = mListCollections.get(position - 1).id;
					Intent intent = new Intent();
					intent.putExtra(IntentKeyNames.KEY_DETAILS_SERVICE_ID,
							serviceID);
					intent.putExtra(IntentKeyNames.KEY_CALL_ID, callID);
					if (type.equals("0")) {
						intent.setClass(PersonShouCangActivity.this,
								YouHuiDetailsActivity.class);
						baseStartActivity(intent);
					} else if (type.equals("1")) {
						intent.setClass(PersonShouCangActivity.this,
								VipCardDetailsActivity.class);
						baseStartActivity(intent);

					} else if (type.equals("2")) {
						intent.setClass(PersonShouCangActivity.this,
								HuoDongDetailsActivity.class);
						baseStartActivity(intent);

					} else if (type.equals("3")) {
						intent.setClass(PersonShouCangActivity.this,
								XinPinDetailsActivity.class);
						baseStartActivity(intent);

					} else {
						intent.setClass(PersonShouCangActivity.this,
								YaoHeLaDetailsActivity.class);
						baseStartActivity(intent);
					}
				}
			}
		}); */

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
	 * UI界面动作事件
	 */
	@Override
	public void onClick(View arg0) {

		switch (arg0.getId()) {

		case R.id.ll_tv_actionbarback:
			finish();
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

		RelativeLayout rel_mysc_sj = (RelativeLayout) findViewById(R.id.mysc_rl_root);
		SupportDisplay.resetAllChildViewParam(rel_mysc_sj);

		LinearLayout llLayout = (LinearLayout) findViewById(R.id.ll_tv_actionbarback);
		llLayout.setOnClickListener(this);

		mLlEmpty = (LinearLayout) this
				.findViewById(R.id.ll_person_shoucang_empty);
		mLlEmpty.setVisibility(View.GONE);
		mTvEmptyTips = (TextView) this.findViewById(R.id.tv_yaohe_no_data_text);
		mTvEmptyTips.setText("您还没有任何收藏\n\n去首页推荐看看吧");
		mTvEmptyTips.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				baseStartActivity(new Intent(PersonShouCangActivity.this,
						MainActivity.class));
				finish();
			}
		});
	}
	
	@SuppressLint("ShowToast")
	private void initListView() {
		
		mListView = (PullToRefreshSwipeMenuListView) findViewById(R.id.xlv_sc_business_);
		mListView.setPullRefreshEnable(true);
	    mListView.setPullLoadEnable(true);
	    mListView.setXListViewListener(new IXListViewListener() {
			
			@Override
			public void onRefresh() {
				mListView.setPullLoadEnable(true);
				accessNetGetBusinessData();
			}
			
			@Override
			public void onLoadMore() {
				mListView.setPullLoadEnable(false);
				onLoad();
				showToast("没有更多数据了");
			}
		});
		
		
		// step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
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
                	try {
                    	deleteShoucang(mListCollections.get(position).id,position);
                	} catch(Exception e) {
                		e.printStackTrace();
                	}
                	
                    break;
//                case 1:
//                    // delete
//                	deleteService(position);
//                    break;
                }
            }
        });
        
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
					String type = null;
					String serviceID = null;
					String callID = null;
					if (mListCollections.get(position-1).type != null) {
						type = mListCollections.get(position - 1).type;
						serviceID = mListCollections.get(position - 1).service_id;
						callID = mListCollections.get(position - 1).id;
						Intent intent = new Intent();
						intent.putExtra(IntentKeyNames.KEY_DETAILS_SERVICE_ID,
								serviceID);
						intent.putExtra(IntentKeyNames.KEY_CALL_ID, callID);
						if (type.equals("0")) {
							intent.setClass(PersonShouCangActivity.this,
									YouHuiDetailsActivity.class);
							baseStartActivity(intent);
						} else if (type.equals("1")) {
							intent.setClass(PersonShouCangActivity.this,
									VipCardDetailsActivity.class);
							baseStartActivity(intent);

						} else if (type.equals("2")) {
							intent.setClass(PersonShouCangActivity.this,
									HuoDongDetailsActivity.class);
							baseStartActivity(intent);

						} else if (type.equals("3")) {
							intent.setClass(PersonShouCangActivity.this,
									XinPinDetailsActivity.class);
							baseStartActivity(intent);

						} else {
							intent.setClass(PersonShouCangActivity.this,
									YaoHeLaDetailsActivity.class);
							baseStartActivity(intent);
						}
					}
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


}
