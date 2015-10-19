package com.collcloud.yaohe.api.info;

import java.io.Serializable;
import java.util.List;

/**
 * 首页分类列表
 * @ClassName HomeTypeInfo
 */
public class HomeTypeInfo implements Serializable {

	private static final long serialVersionUID = -4252206829593971414L;
	public List<TypeInfo> data;

	public static class TypeInfo implements Serializable {
		private static final long serialVersionUID = 4908096420771575576L;
		public String id;
		public String city_id;
		public String class_id;
		public String title;
		public String show_title;
		public String is_hidden;
		public String order_num;
	}
}
