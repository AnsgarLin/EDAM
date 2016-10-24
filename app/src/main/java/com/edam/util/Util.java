package com.edam.util;

import android.graphics.Bitmap;
import android.widget.Toast;

import com.edam.main.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions.Builder;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Util {
	public final static boolean LOG = false;
	public final static boolean AD = true;
	public final static boolean ADMOB = false;
	public final static String AD_STRING = "ad";
	public final static int AD_HEIGHT = 60;
	public final static int AD_POS = 4;
	public final static int AD_LIMIT = 10;

	public final static String FB_FANS = "https://www.facebook.com/pages/EDAM/545147008918252";
	public final static String EMAIL = "edamofficial@gmail.com";

	public final static String FLURRY_KEY = "J7592WP286G6BPWKVJS2";
	public final static String MIXPANEL_KEY = "898a29257aeb38d9dae7eb55fc5be2b7";
	// Parse DB IDs
	public final static String PARSE_APP_ID = "gwfQR5bXGJ7GnrVTnMLt61Yv1GUK8GJ2NutrnrHG";
	public final static String PARSE_CLIENT_ID = "qOvAq66ybhr1HuOjjTnyuBUX8IsjftzbUxVI2KAI";

	public final static String[] ACT_CATAGORY = {"展覽", "戲劇", "活動", "音樂"};
	public final static String[] ACT_CATAGORY_NAME_ON_DB = {"Exhibition", "Drama", "Activity", "Music"};

	public final static String[] ACT_CATAGORY_ID = {"57", "58", "55", "561"};
	public final static int ACT_CATAGORY_ACTIVITY = 55;
	public final static int ACT_CATAGORY_EXHIBITION = 57;
	public final static int ACT_CATAGORY_DRAMA = 58;
	public final static int ACT_CATAGORY_MUSIC = 561;
	public final static String[] ACT_CATAGORY_COLOR = {"#3b50ce", "#9c27b0", "#ff8f00", "#e91e63"};

	public static Toast mToast;
	public static float PIXEL;
	public static float MULTI;
	public static float HMULTI;
	public static Date UPDATE;
	public static String UPDATE_PARAM = "UPDATE";
	public final static float LIST_DIVIDER_HEIGHT = 3;

	/**
	 * Colors
	 */
	public final static String TRANSPARENT = "#00ffffff";
	public final static String WHITE = "#ffffff";
	public final static String BLACK = "#000000";
	public final static String LIGHT_BLACK = "#222222";
	public final static String TAB_TEXT_COLOR = "#ffffff";
	// Alpha value for white color to mix with others
	public final static float BASIC_ALPHA = 0.9f;
	public final static float BUILDING_COLOR_LAYER_1 = 0.7f;
	public final static float BUILDING_COLOR_LAYER_2 = 0.5f;
	/**
	 * List item view mode
	 */
	public static enum LISTMODE{LIST, DETAIL};

	/**
	 * Query
	 */
	public final static int QUERY_LIMIT = 25;

	/**
	 * DB columns' name
	 */
	public final static String DB_ID = "id";
	public final static String DB_TITLE = "title";
	public final static String DB_BRIEF = "brief";
	public final static String DB_ACT_URL = "act_url";
	public final static String DB_IS_FREE = "is_free";
	public final static String DB_TICKET_URL = "ticket_url";
	public final static String DB_START_DATE = "start_date";
	public final static String DB_START_TIME = "start_time";
	public final static String DB_END_DATE = "end_date";
	public final static String DB_END_TIME = "end_time";
	public final static String DB_CITY = "city";
	public final static String DB_PLACE = "place";
	public final static String DB_ADDRESS = "address";
	public final static String DB_COVER_IMG_URL = "cover_img_url";
	public final static String DB_IMG_URL = "img_url";
	public final static String DB_UPDATEDAT = "updatedAt";
	public final static String DB_CREATEDAT = "createdAt";
	/**
	 * IME
	 */
	public final static int SOFT_KEYBOARD_HEIGHT = 100;

	public final static int BUTTOM_PANEL_BAR_HEIGHT = 48;

	public final static float CATA_TAB_HEIGHT = 48;
	public final static int CATA_TAB_TEXT_SIZE = 24;

	public final static int TITLE_GRAPH_BASIC_HEIGHT = 100;
	public final static int TITLE_GRAPH_BASIC_WIDTH = 300;

	/**
	 * Activity detail page
	 */
	public final static float DETAIL_PAGE_BASIC_HEIGHT = 331.875f;
	public static float DETAIL_PAGE_WRAP_MARGIN = 10;

	public static float BACK_FRAME_STROKE_WIDTH = 3;
	public static float BACK_FRAME_MARGIN = 5;
	public static float BACK_FRAME_BUTTOM_MARGIN = 26;

	public static float TITLE_UP_SPACE_HEIGHT = 14;
	public static int TITLE_ID = 11;
	public static float TITLE_HEIGHT = 42;
	public static float TITLE_PADDING_LEFT = 66;
	public static float TITLE_PADDING_TOP = 3;
	public static float TITLE_PADDING_RIGHT = 8;
	public static float TITLE_TEXT_WRAP_HEIGHT;

	public static float DATE_UP_SPACE_HEIGHT = 25;
	public static int DATE_ID = TITLE_ID + TITLE_ID;
	public static float DATE_HEIGHT = 21;
	public static float DATE_IN_HEIGHT;

	public static float LOCATE_UP_SPACE_HEIGHT = 5;
	public static int LOCATEE_ID = DATE_ID + TITLE_ID;
	public static float LOCATE_HEIGHT = 21;
	public static float LOCATE_IN_HEIGHT;

	public static float BRIEF_UP_SPACE_HEIGHT = 5;
	public static int BRIEF_ID = LOCATEE_ID + TITLE_ID;
	public static float BRIEF_HEIGHT = 81;
	public static float BRIEF_IN_HEIGHT;

	public static float GRAPH_UP_SPACE_HEIGHT = 5;
	public static int GRAPH_ID = BRIEF_ID + TITLE_ID;
	public static int GRAPH_PAGE_ID = GRAPH_ID + 1;
	public static float GRAPH_HEIGHT = 54;
	public static float GRAPH_IN_HEIGHT;
	public static float GRAPH_PAGE_MODE_HEIGHT;
	public static float GRAPH_PAGE_CLOSE_SIZE = 25;

	public static float GRAFFITI_HEIGHT = 39;
	public static int GRAFFITI_ID = GRAPH_ID + TITLE_ID;

	public static float TITLE_UP_LAYER_WRAP_HEIGHT = 76;

	public static int TITLE_GRAPH_WRAP_ID = GRAFFITI_ID + TITLE_ID;
	public static float TITLE_GRAPH_WRAP_SIZE = 66;
	public static int TITLE_GRAPH_ID = TITLE_GRAPH_WRAP_ID + 1;
	public static float TITLE_GRAPH_SIZE = 60;

	public static int GOTO_BUTTON_SHARE_ID = TITLE_GRAPH_WRAP_ID + TITLE_ID;
	public static int GOTO_BUTTON_ACT_ID = GOTO_BUTTON_SHARE_ID + TITLE_ID;
	public static int GOTO_BUTTON_TICKET_ID = GOTO_BUTTON_ACT_ID + 1;
	public static int GOTO_BUTTON_FREE_TICKET_ID = GOTO_BUTTON_TICKET_ID + 1;
	public static int GOTO_BUTTON_GET_TICKET_ID = GOTO_BUTTON_FREE_TICKET_ID + 1;
	public static float GOTO_BUTTON_WRAP_HEIGHT = 40;
	public static float GOTO_BUTTON_WRAP_WIDTH = (GOTO_BUTTON_WRAP_HEIGHT * 3) - 6;
	public static float GOTO_BUTTON_SZIE = GOTO_BUTTON_WRAP_HEIGHT;

	/**
	 * Share Platform name
	 */
	public static String[] SHARE_PLATFORM = {"FB", "LINE"};
	/**
	 * Act info item
	 */
	public static int ACT_INFO_ITEM_IMAGE_SIZE = 60;
	/**
	 * search Button
	 */
	public static int SEARCH_BUTTON_BASIC_SIZE = 36;
	public static int SEARCH_BUTTON_SURROUND_SPACE = 5;
	/**
	 * Search Text
	 */
	public static int TEXT_SIZE;
	// The blank % of the height of up and down to the test size
	public static float TEXT_SIZE_BLANK_MULTI = 0.375f;
	// Multiple 0.8 can make text in the center of the layout
	public final static float TEXT_SIZE_ERROR = 0.8f;
	public final static int CUSTOM_LIST_SCROLL_HEIGHT = 120;
	/**
	 * Search Panel
	 */
	public final static int SEARCH_PANEL_BACKGROUND_RADIUS = 10;
	public final static int SEARCH_CATA_IMG_BASIC_SIZE = 30;
	// Image * 8 = The actual height shown on the screen
	// public final static int SEARCH_PANEL_CONTAINER_BASIC_HEIGHT = ((SEARCH_CATA_IMG_BASIC_SIZE * 7 + SEARCH_CATA_IMG_BASIC_SIZE / 2) + (SEARCH_BUTTON_SURROUND_SPACE * 2) + SEARCH_BUTTON_BASIC_SIZE + (Util.LIST_DIVIDER_HEIGHT * 2f));
	public final static int SEARCH_PANEL_CONTAINER_BASIC_WIDTH = 220;
	/**
	 * Animation
	 */
	public final static long ANIMATION_BASIC_DURATION_MS = 200;
	/**
	 * Http package content
	 */
	public final static String DESKTOP_USER_AGENT = "Mozilla/5.0 (X11; Linux i686) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.63 Safari/537.31";
	public final static String BASE_SITE_URL = "http://www.citytalk.tw";
	public final static String QUERY_SITE_URL = BASE_SITE_URL + "/post/v3/cata/index";
	public final static String SMALL_IMAGE_FILENAME = "200x168-exactly.jpg";

	public final static String TEST_DATA = "/sdcard/";

	public final static ArrayList<String> FILTER_CITY = new ArrayList<String>(Arrays.asList(new String[] {"全台灣", "台北市", "新北市", "基隆市", "桃園縣", "新竹市", "新竹縣", "苗栗縣", "台中市",
			"彰化縣", "雲林縣", "嘉義縣", "台南市", "高雄市", "屏東縣", "台東縣", "花蓮縣", "宜蘭縣", "南投縣", "澎湖縣", "金門縣", "連江縣"} ));

	public final static String[] FILTER_CITY_CODE = { "0", "28", "30", "29", "34", "32", "33", "35", "36",
		"38", "42", "41", "43", "45", "48", "49", "50", "31", "39", "47", "51", "52" };

	public final static ArrayList<String> FILTER_DAY_RANGE_AFTER_START = new ArrayList<String>(Arrays.asList(new String[]{"1日", "7日", "14日", "1個月", "3個月", "6個月", "1年"}));

	public final static int[] FILTER_DAY_RANGE_AFTER_START_CODE = {1, 7, 14, 30, 90, 180, 365};

	public final static ArrayList<String> FILTER_YEAR = new ArrayList<String>(Arrays.asList(new String[]{"2014", "2015", "2016", "2017", "2018", "2019", "2020"}));
	public final static String[] FILTER_YEAR_CODE = {"2014", "2015", "2016", "2017", "2018", "2019", "2020"};

	public final static ArrayList<String> FILTER_MONTH = new ArrayList<String>(Arrays.asList(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"}));
	public final static String[] FILTER_MONTH_CODE = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

	// Cause list only use fotr app first startup, so set the big date to list only
	public final static ArrayList<String> FILTER_DATE_BIG = new ArrayList<String>(Arrays.asList(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
		"11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
		"21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"}));
	public final static String[] FILTER_DATE_BIG_CODE = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
		"11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
		"21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};

	public final static String[] FILTER_DATE_SMALL = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
	"11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
	"21", "22", "23", "24", "25", "26", "27", "28", "29", "30"};
	public final static String[] FILTER_DATE_SMALL_CODE = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
		"11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
		"21", "22", "23", "24", "25", "26", "27", "28", "29", "30"};

	public final static String[] FILTER_DATE_FEB = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
	"11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
	"21", "22", "23", "24", "25", "26", "27", "28"};
	public final static String[] FILTER_DATE_FEB_CODE = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10",
		"11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
		"21", "22", "23", "24", "25", "26", "27", "28"};

	public static String getSmallImageURL(String originImageURL) {
		int lastSlashIndex = originImageURL.lastIndexOf('/');
		return Util.BASE_SITE_URL + originImageURL.substring(0, ++lastSlashIndex) + Util.SMALL_IMAGE_FILENAME;
	}

	public static DisplayImageOptions getDisplayImageOptions(int mActPagePosition){
		Builder displayImageOptions = new DisplayImageOptions.Builder()
		.bitmapConfig(Bitmap.Config.RGB_565)
//		.cacheInMemory(true)
		.cacheOnDisk(true)
		.displayer(new FadeInBitmapDisplayer((int) Util.ANIMATION_BASIC_DURATION_MS));

		switch(mActPagePosition) {
		case 0:
			displayImageOptions.showImageOnLoading(R.drawable.ic_exhibition);
			break;
		case 1:
			displayImageOptions.showImageOnLoading(R.drawable.ic_drama);
			break;
		case 2:
			displayImageOptions.showImageOnLoading(R.drawable.ic_activity);
			break;
		case 3:
			displayImageOptions.showImageOnLoading(R.drawable.ic_music);
			break;
		default:
			break;
		}
		return displayImageOptions.build();

	}
}
