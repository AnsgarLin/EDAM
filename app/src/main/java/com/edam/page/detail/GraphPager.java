package com.edam.page.detail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edam.main.R;
import com.edam.util.MyUtils;
import com.edam.util.Util;

public class GraphPager extends FrameLayout{
	private ImageView mGraphPageImage;
	private TextView mImageNotLodFinish;
	/*
	 * Graph touched mode
	 */
	private boolean isGraphPageTouched;

	public GraphPager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public GraphPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public GraphPager(Context context) {
		super(context);
		init();
	}

	private void init() {
		FrameLayout graphPageImageFrame = new FrameLayout(getContext());
		FrameLayout.LayoutParams graphPageImageFrameParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		graphPageImageFrame.setLayoutParams(graphPageImageFrameParams);

		mImageNotLodFinish = new TextView(getContext());
		mImageNotLodFinish.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		mImageNotLodFinish.setGravity(Gravity.CENTER);
		mImageNotLodFinish.setTextSize(TypedValue.COMPLEX_UNIT_PX, Util.DATE_IN_HEIGHT * Util.TEXT_SIZE_ERROR);
		mImageNotLodFinish.setText(R.string.image_not_load_finish);

		RelativeLayout graphPageImageWrap = new RelativeLayout(getContext());
		FrameLayout.LayoutParams graphPageImageWrapParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		graphPageImageWrapParams.setMargins((int) Util.BACK_FRAME_STROKE_WIDTH, (int) Util.BACK_FRAME_STROKE_WIDTH, (int) Util.BACK_FRAME_STROKE_WIDTH, (int) Util.BACK_FRAME_STROKE_WIDTH);
		graphPageImageWrap.setLayoutParams(graphPageImageWrapParams);

		mGraphPageImage = new ImageView(getContext());
		mGraphPageImage.setClickable(true);
		mGraphPageImage.setScaleType(ScaleType.MATRIX);
		mGraphPageImage.setOnTouchListener(new ImageTouchListener());
		graphPageImageWrap.addView(mGraphPageImage, new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		addView(mImageNotLodFinish);
		addView(graphPageImageWrap);
	}

	public void onDestroy() {
		mGraphPageImage.setOnTouchListener(null);
		mGraphPageImage.setImageBitmap(null);
	}
	// =====================================================================================================//
	private class ImageTouchListener implements OnTouchListener {
		private final int NONE = 0;
		private final int DRAG = 1;
		private final int ZOOM = 2;
		private int mGraphPageTouchMode;

		private Matrix tempMatrix;
		private Matrix startMatrix;

		private PointF startPoint;
		private PointF startMidPoint;
		private float startDistance;

		public ImageTouchListener() {
			mGraphPageTouchMode = NONE;

			tempMatrix = new Matrix();
			startMatrix = new Matrix();

			startPoint = new PointF();
			startMidPoint = new PointF();
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				isGraphPageTouched = true;
				mGraphPageTouchMode = DRAG;
				startPoint.set(event.getX(), event.getY());
				startMatrix.set(mGraphPageImage.getImageMatrix());
				tempMatrix.set(startMatrix);

				break;
			case MotionEvent.ACTION_MOVE:
				if (mGraphPageTouchMode != NONE) {
					tempMatrix.set(startMatrix);
					if (mGraphPageTouchMode == DRAG) {
						tempMatrix.postTranslate(event.getX() - startPoint.x, event.getY() - startPoint.y);
					} else if (mGraphPageTouchMode == ZOOM) {
						float scale = (float) MyUtils.MathUtil.getDistance(event.getX(0), event.getX(1), event.getY(0), event.getY(1))
								/ startDistance;
						tempMatrix.postScale(scale, scale, startMidPoint.x, startMidPoint.y);
					}
				}

				break;
			case MotionEvent.ACTION_UP:
				isGraphPageTouched = false;
				mGraphPageTouchMode = NONE;

				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				mGraphPageTouchMode = ZOOM;
				startDistance = (float) MyUtils.MathUtil.getDistance(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
				startMidPoint = MyUtils.MathUtil.getMidPoint(event.getX(0), event.getX(1), event.getY(0), event.getY(1));
				startMatrix.set(mGraphPageImage.getImageMatrix());
				tempMatrix.set(startMatrix);

				break;
			case MotionEvent.ACTION_POINTER_UP:
				mGraphPageTouchMode = NONE;

				break;
			default:
				break;
			}
			mGraphPageImage.setImageMatrix(tempMatrix);

			return true;
		}
	}
	// =====================================================================================================//
	public void setImageMatrix(Matrix matrix) {
		mGraphPageImage.setImageMatrix(matrix);
	}

	public void setImageBitmap(Bitmap bitmap) {
		mImageNotLodFinish.setText("");
		mGraphPageImage.setImageBitmap(bitmap);
	}

	public ImageView getImageView() {
		return mGraphPageImage;
	}
}
