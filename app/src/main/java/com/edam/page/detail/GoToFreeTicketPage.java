package com.edam.page.detail;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.edam.main.R;
import com.edam.util.MyUtils;
import com.edam.util.Util;

public class GoToFreeTicketPage extends GoToPage{

	public GoToFreeTicketPage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public GoToFreeTicketPage(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public GoToFreeTicketPage(Context context) {
		super(context);
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.mToast = MyUtils.ToastUtil.restart(getContext(), Util.mToast, R.string.ticket_free);
			}
		});
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		float floorO = Util.PIXEL * Util.HMULTI;

		mSketch.reset();
		// F
		mSketch.moveTo(floorO * 8f, floorO * 14f);
		mSketch.rLineTo(floorO * 7f, 0);
		mSketch.rLineTo(0, floorO * 2f);
		mSketch.rLineTo(-floorO * 5f, 0);
		mSketch.rLineTo(0, floorO * 4f);
		mSketch.rLineTo(floorO * 3f, 0);
		mSketch.rLineTo(0, floorO * 2f);
		mSketch.rLineTo(-floorO * 3f, 0);
		mSketch.rLineTo(0, floorO * 5f);
		mSketch.rLineTo(-floorO * 2f, 0);
		// R
		mSketch.moveTo(floorO * 13f, floorO * 17f);
		mSketch.rLineTo(floorO * 4f, 0);
		mSketch.rLineTo(0, floorO * 2f);
		mSketch.rLineTo(-floorO * 2f, 0);
		mSketch.rLineTo(0, floorO * 2f);
		mSketch.rLineTo(floorO * 2f, 0);
		mSketch.rLineTo(0, floorO * 2f);
		mSketch.rLineTo(floorO * 3f, floorO * 4f);
		mSketch.rLineTo(-floorO * 2f, 0);
		mSketch.rLineTo(-floorO * 3f, -floorO * 4f);
		mSketch.rLineTo(0, floorO * 4f);
		mSketch.rLineTo(-floorO * 2f, 0);
		canvas.drawCircle(floorO * 17f, floorO * 20f, floorO * 3f, mBackPaint);
		canvas.drawCircle(floorO * 17f, floorO * 20f, floorO, mWhitePaint);
		canvas.drawRect(floorO * 15f, floorO * 19f, floorO * 17f, floorO * 21f, mWhitePaint);
		// E
		mSketch.moveTo(floorO * 20f, floorO * 16f);
		mSketch.rLineTo(floorO * 6f, 0);
		mSketch.rLineTo(0, floorO * 2f);
		mSketch.rLineTo(-floorO * 4f, 0);
		mSketch.rLineTo(0, floorO * 2f);
		mSketch.rLineTo(floorO * 3f, 0);
		mSketch.rLineTo(0, floorO * 2f);
		mSketch.rLineTo(-floorO * 3f, 0);
		mSketch.rLineTo(0, floorO * 3f);
		mSketch.rLineTo(floorO * 4f, 0);
		mSketch.rLineTo(0, floorO * 2f);
		mSketch.rLineTo(-floorO * 6f, 0);
		// E
		mSketch.moveTo(floorO *26f, floorO * 14f);
		mSketch.rLineTo(floorO * 6f, 0);
		mSketch.rLineTo(0, floorO * 2f);
		mSketch.rLineTo(-floorO * 4f, 0);
		mSketch.rLineTo(0, floorO * 4f);
		mSketch.rLineTo(floorO * 3f, 0);
		mSketch.rLineTo(0, floorO * 2f);
		mSketch.rLineTo(-floorO * 3f, 0);
		mSketch.rLineTo(0, floorO * 3f);
		mSketch.rLineTo(floorO * 4f, 0);
		mSketch.rLineTo(0, floorO * 2f);
		mSketch.rLineTo(-floorO * 6f, 0);
		mSketch.close();
		canvas.drawPath(mSketch, mBackPaint);
	}
}
