package com.collcloud.yaohe.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.entity.ChatInfo;

public class ChatAdapter extends BaseAdapter {

	/**
	 * 申明一个ArrayList对象，一个关联上下午的Context对象
	 */
	@SuppressWarnings("rawtypes")
	private ArrayList arrayList;
	private Context context;

	/**
	 * 构成方法
	 */
	public ChatAdapter(Context context,
			@SuppressWarnings("rawtypes") ArrayList arrayList) {
		this.arrayList = arrayList;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arrayList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return (ChatInfo) arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = null;
		LayoutInflater li = LayoutInflater.from(context);
		v = li.inflate(R.layout.chatting_listview_item_from, null);
		TextView tv_content_from = (TextView) v
				.findViewById(R.id.tv_message_content);// 发送内容
		TextView tv_content_to = (TextView) v.findViewById(R.id.tv_message_to);// 接收内容
		TextView tv_sender = (TextView) v.findViewById(R.id.tv_account);// 发送者
		TextView tv_receiver = (TextView) v.findViewById(R.id.tv_account_to);// 接收者
		
		ChatInfo chatInfo = (ChatInfo) arrayList.get(position);
		// 对显示聊天内容的ListView中显示接收者和发送者的聊天不就进行初始化
		if (((ChatInfo) arrayList.get(position)).getNum() == 1) {// 发送者
			//tv_sender.setText("<自己>");
			//tv_sender.setBackgroundResource(R.drawable.tx);
			tv_content_from.setBackgroundResource(R.drawable.chat_to_img_bg_normal); 
			tv_content_from.setText(chatInfo.getContent());

		} else if (((ChatInfo) arrayList.get(position)).getNum() == 0) {// 接收者
			//tv_receiver.setText("<对方>");
			//tv_receiver.setBackgroundResource(R.drawable.tx);
			Log.i("适配器", chatInfo.getContent().toString());
			
			tv_content_to.setText(chatInfo.getContent());
			tv_content_to.setBackgroundResource(R.drawable.chat_from_img_bg_normal);
		}
		return v;
	}

}
