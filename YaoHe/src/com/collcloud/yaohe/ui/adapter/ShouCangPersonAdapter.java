package com.collcloud.yaohe.ui.adapter;

import java.util.List;

import com.collcloud.yaohe.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ShouCangPersonAdapter extends BaseAdapter {

	Context context;
	//List<E> data;
	LayoutInflater layout;
	//构造传参
	public ShouCangPersonAdapter(Context context,List<String> data) {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		//获取布局
		View view=arg1;
		if(view==null)
			view = layout.inflate(R.layout.item_sc_per_list, null);
		//获取布局中的组件
		ImageView im_sc_per_pic=(ImageView) view.findViewById(R.id.im_sc_per_pic);
		TextView tv_item_per_content=(TextView) view.findViewById(R.id.tv_item_per_content);
		
		TextView tv_item_rl_name=(TextView) view.findViewById(R.id.tv_item_rl_name);
		TextView tv_item_sc_per_time=(TextView) view.findViewById(R.id.tv_item_sc_per_time);
		//绑定数据
		
		return view;
	}

}
