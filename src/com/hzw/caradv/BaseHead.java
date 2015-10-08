package com.hzw.caradv;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class BaseHead {
	
	public static interface More{
		/**
		 * 头部右边扩展方法，把有关的扩展代码写到这里
		 * 一定要先把View设置为Visible
		 * 点击View的id是head_more;
		 * TextView的id是head_extend
		 * @param extendImage TODO
		 */
		void initExtend(View more, TextView extend, ImageView extendImage);
	}
	public static interface TwoExtend{
		/**
		 * 头部右边扩张方法，把有关的扩展代码写到这里
		 * 一定要先把View设置为Visible
		 * @date 2015-5-21
		 * @param oneMore 左边数起第一个图标
		 * @param twoMore 左边数起第二个图标
		 */
		void initExtendMore(View oneMoreLayout, TextView oneMoreExtend,
				View twoMoreLayout, TextView twoMoreExtend);
	}
}
