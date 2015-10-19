package com.collcloud.yaohe.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.adapter.FujinCategoryPopAdapter;
import com.collcloud.yaohe.ui.adapter.FujinTwoClassfyAdapter;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.Utils;

/**
 * 【附近】模块中分类列表
 * 
 * @ClassName FujinShopPopWindow
 * @author CollCloud_小米
 */
public class FujinCategoryPopWindow {
	/**
	 * Activity
	 */
	private Context mContext = null;
	private LayoutInflater mLayoutInflater;

	/** 一级分类列表 */
	private ListView mLvList = null;
	public FujinCategoryPopAdapter mFuJinCategoryAdapter = null;
	/**
	 * 二级分类列表
	 */
	private ListView mLvTwoClassfy = null;
	public FujinTwoClassfyAdapter mFujinTwoClassfyAdapter = null;

	/** 一级分类列表数据 */
	private List<String> mDatas = new ArrayList<String>();
	/** 二级分类列表数据 */
	private List<String> mTwoData = new ArrayList<String>();

	private TextView mTvClose;
	/** Popup Window */
	private PopupWindow mSelectPopupWindow = null;
	private Handler mHandler;
	public boolean popIsShowing = false;

	private String[] mMenuItems;
	private TextView mTvCategory;
	private int mScreenWidth;
	private int mScreenHeight;

	public FujinCategoryPopWindow(Context context, String[] menuLists,
			Handler handler, TextView tview) {
		this.mContext = context;
		this.mMenuItems = menuLists;
		this.mHandler = handler;
		this.mTvCategory = tview;
		mLayoutInflater = LayoutInflater.from(mContext);
		Point screenMetris = Utils.getScreenMetrics(context);
		mScreenWidth = screenMetris.x;
		mScreenHeight = screenMetris.y;
	}

	public FujinCategoryPopWindow(Context context, List<String> menuLists,
			Handler handler, TextView tView) {
		this.mContext = context;
		this.mDatas = menuLists;
		this.mHandler = handler;
		this.mTvCategory = tView;
		mLayoutInflater = LayoutInflater.from(mContext);
		Point screenMetris = Utils.getScreenMetrics(context);
		mScreenWidth = screenMetris.x;
		mScreenHeight = screenMetris.y;
	}

	public FujinCategoryPopWindow(Context context, List<String> menuLists,
			List<String> twoClassfy, Handler handler, TextView tView) {
		this.mContext = context;
		this.mDatas = menuLists;
		this.mTwoData = twoClassfy;
		this.mHandler = handler;
		this.mTvCategory = tView;
		mLayoutInflater = LayoutInflater.from(mContext);
		Point screenMetris = Utils.getScreenMetrics(context);
		mScreenWidth = screenMetris.x;
		mScreenHeight = screenMetris.y;
	}

	private void popupWindwShowing() {
		mSelectPopupWindow.showAsDropDown(mTvCategory, 0, 0);
	}

	public void dismiss() {
		mSelectPopupWindow.dismiss();
	}

	public void onPauseDismiss() {

		if (popIsShowing) {
			mSelectPopupWindow.dismiss();
			popIsShowing = false;
		}

		if (mDatas != null) {
			mDatas.clear();
		}
	}

	public void showPopUpWindow() {
		initPopWindow();
		popupWindwShowing();
		CCLog.e("ScreenHeight", " " + mScreenHeight);

	}

	public void initPopWindowListData(List<String> menuList) {

		initPopWindow();
	}

	public void initPopWindowListData(String[] menuList) {
		initDatas(menuList);

		initPopWindow();
	}

	private void initPopWindow() {
		View loginwindow = (View) mLayoutInflater.inflate(
				R.layout.common_custom_listview, null);

		mLvList = (ListView) loginwindow
				.findViewById(R.id.lv_common_custom_list);
		mLvTwoClassfy = (ListView) loginwindow
				.findViewById(R.id.lv_fujin_two_classfy_list);

		mTvClose = (TextView) loginwindow
				.findViewById(R.id.tv_fujin_shop_category_bottom_blank);
		mLvList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mLvTwoClassfy.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mLvList
				.getLayoutParams();
		// params.width = mScreenWidth;
		// TODO 高度
		params.height = SupportDisplay.calculateActualControlerSize(480);

		LinearLayout.LayoutParams townParams = (LinearLayout.LayoutParams) mLvTwoClassfy
				.getLayoutParams();
		// TODO 高度
		townParams.height = SupportDisplay.calculateActualControlerSize(480);

		// 一级分类数据
		mFuJinCategoryAdapter = new FujinCategoryPopAdapter(mContext, mHandler,
				mDatas);
		mLvList.setAdapter(mFuJinCategoryAdapter);

		// 二级分类数据
		mFujinTwoClassfyAdapter = new FujinTwoClassfyAdapter(mContext, mHandler, mTwoData);
		mLvTwoClassfy.setAdapter(mFujinTwoClassfyAdapter);

		mSelectPopupWindow = new PopupWindow(loginwindow, mScreenWidth,
				LayoutParams.WRAP_CONTENT);

		mSelectPopupWindow.setFocusable(false);
		mSelectPopupWindow.setOutsideTouchable(true);
		// mSelectPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		// Drawable drawable = mContext.getResources().getDrawable(
		// R.drawable.common_yellow_corners_on_color);
		// mSelectPopupWindow.setBackgroundDrawable(drawable);

		// mTvClose.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Message msg = new Message();
		// Bundle data = new Bundle();
		// msg.setData(data);
		// msg.what = 4;
		// mHandler.sendMessage(msg);
		// }
		// });
	}

	/**
	 * 設定データ集
	 * 
	 * @param menuList
	 */
	private void initDatas(String[] menuList) {

		if (menuList != null) {
			this.mMenuItems = menuList;
		}
		if (null != mDatas && mDatas.size() > 0) {
			mDatas.clear();
		}

		if (null != mMenuItems) {
			for (int i = 0; i < mMenuItems.length; i++) {
				String question = mMenuItems[i];
				mDatas.add(question);
			}
		}
	}

	public List<String> getmDatas() {
		return mDatas;
	}

	public void setmDatas(List<String> mDatas) {
		this.mDatas = mDatas;
	}

}
