package com.edam.page.detail;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edam.page.list.ActInfoItemAdapter;
import com.edam.util.Logger;
import com.edam.util.Util;

public class ActInfoDetailAdapter extends ActInfoItemAdapter {

	private RelativeLayout mFroudLayout;
	private FrameLayout mDetailPageWrap;
	private BackFrame mBackFrame;
	private LinearLayout mContentWrap;
	private TextView mTitleUpSpace;
	private Title mTitle;
	private TextView mDateUpSpace;
	private Date mDate;
	private TextView mLocateUpSpace;
	private Locate mLocate;
	private TextView mBriefUpSpace;
	private Brief mBrief;
	private TextView mGraphUpSpace;
	private Graph mGraph;
	private Graffiti mGraffiti;
	private RelativeLayout mTitleUpLayerWrap;
	private RelativeLayout mTitleButtonWrap;
	private GoToSharePage mGoToSharePage;
	private GoToActPage mGoToActPage;
	private GoToTicketPage mGoToTicketPage;
	private GoToFreeTicketPage mGoToFreeTicketPage;
	private GoToGetTicketPage mGoToGetTicketPage;

	public ActInfoDetailAdapter(Context context, int actpagePosition, int resource) {
		super(context, actpagePosition, resource);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			initFroundLayout(position, parent);

			initDetailWrap();
			mFroudLayout.addView(mDetailPageWrap);

			initBackFrame();
			mDetailPageWrap.addView(mBackFrame);

			initContentWrap();
			mDetailPageWrap.addView(mContentWrap);

			initTitleUpSpace();
			mContentWrap.addView(mTitleUpSpace);
			initTitle();
			mContentWrap.addView(mTitle);

			initDateUpSpace();
			mContentWrap.addView(mDateUpSpace);
			initDate();
			mContentWrap.addView(mDate);

			initLocateUpSpace();
			mContentWrap.addView(mLocateUpSpace);
			initLocate();
			mContentWrap.addView(mLocate);

			initBriefUpSpace();
			mContentWrap.addView(mBriefUpSpace);
			initBrief();
			mContentWrap.addView(mBrief);

			initGraphUpSpace();
			mContentWrap.addView(mGraphUpSpace);

			initGraph();
			mContentWrap.addView(mGraph);

			initGraffiti();
			mContentWrap.addView(mGraffiti);

			initTitleUpLayerWrap();
			mDetailPageWrap.addView(mTitleUpLayerWrap);

			initTitleButtonWrap();
			mTitleUpLayerWrap.addView(mTitleButtonWrap);

			initGoToSharePage(position);
			mTitleButtonWrap.addView(mGoToSharePage);

			initGoToTicketPage(position);
			mTitleButtonWrap.addView(mGoToTicketPage);
			initGoToFreeTicketPage(position);
			mTitleButtonWrap.addView(mGoToFreeTicketPage);
			initGoToGetTicketPage(position);
			mTitleButtonWrap.addView(mGoToGetTicketPage);

			if (getItem(position).getIsFree().equals("1")) {
				mGoToFreeTicketPage.setVisibility(View.GONE);
				mGoToGetTicketPage.setVisibility(View.GONE);
			} else if (getItem(position).getIsFree().equals("2")) {
				mGoToTicketPage.setVisibility(View.GONE);
				mGoToGetTicketPage.setVisibility(View.GONE);
			} else if (getItem(position).getIsFree().equals("3")) {
				mGoToTicketPage.setVisibility(View.GONE);
				mGoToFreeTicketPage.setVisibility(View.GONE);
			}

			initGoToActPage(position);
			mTitleButtonWrap.addView(mGoToActPage);

			convertView = mFroudLayout;
		}
		else {
			mTitle = (Title) convertView.findViewById(Util.TITLE_ID);
			mTitle.resetState();

			mDate = (Date) convertView.findViewById(Util.DATE_ID);
			mLocate = (Locate) convertView.findViewById(Util.LOCATEE_ID);
			mBrief = (Brief) convertView.findViewById(Util.BRIEF_ID);

			mGraph = (Graph) convertView.findViewById(Util.GRAPH_ID);
			mGraph.onDestroy();

			mGraffiti = (Graffiti) convertView.findViewById(Util.GRAFFITI_ID);

			mGoToSharePage = (GoToSharePage) convertView.findViewById(Util.GOTO_BUTTON_SHARE_ID);

			mGoToTicketPage = (GoToTicketPage) convertView.findViewById(Util.GOTO_BUTTON_TICKET_ID);
			mGoToFreeTicketPage = (GoToFreeTicketPage) convertView.findViewById(Util.GOTO_BUTTON_FREE_TICKET_ID);
			mGoToGetTicketPage = (GoToGetTicketPage) convertView.findViewById(Util.GOTO_BUTTON_GET_TICKET_ID);

			Logger.d(getClass(), convertView.toString());
			if (getItem(position).getIsFree().equals("1")) {
				mGoToTicketPage.setVisibility(View.VISIBLE);
				mGoToFreeTicketPage.setVisibility(View.GONE);
				mGoToGetTicketPage.setVisibility(View.GONE);
				mGoToTicketPage.setTicketURLs(getFeasibleURLs(getItem(position).getTicketURL().split("\\(")));
			} else if (getItem(position).getIsFree().equals("2")) {
				mGoToTicketPage.setVisibility(View.GONE);
				mGoToFreeTicketPage.setVisibility(View.VISIBLE);
				mGoToGetTicketPage.setVisibility(View.GONE);
			} else if (getItem(position).getIsFree().equals("3")) {
				mGoToTicketPage.setVisibility(View.GONE);
				mGoToFreeTicketPage.setVisibility(View.GONE);
				mGoToGetTicketPage.setVisibility(View.VISIBLE);
			}

			mGoToActPage = (GoToActPage) convertView.findViewById(Util.GOTO_BUTTON_ACT_ID);
		}


		setPaintColor(Util.ACT_CATAGORY_COLOR[mActPagePosition]);
		setText(mTitle, getItem(position).getTitle());

		setText(mDate, getItem(position).getStartDate() + " ~ " + getItem(position).getEndDate() + ", " + getItem(position).getStartTime() + " ~ "
				+ getItem(position).getEndTime());
		setText(mLocate, getItem(position).getPlace());
		setText(mBrief.getTextView(), getItem(position).getBrief());
		mBrief.setScrollY(0);

		String[] tmpURLs = getItem(position).getImgURL().split(",");
		String[] imgURLs;
		// Set title graph
		if (!getItem(position).getCoverImgURL().contains("basic")) {
			imgURLs = new String[tmpURLs.length];
			for(int i = 0; i < tmpURLs.length; i++) {
				imgURLs[i] = tmpURLs[i];
			}
		} else {
			if(tmpURLs[0].equals("")) {
				imgURLs = new String[]{getItem(position).getCoverImgURL()};
			} else {
				imgURLs = new String[1 + tmpURLs.length];
				imgURLs[0] = getItem(position).getCoverImgURL();
				for(int i = 0; i < tmpURLs.length; i++) {
					imgURLs[i + 1] = tmpURLs[i];
				}
			}
		}
		// Set graph
		mGraph.addGraph(imgURLs);

		// Set act url
		mGoToSharePage.setShareURL(getItem(position).getTitle(), getItem(position).getActURL());
		mGoToActPage.setActURL(getItem(position).getActURL());

		return convertView;
	}

	private void initFroundLayout(int position, ViewGroup parent) {
		mFroudLayout = new RelativeLayout(getContext());
		mFroudLayout.setLayoutParams(new AbsListView.LayoutParams(parent.getWidth(), parent.getHeight()));
	}

	private void initDetailWrap() {
		mDetailPageWrap = new FrameLayout(getContext());
		RelativeLayout.LayoutParams detailPageWrapParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		detailPageWrapParams.setMargins((int) Util.DETAIL_PAGE_WRAP_MARGIN, (int) Util.DETAIL_PAGE_WRAP_MARGIN, (int) Util.DETAIL_PAGE_WRAP_MARGIN,
				(int) Util.DETAIL_PAGE_WRAP_MARGIN);
		mDetailPageWrap.setLayoutParams(detailPageWrapParams);
	}

	private void initBackFrame() {
		mBackFrame = new BackFrame(getContext());
		FrameLayout.LayoutParams backFrameParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		backFrameParams.setMargins((int) Util.BACK_FRAME_MARGIN, (int) Util.BACK_FRAME_MARGIN, (int) Util.BACK_FRAME_MARGIN,
				(int) Util.BACK_FRAME_BUTTOM_MARGIN);
		mBackFrame.setLayoutParams(backFrameParams);
		mBackFrame.setBackgroundColor(Color.TRANSPARENT);
	}

	private void initContentWrap() {
		mContentWrap = new LinearLayout(getContext());
		FrameLayout.LayoutParams contentWrapParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mContentWrap.setLayoutParams(contentWrapParams);
		mContentWrap.setOrientation(LinearLayout.VERTICAL);
	}

	private void initTitleUpSpace() {
		mTitleUpSpace = new TextView(getContext());
		LinearLayout.LayoutParams titleUpSpaceParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) Util.TITLE_UP_SPACE_HEIGHT);
		mTitleUpSpace.setLayoutParams(titleUpSpaceParams);
	}

	private void initTitle() {
		mTitle = new Title(getContext(), (int) Util.GOTO_BUTTON_WRAP_WIDTH, (int) Util.TITLE_PADDING_TOP);
		LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) Util.TITLE_HEIGHT);
		mTitle.setLayoutParams(titleParams);
		mTitle.setLines(2);
		mTitle.setEllipsize(TruncateAt.END);
		// Space between lines, must equals the top padding of the first line
		mTitle.setLineSpacing(Util.TITLE_PADDING_TOP, 1);
		mTitle.setTextColor(Color.parseColor(Util.LIGHT_BLACK));
		mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, Util.TITLE_TEXT_WRAP_HEIGHT * Util.TEXT_SIZE_ERROR);
		mTitle.setIncludeFontPadding(false);
		mTitle.setPadding((int) Util.BACK_FRAME_MARGIN, (int) Util.TITLE_PADDING_TOP, (int) Util.TITLE_PADDING_RIGHT, 0);
		mTitle.setFocusable(true);
		mTitle.setFocusableInTouchMode(true);
		mTitle.setId(Util.TITLE_ID);
	}

	private void initDateUpSpace() {
		mDateUpSpace = new TextView(getContext());
		LinearLayout.LayoutParams dateUpSpaceParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) Util.DATE_UP_SPACE_HEIGHT);
		mDateUpSpace.setLayoutParams(dateUpSpaceParams);
	}

	private void initDate() {
		mDate = new Date(getContext());
		LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) Util.DATE_HEIGHT);
		mDate.setLayoutParams(dateParams);
		mDate.setSingleLine();
		mDate.setTextColor(Color.parseColor(Util.LIGHT_BLACK));
		mDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, Util.DATE_IN_HEIGHT * Util.TEXT_SIZE_ERROR);
		mDate.setIncludeFontPadding(false);
		// Note: set top padding to make the text can be at the vertical center exactly
		mDate.setPadding((int) Util.BACK_FRAME_MARGIN, 1, (int) Util.BACK_FRAME_MARGIN, 0);
		mDate.setGravity(Gravity.CENTER_VERTICAL);
		mDate.setId(Util.DATE_ID);
	}

	private void initLocateUpSpace() {
		mLocateUpSpace = new TextView(getContext());
		LinearLayout.LayoutParams locateUpSpaceParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) Util.LOCATE_UP_SPACE_HEIGHT);
		mLocateUpSpace.setLayoutParams(locateUpSpaceParams);
	}

	private void initLocate() {
		mLocate = new Locate(getContext());
		LinearLayout.LayoutParams locateParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) Util.LOCATE_HEIGHT);
		mLocate.setLayoutParams(locateParams);
		mLocate.setSingleLine();
		mLocate.setTextColor(Color.parseColor(Util.LIGHT_BLACK));
		mLocate.setTextSize(TypedValue.COMPLEX_UNIT_PX, Util.DATE_IN_HEIGHT * Util.TEXT_SIZE_ERROR);
		mLocate.setIncludeFontPadding(false);
		// Note: set top padding to make the text can be at the vertical center exactly
		mLocate.setPadding((int) Util.BACK_FRAME_MARGIN, 1, (int) Util.BACK_FRAME_MARGIN, 0);
		mLocate.setGravity(Gravity.CENTER_VERTICAL);
		mLocate.setId(Util.LOCATEE_ID);
	}

	private void initBriefUpSpace() {
		mBriefUpSpace = new TextView(getContext());
		// Use Math.ceil to fix the missing part when cast float into int
		LinearLayout.LayoutParams briefUpSpaceParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) Util.BRIEF_UP_SPACE_HEIGHT);
		mBriefUpSpace.setLayoutParams(briefUpSpaceParams);
	}

	private void initBrief() {
		mBrief = new Brief(getContext());
		LinearLayout.LayoutParams briefParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) Util.BRIEF_HEIGHT);
		mBrief.setLayoutParams(briefParams);
		mBrief.setTextColor(Color.parseColor(Util.LIGHT_BLACK));
		mBrief.setTextSize(TypedValue.COMPLEX_UNIT_PX, mDate.getTextSize());
		mBrief.setIncludeFontPadding(false);
		// Disable scroll bar
		mBrief.setVerticalScrollBarEnabled(false);
		// Disable the edge effect from top and bottom of listview
		mBrief.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
		// Stretch content height to fill the view
		mBrief.setFillViewport(true);
		mBrief.setPadding((int) Util.BACK_FRAME_STROKE_WIDTH, (int) Util.BACK_FRAME_STROKE_WIDTH, (int) Util.BACK_FRAME_STROKE_WIDTH,
				(int) Util.BACK_FRAME_STROKE_WIDTH);
		mBrief.setId(Util.BRIEF_ID);
	}

	private void initGraphUpSpace() {
		mGraphUpSpace = new TextView(getContext());
		LinearLayout.LayoutParams graphUpSpaceParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) Util.GRAPH_UP_SPACE_HEIGHT);
		mGraphUpSpace.setLayoutParams(graphUpSpaceParams);
	}

	private void initGraph() {
		mGraph = new Graph(getContext(), mImageLoaderOptions);
		LinearLayout.LayoutParams graphParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) Util.GRAPH_HEIGHT);
		mGraph.setLayoutParams(graphParams);
		mGraph.setPadding((int) Util.BACK_FRAME_STROKE_WIDTH, (int) Util.BACK_FRAME_STROKE_WIDTH, (int) Util.BACK_FRAME_STROKE_WIDTH,
				(int) Util.BACK_FRAME_STROKE_WIDTH);
		// Disable scroll bar
		mGraph.setHorizontalScrollBarEnabled(false);
		// Disable the edge effect from top and bottom of listview
		mGraph.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
		// Stretch content height to fill the view
		mGraph.setFillViewport(true);
		mGraph.setId(Util.GRAPH_ID);
	}

	private void initGraffiti() {
		mGraffiti = new Graffiti(getContext());
		LinearLayout.LayoutParams graffitiParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) Util.GRAFFITI_HEIGHT);
		mGraffiti.setLayoutParams(graffitiParams);
		mGraffiti.setBackgroundColor(Color.TRANSPARENT);
		mGraffiti.setId(Util.GRAFFITI_ID);
	}

	private void initTitleUpLayerWrap() {
		mTitleUpLayerWrap = new RelativeLayout(getContext());
		FrameLayout.LayoutParams titleUpLayerWrapParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				(int) Util.TITLE_UP_LAYER_WRAP_HEIGHT);
		mTitleUpLayerWrap.setLayoutParams(titleUpLayerWrapParams);
	}

	private void initTitleButtonWrap() {
		mTitleButtonWrap = new RelativeLayout(getContext());
		RelativeLayout.LayoutParams titleButtonWrapParams = new RelativeLayout.LayoutParams((int) Util.GOTO_BUTTON_WRAP_WIDTH,
				(int) Util.GOTO_BUTTON_WRAP_HEIGHT);
		titleButtonWrapParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		titleButtonWrapParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		mTitleButtonWrap.setLayoutParams(titleButtonWrapParams);
	}

	private void initGoToSharePage(final int position) {
		mGoToSharePage = new GoToSharePage(getContext());
		RelativeLayout.LayoutParams goToActPageParams = new RelativeLayout.LayoutParams((int) Util.GOTO_BUTTON_SZIE, (int) Util.GOTO_BUTTON_SZIE);
		goToActPageParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		mGoToSharePage.setLayoutParams(goToActPageParams);
		mGoToSharePage.setId(Util.GOTO_BUTTON_SHARE_ID);
	}

	private void initGoToFreeTicketPage(final int position) {
		mGoToFreeTicketPage = new GoToFreeTicketPage(getContext());
		RelativeLayout.LayoutParams goToFreeTicketPageParams = new RelativeLayout.LayoutParams((int) Util.GOTO_BUTTON_SZIE, (int) Util.GOTO_BUTTON_SZIE);
		goToFreeTicketPageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		mGoToFreeTicketPage.setLayoutParams(goToFreeTicketPageParams);
		mGoToFreeTicketPage.setId(Util.GOTO_BUTTON_FREE_TICKET_ID);
	}

	private void initGoToGetTicketPage(final int position) {
		mGoToGetTicketPage = new GoToGetTicketPage(getContext());
		RelativeLayout.LayoutParams goToGetTicketPageParams = new RelativeLayout.LayoutParams((int) Util.GOTO_BUTTON_SZIE, (int) Util.GOTO_BUTTON_SZIE);
		goToGetTicketPageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		mGoToGetTicketPage.setLayoutParams(goToGetTicketPageParams);
		mGoToGetTicketPage.setId(Util.GOTO_BUTTON_GET_TICKET_ID);
	}

	private void initGoToTicketPage(final int position) {
		mGoToTicketPage = new GoToTicketPage(getContext(), getFeasibleURLs(getItem(position).getTicketURL().split("\\(")));
		RelativeLayout.LayoutParams goToTicketPageParams = new RelativeLayout.LayoutParams((int) Util.GOTO_BUTTON_SZIE, (int) Util.GOTO_BUTTON_SZIE);
		goToTicketPageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		mGoToTicketPage.setLayoutParams(goToTicketPageParams);
		mGoToTicketPage.setId(Util.GOTO_BUTTON_TICKET_ID);
	}

	private void initGoToActPage(final int position) {
		mGoToActPage = new GoToActPage(getContext());
		RelativeLayout.LayoutParams goToActPageParams = new RelativeLayout.LayoutParams((int) Util.GOTO_BUTTON_SZIE, (int) Util.GOTO_BUTTON_SZIE);
		goToActPageParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		mGoToActPage.setLayoutParams(goToActPageParams);
		mGoToActPage.setId(Util.GOTO_BUTTON_ACT_ID);
	}

	private ArrayList<String> getFeasibleURLs(String[] ticketURLs) {
		ArrayList<String> avalibleURLs = new ArrayList<String>();
		for(String ticketURL : ticketURLs) {
			if (!ticketURL.equals("") && (ticketURL.charAt(0) == '0')) {
				avalibleURLs.add(ticketURL.substring(2));
			}
		}
		return avalibleURLs;
	}
	// =====================================================================================================//
	@Override
	public void onDestroy() {
		if (mFroudLayout != null) {
			mFroudLayout.setOnTouchListener(null);
		}

		if (mGraph != null) {
			mGraph.onDestroy();
		}

		if (mGoToSharePage != null) {
			mGoToSharePage.setOnClickListener(null);
		}

		if (mGoToTicketPage != null) {
			mGoToTicketPage.onDestroy();
		}

		if (mGoToFreeTicketPage != null) {
			mGoToFreeTicketPage.setOnClickListener(null);
		}

		if (mGoToGetTicketPage != null) {
			mGoToGetTicketPage.setOnClickListener(null);
		}

		if (mGoToActPage != null) {
			mGoToActPage.setOnClickListener(null);
		}

		if (mTitle != null) {
			mTitle.setOnTouchListener(null);
		}
	}

	public void clearCache() {
		if (mGraph != null) {
			mGraph.clearCache();
		}
	}
	// =====================================================================================================//
	@Override
	public void setPaintColor(String color) {
		// The last view remain null, means the view is not created yet, so do nothing
		if (mGraffiti != null) {
			mBackFrame.setPaintColor(color);
			mTitle.setBackgroundColor(Color.parseColor(color));
			mDate.setBackgroundColor(Color.parseColor(color));
			mDate.setPaintColor(color);
			mLocate.setBackgroundColor(Color.parseColor(color));
			mLocate.setPaintColor(color);
			mBrief.setBackgroundColor(color);
			mGraph.setBackgroundColor(color);
			mGraffiti.setPaintColor(color);
		}

		if (mGoToSharePage != null) {
			mGoToSharePage.setPaintColor(color);
		}

		if (mGoToTicketPage != null) {
			mGoToTicketPage.setPaintColor(color);
		}

		if (mGoToFreeTicketPage != null) {
			mGoToFreeTicketPage.setPaintColor(color);
		}

		if (mGoToGetTicketPage != null) {
			mGoToGetTicketPage.setPaintColor(color);
		}

		if (mGoToActPage != null) {
			mGoToActPage.setPaintColor(color);
		}
	}

	public boolean dispatchTouchEvent(MotionEvent ev) {
		return mFroudLayout.dispatchTouchEvent(ev);
	}

	public boolean isGraphPagerOpen() {
		if (mGraph != null) {
			return mGraph.isGraphPagerOpen();
		}
		return false;
	}

	public void closeGraphPager() {
		if (mGraph != null) {
			mGraph.closeGraphPager();
		}
	}

	public boolean isTicketPagePanelOpen() {
		if (mGoToTicketPage != null) {
			return mGoToTicketPage.isTicketPagePanelOpen();
		}
		return false;
	}

	public void closeTicketPagePanel() {
		if (mGoToTicketPage != null) {
			mGoToTicketPage.closeTicketPagePanel();
		}
	}

	public boolean isSharePagePanelOpen() {
		if (mGoToSharePage != null) {
			return mGoToSharePage.isSharePagePanelOpen();
		}
		return false;
	}

	public void closeSharePagePanel() {
		if (mGoToSharePage != null) {
			mGoToSharePage.closeSharePagePanel();
		}
	}
}
