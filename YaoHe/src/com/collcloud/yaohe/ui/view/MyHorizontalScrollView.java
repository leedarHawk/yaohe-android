package com.collcloud.yaohe.ui.view;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.collcloud.yaohe.ui.adapter.HorizontalScrollViewAdapter;
import com.collcloud.yaohe.ui.utils.CCLog;

public class MyHorizontalScrollView extends HorizontalScrollView implements
		OnClickListener {

	/**
	 * 条目点击时的回调
	 * 
	 */
	public interface OnItemClickListener {
		void onClick(View view, String txt);
	}

	private OnItemClickListener mOnClickListener;

	private static final String TAG = "MyHorizontalScrollView";

	/**
	 * HorizontalListView中的LinearLayout
	 */
	public static LinearLayout mContainer;

	/**
	 * 子元素的宽度
	 */
	private int mChildWidth;
	/**
	 * 子元素的高度
	 */
	private int mChildHeight;
	/**
	 * 数据适配器
	 */
	private HorizontalScrollViewAdapter mAdapter;
	/**
	 * 每屏幕最多显示的个数
	 */
	private int mCountOneScreen;
	/**
	 * 屏幕的宽度
	 */
	private int mScreenWitdh;

	/**
	 * 保存View与位置的键值对
	 */
	public static  Map<View, String> mViewStr = new HashMap<View, String>();

	public MyHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 获得屏幕宽度
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWitdh = outMetrics.widthPixels;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mContainer = (LinearLayout) getChildAt(0);
	}

	/**
	 * 初始化数据，设置数据适配器
	 * 
	 * @param mAdapter
	 */
	public void initDatas(HorizontalScrollViewAdapter mAdapter) {
		this.mAdapter = mAdapter;
		mContainer = (LinearLayout) getChildAt(0);
		// 获得适配器中第一个View
		final View view = mAdapter.getView(0, null, mContainer);
		mContainer.addView(view);

		// 强制计算当前View的宽和高
		if (mChildWidth == 0 && mChildHeight == 0) {
			int w = View.MeasureSpec.makeMeasureSpec(0,
					View.MeasureSpec.UNSPECIFIED);
			int h = View.MeasureSpec.makeMeasureSpec(0,
					View.MeasureSpec.UNSPECIFIED);
			view.measure(w, h);
			mChildHeight = view.getMeasuredHeight();
			mChildWidth = view.getMeasuredWidth();
			mChildHeight = view.getMeasuredHeight();
			// 计算每次加载多少个View
			mCountOneScreen = (mScreenWitdh / mChildWidth == 0) ? mScreenWitdh
					/ mChildWidth + 1 : mScreenWitdh / mChildWidth + 2;
		}

		initChildren();
	}

	public void initChildren() {
		mContainer = (LinearLayout) getChildAt(0);
		mContainer.removeAllViews();
		mViewStr.clear();

		for (int i = 0; i < HorizontalScrollViewAdapter.mDatas.size(); i++) {
			View view = mAdapter.getView(i, null, mContainer);
			view.setTag(HorizontalScrollViewAdapter.mDatas.get(i));
			view.setOnClickListener(this);
			mContainer.addView(view);
			mViewStr.put(view, HorizontalScrollViewAdapter.mDatas.get(i));
		}

	}

	@Override
	public void onClick(View v) {
		if (mOnClickListener != null) {
			mOnClickListener.onClick(v, mViewStr.get(v));
		}
	}

	public void setOnItemClickListener(OnItemClickListener mOnClickListener) {
		this.mOnClickListener = mOnClickListener;
	}

}
