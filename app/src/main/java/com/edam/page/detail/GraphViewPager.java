package com.edam.page.detail;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.edam.main.R;
import com.edam.page.detail.Graph.ImageMap;
import com.edam.util.Logger;
import com.edam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class GraphViewPager extends FrameLayout{
	private GraphPagerAdapter mGraphPagerAdapter;
	private String[] mImgURLs;
	private HashMap<String, ImageMap> mImageMaps;
	private Close mClose;
	private DisplayImageOptions mImageLoaderOptions;
	private boolean mIsClose;

	public GraphViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public GraphViewPager(Context context, int select, String pageColor, FragmentManager fragmentManager, String[] imgURLs, HashMap<String, ImageMap> imageMaps) {
		super(context);
		mImageLoaderOptions = Util.getDisplayImageOptions(-1);
		mImgURLs = imgURLs;
		mImageMaps = imageMaps;

		ViewPager pager = new ViewPager(getContext());
		pager.setBackgroundColor(Color.parseColor(pageColor));
		pager.setId(Util.GRAPH_PAGE_ID);
		// Set cache all fragments' state, which may cause all the fragment will not run through DestroyView until activity is destroied
		pager.setAdapter(mGraphPagerAdapter = new GraphPagerAdapter(fragmentManager));
		pager.setOffscreenPageLimit(mImgURLs.length);
		pager.setCurrentItem(select);
		addView(pager, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		RelativeLayout closeWrap = new RelativeLayout(getContext());
		mClose = new Close(getContext());
		RelativeLayout.LayoutParams graphPageCloseParams = new RelativeLayout.LayoutParams((int) Util.GRAPH_PAGE_CLOSE_SIZE, (int) Util.GRAPH_PAGE_CLOSE_SIZE);
		graphPageCloseParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		mClose.setLayoutParams(graphPageCloseParams);
		mClose.setPaintColor(pageColor);
		mClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onDesrtoy();
				((RelativeLayout) getRootView().findViewById(R.id.edam_root)).removeView((View) v.getParent().getParent());
				mIsClose = true;
			}
		});
		closeWrap.addView(mClose);

		addView(closeWrap, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	public void onDesrtoy() {
		mClose.setOnClickListener(null);
		mGraphPagerAdapter.onDestroy();
	}

	public void setCloseListener(OnClickListener listener) {
		mClose.setOnClickListener(listener);
	}

	public void closeGraphPager() {
		mClose.performClick();
	}
	// =====================================================================================================//
	private class GraphPagerAdapter extends FragmentStatePagerAdapter {
		private SparseArray<GraphPagerWrap> mPagers;

		public GraphPagerAdapter(FragmentManager fm) {
			super(fm);
			Logger.d(getClass(), "GraphPagerAdapter");

			mPagers = new SparseArray<GraphPagerWrap>();
		}

		@Override
		public int getCount() {
			return mImageMaps.size();
		}

		@Override
		public Fragment getItem(int position) {
			if (mPagers.get(position) == null) {
				mPagers.put(position, GraphPagerWrap.newInstance(position, mImgURLs[position]).setImageMaps(mImageMaps));
			}

			return mPagers.get(position);
		}

		public void onDestroy() {
			Logger.d(getClass(), "ActPageAdapter onDestroy()");

			for(int i = 0; i < mPagers.size(); i++) {
				mPagers.get(i).onClose();
			}
			mPagers.clear();
			mPagers = null;
		}
	}
	// =====================================================================================================//
	public void setImageBitmap(String targetUrl) {
		for (int i = 0; i < mImgURLs.length; i++) {
			if (targetUrl.equals(mImgURLs[i])) {
				((GraphPagerWrap) mGraphPagerAdapter.getItem(i)).restartlImageLoader();
			}
		}
	}

	public boolean isClose() {
		return mIsClose;
	}
}
