package com.edam.page.detail;

import com.edam.util.MyUtils;
import com.edam.util.Util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class CommonText extends TextView{
	private Paint mWhitePaint;

	public CommonText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public CommonText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CommonText(Context context) {
		super(context);
		init();
	}

	private void init() {
		mWhitePaint = new Paint();
		mWhitePaint.setAntiAlias(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawRect(Util.BACK_FRAME_STROKE_WIDTH, Util.BACK_FRAME_STROKE_WIDTH, getWidth() - Util.BACK_FRAME_STROKE_WIDTH, getHeight()
				- Util.BACK_FRAME_STROKE_WIDTH, mWhitePaint);
		super.onDraw(canvas);
	}

	// ====================================================================================================
	public void setPaintColor(String color) {
		mWhitePaint.setColor(Color.parseColor(MyUtils.ColorUtil.getColorBetween(color, Util.WHITE, Util.BASIC_ALPHA)));
		invalidate();
	}
}
