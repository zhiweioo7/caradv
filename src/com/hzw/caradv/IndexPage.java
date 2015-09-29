package com.hzw.caradv;

import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.hzw.caradv.bean.LocateBaseInfo;
import com.hzw.caradv.bean.ServiceProviderBean;
import com.hzw.caradv.bean.ServiceProviderJson;
import com.hzw.caradv.dao.CarDao;
import com.hzw.caradv.util.BDLocationUtil;
import com.hzw.caradv.util.BDLocationUtil.OnLoactionListener;
import com.hzw.caradv.util.CutText;
import com.hzw.caradv.util.HActivityManager;
import com.hzw.caradv.view.MapZoomView;

public class IndexPage extends BasePage implements OnLoactionListener {
	public static final int LOC_ME = 2;
	public static final int REQ_FIRSTTIME = 10;
	private static final int HTTP_ACTION_GETSERVICEPROVIDER = 0;

	private MapView mMap;
	private BaiduMap mBaiduMap;
	private BDLocationUtil mBDLocationUtil;
	private Button mLocMe;
	private MapZoomView mZoomView;
	
	private CarDao mCarDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.index);
		initActivity(R.string.index_title, false);
		mBDLocationUtil.reqeusetLocation(REQ_FIRSTTIME);
		execTask(HTTP_ACTION_GETSERVICEPROVIDER, getIdentification());
	}

	@Override
	public void initViews() {
		mMap = find(R.id.map);
		mBaiduMap = mMap.getMap();
		mLocMe = find(R.id.locme);
		mZoomView = find(R.id.mapZoomView1);

		mBDLocationUtil = BDLocationUtil.newInstance(getApplicationContext(),
				this);
		mCarDao = new CarDao(this);
	}

	@Override
	public void setupViews() {
		mMap.showZoomControls(false);
		mMap.removeViewAt(1);
		mZoomView.setMapView(mMap);
		mLocMe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mBDLocationUtil.reqeusetLocation(LOC_ME);
			}
		});
		mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {
			@Override
			public void onMapStatusChange(MapStatus arg0) {
			}

			@Override
			public void onMapStatusChangeFinish(MapStatus arg0) {
				refreshScaleAndZoomControl();
			}

			@Override
			public void onMapStatusChangeStart(MapStatus arg0) {
			}
		});
		mBaiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
				refreshScaleAndZoomControl();
			}
		});
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng arg0) {
				mBaiduMap.hideInfoWindow();
			}

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}
		});
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker arg0) {
				Bundle data = arg0.getExtraInfo();
				if (data != null) {
					markerClick(data, arg0);
					return true;
				} else {
					return false;
				}
			}
		});
		refreshScaleAndZoomControl();
	}

	/**
	 * 更新缩放按钮的状态
	 * 
	 * @date 2015-7-9
	 */
	private void refreshScaleAndZoomControl() {
		mZoomView
				.refreshZoomButtonStatus(Math.round(mBaiduMap.getMapStatus().zoom));
	}

	@Override
	public void refreshUI(int id, Object params) {
		if(params == null){
			showInfoPrompt(R.string.nodata);
			return ;
		}
		if (id == HTTP_ACTION_GETSERVICEPROVIDER) {
			ServiceProviderJson json = (ServiceProviderJson) params;
			if(json.code == 0){
				fillMap(json.data);
			}else{
				showInfoPrompt(json.msg);
			}
		}
	}

	@Override
	public Object doingBackground(int id, String idName) {
		Object obj = null;
		if(id == HTTP_ACTION_GETSERVICEPROVIDER){
			obj = mCarDao.getServiceProvider();
		}
		return obj;
	}

	@Override
	public void onBDLocationSuccess(LocateBaseInfo locate, List<Object> objs) {
		int requestCode = (Integer) objs.get(0);
		if (requestCode <= 0) {
			Log.d("888", "no requestCode ");
			return;
		}

		if (requestCode == REQ_FIRSTTIME) {
			MyLocationData mLoc = new MyLocationData.Builder().direction(100)
					.latitude(locate.lat).longitude(locate.lng).build();
			mBaiduMap.setMyLocationData(mLoc);
			mBaiduMap.setMyLocationEnabled(true);
			BitmapDescriptor bitmap = BitmapDescriptorFactory
					.fromResource(R.drawable.map_selfposition);
			MyLocationConfiguration config = new MyLocationConfiguration(
					LocationMode.FOLLOWING, false, bitmap);
			mBaiduMap.setMyLocationConfigeration(config);
			anim2Position(mBaiduMap, locate.lat, locate.lng, true);
		} else if (requestCode == LOC_ME) {
			anim2Position(mBaiduMap, locate.lat, locate.lng, true);
		}
//		fillMap(locate.lat, locate.lng);
	}

	/**
	 * @date 2015-7-6
	 * @param report_ld
	 */
	private void fillMap(List<ServiceProviderBean> list) {
		if (list != null && list.size() > 0) {
			mBaiduMap.clear();
			int len = list.size();
			for (int i = 0; i < len; i++) {
				ServiceProviderBean bean = list.get(i);
				Bundle data = new Bundle();
				// data.putSerializable("MarkerType",
				// MapMarkerType.SOG_CARFRIEND);
				data.putSerializable("info", bean);
//				data.putDouble("lat", lat);
//				data.putDouble("lng", lng);
//				OverlayOptions oopts = createSogMarker(new Object(), data,
//						generateLat(lat, i), generateLng(lng, i), 4 - i,
//						R.drawable.map_marker);
				OverlayOptions oopts = createSogMarker(new Object(), data,
						bean.lat, bean.lng, len - i,
						R.drawable.map_marker);
				if (mBaiduMap != null) {
					if (oopts != null) { 
						LatLng llText = new LatLng((bean.lat + 0.000210),
								(bean.lng - 0.000020));  
						//构建文字Option对象，用于在地图上添加文字  
						OverlayOptions textOption = new TextOptions()  
						    .bgColor(0xAAFFFF00)  
						    .fontSize(24)  
						    .fontColor(0xFFFF00FF)  
						    .text(bean.name)  
						    .position(llText)
						    .zIndex(Integer.MAX_VALUE);
						//在地图上添加该文字对象并显示  
						mBaiduMap.addOverlay(textOption);
						mBaiduMap.addOverlay(oopts);
					}
				}

			}
		}
	}

	/**
	 * 地图标注点击事件
	 * 
	 * @param data
	 * @param marker
	 */
	private void markerClick(Bundle data, Marker marker) {
		if(data == null 
				|| !data.containsKey("info")){
			return;
		}
		View view = null;
		ServiceProviderBean info = (ServiceProviderBean) data.getSerializable("info");
		int infoWindowHeight = 0;
		if (data != null) {
			// 构建弹窗View
			view = createBaiduNaviView(data);
			infoWindowHeight = getResourceDimen(R.dimen.px64);
		}

		LatLng ll = new LatLng(info.lat, info.lng);
		MapStatusUpdate m = MapStatusUpdateFactory.newLatLngZoom(ll, 19);
		mBaiduMap.animateMapStatus(m);

		InfoWindow infoWindow = new InfoWindow(view, ll, infoWindowHeight * -1);
		mBaiduMap.showInfoWindow(infoWindow);

	}

	/**
	 * 点击百度停车的弹出的窗口
	 */
	private View createBaiduNaviView(final Bundle data) {
		View view = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.server_map_marker, null);
		TextView name = (TextView) view.findViewById(R.id.title_text);
		TextView address = (TextView) view.findViewById(R.id.address_text);
		TextView tell = (TextView) view.findViewById(R.id.lable_text);
		TextView desc = (TextView) view.findViewById(R.id.desc);
		ImageView call_img = (ImageView) view.findViewById(R.id.call_img);
		final ServiceProviderBean info = (ServiceProviderBean) data.getSerializable("info");
		call_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (TextUtils.isEmpty(info.tel)) {
					showInfoPrompt(getString(R.string.index_nonumber));
					return;
				}
				showRemindDialog(getString(R.string.index_makephonecall),
						info.tel, getString(R.string.index_call),
						getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
//								Intent intent = new Intent(Intent.ACTION_CALL,
//										Uri.parse("tel:" + info.tel));
//								startActivity(intent);
								Intent intent = new Intent(Intent.ACTION_DIAL,
										Uri.parse("tel:" + info.tel));
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
							}

						}, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {

							}
						});

			}
		});
		String companyName = info.name;
		int length = CutText.stringLength(companyName);
		if(length > CutText.TITLE_LENGTH){
			companyName = new StringBuffer(companyName)
				.insert((companyName.length()/2), "\n").toString();
		}
		String addressStr = info.address;
		length = CutText.stringLength(addressStr);
		if(length > CutText.TITLE_LENGTH){
			companyName = new StringBuffer(addressStr)
				.insert((addressStr.length()/2), "\n").toString();
		}
//		initTitle(companyName);
		name.setText(companyName);
		address.setText(addressStr);
		tell.setText(info.tel);
		desc.setText(info.smalldesc);
		return view;
	}

	/**
	 */
	private OverlayOptions createSogMarker(Object info, Bundle data,
			double lat, double lng, int zIndex, int drawable) {
		// LatLng ll = new LatLng(info.lat, info.lng); 114.063038,22.511886
		BitmapDescriptor bitmap = BitmapDescriptorFactory
				.fromResource(drawable);
		// if (mSogBitmap == null) {
		// mSogBitmap =
		// }
		LatLng ll = new LatLng(lat, lng);
		MarkerOptions overlay = new MarkerOptions().position(ll).icon(bitmap)
				.title("a").extraInfo(data);
		return overlay;
	}

	/**
	 * 移动到地图
	 * 
	 * @date 2015-5-26
	 * @param baiduMap
	 * @param lat
	 * @param lng
	 * @param isUpdateLocation
	 */
	private void anim2Position(BaiduMap baiduMap, double lat, double lng,
			boolean isUpdateLocation) {
		LatLng ll = new LatLng(lat, lng);
		if (isUpdateLocation) {
			MyLocationData mLoc = new MyLocationData.Builder().direction(100)
					.accuracy(50).latitude(lat).longitude(lng).build();
			baiduMap.setMyLocationData(mLoc);
			baiduMap.setMyLocationEnabled(true);
			BitmapDescriptor bitmap = BitmapDescriptorFactory
					.fromResource(R.drawable.map_selfposition);
			MyLocationConfiguration config = new MyLocationConfiguration(
					LocationMode.NORMAL, true, bitmap);
			baiduMap.setMyLocationConfigeration(config);
		}

		// 将地图放大到当前位置
		MapStatusUpdate mapStatus = MapStatusUpdateFactory
				.newLatLngZoom(ll, 17);
		baiduMap.animateMapStatus(mapStatus);
	}

	@Override
	public void onBDLocationFaild(List<Object> objs) {
		showInfoPrompt(R.string.baiduerror);
	}

	@Override
	public void onResume() {
		super.onResume();
		mMap.onResume();
		mBDLocationUtil.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		mBDLocationUtil.onPause();
		mMap.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
		mBDLocationUtil.onStop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mMap.onDestroy();
		HActivityManager.getActivityManager().finishAllActivity();
	}
	
	long mLastPressBackTime;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			// boolean isFinish = onBack();
			// if (isFinish) {
			// finish();
			// }
			// return isFinish;
			long secondTime = System.currentTimeMillis();
			if (secondTime - mLastPressBackTime > 2000) { //
				// 如果两次按键时间间隔大于2秒，则不退出
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mLastPressBackTime = secondTime;// 更新firstTime
				return true;
			} else { // 两次按键小于2秒时，退出应用
//				HActivityManager.getActivityManager().finishAllActivity();
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
