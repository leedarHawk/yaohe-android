package com.collcloud.yaohe.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.api.info.MsgInfo;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.meg7.widget.CustomShapeImageView;

/**
 * 
 * @author LEE
 * 我得消息adapter
 *
 */
public class MsgAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mLayoutInflater = null;



	private static ImageLoader mImageLoader;
	private int mItemCount = 0;
	private String tag = MsgAdapter.class.getSimpleName();

	private List<MsgInfo> msgInfoList = new ArrayList<MsgInfo>();
	public MsgClickListener msgClickListener;
	public interface MsgClickListener {
		public void onClick(MsgInfo msgInfo);
	}

	public MsgAdapter(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
	}

	public MsgAdapter(Context context, List<MsgInfo> datas,MsgClickListener msgClickListener) {
		this.mContext = context;
		this.msgInfoList = datas;
		this.msgClickListener = msgClickListener;
		mLayoutInflater = LayoutInflater.from(mContext);

		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
	}

	public void setItemCount(int count) {
		this.mItemCount = count;
	}

	@Override
	public int getCount() {
		if (msgInfoList == null) {
			return 0;
		}
		return msgInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return msgInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void refresh(List<MsgInfo> data) {
		this.msgInfoList = data;
		notifyDataSetChanged();
	}

	@SuppressLint({ "InflateParams", "ClickableViewAccessibility" })
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final MsgContent holder;
		MsgInfo msgInfo = null;
		if (msgInfoList != null && msgInfoList.size() > 0) {
			msgInfo = msgInfoList.get(position);
		}
		if (convertView == null) {

			convertView = mLayoutInflater.inflate(R.layout.item_person_msg,
					null);

			holder = new MsgContent();
			resetLayout(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (MsgContent) convertView.getTag();
		}
		
		try {
			if(Integer.parseInt(msgInfo.noReadCount) > 0) {
				holder.msg_count.setVisibility(View.VISIBLE);
			} else {
				holder.msg_count.setVisibility(View.GONE);
			}
		} catch(Exception e) {
			e.printStackTrace();
			holder.msg_count.setVisibility(View.GONE);
		}
		
		if(msgInfo !=null) {
			holder.msg_count.setText(msgInfo.noReadCount);
			holder.msg_sender_name.setText(msgInfo.nickname);
			holder.last_send_time.setText(msgInfo.lastSendtime);
		}

		// 设定首页吆喝信息
		ImageListener listener = ImageLoader.getImageListener(holder.msgFace,
				R.drawable.icon_yaohe_default_logo,
				R.drawable.icon_yaohe_default_logo);
		
		if (msgInfo != null && msgInfo.face != null) {
			mImageLoader.get(msgInfo.face, listener, mContext.getResources().getDimensionPixelSize(R.dimen.photo_width), mContext.getResources().getDimensionPixelSize(R.dimen.photo_height));
		}
		
		holder.rl.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				msgClickListener.onClick(msgInfoList.get(position));
			}
		});

		return convertView;
	}

	private class MsgContent {
		/** 发送者头像 */
		CustomShapeImageView msgFace;
		/** 消息数量 */
		TextView msg_count;
		/** 发送者名称 */
		TextView msg_sender_name;
		/** 日期时间 */
		TextView last_send_time;
		
		View rl;
	}

	private void resetLayout(MsgContent holder, View view) {
		holder.msgFace = (CustomShapeImageView) view.findViewById(R.id.msgFace);
		holder.msg_count = (TextView) view.findViewById(R.id.msg_count);
		holder.msg_sender_name = (TextView) view
				.findViewById(R.id.msg_sender_name);
		holder.last_send_time = (TextView) view
				.findViewById(R.id.last_send_time);
		holder.rl = view.findViewById(R.id.rl);
	}
}
