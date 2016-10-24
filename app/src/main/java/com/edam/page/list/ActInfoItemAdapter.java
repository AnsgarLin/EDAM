package com.edam.page.list;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.edam.main.R;
import com.edam.util.MyUtils;
import com.edam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.vpadn.ads.VpadnAdRequest;
import com.vpadn.ads.VpadnBanner;

import java.util.HashMap;
import java.util.Iterator;

public class ActInfoItemAdapter extends ArrayAdapter<ActInfo> {
	protected int mActPagePosition;
	protected DisplayImageOptions mImageLoaderOptions;

	protected LoadListener mLoadListener;
	protected HashMap<String, Bitmap> mImageList;

	protected View mAdView;

	protected LinearLayout mAdContainer;

	private float mAdViewHeight;

	public ActInfoItemAdapter(Context context, int actpagePosition, int resource) {
		super(context, resource);
		mActPagePosition = actpagePosition;
		mImageLoaderOptions = Util.getDisplayImageOptions(mActPagePosition);
		mLoadListener = new LoadListener();
		mImageList = new HashMap<String, Bitmap>();
		mAdViewHeight = MyUtils.TypedValueUtil.toPixel(getContext().getResources().getDisplayMetrics(), "dp", Util.AD_HEIGHT);
		mAdContainer = new LinearLayout(getContext());
		AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, (int) mAdViewHeight);
		mAdContainer.setLayoutParams(layoutParams);
		mAdContainer.setGravity(Gravity.CENTER);
		mAdContainer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
	}

	public void onDestroy() {
		if (mImageList.size() != 0) {
			Iterator<String> iterator = mImageList.keySet().iterator();
			while (iterator.hasNext()) {
				mImageList.get(iterator.next()).recycle();
			}
		}
		mAdContainer.setOnClickListener(null);
	}
	// =====================================================================================================//
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (isLoadMode()) {
			View actInfoItemView = MyUtils.InflateUtil.InflateReource(getContext(), R.layout.act_page_load, parent);
			actInfoItemView.getLayoutParams().height = parent.getHeight();
			return actInfoItemView;
		}

		if (getItem(position).getId().equals(Util.AD_STRING)) {
			mAdContainer.setBackgroundColor(Color.parseColor(Util.ACT_CATAGORY_COLOR[mActPagePosition]));
		    return mAdContainer;
		}

//		Logger.d(getClass(), position + " " + "getView " + ((AbsListView) parent).getFirstVisiblePosition() + "/" + ((AbsListView) parent).getLastVisiblePosition());

		View actInfoItemView = MyUtils.InflateUtil.InflateReource(getContext(), R.layout.act_info_list_item, parent);

		setText((TextView) actInfoItemView.findViewById(R.id.title), getItem(position).getTitle());
		setText((TextView) actInfoItemView.findViewById(R.id.location), getItem(position).getCity());
		setText((TextView) actInfoItemView.findViewById(R.id.date), getItem(position).getStartDate() + " ~ " + getItem(position).getEndDate());
		// Set act image
		if (!getItem(position).getCoverImgURL().contains("basic")) {
			((ImageView) actInfoItemView.findViewById(R.id.image)).setImageResource(R.drawable.ic_default);
		} else if (mImageList.get(getItem(position).getCoverImgURL()) == null) {
			ImageLoader.getInstance().displayImage(getItem(position).getCoverImgURL(), (ImageView) actInfoItemView.findViewById(R.id.image), mImageLoaderOptions, mLoadListener);
		} else {
			((ImageView) actInfoItemView.findViewById(R.id.image)).setImageBitmap(mImageList.get(getItem(position).getCoverImgURL()));
		}
		actInfoItemView.setBackgroundDrawable(MyUtils.DrawableUtil.getStateDrawableWithColor(Util.ACT_CATAGORY_COLOR[mActPagePosition], Util.TRANSPARENT));

		// Item will fade in/out while the new view is outside the screen at first
		// Calculate the total height of child on screen to avoid setting animation to target view, which may cause flash on screen
		if (convertView != null &&
				convertView.getHeight() * parent.getChildCount() > parent.getHeight() &&
				(position < ((AbsListView) parent).getFirstVisiblePosition() || position > ((AbsListView) parent).getLastVisiblePosition())) {
			AlphaAnimation animation = new AlphaAnimation(0f, 1f);
			animation.setDuration(Util.ANIMATION_BASIC_DURATION_MS / 2);
			actInfoItemView.setAnimation(animation);
		}

		return actInfoItemView;
	}

	protected void setText(TextView view, String text) {
		view.setText(text);
		view.setTextColor(MyUtils.ColorUtil.getColorStateList(Util.ACT_CATAGORY_COLOR[mActPagePosition], Util.LIGHT_BLACK, Util.BASIC_ALPHA));
	}

	private class LoadListener implements ImageLoadingListener {

		@Override
		public void onLoadingCancelled(String arg0, View arg1) {}

		@Override
		public void onLoadingComplete(String uri, View view, Bitmap bitmap) {
			if (mImageList != null) {
				mImageList.put(uri, bitmap);
			}
		}

		@Override
		public void onLoadingFailed(String uri, View view, FailReason failReason) {
			if (view != null && view instanceof ImageView) {
				((ImageView) view).setImageResource(R.drawable.ic_default);
			}
		}

		@Override
		public void onLoadingStarted(String arg0, View arg1) {}
	}
	// =====================================================================================================//
	protected HashMap<String, Bitmap> getImageList() {
		return mImageList;
	}

	protected void setImageList(HashMap<String, Bitmap> imageList) {
		mImageList = imageList;
	}

	public boolean isLoadMode() {
		return getCount() == 1 && getItem(0).getId() == null;
	}

	public void setAdView(View adView) {
		if (adView.getParent() != null) {
			((LinearLayout) adView.getParent()).removeView(adView);
		}

		mAdContainer.addView(mAdView = adView);
		if (adView instanceof AdView) {
			AdRequest adRequest = new AdRequest.Builder().build();
			((AdView) mAdView).loadAd(adRequest);
		} else {
			VpadnAdRequest adRequest = new VpadnAdRequest();
			adRequest.setEnableAutoRefresh(true);
			((VpadnBanner) mAdView).loadAd(adRequest);
		}
	}

	public void setPaintColor(String color) {
		mAdContainer.setBackgroundColor(Color.parseColor(color));
	}
}
