package com.collcloud.yaohe.api.info;

import java.io.Serializable;
import java.util.List;

/**
 * 城市信息列表内容
 * @ClassName CityListInfo
 */
public class CityListInfo implements Serializable {

	private static final long serialVersionUID = 4286260737409036338L;

	public List<Citys> data;

	public static class Citys implements Serializable {
		private static final long serialVersionUID = 5155042394656498781L;
		public String id;
		public String title;
	}

}
