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

public class GoToSharePage extends GoToPage {
	public SharePagePanel mSharePagePanel;
	private String mCurrentColor;
	private ArrayList<String> mShareURLs;

	public GoToSharePage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public GoToSharePage(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public GoToSharePage(Context context) {
		super(context);
		mShareURLs = new ArrayList<String>();

		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initGoToSharePagePanel(mShareURLs);
				((ViewGroup) ((Activity) getContext()).getWindow().getDecorView().findViewById(R.id.edam_root)).addView(mSharePagePanel);
				openSharePagePanel();
			}
		});
	}

	private void initGoToSharePagePanel(ArrayList<String> shareURLs) {
		mSharePagePanel = new SharePagePanel(getContext(), shareURLs);
		FrameLayout.LayoutParams goToTicketPagePanelParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mSharePagePanel.setLayoutParams(goToTicketPagePanelParams);
		mSharePagePanel.setPaintColor(mCurrentColor);
		mSharePagePanel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeSharePagePanel();
			}
		});
	}

	public void onDestroy() {
		setOnClickListener(null);
		if (mSharePagePanel != null) {
			mSharePagePanel.onDestroy();
		}
	}
	// =====================================================================================================//
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		float floorO = Util.PIXEL * Util.HMULTI;

		mSketch.reset();

		mSketch.moveTo(floorO * 10f, floorO * 28f);
		mSketch.rCubicTo(0, 0, 0, floorO * -13f, floorO * 12f, floorO * -13f);
		mSketch.rLineTo(0, floorO * -5f);
		mSketch.rLineTo(floorO * 8f, floorO * 9f);
		mSketch.rLineTo(floorO * -8f, floorO * 9f);
		mSketch.rLineTo(0, floorO * -5f);
		mSketch.rCubicTo(0, 0, floorO * -10f, -floorO / 2f, floorO * -12f, floorO * 6f);

		mSketch.close();
		canvas.drawPath(mSketch, mBackPaint);
	}
	// =====================================================================================================//
	public void openSharePagePanel() {
		// Add start animation delay to avoid the view get blink
		AnimatorSet set = mSharePagePanel.openTicketPagePanel();
		set.setInterpolator(new DecelerateInterpolator(2f));
		set.setStartDelay(Util.ANIMATION_BASIC_DURATION_MS);
		set.setDuration((long) (Util.ANIMATION_BASIC_DURATION_MS * 1.5)).start();
	}

	public void closeSharePagePanel() {
		AnimatorSet set = mSharePagePanel.closeTicketPagePanel();
		set.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator arg0) {}

			@Override
			public void onAnimationRepeat(Animator arg0) {}

			@Override
			public void onAnimationEnd(Animator arg0) {
				if (mSharePagePanel != null) {
					mSharePagePanel.onDestroy();
					((ViewGroup) ((Activity) getContext()).getWindow().getDecorView().findViewById(R.id.edam_root)).removeView(mSharePagePanel);
					mSharePagePanel = null;
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

	public boolean isSharePagePanelOpen() {
		return mSharePagePanel != null;
	}

	public void setShareURL(String title, String actURL) {
		mShareURLs.clear();
		mShareURLs.add(title + "\n" + actURL);
		mShareURLs.add(title + "\n" + actURL);
	}
}
