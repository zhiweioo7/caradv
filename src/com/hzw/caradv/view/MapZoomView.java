/**
 * MaoZoomView
 * 下午3:14:08
 */
package com.hzw.caradv.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.hzw.caradv.R;

/**
 * 
 * @author zhiwei
 * @time 下午3:14:08
 */
public class MapZoomView extends LinearLayout implements OnClickListener {
	private Button mButtonZoomin;
	private Button mButtonZoomout;
	private MapView mapView;
	private int maxZoomLevel;
	private int minZoomLevel;

	public MapZoomView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MapZoomView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		if (!isInEditMode()) {
			View view = LayoutInflater.from(getContext()).inflate(
					R.layout.zoom_controls_layout, null);
			mButtonZoomin = (Button) view.findViewById(R.id.map_zoomin);
			mButtonZoomout = (Button) view.findViewById(R.id.map_zoomout);
			mButtonZoomin.setOnClickListener(this);
			mButtonZoomout.setOnClickListener(this);
			addView(view);
		}
	}

	@Override
	public void onClick(View v) {
		if (mapView == null) {
			throw new NullPointerException(
					"you can call setMapView(MapView mapView) at first");
		}
		switch (v.getId()) {
		case R.id.map_zoomin: {
			mapView.getMap().animateMapStatus(MapStatusUpdateFactory.zoomIn());
			break;
		}
		case R.id.map_zoomout: {
			mapView.getMap().animateMapStatus(MapStatusUpdateFactory.zoomOut());
			break;
		}
		}
	}

	/**
	 * 与MapView设置关联
	 * 
	 * @param mapView
	 */
	public void setMapView(MapView mapView) {
		this.mapView = mapView;
		// 获取最大的缩放级别
		maxZoomLevel = (int) mapView.getMap().getMaxZoomLevel();
		// 获取最大的缩放级别
		minZoomLevel = (int) mapView.getMap().getMinZoomLevel();
	}

	/**
	 * 根据MapView的缩放级别更新缩放按钮的状态，当达到最大缩放级别，设置mButtonZoomin
	 * 为不能点击，反之设置mButtonZoomout
	 * 
	 * @param level
	 */
	public void refreshZoomButtonStatus(int level) {
		if (mapView == null) {
			throw new NullPointerException(
					"you can call setMapView(MapView mapView) at first");
		}
		if (level > minZoomLevel && level < maxZoomLevel) {
			if (!mButtonZoomout.isEnabled()) {
				mButtonZoomout.setEnabled(true);
			}
			if (!mButtonZoomin.isEnabled()) {
				mButtonZoomin.setEnabled(true);
			}
		} else if (level == minZoomLevel) {
			mButtonZoomout.setEnabled(false);
		} else if (level == maxZoomLevel) {
			mButtonZoomin.setEnabled(false);
		}
	}

}
