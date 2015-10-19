package com.collcloud.yaohe.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.entity.UseTimeList;

public class YHQDetailUseTimeAdapter extends BaseAdapter {

	Context context;
	List<UseTimeList> data_usetime;
	LayoutInflater layout;

	public YHQDetailUseTimeAdapter(Context context,
			List<UseTimeList> data_usetime) {

		this.context = context;
		this.data_usetime = data_usetime;
		this.layout = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data_usetime.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// 获取布局
		View view = arg1;
		if (view == null) {

			view = layout.inflate(R.layout.item_usetime_list, null);
		}
		TextView tv_yhq_time_fir = (TextView) view
				.findViewById(R.id.tv_yhq_time_fir);

		UseTimeList usetime = data_usetime.get(arg0);

		// 绑定数据
		tv_yhq_time_fir.setText(usetime.ply_time);

		return view;
	}

}
