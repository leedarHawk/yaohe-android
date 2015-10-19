package com.collcloud.yaohe.activity.person.shenqingweishangjia;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.collcloud.yaohe.R;
import com.collcloud.yaohe.api.ApiAccess;
import com.collcloud.yaohe.common.base.BaseActivity;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.common.base.SupportDisplay;
import com.collcloud.yaohe.entity.Area;
import com.collcloud.yaohe.entity.BusinessArea;
import com.collcloud.yaohe.ui.adapter.FuJinShopCityAdapter;
import com.collcloud.yaohe.ui.adapter.FujinShopTownAdapter;
import com.collcloud.yaohe.ui.utils.CCLog;
import com.collcloud.yaohe.url.ContantsValues;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

/**
 * 申请商家- 选择商圈
 * 
 * @ClassName ChoiceDistrictActivity
 */
public class ChoiceDistrictActivity extends BaseActivity implements Callback {
	private static final String TAG = "选择商圈Activity";
	public static final String DEFAULT_TIPS = "当前区域未开通商圈";
	private ListView mLvLeft = null;
	private ListView mLvRight = null;

	public FuJinShopCityAdapter mAreaAdapter = null;
	public FujinShopTownAdapter mDistrictAdapter = null;

	private List<String> mAreaData = new ArrayList<String>();
	private List<String> mDistrictData = new ArrayList<String>();

	/** 区域列表 */
	private List<Area> mAreaList;
	private List<BusinessArea> mDistrictList;
	private int mLeftIndex = 0;

	private Handler mHandler;
	private String mStrCityID;
	private String mStrAreaID;
	private String mStrDistrictID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choice_district);
		mStrCityID = getStringExtra("current_city_id");
		getsAreaList(mStrCityID);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (mAreaAdapter != null) {
					mAreaAdapter.setSelectedItem(0);
					mAreaAdapter.notifyDataSetChanged();
				}
			}
		}, 500);
	}

	/**
	 * 获取当前区域By city_id
	 */
	private void getsAreaList(String cityId) {

		ApiAccess.showProgressDialog(ChoiceDistrictActivity.this, "获取区域信息中...",
				R.style.progress_dialog);
		HttpUtils http = new HttpUtils();
		// 用来封装参数
		RequestParams params = new RequestParams();
		params.addBodyParameter("city_id", cityId);

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.CITYAREA, params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						CCLog.v(TAG, "获取城市对应的区域失败了。");
						ApiAccess.dismissProgressDialog();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						ApiAccess.dismissProgressDialog();
						CCLog.v(TAG, "获取城市区域成功");
						JSONObject object, object2;
						// 网络返回结果状态信息
						String responseInfo = "";
						// 网络返回数据信息
						JSONArray responseData = null;
						// 网络访问状态码
						String code = "";
						// 网络返回消息
						String responseMsg = "";
						try {
							object = new JSONObject(arg0.result);
							CCLog.v("区域信息内容：", arg0.result);
							responseInfo = object.getString("status");
							responseData = object.getJSONArray("data");

							CCLog.v("城市区域信息：", responseData.toString());
							object2 = new JSONObject(responseInfo);
							code = object2.getString("code");
							responseMsg = object2.getString("message");
						} catch (JSONException e) {
							e.printStackTrace();
						}

						if (code.equals("0")) {// 请求成功
							if (mAreaList != null) {
								mAreaList.clear();
								mAreaList = new ArrayList<Area>();
							} else {
								mAreaList = new ArrayList<Area>();
							}
							if (responseData != null
									&& responseData.length() > 0) {
								mAreaList = parseJSONArray(responseData);
							}
							if (mAreaList != null && mAreaList.size() > 0) {
								getsDistrictList(mAreaList.get(0).getId());
							}
							if (mAreaData != null && mAreaData.size() > 0) {
								setAreaData();
							}

							// showToast("未定位到当前城市所在区域,请重试!");

						} else {// 返回提示信息
							CCLog.v(TAG, "请求区域数据时网络返回了错误信息");
							showToast(responseMsg);
						}
					}
				});
	}

	/**
	 * 区域Gson解析
	 */
	public ArrayList<Area> parseJSONArray(JSONArray response) {
		ArrayList<Area> areas = new ArrayList<Area>();
		if (mAreaData != null) {
			mAreaData.clear();
			mAreaData = new ArrayList<String>();
		}
		Area area = null;
		for (int i = 0; i < response.length(); i++) {
			area = new Area();
			JSONObject object = null;
			try {
				object = response.getJSONObject(i);
				area.setId(object.getString("id"));
				area.setTitle(object.getString("title"));
				mAreaData.add(object.getString("title"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			areas.add(area);
		}
		return areas;
	}

	/**
	 * 获取商家商圈By area_id
	 */
	private void getsDistrictList(String areaID) {

		ApiAccess.showProgressDialog(ChoiceDistrictActivity.this, "获取商圈信息中...",
				R.style.progress_dialog);
		HttpUtils http = new HttpUtils();
		// 用来封装参数
		RequestParams params = new RequestParams();

		params.addBodyParameter("area_id", areaID);

		http.send(HttpRequest.HttpMethod.POST, ContantsValues.SHANGQUAN,
				params, new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						CCLog.v(TAG, "获取商圈失败");
						ApiAccess.dismissProgressDialog();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						CCLog.v(TAG, "获取商圈成功");
						ApiAccess.dismissProgressDialog();

						JSONObject object, object2;
						// 网络返回商圈结果状态信息
						String responseInfo = "";
						// 网络返回商圈数据信息
						JSONArray responseData = null;
						// 网络访问商圈状态码
						String code = "";
						// 网络商圈返回消息
						String responseMsg = "";

						try {

							object = new JSONObject(arg0.result);
							CCLog.v("商圈信息内容：", arg0.result);
							responseInfo = object.getString("status");
							responseData = object.optJSONArray("data");
							object2 = new JSONObject(responseInfo);

							code = object2.getString("code");
							responseMsg = object2.getString("message");

							if (code.equals("0")) {

								if (mDistrictList != null) {
									mDistrictList.clear();
								}
								mDistrictList = new ArrayList<BusinessArea>();
								if (responseData != null
										&& responseData.length() > 0) {
									mDistrictList = parseJSONArrayShQ(responseData);
									refreshDistrictData();
								} else {
									if (mDistrictData != null) {
										mDistrictData.clear();
										mDistrictData = new ArrayList<String>();
										mDistrictData.add(DEFAULT_TIPS);
									} else {
										mDistrictData = new ArrayList<String>();
										mDistrictData.add(DEFAULT_TIPS);
									}
									refreshDistrictData();
								}

							} else {
								showToast(responseMsg);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	/**
	 * 商圈Gson解析
	 */
	public ArrayList<BusinessArea> parseJSONArrayShQ(final JSONArray response) {
		final ArrayList<BusinessArea> businessareas = new ArrayList<BusinessArea>();

		if (mDistrictData != null) {
			mDistrictData.clear();
			mDistrictData = new ArrayList<String>();
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				BusinessArea businessarea = null;
				for (int i = 0; i < response.length(); i++) {

					businessarea = new BusinessArea();
					JSONObject object = null;

					try {
						object = response.getJSONObject(i);
						businessarea.setId(object.getString("id"));
						businessarea.setTitle(object.getString("title"));
						mDistrictData.add(object.getString("title"));

					} catch (JSONException e) {

						e.printStackTrace();
					}
					businessareas.add(businessarea);
				}
			}
		}).start();

		return businessareas;
	}

	private void setAreaData() {
		mAreaAdapter = new FuJinShopCityAdapter(ChoiceDistrictActivity.this,
				mHandler, mAreaData);
		mLvLeft.setAdapter(mAreaAdapter);
	}

	private void setDistrictData() {
		mDistrictAdapter = new FujinShopTownAdapter(
				ChoiceDistrictActivity.this, mHandler, mDistrictData);
		mLvRight.setAdapter(mDistrictAdapter);
	}

	private void refreshDistrictData() {
		if (mDistrictAdapter != null) {
			mDistrictAdapter.refreshData(mDistrictData);
		} else {
			mDistrictAdapter = new FujinShopTownAdapter(
					ChoiceDistrictActivity.this, mHandler, mDistrictData);
			mLvRight.setAdapter(mDistrictAdapter);
		}
	}

	@Override
	protected void resetLayout() {

		LinearLayout rlLayout = (LinearLayout) this
				.findViewById(R.id.ll_shenqing_choice_district);
		SupportDisplay.resetAllChildViewParam(rlLayout);
		initTopOnlyBackTitle();
		setTopOnlyBackTitle("申请为商家");

		mLvLeft = (ListView) findViewById(R.id.lv_shenqing_shop_district_left_list);
		mLvRight = (ListView) findViewById(R.id.lv_shenqing_shop_district_right_list);
		mLvLeft.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mLvLeft.setSelection(0);
		mLvRight.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mHandler = new Handler(ChoiceDistrictActivity.this);
		mBtnIsCancelButton = true;
		mTopCancel = true;
		setDistrictData();
	}

	@Override
	public boolean handleMessage(Message msg) {
		Bundle data = msg.getData();

		switch (msg.what) {
		case 2: // 点击商圈-区域列表子项事件
			int areaIndex = data.getInt(
					IntentKeyNames.KEY_POPUP_WINDOW_SELECTED_CITY_INDEX, 0);
			mAreaAdapter.setSelectedItem(areaIndex);
			mAreaAdapter.notifyDataSetChanged();
			mLeftIndex = areaIndex;
			// 选择区域对应下的商圈列表内容
			if (mAreaList.size() > 0) {
				mDistrictData.clear();
				getsDistrictList(mAreaList.get(areaIndex).getId());
			}

			break;
		case 3: // 小分类列表子项点击事件
			final int districtIndex = data.getInt(
					IntentKeyNames.KEY_POPUP_WINDOW_SELECTED_TOWN_INDEX, 0);

			mDistrictAdapter.setSelectedItem(districtIndex);
			mDistrictAdapter.notifyDataSetChanged();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					String areaID = "";
					String districtID = "";
					String areaName = "";
					String districtName = "";
					if (mAreaList != null && mAreaList.size() > 0) {
						areaID = mAreaList.get(mLeftIndex).getId();
						areaName = mAreaList.get(mLeftIndex).getTitle();
						CCLog.v("areaID :" + areaID + "  区域名称：" + areaName);
					}
					if (mDistrictList != null && mDistrictList.size() > 0) {
						districtID = mDistrictList.get(districtIndex).getId();
						districtName = mDistrictList.get(districtIndex)
								.getTitle();
						CCLog.v("districtID :" + districtID + "  商圈名称："
								+ districtName);
					}
					cancelDefinedAction(areaID, areaName, districtID,
							districtName);
				}
			}, 500);

			break;
		default:
			break;
		}
		return false;
	}

	@Override
	protected void topCancelDefined() {
		super.topCancelDefined();
		cancelDefinedAction("", "", "", "");
	}

	@Override
	protected void onCancelButtonListener() {
		super.onCancelButtonListener();
		cancelDefinedAction("", "", "", "");
	}

	/**
	 * 返回前个页面传递的信息
	 * 
	 * @Title cancelDefinedAction
	 */
	private void cancelDefinedAction(String areaID, String areaName,
			String districtID, String districtName) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		intent.putExtra("areaID", areaID);
		intent.putExtra("areaName", areaName);
		intent.putExtra("districtID", districtID);
		intent.putExtra("districtName", districtName);
		intent.putExtras(bundle);
		setResult(22, intent);
		ChoiceDistrictActivity.this.finish();
	}
}
