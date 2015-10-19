package com.collcloud.yaohe.common.data;

import android.content.Context;

import com.collcloud.swipe.interfaces.ILoginDataManager;
import com.collcloud.yaohe.ui.utils.SharedPreferenceAccesser;

/**
 * 吆喝APP（个人/商家，登陆前后）保存实现类
 * 
 * @ClassName LoginDataManagerSPImpl
 * @Description
 * @author CollCloud_小米
 */
public class LoginDataManagerSPImpl implements ILoginDataManager {
	/**
	 * Activity
	 */
	private Context mContext = null;
	private static ILoginDataManager mLoginDataManager = null;
	/**
	 * 注册状态 KEY
	 */
	private static final String LOGIN_STATE = "loginState";
	/**
	 * 商家状态 KEY
	 */
	private static final String BUSINESS_STATE = "BussinessState";
	private static final String MEMBER_ID = "MemberID";
	private static final String USER_ID = "UserID";
	private static final String USER_PWD = "UserPassword";
	private static final String USER_TYPE = "UserType";
	private static final String USER_PHONE = "UserPhone";
	private static final String USER_NICKNAME = "UserNickName";

	public static ILoginDataManager getInstace(Context context) {
		if (mLoginDataManager == null) {
			mLoginDataManager = new LoginDataManagerSPImpl(context);
		}
		return mLoginDataManager;
	}

	private LoginDataManagerSPImpl(Context context) {
		this.mContext = context;
	}

	/**
	 * 设定个人登陆状态
	 */
	@Override
	public boolean setLoginState(String state) {
		boolean flag = false;
		if (mContext == null) {
			return false;
		}
		// 注册成功后的状态保存
		flag = SharedPreferenceAccesser.saveStringData(mContext, LOGIN_STATE,
				state);
		return flag;
	}

	/**
	 * 获取个人信息，是否已经成功登陆？
	 */
	@Override
	public String getLoginState() {
		String state = "";
		if (mContext == null) {
			return null;
		}

		// 获取登陆的状态值
		if (SharedPreferenceAccesser.getStringData(mContext, LOGIN_STATE) != null) {
			state = SharedPreferenceAccesser.getStringData(mContext,
					LOGIN_STATE);
		}

		return state;
	}

	/**
	 * 设定商家的状态
	 */
	@Override
	public boolean setBusinessState(String businessState) {
		boolean flag = false;
		if (mContext == null) {
			return false;
		}
		// 申请为商家成功后的状态保存
		flag = SharedPreferenceAccesser.saveStringData(mContext,
				BUSINESS_STATE, businessState);
		return flag;
	}

	/**
	 * 获取商家的状态,是否已经通过商家审核
	 */
	@Override
	public String getBusinessState() {
		String state = "0";
		if (mContext == null) {
			return null;
		}

		// 【申请为商家】的状态值
		if (SharedPreferenceAccesser.getStringData(mContext, BUSINESS_STATE) != null) {
			state = SharedPreferenceAccesser.getStringData(mContext,
					BUSINESS_STATE);
		}

		return state;
	}

	/**
	 * 保存用户memberid
	 */
	@Override
	public boolean setMemberId(String memberID) {
		boolean flag = false;
		if (mContext == null) {
			return false;
		}
		flag = SharedPreferenceAccesser.saveStringData(mContext, MEMBER_ID,
				memberID);
		return flag;
	}

	/**
	 * 获取用户memberid
	 */
	@Override
	public String getMemberId() {
		String memberid = "";
		if (mContext == null) {
			return null;
		}

		if (SharedPreferenceAccesser.getStringData(mContext, MEMBER_ID) != null) {
			memberid = SharedPreferenceAccesser.getStringData(mContext,
					MEMBER_ID);
		}

		return memberid;
	}

	@Override
	public boolean setUserId(String userID) {
		boolean flag = false;
		if (mContext == null) {
			return false;
		}
		flag = SharedPreferenceAccesser.saveStringData(mContext, USER_ID,
				userID);
		return flag;
	}

	@Override
	public String getUserID() {
		String userID = "";
		if (mContext == null) {
			return null;
		}

		if (SharedPreferenceAccesser.getStringData(mContext, USER_ID) != null) {
			userID = SharedPreferenceAccesser.getStringData(mContext, USER_ID);
		}

		return userID;
	}

	@Override
	public boolean setUserPassword(String password) {
		boolean flag = false;
		if (mContext == null) {
			return false;
		}
		flag = SharedPreferenceAccesser.saveStringData(mContext, USER_PWD,
				password);
		return flag;

	}

	@Override
	public String getUserPassword() {
		String password = "";
		if (mContext == null) {
			return null;
		}

		if (SharedPreferenceAccesser.getStringData(mContext, USER_PWD) != null) {
			password = SharedPreferenceAccesser.getStringData(mContext,
					USER_PWD);
		}

		return password;
	}

	@Override
	public boolean setUserType(String userType) {
		boolean flag = false;
		if (mContext == null) {
			return false;
		}
		flag = SharedPreferenceAccesser.saveStringData(mContext, USER_TYPE,
				userType);
		return flag;

	}

	@Override
	public String getUserType() {
		String userType = "";
		if (mContext == null) {
			return null;
		}

		if (SharedPreferenceAccesser.getStringData(mContext, USER_TYPE) != null) {
			userType = SharedPreferenceAccesser.getStringData(mContext,
					USER_TYPE);
		}

		return userType;
	}

	@Override
	public boolean setUserPhone(String userPhone) {
		boolean flag = false;
		if (mContext == null) {
			return false;
		}
		flag = SharedPreferenceAccesser.saveStringData(mContext, USER_PHONE,
				userPhone);
		return flag;

	}

	@Override
	public String getUserPhone() {
		String userPhone = "";
		if (mContext == null) {
			return null;
		}

		if (SharedPreferenceAccesser.getStringData(mContext, USER_PHONE) != null) {
			userPhone = SharedPreferenceAccesser.getStringData(mContext,
					USER_PHONE);
		}

		return userPhone;
	}

	@Override
	public boolean setUserNickname(String nickname) {
		boolean flag = false;
		if (mContext == null) {
			return false;
		}
		flag = SharedPreferenceAccesser.saveStringData(mContext, USER_NICKNAME,
				nickname);
		return flag;

	}

	@Override
	public String getUserNickName() {
		String userPhone = "";
		if (mContext == null) {
			return null;
		}

		if (SharedPreferenceAccesser.getStringData(mContext, USER_NICKNAME) != null) {
			userPhone = SharedPreferenceAccesser.getStringData(mContext,
					USER_NICKNAME);
		}

		return userPhone;
	}

}
