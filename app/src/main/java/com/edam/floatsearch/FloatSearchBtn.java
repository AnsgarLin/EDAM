package com.edam.floatsearch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.util.AttributeSet;
import android.widget.Button;

import com.edam.util.MyUtils;
import com.edam.util.Util;

public class FloatSearchBtn extends Button {
	private Paint mPaintBack;
	private Paint mPaintBorder;
	private Paint mPaintMagnifier;

	private float mMutil;
	private Path mPath;

	public FloatSearchBtn(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FloatSearchBtn(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FloatSearchBtn(Context context) {
		super(context);
		init();
	}

	public void init() {
		setBackgroundColor(Color.TRANSPARENT);

		mPaintBorder = new Paint();
		mPaintBorder.setAntiAlias(true);
		mPaintBack = new Paint();
		mPaintBack.setAntiAlias(true);

		mPath = new Path();

		mPaintMagnifier = new Paint();
		mPaintMagnifier.setAntiAlias(true);
		mPaintMagnifier.setStyle(Style.STROKE);
		mPaintMagnifier.setStrokeWidth(Util.LIST_DIVIDER_HEIGHT + 2);
		mPaintMagnifier.setColor(Color.WHITE);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		float radius = getWidth() / 2f;
		canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, radius, mPaintBorder);
		canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, radius - (Util.LIST_DIVIDER_HEIGHT + 1), mPaintBack);

		mPath.reset();
		mPath.addCircle(getWidth() / 2 - radius * 0.125f, getHeight() / 2 - radius * 0.125f, radius * 0.45f, Direction.CW);
		mPath.moveTo(getWidth() / 2 + radius * 0.175f, getWidth() / 2 + radius * 0.175f);
		mPath.rLineTo(radius * 0.35f, radius * 0.35f);
		mPath.close();

		canvas.drawPath(mPath, mPaintMagnifier);
	}
	//====================================================================================================
	public void setPaintColor(String color) {
		mPaintMagnifier.setColor(Color.parseColor(color));
		mPaintBorder.setColor(Color.parseColor(color));
		mPaintBack.setColor(Color.parseColor(MyUtils.ColorUtil.getColorBetween(color, Util.WHITE, Util.BASIC_ALPHA)));
		invalidate();
	}
}
