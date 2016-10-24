package com.edam.floatsearch;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Space;

import com.edam.main.ActPageSlideTab;
import com.edam.main.R;
import com.edam.query.QueryCondition;
import com.edam.util.Logger;
import com.edam.util.MyUtils;
import com.edam.util.MyUtils.ToastUtil;
import com.edam.util.Util;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class FloatSearch extends RelativeLayout {
	private ActPageSlideTab mActPageSlideTab;
	private RelativeLayout mFloatPlayground;

	private boolean mIsMenuOpen;
	private boolean mIsAnimate;

	private InputMethodManager mImm;

	private float mFloatMutil;
	/**
	 * Search Panel
	 */
	private float mImgWidth;

	private FloatSearchPanel mSearchPanel;
	private float mSearchPanelWidth;
	private float mSearchPanelHeight;

	private Space mTitleSpace;

	private FloatSearchEdit mKeywordEdit;
	private Space mKeywordSpace;

	private FloatSearchList mCityList;
	private Space mCitySpace;

	private LinearLayout mStartDateContainer;
	private FloatSearchList mStartYearList;
	private FloatSearchList mStartMonthList;
	private FloatSearchList mStartDateList;

	private Space mDateSpace;

	private LinearLayout mEndDateContainer;
	private FloatSearchList mEndYearList;
	private FloatSearchList mEndMonthList;
	private FloatSearchList mEndDateList;

	private float mButtonSize;
	private RelativeLayout mSubmitBtnContainer;

	/**
	 * Search Button
	 */
	private FloatSearchBtn mFloatSearchButton;
	private FloatSearchBtn mSubmitSearchButton;
	private float mButtonSurroundSpace;

	/**
	 * Animation state
	 */
	private float mStickPosX;
	private float mStickPosY;
	private float mSearchPanelStickPosX;
	private float mSearchPanelStickPosY;
	private float mStartPosX;
	private float mStartPosY;
	private float mSearchPanelStartPosX;
	private float mSearchPanelStartPosY;
	private float mEndPosX;
	private float mEndPosY;

	public FloatSearch(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FloatSearch(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FloatSearch(Context context) {
		super(context);
	}

	public void init() {
		DisplayMetrics dm = getContext().getResources().getDisplayMetrics();

		mFloatMutil = (Util.MULTI * 0.25f) + 0.9f;

		mButtonSize = MyUtils.TypedValueUtil.toPixel(dm, "dp", Util.SEARCH_BUTTON_BASIC_SIZE * mFloatMutil);
		mButtonSurroundSpace = MyUtils.TypedValueUtil.toPixel(dm, "dp", Util.SEARCH_BUTTON_SURROUND_SPACE * 2 * mFloatMutil);

		mImgWidth = MyUtils.TypedValueUtil.toPixel(dm, "dp", Util.SEARCH_CATA_IMG_BASIC_SIZE * mFloatMutil);

		Util.TEXT_SIZE = (int) (mImgWidth * Util.TEXT_SIZE_ERROR);

		mSearchPanelWidth = (int) MyUtils.TypedValueUtil.toPixel(dm, "dp", Util.SEARCH_PANEL_CONTAINER_BASIC_WIDTH * mFloatMutil);

		mSearchPanelHeight = ((mImgWidth * 7 + mImgWidth / 2) + mButtonSurroundSpace + mButtonSize + (Util.LIST_DIVIDER_HEIGHT * 2f));

		mIsMenuOpen = false;
		mImm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

		initFloatSearchButton();

		initFloatPlayground();

		initSearchPanel();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		mStickPosX = (r - l) / 2;
		mStickPosY = (b - t) / 2;
		mSearchPanelStickPosX = mStickPosX - (mSearchPanelWidth / 2f);
		mSearchPanelStickPosY = mStickPosY - (mSearchPanelHeight / 2f);
	}

	public void onDestroy() {
		Logger.d(getClass(), "FloatSearch onDestroy()");

		mKeywordEdit.onDestroy();
		mKeywordEdit = null;

		mCityList.setOnTouchListener(null);
		mCityList.setOnScrollListener(null);
		mCityList.setAdapter(null);
		mCityList = null;

		mStartYearList.setOnTouchListener(null);
		mStartYearList.setOnScrollListener(null);
		mStartYearList.setAdapter(null);
		mStartYearList = null;

		mStartDateList.setOnTouchListener(null);
		mStartDateList.setOnScrollListener(null);
		mStartDateList.setAdapter(null);
		mStartDateList = null;

		mStartMonthList.setOnTouchListener(null);
		mStartMonthList.setOnScrollListener(null);
		mStartMonthList.setAdapter(null);
		mStartMonthList = null;

		mEndYearList.setOnTouchListener(null);
		mEndYearList.setOnScrollListener(null);
		mEndYearList.setAdapter(null);
		mEndYearList = null;

		mEndDateList.setOnTouchListener(null);
		mEndDateList.setOnScrollListener(null);
		mEndDateList.setAdapter(null);
		mEndDateList = null;

		mEndMonthList.setOnTouchListener(null);
		mEndMonthList.setOnScrollListener(null);
		mEndMonthList.setAdapter(null);
		mEndMonthList = null;

		mFloatSearchButton.setOnTouchListener(null);
		mFloatSearchButton = null;

		mSubmitSearchButton.setOnClickListener(null);
		mSubmitSearchButton = null;

		mSearchPanel.setOnTouchListener(null);
		mSearchPanel = null;

		mFloatPlayground.setOnTouchListener(null);
		mFloatPlayground = null;

		mActPageSlideTab = null;
	}

	// =====================================================================
	// Search Panel
	public void initSearchPanel() {
		mSearchPanel = new FloatSearchPanel(getContext(), mFloatMutil, mImgWidth);
		mSearchPanel.setVisibility(View.GONE);
		mSearchPanel.setPadding((int) mImgWidth / 2, 0, (int) mImgWidth / 2, 0);
		mSearchPanel.addView(MyUtils.InflateUtil.InflateReource(getContext(), R.layout.act_search_layout, this));
		mSearchPanel.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// If search panel is open, dont that the touch event pass to the view page
				return mIsMenuOpen;
			}
		});
		mTitleSpace = (Space) mSearchPanel.findViewById(R.id.title_space);
		mTitleSpace.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (mImgWidth * 1.5f)));
		mTitleSpace.setBackgroundColor(Color.BLUE);

		mKeywordEdit = (FloatSearchEdit) mSearchPanel.findViewById(R.id.keyword_container);
		mKeywordEdit.init((int) mImgWidth);

		mKeywordSpace = (Space) mSearchPanel.findViewById(R.id.keyword_space);
		mKeywordSpace.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) mImgWidth / 2));

		mCityList = (FloatSearchList) mSearchPanel.findViewById(R.id.city_list);
		mCityList.setAdapterItems(Util.FILTER_CITY.toArray(new String[Util.FILTER_CITY.size()]));
		mCityList.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (mImgWidth + Util.LIST_DIVIDER_HEIGHT * 2)));
		mCityList.setFloatMulti(mFloatMutil);
		mCityList.setOnTouchListener(new ListTouchListener());

		mCitySpace = (Space) mSearchPanel.findViewById(R.id.city_space);
		mCitySpace.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) mImgWidth / 2));

		mStartDateContainer = (LinearLayout) mSearchPanel.findViewById(R.id.start_time_container);
		mStartDateContainer.addView(MyUtils.InflateUtil.InflateReource(getContext(), R.layout.act_search_start_date, mStartDateContainer));

		mStartYearList = (FloatSearchList) mStartDateContainer.findViewById(R.id.start_year);
		mStartYearList.setAdapterItems(Util.FILTER_YEAR.toArray(new String[Util.FILTER_YEAR.size()]));
		mStartYearList.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) mImgWidth));
		mStartYearList.setFloatMulti(mFloatMutil);
		mStartYearList.setOnTouchListener(new ListTouchListener());
		mStartDateList = (FloatSearchList) mStartDateContainer.findViewById(R.id.start_date);
		mStartDateList.setAdapterItems(Util.FILTER_DATE_BIG.toArray(new String[Util.FILTER_DATE_BIG.size()]));
		mStartDateList.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) mImgWidth));
		mStartDateList.setFloatMulti(mFloatMutil);
		mStartDateList.setOnTouchListener(new ListTouchListener());
		mStartMonthList = (FloatSearchList) mStartDateContainer.findViewById(R.id.start_month);
		mStartMonthList.setAdapterItems(Util.FILTER_MONTH.toArray(new String[Util.FILTER_MONTH.size()]));
		mStartMonthList.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) mImgWidth));
		mStartMonthList.setFloatMulti(mFloatMutil);
		mStartMonthList.setOnTouchListener(new ListTouchListener());
		mStartMonthList.setRelateDateList(mStartDateList);

		mDateSpace = (Space) mSearchPanel.findViewById(R.id.time_space);
		mDateSpace.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) mImgWidth / 2));

		mEndDateContainer = (LinearLayout) mSearchPanel.findViewById(R.id.end_time_container);
		mEndDateContainer.setOnTouchListener(new ListTouchListener());
		mEndDateContainer.addView(MyUtils.InflateUtil.InflateReource(getContext(), R.layout.act_search_end_date, mEndDateContainer));

		mEndYearList = (FloatSearchList) mEndDateContainer.findViewById(R.id.end_year);
		mEndYearList.setAdapterItems(Util.FILTER_YEAR.toArray(new String[Util.FILTER_YEAR.size()]));
		mEndYearList.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) mImgWidth));
		mEndYearList.setFloatMulti(mFloatMutil);
		mEndYearList.setOnTouchListener(new ListTouchListener());
		mEndDateList = (FloatSearchList) mEndDateContainer.findViewById(R.id.end_date);
		mEndDateList.setAdapterItems(Util.FILTER_DATE_BIG.toArray(new String[Util.FILTER_DATE_BIG.size()]));
		mEndDateList.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) mImgWidth));
		mEndDateList.setFloatMulti(mFloatMutil);
		mEndDateList.setOnTouchListener(new ListTouchListener());
		mEndMonthList = (FloatSearchList) mEndDateContainer.findViewById(R.id.end_month);
		mEndMonthList.setAdapterItems(Util.FILTER_MONTH.toArray(new String[Util.FILTER_MONTH.size()]));
		mEndMonthList.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) mImgWidth));
		mEndMonthList.setFloatMulti(mFloatMutil);
		mEndMonthList.setOnTouchListener(new ListTouchListener());
		mEndMonthList.setRelateDateList(mEndDateList);

		mSubmitBtnContainer = (RelativeLayout) mSearchPanel.findViewById(R.id.submit_button_container);
		mSubmitBtnContainer.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) (mButtonSize + mButtonSurroundSpace)));

		mSubmitSearchButton = new FloatSearchBtn(getContext());
		mSubmitSearchButton.setOnClickListener(new SubmitsearchButtonClickListener());
		RelativeLayout.LayoutParams submitSearchBtnParams = new RelativeLayout.LayoutParams((int) mButtonSize, (int) mButtonSize);
		submitSearchBtnParams.addRule(RelativeLayout.CENTER_IN_PARENT);

		mSubmitBtnContainer.addView(mSubmitSearchButton, submitSearchBtnParams);

		addView(mSearchPanel, (int) mSearchPanelWidth, (int) mSearchPanelHeight);

		initList();
	}

	// Submit Search Button
	/**
	 * Close the search panel: 1. Move submit button to the center of screen and invisible 2. Next step is followed by closeSearchPanel()
	 */
	private class SubmitsearchButtonClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (checkDate()) {
				closeSearchPanel();
				if (MyUtils.SystemUtil.isNetworkEnabled(getContext())) {
					triggerQuery();
				} else {
					Util.mToast = ToastUtil.restart(getContext(), Util.mToast, R.string.check_network_enable);
				}
			}
		}
	}

	public boolean checkDate() {
		if (mStartYearList.getCurrentSelect() > mEndYearList.getCurrentSelect()) {
			Util.mToast = MyUtils.ToastUtil.restart(getContext(), Util.mToast, R.string.date_start_after_end_error);
			return false;
		} else if (mStartYearList.getCurrentSelect() == mEndYearList.getCurrentSelect()) {
			if (mStartMonthList.getCurrentSelect() > mEndMonthList.getCurrentSelect()) {
				Util.mToast = MyUtils.ToastUtil.restart(getContext(), Util.mToast, R.string.date_start_after_end_error);
				return false;
			} else if (mStartMonthList.getCurrentSelect() == mEndMonthList.getCurrentSelect()) {
				if (mStartDateList.getCurrentSelect() > mEndDateList.getCurrentSelect()) {
					Util.mToast = MyUtils.ToastUtil.restart(getContext(), Util.mToast, R.string.date_start_after_end_error);
					return false;
				}
			}
		}
		return true;
	}

	private void closeSearchPanel() {
		mIsMenuOpen = false;
		/**
		 * Close up the search panel: 1. Move search button and go invisible 2. Open search panel form different pivot
		 */
		ArrayList<ObjectAnimator> list = new ArrayList<ObjectAnimator>();
		addAnimation(list, mFloatSearchButton, "translationX", mEndPosX, mStartPosX);
		addAnimation(list, mFloatSearchButton, "translationY", mEndPosY, mStartPosY);
		addAnimation(list, mFloatSearchButton, "alpha", 0f, 1f);
		addAnimation(list, mSearchPanel, "translationX", mSearchPanelStickPosX, mSearchPanelStartPosX);
		addAnimation(list, mSearchPanel, "translationY", mSearchPanelStickPosY, mSearchPanelStartPosY);
		addAnimation(list, mSearchPanel, "scaleX", 1f, 0f);
		addAnimation(list, mSearchPanel, "scaleY", 1f, 0f);
		addAnimation(list, mSearchPanel, "alpha", 1f, 0f);
		addAnimation(list, mFloatPlayground, "alpha", 0.5f, 0f);

		AnimatorSet set = new AnimatorSet();
		set.playTogether(list.toArray(new ObjectAnimator[list.size()]));
		set.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator arg0) {
				mIsAnimate = true;
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				mIsAnimate = false;

				mFloatPlayground.setVisibility(View.GONE);
				mSearchPanel.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationCancel(Animator arg0) {
				mIsAnimate = false;
			}
		});
		set.setInterpolator(new DecelerateInterpolator(2f));
		set.setDuration(Util.ANIMATION_BASIC_DURATION_MS).start();
	}

	public void triggerQuery() {
		QueryCondition queryCondition = new QueryCondition();
		queryCondition.push(Util.DB_TITLE, mKeywordEdit.getEdit().getText().toString());
		queryCondition.push(Util.DB_CITY, Util.FILTER_CITY.get(mCityList.getCurrentSelect()));
		queryCondition.push(Util.DB_START_DATE, Util.FILTER_YEAR_CODE[mStartYearList.getCurrentSelect()] + "-"
				+ Util.FILTER_MONTH_CODE[mStartMonthList.getCurrentSelect()] + "-" + Util.FILTER_DATE_BIG_CODE[mStartDateList.getCurrentSelect()]);
		queryCondition.push(Util.DB_END_DATE,
				Util.FILTER_YEAR_CODE[mEndYearList.getCurrentSelect()] + "-" + Util.FILTER_MONTH_CODE[mEndMonthList.getCurrentSelect()] + "-"
						+ Util.FILTER_DATE_BIG_CODE[mEndDateList.getCurrentSelect()]);

		mActPageSlideTab.triggerQuery(queryCondition);
	}

	private class ListTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mImm.isActive(mKeywordEdit.getEdit())) {
				mImm.hideSoftInputFromWindow(mKeywordEdit.getEdit().getWindowToken(), 0);
				mKeywordEdit.getEdit().clearFocus();
			}
			return false;
		}
	}

	private void initList() {
		mCityList.setSelection(0);

		mStartYearList.setSelection(0);
		mStartMonthList.setSelection(0);
		mStartDateList.setSelection(0);

		mEndYearList.setSelection(Util.FILTER_YEAR_CODE.length - 1);
		mEndMonthList.setSelection(Util.FILTER_MONTH_CODE.length - 1);
		mEndDateList.setSelection(Util.FILTER_DATE_BIG_CODE.length - 1);
	}

	// =====================================================================
	// Float playground
	private void initFloatPlayground() {
		mFloatPlayground = new RelativeLayout(getContext());
		mFloatPlayground.setVisibility(View.GONE);
		mFloatPlayground.setBackgroundColor(Color.BLACK);
		mFloatPlayground.setOnTouchListener(new OnTouchListener() {
			private float ox, oy;
			private float tx, ty;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mIsAnimate) {
					Logger.d(getClass(), "On animation, not pass touch event");
					return true;
				}

				if (mIsMenuOpen) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						Logger.d(getClass(), "FloatPlayground ACTION_DOWN");
						recordCurrentPos(event);
						break;
					case MotionEvent.ACTION_MOVE:
						Logger.d(getClass(), "FloatPlayground ACTION_MOVE");
						break;
					case MotionEvent.ACTION_UP:
						Logger.d(getClass(), "FloatPlayground ACTION_UP");
						if (!isMove(event)) {
							// Replace the onClick call back
							if (mImm.isActive(mKeywordEdit.getEdit())) {
								mKeywordEdit.getEdit().clearFocus();
								// If keyboard is invisible, hideSoftInputFromWindow will return false
								if (mImm.hideSoftInputFromWindow(mKeywordEdit.getEdit().getWindowToken(), 0)) {
									return false;
								}
							}
							closeSearchPanel();
						}
						break;
					}
					return true;
				}
				return false;
			}

			private boolean isMove(MotionEvent event) {
				return (Math.abs(event.getRawX() - tx) >= 5) || (Math.abs(event.getRawY() - ty) >= 5);
			}

			private void recordCurrentPos(MotionEvent event) {
				ox = mFloatSearchButton.getX();
				oy = mFloatSearchButton.getY();

				tx = event.getRawX();
				ty = event.getRawY();
			}
		});
		addView(mFloatPlayground, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// Logger.d(getClass(), "FloatSearch dispatchTouchEvent");

		if (mIsMenuOpen) {
			super.dispatchTouchEvent(ev);
			return true;
		}
		return super.dispatchTouchEvent(ev);
	}

	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		AnimatorSet animatorSet = moveSearchButton(mFloatSearchButton, mFloatSearchButton.getX(), mFloatSearchButton.getY(), 0, mFloatSearchButton.getY());
		animatorSet.setInterpolator(AnimationUtils.loadInterpolator(getContext(), android.R.anim.bounce_interpolator));
		animatorSet.setDuration(Util.ANIMATION_BASIC_DURATION_MS * 3);
		animatorSet.setStartDelay(Util.ANIMATION_BASIC_DURATION_MS * 2);
		animatorSet.start();
	}
	// =====================================================================
	// Float Search Button
	private void initFloatSearchButton() {
		mFloatSearchButton = new FloatSearchBtn(getContext());
		mFloatSearchButton.setOnTouchListener(new FloatSearchButtonTouchListener());
		mFloatSearchButton.setX((getContext().getResources().getDisplayMetrics().widthPixels - mButtonSize) / 2);
		mFloatSearchButton.setY(getContext().getResources().getDisplayMetrics().heightPixels / 2);

		addView(mFloatSearchButton, new RelativeLayout.LayoutParams((int) mButtonSize, (int) mButtonSize));
	}

	private class FloatSearchButtonTouchListener implements OnTouchListener {
		private Point point;
		private float ox, oy;
		private float tx, ty;

		public FloatSearchButtonTouchListener() {
			point = new Point();
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Logger.d(getClass(), "ACTION_DOWN");
				recordCurrentPos(event);
				break;
			case MotionEvent.ACTION_MOVE:
				Logger.d(getClass(), "ACTION_MOVE");
				if (isMove(event) && !mIsMenuOpen) {
					checkEdge(event);
					mFloatSearchButton.setX(point.x);
					mFloatSearchButton.setY(point.y);
				}
				break;
			case MotionEvent.ACTION_UP:
				Logger.d(getClass(), "ACTION_UP");

				if (!isMove(event)) {
					// Replace the onClick call back
					floatSearchButtonClick();
					return false;
				}
				checkAutoStickEdge();

				AnimatorSet set = moveSearchButton(mFloatSearchButton, mFloatSearchButton.getX(), mFloatSearchButton.getY(), point.x, point.y);
				set.setInterpolator(AnimationUtils.loadInterpolator(getContext(), android.R.anim.bounce_interpolator));
				set.setDuration(Util.ANIMATION_BASIC_DURATION_MS * 3);
				set.start();

				break;
			}
			return true;
		}

		private void checkEdge(MotionEvent event) {
			point.x = (int) ox + (int) (event.getRawX() - tx);
			point.y = (int) oy + (int) (event.getRawY() - ty);
			if (point.x < 0) {
				point.x = 0;
			} else if (point.x > (getWidth() - mFloatSearchButton.getWidth())) {
				point.x = getWidth() - mFloatSearchButton.getWidth();
			}
			if (point.y < 0) {
				point.y = 0;
			} else if (point.y > (getHeight() - mFloatSearchButton.getHeight())) {
				point.y = getHeight() - mFloatSearchButton.getHeight();
			}
		}

		private void checkAutoStickEdge() {
			point.x = (int) mFloatSearchButton.getX();
			point.y = (int) mFloatSearchButton.getY();
			if (mFloatSearchButton.getX() <= (mStickPosX - (mFloatSearchButton.getWidth() / 2))) {
				point.x = 0;
			} else {
				point.x = getWidth() - mFloatSearchButton.getWidth();
			}

			if (mFloatSearchButton.getY() < (mFloatSearchButton.getWidth() * 1.5f)) {
				point.x = (int) mFloatSearchButton.getX();
				point.y = 0;
			} else if (mFloatSearchButton.getY() > (getHeight() - (mFloatSearchButton.getWidth() * 2f))) {
				point.x = (int) mFloatSearchButton.getX();
				point.y = getHeight() - mFloatSearchButton.getHeight();
			}
		}

		private void recordCurrentPos(MotionEvent event) {
			ox = mFloatSearchButton.getX();
			oy = mFloatSearchButton.getY();

			tx = event.getRawX();
			ty = event.getRawY();
		}

		private boolean isMove(MotionEvent event) {
			return (Math.abs(event.getRawX() - tx) >= 5) || (Math.abs(event.getRawY() - ty) >= 5);
		}

		private void floatSearchButtonClick() {
			openSearchPanel();
		}
	}

	/**
	 * Move view from start (x, y) to end (x, y) within 500 milliseconds.
	 */
	public AnimatorSet moveSearchButton(View view, float sXPx, float sYPx, float eXPx, float eYPx) {
		AnimatorSet set = new AnimatorSet();
		ArrayList<ObjectAnimator> list = new ArrayList<ObjectAnimator>();
		addAnimation(list, view, "translationX", sXPx, eXPx);
		addAnimation(list, view, "translationY", sYPx, eYPx);
		set.playTogether(list.toArray(new ObjectAnimator[list.size()]));
		return set;
	}

	private void openSearchPanel() {
		mIsMenuOpen = true;

		/**
		 * Show up the search panel: 1. Move search button and go invisible 2. Open search panel form different pivot
		 */
		setSpinnersPaintColor();

		mFloatPlayground.setVisibility(View.VISIBLE);
		mSearchPanel.setVisibility(View.VISIBLE);

		mSearchPanelStartPosX = mFloatSearchButton.getX() + (mFloatSearchButton.getWidth() / 2);
		mSearchPanelStartPosY = mFloatSearchButton.getY() + (mFloatSearchButton.getHeight() / 2);
		mSearchPanel.setPivotX(mSearchPanelStartPosX / getWidth());
		mSearchPanel.setPivotY(mSearchPanelStartPosY / getHeight());

		ArrayList<ObjectAnimator> list = new ArrayList<ObjectAnimator>();
		addAnimation(list, mFloatSearchButton, "translationX", (mStartPosX = mFloatSearchButton.getX()), mEndPosX = mStickPosX - (mFloatSearchButton.getWidth() / 2f));
		addAnimation(list, mFloatSearchButton, "translationY", (mStartPosY = mFloatSearchButton.getY()), mEndPosY = mSearchPanelStickPosY + (mImgWidth * 6.5f));
		addAnimation(list, mFloatSearchButton, "alpha", 0f, 0f);
		addAnimation(list, mSearchPanel, "translationX", mSearchPanelStartPosX, mSearchPanelStickPosX);
		addAnimation(list, mSearchPanel, "translationY", mSearchPanelStartPosY, mSearchPanelStickPosY);
		addAnimation(list, mSearchPanel, "scaleX", 0f, 1f);
		addAnimation(list, mSearchPanel, "scaleY", 0f, 1f);
		addAnimation(list, mSearchPanel, "alpha", 0f, 1f);
		addAnimation(list, mFloatPlayground, "alpha", 0f, 0.5f);

		AnimatorSet set = new AnimatorSet();
		set.playTogether(list.toArray(new ObjectAnimator[list.size()]));
		set.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator arg0) {
				mIsAnimate = true;
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				mIsAnimate = false;
			}

			@Override
			public void onAnimationCancel(Animator arg0) {
				mIsAnimate = false;
			}
		});
		set.setInterpolator(new DecelerateInterpolator(2f));
		set.setDuration(Util.ANIMATION_BASIC_DURATION_MS).start();
	}

	// =====================================================================
	public void setFloatSearchButtonColor(String color) {
		mFloatSearchButton.setPaintColor(color);
		mFloatSearchButton.invalidate();
	}

	public void addAnimation(List<ObjectAnimator> list, View view, String property, float start, float end) {
		list.add(ObjectAnimator.ofFloat(view, property, start, end));
	}

	public void setSpinnersPaintColor() {
		Logger.d(getClass(), "setSpinnersPaintColor");

		String color = mActPageSlideTab.getPageColor();

		mSearchPanel.setPaintColor(color);
		mSubmitSearchButton.setPaintColor(color);
		mKeywordEdit.setPaintColor(color);
		mCityList.setPaintColor(color);
		mStartYearList.setPaintColor(color);
		mStartDateList.setPaintColor(color);
		mStartMonthList.setPaintColor(color);
		mEndYearList.setPaintColor(color);
		mEndDateList.setPaintColor(color);
		mEndMonthList.setPaintColor(color);
	}

	public void setActPageSlideTab(ActPageSlideTab actPageSlideTab) {
		mActPageSlideTab = actPageSlideTab;
	}
	// =====================================================================
	public boolean isOpen() {
		return mIsMenuOpen;
	}

	public void close() {
		closeSearchPanel();
	}

}
