package com.collcloud.yaohe.api.info;

import java.io.Serializable;

/**
 * 新品发布详情
 * @ClassName DetailsNewProductInfo
 */
public class DetailsNewProductInfo implements Serializable {

	private static final long serialVersionUID = 7925742331853191662L;
	public NewProduct data;

	public static class NewProduct implements Serializable {

		private static final long serialVersionUID = 3844215772491204447L;
		public String id;
		public String member_id;
		public String title;
		public String price;
		public String content;
		public String addtime;
		public String img1;
		public String img2;
		public String img3;
		public String img4;
		public String img5;
		public String img6;
		public String start_date;
		public String end_date;
		public String day;
		public String shop_id;
		public String shop_name;
		public String shop_subscribe_tel;
		public String shop_address;
		public String shop_fans_num;
		
		public String collection_num;// 收藏数量
		public String zan_num;// 点赞数量
		public String comment_num;// 评论数量
	}

}
