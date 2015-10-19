package com.collcloud.yaohe.api.info;

import java.io.Serializable;
import java.util.List;

/**
 * 获取附近商家
 * 
 * @ClassName NearByListInfo
 */
public class NearByListInfo implements Serializable {

	private static final long serialVersionUID = 1275818123145509819L;
	public List<NearBy> data;

	public static class NearBy implements Serializable {
		private static final long serialVersionUID = 8672834085032804705L;
		public String id;
		public String title;
		public String address;
		public String content;
		public String img;
		public String face;
		public String type;
		public String full_name;
		public String member_id;
		public String shop_id;
		public String star;
		public String fans_num;
		public String area_id;
		public String district_id;
		public String range;
		
		// 区域名称
		public String area_name;
		public String district_name;
	}
}
