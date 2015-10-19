package com.collcloud.yaohe.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.entity.HykUseTimeList;

public class HYKDetailUseTimeAdapter extends BaseAdapter {
	
	Context context;
	List<HykUseTimeList> data_usetime;
	LayoutInflater layout;
	
	public HYKDetailUseTimeAdapter(Context context,List<HykUseTimeList> data_usetime) {
		
		this.context=context;
		this.data_usetime=data_usetime;
        this.layout=LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return data_usetime.size();
	}

	@Override
	public Object getItem(int arg0) {
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
		if (view == null){
			
			view = layout.inflate(R.layout.item_hykusetime_list, null);
		}
		TextView tv_hyk_time_fir=(TextView) view.findViewById(R.id.tv_hyk_time_fir);
		
		HykUseTimeList hykusetime=data_usetime.get(arg0);
		
		// 绑定数据
		tv_hyk_time_fir.setText(hykusetime.ply_time+"使用");
		
		return view;
	}
	
	

}
