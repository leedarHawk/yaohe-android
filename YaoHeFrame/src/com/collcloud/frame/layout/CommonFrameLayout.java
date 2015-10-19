package com.collcloud.frame.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.collcloud.frame.R;


public class CommonFrameLayout extends FrameLayout{

	
	public CommonFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTag(context);
	}

	public CommonFrameLayout(Context context) {
		super(context);
		setTag(context);
	}
	
	public CommonFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTag(context);
	}

	protected void setTag(Context context) {
		this.setTag(context.getResources().getString(R.string.tag));
	}
	

}
