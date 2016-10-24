package com.edam.page.detail;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.edam.main.R;
import com.edam.util.MyUtils;
import com.edam.util.Util;

public class GoToGetTicketPage extends GoToPage{

	public GoToGetTicketPage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public GoToGetTicketPage(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public GoToGetTicketPage(Context context) {
		super(context);
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.mToast = MyUtils.ToastUtil.restart(getContext(), Util.mToast, R.string.ticket_get);
			}
		});
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		float floorO = Util.PIXEL * Util.HMULTI;


		mSketch.reset();

		mSketch.moveTo(floorO * 14f, floorO * 11f);
		mSketch.rLineTo(floorO * 4f, 0);
		mSketch.rCubicTo(0, floorO, floorO * 4f, floorO, floorO * 4f, 0);
		mSketch.rLineTo(floorO * 4f, 0);
		mSketch.rLineTo(0, floorO * 18f);
		mSketch.rLineTo(-floorO * 4f, 0);
		mSketch.rCubicTo(0, -floorO, -floorO * 4f, -floorO, -floorO * 4f, 0);
		mSketch.rLineTo(-floorO * 4f, 0);

		mSketch.close();
		canvas.drawPath(mSketch, mBackPaint);

		mSketch.reset();

		mSketch.moveTo(floorO * 16f, floorO * 14f);
		mSketch.rLineTo(floorO * 8f, 0);
		mSketch.rLineTo(0, floorO * 12f);
		mSketch.rLineTo(-floorO * 8f, 0);

		mSketch.close();
		canvas.drawPath(mSketch, mWhitePaint);

		mSketch.reset();

		mSketch.moveTo(floorO * 17f, floorO * 15f);
		mSketch.rLineTo(floorO * 6f, 0);
		mSketch.rLineTo(0, floorO * 10f);
		mSketch.rLineTo(-floorO * 6f, 0);

		mSketch.close();

		canvas.drawPath(mSketch, mBackPaint);

		// hand-down
		canvas.drawCircle(floorO * 15f, floorO * 22.75f, floorO * 1.5f, mWhitePaint);
		mSketch.reset();
		mSketch.moveTo(floorO * 15f, floorO * 24.25f);
		mSketch.rLineTo(-floorO * 4f, 0);
		mSketch.rLineTo(0, -floorO * 5.5f);
		mSketch.rLineTo(floorO * 2.5f, 0);
		mSketch.rLineTo(0, floorO * 2.25f);
		mSketch.rLineTo(floorO * 1.5f, 0);
		mSketch.close();
		canvas.drawPath(mSketch, mWhitePaint);

		canvas.drawCircle(floorO * 15f, floorO * 22.75f, floorO, mBackPaint);
		mSketch.reset();
		mSketch.moveTo(floorO * 15f, floorO * 23.75f);
		mSketch.rLineTo(-floorO * 4f, 0);
		mSketch.rLineTo(-floorO * 2f, -floorO * 2f);
		mSketch.rLineTo(0, -floorO * 4f);
		mSketch.rLineTo(floorO * 2f, -floorO * 2f);
		mSketch.rLineTo(floorO * 2.5f, 0);
		mSketch.rLineTo(0, floorO * 5.25f);
		mSketch.rLineTo(-floorO * 2f, -floorO * 2f);
		mSketch.rLineTo(-floorO / 2f, 0);
		mSketch.rLineTo(0, floorO * 0.75f);
		mSketch.rLineTo(floorO / 2f, 0);
		mSketch.rLineTo(floorO * 2f, floorO * 2f);
		mSketch.rLineTo(floorO * 1.5f, 0);
		mSketch.close();
		canvas.drawPath(mSketch, mBackPaint);

		// hand-up
		canvas.save();
		// Rotate canvas with pivots
		canvas.rotate(180, getWidth() / 2f, getHeight() / 2f);

		canvas.drawCircle(floorO * 15f, floorO * 22.75f, floorO * 1.5f, mWhitePaint);
		mSketch.reset();
		mSketch.moveTo(floorO * 15f, floorO * 24.25f);
		mSketch.rLineTo(-floorO * 4f, 0);
		mSketch.rLineTo(0, -floorO * 5.5f);
		mSketch.rLineTo(floorO * 2.5f, 0);
		mSketch.rLineTo(0, floorO * 2.25f);
		mSketch.rLineTo(floorO * 1.5f, 0);
		mSketch.close();
		canvas.drawPath(mSketch, mWhitePaint);

		canvas.drawCircle(floorO * 15f, floorO * 22.75f, floorO, mBackPaint);
		mSketch.reset();
		mSketch.moveTo(floorO * 15f, floorO * 23.75f);
		mSketch.rLineTo(-floorO * 4f, 0);
		mSketch.rLineTo(-floorO * 2f, -floorO * 2f);
		mSketch.rLineTo(0, -floorO * 4f);
		mSketch.rLineTo(floorO * 2f, -floorO * 2f);
		mSketch.rLineTo(floorO * 2.5f, 0);
		mSketch.rLineTo(0, floorO * 5.25f);
		mSketch.rLineTo(-floorO * 2f, -floorO * 2f);
		mSketch.rLineTo(-floorO / 2f, 0);
		mSketch.rLineTo(0, floorO * 0.75f);
		mSketch.rLineTo(floorO / 2f, 0);
		mSketch.rLineTo(floorO * 2f, floorO * 2f);
		mSketch.rLineTo(floorO * 1.5f, 0);
		mSketch.close();
		canvas.drawPath(mSketch, mBackPaint);

		canvas.restore();
	}
}
