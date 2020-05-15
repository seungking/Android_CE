package com.imageliner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.Iterator;

public class PaintView extends View {
    private static final float TOUCH_TOLERANCE = 4.0f;
    private Bitmap bitmap;
    private Paint bitmapPaint = new Paint(4);
    private Canvas canvas;
    private int height;
    private Bitmap lined_image;
    private float mX;
    private float mY;
    private Paint paint;
    private Path path = new Path();
    private ArrayList<Path> paths = new ArrayList();
    private ArrayList<Path> undonePaths = new ArrayList();
    private int width;

    public PaintView(Context context, AttributeSet attributeSet, Bitmap bitmap, int i, int i2) {
        super(context, attributeSet);
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);
        this.paint.setColor(-65536);
        this.paint.setStyle(Style.STROKE);
        this.paint.setStrokeJoin(Join.ROUND);
        this.paint.setStrokeCap(Cap.BUTT);
        this.paint.setStrokeWidth(15.0f);
        this.canvas = new Canvas();
    }

    /* Access modifiers changed, original: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        Iterator it = this.paths.iterator();
        while (it.hasNext()) {
            canvas.drawPath((Path) it.next(), this.paint);
        }
        canvas.drawPath(this.path, this.paint);
    }

    private void touchStart(float f, float f2) {
        this.path.reset();
        this.path.moveTo(f, f2);
        this.mX = f;
        this.mY = f2;
    }

    private void touchMove(float f, float f2) {
        float abs = Math.abs(f - this.mX);
        float abs2 = Math.abs(f2 - this.mY);
        if (abs >= TOUCH_TOLERANCE || abs2 >= TOUCH_TOLERANCE) {
            Path path = this.path;
            abs2 = this.mX;
            float f3 = this.mY;
            path.quadTo(abs2, f3, (f + abs2) / 2.0f, (f2 + f3) / 2.0f);
            this.mX = f;
            this.mY = f2;
        }
    }

    private void touchUp() {
        this.path.lineTo(this.mX, this.mY);
        this.canvas.drawPath(this.path, this.paint);
        this.paths.add(this.path);
        this.path = new Path();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int action = motionEvent.getAction();
        if (action == 0) {
            touchStart(x, y);
            invalidate();
        } else if (action == 1) {
            touchUp();
            invalidate();
        } else if (action == 2) {
            touchMove(x, y);
            invalidate();
        }
        return true;
    }

    public Bitmap getBitmap() {
        setDrawingCacheEnabled(true);
        buildDrawingCache();
        Bitmap createBitmap = Bitmap.createBitmap(getDrawingCache());
        setDrawingCacheEnabled(false);
        return createBitmap;
    }

    public void clear() {
        this.paths = new ArrayList();
        invalidate();
        System.gc();
    }

    public void setPathColor(int i) {
        this.paint.setColor(i);
    }

    public void setsize(int i) {
        this.paint.setStrokeWidth((float) i);
    }

    public void undo() {
        if (this.paths.size() > 0) {
            Log.d("adsfa", "1111111111");
            ArrayList arrayList = this.undonePaths;
            ArrayList arrayList2 = this.paths;
            arrayList.add(arrayList2.remove(arrayList2.size() - 1));
            invalidate();
        }
    }

    public void redo() {
        if (this.undonePaths.size() > 0) {
            Log.d("adsfa", "2222222222222");
            ArrayList arrayList = this.paths;
            ArrayList arrayList2 = this.undonePaths;
            arrayList.add(arrayList2.remove(arrayList2.size() - 1));
            invalidate();
        }
    }

    public void change_color(String str) {
        this.paint.setColor(Color.parseColor(str));
    }
}
