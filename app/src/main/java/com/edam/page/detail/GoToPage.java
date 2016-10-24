package com.edam.page.detail;

import com.edam.util.MyUtils;
import com.edam.util.Util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class GoToPage extends View{
	protected Paint mBackPaint;
	protected Paint mWhitePaint;

	protected Path mSketch;

	public GoToPage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public GoToPage(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public GoToPage(Context context) {
		super(context);
		init();
	}

	private void init() {
		mBackPaint = new Paint();
		mBackPaint.setAntiAlias(true);
		mWhitePaint = new Paint();
		mWhitePaint.setAntiAlias(true);

		mSketch = new Path();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, getWidth() / 2f, mBackPaint);
		canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, (getWidth() / 2f) - Util.BACK_FRAME_STROKE_WIDTH, mWhitePaint);
	}
	// ====================================================================================================
	public void setPaintColor(String color) {
		mBackPaint.setColor(Color.parseColor(color));
		mWhitePaint.setColor(Color.parseColor(MyUtils.ColorUtil.getColorBetween(color, Util.WHITE, Util.BASIC_ALPHA)));
		invalidate();
	}
}
