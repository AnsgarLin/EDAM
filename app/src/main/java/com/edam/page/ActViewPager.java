package com.edam.page;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import com.edam.main.R;
import com.edam.query.QueryCondition;
import com.edam.util.MyUtils;
import com.edam.util.Util;

public class ActViewPager extends ViewPager {
	private ActPageAdapter mActPageAdapter;

	public ActViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ActViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void initAdapter(FragmentManager fragmentManager) {
		setAdapter(mActPageAdapter = new ActPageAdapter(fragmentManager));
	}

	public void onDestroy() {
		setOnPageChangeListener(null);
		setPageTransformer(false, null);
		setAdapter(null);

		mActPageAdapter.onDestroy();
		mActPageAdapter = null;
	}
	// =====================================================================================================//
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// Logger.d(getClass(), "OnLayout: " + h);

		DisplayMetrics dm = getContext().getResources().getDisplayMetrics();

		Util.HMULTI = h / Util.PIXEL / Util.DETAIL_PAGE_BASIC_HEIGHT;

		Util.DETAIL_PAGE_WRAP_MARGIN = MyUtils.TypedValueUtil.toPixel(dm, "dp", 10 * Util.HMULTI);
		Util.BACK_FRAME_STROKE_WIDTH = MyUtils.TypedValueUtil.toPixel(dm, "dp", 3 * Util.HMULTI);
		Util.BACK_FRAME_MARGIN = MyUtils.TypedValueUtil.toPixel(dm, "dp", 5 * Util.HMULTI);
		Util.BACK_FRAME_BUTTOM_MARGIN = MyUtils.TypedValueUtil.toPixel(dm, "dp", 26 * Util.HMULTI);

		// Use Math.ceil to fix the missing part when cast float into int
		Util.TITLE_UP_SPACE_HEIGHT = (float) Math.ceil(MyUtils.TypedValueUtil.toPixel(dm, "dp", 14 * Util.HMULTI));
		Util.TITLE_HEIGHT = MyUtils.TypedValueUtil.toPixel(dm, "dp", 42 * Util.HMULTI);
		Util.TITLE_PADDING_LEFT = MyUtils.TypedValueUtil.toPixel(dm, "dp", 66 * Util.HMULTI);
		Util.TITLE_PADDING_TOP = MyUtils.TypedValueUtil.toPixel(dm, "dp", 3 * Util.HMULTI);
		Util.TITLE_PADDING_RIGHT = MyUtils.TypedValueUtil.toPixel(dm, "dp", 8 * Util.HMULTI);
		// Basic bound of text
		Util.TITLE_TEXT_WRAP_HEIGHT = (Util.TITLE_HEIGHT / 2) - Util.TITLE_PADDING_TOP;

		// Use Math.ceil to fix the missing part when cast float into int
		Util.DATE_UP_SPACE_HEIGHT = (float) Math.ceil(MyUtils.TypedValueUtil.toPixel(dm, "dp", 25 * Util.HMULTI));
		Util.DATE_HEIGHT = MyUtils.TypedValueUtil.toPixel(dm, "dp", 21 * Util.HMULTI);
		Util.DATE_IN_HEIGHT = Util.DATE_HEIGHT - (Util.BACK_FRAME_STROKE_WIDTH * 2);

		// Use Math.ceil to fix the missing part when cast float into int
		Util.LOCATE_UP_SPACE_HEIGHT = (float) Math.ceil(MyUtils.TypedValueUtil.toPixel(dm, "dp", 5 * Util.HMULTI));
		Util.LOCATE_HEIGHT = MyUtils.TypedValueUtil.toPixel(dm, "dp", 21 * Util.HMULTI);
		Util.LOCATE_IN_HEIGHT = Util.LOCATE_HEIGHT - (Util.BACK_FRAME_STROKE_WIDTH * 2);

		// Use Math.ceil to fix the missing part when cast float into int
		Util.BRIEF_UP_SPACE_HEIGHT = (float) Math.ceil(MyUtils.TypedValueUtil.toPixel(dm, "dp", 5 * Util.HMULTI));
		Util.BRIEF_HEIGHT = MyUtils.TypedValueUtil.toPixel(dm, "dp", 81 * Util.HMULTI);
		Util.BRIEF_IN_HEIGHT = Util.BRIEF_HEIGHT - (Util.BACK_FRAME_STROKE_WIDTH * 2);

		// Use Math.ceil to fix the missing part when cast float into int
		Util.GRAPH_UP_SPACE_HEIGHT = (float) Math.ceil(MyUtils.TypedValueUtil.toPixel(dm, "dp", 5 * Util.HMULTI));
		Util.GRAPH_HEIGHT = MyUtils.TypedValueUtil.toPixel(dm, "dp", 54 * Util.HMULTI);
		Util.GRAPH_IN_HEIGHT = Util.GRAPH_HEIGHT - (Util.BACK_FRAME_STROKE_WIDTH * 2);
		Util.GRAPH_PAGE_MODE_HEIGHT = Util.TITLE_UP_SPACE_HEIGHT + Util.TITLE_HEIGHT + Util.DATE_UP_SPACE_HEIGHT + Util.DATE_HEIGHT
				+ Util.LOCATE_UP_SPACE_HEIGHT + Util.LOCATE_HEIGHT + Util.BRIEF_UP_SPACE_HEIGHT + Util.BRIEF_HEIGHT;
		Util.GRAPH_PAGE_CLOSE_SIZE = MyUtils.TypedValueUtil.toPixel(dm, "dp", 25 * Util.HMULTI);

		// Use Math.ceil to fix the missing part when cast float into int
		Util.GRAFFITI_HEIGHT = (float) Math.ceil(MyUtils.TypedValueUtil.toPixel(dm, "dp", 39 * Util.HMULTI));

		Util.TITLE_UP_LAYER_WRAP_HEIGHT = (float) Math.ceil(MyUtils.TypedValueUtil.toPixel(dm, "dp", 76 * Util.HMULTI));
		Util.TITLE_GRAPH_WRAP_SIZE = MyUtils.TypedValueUtil.toPixel(dm, "dp", 66 * Util.HMULTI);
		Util.TITLE_GRAPH_SIZE = MyUtils.TypedValueUtil.toPixel(dm, "dp", 60 * Util.HMULTI);

		Util.GOTO_BUTTON_WRAP_HEIGHT = MyUtils.TypedValueUtil.toPixel(dm, "dp", 40 * Util.HMULTI);
		Util.GOTO_BUTTON_WRAP_WIDTH = MyUtils.TypedValueUtil.toPixel(dm, "dp", 114 * Util.HMULTI);
		Util.GOTO_BUTTON_SZIE = MyUtils.TypedValueUtil.toPixel(dm, "dp", 40 * Util.HMULTI);

		super.onSizeChanged(w, h, oldw, oldh);
	}
	// =====================================================================================================//
	public void triggerQuery(int currentPage, QueryCondition queryCondition) {
		if (!mActPageAdapter.triggerQuery(currentPage, queryCondition)) {
			Util.mToast = MyUtils.ToastUtil.restart(getContext(), Util.mToast, R.string.query_is_as_the_same_as_last);
		}
	}

	// =====================================================================================================//
	public void setPageListDividerColor(int currentPage, String currentPageColor) {
		mActPageAdapter.setPageListDividerColor(currentPage, currentPageColor);
	}

	public int getCount() {
		return mActPageAdapter.getCount();
	}

	public boolean isListMode() {
		return mActPageAdapter.isListMode(getCurrentItem());
	}

	public boolean isGraphPagerOpen() {
		return mActPageAdapter.isGraphPagerOpen(getCurrentItem());
	}

	public void closeGraphPager() {
		mActPageAdapter.closeGraphPager(getCurrentItem());
	}

	public boolean isTicketPagePanelOpen() {
		return mActPageAdapter.isTicketPagePanelOpen(getCurrentItem());
	}

	public void closeTicketPagePanel() {
		mActPageAdapter.closeTicketPagePanel(getCurrentItem());
	}

	public boolean isSharePagePanelOpen() {
		return mActPageAdapter.isSharePagePanelOpen(getCurrentItem());
	}

	public void closeSharePagePanel() {
		mActPageAdapter.closeSharePagePanel(getCurrentItem());
	}

	public void backToListMode() {
		mActPageAdapter.backToListMode(getCurrentItem());
	}

	public void moveToAct(int offset) {
		mActPageAdapter.moveToAct(getCurrentItem(), offset);
	}

	public void triggerDefautQueryIfNeed(int currentPage) {
		mActPageAdapter.triggerDefautQueryIfNeed(currentPage);
	}
}
