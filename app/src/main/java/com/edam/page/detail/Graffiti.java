package com.edam.page.detail;

import com.edam.util.MyUtils;
import com.edam.util.Util;

import android.R.integer;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class Graffiti extends RelativeLayout{
	private Paint mBackPaint;
	private Paint mWhitePaint;

	private Path mSketch;
	
	public Graffiti(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public Graffiti(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public Graffiti(Context context) {
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
		float basic = Util.PIXEL * Util.HMULTI;
		float center = getWidth() / 2f;
		
		// Back-Back-Left
		canvas.drawRect(center - basic * 71.5f, 0, center - basic * 66.5f, basic * 17f, mBackPaint);
		canvas.drawCircle(center - basic * 69f, basic * 17f, basic * 2.5f, mBackPaint);
		
		canvas.drawCircle(center - basic * 12f, 0, basic * 5.5f, mBackPaint);
		canvas.drawCircle(center - basic * 12f, 0, basic * 3.5f, mWhitePaint);
		
		// Back-Left
		canvas.drawCircle(center - basic * 103f, basic * 6f, basic * 10f, mBackPaint);
		canvas.drawCircle(center - basic * 103f, basic * 6f, basic * 7.50f, mWhitePaint);
		canvas.drawCircle(center - basic * 103f, basic * 6f, basic * 3.5f, mBackPaint);

		canvas.drawCircle(center - basic * 64.5f, 0, basic * 7.5f, mBackPaint);
		canvas.drawCircle(center - basic * 64.5f, 0, basic * 5f, mWhitePaint);
		
		canvas.drawCircle(center - basic * 42f, basic * 12f, basic * 9f, mBackPaint);
		canvas.drawCircle(center - basic * 42f, basic * 12f, basic * 7.5f, mWhitePaint);
		canvas.drawCircle(center - basic * 42f, basic * 12f, basic * 5f, mBackPaint);

		canvas.drawCircle(center - basic * 4f, basic * 9f, basic * 7.5f, mBackPaint);
		canvas.drawCircle(center - basic * 4f, basic * 9f, basic * 5f, mWhitePaint);

		// Front-Left
		canvas.drawCircle(center - basic * 85f, basic * 9f, basic * 16f, mBackPaint);
		canvas.drawCircle(center - basic * 85f, basic * 9f, basic * 13.5f, mWhitePaint);
		canvas.drawCircle(center - basic * 85f, basic * 9f, basic * 11.5f, mBackPaint);
		canvas.drawCircle(center - basic * 85f, basic * 9f, basic * 6f, mWhitePaint);
			
		canvas.drawRect(center - basic * 60f, 0, center - basic * 56f, basic * 16f, mBackPaint);
		canvas.drawCircle(center - basic * 58f, basic * 16f, basic * 2f, mBackPaint);

		canvas.drawRect(center - basic * 55f, 0, center - basic * 50f, basic * 18f, mBackPaint);
		canvas.drawCircle(center - basic * 52.5f, basic * 18f, basic * 2.5f, mBackPaint);
		
		canvas.drawRect(center - basic * 43f, 0, center - basic * 41f, basic * 9f, mBackPaint);

		canvas.drawRect(center - basic * 34f, 0, center - basic * 30f, basic * 16f, mBackPaint);
		canvas.drawCircle(center - basic * 32f, basic * 16f, basic * 2f, mBackPaint);
		
		canvas.drawRect(center - basic * 27f, 0, center - basic * 21f, basic * 27f, mBackPaint);
		canvas.drawCircle(center - basic * 24f, basic * 27f, basic * 3f, mBackPaint);
		canvas.drawRect(center - basic * 21f, 0, center - basic * 18f, basic * 23f, mBackPaint);

		canvas.drawRect(center - basic * 9f, 0, center - basic * 6f, basic * 4f, mBackPaint);
		canvas.drawCircle(center + basic * 2f, 0, basic * 5f, mBackPaint);

		// Front-Center
		canvas.drawCircle(center, basic * 24f, basic * 15f, mBackPaint);
		canvas.drawCircle(center, basic * 24f, basic * 11.5f, mWhitePaint);
		canvas.drawCircle(center, basic * 24f, basic * 10f, mBackPaint);
		
		// Back-Back-Right
		canvas.drawCircle(center + basic * 11f, basic, basic * 5.5f, mBackPaint);
		canvas.drawCircle(center + basic * 11f, basic, basic * 3.5f, mWhitePaint);
		
		// back-Right
		canvas.drawRect(center + basic * 4.5f, 0, center + basic * 6.5f, basic * 13f, mBackPaint);
		
		canvas.drawRect(center + basic * 8f, 0, center + basic * 10f, basic * 15f, mBackPaint);

		canvas.drawCircle(center + basic * 36f, basic * 12f, basic * 10f, mBackPaint);
		canvas.drawCircle(center + basic * 36f, basic * 12f, basic * 9f, mWhitePaint);
		canvas.drawCircle(center + basic * 36f, basic * 12f, basic * 7.5f, mBackPaint);
		canvas.drawCircle(center + basic * 36f, basic * 12f, basic * 2.5f, mWhitePaint);
	
		canvas.drawRect(center + basic * 63f, 0, center + basic * 68f, basic * 17f, mBackPaint);
		canvas.drawCircle(center + basic * 65.5f, basic * 17f, basic * 2.5f, mBackPaint);
		
		canvas.drawCircle(center + basic * 82f, basic * 9f, basic * 16f, mBackPaint);
		canvas.drawCircle(center + basic * 82f, basic * 9f, basic * 12.5f, mWhitePaint);
		canvas.drawCircle(center + basic * 82f, basic * 9f, basic * 10f, mBackPaint);
		canvas.drawCircle(center + basic * 82f, basic * 9f, basic * 4f, mWhitePaint);
		
		// Front-Right
		canvas.drawRect(center + basic * 18f, 0, center + basic * 24f, basic * 16f, mBackPaint);
		canvas.drawCircle(center + basic * 21f, basic * 16f, basic * 3f, mBackPaint);

		canvas.drawRect(center + basic * 26f, 0, center + basic * 29f, basic * 25f, mBackPaint);

		canvas.drawRect(center + basic * 35f, 0, center + basic * 37f, basic * 9f, mBackPaint);

		canvas.drawRect(center + basic * 44f, 0, center + basic * 48f, basic * 20f, mBackPaint);
		
		canvas.drawRect(center + basic * 49f, 0, center + basic * 55f, basic * 23f, mBackPaint);
		canvas.drawCircle(center + basic * 52f, basic * 23f, basic * 3f, mBackPaint);

		canvas.drawCircle(center + basic * 63f, 0, basic * 8f, mBackPaint);
		canvas.drawCircle(center + basic * 63f, 0, basic * 5.5f, mWhitePaint);

		canvas.drawCircle(center + basic * 102f, basic * 6f, basic * 11f, mBackPaint);
		canvas.drawCircle(center + basic * 102f, basic * 6f, basic * 9f, mWhitePaint);
		canvas.drawCircle(center + basic * 102f, basic * 6f, basic * 7.5f, mBackPaint);
		canvas.drawCircle(center + basic * 102f, basic * 6f, basic * 2.5f, mWhitePaint);

	}
	// ====================================================================================================
	public void setPaintColor(String color) {
		mBackPaint.setColor(Color.parseColor(color));
		mWhitePaint.setColor(Color.parseColor(MyUtils.ColorUtil.getColorBetween(color, Util.WHITE, Util.BASIC_ALPHA)));
		invalidate();
	}
}
