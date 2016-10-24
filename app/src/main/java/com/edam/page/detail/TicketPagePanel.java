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

public class TicketPagePanel extends FrameLayout{
	private boolean mIsPanelOpen;
	private RelativeLayout mTicketPagePnaelWrap;
	private ListView mTicketPageWrap;
	private int mUpDownPanelHeight;
	private int mTicketPagePanelHeight;
	private int mTicketPageHeight;

	private TicketPagePanelUp mTicketPagePanelUp;
	private TicketPagePanelDown mTicketPagePanelDown;

	private ArrayList<String> mTicketURLs;

	public TicketPagePanel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public TicketPagePanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public TicketPagePanel(Context context, ArrayList<String> ticketURLs) {
		super(context);
		mTicketURLs = ticketURLs;
		init();
	}

	private void init() {
		int row;

		if (mTicketURLs.size() <= 5) {
			row =  mTicketURLs.size();
			if (row == 0) {
				mTicketURLs.add("");
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
		mTicketPageHeight = ((int) Util.GOTO_BUTTON_SZIE);
		mTicketPagePanelHeight = (mTicketPageHeight * row) + (mUpDownPanelHeight * 2);

		mTicketPagePnaelWrap = new RelativeLayout(getContext());
		FrameLayout.LayoutParams ticketPageWrapParams = new FrameLayout.LayoutParams(mTicketPageHeight * 3, mTicketPagePanelHeight);
		ticketPageWrapParams.gravity = Gravity.CENTER;
		mTicketPagePnaelWrap.setLayoutParams(ticketPageWrapParams);
		mTicketPagePnaelWrap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {}
		});

		mTicketPagePanelUp = new TicketPagePanelUp(getContext());
		RelativeLayout.LayoutParams upParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, mUpDownPanelHeight);
		upParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		mTicketPagePanelUp.setLayoutParams(upParams);
		mTicketPagePanelUp.setAlpha(0f);
		mTicketPagePanelUp.setTranslationY((mTicketPagePanelHeight / 2) - mUpDownPanelHeight);
		mTicketPagePnaelWrap.addView(mTicketPagePanelUp);

		mTicketPageWrap = new ListView(getContext());
		RelativeLayout.LayoutParams ticketPagesParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, mTicketPageHeight * row);
		ticketPagesParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		mTicketPageWrap.setLayoutParams(ticketPagesParams);
		// The list will be shown by images, so we don't need divider
		mTicketPageWrap.setDividerHeight(0);
		// Disable scroll bar
		mTicketPageWrap.setVerticalScrollBarEnabled(false);
		// Disable the edge effect from top and bottom of listview
		mTicketPageWrap.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
		// Disable the high light effect while touch on the listview item
		mTicketPageWrap.setSelector(android.R.color.transparent);
		mTicketPageWrap.setAdapter(new TicketPageAdapter(getContext(), 0, mTicketURLs));
		mTicketPageWrap.setScaleY(0f);

		mTicketPagePnaelWrap.addView(mTicketPageWrap);

		mTicketPagePanelDown = new TicketPagePanelDown(getContext());
		RelativeLayout.LayoutParams downParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, mUpDownPanelHeight);
		downParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		mTicketPagePanelDown.setLayoutParams(downParams);
		mTicketPagePanelDown.setAlpha(0f);
		mTicketPagePanelDown.setTranslationY(-((mTicketPagePanelHeight / 2) - mUpDownPanelHeight));
		mTicketPagePnaelWrap.addView(mTicketPagePanelDown);

		addView(mTicketPagePnaelWrap);
	}

	public void onDestroy() {
		setOnClickListener(null);
		mTicketPagePnaelWrap.setOnClickListener(null);
		if (mTicketPageWrap.getAdapter() != null) {
			((TicketPageAdapter) mTicketPageWrap.getAdapter()).onDestroy();
			mTicketPageWrap.setAdapter(null);
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
		addAnimation(list, mTicketPagePanelUp, "translationY", (mTicketPagePanelHeight / 2) - mUpDownPanelHeight, 0f);
		addAnimation(list, mTicketPagePanelUp, "alpha", 0.5f, 1f);
		addAnimation(list, mTicketPageWrap, "scaleY", 0f, 1f);
		addAnimation(list, mTicketPagePanelDown, "translationY", -((mTicketPagePanelHeight / 2) - mUpDownPanelHeight), 0f);
		addAnimation(list, mTicketPagePanelDown, "alpha", 0.5f, 1f);

		AnimatorSet set = new AnimatorSet();
		set.playTogether(list.toArray(new ObjectAnimator[list.size()]));
		return set;
	}

	public AnimatorSet closeTicketPagePanel() {
		mIsPanelOpen = false;

		ArrayList<ObjectAnimator> list = new ArrayList<ObjectAnimator>();
		addAnimation(list, getChildAt(0), "alpha", 0.5f, 0f);
		addAnimation(list, mTicketPagePanelUp, "translationY", 0f, (mTicketPagePanelHeight / 2) - mUpDownPanelHeight);
		addAnimation(list, mTicketPagePanelUp, "alpha", 1f, 0f);
		addAnimation(list, mTicketPageWrap, "scaleY", 1f, 0f);
		addAnimation(list, mTicketPagePanelDown, "translationY", 0f, -((mTicketPagePanelHeight / 2) - mUpDownPanelHeight));
		addAnimation(list, mTicketPagePanelDown, "alpha", 1f, 0f);

		AnimatorSet set = new AnimatorSet();
		set.playTogether(list.toArray(new ObjectAnimator[list.size()]));
		return set;
	}
	// =====================================================================================================//
	public void addAnimation(List<ObjectAnimator> list, View view, String property, float start, float end) {
		list.add(ObjectAnimator.ofFloat(view, property, start, end));
	}

	public void setPaintColor(String color) {
		mTicketPagePanelUp.setPaintColor(color);
		mTicketPageWrap.setBackgroundColor(Color.parseColor(MyUtils.ColorUtil.getColorBetween(color, Util.WHITE, Util.BASIC_ALPHA)));
		((TicketPageAdapter) mTicketPageWrap.getAdapter()).setPaintColor(color);
		mTicketPagePanelDown.setPaintColor(color);
	}
}
