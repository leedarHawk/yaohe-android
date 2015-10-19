package com.collcloud.yaohe.activity.details.yaohela;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.photoview.PhotoView;

public class PicFullScreenShowActivity extends Activity implements
ViewPager.OnPageChangeListener{

	private ImagePagerAdapter pagerAdapter;
	private ViewPager viewPager;

	private ImageView[] imageArr;
	private LinearLayout layout;
	int currentIndex = 0;
	private List<String> imgList = new ArrayList<String>();
	private static ImageLoader mImageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.fullscreen_pic_pager);

		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
		// TODO 
		imgList = (List<String>) this.getIntent().getSerializableExtra("imgPath");
//		imgList.add("http://pic38.nipic.com/20140228/5571398_215900721128_2.jpg");
//		imgList.add("http://pic.nipic.com/2008-04-01/20084113367207_2.jpg");
//		imgList.add("http://pic27.nipic.com/20130227/4131239_232632474002_2.jpg");
		
		currentIndex = this.getIntent().getIntExtra("startIndex", 0);
		if (currentIndex < 0 || currentIndex >= imgList.size()) {
			currentIndex = 0;
		}
		initView();
	}

	private void initView() {
		this.viewPager = (ViewPager) ((ViewPager) findViewById(R.id.hacky_viewpager));

		pagerAdapter = new ImagePagerAdapter(this);
		viewPager.setAdapter(pagerAdapter);
		viewPager.setCurrentItem(currentIndex);
		viewPager.setOnPageChangeListener(this);
		this.layout = (LinearLayout) ((LinearLayout) findViewById(R.id.view_idx_layout));

		imageArr = new ImageView[imgList.size()];

		for (int i = 0; i < this.imgList.size(); i++) {
			this.imageArr[i] = new ImageView(this);
			LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(
					-2, -2);
			localLayoutParams.gravity = 16;
			int padding = getResources().getDimensionPixelOffset(
					R.dimen.common_button_corners);
			localLayoutParams.bottomMargin = getResources()
					.getDimensionPixelOffset(R.dimen.common_button_corners);
			this.imageArr[i].setImageResource(R.drawable.startup_point_sec);
			this.imageArr[i].setLayoutParams(localLayoutParams);
			this.imageArr[i].setPadding(padding, padding, padding, padding);
			this.layout.addView(this.imageArr[i]);
			this.imageArr[i].setEnabled(true);
			// this.imageArr[i].setOnClickListener(this);
			this.imageArr[i].setTag(Integer.valueOf(i));
		}
		this.imageArr[this.currentIndex].setEnabled(false);
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private LayoutInflater inflater;
		private Context mContext;

		ImagePagerAdapter(Context context) {
			this.mContext = context;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public int getCount() {
			return imgList.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(
					R.layout.pic_item_fullscreen_layout, view, false);
			PhotoView imageView = (PhotoView) imageLayout
					.findViewById(R.id.photo_view_adapter_pic);
			
//			try {
//				InputStream in = mContext.getAssets().open(imgList.get(position));
//				Bitmap bitmap = BitmapFactory.decodeStream(in);
//				imageView.setImageBitmap(bitmap);
//				in.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
			ImageListener listener = ImageLoader.getImageListener(imageView,
					R.drawable.icon_yaohe_loading_default,
					R.drawable.icon_yaohe_loading_default);
			mImageLoader.get(imgList.get(position), listener);
			
			//如果传过来的是磁盘路径用下面的方式
			//imageView.setImageBitmap(PictureUtils.getBitmapByPath(imgList.get(position)));

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

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int paramInt) {
		if (paramInt < 0||imageArr==null)
			return;
		this.imageArr[paramInt].setEnabled(false);
		this.imageArr[this.currentIndex].setEnabled(true);
		this.currentIndex = paramInt;
	}
}
