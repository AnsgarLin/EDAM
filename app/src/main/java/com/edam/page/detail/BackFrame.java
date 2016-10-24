package com.edam.page.detail;

import com.edam.util.Util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class BackFrame extends RelativeLayout {
	private Paint mPaint;

	public BackFrame(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public BackFrame(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BackFrame(Context context) {
		super(context);
		init();
	}

	private void init() {
		mPaint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		canvas.drawRect(0, 0, getWidth(), (int) Util.BACK_FRAME_STROKE_WIDTH, mPaint);
		canvas.drawRect(0, (int) Util.BACK_FRAME_STROKE_WIDTH, (int) Util.BACK_FRAME_STROKE_WIDTH, getHeight() - (int) Util.BACK_FRAME_STROKE_WIDTH, mPaint);
		canvas.drawRect(getWidth() - (int) Util.BACK_FRAME_STROKE_WIDTH, (int) Util.BACK_FRAME_STROKE_WIDTH , getWidth(), getHeight() - (int) Util.BACK_FRAME_STROKE_WIDTH, mPaint);
		canvas.drawRect(0, getHeight() - (int) Util.BACK_FRAME_STROKE_WIDTH, getWidth(), getHeight(), mPaint);
	}

	// ====================================================================================================
	public void setPaintColor(String color) {
		mPaint.setColor(Color.parseColor(color));
		invalidate();
	}
}
