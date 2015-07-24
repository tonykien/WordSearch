package com.tonyk.ws.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class StrokeView extends View {

	private Paint paint;
	private PointF startPoint, endPoint;
	private boolean isDrawing;

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
		paint = new Paint();
//		paint.setColor(Color.RED);
//		paint.setStyle(Style.STROKE);
//		paint.setStrokeWidth(2);
//		paint.setAntiAlias(true);
		
		paint.setColor(Color.parseColor("#60868686"));                    // set the color
	    paint.setStrokeWidth(30);               // set the size
	    paint.setDither(true);                    // set the dither to true
	    paint.setStyle(Paint.Style.STROKE);       // set to STOKE
	    paint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
	    paint.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
	    // paint.setPathEffect(new CornerPathEffect(10) );   // set the path effect when they join.
	    paint.setAntiAlias(true);                         // set anti alias so it smooths
	}

	@Override
	protected void onDraw(Canvas canvas) {
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