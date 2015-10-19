/**
 * @file XListView.java
 * @package me.maxwin.view
 * @create Mar 18, 2012 6:28:41 PM
 * @author Maxwin
 * @description An ListView support (a) Pull down to refresh, (b) Pull up to load more.
 * 		Implement IXListViewListener, and see stopRefresh() / stopLoadMore().
 */
package com.collcloud.frame.xlistview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.collcloud.frame.R;

/**
 * @Function:自定义ListView,集合和上拽下拉加载更多功能,后期有什么成熟功能还可继续添加.
 * @author sunny
 * */
@SuppressLint("ResourceAsColor")
public class XListView extends ListView implements OnScrollListener {

	private float mLastY = -1; // save event y

	private Scroller mScroller; // used for scroll back

	// the interface to trigger refresh and load more.
	private IXListViewListener mListViewListener;

	private OnSlidingDirectionListen onSlidingDirectionListen = null;

	// -- header view
	private XListViewHeader mHeaderView;

	// header view content, use it to calculate the Header's height. And hide it
	// when disable pull refresh.
	private RelativeLayout mHeaderViewContent;

	private TextView mHeaderTimeView;

	private int mHeaderViewHeight; // header view's height

	private boolean mEnablePullRefresh = true;

	private boolean mPullRefreshing = false; // is refreashing.

	// -- footer view
	private XListViewFooter mFooterView;

	private boolean mEnablePullLoad;

	private boolean mPullLoading;

	private boolean mIsFooterReady = false;

	// total list items, used to detect is at the bottom of listview.
	private int mTotalItemCount;

	// for mScroller, scroll back from header or footer.
	private int mScrollBack;

	private final static int SCROLLBACK_HEADER = 0;

	private final static int SCROLLBACK_FOOTER = 1;

	private final static int SCROLL_DURATION = 400; // scroll back duration

	private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
														// at bottom, trigger
														// load more.

	private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
													// feature.
	private String TAG = XListView.class.getSimpleName();

	/**
	 * @param context
	 */
	public XListView(Context context) {
		super(context);
		initWithContext(context);
	}

	public XListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public XListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	private void initWithContext(Context context) {
		setVerticalScrollBarEnabled(false);

		mScroller = new Scroller(context, new DecelerateInterpolator());
		// XListView need the scroll event, and it will dispatch the event to
		// user's listener (as a proxy).
		super.setOnScrollListener(this);

		// init header view
		mHeaderView = new XListViewHeader(context);
		mHeaderViewContent = (RelativeLayout) mHeaderView
				.findViewById(R.id.xlistview_header_content);
		mHeaderTimeView = (TextView) mHeaderView
				.findViewById(R.id.xlistview_header_time);
		addHeaderView(mHeaderView);

		// init footer view
		mFooterView = new XListViewFooter(context);

		// init header height
		mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						mHeaderViewHeight = mHeaderViewContent.getHeight();
						getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});
		//if(vp.is)
		//setParentProperty();
	}
	


	@Override
	public void setAdapter(ListAdapter adapter) {
		// make sure XListViewFooter is the last footer view, and only add once.
		if (mIsFooterReady == false) {
			mIsFooterReady = true;
			addFooterView(mFooterView);
			mFooterView.setState(XListViewFooter.STATE_READY);
		}
		super.setAdapter(adapter);
	}

	/**
	 * enable or disable pull down refresh feature.
	 * 
	 * @param enable
	 */
	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh) { // disable, hide the content
			mHeaderViewContent.setVisibility(View.INVISIBLE);
		} else {
			mHeaderViewContent.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 
	 * @Title: setPullLoadEnable
	 * @Description: TODO 设置是否加载更多
	 * @param @param enable 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			forbiddenLoadMore();
			mFooterView.setOnClickListener(null);
		} else {
			mPullLoading = false;
			//setLoadMoreReady();
			mFooterView.setState(XListViewFooter.STATE_READY);
			mFooterView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					startLoadMore();
				}
			});
		}
	}

	/**
	 * 
	 * @Title: stopRefresh
	 * @Description: TODO 停止刷新
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void stopRefresh() {
		if (mPullRefreshing == true) {
			mPullRefreshing = false;
			resetHeaderHeight();
		}
		//LEE
		stopRefreshView();
	}

	/**
	 * 
	 * @Title: stopLoadMore
	 * @Description: TODO 停止加载更多
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void stopLoadMore() {
		if (mPullLoading == true) {
			mPullLoading = false;
		}
		mFooterView.setState(XListViewFooter.STATE_READY);
	}
	/**
	 * 
	 * 
	 * @description 到达最后一页 禁止加载更多
	 * @version 1.0
	 * @author LEE
	 * @date 2015-4-28 下午5:17:57 
	 * @update 2015-4-28 下午5:17:57
	 */
	public void forbiddenLoadMore() {
		mFooterView.setState(XListViewFooter.STATE_FORBIDDEN);
		
	}
/*	private void setLoadMoreReady() {
		mFooterView.setState(XListViewFooter.STATE_READY);
	}*/

	public void setRefreshTime(String time) {
		mHeaderTimeView.setText(time);
	}

	// private void invokeOnScrolling() {
	// if (mScrollListener instanceof OnXScrollListener) {
	// OnXScrollListener l = (OnXScrollListener) mScrollListener;
	// l.onXScrolling(this);
	// }
	// }
	/**
	 * 
	 * @Title: updateHeaderHeight
	 * @Description: TODO 更新顶部刷新状态
	 * @param @param delta 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void updateHeaderHeight(float delta) {
		mHeaderView.setVisiableHeight((int) delta
				+ mHeaderView.getVisiableHeight());
		if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
			if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
				mHeaderView.setState(XListViewHeader.STATE_READY);
			} else {
				mHeaderView.setState(XListViewHeader.STATE_NORMAL);
			}
		}
		setSelection(0);
	}

	/**
	 * 
	 * @Title: resetHeaderHeight
	 * @Description: TODO 重设顶部高度
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void resetHeaderHeight() {
		int height = mHeaderView.getVisiableHeight();
		if (height == 0)
			return;
		if (mPullRefreshing && height <= mHeaderViewHeight) {
			return;
		}
		int finalHeight = 0;
		if (mPullRefreshing && height > mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height,
				SCROLL_DURATION);
		invalidate();
	}

	/**
	 * 
	 * @Title: updateFooterHeight
	 * @Description: TODO 更新底部加载更多高度
	 * @param @param delta 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void updateFooterHeight(float delta) {
		int height = mFooterView.getBottomMargin() + (int) delta;
		if (mEnablePullLoad && !mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) {
				mFooterView.setState(XListViewFooter.STATE_READY);
			} else {
				mFooterView.setState(XListViewFooter.STATE_READY);
			}
		}
		mFooterView.setBottomMargin(height);
	}

	/**
	 * 
	 * @Title: resetFooterHeight
	 * @Description: TODO 重设底部加载更多高度
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
					SCROLL_DURATION);
			invalidate();
		}
	}

	/**
	 * 
	 * @Title: startLoadMore
	 * @Description: TODO 开始加载更多
	 * @param 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	private void startLoadMore() {
		if(mPullLoading) {
			return;
		}
		mPullLoading = true;
		mFooterView.setState(XListViewFooter.STATE_LOADING);
		if (mListViewListener != null) {
			mListViewListener.onLoadMore();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}

		switch (ev.getAction()) {

		case MotionEvent.ACTION_POINTER_1_DOWN:
		case MotionEvent.ACTION_POINTER_2_DOWN:
		case MotionEvent.ACTION_POINTER_3_DOWN:
			break;
		case MotionEvent.ACTION_POINTER_1_UP:
		case MotionEvent.ACTION_POINTER_2_UP:
		case MotionEvent.ACTION_POINTER_3_UP:
			break;

		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;

			if (deltaY > 0) {// mListViewOnTouchEvent
				if (onSlidingDirectionListen != null)
					onSlidingDirectionListen.onScrollUpWard(Math.abs(deltaY));
			} else {
				if (onSlidingDirectionListen != null)
					onSlidingDirectionListen.onScrollDownWard(Math.abs(deltaY));
			}

			mLastY = ev.getRawY();
			if (getFirstVisiblePosition() == 0
					&& (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) {
				// the first item is showing, header has shown or pull down.
				updateHeaderHeight(deltaY / OFFSET_RADIO);
				// invokeOnScrolling();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1
					&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
				// last item, already pulled up or want to pull up.
				updateFooterHeight(-deltaY / OFFSET_RADIO);
			}
			break;
		default:
			mLastY = -1; // reset
			if (getFirstVisiblePosition() == 0) {
				// invoke refresh
				if (mEnablePullRefresh
						&& mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
					mPullRefreshing = true;
					mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
					if (mListViewListener != null) {
						mListViewListener.onRefresh();
					}
				}
				resetHeaderHeight();
			} else if (getLastVisiblePosition() == mTotalItemCount - 1) {
				// invoke load more.
				if (mEnablePullLoad
						&& mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
					startLoadMore();
				}
				resetFooterHeight();
			}

			break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				mHeaderView.setVisiableHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
			// invokeOnScrolling();
		}
		super.computeScroll();
	}

	/** XListView 是否在底部 */
	private boolean isListViewBottom = false;

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mTotalItemCount = totalItemCount;
		if (((firstVisibleItem + visibleItemCount) == totalItemCount)
				&& (totalItemCount > 0)) {
			isListViewBottom = true;
		}
	}
//	if (lv.getLastVisiblePosition() == (lv.getCount() - 1)) {
//    }
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			if (isListViewBottom){
				if(view.getLastVisiblePosition() == (view.getCount()-1)){
					//TODO:底部
					if(onSlidingDirectionListen != null) {
						onSlidingDirectionListen.onScrollBottom();
						if(!mPullLoading) {
							startLoadMore();
						}
					}
					
				}else if(view.getFirstVisiblePosition() == 0){
					//TODO:顶部
					if(onSlidingDirectionListen != null)
					onSlidingDirectionListen.onScrollTop();
				}
			}
		} else if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
		}

	}

	public void setXListViewListener(IXListViewListener l) {
		mListViewListener = l;
		isSwipeRefreshLayoutAsParent();
		
	}

	/**
	 * you can listen ListView.OnScrollListener or this one. it will invoke
	 * onXScrolling when header/footer scroll back.
	 */
	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	/**
	 * 
	 * @ClassName: IXListViewListener
	 * @Description: TODO 上拉下拽监听
	 * @author Sunny
	 * @date 2015-3-19 下午4:59:37
	 * 
	 */
	public interface IXListViewListener {
		public void onRefresh();

		public void onLoadMore();
	}

	/**
	 * 
	 * @Title: setOnSlidingDirectionListen
	 * @Description: TODO 设置滚动方向监听
	 * @param @param l 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void setOnSlidingDirectionListen(OnSlidingDirectionListen l) {
		onSlidingDirectionListen = l;
	}

	/**
	 * 
	 * @ClassName: OnSlidingDirectionListen
	 * @Description: TODO 滑动方向监听
	 * @author Sunny
	 * @date 2015-3-19 下午4:57:46
	 * 
	 */
	public interface OnSlidingDirectionListen {
		/**
		 * 
		 * @Title: onScrollUpWard
		 * @Description: TODO 向上滑动
		 * @param @param value 设定文件
		 * @return void 返回类型
		 * @throws
		 */
		public void onScrollUpWard(float value);

		/**
		 * 
		 * @Title: onScrollDownWard
		 * @Description: TODO 向下滑动
		 * @param @param value 设定文件
		 * @return void 返回类型
		 * @throws
		 */
		public void onScrollDownWard(float value);

		/**
		 * 
		 * @Title: onScrollTop
		 * @Description: TODO 滚动顶部
		 * @param 设定文件
		 * @return void 返回类型
		 * @throws
		 */
		public void onScrollTop();

		/**
		 * 
		 * @Title: onScrollBottom
		 * @Description: TODO 滚动到底部
		 * @param 设定文件
		 * @return void 返回类型
		 * @throws
		 */
		public void onScrollBottom();

	}
	
	
	//通过父布局进行刷新///////////////////////////////////////////////////////////////
	private SwipeRefreshLayout mSwipeLayout;
	private void setRefreshView(SwipeRefreshLayout mSwipeLayout) {
		this.mSwipeLayout = mSwipeLayout;
		mSwipeLayout.setOnRefreshListener(new OnRefreshListener() {
		@Override
		public void onRefresh() {
			if (mListViewListener != null) {
				mListViewListener.onRefresh();
			}
		}
	});
	// 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
	/*mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
			android.R.color.holo_orange_light, android.R.color.holo_red_light);*/
	mSwipeLayout.setColorSchemeResources(R.color.cirucular_progress_bar_color);
	mSwipeLayout.setDistanceToTriggerSync(150);// 设置手指在屏幕下拉多少距离会触发下拉刷新
	mSwipeLayout.setProgressBackgroundColor(R.color.cirucular_progress_bar_bg_color);
	mSwipeLayout.setProgressViewOffset(true, 0, 150); 
	mSwipeLayout.setSize(SwipeRefreshLayout.LARGE);
	setPullRefreshEnable(false);
	}
	/**
	 * 
	 * 
	 * @description 停止刷新
	 * @version 1.0
	 * @author LEE
	 * @date 2015-4-27 下午7:31:36 
	 * @update 2015-4-27 下午7:31:36
	 */
	private void stopRefreshView() {
		if(mSwipeLayout != null) {
			mSwipeLayout.setRefreshing(false);
		}
		
	}
	public void startRefreshView() {
		if(mSwipeLayout != null) {
			mSwipeLayout.setRefreshing(true);
		}
	}
	/**
	 * 
	 * @return
	 * @description 检测listview的父布局是否为SwipeRefreshLayout如果是则用父布局的下拉刷新
	 * @version 1.0
	 * @author LEE
	 * @date 2015-4-28 上午10:31:18 
	 * @update 2015-4-28 上午10:31:18
	 */
	private boolean isSwipeRefreshLayoutAsParent() {
		ViewParent vp = this.getParent();
		if(vp !=null && vp instanceof SwipeRefreshLayout) {
			setRefreshView((SwipeRefreshLayout)vp);
			return true;
		}
		return false;
	}
	//通过父布局进行刷新///////////////////////////////////////////////////////////////

}
