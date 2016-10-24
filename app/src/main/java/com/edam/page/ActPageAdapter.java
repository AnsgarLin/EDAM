package com.edam.page;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.MotionEvent;

import com.edam.query.QueryCondition;
import com.edam.util.Logger;
import com.edam.util.Util;

public class ActPageAdapter extends FragmentPagerAdapter{
	private SparseArray<ActPage> mActPages;

	public ActPageAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
		mActPages = new SparseArray<ActPage>();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return Util.ACT_CATAGORY[position];
	}

	@Override
	public int getCount() {
		return Util.ACT_CATAGORY.length;
	}

	@Override
	public Fragment getItem(int position) {
		if (mActPages.get(position) == null) {
			mActPages.put(position, ActPage.newInstance(position));
		}

		return mActPages.get(position);
	}

	public void onDestroy() {
		Logger.d(getClass(), "ActPageAdapter onDestroy()");

		mActPages.clear();
		mActPages = null;
	}
	// =====================================================================================================//
	public void setPageListDividerColor(int position, String color) {
		((ActPage) getItem(position)).setPageListDividerColor(color);
	}

	/**
	 * Use for search panel to trigger a new query, but check if current query data is the same as the new one, but
	 * without offset
	 */
	public boolean triggerQuery(int currentPage, QueryCondition queryCondition) {
		if (!mActPages.get(currentPage).isSameNoOffset(queryCondition)) {
			mActPages.get(currentPage).resetActInfoItemAdapter();
			mActPages.get(currentPage).triggerQuery(queryCondition);
			return true;
		} else {
			return false;
		}
	}
	// =====================================================================================================//
	public boolean dispatchTouchEvent(int currentPage, MotionEvent ev) {
		return mActPages.get(currentPage).dispatchTouchEvent(ev);
	}

	public boolean isListMode(int currentPage) {
		return mActPages.get(currentPage).isListMode();
	}

	public boolean isGraphPagerOpen(int currentPage) {
		return mActPages.get(currentPage).isGraphPagerOpen();
	}

	public void closeGraphPager(int currentPage) {
		mActPages.get(currentPage).closeGraphPager();
	}

	public boolean isTicketPagePanelOpen(int currentPage) {
		return mActPages.get(currentPage).isTicketPagePanelOpen();
	}

	public void closeTicketPagePanel(int currentPage) {
		mActPages.get(currentPage).closeTicketPagePanel();
	}

	public boolean isSharePagePanelOpen(int currentPage) {
		return mActPages.get(currentPage).isSharePagePanelOpen();
	}

	public void closeSharePagePanel(int currentPage) {
		mActPages.get(currentPage).closeSharePagePanel();
	}

	public void backToListMode(int currentPage) {
		mActPages.get(currentPage).backToListMode();
	}

	public void moveToAct(int currentPage, int offset) {
		mActPages.get(currentPage).moveToAct(offset);
	}

	public void triggerDefautQueryIfNeed(int currentPage) {
		mActPages.get(currentPage).triggerDefautQueryIfNeed();
	}
}
