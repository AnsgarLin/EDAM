package com.edam.page.detail;

import java.util.HashMap;
import java.util.Iterator;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edam.main.R;
import com.edam.page.detail.Graph.ImageMap;
import com.edam.util.Logger;
import com.edam.util.MyUtils;
import com.edam.util.MyUtils.ToastUtil;
import com.edam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class GraphPagerWrap extends Fragment{
	private static final String IMG_POSITION = "position";
	private static final String IMG_URL = "url";

	private GraphPager mGraphPage;
	private int mPosition;
	private String mImgURL;
	private DisplayImageOptions mImageLoaderOptions;
	private HashMap<String, ImageMap> mImageMaps;

	public static GraphPagerWrap newInstance(int position, String imgURL) {
		GraphPagerWrap actPageFragment = new GraphPagerWrap();
		Bundle bundle = new Bundle();
		bundle.putInt(IMG_POSITION, position);
		bundle.putString(IMG_URL, imgURL);

		actPageFragment.setArguments(bundle);
		return actPageFragment;
	}

	@Override
	public void onAttach(Activity activity) {
		mPosition = getArguments().getInt(IMG_POSITION);
		mImgURL = getArguments().getString(IMG_URL);
		mImageLoaderOptions = Util.getDisplayImageOptions(-1);
		super.onAttach(activity);
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	mGraphPage = new GraphPager(getActivity());
		ImageLoader.getInstance().displayImage(mImgURL, mGraphPage.getImageView(), mImageLoaderOptions, new GraphLoadListener());
        return mGraphPage;
    }

    public void onClose() {
		ImageLoader.getInstance().cancelDisplayTask(mGraphPage.getImageView());
		mGraphPage.onDestroy();
		mGraphPage = null;
	}
	// =====================================================================================================//
    private class GraphLoadListener implements ImageLoadingListener {
		@Override
		public void onLoadingCancelled(String arg0, View arg1) {}

		@Override
		public void onLoadingComplete(String uri, View view, Bitmap bitmap) {
			if (mImageMaps.size() != 0 && mImageMaps.get(uri) != null) {
				if (mImageMaps.get(mImgURL).getBitmap() == null) {
					Logger.d(getClass(), "Inner loader finish");
					ImageLoader.getInstance().cancelDisplayTask(mImageMaps.get(mImgURL).getImageView());
					mImageMaps.get(mImgURL).getImageView().setImageBitmap(bitmap);
					mImageMaps.get(mImgURL).setBitmap(bitmap);
				}
			} else {
				Logger.d(getClass(), "Check 2: Can't get target uri: " + uri);
				Iterator<String> iterator = mImageMaps.keySet().iterator();
				while (iterator.hasNext()) {
					Logger.d(getClass(), "Check 2: list " + iterator.next());
				}
			}
			setImageBitmap(bitmap);
		}

		@Override
		public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			Util.mToast = ToastUtil.restart(getActivity(), Util.mToast, R.string.image_load_fail);
		}

		@Override
		public void onLoadingStarted(String arg0, View arg1) {}
	}

    public void restartlImageLoader() {
		Logger.d(getClass(), "Out loader finish, restart");
		if (mGraphPage != null) {
			ImageLoader.getInstance().cancelDisplayTask(mGraphPage.getImageView());
			ImageLoader.getInstance().displayImage(mImgURL, mGraphPage.getImageView(), mImageLoaderOptions, new GraphLoadListener());
		}
	}

    public void setImageBitmap(Bitmap bitmap) {
		mGraphPage.setImageMatrix(MyUtils.ImageViewUtil.setMatrix(
				(getActivity().getResources().getDisplayMetrics().widthPixels - (Util.BACK_FRAME_STROKE_WIDTH * 2)), bitmap.getWidth(),
				getActivity().getResources().getDisplayMetrics().heightPixels, bitmap.getHeight()));
		mGraphPage.setImageBitmap(bitmap);
	}
	// =====================================================================================================//
    public GraphPagerWrap setImageMaps(HashMap<String, ImageMap> imageMaps) {
    	mImageMaps = imageMaps;
    	return this;
    }
}
