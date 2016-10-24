package com.edam.page.detail;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ScrollView;
import android.widget.TextView;

import com.edam.util.MyUtils;
import com.edam.util.Util;

public class Brief extends ScrollView {
	private TextView mTextView;

	public Brief(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public Brief(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public Brief(Context context) {
		super(context);
		init();
	}

	private void init() {
		mTextView = new TextView(getContext());
		mTextView.setPadding((int) (Util.BACK_FRAME_MARGIN - Util.BACK_FRAME_STROKE_WIDTH),
				(int) (Util.BACK_FRAME_MARGIN - Util.BACK_FRAME_STROKE_WIDTH), (int) (Util.BACK_FRAME_MARGIN - Util.BACK_FRAME_STROKE_WIDTH),
				(int) (Util.BACK_FRAME_MARGIN - Util.BACK_FRAME_STROKE_WIDTH));

		addView(mTextView);
	}

	public void setTextSize(int unit, float size) {
		mTextView.setTextSize(unit, size);
	}

	public void setTextColor(int color) {
		mTextView.setTextColor(color);
	}

	public void setIncludeFontPadding(boolean includepad) {
		mTextView.setIncludeFontPadding(includepad);
	}

	public void setBackgroundColor(String color) {
		mTextView.setBackgroundColor(Color.parseColor(MyUtils.ColorUtil.getColorBetween(color, Util.WHITE, Util.BASIC_ALPHA)));
		setBackgroundColor(Color.parseColor(color));
	}

	public TextView getTextView() {
		return mTextView;
	}
}
