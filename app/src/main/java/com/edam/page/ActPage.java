/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.edam.page;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.edam.main.R;
import com.edam.page.list.ActInfo;
import com.edam.page.list.ActInfoLoadView;
import com.edam.page.list.ActPageList;
import com.edam.query.QueryCondition;
import com.edam.query.QueryObject;
import com.edam.util.Logger;
import com.edam.util.MyUtils;
import com.edam.util.MyUtils.ToastUtil;
import com.edam.util.Util;
import com.flurry.android.FlurryAgent;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.parse.ConfigCallback;
import com.parse.FindCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.vpadn.ads.VpadnAdSize;
import com.vpadn.ads.VpadnBanner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActPage extends Fragment {
	private static final String ACT_POSITION = "position";
	private boolean mFirstFinish;
	/**
	 * ActPage info
	 */
	private int mActInfoPosition;

	/**
	 * State record
	 */
	private boolean mLoadMore;

	private ParseQuery<QueryObject> mCurQuery;

	private QueryCondition mCurQueryCondition;
	// Record the last query condition
	private QueryCondition mOldQueryCondition;

	private ActPageList mActPageList;

	private View mAdView;

	public static ActPage newInstance(int actInfoPosition) {
		ActPage actPageFragment = new ActPage();
		Bundle bundle = new Bundle();
		bundle.putInt(ACT_POSITION, actInfoPosition);

		actPageFragment.setArguments(bundle);
		return actPageFragment;
	}

	@Override
	public void onAttach(Activity activity) {
		Logger.d(getClass(), (mActInfoPosition = getArguments().getInt(ACT_POSITION)) + " " + "onAttach");

		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Logger.d(getClass(), mActInfoPosition + " " + "onCreate");

		super.onCreate(savedInstanceState);

		mLoadMore = false;
	}

	// Cause all fragment will be cached, so this callback will be run once at runtime
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Logger.d(getClass(), mActInfoPosition + " " + "onCreateView");

		if (Util.AD && mAdView == null) {
			if (Util.ADMOB) {
				mAdView = new AdView(getActivity());
			    ((AdView) mAdView).setAdUnitId("ca-app-pub-6857792855345174/2197446840");
			    ((AdView) mAdView).setAdSize(AdSize.SMART_BANNER);
			} else {
				mAdView = new VpadnBanner(getActivity(), "8a808182494cb9db01496f4b4d45429d", VpadnAdSize.SMART_BANNER, "TW");
			}
		}

		mActPageList = (ActPageList) MyUtils.InflateUtil.InflateReource(getActivity(), R.layout.act_page, container);
		if (Util.AD) {
			mActPageList.setAdView(mAdView);
		}

		mActPageList.resetActInfoItemAdapter(mActInfoPosition);

		mActPageList.setOnItemClickListener(new ItemClickListener());

		// Setting scroll listener
		mActPageList.setOnScrollListener(new ScrollListener());
		// Disable scroll bar
		mActPageList.setVerticalScrollBarEnabled(false);
		// Disable the edge effect from top and bottom of listview
		mActPageList.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
		// Disable the high light effect while touch on the listview item
		mActPageList.setSelector(android.R.color.transparent);

		mActPageList.setActPagePosition(mActInfoPosition);

		return mActPageList;
	}

	// Cause all fragment will be cached, so this callback will be run once at runtime
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Logger.d(getClass(), mActInfoPosition + " " + "onViewCreated");

		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Logger.d(getClass(), mActInfoPosition + " " + "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		Logger.d(getClass(), mActInfoPosition + " " + "onViewStateRestored");
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onStart() {
		Logger.d(getClass(), mActInfoPosition + " " + "onStart");

		super.onStart();
	}

	@Override
	public void onResume() {
		Logger.d(getClass(), mActInfoPosition + " " + "onResume");
		if (mAdView != null) {
			if (mAdView instanceof AdView) {
				((AdView) mAdView).resume();
			}
		}

		triggerDefautQueryIfNeed();

		super.onResume();
	}
	// =====================================================================================================//
	@Override
	public void onPause() {
		Logger.d(getClass(), mActInfoPosition + " " + "onPause");
		if (mAdView != null) {
			if (mAdView instanceof AdView) {
				((AdView) mAdView).pause();
			}
		}
		super.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Logger.d(getClass(), mActInfoPosition + " " + "onSaveInstanceState");

		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStop() {
		Logger.d(getClass(), mActInfoPosition + " " + "onStop");
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		Logger.d(getClass(), mActInfoPosition + " " + "onDestroyView");

		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		Logger.d(getClass(), "ActPage " + mActInfoPosition + " onDestroy");

		if (mAdView != null) {
			if (mAdView instanceof AdView) {
				((AdView) mAdView).destroy();
			} else {
				((VpadnBanner) mAdView).destroy();
			}
			mAdView = null;
		}

		new CancelQueryProcessTask().execute();

		if (mActPageList != null) {
			mActPageList.onDestroy();
		}

		super.onDestroy();
	}

	private class CancelQueryProcessTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			resetQuery();
			return null;
		}
	}
	// =====================================================================================================//
	private class ScrollListener implements OnScrollListener {

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			// Logger.d(getClass(), mActInfoPosition + " " + "onScroll");
			// Logger.d(getClass(), mActInfoPosition + " " + "View count: " + view.getChildCount() + ". First: "
			// + firstVisibleItem + ". Visible: " + visibleItemCount + ". Total: " + totalItemCount);

			if (isLoadMode()) {
				return;
			}

			// Check whether the total count == 0 to block from triggering loading task at first start up
			if ((totalItemCount != 0) && ((firstVisibleItem + visibleItemCount) == totalItemCount)) {
				// Equal means the last query has been trigger, or means has been trigger but got no result
				Logger.d(getClass(), mActInfoPosition + " " + "Scroll to the last one");

				if ((mOldQueryCondition.getOffset() + 1) == mCurQueryCondition.getOffset()) {
					Logger.d(getClass(), mActInfoPosition + " " + "Query to the last page");
					return;
				} else {
					Logger.d(getClass(), mActInfoPosition + " " + "Don't Query to the last page yet");
				}

				// It is time to add new data. We call the listener, add loading view first
				mLoadMore = true;
				// this.addFooterView(footer);
				Logger.d(getClass(), mActInfoPosition + " " + "Trigger new page: " + (mOldQueryCondition.getOffset() + 1));

				triggerQuery(new QueryCondition(mCurQueryCondition).setOffset(mCurQueryCondition.getOffset() + 1));

				Util.mToast = ToastUtil.restart(getActivity(), Util.mToast, R.string.read_more);
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (getAdapter() == null) {
				return;
			}
			// When there is no data
			if (getAdapterItemCount() == 0) {
				return;
			}
		}
	}

	public ListAdapter getAdapter() {
		return mActPageList.getAdapter();
	}

	public int getAdapterItemCount() {
		return mActPageList.getAdapter().getCount();
	}
	// =====================================================================================================//
	public class ItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
			if (!isLoadMode()) {
				mActPageList.changeListMode(position);
			}
		}
	}
	// =====================================================================================================//
	public void triggerDefautQueryIfNeed() {
		if (!mFirstFinish) {
			// Start task with default query, cause this callback will be run only once at runtime, so we don't need to worry that the query will be
			// trigger every time when sliding between pages
			if (MyUtils.SystemUtil.isNetworkEnabled(getActivity())) {
				Logger.d(getClass(), mActInfoPosition + " " + "Get config");

				ParseConfig.getInBackground(new ConfigCallback() {
					@Override
					public void done(ParseConfig config, ParseException e) {
						if (getActivity() == null) {
							return;
						}

						if (e == null) {
							if (Util.UPDATE != null && !Util.UPDATE.before(config.getDate(Util.UPDATE_PARAM))) {
								Logger.d(getClass(), mActInfoPosition + " " + "Update info is greater or equal the new one, no need to change: " + Util.UPDATE);
							} else {
								Logger.d(getClass(), mActInfoPosition + " UPDATE: " + (Util.UPDATE = config.getDate(Util.UPDATE_PARAM)));
							}
						} else {
							Logger.d(getClass(), mActInfoPosition + " UPDATE error, use cache config: " + (Util.UPDATE = ParseConfig.getCurrentConfig().getDate(Util.UPDATE_PARAM)));
						}
						if (Util.UPDATE == null) {
							// Means the cache is null, only when using the app first time and without network
							Calendar yesterDay = Calendar.getInstance();
							yesterDay.add(Calendar.DATE, -1);
							Logger.d(getClass(), mActInfoPosition + " UPDATE null: " + (Util.UPDATE = yesterDay.getTime()));
						}

						Logger.d(getClass(), mActInfoPosition + " " + "restart and trigger default.");
						QueryCondition queryCondition = new QueryCondition();
						queryCondition.push(Util.DB_TITLE, "");
						queryCondition.push(Util.DB_CITY, Util.FILTER_CITY.get(0));
						queryCondition.push(Util.DB_START_DATE, Util.FILTER_YEAR_CODE[0] + "-" + Util.FILTER_MONTH_CODE[0] + "-" + Util.FILTER_DATE_BIG_CODE[0]);
						queryCondition.push(Util.DB_END_DATE, Util.FILTER_YEAR_CODE[Util.FILTER_YEAR_CODE.length - 1] + "-"
								+ Util.FILTER_MONTH_CODE[Util.FILTER_MONTH_CODE.length - 1] + "-" + Util.FILTER_DATE_BIG_CODE[Util.FILTER_DATE_BIG_CODE.length - 1]);
						if (!isSameNoOffset(queryCondition)) {
							triggerQuery(queryCondition);
						} else {
							Logger.d(getClass(), mActInfoPosition + " " + "restart and been trigger default.");
						}
					}
				});
			} else {
				Util.mToast = ToastUtil.restart(getActivity(), Util.mToast, R.string.check_network_enable);
			}
		} else {
			Logger.d(getClass(), mActInfoPosition + " " + "default query has been down.");
		}
	}

	/**
	 * Load data with the given query data
	 */
	public void triggerQuery(QueryCondition queryCondition) {
		mCurQueryCondition = queryCondition;
		triggerQuery();
	}

	/**
	 * Load data with the current used query data
	 */
	public void triggerQuery() {
		if (MyUtils.SystemUtil.isNetworkEnabled(getActivity())) {
			resetQuery();

			Map<String, String> flurryData = new HashMap<String, String>();
			flurryData.put("Page", Util.ACT_CATAGORY_NAME_ON_DB[mActInfoPosition]);
			flurryData.put("From", String.valueOf(Util.QUERY_LIMIT * mCurQueryCondition.getOffset()));
			flurryData.put("To", String.valueOf(Util.QUERY_LIMIT * (mCurQueryCondition.getOffset() + 1)));
			FlurryAgent.logEvent("Query", flurryData);

			try {
				JSONObject props = new JSONObject();
				props.put("Page", Util.ACT_CATAGORY_NAME_ON_DB[mActInfoPosition]);
				props.put("From", Util.QUERY_LIMIT * mCurQueryCondition.getOffset());
				props.put("To", Util.QUERY_LIMIT * (mCurQueryCondition.getOffset() + 1));
				MixpanelAPI.getInstance(getActivity(), Util.MIXPANEL_KEY).track("Query", props);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

			Logger.d(getClass(), mActInfoPosition + " " + "Query from: " + (Util.QUERY_LIMIT * mCurQueryCondition.getOffset()) + "~"
					+ (Util.QUERY_LIMIT * (mCurQueryCondition.getOffset() + 1)));

			mCurQuery = ParseQuery.getQuery(Util.ACT_CATAGORY_NAME_ON_DB[mActInfoPosition]);
			mCurQueryCondition.setQuery(mCurQuery);
			mCurQuery.orderByAscending(Util.DB_END_DATE);
			mCurQuery.findInBackground(new FindCallback<QueryObject>() {
				@Override
				public void done(List<QueryObject> result, ParseException e) {
					if (result == null) {
						if (this != null) {
							if (getActivity() != null) {
								Util.mToast = MyUtils.ToastUtil.restart(getActivity(), Util.mToast, R.string.check_network_enable);
								// Here is to avoid network is fine while querying, but not after
								if (!mFirstFinish) {
									resetOldQueryCondition();
								}
							}
						}
						return;
					}

					if (this == null) {
						return;
					}

					if (getActivity() == null) {
						return;
					}

					Logger.d(getClass(), mActInfoPosition + " " + "Got result count: " + result.size());
					// No mater what the result is, the first query has been finished
					if (!mFirstFinish) {
						mFirstFinish = true;
					}

					List<ActInfo> actInfos = new ArrayList<ActInfo>();

					// Adapter will get create and set to listView while ActPage get created, so no matter how much result we get, we can always add
					// items
					// into adapter
					if ((result.size() == 0) && (getActivity() != null)) {
						Util.mToast = MyUtils.ToastUtil.restart(getActivity(), Util.mToast, Util.ACT_CATAGORY[mActInfoPosition]
								+ getActivity().getResources().getString(R.string.got_no_futher_result));

						if (mActPageList.getChildAt(0) instanceof ActInfoLoadView ||
								(mActPageList.getFirstVisiblePosition() + 1) != mActPageList.getCount()) {
							mActPageList.updateAdapters(actInfos);
						}
					} else if (result.size() != 0) {
						/**
						 * Default replacing the old one with current
						 */
						for (QueryObject queryObject : result) {
							actInfos.add(new ActInfo(queryObject));
						}
						mOldQueryCondition = mCurQueryCondition;
						mActPageList.updateAdapters(actInfos);
					}

					if (mLoadMore) {
						mLoadMore = false;
					}
				}
			});
		} else {
			Util.mToast = ToastUtil.restart(getActivity(), Util.mToast, R.string.check_network_enable);
		}
	}

	public void resetOldQueryCondition() {
		mOldQueryCondition = mCurQueryCondition;
	}

	public void resetActInfoItemAdapter() {
		if (mActPageList != null) {
			mActPageList.resetActInfoItemAdapter(mActInfoPosition);
		}
	}

	public void resetQuery() {
		if (mCurQuery != null) {
			mCurQuery.cancel();
			mCurQuery = null;
		}
	}
	// =====================================================================================================//
	public void setPageListDividerColor(String color) {
		if (mActPageList != null) {
			mActPageList.setDivider(new ColorDrawable(Color.parseColor(color)));
			mActPageList.setDividerHeight((int) Util.LIST_DIVIDER_HEIGHT);
			mActPageList.setPaintColor(color);
		}
	}

	/**
	 * Check if current query data is the same as the new one, but without offset.
	 */
	public boolean isSameNoOffset(QueryCondition queryCondition) {
		if ((mCurQueryCondition != null) && mCurQueryCondition.equals(queryCondition)) {
			return true;
		}
		return false;
	}
	// =====================================================================================================//
	@Override
	public void onDetach() {
		Logger.d(getClass(), mActInfoPosition + " " + "onDetach");

		super.onDetach();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		Logger.d(getClass(), mActInfoPosition + " " + "onPrepareOptionsMenu");

		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
		Logger.d(getClass(), mActInfoPosition + " " + "onInflate");

		super.onInflate(activity, attrs, savedInstanceState);
	}

	// =====================================================================================================//
	public int getActInfoPosition() {
		return mActInfoPosition;
	}

	public QueryCondition getOldQueryCondition() {
		return mOldQueryCondition;
	}

	public QueryCondition getCurQueryCondition() {
		return mCurQueryCondition;
	}

	public void setCurQueryCondition(QueryCondition curQueryCondition) {
		mCurQueryCondition = curQueryCondition;
	}

	public boolean getLoadMore() {
		return mLoadMore;
	}

	public void setLoadMore(boolean loadMore) {
		mLoadMore = loadMore;
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		return mActPageList.dispatchTouchEvent(ev);
	}

	public boolean isListMode() {
		return mActPageList.isListMode();
	}

	public boolean isGraphPagerOpen() {
		return mActPageList.isGraphPagerOpen();
	}

	public void closeGraphPager() {
		mActPageList.closeGraphPager();
	}

	public boolean isTicketPagePanelOpen() {
		return mActPageList.isTicketPagePanelOpen();
	}

	public void closeTicketPagePanel() {
		mActPageList.closeTicketPagePanel();
	}

	public boolean isSharePagePanelOpen() {
		return mActPageList.isSharePagePanelOpen();
	}

	public void closeSharePagePanel() {
		mActPageList.closeSharePagePanel();
	}

	public void backToListMode() {
		mActPageList.backToListMode();
	}

	public void moveToAct(int offset) {
		int to = checkOffset(offset);
		if (to != -1) {
			mActPageList.notifyDataSetChanged();
			mActPageList.setSelection(to);
			Util.mToast = MyUtils.ToastUtil.restart(getActivity(), Util.mToast, mActPageList.getActTitleAt(to));
		}
	}

	public int checkOffset(int direction) {
		int offset = mActPageList.getFirstVisiblePosition() + direction;

		if (offset < 0) {
			Util.mToast = MyUtils.ToastUtil.restart(getActivity(), Util.mToast, R.string.already_top);
			return -1;
		} else if (offset == mActPageList.getCount() ||
				(offset == mActPageList.getCount() - 1) && mActPageList.getActIdAt(offset).equals(Util.AD_STRING)) {
			Util.mToast = MyUtils.ToastUtil.restart(getActivity(), Util.mToast, R.string.already_buttom);
			return -1;
		} else if (mAdView != null && mActPageList.getActIdAt(offset).equals(Util.AD_STRING)) {
			offset += direction;
		}
		return offset;
	}

	public boolean isLoadMode() {
		return mActPageList.isLoadMode();
	}
}

