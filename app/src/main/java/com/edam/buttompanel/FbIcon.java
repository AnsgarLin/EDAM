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

public class FbIcon extends View {
	private Paint mBackPaint;
	private Paint mWhitePaint;
	private RectF mIconRect;
	private float mPixel;

	private Path mSketch;

	public FbIcon(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public FbIcon(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FbIcon(Context context) {
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
		mSketch.moveTo(mPixel * 14f, getHeight());
		mSketch.rLineTo(0, mPixel * -9f);
		mSketch.rLineTo(mPixel * -3f, 0);
		mSketch.rLineTo(0, mPixel * -4f);
		mSketch.rLineTo(mPixel * 3f, 0);
		mSketch.rLineTo(0, mPixel * -3f);
		mSketch.rCubicTo(0, mPixel * -4f, mPixel, mPixel * -5f, mPixel * 5f, mPixel * -5f);
		mSketch.rLineTo(mPixel * 2f, 0);
		mSketch.rCubicTo(mPixel, 0, mPixel, 0, mPixel, mPixel);
		mSketch.rLineTo(0, mPixel * 3f);
		mSketch.rLineTo(mPixel * -2f, 0);
		mSketch.rCubicTo(mPixel * -2f, 0, mPixel * -2f, 0, mPixel * -2f, mPixel * 2f);
		mSketch.rLineTo(0, mPixel * 2f);
		mSketch.rLineTo(mPixel * 4f, 0);
		mSketch.rLineTo(-mPixel, mPixel * 4f);
		mSketch.rLineTo(mPixel * -3f, 0);
		mSketch.rLineTo(0, mPixel * 9f);
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
