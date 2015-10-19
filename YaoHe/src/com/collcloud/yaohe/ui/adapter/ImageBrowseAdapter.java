package com.collcloud.yaohe.ui.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.photoview.PhotoView;
import com.collcloud.yaohe.ui.utils.Utils;

/**
 * 加载网络图片显示-ViewPager适配
 * 
 * @ClassName ImageBrowseAdapter
 * @author CollCloud_小米
 */
public class ImageBrowseAdapter extends PagerAdapter {

	private LayoutInflater inflater;
	private Context mContext;
	private List<String> mListPath;
	private int mScreenWidth;
	private int mScreenHeight;

	public ImageBrowseAdapter(Context context, List<String> paths) {
		this.mContext = context;
		inflater = LayoutInflater.from(context);
		this.mListPath = paths;
		Point screenMetris = Utils.getScreenMetrics(context);
		mScreenWidth = screenMetris.x;
		mScreenHeight = screenMetris.y;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

	@Override
	public int getCount() {
		return mListPath.size();
	}

	@Override
	public Object instantiateItem(ViewGroup view, final int position) {
		View imageLayout = inflater.inflate(
				R.layout.pic_item_fullscreen_layout, view, false);
		PhotoView imageView = (PhotoView) imageLayout
				.findViewById(R.id.photo_view_adapter_pic);

		// 如果传过来的是磁盘路径用下面的方式
		// imageView.setImageBitmap(PictureUtils.getBitmapByPath(imgList.get(position)));

		RequestQueue mQueue = Volley.newRequestQueue(mContext);
		ImageLoader imageLoader = new ImageLoader(mQueue, new BitmapCache());

		ImageListener listener = ImageLoader.getImageListener(imageView,
				R.drawable.icon_yaohe_loading_default,
				R.drawable.icon_yaohe_loading_default);
		imageLoader.get(mListPath.get(position), listener);

		((ViewPager) view).addView(imageLayout, 0);
		return imageLayout;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

}