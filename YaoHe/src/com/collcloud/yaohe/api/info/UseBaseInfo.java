package com.collcloud.yaohe.api.info;

import java.io.Serializable;
/**
 * 个人基本信息
 * @ClassName UseBaseInfo
 */
public class UseBaseInfo implements Serializable {
	private static final long serialVersionUID = -4162922150145167974L;
	public UseBase data;

	public static class UseBase implements Serializable {
		private static final long serialVersionUID = 2606631054821529819L;
		public String login_user;
		public String face;
		public String nickname;
		public String sex;
	}

}
