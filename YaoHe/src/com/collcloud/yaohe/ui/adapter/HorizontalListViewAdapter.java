package com.collcloud.yaohe.ui.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.SupportDisplay;

public class HorizontalListViewAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	Bitmap iconBitmap;
	private int selectIndex = -1;
	public List<String> mDatas;

	public HorizontalListViewAdapter(Context context, List<String> datas) {
		this.mContext = context;
		this.mDatas = datas;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);// LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return mDatas == null ? 0 : mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void refreshData(List<String> datas) {
		this.mDatas = datas;
		notifyDataSetChanged();

	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		TabTitleHolder holder = null;
		if(convertView == null) {
			holder = new TabTitleHolder();
			convertView = mInflater.inflate(R.layout.item_top_tab_text, parent,
					false);

			LinearLayout layout = (LinearLayout) convertView
					.findViewById(R.id.ll_item_top_text_viewgroup);
			SupportDisplay.resetAllChildViewParam(layout);
			holder.mTitle = (TextView) convertView
					.findViewById(R.id.tv_top_item_text);
			holder.mRbtnTitle = (RadioButton) convertView
					.findViewById(R.id.rbtn_top_item_text);

			convertView.setTag(holder);
		} else {
			holder = (TabTitleHolder) convertView.getTag();
		}
		
		
		

		if (mDatas != null && mDatas.size() > 0) {

			holder.mTitle.setText(mDatas.get(position));
			holder.mRbtnTitle.setText(mDatas.get(position));
		}

		if (position == selectIndex) {
			// convertView.setSelected(true);
			holder.mTitle.setTextColor(mContext.getResources().getColor(
					R.color.common_text_color_chengse));
//			holder.mTitle.setTextSize(SupportDisplay.calculateControlerSizeY(15.0f));

		} else {
			// convertView.setSelected(false);
			holder.mTitle.setTextColor(mContext.getResources().getColor(
					R.color.common_home_text_color_title));
//			holder.mTitle.setTextSize(SupportDisplay.calculateControlerSizeY(14.0f));
		}

		return convertView;
	}

	class TabTitleHolder {
		TextView mTitle;
		RadioButton mRbtnTitle;
	}

	public void setSelectIndex(int i) {
		selectIndex = i;
	}
}