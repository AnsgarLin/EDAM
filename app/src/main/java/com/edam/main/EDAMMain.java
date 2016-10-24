package com.edam.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.edam.buttompanel.ButtomPanel;
import com.edam.floatsearch.FloatSearch;
import com.edam.parse.Activity;
import com.edam.parse.Drama;
import com.edam.parse.Exhibition;
import com.edam.parse.Music;
import com.edam.share.fb.FBShareHelper;
import com.edam.util.Logger;
import com.edam.util.Util;
import com.facebook.FacebookDialog;
import com.flurry.android.FlurryAgent;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;
import com.parse.Parse;
import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EDAMMain extends FragmentActivity {
	private ActPageSlideTab mActPageSlideTab;
	private FloatSearch mFloatSearch;
	private ButtomPanel mButtomPanel;
	private ImageLoader mImageLoader;
	private FBShareHelper mFBShareHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Use StrictMode to detect potential memory leak and other problems
		// if (BuildConfig.DEBUG) {
		// // or .detectAll() for all detectable problems
		// StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		// .detectDiskReads().detectDiskWrites().detectNetwork()
		// .penaltyLog().build());
		// StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
		// .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
		// .penaltyLog().penaltyDeath().build());
		// }
		Logger.d(getClass(), "EDAM onCreate");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.edam_main);

		getActionBar().hide();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		mImageLoader = ImageLoader.getInstance();
		L.writeLogs(Util.LOG);
		if (!mImageLoader.isInited()) {
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
					.threadPriority(Thread.NORM_PRIORITY - 2)
					.denyCacheImageMultipleSizesInMemory()
					// .memoryCache(new UsingFreqLimitedMemoryCache(sizeLimit))
					.tasksProcessingOrder(QueueProcessingType.LIFO)
					.diskCache(new UnlimitedDiskCache(new File(getCacheDir() + "/uil-images")))
					.diskCacheFileNameGenerator(new Md5FileNameGenerator())
					.diskCacheSize(25 * 1024 * 1024) // 50 Mb
					.writeDebugLogs() // Remove for release app
					.build();
			// Initialize ImageLoader with configuration.
			mImageLoader.init(config);
		}

		initParse();

		mActPageSlideTab = (ActPageSlideTab) findViewById(R.id.act_page_slide_tab);
		mActPageSlideTab.setViewPager(findViewById(R.id.act_page), getSupportFragmentManager());

		mButtomPanel = (ButtomPanel) findViewById(R.id.buttom_panel);
		mButtomPanel.init(mActPageSlideTab);
		mActPageSlideTab.setButtomPanel(mButtomPanel);

		mFloatSearch = (FloatSearch) findViewById(R.id.float_search_container);
		mActPageSlideTab.setFloatSearch(mFloatSearch);
		mFloatSearch.setActPageSlideTab(mActPageSlideTab);

		mFBShareHelper = FBShareHelper.getInstance(this);
		mFBShareHelper.onCreate(savedInstanceState);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// Logger.d(getClass(), "EDAM dispatchTouchEvent");
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK ||
				keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			if (mFloatSearch.isOpen()) {
				mFloatSearch.close();
				return true;
			} else if (mButtomPanel.isOpen()) {
				mButtomPanel.close();
				return true;
			} else if (mActPageSlideTab.onKeyDown(keyCode, event)) {
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onRestart() {
		Logger.d(getClass(), "EDAM onRestart");
		super.onRestart();
	}

	@Override
	protected void onStart() {
		Logger.d(getClass(), "EDAM onStart");
		FlurryAgent.onStartSession(this, Util.FLURRY_KEY);
		super.onStart();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Logger.d(getClass(), "EDAM onRestoreInstanceState");
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    mFBShareHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
	        @Override
	        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
	            Logger.d(getClass(), String.format("Error: %s", error.toString()));
	        }

	        @Override
	        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
	        	Logger.d(getClass(), "Success!");
	        }
	    });
	}

	@Override
	protected void onResume() {
		Logger.d(getClass(), "EDAM onResume");
		mFBShareHelper.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		Logger.d(getClass(), "EDAM onPause");
		mFBShareHelper.onPause();
		super.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Logger.d(getClass(), "EDAM onSaveInstanceState");
		mFBShareHelper.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStop() {
		Logger.d(getClass(), "EDAM onStop");
	    long size = 0;
	    File[] files = mImageLoader.getDiskCache().getDirectory().listFiles();
		if (files != null) {
			for (File f : files) {
				size = size + f.length();
				f.delete();
			}
			Logger.d(getClass(), "EDAM use " + String.valueOf(size / 1024 / 1024) + "M to cache");

			try {
				Map<String, String> flurryData = new HashMap<String, String>();
				flurryData.put("Count", String.valueOf(size / 1024 / 1024));
				FlurryAgent.logEvent("DiskCache", flurryData);
				JSONObject props = new JSONObject();
				props.put("Count", size / 1024 / 1024);
				MixpanelAPI.getInstance(this, Util.MIXPANEL_KEY).track("DiskCache", props);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		MixpanelAPI.getInstance(this, Util.MIXPANEL_KEY).flush();

		FlurryAgent.onEndSession(this);

		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Logger.d(getClass(), "EDAM onDestroy()");

		mActPageSlideTab.onDestroy();
		mActPageSlideTab = null;

		mFloatSearch.onDestroy();
		mFloatSearch = null;

		mButtomPanel.onDestroy();
		mButtomPanel = null;
		if (mImageLoader.getDiskCache() != null) {
			mImageLoader.getDiskCache().clear();
		}

		mFBShareHelper.onDestroy();
		super.onDestroy();
	}
	// =====================================================================================================//
	private void initParse() {
		Parse.initialize(this, Util.PARSE_APP_ID, Util.PARSE_CLIENT_ID);
		ParseObject.registerSubclass(Exhibition.class);
		ParseObject.registerSubclass(Drama.class);
		ParseObject.registerSubclass(Activity.class);
		ParseObject.registerSubclass(Music.class);
	}
}
