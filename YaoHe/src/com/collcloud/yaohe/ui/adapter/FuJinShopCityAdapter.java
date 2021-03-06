package com.collcloud.yaohe.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;

public class FuJinShopCityAdapter extends BaseAdapter {

	private List<String> mList = new ArrayList<String>();
	private Context mContext = null;
	private Handler mHandler;

	private SparseBooleanArray selected;
	boolean isSingle = true;
	int old = -1;

	public FuJinShopCityAdapter(Context activity, Handler handler,
			List<String> list) {
		this.mContext = activity;
		this.mHandler = handler;
		this.mList = list;
		selected = new SparseBooleanArray();
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setSelectedItem(int selected) {
		if (isSingle = true && old != -1) {
			this.selected.put(old, false);
		}
		this.selected.put(selected, true);
		old = selected;
	}

	public void refresh(List<String> data) {
		this.mList = data;
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		CityItem holder = null;
		if (convertView == null) {
			holder = new CityItem();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.common_list_item_fenlei_popup, null);

			RelativeLayout rlLayout = (RelativeLayout) convertView
					.findViewById(R.id.rl_item_fenlei_complete_adapter);
			SupportDisplay.resetAllChildViewParam(rlLayout);

			holder.textView = (TextView) convertView
					.findViewById(R.id.tv_common_list_item_popup);

			convertView.setTag(holder);
		} else {
			holder = (CityItem) convertView.getTag();
		}
		String data = mList.get(position);

		if (data != null) {
			holder.textView.setText(data);
		}
		if (selected.get(position)) {
			convertView.setBackgroundResource(R.color.diliver_out_gray);
			holder.textView.setTextColor(mContext.getResources().getColor(
					R.color.common_text_color_chengse));
		} else {
			convertView.setBackgroundResource(R.color.transparent);
			holder.textView.setTextColor(mContext.getResources().getColor(
					R.color.common_home_text_color_jianjie));
		}
		holder.textView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Message msg =  mHandler
						.obtainMessage(2);
				Bundle data = new Bundle();
				data.putInt(
						IntentKeyNames.KEY_POPUP_WINDOW_SELECTED_CITY_INDEX,
						position);
				msg.setData(data);
				msg.what = 2;
				mHandler.sendMessage(msg);
			}
		});

		return convertView;
	}

	class CityItem {
		TextView textView;
		TextView tvType;
	}
}
