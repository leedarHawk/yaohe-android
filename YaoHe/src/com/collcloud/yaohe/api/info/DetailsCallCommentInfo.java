package com.collcloud.yaohe.api.info;

import java.io.Serializable;
import java.util.List;

public class DetailsCallCommentInfo implements Serializable {
	private static final long serialVersionUID = 5471458671113423068L;
	public List<CallCommentInfo> data;

	public static class CallCommentInfo implements Serializable {
		private static final long serialVersionUID = -4584312663596204498L;
		public String id;// 评论ID
		public String member_id;
		public String content;// 评论内容
		public String is_anonymous;// 0不匿名 1匿名
		public String addtime;
		public String face;
		public String nickname;
		public String shop_star;
		public String star;
		//回复的评论id
		public String parentid;
		//parentid所属的用户昵称
		public String answerName;

	}

}
