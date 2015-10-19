package com.collcloud.yaohe.ui.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.SupportDisplay;

public class HorizontalScrollViewAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	public static List<String> mDatas;
	private int mLastClickPositon = -1;
	public TextView mTabTextView ;

	public HorizontalScrollViewAdapter(Context context, List<String> mDatas) {
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		this.mDatas = mDatas;
	}

	public int getCount() {
		return mDatas.size();
	}

	public Object getItem(int position) {
		return mDatas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.item_top_tab_text, parent,
					false);

			LinearLayout layout = (LinearLayout) convertView
					.findViewById(R.id.ll_item_top_text_viewgroup);
			SupportDisplay.resetAllChildViewParam(layout);
			viewHolder.mText = (TextView) convertView
					.findViewById(R.id.tv_top_item_text);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.mText.setText(mDatas.get(position));
//		if (mLastClickPositon != position) {
//			mLastClickPositon = position;
//			viewHolder.mText.setText(Color.rgb(252, 96, 71));
//
//		} else {
//			viewHolder.mText.setText(Color.rgb(95, 95, 95));
//			mLastClickPositon = -1;
//		}

		return convertView;
	}



	class ViewHolder {
		TextView mText;
	}

}
