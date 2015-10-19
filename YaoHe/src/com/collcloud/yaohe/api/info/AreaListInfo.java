package com.collcloud.yaohe.api.info;

import java.io.Serializable;
import java.util.List;

public class AreaListInfo implements Serializable {

	private static final long serialVersionUID = -4462923905867240595L;
	public List<AreaList> data;

	public static class AreaList implements Serializable {

		private static final long serialVersionUID = -675963790641437160L;
		public String id;
		public String title;
	}

}
