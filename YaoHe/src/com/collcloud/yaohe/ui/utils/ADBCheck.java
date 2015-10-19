package com.collcloud.yaohe.ui.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.os.Build;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.collcloud.yaohe.common.base.GlobalConstant;

/**
 * USB调试模式
 * 
 * @ClassName: ADBCheck
 * @Description: 判断程序是否开启 USB调试模式。（正式发布时，不允许个人调试本吆喝APP）
 * @author CollCloud_小米
 */
public class ADBCheck {
	private static final int ENABLE = 1;
	private static final int DISABLE = 0;

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static boolean isADBEnabled(ContentResolver contentResolver) {
		boolean result = true;
		try {
			int adbState = -1;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				adbState = Settings.Global.getInt(contentResolver,
						Settings.Global.ADB_ENABLED);
			} else {
				adbState = Settings.Secure.getInt(contentResolver,
						Settings.Secure.ADB_ENABLED);
			}
			switch (adbState) {
			case ENABLE:
				result = true;
				break;
			case DISABLE:
				result = false;
				break;
			default:
				result = false;
				break;
			}
		} catch (SettingNotFoundException e) {
		}
		if (GlobalConstant.RELEASE) {
			return result;
		} else {
			return false;
		}
	}

}
