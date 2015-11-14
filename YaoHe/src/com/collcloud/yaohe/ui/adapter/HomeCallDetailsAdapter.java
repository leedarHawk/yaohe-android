package com.collcloud.yaohe.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.api.info.DetailsCallCommentInfo.CallCommentInfo;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.utils.Utils;
import com.meg7.widget.CustomShapeImageView;

public class HomeCallDetailsAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mLayoutInflater = null;

	private static ImageLoader mImageLoader;
	private String mStrMemberId ;

	private List<CallCommentInfo> mCallCommentList = new ArrayList<CallCommentInfo>();
	
	private CommentHuifuListener commentHuifuListener;
	public interface CommentHuifuListener {
		public void onCommentForHuifu(CallCommentInfo callCommentInfo);
		public void onDelComment(CallCommentInfo callCommentInfo);
	}

	public HomeCallDetailsAdapter(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
	}

	public HomeCallDetailsAdapter(Context context, List<CallCommentInfo> datas,String memberId) {
		this.mContext = context;
		this.mCallCommentList = datas;
		this.mStrMemberId = memberId;
		mLayoutInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
	}
	public HomeCallDetailsAdapter(Context context, List<CallCommentInfo> datas,String memberId,CommentHuifuListener commentHuifuListener) {
		this.mContext = context;
		this.mCallCommentList = datas;
		this.mStrMemberId = memberId;
		mLayoutInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
		this.commentHuifuListener = commentHuifuListener;
	}
	

	@Override
	public int getCount() {
		return mCallCommentList == null ? 0 : mCallCommentList.size();
	}

	@Override
	public Object getItem(int position) {
		return mCallCommentList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	// public interface OnBusinessInfoListener {
	// void onBusinessInfoClick(int position,String type, String serviceId);
	// }
	//
	// private OnBusinessInfoListener mOnItemClickListener = null;
	//
	// public void setOnHomeItemClickListener(OnBusinessInfoListener listener) {
	// this.mOnItemClickListener = listener;
	// }

	public void refresh(List<CallCommentInfo> data) {
		this.mCallCommentList = data;
		notifyDataSetChanged();
	}

	@SuppressLint({ "InflateParams", "ClickableViewAccessibility",
			"SimpleDateFormat" })
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		DetailsCallContent holder;
		CallCommentInfo callInfo = null;
		if (mCallCommentList != null && mCallCommentList.size() > 0) {
			callInfo = mCallCommentList.get(position);
		}
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(
					R.layout.item_home_call_comment_details, null);

			holder = new DetailsCallContent();
			resetLayout(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (DetailsCallContent) convertView.getTag();
		}

		// 设定首页吆喝信息
		ImageListener listener = ImageLoader.getImageListener(holder.mIvThum,
				R.drawable.icon_yaohe_default_logo,
				R.drawable.icon_yaohe_default_logo);
		

		if (callInfo != null && callInfo.content != null) {
			holder.mTvContent.setText(callInfo.content);
		}
		if (callInfo != null && callInfo.is_anonymous != null) {
			if (callInfo.is_anonymous.equals("1")) {
				holder.mTvName.setText("匿名用户");
			}else {
				if (!Utils.isStringEmpty(callInfo.nickname)) {
					holder.mTvName.setText(callInfo.nickname);
				}else {
					holder.mTvName.setText("吆喝用户");
				}
				
				// if (title.length() == 11) {
				// try {
				// String pre = title.substring(0, 3);
				// String after = title.substring(8, 11);
				// holder.mTvName.setText(pre+"*****"+after);
				// } catch (ArrayIndexOutOfBoundsException e) {
				// holder.mTvName.setText(title);
				// }
				// }else {
				// holder.mTvName.setText("吆喝用户");
				// }
				
			}
			//如果是回复的情况
			if(callInfo.parentid != null && !"".equals(callInfo.parentid) && !"0".equals(callInfo.parentid) ) {
				String answerName="吆喝用户";
				if(callInfo.answerName!= null && !"".equals(callInfo.answerName)) {
					answerName = callInfo.answerName;
				}
				if (callInfo.is_anonymous.equals("1")) {
					holder.mTvName.setText("匿名用户 回复 " +answerName);
				}else {
					if (!Utils.isStringEmpty(callInfo.nickname)) {
						holder.mTvName.setText(callInfo.nickname+" 回复 "+answerName);
					}else {
						holder.mTvName.setText("吆喝用户1"+" 回复 "+answerName);
					}
				}
			}
		}
		if (callInfo != null && callInfo.addtime != null) {
			holder.mTvDate.setVisibility(View.VISIBLE);
			holder.mTvDate.setText(callInfo.addtime);
			
			// 确保long型日期/时间值是正确的，比如检测长度，是否少了最后的毫秒数
			// if (callInfo.addtime.length() == 10) {
			// callInfo.addtime = callInfo.addtime + "000";
			// }
			// try {
			// SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");
			// Date date = new Date(Long.valueOf(callInfo.addtime));
			// holder.mTvDate.setText(df.format(date));
			// } catch (NumberFormatException e) {
			// e.printStackTrace();
			// holder.mTvDate.setText(callInfo.addtime);
			// }
		} else {
			holder.mTvDate.setVisibility(View.GONE);
		}

		if (callInfo != null && callInfo.face != null) {
			//mImageLoader.get(callInfo.face, listener);
			mImageLoader.get(callInfo.face, listener, mContext.getResources().getDimensionPixelSize(R.dimen.photo_width), mContext.getResources().getDimensionPixelSize(R.dimen.photo_height));
		}
		
		holder.commentBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(commentHuifuListener !=null) {
					commentHuifuListener.onCommentForHuifu(mCallCommentList.get(position));
				}
			}
		});
		holder.delCommentBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(commentHuifuListener !=null) {
					commentHuifuListener.onDelComment(mCallCommentList.get(position));
				}
			}
		});
		
		//是自己的评论 能够删除
		if(mStrMemberId !=null && mStrMemberId.equals(callInfo.member_id)) {
			holder.delCommentBtn.setVisibility(View.VISIBLE);
		} else {//不是自己的评论 就不能删除
			holder.delCommentBtn.setVisibility(View.INVISIBLE);
		}
		
		

		return convertView;
	}

	private class DetailsCallContent {

		TextView mTvName;
		TextView mTvContent;
		RelativeLayout mRlBusinessContent;
		/** 日期时间 */
		TextView mTvDate;
		/** 列表简介图片 */
		CustomShapeImageView mIvThum;
		//评论按钮
		TextView commentBtn;
		//删除评论
		TextView delCommentBtn;

	}

	private void resetLayout(DetailsCallContent holder, View view) {

		LinearLayout rlLayout = (LinearLayout) view
				.findViewById(R.id.ll_item_home_call_comment_root);
		SupportDisplay.resetAllChildViewParam(rlLayout);
		holder.mTvName = (TextView) view
				.findViewById(R.id.tv_item_home_call_comment_name);
		holder.mTvContent = (TextView) view
				.findViewById(R.id.tv_item_home_call_comment_content);
		holder.mRlBusinessContent = (RelativeLayout) view
				.findViewById(R.id.rl_item_home_call_comment_content);
		holder.mTvDate = (TextView) view
				.findViewById(R.id.tv_item_home_call_comment_time);

		holder.mIvThum = (CustomShapeImageView) view
				.findViewById(R.id.iv_item_home_call_comment_img);
		holder.commentBtn = (TextView)view.findViewById(R.id.commentBtn);
		holder.delCommentBtn = (TextView)view.findViewById(R.id.delCommentBtn);
	}

}