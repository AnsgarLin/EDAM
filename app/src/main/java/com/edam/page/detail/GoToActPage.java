package com.edam.page.detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;

import com.edam.util.Util;

public class GoToActPage extends GoToPage {
	public String mActURL;

	public GoToActPage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public GoToActPage(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public GoToActPage(Context context) {
		super(context);
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mActURL));
				if (intent != null) {
					getContext().startActivity(intent);
				}
			}
		});
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		float floorO = Util.PIXEL * Util.HMULTI;

		mSketch.reset();

		mSketch.moveTo(floorO * 10f, floorO * 29f);
		mSketch.rLineTo(floorO * 13f, 0);
		mSketch.rLineTo(0, -floorO * 10f);
		mSketch.rLineTo(floorO * 4f, 0);
		mSketch.rLineTo(0, floorO * 10f);
		mSketch.rLineTo(floorO * 3f, 0);
		mSketch.rLineTo(0, -floorO * 12f);
		mSketch.rLineTo(-floorO * 10f, -floorO * 8f);
		mSketch.rLineTo(-floorO * 10f, floorO * 8f);

		mSketch.close();
		canvas.drawPath(mSketch, mBackPaint);
	}

	public void setActURL(String actURL) {
		mActURL = actURL;
	}
}
