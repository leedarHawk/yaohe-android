package com.collcloud.yaohe.ui.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.api.info.HomeCallInfo.CallInfo;
import com.collcloud.yaohe.common.base.AppApplacation;
import com.collcloud.yaohe.common.base.GlobalConstant;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.ui.photoview.BitmapCache;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.ui.utils.Utils;

/**
 * 首页推荐标签下的内容适配器
 * 
 * @ClassName HomeAdapter
 * @Description 推荐内容适配器
 * @author CollCloud_小米
 */
public class HomeAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mLayoutInflater = null;

	/** 首页 活动消息类型， 0 ： 优惠券 */
	public static final String TYPE_YOUHUI = "0";
	/** 首页 活动消息类型， 1 ： 会员卡 */
	public static final String TYPE_CARD = "1";
	/** 首页 活动消息类型， 2 ： 活动 */
	public static final String TYPE_HUODONG = "2";
	/** 首页 活动消息类型， 3 ： 新品 */
	public static final String TYPE_XINPIN = "3";
	/** 首页 活动消息类型， 4 ： 吆喝啦 */
	public static final String TYPE_DEFAULT = "4";

	private static ImageLoader mImageLoader;
	private int mItemCount = 0;
	private String tag = HomeAdapter.class.getSimpleName();

	private List<CallInfo> mCallInfos = new ArrayList<CallInfo>();

	public HomeAdapter(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
	}

	public HomeAdapter(Context context, List<CallInfo> datas) {
		this.mContext = context;
		this.mCallInfos = datas;
		mLayoutInflater = LayoutInflater.from(mContext);

		mImageLoader = new ImageLoader(AppApplacation.requestQueue,
				new BitmapCache());
	}

	public void setItemCount(int count) {
		this.mItemCount = count;
	}

	@Override
	public int getCount() {
		if (mCallInfos == null) {
			return 0;
		}
		return mCallInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return mCallInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 监听器-用于点击首页吆喝列表内容时的逻辑处理 具体实现 在 HomeTuiJianFragment里。
	 * 
	 * @ClassName OnButtonClickListener
	 */
	public interface OnButtonClickListener {
		void onBusinessShopClick(int position, String shopId, String memberID);

		void onButtonClick(int position, String call_id, String service_id,
				String shopId, String memberID, String type);

		// 关注按钮点击
		void onGuanZhuClick(TextView tvGuanzhu, TextView mTvFans,String callID);

		// 点赞按钮点击
		void onZanButtonClick(int position, TextView tvZanImg, TextView tvZan,
				String callID);

		// 收藏按钮点击
		void onShouCangButtonClick(int position, TextView tvShouCangImg,
				TextView tvShouCang, String callID);

		// 评论按钮点击
		void onPinLunButtonClick(int position, TextView tvPinLunImg,
				TextView tvPinLun, String callID, String type);
	}

	private OnButtonClickListener mOnBusinessShopListener = null;

	public void setOnBusinessShopListener(OnButtonClickListener listener) {
		this.mOnBusinessShopListener = listener;
	}

	private OnButtonClickListener mOnItemClickListener = null;

	public void setOnHomeItemClickListener(OnButtonClickListener listener) {
		this.mOnItemClickListener = listener;
	}

	private OnButtonClickListener mOnGuanZhuClickListener = null;

	public void setOnGuanZhuClickListener(OnButtonClickListener listener) {
		this.mOnGuanZhuClickListener = listener;
	}

	private OnButtonClickListener mOnZanClickListener = null;

	public void setOnZanClickListenerClickListener(
			OnButtonClickListener listener) {
		this.mOnZanClickListener = listener;
	}

	private OnButtonClickListener mOnPinlunClickListener = null;

	public void setOnPinLunClickListenerClickListener(
			OnButtonClickListener listener) {
		this.mOnPinlunClickListener = listener;
	}

	private OnButtonClickListener mOnShouCangClickListener = null;

	public void setOnShouCangClickListenerClickListener(
			OnButtonClickListener listener) {
		this.mOnShouCangClickListener = listener;
	}

	public void refresh(List<CallInfo> data) {
		this.mCallInfos = data;
		notifyDataSetChanged();
	}

	@SuppressWarnings("deprecation")
	@SuppressLint({ "InflateParams", "ClickableViewAccessibility" })
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final HomeContent holder;
		CallInfo callInfo = null;
		if (mCallInfos != null && mCallInfos.size() > 0) {
			callInfo = mCallInfos.get(position);
		}
		if (convertView == null) {

			convertView = mLayoutInflater.inflate(R.layout.item_home_content,
					null);

			holder = new HomeContent();
			resetLayout(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (HomeContent) convertView.getTag();
		}

		// 设定首页吆喝信息
		ImageListener listener = ImageLoader.getImageListener(holder.mIvThum,
				R.drawable.icon_yaohe_loading_default,
				R.drawable.icon_yaohe_loading_default);

		if (callInfo != null && callInfo.shop_fans_num != null) {
			holder.mTvFans.setText(callInfo.shop_fans_num + " 粉丝");
		}
		if (callInfo != null && callInfo.zan_num != null) {
			holder.mTvZan.setText(callInfo.zan_num);
		}
		if (callInfo != null && callInfo.comment_num != null) {
			holder.mTvPinlun.setText(callInfo.comment_num);
		}
		if (callInfo != null && callInfo.collection_num != null) {
			holder.mTvShoucang.setText(callInfo.collection_num);
		}

		if (callInfo != null && callInfo.shop_name != null) {
			holder.mTvName.setText(callInfo.shop_name);
		}
		if (callInfo != null && callInfo.addtime != null) {
			holder.mTvDate.setVisibility(View.VISIBLE);
			// 确保long型日期/时间值是正确的，比如检测长度，是否少了最后的毫秒数
			if (callInfo.addtime.length() == 10) {
				callInfo.addtime = callInfo.addtime + "000";
			}
			SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");
			Date date = null;
			try {  
				 date = new Date(Long.valueOf(callInfo.addtime));
			} catch(Exception e) {
				e.printStackTrace();
				 date = new Date();
			}
			
			holder.mTvDate.setText(df.format(date));
		} else {
			holder.mTvDate.setVisibility(View.GONE);
		}

		// TODO 关注按钮初始状态
		holder.mTvZanImg.setText(GlobalConstant.INVALID_VALUE);
		holder.mTvShoucangImg.setText(GlobalConstant.INVALID_VALUE);
		holder.mTvPinlunImg.setText(GlobalConstant.INVALID_VALUE);
		
		//holder.mIvThum.setImageResource(R.drawable.icon_yaohe_loading_default);
		if (callInfo != null && !Utils.isStringEmpty(callInfo.s_img)) {
			holder.fv_item_home_shop_image.setVisibility(View.VISIBLE);
			CCLog.d(tag, "yaohe s_img url:"+callInfo.s_img);
			mImageLoader.get(callInfo.s_img, listener,mContext.getResources().getDimensionPixelSize(R.dimen.max_list_width),mContext.getResources().getDimensionPixelSize(R.dimen.max_list_height));
		} else {
			CCLog.d(tag, "yaohe s_img url:"+callInfo.s_img);
			holder.fv_item_home_shop_image.setVisibility(View.GONE);
			holder.mIvThum.setImageResource(R.drawable.icon_yaohe_loading_default);
		}
		
		//吆喝本身内容
		holder.tv_item_home_content_yaohe.setText(callInfo.content); 
		
		ImageListener listener2 = ImageLoader.getImageListener(holder.iv_item_hme_shop_image_yaohe,
				R.drawable.icon_yaohe_loading_default,
				R.drawable.icon_yaohe_loading_default);
		
		
		//holder.iv_item_hme_shop_image_yaohe.setImageResource(R.drawable.icon_yaohe_loading_default);
		if (callInfo != null && !Utils.isStringEmpty(callInfo.img)) {
			holder.fv_item_home_shop_image_yaohe.setVisibility(View.VISIBLE);
			CCLog.d(tag, "yaohe img url:"+callInfo.img);
			mImageLoader.get(callInfo.img, listener2,mContext.getResources().getDimensionPixelSize(R.dimen.max_list_width),mContext.getResources().getDimensionPixelSize(R.dimen.max_list_height));
		} else {
			CCLog.d(tag, "yaohe img url:"+callInfo.img);
			holder.fv_item_home_shop_image_yaohe.setVisibility(View.GONE);
			holder.iv_item_hme_shop_image_yaohe.setImageResource(R.drawable.icon_yaohe_loading_default);
		}
		
		
		//不纯吆喝 引用了某一个服务 则显示 两行
		if(callInfo != null && "1".equals(callInfo.is_yinyong)) {
			holder.ll_item_hoe_image_content_yaohe.setVisibility(View.VISIBLE);
			holder.mRlContent.setVisibility(View.VISIBLE);
			holder.mRlContent.setBackgroundResource(R.color.diliver_in_gray);
		} else if(callInfo != null && !"1".equals(callInfo.is_yinyong)) {//不是引用 就显示一样
			if(!TYPE_DEFAULT.equals(callInfo.type)) {//不是纯吆喝 也不是 发吆喝引用了服务 而是纯服务
				holder.ll_item_hoe_image_content_yaohe.setVisibility(View.GONE);
				holder.mRlContent.setVisibility(View.VISIBLE);
				holder.mRlContent.setBackgroundResource(R.color.transparent);
			} else {//纯吆喝
				holder.ll_item_hoe_image_content_yaohe.setVisibility(View.VISIBLE);
				holder.mRlContent.setVisibility(View.GONE);
			}
			
		}
		
		
		
//		if(callInfo != null &&  !TYPE_DEFAULT.equals(callInfo.type)) {
//			holder.ll_item_hoe_image_content_yaohe.setVisibility(View.GONE);
//			holder.mRlContent.setVisibility(View.VISIBLE);
//			holder.mRlContent.setBackgroundResource(R.color.transparent);
//		} else {//是吆喝
//			holder.ll_item_hoe_image_content_yaohe.setVisibility(View.VISIBLE);
//			if("0".equals(callInfo.c_id) || Utils.isStringEmpty(callInfo.c_id)) {//纯吆喝
//				holder.mRlContent.setVisibility(View.GONE);
//			} else {//有引用
//				holder.mRlContent.setVisibility(View.VISIBLE);
//				holder.mRlContent.setBackgroundResource(R.color.diliver_in_gray);
//			}
//		}
		
		

		if (callInfo != null && callInfo.type != null) {
			String type = callInfo.type;
			if (type.equals(TYPE_YOUHUI)) {
				holder.mTvContent.setText(callInfo.title);
				holder.mIvTag.setVisibility(View.VISIBLE);
				holder.mIvTag
						.setBackgroundResource(R.drawable.icon_home_type_youhuiquan);
			} else if (type.equals(TYPE_CARD)) {
				holder.mTvContent.setText(callInfo.title);
				holder.mIvTag.setVisibility(View.VISIBLE);
				holder.mIvTag
						.setBackgroundResource(R.drawable.icon_home_type_huiyuanka);
			} else if (type.equals(TYPE_XINPIN)) {
				holder.mTvContent.setText(callInfo.title);
				holder.mIvTag.setVisibility(View.VISIBLE);
				holder.mIvTag
						.setBackgroundResource(R.drawable.icon_home_type_xinpin);
			} else if (type.equals(TYPE_HUODONG)) {
				holder.mTvContent.setText(callInfo.title);
				holder.mIvTag.setVisibility(View.VISIBLE);
				holder.mIvTag
						.setBackgroundResource(R.drawable.icon_home_type_huodong);
			} else if (type.equals(TYPE_DEFAULT)) {
				holder.mTvContent.setText(callInfo.s_content);
				holder.mIvTag.setVisibility(View.GONE);
			} else {
				holder.mIvTag.setVisibility(View.GONE);
			}
			//LEE
			if("1".equals(callInfo.is_yinyong)) {
				holder.mTvContent.setText(callInfo.s_content);
			} else {
				holder.mTvContent.setText(callInfo.title);
			}
		}
		if (callInfo != null && callInfo.shop_star != null) {
			String shopStar = callInfo.shop_star;
			if (shopStar.equals("1")) {
				holder.mIvxing1
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing2
						.setBackgroundResource(R.drawable.icon_rate_star_off);
				holder.mIvxing3
						.setBackgroundResource(R.drawable.icon_rate_star_off);
				holder.mIvxing4
						.setBackgroundResource(R.drawable.icon_rate_star_off);
				holder.mIvxing5
						.setBackgroundResource(R.drawable.icon_rate_star_off);
			} else if (shopStar.equals("2")) {
				holder.mIvxing1
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing2
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing3
						.setBackgroundResource(R.drawable.icon_rate_star_off);
				holder.mIvxing4
						.setBackgroundResource(R.drawable.icon_rate_star_off);
				holder.mIvxing5
						.setBackgroundResource(R.drawable.icon_rate_star_off);
			} else if (shopStar.equals("3")) {
				holder.mIvxing1
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing2
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing3
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing4
						.setBackgroundResource(R.drawable.icon_rate_star_off);
				holder.mIvxing5
						.setBackgroundResource(R.drawable.icon_rate_star_off);
			} else if (shopStar.equals("4")) {
				holder.mIvxing1
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing2
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing3
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing4
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing5
						.setBackgroundResource(R.drawable.icon_rate_star_off);
			} else if (shopStar.equals("5")) {
				holder.mIvxing1
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing2
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing3
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing4
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing5
						.setBackgroundResource(R.drawable.icon_rate_star_on);
			} else {
				holder.mIvxing1
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing2
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing3
						.setBackgroundResource(R.drawable.icon_rate_star_on);
				holder.mIvxing4
						.setBackgroundResource(R.drawable.icon_rate_star_off);
				holder.mIvxing5
						.setBackgroundResource(R.drawable.icon_rate_star_off);

			}
		}

		if (callInfo != null && callInfo.guanzhu != null) {
			String guanzhu = callInfo.guanzhu;
			if (guanzhu.equals(GlobalConstant.VALID_VALUE)) {
				holder.mTvGuanzhu.setText(GlobalConstant.VALID_VALUE);
				holder.mTvGuanzhu
						.setBackgroundResource(R.drawable.icon_home_type_yiguanzhu);
			} else {
				holder.mTvGuanzhu.setText(GlobalConstant.INVALID_VALUE);
				holder.mTvGuanzhu
						.setBackgroundResource(R.drawable.icon_home_type_weiguanzhu);
			}
		} else {
			holder.mTvGuanzhu.setText(GlobalConstant.INVALID_VALUE);
			holder.mTvGuanzhu
					.setBackgroundResource(R.drawable.icon_home_type_weiguanzhu);
		}

		final String callID = callInfo.id;
		final String type = callInfo.type;
		final String shopID = callInfo.shop_id;
		final String memberID = callInfo.member_id;
		final String service_id = callInfo.service_id;
		// *********** 点击List列表的关注按钮 ***********
		holder.mTvGuanzhu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnGuanZhuClickListener != null) {
					mOnGuanZhuClickListener.onGuanZhuClick(holder.mTvGuanzhu,
							holder.mTvFans,shopID);
				}
			}
		});
		// 点击吆喝商家信息
		holder.mRlBusinessContent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mOnBusinessShopListener != null) {
					mOnBusinessShopListener.onBusinessShopClick(position,
							shopID, memberID);
				}
			}
		});
		// 点击List列表项的文字说明或者图片
		holder.mRlContent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onButtonClick(position, callID,
							service_id, shopID, memberID, type);
				}
			}
		});
		holder.tv_item_home_content_yaohe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onButtonClick(position, callID,
							service_id, shopID, memberID, type);
				}
			}
		});
		
		holder.iv_item_hme_shop_image_yaohe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onButtonClick(position, callID,
							service_id, shopID, memberID, type);
				}
			}
		});
		
		
		holder.mTvContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onButtonClick(position, callID,
							service_id, shopID, memberID, type);
				}
			}
		});
		holder.mTvShoucang.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnShouCangClickListener != null) {
					mOnShouCangClickListener.onShouCangButtonClick(position,
							holder.mTvShoucangImg, holder.mTvShoucang, callID);
				}

			}
		});
		holder.mTvShoucangImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnShouCangClickListener != null) {
					mOnShouCangClickListener.onShouCangButtonClick(position,
							holder.mTvShoucangImg, holder.mTvShoucang, callID);
				}
			}
		});

		holder.mTvZan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnZanClickListener != null) {
					mOnZanClickListener.onZanButtonClick(position,
							holder.mTvZanImg, holder.mTvZan, callID);
				}

			}
		});
		holder.mTvZanImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnZanClickListener != null) {
					mOnZanClickListener.onZanButtonClick(position,
							holder.mTvZanImg, holder.mTvZan, callID);
				}

			}
		});
		holder.mTvPinlun.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnPinlunClickListener != null) {
					mOnPinlunClickListener
							.onPinLunButtonClick(position, holder.mTvPinlunImg,
									holder.mTvPinlun, callID, type);
				}

			}
		});
		holder.mTvPinlunImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnPinlunClickListener != null) {
					mOnPinlunClickListener
							.onPinLunButtonClick(position, holder.mTvPinlunImg,
									holder.mTvPinlun, callID, type);
				}

			}
		});

		return convertView;
	}

	private class HomeContent {

		/** 店铺名称 */
		TextView mTvName;
		/** 店铺简介 */
		TextView mTvContent;
		RelativeLayout mRlContent;
		RelativeLayout mRlBusinessContent;
		/** 粉丝数 */
		TextView mTvFans;
		/** 日期时间 */
		TextView mTvDate;
		/** 点赞数 */
		TextView mTvZan;
		/** 点赞 小图标 */
		TextView mTvZanImg;
		/** 评论数 */
		TextView mTvPinlun;
		/** 评论 的小图标 */
		TextView mTvPinlunImg;
		/** 收藏数 */
		TextView mTvShoucang;
		/** 收藏 小图标 */
		TextView mTvShoucangImg;
		/** 列表简介图片 */
		ImageView mIvThum;
		/** 消息活动类型 */
		ImageView mIvTag;
		/** 关注按钮 */
		Button mTvGuanzhu;
		/** 评价星星1 */
		TextView mIvxing1;
		/** 评价星星2 */
		TextView mIvxing2;
		/** 评价星星3 */
		TextView mIvxing3;
		/** 评价星星4 */
		TextView mIvxing4;
		/** 评价星星5 */
		TextView mIvxing5;
		//吆喝本身图片
		ImageView iv_item_hme_shop_image_yaohe;
		//吆喝本身内容
		TextView tv_item_home_content_yaohe;
		View ll_item_hoe_image_content_yaohe;
		
		// 引用
		FrameLayout fv_item_home_shop_image;
		//吆喝本身 frameLayout
		FrameLayout fv_item_home_shop_image_yaohe;
		

	}

	private void resetLayout(HomeContent holder, View view) {

		LinearLayout rlLayout = (LinearLayout) view
				.findViewById(R.id.rl_item_home_content_viewgroup);
		SupportDisplay.resetAllChildViewParam(rlLayout);

		holder.mTvName = (TextView) view.findViewById(R.id.tv_item_home_name);
		holder.mTvFans = (TextView) view.findViewById(R.id.tv_item_home_fans);
		holder.mTvContent = (TextView) view
				.findViewById(R.id.tv_item_home_content);
		holder.mRlContent = (RelativeLayout) view
				.findViewById(R.id.ll_item_hoe_image_content);
		holder.mRlBusinessContent = (RelativeLayout) view
				.findViewById(R.id.rl_item_business_shop_content);
		holder.mTvDate = (TextView) view
				.findViewById(R.id.tv_item_home_shop_time);
		holder.mTvZan = (TextView) view.findViewById(R.id.tv_item_home_zan);
		holder.mTvZanImg = (TextView) view
				.findViewById(R.id.tv_item_home_zan_img);
		holder.mTvPinlun = (TextView) view
				.findViewById(R.id.tv_item_home_pinlun);
		holder.mTvPinlunImg = (TextView) view
				.findViewById(R.id.tv_item_home_pinlun_img);
		holder.mTvShoucang = (TextView) view
				.findViewById(R.id.tv_item_home_shoucang);
		holder.mTvShoucangImg = (TextView) view
				.findViewById(R.id.tv_item_home_shoucang_img);

		holder.mIvThum = (ImageView) view
				.findViewById(R.id.iv_item_hme_shop_image);
		holder.mIvTag = (ImageView) view
				.findViewById(R.id.iv_item_hme_shop_image_tags);
		holder.mIvxing1 = (TextView) view
				.findViewById(R.id.tv_item_home_pingjia1);
		holder.mIvxing2 = (TextView) view
				.findViewById(R.id.tv_item_home_pingjia2);
		holder.mIvxing3 = (TextView) view
				.findViewById(R.id.tv_item_home_pingjia3);
		holder.mIvxing4 = (TextView) view
				.findViewById(R.id.tv_item_home_pingjia4);
		holder.mIvxing5 = (TextView) view
				.findViewById(R.id.tv_item_home_pingjia5);
		holder.mTvGuanzhu = (Button) view
				.findViewById(R.id.tv_item_home_guanzhu);
		
		//吆喝图片 以及内容 LEE
		holder.iv_item_hme_shop_image_yaohe = (ImageView)view.findViewById(R.id.iv_item_hme_shop_image_yaohe);
		holder.tv_item_home_content_yaohe = (TextView)view.findViewById(R.id.tv_item_home_content_yaohe);
		holder.ll_item_hoe_image_content_yaohe = view.findViewById(R.id.ll_item_hoe_image_content_yaohe);
		
		holder.fv_item_home_shop_image = (FrameLayout)view.findViewById(R.id.fv_item_home_shop_image);
		holder.fv_item_home_shop_image_yaohe = (FrameLayout)view.findViewById(R.id.fv_item_home_shop_image_yaohe);
	}

	// private static class AnimateFirstDisplayListener extends
	// SimpleImageLoadingListener {
	//
	// static final List<String> displayedImages = Collections
	// .synchronizedList(new LinkedList<String>());
	//
	// @Override
	// public void onLoadingComplete(String imageUri, View view,
	// Bitmap loadedImage) {
	// if (loadedImage != null) {
	// ImageView imageView = (ImageView) view;
	// boolean firstDisplay = !displayedImages.contains(imageUri);
	// if (firstDisplay) {
	// FadeInBitmapDisplayer.animate(imageView, 500);
	// displayedImages.add(imageUri);
	// }
	// }
	// }
	// }
}
