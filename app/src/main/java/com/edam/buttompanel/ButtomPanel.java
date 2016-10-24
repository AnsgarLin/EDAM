package com.edam.buttompanel;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Space;

import com.edam.main.ActPageSlideTab;
import com.edam.main.R;
import com.edam.util.MyUtils;
import com.edam.util.Util;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class ButtomPanel extends RelativeLayout {
	private ActPageSlideTab mActPageSlideTab;
	private RelativeLayout mButtomPanelBar;
	private Space mButtomPanelSpace;
	private float mButtomPanelBarHeight;
	private View mButtonPanelContent;
	private ButtomPanelInfo mButtonPanelInfo;
	private View mAbout;
	private Paint mWhitePaint;
	private float mPixel;
	private boolean mIsOpen;

	public ButtomPanel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public ButtomPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ButtomPanel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void init(ActPageSlideTab actPageSlideTab) {
		mActPageSlideTab = actPageSlideTab;

		getLayoutParams().height = (int) (mButtomPanelBarHeight = MyUtils.TypedValueUtil.toPixel(getResources().getDisplayMetrics(), "dp", Util.BUTTOM_PANEL_BAR_HEIGHT));

		mButtomPanelSpace = (Space) ((ViewGroup) getParent()).findViewById(R.id.buttom_panel_space);
		mButtomPanelSpace.getLayoutParams().height = (int) mButtomPanelBarHeight;

		mButtomPanelBar = (RelativeLayout) findViewById(R.id.buttom_panel_bar);
		mButtomPanelBar.getLayoutParams().height = (int) mButtomPanelBarHeight;

		mWhitePaint = new Paint();
		mPixel = Util.PIXEL;
		mAbout = new View(getContext()){
			@Override
			protected void onDraw(Canvas canvas) {
				super.onDraw(canvas);

				canvas.save();
				// Rotate canvas with pivots
				canvas.rotate(45, mPixel * 24f, mPixel * 17f);
				canvas.drawCircle(mPixel * 24f, mPixel * 17f, mPixel * 1.5f, mWhitePaint);
				canvas.drawRect(mPixel * 22.5f, mPixel * 17f, mPixel * 25.5f, mPixel * 32.5f, mWhitePaint);
				canvas.drawCircle(mPixel * 24f, mPixel * 32.5f, mPixel * 1.5f, mWhitePaint);
				canvas.drawCircle(mPixel * 24f, mPixel * 37.5f, mPixel * 1.5f, mWhitePaint);
				canvas.restore();

				canvas.save();
				canvas.rotate(-45, mPixel * 24f, mPixel * 17f);
				// Rotate canvas with pivots
				canvas.drawCircle(mPixel * 24f, mPixel * 17f, mPixel * 1.5f, mWhitePaint);
				canvas.drawRect(mPixel * 22.5f, mPixel * 17f, mPixel * 25.5f, mPixel * 32.5f, mWhitePaint);
				canvas.drawCircle(mPixel * 24f, mPixel * 32.5f, mPixel * 1.5f, mWhitePaint);
				canvas.drawCircle(mPixel * 24f, mPixel * 37.5f, mPixel * 1.5f, mWhitePaint);
				canvas.restore();
			}
		};
		RelativeLayout.LayoutParams aboutParams = new RelativeLayout.LayoutParams((int) mButtomPanelBarHeight, (int) mButtomPanelBarHeight);
		aboutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		mAbout.setLayoutParams(aboutParams);
		mAbout.setOnClickListener(new OnClickListener() {
			float contentHeight;
			ArrayList<ObjectAnimator> list = new ArrayList<ObjectAnimator>();

			@Override
			public void onClick(final View v) {
				list.clear();
				if (mIsOpen) {
					addAnimation(list, (View) v.getParent().getParent(), "y", mButtomPanelSpace.getY() - contentHeight, mButtomPanelSpace.getY());
					addAnimation(list, v, "rotation", -180f, 0f);

					mIsOpen = false;
				} else {
					contentHeight = ((ViewGroup) getParent()).getHeight() - v.getHeight();

					// Reset layout by button
					RelativeLayout.LayoutParams buttomPanelParams = (RelativeLayout.LayoutParams) getLayoutParams();
					buttomPanelParams.height = (int) (v.getHeight() + contentHeight);
					buttomPanelParams.setMargins(0, 0, 0, (int) (-contentHeight));
					setLayoutParams(buttomPanelParams);

					mButtonPanelContent.findViewById(R.id.info_container).getLayoutParams().height =
							(int) (contentHeight - v.getHeight());

					addAnimation(list, (View) v.getParent().getParent(), "y", mButtomPanelSpace.getY(), mButtomPanelSpace.getY() - contentHeight);
					addAnimation(list, v, "rotation", 0f, -180f);
					mIsOpen = true;
				}
				AnimatorSet set = new AnimatorSet();
				set.playTogether(list.toArray(new ObjectAnimator[list.size()]));
				set.setDuration(Util.ANIMATION_BASIC_DURATION_MS / 3 * 2).start();
			}
		});
		mButtomPanelBar.addView(mAbout);

		mButtonPanelInfo = (ButtomPanelInfo) MyUtils.InflateUtil.InflateReource(getContext(), R.layout.buttom_panel_info, this);
		mButtonPanelInfo.init();
		addView(mButtonPanelContent = mButtonPanelInfo);
	}

	public void addAnimation(List<ObjectAnimator> list, View view, String property, float start, float end) {
		list.add(ObjectAnimator.ofFloat(view, property, start, end));
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		mActPageSlideTab = (ActPageSlideTab) ((RelativeLayout) getParent()).findViewById(R.id.act_page_slide_tab);
		mActPageSlideTab.setButtomPanel(this);
	}

	public void onDestroy() {
		mActPageSlideTab = null;
		mButtomPanelBar.setOnClickListener(null);
		mButtomPanelBar = null;
		mAbout.setOnClickListener(null);
		mAbout = null;
		mButtonPanelInfo.onDestroy();
		mButtonPanelInfo = null;
	}
	// =====================================================================
	public void setButtomPanelColor(String color) {
		setBackgroundColor(Color.parseColor(MyUtils.ColorUtil.getColorBetween(color, Util.WHITE, Util.BASIC_ALPHA)));
		mWhitePaint.setColor(Color.parseColor(MyUtils.ColorUtil.getColorBetween(color, Util.WHITE, Util.BASIC_ALPHA)));
		mButtomPanelBar.setBackgroundColor(Color.parseColor(color));
		mButtonPanelInfo.setPaintColor(color);
		invalidate();
	}

	public void close() {
		mAbout.performClick();
	}

	public boolean isOpen() {
		return mIsOpen;
	}
}
