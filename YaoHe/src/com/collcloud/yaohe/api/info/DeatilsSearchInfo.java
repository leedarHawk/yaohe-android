package com.collcloud.yaohe.api.info;

import java.io.Serializable;
import java.util.List;

public class DeatilsSearchInfo implements Serializable {
	private static final long serialVersionUID = 931815126587086663L;
	public List<SearchShopInfo> service;
	public List<SearchYaoheInfo> call;

	public static class SearchShopInfo implements Serializable {
		private static final long serialVersionUID = 4744268207085270601L;
		public String id;// 店铺ID
		public String title; // 商店标题
		public String member_id;// 会员ID
		public String face;// 图片地址
	}

	public static class SearchYaoheInfo implements Serializable {
		private static final long serialVersionUID = -6908446548557009506L;
		public String id;// 吆喝的ID
		public String title; // /吆喝标题
		public String content; // /吆喝标题
		public String member_id;// 会员ID
		public String img;// 图片地址
		public String shop_id;// 店铺ID
		public String shop_title;// 店铺名称
		public String type;
		public String service_id;

	}

}
