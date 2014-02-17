package org.bangbang.song.focuslayer;

import org.bangbang.song.android.commonlib.FPSLoger;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

public class FastFocusLayer extends SurfaceView implements IFocusAnimationLayer, Callback {
    private static final String TAG = FastFocusLayer.class.getSimpleName();
    private WorkThread mWorker;
    private SurfaceHolder mHolder;

    public FastFocusLayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        init();
    }

    public FastFocusLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        init();
    }

    public FastFocusLayer(Context context) {
        super(context);
        
        init();
    }
    
    void init() {
        mHolder = getHolder();
        // for show what we draw.
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);
        
        getHolder().addCallback(this);
        
        mWorker = new WorkThread();
        mWorker.start();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
        mWorker.startOrPause(false);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged");
        mWorker.startOrPause(false);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
        mWorker.startOrPause(true);
    }

    class WorkThread extends Thread {
        private static final boolean DEBUG_FPS = true;
        private boolean mPaused = false;
        private Canvas mCanvas = null;
        private Paint mPaint;
        private FPSLoger mPps;

        public WorkThread() {
            super(TAG + ":workthread");
            mPaint = new Paint();
            mPaint.setColor(Color.RED);
            mPaint.setTextSize(20);
            Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
            mPaint.setTypeface(font);
            mPps = new FPSLoger(TAG);
        }
        
        public void startOrPause(boolean pause) {
            mPaused = pause;
        }
        
        public void onDraw(Canvas canvas) {
            if (DEBUG_FPS) {
                mPps.onDraw();
            }
//            Log.d(TAG, "onDraw()");
            canvas.drawColor(Color.TRANSPARENT);
            mCanvas.drawText("hello", 100, 100, mPaint);
        }
        
        @Override
        public void run() {
            super.run();
            
            while (true) {
                if (!mPaused) {
                    try {
                        mCanvas = mHolder.lockCanvas();
                        if (null != mCanvas) {
                            onDraw(mCanvas);
                        }
                    } finally {
                       if (null != mCanvas) {
                           mHolder.unlockCanvasAndPost(mCanvas);
                       }
                    }
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
