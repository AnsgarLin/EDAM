package com.edam.page.detail;

import com.edam.util.MyUtils;
import com.edam.util.Util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class Close extends View{
	private Paint mWhitePaint;

	public Close(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public Close(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public Close(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		mWhitePaint = new Paint();
		mWhitePaint.setAntiAlias(true);
		mWhitePaint.setStyle(Style.STROKE);
		mWhitePaint.setStrokeWidth(Util.BACK_FRAME_STROKE_WIDTH);
		mWhitePaint.setStrokeCap(Cap.ROUND);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.save();
		canvas.rotate(45, getWidth() / 2f, getHeight() / 2f);
		
		canvas.drawLine(getWidth() - Util.BACK_FRAME_STROKE_WIDTH, getHeight() / 2f, Util.BACK_FRAME_STROKE_WIDTH, getHeight() / 2f, mWhitePaint);
		canvas.drawLine(getWidth() / 2f, Util.BACK_FRAME_STROKE_WIDTH, getWidth() / 2f, getHeight() - Util.BACK_FRAME_STROKE_WIDTH, mWhitePaint);

		canvas.restore();
		
		super.onDraw(canvas);
	}

	// ====================================================================================================
	public void setPaintColor(String color) {
		mWhitePaint.setColor(Color.parseColor(MyUtils.ColorUtil.getColorBetween(color, Util.WHITE, Util.BASIC_ALPHA)));
		invalidate();
	}
}
