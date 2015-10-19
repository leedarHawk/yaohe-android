/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.collcloud.frame.viewpager.main;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.collcloud.frame.R;
import com.collcloud.frame.viewpager.PagerSlidingTabStrip;

public class PagerTabMainActivity extends FragmentActivity {

	private final Handler handler = new Handler();

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;

	private Drawable oldBackground = null;
	private int currentColor = 0xFF666666;
	private static String tag = PagerTabMainActivity.class.getSimpleName();
	
	SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab_pagertab__main);

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new MyPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
		
		//initView();
//		viewPagerAdapter = new  ViewPagerAdapter(lists);
//		pager.setAdapter(viewPagerAdapter);
		
		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
				.getDisplayMetrics());
		pager.setPageMargin(pageMargin);
		

		tabs.setViewPager(pager);
		
		tabs.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				/*initListView(position);
				clickTitleItem(position);*/
				Log.i(tag, "onPageSelected position is:"+position);
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});

		changeColor(currentColor);
		
		
	}





	private void changeColor(int newColor) {

		tabs.setIndicatorColor(newColor);

		// change ActionBar color just if an ActionBar is available
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

			Drawable colorDrawable = new ColorDrawable(newColor);
			Drawable bottomDrawable = getResources().getDrawable(R.drawable.tab_pagertab_actionbar_bottom);
			LayerDrawable ld = new LayerDrawable(new Drawable[] { colorDrawable, bottomDrawable });

			if (oldBackground == null) {

				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
					ld.setCallback(drawableCallback);
				} else {
					//getActionBar().setBackgroundDrawable(ld);
				}

			} else {

				TransitionDrawable td = new TransitionDrawable(new Drawable[] { oldBackground, ld });

				// workaround for broken ActionBarContainer drawable handling on
				// pre-API 17 builds
				// https://github.com/android/platform_frameworks_base/commit/a7cc06d82e45918c37429a59b14545c6a57db4e4
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
					td.setCallback(drawableCallback);
				} else {
					//getActionBar().setBackgroundDrawable(td);
				}

				td.startTransition(200);

			}

			oldBackground = ld;

			// http://stackoverflow.com/questions/11002691/actionbar-setbackgrounddrawable-nulling-background-from-thread-handler
//			getActionBar().setDisplayShowTitleEnabled(false);
//			getActionBar().setDisplayShowTitleEnabled(true);

		}

		currentColor = newColor;

	}

	public void onColorClicked(View v) {

		int color = Color.parseColor(v.getTag().toString());
		changeColor(color);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("currentColor", currentColor);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		currentColor = savedInstanceState.getInt("currentColor");
		changeColor(currentColor);
	}

	private Drawable.Callback drawableCallback = new Drawable.Callback() {
		@Override
		public void invalidateDrawable(Drawable who) {
			//getActionBar().setBackgroundDrawable(who);
		}

		@Override
		public void scheduleDrawable(Drawable who, Runnable what, long when) {
			handler.postAtTime(what, when);
		}

		@Override
		public void unscheduleDrawable(Drawable who, Runnable what) {
			handler.removeCallbacks(what);
		}
	};

	public class MyPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "Categories", "Home", "Top Paid", "Top Free", "Top Grossing", "Top New Paid",
				"Top New Free", "Trending","lajdfljaslfjlasjf"};

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			Log.i(tag, "getItem position is:"+position);
			
			
			final Fragment result;
	        switch (position) {
	            case 0:
	                // First Fragment of First Tab
	            	Log.i(tag, "create SuperAwesomeCardFragment  posotion:"+position);
	                result = SuperAwesomeCardFragment.newInstance(position);
	                break;
	            case 1:
	                // First Fragment of Second Tab
	            	Log.i(tag, "create Fragment1  posotion:"+position);
	                result = Fragment1.newInstance(position);
	                break;
	            default:
	            	Log.i(tag, "create Fragment2  posotion:"+position);
	            	result = Fragment2.newInstance(position);
	                break;
	        }
			return result;
		}
		@Override
	    public void destroyItem(ViewGroup container, int position, Object object) {
	        registeredFragments.remove(position);
			Log.i(tag, "destroyItem position is:"+position);
	        super.destroyItem(container, position, object);
	    }
		
		@Override
	    public Object instantiateItem(ViewGroup container, int position) {
			Log.i(tag, "instantiateItem position is:"+position);
	        Fragment fragment = (Fragment) super.instantiateItem(container, position);
	        registeredFragments.put(position, fragment);
	        return fragment;
	    }
		 /**
	     * Get the Fragment by position
	     *
	     * @param position tab position of the fragment
	     * @return
	     */
	    public Fragment getRegisteredFragment(int position) {
	        return registeredFragments.get(position);
	    }

	}
	
	///////////////////////////////////////////////////////////////////////////
//	private ViewPagerAdapter viewPagerAdapter;
//	private final String[] TITLES = { "Categories_1", "Home_2", "Top Paid_3", "Top Free_4", "Top Grossing_5", "Top New Paid_6",
//			"Top New Free_7", "Trending_8","lajdfljaslfjlasjf_9","FFFFFFFF_10"};
//	private void initView() {
//		
//		for(int i=0;i<10;i++) {
//			View view = getLayoutInflater().inflate(R.layout.activity_main, null);
//			lists.add(view);
//		}
//		
//		
//	}
//	
//	
//	 private List<View> lists = new ArrayList<View>();  
//    public class ViewPagerAdapter extends PagerAdapter{  
//        
//        List<View> viewLists;  
//		
//          
//        public ViewPagerAdapter(List<View> lists)  
//        {  
//            viewLists = lists;  
//        }  
//      
//        @Override  
//        public int getCount() {                                                                 //????????????size  
//            // TODO Auto-generated method stub  
//            return viewLists.size();  
//        }  
//      
//        @Override  
//        public boolean isViewFromObject(View arg0, Object arg1) {                           
//            // TODO Auto-generated method stub  
//            return arg0 == arg1;  
//        }  
//          
//        @Override  
//        public void destroyItem(View view, int position, Object object)                       //????????????Item  
//        {  
//            ((ViewPager) view).removeView(viewLists.get(position));  
//        }  
//          
//        @Override  
//        public Object instantiateItem(View view, int position)                                //??????????????????Item  
//        {  
//        	/*try {
//        		((ViewPager) view).removeView(viewLists.get(position-2));
//        	} catch(Exception e) {
//        		e.printStackTrace();
//        	}*/
//        	
//        	
//            ((ViewPager) view).addView(viewLists.get(position), 0);  
//            
//            View view1 = viewLists.get(position);  
//            TextView textview = (TextView) view1.findViewById(R.id.textID);
//            textview.setText("card==="+position);
//              
//            return view1;
//        }  
//        
//        @Override
//        public CharSequence getPageTitle(int position) {
//        	return TITLES[position];
//        }
//          
//    }  
	

}