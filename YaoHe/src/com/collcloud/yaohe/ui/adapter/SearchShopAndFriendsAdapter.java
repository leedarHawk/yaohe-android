package com.collcloud.yaohe.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.collcloud.yaohe.R;

public class SearchShopAndFriendsAdapter extends BaseAdapter {
	
	Context context;
	List<String> data;//数据实体,需要替换成相应的entity
	LayoutInflater layout;
	
	// 构造方法传参
	public SearchShopAndFriendsAdapter(Context context,List<String> data) {
		this.context = context;
		this.data = data;
		this.layout = LayoutInflater.from(context);		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		//获取布局
		View view=arg1;
		if(view==null)
			view = layout.inflate(R.layout.xml_adapter_searchandfriends, null);
		//获取布局中的组件
		//头像
		ImageView iv_photo=(ImageView) view.findViewById(R.id.iv_search_shopandfriends);
		TextView  tv_name=(TextView) view.findViewById(R.id.tv_search_shopandfriends);
		
		//绑定数据  还没有做（涉及到图片的加载）
		//DiaryInfo dinfo=(DiaryInfo) data.get(arg0);
        //iv_photo.setImageResource()
		
		return view;
	}

}
