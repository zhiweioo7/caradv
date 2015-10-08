package com.hzw.caradv.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzw.caradv.R;

public class SimpleHUDDialog extends Dialog {

	public SimpleHUDDialog(Context context, int theme) {
		super(context, theme);
	}
	
	public static SimpleHUDDialog createDialog(Context context) {
		SimpleHUDDialog dialog = new SimpleHUDDialog(context, R.style.SimpleHUD);
		dialog.setContentView(R.layout.simplehud);
		dialog.getWindow().getAttributes().gravity = Gravity.CENTER;
		return dialog;
	}
	public static SimpleHUDDialog createDialog(Context context,int layoutId) {
		SimpleHUDDialog dialog = new SimpleHUDDialog(context, R.style.SimpleHUD_NotBackground);
		dialog.setContentView(layoutId);
		dialog.getWindow().getAttributes().gravity = Gravity.BOTTOM;
		return dialog;
	}

	public void setMessage(String message) {
		TextView msgView = (TextView)findViewById(R.id.simplehud_message);
		msgView.setText(Html.fromHtml(message));
	}
	public void setImage(Context ctx, int resId) {
		ImageView image = (ImageView)findViewById(R.id.simplehud_image);
		image.setImageResource(resId);
		
		if(resId==R.drawable.simplehud_spinner) {
			Animation anim = AnimationUtils.loadAnimation(ctx, R.anim.simplehud_progressbar);  
			anim.start();
			image.startAnimation(anim);
		}
	}

}
