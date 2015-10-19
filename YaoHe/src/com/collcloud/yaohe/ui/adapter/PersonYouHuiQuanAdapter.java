package com.collcloud.yaohe.ui.adapter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.entity.YhqInfoList;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 我的(个人)优惠券适配器
 * 
 * @ClassName: MainActivity
 * @author 赵洋洋
 */
public class PersonYouHuiQuanAdapter extends BaseAdapter {

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	Context context;
	List<YhqInfoList> data_usedYhq;
	LayoutInflater layout;
	private DisplayImageOptions options; // 加载参数
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	// 构造传参
	public PersonYouHuiQuanAdapter(Context context, List<YhqInfoList> data,
			DisplayImageOptions options) {

		this.context=context;
		this.data_usedYhq=data;
		this.options = options;
	}

	@Override
	public int getCount() {

		return data_usedYhq.size();
	}

	@Override
	public Object getItem(int arg0) {

		return null;
	}

	@Override
	public long getItemId(int arg0) {

		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ViewHolder vHolder;
		// 获取布局
		View view = arg1;
		if (view == null) {
			vHolder = new ViewHolder();
			view = layout.inflate(R.layout.item_youhuiquan_list, null);
			// 获取布局中的组件
			vHolder.im_youhuiquan_shop_photo = (ImageView) view
					.findViewById(R.id.im_youhuiquan_shop_photo);
			vHolder.tv_youhuiquan_shop_name = (TextView) view
					.findViewById(R.id.tv_youhuiquan_shop_name);
			vHolder.tv_youhuiquan_info = (TextView) view
					.findViewById(R.id.tv_youhuiquan_info);
			vHolder.tv_youhuiquan_totime = (TextView) view
					.findViewById(R.id.tv_youhuiquan_totime);
			vHolder.tv_youhuiquan_num = (TextView) view
					.findViewById(R.id.tv_youhuiquan_num);
			// 绑定数据
			view.setTag(vHolder);

		} else {
			vHolder = (ViewHolder) view.getTag();
		}

		YhqInfoList used_yhq = data_usedYhq.get(arg0);
		
		vHolder.tv_youhuiquan_num.setText(used_yhq.use_number);
		vHolder.tv_youhuiquan_totime.setText(used_yhq.valid_end);
		vHolder.tv_youhuiquan_info.setText(used_yhq.title);
		vHolder.tv_youhuiquan_shop_name.setText(used_yhq.shop_name);

		imageLoader.displayImage("http://static.htcheng.com/imgs/"
				+ used_yhq.img1, vHolder.im_youhuiquan_shop_photo, options,
				animateFirstListener);

		return view;
	}

	class ViewHolder {

		private TextView tv_youhuiquan_num;
		private TextView tv_youhuiquan_totime;
		private TextView tv_youhuiquan_info;
		private TextView tv_youhuiquan_shop_name;
		private ImageView im_youhuiquan_shop_photo;// 相片

	}

	/**
	 * @类说明 图片加载
	 * @author 赵洋洋
	 * 
	 */
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
