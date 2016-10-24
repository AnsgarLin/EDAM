package com.edam.page.detail;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.edam.util.Logger;
import com.edam.util.MyUtils;
import com.edam.util.Util;

public class Title extends TextView {
	private static final String ELLIPSIS = "...";
	private boolean mIsReset;
	private int titleButtomPadding;

	private String mOriginText;
	private String mText;

	public Title(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public Title(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public Title(Context context) {
		super(context);
	}

	public Title(Context context, int titleButtonWrapW, int rightPadding) {
		super(context);
		titleButtomPadding = titleButtonWrapW - rightPadding;
		setOnTouchListener(new TouchListener());
	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int before, int after) {
		super.onTextChanged(text, start, before, after);
		if (!mIsReset) {
			mOriginText = text.toString();
		}
		mText = text.toString();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if ((getLineCount() == 2) & !mIsReset) {
			StaticLayout textLayout = new StaticLayout(mText, getPaint(), getWidth() - getPaddingLeft() - getPaddingRight(), Alignment.ALIGN_NORMAL,
					0, 0, false);

			if (isTextOutOfBound(textLayout, textLayout.getLineEnd(1), textLayout.getWidth() - titleButtomPadding)) {
				int subOffset = textLayout.getLineEnd(1) - 1;

				while (textLayout.getPrimaryHorizontal(subOffset) > (textLayout.getWidth() - titleButtomPadding)) {
					subOffset--;
				}
				mText = mText.substring(0, subOffset).trim();
				mText += ELLIPSIS;
			}

			// Check whether the text is still out of bound, if so, reset again
			textLayout = new StaticLayout(mText, getPaint(), getWidth() - getPaddingLeft() - getPaddingRight(), Alignment.ALIGN_NORMAL,
					0, 0, false);
			if (textLayout.getPrimaryHorizontal(textLayout.getLineEnd(1)) > (textLayout.getWidth() - titleButtomPadding)) {
				int subOffset = textLayout.getLineEnd(1) - 1;

				while ((mText.charAt(subOffset) == '.')
						|| (textLayout.getPrimaryHorizontal(subOffset) > (textLayout.getWidth() - titleButtomPadding))) {
					subOffset--;
				}
				mText = mText.substring(0, subOffset).trim();
				mText += ELLIPSIS;
			}

			mIsReset = true;
			setText(mText);
		}
		super.onDraw(canvas);
	}

	private boolean isTextOutOfBound(StaticLayout textLayout, int lastOffset, int bound) {
		// Use total word count to get the right position of the latest word
		return ((textLayout.getPrimaryHorizontal(lastOffset) == 0) && (textLayout.getPrimaryHorizontal(lastOffset - 1) > bound))
				|| (textLayout.getPrimaryHorizontal(lastOffset) > bound);
	}
	// =====================================================================================================//
	private class TouchListener implements OnTouchListener {
		private float ox, oy;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Logger.d(getClass(), "ACTION_DOWN");
				recordCurrentPos(event);
				break;
			case MotionEvent.ACTION_MOVE:
				Logger.d(getClass(), "ACTION_MOVE");
				break;
			case MotionEvent.ACTION_UP:
				Logger.d(getClass(), "ACTION_UP");
				if (!isMove(event)) {
					Util.mToast = MyUtils.ToastUtil.restart(getContext(), Util.mToast, mOriginText);
					return false;
				}
				break;
			}
			return true;
		}

		private void recordCurrentPos(MotionEvent event) {
			ox = event.getRawX();
			oy = event.getRawY();
		}

		private boolean isMove(MotionEvent event) {
			return (Math.abs(event.getRawX() - ox) >= 5) || (Math.abs(event.getRawY() - oy) >= 5);
		}
	}
	// =====================================================================================================//
	// For user to run codes after finishing custom ellipsis
	public interface EllipsizeListener {
		void ellipsizeStateChanged(boolean ellipsized);
	}

	public void resetState() {
		mIsReset = false;
	}
}
