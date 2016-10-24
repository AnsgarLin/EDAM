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

public class TicketPagePanelUp extends View{
	protected Paint mBackPaint;
	protected Path mSketch;
	
	protected float mRadius;
	
	public TicketPagePanelUp(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public TicketPagePanelUp(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public TicketPagePanelUp(Context context) {
		super(context);
		init();
	}
	
	protected void init() {
		mBackPaint = new Paint();
		mBackPaint.setAntiAlias(true);
		
		mSketch = new Path();
		
		mRadius = MyUtils.TypedValueUtil.toPixel(getContext().getResources().getDisplayMetrics(), "dp", Util.SEARCH_PANEL_BACKGROUND_RADIUS);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// Up panel
		mSketch.reset();
		
		mSketch.moveTo(0, mRadius);
		mSketch.rCubicTo(0, -mRadius * 0.55f, mRadius * 0.45f, -mRadius, mRadius, -mRadius);
		mSketch.rLineTo(getWidth() - mRadius * 2f, 0);
		mSketch.rCubicTo(mRadius * 0.55f, 0, mRadius, mRadius * 0.45f, mRadius, mRadius);
		mSketch.rLineTo(0, getHeight() - mRadius);
		mSketch.rLineTo(-getWidth(), 0);
		
		mSketch.close();
		
		canvas.drawPath(mSketch, mBackPaint);
	}
	// ====================================================================================================
	public void setPaintColor(String color) {
		mBackPaint.setColor(Color.parseColor(color));
		invalidate();
	}
}
