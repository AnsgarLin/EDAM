package com.edam.page.detail;

import java.util.List;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout.LayoutParams;

import com.edam.util.MyUtils;
import com.edam.util.Util;

public class SharePageAdapter extends ArrayAdapter<String>{
	private String mCurrentColor;

	public SharePageAdapter(Context context, int resource, List<String> objects) {
		super(context, resource, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SharePage sharePage = new SharePage(getContext());
		sharePage.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, (int) Util.GOTO_BUTTON_SZIE));
		// TextView
		sharePage.setTextSize(TypedValue.COMPLEX_UNIT_PX, Util.TEXT_SIZE);
		sharePage.setGravity(Gravity.CENTER);
		sharePage.setText(Util.SHARE_PLATFORM[position]);
		sharePage.setShareURL(getItem(position));
		sharePage.setTextColor(MyUtils.ColorUtil.getColorStateList(mCurrentColor, Util.LIGHT_BLACK, Util.BASIC_ALPHA));

		sharePage.setBackgroundDrawable(MyUtils.DrawableUtil.getStateDrawableWithColor(mCurrentColor, Util.TRANSPARENT));

		return sharePage;
	}

	public void onDestroy() {
	}
	// =====================================================================================================//
	public void setPaintColor(String currentColor) {
		mCurrentColor = currentColor;
	}
}
