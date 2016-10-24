package com.edam.page.detail;

import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edam.main.R;
import com.edam.util.Logger;
import com.edam.util.Util;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * The images in this view will be release while change back to list mode
 */
public class Graph extends HorizontalScrollView {
	private LinearLayout mGraphWrapper;

	public class ImageMap {
		ImageView mImageView;
		Bitmap mBitmap;
		public ImageMap(ImageView imageView, Bitmap bitmap) {
			mImageView = imageView;
			mBitmap = bitmap;
		}

		public ImageView getImageView() {
			return mImageView;
		}
		public void setImageView(ImageView imageView) {
			this.mImageView = imageView;
		}
		public Bitmap getBitmap() {
			return mBitmap;
		}
		public void setBitmap(Bitmap bitmap) {
			this.mBitmap = bitmap;
		}

		public void clear() {
			mImageView = null;
			mBitmap = null;
		}
	}
	private HashMap<String, ImageMap> mImageMaps;
	private GraphViewPager mGraphPagerWrap;
	// Store image url of act which will be show in graph wrap
	private String[] mImgURLs;
	private String mActPageColor;

	private GraphImageLoadListener mGraphImageLoadListener;
	private DisplayImageOptions mImageLoaderOptions;

	public Graph(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public Graph(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public Graph(Context context, DisplayImageOptions imageLoaderOptions) {
		super(context);
		mImageLoaderOptions = imageLoaderOptions;
		init();
	}

	public void init() {
		mGraphImageLoadListener = new GraphImageLoadListener();

		mGraphWrapper = new LinearLayout(getContext());
		mGraphWrapper.setOrientation(LinearLayout.HORIZONTAL);
		addView(mGraphWrapper);

		mImageMaps = new HashMap<String, ImageMap>();
	}

	public void onDestroy() {
		closeGraphPager();

		Iterator<String> iterator = mImageMaps.keySet().iterator();
		while (iterator.hasNext()) {
			ImageMap imageMap = mImageMaps.get(iterator.next());
			ImageLoader.getInstance().cancelDisplayTask(imageMap.getImageView());
			imageMap.getImageView().setImageBitmap(null);
			imageMap.getImageView().setOnClickListener(null);
			imageMap.clear();
			imageMap = null;
		}
		mImageMaps.clear();

		mGraphWrapper.removeAllViews();
	}

	public void clearCache() {
		UnlimitedDiscCache cache = (UnlimitedDiscCache) ImageLoader.getInstance().getDiskCache();
		if (cache != null) {
			for (String mImgURL : mImgURLs) {
				cache.get(mImgURL).delete();
			}
		}
	}
	// =====================================================================================================//
	public void addGraph(String[] imgURLs) {
		mImgURLs = imgURLs;

		if (mImgURLs.length == 1 && mImgURLs[0].equals("")) {
			mGraphWrapper.addView(initNoImageTxt());
			return;
		}

		int i = 0;
		int j = 0;
		for (; j < (mImgURLs.length - 1); j++) {
			if (!mImgURLs[j].equals("")) {
				mGraphWrapper.addView(initImageView(i, mImgURLs[j], false));
				i++;
			}
		}
		mGraphWrapper.addView(initImageView(i, mImgURLs[j], true));
	}

	private TextView initNoImageTxt() {
		TextView txt = new TextView(getContext());
		txt.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		txt.setGravity(Gravity.CENTER);
		txt.setTextSize(TypedValue.COMPLEX_UNIT_PX, Util.DATE_IN_HEIGHT * Util.TEXT_SIZE_ERROR);
		txt.setText(R.string.no_relative_image);
		return txt;
	}

	private ImageView initImageView(int index, String imgURL, boolean isEnd) {
		ImageView img;

		LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams((int) Util.GRAPH_IN_HEIGHT, (int) Util.GRAPH_IN_HEIGHT);
		if (isEnd) {
			imgParams.setMargins(0, 0, 0, 0);
		} else {
			imgParams.setMargins(0, 0, (int) Util.BACK_FRAME_STROKE_WIDTH, 0);
		}
		img = new ImageView(getContext());
		img.setLayoutParams(imgParams);
		img.setScaleType(ScaleType.CENTER_CROP);
		img.setOnClickListener(new GraphClickListener(index));

		mImageMaps.put(imgURL, new ImageMap(img, null));
		// Can add a listener
		ImageLoader.getInstance().displayImage(imgURL, img, mImageLoaderOptions, mGraphImageLoadListener);

		return img;
	}
	// =====================================================================================================//
	public class GraphClickListener implements OnClickListener {
		int mPosition;

		public GraphClickListener(int position) {
			mPosition = position;
		}

		@Override
		public void onClick(View v) {
			Logger.d(getClass(), mImgURLs[mPosition]);

			displayImage(mPosition);
		}
	}

	public void displayImage(int position) {
		initGraphPage(position);
	}

	private void initGraphPage(int position) {
		RelativeLayout edamRoot = (RelativeLayout) getRootView().findViewById(R.id.edam_root);

		if (mGraphPagerWrap != null) {
			edamRoot.removeView(mGraphPagerWrap);
		}
		mGraphPagerWrap = new GraphViewPager(getContext(), position, mActPageColor, ((FragmentActivity) getContext()).getSupportFragmentManager(),
				mImgURLs, mImageMaps);
		edamRoot.addView(mGraphPagerWrap, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	// =====================================================================================================//
	private class GraphImageLoadListener implements ImageLoadingListener {

		@Override
		public void onLoadingCancelled(String arg0, View arg1) {}

		@Override
		public void onLoadingComplete(String uri, View view, Bitmap bitmap) {
			if (mImageMaps.get(uri) != null) {
				if (mImageMaps.get(uri).getBitmap() == null) {
					mImageMaps.get(uri).setBitmap(bitmap);
				}
				if (isGraphPagerOpen()) {
					mGraphPagerWrap.setImageBitmap(uri);
				}
			} else {
				Logger.d(getClass(), "Check 1: Can't get target uri: " + uri);
				Iterator<String> iterator = mImageMaps.keySet().iterator();
				while (iterator.hasNext()) {
					Logger.d(getClass(), "Check 1: list " + iterator.next());
				}
			}
		}

		@Override
		public void onLoadingFailed(String url, View view, FailReason failReason) {
			mGraphWrapper.removeView(view);
		}

		@Override
		public void onLoadingStarted(String arg0, View arg1) {}
	}
	// =====================================================================================================//
	public void setBackgroundColor(String color) {
		mActPageColor = color;
		setBackgroundColor(Color.parseColor(mActPageColor));
	}

	public boolean isGraphPagerOpen() {
		return (mGraphPagerWrap != null) && !mGraphPagerWrap.isClose();
	}

	public void closeGraphPager() {
		if (isGraphPagerOpen()) {
			mGraphPagerWrap.closeGraphPager();
			mGraphPagerWrap = null;
		}
	}
}
