package com.collcloud.yaohe.api.info;

import java.io.Serializable;
/**
 * 商家基本信息
 * @ClassName ShopBaseInfo
 */
public class ShopBaseInfo implements Serializable {
	private static final long serialVersionUID = -3450861489696847918L;
	public ShopBase data;

	public static class ShopBase implements Serializable {
		private static final long serialVersionUID = -3209990898990988128L;
		public String title;
		public String face;
		public String star;
		public String _class;
	}

}
