package com.collcloud.yaohe.photo.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Bimp {
	public  static int max = 0;
	public static ArrayList<ImageItem> tempSelectBitmap = new ArrayList<ImageItem>();   //选择的图片的临时列表
	//public  ArrayList<ImageItem> tempSelectBitmap2;   //选择的图片的临时列表
	public Bimp() {
		//tempSelectBitmap2 = new ArrayList<ImageItem>();
	}
	
	

	public  static Bitmap revitionImageSize(String path) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 1000)
					&& (options.outHeight >> i <= 1000)) {
				in = new BufferedInputStream(
						new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}
	/**
	 * 
	 * @param path 图片路径
	 * @param width 最小宽度
	 * @param height 最小高度
	 * @return
	 * @description 获取图片
	 * @version 1.0
	 * @author LEE
	 * @date 2015年10月3日 下午9:50:26 
	 * @update 2015年10月3日 下午9:50:26
	 */
	public  static Bitmap getBitmapFromFile(String path, int width, int height) {
		try {
			File dst = new File(path);
		    if (null != dst && dst.exists()) {
		        BitmapFactory.Options opts = null;
		        if (width > 0 && height > 0) {
		            opts = new BitmapFactory.Options();
		            opts.inJustDecodeBounds = true;
		            BitmapFactory.decodeFile(dst.getPath(), opts);
		            // 计算图片缩放比例
		            final int minSideLength = Math.min(width, height);
		            opts.inSampleSize = computeSampleSize(opts, minSideLength,
		                    width * height);
		            opts.inJustDecodeBounds = false;
		            opts.inInputShareable = true;
		            opts.inPurgeable = true;
		        }
		        try {
		            return BitmapFactory.decodeFile(dst.getPath(), opts);
		        } catch (OutOfMemoryError e) {
		            e.printStackTrace();
		        }
		    }
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	    return null;
	}
	
	
	public static int computeSampleSize(BitmapFactory.Options options,
	        int minSideLength, int maxNumOfPixels) {
	    int initialSize = computeInitialSampleSize(options, minSideLength,
	            maxNumOfPixels);

	    int roundedSize;
	    if (initialSize <= 8) {
	        roundedSize = 1;
	        while (roundedSize < initialSize) {
	            roundedSize <<= 1;
	        }
	    } else {
	        roundedSize = (initialSize + 7) / 8 * 8;
	    }

	    return roundedSize;
	}
	
	
	private  static int computeInitialSampleSize(BitmapFactory.Options options,
	        int minSideLength, int maxNumOfPixels) {
	    double w = options.outWidth;
	    double h = options.outHeight;

	    int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
	            .sqrt(w * h / maxNumOfPixels));
	    int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
	            .floor(w / minSideLength), Math.floor(h / minSideLength));

	    if (upperBound < lowerBound) {
	        // return the larger one when there is no overlapping zone.
	        return lowerBound;
	    }

	    if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
	        return 1;
	    } else if (minSideLength == -1) {
	        return lowerBound;
	    } else {
	        return upperBound;
	    }
	}
}
