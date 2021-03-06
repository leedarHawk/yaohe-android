package com.collcloud.yaohe.entity;

import java.io.Serializable;

public class SCInfoList implements Serializable {

	/**
	 * @类说明：收藏信息类
	 * @author 赵洋洋 
	 */
	private static final long serialVersionUID = 1L;


		/** 收藏id */
		public String id;
		/** 发信息的会员ID */
		public String member_id;
		public String service_id;
		public String title;
		/** 吆喝内容 */
		public String content;
		/** 图片地址1 */
		public String img1;
		/** 1券 2卡 3新品 4活动 0无 */
		public String type;
		/** 时间*/
		public String addtime;
		/** 店铺名*/
		public String shop_name;
		/**店铺id*/
		public String shop_id;


}
