/**
 * @file XFooterView.java
 * @create Mar 31, 2012 9:33:43 PM
 * @author Maxwin
 * @description XListView's footer
 */
package com.collcloud.frame.xlistview;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.collcloud.frame.R;
import com.collcloud.frame.widget.progress.cycle.CircularProgressBar;
import com.collcloud.frame.widget.progress.cycle.CircularProgressDrawable;

public class XListViewFooter extends LinearLayout {
	//public final static int STATE_NORMAL = 0;
	public final static int STATE_READY = 1;
	public final static int STATE_LOADING = 2;
	public final static int STATE_FORBIDDEN=3;

	private Context mContext;

	private View mContentView;
	private ProgressBar mProgressBar;
	private TextView mHintView;
	private LinearLayout moreView;
	
	public XListViewFooter(Context context) {
		super(context);
		initView(context);
	}
	
	public XListViewFooter(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	
	public void setState(int state) {
		moreView.setVisibility(View.GONE);
		mHintView.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.GONE);
		//((CircularProgressDrawable)mProgressBar.getIndeterminateDrawable()).stop();
		mHintView.setVisibility(View.GONE);
		if (state == STATE_READY) {
			mHintView.setVisibility(View.VISIBLE);
			mHintView.setText("");
			moreView.setVisibility(View.VISIBLE);
		} else if (state == STATE_LOADING) {
			moreView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.VISIBLE);
			//((CircularProgressDrawable)mProgressBar.getIndeterminateDrawable()).start();
		} else if (state == STATE_FORBIDDEN) {
			mHintView.setVisibility(View.GONE);
			mHintView.setText("");
			moreView.setVisibility(View.GONE);
		}else {
			mHintView.setVisibility(View.GONE);
			mHintView.setText("");
		}
	}
	
	public void setBottomMargin(int height) {
		if (height < 0) return ;
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
		lp.bottomMargin = height;
		mContentView.setLayoutParams(lp);
	}
	
	public int getBottomMargin() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
		return lp.bottomMargin;
	}
	
	
	/**
	 * normal status
	 */
	public void normal() {
		mHintView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
	}
	
	
	/**
	 * loading status 
	 */
	public void loading() {
		mHintView.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);
	}
	
	/**
	 * hide footer when disable pull load more
	 */
	/*public void hide() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
		lp.height = 0;
		mContentView.setLayoutParams(lp);
	}
	
	*//**
	 * show footer
	 *//*
	public void show() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)mContentView.getLayoutParams();
		lp.height = (int)dpToPx(mContext.getResources(),50);
		mContentView.setLayoutParams(lp);
	}*/
	
	private void initView(Context context) {
		mContext = context;
		moreView = (LinearLayout)LayoutInflater.from(mContext).inflate(R.layout.xlistview_footer, null);
		addView(moreView);
		moreView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, (int)dpToPx(context.getResources(),50)));
		
		mContentView = moreView.findViewById(R.id.xlistview_footer_content);
		mProgressBar =  (ProgressBar) moreView.findViewById(R.id.xlistview_footer_progressbar);
		mHintView = (TextView)moreView.findViewById(R.id.xlistview_footer_hint_textview);
	}
	
    private float dpToPx(Resources resources, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }
	
	
}
