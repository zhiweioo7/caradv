package com.hzw.caradv.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

public abstract class BaseListAdapter<T> extends BaseAdapter {
	protected List<T> items=new ArrayList<T>();
	protected Context context;
	protected LayoutInflater inflater;
	
	public BaseListAdapter(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		
	}
	public BaseListAdapter(Context context,List<T> items) {
		this.context = context;
		this.items = items;
		inflater = LayoutInflater.from(context);
		
	}

	@Override
	public int getCount() {
		return items == null ? 0 : items.size();
	}

	@Override
	public T getItem(int position) {
	    if (position <= -1 || position >= getCount())
	        return null;
		return items == null ? null : items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public List<T> getItems() {
		return items;
	}
	
	public void addList(Collection<T> list,boolean reload) {
		if(null != list && list.size() > 0){
			addList(items.size(),list,reload);
		}
	}
	public void addList(int position, Collection<T> list,boolean reload) {
		if(null != list && list.size() > 0){
			this.items.addAll(position,list);
		}
		if(reload) {
			notifyDataSetChanged();
		}
	}
	public int add(T t,boolean reload) {
		add(items.size(), t, reload);
		return items.size();
	}

	public int add(int position, T t,boolean reload) {
		if(null != t)
			this.items.add(position,t);
		if(reload) {
			notifyDataSetChanged();
		}
		return items.size();
	}

	public List<T> getList() {
	    return items;
	}
	
	public void setItems(List<T> items) {
		this.items = items;
		notifyDataSetChanged();
	}
	
	public void remove(Object obj, boolean reload) {
		if(null != obj) 
			items.remove(obj);
		if(reload) {
			notifyDataSetChanged();
		}
	}

    /**
     * 检测String是否为 null或者"" 如果是 null或者"" 返回true
     * 
     * @param text
     */
    protected boolean equalsNull(String text) {
        return text == null || "".equals(text);
    }

	/**
	 * 释放资源
	 */
	public void recyle() {
		if(items!=null){
			items.clear();
		}
		notifyDataSetChanged();
	}
	
	/**
	 * Intent跳转
	 * 
	 * @param target
	 */
	protected void redirectActivity(Class<? extends Activity> target,
			Bundle data) {
		Intent it = new Intent(context, target);
		it.putExtras(data);
		context.startActivity(it);
	}

}
