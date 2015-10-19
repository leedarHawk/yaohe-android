package com.collcloud.yaohe.ui.adapter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.collcloud.swipe.SimpleSwipeListener;
import com.collcloud.swipe.SwipeLayout;
import com.collcloud.swipe.adapters.BaseSwipeAdapter;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.activity.chat.ChattingActivity;
import com.collcloud.yaohe.entity.MyFansList;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * @类说明 我的粉丝列表
 * @author 赵洋洋
 */
public class BusinessMyFansAdapter extends BaseSwipeAdapter {

	// 上下文对象
	private Context mContext;
	// 上下文对象
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	List<MyFansList> data_myfans;
	private DisplayImageOptions options; // 加载参数
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	// 构造函数
	public BusinessMyFansAdapter(Context mContext,List<MyFansList> data,
			DisplayImageOptions options) {
		
		this.mContext = mContext;
		this.data_myfans = data;
		this.options=options;
	}

	// SwipeLayout的布局id
	@Override
	public int getSwipeLayoutResourceId(int position) {
		return R.id.swipe_myfans;
	}

	@Override
	public View generateView(final int position, ViewGroup parent) {
		View v = LayoutInflater.from(mContext).inflate(
				R.layout.item_myfans_list, parent, false);
		final SwipeLayout swipeLayout = (SwipeLayout) v
				.findViewById(getSwipeLayoutResourceId(position));
		// 当隐藏的删除menu被打开的时候的回调函数
		swipeLayout.addSwipeListener(new SimpleSwipeListener() {
			@Override
			public void onOpen(SwipeLayout layout) {

			}
		});
		// 双击的回调函数
		swipeLayout
				.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
					@Override
					public void onDoubleClick(SwipeLayout layout,
							boolean surface) {
					}
				});
		// 添加删除布局的点击事件
		v.findViewById(R.id.ll_menu_fans_del).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						data_myfans.remove(position);
						notifyDataSetChanged(); 
						// 点击完成之后，关闭删除menu
						swipeLayout.close();
					}
				});
		
		Button mFansBt=(Button) v.findViewById(R.id.bt_bus_item_fans_fxx);
		mFansBt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				Intent  intent =new Intent(mContext,ChattingActivity.class);
				intent.putExtra("ACCOUNTTO",data_myfans.get(position).login_user);
				intent.putExtra("NICKNAME", data_myfans.get(position).nickname);

				mContext.startActivity(intent);
			}
		});
		return v;
	}

	// 对控件的填值操作独立出来了，我们可以在这个方法里面进行item的数据赋值
	@Override
	public void fillValues(int position, View convertView) {
		
		MyFansList myFansList = data_myfans.get(position);

		ImageView im_item_per_fans_img = (ImageView) convertView
				.findViewById(R.id.im_item_per_fans_img);

		imageLoader.displayImage("http://static.htcheng.com/imgs/"
				+ myFansList.face, im_item_per_fans_img, options,
				animateFirstListener);
		
		TextView tv_bus_item_fans_name = (TextView) convertView
				.findViewById(R.id.tv_bus_item_fans_name);
		tv_bus_item_fans_name.setText(myFansList.nickname);

	}

	@Override
	public int getCount() {
		return data_myfans.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}
