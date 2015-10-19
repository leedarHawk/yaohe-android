package com.collcloud.yaohe.api;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * API请求网络数据
 * 
 * @ClassName ApiAccess
 * @Description 从这里发出请求，获得相应模块的业务数据
 * @author CollCloud_小米
 */
public class ApiAccess {
	@SuppressWarnings("unused")
	private Context mContext;
	private  ProgressDialog mProgressDialog;

//	public static ApiAccess getInstance(Context context) {
//		if (apiAccess == null) {
//			apiAccess = new ApiAccess();
//			apiAccess.mContext = context;
//		}
//		return apiAccess;
//	}
	
	public ApiAccess() {
		
	}

	@SuppressLint("InlinedApi")
	public  void showProgressDialog(Activity activity) {
		// mProgressDialog = new ProgressDialog(activity);
		// mProgressDialog.setCancelable(false);
		// mProgressDialog.setMessage(GlobalConstant.PROGRESS_DIALOG_MESSAGE);
		// mProgressDialog.show();

		mProgressDialog = new ProgressDialog(activity,
				ProgressDialog.THEME_HOLO_LIGHT);
		mProgressDialog.setMessage("加载中...");
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.show();
	}

	public  void showProgressDialog(Activity activity, String msg) {
		mProgressDialog = new ProgressDialog(activity,
				ProgressDialog.THEME_HOLO_LIGHT);
		mProgressDialog.setMessage(msg);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setCanceledOnTouchOutside(true);
		mProgressDialog.show();
	}

	public  void showProgressDialog(Activity activity, String msg,
			int theme) {

		mProgressDialog = new ProgressDialog(activity, theme);
		mProgressDialog.setMessage(msg);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setCanceledOnTouchOutside(true);
		mProgressDialog.show();
	}
	
//	public void showLoadingDialog(Activity activity, String msg,
//			int theme) {
//
//		mProgressDialog = new ProgressDialog(activity, theme);
//		mProgressDialog.setMessage(msg);
//		mProgressDialog.setCancelable(true);
//		mProgressDialog.setCanceledOnTouchOutside(true);
//		mProgressDialog.show();
//	}

	public  boolean isProgressDialogShow(Activity activity) {
		if (mProgressDialog == null) {
			return false;
		}
		return mProgressDialog.isShowing();
	}

	public  void dismissProgressDialog() {
		if (mProgressDialog == null) {
			return;
		}
		if (mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
	}

//	public interface ApiAccessResponseListener {
//		void onApiAccessResponse(ResponseDataToUI responseData);
//
//	}

//	private  ApiAccessResponseListener mApiResponseListener;
//
//	private  void sendResponseDataToUILayer(ResponseDataToUI responseData) {
//		if (mApiResponseListener != null) {
//			mApiResponseListener.onApiAccessResponse(responseData);
//		}
//	}
//
//	public void sendRequestListener(ApiAccessResponseListener listener) {
//		mApiResponseListener = listener;
//	}

	public void assessPostNet(String url, RequestParams params) {
		RequestParams param = null;
		if (params != null) {
			param = params;
		}
		assessNet(HttpRequest.HttpMethod.POST, url, param);
	}

	public void assessGetNet(String url) {
		assessNet(HttpRequest.HttpMethod.GET, url, null);
	}

	private void assessNet(HttpRequest.HttpMethod httpMethod, String url,
			RequestParams params) {
		HttpUtils http = new HttpUtils();

		http.send(httpMethod, url, params, new RequestCallBack<String>() {
			@Override
			public void onStart() {
			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {

				ResponseDataToUI responseData = new ResponseDataToUI();
				responseData.result = responseInfo.result;
				responseData.netWorkErrorCode = ApiAccessErrorManager.OK;
				//sendResponseDataToUILayer(responseData);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				ResponseDataToUI responseData = new ResponseDataToUI();
				responseData.netWorkErrorCode = ApiAccessErrorManager.RESPONSE_DATA_INVALID;
				//sendResponseDataToUILayer(responseData);
			}
		});
	}

	// ******************** get url *************************//

	/**
	 * 获取首页轮换图列表 URL
	 */
	public String parseHomeRotateUrl(String cityid) {
		String url;
		url = ContantsValues.HOME_BANNER_ROTATE_URL + "&city_id=" + cityid;
		return url;
	}

	/**
	 * 获取首页分类列表 URL
	 */
	public String parseHomeTypeUrl(String cityid) {
		String url;
		url = ContantsValues.HOME_TYPE_LIST_URL + "&city_id=" + cityid;
		return url;
	}

}
