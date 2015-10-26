package com.collcloud.yaohe.api.info;

import java.io.Serializable;
import java.util.List;

public class HomeRotate implements Serializable {

	private static final long serialVersionUID = -8551115912406402456L;

	public List<Rotate> data;
	
	public static class Rotate implements Serializable {
		private static final long serialVersionUID = -1849924332419198966L;
		public String id;
		public String type;
		public String content_id;
		public String link_url;
		public String title;
		public String img;
		public String service_id;
	}
}
