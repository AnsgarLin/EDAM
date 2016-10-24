package com.edam.floatsearch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

public class FloatSearchText extends TextView{
	public int mPosition;
	public Paint mPaint;

	public FloatSearchText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FloatSearchText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FloatSearchText(Context context) {
		super(context);
		init();
	}

	private void init() {
		setIncludeFontPadding(false);
		setGravity(Gravity.CENTER);
		// Note: set top padding to make the text can be at the vertical center exactly
		setPadding(0, 2, 0, 0);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Style.STROKE);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		canvas.drawRect(0, 0, 10, 10, mPaint);
	}
	//====================================================================================================
	public void setPaintColor(int color) {
		mPaint.setColor(color);
	}

	public void setPosition(int position) {
		mPosition = position;
	}
}
