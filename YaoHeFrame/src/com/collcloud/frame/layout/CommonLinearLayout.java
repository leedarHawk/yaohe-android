package com.collcloud.frame.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.collcloud.frame.R;

@SuppressLint("NewApi")
public class CommonLinearLayout extends LinearLayout{


	public CommonLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTag(context);
	}

	public CommonLinearLayout(Context context) {
		super(context);
		setTag(context);
	}

	public CommonLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTag(context);
	}

	protected void setTag(Context context) {
		this.setTag(context.getResources().getString(R.string.tag));
	}
	
}
