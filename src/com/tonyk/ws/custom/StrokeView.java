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

	private Paint paint;
	private PointF startPoint, endPoint;
	public static boolean isDrawing;
	
	private String[] colors = new String[] {"#50f44336", "#50e91e63", "#509c27b0", "#50673ab7", "#503f51b5",
			"#502196f3", "#5003a9f4", "#5000bcd4", "#50009688", "#504caf50", "#508bc34a", "#50cddc39",
			"#50ff9800", "#50ff5722", "#50795548"};
	
	private Random random;
	
	private ArrayList<PointF> listStartPoint = new ArrayList<PointF>();
	private ArrayList<PointF> listEndPoint = new ArrayList<PointF>();
	private ArrayList<Paint> listPaint = new ArrayList<Paint>();

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

	private void init() {
		random = new Random();
		paint = new Paint();
//		paint.setColor(Color.RED);
//		paint.setStyle(Style.STROKE);
//		paint.setStrokeWidth(2);
//		paint.setAntiAlias(true);
		
	    paint.setStrokeWidth(50);               // set the size
	    paint.setDither(true);                    // set the dither to true
	    paint.setStyle(Paint.Style.STROKE);       // set to STOKE
	    // paint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
	    paint.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
	    // paint.setPathEffect(new CornerPathEffect(10) );   // set the path effect when they join.
	    paint.setAntiAlias(true);                         // set anti alias so it smooths
	}

	public void initStartPoint(float x, float y) {
		startPoint = new PointF(x, y);
	}
	
	public void initEndPoint() {
		endPoint = new PointF();
	}
	
	public void setEndPoint(float x, float y) {
		endPoint.x = x;
		endPoint.y = y;
	}
	
	public void setColorRandom() {
		paint.setColor(Color.parseColor(colors[random.nextInt(colors.length)]));
	}
	
	public void setStrokeWidth(float width) {
		paint.setStrokeWidth(width);
	}
	
	public void addFixLine() {
		Log.i("addFixLine", "addFixLine");
		listStartPoint.add(startPoint);
		listEndPoint.add(endPoint);
		Paint fixPaint = new Paint(paint);
		listPaint.add(fixPaint);
	}
	
	public void reset() {
		listEndPoint.clear();
		listPaint.clear();
		listStartPoint.clear();
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		for (int i = 0; i < listStartPoint.size(); i++) {
			PointF startP = listStartPoint.get(i);
			PointF endP = listEndPoint.get(i);
			Paint paint = listPaint.get(i);
			canvas.drawLine(startP.x, startP.y, endP.x, endP.y, paint);
		}
		if (isDrawing) {
			canvas.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y, paint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			startPoint = new PointF(event.getX(), event.getY());
			endPoint = new PointF();
			isDrawing = true;
			break;
		case MotionEvent.ACTION_MOVE:
			if (isDrawing) {
				endPoint.x = event.getX();
				endPoint.y = event.getY();
				invalidate();
			}
			break;
		case MotionEvent.ACTION_UP:
			if (isDrawing) {
				endPoint.x = event.getX();
				endPoint.y = event.getY();
				isDrawing = false;
				invalidate();
			}
			break;
		default:
			break;
		}
		return true;
	}
}