package com.collcloud.yaohe.ui.adapter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.collcloud.swipe.SimpleSwipeListener;
import com.collcloud.swipe.SwipeLayout;
import com.collcloud.swipe.adapters.BaseSwipeAdapter;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.entity.MyYhList;
import com.collcloud.yaohe.ui.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * @类说明 我的吆喝列表适配器
 * @author 赵洋洋
 * 
 */
public class BusinessMyYaoHeAdapter extends BaseSwipeAdapter {

	// 上下文对象
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	Context mContext;
	List<MyYhList> data_myh;
	private DisplayImageOptions options; // 加载参数
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	// 构造函数
	public BusinessMyYaoHeAdapter(Context mContext, List<MyYhList> data,
			DisplayImageOptions options) {
		this.mContext = mContext;
		this.data_myh = data;
		this.options = options;
	}

	// SwipeLayout的布局id
	@Override
	public int getSwipeLayoutResourceId(int position) {
		return R.id.swipe;
	}

	@Override
	public View generateView(final int position, ViewGroup parent) {
		View v = LayoutInflater.from(mContext).inflate(
				R.layout.item_business_yaohe_content, parent, false);
		final SwipeLayout swipeLayout = (SwipeLayout) v
				.findViewById(getSwipeLayoutResourceId(position));
		// 当隐藏的删除menu被打开的时候的回调函数
		swipeLayout.addSwipeListener(new SimpleSwipeListener() {
			@Override
			public void onOpen(SwipeLayout layout) {
				// Toast.makeText(mContext, "Open", Toast.LENGTH_SHORT).show();
			}
		});
		// 双击的回调函数 双击进入吆喝详情
		swipeLayout
				.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
					@Override
					public void onDoubleClick(SwipeLayout layout,
							boolean surface) {

					}
				});
		// 添加删除布局的点击事件
		v.findViewById(R.id.ll_menu_yaohe).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						data_myh.remove(position);
						notifyDataSetChanged();
						// 点击完成之后，关闭删除menu
						swipeLayout.close();
					}
				});
		return v;
	}

	// 对控件的填值操作独立出来了，我们可以在这个方法里面进行item的数据赋值
	@Override
	public void fillValues(int position, View convertView) {

		MyYhList myYhList = data_myh.get(position);

		ImageView iv_item_myh_shop_image = (ImageView) convertView
				.findViewById(R.id.iv_item_myh_shop_image);

		imageLoader.displayImage("http://static.htcheng.com/imgs/"
				+ myYhList.img1, iv_item_myh_shop_image, options,
				animateFirstListener);

		ImageView iv_item_myh_shop_image_tags = (ImageView) convertView
				.findViewById(R.id.iv_item_myh_shop_image_tags);
		// 0券 1卡 3新品 2活动
		if (!Utils.isStringEmpty(myYhList.type)) {
			String type = myYhList.type;
			if (type.equals("0")) {
				iv_item_myh_shop_image_tags.setVisibility(View.VISIBLE);
				iv_item_myh_shop_image_tags
						.setBackgroundResource(R.drawable.icon_home_type_youhuiquan);
			} else if (type.equals("1")) {
				iv_item_myh_shop_image_tags.setVisibility(View.VISIBLE);
				iv_item_myh_shop_image_tags
						.setBackgroundResource(R.drawable.icon_home_type_huiyuanka);
			} else if (type.equals("2")) {
				iv_item_myh_shop_image_tags.setVisibility(View.VISIBLE);
				iv_item_myh_shop_image_tags
						.setBackgroundResource(R.drawable.icon_home_type_huodong);
			} else if (type.equals("3")) {
				iv_item_myh_shop_image_tags.setVisibility(View.VISIBLE);
				iv_item_myh_shop_image_tags
						.setBackgroundResource(R.drawable.icon_home_type_xinpin);
			} else if (type.equals("4")) {
				iv_item_myh_shop_image_tags.setVisibility(View.GONE);

			}
		}

		TextView tv_item_myh_shop_content = (TextView) convertView
				.findViewById(R.id.tv_item_myh_shop_content);
		tv_item_myh_shop_content.setText(myYhList.content);

		TextView tv_item_myh_shop_time = (TextView) convertView
				.findViewById(R.id.tv_item_myh_shop_time);
		tv_item_myh_shop_time.setText(myYhList.c_id);

		TextView tv_item_myh_zan = (TextView) convertView
				.findViewById(R.id.tv_item_myh_zan);
		tv_item_myh_zan.setText(myYhList.zan_num);

		TextView tv_item_myh_pinlun = (TextView) convertView
				.findViewById(R.id.tv_item_myh_pinlun);
		tv_item_myh_pinlun.setText(myYhList.comment_num);

		TextView tv_item_myh_shoucang = (TextView) convertView
				.findViewById(R.id.tv_item_myh_shoucang);
		tv_item_myh_shoucang.setText(myYhList.collection_num);

	}

	@Override
	public int getCount() {
		return data_myh.size();
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
