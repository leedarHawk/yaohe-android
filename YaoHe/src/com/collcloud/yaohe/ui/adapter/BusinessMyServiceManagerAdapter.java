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
import com.collcloud.yaohe.entity.FourService;
import com.collcloud.yaohe.ui.utils.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 
 * @author 赵洋洋
 * 
 */
public class BusinessMyServiceManagerAdapter extends BaseSwipeAdapter {

	// 上下文对象
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private Context mContext;
	// 数据
	private List<FourService> data_mService;
	private DisplayImageOptions options; // 加载参数
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	// 构造函数
	public BusinessMyServiceManagerAdapter(Context mContext,
			List<FourService> data_mService, DisplayImageOptions options) {

		this.mContext = mContext;
		this.data_mService = data_mService;
		this.options = options;
	}

	// SwipeLayout的布局id
	@Override
	public int getSwipeLayoutResourceId(int position) {
//		return R.id.swipe_fwgl;
		//TODO 删除
		return 0 ;
	}

	@Override
	public View generateView(final int position, ViewGroup parent) {
		View v = LayoutInflater.from(mContext).inflate(
				R.layout.item_business_fwgl_content, parent, false);
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
//		v.findViewById(R.id.ll_menu_fwgl).setOnClickListener(
//				new OnClickListener() {
//
//					@Override
//					public void onClick(View arg0) {
//						data_mService.remove(position);
//						notifyDataSetChanged();
//						// 点击完成之后，关闭删除menu
//						swipeLayout.close();
//					}
//				});
		return v;
	}

	// 对控件的填值操作独立出来了，我们可以在这个方法里面进行item的数据赋值
	@Override
	public void fillValues(int position, View convertView) {

		FourService myFWList = data_mService.get(position);

		TextView tv_item_myfwgl_shop_content = (TextView) convertView
				.findViewById(R.id.tv_item_myfwgl_shop_content);

		TextView tv_item_myfwgl_shop_time = (TextView) convertView
				.findViewById(R.id.tv_item_myfwgl_shop_time);
		tv_item_myfwgl_shop_time.setText(myFWList.addtime);

		TextView tv_item_myfwgl_zan = (TextView) convertView
				.findViewById(R.id.tv_item_myfwgl_zan);
		tv_item_myfwgl_zan.setText(myFWList.zan_num);

		TextView tv_item_myfwgl_pinlun = (TextView) convertView
				.findViewById(R.id.tv_item_myfwgl_pinlun);
		tv_item_myfwgl_pinlun.setText(myFWList.comment_num);

		TextView tv_item_myfwgl_shoucang = (TextView) convertView
				.findViewById(R.id.tv_item_myfwgl_shoucang);
		tv_item_myfwgl_shoucang.setText(myFWList.collection_num);

		ImageView iv_item_myfwgl_shop_image = (ImageView) convertView
				.findViewById(R.id.iv_item_myfwgl_shop_image);

		if (!Utils.isStringEmpty(myFWList.img)) {
			imageLoader.displayImage("http://static.htcheng.com/imgs/"
					+ myFWList.img, iv_item_myfwgl_shop_image, options,
					animateFirstListener);
		}

		ImageView iv_item_myfwgl_shop_image_tags = (ImageView) convertView
				.findViewById(R.id.iv_item_myfwgl_shop_image_tags);
		if (!Utils.isStringEmpty(myFWList.type)) {
			try {
				int type = Integer.valueOf(myFWList.type);
				// 0券 1卡 3新品 2活动
				switch (type) {

				case 0:
					tv_item_myfwgl_shop_content.setText(myFWList.title);
					iv_item_myfwgl_shop_image_tags.setVisibility(View.VISIBLE);
					iv_item_myfwgl_shop_image_tags
							.setBackgroundResource(R.drawable.icon_home_type_youhuiquan);
					break;
				case 1:

					tv_item_myfwgl_shop_content.setText(myFWList.title);
					iv_item_myfwgl_shop_image_tags.setVisibility(View.VISIBLE);
					iv_item_myfwgl_shop_image_tags
							.setBackgroundResource(R.drawable.icon_home_type_huiyuanka);
					break;
				case 3:

					tv_item_myfwgl_shop_content.setText(myFWList.title);
					iv_item_myfwgl_shop_image_tags.setVisibility(View.VISIBLE);
					iv_item_myfwgl_shop_image_tags
							.setBackgroundResource(R.drawable.icon_home_type_xinpin);
					break;
				case 2:

					tv_item_myfwgl_shop_content.setText(myFWList.title);
					iv_item_myfwgl_shop_image_tags.setVisibility(View.VISIBLE);
					iv_item_myfwgl_shop_image_tags
							.setBackgroundResource(R.drawable.icon_home_type_huodong);
					break;
				case 4:

					tv_item_myfwgl_shop_content.setText("暂无吆喝介绍。");
					iv_item_myfwgl_shop_image_tags.setVisibility(View.GONE);
					break;

				default:
					iv_item_myfwgl_shop_image_tags.setVisibility(View.GONE);
					break;
				}

			} catch (NumberFormatException e) {
			}

		}

	}

	@Override
	public int getCount() {
		return data_mService == null ? 0 : data_mService.size();
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
