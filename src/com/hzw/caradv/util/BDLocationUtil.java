package com.hzw.caradv.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption.DrivingPolicy;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.hzw.caradv.R;
import com.hzw.caradv.bean.LocateBaseInfo;

/**
 * 百度地图定位使用助手<br/>
 * 一定要配合Activity的生命周期来使用此类，在注意调用onStop和onDestroy的方法</br>
 * 目前实现了定位，和根据经纬度反编译地理位置</br>
 * @author zhiwei
 * 2015-3-27
 */
public class BDLocationUtil {
	private final String LOCAL_LEY = "CLocate";
	private static final int TIMEOUT = 1000 * 20;

	/**
	 * 百度地图定位类
	 */
	private LocationClient mLocationClient;
	/**
	 * 百度地图定位监听
	 */
	private BDLocationListener myListener = new MyLocationListener();

	/**
	 * 百度反地理编码
	 */
	private GeoCoder mGeoCoder;
	
	/**
	 * 百度兴趣点检索
	 */
	private PoiSearch mPoiSearch;
	
	/**
	 * 百度线路规划
	 */
	private RoutePlanSearch mRouteSearch;
	/**
	 * 地理位置信息实体类
	 */
	private LocateBaseInfo mLocateBaseInfo;

	/**
	 * 回调成功函数的时候要clear和赋值null
	 */
	private List<Object> mTempObjs;
	
	private OnLoactionListener mOnLoactionListener;
	private OnPoiListener mOnPoiListener;
	private OnRoutePlaneListener mOnRoutePlaneListener;
	
	private static BDLocationUtil mBDLocationUtil;
	private Context mContext;
	public static BDLocationUtil newInstance(Context applicationContext, OnLoactionListener listener){
		if(mBDLocationUtil == null){
			mBDLocationUtil = new BDLocationUtil(applicationContext);
			
		}
		
		mBDLocationUtil.setOnLoactionListener(listener);
		return mBDLocationUtil;
	}
	public static void destoryInstance(){
		if(mBDLocationUtil != null){
			mBDLocationUtil = null;
		}
	}
	
	private BDLocationUtil(Context applicationContext) {
		super();
		this.mContext = applicationContext;
		mLocationClient = new LocationClient(applicationContext);
		// 开启百度地图定位
		mLocationClient.setLocOption(createLocationOpt());
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		
		mGeoCoder = GeoCoder.newInstance();
		mGeoCoder.setOnGetGeoCodeResultListener(new MyGeoListener());
		
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(new MyPoiResultListener());
		
		mRouteSearch = RoutePlanSearch.newInstance();
		mRouteSearch.setOnGetRoutePlanResultListener(new MyRoutePlanListener());
	}

	private LocationClientOption createLocationOpt() {
		LocationClientOption locationOpt = new LocationClientOption();
		locationOpt
				.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
		locationOpt.setIsNeedAddress(true);
//		locationOpt.setTimeOut(TIMEOUT);
//		locationOpt.setOpenGps(true);
//		.setScanSpan(1000 * 60 * 10);
//		locationOpt.setScanSpan(800);
		locationOpt.setProdName(mContext.getString(R.string.app_name));
		// 百度经纬度坐标系 ：bd09ll
		locationOpt.setCoorType("bd09ll");
		return locationOpt;
	}

	/**
	 * 将请求的参数填充到List<Object>
	 * 
	 * @param objects
	 */
	private void loadObjsToTempList(Object... objects) {
		if (mTempObjs == null) {
			mTempObjs = new ArrayList<Object>(objects.length);
		}
		for (int i = 0; i < objects.length; i++) {
			mTempObjs.add(objects[i]);
		}
	}

	private void clearTempListObject() {
		if (mTempObjs != null && mTempObjs.size() > 0) {
			mTempObjs.clear();
			mTempObjs = null;
		}
	}
	
	/**
	 * 获取最后一次定位信息
	 * @return
	 */
	public LocateBaseInfo getLocateBaseInfo(){
//		BDLocation bdLocate = mLocationClient.getLastKnownLocation();
//		mLocateBaseInfo = parseBDLocation(bdLocate);
		if(mLocateBaseInfo == null){
			mLocateBaseInfo = new LocateBaseInfo();
			String json = UPreference.getString(mContext, LOCAL_LEY, "");
			if(!TextUtils.isEmpty(json)){
				mLocateBaseInfo = Common.json2Bean(json, mLocateBaseInfo);
			}
		}
		return mLocateBaseInfo;
	}
	
	/**
	 * 设置定位监听
	 * @param onLoactionSuccess
	 */
	public void setOnLoactionListener(OnLoactionListener onLoactionListener){
		this.mOnLoactionListener = onLoactionListener;
	}
	/**
	 * 设置路线规划监听
	 * @param onRoutePlaneListener
	 */
	public void setOnRoutePlanListener(OnRoutePlaneListener onRoutePlaneListener){
		this.mOnRoutePlaneListener = onRoutePlaneListener;
	}
	
	/**
	 * 设置兴趣点检索的监听
	 * @param onPoiListener
	 */
	public void setOnPoiListener(OnPoiListener onPoiListener){
		this.mOnPoiListener = onPoiListener;
	}
	
	/**
	 * 发起定位请求
	 * 
	 * @param objects
	 *            objects 第一个参数一定要是int，以便回调的时候区分操作
	 */
	public void reqeusetLocation(Object... objects) {
		//每次定位请求都把上一次定位请求所带的参数清除掉
		if (objects != null && objects.length > 0) {
			loadObjsToTempList(objects);
		}
		
//		long requestTime = UDateTime.getNowUnixTime();
//		long time = requestTime - mLastTime2Loc;
//		if(time < HOUR_HALF && mLocateBaseInfo != null){
//			if(mLocateBaseInfo == null){
//				String json = UPreference.getString(mContext, LOCAL_LEY, "");
//				mLocateBaseInfo = Common.json2Bean(json, mLocateBaseInfo);
//			}
//			if(mOnLoactionListener != null){
//				mOnLoactionListener.onBDLocationSuccess(getLocateBaseInfo(), mTempObjs);
//			}
//			clearTempListObject();
//			return;
//		}
//		mLastTime2Loc = requestTime;
		//请求定位的时候把之前的位置信息清空
		mLocateBaseInfo = null;
//		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
//		}
		int reqLocCode = mLocationClient.requestLocation();
		Log.d("888", "requestLocation code " + reqLocCode);
		mHandler.postDelayed(new Runnable(){
			@Override
			public void run() {
				mHandler.dispatchMessage(mHandler.obtainMessage());
			}
		}, TIMEOUT);
//		if(reqLocCode == 0){
//			locFaildOnLocal();
//		}
//		clearTempListObject();
	}
	/**
	 * @date 2015-7-7
	 */
	private void locFaildOnLocal() {
		LocateBaseInfo info = getLocateBaseInfo();
		if(!isClearTemp()){
			if(info != null){
				if(mOnLoactionListener != null){
					mOnLoactionListener.onBDLocationSuccess(info, mTempObjs);
				} 
			}else{
				if(mOnLoactionListener != null){
					mOnLoactionListener.onBDLocationFaild(mTempObjs);
				}
			}		
			clearTempListObject();
		}
	}
	/**
	 * @date 2015-7-7
	 * @return
	 */
	private boolean isClearTemp() {
		return !(mTempObjs != null && mTempObjs.size() > 0 && mTempObjs.get(0) instanceof Integer);
	}

	Handler mHandler = new Handler(new Handler.Callback(){
		@Override
		public boolean handleMessage(Message msg) {
			if(!isClearTemp()){
				locFaildOnLocal();
				return true;
			}
			return false;
		}
	});
	
	/**
	 * 发起地理位置名称获取详细信息，经纬度等。。。的请求
	 * 
	 * @param addressInfo
	 */
	public void geocode(String addressInfo) {
		mGeoCoder.geocode(new GeoCodeOption().city("").address(addressInfo));
	}

	/**
	 * 发起反编译地理位置信息，经纬度获取详细信息
	 * 
	 * @param lat
	 * @param lng
	 */
	public void reverseGeoCode(double lat, double lng, Object... objects) {
		if (objects != null && objects.length > 0) {
			loadObjsToTempList(objects);
		}
		mLocateBaseInfo = null;
//		if(mGeoCoder == null){
//			reqeusetLocation(mTempObjs);
//		}
		mGeoCoder = GeoCoder.newInstance();
		mGeoCoder.setOnGetGeoCodeResultListener(new MyGeoListener()); 
		mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption()
				.location(new LatLng(lat, lng)));
	}
	
	/**
	 * 搜索附近Poi</br>
	 * 要记得设置OnPoiListener
	 * @param poiNearbySearchOption
	 */
	public void poiSearchNear(PoiNearbySearchOption poiNearbySearchOption){
		mPoiSearch.searchNearby(poiNearbySearchOption);
	}
	
	/**
	 * 发起路线规划
	 * @param start 起点
	 * @param end 终点
	 * @param policy 行车策略
	 * @param objects
	 */
	public void routePlane(LatLng start, LatLng end, Object... objects){
		if (objects != null && objects.length > 0) {
			loadObjsToTempList(objects);
		}
		if(objects.length <= 1 || !(objects[1] instanceof DrivingPolicy)){
			throw new IllegalArgumentException("you must put policy in objects[1]");
		}
		DrivingPolicy p = (DrivingPolicy) objects[1];
		mRouteSearch.drivingSearch(new DrivingRoutePlanOption()
					.from(PlanNode.withLocation(start))
					.to(PlanNode.withLocation(end))
					.policy(p));
	}
	
	
//	反地理编码监听
	class MyGeoListener implements OnGetGeoCoderResultListener{
		@Override
		public void onGetGeoCodeResult(GeoCodeResult arg0) {
			if (arg0 == null || arg0.error != SearchResult.ERRORNO.NO_ERROR) {
				return;
			}else{
				//获得经纬度在反编译地理位置获取地理详细信息
				reverseGeoCode(arg0.getLocation().latitude,arg0.getLocation().longitude);
			}
		}
		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
			if (arg0 == null || arg0.error != SearchResult.ERRORNO.NO_ERROR) {
				return;
			}
			//反编译地理位置成功，赋值给mLocateBaseInfo
			AddressComponent ac = arg0.getAddressDetail();
			boolean isnull = isLoactionAddressNull(ac.province,ac.city,ac.district,ac.street,ac.streetNumber
					,"onGetReverseGeoCodeResult");
			if(!isnull){
				mLocateBaseInfo = new LocateBaseInfo();
				mLocateBaseInfo.lng = arg0.getLocation().longitude;
				mLocateBaseInfo.lat = arg0.getLocation().latitude;
				mLocateBaseInfo.province = ac.province;
				mLocateBaseInfo.city = ac.city;
				mLocateBaseInfo.district = ac.district;
				mLocateBaseInfo.street = ac.street;
				mLocateBaseInfo.street_number = ac.streetNumber;
				mLocateBaseInfo.writetime = UDateTime.dateToStr(new Date());
				String json = Common.bean2Json(mLocateBaseInfo);
				UPreference.putString(mContext, LOCAL_LEY, json);
//				UserAdo.setAccount(mContext, UserBean.GetUid(mContext),
//						mLocateBaseInfo.lng+"", mLocateBaseInfo.lat+"");
				if(mOnLoactionListener != null){
					mOnLoactionListener.onBDLocationSuccess(getLocateBaseInfo(), mTempObjs);
				}
			}else{
				if(mOnLoactionListener != null){
					mOnLoactionListener.onBDLocationFaild(mTempObjs);
				}else{
					Log.d("888", "LocationListener is null");
				}
			}
			clearTempListObject();
		}	
	}
	
	// 定位监听
	class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			boolean isnull = isLoactionAddressNull(location.getProvince(),location.getCity(),
					location.getDistrict(),location.getStreet(),
					location.getStreetNumber(),"onReceiveLocation");
			
			if (!isnull) {
				mLocateBaseInfo = new LocateBaseInfo();
				mLocateBaseInfo.lng = location.getLongitude();
				mLocateBaseInfo.lat = location.getLatitude();
				mLocateBaseInfo.province = location.getProvince();
				mLocateBaseInfo.city = location.getCity();
				mLocateBaseInfo.district = location.getDistrict();
				mLocateBaseInfo.street = location.getStreet();
				mLocateBaseInfo.street_number = location.getStreetNumber();
				mLocateBaseInfo.writetime = UDateTime.dateToStr(new Date());
				String json = Common.bean2Json(mLocateBaseInfo);
				UPreference.putString(mContext, LOCAL_LEY, json);
//				UserAdo.setAccount(mContext, UserBean.GetUid(mContext),
//						mLocateBaseInfo.lng+"", mLocateBaseInfo.lat+"");
				Log.d("888", "Location save " + json);
				if(mOnLoactionListener != null){
					if(!isClearTemp()){
						mOnLoactionListener.onBDLocationSuccess(getLocateBaseInfo(), mTempObjs);
					}
				}else{
					Log.d("888", "LocationListener is null");
				}
				clearTempListObject();

			} else {
				reverseGeoCode(location.getLatitude(), location.getLongitude());
			}
		}

	}
	
	 /**
     * 搜索百度地图的停车场信息
     * 
     * @author zhiwei 2015-2-4
     */
    class MyPoiResultListener implements OnGetPoiSearchResultListener {
        @Override
        public void onGetPoiDetailResult(PoiDetailResult arg0) {
        	if (arg0.error != SearchResult.ERRORNO.NO_ERROR) {
        	}else{
        	}
        }

        @Override
        public void onGetPoiResult(PoiResult arg0) {
            if (arg0.error != SearchResult.ERRORNO.NO_ERROR) {
                System.out.println("详情检索失败");
                if(mOnPoiListener != null){
            		mOnPoiListener.onPoiFaild(mTempObjs);
            	}
            } else {
            	if(mOnPoiListener != null){
            		mOnPoiListener.onPoiSuccess(arg0.getAllPoi(), mTempObjs);
            	}
            	clearTempListObject();
            }
        }
    }
    
    class MyRoutePlanListener implements OnGetRoutePlanResultListener {  
	    public void onGetWalkingRouteResult(WalkingRouteResult result) {  
	        //获取步行线路规划结果  
	    }  
	    public void onGetTransitRouteResult(TransitRouteResult result) {  
	        //获取公交换乘路径规划结果  
	    }  
	    public void onGetDrivingRouteResult(DrivingRouteResult result) {  
	        //获取驾车线路规划结果  
	    	if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
	    		if(mOnRoutePlaneListener != null){
	    			mOnRoutePlaneListener.onRoutePlaneFaild(mTempObjs);
	    		}
	    		clearTempListObject();
	            return;
	        }
	    	if(mOnRoutePlaneListener != null){
	    		mOnRoutePlaneListener.onRoutePlaneSuccess(result.getRouteLines(), mTempObjs);
	    	}
	    	clearTempListObject();
//	    	DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
//	    	mBaiduMap.setOnMarkerClickListener(overlay);  
//	    	DrivingRouteLine line = result.getRouteLines().get(0);
//            overlay.setData(line);  
//            overlay.addToMap();  
//            overlay.zoomToSpan();  
            
	    }  
	};
	
	private boolean isLoactionAddressNull(String p, String c, String d, String s, String sn, String method) {
		if (p == null 
				|| c == null
				|| d == null
				|| s == null
				|| "".equals(p)
				|| "".equals(c)
				|| "".equals(d)
				|| "".equals(s)) {
			return true;
		}
		return false;
	}
	
	public void onPause(){
		mBDLocationUtil.setOnLoactionListener(null);
		mLocationClient.stop();
	}
	
	public void onResume(OnLoactionListener listener){
		mBDLocationUtil.setOnLoactionListener(listener);
//		mLocationClient.start();
	}
	
	public void onStop() {
//		if(mLocationClient.isStarted()){
			
//		}
	}
	
	public void onDestroy() {
		if(mGeoCoder != null)
			mGeoCoder.destroy();
		if(mPoiSearch != null)
			mPoiSearch.destroy();
		if(mBDLocationUtil != null)
//			mBDLocationUtil = null;
		System.gc();
	}
	
	public static interface OnLoactionListener{
		/**
		 * 定位成功之后回调
		 * @param LocateBaseInfo
		 * @param objs
		 */
		void onBDLocationSuccess(LocateBaseInfo locate, List<Object> objs);
		/**
		 * 定位失败之后回调
		 * @param objs
		 */
		void onBDLocationFaild(List<Object> objs);
	}
	
	public static interface OnPoiListener{
		/**
		 * 兴趣点检索成功之后回调
		 * @param list
		 * @param objs
		 */
		void onPoiSuccess(List<PoiInfo> list, List<Object> objs);
		/**
		 * 兴趣点检索失败之后回调
		 * @param objs
		 */
		void onPoiFaild(List<Object> objs);
	}
	
	public static interface OnRoutePlaneListener{
		/**
		 * 路线规划成功
		 * @param list
		 * @param objs
		 */
		void onRoutePlaneSuccess(List<DrivingRouteLine> list, List<Object> objs);
		/**
		 * 路线规划失败
		 * @param objs
		 */
		void onRoutePlaneFaild(List<Object> objs);
	}
}
