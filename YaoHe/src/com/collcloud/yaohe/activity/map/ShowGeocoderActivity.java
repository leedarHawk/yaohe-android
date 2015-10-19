package com.collcloud.yaohe.activity.map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.collcloud.yaohe.R;
import com.collcloud.yaohe.common.base.GlobalConstant;
import com.collcloud.yaohe.common.base.GlobalVariable;
import com.collcloud.yaohe.common.base.IntentKeyNames;
import com.collcloud.yaohe.ui.model.AmapGencodeInfo;
import com.collcloud.yaohe.ui.utils.AMapUtil;
import com.collcloud.yaohe.ui.utils.UIHelper;

/**
 * 高德地图API-地理编码位置信息显示
 * 
 * @ClassName ShowGeocoderActivity
 * @Description
 * @author CollCloud_小米
 */
public class ShowGeocoderActivity extends Activity implements
		OnGeocodeSearchListener, OnClickListener {
	private Button mBtnBack;
	private ProgressDialog progDialog = null;
	private GeocodeSearch geocoderSearch;
	private String addressName;
	private AMap aMap;
	private MapView mapView;
	private Marker geoMarker;
	private Marker regeoMarker;
	private LatLonPoint latLonPoint = new LatLonPoint(40.003662, 116.465271);

	private TextView mTvShopTitle; // 显示商铺名称
	private TextView mTvShopAddress; // 显示商铺地址
	private AmapGencodeInfo mAmapGencodeInfo; // 传递过来的对象，包含商铺名称，地址，和城市名

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_amap_show_geocoder);
		mapView = (MapView) findViewById(R.id.map_show_geocoder_location);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
		getExtras();
	}

	private void getExtras() {
		if (getIntent().getSerializableExtra(
				IntentKeyNames.KEY_GEOCODER_LOCATION) != null) {
			mAmapGencodeInfo = (AmapGencodeInfo) getIntent()
					.getSerializableExtra(IntentKeyNames.KEY_GEOCODER_LOCATION);
			if (mAmapGencodeInfo.getmStrQueryName() != null) {
				mTvShopTitle.setText(mAmapGencodeInfo.getmStrQueryName());
			} else {
				mTvShopTitle.setText(GlobalConstant.EMPTY_STRING);
			}
			if (mAmapGencodeInfo.getmStrQueryAddress() != null) {
				mTvShopAddress.setText(mAmapGencodeInfo.getmStrQueryAddress());
			} else {
				mTvShopAddress.setText(GlobalConstant.EMPTY_STRING);
			}
			if (GlobalVariable.sChoiceCity != null) {
				getLatlon(mAmapGencodeInfo.getmStrQueryAddress(),
						GlobalVariable.sChoiceCity);
			} else {
				getLatlon(mAmapGencodeInfo.getmStrQueryAddress(),
						GlobalVariable.sChoiceCity);
			}
		} else {
			getLatlon("朝阳区建国路81号楼华贸中心B1楼", "北京");
		}
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
			geoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
			regeoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		}
		mTvShopTitle = (TextView) findViewById(R.id.tv_map_geocoder_location_title);
		mTvShopAddress = (TextView) findViewById(R.id.tv_map_geocoder_location_address);
		mTvShopTitle.setOnClickListener(this);

		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
		progDialog = new ProgressDialog(this);
		mBtnBack = (Button) findViewById(R.id.btn_map_show_geocoder_location_back);
		mBtnBack.setOnClickListener(this);

	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	/**
	 * 显示进度条对话框
	 */
	public void showDialog() {
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage("正在获取地址");
		progDialog.show();
	}

	/**
	 * 隐藏进度条对话框
	 */
	public void dismissDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * 响应地理编码
	 */
	public void getLatlon(final String name, String cityID) {
		showDialog();
		GeocodeQuery query = new GeocodeQuery(name, cityID);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
		geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
	}

	/**
	 * 响应逆地理编码
	 */
	public void getAddress(final LatLonPoint latLonPoint) {
		showDialog();
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 1000,
				GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
	}

	/**
	 * 地理编码查询回调
	 */
	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
		dismissDialog();
		if (rCode == 0) {
			if (result != null && result.getGeocodeAddressList() != null
					&& result.getGeocodeAddressList().size() > 0) {
				GeocodeAddress address = result.getGeocodeAddressList().get(0);
				aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						AMapUtil.convertToLatLng(address.getLatLonPoint()), 15));
				geoMarker.setPosition(AMapUtil.convertToLatLng(address
						.getLatLonPoint()));
				// addressName = "经纬度值:" + address.getLatLonPoint() + "\n位置描述:"
				// + address.getFormatAddress();
				// UIHelper.ToastMessage(ShowGeocoderActivity.this,
				// addressName);
			} else {
				UIHelper.ToastMessage(ShowGeocoderActivity.this,
						R.string.no_result);
			}

		} else if (rCode == 27) {
			UIHelper.ToastMessage(ShowGeocoderActivity.this,
					R.string.error_network);
		} else if (rCode == 32) {
			UIHelper.ToastMessage(ShowGeocoderActivity.this, R.string.error_key);
		} else {
			UIHelper.ToastMessage(ShowGeocoderActivity.this,
					getString(R.string.error_other) + rCode);
		}
	}

	/**
	 * 逆地理编码回调
	 */
	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		dismissDialog();
		if (rCode == 0) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				addressName = result.getRegeocodeAddress().getFormatAddress()
						+ "附近";
				aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						AMapUtil.convertToLatLng(latLonPoint), 15));
				regeoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint));
				UIHelper.ToastMessage(ShowGeocoderActivity.this, addressName);
			} else {
				UIHelper.ToastMessage(ShowGeocoderActivity.this,
						R.string.no_result);
			}
		} else if (rCode == 27) {
			UIHelper.ToastMessage(ShowGeocoderActivity.this,
					R.string.error_network);
		} else if (rCode == 32) {
			UIHelper.ToastMessage(ShowGeocoderActivity.this, R.string.error_key);
		} else {
			UIHelper.ToastMessage(ShowGeocoderActivity.this,
					getString(R.string.error_other) + rCode);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/**
		 * 响应地理编码按钮
		 */
		// case R.id.tv_map_geocoder_location_address:
		// break;
		case R.id.btn_map_show_geocoder_location_back:
			this.finish();
			break;
		default:
			break;
		}

	}
}