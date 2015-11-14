package com.collcloud.yaohe.api.info;

import java.io.Serializable;
import java.util.List;

public class DetailBusinessCommentInfo implements Serializable {
	private static final long serialVersionUID = -7888591910473607944L;
	public List<BusinessCommentInfo> data;

	public static class BusinessCommentInfo implements Serializable {
		private static final long serialVersionUID = 4226956883790915250L;
		public String id;// 评论ID
		public String shop_id;// 
		public String member_id;
		public String star;
		public String content;// 评论内容
		public String is_anonymous;// 0不匿名 1匿名
		public String addtime;
		public String title;
		public String face;
		public String nickname;
		public String type;
		
		public String parentid;
		public String to_member_id;
		public String is_read;
		public String comment_title;
		
		public String totalStar;
		
		//parentid所属的用户昵称
		public String answerName;
		
		


	}

}