package com.collcloud.yaohe.api;

/**
 * 服务器返回json型数据所包含的共通内容
 * 
 * @ClassName BaseResponseInfo
 * @author CollCloud_小米
 */
public class BaseResponseInfo extends Base {

	private static final long serialVersionUID = -4337750146767231936L;

	public final static String KEY_RESULT_CODE = "code";
	public final static String KEY_RESULT_MSG = "message";
	public final static String KEY_RESULT_ID = "id";
	public final static String KEY_RESULT_TITLE = "title";
	/**
	 * API返回状态码 （0: 正常 ； 1:错误）
	 */
	public int mCode;

	/**
	 * 状态码为1时，返回的错误信息
	 */
	public String mStrMsg;

	/**
	 * API返回的ID
	 */
	public String mStrId;
	/**
	 * API返回的Title
	 */
	public String mStrTitle;

	public int getmCode() {
		return mCode;
	}

	public void setmCode(int mCode) {
		this.mCode = mCode;
	}

	public String getmStrMsg() {
		return mStrMsg;
	}

	public void setmStrMsg(String strMsg) {
		mStrMsg = strMsg;
	}

	public String getmStrId() {
		return mStrId;
	}

	public void setmStrId(String strId) {
		mStrId = strId;
	}

	public String getmStrTitle() {
		return mStrTitle;
	}

	public void setmStrTitle(String strTitle) {
		mStrTitle = strTitle;
	}

}
