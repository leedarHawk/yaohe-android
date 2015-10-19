package com.collcloud.yaohe.api.info;

import java.io.Serializable;
import java.util.List;

/**
 * 获取一级和二级分类列表
 * 
 * @ClassName ClassifyListInfo
 */
public class ClassifyListInfo implements Serializable {

	private static final long serialVersionUID = 2952355628054588924L;

	public List<Classify> data;

	public static class Classify implements Serializable {

		private static final long serialVersionUID = 3179532920189831748L;
		public String id;
		public String title;
	}

}
