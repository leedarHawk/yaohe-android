package com.collcloud.yaohe.entity;

import java.io.Serializable;
import java.util.List;

public class HYCardList implements Serializable{

	/**
	 * @类说明：会员卡基本信息类
	 * @author 赵洋洋
	 */	
		/** 会员卡ID */
		public String id;
		/** 会员ID */
		public String member_id;
		/** 会员卡标题*/
		public String title;
		/** 图片地址1 */
		public String img1;
		/** 折扣 */
		public String discount;
		/** 会员的会员卡ID*/
		public String member_card_id;

}
