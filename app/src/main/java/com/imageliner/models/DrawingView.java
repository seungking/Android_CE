package com.imageliner.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.imageliner.activities.Painting;

import java.util.ArrayList;

public class DrawingView extends View
{
	Path mDrawPath;
	Paint mBackgroundPaint;
	Bitmap mBitmapBackground;
	Paint mDrawPaint;
	Canvas mDrawCanvas;
	Bitmap mCanvasBitmap;
	Bitmap temp;
	int width;
	int height;

	ArrayList<Path> mPaths = new ArrayList<>();
	ArrayList<Paint> mPaints = new ArrayList<>();
	ArrayList<Path> mUndonePaths = new ArrayList<>();
	ArrayList<Paint> mUndonePaints = new ArrayList<>();

	private boolean extractingColor = false;

	// Set default values
	private int mBackgroundColor = 0xFFFFFFFF;
	private int mPaintColor = Color.parseColor("#ff4444");
	private int mStrokeWidth = 10;

	public DrawingView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	private void init()
	{
		mDrawPath = new Path();
		mBackgroundPaint = new Paint();
		temp = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
//		initPaint();
		Log.d("LOG1", "drawingview 1");

	}

	private void initPaint()
	{
		mDrawPaint = new Paint();
		mDrawPaint.setColor(mPaintColor);
		mDrawPaint.setAntiAlias(true);
		mDrawPaint.setStrokeWidth(mStrokeWidth);
		mDrawPaint.setStyle(Paint.Style.STROKE);
		mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
		mDrawPaint.setStrokeCap(Paint.Cap.ROUND);

		mDrawCanvas = new Canvas();

		mBitmapBackground = temp;

		Log.d("LOG1", "drawingview 2");
	}

	public void setwh(int width, int height){
		this.width = width;
		this.height = height;
	}

	public void initPaintwithbitmap(Bitmap bitmap)
	{
		mDrawPaint = new Paint();
		mDrawPaint.setColor(mPaintColor);
		mDrawPaint.setAntiAlias(true);
		mDrawPaint.setStrokeWidth(mStrokeWidth);
		mDrawPaint.setStyle(Paint.Style.STROKE);
		mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
		mDrawPaint.setStrokeCap(Paint.Cap.ROUND);

		mDrawCanvas = new Canvas();

        float bmpWidth = bitmap.getWidth();
        float bmpHeight = bitmap.getHeight();

        Log.d("LOG1", "width : " + String.valueOf(width));
        Log.d("LOG1", "height : " + String.valueOf(height));
        Log.d("LOG1", "bmpwidth : " + String.valueOf(bmpWidth));
        Log.d("LOG1", "bmpHeight : " + String.valueOf(bmpHeight));

        if (bmpWidth >= width) {
            // 원하는 너비보다 클 경우의 설정
            float mWidth = bmpWidth / 100;
            float scale = width/ mWidth;
            bmpWidth *= (scale / 100);
            bmpHeight *= (scale / 100);
            Log.d("LOG1", "resize case1");
        } else if (bmpHeight >= height ) {
            // 원하는 높이보다 클 경우의 설정
            float mHeight = bmpHeight / 100;
            float scale = height/ mHeight;
            bmpWidth *= (scale / 100);
            bmpHeight *= (scale / 100);
            Log.d("LOG1", "resize case2");
        } else if(bmpWidth>=bmpHeight) {
            float scale = (float)(width/bmpWidth);
            bmpWidth *= (scale);
            bmpHeight *= (scale);
            Log.d("LOG1", "resize case3");
        } else if(bmpWidth<bmpHeight){
            float scale = (float)(height / bmpHeight);
            Log.d("LOG1", String.valueOf(scale));
            bmpWidth *= (scale);
            bmpHeight *= (scale);
            Log.d("LOG1", "resize case4");
        }if (bmpWidth >= width) {
			// 원하는 너비보다 클 경우의 설정
			float mWidth = bmpWidth / 100;
			float scale = width/ mWidth;
			bmpWidth *= (scale / 100);
			bmpHeight *= (scale / 100);
			Log.d("LOG1", "resize case1");
		} if (bmpHeight >= height ) {
			// 원하는 높이보다 클 경우의 설정
			float mHeight = bmpHeight / 100;
			float scale = height / mHeight;
			bmpWidth *= (scale / 100);
			bmpHeight *= (scale / 100);
			Log.d("LOG1", "resize case2");
		}


        Log.d("LOG1", String.valueOf(bmpWidth));
        Log.d("LOG1", String.valueOf(bmpHeight));

        temp = Bitmap.createScaledBitmap(bitmap, (int) bmpWidth, (int) bmpHeight, true);
		mBitmapBackground = temp;

		Painting.setbitmapsize(bmpWidth,bmpHeight,Math.abs((this.getWidth()-mBitmapBackground.getWidth()))/2,Math.abs((this.getHeight()-mBitmapBackground.getHeight()))/2);
		Log.d("LOG1", "drawingview 3");

}

	private void drawBackground(Canvas canvas)
	{
		mBackgroundPaint.setColor(mBackgroundColor);
		mBackgroundPaint.setStyle(Paint.Style.FILL);
		canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), mBackgroundPaint);
		canvas.drawBitmap( mBitmapBackground, (this.getWidth()-mBitmapBackground.getWidth())/2, (this.getHeight()-mBitmapBackground.getHeight())/2, new Paint() );
		Log.d("LOG1", "drawingview 4");
	}

	private void drawPaths(Canvas canvas)
	{
		int i = 0;
		for (Path p : mPaths)
		{
			canvas.drawPath(p, mPaints.get(i));
			i++;
		}
		Log.d("LOG1", "drawingview 5");
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		drawBackground(canvas);
		drawPaths(canvas);

		canvas.drawPath(mDrawPath, mDrawPaint);
		Log.d("LOG1", "drawingview 6");
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

		Log.d("LOG1", "drawingview 7");
		mDrawCanvas = new Canvas(mCanvasBitmap);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		float touchX = event.getX();
		float touchY = event.getY();

		int index=0;
		int id=0;
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				mDrawPath.moveTo(touchX, touchY);
				//mDrawPath.addCircle(touchX, touchY, mStrokeWidth/10, Path.Direction.CW);
			case MotionEvent.ACTION_POINTER_DOWN:

				index = event.getActionIndex();
				id = event.getPointerId(index);

				if (extractingColor) { //If the user chose the 'extract color' menu option, the touch event indicates where they want to extract the color from.
					extractingColor = false;

					Painting.aaa(touchX,touchY);

				}

				break;
			case MotionEvent.ACTION_MOVE:
				mDrawPath.lineTo(touchX, touchY);
				break;
			case MotionEvent.ACTION_UP:
				mDrawPath.lineTo(touchX, touchY);
				mPaths.add(mDrawPath);
				mPaints.add(mDrawPaint);
				mDrawPath = new Path();
				initPaint();
				break;
			default:
				return false;
		}

		invalidate();
		return true;
	}

	public void clearCanvas()
	{
		mPaths.clear();
		mPaints.clear();
		mUndonePaths.clear();
		mUndonePaints.clear();
		mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
		invalidate();
	}

	public void setPaintColor(int color)
	{
		mPaintColor = color;
		mDrawPaint.setColor(mPaintColor);
	}

	public void setPaintStrokeWidth(int strokeWidth)
	{
		mStrokeWidth = strokeWidth;
		mDrawPaint.setStrokeWidth(mStrokeWidth);
	}

	public void setBackgroundColor(int color)
	{
		mBackgroundColor = color;
		mBackgroundPaint.setColor(mBackgroundColor);
		invalidate();
	}

	public Bitmap getBitmap()
	{
		drawBackground(mDrawCanvas);
		drawPaths(mDrawCanvas);
		return mCanvasBitmap;
	}

	public void undo()
	{
		if (mPaths.size() > 0)
		{
			mUndonePaths.add(mPaths.remove(mPaths.size() - 1));
			mUndonePaints.add(mPaints.remove(mPaints.size() - 1));
			invalidate();
		}
	}

	public void redo()
	{
		if (mUndonePaths.size() > 0)
		{
			mPaths.add(mUndonePaths.remove(mUndonePaths.size() - 1));
			mPaints.add(mUndonePaints.remove(mUndonePaints.size() - 1));
			invalidate();
		}
	}

	public void extractingColor(){
		extractingColor = true;
	}
}
