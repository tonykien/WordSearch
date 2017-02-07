package com.tonyk.ws.custom;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class StrokeView extends View {

	private Paint mPaint;
	private PointF mStartPoint, mEndPoint;
	private boolean mIsDrawing;
	
	private String[] colors = new String[] {"#50f44336", "#50e91e63", "#509c27b0", "#50673ab7", "#503f51b5",
			"#502196f3", "#5003a9f4", "#5000bcd4", "#50009688", "#504caf50", "#508bc34a", "#50cddc39",
			"#50ff9800", "#50ff5722", "#50795548"};
	
	private Random random;
	
	private ArrayList<PointF> mListStartPoint = new ArrayList<PointF>();
	private ArrayList<PointF> mListEndPoint = new ArrayList<PointF>();
	private ArrayList<Paint> mListPaint = new ArrayList<Paint>();

	public StrokeView(Context context) {
		super(context);
		init();
	}
	
	public StrokeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public StrokeView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	public void setIsDrawing(boolean drawing) {
		mIsDrawing = drawing;
	}
	
	public boolean isDrawing() {
		return mIsDrawing;
	}

	private void init() {
		random = new Random();
		mPaint = new Paint();
//		paint.setColor(Color.RED);
//		paint.setStyle(Style.STROKE);
//		paint.setStrokeWidth(2);
//		paint.setAntiAlias(true);
		
	    mPaint.setStrokeWidth(50);               // set the size
	    mPaint.setDither(true);                    // set the dither to true
	    mPaint.setStyle(Paint.Style.STROKE);       // set to STOKE
	    // paint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
	    mPaint.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
	    // paint.setPathEffect(new CornerPathEffect(10) );   // set the path effect when they join.
	    mPaint.setAntiAlias(true);                         // set anti alias so it smooths
	}

	public void initStartPoint(float x, float y) {
		mStartPoint = new PointF(x, y);
	}
	
	public void initEndPoint(float x, float y) {
		mEndPoint = new PointF(x, y);
	}
	
	public void setEndPoint(float x, float y) {
		mEndPoint.x = x;
		mEndPoint.y = y;
	}
	
	public void setColorRandom() {
		mPaint.setColor(Color.parseColor(colors[random.nextInt(colors.length)]));
	}
	
	public void setColorOfPlayer1() {
		mPaint.setColor(Color.parseColor(colors[4]));
	}
	
	public void setColorOfPlayer2() {
		mPaint.setColor(Color.parseColor(colors[8]));
	}
	
	public void setStrokeWidth(float width) {
		mPaint.setStrokeWidth(width);
	}
	
	public Paint getPaint() {
		return mPaint;
	}
	
	public PointF getCurrentStartPoint() {
		return mStartPoint;
	}
	
	public PointF getCurrentEndPoint() {
		return mEndPoint;
	}
	
	public void addFixLine() {
		Log.i("addFixLine", "addFixLine");
		mListStartPoint.add(mStartPoint);
		mListEndPoint.add(mEndPoint);
		Paint fixPaint = new Paint(mPaint);
		mListPaint.add(fixPaint);
	}
	
	public void addFixLine(PointF startPoint, PointF endPoint, Paint paint) {
		Log.i("addFixLine", "addFixLine2");
		mListStartPoint.add(startPoint);
		mListEndPoint.add(endPoint);
		Paint fixPaint = new Paint(paint);
		mListPaint.add(fixPaint);
	}
	
	public void reset() {
		mListEndPoint.clear();
		mListPaint.clear();
		mListStartPoint.clear();
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		for (int i = 0; i < mListStartPoint.size(); i++) {
			PointF startP = mListStartPoint.get(i);
			PointF endP = mListEndPoint.get(i);
			Paint paint = mListPaint.get(i);
			canvas.drawLine(startP.x, startP.y, endP.x, endP.y, paint);
		}
		if (mIsDrawing) {
			canvas.drawLine(mStartPoint.x, mStartPoint.y, mEndPoint.x, mEndPoint.y, mPaint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mStartPoint = new PointF(event.getX(), event.getY());
			mEndPoint = new PointF();
			mIsDrawing = true;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mIsDrawing) {
				mEndPoint.x = event.getX();
				mEndPoint.y = event.getY();
				invalidate();
			}
			break;
		case MotionEvent.ACTION_UP:
			if (mIsDrawing) {
				mEndPoint.x = event.getX();
				mEndPoint.y = event.getY();
				mIsDrawing = false;
				invalidate();
			}
			break;
		default:
			break;
		}
		return true;
	}
}