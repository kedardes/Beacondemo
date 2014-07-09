package com.i360.estimotedemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;


public class DistanceBackgroundView extends android.view.View {

	private final Drawable drawable;
	
	public DistanceBackgroundView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		drawable = context.getResources().getDrawable(R.drawable.bg_distance);
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
	 super.onDraw(canvas);
	 
	 int width = drawable.getIntrinsicWidth() * canvas.getHeight() / drawable.getIntrinsicHeight();
	 int deltaX = (width - canvas.getWidth())/2;
	 drawable.setBounds(-deltaX, 0, width-deltaX, canvas.getHeight());
	 drawable.draw(canvas);
	}

}
