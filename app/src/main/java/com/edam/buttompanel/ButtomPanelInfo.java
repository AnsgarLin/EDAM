package com.edam.buttompanel;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edam.main.R;
import com.edam.util.MyUtils;
import com.edam.util.Util;

public class ButtomPanelInfo extends LinearLayout{
	private LinearLayout mTabContainer;
	private LinearLayout mInfoContainer;
	private LinearLayout mFB;
	private LinearLayout mMail;

	public ButtomPanelInfo(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ButtomPanelInfo(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ButtomPanelInfo(Context context) {
		super(context);
	}

	public void init() {
		float buttomPanelBarHeight = MyUtils.TypedValueUtil.toPixel(getResources().getDisplayMetrics(), "dp", Util.BUTTOM_PANEL_BAR_HEIGHT);

		((RelativeLayout.LayoutParams) getLayoutParams()).addRule(RelativeLayout.BELOW, R.id.buttom_panel_bar);

		mTabContainer = (LinearLayout) findViewById(R.id.tab_container);
		mTabContainer.getLayoutParams().height = (int) buttomPanelBarHeight;

		float textSize = MyUtils.TypedValueUtil.toPixel(getResources().getDisplayMetrics(), "sp", Util.CATA_TAB_TEXT_SIZE);

		mFB = (LinearLayout) mTabContainer.findViewById(R.id.fb);
		mFB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Util.FB_FANS));
				if (intent != null) {
					getContext().startActivity(intent);
				}
			}
		});
		((TextView) mFB.findViewById(R.id.fb_string)).setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		((LinearLayout.LayoutParams) ((TextView) mFB.findViewById(R.id.fb_string)).getLayoutParams()).leftMargin =
				(int) (((TextView) mFB.findViewById(R.id.fb_string)).getTextSize() * Util.TEXT_SIZE_BLANK_MULTI / 2f);

		((FbIcon) mFB.findViewById(R.id.fb_icon)).getLayoutParams().width = (int) textSize;
		((FbIcon) mFB.findViewById(R.id.fb_icon)).getLayoutParams().height = (int) textSize;

		mMail = (LinearLayout) mTabContainer.findViewById(R.id.mail);
		mMail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + Util.EMAIL));
				if (intent != null) {
					getContext().startActivity(Intent.createChooser(intent, getResources().getString(R.string.mail_question_dialog_title)));
				}
			}
		});
		((TextView) mMail.findViewById(R.id.mail_string)).setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		((LinearLayout.LayoutParams) ((TextView) mMail.findViewById(R.id.mail_string)).getLayoutParams()).leftMargin =
				(int) (((TextView) mMail.findViewById(R.id.mail_string)).getTextSize() * Util.TEXT_SIZE_BLANK_MULTI / 2f);

		((MailIcon) mMail.findViewById(R.id.mail_icon)).getLayoutParams().width = (int) textSize;
		((MailIcon) mMail.findViewById(R.id.mail_icon)).getLayoutParams().height = (int) textSize;

		mInfoContainer = (LinearLayout) findViewById(R.id.info_container);
		mInfoContainer.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});

		try {
			((TextView) mInfoContainer.findViewById(R.id.version_num)).setText(getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void onDestroy() {
		mFB.setOnClickListener(null);
		mMail.setOnClickListener(null);
		mInfoContainer.setOnTouchListener(null);
	}
	// ====================================================================================================
	public void setPaintColor(String color) {
		mTabContainer.setBackgroundColor(Color.parseColor(color));
		mInfoContainer.setBackgroundColor(Color.parseColor(MyUtils.ColorUtil.getColorBetween(color, Util.WHITE, Util.BASIC_ALPHA)));

		((TextView) mTabContainer.findViewById(R.id.fb_string)).setTextColor(Color.parseColor(MyUtils.ColorUtil.getColorBetween(color, Util.WHITE, Util.BASIC_ALPHA)));
		((FbIcon) mTabContainer.findViewById(R.id.fb_icon)).setPaintColor(color);

		((TextView) mTabContainer.findViewById(R.id.mail_string)).setTextColor(Color.parseColor(MyUtils.ColorUtil.getColorBetween(color, Util.WHITE, Util.BASIC_ALPHA)));
		((MailIcon) mTabContainer.findViewById(R.id.mail_icon)).setPaintColor(color);

		((TextView) mInfoContainer.findViewById(R.id.version)).setTextColor(Color.parseColor(color));
		((TextView) mInfoContainer.findViewById(R.id.update)).setTextColor(Color.parseColor(color));
	}
}
