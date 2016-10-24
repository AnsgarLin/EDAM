package com.edam.page.detail;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.LruCache;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

import com.edam.util.Logger;
import com.edam.util.MyUtils;
import com.edam.util.Util;

public class TicketPageAdapter extends ArrayAdapter<String>{
	private String mCurrentColor;

	private LruCache<Integer, Bitmap> mImageCache;
	private ExecutorService mLoadActImageTaskPool;

	private ArrayList<LoadActImage> mLoadTasks;

	public TicketPageAdapter(Context context, int resource, List<String> objects) {
		super(context, resource, objects);
		// Use all free memory as cache
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		mImageCache = new LruCache<Integer, Bitmap>(maxMemory);
		mLoadTasks = new ArrayList<LoadActImage>();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TicketPage ticketPage = new TicketPage(getContext());
		ticketPage.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, (int) Util.GOTO_BUTTON_SZIE));
		// TextView
		ticketPage.setTextSize(TypedValue.COMPLEX_UNIT_PX, Util.TEXT_SIZE);
		ticketPage.setGravity(Gravity.CENTER);
		ticketPage.setText(ticketPlatformFilterTxt(getItem(position)));
		// ImageView
//		ticketPage.setScaleType(ScaleType.FIT_CENTER);
//		setTitleGraph(((TicketPage) convertView), position, ticketPlatformFilter(getItem(position)));
		// =========
		ticketPage.setTicketURL(getItem(position));
		ticketPage.setTextColor(MyUtils.ColorUtil.getColorStateList(mCurrentColor, Util.LIGHT_BLACK, Util.BASIC_ALPHA));

		ticketPage.setBackgroundDrawable(MyUtils.DrawableUtil.getStateDrawableWithColor(mCurrentColor, Util.TRANSPARENT));

		return ticketPage;
	}

	public void onDestroy() {
		if (mLoadTasks != null) {
			for (LoadActImage loadTask : mLoadTasks) {
				loadTask.cancel(true);
			}
			mLoadTasks.clear();
		}

		if (mImageCache != null) {
			for (int i = 0; i < mImageCache.size(); i++) {
				if ((mImageCache.get(i) != null) && !mImageCache.get(i).isRecycled()) {
					mImageCache.get(i).recycle();
					mImageCache.remove(i);
				}
			}
			mImageCache = null;
		}
	}
	// =====================================================================================================//
	public String ticketPlatformFilter(String ticketURL) {
		if (ticketURL.contains("kktix")) {
			return "http://www.citytalk.tw/img/ct/site/v3/event/root/index/tag/tag-kktix.gif";
		}
		else if (ticketURL.contains("tickets.books")) {
			return "http://www.citytalk.tw/img/ct/site/v3/event/root/index/tag/tag-tickets-books.gif";
		}
		else if (ticketURL.contains("groupon")) {
			return "http://www.citytalk.tw/img/ct/site/v3/event/root/index/tag/tag-groupon.gif";
		}
		else if (ticketURL.contains("gomaji")) {
			return "http://www.citytalk.tw/img/ct/site/v3/event/root/index/tag/tag-gomaji.gif";
		}
		else if (ticketURL.contains("17life")) {
			return "http://www.citytalk.tw/img/ct/site/v3/event/root/index/tag/tag-17life.gif";
		}
		else if (ticketURL.contains("dmarketnet")) {
			return "http://www.citytalk.tw/img/ct/site/v3/event/root/index/tag/tag-dmarketnet.gif";
		}
		else if (ticketURL.contains("tw.discount")) {
			return "https://s.yimg.com/rz/d/yahoo_shopping_zh-Hant-TW_mall_f_p_350x40_store.png";
		}
		else if (ticketURL.contains("indievox")) {
			return "http://www.citytalk.tw/img/ct/site/v3/event/root/index/tag/tag-indievox.gif";
		}
		else if (ticketURL.contains("artsticket")) {
			return "http://www.citytalk.tw/img/ct/site/v3/event/root/index/tag/tag-artiicker.gif";
		}
		else if (ticketURL.contains("walkieticket")) {
			return "http://www.citytalk.tw/img/ct/site/v3/event/root/index/tag/tag-walkieticker.gif";
		}
		else if (ticketURL.contains("kham")) {
			return "http://www.citytalk.tw/img/ct/site/v3/event/root/index/tag/tag-kham.gif";
		}
		else if (ticketURL.contains("7net")) {
			return "http://www.citytalk.tw/img/ct/site/v3/event/root/index/tag/tag-7net.gif";
		}
		else if (ticketURL.equals("")) {
			return "http://www.citytalk.tw/img/ct/site/v3/event/root/index/tag/tag-booking-other.gif";
		}
		return "http://www.citytalk.tw/img/ct/site/v3/event/root/index/tag/tag-booking-other.gif";
	}

	public String ticketPlatformFilterTxt(String ticketURL) {
		if (ticketURL.contains("kktix")) {
			return "KKTIX";
		}
		else if (ticketURL.contains("tickets.books")) {
			return "博客來";
		}
		else if (ticketURL.contains("groupon")) {
			return "酷碰";
		}
		else if (ticketURL.contains("gomaji")) {
			return "GOMAJI";
		}
		else if (ticketURL.contains("17life")) {
			return "17Life";
		}
		else if (ticketURL.contains("dmarketnet")) {
			return "大市集";
		}
		else if (ticketURL.contains("tw.discount")) {
			return "Yahoo超級商城";
		}
		else if (ticketURL.contains("indievox")) {
			return "INDIEVOX";
		}
		else if (ticketURL.contains("artsticket")) {
			return "兩廳院";
		}
		else if (ticketURL.contains("walkieticket")) {
			return "華娛";
		}
		else if (ticketURL.contains("kham")) {
			return "寬宏";
		}
		else if (ticketURL.contains("7net")) {
			return "ibon";
		}
		else if (ticketURL.contains("eslite")) {
			return "藝。起來";
		}
		else if (ticketURL.contains("yourart")) {
			return "藝遊網";
		}
		else if (ticketURL.equals("cafe")) {
			return "現場消費";
		}
		else if (ticketURL.equals("TBD")) {
			return "尚未公佈";
		}
		else if (ticketURL.contains("free-get_")) {
			return "限量索票";
		}
		else if (ticketURL.contains("official_")){
			return "請洽官網";
		}
		else if (ticketURL.equals("")){
			return "無資料";
		}

		return "其他";
	}

	protected void setTitleGraph(ImageView image, int position, String ticketImageURL) {
		Bitmap bitmap = mImageCache.get(position);

		if (bitmap != null) {
			Logger.d(getClass(), "Ticket image" + "-" + position + " " + "Image already load");
			image.setImageBitmap(bitmap);
		} else {
			Logger.d(getClass(), "Ticket Image" + "-" + position + " " + "Image not load");
			if (mLoadActImageTaskPool == null) {
				mLoadActImageTaskPool = Executors.newFixedThreadPool(20);
			}
			LoadActImage loadTask = new LoadActImage(position, image);
			mLoadTasks.add(loadTask);
			loadTask.executeOnExecutor(mLoadActImageTaskPool, ticketImageURL);
		}

		bitmap = null;
	}
	// =====================================================================================================//
	private class LoadActImage extends AsyncTask<String, Void, Bitmap> {
		private int mPosition;
		private ImageView mTargetImgaeView;

		public LoadActImage(int position, ImageView imageView) {
			super();
			mPosition = position;
			mTargetImgaeView = imageView;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			// Open an new connection for each image
			try {
				return MyUtils.BitmapUtil.getBitmapFromURL(new URL(params[0]));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);

			if (result != null) {
				Logger.d(getClass(), "Ticket image title/act Image load finish");
				mImageCache.get(mPosition).recycle();
				mImageCache.put(mPosition, result);
				mTargetImgaeView.setImageBitmap(result);
			} else {
				Logger.d(getClass(), "Ticket image Image load error");
			}
			mTargetImgaeView = null;
		}

		@Override
		protected void onCancelled(Bitmap result) {
			if ((result != null) && !result.isRecycled()) {
				Logger.d(getClass(), "Ticket image get bitmap but panel is closed, recycle");
				result.recycle();
			}
			super.onCancelled(result);
		}
	}
	// =====================================================================================================//
	public void setPaintColor(String currentColor) {
		mCurrentColor = currentColor;
	}

}
