package com.collcloud.yaohe.ui.photoview;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.ui.utils.CCLog;

public class BitmapCache implements ImageCache {

	private static LruCache<String, Bitmap> cache;
	private static String LOCK = "lock";
	public BitmapCache() {
		synchronized (LOCK) {
			if(cache == null) {
				CCLog.d("BitmapCache", "cache is  null");
				//int maxcache = AppApplacation.memoryCacheSize/8;
				
				cache = new LruCache<String, Bitmap>(50 * 1024 * 1024) {
					@Override
					protected int sizeOf(String key, Bitmap bitmap) {
						return bitmap.getRowBytes() * bitmap.getHeight();
					}
				};
			} else {
				CCLog.d("BitmapCache", "cache is not null");
			}
		}
		
		
	}

	@Override
	public Bitmap getBitmap(String url) {
		return cache.get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		cache.put(url, bitmap);
	}
}