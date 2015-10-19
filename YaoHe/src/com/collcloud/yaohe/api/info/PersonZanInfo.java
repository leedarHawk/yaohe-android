package com.collcloud.yaohe.api.info;

import java.io.Serializable;
import java.util.List;

public class PersonZanInfo implements Serializable {
	private static final long serialVersionUID = 6337053569448325347L;
	public List<ZANInfo> data;

	public static class ZANInfo implements Serializable {
		private static final long serialVersionUID = 54636573553261319L;
		public String id;// 点赞的ID
		public String member_id;
		public String content;// 服务内容
		public String shop_service_id;
		public String addtime;
		public String face;
		public String nickname;
		public String star;
		public String img;

	}

}