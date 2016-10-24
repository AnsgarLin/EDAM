package com.edam.buttompanel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.edam.util.MyUtils;
import com.edam.util.Util;

public class MailIcon extends View{
	protected Paint mBackPaint;
	protected Paint mWhitePaint;
	private RectF mIconRect;
	private float mPixel;

	protected Path mSketch;

	public MailIcon(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public MailIcon(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MailIcon(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	private void init() {
		mBackPaint = new Paint();
		mBackPaint.setAntiAlias(true);
		mWhitePaint = new Paint();
		mWhitePaint.setAntiAlias(true);

		mIconRect = new RectF();
		mPixel = Util.PIXEL;
		mSketch = new Path();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		mIconRect.set(0, 0, right - left, bottom - top);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawRoundRect(mIconRect, mPixel * 2f, mPixel * 2f, mWhitePaint);
		mSketch.reset();
		mSketch.moveTo(0, mPixel * 10f);
		mSketch.rLineTo(0, mPixel * 2f);
		mSketch.rLineTo(getWidth() / 2f, mPixel * 4.5f);
		mSketch.rLineTo(getWidth() / 2f, mPixel * -4.5f);
		mSketch.rLineTo(0, mPixel * -2f);
		mSketch.rLineTo(-getWidth() / 2f, mPixel * 4.5f);
		mSketch.rLineTo(-getWidth() / 2f, mPixel * -4.5f);
		mSketch.close();
		canvas.drawPath(mSketch, mBackPaint);
	}
	// ====================================================================================================
	public void setPaintColor(String color) {
		mBackPaint.setColor(Color.parseColor(color));
		mWhitePaint.setColor(Color.parseColor(MyUtils.ColorUtil.getColorBetween(color, Util.WHITE, Util.BASIC_ALPHA)));
		invalidate();
	}
}
