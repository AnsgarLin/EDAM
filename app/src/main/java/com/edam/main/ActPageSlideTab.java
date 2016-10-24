package com.edam.main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.edam.buttompanel.ButtomPanel;
import com.edam.floatsearch.FloatSearch;
import com.edam.page.ActViewPager;
import com.edam.query.QueryCondition;
import com.edam.util.Logger;
import com.edam.util.MyUtils;
import com.edam.util.Util;

public class ActPageSlideTab extends HorizontalScrollView {
	private FloatSearch mFloatSearch;
	private ButtomPanel mButtomPanel;
	private LinearLayout mActPageHeadWrapper;
	private LinearLayout mActPageTabsContainer;
	private TextView mActPageTitle;
	private ActViewPager mActPages;

	/**
	 * State record
	 */
	private int mCurrentPage;
	private String mCurrentPageColor;
	private float mCurrentPageOffsetInPersent;
	private int mLastScrollX;
	private float mCurrentFrameX;
	private boolean mScrollDirection;

	/**
	 * Tab is used to show the title of ActPage
	 */
	// 100 is the basic unit
	private float mTitleHeight;
	// Title paints
	private Paint mWindowPaint;
	private Paint mTitlePaint;
	private Paint mBack70Paint;
	private Paint mBack85Paint;
	private Path mSketch;
	private Path mWindow;
	private RectF mArcRect;

	/**
	 * Tab is used to show the category of ActPage
	 */
	private Paint mTabPaint;
	private float mTabHight;
	private float mTabTextSize;

	/**
	 * Cursor is used to show which tab is corresponded to current page
	 */
	// The pixel of cursor from left when sliding
	private float mTabCursorLeftOffset = 52; // scrollOffset

	public ActPageSlideTab(Context context) {
		this(context, null);
	}

	public ActPageSlideTab(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ActPageSlideTab(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// Set view to auto stretch to fit the parent
		setFillViewport(true);
		// Need set to false when override onDraw()
		setWillNotDraw(false);

		DisplayMetrics dm = getResources().getDisplayMetrics();
		Util.PIXEL = MyUtils.TypedValueUtil.toPixel(dm, "dp", 1);
		// 300 is the smallest screen(mdpi) dp, muilt = current width pixel / basic pixel / 300
		Util.MULTI = dm.widthPixels / Util.PIXEL / Util.TITLE_GRAPH_BASIC_WIDTH;

		mTabHight = MyUtils.TypedValueUtil.toPixel(dm, "dp", Util.CATA_TAB_HEIGHT);

		mTitleHeight = MyUtils.TypedValueUtil.toPixel(dm, "dp", Util.TITLE_GRAPH_BASIC_HEIGHT * Util.MULTI);

		// Use the android API to translate the left offset of scroll bar to pixel by DisplayMetrics
		mTabCursorLeftOffset = (int) MyUtils.TypedValueUtil.toPixel(dm, "dp", mTabCursorLeftOffset);

		mTabTextSize = MyUtils.TypedValueUtil.toPixel(dm, "sp", Util.CATA_TAB_TEXT_SIZE);

		// Set the act page title basic layout settings, like in xml
		mActPageTitle = new TextView(context);
		mActPageTitle.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) mTitleHeight));

		// Set the tab container basic layout settings, like in xml
		mActPageTabsContainer = new LinearLayout(context);
		mActPageTabsContainer.setOrientation(LinearLayout.HORIZONTAL);
		mActPageTabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) mTabHight));

		// Set the act page header basic layout settings, like in xml
		mActPageHeadWrapper = new LinearLayout(context);
		mActPageHeadWrapper.setOrientation(LinearLayout.VERTICAL);
		mActPageHeadWrapper.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		mActPageHeadWrapper.addView(mActPageTitle);
		mActPageHeadWrapper.addView(mActPageTabsContainer);

		addView(mActPageHeadWrapper);

		// Set Anti alias to make the Paint smooth, default Style is FULL

		mTabPaint = new Paint();
		mTabPaint.setAntiAlias(true);

		mWindowPaint = new Paint();
		mWindowPaint.setAntiAlias(true);

		mTitlePaint = new Paint();
		mTitlePaint.setAntiAlias(true);

		mBack70Paint = new Paint();
		mBack70Paint.setAntiAlias(true);

		mBack85Paint = new Paint();
		mBack85Paint.setAntiAlias(true);

		mSketch = new Path();
		mWindow = new Path();

		mArcRect = new RectF();
	}

	public void onDestroy() {
		Logger.d(getClass(), "ActPageSlideTab onDestroy()");

		mFloatSearch = null;
		mButtomPanel = null;

		mActPages.onDestroy();
		mActPages = null;

		mActPageHeadWrapper = null;
		mActPageTabsContainer = null;
	}
	// =====================================================================================================//
	public void setViewPager(View actPages, FragmentManager fragmentManager) {
		Logger.d(getClass(), "setViewPager");

		mActPages = (ActViewPager) actPages;
		mActPages.initAdapter(fragmentManager);

		mActPages.setOnPageChangeListener(new PageListener());
		mActPages.setPageTransformer(true, new PageTrans());
		// Disable the edge effect from left and right of viewpager
		mActPages.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
		// Set cache all fragments' state, which may cause all the fragment will not run through DestroyView until activity is destroied
		mActPages.setOffscreenPageLimit(mActPages.getCount());
		notifyDataSetChanged();
	}

	public void notifyDataSetChanged() {
		// Clear all view in container
		mActPageTabsContainer.removeAllViews();
		// Set ActPages by order
		for (int i = 0; i < mActPages.getCount(); i++) {
			addTextTab(i, mActPages.getAdapter().getPageTitle(i).toString());
		}
	}

	private void addTextTab(final int position, String title) {
		// Set default tab style
		TextView tab = new TextView(getContext());
		tab.setText(title);
		tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTabTextSize);
		tab.setTextColor(Color.parseColor(Util.TAB_TEXT_COLOR));
		tab.setGravity(Gravity.CENTER);
		tab.setSingleLine();
		tab.setFocusable(true);

		tab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCurrentPage != position) {
					Logger.d(getClass(), mCurrentPage + " " + "Change to " + position + " page");
					mActPages.setCurrentItem(position);
				} else {
					if (!mActPages.isListMode()) {
						Logger.d(getClass(), mCurrentPage + " " + "stay in the same page, back to list mode");
						mActPages.backToListMode();
					}
				}
			}
		});
		mActPageTabsContainer.addView(tab, position, new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f));
	}
	// =====================================================================================================//
	/**
	 * Use for search panel to trigger a new query
	 */
	public void triggerQuery(QueryCondition queryCondition) {
		mActPages.triggerQuery(mCurrentPage, queryCondition);
	}
	// =====================================================================================================//
	private class PageTrans implements ViewPager.PageTransformer {

		@Override
		public void transformPage(View view, float position) {
			int pageWidth = ((ViewGroup) view.getParent()).getWidth();
			if (position < 0) { // [-1,0)
				// During the depth animation, the default animation (a screen slide) still takes place, so you must
				// counteract the screen slide with a negative X translation.
				view.findViewById(R.id.act_page_list).setX((pageWidth * -position) / 2);
			} else {
				view.findViewById(R.id.act_page_list).setX((pageWidth * -position) / 2);
			}
		}

	}

	private class PageListener implements OnPageChangeListener {
		// Page on drag
		/**
		 * the positionOffset can be seen as the percentage of width will appear, and positionOffsetPicels in pixel
		 */
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			// Change color if scroll, and not the last page
			if ((mCurrentPage == position) && (mCurrentPage < (mActPages.getCount() - 1))) {
				setBackgroundColor(Color.parseColor(mCurrentPageColor = MyUtils.ColorUtil.getColorBetween(
						Util.ACT_CATAGORY_COLOR[mCurrentPage], Util.ACT_CATAGORY_COLOR[mCurrentPage + 1],
						positionOffset)));

				mActPages.setPageListDividerColor(mCurrentPage, mCurrentPageColor);
				mActPages.setBackgroundColor(Color.parseColor(MyUtils.ColorUtil.getColorBetween(mCurrentPageColor,
						Util.WHITE, Util.BASIC_ALPHA)));
				mActPages.setPageListDividerColor(mCurrentPage + 1, mCurrentPageColor);

				setFloatSearchButtonColor(mCurrentPageColor);
				setButtomPanelColor(mCurrentPageColor);
			}
			mCurrentPage = position;
			mCurrentPageOffsetInPersent = positionOffset;

			scrollToChild(position, (int) (positionOffset * mActPageTabsContainer.getChildAt(position).getWidth()));
			// Refresh layout
			invalidate();
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			// Page on drag
			// 0 == ViewPager.SCROLL_STATE_IDLE
			if (state == 0) {
				Logger.d(getClass(), mCurrentPage + " " + "Page state idle");
				scrollToChild(mCurrentPage, 0);
				mActPages.triggerDefautQueryIfNeed(mCurrentPage);

				setBackgroundColor(Color.parseColor(mCurrentPageColor = MyUtils.ColorUtil.getColorBetween(
						Util.ACT_CATAGORY_COLOR[mCurrentPage], Util.WHITE, 0)));

				mActPages.setPageListDividerColor(mCurrentPage, mCurrentPageColor);
				mActPages.setBackgroundColor(Color.parseColor(MyUtils.ColorUtil.getColorBetween(mCurrentPageColor,
						Util.WHITE, Util.BASIC_ALPHA)));
				mActPages.setPageListDividerColor(mCurrentPage + 1, mCurrentPageColor);

				setFloatSearchButtonColor(mCurrentPageColor);
				setButtomPanelColor(mCurrentPageColor);
			}
		}

		// Page start drag
		@Override
		public void onPageSelected(int position) {
			Logger.d(getClass(), mCurrentPage + " " + "Page state drag end & change end");
		}

		private void scrollToChild(int position, int offset) {
			if (mActPages.getCount() == 0) {
				return;
			}

			int newScrollX = mActPageTabsContainer.getChildAt(position).getLeft() + offset;

			if ((position > 0) || (offset > 0)) {
				newScrollX -= mTabCursorLeftOffset;
			}

			if (newScrollX != mLastScrollX) {
				mLastScrollX = newScrollX;
				scrollTo(newScrollX, 0);
			}
		}
	}

	// =====================================================================================================//
	@Override
	protected void onDraw(Canvas canvas) {
		// Logger.d(getClass(), "onDraw");

		super.onDraw(canvas);

		// Check whether the view is in edit mode(IDE purpose)
		if (isInEditMode() || (mActPages.getCount() == 0)) {
			return;
		}

		// default: line below current tab
		float lineLeft = mActPageTabsContainer.getChildAt(mCurrentPage).getLeft();
		float lineRight = mActPageTabsContainer.getChildAt(mCurrentPage).getRight();

		// if there is an offset, start interpolating left and right coordinates between current and next tab
		if ((mCurrentPageOffsetInPersent > 0f) && (mCurrentPage < (mActPages.getCount() - 1))) {
			lineLeft = ((mCurrentPageOffsetInPersent * mActPageTabsContainer.getChildAt(mCurrentPage + 1).getLeft()) + ((1f - mCurrentPageOffsetInPersent) * lineLeft));
			lineRight = ((mCurrentPageOffsetInPersent * mActPageTabsContainer.getChildAt(mCurrentPage + 1).getRight()) + ((1f - mCurrentPageOffsetInPersent) * lineRight));
		}
		checkScrollDirection(lineLeft);
		drawTitleAnimation(canvas, lineLeft);
		// draw float frame
		drawFloatFrame(canvas, lineLeft, lineRight);

	}

	private void checkScrollDirection(float lineLeft) {
		// Check Scroll direction
		if ((mCurrentFrameX != lineLeft)) {
			mScrollDirection = true;
		} else {
			mScrollDirection = false;
		}
		mCurrentFrameX = lineLeft;
	}

	/**
	 * Draw a float frame which can move during page changed
	 *
	 * @param Region
	 */
	private void drawFloatFrame(Canvas canvas, float lineLeft, float lineRight) {
		// Draw tab upper line
		mTabPaint.setStyle(Style.STROKE);
		mTabPaint.setStrokeWidth(Util.LIST_DIVIDER_HEIGHT);
		mTabPaint.setColor(Color.WHITE);
		canvas.drawLine(0, mTitleHeight, getWidth(), mTitleHeight, mTabPaint);
		canvas.drawLine(lineLeft, mTitleHeight, lineLeft, mTitleHeight + mTabHight, mTabPaint);
		canvas.drawLine(lineRight, mTitleHeight, lineRight, mTitleHeight + mTabHight, mTabPaint);
	}

	private void drawTitleAnimation(Canvas canvas, float lineLeft) {
		mBack85Paint.setColor(Color.parseColor(MyUtils.ColorUtil.mixColor(mCurrentPageColor, Util.WHITE, Util.BUILDING_COLOR_LAYER_1)));
		mBack70Paint.setColor(Color.parseColor(MyUtils.ColorUtil.mixColor(mCurrentPageColor, Util.WHITE, Util.BUILDING_COLOR_LAYER_2)));
		mWindowPaint.setColor(Color.parseColor(mCurrentPageColor));

		drawBack70(canvas, -(Util.PIXEL * 24f * Util.MULTI) * (lineLeft / getWidth()));
		drawBack85(canvas, -(Util.PIXEL * 36f * Util.MULTI) * (lineLeft / getWidth()));

		if (mCurrentPageOffsetInPersent != 0) {
			mTitlePaint.setColor(Color.parseColor(MyUtils.ColorUtil.mixColor(mCurrentPageColor, Util.WHITE,
					1 - ((1 - Util.BUILDING_COLOR_LAYER_1) * mCurrentPageOffsetInPersent))));
		} else {
			mTitlePaint.setColor(Color.parseColor(MyUtils.ColorUtil.mixColor(mCurrentPageColor, Util.WHITE, 1f)));
		}
		switch (mCurrentPage) {
		case 0:
			draw101Tower(canvas, (Util.PIXEL * 28f * Util.MULTI) * (1f - mCurrentPageOffsetInPersent));

			float skmRight = (Util.PIXEL * 198f * Util.MULTI) * (1f - mCurrentPageOffsetInPersent);
			drawSkmA8(canvas, skmRight - (Util.PIXEL * 62f * Util.MULTI));// 28
			drawSkmBridge(canvas, skmRight - (Util.PIXEL * 34f * Util.MULTI));// 34
			drawSkmA4(canvas, skmRight);

			drawPresidentialHouse(canvas, (Util.PIXEL * 290f * Util.MULTI) * (1f - mCurrentPageOffsetInPersent));

			if (mCurrentPageOffsetInPersent != 0) {
				mTitlePaint.setColor(Color.parseColor(MyUtils.ColorUtil.mixColor(mCurrentPageColor, Util.WHITE,
						Util.BUILDING_COLOR_LAYER_1 + ((1 - Util.BUILDING_COLOR_LAYER_1) * mCurrentPageOffsetInPersent))));
			} else {
				mTitlePaint.setColor(Color.parseColor(MyUtils.ColorUtil.mixColor(mCurrentPageColor, Util.WHITE, Util.BUILDING_COLOR_LAYER_1)));
			}
			drawSongShan(
					canvas,
					(Util.PIXEL * 295f * Util.MULTI)
							+ (((getRight() - (Util.PIXEL * 295.5f * Util.MULTI)) + (Util.PIXEL * 153f * Util.MULTI)) * (1f - mCurrentPageOffsetInPersent)));
			drawHuashan(
					canvas,
					(Util.PIXEL * 135.5f * Util.MULTI)
							+ (((getRight() - (Util.PIXEL * 135.5f * Util.MULTI)) + (Util.PIXEL * 130.5f * Util.MULTI)) * (1f - mCurrentPageOffsetInPersent)));

			break;
		case 1:
			drawHuashan(canvas, (Util.PIXEL * 135.5f * Util.MULTI) * (1f - mCurrentPageOffsetInPersent));
			drawSongShan(canvas, (Util.PIXEL * 295f * Util.MULTI) * (1f - mCurrentPageOffsetInPersent));

			if (mCurrentPageOffsetInPersent != 0) {
				mTitlePaint.setColor(Color.parseColor(MyUtils.ColorUtil.mixColor(mCurrentPageColor, Util.WHITE,
						Util.BUILDING_COLOR_LAYER_1 + ((1 - Util.BUILDING_COLOR_LAYER_1) * mCurrentPageOffsetInPersent))));
			} else {
				mTitlePaint.setColor(Color.parseColor(MyUtils.ColorUtil.mixColor(mCurrentPageColor, Util.WHITE, Util.BUILDING_COLOR_LAYER_1)));
			}
			drawMemHallMain(
					canvas,
					(Util.PIXEL * 299.5f * Util.MULTI)
							+ (((getRight() - (Util.PIXEL * 299.5f * Util.MULTI)) + (Util.PIXEL * 103.5f * Util.MULTI)) * (1f - mCurrentPageOffsetInPersent)));
			drawMemHallCenter(
					canvas,
					(Util.PIXEL * 193.5f * Util.MULTI)
							+ (((getRight() - (Util.PIXEL * 193.5f * Util.MULTI)) + (Util.PIXEL * 87.5f * Util.MULTI)) * (1f - mCurrentPageOffsetInPersent)));
			drawMemHallDoor(
					canvas,
					(Util.PIXEL * 103.5f * Util.MULTI)
							+ (((getRight() - (Util.PIXEL * 103.5f * Util.MULTI)) + (Util.PIXEL * 101.5f * Util.MULTI)) * (1f - mCurrentPageOffsetInPersent)));

			break;
		case 2:
			drawMemHallDoor(canvas, (Util.PIXEL * 103.5f * Util.MULTI) * (1f - mCurrentPageOffsetInPersent));
			drawMemHallCenter(canvas, (Util.PIXEL * 193.5f * Util.MULTI) * (1f - mCurrentPageOffsetInPersent));
			drawMemHallMain(canvas, (Util.PIXEL * 299.5f * Util.MULTI) * (1f - mCurrentPageOffsetInPersent));

			if (mCurrentPageOffsetInPersent != 0) {
				mTitlePaint.setColor(Color.parseColor(MyUtils.ColorUtil.mixColor(mCurrentPageColor, Util.WHITE,
						Util.BUILDING_COLOR_LAYER_1 + ((1 - Util.BUILDING_COLOR_LAYER_1) * mCurrentPageOffsetInPersent))));
			} else {
				mTitlePaint.setColor(Color.parseColor(MyUtils.ColorUtil.mixColor(mCurrentPageColor, Util.WHITE, Util.BUILDING_COLOR_LAYER_1)));
			}
			drawTitanEgg(
					canvas,
					(Util.PIXEL * 290f * Util.MULTI)
							+ (((getRight() - (Util.PIXEL * 290f * Util.MULTI)) + (Util.PIXEL * 111f * Util.MULTI)) * (1f - mCurrentPageOffsetInPersent)));
			drawCrowd(
					canvas,
					(Util.PIXEL * 178f * Util.MULTI)
							+ (((getRight() - (Util.PIXEL * 178f * Util.MULTI)) + (Util.PIXEL * 168f * Util.MULTI)) * (1f - mCurrentPageOffsetInPersent)));
			drawFlag(
					canvas,
					(Util.PIXEL * 173f * Util.MULTI)
							+ (((getRight() - (Util.PIXEL * 173f * Util.MULTI)) + (Util.PIXEL * 163f * Util.MULTI)) * (1f - mCurrentPageOffsetInPersent)));

			break;
		case 3:
			drawFlag(canvas, (Util.PIXEL * 173f * Util.MULTI) * (1f - mCurrentPageOffsetInPersent));
			drawCrowd(canvas, (Util.PIXEL * 178f * Util.MULTI) * (1f - mCurrentPageOffsetInPersent));
			drawTitanEgg(canvas, (Util.PIXEL * 290f * Util.MULTI) * (1f - mCurrentPageOffsetInPersent));

			break;
		default:
			break;
		}
	}

	// =====================================================================================================//
	private void drawTitanEgg(Canvas canvas, float right) {
		float floorO = Util.PIXEL * Util.MULTI;
		float buildR = right;
		float tmpT = floorO * 100f;

		mSketch.reset();
		mWindow.reset();

		mSketch.moveTo(buildR, tmpT);
		mSketch.rLineTo(-floorO, -floorO * 19f);
		mSketch.rLineTo(0, -floorO * 2f);
		mSketch.rLineTo(-floorO * 5.5f, 0);
		mSketch.rLineTo(floorO / 2f, -floorO * 7.5f);
		mSketch.rLineTo(-floorO * 3.5f, 0);
		mSketch.rLineTo(-floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(0, -floorO);
		mSketch.rLineTo(-floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(-floorO * 32f, -floorO * 9.5f);
		mSketch.rQuadTo(-floorO * 51f, 0, -floorO * 67f, floorO * 8.5f);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(-floorO / 2f, 0);
		mSketch.rLineTo(-floorO * 2f, floorO * 2f);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(floorO * 3.5f, floorO * 18f);
		mSketch.rLineTo(-floorO * 3f, floorO * 4.5f);
		mSketch.rLineTo(0, floorO * 6f);

		mWindow.moveTo(buildR - (floorO * 9.5f), tmpT -= floorO * 30.5f);
		mWindow.rLineTo(-floorO * 32f, -floorO * 6.5f);
		mWindow.rLineTo(-floorO * 23.5f, floorO * 2f);
		mWindow.rLineTo(-floorO * 8.5f, floorO * 6f);
		mWindow.rLineTo(-floorO * 35f, -floorO * 2.5f);
		mWindow.rLineTo(-floorO / 2f, floorO / 2f);
		mWindow.rLineTo(floorO * 35.5f, floorO * 2.5f);
		mWindow.rLineTo(floorO * 8.5f, -floorO * 6f);
		mWindow.rLineTo(floorO * 23.5f, -floorO * 2f);
		mWindow.rLineTo(floorO * 32f, floorO * 6.5f);

		for (int i = 0; i < 6; i++) {
			mWindow.moveTo(buildR - (floorO * 16.5f) - (floorO * 5f * i), tmpT + floorO);
			mWindow.rLineTo(floorO * 3f, floorO);
			mWindow.rLineTo(-floorO, floorO * 1.5f);
			mWindow.rLineTo(-floorO * 3f, -floorO);
		}

		mWindow.moveTo(buildR - (floorO * 13f), tmpT += (floorO * 3.5f));
		mWindow.rLineTo(-floorO * 30f, 0);
		mWindow.rLineTo(0, floorO * 1.5f);
		mWindow.rLineTo(-floorO * 21.5f, 0);
		mWindow.rLineTo(0, floorO / 2f);
		mWindow.rLineTo(floorO * 21.5f, 0);
		mWindow.rLineTo(0, floorO);
		mWindow.rLineTo(-floorO * 12.5f, 0);
		mWindow.rLineTo(0, floorO * 2.5f);
		mWindow.rLineTo(-floorO * 5f, 0);
		mWindow.rLineTo(0, floorO * 5.5f);
		mWindow.rLineTo(-floorO * 4f, 0);
		mWindow.rLineTo(0, floorO / 2f);
		mWindow.rLineTo(floorO * 4.5f, 0);
		mWindow.rLineTo(0, -floorO * 5.5f);
		mWindow.rLineTo(floorO * 5f, 0);
		mWindow.rLineTo(0, -floorO * 2.5f);
		mWindow.rLineTo(floorO * 12.5f, 0);
		mWindow.rLineTo(0, -floorO * 3f);
		mWindow.rLineTo(floorO * 29.5f, 0);

		mWindow.moveTo(buildR - (floorO * 13f), tmpT += (floorO * 3.5f));
		mWindow.rLineTo(0, floorO * 2.5f);
		mWindow.rLineTo(-floorO * 26.5f, 0);
		mWindow.rLineTo(0, -floorO * 2.5f);

		for (int i = 0; i < 6; i++) {
			mWindow.moveTo(buildR - (floorO * 40f) - (floorO * 2.5f * i), tmpT + (floorO * 1.5f));
			mWindow.rLineTo(0, floorO);
			mWindow.rLineTo(-floorO * 2f, 0);
			mWindow.rLineTo(0, -floorO);
		}

		mWindow.moveTo(buildR - (floorO * 1.5f), tmpT += (floorO * 4.5f));
		mWindow.rLineTo(floorO, floorO * 19f);
		mWindow.rLineTo(-floorO * 6f, 0);
		mWindow.rLineTo(0, -floorO * 19f);

		for (int j = 0; j < 9; j++) {
			if (j < 7) {
				for (int i = 0; i < 10; i++) {
					mWindow.addRect(buildR - (floorO * 7f) - (floorO * 5.5f * j), tmpT + (floorO * 1.5f * i), buildR
							- (floorO * 12f) - (floorO * 5.5f * j), tmpT + (floorO / 2f) + (floorO * 1.5f * i),
							Direction.CW);
				}
				mWindow.addRect(buildR - (floorO * 7f) - (floorO * 5.5f * j), tmpT + (floorO * 15f), buildR
						- (floorO * 12f) - (floorO * 5.5f * j), tmpT + (floorO * 19f), Direction.CW);
			} else if (j == 7) {
				for (int i = 0; i < 3; i++) {
					mWindow.addRect(buildR - (floorO * 7f) - (floorO * 5.5f * j), tmpT + (floorO * 1.5f * i), buildR
							- (floorO * 12f) - (floorO * 5.5f * j), tmpT + (floorO / 2f) + (floorO * 1.5f * i),
							Direction.CW);
				}
			} else {
				for (int i = 0; i < 3; i++) {
					mWindow.addRect(buildR - (floorO * 7f) - (floorO * 5.5f * j), tmpT + (floorO * 1.5f * i), buildR
							- (floorO * 10.5f) - (floorO * 5.5f * j), tmpT + (floorO / 2f) + (floorO * 1.5f * i),
							Direction.CW);
				}
			}
		}

		for (int j = 0; j < 11; j++) {
			if (j < 2) {
				for (int i = 0; i < 2; i++) {
					mWindow.addRect(buildR - (floorO * 45.5f) - (floorO * 1.5f * j), tmpT + (floorO * 6f)
							+ (floorO * 1.5f * i), buildR - (floorO * 46.5f) - (floorO * 1.5f * j), tmpT
							+ (floorO * 7f) + (floorO * 1.5f * i), Direction.CW);
				}
			} else if (j > 8) {
				for (int i = 0; i < 2; i++) {
					mWindow.addRect(buildR - (floorO * 48.5f) - (floorO * 1.5f * j), tmpT + (floorO * 6f)
							+ (floorO * 1.5f * i), buildR - (floorO * 49.5f) - (floorO * 1.5f * j), tmpT
							+ (floorO * 7f) + (floorO * 1.5f * i), Direction.CW);
				}
			} else {
				for (int i = 0; i < 2; i++) {
					mWindow.addRect(buildR - (floorO * 47f) - (floorO * 1.5f * j), tmpT + (floorO * 6f)
							+ (floorO * 1.5f * i), buildR - (floorO * 48f) - (floorO * 1.5f * j), tmpT + (floorO * 7f)
							+ (floorO * 1.5f * i), Direction.CW);
				}
			}
		}

		mWindow.moveTo(buildR - (floorO * 65.5f), tmpT -= floorO * 6.5f);
		mWindow.rLineTo(floorO / 4f, floorO / 4f);
		mWindow.rLineTo(floorO / 4f, -floorO / 4f);
		mWindow.rLineTo(0, floorO * 19.5f);
		mWindow.rLineTo(-floorO * 5f, 0);
		mWindow.rLineTo(0, floorO * 6f);
		mWindow.rLineTo(-floorO * 39f, 0);
		mWindow.rLineTo(0, -floorO * 7.5f);
		mWindow.rLineTo(floorO * 2f, -floorO * 3f);
		mWindow.rLineTo(-floorO * 3.5f, -floorO * 18f);

		mSketch.close();
		mWindow.close();

		canvas.drawPath(mSketch, mTitlePaint);
		canvas.drawPath(mWindow, mWindowPaint);

		mSketch.reset();
		mWindow.reset();

		tmpT = floorO * 71f;
		mWindow.moveTo(buildR - (floorO * 9.5f), tmpT);
		mWindow.rLineTo(-floorO * 4f, 0);
		mWindow.rLineTo(floorO / 2f, floorO * 8f);
		mWindow.rLineTo(floorO / 2f, 0);
		mWindow.rLineTo(-floorO / 2f, -floorO * 7.5f);
		mWindow.rLineTo(floorO * 3.5f, 0);

		for (int i = 0; i < 10; i++) {
			if (i < 6) {
				mSketch.addRect(buildR - floorO, tmpT + (floorO * 10.5f) + (floorO * 1.5f * i), buildR - (floorO * 7f),
						tmpT + (floorO * 11.5f) + (floorO * 1.5f * i), Direction.CW);
			} else {
				mSketch.addRect(buildR - (floorO / 2f), tmpT + (floorO * 10.5f) + (floorO * 1.5f * i), buildR
						- (floorO * 7f), tmpT + (floorO * 11.5f) + (floorO * 1.5f * i), Direction.CW);
			}
		}

		mSketch.moveTo(buildR -= (floorO * 65f), tmpT + (floorO * 3.5f));
		mSketch.rLineTo(-floorO / 2f, floorO * 15f);
		mSketch.rLineTo(-floorO, 0);
		mSketch.rLineTo(-floorO, -floorO * 15.5f);
		mSketch.rLineTo(-floorO * 1.5f, floorO * 15.5f);
		mSketch.rLineTo(-floorO / 2f, 0);
		mSketch.rLineTo(floorO * 1.5f, -floorO * 15.5f);
		mSketch.rLineTo(floorO, 0);
		mSketch.rLineTo(floorO, floorO * 15.5f);
		mSketch.rLineTo(floorO / 2f, -floorO * 15f);

		for (int i = 0; i < 3; i++) {
			mSketch.addRect(buildR, tmpT + (floorO * 18.5f) + (floorO * i), buildR - (floorO * 4.5f), tmpT
					+ (floorO * 19f) + (floorO * i), Direction.CW);
		}

		tmpT += floorO / 2f;
		buildR -= (floorO * 4.5f);
		for (int i = 0; i < 12; i++) {
			if (i < 4) {
				mSketch.addRect(buildR - (floorO * 4.5f * i), tmpT, buildR - (floorO / 2f) - (floorO * 4.5f * i), tmpT
						+ (floorO * 28.5f), Direction.CW);
			} else if (i > 6) {
				mSketch.addRect((buildR - (floorO * 4.5f * i)) + (floorO * 2f) + (floorO * 2f * (i - 6)), tmpT, (buildR
						- (floorO / 2f) - (floorO * 4.5f * i))
						+ (floorO * 2f) + (floorO * 2f * (i - 6)), tmpT + (floorO * 28.5f), Direction.CW);
			} else {
				mSketch.addRect((buildR - (floorO * 4.5f * i)) + (floorO * (i - 3)), tmpT,
						(buildR - (floorO / 2f) - (floorO * 4.5f * i)) + (floorO * (i - 3)), tmpT + (floorO * 28.5f),
						Direction.CW);
			}
		}

		for (int i = 0; i < 11; i++) {
			if (i < 3) {
				mSketch.addRect(buildR - (floorO * 2f) - (floorO * 4.5f * i), tmpT + (floorO * 21.5f), buildR
						- (floorO * 3f) - (floorO * 4.5f * i), tmpT + (floorO * 28.5f), Direction.CW);
			} else if (i > 6) {
				mSketch.addRect((buildR - (floorO * 28.5f) - (floorO * 2.5f * (i - 7))), tmpT + (floorO * 21.5f),
						(buildR - (floorO * 29.5f) - (floorO * 2.5f * (i - 7))), tmpT + (floorO * 28.5f), Direction.CW);
			} else {
				mSketch.addRect((buildR - (floorO * 15f) - (floorO * 3.5f * (i - 3))), tmpT + (floorO * 21.5f), (buildR
						- (floorO * 16f) - (floorO * 3.5f * (i - 3))), tmpT + (floorO * 28.5f), Direction.CW);
			}
		}

		for (int i = 0; i < 7; i++) {
			if (i < 6) {
				mSketch.addRect(buildR - (floorO / 2f), tmpT + (floorO * 3f) + (floorO * 3f * i),
						(buildR - (floorO * 40.5f)) + ((floorO / 2f) * i), tmpT + (floorO * 3.5f) + (floorO * 3f * i),
						Direction.CW);
			} else {
				mSketch.addRect(buildR - (floorO / 2f), tmpT + (floorO * 4.5f) + (floorO * 3f * i),
						(buildR - (floorO * 40.5f)), tmpT + (floorO * 5.5f) + (floorO * 3f * i), Direction.CW);
			}
		}

		for (int i = 0; i < 3; i++) {
			mSketch.moveTo(buildR - (floorO / 2f) - (floorO * 4.5f * i), tmpT + (floorO * 18.5f));
			mSketch.rLineTo(-floorO * 1.5f, floorO * 3f);
			mSketch.rLineTo(-floorO, 0);
			mSketch.rLineTo(-floorO * 1.5f, -floorO * 3f);
			mSketch.rLineTo(floorO / 2f, 0);
			mSketch.rLineTo(floorO * 1.5f, floorO * 3f);
			mSketch.rLineTo(floorO * 1.5f, -floorO * 3f);
		}

		for (int i = 0; i < 4; i++) {
			mSketch.moveTo(buildR - (floorO * 14f) - (floorO * 3.5f * i), tmpT + (floorO * 18.5f));
			mSketch.rLineTo(-floorO, floorO * 3f);
			mSketch.rLineTo(-floorO, 0);
			mSketch.rLineTo(-floorO, -floorO * 3f);
			mSketch.rLineTo(floorO / 2f, 0);
			mSketch.rLineTo(floorO, floorO * 3f);
			mSketch.rLineTo(floorO, -floorO * 3f);
		}

		for (int i = 0; i < 4; i++) {
			mSketch.moveTo(buildR - (floorO * 28f) - (floorO * 2.5f * i), tmpT + (floorO * 18.5f));
			mSketch.rLineTo(-floorO / 2f, floorO * 3f);
			mSketch.rLineTo(-floorO, 0);
			mSketch.rLineTo(-floorO / 2f, -floorO * 3f);
			mSketch.rLineTo(floorO / 2f, 0);
			mSketch.rLineTo(floorO / 2f, floorO * 3f);
			mSketch.rLineTo(floorO / 2f, -floorO * 3f);
		}
		mSketch.close();
		mWindow.close();

		canvas.drawPath(mSketch, mTitlePaint);
		canvas.drawPath(mWindow, mWindowPaint);
	}

	private void drawFlag(Canvas canvas, float right) {
		float floorO = Util.PIXEL * Util.MULTI;
		float buildR = right;
		float tmpT = floorO * 71f;

		mSketch.reset();
		mWindow.reset();

		mSketch.addRect(buildR, tmpT, buildR - (floorO * 45f), tmpT + (floorO * 17f), Direction.CW);
		mSketch.addRect(buildR - (floorO * 5f), tmpT + (floorO * 17f), buildR - (floorO * 6f), tmpT + (floorO * 27f),
				Direction.CW);
		mSketch.addRect(buildR - (floorO * 16f), tmpT + (floorO * 17f), buildR - (floorO * 17f), tmpT + (floorO * 27f),
				Direction.CW);
		mSketch.addRect(buildR - (floorO * 20f), tmpT + (floorO * 17f), buildR - (floorO * 21f), tmpT + (floorO * 27f),
				Direction.CW);
		mSketch.addRect(buildR - (floorO * 25f), tmpT + (floorO * 17f), buildR - (floorO * 26f), tmpT + (floorO * 27f),
				Direction.CW);
		mSketch.addRect(buildR - (floorO * 32f), tmpT + (floorO * 17f), buildR - (floorO * 33f), tmpT + (floorO * 27f),
				Direction.CW);
		mSketch.addRect(buildR - (floorO * 38f), tmpT + (floorO * 17f), buildR - (floorO * 39f), tmpT + (floorO * 27f),
				Direction.CW);

		mSketch.addRect(buildR -= (floorO * 48f), tmpT, buildR - (floorO * 48f), tmpT + (floorO * 17f), Direction.CW);
		mSketch.addRect(buildR - (floorO * 5f), tmpT + (floorO * 17f), buildR - (floorO * 6f), tmpT + (floorO * 27f),
				Direction.CW);
		mSketch.addRect(buildR - (floorO * 10f), tmpT + (floorO * 17f), buildR - (floorO * 11f), tmpT + (floorO * 27f),
				Direction.CW);
		mSketch.addRect(buildR - (floorO * 19f), tmpT + (floorO * 17f), buildR - (floorO * 20f), tmpT + (floorO * 27f),
				Direction.CW);
		mSketch.addRect(buildR - (floorO * 23f), tmpT + (floorO * 17f), buildR - (floorO * 24f), tmpT + (floorO * 27f),
				Direction.CW);
		mSketch.addRect(buildR - (floorO * 33f), tmpT + (floorO * 17f), buildR - (floorO * 34f), tmpT + (floorO * 27f),
				Direction.CW);
		mSketch.addRect(buildR - (floorO * 42f), tmpT + (floorO * 17f), buildR - (floorO * 43f), tmpT + (floorO * 27f),
				Direction.CW);

		mSketch.addRect(buildR -= (floorO * 51f), tmpT, buildR - (floorO * 31f), tmpT + (floorO * 17f), Direction.CW);
		mSketch.addRect(buildR - (floorO * 5f), tmpT + (floorO * 17f), buildR - (floorO * 6f), tmpT + (floorO * 27f),
				Direction.CW);
		mSketch.addRect(buildR - (floorO * 12f), tmpT + (floorO * 17f), buildR - (floorO * 13f), tmpT + (floorO * 27f),
				Direction.CW);
		mSketch.addRect(buildR - (floorO * 18f), tmpT + (floorO * 17f), buildR - (floorO * 19f), tmpT + (floorO * 27f),
				Direction.CW);
		mSketch.addRect(buildR - (floorO * 25f), tmpT + (floorO * 17f), buildR - (floorO * 26f), tmpT + (floorO * 27f),
				Direction.CW);

		mSketch.addRect(buildR -= (floorO * 34f), tmpT, buildR - (floorO * 30f), tmpT + (floorO * 17f), Direction.CW);
		mSketch.addRect(buildR - (floorO * 5f), tmpT + (floorO * 17f), buildR - (floorO * 6f), tmpT + (floorO * 27f),
				Direction.CW);
		mSketch.addRect(buildR - (floorO * 10f), tmpT + (floorO * 17f), buildR - (floorO * 11f), tmpT + (floorO * 27f),
				Direction.CW);
		mSketch.addRect(buildR - (floorO * 15f), tmpT + (floorO * 17f), buildR - (floorO * 16f), tmpT + (floorO * 27f),
				Direction.CW);
		mSketch.addRect(buildR - (floorO * 20f), tmpT + (floorO * 17f), buildR - (floorO * 21f), tmpT + (floorO * 27f),
				Direction.CW);
		mSketch.addRect(buildR - (floorO * 24.5f), tmpT + (floorO * 17f), buildR - (floorO * 25.5f), tmpT
				+ (floorO * 27f), Direction.CW);
		// A
		mWindow.moveTo(buildR -= floorO * 26f, tmpT += floorO);
		mWindow.rLineTo(-floorO * 3f, floorO * 15f);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(floorO * 3f, -floorO * 15f);
		mWindow.rLineTo(floorO * 3f, floorO * 15f);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(-floorO * 3f, -floorO * 15f);
		// T
		mWindow.moveTo(buildR + (floorO * 6f), tmpT);
		mWindow.rLineTo(floorO * 9f, 0);
		mWindow.rLineTo(0, floorO);
		mWindow.rLineTo(-floorO * 4f, 0);
		mWindow.rLineTo(0, floorO * 14f);
		mWindow.rLineTo(-floorO, 0);
		mWindow.rLineTo(0, -floorO * 14f);
		mWindow.rLineTo(-floorO * 4f, 0);
		// T
		mWindow.moveTo(buildR + (floorO * 16f), tmpT);
		mWindow.rLineTo(floorO * 9f, 0);
		mWindow.rLineTo(0, floorO);
		mWindow.rLineTo(-floorO * 4f, 0);
		mWindow.rLineTo(0, floorO * 14f);
		mWindow.rLineTo(-floorO, 0);
		mWindow.rLineTo(0, -floorO * 14f);
		mWindow.rLineTo(-floorO * 4f, 0);
		// 卡
		mWindow.moveTo(buildR + (floorO * 34f), tmpT);
		mWindow.rLineTo(0, floorO * 9f);
		mWindow.rLineTo(-floorO * 4f, 0);
		mWindow.rLineTo(0, floorO);
		mWindow.rLineTo(floorO * 4f, 0);
		mWindow.rLineTo(0, floorO * 5f);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(0, -floorO * 3f);
		mWindow.rLineTo(floorO * 4f, floorO * 2f);
		mWindow.rLineTo(0, -floorO);
		mWindow.rLineTo(-floorO * 4f, -floorO * 2f);
		mWindow.rLineTo(0, -floorO);
		mWindow.rLineTo(floorO * 4f, 0);
		mWindow.rLineTo(0, -floorO);
		mWindow.rLineTo(-floorO * 4f, 0);
		mWindow.rLineTo(0, -floorO * 3f);
		mWindow.rLineTo(floorO * 4f, 0);
		mWindow.rLineTo(0, -floorO);
		mWindow.rLineTo(-floorO * 4f, 0);
		mWindow.rLineTo(0, -floorO * 5f);
		// 夫
		mWindow.moveTo(buildR + (floorO * 44f), tmpT);
		mWindow.rLineTo(0, floorO * 5f);
		mWindow.rLineTo(-floorO * 3f, 0);
		mWindow.rLineTo(0, floorO);
		mWindow.rLineTo(floorO * 3f, 0);
		mWindow.rLineTo(0, floorO * 3f);
		mWindow.rLineTo(-floorO * 4f, 0);
		mWindow.rLineTo(0, floorO);
		mWindow.rLineTo(floorO * 4f, 0);
		mWindow.rLineTo(-floorO * 3f, floorO * 5f);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(floorO * 3f, -floorO * 5f);
		mWindow.rLineTo(floorO * 4f, 0);
		mWindow.rLineTo(0, -floorO);
		mWindow.rLineTo(-floorO * 4f, 0);
		mWindow.rLineTo(0, -floorO * 3f);
		mWindow.rLineTo(floorO * 3f, 0);
		mWindow.rLineTo(0, -floorO);
		mWindow.rLineTo(-floorO * 3f, 0);
		mWindow.rLineTo(0, -floorO * 5f);
		// 卡
		mWindow.moveTo(buildR + (floorO * 54f), tmpT);
		mWindow.rLineTo(0, floorO * 9f);
		mWindow.rLineTo(-floorO * 4f, 0);
		mWindow.rLineTo(0, floorO);
		mWindow.rLineTo(floorO * 4f, 0);
		mWindow.rLineTo(0, floorO * 5f);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(0, -floorO * 3f);
		mWindow.rLineTo(floorO * 4f, floorO * 2f);
		mWindow.rLineTo(0, -floorO);
		mWindow.rLineTo(-floorO * 4f, -floorO * 2f);
		mWindow.rLineTo(0, -floorO);
		mWindow.rLineTo(floorO * 4f, 0);
		mWindow.rLineTo(0, -floorO);
		mWindow.rLineTo(-floorO * 4f, 0);
		mWindow.rLineTo(0, -floorO * 3f);
		mWindow.rLineTo(floorO * 4f, 0);
		mWindow.rLineTo(0, -floorO);
		mWindow.rLineTo(-floorO * 4f, 0);
		mWindow.rLineTo(0, -floorO * 5f);
		// Th
		mWindow.moveTo(buildR + (floorO * 64f), tmpT);
		mWindow.rLineTo(floorO * 9f, 0);
		mWindow.rLineTo(0, floorO * 9f);
		mWindow.rLineTo(floorO * 4f, 0);
		mWindow.rLineTo(0, floorO * 6f);
		mWindow.rLineTo(-floorO, 0);
		mWindow.rLineTo(0, -floorO * 5f);
		mWindow.rLineTo(-floorO * 3f, 0);
		mWindow.rLineTo(0, floorO * 5f);
		mWindow.rLineTo(-floorO, 0);
		mWindow.rLineTo(0, -floorO * 14f);
		mWindow.rLineTo(-floorO * 3f, 0);
		mWindow.rLineTo(0, floorO * 14f);
		mWindow.rLineTo(-floorO, 0);
		mWindow.rLineTo(0, -floorO * 14f);
		mWindow.rLineTo(-floorO * 4f, 0);
		// E
		mWindow.moveTo(buildR + (floorO * 78f), tmpT);
		mWindow.rLineTo(floorO * 5f, 0);
		mWindow.rLineTo(0, floorO);
		mWindow.rLineTo(-floorO * 4f, 0);
		mWindow.rLineTo(0, floorO * 8f);
		mWindow.rLineTo(floorO * 3.5f, 0);
		mWindow.rLineTo(0, floorO);
		mWindow.rLineTo(-floorO * 3.5f, 0);
		mWindow.rLineTo(0, floorO * 4f);
		mWindow.rLineTo(floorO * 5f, 0);
		mWindow.rLineTo(0, floorO);
		mWindow.rLineTo(-floorO * 6f, 0);
		// W
		mWindow.moveTo(buildR + (floorO * 84f), tmpT);
		mWindow.rLineTo(floorO * 3f, floorO * 15f);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(-floorO * 3f, -floorO * 15f);
		mWindow.moveTo(buildR + (floorO * 88.5f), tmpT + (floorO * 7f));
		mWindow.rLineTo(floorO * 1.5f, floorO * 8f);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(-floorO * 1.5f, -floorO * 8f);
		// A
		mWindow.moveTo(buildR + (floorO * 95f), tmpT);
		mWindow.rLineTo(-floorO * 3f, floorO * 15f);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(floorO * 3f, -floorO * 15f);
		mWindow.rLineTo(floorO * 3f, floorO * 15f);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(-floorO * 3f, -floorO * 15f);
		// LL
		mWindow.moveTo(buildR + (floorO * 100f), tmpT);
		mWindow.rLineTo(0, floorO * 15f);
		mWindow.rLineTo(floorO * 10f, 0);
		mWindow.rLineTo(0, -floorO);
		mWindow.rLineTo(-floorO * 4f, 0);
		mWindow.rLineTo(0, -floorO * 14f);
		mWindow.rLineTo(-floorO, 0);
		mWindow.rLineTo(0, floorO * 14f);
		mWindow.rLineTo(-floorO * 4f, 0);
		mWindow.rLineTo(0, -floorO * 14f);
		// LE
		mWindow.moveTo(buildR + (floorO * 115f), tmpT);
		mWindow.rLineTo(0, floorO * 15f);
		mWindow.rLineTo(floorO * 11f, 0);
		mWindow.rLineTo(0, -floorO);
		mWindow.rLineTo(-floorO * 5f, 0);
		mWindow.rLineTo(0, -floorO * 4f);
		mWindow.rLineTo(floorO * 3.5f, 0);
		mWindow.rLineTo(0, -floorO);
		mWindow.rLineTo(-floorO * 3.5f, 0);
		mWindow.rLineTo(0, -floorO * 8f);
		mWindow.rLineTo(floorO * 4f, 0);
		mWindow.rLineTo(0, -floorO);
		mWindow.rLineTo(-floorO * 5f, 0);
		mWindow.rLineTo(0, floorO * 14f);
		mWindow.rLineTo(-floorO * 4f, 0);
		mWindow.rLineTo(0, -floorO * 14f);
		// G
		mWindow.moveTo(buildR + (floorO * 132f), tmpT);
		mWindow.rLineTo(0, floorO * 14f);
		mWindow.rLineTo(-floorO * 5f, 0);
		mWindow.rLineTo(0, floorO);
		mWindow.rLineTo(floorO * 6f, 0);
		mWindow.rLineTo(0, -floorO * 15f);
		// A
		mWindow.moveTo(buildR + (floorO * 137f), tmpT);
		mWindow.rLineTo(-floorO * 3f, floorO * 15f);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(floorO * 3f, -floorO * 15f);
		mWindow.rLineTo(floorO * 3f, floorO * 15f);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(-floorO * 3f, -floorO * 15f);
		// C
		mWindow.moveTo(buildR + (floorO * 143f), tmpT);
		mWindow.rLineTo(0, floorO * 15f);
		mWindow.rLineTo(floorO * 6f, 0);
		mWindow.rLineTo(0, -floorO);
		mWindow.rLineTo(-floorO * 5f, 0);
		mWindow.rLineTo(0, -floorO * 13f);
		mWindow.rLineTo(floorO * 4f, 0);
		mWindow.rLineTo(0, -floorO);
		// Y
		mWindow.moveTo(buildR + (floorO * 149f), tmpT);
		mWindow.rLineTo(floorO * 4f, floorO * 9f);
		mWindow.rLineTo(0, floorO * 6f);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(0, -floorO * 6f);
		mWindow.rLineTo(-floorO * 4f, -floorO * 9f);

		mSketch.close();
		mWindow.close();

		canvas.drawPath(mSketch, mTitlePaint);
		canvas.drawPath(mWindow, mWindowPaint);

		mSketch.reset();
		mWindow.reset();

		mWindow.moveTo(buildR - floorO, tmpT + (floorO * 9f));
		mWindow.rLineTo(floorO * 4f, 0);
		mWindow.rLineTo(0, floorO);
		mWindow.rLineTo(-floorO * 4f, 0);

		mWindow.moveTo(buildR + (floorO * 44f), tmpT + (floorO * 10f));
		mWindow.rLineTo(floorO * 3f, floorO * 5f);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(-floorO * 3f, -floorO * 5f);

		mWindow.moveTo(buildR + (floorO * 87f), tmpT + (floorO * 15f));
		mWindow.rLineTo(floorO * 1.5f, -floorO * 8f);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(-floorO * 1.5f, floorO * 8f);
		mWindow.moveTo(buildR + (floorO * 90f), tmpT + (floorO * 15f));
		mWindow.rLineTo(floorO * 3f, -floorO * 15f);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(-floorO * 3f, floorO * 15f);

		mWindow.moveTo(buildR + (floorO * 94f), tmpT + (floorO * 9f));
		mWindow.rLineTo(floorO * 4f, 0);
		mWindow.rLineTo(0, floorO);
		mWindow.rLineTo(-floorO * 4f, 0);

		mWindow.addCircle(buildR + (floorO * 129f), tmpT + (floorO * 4f), floorO * 4f, Direction.CW);
		mSketch.addCircle(buildR + (floorO * 129f), tmpT + (floorO * 4f), floorO * 3f, Direction.CW);

		mWindow.moveTo(buildR + (floorO * 136f), tmpT + (floorO * 9f));
		mWindow.rLineTo(floorO * 4f, 0);
		mWindow.rLineTo(0, floorO);
		mWindow.rLineTo(-floorO * 4f, 0);

		mWindow.moveTo(buildR + (floorO * 153f), tmpT + (floorO * 9f));
		mWindow.rLineTo(floorO * 4f, -floorO * 9f);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(-floorO * 4f, floorO * 9f);

		mSketch.close();
		mWindow.close();

		canvas.drawPath(mWindow, mWindowPaint);
		canvas.drawPath(mSketch, mTitlePaint);

	}

	private void drawCrowd(Canvas canvas, float right) {
		float floorO = Util.PIXEL * Util.MULTI;
		float buildR = right;
		float tmpT = floorO * 91f;

		mSketch.reset();

		mSketch.addRect(buildR, tmpT + (floorO * 4f), buildR - floorO, tmpT + (floorO * 9f), Direction.CW);

		for (int i = 0; i < 55; i++) {
			mSketch.addCircle(buildR - (floorO * 4.5f) - (floorO * 3f * i), tmpT + (floorO * 2.5f), floorO * 1.5f,
					Direction.CW);
			mSketch.addRect(buildR - (floorO * 3f) - (floorO * 3f * i), tmpT + (floorO * 4f), buildR - (floorO * 6f)
					- (floorO * 3f * i), tmpT + (floorO * 7f), Direction.CW);
			mSketch.addRect(buildR - (floorO * 4f) - (floorO * 3f * i), tmpT + (floorO * 7f), buildR - (floorO * 5f)
					- (floorO * 3f * i), tmpT + (floorO * 9f), Direction.CW);
		}
		mSketch.addRect(buildR -= (floorO * 7f), tmpT, buildR -= floorO, tmpT + floorO, Direction.CW);
		mSketch.addRect(buildR -= (floorO * 8f), tmpT, buildR -= floorO, tmpT + floorO, Direction.CW);
		mSketch.moveTo(buildR -= floorO * 2f, tmpT);
		mSketch.rLineTo(-floorO / 2f, -floorO);
		mSketch.rLineTo(-floorO / 2f, floorO);
		mSketch.rLineTo(floorO / 2f, floorO);
		mSketch.moveTo(buildR -= floorO * 21f, tmpT);
		mSketch.rLineTo(-floorO / 2f, -floorO);
		mSketch.rLineTo(-floorO / 2f, floorO);
		mSketch.rLineTo(floorO / 2f, floorO);
		mSketch.addRect(buildR -= (floorO * 6f), tmpT, buildR -= floorO, tmpT + floorO, Direction.CW);
		mSketch.addRect(buildR -= (floorO * 14f), tmpT, buildR -= floorO, tmpT + floorO, Direction.CW);
		mSketch.moveTo(buildR -= floorO * 2f, tmpT);
		mSketch.rLineTo(-floorO / 2f, -floorO);
		mSketch.rLineTo(-floorO / 2f, floorO);
		mSketch.rLineTo(floorO / 2f, floorO);
		mSketch.moveTo(buildR -= floorO * 6f, tmpT);
		mSketch.rLineTo(-floorO / 2f, -floorO);
		mSketch.rLineTo(-floorO / 2f, floorO);
		mSketch.rLineTo(floorO / 2f, floorO);
		mSketch.moveTo(buildR -= floorO * 9f, tmpT);
		mSketch.rLineTo(-floorO / 2f, -floorO);
		mSketch.rLineTo(-floorO / 2f, floorO);
		mSketch.rLineTo(floorO / 2f, floorO);
		mSketch.moveTo(buildR -= floorO * 3f, tmpT);
		mSketch.rLineTo(-floorO / 2f, -floorO);
		mSketch.rLineTo(-floorO / 2f, floorO);
		mSketch.rLineTo(floorO / 2f, floorO);
		mSketch.moveTo(buildR -= floorO * 3f, tmpT);
		mSketch.rLineTo(-floorO / 2f, -floorO);
		mSketch.rLineTo(-floorO / 2f, floorO);
		mSketch.rLineTo(floorO / 2f, floorO);
		mSketch.addRect(buildR -= (floorO * 3f), tmpT, buildR -= floorO, tmpT + floorO, Direction.CW);
		mSketch.addRect(buildR -= (floorO * 2f), tmpT, buildR -= floorO, tmpT + floorO, Direction.CW);
		mSketch.moveTo(buildR -= floorO * 8f, tmpT);
		mSketch.rLineTo(-floorO / 2f, -floorO);
		mSketch.rLineTo(-floorO / 2f, floorO);
		mSketch.rLineTo(floorO / 2f, floorO);
		mSketch.moveTo(buildR -= floorO * 3f, tmpT);
		mSketch.rLineTo(-floorO / 2f, -floorO);
		mSketch.rLineTo(-floorO / 2f, floorO);
		mSketch.rLineTo(floorO / 2f, floorO);
		mSketch.moveTo(buildR -= floorO * 3f, tmpT);
		mSketch.rLineTo(-floorO / 2f, -floorO);
		mSketch.rLineTo(-floorO / 2f, floorO);
		mSketch.rLineTo(floorO / 2f, floorO);
		mSketch.addRect(buildR -= (floorO * 6f), tmpT, buildR -= floorO, tmpT + floorO, Direction.CW);
		mSketch.addRect(buildR -= (floorO * 2f), tmpT, buildR -= floorO, tmpT + floorO, Direction.CW);
		mSketch.addRect(buildR -= (floorO * 2f), tmpT, buildR -= floorO, tmpT + floorO, Direction.CW);
		mSketch.moveTo(buildR -= floorO * 5f, tmpT);
		mSketch.rLineTo(-floorO / 2f, -floorO);
		mSketch.rLineTo(-floorO / 2f, floorO);
		mSketch.rLineTo(floorO / 2f, floorO);
		mSketch.addRect(buildR -= (floorO * 3f), tmpT, buildR -= floorO, tmpT + floorO, Direction.CW);
		mSketch.addRect(buildR -= (floorO * 8f), tmpT, buildR -= floorO, tmpT + floorO, Direction.CW);
		mSketch.addRect(buildR -= (floorO * 2f), tmpT, buildR -= floorO, tmpT + floorO, Direction.CW);
		mSketch.moveTo(buildR -= floorO * 2f, tmpT);
		mSketch.rLineTo(-floorO / 2f, -floorO);
		mSketch.rLineTo(-floorO / 2f, floorO);
		mSketch.rLineTo(floorO / 2f, floorO);
		mSketch.moveTo(buildR -= floorO * 18f, tmpT);
		mSketch.rLineTo(-floorO / 2f, -floorO);
		mSketch.rLineTo(-floorO / 2f, floorO);
		mSketch.rLineTo(floorO / 2f, floorO);
		mSketch.addRect(buildR -= (floorO * 3f), tmpT, buildR -= floorO, tmpT + floorO, Direction.CW);
		mSketch.addRect(buildR -= (floorO * 2f), tmpT, buildR -= floorO, tmpT + floorO, Direction.CW);

		mSketch.close();

		canvas.drawPath(mSketch, mTitlePaint);
	}

	private void drawMemHallMain(Canvas canvas, float right) {
		float floorO = Util.PIXEL * Util.MULTI;
		float buildR = right;
		float tmpT = floorO * 100f;
		float tmpC = buildR - (floorO * 51.5f);

		mSketch.reset();
		mWindow.reset();

		mSketch.moveTo(buildR, tmpT);
		mSketch.rLineTo(-floorO, -floorO * 4.5f);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(-floorO * 11f, 0);
		mSketch.rLineTo(-floorO, -floorO * 2f);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(-floorO * 9f, 0);
		mSketch.rLineTo(-floorO * 0.75f, -floorO * 1.5f);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(-floorO * 9.5f, 0);
		mSketch.rLineTo(-floorO * 3.25f, -floorO * 12.5f);
		mSketch.rLineTo(-floorO / 2f, 0);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(floorO / 2f, 0);
		mSketch.rLineTo(floorO / 2f, -floorO * 1.5f);
		mSketch.rLineTo(-floorO * 1.5f, 0);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(-floorO * 1.5f, 0);
		mSketch.rLineTo(-floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(0, -floorO);
		mSketch.rLineTo(-floorO / 2f, 0);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(floorO * 1.5f, -floorO);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(floorO * 2f, -floorO);
		mSketch.rQuadTo(-floorO * 3.5f, floorO / 2f, -floorO * 5.5f, -floorO * 1.5f);
		mSketch.rLineTo(floorO * 1.5f, -floorO);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(floorO * 2f, -floorO * 1.5f);
		mSketch.rQuadTo(-floorO * 6.5f, floorO / 2f, -floorO * 13.5f, -floorO * 7f);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(-floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(floorO / 4f, -floorO);
		mSketch.rLineTo(-floorO / 4f, -floorO / 2f);
		mSketch.rLineTo(-floorO * 2f, 0);
		mSketch.rLineTo(-floorO / 4f, floorO / 2f);
		mSketch.rLineTo(floorO / 4f, floorO);
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(0, floorO * 40f);
		mSketch.rLineTo(-floorO * 50.5f, 0);
		// Backward
		mSketch.rLineTo(floorO, -floorO * 4.5f);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(floorO * 11f, 0);
		mSketch.rLineTo(floorO, -floorO * 2f);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(floorO * 9f, 0);
		mSketch.rLineTo(floorO * 0.75f, -floorO * 1.5f);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(floorO * 9.5f, 0);
		mSketch.rLineTo(floorO * 3.25f, -floorO * 12.5f);
		mSketch.rLineTo(floorO / 2f, 0);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(-floorO / 2f, 0);
		mSketch.rLineTo(-floorO / 2f, -floorO * 1.5f);
		mSketch.rLineTo(floorO * 1.5f, 0);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(floorO * 1.5f, 0);
		mSketch.rLineTo(floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(-floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(0, -floorO);
		mSketch.rLineTo(floorO / 2f, 0);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(-floorO * 1.5f, -floorO);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(-floorO * 2f, -floorO);
		mSketch.rQuadTo(floorO * 3.5f, floorO / 2f, floorO * 5.5f, -floorO * 1.5f);
		mSketch.rLineTo(-floorO * 1.5f, -floorO);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(-floorO * 2f, -floorO * 1.5f);
		mSketch.rQuadTo(floorO * 6.5f, floorO / 2f, floorO * 13.5f, -floorO * 7f);
		mSketch.rLineTo(0, floorO * 39.5f);

		// Fences 1
		mSketch.moveTo(tmpC + (floorO * 39f), tmpT - (floorO * 6f));
		mSketch.rLineTo(floorO, 0);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(-floorO * 0.75f, 0);
		mSketch.moveTo(tmpC - (floorO * 39f), tmpT - (floorO * 6f));
		mSketch.rLineTo(-floorO, 0);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(floorO * 0.75f, 0);
		mSketch.addRect(tmpC + (floorO * 40f), tmpT - (floorO * 6.5f), tmpC + (floorO * 40.5f), tmpT - (floorO * 5f),
				Direction.CW);
		mSketch.addRect(tmpC - (floorO * 40f), tmpT - (floorO * 6.5f), tmpC - (floorO * 40.5f), tmpT - (floorO * 5f),
				Direction.CW);
		for (int i = 0; i < 5; i++) {
			mSketch.addRect(tmpC + (floorO * 40.5f) + (floorO * 2f * i), tmpT - (floorO * 6f), tmpC + (floorO * 42f)
					+ (floorO * 2f * i), tmpT - (floorO * 5.5f), Direction.CW);
			mSketch.addRect(tmpC - (floorO * 40.5f) - (floorO * 2f * i), tmpT - (floorO * 6f), tmpC - (floorO * 42f)
					- (floorO * 2f * i), tmpT - (floorO * 5.5f), Direction.CW);
			mSketch.addRect(tmpC + (floorO * 42f) + (floorO * 2f * i), tmpT - (floorO * 6.5f), tmpC + (floorO * 42.5f)
					+ (floorO * 2f * i), tmpT - (floorO * 5f), Direction.CW);
			mSketch.addRect(tmpC - (floorO * 42f) - (floorO * 2f * i), tmpT - (floorO * 6.5f), tmpC - (floorO * 42.5f)
					- (floorO * 2f * i), tmpT - (floorO * 5f), Direction.CW);
		}

		// Fences 2
		mSketch.moveTo(tmpC + (floorO * 29f), tmpT - (floorO * 8.5f));
		mSketch.rLineTo(floorO, 0);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(-floorO * 0.75f, 0);
		mSketch.moveTo(tmpC - (floorO * 29f), tmpT - (floorO * 8.5f));
		mSketch.rLineTo(-floorO, 0);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(floorO * 0.75f, 0);
		mSketch.addRect(tmpC + (floorO * 30f), tmpT - (floorO * 9f), tmpC + (floorO * 30.5f), tmpT - (floorO * 7.5f),
				Direction.CW);
		mSketch.addRect(tmpC - (floorO * 30f), tmpT - (floorO * 9f), tmpC - (floorO * 30.5f), tmpT - (floorO * 7.5f),
				Direction.CW);
		for (int i = 0; i < 4; i++) {
			mSketch.addRect(tmpC + (floorO * 30.5f) + (floorO * 2f * i), tmpT - (floorO * 8.5f), tmpC + (floorO * 32f)
					+ (floorO * 2f * i), tmpT - (floorO * 8f), Direction.CW);
			mSketch.addRect(tmpC - (floorO * 30.5f) - (floorO * 2f * i), tmpT - (floorO * 8.5f), tmpC - (floorO * 32f)
					- (floorO * 2f * i), tmpT - (floorO * 8f), Direction.CW);
			mSketch.addRect(tmpC + (floorO * 32f) + (floorO * 2f * i), tmpT - (floorO * 9f), tmpC + (floorO * 32.5f)
					+ (floorO * 2f * i), tmpT - (floorO * 7.5f), Direction.CW);
			mSketch.addRect(tmpC - (floorO * 32f) - (floorO * 2f * i), tmpT - (floorO * 9f), tmpC - (floorO * 32.5f)
					- (floorO * 2f * i), tmpT - (floorO * 7.5f), Direction.CW);
		}

		// Fences 3
		mSketch.moveTo(tmpC + (floorO * 19f), tmpT - (floorO * 10.5f));
		mSketch.rLineTo(floorO * 1.25f, 0);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(-floorO * 1.125f, 0);
		mSketch.moveTo(tmpC - (floorO * 19f), tmpT - (floorO * 10.5f));
		mSketch.rLineTo(-floorO * 1.25f, 0);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(floorO * 1.125f, 0);
		mSketch.addRect(tmpC + (floorO * 20.25f), tmpT - (floorO * 11f), tmpC + (floorO * 20.75f), tmpT
				- (floorO * 9.5f), Direction.CW);
		mSketch.addRect(tmpC - (floorO * 20.25f), tmpT - (floorO * 11f), tmpC - (floorO * 20.75f), tmpT
				- (floorO * 9.5f), Direction.CW);
		for (int i = 0; i < 4; i++) {
			mSketch.addRect(tmpC + (floorO * 20.75f) + (floorO * 2f * i), tmpT - (floorO * 10.5f), tmpC
					+ (floorO * 22.25f) + (floorO * 2f * i), tmpT - (floorO * 10f), Direction.CW);
			mSketch.addRect(tmpC - (floorO * 20.75f) - (floorO * 2f * i), tmpT - (floorO * 10.5f), tmpC
					- (floorO * 22.25f) - (floorO * 2f * i), tmpT - (floorO * 10f), Direction.CW);
			mSketch.addRect(tmpC + (floorO * 22.25f) + (floorO * 2f * i), tmpT - (floorO * 11f), tmpC
					+ (floorO * 22.75f) + (floorO * 2f * i), tmpT - (floorO * 9.5f), Direction.CW);
			mSketch.addRect(tmpC - (floorO * 22.25f) - (floorO * 2f * i), tmpT - (floorO * 11f), tmpC
					- (floorO * 22.75f) - (floorO * 2f * i), tmpT - (floorO * 9.5f), Direction.CW);
		}

		// Stair
		for (int i = 0; i < 2; i++) {
			mWindow.moveTo(buildR - (floorO * 34.5f) - (floorO * 9.5f * i), tmpT);
			mWindow.rLineTo(-floorO * 1.5f, -floorO * 9f);
			mWindow.rLineTo(-floorO / 2f, 0);
			mWindow.rLineTo(floorO * 1.5f, floorO * 9f);
		}
		for (int i = 0; i < 2; i++) {
			mWindow.moveTo(buildR - (floorO * 58.5f) - (floorO * 9.5f * i), tmpT);
			mWindow.rLineTo(floorO * 1.5f, -floorO * 9f);
			mWindow.rLineTo(-floorO / 2f, 0);
			mWindow.rLineTo(-floorO * 1.5f, floorO * 9f);
		}

		// Door
		mWindow.moveTo(tmpC, tmpT -= floorO * 9f);
		mWindow.rLineTo(floorO * 4, 0);
		mWindow.rLineTo(0, -floorO * 4.5f);
		mWindow.rCubicTo(0, -floorO * 5.375f, -floorO * 8f, -floorO * 5.375f, -floorO * 8f, 0);
		mWindow.rLineTo(0, floorO * 4.5f);

		mWindow.moveTo(tmpC + (floorO * 6f), tmpT);
		mWindow.rLineTo(0, -floorO * 4.5f);
		mWindow.rCubicTo(-floorO / 4f, -floorO * 8f, -floorO * 11.75f, -floorO * 8f, -floorO * 12f, 0);
		mWindow.rLineTo(0, floorO * 4.5f);
		mWindow.rLineTo(floorO / 2f, 0);
		mWindow.rLineTo(0, -floorO * 4.5f);
		mWindow.rCubicTo(floorO / 4f, -floorO * 7.375f, floorO * 10.75f, -floorO * 7.25f, floorO * 11f, 0);
		mWindow.rLineTo(0, floorO * 4.5f);

		mWindow.moveTo(tmpC + (floorO * 8f), tmpT);
		mWindow.rLineTo(floorO / 2f, 0);
		mWindow.rLineTo(0, -floorO * 14.5f);
		mWindow.rLineTo(floorO * 5f, 0);
		mWindow.rLineTo(0, -floorO / 2f);
		mWindow.rLineTo(-floorO * 5.5f, 0);
		mWindow.moveTo(tmpC - (floorO * 8f), tmpT);
		mWindow.rLineTo(-floorO / 2f, 0);
		mWindow.rLineTo(0, -floorO * 14.5f);
		mWindow.rLineTo(-floorO * 5f, 0);
		mWindow.rLineTo(0, -floorO / 2f);
		mWindow.rLineTo(floorO * 5.5f, 0);

		mWindow.moveTo(tmpC + (floorO * 1.5f), tmpT -= floorO * 14.5f);
		mWindow.rLineTo(0, -floorO * 4f);
		mWindow.rLineTo(-floorO * 3f, 0);
		mWindow.rLineTo(0, floorO * 4f);
		mWindow.rLineTo(floorO * 2.5f, 0);
		mWindow.rLineTo(0, -floorO / 2f);
		mWindow.rLineTo(-floorO * 2f, 0);
		mWindow.rLineTo(0, -floorO * 3f);
		mWindow.rLineTo(floorO * 2f, 0);
		mWindow.rLineTo(0, floorO * 3.5f);

		tmpT -= floorO * 2f;
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < 2; i++) {
				mWindow.moveTo(((tmpC + (floorO * 2f) + (floorO * i)) - (floorO * 1.5f * j)), tmpT - (floorO * 4f * j));
				mWindow.rLineTo(floorO / 2f, 0);
				mWindow.rLineTo(0, -floorO / 2f);
				mWindow.moveTo(((tmpC - (floorO * 2f) - (floorO * i)) + (floorO * 1.5f * j)), tmpT - (floorO * 4f * j));
				mWindow.rLineTo(-floorO / 2f, 0);
				mWindow.rLineTo(0, -floorO / 2f);
			}
		}

		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < 3; i++) {
				if ((j == 0) && (i == 2)) {
					break;
				}
				mWindow.moveTo(((tmpC + (floorO * 4f) + (floorO * 1.5f * i)) - (floorO * 1.5f * j)), tmpT
						- (floorO * 4f * j));
				mWindow.rLineTo(floorO, 0);
				mWindow.rLineTo(0, -floorO * 0.75f);
				mWindow.moveTo(((tmpC - (floorO * 4f) - (floorO * 1.5f * i)) + (floorO * 1.5f * j)), tmpT
						- (floorO * 4f * j));
				mWindow.rLineTo(-floorO, 0);
				mWindow.rLineTo(0, -floorO * 0.75f);
			}
		}

		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < 3; i++) {
				if ((j == 1) && (i == 2)) {
					break;
				}
				mWindow.moveTo(tmpC + (floorO * 7f) + (floorO * 2f * i), tmpT - (floorO * 4f * j));
				mWindow.rLineTo(floorO * 1.5f, 0);
				mWindow.rLineTo(0, -floorO);
				mWindow.moveTo(tmpC - (floorO * 7f) - (floorO * 2f * i), tmpT - (floorO * 4f * j));
				mWindow.rLineTo(-floorO * 1.5f, 0);
				mWindow.rLineTo(0, -floorO);
			}
		}

		mWindow.addRect(tmpC - floorO, tmpT - (floorO * 15f), tmpC + floorO, tmpT - (floorO * 14.5f), Direction.CW);

		mSketch.close();
		mWindow.close();

		canvas.drawPath(mSketch, mTitlePaint);
		canvas.drawPath(mWindow, mWindowPaint);

		mWindow.reset();

		tmpT = floorO * 100f;
		for (int j = 0; j < 3; j++) {
			if (j == 0) {
				for (int i = 0; i < 2; i++) {
					mWindow.addRect(tmpC + (floorO * 7f), tmpT - floorO - (floorO * i), tmpC + (floorO * 16.5f), tmpT
							- (floorO * 1.5f) - (floorO * i), Direction.CW);
					mWindow.addRect(tmpC - (floorO * 7f), tmpT - floorO - (floorO * i), tmpC - (floorO * 16.5f), tmpT
							- (floorO * 1.5f) - (floorO * i), Direction.CW);
				}
			} else {
				for (int i = 0; i < 3; i++) {
					for (int k = 0; k < 2; k++) {
						mWindow.addRect((tmpC + (floorO * 7f)) - ((floorO / 2f) * j), tmpT - (floorO * 3f)
								- (floorO * i) - (floorO * 3f * (j - 1)), (tmpC + (floorO * 16.5f))
								- ((floorO / 2f) * j), tmpT - (floorO * 3.5f) - (floorO * i) - (floorO * 3f * (j - 1)),
								Direction.CW);
						mWindow.addRect((tmpC - (floorO * 7f)) + ((floorO / 2f) * j), tmpT - (floorO * 3f)
								- (floorO * i) - (floorO * 3f * (j - 1)), (tmpC - (floorO * 16.5f))
								+ ((floorO / 2f) * j), tmpT - (floorO * 3.5f) - (floorO * i) - (floorO * 3f * (j - 1)),
								Direction.CW);
					}
				}
			}
		}

		mWindow.close();

		canvas.drawPath(mWindow, mWindowPaint);
	}

	private void drawMemHallCenter(Canvas canvas, float right) {
		float floorO = Util.PIXEL * Util.MULTI;
		float buildR = right;
		float tmpT = floorO * 100f;

		mSketch.reset();
		mWindow.reset();

		mSketch.moveTo(buildR - (floorO * 2f), tmpT);
		mSketch.rLineTo(-floorO / 2f, -floorO * 4f);
		mSketch.rLineTo(-floorO * 7.5f, 0);
		mSketch.rLineTo(0, -floorO * 6f);
		mSketch.rLineTo(floorO * 3.5f, 0);
		mSketch.rLineTo(0, floorO * 6f);
		mSketch.rLineTo(floorO, 0);
		mSketch.rLineTo(0, -floorO * 8.5f);
		mSketch.rLineTo(floorO * 5f, -floorO);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rQuadTo(-floorO * 4f, 0, -floorO * 5f, -floorO * 2.5f);
		mSketch.rLineTo(floorO * 2.5f, -floorO / 2f);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rQuadTo(-floorO * 14f, 0, -floorO * 19f, -floorO * 11f);
		mSketch.rLineTo(0, -floorO);
		mSketch.rLineTo(-floorO, 0);
		mSketch.rLineTo(0, floorO);
		mSketch.rLineTo(-floorO * 44f, 0);
		mSketch.rLineTo(0, -floorO);
		mSketch.rLineTo(-floorO, 0);
		mSketch.rLineTo(0, floorO);
		mSketch.rQuadTo(-floorO * 7.5f, floorO * 11.5f, -floorO * 19f, floorO * 11f);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(floorO * 2.5f, floorO / 2f);
		mSketch.rQuadTo(-floorO * 1.5f, floorO * 2.5f, -floorO * 5f, floorO * 2.5f);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(floorO * 5f, floorO);
		mSketch.rLineTo(0, floorO * 8.5f);
		mSketch.rLineTo(floorO, 0);
		mSketch.rLineTo(0, -floorO * 6f);
		mSketch.rLineTo(floorO * 3.5f, 0);
		mSketch.rLineTo(0, floorO * 6f);
		mSketch.rLineTo(-floorO * 7.5f, 0);
		mSketch.rLineTo(-floorO / 2f, floorO * 4f);

		mWindow.moveTo(buildR - (floorO * 17f), tmpT);
		mWindow.rLineTo(-floorO / 2f, -floorO * 4f);
		mWindow.rLineTo(-floorO / 2f, 0);
		mWindow.rLineTo(floorO / 2f, floorO * 4f);

		mWindow.moveTo(buildR - (floorO * 70f), tmpT);
		mWindow.rLineTo(floorO / 2f, -floorO * 4f);
		mWindow.rLineTo(floorO / 2f, 0);
		mWindow.rLineTo(-floorO / 2f, floorO * 4f);

		mWindow.moveTo(buildR - (floorO * 35.5f), tmpT);
		mWindow.rLineTo(-floorO, -floorO * 4f);
		mWindow.rLineTo(-floorO / 2f, 0);
		mWindow.rLineTo(floorO, floorO * 4f);

		mWindow.moveTo(buildR - (floorO * 51.5f), tmpT);
		mWindow.rLineTo(floorO, -floorO * 4f);
		mWindow.rLineTo(floorO / 2f, 0);
		mWindow.rLineTo(-floorO, floorO * 4f);

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				mWindow.moveTo(buildR - (floorO * 11f) - (floorO * 5f * j) - (floorO * 56f * i), tmpT - (floorO * 4f));
				mWindow.rLineTo(0, -floorO * 6f);
				mWindow.rLineTo(-floorO / 2f, 0);
				mWindow.rLineTo(0, floorO * 4.5f);
				mWindow.rLineTo(-floorO * 3f, 0);
				mWindow.rLineTo(0, -floorO * 4.5f);
				mWindow.rLineTo(-floorO / 2f, 0);
				mWindow.rLineTo(0, floorO * 6f);
				mWindow.rLineTo(floorO / 2f, 0);
				mWindow.rLineTo(0, -floorO);
				mWindow.rLineTo(floorO * 3f, 0);
				mWindow.rLineTo(0, floorO);
			}
		}

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 3; j++) {
				mWindow.moveTo(buildR - (floorO * 22.5f) - (floorO * 5.5f * j) - (floorO * 26.5f * i), tmpT
						- (floorO * 4f));
				mWindow.rLineTo(0, -floorO * 6f);
				mWindow.rLineTo(-floorO / 2f, 0);
				mWindow.rLineTo(0, floorO * 4.5f);
				mWindow.rLineTo(-floorO * 3.5f, 0);
				mWindow.rLineTo(0, -floorO * 4.5f);
				mWindow.rLineTo(-floorO / 2f, 0);
				mWindow.rLineTo(0, floorO * 6f);
				mWindow.rLineTo(floorO / 2f, 0);
				mWindow.rLineTo(0, -floorO);
				mWindow.rLineTo(floorO * 3.5f, 0);
				mWindow.rLineTo(0, floorO);

				mWindow.addRect(buildR - (floorO * 24f) - (floorO * 5.5f * j) - (floorO * 26f * i), tmpT
						- (floorO * 9.5f), buildR - (floorO * 25.5f) - (floorO * 5.5f * j) - (floorO * 26f * i), tmpT
						- (floorO * 8f), Direction.CW);
			}
		}

		mWindow.addRect(buildR - (floorO * 39f), tmpT - (floorO * 9.5f), buildR - (floorO * 48f), tmpT - (floorO * 4f),
				Direction.CW);
		mWindow.moveTo(buildR - (floorO * 42.5f), tmpT - (floorO * 10.5f));
		mWindow.rLineTo(0, -floorO * 2.5f);
		mWindow.rLineTo(-floorO * 2f, 0);
		mWindow.rLineTo(0, floorO * 2.5f);
		mWindow.rLineTo(floorO * 1.5f, 0);
		mWindow.rLineTo(0, -floorO / 2f);
		mWindow.rLineTo(-floorO, 0);
		mWindow.rLineTo(0, -floorO * 1.5f);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(0, floorO * 2f);

		mWindow.moveTo(buildR -= (floorO * 21f), tmpT -= (floorO * 4f));
		mWindow.rLineTo(0, -floorO * 8f);
		mWindow.rLineTo(floorO * 5f, -floorO);
		mWindow.rLineTo(0, -floorO * 1.5f);
		mWindow.rQuadTo(-floorO * 4f, 0, -floorO * 5f, -floorO * 2.5f);
		mWindow.rLineTo(-floorO * 45f, 0);
		mWindow.rQuadTo(-floorO * 1.5f, floorO * 2.75f, -floorO * 5f, floorO * 2.5f);
		mWindow.rLineTo(0, floorO * 1.5f);
		mWindow.rLineTo(floorO * 5f, floorO);
		mWindow.rLineTo(0, floorO * 8f);
		mWindow.rLineTo(floorO / 2f, 0);
		mWindow.rLineTo(0, -floorO * 8.5f);
		mWindow.rLineTo(-floorO * 5f, -floorO);
		mWindow.rLineTo(0, -floorO / 2f);
		mWindow.rQuadTo(floorO * 4f, 0, floorO * 5f, -floorO * 2.5f);
		mWindow.rLineTo(floorO * 44f, 0);
		mWindow.rQuadTo(floorO * 1.5f, floorO * 2.75f, floorO * 5f, floorO * 2.5f);
		mWindow.rLineTo(0, floorO / 2f);
		mWindow.rLineTo(-floorO * 5f, floorO);
		mWindow.rLineTo(0, floorO * 8.5f);

		mSketch.close();
		mWindow.close();

		canvas.drawPath(mSketch, mTitlePaint);
		canvas.drawPath(mWindow, mWindowPaint);
	}

	private void drawMemHallDoor(Canvas canvas, float right) {
		float floorO = Util.PIXEL * Util.MULTI;
		float buildR = right;
		float tmpT = floorO * 100f;

		mSketch.reset();
		mWindow.reset();

		// Foots
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				if (i == 2) {
					mSketch.moveTo(buildR - (floorO * 36f * (i - 1)) - (floorO * 24.5f * j), tmpT);
					mWindow.moveTo(buildR - (floorO * 37.5f * (i - 1)) - (floorO * 24.5f * j), tmpT);
				} else {
					mSketch.moveTo(buildR - floorO - (floorO * 15.5f * i) - (floorO * 79f * j), tmpT);
					mWindow.moveTo(buildR - (floorO * 2.5f) - (floorO * 15.5f * i) - (floorO * 79f * j), tmpT);
				}
				mSketch.rLineTo(0, -floorO);
				mSketch.rLineTo(-floorO / 2f, 0);
				mSketch.rLineTo(0, -floorO * 4f);
				mSketch.rLineTo(-floorO / 2f, 0);
				mSketch.rLineTo(0, -floorO / 2f);
				mSketch.rLineTo(floorO / 2f, 0);
				mSketch.rLineTo(0, -floorO);
				mSketch.rLineTo(-floorO * 1.5f, 0);
				if (i == 2) {
					mSketch.rLineTo(0, -floorO * 11f);
					mSketch.rLineTo(-floorO * 2f, 0);
					mSketch.rLineTo(0, floorO * 11f);
				} else {
					mSketch.rLineTo(0, -floorO * 9f);
					mSketch.rLineTo(-floorO * 2f, 0);
					mSketch.rLineTo(0, floorO * 9f);
				}
				mSketch.rLineTo(-floorO * 1.5f, 0);
				mSketch.rLineTo(0, floorO);
				mSketch.rLineTo(floorO / 2f, 0);
				mSketch.rLineTo(0, floorO / 2f);
				mSketch.rLineTo(-floorO / 2f, 0);
				mSketch.rLineTo(0, floorO * 4f);
				mSketch.rLineTo(-floorO / 2f, 0);
				mSketch.rLineTo(0, floorO);

				mWindow.rLineTo(0, -floorO * 4f);
				mWindow.rLineTo(-floorO * 3f, 0);
				mWindow.rLineTo(0, floorO * 4f);
				mWindow.rLineTo(floorO / 2f, 0);
				mWindow.rLineTo(0, -floorO * 3.5f);
				mWindow.rLineTo(floorO * 2f, 0);
				mWindow.rLineTo(0, floorO * 3.5f);
			}
		}

		// R/L 2 to 3
		tmpT -= floorO * 6.5f;
		for (int i = 0; i < 2; i++) {
			mSketch.moveTo(buildR - (floorO * 5f) - (floorO * 79f * i), tmpT);
			mWindow.moveTo(buildR - (floorO * 5f) - (floorO * 79f * i), tmpT);
			mSketch.rLineTo(0, -floorO * 9f);
			mSketch.rLineTo(-floorO * 13.5f, 0);
			mSketch.rLineTo(0, floorO * 9f);
			mSketch.rLineTo(floorO, 0);
			mSketch.rCubicTo(0, -floorO * 9.5f, floorO * 11.5f, -floorO * 9.5f, floorO * 11.5f, 0);

			mWindow.rLineTo(0, -floorO * 9f);
			mWindow.rLineTo(-floorO * 13.5f, 0);
			mWindow.rLineTo(0, floorO * 9f);
			mWindow.rLineTo(floorO / 2f, 0);
			mWindow.rLineTo(0, -floorO * 8.5f);
			mWindow.rLineTo(floorO * 12.5f, 0);
			mWindow.rLineTo(0, floorO * 8.5f);

			for (int j = 0; j < 14; j++) {
				mWindow.addRect(buildR - (floorO * 5.5f) - (floorO * j) - (floorO * 79f * i), tmpT - (floorO * 12.5f),
						buildR - (floorO * 5f) - (floorO * j) - (floorO * 79f * i), tmpT - (floorO * 11f), Direction.CW);
			}
		}

		// R/L 1 to 2
		for (int i = 0; i < 2; i++) {
			mSketch.moveTo(buildR - (floorO * 20.5f) - (floorO * 44f * i), tmpT);
			mWindow.moveTo(buildR - (floorO * 20.5f) - (floorO * 44f * i), tmpT);

			mSketch.rLineTo(0, -floorO * 11f);
			mSketch.rLineTo(-floorO * 17.5f, 0);
			mSketch.rLineTo(0, floorO * 11f);
			mSketch.rLineTo(floorO, 0);
			mSketch.rCubicTo(0, -floorO * 11.5f, floorO * 15.5f, -floorO * 11.5f, floorO * 15.5f, 0);

			mWindow.rLineTo(0, -floorO * 11f);
			mWindow.rLineTo(-floorO * 17.5f, 0);
			mWindow.rLineTo(0, floorO * 11f);
			mWindow.rLineTo(floorO / 2f, 0);
			mWindow.rLineTo(0, -floorO * 10.5f);
			mWindow.rLineTo(floorO * 16.5f, 0);
			mWindow.rLineTo(0, floorO * 10.5f);

			for (int j = 0; j < 18; j++) {
				mWindow.addRect(buildR - (floorO * 21f) - (floorO * j) - (floorO * 44f * i), tmpT - (floorO * 14.5f),
						buildR - (floorO * 20.5f) - (floorO * j) - (floorO * 44f * i), tmpT - (floorO * 13f),
						Direction.CW);
			}
		}

		// Middle
		mSketch.moveTo(buildR - (floorO * 40f), tmpT);
		mWindow.moveTo(buildR - (floorO * 40f), tmpT);

		mSketch.rLineTo(0, -floorO * 13f);
		mSketch.rLineTo(-floorO * 22.5f, 0);
		mSketch.rLineTo(0, floorO * 13f);
		mSketch.rLineTo(floorO, 0);
		mSketch.rCubicTo(0, -floorO * 13.5f, floorO * 20.5f, -floorO * 13.5f, floorO * 20.5f, 0);

		mWindow.rLineTo(0, -floorO * 13f);
		mWindow.rLineTo(-floorO * 22.5f, 0);
		mWindow.rLineTo(0, floorO * 13f);
		mWindow.rLineTo(floorO / 2f, 0);
		mWindow.rLineTo(0, -floorO * 12.5f);
		mWindow.rLineTo(floorO * 21.5f, 0);
		mWindow.rLineTo(0, floorO * 12.5f);

		for (int j = 0; j < 23; j++) {
			mWindow.addRect(buildR - (floorO * 40.5f) - (floorO * j), tmpT - (floorO * 16.5f), buildR - (floorO * 40f)
					- (floorO * j), tmpT - (floorO * 15f), Direction.CW);
		}

		// Fences R 3 to L 3
		// Layer 1
		mSketch.moveTo(buildR - (floorO * 2f), tmpT -= floorO * 9f);
		mSketch.rLineTo(-floorO * 18.5f, 0);
		mSketch.rLineTo(0, -floorO * 2f);
		mSketch.rLineTo(-floorO * 19.5f, 0);
		mSketch.rLineTo(0, -floorO * 2f);
		mSketch.rLineTo(-floorO * 22.5f, 0);
		mSketch.rLineTo(0, floorO * 2f);
		mSketch.rLineTo(-floorO * 19.5f, 0);
		mSketch.rLineTo(0, floorO * 2f);
		mSketch.rLineTo(-floorO * 18.5f, 0);
		mSketch.rLineTo(0, -floorO * 2f);

		mSketch.rLineTo(floorO, 0);
		mSketch.rLineTo(0, -floorO * 1.5f);
		// Layer 2
		mSketch.rLineTo(-floorO * 1.5f, 0);
		mSketch.rLineTo(0, -floorO * 2f);
		mSketch.rLineTo(floorO * 15f, 0);
		mSketch.rLineTo(0, -floorO * 2f);
		mSketch.rLineTo(floorO * 18.5f, 0);
		mSketch.rLineTo(0, -floorO * 2f);
		mSketch.rLineTo(floorO * 32.5f, 0);
		mSketch.rLineTo(0, floorO * 2f);
		mSketch.rLineTo(floorO * 18.5f, 0);
		mSketch.rLineTo(0, floorO * 2f);
		mSketch.rLineTo(floorO * 15f, 0);
		mSketch.rLineTo(0, floorO * 2f);
		mSketch.rLineTo(-floorO * 1.5f, 0);
		mSketch.rLineTo(0, floorO * 1.5f);
		mSketch.rLineTo(floorO, 0);
		mSketch.rLineTo(0, floorO * 2f);

		// Top R/L 3
		mSketch.moveTo(buildR - (floorO * 2f), tmpT -= floorO * 5.5f);
		mSketch.rLineTo(floorO, -floorO);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(floorO, 0);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(-floorO, -floorO * 1.5f);
		mSketch.rLineTo(-floorO * 6f, 0);
		mSketch.rLineTo(0, floorO * 3f);
		mSketch.rLineTo(floorO / 2f, floorO / 2f);
		for (int i = 0; i < 2; i++) {
			mSketch.rLineTo(floorO / 2f, 0);
			mSketch.rLineTo(floorO / 2f, -floorO / 2f);
			mSketch.rLineTo(0, -floorO);
			mSketch.rLineTo(floorO / 2f, 0);
			mSketch.rLineTo(0, floorO);
			mSketch.rLineTo(floorO / 2f, floorO / 2f);
		}

		for (int i = 0; i < 2; i++) {
			mSketch.moveTo(buildR - (floorO * 7f) - (floorO * 79f * i), tmpT);
			mSketch.rLineTo(0, -floorO * 5.5f);
			mSketch.rLineTo(floorO, -floorO * 1.5f);
			mSketch.rLineTo(0, -floorO / 2f);
			mSketch.rLineTo(floorO * 2.5f, -floorO * 1.5f);
			mSketch.rLineTo(0, -floorO);
			mSketch.rLineTo(-floorO / 4f, -floorO / 4f);
			mSketch.rLineTo(floorO / 4f, -floorO / 4f);
			mSketch.rQuadTo(-floorO * 1.5f, 0, -floorO * 2.5f, -floorO);
			mSketch.rLineTo(-floorO * 11.5f, 0);
			mSketch.rQuadTo(-floorO, floorO, -floorO * 2.5f, floorO);
			mSketch.rLineTo(floorO / 4f, floorO / 4f);
			mSketch.rLineTo(-floorO / 4f, floorO / 4f);
			mSketch.rLineTo(0, floorO);
			mSketch.rLineTo(floorO * 2.5f, floorO * 1.5f);
			mSketch.rLineTo(0, floorO / 2f);
			mSketch.rLineTo(floorO, floorO * 1.5f);
			if (i == 0) {
				mSketch.rLineTo(0, floorO * 5.5f);
			} else {
				mSketch.rLineTo(0, floorO * 2f);
				mSketch.rLineTo(-floorO * 6f, 0);
				mSketch.rLineTo(-floorO, floorO * 1.5f);
				mSketch.rLineTo(0, floorO / 2f);
				mSketch.rLineTo(floorO, 0);
				mSketch.rLineTo(0, floorO / 2f);
				mSketch.rLineTo(floorO, floorO);
				for (int j = 0; j < 2; j++) {
					mSketch.rLineTo(floorO / 2f, 0);
					mSketch.rLineTo(floorO / 2f, -floorO / 2f);
					mSketch.rLineTo(0, -floorO);
					mSketch.rLineTo(floorO / 2f, 0);
					mSketch.rLineTo(0, floorO);
					mSketch.rLineTo(floorO / 2f, floorO / 2f);
				}
				mSketch.rLineTo(floorO / 2f, 0);
				mSketch.rLineTo(floorO / 2f, -floorO / 2f);
				mSketch.rLineTo(0, floorO / 2f);
			}

			mWindow.moveTo(buildR - (floorO * 8.5f) - (floorO * 79f * i), tmpT);
			mWindow.rLineTo(0, -floorO * 4f);
			mWindow.rLineTo(-floorO * 6.5f, 0);
			mWindow.rLineTo(0, floorO * 4f);
			mWindow.rLineTo(floorO * 6f, 0);
			mWindow.rLineTo(0, -floorO / 2f);
			mWindow.rLineTo(-floorO * 5.5f, 0);
			mWindow.rLineTo(0, -floorO * 3f);
			mWindow.rLineTo(floorO * 5.5f, 0);
			mWindow.rLineTo(0, floorO * 3.5f);

			mWindow.moveTo(buildR - (floorO * 3.5f) - (floorO * 79f * i), tmpT - (floorO * 10f));
			mWindow.rLineTo(0, -floorO / 2f);
			mWindow.rLineTo(-floorO * 2.5f, floorO * 1.5f);
			mWindow.rLineTo(-floorO * 11.5f, 0);
			mWindow.rLineTo(-floorO * 2.5f, -floorO * 1.5f);
			mWindow.rLineTo(0, floorO / 2f);
			mWindow.rLineTo(floorO * 2.5f, floorO * 1.5f);
			mWindow.rLineTo(floorO * 11.5f, 0);
		}
		// Top R/L 2
		mSketch.moveTo(buildR - (floorO * 17.5f), tmpT -= floorO * 2f);
		mSketch.rLineTo(floorO / 2f, -floorO / 2f);
		mSketch.rLineTo(0, -floorO);
		mSketch.rLineTo(floorO / 2f, 0);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(-floorO, -floorO * 1.5f);
		mSketch.rLineTo(-floorO * 6f, 0);
		mSketch.rLineTo(0, floorO * 3f);
		mSketch.rLineTo(floorO / 2f, floorO / 2f);
		for (int i = 0; i < 2; i++) {
			mSketch.rLineTo(floorO / 2f, 0);
			mSketch.rLineTo(floorO / 2f, -floorO / 2f);
			mSketch.rLineTo(0, -floorO);
			mSketch.rLineTo(floorO, 0);
			mSketch.rLineTo(0, floorO);
			mSketch.rLineTo(floorO / 2f, floorO / 2f);
		}

		for (int i = 0; i < 2; i++) {
			mSketch.moveTo(buildR - (floorO * 23.5f) - (floorO * 44f * i), tmpT);
			mSketch.rLineTo(0, -floorO * 6f);
			mSketch.rLineTo(floorO, -floorO * 1.5f);
			mSketch.rLineTo(0, -floorO / 2f);
			mSketch.rLineTo(floorO * 4.5f, -floorO * 2.5f);
			mSketch.rLineTo(0, -floorO);
			mSketch.rLineTo(-floorO / 4f, -floorO / 4f);
			mSketch.rLineTo(floorO / 4f, -floorO / 4f);
			mSketch.rQuadTo(-floorO * 3f, 0, -floorO * 4.5f, -floorO * 1.5f);
			mSketch.rLineTo(-floorO * 13.5f, 0);
			mSketch.rQuadTo(-floorO * 1.5f, floorO * 1.5f, -floorO * 4.5f, floorO * 1.5f);
			mSketch.rLineTo(floorO / 4f, floorO / 4f);
			mSketch.rLineTo(-floorO / 4f, floorO / 4f);
			mSketch.rLineTo(0, floorO);
			mSketch.rLineTo(floorO * 4.5f, floorO * 2.5f);
			mSketch.rLineTo(0, floorO / 2f);
			mSketch.rLineTo(floorO, floorO * 1.5f);
			if (i == 0) {
				mSketch.rLineTo(0, floorO * 6f);
			} else {
				mSketch.rLineTo(0, floorO * 2.5f);
				mSketch.rLineTo(-floorO * 6f, 0);
				mSketch.rLineTo(-floorO, floorO * 1.5f);
				mSketch.rLineTo(0, floorO / 2f);
				mSketch.rLineTo(floorO / 2f, 0);
				mSketch.rLineTo(0, floorO);
				mSketch.rLineTo(floorO / 2f, floorO / 2f);
				for (int j = 0; j < 2; j++) {
					mSketch.rLineTo(floorO / 2f, 0);
					mSketch.rLineTo(floorO / 2f, -floorO / 2f);
					mSketch.rLineTo(0, -floorO);
					mSketch.rLineTo(floorO, 0);
					mSketch.rLineTo(0, floorO);
					mSketch.rLineTo(floorO / 2f, floorO / 2f);
				}
			}
			mWindow.moveTo(buildR - (floorO * 25f) - (floorO * 44f * i), tmpT);
			mWindow.rLineTo(0, -floorO * 4.5f);
			mWindow.rLineTo(-floorO * 8.5f, 0);
			mWindow.rLineTo(0, floorO * 4.5f);
			mWindow.rLineTo(floorO * 8f, 0);
			mWindow.rLineTo(0, -floorO / 2f);
			mWindow.rLineTo(-floorO * 7.5f, 0);
			mWindow.rLineTo(0, -floorO * 3.5f);
			mWindow.rLineTo(floorO * 7.5f, 0);
			mWindow.rLineTo(0, floorO * 4f);

			mWindow.moveTo(buildR - (floorO * 18f) - (floorO * 44f * i), tmpT - (floorO * 11.5f));
			mWindow.rLineTo(0, -floorO / 2f);
			mWindow.rLineTo(-floorO * 4.5f, floorO * 2.5f);
			mWindow.rLineTo(-floorO * 13.5f, 0);
			mWindow.rLineTo(-floorO * 4.5f, -floorO * 2.5f);
			mWindow.rLineTo(0, floorO / 2f);
			mWindow.rLineTo(floorO * 4.5f, floorO * 2.5f);
			mWindow.rLineTo(floorO * 13.5f, 0);
		}

		// Middle
		mSketch.moveTo(buildR - (floorO * 36f), tmpT -= floorO * 2f);
		mSketch.rLineTo(floorO / 2f, -floorO / 2f);
		mSketch.rLineTo(0, -floorO * 1.5f);
		mSketch.rLineTo(floorO / 2f, 0);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(-floorO, -floorO * 1.5f);
		mSketch.rLineTo(-floorO * 7f, 0);
		mSketch.rLineTo(0, -floorO * 3.5f);
		mSketch.rLineTo(floorO, -floorO * 1.5f);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(floorO * 4.5f, -floorO * 2.5f);
		mSketch.rLineTo(0, -floorO);
		mSketch.rLineTo(-floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(floorO / 4f, -floorO / 4f);
		mSketch.rQuadTo(-floorO * 3f, 0, -floorO * 4.5f, -floorO * 1.5f);
		mSketch.rLineTo(-floorO * 18.5f, 0);
		mSketch.rQuadTo(-floorO * 1.5f, floorO * 1.5f, -floorO * 4.5f, floorO * 1.5f);
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(0, floorO);
		mSketch.rLineTo(floorO * 4.5f, floorO * 2.5f);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(floorO, floorO * 1.5f);
		mSketch.rLineTo(0, floorO * 3.5f);
		mSketch.rLineTo(-floorO * 7f, 0);
		mSketch.rLineTo(-floorO, floorO * 1.5f);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(floorO / 2f, 0);
		mSketch.rLineTo(0, floorO * 1.5f);
		mSketch.rLineTo(floorO / 2f, floorO / 2f);
		for (int j = 0; j < 3; j++) {
			mSketch.rLineTo(floorO / 2f, 0);
			mSketch.rLineTo(floorO / 2f, -floorO / 2f);
			mSketch.rLineTo(0, -floorO * 1.5f);
			mSketch.rLineTo(floorO / 2f, 0);
			mSketch.rLineTo(0, floorO * 1.5f);
			mSketch.rLineTo(floorO / 2f, floorO / 2f);
		}
		mSketch.rLineTo(floorO / 2f, 0);
		mSketch.rLineTo(floorO / 2f, -floorO / 2f);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(floorO * 16.5f, 0);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(floorO / 2f, floorO / 2f);
		for (int j = 0; j < 3; j++) {
			mSketch.rLineTo(floorO / 2f, 0);
			mSketch.rLineTo(floorO / 2f, -floorO / 2f);
			mSketch.rLineTo(0, -floorO * 1.5f);
			mSketch.rLineTo(floorO / 2f, 0);
			mSketch.rLineTo(0, floorO * 1.5f);
			mSketch.rLineTo(floorO / 2f, floorO / 2f);
		}

		mWindow.moveTo(buildR - (floorO * 44.5f), tmpT);
		mWindow.rLineTo(0, -floorO * 6f);
		mWindow.rLineTo(-floorO * 13.5f, 0);
		mWindow.rLineTo(0, floorO * 6f);
		mWindow.rLineTo(floorO * 13f, 0);
		mWindow.rLineTo(0, -floorO / 2f);
		mWindow.rLineTo(-floorO * 12.5f, 0);
		mWindow.rLineTo(0, -floorO * 5f);
		mWindow.rLineTo(floorO * 12.5f, 0);
		mWindow.rLineTo(0, floorO * 5.5f);

		mWindow.moveTo(buildR - (floorO * 37.5f), tmpT - (floorO * 13f));
		mWindow.rLineTo(0, -floorO / 2f);
		mWindow.rLineTo(-floorO * 4.5f, floorO * 2.5f);
		mWindow.rLineTo(-floorO * 18.5f, 0);
		mWindow.rLineTo(-floorO * 4.5f, -floorO * 2.5f);
		mWindow.rLineTo(0, floorO / 2f);
		mWindow.rLineTo(floorO * 4.5f, floorO * 2.5f);
		mWindow.rLineTo(floorO * 18.5f, 0);

		mSketch.close();
		mWindow.close();

		canvas.drawPath(mSketch, mTitlePaint);
		canvas.drawPath(mWindow, mWindowPaint);
	}

	private void drawSongShan(Canvas canvas, float right) {
		float floorO = Util.PIXEL * Util.MULTI;
		float buildR = right;
		float tmpT = floorO * 45.5f;

		mSketch.reset();
		mWindow.reset();

		mSketch.moveTo(buildR, tmpT + (floorO * 54.5f));
		mSketch.rLineTo(0, -floorO * 51.5f);
		mSketch.rLineTo(-floorO * 2f, 0);
		mSketch.rLineTo(0, -floorO * 2f);
		mSketch.rLineTo(-floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(floorO, 0);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(-floorO * 55f, 0);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(-floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(-floorO * 3.5f, 0);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(floorO, 0);
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(0, floorO * 2f);
		mSketch.rLineTo(-floorO * 49f, 0);
		mSketch.rLineTo(0, floorO * 51.5f);
		// Top fences
		mSketch.addRect(buildR -= floorO * 81f, tmpT -= floorO / 2f, buildR - floorO, tmpT + (floorO * 3.5f),
				Direction.CW);
		for (int i = 0; i < 3; i++) {
			mSketch.addRect(buildR - floorO, tmpT + (floorO / 2f) + (floorO * i), buildR - (floorO * 26.5f), tmpT
					+ floorO + (floorO * i), Direction.CW);
		}
		mSketch.addRect(buildR -= floorO * 2.5f, tmpT, buildR - (floorO / 2f), tmpT + (floorO * 3.5f), Direction.CW);
		for (int i = 0; i < 3; i++) {
			mSketch.addRect(buildR -= floorO * 2f, tmpT, buildR - (floorO / 2f), tmpT + (floorO * 3.5f), Direction.CW);
		}
		mSketch.addRect(buildR -= floorO * 2f, tmpT, buildR - (floorO * 1.5f), tmpT + (floorO * 3.5f), Direction.CW);
		for (int i = 0; i < 2; i++) {
			mSketch.addRect(buildR -= floorO * 3.5f, tmpT, buildR - (floorO * 1.5f), tmpT + (floorO * 3.5f),
					Direction.CW);
		}
		mSketch.addRect(buildR -= floorO * 3f, tmpT, buildR - (floorO / 2f), tmpT + (floorO * 3.5f), Direction.CW);
		for (int i = 0; i < 3; i++) {
			mSketch.addRect(buildR -= floorO * 2f, tmpT, buildR - (floorO / 2f), tmpT + (floorO * 3.5f), Direction.CW);
		}
		// //////////////////////////////
		// Top window
		mWindow.moveTo(buildR += floorO * 48.5f, tmpT += floorO);
		mWindow.rLineTo(0, floorO / 2f);
		mWindow.rLineTo(floorO * 2.5f, 0);
		mWindow.rLineTo(0, floorO * 2f);
		mWindow.rLineTo(floorO / 2f, 0);
		mWindow.rLineTo(0, -floorO * 2f);
		mWindow.rLineTo(floorO * 54f, 0);
		mWindow.rLineTo(0, -floorO / 2f);
		mWindow.rLineTo(-floorO * 54f, 0);
		mWindow.rLineTo(0, -floorO / 2f);
		mWindow.rLineTo(-floorO / 2f, 0);
		mWindow.rLineTo(0, floorO / 2f);

		buildR += floorO * 58.5f;
		tmpT += floorO * 4.5f;
		for (int j = 0; j < 12; j++) {
			mWindow.addRect(buildR, tmpT + (floorO * 4f * j), buildR - floorO, tmpT + (floorO * 3.5f)
					+ (floorO * 4f * j), Direction.CW);
			for (int i = 0; i < 12; i++) {
				if (i > 9) {
					mWindow.addRect((buildR - (floorO * 36.5f)) - (floorO * 5.5f * (i - 10)), tmpT + (floorO * 4f * j),
							(buildR - (floorO * 41.5f)) - (floorO * 5.5f * (i - 10)),
							tmpT + floorO + (floorO * 4f * j), Direction.CW);
					mWindow.addRect((buildR - (floorO * 36.5f)) - (floorO * 5.5f * (i - 10)), tmpT + (floorO * 1.5f)
							+ (floorO * 4f * j), (buildR - (floorO * 41.5f)) - (floorO * 5.5f * (i - 10)), tmpT
							+ (floorO * 3.5f) + (floorO * 4f * j), Direction.CW);
				} else {
					mWindow.addRect((buildR - (floorO * 1.5f)) - (floorO * 3.5f * i), tmpT + (floorO * 4f * j),
							(buildR - (floorO * 4.5f)) - (floorO * 3.5f * i), tmpT + floorO + (floorO * 4f * j),
							Direction.CW);
					mWindow.addRect((buildR - (floorO * 1.5f)) - (floorO * 3.5f * i), tmpT + (floorO * 1.5f)
							+ (floorO * 4f * j), (buildR - (floorO * 4.5f)) - (floorO * 3.5f * i), tmpT
							+ (floorO * 3.5f) + (floorO * 4f * j), Direction.CW);
				}
			}
		}
		mWindow.addRect(buildR -= (floorO * 47.5f), tmpT, buildR -= (floorO / 2f), tmpT + (floorO * 49.5f),
				Direction.CW);
		buildR -= floorO / 2f;

		for (int j = 0; j < 12; j++) {
			mWindow.addRect(buildR - (floorO * 58.5f), tmpT + (floorO * 4f * j), buildR - (floorO * 57.5f), tmpT
					+ (floorO * 3.5f) + (floorO * 4f * j), Direction.CW);
			if (j < 4) {
				for (int i = 0; i < 16; i++) {
					if (i == (j + 12)) {
						buildR -= floorO * 1.5f;
					}
					mWindow.addRect(buildR - (floorO * 3.5f * i), tmpT + (floorO * 4f * j), buildR - (floorO * 3f)
							- (floorO * 3.5f * i), tmpT + floorO + (floorO * 4f * j), Direction.CW);
					mWindow.addRect(buildR - (floorO * 3.5f * i), tmpT + (floorO * 1.5f) + (floorO * 4f * j), buildR
							- (floorO * 3f) - (floorO * 3.5f * i), tmpT + (floorO * 3.5f) + (floorO * 4f * j),
							Direction.CW);
				}
				buildR += floorO * 1.5f;
			} else if ((j > 3) && (j < 6)) {
				for (int i = 0; i < 16; i++) {
					if (i == 1) {
						mWindow.addRect(buildR - (floorO * 3.5f * i), tmpT + (floorO * 4f * j), buildR
								- (floorO * 4.5f) - (floorO * 3.5f * i), tmpT + floorO + (floorO * 4f * j),
								Direction.CW);
						mWindow.addRect(buildR - (floorO * 3.5f * i), tmpT + (floorO * 1.5f) + (floorO * 4f * j),
								buildR - (floorO * 4.5f) - (floorO * 3.5f * i), tmpT + (floorO * 3.5f)
										+ (floorO * 4f * j), Direction.CW);
						buildR -= floorO * 1.5f;
					} else {
						mWindow.addRect(buildR - (floorO * 3.5f * i), tmpT + (floorO * 4f * j), buildR - (floorO * 3f)
								- (floorO * 3.5f * i), tmpT + floorO + (floorO * 4f * j), Direction.CW);
						mWindow.addRect(buildR - (floorO * 3.5f * i), tmpT + (floorO * 1.5f) + (floorO * 4f * j),
								buildR - (floorO * 3f) - (floorO * 3.5f * i), tmpT + (floorO * 3.5f)
										+ (floorO * 4f * j), Direction.CW);
					}
				}
				buildR += floorO * 1.5f;
			} else {
				for (int i = 0; i < 16; i++) {
					if (i == (j - 4)) {
						buildR -= floorO * 1.5f;
					}
					mWindow.addRect(buildR - (floorO * 3.5f * i), tmpT + (floorO * 4f * j), buildR - (floorO * 3f)
							- (floorO * 3.5f * i), tmpT + floorO + (floorO * 4f * j), Direction.CW);
					mWindow.addRect(buildR - (floorO * 3.5f * i), tmpT + (floorO * 1.5f) + (floorO * 4f * j), buildR
							- (floorO * 3f) - (floorO * 3.5f * i), tmpT + (floorO * 3.5f) + (floorO * 4f * j),
							Direction.CW);
				}
				buildR += floorO * 1.5f;
			}
		}
		mSketch.close();
		mWindow.close();

		canvas.drawPath(mSketch, mTitlePaint);
		canvas.drawPath(mWindow, mWindowPaint);

		mSketch.reset();
		mWindow.reset();
		// Side Stairs
		mSketch.addRect(buildR -= floorO * 60f, tmpT += floorO * 49f, buildR - (floorO * 46f), tmpT + (floorO / 2f),
				Direction.CW);
		mSketch.addRect(buildR -= floorO / 2f, tmpT, buildR - (floorO * 45f), tmpT -= (floorO / 2f), Direction.CW);
		// Side Main
		mSketch.moveTo(buildR -= floorO / 2f, tmpT);
		mSketch.rLineTo(0, -floorO * 3.5f);
		mSketch.rLineTo(-floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(0, -floorO * 5.5f);
		mSketch.rLineTo(-floorO, 0);
		for (int i = 0; i < 3; i++) {
			mSketch.rLineTo(-floorO * 7f, -floorO * 4.5f);
			mSketch.rLineTo(-floorO * 7f, floorO * 4.5f);
		}
		mSketch.rLineTo(-floorO, 0);
		mSketch.rLineTo(0, floorO * 5.5f);
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(0, floorO * 3.5f);
		// Doors
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < 5; i++) {
				mWindow.addCircle(buildR - (floorO * 3f) - (floorO * 3f * i) - (floorO * 26f * j),
						tmpT - (floorO * 3f), floorO, Direction.CW);
				mWindow.addRect(buildR - (floorO * 2f) - (floorO * 3f * i) - (floorO * 26f * j), tmpT, buildR
						- (floorO * 4f) - (floorO * 3f * i) - (floorO * 26f * j), tmpT - (floorO * 3f), Direction.CW);
			}
		}
		mWindow.addRect(buildR - (floorO * 18f), tmpT - (floorO * 4f), buildR - (floorO * 26f), tmpT, Direction.CW);
		// Ceiling
		mWindow.moveTo(buildR, tmpT -= floorO * 3.5f);
		mWindow.rLineTo(-floorO * 1.5f, 0);
		mWindow.rLineTo(0, -floorO * 1.5f);
		mWindow.rLineTo(-floorO * 41f, 0);
		mWindow.rLineTo(0, floorO * 1.5f);
		mWindow.rLineTo(-floorO * 1.5f, 0);
		mWindow.rLineTo(0, -floorO / 2f);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(0, -floorO * 1.5f);
		mWindow.rLineTo(floorO * 42f, 0);
		mWindow.rLineTo(0, floorO * 1.5f);
		mWindow.rLineTo(floorO, 0);

		tmpT -= floorO * 2.5f;
		for (int k = 0; k < 3; k++) {
			for (int i = 0; i < 14; i++) {
				mWindow.addRect(buildR - (floorO * 1.5f) - (floorO * i), tmpT, buildR - (floorO * 2f) - (floorO * i),
						tmpT - (floorO * 2f), Direction.CW);
			}
			for (int j = 0; j < 2; j++) {
				for (int i = 0; i < 2; i++) {
					mWindow.addRect(buildR - (floorO * 5f) - (floorO * 1.5f * i) - (floorO * 3.5f * j), tmpT
							- (floorO * 4f), buildR - (floorO * 6f) - (floorO * 1.5f * i) - (floorO * 3.5f * j), tmpT
							- (floorO * 3f), Direction.CW);
				}
			}
			mWindow.addCircle(buildR - (floorO * 8f), tmpT - (floorO * 5f), floorO / 2f, Direction.CW);
			mWindow.moveTo(buildR - floorO, tmpT - (floorO * 2.5f));
			mWindow.rLineTo(-floorO * 7f, -floorO * 4.5f);
			mWindow.rLineTo(-floorO * 7f, floorO * 4.5f);
			mWindow.rLineTo(0, -floorO / 2f);
			mWindow.rLineTo(floorO * 7f, -floorO * 4.5f);
			mWindow.rLineTo(floorO * 7f, floorO * 4.5f);
			buildR -= floorO * 14f;
		}
		mSketch.close();
		mWindow.close();

		canvas.drawPath(mSketch, mTitlePaint);
		canvas.drawPath(mWindow, mWindowPaint);

		mSketch.reset();

		buildR += floorO * 5.5f;
		for (int i = 0; i < 3; i++) {
			mSketch.addRect(buildR + (floorO * 14f * i), tmpT - (floorO * 8f), buildR + floorO + (floorO * 14f * i),
					tmpT - (floorO * 7f), Direction.CW);
		}

		mSketch.close();

		canvas.drawPath(mSketch, mTitlePaint);
	}

	private void drawHuashan(Canvas canvas, float right) {
		float floorO = Util.PIXEL * Util.MULTI;
		float buildR = right;
		float tmpT = floorO * 85f;

		mSketch.reset();
		mWindow.reset();

		// Fences
		mSketch.moveTo(buildR, tmpT);
		mSketch.rLineTo(-floorO * 29.5f, 0);
		mSketch.rLineTo(0, floorO * 15f);
		mSketch.rLineTo(floorO * 10f, 0);
		mSketch.rLineTo(0, -floorO * 9.5f);
		mSketch.rLineTo(floorO * 9.5f, 0);
		mSketch.rLineTo(0, floorO * 9.5f);
		mSketch.rLineTo(floorO * 10f, 0);

		tmpT += floorO / 2f;
		for (int j = 0; j < 5; j++) {
			if (j == 0) {
				for (int i = 0; i < 19; i++) {
					mWindow.moveTo(buildR - floorO - (floorO * 1.5f * i), tmpT);
					mWindow.rLineTo(-floorO / 2f, 0);
					mWindow.rLineTo(0, floorO * 2.5f);
					mWindow.rLineTo(floorO / 2f, 0);
				}
			}

			else if (j == 1) {
				for (int i = 0; i < 19; i++) {
					mWindow.moveTo(buildR - floorO - (floorO * 1.5f * i), tmpT + (floorO * 3f));
					mWindow.rLineTo(-floorO / 2f, 0);
					mWindow.rLineTo(0, floorO * 1.5f);
					mWindow.rLineTo(floorO / 2f, 0);
				}
			}

			else if ((j == 2) || (j == 3)) {
				for (int i = 0; i < 19; i++) {
					if ((i < 6) || (i > 12)) {
						mWindow.moveTo(buildR - floorO - (floorO * 1.5f * i), tmpT + (floorO * 5f)
								+ (floorO * 3f * (j - 2)));
						mWindow.rLineTo(-floorO / 2f, 0);
						mWindow.rLineTo(0, floorO * 2.5f);
						mWindow.rLineTo(floorO / 2f, 0);
					}
				}
			} else {
				for (int i = 0; i < 19; i++) {
					if ((i < 6) || (i > 12)) {
						mWindow.moveTo(buildR - floorO - (floorO * 1.5f * i), tmpT + (floorO * 11f));
						mWindow.rLineTo(-floorO / 2f, 0);
						mWindow.rLineTo(0, floorO * 3.5f);
						mWindow.rLineTo(floorO / 2f, 0);
					}
				}
			}
		}

		// Main, House and Church's outline
		mSketch.moveTo(buildR -= floorO * 30.5f, floorO * 100f);
		// Main
		mSketch.rLineTo(0, -floorO * 7.5f);
		for (int i = 0; i < 2; i++) {
			mSketch.rLineTo(-floorO / 4f, -floorO / 4f);
			mSketch.rLineTo(floorO / 4f, -floorO / 4f);
			mSketch.rLineTo(0, -floorO / 2f);
		}
		mSketch.rLineTo(-floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(0, -floorO * 8f);
		mSketch.rLineTo(-floorO * 1.5f, 0);
		mSketch.rLineTo(0, floorO);
		mSketch.rLineTo(-floorO * 46.5f, 0);
		// House
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(-floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(0, -floorO * 2f);
		mSketch.rLineTo(floorO, -floorO);
		mSketch.rLineTo(0, -floorO);
		mSketch.rLineTo(-floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(-floorO, 0);
		mSketch.rLineTo(-floorO * 12.5f, -floorO * 9f);
		mSketch.rLineTo(-floorO * 11.5f, floorO * 8.25f);
		// Church
		mSketch.rLineTo(-floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(0, -floorO * 9.25f);
		mSketch.rLineTo(-floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(0, -floorO * 4.5f);
		mSketch.rLineTo(-floorO * 1.5f, 0);
		mSketch.rLineTo(0, floorO);
		mSketch.rLineTo(-floorO * 10.5f, 0);
		// Church cross
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(-floorO * 0.75f, 0);
		mSketch.rLineTo(0, -floorO * 3f);
		mSketch.rLineTo(floorO * 1.25f, 0);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(-floorO * 1.25f, 0);
		mSketch.rLineTo(0, -floorO);
		mSketch.rLineTo(-floorO / 2f, 0);
		mSketch.rLineTo(0, floorO);
		mSketch.rLineTo(-floorO * 1.25f, 0);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(floorO * 1.25f, 0);
		mSketch.rLineTo(0, floorO * 3f);
		mSketch.rLineTo(-floorO * 0.75f, 0);
		mSketch.rLineTo(0, floorO / 2f);
		// ///////////////////////////////////
		mSketch.rLineTo(-floorO * 10.5f, 0);
		mSketch.rLineTo(0, -floorO);
		mSketch.rLineTo(-floorO * 1.5f, 0);
		mSketch.rLineTo(0, floorO * 4.5f);
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(0, floorO * 32.5f);
		// Main Window
		tmpT -= floorO * 2f;
		for (int i = 0; i < 53; i++) {
			if (i > 45) {
				mWindow.addRect((buildR - (floorO * 2.5f)) - (floorO * i), tmpT + (floorO * 4.5f), buildR
						- (floorO * 2f) - (floorO * i), tmpT + (floorO * 6.5f), Direction.CW);
			} else {
				mWindow.addRect((buildR - (floorO * 2.5f)) - (floorO * i), tmpT, buildR - (floorO * 2f) - (floorO * i),
						tmpT + (floorO * 6.5f), Direction.CW);
			}
		}
		mWindow.addRect(buildR - (floorO * 1.5f), tmpT += floorO * 6.5f, buildR, tmpT + (floorO / 2), Direction.CW);
		mWindow.addRect(buildR - (floorO * 56.5f), tmpT, buildR - (floorO * 55f), tmpT + (floorO / 2), Direction.CW);
		mWindow.addRect(buildR - (floorO * 55f), tmpT + (floorO / 2f), buildR - (floorO * 1.5f), tmpT + floorO,
				Direction.CW);
		for (int i = 0; i < 2; i++) {
			mWindow.addRect(buildR - (floorO * 1.5f), tmpT += floorO, buildR, tmpT + (floorO / 2), Direction.CW);
			mWindow.addRect(buildR - (floorO * 56.5f), tmpT, buildR - (floorO * 55f), tmpT + (floorO / 2), Direction.CW);
		}

		mWindow.addRect(buildR -= floorO * 5f, tmpT += floorO * 3f, buildR -= floorO * 4f, tmpT + (floorO * 3.5f),
				Direction.CCW);
		mWindow.addRect(buildR -= floorO * 2.5f, tmpT, buildR -= floorO * 4f, tmpT + (floorO * 3.5f), Direction.CCW);
		mWindow.addRect(buildR -= floorO * 3.5f, tmpT, buildR -= floorO * 5f, tmpT + (floorO * 3.5f), Direction.CCW);
		mWindow.addRect(buildR -= floorO * 1.5f, tmpT, buildR -= floorO * 5f, tmpT + (floorO * 3.5f), Direction.CCW);
		mWindow.addRect(buildR -= floorO * 1.5f, tmpT, buildR -= floorO * 5f, tmpT + (floorO * 3.5f), Direction.CCW);
		for (int i = 0; i < 3; i++) {
			mWindow.addCircle(buildR + (floorO * 2.5f) + (floorO * 6.5f * i), tmpT, floorO * 2.5f, Direction.CW);
		}
		buildR -= floorO * 2.5f;
		tmpT -= floorO * 2.5f;
		for (int i = 0; i < 2; i++) {
			mWindow.moveTo(buildR - (floorO * 8f * i), tmpT);
			mWindow.rLineTo(-floorO * 5.5f, 0);
			mWindow.rLineTo(0, floorO * 7.5f);
			mWindow.rLineTo(floorO / 4f, 0);
			mWindow.rLineTo(0, -floorO * 7.25f);
			mWindow.rLineTo(floorO * 5f, 0);
			mWindow.rLineTo(0, floorO * 7.25f);
			mWindow.rLineTo(floorO / 4f, 0);
		}
		mWindow.addRect(buildR -= (floorO / 2f), tmpT += floorO / 2f, buildR -= floorO * 2f, tmpT + (floorO * 7f),
				Direction.CW);
		mWindow.addRect(buildR -= (floorO / 2f), tmpT, buildR -= floorO * 2f, tmpT + (floorO * 7f), Direction.CW);
		mWindow.addRect(buildR -= (floorO * 3.5f), tmpT, buildR -= floorO * 2f, tmpT + (floorO * 2.5f), Direction.CW);
		mWindow.addRect(buildR -= (floorO / 2f), tmpT, buildR -= floorO * 2f, tmpT + (floorO * 2.5f), Direction.CW);
		// House outline Window
		mWindow.moveTo(buildR -= floorO * 4f, tmpT += floorO * 7f);
		mWindow.rLineTo(0, -floorO * 15f);
		mWindow.rLineTo(floorO * 1.5f, 0);
		mWindow.rLineTo(0, floorO * 2.5f);
		mWindow.rLineTo(floorO * 7f, 0);
		mWindow.rLineTo(0, -floorO * 4.5f);
		mWindow.rLineTo(-floorO / 2f, 0);
		mWindow.rLineTo(0, floorO * 4f);
		mWindow.rLineTo(-floorO * 3f, 0);
		mWindow.rLineTo(0, -floorO * 5f);
		mWindow.rLineTo(-floorO * 2f, 0);
		mWindow.rLineTo(0, floorO * 5f);
		mWindow.rLineTo(-floorO, 0);
		mWindow.rLineTo(0, -floorO * 2.5f);
		mWindow.rLineTo(-floorO * 2.5f, 0);
		mWindow.rLineTo(0, floorO * 14f);
		mWindow.rLineTo(-floorO * 12.5f, 0);
		mWindow.rLineTo(0, -floorO * 5.5f);
		mWindow.rLineTo(-floorO * 3f, 0);
		mWindow.rLineTo(0, floorO * 5.5f);
		mWindow.rLineTo(-floorO, 0);
		mWindow.rLineTo(0, -floorO * 17.5f);
		mWindow.rLineTo(-floorO, -floorO);
		mWindow.rLineTo(0, -floorO);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(floorO * 12.5f, -floorO * 9f);
		mWindow.rLineTo(floorO * 12.5f, floorO * 9f);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(0, -floorO / 2f);
		mWindow.rLineTo(-floorO, 0);
		mWindow.rLineTo(-floorO * 12.5f, -floorO * 9f);
		mWindow.rLineTo(-floorO * 12.5f, floorO * 9f);
		mWindow.rLineTo(-floorO, 0);
		mWindow.rLineTo(0, -floorO / 2f);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(floorO, -floorO * 0.75f);
		mWindow.rLineTo(0, -floorO / 2f);
		mWindow.rLineTo(-floorO, floorO * 0.75f);
		mWindow.rLineTo(-floorO * 1.5f, 0);
		mWindow.rLineTo(0, floorO * 2.5f);
		mWindow.rLineTo(floorO, floorO);
		mWindow.rLineTo(0, floorO * 17.5f);
		mWindow.rLineTo(-floorO * 24.5f, 0);
		mWindow.rLineTo(0, floorO / 2f);
		mWindow.rLineTo(floorO * 24.5f, 0);
		mWindow.rLineTo(0, floorO);
		mWindow.rLineTo(floorO / 2f, 0);
		mWindow.rLineTo(0, -floorO);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(0, floorO);
		mWindow.rLineTo(floorO * 3f, 0);
		mWindow.rLineTo(0, -floorO);
		mWindow.rLineTo(floorO * 12.5f, 0);
		mWindow.rLineTo(0, floorO);
		// House Window
		// Center = buildR
		buildR -= floorO * 4.5f;
		tmpT -= floorO * 26f;
		for (int i = 0; i < 4; i++) {
			mWindow.addCircle((buildR - (floorO * 4.5f)) + (floorO * 3f * i), tmpT + floorO, floorO, Direction.CW);
			mWindow.addRect((buildR - (floorO * 5.5f)) + (floorO * 3f * i), tmpT + (floorO * 2f),
					(buildR - (floorO * 3.5f)) + (floorO * 3f * i), tmpT + (floorO * 6f), Direction.CW);
		}

		tmpT += floorO * 8f;
		mWindow.addRect(buildR + (floorO * 1.5f), tmpT, buildR + (floorO * 3.5f), tmpT + (floorO * 6f), Direction.CCW);
		mWindow.addRect(buildR - (floorO * 1.5f), tmpT, buildR - (floorO * 3.5f), tmpT + (floorO * 6f), Direction.CW);
		mWindow.addRect(buildR + (floorO * 7.5f), tmpT, buildR + (floorO * 9.5f), tmpT + (floorO * 5f), Direction.CCW);
		mWindow.addRect(buildR - (floorO * 7.5f), tmpT, buildR - (floorO * 9.5f), tmpT + (floorO * 6f), Direction.CCW);

		buildR += floorO * 2f;
		tmpT += floorO * 10.5f;
		for (int i = 0; i < 3; i++) {
			mArcRect.set(buildR - (floorO * 2f) - (floorO * 3f * i), tmpT - floorO, buildR - (floorO * 3f * i), tmpT
					+ floorO);
			mWindow.addArc(mArcRect, 180f, 180f);
			mWindow.addRect(buildR - (floorO * 3f * i), tmpT, buildR - (floorO * 2f) - (floorO * 3f * i), tmpT
					+ (floorO * 5.5f), Direction.CW);
		}
		// Church Window
		// Center = buildR
		buildR -= floorO * 15.5f;
		tmpT -= floorO * 28.5f;
		for (int i = 0; i < 23; i++) {
			mWindow.addRect(buildR - (floorO * i), tmpT, buildR - (floorO / 2f) - (floorO * i), tmpT + (floorO * 3f),
					Direction.CW);
		}
		mWindow.addRect(buildR + (floorO * 2f), tmpT += floorO * 3f, buildR + (floorO / 2f), tmpT + (floorO / 2f),
				Direction.CW);
		mWindow.addRect(buildR - (floorO * 22.5f), tmpT, buildR - (floorO * 24f), tmpT + (floorO / 2f), Direction.CW);
		mWindow.addRect(buildR + (floorO / 2f), tmpT += floorO / 2f, buildR - (floorO * 22.5f), tmpT + (floorO / 2f),
				Direction.CW);

		buildR -= floorO * 2.5f;
		tmpT += floorO * 3f;
		for (int i = 0; i < 3; i++) {
			mWindow.addRect(buildR - (floorO * 7.5f * i), tmpT, buildR - (floorO * 2f) - (floorO * 7.5f * i), tmpT
					+ (floorO * 4f), Direction.CW);
			mWindow.addRect(buildR - (floorO * 7.5f * i), tmpT + (floorO * 7f), buildR - (floorO * 2f)
					- (floorO * 7.5f * i), tmpT + (floorO * 13f), Direction.CW);
		}

		mWindow.addCircle(buildR -= floorO * 8.5f, tmpT += floorO * 16.5f, floorO, Direction.CW);

		mArcRect.set(buildR - (floorO * 2.5f), tmpT += floorO * 3.5f, buildR - (floorO / 2f), tmpT + (floorO * 2f));
		mWindow.addArc(mArcRect, 180f, 180f);
		mArcRect.set(buildR + (floorO / 2f), tmpT, buildR + (floorO * 2.5f), tmpT + (floorO * 2f));
		mWindow.addArc(mArcRect, 180f, 180f);
		mWindow.addRect(buildR - (floorO / 2f), tmpT += floorO, buildR - (floorO * 2.5f), tmpT + (floorO * 6f),
				Direction.CW);
		mWindow.addRect(buildR + (floorO / 2f), tmpT, buildR + (floorO * 2.5f), tmpT + (floorO * 6f), Direction.CW);

		mArcRect.set(buildR - (floorO * 10.5f), tmpT - (floorO * 3.5f), buildR - (floorO * 4.5f), tmpT
				+ (floorO * 2.5f));
		mWindow.addArc(mArcRect, 180f, 180f);
		mArcRect.set(buildR + (floorO * 4.5f), tmpT - (floorO * 3.5f), buildR + (floorO * 10.5f), tmpT
				+ (floorO * 2.5f));
		mWindow.addArc(mArcRect, 180f, 180f);
		mWindow.addRect(buildR - (floorO * 10.5f), tmpT, buildR - (floorO * 4.5f), tmpT + (floorO * 6f), Direction.CW);
		mWindow.addRect(buildR + (floorO * 10.5f), tmpT, buildR + (floorO * 4.5f), tmpT + (floorO * 6f), Direction.CW);

		mSketch.close();
		mWindow.close();

		canvas.drawPath(mSketch, mTitlePaint);
		canvas.drawPath(mWindow, mWindowPaint);

		mWindow.reset();

		mWindow.moveTo(buildR, tmpT -= floorO * 4.5f);
		mWindow.rLineTo(floorO / 2f, -floorO * 2.5f);
		mWindow.rLineTo(-floorO, 0);
		mWindow.rLineTo(floorO, floorO * 5f);
		mWindow.rLineTo(-floorO, 0);
		mWindow.rLineTo(floorO / 2f, -floorO * 2.5f);

		mWindow.rLineTo(-floorO * 2.5f, -floorO / 2f);
		mWindow.rLineTo(0, floorO);
		mWindow.rLineTo(floorO * 5f, -floorO);
		mWindow.rLineTo(0, floorO);
		mWindow.rLineTo(-floorO * 2.5f, -floorO / 2f);

		mWindow.close();

		canvas.drawPath(mWindow, mWindowPaint);
	}

	private void drawWheel(Canvas canvas, float left) {
		float floorO = Util.PIXEL * Util.MULTI;
		// Draw wheel
		// Middle circle
		mSketch.reset();
		mSketch.addCircle(left + (floorO * 89f), floorO * 32f, floorO * 6f, Direction.CW);
		mSketch.addRect(left + (floorO * 79f), floorO * 63f, left + (floorO * 99f), floorO * 65f, Direction.CW);
		mSketch.close();
		canvas.drawPath(mSketch, mBack70Paint);

		mSketch.reset();
		mWindow.reset();
		mSketch.addCircle(left + (floorO * 89f), floorO * 32f, floorO * 3f, Direction.CW);
		mWindow.addCircle(left + (floorO * 89f), floorO * 32f, floorO * 5f, Direction.CW);
		mSketch.close();
		mWindow.close();
		canvas.drawPath(mWindow, mWindowPaint);
		canvas.drawPath(mSketch, mBack70Paint);

		// Foot
		mSketch.reset();
		mSketch.moveTo(left + (floorO * 88f), floorO * 32f);
		mSketch.rLineTo(-floorO * 20f, floorO * 68f);
		mSketch.rLineTo(floorO * 2f, 0);
		mSketch.rLineTo(floorO * 19f, -floorO * 64.5f);
		mSketch.rLineTo(floorO * 19f, floorO * 64.5f);
		mSketch.rLineTo(floorO * 2f, 0);
		mSketch.rLineTo(-floorO * 20f, -floorO * 68f);
		mSketch.close();
		canvas.drawPath(mSketch, mBack70Paint);

		mBack70Paint.setStyle(Style.STROKE);
		mBack70Paint.setStrokeWidth(floorO / 2f);
		// Out side circle
		mSketch.reset();
		mSketch.addCircle(left + (floorO * 89f), floorO * 32f, floorO * 12f, Direction.CW);
		mSketch.addCircle(left + (floorO * 89f), floorO * 32f, floorO * 18f, Direction.CW);
		mSketch.addCircle(left + (floorO * 89f), floorO * 32f, floorO * 25f, Direction.CW);
		mSketch.close();
		canvas.drawPath(mSketch, mBack70Paint);

		// Seat stand
		canvas.save();
		canvas.translate(left + (floorO * 89f), floorO * 32f); // move canvas to wheel midPoint
		for (int i = 0; i < 60; i++) {
			canvas.rotate(360 / 60);
			if ((i % 2) == 0) {
				canvas.drawLine(0, 0, 0, floorO * 18f, mBack70Paint);
			} else {
				canvas.drawLine(0, 0, 0, floorO * 28f, mBack70Paint);
			}
		}
		canvas.restore();
		mBack70Paint.setStyle(Style.FILL);

		// Seat
		canvas.save();
		canvas.translate(left + (floorO * 89f), floorO * 32f); // move canvas to wheel midPoint
		mSketch.reset();
		for (int i = 0; i < 60; i++) {
			canvas.rotate(360 / 60);
			if ((i % 2) != 0) {
				canvas.drawCircle(0, floorO * 28f, floorO * 2f, mBack70Paint);
			}
		}
		mSketch.close();

		canvas.drawPath(mSketch, mBack70Paint);
		canvas.restore();
	}

	private void drawBack70(Canvas canvas, float left) {
		float floorO = Util.PIXEL * Util.MULTI;

		drawWheel(canvas, left);

		mSketch.reset();
		mWindow.reset();

		mSketch.moveTo(left, floorO * 100f);
		mSketch.rLineTo(0, -floorO * 30f);
		mSketch.rLineTo(floorO * 6f, 0);
		mSketch.rLineTo(0, floorO * 6f);
		mWindow.addRect(left + floorO, floorO * 72f, left + (floorO * 2f), floorO * 76f, Direction.CW);

		mSketch.rLineTo(floorO * 5f, -floorO * 4f);
		mWindow.addRect(left + (floorO * 8f), floorO * 82f, left + (floorO * 10f), floorO * 85f, Direction.CW);

		mSketch.rLineTo(0, -floorO * 4f);
		mSketch.rLineTo(floorO * 6f, -floorO * 7f);
		mSketch.rLineTo(0, floorO * 24f);
		mWindow.addRect(left + (floorO * 11f), floorO * 77f, left + (floorO * 13f), floorO * 80f, Direction.CW);

		mSketch.rLineTo(floorO * 5f, 0);
		mSketch.rLineTo(0, -floorO * 24f);
		mSketch.rLineTo(floorO * 10f, 0);
		mSketch.rLineTo(0, floorO * 7f);
		mWindow.addRect(left + (floorO * 29f), floorO * 65f, left + (floorO * 31f), floorO * 68f, Direction.CW);

		mSketch.rLineTo(floorO * 3f, 0);
		mWindow.moveTo(left + (floorO * 32f), floorO * 69f);
		mWindow.rLineTo(0, floorO * 8f);
		mWindow.rLineTo(floorO * 3f, floorO * 3f);
		mWindow.rLineTo(0, -floorO * 11f);

		mSketch.rLineTo(0, -floorO * 20f);
		mSketch.rLineTo(floorO * 8f, 0);
		mSketch.rLineTo(0, floorO * 20f);
		mWindow.addRect(left + (floorO * 36f), floorO * 54f, left + (floorO * 38f), floorO * 58f, Direction.CW);
		mWindow.addRect(left + (floorO * 40f), floorO * 61f, left + (floorO * 42f), floorO * 65f, Direction.CW);

		mSketch.rLineTo(floorO * 4.5f, 0);
		mWindow.moveTo(left + (floorO * 43f), floorO * 69f);
		mWindow.rLineTo(0, floorO * 22f);
		mWindow.rLineTo(floorO * 4.5f, 0);
		mWindow.rLineTo(0, -floorO * 22f);

		mSketch.rLineTo(0, -floorO * 3f);
		mSketch.rLineTo(floorO * 2f, 0);
		mSketch.rLineTo(0, floorO * 3f);
		mSketch.rLineTo(floorO, 0);
		mWindow.moveTo(left + (floorO * 49.5f), floorO * 69f);
		mWindow.rLineTo(0, floorO * 22f);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(0, -floorO * 22f);

		mSketch.rLineTo(0, -floorO * 3f);
		mSketch.rLineTo(floorO * 2f, 0);
		mSketch.rLineTo(0, floorO * 3f);
		mSketch.rLineTo(floorO * 2f, 0);
		mWindow.moveTo(left + (floorO * 52.5f), floorO * 69f);
		mWindow.rLineTo(0, floorO * 22f);
		mWindow.rLineTo(floorO * 2f, 0);
		mWindow.rLineTo(0, -floorO * 22f);

		mSketch.rLineTo(0, -floorO * 7f);
		mSketch.rLineTo(floorO * 6f, 0);
		mSketch.rLineTo(0, floorO * 7f);
		mWindow.addRect(left + (floorO * 56f), floorO * 72f, left + (floorO * 58f), floorO * 76f, Direction.CW);

		mSketch.rLineTo(floorO * 9.5f, 0);
		mWindow.moveTo(left + (floorO * 60.5f), floorO * 69f);
		mWindow.rLineTo(0, floorO * 26f);
		mWindow.rLineTo(floorO * 3.5f, 0);
		mWindow.rLineTo(0, -floorO * 15f);
		mWindow.rLineTo(floorO * 6f, 0);
		mWindow.rLineTo(0, -floorO * 11f);

		mSketch.rLineTo(0, -floorO * 3f);
		mSketch.rLineTo(floorO * 6f, 0);
		mSketch.rLineTo(0, floorO * 26f);
		mSketch.rLineTo(floorO * 4f, 0);
		mSketch.rLineTo(0, -floorO * 15f);
		mSketch.rLineTo(floorO * 10f, 0);
		mSketch.rLineTo(0, floorO * 6f);
		mWindow.addRect(left + (floorO * 82f), floorO * 77f, left + (floorO * 88f), floorO * 80f, Direction.CW);

		mSketch.rLineTo(floorO * 8f, 0);
		mSketch.rLineTo(0, floorO * 3f);
		mWindow.addRect(left + (floorO * 92f), floorO * 87.5f, left + (floorO * 93f), floorO * 91f, Direction.CW);
		mWindow.addRect(left + (floorO * 96f), floorO * 85.5f, left + (floorO * 97f), floorO * 87.5f, Direction.CW);

		mSketch.rLineTo(floorO * 4f, 0);
		mSketch.rLineTo(0, -floorO * 13f);
		mSketch.rLineTo(floorO * 10f, 0);
		mSketch.rLineTo(0, -floorO * 18f);
		mSketch.rLineTo(floorO * 8f, 0);
		mSketch.rLineTo(0, -floorO * 17f);
		mSketch.rLineTo(floorO * 6f, 0);
		mSketch.rLineTo(0, floorO * 25f);
		mSketch.rLineTo(floorO * 7f, 0);
		mSketch.rLineTo(0, floorO * 14f);
		mWindow.addRect(left + (floorO * 115f), floorO * 61f, left + (floorO * 117f), floorO * 65f, Direction.CW);
		mWindow.addRect(left + (floorO * 117f), floorO * 72f, left + (floorO * 119.5f), floorO * 77f, Direction.CW);
		mWindow.addRect(left + (floorO * 128f), floorO * 68f, left + (floorO * 130f), floorO * 72f, Direction.CW);

		mSketch.rLineTo(floorO * 3f, 0);
		mSketch.rLineTo(0, -floorO * 28f);
		mSketch.rLineTo(floorO * 10f, 0);
		mSketch.rLineTo(0, -floorO * 6f);
		mSketch.rLineTo(floorO * 6f, 0);
		mSketch.rLineTo(0, floorO * 19f);
		mWindow.addRect(left + (floorO * 140f), floorO * 54f, left + (floorO * 142f), floorO * 58f, Direction.CW);

		mSketch.rLineTo(floorO * 4f, 0);
		mSketch.rLineTo(0, -floorO * 31f);
		mSketch.rLineTo(floorO, 0);
		mSketch.rLineTo(0, -floorO);
		mSketch.rLineTo(floorO, 0);
		mSketch.rLineTo(0, -floorO);
		mSketch.rLineTo(floorO, 0);
		mSketch.rLineTo(0, -floorO);
		mSketch.rLineTo(floorO * 2f, 0);
		mSketch.rLineTo(0, floorO);
		mSketch.rLineTo(floorO, 0);
		mSketch.rLineTo(0, floorO);
		mSketch.rLineTo(floorO, 0);
		mSketch.rLineTo(0, floorO);
		mSketch.rLineTo(floorO, 0);
		mSketch.rLineTo(0, floorO * 24f);
		mSketch.rLineTo(floorO * 6f, 0);
		mSketch.rLineTo(0, floorO * 16f);
		mWindow.addRect(left + (floorO * 159f), floorO * 32f, left + (floorO * 161f), floorO * 37f, Direction.CW);
		mWindow.addRect(left + (floorO * 159f), floorO * 42f, left + (floorO * 161f), floorO * 48f, Direction.CW);

		mSketch.rLineTo(floorO * 8f, 0);
		mWindow.addRect(left + (floorO * 170f), floorO * 72f, left + (floorO * 178f), floorO * 80f, Direction.CW);

		mSketch.rLineTo(0, -floorO * 9f);
		mSketch.rLineTo(floorO * 7f, -floorO * 7f);
		mSketch.rLineTo(0, floorO * 7f);
		mSketch.rLineTo(floorO * 7f, -floorO * 7f);
		mSketch.rLineTo(0, -floorO * 11f);
		mSketch.rLineTo(floorO * 9.5f, 0);
		mSketch.rLineTo(0, floorO * 27f);
		mWindow.addRect(left + (floorO * 198f), floorO * 48f, left + (floorO * 200f), floorO * 54f, Direction.CW);

		mSketch.rLineTo(floorO * 8f, 0);
		mWindow.moveTo(left + (floorO * 201.5f), floorO * 72f);
		mWindow.rLineTo(floorO * 8f, 0);
		mWindow.rLineTo(0, floorO * 10f);
		mWindow.rLineTo(-floorO * 8f, floorO * 6f);

		mSketch.rLineTo(0, -floorO * 12f);
		mSketch.rLineTo(floorO * 8.5f, 0);
		mSketch.rLineTo(0, floorO * 22f);
		mWindow.addRect(left + (floorO * 214f), floorO * 64f, left + (floorO * 216f), floorO * 67f, Direction.CW);

		mSketch.rLineTo(floorO * 5f, floorO * 5f);
		mSketch.rLineTo(0, -floorO * 29f);
		mSketch.rLineTo(floorO * 8.5f, 0);
		mSketch.rLineTo(0, floorO * 15f);
		mWindow.addRect(left + (floorO * 224f), floorO * 61f, left + (floorO * 226f), floorO * 65f, Direction.CW);
		mWindow.addRect(left + (floorO * 228f), floorO * 68f, left + (floorO * 230f), floorO * 72f, Direction.CW);

		mSketch.rLineTo(floorO * 6.5f, 0);
		mSketch.rLineTo(0, floorO * 14f);
		mWindow.addRect(left + (floorO * 231.5f), floorO * 80f, left + (floorO * 233.5f), floorO * 85f, Direction.CW);

		mSketch.rLineTo(floorO * 2f, 0);
		mSketch.rLineTo(0, -floorO * 31f);
		mSketch.rLineTo(floorO * 40f, 0);
		mSketch.rLineTo(0, floorO * 18f);
		mWindow.addRect(left + (floorO * 246f), floorO * 58f, left + (floorO * 248f), floorO * 63f, Direction.CW);
		mWindow.addRect(left + (floorO * 254f), floorO * 72f, left + (floorO * 255f), floorO * 76f, Direction.CW);
		for (int i = 0; i < 3; i++) {
			mWindow.addRect(left + (floorO * 274f), (floorO * 54f) + (floorO * 7f * i), left + (floorO * 276f),
					(floorO * 58f) + (floorO * 7f * i), Direction.CW);
		}

		mSketch.rLineTo(floorO * 9.5f, 0);
		mSketch.rLineTo(0, floorO * 10f);
		mSketch.rLineTo(floorO * 3.5f, 0);
		mSketch.rLineTo(0, -floorO * 2f);
		mSketch.rLineTo(floorO * 2f, 0);
		mSketch.rLineTo(0, -floorO * 17f);
		mSketch.rLineTo(floorO * 5f, 0);
		mSketch.rLineTo(0, floorO * 9f);
		mWindow.addRect(left + (floorO * 297f), floorO * 67f, left + (floorO * 299f), floorO * 71f, Direction.CW);

		mSketch.rLineTo(floorO * 4f, 0);
		mSketch.rLineTo(0, -floorO * 22f);
		mSketch.rLineTo(floorO * 6f, 0);
		mSketch.rLineTo(0, floorO * 4f);
		mSketch.rLineTo(floorO, 0);
		mSketch.rLineTo(0, -floorO * 4f);
		mSketch.rLineTo(floorO * 6f, 0);
		mSketch.rLineTo(0, floorO * 4f);
		mSketch.rLineTo(floorO, 0);
		mSketch.rLineTo(0, -floorO * 4f);
		mSketch.rLineTo(floorO * 6f, 0);
		mSketch.rLineTo(0, floorO * 50f);

		for (int i = 0; i < 3; i++) {
			mWindow.addRect(left + (floorO * 305f) + (floorO * 7f * i), floorO * 56f, left + (floorO * 306f)
					+ (floorO * 7f * i), floorO * 76f, Direction.CW);
			mWindow.addRect(left + (floorO * 308f) + (floorO * 7f * i), floorO * 56f, left + (floorO * 309f)
					+ (floorO * 7f * i), floorO * 76f, Direction.CW);
		}

		mSketch.close();
		mWindow.close();

		canvas.drawPath(mSketch, mBack70Paint);
		canvas.drawPath(mWindow, mWindowPaint);

		mSketch.reset();
		mSketch.addCircle(left + (floorO * 107f), floorO * 72f, floorO * 4f, Direction.CW);
		mSketch.addCircle(left + (floorO * 250f), floorO * 54f, floorO * 10f, Direction.CW);
		mSketch.addCircle(left + (floorO * 270f), floorO * 54f, floorO * 10f, Direction.CW);

		mSketch.close();

		canvas.drawPath(mSketch, mBack70Paint);

	}

	private void drawBack85(Canvas canvas, float left) {
		float floorO = Util.PIXEL * Util.MULTI;
		mSketch.reset();
		mWindow.reset();

		for (int i = 0; i < 4; i++) {
			mSketch.moveTo(left + (floorO * 4f) + (floorO * 4f * i), floorO * 87.5f);
			mSketch.rLineTo(floorO * 4f, floorO * 3.5f);
			mSketch.rLineTo(0, floorO * 9f);
			mSketch.rLineTo(-floorO * 4f, 0);

			mWindow.moveTo(left + (floorO * 6.5f) + (floorO * 4f * i), floorO * 92.5f);
			mWindow.rLineTo(floorO, 0);
			mWindow.rLineTo(0, floorO * 2.5f);
			mWindow.rLineTo(-floorO, 0);
		}
		mSketch.moveTo(left + (floorO * 68f), floorO * 85f);
		mSketch.rLineTo(floorO * 4f, 0);
		mSketch.rLineTo(0, floorO * 7.5f);
		mSketch.rLineTo(floorO * 2f, 0);
		mSketch.rLineTo(0, floorO * 7.5f);
		mSketch.rLineTo(-floorO * 6f, 0);
		mWindow.addRect(left + (floorO * 69f), floorO * 87.5f, left + (floorO * 70f), floorO * 91f, Direction.CW);

		mSketch.moveTo(left + (floorO * 110f), floorO * 100f);
		mSketch.rLineTo(0, -floorO * 23f);
		mSketch.rLineTo(floorO * 5f, 0);
		mSketch.rLineTo(0, floorO * 10f);
		mSketch.rLineTo(floorO * 3f, 0);
		mSketch.rLineTo(0, floorO * 3f);
		mWindow.addRect(left + (floorO * 111f), floorO * 80f, left + (floorO * 112f), floorO * 85f, Direction.CW);

		mSketch.rLineTo(floorO * 1.5f, 0);
		mSketch.rLineTo(0, -floorO * 8f);
		mSketch.rLineTo(floorO * 5.5f, 0);
		mSketch.rLineTo(0, floorO * 8f);
		mWindow.addRect(left + (floorO * 123f), floorO * 85f, left + (floorO * 124f), floorO * 90f, Direction.CW);

		mSketch.rLineTo(floorO * 14f, 0);
		mSketch.rLineTo(0, -floorO * 18f);
		mSketch.rLineTo(floorO * 2f, 0);
		mSketch.rLineTo(0, -floorO * 9f);
		mSketch.rLineTo(floorO * 6f, 0);
		mSketch.rLineTo(0, floorO * 27f);
		mSketch.rLineTo(floorO, 0);
		mWindow.addRect(left + (floorO * 142f), floorO * 77f, left + (floorO * 144f), floorO * 80f, Direction.CW);
		mWindow.addRect(left + (floorO * 144f), floorO * 68f, left + (floorO * 146f), floorO * 70.5f, Direction.CW);

		mSketch.rLineTo(0, -floorO * 14f);
		mSketch.rLineTo(floorO * 4f, 0);
		mSketch.rLineTo(0, floorO * 14f);
		mSketch.rLineTo(floorO * 5f, 0);
		mSketch.rLineTo(0, -floorO * 18f);
		mSketch.rLineTo(floorO * 6f, 0);
		mSketch.rLineTo(0, floorO * 28f);
		mWindow.addRect(left + (floorO * 158f), floorO * 77f, left + (floorO * 159f), floorO * 80f, Direction.CW);
		mWindow.addRect(left + (floorO * 158f), floorO * 85f, left + (floorO * 159f), floorO * 87.5f, Direction.CW);

		mSketch.rLineTo(-floorO * 6f, 0);
		mSketch.rLineTo(0, -floorO * 9f);
		mSketch.rLineTo(-floorO * 5f, 0);
		mSketch.rLineTo(0, floorO * 9f);
		mSketch.rLineTo(-floorO * 4f, 0);
		mSketch.rLineTo(0, -floorO * 9f);
		mSketch.rLineTo(-floorO, 0);
		mSketch.rLineTo(0, floorO * 9f);
		mSketch.rLineTo(-floorO * 8f, 0);
		mSketch.rLineTo(0, -floorO * 9f);
		mSketch.rLineTo(-floorO * 14f, 0);
		mSketch.rLineTo(0, floorO * 9f);
		mSketch.rLineTo(-floorO * 5.5f, 0);
		mSketch.rLineTo(0, -floorO * 9f);
		mSketch.rLineTo(-floorO * 1.5f, 0);
		mSketch.rLineTo(0, floorO * 9f);

		mSketch.addRect(left + (floorO * 178f), floorO * 91f, left + (floorO * 182f), floorO * 100f, Direction.CW);
		mSketch.addRect(left + (floorO * 185f), floorO * 82f, left + (floorO * 191f), floorO * 100f, Direction.CW);
		mWindow.addRect(left + (floorO * 189f), floorO * 85f, left + (floorO * 190f), floorO * 87.5f, Direction.CW);

		mSketch.addRect(left + (floorO * 248f), floorO * 80f, left + (floorO * 254f), floorO * 100f, Direction.CW);
		mWindow.addRect(left + (floorO * 250f), floorO * 82f, left + (floorO * 251f), floorO * 85f, Direction.CW);

		mSketch.addRect(left + (floorO * 255f), floorO * 80f, left + (floorO * 260f), floorO * 100f, Direction.CW);
		mWindow.addRect(left + (floorO * 258f), floorO * 87f, left + (floorO * 259f), floorO * 90f, Direction.CW);

		mSketch.addRect(left + (floorO * 289.5f), floorO * 85f, left + (floorO * 293f), floorO * 100f, Direction.CW);

		mSketch.moveTo(left + (floorO * 297f), floorO * 91f);
		mSketch.rLineTo(floorO * 3f, -floorO * 3.5f);
		mSketch.rLineTo(0, floorO * 12.5f);
		mSketch.rLineTo(-floorO * 3f, 0);

		mSketch.moveTo(left + (floorO * 304f), floorO * 74f);
		mSketch.rLineTo(floorO * 3f, 0);
		mSketch.rLineTo(0, floorO * 13f);
		mSketch.rLineTo(floorO * 3f, 0);
		mSketch.rLineTo(0, -floorO * 7f);
		mSketch.rLineTo(floorO * 5f, 0);
		mSketch.rLineTo(0, floorO * 20f);
		mSketch.rLineTo(-floorO * 11f, 0);
		mWindow.addRect(left + (floorO * 305f), floorO * 87f, left + (floorO * 306f), floorO * 88f, Direction.CW);
		mWindow.addRect(left + (floorO * 305f), floorO * 90f, left + (floorO * 306f), floorO * 91f, Direction.CW);
		mWindow.addRect(left + (floorO * 312f), floorO * 82f, left + (floorO * 313f), floorO * 85f, Direction.CW);

		mSketch.moveTo(left + (floorO * 317f), floorO * 85f);
		mSketch.rLineTo(floorO * 2f, 0);
		mSketch.rLineTo(0, -floorO * 5f);
		mSketch.rLineTo(floorO * 5f, 0);
		mSketch.rLineTo(0, floorO * 20f);
		mSketch.rLineTo(-floorO * 7f, 0);
		mWindow.addRect(left + (floorO * 318f), floorO * 91f, left + (floorO * 319f), floorO * 92.5f, Direction.CW);
		mWindow.addRect(left + (floorO * 322f), floorO * 82f, left + (floorO * 323f), floorO * 85f, Direction.CW);
		mWindow.addRect(left + (floorO * 322f), floorO * 87f, left + (floorO * 323f), floorO * 90f, Direction.CW);

		mSketch.close();
		mWindow.close();

		canvas.drawPath(mSketch, mBack85Paint);
		canvas.drawPath(mWindow, mBack70Paint);
	}

	private void drawSkmA4(Canvas canvas, float right) {
		float floorO = Util.PIXEL * Util.MULTI;
		float center = right - (floorO * 17.5f);
		float tmpT = floorO * 61f;

		mSketch.reset();
		mWindow.reset();

		// Draw top 1
		mSketch.addRect(center - (floorO * 17.5f), tmpT, center + (floorO * 17.5f), tmpT += floorO / 2f, Direction.CW);
		mWindow.addRect(center - (floorO * 17f), tmpT - (floorO / 4f), center + (floorO * 17f), tmpT + (floorO / 2f)
				+ (floorO / 4f), Direction.CW);
		// Draw top 2
		mSketch.addRect(center - (floorO * 17f), tmpT += floorO / 2f, center + (floorO * 17f), tmpT += floorO / 2f,
				Direction.CW);
		mSketch.addRect(center - (floorO * 16.5f), tmpT, center + (floorO * 16.5f), tmpT += floorO * 2f, Direction.CW);
		// Draw top 3 & 5
		for (int i = 0; i < 2; i++) {
			tmpT += floorO * 12f * i;
			mSketch.addRect(center - (floorO * 17f), tmpT, center + (floorO * 17f), tmpT += floorO / 2f, Direction.CW);
			mWindow.addRect(center - (floorO * 16.5f), tmpT, center + (floorO * 16.5f), tmpT + (floorO * 2.75f),
					Direction.CW);
			mSketch.addRect(center - (floorO / 4f), tmpT, center + (floorO / 4f), tmpT + (floorO * 2.75f), Direction.CW);
			for (int j = 1; j < 12; j++) {
				mSketch.addRect((center - (floorO / 4f)) + (floorO * 1.5f * j), tmpT, center + (floorO / 4f)
						+ (floorO * 1.5f * j), tmpT + (floorO * 2.75f), Direction.CW);
				mSketch.addRect((center + (floorO / 4f)) - (floorO * 1.5f * j), tmpT, center - (floorO / 4f)
						- (floorO * 1.5f * j), tmpT + (floorO * 2.75f), Direction.CCW);
			}
			for (int j = 0; j < 3; j++) {
				mSketch.addRect(center - (floorO * 16.5f), tmpT += floorO / 2f, center + (floorO * 16.5f),
						tmpT += floorO / 4f, Direction.CW);
			}
			mSketch.addRect(center - (floorO * 17f), tmpT += floorO / 2f, center + (floorO * 17f), tmpT += floorO / 2f,
					Direction.CW);
		}
		// Draw top 4
		mWindow.addRect(center - (floorO * 16.5f), tmpT -= floorO * 15.75f, center + (floorO * 16.5f), tmpT
				+ (floorO * 12f), Direction.CW);

		for (int i = 0; i < 3; i++) {
			mSketch.addRect(center - (floorO * 16.5f), tmpT + (floorO * 2.75f) + (floorO * 3f * i), center
					+ (floorO * 16.5f), tmpT + (floorO * 3f) + (floorO * 3f * i), Direction.CW);
		}

		mSketch.addRect(center - (floorO * 16.5f), tmpT, center - (floorO * 16f), tmpT + (floorO * 12f), Direction.CW);
		mSketch.addRect(center + (floorO * 16.5f), tmpT, center + (floorO * 16f), tmpT + (floorO * 12f), Direction.CCW);
		// F
		mSketch.addRect(center - (floorO * 15f), tmpT, center - (floorO * 13f), tmpT + (floorO * 12f), Direction.CW);
		mSketch.addRect(center - (floorO * 13f), tmpT, center - (floorO * 7f), tmpT + (floorO * 3f), Direction.CW);
		mSketch.addRect(center - (floorO * 13f), tmpT + (floorO * 6f), center - (floorO * 9f), tmpT + (floorO * 9f),
				Direction.CW);
		// U
		mSketch.addRect(center - (floorO * 4f), tmpT, center - (floorO * 2f), tmpT + (floorO * 12f), Direction.CW);
		mSketch.addRect(center + (floorO * 4f), tmpT, center + (floorO * 2f), tmpT + (floorO * 12f), Direction.CCW);
		mSketch.addRect(center - (floorO * 2f), tmpT + (floorO * 9f), center + (floorO * 2f), tmpT + (floorO * 12f),
				Direction.CW);
		// N
		mSketch.addRect(center + (floorO * 6f), tmpT, center + (floorO * 8f), tmpT + (floorO * 12f), Direction.CW);
		for (int i = 0; i < 4; i++) {
			mSketch.addRect(center + (floorO * 8f) + (floorO * i), tmpT + (floorO * 3f * i), center + (floorO * 10f)
					+ (floorO * i), tmpT + (floorO * 3f) + (floorO * 3f * i), Direction.CW);
		}
		mSketch.addRect(center + (floorO * 13f), tmpT, center + (floorO * 15f), tmpT += (floorO * 12f), Direction.CW);
		// Draw Top 6
		mSketch.addRect(center - (floorO * 16.5f), tmpT += floorO * 3.75f, center - (floorO * 15.5f), tmpT
				+ (floorO * 3f), Direction.CW);
		mSketch.addRect(center + (floorO * 16.5f), tmpT, center + (floorO * 15.5f), tmpT + (floorO * 3f), Direction.CCW);
		mSketch.addRect(center - floorO, tmpT, center - (floorO / 4f), tmpT + (floorO * 3f), Direction.CW);
		mSketch.addRect(center + floorO, tmpT, center + (floorO / 4f), tmpT + (floorO * 3f), Direction.CCW);

		for (int i = 0; i < 14; i++) {
			mSketch.addRect((center - (floorO * 1.5f)) - (floorO * i), tmpT, (center - (floorO * 2f)) - (floorO * i),
					tmpT + (floorO * 3f), Direction.CW);
			mSketch.addRect((center + (floorO * 1.5f)) + (floorO * i), tmpT, (center + (floorO * 2f)) + (floorO * i),
					tmpT + (floorO * 3f), Direction.CCW);
		}
		mWindow.addRect(center - (floorO * 16f), tmpT, center + (floorO * 16f), tmpT += (floorO * 3f), Direction.CW);
		mSketch.addRect(center - (floorO * 16.5f), tmpT, center + (floorO * 16.5f), tmpT += (floorO / 2f), Direction.CW);

		mWindow.addRect(center - (floorO * 16.5f), tmpT, center + (floorO * 16.5f), tmpT + (floorO * 1.5f),
				Direction.CW);
		mSketch.addRect(center - (floorO * 17f), tmpT += floorO / 2f, center + (floorO * 17f), tmpT += floorO / 2f,
				Direction.CW);
		// Draw Top 7
		mWindow.addRect(center - (floorO * 16f), tmpT += floorO / 2f, center + (floorO * 16f), tmpT + (floorO * 11f),
				Direction.CW);
		mSketch.addRect(center - (floorO * 16.5f), tmpT, center + (floorO * 16.5f), tmpT += floorO, Direction.CW);
		mSketch.addRect(center - (floorO * 16.5f), tmpT + (floorO * 3.5f), center + (floorO * 16.5f), tmpT
				+ (floorO * 4.5f), Direction.CW);

		mSketch.addRect(center - (floorO * 16.5f), tmpT, center - (floorO * 16f), tmpT + (floorO * 10f), Direction.CW);
		mSketch.addRect(center + (floorO * 16.5f), tmpT, center + (floorO * 16f), tmpT + (floorO * 10f), Direction.CCW);
		for (int i = 0; i < 4; i++) {
			mSketch.addRect((center - (floorO * 16f)) + (floorO * 8f * i), tmpT, (center - (floorO * 15.5f))
					+ (floorO * 8f * i), tmpT + (floorO * 10f), Direction.CW);
			mSketch.addRect((center - (floorO * 13.5f)) + (floorO * 8f * i), tmpT, (center - (floorO * 13f))
					+ (floorO * 8f * i), tmpT + (floorO * 10f), Direction.CW);
			mSketch.addRect((center - (floorO * 11f)) + (floorO * 8f * i), tmpT, (center - (floorO * 10.5f))
					+ (floorO * 8f * i), tmpT + (floorO * 10f), Direction.CW);
			mSketch.addRect((center - (floorO * 8.5f)) + (floorO * 8f * i), tmpT, (center - (floorO * 8f))
					+ (floorO * 8f * i), tmpT + (floorO * 10f), Direction.CW);
		}

		mSketch.close();
		mWindow.close();

		canvas.drawPath(mWindow, mWindowPaint);
		canvas.drawPath(mSketch, mTitlePaint);
	}

	private void drawSkmBridge(Canvas canvas, float right) {
		float floorO = Util.PIXEL * Util.MULTI;
		float center = right - (floorO * 14f);
		float tmpT = floorO * 89f;

		mSketch.reset();

		mSketch.addRect(center - (floorO * 14f), tmpT, right, tmpT += floorO, Direction.CW);
		mSketch.addRect(center - (floorO * 14f), tmpT += floorO * 2f, right, tmpT += floorO / 4f, Direction.CW);

		for (int i = 0; i < 19; i++) {
			mSketch.addRect(center - (floorO / 4f) - (floorO * 0.75f * i), tmpT, center - (floorO / 2f)
					- (floorO * 0.75f * i), tmpT + (floorO * 1.5f), Direction.CW);
			mSketch.addRect(center + (floorO / 4f) + (floorO * 0.75f * i), tmpT, center + (floorO / 2f)
					+ (floorO * 0.75f * i), tmpT + (floorO * 1.5f), Direction.CCW);
		}
		mSketch.addRect(center - (floorO * 14f), tmpT += floorO * 1.5f, right, tmpT += floorO, Direction.CW);

		mSketch.close();

		canvas.drawPath(mSketch, mTitlePaint);
	}

	private void drawSkmA8(Canvas canvas, float right) {
		float floorO = Util.PIXEL * Util.MULTI;
		float center = right - (floorO * 36.5f);
		float tmpT = floorO * 66.5f;

		mSketch.reset();
		mWindow.reset();

		// Draw A8 top
		mSketch.addRect(center - (floorO * 36.5f), tmpT, center - floorO, tmpT + (floorO * 18f), Direction.CW);
		mSketch.addRect(center + (floorO * 36.5f), tmpT, center + floorO, tmpT + (floorO * 18f), Direction.CW);
		mSketch.addRect(center - floorO, tmpT + (floorO * 4f), center + floorO, tmpT + (floorO * 18f), Direction.CW);

		// Draw right/left bridge mWindow
		mWindow.addRect(center - (floorO * 2f), tmpT += floorO, center - (floorO * 4f), tmpT + (floorO * 2f),
				Direction.CCW);
		mWindow.addRect(center - (floorO * 4.5f), tmpT, center - (floorO * 6f), tmpT + (floorO * 2f), Direction.CCW);
		mWindow.addRect(center - (floorO * 6.5f), tmpT, center - (floorO * 8.5f), tmpT + (floorO * 2f), Direction.CCW);
		mWindow.addRect(center - (floorO * 9f), tmpT, center - (floorO * 10.5f), tmpT + (floorO * 2f), Direction.CCW);
		mWindow.addRect(center - (floorO * 11f), tmpT, center - (floorO * 12.5f), tmpT + (floorO * 2f), Direction.CCW);
		mWindow.addRect(center - (floorO * 13f), tmpT, center - (floorO * 15f), tmpT + (floorO * 2f), Direction.CCW);
		mWindow.addRect(center - (floorO * 15.5f), tmpT, center - (floorO * 17f), tmpT + (floorO * 2f), Direction.CCW);
		mWindow.addRect(center - (floorO * 17.5f), tmpT, center - (floorO * 19f), tmpT + (floorO * 2f), Direction.CCW);
		mWindow.addRect(center - (floorO * 19.5f), tmpT, center - (floorO * 21.5f), tmpT + (floorO * 2f), Direction.CCW);
		mWindow.addRect(center - (floorO * 22f), tmpT, center - (floorO * 23.5f), tmpT + (floorO * 2f), Direction.CCW);
		mWindow.addRect(center - (floorO * 24f), tmpT, center - (floorO * 25.5f), tmpT + (floorO * 2f), Direction.CCW);
		mWindow.addRect(center - (floorO * 26f), tmpT, center - (floorO * 27.5f), tmpT + (floorO * 2f), Direction.CCW);
		mWindow.addRect(center - (floorO * 28f), tmpT, center - (floorO * 29.5f), tmpT + (floorO * 2f), Direction.CCW);
		mWindow.addRect(center - (floorO * 30f), tmpT, center - (floorO * 32.5f), tmpT + (floorO * 2f), Direction.CCW);
		mWindow.addRect(center - (floorO * 33.5f), tmpT, center - (floorO * 35.5f), tmpT + (floorO * 2f), Direction.CCW);

		mWindow.addRect(center + (floorO * 2f), tmpT, center + (floorO * 4), tmpT + (floorO * 2f), Direction.CW);
		mWindow.addRect(center + (floorO * 4.5f), tmpT, center + (floorO * 6f), tmpT + (floorO * 2f), Direction.CW);
		mWindow.addRect(center + (floorO * 6.5f), tmpT, center + (floorO * 8.5f), tmpT + (floorO * 2f), Direction.CW);
		mWindow.addRect(center + (floorO * 9f), tmpT, center + (floorO * 10.5f), tmpT + (floorO * 2f), Direction.CW);
		mWindow.addRect(center + (floorO * 11f), tmpT, center + (floorO * 12.5f), tmpT + (floorO * 2f), Direction.CW);
		mWindow.addRect(center + (floorO * 13f), tmpT, center + (floorO * 15f), tmpT + (floorO * 2f), Direction.CW);
		mWindow.addRect(center + (floorO * 15.5f), tmpT, center + (floorO * 17f), tmpT + (floorO * 2f), Direction.CW);
		mWindow.addRect(center + (floorO * 17.5f), tmpT, center + (floorO * 19f), tmpT + (floorO * 2f), Direction.CW);
		mWindow.addRect(center + (floorO * 19.5f), tmpT, center + (floorO * 21.5f), tmpT + (floorO * 2f), Direction.CW);
		mWindow.addRect(center + (floorO * 22f), tmpT, center + (floorO * 23.5f), tmpT + (floorO * 2f), Direction.CW);
		mWindow.addRect(center + (floorO * 24f), tmpT, center + (floorO * 25.5f), tmpT + (floorO * 2f), Direction.CW);
		mWindow.addRect(center + (floorO * 26f), tmpT, center + (floorO * 27.5f), tmpT + (floorO * 2f), Direction.CW);
		mWindow.addRect(center + (floorO * 28f), tmpT, center + (floorO * 29.5f), tmpT + (floorO * 2f), Direction.CW);
		mWindow.addRect(center + (floorO * 30f), tmpT, center + (floorO * 32.5f), tmpT + (floorO * 2f), Direction.CW);
		mWindow.addRect(center + (floorO * 33.5f), tmpT, center + (floorO * 35.5f), tmpT += (floorO * 2f), Direction.CW);

		mWindow.addRect(center - floorO, tmpT += floorO, center - (floorO * 35.5f), tmpT + (floorO / 2f), Direction.CCW);
		mWindow.addRect(center + floorO, tmpT, center + (floorO * 35.5f), tmpT + (floorO / 2f), Direction.CW);

		mWindow.addRect(center - (floorO * 14f), tmpT += floorO / 2f, center - (floorO * 15f), tmpT + (floorO * 1.5f),
				Direction.CCW);
		mWindow.addRect(center + (floorO * 14f), tmpT, center + (floorO * 15f), tmpT + (floorO * 1.5f), Direction.CW);
		for (int i = 0; i < 4; i++) {
			mWindow.addRect(center - (floorO * 15.5f) - (floorO * i), tmpT, center - (floorO * 16f) - (floorO * i),
					tmpT + (floorO * 1.5f), Direction.CCW);
			mWindow.addRect(center + (floorO * 15.5f) + (floorO * i), tmpT, center + (floorO * 16f) + (floorO * i),
					tmpT + (floorO * 1.5f), Direction.CW);
		}
		mWindow.addRect(center - (floorO * 19.5f), tmpT, center - (floorO * 20.5f), tmpT + (floorO * 1.5f),
				Direction.CCW);
		mWindow.addRect(center + (floorO * 19.5f), tmpT, center + (floorO * 20.5f), tmpT + (floorO * 1.5f),
				Direction.CW);

		for (int i = 0; i < 4; i++) {
			for (int k = 0; k < 2; k++) {
				mWindow.addRect((center - (floorO * 14f)) + (floorO * 34.5f * k), tmpT + (floorO * 2f)
						+ (floorO * 3f * i), (center - (floorO * 15f)) + (floorO * 34.5f * k), tmpT + (floorO * 4.5f)
						+ (floorO * 3f * i), Direction.CCW);
				for (int j = 0; j < 4; j++) {
					mWindow.addRect((center - (floorO * 15.5f) - (floorO * j)) + (floorO * 34.5f * k), tmpT
							+ (floorO * 2f) + (floorO * 3f * i), (center - (floorO * 16f) - (floorO * j))
							+ (floorO * 34.5f * k), tmpT + (floorO * 4.5f) + (floorO * 3f * i), Direction.CCW);
				}
				mWindow.addRect((center - (floorO * 19.5f)) + (floorO * 34.5f * k), tmpT + (floorO * 2f)
						+ (floorO * 3f * i), (center - (floorO * 20.5f)) + (floorO * 34.5f * k), tmpT + (floorO * 4.5f)
						+ (floorO * 3f * i), Direction.CCW);
			}
		}

		mWindow.addRect(center - floorO, tmpT, center + floorO, tmpT + (floorO * 1.5f), Direction.CW);
		mWindow.addRect(center - (floorO * 35.5f), tmpT, center - (floorO * 33.5f), tmpT + (floorO * 1.5f),
				Direction.CW);
		mWindow.addRect(center + (floorO * 35.5f), tmpT, center + (floorO * 33.5f), tmpT + (floorO * 1.5f),
				Direction.CCW);

		for (int i = 0; i < 4; i++) {
			mWindow.addRect(center - floorO, tmpT + (floorO * 2f) + (floorO * 3f * i), center + floorO, tmpT
					+ (floorO * 4.5f) + (floorO * 3f * i), Direction.CW);
			mWindow.addRect(center - (floorO * 35.5f), tmpT + (floorO * 2f) + (floorO * 3f * i), center
					- (floorO * 33.5f), tmpT + (floorO * 4.5f) + (floorO * 3f * i), Direction.CW);
			mWindow.addRect(center + (floorO * 35.5f), tmpT + (floorO * 2f) + (floorO * 3f * i), center
					+ (floorO * 33.5f), tmpT + (floorO * 4.5f) + (floorO * 3f * i), Direction.CCW);
		}

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 8; j++) {
				for (int k = 0; k < 2; k++) {
					mWindow.addRect((center - floorO - (floorO * 19.5f * i)) + (floorO * 34.5f * k), tmpT
							+ (floorO * 1.5f) + (floorO * 1.5f * j), (center - (floorO * 14f) - (floorO * 19.5f * i))
							+ (floorO * 34.5f * k), tmpT + (floorO * 1.5f) + (floorO / 2f) + (floorO * 1.5f * j),
							Direction.CCW);
				}
			}
		}
		// Draw A8 Main
		mSketch.moveTo(center, tmpT += floorO * 13.5f);
		mSketch.rLineTo(-floorO * 64.5f, 0);
		mSketch.rLineTo(0, -floorO * 4f);
		mSketch.rLineTo(-floorO * 3f, 0);
		mSketch.rLineTo(0, floorO * 19.5f);
		mSketch.rLineTo(floorO * 104f, 0);
		mSketch.rLineTo(0, -floorO * 15.5f);

		for (int i = 0; i < 2; i++) {
			mWindow.addRect((center - floorO) + (floorO * 34.5f * i), tmpT += floorO, (center + floorO)
					+ (floorO * 34.5f * i), tmpT += (floorO * 3.5f), Direction.CW);
			mWindow.addRect((center - floorO) + (floorO * 34.5f * i), tmpT += floorO, (center + floorO)
					+ (floorO * 34.5f * i), tmpT += (floorO * 3.5f), Direction.CW);
			mWindow.addRect((center - floorO) + (floorO * 34.5f * i), tmpT += floorO, (center + floorO)
					+ (floorO * 34.5f * i), tmpT += (floorO * 5.5f), Direction.CW);
			tmpT -= floorO * 15.5f;
		}
		tmpT += floorO;
		for (int i = 0; i < 3; i++) {
			float shift = 0;
			if (i == 1) {
				shift += floorO * 34.5f;
			} else if (i == 2) {
				shift += floorO * 66.5f;
			}
			mWindow.addRect((center + (floorO * 32.5f)) - shift, tmpT, (center + (floorO * 28.5f)) - shift, tmpT
					+ (floorO * 3.5f), Direction.CCW);
			mWindow.addRect((center + (floorO * 32.5f)) - shift, tmpT + (floorO * 4.5f), (center + (floorO * 28.5f))
					- shift, tmpT + (floorO * 8f), Direction.CCW);
			mWindow.addRect((center + (floorO * 32.5f)) - shift, tmpT + (floorO * 9f), (center + (floorO * 28.5f))
					- shift, tmpT + (floorO * 14.5f), Direction.CCW);

			mWindow.addRect((center + (floorO * 27.5f)) - shift, tmpT, (center + (floorO * 26.5f)) - shift,
					(tmpT + (floorO * 3.5f)), Direction.CCW);
			mWindow.addRect((center + (floorO * 27.5f)) - shift, tmpT + (floorO * 4.5f), (center + (floorO * 26.5f))
					- shift, tmpT + (floorO * 8f), Direction.CCW);
			mWindow.addRect((center + (floorO * 27.5f)) - shift, tmpT + (floorO * 9f), (center + (floorO * 26.5f))
					- shift, tmpT + (floorO * 14.5f), Direction.CCW);

			mWindow.addRect((center + (floorO * 26f)) - shift, tmpT, (center + (floorO * 22f)) - shift, tmpT
					+ (floorO * 3.5f), Direction.CCW);
			mWindow.addRect((center + (floorO * 26f)) - shift, tmpT + (floorO * 4.5f), (center + (floorO * 22f))
					- shift, tmpT + (floorO * 8f), Direction.CCW);
			mWindow.addRect((center + (floorO * 26f)) - shift, tmpT + (floorO * 9f), (center + (floorO * 22f)) - shift,
					tmpT + (floorO * 14.5f), Direction.CCW);

			mWindow.addRect((center + (floorO * 21.5f)) - shift, tmpT, (center + (floorO * 20.5f)) - shift, tmpT
					+ (floorO * 3.5f), Direction.CCW);
			mWindow.addRect((center + (floorO * 21.5f)) - shift, tmpT + (floorO * 4.5f), (center + (floorO * 20.5f))
					- shift, tmpT + (floorO * 8f), Direction.CCW);
			mWindow.addRect((center + (floorO * 21.5f)) - shift, tmpT + (floorO * 9f), (center + (floorO * 20.5f))
					- shift, tmpT + (floorO * 14.5f), Direction.CCW);

			mWindow.addRect((center + (floorO * 19.5f)) - shift, tmpT, (center + (floorO * 17.5f)) - shift, tmpT
					+ (floorO * 3.5f), Direction.CCW);
			mWindow.addRect((center + (floorO * 19.5f)) - shift, tmpT + (floorO * 4.5f), (center + (floorO * 17.5f))
					- shift, tmpT + (floorO * 8f), Direction.CCW);
			mWindow.addRect((center + (floorO * 19.5f)) - shift, tmpT + (floorO * 9f), (center + (floorO * 17.5f))
					- shift, tmpT + (floorO * 14.5f), Direction.CCW);

			mWindow.addRect((center + (floorO * 17f)) - shift, tmpT, (center + (floorO * 15f)) - shift, tmpT
					+ (floorO * 3.5f), Direction.CCW);
			mWindow.addRect((center + (floorO * 17f)) - shift, tmpT + (floorO * 4.5f), (center + (floorO * 15f))
					- shift, tmpT + (floorO * 8f), Direction.CCW);
			mWindow.addRect((center + (floorO * 17f)) - shift, tmpT + (floorO * 9f), (center + (floorO * 15f)) - shift,
					tmpT + (floorO * 14.5f), Direction.CCW);

			mWindow.addRect((center + (floorO * 14f)) - shift, tmpT, (center + (floorO * 13f)) - shift, tmpT
					+ (floorO * 3.5f), Direction.CCW);
			mWindow.addRect((center + (floorO * 14f)) - shift, tmpT + (floorO * 4.5f), (center + (floorO * 13f))
					- shift, tmpT + (floorO * 8f), Direction.CCW);
			mWindow.addRect((center + (floorO * 14f)) - shift, tmpT + (floorO * 9f), (center + (floorO * 13f)) - shift,
					tmpT + (floorO * 14.5f), Direction.CCW);

			mWindow.addRect((center + (floorO * 12.5f)) - shift, tmpT, (center + (floorO * 8.5f)) - shift, tmpT
					+ (floorO * 3.5f), Direction.CCW);
			mWindow.addRect((center + (floorO * 12.5f)) - shift, tmpT + (floorO * 4.5f), (center + (floorO * 8.5f))
					- shift, tmpT + (floorO * 8f), Direction.CCW);
			mWindow.addRect((center + (floorO * 12.5f)) - shift, tmpT + (floorO * 9f), (center + (floorO * 8.5f))
					- shift, tmpT + (floorO * 14.5f), Direction.CCW);

			mWindow.addRect((center + (floorO * 8f)) - shift, tmpT, (center + (floorO * 7f)) - shift, tmpT
					+ (floorO * 3.5f), Direction.CCW);
			mWindow.addRect((center + (floorO * 8f)) - shift, tmpT + (floorO * 4.5f), (center + (floorO * 7f)) - shift,
					tmpT + (floorO * 8f), Direction.CCW);
			mWindow.addRect((center + (floorO * 8f)) - shift, tmpT + (floorO * 9f), (center + (floorO * 7f)) - shift,
					tmpT + (floorO * 14.5f), Direction.CCW);

			mWindow.addRect((center + (floorO * 6f)) - shift, tmpT, (center + (floorO * 2f)) - shift, tmpT
					+ (floorO * 3.5f), Direction.CCW);
			mWindow.addRect((center + (floorO * 6f)) - shift, tmpT + (floorO * 4.5f), (center + (floorO * 2f)) - shift,
					tmpT + (floorO * 8f), Direction.CCW);
			mWindow.addRect((center + (floorO * 6f)) - shift, tmpT + (floorO * 9f), (center + (floorO * 2f)) - shift,
					tmpT + (floorO * 14.5f), Direction.CCW);
		}
		mWindow.addRect(center - (floorO * 66.5f), tmpT - (floorO * 3f), center - (floorO * 66f), tmpT
				+ (floorO * 14.5f), Direction.CW);

		mSketch.close();
		mWindow.close();

		canvas.drawPath(mSketch, mTitlePaint);
		canvas.drawPath(mWindow, mWindowPaint);

	}

	private void drawPresidentialHouse(Canvas canvas, float right) {
		float floorO = Util.PIXEL * Util.MULTI;
		float center = right - (floorO * 44.5f);
		float tmpT = floorO * 37f;

		mSketch.reset();
		mWindow.reset();

		// Top spear
		mSketch.moveTo(center, tmpT);
		mSketch.rLineTo((-floorO / 2f), (floorO * 9f));
		mSketch.rLineTo(floorO / 2f, floorO / 2f);
		mSketch.rLineTo(floorO / 2f, -floorO / 2f);

		// Top floor 1
		mSketch.moveTo(center, tmpT += floorO * 9f);
		// Downward
		mSketch.rLineTo((-floorO * 3.5f), (floorO * 3f));
		mSketch.rLineTo((-floorO / 2f), 0);
		mSketch.rLineTo((-floorO / 2f), floorO * 2f);
		mSketch.rLineTo((-floorO / 2f), 0);
		mSketch.rLineTo(0, floorO);
		mSketch.rLineTo(floorO * 3.5f, 0);
		mSketch.rLineTo(0, floorO * 1.5f);
		mSketch.rLineTo(-floorO * 3.5f, 0);
		mSketch.rLineTo(0, floorO * 1.5f);
		mSketch.rLineTo((-floorO / 2f), 0);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(floorO * 11, 0);
		// Backward
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo((-floorO / 2f), 0);
		mSketch.rLineTo(0, -floorO * 1.5f);
		mSketch.rLineTo(-floorO * 3.5f, 0);
		mSketch.rLineTo(0, -floorO * 1.5f);
		mSketch.rLineTo(floorO * 3.5f, 0);
		mSketch.rLineTo(0, -floorO);
		mSketch.rLineTo((-floorO / 2f), 0);
		mSketch.rLineTo((-floorO / 2f), -floorO * 2f);
		mSketch.rLineTo((-floorO / 2f), 0);
		mSketch.rLineTo((-floorO * 3.5f), -(floorO * 3f));

		// Draw Top floor 1 mWindow
		mWindow.moveTo(center, tmpT += (floorO * 3f));
		mWindow.addRect(center - floorO, tmpT, center - (floorO / 4f), tmpT + (floorO * 2f), Direction.CW);
		mWindow.addRect(center + (floorO / 4f), tmpT, center + floorO, tmpT + (floorO * 2f), Direction.CW);
		mWindow.addCircle(center, tmpT + (floorO * 3.5f), floorO, Direction.CW);
		mWindow.addRect(center - floorO, tmpT + (floorO * 3.5f), center + floorO, tmpT + (floorO * 5.5f), Direction.CW);
		// Top floor 1 side bar
		mWindow.addRect(center - (floorO * 5f), tmpT + (floorO * 3f), center - (floorO * 1.5f), tmpT + (floorO * 4.5f),
				Direction.CW);
		mWindow.addRect(center + (floorO * 1.5f), tmpT + (floorO * 3f), center + (floorO * 5f), tmpT + (floorO * 4.5f),
				Direction.CW);

		mSketch.close();
		mWindow.close();

		canvas.drawPath(mSketch, mTitlePaint);
		canvas.drawPath(mWindow, mWindowPaint);

		// Draw Top floor 1 mWindow's middle line & side bar
		mSketch.reset();

		mSketch.moveTo(center, tmpT += floorO * 3.5f);
		mSketch.addRect(center - (floorO * 5.5f), tmpT, center - (floorO * 2f), tmpT + (floorO / 2f), Direction.CW);
		mSketch.addRect(center + (floorO * 2f), tmpT, center + (floorO * 5.5f), tmpT + (floorO / 2f), Direction.CW);

		mSketch.close();
		canvas.drawPath(mSketch, mTitlePaint);

		mSketch.reset();
		mWindow.reset();

		// Draw Top floor 2
		mSketch.moveTo(center - (floorO * 5.5f), tmpT + (floorO * 3f));
		// Downward
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(0, floorO);
		mSketch.rLineTo(floorO / 2f, floorO);
		mSketch.rLineTo(-floorO / 2f, 0);
		mSketch.rLineTo(floorO / 2f, floorO / 2f);
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(floorO * 10f, 0);
		// Backward
		mSketch.rLineTo(-floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(floorO / 2f, -floorO / 2f);
		mSketch.rLineTo(-floorO / 2f, 0);
		mSketch.rLineTo(floorO / 2f, -floorO);
		mSketch.rLineTo(0, -floorO);
		mSketch.rLineTo(-floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(floorO / 4f, -floorO / 4f);

		mWindow.addRect(center - (floorO * 5.5f), tmpT += (floorO * 3f), center + (floorO * 5.5f), tmpT += floorO / 2f,
				Direction.CW);
		mWindow.addCircle(center, tmpT + floorO, floorO / 2, Direction.CW);
		mWindow.addRect(center - (floorO * 3.5f), tmpT + (floorO / 2), center - (floorO * 2.5f),
				tmpT + (floorO * 1.5f), Direction.CW);
		mWindow.addRect(center + (floorO * 2.5f), tmpT + (floorO / 2), center + (floorO * 3.5f),
				tmpT + (floorO * 1.5f), Direction.CW);

		mSketch.close();
		mWindow.close();

		canvas.drawPath(mSketch, mTitlePaint);
		canvas.drawPath(mWindow, mWindowPaint);

		mSketch.reset();
		mWindow.reset();
		// Draw Main
		mSketch.moveTo(center - (floorO * 5f), tmpT += (floorO * 3f));
		mSketch.addRect(center - (floorO * 5f), tmpT, center + (floorO * 5f), tmpT + (floorO * 29f), Direction.CW);

		mWindow.addRect(center - (floorO * 5f), tmpT - (floorO / 2f), center + (floorO * 5f), tmpT, Direction.CW);
		mWindow.addRect(center - floorO, tmpT += floorO, center - (floorO / 4f), tmpT + (floorO * 2f), Direction.CW);
		mWindow.addRect(center + (floorO / 4f), tmpT, center + floorO, tmpT += (floorO * 2f), Direction.CW);
		mWindow.addRect(center - floorO, tmpT += floorO, center + floorO, tmpT += (floorO * 4f), Direction.CW);
		mWindow.addRect(center - floorO, tmpT += floorO, center + floorO, tmpT += (floorO * 5.5f), Direction.CW);

		mWindow.addRect(center - (floorO * 5.5f), tmpT, center - (floorO * 5f), tmpT + (floorO * 14.5f), Direction.CW);
		mWindow.addRect(center + (floorO * 5.5f), tmpT, center + (floorO * 5f), tmpT + (floorO * 14.5f), Direction.CCW);

		mSketch.moveTo(center - (floorO * 10.5f), tmpT);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(0, floorO);
		mSketch.rLineTo(floorO * 5f, 0);
		mSketch.rLineTo(0, -floorO * 2f);

		mSketch.moveTo(center + (floorO * 10.5f), tmpT);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(0, floorO);
		mSketch.rLineTo(-floorO * 5f, 0);
		mSketch.rLineTo(0, -floorO * 2f);

		mWindow.addRect(center - (floorO * 10.5f), tmpT += floorO / 2f, center - (floorO * 5.5f), tmpT + (floorO / 2f),
				Direction.CW);
		mWindow.addRect(center + (floorO * 10.5f), tmpT, center + (floorO * 5.5f), tmpT += floorO / 2f, Direction.CCW);

		mWindow.addRect(center - (floorO * 10.5f), tmpT += floorO, center - (floorO * 8.5f), tmpT + (floorO / 2f),
				Direction.CW);
		mWindow.addRect(center + (floorO * 10.5f), tmpT, center + (floorO * 8.5f), tmpT + (floorO / 2f), Direction.CCW);
		mSketch.addRect(center - (floorO * 8.5f), tmpT, center - (floorO * 7.5f), tmpT + (floorO * 1.5f), Direction.CW);
		mSketch.addRect(center + (floorO * 8.5f), tmpT, center + (floorO * 7.5f), tmpT + (floorO * 1.5f), Direction.CCW);
		mWindow.addRect(center - (floorO * 7.5f), tmpT, center - (floorO * 5.5f), tmpT + (floorO / 2f), Direction.CW);
		mWindow.addRect(center + (floorO * 7.5f), tmpT, center + (floorO * 5.5f), tmpT += floorO / 2f, Direction.CCW);

		mSketch.moveTo(center - (floorO * 10.5f), tmpT - (floorO / 2f));
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(floorO * 2f, 0);
		mSketch.rLineTo(0, -floorO * 1.5f);

		mSketch.moveTo(center + (floorO * 10.5f), tmpT - (floorO / 2f));
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(-floorO * 2f, 0);
		mSketch.rLineTo(0, -floorO * 1.5f);

		mWindow.addRect(center - (floorO * 9f), tmpT, center - (floorO * 8.5f), tmpT + (floorO / 2f), Direction.CW);
		mWindow.addRect(center + (floorO * 9f), tmpT, center + (floorO * 8.5f), tmpT + (floorO / 2f), Direction.CCW);
		mWindow.addRect(center - (floorO * 7.5f), tmpT, center - (floorO * 7f), tmpT + (floorO / 2f), Direction.CW);
		mWindow.addRect(center + (floorO * 7.5f), tmpT, center + (floorO * 7f), tmpT + (floorO / 2f), Direction.CCW);
		mSketch.moveTo(center - (floorO * 7.5f), tmpT - (floorO / 2f));
		mSketch.rLineTo(floorO / 2f, floorO / 2f);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(-floorO / 2f, floorO / 2f);
		mSketch.rLineTo(floorO * 2f, 0);
		mSketch.rLineTo(-floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(-floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(floorO / 4f, -floorO / 4f);

		mSketch.moveTo(center + (floorO * 7.5f), tmpT - (floorO / 2f));
		mSketch.rLineTo(-floorO / 2f, floorO / 2f);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(floorO / 2f, floorO / 2f);
		mSketch.rLineTo(-floorO * 2f, 0);
		mSketch.rLineTo(floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(-floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(0, -floorO / 2f);
		mSketch.rLineTo(floorO / 4f, -floorO / 4f);
		mSketch.rLineTo(-floorO / 4f, -floorO / 4f);

		mWindow.addRect(center - (floorO * 10.5f), tmpT += floorO / 2f, center - (floorO * 8.5f), tmpT + (floorO / 2f),
				Direction.CW);
		mWindow.addRect(center + (floorO * 10.5f), tmpT, center + (floorO * 8.5f), tmpT + (floorO / 2f), Direction.CCW);
		mWindow.addRect(center - (floorO * 7.5f), tmpT, center - (floorO * 5.5f), tmpT + (floorO / 2f), Direction.CW);
		mWindow.addRect(center + (floorO * 7.5f), tmpT, center + (floorO * 5.5f), tmpT += floorO / 2f, Direction.CCW);

		mSketch.addRect(center - (floorO * 10.5f), tmpT, center - (floorO * 5.5f), tmpT + (floorO * 3f), Direction.CW);
		mSketch.addRect(center + (floorO * 10.5f), tmpT, center + (floorO * 5.5f), tmpT + (floorO * 3f), Direction.CCW);

		mWindow.addRect(center - (floorO * 11f), tmpT += floorO, center - (floorO * 5.5f), tmpT + (floorO / 2f),
				Direction.CW);
		mWindow.addRect(center + (floorO * 11f), tmpT, center + (floorO * 5.5f), tmpT += floorO / 2f, Direction.CCW);

		mWindow.addCircle(center - (floorO * 3), tmpT - floorO, floorO, Direction.CW);
		mWindow.addCircle(center + (floorO * 3), tmpT, floorO, Direction.CW);

		mWindow.addRect(center - (floorO * 11f), tmpT += floorO / 2f, center - (floorO * 5.5f), tmpT + (floorO / 2f),
				Direction.CW);
		mWindow.addRect(center + (floorO * 11f), tmpT, center + (floorO * 5.5f), tmpT += floorO / 2f, Direction.CCW);

		mSketch.addRect(center - (floorO * 10.5f), tmpT, center - (floorO * 5.5f), tmpT + (floorO * 8.5f), Direction.CW);
		mSketch.addRect(center + (floorO * 10.5f), tmpT, center + (floorO * 5.5f), tmpT + (floorO * 8.5f),
				Direction.CCW);
		mWindow.addRect(center - (floorO * 8.5f), tmpT += floorO * 1.5f, center - (floorO * 7.5f),
				tmpT + (floorO * 3f), Direction.CW);
		mWindow.addRect(center + (floorO * 8.5f), tmpT, center + (floorO * 7.5f), tmpT + (floorO * 3f), Direction.CCW);
		mWindow.addCircle(center, tmpT += floorO, floorO, Direction.CW);
		mWindow.addRect(center - floorO, tmpT, center + floorO, tmpT += (floorO * 2f), Direction.CW);

		mWindow.addRect(center - (floorO * 8.5f), tmpT += floorO, center - (floorO * 7.5f), tmpT + (floorO * 3f),
				Direction.CW);
		mWindow.addRect(center - floorO, tmpT, center + floorO, tmpT + (floorO * 3f), Direction.CW);
		mWindow.addRect(center + (floorO * 8.5f), tmpT, center + (floorO * 7.5f), tmpT += (floorO * 3f), Direction.CCW);

		mSketch.addRect(center - (floorO * 10.5f), tmpT, center + (floorO * 10.5f), tmpT += floorO, Direction.CW);

		mSketch.addRect(center - (floorO * 10.5f), tmpT, center - (floorO * 8f), tmpT + (floorO / 2f), Direction.CW);
		mSketch.addRect(center + (floorO * 10.5f), tmpT, center + (floorO * 8f), tmpT + (floorO / 2f), Direction.CCW);
		mWindow.addRect(center - (floorO * 8f), tmpT, center + (floorO * 8f), tmpT += (floorO / 2f), Direction.CW);

		// Door side
		mSketch.addRect(center - (floorO * 10.5f), tmpT, center - (floorO * 8.5f), tmpT + (floorO * 7f), Direction.CW);
		mSketch.addRect(center + (floorO * 10.5f), tmpT, center + (floorO * 8.5f), tmpT + (floorO * 7f), Direction.CCW);
		mWindow.addRect(center - (floorO * 8.5f), tmpT, center - (floorO * 8f), tmpT + (floorO * 7f), Direction.CW);
		mWindow.addRect(center + (floorO * 8.5f), tmpT, center + (floorO * 8f), tmpT + (floorO * 7f), Direction.CCW);
		mWindow.addRect(center - (floorO * 8f), tmpT, center - (floorO * 7.5f), tmpT + (floorO / 2f), Direction.CW);
		mWindow.addRect(center + (floorO * 8f), tmpT, center + (floorO * 7.5f), tmpT + (floorO / 2f), Direction.CCW);
		mSketch.addRect(center - (floorO * 7.5f), tmpT, center + (floorO * 7.5f), tmpT += (floorO / 2f), Direction.CCW);
		mSketch.addRect(center - (floorO * 8f), tmpT, center + (floorO * 8f), tmpT += floorO, Direction.CCW);

		// Door
		mWindow.addRect(center - (floorO * 8f), tmpT, center - (floorO * 7.5f), tmpT + (floorO * 5.5f), Direction.CW);
		mWindow.addRect(center + (floorO * 8f), tmpT, center + (floorO * 7.5f), tmpT + (floorO * 5.5f), Direction.CCW);
		mSketch.addRect(center - (floorO * 7.5f), tmpT, center - (floorO * 7f), tmpT + (floorO * 5.5f), Direction.CW);
		mSketch.addRect(center + (floorO * 7.5f), tmpT, center + (floorO * 7f), tmpT + (floorO * 5.5f), Direction.CCW);
		mWindow.addRect(center - (floorO * 7f), tmpT, center - (floorO * 6.5f), tmpT + (floorO * 5.5f), Direction.CW);
		mWindow.addRect(center + (floorO * 7f), tmpT, center + (floorO * 6.5f), tmpT + (floorO * 5.5f), Direction.CCW);
		mSketch.addRect(center - (floorO * 6.5f), tmpT, center - (floorO * 6f), tmpT + (floorO * 5.5f), Direction.CW);
		mSketch.addRect(center + (floorO * 6.5f), tmpT, center + (floorO * 6f), tmpT + (floorO * 5.5f), Direction.CCW);
		mWindow.addRect(center - (floorO * 6f), tmpT, center - (floorO * 4.5f), tmpT + (floorO * 5.5f), Direction.CW);
		mWindow.addRect(center + (floorO * 6f), tmpT, center + (floorO * 4.5f), tmpT + (floorO * 5.5f), Direction.CCW);
		mSketch.addRect(center - (floorO * 4.5f), tmpT, center - (floorO * 4f), tmpT + (floorO * 5.5f), Direction.CW);
		mSketch.addRect(center + (floorO * 4.5f), tmpT, center + (floorO * 4f), tmpT + (floorO * 5.5f), Direction.CCW);
		mWindow.addRect(center - (floorO * 4f), tmpT, center - (floorO * 3.5f), tmpT + (floorO * 5.5f), Direction.CW);
		mWindow.addRect(center + (floorO * 4f), tmpT, center + (floorO * 3.5f), tmpT + (floorO * 5.5f), Direction.CCW);
		mSketch.addRect(center - (floorO * 3.5f), tmpT, center - (floorO * 3f), tmpT + (floorO * 5.5f), Direction.CW);
		mSketch.addRect(center + (floorO * 3.5f), tmpT, center + (floorO * 3f), tmpT + (floorO * 5.5f), Direction.CCW);
		mWindow.addRect(center - (floorO * 3f), tmpT, center + (floorO * 3f), tmpT += (floorO * 5.5f), Direction.CW);

		mSketch.moveTo(center - (floorO * 10.5f), tmpT);
		mSketch.rLineTo(floorO * 1.5f, 0);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(-floorO, 0);
		mSketch.rLineTo(0, floorO);
		mSketch.rLineTo(-floorO / 2f, 0);
		mSketch.moveTo(center + (floorO * 10.5f), tmpT);
		mSketch.rLineTo(-floorO * 1.5f, 0);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(floorO, 0);
		mSketch.rLineTo(0, floorO);
		mSketch.rLineTo(floorO / 2f, 0);

		mWindow.moveTo(center - (floorO * 9f), tmpT);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(0, floorO / 2f);
		mWindow.rLineTo(-floorO / 2f, 0);
		mWindow.rLineTo(0, floorO / 2f);
		mWindow.rLineTo(-floorO, 0);
		mWindow.rLineTo(0, floorO);
		mWindow.rLineTo(-floorO, 0);
		mWindow.rLineTo(0, -floorO / 2f);
		mWindow.rLineTo(floorO / 2f, 0);
		mWindow.rLineTo(0, -floorO);
		mWindow.rLineTo(floorO, 0);
		mWindow.moveTo(center + (floorO * 9f), tmpT);
		mWindow.rLineTo(-floorO, 0);
		mWindow.rLineTo(0, floorO / 2f);
		mWindow.rLineTo(floorO / 2f, 0);
		mWindow.rLineTo(0, floorO / 2f);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(0, floorO);
		mWindow.rLineTo(floorO, 0);
		mWindow.rLineTo(0, -floorO / 2f);
		mWindow.rLineTo(-floorO / 2f, 0);
		mWindow.rLineTo(0, -floorO);
		mWindow.rLineTo(-floorO, 0);

		mSketch.addRect(center - (floorO * 8f), tmpT, center - (floorO * 5.5f), tmpT + (floorO / 2f), Direction.CW);
		mSketch.addRect(center + (floorO * 8f), tmpT, center + (floorO * 5.5f), tmpT + (floorO / 2f), Direction.CCW);
		mWindow.addRect(center - (floorO * 5.5f), tmpT, center - (floorO * 5f), tmpT + (floorO / 2f), Direction.CW);
		mWindow.addRect(center + (floorO * 5.5f), tmpT, center + (floorO * 5f), tmpT + (floorO / 2f), Direction.CCW);
		mSketch.addRect(center - (floorO * 5f), tmpT, center - (floorO * 2.5f), tmpT + (floorO / 2f), Direction.CW);
		mSketch.addRect(center + (floorO * 5f), tmpT, center + (floorO * 2.5f), tmpT + (floorO / 2f), Direction.CCW);
		mWindow.addRect(center - (floorO * 2.5f), tmpT, center + (floorO * 2.5f), tmpT += (floorO / 2f), Direction.CW);

		// Stairs
		mSketch.addRect(center - (floorO * 8.5f), tmpT, center + (floorO * 8.5f), tmpT += (floorO / 2f), Direction.CW);
		mSketch.addRect(center - (floorO * 9.5f), tmpT, center + (floorO * 9.5f), tmpT += floorO, Direction.CW);
		mSketch.addRect(center - (floorO * 10.5f), tmpT, center + (floorO * 10.5f), tmpT += floorO, Direction.CW);
		mSketch.addRect(center - (floorO * 11f), tmpT, center + (floorO * 11f), tmpT += floorO / 2f, Direction.CW);

		mSketch.close();
		mWindow.close();

		canvas.drawPath(mSketch, mTitlePaint);
		canvas.drawPath(mWindow, mWindowPaint);

		mSketch.reset();
		mWindow.reset();
		// Draw side
		mSketch.moveTo(center - (floorO * 11f), tmpT -= floorO * 22f);
		mWindow.addRect(center - (floorO * 11f), tmpT, center - (floorO * 10.5f), tmpT + (floorO * 21.5f), Direction.CW);
		mWindow.addRect(center + (floorO * 11f), tmpT, center + (floorO * 10.5f), tmpT + (floorO * 21.5f),
				Direction.CCW);

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				mSketch.moveTo((center - (floorO * 15.5f) - (floorO * 12 * i)) + (floorO * 43 * j), tmpT
						- (floorO * 3f));
				mSketch.rLineTo(-floorO, floorO);
				mSketch.rLineTo(0, floorO * 1.5f);
				mSketch.rLineTo(-floorO / 2f, 0);
				mSketch.rLineTo(0, floorO / 2f);
				mSketch.rLineTo(floorO * 3f, 0);
				mSketch.rLineTo(0, -floorO / 2f);
				mSketch.rLineTo(-floorO / 2f, 0);
				mSketch.rLineTo(0, -floorO * 1.5f);
			}
		}

		mSketch.addRect(center - (floorO * 11f), tmpT, center - (floorO * 32f), tmpT + (floorO * 22f), Direction.CCW);
		mSketch.addRect(center + (floorO * 11f), tmpT, center + (floorO * 32f), tmpT + (floorO * 22f), Direction.CW);
		mSketch.moveTo(center - (floorO * 44.5f), tmpT);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(floorO / 2f, 0);
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(0, floorO);
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(-floorO / 2f, 0);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(floorO / 2f, 0);
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(0, floorO * 15.5f);
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(-floorO / 2f, 0);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(floorO * 4.5f, 0);
		mSketch.rLineTo(0, -floorO * 22f);

		mSketch.moveTo(center + (floorO * 44.5f), tmpT);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(-floorO / 2f, 0);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(0, floorO);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(floorO / 2f, 0);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(-floorO / 2f, 0);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(0, floorO * 15.5f);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(-floorO / 4f, floorO / 4f);
		mSketch.rLineTo(floorO / 4f, floorO / 4f);
		mSketch.rLineTo(floorO / 2f, 0);
		mSketch.rLineTo(0, floorO / 2f);
		mSketch.rLineTo(-floorO * 4.5f, 0);
		mSketch.rLineTo(0, -floorO * 22f);

		mWindow.addRect(center - (floorO * 11f), tmpT += (floorO / 2f), center - (floorO * 32f), tmpT + (floorO / 2f),
				Direction.CCW);
		mWindow.addRect(center + (floorO * 11f), tmpT, center + (floorO * 32f), tmpT + (floorO / 2f), Direction.CW);
		mWindow.addRect(center - (floorO * 44f), tmpT, center - (floorO * 40f), tmpT + (floorO / 2f), Direction.CCW);
		mWindow.addRect(center + (floorO * 44f), tmpT, center + (floorO * 40f), tmpT += (floorO / 2f), Direction.CW);

		mWindow.addRect(center - (floorO * 11f), tmpT += floorO, center - (floorO * 32f), tmpT + (floorO / 2f),
				Direction.CCW);
		mWindow.addRect(center + (floorO * 11f), tmpT, center + (floorO * 32f), tmpT + (floorO / 2f), Direction.CW);
		mWindow.addRect(center - (floorO * 44f), tmpT, center - (floorO * 40f), tmpT + (floorO / 2f), Direction.CCW);
		mWindow.addRect(center + (floorO * 44f), tmpT, center + (floorO * 40f), tmpT += (floorO / 2f), Direction.CW);

		mWindow.addRect(center - (floorO * 11f), tmpT += (floorO / 2f), center - (floorO * 32f), tmpT + (floorO / 2f),
				Direction.CCW);
		mWindow.addRect(center + (floorO * 11f), tmpT, center + (floorO * 32f), tmpT + (floorO / 2f), Direction.CW);
		mWindow.addRect(center - (floorO * 44f), tmpT, center - (floorO * 40f), tmpT + (floorO / 2f), Direction.CCW);
		mWindow.addRect(center + (floorO * 44f), tmpT, center + (floorO * 40f), tmpT += (floorO / 2f), Direction.CW);

		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 7; j++) {
				mWindow.addCircle(center - (floorO * 12.5f) - (floorO * 3f * j), tmpT + (floorO * 2.5f)
						+ (floorO * 4 * i), floorO, Direction.CW);
				mWindow.addCircle(center + (floorO * 12.5f) + (floorO * 3f * j), tmpT + (floorO * 2.5f)
						+ (floorO * 4 * i), floorO, Direction.CW);

				mWindow.addRect(center - (floorO * 13.5f) - (floorO * 3f * j), tmpT + (floorO * 2.5f)
						+ (floorO * 4 * i), center - (floorO * 11.5f) - (floorO * 3f * j), tmpT + (floorO * 4.5f)
						+ (floorO * 4 * i), Direction.CW);
				mWindow.addRect(center + (floorO * 13.5f) + (floorO * 3f * j), tmpT + (floorO * 2.5f)
						+ (floorO * 4 * i), center + (floorO * 11.5f) + (floorO * 3f * j), tmpT + (floorO * 4.5f)
						+ (floorO * 4 * i), Direction.CCW);
				mWindow.addRect(center - (floorO * 13.5f) - (floorO * 3f * j), tmpT + (floorO * 9.5f), center
						- (floorO * 11.5f) - (floorO * 3f * j), tmpT + (floorO * 14.5f), Direction.CW);
				mWindow.addRect(center + (floorO * 13.5f) + (floorO * 3f * j), tmpT + (floorO * 9.5f), center
						+ (floorO * 11.5f) + (floorO * 3f * j), tmpT + (floorO * 14.5f), Direction.CCW);
			}
			mWindow.addRect(center - (floorO * 41.5f), tmpT + (floorO * 1.5f) + (floorO * 4 * i), center
					- (floorO * 40.5f), tmpT + (floorO * 4.5f) + (floorO * 4 * i), Direction.CW);
			mWindow.addRect(center + (floorO * 41.5f), tmpT + (floorO * 1.5f) + (floorO * 4 * i), center
					+ (floorO * 40.5f), tmpT + (floorO * 4.5f) + (floorO * 4 * i), Direction.CCW);

			mWindow.addRect(center - (floorO * 44f), tmpT + (floorO * 15.5f) + (floorO * i), center - (floorO * 40f),
					tmpT + (floorO * 16f) + (floorO * i), Direction.CCW);
			mWindow.addRect(center + (floorO * 44f), tmpT + (floorO * 15.5f) + (floorO * i), center + (floorO * 40f),
					tmpT + (floorO * 16f) + (floorO * i), Direction.CW);
		}
		mWindow.addRect(center - (floorO * 44f), tmpT + (floorO * 17.5f), center - (floorO * 40f), tmpT
				+ (floorO * 18f), Direction.CCW);
		mWindow.addRect(center + (floorO * 44f), tmpT + (floorO * 17.5f), center + (floorO * 40f), tmpT
				+ (floorO * 18f), Direction.CW);

		for (int i = 0; i < 2; i++) {
			mSketch.moveTo((center - (floorO * 36f)) + (floorO * 72 * i), tmpT - (floorO * 8.5f));
			mSketch.rLineTo(-floorO * 2.5f, floorO);
			mSketch.rLineTo(-floorO * 1.5f, 0);
			mSketch.rLineTo(0, floorO / 2f);
			mSketch.rLineTo(floorO * 8f, 0);
			mSketch.rLineTo(0, -floorO / 2f);
			mSketch.rLineTo(-floorO * 1.5f, 0);
		}
		mWindow.addRect(center - (floorO * 39.5f), tmpT -= floorO * 7f, center - (floorO * 32.5f),
				tmpT + (floorO / 2f), Direction.CW);
		mWindow.addRect(center + (floorO * 39.5f), tmpT, center + (floorO * 32.5f), tmpT += floorO / 2f, Direction.CCW);

		mSketch.addRect(center - (floorO * 39.5f), tmpT, center - (floorO * 32.5f), tmpT + (floorO / 2f), Direction.CW);
		mSketch.addRect(center + (floorO * 39.5f), tmpT, center + (floorO * 32.5f), tmpT += floorO / 2f, Direction.CCW);

		mSketch.addRect(center - (floorO * 39.5f), tmpT + (floorO * 1.5f), center - (floorO * 32.5f), tmpT
				+ (floorO * 2.5f), Direction.CW);
		mSketch.addRect(center + (floorO * 39.5f), tmpT + (floorO * 1.5f), center + (floorO * 32.5f), tmpT
				+ (floorO * 2.5f), Direction.CCW);

		mSketch.addRect(center - (floorO * 36.5f), tmpT, center - (floorO * 35.5f), tmpT + (floorO * 4f), Direction.CW);
		mSketch.addRect(center + (floorO * 36.5f), tmpT, center + (floorO * 35.5f), tmpT + (floorO * 4f), Direction.CCW);

		for (int i = 0; i < 2; i++) {
			for (int k = 0; k < 2; k++) {
				mSketch.addRect((center - (floorO * 40f)) + (floorO * 72 * i), tmpT + (floorO / 2f)
						+ (floorO * 2.5f * k), (center - (floorO * 37f)) + (floorO * 72 * i), tmpT + floorO
						+ (floorO * 2.5f * k), Direction.CW);
				mWindow.moveTo((center - (floorO * 39.5f)) + (floorO * 72 * i), tmpT + (floorO * 2.5f * k));
				mWindow.rLineTo(floorO * 3f, 0);
				mWindow.rLineTo(0, floorO * 1.5f);
				mWindow.rLineTo(-floorO * 3f, 0);
				mWindow.rLineTo(0, -floorO / 2f);
				mWindow.rLineTo(floorO * 2.5f, 0);
				mWindow.rLineTo(0, -floorO / 2f);
				mWindow.rLineTo(-floorO * 2.5f, 0);

				mSketch.addRect((center - (floorO * 35f)) + (floorO * 72 * i), tmpT + (floorO / 2f)
						+ (floorO * 2.5f * k), (center - (floorO * 32f)) + (floorO * 72 * i), tmpT + floorO
						+ (floorO * 2.5f * k), Direction.CW);
				mWindow.moveTo((center - (floorO * 32.5f)) + (floorO * 72 * i), tmpT + (floorO * 2.5f * k));
				mWindow.rLineTo(-floorO * 3f, 0);
				mWindow.rLineTo(0, floorO * 1.5f);
				mWindow.rLineTo(floorO * 3f, 0);
				mWindow.rLineTo(0, -floorO / 2f);
				mWindow.rLineTo(-floorO * 2.5f, 0);
				mWindow.rLineTo(0, -floorO / 2f);
				mWindow.rLineTo(floorO * 2.5f, 0);

				mWindow.addRect((center - (floorO * 40f)) + (floorO * 7.5f * k) + (floorO * 72 * i), tmpT
						+ (floorO * 3.5f), (center - (floorO * 39.5f)) + (floorO * 7.5f * k) + (floorO * 72 * i), tmpT
						+ (floorO * 24f), Direction.CW);
				mWindow.addRect((center - (floorO * 37f)) + (floorO * 72 * i), tmpT + (floorO * 8.5f)
						+ (floorO * 4f * k), (center - (floorO * 35f)) + (floorO * 72 * i), tmpT + (floorO * 11.5f)
						+ (floorO * 4f * k), Direction.CW);

				mWindow.moveTo((center - (floorO * 39f)) + (floorO * 4.5f * k) + (floorO * 72f * i), tmpT
						+ (floorO * 21f));
				mWindow.rLineTo(floorO * 1.5f, 0);
				mWindow.rLineTo(0, floorO * 3);
				mWindow.rLineTo(-floorO / 2f, 0);
				mWindow.rLineTo(0, floorO / 2f);
				mWindow.rLineTo(-floorO / 2f, 0);
				mWindow.rLineTo(0, -floorO / 2f);
				mWindow.rLineTo(-floorO / 2f, 0);
			}

			mSketch.moveTo((center - (floorO * 39.5f)) + (floorO * 72 * i), tmpT + (floorO * 4f));
			mSketch.rLineTo(floorO * 7f, 0);
			mSketch.rLineTo(0, floorO * 2.5f);
			mSketch.rLineTo(-floorO * 3.5f, -floorO * 2f);
			mSketch.rLineTo(-floorO * 3.5f, floorO * 2f);

			mWindow.moveTo((center - (floorO * 39.5f)) + (floorO * 72 * i), tmpT + (floorO * 6.5f));
			mWindow.rLineTo(floorO * 3.5f, -floorO * 2f);
			mWindow.rLineTo(floorO * 3.5f, floorO * 2f);
			mWindow.rLineTo(0, floorO / 2f);
			mWindow.rLineTo(-floorO * 3.5f, -floorO * 2f);
			mWindow.rLineTo(-floorO * 3.5f, floorO * 2f);

			mSketch.moveTo((center - (floorO * 39.5f)) + (floorO * 72 * i), tmpT + (floorO * 7f));
			mSketch.rLineTo(floorO * 3.5f, -floorO * 2f);
			mSketch.rLineTo(floorO * 3.5f, floorO * 2f);
			mSketch.rLineTo(0, floorO / 2f);
			mSketch.rLineTo(-floorO * 3.5f, -floorO * 2f);
			mSketch.rLineTo(-floorO * 3.5f, floorO * 2f);

			mWindow.moveTo((center - (floorO * 39.5f)) + (floorO * 72 * i), tmpT + (floorO * 7.5f));
			mWindow.rLineTo(floorO * 3.5f, -floorO * 2f);
			mWindow.rLineTo(floorO * 3.5f, floorO * 2f);
			mWindow.rLineTo(0, floorO / 2f);
			mWindow.rLineTo(-floorO * 3.5f, -floorO * 2f);
			mWindow.rLineTo(-floorO * 3.5f, floorO * 2f);

			mSketch.moveTo((center - (floorO * 39.5f)) + (floorO * 72 * i), tmpT + (floorO * 8f));
			mSketch.rLineTo(floorO * 3.5f, -floorO * 2f);
			mSketch.rLineTo(floorO * 3.5f, floorO * 2f);
			mSketch.rLineTo(0, floorO * 16.5f);
			mSketch.rLineTo(-floorO * 7f, 0);

			mSketch.addRect((center - (floorO * 32.5f)) + (floorO * 72 * i), tmpT + (floorO * 24f),
					(center - (floorO * 32f)) + (floorO * 72 * i), tmpT + (floorO * 24.5f), Direction.CW);

			mWindow.moveTo((center - (floorO * 37f)) + (floorO * 72f * i), tmpT + (floorO * 21f));
			mWindow.rLineTo(floorO * 2f, 0);
			mWindow.rLineTo(0, floorO * 3);
			mWindow.rLineTo(-floorO / 2f, 0);
			mWindow.rLineTo(0, floorO / 2f);
			mWindow.rLineTo(-floorO, 0);
			mWindow.rLineTo(0, -floorO / 2f);
			mWindow.rLineTo(-floorO / 2f, 0);
		}

		mSketch.close();
		mWindow.close();
		canvas.drawPath(mSketch, mTitlePaint);
		canvas.drawPath(mWindow, mWindowPaint);
	}

	private void draw101Tower(Canvas canvas, float right) {
		float floorO = Util.PIXEL * Util.MULTI;
		float floorH = floorO * 8f;
		float tmpT = getTop();
		float tmpCen = right - (floorO * 6f);

		mSketch.reset();
		mWindow.reset();
		mSketch.moveTo(tmpCen - (floorO / 2f), tmpT);
		// Draw antenna
		mSketch.lineTo(tmpCen - (floorO / 2f), tmpT += (floorO * 10));
		// 12F
		mSketch.lineTo(tmpCen - (floorO * 2), tmpT);
		mSketch.lineTo(tmpCen - (floorO * 3), tmpT += (floorO * 2));
		// 11F
		mSketch.lineTo(tmpCen - (floorO * 4), tmpT);
		mSketch.lineTo(tmpCen - (floorO * 3), tmpT += (floorO * 9));
		// 10F
		mSketch.lineTo(tmpCen - (floorO * 4), tmpT += (floorO * 2));
		// backward
		mSketch.lineTo(tmpCen + (floorO * 4), tmpT);
		mSketch.lineTo(tmpCen + (floorO * 3), tmpT -= (floorO * 2));
		mSketch.lineTo(tmpCen + (floorO * 4), tmpT -= (floorO * 9));
		mSketch.lineTo(tmpCen + (floorO * 3), tmpT);
		mSketch.lineTo(tmpCen + (floorO * 2), tmpT -= (floorO * 2));
		mSketch.lineTo(tmpCen + (floorO / 2f), tmpT);
		// 2 ~ 9F
		mSketch.moveTo(tmpCen - (floorO * 6f), tmpT = floorO * 23);

		for (int i = 0; i < 8; i++) {
			mSketch.rLineTo(floorO, floorH);
			mSketch.rLineTo(floorO * 10f, 0);
			mSketch.rLineTo(floorO, -floorH);

			mWindow.addRect(tmpCen - floorO, tmpT, tmpCen + floorO, tmpT + floorO, Direction.CW);
			mSketch.moveTo(tmpCen - (floorO * 6f), tmpT += floorH);
		}
		// 1F
		mSketch.moveTo(tmpCen - (floorO * 5f), tmpT);
		mSketch.rLineTo(-floorO, floorO * 13);

		mSketch.rLineTo(floorO * 12f, 0);
		mSketch.rLineTo(-floorO, -floorO * 13);
		// Draw mWindow of main tower
		mWindow.addCircle(tmpCen, tmpT, floorO * 1.5f, Direction.CW);
		mWindow.addRect(tmpCen - (floorO * 5.25f), tmpT += (floorO * 3), tmpCen + (floorO * 5.25f), tmpT
				+ (floorO / 2f), Direction.CW);
		mWindow.addRect(tmpCen - (floorO * 5.375f), tmpT += (floorO * 2), tmpCen + (floorO * 5.375f), tmpT
				+ (floorO / 2f), Direction.CW);
		mWindow.addRect(tmpCen - floorO, tmpT += (floorO * 2), tmpCen + floorO, tmpT += floorO * 6, Direction.CW);

		mSketch.close();
		mWindow.close();

		canvas.drawPath(mSketch, mTitlePaint);
		canvas.drawPath(mWindow, mWindowPaint);
	}

	// =====================================================================================================//
	public void setFloatSearch(FloatSearch floatSearch) {
		mFloatSearch = floatSearch;
	}

	public void setFloatSearchButtonColor(String color) {
		mFloatSearch.setFloatSearchButtonColor(color);
	}

	public void setButtomPanel(ButtomPanel buttomPanel) {
		mButtomPanel = buttomPanel;
	}

	public void setButtomPanelColor(String color) {
		mButtomPanel.setButtomPanelColor(color);
	}


	public String getPageColor() {
		return Util.ACT_CATAGORY_COLOR[mCurrentPage];
	}

	// Return true means the keyCode has been handle
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mActPages.isGraphPagerOpen()) {
			mActPages.closeGraphPager();
			return true;
		} else if (mActPages.isTicketPagePanelOpen()) {
			mActPages.closeTicketPagePanel();
			return true;
		} else if (mActPages.isSharePagePanelOpen()) {
			mActPages.closeSharePagePanel();
			return true;
		} else if (!mActPages.isListMode()) {
			if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
				mActPages.moveToAct(1);
			} else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
				mActPages.moveToAct(-1);
			} else {
				mActPages.backToListMode();
			}
			return true;
		}
		return false;
	}
}
