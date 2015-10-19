package com.collcloud.yaohe.api;

import android.content.Context;

import com.collcloud.yaohe.R;

public class ApiAccessErrorManager {

	public static final int UNKNOWN_ERROR = -1;
	public static final int OK = 0;
	public static final int NETWORK_DISABLED = 1;
	public static final int INPUT_DATA_ERROR = 2;
	public static final int TIMEOUT = 3;
	public static final int URL_INVALID = 4;
	public static final int RESPONSE_DATA_INVALID = 5;
	public static final int EXTERNAL_LINK_MESSAGE = 6;

	public static final int LOGIN_REDIRECT301 = 301;
	public static final int LOGIN_REDIRECT302 = 302;

	public static boolean isNotOK(int errorCode) {
		if (errorCode != OK) {
			return true;
		}
		return false;
	}

	public static boolean isRedirect(int errorCode) {
		if (errorCode == LOGIN_REDIRECT301 || errorCode == LOGIN_REDIRECT302) {
			return true;
		}
		return false;
	}

	public static boolean postErrorCode(int errorCode) {
		if (errorCode == OK || errorCode == LOGIN_REDIRECT301
				|| errorCode == LOGIN_REDIRECT302) {
			return false;
		}
		return true;
	}

	public static String getNetworkDisabled(Context context) {
		return context.getResources().getString(R.string.network_not_connected);
	}

	public static String getInputError(Context context) {
		return context.getResources().getString(
				R.string.api_input_error_exception);
	}

	public static String getTimeOutException(Context context) {
		return context.getResources().getString(
				R.string.api_request_timeout_exception);
	}

	public static String getUrlInvaildException(Context context) {
		return context.getResources().getString(
				R.string.api_url_invaild_exception);
	}

	public static String getResponseDataInvalid(Context context) {
		return context.getResources().getString(R.string.xml_parser_failed);
	}

	public static String getMessage(int errorCode, Context context) {
		String result = getNetworkDisabled(context);
		switch (errorCode) {
		case NETWORK_DISABLED:
			result = getNetworkDisabled(context);
			break;
		case INPUT_DATA_ERROR:
			result = getInputError(context);
			break;
		case TIMEOUT:
			result = getTimeOutException(context);
			break;
		case URL_INVALID:
			result = getUrlInvaildException(context);
			break;
		case RESPONSE_DATA_INVALID:
			result = getResponseDataInvalid(context);
			break;
		default:
			break;
		}
		return result;
	}
}
