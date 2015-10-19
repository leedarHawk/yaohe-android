package com.collcloud.yaohe.timertask;

import android.os.AsyncTask;

/**
 * @类说明 计时器，用于验证码的倒计时
 * @author 赵洋洋 E-mail:395670690@qq.com
 * @version 创建时间：2015年7月2日 下午3:02:18
 */
public class TimerTask extends AsyncTask<String, Integer, Integer> {
	int count = 60;
	TimerCallBack callBack;

	public TimerTask(TimerCallBack callBack) {

		this.callBack = callBack;
	}

	@Override
	protected Integer doInBackground(String... params) {
		try {
			while (count > 0) {

				Thread.sleep(1000);
				count--;
				this.publishProgress(count);
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {

		super.onProgressUpdate(values);
		if (callBack != null) {
			callBack.notify(values[0]);

		}
	}

	public interface TimerCallBack {
		public void notify(int currentCount);
	}

}
