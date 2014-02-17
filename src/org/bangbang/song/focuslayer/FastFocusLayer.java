
package org.bangbang.song.focuslayer;

import org.bangbang.song.android.commonlib.FPSLoger;
import org.bangbang.song.demo.focuslayer.R;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

/**
 * @author bysong
 */
public class FastFocusLayer extends SurfaceView implements IFocusAnimationLayer, Callback {
    private static final String TAG = FastFocusLayer.class.getSimpleName();
    private static final boolean DEBUG = true;

    private WorkThread mWorker;
    private Handler mHandler;

    private SurfaceHolder mHolder;

    private AnimationConfigure mConfigure;
    private FPSLoger mFps;
    private Paint mPaint;
    private Canvas mCanvas = null;

    private boolean mAnimationEnd;
    private ValueAnimator mAnimation;
    private AnimatorableRect mTransalteRect;
    private Rect mTmp;
    private boolean mRequestAnimation;
    private Drawable mTranslateDrawable;

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
        setId(Utils.FOCUS_LAYER_ID);
        mConfigure = new AnimationConfigure();

        mHolder = getHolder();
        // for show what we draw on content.
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);

        getHolder().addCallback(this);

        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(20);
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        mPaint.setTypeface(font);
        mFps = new FPSLoger(TAG);

        mTransalteRect = new AnimatorableRect();
        mTranslateDrawable = getResources().getDrawable(R.drawable.search_button_hover);
        mWorker = new WorkThread();
        mWorker.start();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        mConfigure.onNewFocus(this, v);

        mAnimationEnd = false;
        mRequestAnimation = true;
        requestDrawFrame();
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

    @Override
    protected void onAttachedToWindow() {
        // TODO Auto-generated method stub
        super.onAttachedToWindow();
        // mWorker.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        // TODO Auto-generated method stub
        super.onDetachedFromWindow();
    }

    private void requestDrawFrame() {
        if (null != mHandler) {
            mHandler.removeMessages(0);
            mHandler.sendEmptyMessage(0);
        } else {
            Log.w(TAG, "mHandler is null!!! ignore.");
        }
    }

    /**
     * all states are ready.
     * 
     * @param canvas
     */
    public void onDrawFrame(Canvas canvas) {
        if (mConfigure.TRACK_FPS) {
            mFps.onDraw();
        }
        Log.d(TAG, "onDrawFrame()");
        canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
        canvas.drawColor(Color.TRANSPARENT);

        mTmp = new Rect(mTransalteRect.left, mTransalteRect.top, mTransalteRect.right,
                mTransalteRect.bottom);
        Log.d(TAG, "drawRect. rect: " + mTmp);
        mTranslateDrawable.setBounds(mTmp);
        mTranslateDrawable.draw(canvas);

        if (mConfigure.mLastFocusBitmap == null) {

        }

        mCanvas.drawText("hello", 100, 100, mPaint);
    }

    public class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            try {
                mCanvas = mHolder.lockCanvas();
                if (null != mCanvas) {
                    // if (null != mAnimation) {
                    // Log.d(TAG, "running: " + mAnimation.isRunning() +
                    // " started: " + mAnimation.isStarted());
                    // }
                    Log.d(TAG, "mAnimationEnd: " + mAnimationEnd);
                    if (mRequestAnimation) {
                        mRequestAnimation = false;
                        startAnimation();
                    } else if (mAnimation.isRunning()) {
                    }

                    onDrawFrame(mCanvas);
                }
            } finally {
                if (null != mCanvas) {
                    mHolder.unlockCanvasAndPost(mCanvas);
                }
            }
        }

        private void startAnimation() {
            mAnimation = ValueAnimator.ofObject(new AnimatorableEvaluator(),
                    new AnimatorableRect[] {
                            AnimatorableRect.fromRect(mConfigure.mLastScaledFocusRect),
                            AnimatorableRect.fromRect(mConfigure.mCurrentScaledFocusRect)
                    });
            mAnimation.addUpdateListener(new AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mTransalteRect = (AnimatorableRect) animation.getAnimatedValue();
                    // Log.d(TAG, "onAnimationUpdate. rect: " + mTransalteRect);
                    requestDrawFrame();
                }
            });
            mAnimation.addListener(new AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    Log.d(TAG, "onAnimationEnd");
                    mAnimationEnd = true;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }
            });
            mAnimation.setRepeatCount(0);
            mAnimation.setDuration(mConfigure.mDuration);
            mAnimation.start();
            Log.d(TAG, "start animtion");
        }
    }

    class WorkThread extends Thread {
        private boolean mPaused = false;

        public WorkThread() {
            super(TAG + ":workthread");
        }

        public void startOrPause(boolean pause) {
            mPaused = pause;
        }

        @Override
        public void run() {
            super.run();

            Looper.prepare();
            mHandler = new MyHandler();
            Looper.loop();

        }
    }

    public static class AnimatorableRect {
        public int left, top;
        public int right, bottom;

        public AnimatorableRect() {
            this(0, 0, 0, 0);
        }

        public AnimatorableRect(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        public static AnimatorableRect fromRect(Rect r) {
            AnimatorableRect rect = new AnimatorableRect(r.left, r.top, r.right, r.bottom);
            return rect;
        }

        public int getLeft() {
            return left;
        }

        public void setLeft(int left) {
            this.left = left;
        }

        public int getTop() {
            return top;
        }

        public void setTop(int top) {
            this.top = top;
        }

        public int getRight() {
            return right;
        }

        public void setRight(int right) {
            this.right = right;
        }

        public int getBottom() {
            return bottom;
        }

        public void setBottom(int bottom) {
            this.bottom = bottom;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(32);
            sb.append("MyRect(");
            sb.append(left);
            sb.append(", ");
            sb.append(top);
            sb.append(" - ");
            sb.append(right);
            sb.append(", ");
            sb.append(bottom);
            sb.append(")");
            return sb.toString();
        }
    }

    public static class AnimatorableEvaluator implements TypeEvaluator<AnimatorableRect> {

        @Override
        public AnimatorableRect evaluate(float fraction, AnimatorableRect startValue,
                AnimatorableRect endValue) {
            return new AnimatorableRect(startValue.left
                    + (int) ((endValue.left - startValue.left) * fraction),
                    startValue.top + (int) ((endValue.top - startValue.top) * fraction),
                    startValue.right + (int) ((endValue.right - startValue.right) * fraction),
                    startValue.bottom + (int) ((endValue.bottom - startValue.bottom) * fraction));
        }
    }
}
