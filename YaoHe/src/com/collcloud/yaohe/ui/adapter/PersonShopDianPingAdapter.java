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

public class PersonShopDianPingAdapter extends BaseAdapter {

	Context context;
	//List<E> data;
	LayoutInflater layout;
	//构造传参
	public PersonShopDianPingAdapter(Context context,List<String> data) {
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
			view = layout.inflate(R.layout.item_pinglun_list, null);
		//获取布局中的组件(5颗星星未获取)
		ImageView im_per_shopdianping_item_photo=(ImageView) view.findViewById(R.id.im_per_shopdianping_item_photo);
		ImageView im_per_shopdianping_item_shop_photo=(ImageView) view.findViewById(R.id.im_per_shopdianping_item_shop_photo);
		TextView tv_per_item_shopdianping_name=(TextView) view.findViewById(R.id.tv_per_item_shopdianping_name);
		TextView tv_per_item_shopdianping_time=(TextView) view.findViewById(R.id.tv_per_item_shopdianping_time);
		TextView tv_per_item_shopdianping_newmsg=(TextView) view.findViewById(R.id.tv_per_item_shopdianping_newmsg);
		TextView tv_per_item_shop_name=(TextView) view.findViewById(R.id.tv_per_item_shopdianping_shop_name);
		TextView tv_per_item_shop_word=(TextView) view.findViewById(R.id.tv_per_item_shopdianping_shop_word);
		Button bt_per_item_shopdianping_huifu=(Button) view.findViewById(R.id.bt_per_item_shopdianping_huifu);
		//绑定数据
		
		return view;
	}

}
