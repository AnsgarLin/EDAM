package com.edam.page.detail;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.edam.util.MyUtils;
import com.edam.util.Util;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class SharePagePanel extends FrameLayout{
	private boolean mIsPanelOpen;
	private RelativeLayout mSharePagePnaelWrap;
	private ListView mSharePageWrap;
	private int mUpDownPanelHeight;
	private int mSharePagePanelHeight;
	private int mSharePageHeight;

	private SharePagePanelUp mSharePagePanelUp;
	private SharePagePanelDown mSharePagePanelDown;

	private ArrayList<String> mShareURLs;

	public SharePagePanel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public SharePagePanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public SharePagePanel(Context context, ArrayList<String> shareURLs) {
		super(context);
		mShareURLs = shareURLs;
		init();
	}

	private void init() {
		int row;

		if (mShareURLs.size() <= 5) {
			row =  mShareURLs.size();
			if (row == 0) {
				mShareURLs.add("");
				row = 1;
			}
		} else {
			row = 5;
		}

		RelativeLayout shadow = new RelativeLayout(getContext());
		shadow.setBackgroundColor(Color.BLACK);
		shadow.setAlpha(0f);
		addView(shadow, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		mUpDownPanelHeight = ((int) Util.BACK_FRAME_STROKE_WIDTH) * 3;
		mSharePageHeight = ((int) Util.GOTO_BUTTON_SZIE);
		mSharePagePanelHeight = (mSharePageHeight * row) + (mUpDownPanelHeight * 2);

		mSharePagePnaelWrap = new RelativeLayout(getContext());
		FrameLayout.LayoutParams ticketPageWrapParams = new FrameLayout.LayoutParams(mSharePageHeight * 3, mSharePagePanelHeight);
		ticketPageWrapParams.gravity = Gravity.CENTER;
		mSharePagePnaelWrap.setLayoutParams(ticketPageWrapParams);
		mSharePagePnaelWrap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {}
		});

		mSharePagePanelUp = new SharePagePanelUp(getContext());
		RelativeLayout.LayoutParams upParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, mUpDownPanelHeight);
		upParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		mSharePagePanelUp.setLayoutParams(upParams);
		mSharePagePanelUp.setAlpha(0f);
		mSharePagePanelUp.setTranslationY((mSharePagePanelHeight / 2) - mUpDownPanelHeight);
		mSharePagePnaelWrap.addView(mSharePagePanelUp);

		mSharePageWrap = new ListView(getContext());
		RelativeLayout.LayoutParams ticketPagesParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, mSharePageHeight * row);
		ticketPagesParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		mSharePageWrap.setLayoutParams(ticketPagesParams);
		// The list will be shown by images, so we don't need divider
		mSharePageWrap.setDividerHeight(0);
		// Disable scroll bar
		mSharePageWrap.setVerticalScrollBarEnabled(false);
		// Disable the edge effect from top and bottom of listview
		mSharePageWrap.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
		// Disable the high light effect while touch on the listview item
		mSharePageWrap.setSelector(android.R.color.transparent);
		mSharePageWrap.setAdapter(new SharePageAdapter(getContext(), 0, mShareURLs));
		mSharePageWrap.setScaleY(0f);

		mSharePagePnaelWrap.addView(mSharePageWrap);

		mSharePagePanelDown = new SharePagePanelDown(getContext());
		RelativeLayout.LayoutParams downParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, mUpDownPanelHeight);
		downParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		mSharePagePanelDown.setLayoutParams(downParams);
		mSharePagePanelDown.setAlpha(0f);
		mSharePagePanelDown.setTranslationY(-((mSharePagePanelHeight / 2) - mUpDownPanelHeight));
		mSharePagePnaelWrap.addView(mSharePagePanelDown);

		addView(mSharePagePnaelWrap);
	}

	public void onDestroy() {
		setOnClickListener(null);
		mSharePagePnaelWrap.setOnClickListener(null);
		if (mSharePageWrap.getAdapter() != null) {
			((SharePageAdapter) mSharePageWrap.getAdapter()).onDestroy();
			mSharePageWrap.setAdapter(null);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
//		Logger.d(getClass(), "TicketPanel dispatchTouchEvent");

		if (mIsPanelOpen) {
			super.dispatchTouchEvent(ev);
			return true;
		}
		return super.dispatchTouchEvent(ev);
	}
	// =====================================================================================================//
	public AnimatorSet openTicketPagePanel() {
		mIsPanelOpen = true;

		ArrayList<ObjectAnimator> list = new ArrayList<ObjectAnimator>();
		addAnimation(list, getChildAt(0), "alpha", 0f, 0.5f);
		addAnimation(list, mSharePagePanelUp, "translationY", (mSharePagePanelHeight / 2) - mUpDownPanelHeight, 0f);
		addAnimation(list, mSharePagePanelUp, "alpha", 0.5f, 1f);
		addAnimation(list, mSharePageWrap, "scaleY", 0f, 1f);
		addAnimation(list, mSharePagePanelDown, "translationY", -((mSharePagePanelHeight / 2) - mUpDownPanelHeight), 0f);
		addAnimation(list, mSharePagePanelDown, "alpha", 0.5f, 1f);

		AnimatorSet set = new AnimatorSet();
		set.playTogether(list.toArray(new ObjectAnimator[list.size()]));
		return set;
	}

	public AnimatorSet closeTicketPagePanel() {
		mIsPanelOpen = false;

		ArrayList<ObjectAnimator> list = new ArrayList<ObjectAnimator>();
		addAnimation(list, getChildAt(0), "alpha", 0.5f, 0f);
		addAnimation(list, mSharePagePanelUp, "translationY", 0f, (mSharePagePanelHeight / 2) - mUpDownPanelHeight);
		addAnimation(list, mSharePagePanelUp, "alpha", 1f, 0f);
		addAnimation(list, mSharePageWrap, "scaleY", 1f, 0f);
		addAnimation(list, mSharePagePanelDown, "translationY", 0f, -((mSharePagePanelHeight / 2) - mUpDownPanelHeight));
		addAnimation(list, mSharePagePanelDown, "alpha", 1f, 0f);

		AnimatorSet set = new AnimatorSet();
		set.playTogether(list.toArray(new ObjectAnimator[list.size()]));
		return set;
	}
	// =====================================================================================================//
	public void addAnimation(List<ObjectAnimator> list, View view, String property, float start, float end) {
		list.add(ObjectAnimator.ofFloat(view, property, start, end));
	}

	public void setPaintColor(String color) {
		mSharePagePanelUp.setPaintColor(color);
		mSharePageWrap.setBackgroundColor(Color.parseColor(MyUtils.ColorUtil.getColorBetween(color, Util.WHITE, Util.BASIC_ALPHA)));
		((SharePageAdapter) mSharePageWrap.getAdapter()).setPaintColor(color);
		mSharePagePanelDown.setPaintColor(color);
	}
}
