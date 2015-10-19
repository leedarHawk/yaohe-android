package com.collcloud.yaohe.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.adapter.FuJinShopCityAdapter;
import com.collcloud.yaohe.ui.adapter.FujinShopTownAdapter;
import com.collcloud.yaohe.ui.utils.Utils;

/**
 * 【附近】模块中商圈列表
 * 
 * @ClassName FuJinShopPopWindow
 * @Description
 * @author CollCloud_小米
 */
public class FuJinShopPopWindow {
	private Context mContext = null;
	private LayoutInflater mLayoutInflater;

	private ListView mLvArea = null;
	private ListView mLvDistrict = null;
	public FuJinShopCityAdapter mAreaAdapter = null;
	public FujinShopTownAdapter mTownAdapter = null;

	private List<String> mAreasList = new ArrayList<String>();
	private List<String> mDistrictList = new ArrayList<String>();

	private TextView mTvClose;
	private PopupWindow mSelectPopupWindow = null;
	private Handler mHandler;
	public boolean popIsShowing = false;

	private TextView mTvCity;
	private int mScreenWidth;
	private int mScreenHeight;

	public FuJinShopPopWindow(Context context, List<String> areas,
			List<String> districts, Handler handler, TextView tvCity) {
		this.mContext = context;
		this.mAreasList = areas;
		this.mDistrictList = districts;
		this.mHandler = handler;
		this.mTvCity = tvCity;
		mLayoutInflater = LayoutInflater.from(mContext);
		Point screenMetris = Utils.getScreenMetrics(context);
		mScreenWidth = screenMetris.x;
		mScreenHeight = screenMetris.y;
	}

	private void popupWindwShowing() {
		mSelectPopupWindow.showAsDropDown(mTvCity, 0, 0);
	}

	public void dismiss() {
		mSelectPopupWindow.dismiss();
	}

	public void onPauseDismiss() {

		if (popIsShowing) {
			mSelectPopupWindow.dismiss();
			popIsShowing = false;
		}
		if (mAreasList != null) {
			mAreasList.clear();
		}
	}

	public void refreshAreaData(List<String> areaList) {
		this.mAreasList = areaList;
		if (mAreaAdapter != null) {
			mAreaAdapter.notifyDataSetChanged();
		}

	}

	public void refreshDistrictData(List<String> districtList) {
		this.mDistrictList = districtList;
		if (mTownAdapter != null) {
			mTownAdapter.notifyDataSetChanged();
		}

	}

	public void showPopUpWindow() {
		initPopWindow();
		popupWindwShowing();
	}

	public void initPopWindowListData(List<String> menuList) {

		initPopWindow();
	}

	private void initPopWindow() {
		View shopwindow = (View) mLayoutInflater.inflate(
				R.layout.common_fujin_shop_listview, null);

		mLvArea = (ListView) shopwindow
				.findViewById(R.id.lv_fujin_shop_city_list);
		mLvDistrict = (ListView) shopwindow
				.findViewById(R.id.lv_fujin_shop_town_list);
		mLvArea.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		// mLvArea.setSelection(0);
		mLvDistrict.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mTvClose = (TextView) shopwindow
				.findViewById(R.id.tv_fujin_shop_bottom_blank);

		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mLvArea
				.getLayoutParams();
		// TODO 高度
		params.height = SupportDisplay.calculateActualControlerSize(480);

		LinearLayout.LayoutParams townParams = (LinearLayout.LayoutParams) mLvDistrict
				.getLayoutParams();
		// TODO 高度
		townParams.height = SupportDisplay.calculateActualControlerSize(480);

		mAreaAdapter = new FuJinShopCityAdapter(mContext, mHandler, mAreasList);
		mLvArea.setAdapter(mAreaAdapter);

		mTownAdapter = new FujinShopTownAdapter(mContext, mHandler,
				mDistrictList);
		mLvDistrict.setAdapter(mTownAdapter);

		mSelectPopupWindow = new PopupWindow(shopwindow, mScreenWidth,
				LayoutParams.WRAP_CONTENT);

		mSelectPopupWindow.setFocusable(false);

		mSelectPopupWindow.setOutsideTouchable(true);
		mSelectPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		// Drawable drawable = mContext.getResources().getDrawable(
		// R.drawable.popup_window_background_grey);
		// mSelectPopupWindow.setBackgroundDrawable(drawable);
		// mTvClose.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Message msg = new Message();
		// Bundle data = new Bundle();
		// msg.setData(data);
		// msg.what = 5;
		// mHandler.sendMessage(msg);
		// }
		// });
	}

	public List<String> getAreasData() {
		return mAreasList;
	}

	public void setAreaData(List<String> mDatas) {
		this.mAreasList = mDatas;
	}

	public List<String> getDistrictDatas() {
		return mDistrictList;
	}

	public void setDistrictDatas(List<String> townDatas) {
		this.mDistrictList = townDatas;
	}

}
