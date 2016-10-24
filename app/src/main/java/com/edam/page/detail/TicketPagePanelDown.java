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

public class TicketPagePanelDown extends TicketPagePanelUp{
	
	public TicketPagePanelDown(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public TicketPagePanelDown(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public TicketPagePanelDown(Context context) {
		super(context);
		init();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// Bottom panel
		mSketch.reset();

		mSketch.moveTo(0, 0);
		mSketch.rLineTo(getWidth(), 0);
		mSketch.rLineTo(0, getHeight() - mRadius);
		mSketch.rCubicTo(0, mRadius * 0.55f, -mRadius * 0.45f, mRadius, -mRadius, mRadius);
		mSketch.rLineTo(-(getWidth() - mRadius * 2f), 0);
		mSketch.rCubicTo(-mRadius * 0.55f, 0, -mRadius, -mRadius * 0.45f, -mRadius, -mRadius);

		mSketch.close();
		
		canvas.drawPath(mSketch, mBackPaint);
	}
	// ====================================================================================================
	public void setPaintColor(String color) {
		mBackPaint.setColor(Color.parseColor(color));
		invalidate();
	}
}
