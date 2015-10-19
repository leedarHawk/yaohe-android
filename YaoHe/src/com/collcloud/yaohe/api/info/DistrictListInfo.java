package com.collcloud.yaohe.api.info;

import java.io.Serializable;
import java.util.List;

public class DistrictListInfo implements Serializable {
	private static final long serialVersionUID = 7105915240307357389L;
	public List<DistrictList> data;

	public static class DistrictList implements Serializable {
		private static final long serialVersionUID = -6893462029172217853L;
		public String id;
		public String title;
	}

}
