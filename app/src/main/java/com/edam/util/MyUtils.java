package com.edam.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

public class MyUtils {
	public static String WHITE = "#ffffff";

	/**
	 * Default width and height are WRAP_CONTENT
	 */
	public static class RelativeLayoutUtil {
		/**
		 * Return a Center-Button RelativeLayout.LayoutParams
		 */
		public static RelativeLayout.LayoutParams getCenterBottomLayoutParams() {
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			return lp;
		}

		/**
		 * Return a Center-Button RelativeLayout.LayoutParams with margin from bottom
		 */
		public static RelativeLayout.LayoutParams getCenterBottomLayoutParams(int bottom) {
			RelativeLayout.LayoutParams lp = getCenterBottomLayoutParams();
			lp.setMargins(0, 0, 0, bottom);
			return lp;
		}

		/**
		 * Return a RelativeLayout.LayoutParams with margin from left, top, right and bottom
		 */
		public static RelativeLayout.LayoutParams getMarginLayoutParams(int left, int top, int right, int bottom) {
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lp.setMargins(left, top, right, bottom);
			return lp;
		}

	}

	public static class SearchViewUtil {
		/**
		 * SearchView will always expand to fix the width of screen
		 */
		public static void setWidthToFitWindow(Context context, SearchView searchView) {
			searchView.setMaxWidth(context.getResources().getDisplayMetrics().widthPixels);
		}

		/**
		 * Customized SearView button with image resource
		 */
		public static void setSearchButton(SearchView searchView, int resourceId) {
			ImageView searchHintIcon = (ImageView) searchView.findViewById(searchView.getContext().getResources()
					.getIdentifier("android:id/search_button", null, null));
			if (searchHintIcon != null) {
				searchHintIcon.setImageResource(resourceId);
			}
		}
	}

	public static class BitmapUtil {
		/**
		 * Recycle a Bitmap resource, but need to set null outside
		 */
		public static void recycleBitmap(Bitmap bitmap) {
			bitmap.recycle();
			System.gc();
		}

		/**
		 * Save a Bitmap as a JPG file in given filePath and quality
		 */
		public static void saveBitmapAsJPGFile(File filePath, Bitmap bitmap, int quality) {
			try {
				filePath.createNewFile();
				bitmap.compress(CompressFormat.JPEG, quality, new FileOutputStream(filePath, false));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Get a Bitmap from URL
		 */
		public static Bitmap getBitmapFromURL(URL url) {
			return BitmapFactory.decodeStream(InputStreamUtil.getURLInputStream(url));
		}

		/**
		 * Get a Bitmap from URL and scaled to simple size by given width and height
		 */
		public static Bitmap getBitmapFromURLBySize(URL url, int width, int height) {
			byte[] buff;
			if ((buff = InputStreamUtil.convertInStreamToBytes(InputStreamUtil.getURLInputStream(url))) == null) {
				return null;
			}

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(buff, 0, buff.length, options);

			options.inSampleSize = 1;
			if ((options.outWidth > width) || (options.outHeight > height)) {
				// Use floor to make the ratio as minimum as possible
				int widthRatio = (int) Math.floor(options.outWidth / (float) width);
				int heightRatio = (int) Math.floor(options.outHeight / (float) height);

				// If one of width/height is bigger then target width/height, use the big one ratio to make sure the bitmap will smaller
				options.inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
			}

			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeByteArray(buff, 0, buff.length, options);
		}
	}

	public static class InputStreamUtil {
		/**
		 * Get a opened input stream for given url
		 */
		public static InputStream getURLInputStream(URL url) {
			try {
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.connect();
				return connection.getInputStream();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		/**
		 * Convert inputStream to byteArray
		 */
		public static byte[] convertInStreamToBytes(InputStream inputStream) {
			if (inputStream == null) {
				Logger.d(MyUtils.class, "Something wrong with loading image");

				return null;
			}

			try {
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				byte[] buff = new byte[1024];
				int len = 0;
				while ((len = inputStream.read(buff)) != -1) {
					outputStream.write(buff, 0, len);
				}
				inputStream.close();
				outputStream.close();
				return outputStream.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public static class DrawableUtil {
		/**
		 * Get a Drawable from URL
		 */
		public static Drawable getDrawableFromURL(URL url) {
			try {
				return Drawable.createFromStream((InputStream) url.getContent(), null);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * Get a StateDrawable with press state
		 */
		public static StateListDrawable getStateDrawableWithColor(String pressColor, String defaultColor) {
			StateListDrawable stateListDrawable = new StateListDrawable();
			stateListDrawable.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled },
					new ColorDrawable(Color.parseColor(pressColor)));
			stateListDrawable.addState(new int[] { -android.R.attr.state_enabled }, new ColorDrawable(Color.parseColor(defaultColor)));
			return stateListDrawable;
		}
	}

	public static class WebViewUtil {
		/**
		 * Return a WebView that align setting of text is justify
		 */
		public static WebView getWebViewWithAlignJustify(Activity activity, int resourceString) {
			WebView webView = new WebView(activity);
			webView.setVerticalScrollBarEnabled(false);
			webView.loadDataWithBaseURL("",
					"<![CDATA[<html><head></head><body style=\"text-align:justify;\">" + activity.getResources().getString(resourceString)
							+ "</body>", "text/html", "utf-8", null);
			return webView;
		}

		/**
		 * Set a exist WebView that align of text is justify
		 */
		public static void setWebViewWithAlignJustify(WebView webView, Activity activity, int resourceString) {
			webView.setVerticalScrollBarEnabled(false);
			webView.loadDataWithBaseURL("",
					"<![CDATA[<html><head></head><body style=\"text-align:justify;\">" + activity.getResources().getString(resourceString)
							+ "</body>", "text/html", "utf-8", null);
		}
	}

	public static class SystemUtil {
		/**
		 * Check there has at least "sizeNeed" MB in storage
		 */
//		public static Boolean isEnoughAvalibleStorage(int sizeNeed) {
//			StatFs fs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
//			return (fs.getAvailableBlocksLong() * (fs.getBlockSizeLong() / 1024f / 1024f)) >= sizeNeed;
//		}

		/**
		 * Get LayoutInflater from context service
		 */
		public static LayoutInflater getLayoutInflater(Context context) {
			return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		/**
		 * Check whether network connection is available
		 */
		public static boolean isNetworkEnabled(Context context) {
			if (context != null) {
				NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
				if ((info != null) && info.isConnected()) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Check whether IME soft input window is open, the input view must the same size as the full screen
		 */
		public static boolean isSoftInputWinOpen(View view) {
			int heightDiff = view.getRootView().getHeight() - view.getHeight();
			if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard
				return true;
			}
			return false;
		}
	}

	public static class InflateUtil {
		/**
		 * Inflate the specific layout, default attach to root is false
		 */
		public static View InflateReource(Context context, int resourceID, ViewGroup parent) {
			return SystemUtil.getLayoutInflater(context).inflate(resourceID, parent, false);
		}
	}

	public static class FileUtil {
		/**
		 * Create a directory for specific type and name
		 */
		public static File getDir(String type, String name) {
			File tempDir = null;
			if (type == Environment.DIRECTORY_PICTURES) {
				tempDir = new File(Environment.getExternalStoragePublicDirectory(type), name);
			}

			if (!tempDir.exists()) {
				tempDir.mkdirs();
			}
			return tempDir;
		}
	}

	public static class TimerUtil {
		/**
		 * Start or Reset timer with given time
		 */
		public static Timer start(Timer timer, TimerTask task, long delay) {
			if (timer != null) {
				timer.purge();
				timer.cancel();
			}
			timer = new Timer();
			timer.schedule(task, delay);
			return timer;
		}

		/**
		 * Stop and clear timer
		 */
		public static Timer stop(Timer timer) {
			if (timer != null) {
				timer.purge();
				timer.cancel();
			}
			return null;
		}
	}

	public static class ResourceUtil {
		/**
		 * Get a real image size for resource image
		 */
		public static Point getActualImageSize(Context context, int resourceID) {
			BitmapFactory.Options dimensions = new BitmapFactory.Options();
			dimensions.inJustDecodeBounds = true;
			BitmapFactory.decodeResource(context.getResources(), resourceID, dimensions);
			return new Point(dimensions.outWidth, dimensions.outHeight);
		}
	}

	public static class TypedValueUtil {
		/**
		 * Transfer value from given dimension unit to pixel, return -1 for unknown dimension
		 */
		public static float toPixel(DisplayMetrics dm, String unit, float value) {
			if ((unit == "dip") || (unit == "dp")) {
				return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, dm);
			} else if (unit == "in") {
				return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, value, dm);
			} else if (unit == "mm") {
				return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, value, dm);
			} else if (unit == "pt") {
				return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PT, value, dm);
			} else if (unit == "px") {
				return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, value, dm);
			} else if (unit == "sp") {
				return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, dm);
			} else {
				return -1;
			}
		}
	}

	public static class ToastUtil {
		/**
		 * Re-trigger a toast by using the same toast instance
		 */
		public static Toast restart(Context context, Toast oldToast, String message) {
			if (oldToast != null) {
				oldToast.cancel();
			}
			oldToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
			oldToast.show();
			return oldToast;
		}

		/**
		 * Re-trigger a toast by using the same toast instance
		 */
		public static Toast restart(Context context, Toast oldToast, int messageID) {
			if (oldToast != null) {
				oldToast.cancel();
			}
			oldToast = Toast.makeText(context, context.getString(messageID), Toast.LENGTH_SHORT);
			oldToast.show();
			return oldToast;
		}
	}

	public static class DateUtil {
		/**
		 * Get the date after days from today in format
		 */
		public static String getDateFromToday(int range, SimpleDateFormat format) {
			Calendar today = Calendar.getInstance();
			today.add(Calendar.DATE, range);

			return format.format(today.getTime());
		}

		/**
		 * Get today's date info by field
		 */
		public static String getToday(int field) {
			Calendar today = Calendar.getInstance();
			if (field == Calendar.MONTH) {
				return String.valueOf(today.get(field) + 1);
			} else {
				return String.valueOf(today.get(field));
			}
		}

		/**
		 * Get calendar instance by format
		 */
		public static Calendar getCalenderByFormat(String dateString, String format) throws ParseException {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
			Calendar calender = Calendar.getInstance();
			calender.setTime(simpleDateFormat.parse(dateString));
			return calender;
		}

		/**
		 * Get date instance by format
		 *
		 * @throws ParseException
		 */
		public static Date getDateByFormat(String dateString, String format) throws ParseException {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
			return simpleDateFormat.parse(dateString);
		}

		/**
		 * Get date duration, use date as basic unit
		 *
		 * @throws ParseException
		 */
		public static int getDateDuration(Date startDate, Date endDate) {
			return (int) Math.ceil((endDate.getTime() - startDate.getTime()) / (1000f * 3600f * 24f)); // A day in milliseconds
		}
	}

	public static class ColorUtil {
		/**
		 * Get color hex string between colors in percentage
		 */
		public static String getColorBetween(String c1, String c2, float percent) {
			int[] green = hexToRGB(c1);
			int[] gray = hexToRGB(c2);
			int[] result = new int[3];
			for (int i = 0; i < green.length; i++) {
				result[i] = (int) (green[i] + ((gray[i] - green[i]) * percent));
			}
			return rgbToHex(result);
		}

		/**
		 * Mix two color with the front in alpha
		 */
		public static String mixColor(String back, String cover, float coverAlpha) {
			int[] backRGB = MyUtils.ColorUtil.hexToRGB(back);
			int[] coverRGB = MyUtils.ColorUtil.hexToRGB(cover);
			int[] mixRGB = new int[3];
			for (int i = 0; i < backRGB.length; i++) {
				mixRGB[i] = backRGB[i] + Math.round(((coverRGB[i] - backRGB[i]) * coverAlpha));
			}
			return MyUtils.ColorUtil.rgbToHex(mixRGB);
		}

		/**
		 * Transform hex color string to integer RBG array
		 */
		public static int[] hexToRGB(String color) {
			int[] numRGB = new int[3];
			numRGB[0] = Integer.parseInt(color.substring(1, 3), 16);
			numRGB[1] = Integer.parseInt(color.substring(3, 5), 16);
			numRGB[2] = Integer.parseInt(color.substring(5, 7), 16);
			return numRGB;
		}

		/**
		 * Transform integer RBG to hex color string
		 */
		public static String rgbToHex(int[] color) {
			String colorString = "#";
			for (int element : color) {
				if (element < 16) {
					colorString += "0";
				}
				colorString += Integer.toHexString(element);
			}
			return colorString;
		}

		/**
		 * Get color state list with press state. Press color will be mix with white in 90% of alpha
		 */
		public static ColorStateList getColorStateList(String pressColor, String defaultColor, float mixAlpha) {
			int[][] states = new int[][] { new int[] { android.R.attr.state_enabled, android.R.attr.state_pressed },
					new int[] { android.R.attr.state_enabled } };
			int[] colors = new int[] { Color.parseColor(MyUtils.ColorUtil.getColorBetween(pressColor, WHITE, mixAlpha)),
					Color.parseColor(defaultColor) };
			return new ColorStateList(states, colors);
		}
	}

	public static class ListViewUtil {
		/**
		 * Calculate the scroll bar's top-left and bottom-right position on screen with limit
		 */
		public static float[] getScrollBarDrawPos(ListView listView, float childH, float dividerH, int childCount, float scrollBarR,
				float scrollBarW, float scrollBarH, float scrollBarHLimit) {
			float scrollbarButtom = getScrollBarButtomPos(getScrollOffset(listView, childH, dividerH),
					calEndScrollY(childCount, childH, dividerH, listView.getHeight()), scrollBarH, listView.getHeight());

			scrollBarH /= ((((childH + dividerH) * childCount) - dividerH) / listView.getHeight());

			if (scrollBarH < scrollBarHLimit) {
				scrollBarH = scrollBarHLimit;
			}

			float[] scrollBarPos = new float[4];
			scrollBarPos[0] = scrollBarR - scrollBarW;
			scrollBarPos[1] = scrollbarButtom - scrollBarH;
			scrollBarPos[2] = scrollBarR;
			scrollBarPos[3] = scrollbarButtom;
			return scrollBarPos;
		}

		/**
		 * Calculate the scroll offset manually by given item view's height and divider's height
		 */
		public static float getScrollOffset(ListView listView, float itemViewHeight, float dividerHeight) {
			float offset;
			if (listView.getChildAt(0).getTop() < 0) {
				offset = ((itemViewHeight + dividerHeight) * listView.getFirstVisiblePosition()) + Math.abs(listView.getChildAt(0).getTop());
			} else if (listView.getChildAt(0).getTop() > 0) {
				offset = (itemViewHeight * listView.getFirstVisiblePosition()) + (dividerHeight * (listView.getFirstVisiblePosition() - 1))
						+ Math.abs(listView.getChildAt(0).getTop() - dividerHeight);
			} else {
				offset = ((itemViewHeight + dividerHeight) * listView.getFirstVisiblePosition());
			}
			return offset;
		}

		/**
		 * Calculate the total height of all the views, than calculate the end offset while the list is scroll to the end
		 */
		public static float calEndScrollY(int itemCount, float itemH, float dividerH, float viewH) {
			float totalLength = ((itemH + dividerH) * itemCount) - dividerH;
			return (((int) (totalLength / viewH) - 1) * viewH) + (totalLength % viewH);
		}

		/**
		 * Reflect the offset on the total height of all view to the list height to get the scroll bar offset
		 */
		public static float getScrollBarButtomPos(float scrollOffset, float endScrollY, float scrollBarH, float viewH) {
			return ((scrollOffset / endScrollY) * (viewH - (scrollOffset / endScrollY) * scrollBarH)) + (scrollOffset / endScrollY) * scrollBarH;
		}
	}

	public static class MathUtil {
		/**
		 * Ceiling of the number, accurate to given decimal places
		 */
		public static double CeilWithPoint(double num, int decimalAfter) {
			return Math.ceil(num * Math.pow(10, decimalAfter)) / Math.pow(10, decimalAfter);
		}

		/**
		 * Floor of the number, accurate to given decimal places
		 */
		public static double FloorWithPoint(double num, int decimalAfter) {
			return Math.floor(num * Math.pow(10, decimalAfter)) / Math.pow(10, decimalAfter);
		}

		/**
		 * Round of the number, accurate to given decimal places
		 */
		public static double RoundWithPoint(double num, int decimalAfter) {
			return Math.round(num * Math.pow(10, decimalAfter)) / Math.pow(10, decimalAfter);
		}

		/**
		 * Get distance between two points in 2-dimension
		 */
		public static double getDistance(float sX, float tX, float sY, float tY) {
			float x = sX - tX;
			float y = sY - tY;
			return Math.sqrt(x * x + y * y);
		}

		/**
		 * Get mid-point between two points in 2-dimension
		 */
		public static PointF getMidPoint(float sX, float tX, float sY, float tY) {
			return new PointF((sX + tX) / 2, (sY + tY) / 2);
		}
	}

	public static class ImageViewUtil {
		/**
		 * Get a matrix by the original and target size, which makes the image to fit the target size. Then move to center
		 */
		public static Matrix setMatrix(float targetX, float originX, float targetY, float originY) {
			float scaleX = targetX / originX;
			float scaleY = targetY / originY;
			float scaleFinal = scaleX >= scaleY ? scaleY : scaleX;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleFinal, scaleFinal);

			float finalX = originX * scaleFinal;
			float finalY = originY * scaleFinal;

			if (scaleFinal == scaleX) {
				matrix.postTranslate(0, (targetY - finalY) / 2f);
			} else {
				matrix.postTranslate((targetX - finalX) / 2f, 0);
			}
			return matrix;
		}
	}
}
