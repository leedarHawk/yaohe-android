package com.collcloud.yaohe.api.info;

import java.io.Serializable;

/**
 * 商家基本信息
 * @ClassName ShopInfo
 */
public class ShopInfo implements Serializable {

	private static final long serialVersionUID = 8424412686386538919L;
	public Shop data;
	
	public static class Shop implements Serializable{

		private static final long serialVersionUID = -7927140489490877395L;
		public String id;
		public String full_name;
		public String fans_num;
		public String star;
		public String content;
		public String address;
		public String subscribe_tel;
		public String business_time;
		public String follow;
	}

}
