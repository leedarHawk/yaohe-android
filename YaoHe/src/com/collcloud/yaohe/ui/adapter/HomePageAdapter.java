package com.collcloud.yaohe.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.ui.adapter.HomeAdapter.OnButtonClickListener;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.utils.Utils;

public class HomePageAdapter extends PagerAdapter {

	@SuppressWarnings("unused")
	private Context mContext;
	private List<String> mImgPaths;
	private LayoutInflater mInflater;

	private int mScreenWidth;
	@SuppressWarnings("unused")
	private int mScreenHeight;
	private int size;

	private static ImageLoader mImageLoader;

	public HomePageAdapter(Context context) {
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		Point screenMetris = Utils.getScreenMetrics(context);
		mScreenWidth = screenMetris.x;
		mScreenHeight = screenMetris.y;
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache()); // 初始化一个loader对象，可以进行自定义配置
	}

	public HomePageAdapter(Context context, List<String> imgPath) {
		this.mContext = context;
		if (mImgPaths == null) {
			mImgPaths = new ArrayList<String>();
		}
		this.mImgPaths = imgPath;
		mInflater = LayoutInflater.from(context);
		Point screenMetris = Utils.getScreenMetrics(context);
		mScreenWidth = screenMetris.x;
		mScreenHeight = screenMetris.y;
		size = imgPath.size();
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache()); // 初始化一个loader对象，可以进行自定义配置
	}

	@Override
	public int getCount() {
		if (mImgPaths != null && mImgPaths.size() > 0) {
			return mImgPaths.size();
		} else {
			// return 0;
			return Integer.MAX_VALUE;
		}

	}

	@Override
	public int getItemPosition(Object object) {
		if (size > 0) {
			size--;
			return POSITION_NONE;
		}
		return super.getItemPosition(object);
	}

	public void refreshData(List<String> imgPaths) {
		this.mImgPaths = imgPaths;
		notifyDataSetChanged();

	}

	public interface OnPagerItemClickListener {
		void onPagerItemClick(int position);

	}

	private OnPagerItemClickListener mOnItemClickListener = null;

	public void setOnPagerItemClick(OnPagerItemClickListener listener) {
		this.mOnItemClickListener = listener;
	}

	@Override
	public Object instantiateItem(ViewGroup view, final int position) {
		View imageLayout = mInflater.inflate(R.layout.pic_item_pager_layout,
				view, false);

		ImageView imageView = (ImageView) imageLayout
				.findViewById(R.id.iv_pager_adapter_item_img);

		if (mImgPaths != null && mImgPaths.size() > 0) {

			ImageListener listener = ImageLoader.getImageListener(imageView,
					R.drawable.icon_yaohe_loading_default,
					R.drawable.icon_yaohe_loading_default);
			// imageLoader.get(mImgPaths.get(position), listener);
			//http://static.htcheng.com/imgs/2015-10-08/5615d6468cfad869_thumb.jpg
			String imgUrl = mImgPaths.get(position);
			try {
				if(imgUrl !=null && imgUrl.indexOf("_thumb")>-1) {
					String [] array = imgUrl.split("_thumb");
					imgUrl = array[0]+array[1];
					
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			mImageLoader.get(imgUrl, listener, mScreenWidth,
					200);

			((ViewPager) view).addView(imageLayout, 0);
			imageView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mOnItemClickListener != null) {
						mOnItemClickListener.onPagerItemClick(position);
					}

				}
			});
			return imageLayout;
		}

		return imageLayout;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {

		if (mImgPaths != null && mImgPaths.size() > 0) {

			((ViewPager) container).removeView((View) object);
			((View) object).destroyDrawingCache();
			return;
		}
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == (arg1);
	}

}
