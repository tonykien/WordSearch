package com.tonyk.ws.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * A view container with layout behavior like that of the Swing FlowLayout.
 * Originally from
 * http://nishantvnair.wordpress.com/2010/09/28/flowlayout-in-android/ itself
 * derived from
 * http://stackoverflow.com/questions/549451/line-breaking-widget-layout
 * -for-android
 * 
 * @author Melinda Green
 */
public class FlowLayout extends ViewGroup {
	private final static int PAD_H = 2, PAD_V = 2; // Space between child views.
	private int mHeight;

	public FlowLayout(Context context) {
		super(context);
	}

	public FlowLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		assert (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED);
		final int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()
				- getPaddingRight();
		int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
		final int count = getChildCount();
		int xpos = getPaddingLeft();
		int ypos = getPaddingTop();
		int childHeightMeasureSpec;
		if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST)
			childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST);
		else
			childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		mHeight = 0;
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
						childHeightMeasureSpec);
				final int childw = child.getMeasuredWidth();
				mHeight = Math.max(mHeight, child.getMeasuredHeight() + PAD_V);
				if (xpos + childw > width) {
					xpos = getPaddingLeft();
					ypos += mHeight;
				}
				xpos += childw + PAD_H;
			}
		}
		if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
			height = ypos + mHeight;
		} else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
			if (ypos + mHeight < height) {
				height = ypos + mHeight;
			}
		}
		height += 5; // Fudge to avoid clipping bottom of last row.
		setMeasuredDimension(width, height);
	} // end onMeasure()

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int width = r - l;
		int xpos = getPaddingLeft();
		int ypos = getPaddingTop();
		int newLineIdx = 0;
		int lineLength = 0;
		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				final int childw = child.getMeasuredWidth();
				final int childh = child.getMeasuredHeight();
				if (xpos + childw > width) {
					int padding = (width - lineLength) / 2;
					for (int j = newLineIdx; j < i; j++) {
						View childView = getChildAt(j);
						int childw2 = childView.getMeasuredWidth();
						int childh2 = childView.getMeasuredHeight();
						childView.layout(padding, ypos, padding + childw2, ypos + childh2);
						padding += childw2 + PAD_H;
					}
					
					xpos = getPaddingLeft();
					ypos += mHeight;
					
					newLineIdx = i;
					lineLength = 0;
					
				}
				child.layout(xpos, ypos, xpos + childw, ypos + childh);
				xpos += childw + PAD_H;
				lineLength += childw + PAD_H;
			}
		}
		
		int padding = getPaddingLeft() + (width - lineLength) / 2;
		for (int j = newLineIdx; j < getChildCount(); j++) {
			View childView = getChildAt(j);
			int childw2 = childView.getMeasuredWidth();
			int childh2 = childView.getMeasuredHeight();
			childView.layout(padding, ypos, padding + childw2, ypos + childh2);
			padding += childw2 + PAD_H;
		}
	} // end onLayout()

}
