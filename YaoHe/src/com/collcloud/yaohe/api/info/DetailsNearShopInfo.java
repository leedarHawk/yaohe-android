package com.collcloud.yaohe.api.info;

import java.io.Serializable;

public class DetailsNearShopInfo implements Serializable{

	private static final long serialVersionUID = -7076052827490554409L;
	public NearShopInfo data;
	
	public static class NearShopInfo implements Serializable {
		private static final long serialVersionUID = -5991453539793331064L;
		public String id;
		public String member_id;
		public String title;
		public String addtime;
		public String content;
		public String type;
		public String address;
		public String img1;
		public String start_date;
		public String end_date;
		public String _long;
		public String lat;
		public String shop_id;
		public String shop_name;
		public String shop_subscribe_tel;
		public String shop_address;
		public String shop_fans_num;
		
		
	}

}
