package com.collcloud.yaohe.api.info;

import java.io.Serializable;

/**
 * 活动详情
 * @ClassName DetailsActivityInfo
 */
public class DetailsActivityInfo implements Serializable{

	private static final long serialVersionUID = -8167308961566273853L;
	public HuoDong data;
	
	public static class HuoDong implements Serializable {
		private static final long serialVersionUID = -7718478666199081710L;
		public String id;
		public String member_id;
		public String title;
		public String addtime;
		public String content;
		public String address;
		public String img1;
		public String img2;
		public String img3;
		public String img4;
		public String img5;
		public String img6;
		public String start_date;
		public String end_date;
		public String _long;
		public String lat;
		public String shop_id;
		public String shop_name;
		public String shop_subscribe_tel;
		public String shop_address;
		public String shop_fans_num;

		public String zan_num;
		public String comment_num;
		public String collection_num;
		
	}

}
