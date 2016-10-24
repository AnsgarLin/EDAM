package com.edam.page.list;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.edam.main.R;
import com.edam.page.detail.ActInfoDetailAdapter;
import com.edam.util.Logger;
import com.edam.util.MyUtils;
import com.edam.util.Util;
import com.flurry.android.FlurryAgent;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActPageList extends ListView{
	private ActInfoListAdapter mActInfoListAdapter;
	private ActInfoDetailAdapter mActInfoDetailAdapter;
	private int mActPagePosition;

	private Paint mPaint;
	private Path mPath;
	private float mEndScrollY;

	private boolean mIsChange;

	private View mAdView;
	public ActPageList(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ActPageList(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ActPageList(Context context) {
		super(context);
		init();
	}

	public void init() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);

		mPath = new Path();
	}

	public void onDestroy() {
		setOnItemClickListener(null);
		setOnScrollListener(null);
		setAdapter(null);

		mActInfoListAdapter.onDestroy();
		mActInfoListAdapter = null;

		mActInfoDetailAdapter.onDestroy();
		mActInfoDetailAdapter = null;
	}
	// =====================================================================================================//
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (((getAdapter() != null) && (getCount() > 0)) && ((getAdapter() instanceof ActInfoListAdapter)) && (getChildCount() > 0)) {
			float[] scrollBarPos = MyUtils.ListViewUtil.getScrollBarDrawPos(this, getChildAt(0).getHeight(), Util.LIST_DIVIDER_HEIGHT, getCount(),
					getWidth(), Util.LIST_DIVIDER_HEIGHT * 2, Util.CUSTOM_LIST_SCROLL_HEIGHT, Util.LIST_DIVIDER_HEIGHT);

			canvas.drawRect(scrollBarPos[0], scrollBarPos[1], scrollBarPos[2], scrollBarPos[3], mPaint);

			mPath.reset();
			mPath.moveTo(scrollBarPos[2], scrollBarPos[1] - 5);
			mPath.rLineTo(-Util.LIST_DIVIDER_HEIGHT * 2, 0);
			mPath.rLineTo(0, -Util.LIST_DIVIDER_HEIGHT * 2);
			mPath.rLineTo(Util.LIST_DIVIDER_HEIGHT * 2, 0);

			mPath.moveTo(scrollBarPos[2], scrollBarPos[3] + 5);
			mPath.rLineTo(-Util.LIST_DIVIDER_HEIGHT * 2, 0);
			mPath.rLineTo(0, Util.LIST_DIVIDER_HEIGHT * 2);
			mPath.rLineTo(Util.LIST_DIVIDER_HEIGHT * 2, 0);
			mPath.close();
			canvas.drawPath(mPath, mPaint);
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// Ignore move action to disable the scroll action on list view

		if((getAdapter() instanceof ActInfoDetailAdapter)) {// && (ev.getAction() == MotionEvent.ACTION_MOVE)) {
			return mActInfoDetailAdapter.dispatchTouchEvent(ev);
		}

		return super.dispatchTouchEvent(ev);
	}
	// =====================================================================================================//
	public void changeListMode(int position) {
		try {
			if (!mIsChange) {
				Map<String, String> flurryData = new HashMap<String, String>();
				JSONObject props = new JSONObject();
				flurryData.put("Page", Util.ACT_CATAGORY_NAME_ON_DB[mActPagePosition]);
				props.put("Page", Util.ACT_CATAGORY_NAME_ON_DB[mActPagePosition]);

				if (((getAdapter() instanceof ActInfoDetailAdapter))) {
					Logger.d(getClass(), "change to list");
					flurryData.put("ModeFrom", "page");
					flurryData.put("ModeTo", "list");
					props.put("ModeFrom", "page");
					props.put("ModeTo", "list");
					mActInfoDetailAdapter.onDestroy();
					changeMode(mActInfoListAdapter, position);
				} else {
					Logger.d(getClass(), "change to page");
					flurryData.put("ModeFrom", "list");
					flurryData.put("ModeTo", "page");
					props.put("ModeFrom", "list");
					props.put("ModeTo", "page");
					mActInfoDetailAdapter.setImageList(mActInfoListAdapter.getImageList());
					changeMode(mActInfoDetailAdapter, position);
					Util.mToast = MyUtils.ToastUtil.restart(getContext(), Util.mToast, mActInfoListAdapter.getItem(position).getTitle());
				}
				flurryData.put("Title", mActInfoListAdapter.getItem(position).getTitle());
				props.put("Title", mActInfoListAdapter.getItem(position).getTitle());
		        FlurryAgent.logEvent("ChangeMode", flurryData);
				MixpanelAPI.getInstance(getContext(), Util.MIXPANEL_KEY).track("ChangeMode", props);
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
	}

	public void clearActInfoItemAdapter() {
		if (mActInfoListAdapter != null) {
			mActInfoListAdapter.clear();
		}
		if (mActInfoDetailAdapter != null) {
			mActInfoDetailAdapter.clear();
		}
	}

	public void resetActInfoItemAdapter(int actInfoPosition) {
		clearActInfoItemAdapter();

		mActInfoListAdapter = new ActInfoListAdapter(getContext(), actInfoPosition, R.layout.act_info_list_item);
		if (mAdView != null) {
			mActInfoListAdapter.setAdView(mAdView);
		}

		mActInfoDetailAdapter = new ActInfoDetailAdapter(getContext(), actInfoPosition, 0);
		mActInfoListAdapter.add(new ActInfo());

		setAdapter(mActInfoListAdapter);
	}

	public void updateAdapters(List<ActInfo> actInfos) {
		Logger.d(getClass(), mActPagePosition + " updateAdapters");

		if (isLoadMode()) {
			clearActInfoItemAdapter();
		}
		if (actInfos.size() != 0 && mAdView != null) {
			if (actInfos.size() >= Util.AD_LIMIT) {
				actInfos.add(Util.AD_POS, new ActInfo(Util.AD_STRING));
			} else {
				actInfos.add(new ActInfo(Util.AD_STRING));
			}
		}
		mActInfoListAdapter.addAll(actInfos);
		mActInfoDetailAdapter.addAll(actInfos);
	}
	// =====================================================================================================//
	public void changeMode(final ArrayAdapter<?> arrayAdapter, final int position) {
		ArrayList<ObjectAnimator> list = new ArrayList<ObjectAnimator>();
		addAnimation(list, this, "alpha", 1f, 0f);

		AnimatorSet set = new AnimatorSet();
		set.playTogether(list.toArray(new ObjectAnimator[list.size()]));
		set.setInterpolator(new DecelerateInterpolator(3f));
		set.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator arg0) {
				mIsChange = true;
			}
			@Override
			public void onAnimationRepeat(Animator arg0) {}
			@Override
			public void onAnimationEnd(Animator arg0) {
				setAdapter(arrayAdapter);
				setSelection(position);
				showUpView(position);
			}
			@Override
			public void onAnimationCancel(Animator arg0) {}
		});
		set.setDuration(Util.ANIMATION_BASIC_DURATION_MS * 2 + Util.ANIMATION_BASIC_DURATION_MS / 2).start();
	}

	private void showUpView(final int position) {
		ArrayList<ObjectAnimator> list = new ArrayList<ObjectAnimator>();
		addAnimation(list, this, "alpha", 0f, 1f);

		AnimatorSet set = new AnimatorSet();
		set.playTogether(list.toArray(new ObjectAnimator[list.size()]));
		set.setInterpolator(new DecelerateInterpolator(3f));
		set.addListener(new AnimatorListener() {
			@Override
			public void onAnimationStart(Animator arg0) {}
			@Override
			public void onAnimationRepeat(Animator arg0) {}
			@Override
			public void onAnimationEnd(Animator arg0) {
				mIsChange = false;

				if (getAdapter() instanceof ActInfoDetailAdapter) {
//					((ActInfoListAdapter) getAdapter()).showUpView(getChildAt(0), position);
				}
			}
			@Override
			public void onAnimationCancel(Animator arg0) {}
		});
		set.setDuration(Util.ANIMATION_BASIC_DURATION_MS * 2 + Util.ANIMATION_BASIC_DURATION_MS / 2).start();
	}

	public void addAnimation(List<ObjectAnimator> list, View view, String property, float start, float end) {
		list.add(ObjectAnimator.ofFloat(view, property, start, end));
	}
	// =====================================================================================================//
	public void setPaintColor(String color) {
		mPaint.setColor(Color.parseColor(color));
		if (getAdapter() instanceof ActInfoDetailAdapter) {
			mActInfoDetailAdapter.setPaintColor(color);
		} else {
			mActInfoListAdapter.setPaintColor(color);
		}
	}

	public boolean isListMode() {
		if (((getAdapter() instanceof ActInfoDetailAdapter))) {
			return false;
		}
		return true;
	}

	public boolean isGraphPagerOpen() {
		if (((getAdapter() instanceof ActInfoDetailAdapter))) {
			return mActInfoDetailAdapter.isGraphPagerOpen();
		}
		return false;
	}

	public void closeGraphPager() {
		mActInfoDetailAdapter.closeGraphPager();
	}

	public boolean isTicketPagePanelOpen() {
		if (((getAdapter() instanceof ActInfoDetailAdapter))) {
			return mActInfoDetailAdapter.isTicketPagePanelOpen();
		}
		return false;
	}

	public void closeTicketPagePanel() {
		mActInfoDetailAdapter.closeTicketPagePanel();
	}

	public boolean isSharePagePanelOpen() {
		if (((getAdapter() instanceof ActInfoDetailAdapter))) {
			return mActInfoDetailAdapter.isSharePagePanelOpen();
		}
		return false;
	}

	public void closeSharePagePanel() {
		mActInfoDetailAdapter.closeSharePagePanel();
	}

	public void backToListMode() {
		changeListMode(getFirstVisiblePosition());
	}

	public void setActPagePosition(int actPagePosition) {
		mActPagePosition = actPagePosition;
	}

	public boolean isLoadMode() {
		return mActInfoListAdapter.isLoadMode();
	}

	public void notifyDataSetChanged() {
		mActInfoDetailAdapter.notifyDataSetChanged();
	}

	public void setAdView(View adView) {
		mAdView = adView;
	}

	public String getActTitleAt(int position) {
		return mActInfoListAdapter.getItem(position).getTitle();
	}

	public String getActIdAt(int position) {
		return mActInfoListAdapter.getItem(position).getId();
	}
}
