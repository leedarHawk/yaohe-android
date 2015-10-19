package com.collcloud.yaohe.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.collcloud.swipe.SimpleSwipeListener;
import com.collcloud.swipe.SwipeLayout;
import com.collcloud.swipe.adapters.BaseSwipeAdapter;
import com.collcloud.yaohe.R;

/**
 * 
 * @author 赵洋洋
 * 
 */
public class BusinessMyMsgAdapter extends BaseSwipeAdapter {

	// 上下文对象
	private Context mContext;

	// 构造函数
	public BusinessMyMsgAdapter(Context mContext) {
		this.mContext = mContext;
	}

	// SwipeLayout的布局id
	@Override
	public int getSwipeLayoutResourceId(int position) {
		return R.id.swipe;
	}

	@Override
	public View generateView(int position, ViewGroup parent) {
		View v = LayoutInflater.from(mContext).inflate(R.layout.item_bus_msg_list,
				parent, false);
		final SwipeLayout swipeLayout = (SwipeLayout) v
				.findViewById(getSwipeLayoutResourceId(position));
		// 当隐藏的删除menu被打开的时候的回调函数
		swipeLayout.addSwipeListener(new SimpleSwipeListener() {
			@Override
			public void onOpen(SwipeLayout layout) {
				Toast.makeText(mContext, "Open", Toast.LENGTH_SHORT).show();
			}
		});
		// 双击的回调函数
		swipeLayout
				.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
					@Override
					public void onDoubleClick(SwipeLayout layout,
							boolean surface) {
						Toast.makeText(mContext, "DoubleClick",
								Toast.LENGTH_SHORT).show();
					}
				});
		// 添加删除布局的点击事件
		v.findViewById(R.id.ll_menu).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast.makeText(mContext, "delete", Toast.LENGTH_SHORT).show();
				// 点击完成之后，关闭删除menu
				swipeLayout.close();
			}
		});
		return v;
	}

	// 对控件的填值操作独立出来了，我们可以在这个方法里面进行item的数据赋值
	@Override
	public void fillValues(int position, View convertView) {
		TextView t = (TextView) convertView.findViewById(R.id.tv_bus_item_fans_name);
		t.setText((position + 1) + "." + "我就是一行很长");
	}

	@Override
	public int getCount() {
		return 20;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
