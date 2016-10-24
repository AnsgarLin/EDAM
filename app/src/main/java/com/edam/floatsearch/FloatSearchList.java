package com.edam.floatsearch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.edam.main.R;
import com.edam.util.Logger;
import com.edam.util.MyUtils;
import com.edam.util.Util;

public class FloatSearchList extends ListView {
	private FloatSearchList mRelativeDateList;
	private float mFloatMutil;

	private Path mSketch;
	private Paint mPaint;
	private Paint mWhitePaint;

	public FloatSearchList(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FloatSearchList(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FloatSearchList(Context context) {
		super(context);

		init();
	}

	public void init() {
		setVerticalScrollBarEnabled(false);
		// Disable the edge effect from top and bottom of listview
		setOverScrollMode(ListView.OVER_SCROLL_NEVER);
		// Disable the height effect while touch on the listview item
		setSelector(android.R.color.transparent);

		setOnScrollListener(new ScrollListener());

		mSketch = new Path();

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mWhitePaint = new Paint();
		mWhitePaint.setAntiAlias(true);
	}
	// =====================================================================================================//
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	/**
	 * Cause we need to cover the child view of listView, so we need to override dispatchDraw to draw something after the child is draw.
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		float basic = Util.PIXEL * mFloatMutil;
		canvas.drawRect(0, 0, getRight(), Util.LIST_DIVIDER_HEIGHT * 2f, mWhitePaint);
		canvas.drawRect(0, getHeight() - Util.LIST_DIVIDER_HEIGHT * 2f, getRight(), getHeight(), mWhitePaint);

		if (getChildCount() == 2 ||
				((getChildCount() == 1) &&
				((getFirstVisiblePosition()) > 0 && (getFirstVisiblePosition()) < getCount()-1))) {
			Logger.d(getClass(), "Bound:" + getChildCount());

			mSketch.reset();
			mSketch.moveTo(getWidth() / 2 - basic * 10f, Util.LIST_DIVIDER_HEIGHT * 3f);
			mSketch.rLineTo(basic * 10f, -Util.LIST_DIVIDER_HEIGHT * 3f);
			mSketch.rLineTo(basic * 10f, Util.LIST_DIVIDER_HEIGHT * 3f);
			mSketch.close();
			canvas.drawPath(mSketch, mPaint);

			mSketch.reset();
			mSketch.moveTo(getWidth() / 2 - basic * 10f, getHeight() - Util.LIST_DIVIDER_HEIGHT * 3f);
			mSketch.rLineTo(basic * 10f, Util.LIST_DIVIDER_HEIGHT * 3f);
			mSketch.rLineTo(basic * 10f, -Util.LIST_DIVIDER_HEIGHT * 3f);
			mSketch.close();
			canvas.drawPath(mSketch, mPaint);
		}
		else if (getChildCount() == 1) {
			if (getFirstVisiblePosition() == 0) {
				mSketch.reset();
				mSketch.moveTo(getWidth() / 2 - basic * 10f, getHeight() - Util.LIST_DIVIDER_HEIGHT * 3f);
				mSketch.rLineTo(basic * 10f, Util.LIST_DIVIDER_HEIGHT * 3f);
				mSketch.rLineTo(basic * 10f, -Util.LIST_DIVIDER_HEIGHT * 3f);
				mSketch.close();
				canvas.drawPath(mSketch, mPaint);
			} else if ((getFirstVisiblePosition() + 1) == getCount()) {
				mSketch.reset();
				mSketch.moveTo(getWidth() / 2 - basic * 10f, Util.LIST_DIVIDER_HEIGHT * 3f);
				mSketch.rLineTo(basic * 10f, -Util.LIST_DIVIDER_HEIGHT * 3f);
				mSketch.rLineTo(basic * 10f, Util.LIST_DIVIDER_HEIGHT * 3f);
				mSketch.close();
				canvas.drawPath(mSketch, mPaint);
			}
		}
	}
	// =====================================================================================================//
	private class ScrollListener implements OnScrollListener {
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (checkScrollState(scrollState) && (getChildAt(0) != null)) {
				if ((getScrollOffset() % (getHeight())) >= ((getHeight()) / 2)) {
					setSelection(getFirstVisiblePosition() + 1);
				} else {
					setSelection(getFirstVisiblePosition());
				}
				if (mRelativeDateList != null) {
					adjustDateItems();
				}
			}
		}

		/**
		 * Only for Month list
		 */
		private void adjustDateItems() {
			int position = getFirstVisiblePosition() + 1;

			if (position != 2) {
				if ((position != 4) && (position != 6) && (position != 9) && (position != 11)) {
					mRelativeDateList.setAdapterItems(Util.FILTER_DATE_BIG.toArray(new String[Util.FILTER_DATE_BIG.size()]));
				} else {
					mRelativeDateList.setAdapterItems(Util.FILTER_DATE_SMALL);
				}
			} else {
				mRelativeDateList.setAdapterItems(Util.FILTER_DATE_FEB);
			}
		}

		private boolean checkScrollState(int scrollState) {
			if (scrollState == SCROLL_STATE_FLING) {
				Logger.d(getClass(), "SCROLL_STATE_FLING");
			} else if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
				Logger.d(getClass(), "SCROLL_STATE_TOUCH_SCROLL");
			} else {
				Logger.d(getClass(), "SCROLL_STATE_IDLE");
				return true;
			}
			return false;
		}
	}

	public float getScrollOffset() {
		return MyUtils.ListViewUtil.getScrollOffset(this, getChildAt(0).getHeight(), 0);
	}
	// =====================================================================================================//
	public void setAdapterItems(String[] items) {
		setAdapter(new StyleListAdapter(getContext(), R.layout.basic_text, items));
	}

	private class StyleListAdapter extends ArrayAdapter<String>{
		public StyleListAdapter(Context context, int resource, String[] objects) {
			super(context, resource, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			FloatSearchText view = new FloatSearchText(getContext());
			view.setTextSize(TypedValue.COMPLEX_UNIT_PX, getHeight() - Util.LIST_DIVIDER_HEIGHT * 8f);
			view.setPosition(position);
			view.setPaintColor(mPaint.getColor());
			view.setHeight(getHeight());
			view.setText(getItem(position));
			return view;
		}
	}
	// =====================================================================================================//
	public void setPaintColor(String color) {
		mPaint.setColor(Color.parseColor(color));
		mWhitePaint.setColor(Color.parseColor(MyUtils.ColorUtil.getColorBetween(color, Util.WHITE, Util.BASIC_ALPHA)));
		setBackgroundColor(Color.parseColor(MyUtils.ColorUtil.getColorBetween(color, Util.WHITE, Util.BASIC_ALPHA)));
		setDivider(null);
	}

	public int getCurrentSelect() {
		return getFirstVisiblePosition();
	}

	// Only use for month list to control the date list
	public void setRelateDateList(FloatSearchList relateDateList) {
		mRelativeDateList = relateDateList;
	}

	public void setFloatMulti(float multi) {
		mFloatMutil = multi;
	}
}
