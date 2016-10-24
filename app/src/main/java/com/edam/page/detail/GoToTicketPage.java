package com.edam.page.detail;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.edam.main.R;
import com.edam.util.Util;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class GoToTicketPage extends GoToPage {
	private TicketPagePanel mGoToTicketPagePanel;
	private ArrayList<String> mTicketURLs;
	private String mCurrentColor;

	public GoToTicketPage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public GoToTicketPage(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public GoToTicketPage(Context context, final ArrayList<String> ticketURLs) {
		super(context);
		mTicketURLs = ticketURLs;
		setOnClickListener(new TicketListener());
	}

	private class TicketListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			initGoToTicketPagePanel(mTicketURLs);
			((ViewGroup) ((Activity) getContext()).getWindow().getDecorView().findViewById(R.id.edam_root)).addView(mGoToTicketPagePanel);
			openTicketPagePanel();
		}
	}

	private void initGoToTicketPagePanel(ArrayList<String> ticketURLs) {
		mGoToTicketPagePanel = new TicketPagePanel(getContext(), ticketURLs);
		FrameLayout.LayoutParams goToTicketPagePanelParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mGoToTicketPagePanel.setLayoutParams(goToTicketPagePanelParams);
		mGoToTicketPagePanel.setPaintColor(mCurrentColor);
		mGoToTicketPagePanel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeTicketPagePanel();
			}
		});
	}

	public void onDestroy() {
		setOnClickListener(null);
		if (mGoToTicketPagePanel != null) {
			mGoToTicketPagePanel.onDestroy();
		}
	}
	// =====================================================================================================//
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		float floorO = Util.PIXEL * Util.HMULTI;

		canvas.save();
		// Rotate canvas with pivots
		canvas.rotate(45, getWidth() / 2f, getHeight() / 2f);

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

		canvas.restore();
	}
	// =====================================================================================================//
	public void openTicketPagePanel() {
		// Add start animation delay to avoid the view get blink
		AnimatorSet set = mGoToTicketPagePanel.openTicketPagePanel();
		set.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {
				animate().setStartDelay((long) (Util.ANIMATION_BASIC_DURATION_MS * 1.5f)).setDuration(Util.ANIMATION_BASIC_DURATION_MS / 2).rotation(-90f).start();
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {}

			@Override
			public void onAnimationEnd(Animator arg0) {}

			@Override
			public void onAnimationCancel(Animator arg0) {}
		});
		set.setInterpolator(new DecelerateInterpolator(2f));
		set.setStartDelay(Util.ANIMATION_BASIC_DURATION_MS);
		set.setDuration((long) (Util.ANIMATION_BASIC_DURATION_MS * 1.5)).start();
	}

	public void closeTicketPagePanel() {
		AnimatorSet set = mGoToTicketPagePanel.closeTicketPagePanel();
		set.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator arg0) {
				animate().setStartDelay(Util.ANIMATION_BASIC_DURATION_MS).setDuration(Util.ANIMATION_BASIC_DURATION_MS / 2).rotation(0f).start();
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {}

			@Override
			public void onAnimationEnd(Animator arg0) {
				if (mGoToTicketPagePanel != null) {
					mGoToTicketPagePanel.onDestroy();
					((ViewGroup) ((Activity) getContext()).getWindow().getDecorView().findViewById(R.id.edam_root)).removeView(mGoToTicketPagePanel);
					mGoToTicketPagePanel = null;
				}
			}

			@Override
			public void onAnimationCancel(Animator arg0) {}
		});
		set.setDuration(Util.ANIMATION_BASIC_DURATION_MS).start();
	}
	// ====================================================================================================
	public void addAnimation(List<ObjectAnimator> list, View view, String property, float start, float end) {
		list.add(ObjectAnimator.ofFloat(view, property, start, end));
	}

	@Override
	public void setPaintColor(String color) {
		super.setPaintColor(mCurrentColor = color);
	}

	public boolean isTicketPagePanelOpen() {
		return mGoToTicketPagePanel != null;
	}

	public void setTicketURLs(ArrayList<String> ticketURLs) {
		mTicketURLs = ticketURLs;
	}
}
