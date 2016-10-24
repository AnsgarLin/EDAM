package com.edam.floatsearch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.edam.util.MyUtils;
import com.edam.util.Util;

public class FloatSearchPanel extends LinearLayout{
	private Paint mBackPaint;
	private Paint mWhitePaint;
	private float mRadius;
	private float mFloatMutil;
	private float mImgWidth;
	private Path mPath;

	public FloatSearchPanel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FloatSearchPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FloatSearchPanel(Context context, float mutil, float imgWidth) {
		super(context);

		mFloatMutil = mutil;
		mImgWidth = imgWidth;
		init();
	}

	public void init() {
		setBackgroundColor(Color.TRANSPARENT);

		mBackPaint = new Paint();
		mBackPaint.setAntiAlias(true);
		mWhitePaint = new Paint();
		mWhitePaint.setAntiAlias(true);

		mPath = new Path();

		mRadius = MyUtils.TypedValueUtil.toPixel(getContext().getResources().getDisplayMetrics(), "dp", Util.SEARCH_PANEL_BACKGROUND_RADIUS);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float buttom = mImgWidth * 1.5f;
		float basic = Util.PIXEL * mFloatMutil;
		float center = basic * 110f;

		// Up panel
		mPath.reset();

		mPath.moveTo(0, mRadius);
		mPath.rCubicTo(0, -mRadius * 0.55f, mRadius * 0.45f, -mRadius, mRadius, -mRadius);
		mPath.rLineTo(getWidth() - mRadius * 2f, 0);
		mPath.rCubicTo(mRadius * 0.55f, 0, mRadius, mRadius * 0.45f, mRadius, mRadius);
		mPath.rLineTo(0, buttom - mRadius);
		mPath.rLineTo(-getWidth(), 0);

		mPath.close();

		canvas.drawPath(mPath, mBackPaint);

		// City
		mPath.reset();
		// Section-left
		mPath.moveTo(basic * 2f, buttom);
		mPath.rLineTo(0, -basic * 5f);
		mPath.rLineTo(basic * 2f, 0);
		mPath.rLineTo(0, basic);
		mPath.rLineTo(basic * 3f, 0);
		mPath.rLineTo(0, -basic * 5f);
		mPath.rLineTo(basic * 4f, 0);
		mPath.rLineTo(0, basic * 3f);
		mPath.rLineTo(basic * 3f, 0);
		mPath.rLineTo(0, -basic * 3f);
		canvas.drawCircle(basic * 20f, basic * 36f, basic * 6f, mWhitePaint);
		mPath.rLineTo(basic * 12f, 0);
		mPath.rLineTo(0, -basic * 2f);
		mPath.rLineTo(basic, 0);
		mPath.rLineTo(0, -basic * 4f);
		mPath.rLineTo(basic, 0);
		mPath.rLineTo(0, basic * 4f);
		mPath.rLineTo(basic * 4f, 0);
		mPath.rLineTo(0, -basic * 12f);
		mPath.rLineTo(basic * 5f, 0);
		mPath.rLineTo(0, basic * 4f);
		mPath.rLineTo(basic * 5f, -basic * 4f);
		mPath.rLineTo(0, -basic * 5f);
		mPath.rLineTo(basic, 0);
		mPath.rLineTo(0, -basic);
		mPath.rLineTo(basic * 3f, 0);
		mPath.rLineTo(0, basic);
		mPath.rLineTo(basic * 3f, 0);
		mPath.rLineTo(0, basic * 12f);
		mPath.rLineTo(basic * 9f, 0);
		mPath.rLineTo(0, -basic * 5f);
		mPath.rLineTo(basic * 5.5f, 0);
		mPath.rLineTo(0, basic * 21f);
		// Section-center
		mPath.rLineTo(basic * 1.5f, 0);
		mPath.rLineTo(0, -basic * 23f);
		mPath.rLineTo(basic * 6f, 0);
		mPath.rLineTo(0, basic * 6f);
		mPath.rLineTo(basic * 5f, 0);
		mPath.rLineTo(0, -basic * 11f);
		mPath.rLineTo(basic * 4f, 0);
		mPath.rLineTo(0, basic * 17f);
		mPath.rLineTo(basic * 2f, 0);
		mPath.rLineTo(0, -basic * 4f);
		mPath.rLineTo(basic * 3f, basic * 2f);
		mPath.rLineTo(0, -basic * 4f);
		mPath.rLineTo(basic * 5f, 0);
		mPath.rLineTo(0, basic * 8f);
		mPath.rLineTo(basic * 2f, 0);
		mPath.rLineTo(0, -basic * 16f);
		mPath.rLineTo(basic * 9f, 0);
		mPath.rLineTo(0, basic * 7f);
		mPath.rLineTo(basic * 2f, 0);
		mPath.rLineTo(0, -basic * 12f);
		mPath.rLineTo(basic, 0);
		mPath.rLineTo(0, -basic);
		mPath.rLineTo(basic * 2f, 0);
		mPath.rLineTo(0, basic);
		mPath.rLineTo(basic * 2f, 0);
		mPath.rLineTo(0, -basic * 8f);
		mPath.rLineTo(basic * 4f, 0);
		mPath.rLineTo(0, basic * 4f);
		mPath.rLineTo(basic * 6f, 0);
		mPath.rLineTo(0, basic * 21f);
		mPath.rLineTo(basic * 2f, 0);
		mPath.rLineTo(0, -basic * 9f);
		mPath.rLineTo(basic * 7f, 0);
		mPath.rLineTo(0, basic * 4f);
		mPath.rLineTo(basic * 3f, 0);
		mPath.rLineTo(0, -basic * 6f);
		mPath.rLineTo(basic * 5f, 0);
		mPath.rLineTo(0, basic * 8f);
		mPath.rLineTo(basic * 2f, 0);
		mPath.rLineTo(0, -basic * 3f);
		mPath.rLineTo(basic * 2.5f, 0);
		mPath.rLineTo(0, -basic * 2f);
		mPath.rLineTo(basic * 4.5f, 0);
		mPath.rLineTo(0, basic * 5f);
		mPath.rLineTo(basic, 0);
		mPath.rLineTo(0, -basic * 3f);
		mPath.rLineTo(basic * 2f, 0);
		mPath.rLineTo(0, basic * 3f);
		mPath.rLineTo(basic, 0);
		mPath.rLineTo(0, -basic * 3f);
		mPath.rLineTo(basic * 2f, 0);
		mPath.rLineTo(0, basic * 3f);
		mPath.rLineTo(basic, 0);
		mPath.rLineTo(0, -basic * 12f);
		mPath.rLineTo(basic * 5f, 0);
		mPath.rLineTo(0, basic * 10f);
		mPath.rLineTo(basic * 2f, 0);
		mPath.rLineTo(0, basic * 18f);
		// Section-right
		mPath.rLineTo(basic * 1.5f, 0);
		mPath.rLineTo(0, -basic * 21f);
		mPath.rLineTo(basic * 3.5f, 0);
		mPath.rLineTo(0, -basic * 1.5f);
		mPath.rLineTo(basic * 3f, 0);
		mPath.rLineTo(0, basic * 1.5f);
		mPath.rLineTo(basic, 0);
		mPath.rLineTo(0, basic * 6f);
		mPath.rLineTo(basic * 2f, 0);
		mPath.rLineTo(0, basic * 7f);
		mPath.rLineTo(basic * 2f, 0);
		mPath.rLineTo(0, -basic * 5f);
		mPath.rLineTo(basic * 5f, -basic * 5f);
		mPath.rLineTo(0, basic * 5f);
		mPath.rLineTo(basic * 5f, -basic * 5f);
		mPath.rLineTo(0, basic * 5f);
		mPath.rLineTo(basic * 4f, -basic * 4f);
		mPath.rLineTo(0, -basic * 8f);
		mPath.rLineTo(basic * 5f, 0);
		mPath.rLineTo(0, -basic * 4f);
		mPath.rLineTo(basic, 0);
		mPath.rLineTo(0, basic * 4f);
		mPath.rLineTo(basic * 2f, 0);
		mPath.rLineTo(0, basic * 13f);
		mPath.rLineTo(basic * 3f, 0);
		mPath.rLineTo(0, -basic);
		canvas.drawCircle(basic * 202f, basic * 32f, basic * 6f, mWhitePaint);
		mPath.rLineTo(basic * 12f, 0);
		mPath.rLineTo(0, basic * 4f);
		mPath.rLineTo(basic * 6f, 0);
		mPath.rLineTo(0, basic * 5f);
		mPath.rLineTo(basic * 2f, 0);
		mPath.rLineTo(0, -basic);
		mPath.rLineTo(basic * 2f, 0);
		mPath.rLineTo(0, basic * 5f);

		mPath.rLineTo(0, 1);
		mPath.rLineTo(-getWidth() - basic * 4f, 0);

		mPath.close();

		canvas.drawPath(mPath, mWhitePaint);

		// Section-left-window
		canvas.drawRect(basic * 4.5f, basic * 42f, basic * 5f, basic * 43f, mBackPaint);
		canvas.drawRect(basic * 16f, basic * 37f, basic * 17.5f, basic * 40f, mBackPaint);
		canvas.drawRect(basic * 22f, basic * 41f, basic * 23f, basic * 44f, mBackPaint);
		canvas.drawRect(basic * 40f, basic * 29f, basic * 41f, basic * 32f, mBackPaint);
		canvas.drawRect(basic * 40f, basic * 34f, basic * 41f, basic * 37f, mBackPaint);
		canvas.drawRect(basic * 46f, basic * 19f, basic * 48f, basic * 22f, mBackPaint);
		canvas.drawRect(basic * 52f, basic * 38f, basic * 54f, basic * 41f, mBackPaint);

		mPath.reset();

		mPath.moveTo(basic * 49f, basic * 30f);
		mPath.rLineTo(basic * 9f, 0);
		mPath.rLineTo(0, basic * 3f);
		mPath.rLineTo(-basic * 4f, 0);
		mPath.rLineTo(0, basic * 2f);
		mPath.rLineTo(-basic * 5f, 0);
		mPath.close();
		canvas.drawPath(mPath, mBackPaint);
		// Section-center-window
		canvas.drawRect(basic * 69f, basic * 24f, basic * 70f, basic * 27f, mBackPaint);
		canvas.drawRect(basic * 75f, basic * 32f, basic * 76f, basic * 35f, mBackPaint);
		canvas.drawRect(basic * 88.5f, basic * 30f, basic * 89.5f, basic * 32.5f, mBackPaint);
		canvas.drawRect(basic * 94f, basic * 22f, basic * 96f, basic * 25f, mBackPaint);
		canvas.drawRect(basic * 97f, basic * 35f, basic * 98f, basic * 37f, mBackPaint);
		canvas.drawRect(basic * 95f, basic * 41f, basic * 96f, basic * 43f, mBackPaint);
		canvas.drawRect(basic * 106f, basic * 21f, basic * 107f, basic * 22f, mBackPaint);
		canvas.drawRect(basic * 115f, basic * 13f, basic * 116f, basic * 15f, mBackPaint);
		canvas.drawRect(basic * 116f, basic * 17f, basic * 117f, basic * 20f, mBackPaint);
		canvas.drawRect(basic * 113f, basic * 28f, basic * 114f, basic * 31f, mBackPaint);
		canvas.drawRect(basic * 125f, basic * 40f, basic * 126f, basic * 42f, mBackPaint);
		canvas.drawRect(basic * 132.5f, basic * 27f, basic * 134f, basic * 30f, mBackPaint);
		canvas.drawRect(basic * 135f, basic * 30f, basic * 137f, basic * 36f, mBackPaint);
		canvas.drawRect(basic * 142f, basic * 36f, basic * 143f, basic * 39f, mBackPaint);
		canvas.drawRect(basic * 144f, basic * 30f, basic * 145f, basic * 32.5f, mBackPaint);
		canvas.drawRect(basic * 147f, basic * 30f, basic * 148f, basic * 32.5f, mBackPaint);
		canvas.drawRect(basic * 150f, basic * 30f, basic * 151f, basic * 32.5f, mBackPaint);
		// Section-right-window
		canvas.drawRect(basic * 163f, basic * 39f, basic * 165f, basic * 42f, mBackPaint);
		canvas.drawRect(basic * 172f, basic * 34f, basic * 173f, basic * 36f, mBackPaint);
		canvas.drawRect(basic * 177f, basic * 34f, basic * 178.5f, basic * 36f, mBackPaint);
		canvas.drawRect(basic * 189f, basic * 24f, basic * 191f, basic * 28f, mBackPaint);
		canvas.drawRect(basic * 201f, basic * 32f, basic * 203f, basic * 35f, mBackPaint);
		canvas.drawRect(basic * 209f, basic * 40f, basic * 211f, basic * 43f, mBackPaint);

		// Content panel
		mPath.reset();

		mPath.moveTo(0, buttom);
		mPath.rLineTo(getWidth(), 0);
		mPath.rLineTo(0, getHeight() - mImgWidth * 2f);
		mPath.rLineTo(-getWidth(), 0);

		mPath.close();

		canvas.drawPath(mPath, mWhitePaint);

		// Bottom panel
		mPath.reset();

		mPath.moveTo(0, getHeight() - mImgWidth / 2);
		mPath.rLineTo(getWidth(), 0);
		mPath.rLineTo(0, mImgWidth / 2 - mRadius);
		mPath.rCubicTo(0, mRadius * 0.55f, -mRadius * 0.45f, mRadius, -mRadius, mRadius);
		mPath.rLineTo(-(getWidth() - mRadius * 2f), 0);
		mPath.rCubicTo(-mRadius * 0.55f, 0, -mRadius, -mRadius * 0.45f, -mRadius, -mRadius);

		mPath.close();

		canvas.drawPath(mPath, mBackPaint);
	}
	//====================================================================================================
	public void setPaintColor(String color) {
		mBackPaint.setColor(Color.parseColor(color));
		mWhitePaint.setColor(Color.parseColor(MyUtils.ColorUtil.getColorBetween(color, Util.WHITE, Util.BASIC_ALPHA)));
		invalidate();
	}
}
