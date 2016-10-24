package com.edam.floatsearch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.edam.main.R;
import com.edam.util.Logger;
import com.edam.util.MyUtils;
import com.edam.util.Util;

public class FloatSearchEdit extends LinearLayout{
	private Paint mPaint;
	private EditText mEdit;

	public FloatSearchEdit(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FloatSearchEdit(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FloatSearchEdit(Context context) {
		super(context);
	}

	public void init(int imgHeight) {
		setBackgroundColor(Color.TRANSPARENT);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);

		mEdit = new EditText(getContext()) {
			@Override
			protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
				Logger.d(getClass(), "onTextChanged: " + lengthAfter);
				if (mEdit != null && lengthAfter == 0) {
					mEdit.setHintTextColor(Color.parseColor(MyUtils.ColorUtil.mixColor(
							MyUtils.ColorUtil.rgbToHex(new int[]{Color.red(mPaint.getColor()), Color.green(mPaint.getColor()), Color.blue(mPaint.getColor())}), Util.WHITE, Util.BUILDING_COLOR_LAYER_2)));
				}
				super.onTextChanged(text, start, lengthBefore, lengthAfter);
			}
		};
		mEdit.setBackgroundColor(Color.TRANSPARENT);
		mEdit.setSingleLine();
		mEdit.setIncludeFontPadding(false);
		mEdit.setGravity(Gravity.CENTER);
		mEdit.setHint(R.string.title_keyword);
		// Note: set top padding to make the text can be at the vertical center exactly
		mEdit.setPadding(0, 10, 0, 0);
		mEdit.setTextColor(Color.parseColor(Util.LIGHT_BLACK));
		mEdit.setTextSize(TypedValue.COMPLEX_UNIT_PX, Util.TEXT_SIZE);
		mEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
		mEdit.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE) {
					v.clearFocus();
					InputMethodManager mImm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					mImm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				return false;
			}
		});
		addView(mEdit, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, imgHeight));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawRect(0, getHeight() - Util.LIST_DIVIDER_HEIGHT, getWidth(), getHeight(), mPaint);
	}

	public void onDestroy() {
		mEdit.setOnEditorActionListener(null);
	}
	//====================================================================================================
	public void setPaintColor(String color) {
		mPaint.setColor(Color.parseColor(color));
		mEdit.setHintTextColor(Color.parseColor(MyUtils.ColorUtil.mixColor(color, Util.WHITE, Util.BUILDING_COLOR_LAYER_2)));
		invalidate();
	}

	public EditText getEdit() {
		return mEdit;
	}
}
