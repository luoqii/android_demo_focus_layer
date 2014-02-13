
package org.bangbang.song.focuslayer;

import org.bangbang.song.android.commonlib.Grid;
import org.bangbang.song.android.commonlib.Grid.GridDrawer;
import org.bangbang.song.android.commonlib.ReflectUtil;
import org.bangbang.song.demo.focuslayer.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;

/**
 * the animated bitmap MUST not has alpha channel.
 * 
 * @author bysong
 *
 */
public class BaseAnimationFocusLayer extends 
AbsoluteLayout // yes we need this layout absolutely.
implements IFocusAnimationLayer {
    private static final String TAG = BaseAnimationFocusLayer.class.getSimpleName();

    protected static final boolean DEBUG = true;
    protected static final boolean DRAW_GRIG = false && DEBUG;
    protected static final int OFFSET_X = 0;
    protected static final int OFFSET_Y = 0;
    private static final int DEFAULT_ANIMATION_DURATION = 2222;
    private static final float DEFAULT_SCALE_FACOTR = 1.3f;

    private Grid.GridDrawer mGridDrawer;

    protected Rect mTmpRect = new Rect();

    protected Rect mLastFocusRect = new Rect();
    protected Rect mLastScaledFocusRect = new Rect();
    protected Bitmap mLastFocusBitmap = null;

    protected Rect mCurrentFocusRect = new Rect();
    protected Rect mCurrentScaledFocusRect = new Rect();
    protected Bitmap mCurrentFocusBitmap = null;

    private Matrix mMatrix;
    private RectF mTmpRectF;
    

    /** used for transfer */
    protected FixedSizeView mFocusRectView;

    /** in millisec */
    protected int mDuration;
    protected float mScaleFactor;
    protected boolean mDisableScaleAnimation;

    public BaseAnimationFocusLayer(Context context) {
        this(context, null);
    }

    public BaseAnimationFocusLayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseAnimationFocusLayer(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);

        mDuration = DEFAULT_ANIMATION_DURATION;
        mScaleFactor = DEFAULT_SCALE_FACOTR;
        mDisableScaleAnimation = true;

        init();
    }

    private void init() {
        mMatrix = new Matrix();
        mFocusRectView = onInflateFocusRectView(((LayoutInflater) getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE)));
        addView(mFocusRectView);

        setId(Utils.FOCUS_LAYER_ID);

        setBackgroundColor(Color.TRANSPARENT);
        if (DRAW_GRIG) {
            setWillNotDraw(false);
            mGridDrawer = new GridDrawer(100, 100, 10, 10);
        }
        
        setWillNotDraw(false);
    }
    

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public int getDration() {
        return mDuration;
    }

    public void setScaleFactor(float factor) {
        mScaleFactor = factor;
    }

    public float getScaleFactor() {
        return mScaleFactor;
    }

    /**
     * inflate focus view, but NOT add to view hierarchy, caller will do this
     * for u.
     * 
     * @param layoutInflater
     * @return
     */
    protected BaseAnimationFocusLayer.FixedSizeView onInflateFocusRectView(
            LayoutInflater layoutInflater) {
        FixedSizeView v = new FixedSizeView(getContext());
        v.setBackgroundResource(R.drawable.search_button_hover);

        return v;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (DRAW_GRIG) {
            int w = getWidth();
            int h = getHeight();
            mGridDrawer.onDraw(canvas, w, h);
        }
    }

    protected void offsetAnyViewRectToMyCoord(View view, Rect rect) {
        View root = view.getRootView();
        if (root instanceof ViewGroup) {
            ((ViewGroup) root).offsetDescendantRectToMyCoords(view, rect);
            ((ViewGroup) root).offsetRectIntoDescendantCoords(this, rect);
        } else {
            Log.e(TAG, "view's parent is NOT a ViewGroup.");
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            updateFocusView(v);
        }
    }

    protected void updateFocusView(View focus) {
        if (null == focus) {
            return;
        }
        
        focus.getFocusedRect(mTmpRect);
        // focus.getDrawingRect(mTmpRect);
        offsetAnyViewRectToMyCoord(focus, mTmpRect);
        if (DEBUG) {
            Log.d(TAG, "focusRect: " + mTmpRect);
        }

        mLastFocusRect = new Rect(mCurrentFocusRect);
        mCurrentFocusRect = new Rect(mTmpRect);
        
        int width = focus.getWidth();
        int height = focus.getHeight();
        if (width <= 0 || height <= 0) {
            Log.w(TAG, "invalid w or h. w: " + width + " h: " + height);
            return;
        }
        
        if (!mDisableScaleAnimation) {
            mMatrix.reset();
            // adjust rect by scale factor.
            mTmpRectF = new RectF(mCurrentFocusRect.left, mCurrentFocusRect.top,
                    mCurrentFocusRect.right, mCurrentFocusRect.bottom);
            mMatrix.setScale(mScaleFactor, mScaleFactor, mTmpRectF.centerX(), mTmpRectF.centerY());
            mMatrix.mapRect(mTmpRectF);
            mCurrentScaledFocusRect = new Rect((int)mTmpRectF.left, (int)mTmpRectF.top,
                    (int)mTmpRectF.right, (int)mTmpRectF.bottom);

            mMatrix.reset();
            mTmpRectF = new RectF(mLastFocusRect.left, mLastFocusRect.top,
                    mLastFocusRect.right, mLastFocusRect.bottom);
            mMatrix.setScale(mScaleFactor, mScaleFactor, mTmpRectF.centerX(),
                    mTmpRectF.centerY());
            mMatrix.mapRect(mTmpRectF);
            mLastScaledFocusRect = new Rect((int) mTmpRectF.left,
                    (int) mTmpRectF.top, (int) mTmpRectF.right,
                    (int) mTmpRectF.bottom);
        } else {
            mLastScaledFocusRect = new Rect(mLastFocusRect);
            mCurrentScaledFocusRect = new Rect(mCurrentFocusRect);
        }

        Log.d(TAG, "mLastScaledFocusRect: " + mLastScaledFocusRect);
        Log.d(TAG, "mCurrentScaledFocusRect: " + mCurrentScaledFocusRect);

        if (!mDisableScaleAnimation) {       
            mLastFocusBitmap = mCurrentFocusBitmap;
            mCurrentFocusBitmap = getBitmap(focus);;
        }

        Log.d(TAG, "mCurrentFocusRect: " + mCurrentFocusRect + " mLastFocusRect: " + mLastFocusRect);
    }
    
    private Bitmap getBitmap(View focus) {
        int oldWidth = focus.getWidth();
        int oldHeight = focus.getHeight();
        
        Bitmap bitmap = Bitmap.createBitmap(oldWidth, oldHeight, Bitmap.Config.ARGB_8888);
        focus.draw(new Canvas(bitmap));
//        bitmap = Bitmap.createScaledBitmap(bitmap, mCurrentScaledFocusRect.width(), mCurrentScaledFocusRect.height(), false);
        
//        return bitmap;
        return getBitmapX(focus);
    }

    private Bitmap getBitmapX(View focus) {
        int oldWidth = focus.getWidth();
        int oldHeight = focus.getHeight();
        int oldLeft = focus.getLeft();
        int oldTop = focus.getTop();
        int oldWSpec = ReflectUtil.getIntFieldValue(View.class, focus, "mOldWidthMeasureSpec");
        int oldHSpec = ReflectUtil.getIntFieldValue(View.class, focus, "mOldHeightMeasureSpec");
        
        int w = (int) ((float)oldWidth * ( 1.0 + 2.0 * ( 1 - mScaleFactor)));
        int h = (int) ((float)oldHeight * ( 1.0 + 2.0 * ( 1 - mScaleFactor)));
        w = mCurrentScaledFocusRect.width();
        h = mCurrentScaledFocusRect.height();
        if (w == 0 || h == 0) {
            return null;
        }
//        w = 50;
//        h = 50;
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//        DisplayMetrics outMetrics  = new DisplayMetrics();
//        getDisplay().getMetrics(outMetrics);
//        int density = outMetrics.densityDpi;
//        bitmap.setDensity(density);
        int newWSpec = MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY);
        int newHSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
        focus.measure(newWSpec, newHSpec);
        focus.layout(0, 0, w, h);
        focus.draw(new Canvas(bitmap));
        
        // restore previous state.
        focus.measure(oldWSpec, oldHSpec);
        focus.layout(oldLeft, oldTop, oldLeft + oldWidth, oldTop + oldHeight);
        
        Log.d(TAG, "bitmap w: " + bitmap.getWidth() + " h: " + bitmap.getHeight());
        return bitmap;
    }

    private void deubgFocusRect() {
        int width = mFocusRectView.getWidth();
        int height = mFocusRectView.getHeight();
        int left = mFocusRectView.getLeft();
        int top = mFocusRectView.getTop();
        Log.d(TAG, "mFocusRectView left: " + left + " top: " + top + " width: " + width
                + " height: " + height);
    }

    /**
     * 
     * fixed width & height.
     * <p>
     * after construct it, you must explicitly set width & height
     * 
     * @see #setWidth(float)
     * @see #setHeight(float)
     * @author bysong
     *
     */
    public static class FixedSizeView extends 
//    View ?
            ImageView
    {

        private int mWidth;
        private int mHeight;

        public FixedSizeView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);

            init();
        }

        public FixedSizeView(Context context, AttributeSet attrs) {
            super(context, attrs);

            init();
        }

        public FixedSizeView(Context context) {
            super(context);

            init();
        }

        void init() {
            mWidth = 0;
            mHeight = 0;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            int measuredWidth = MeasureSpec.makeMeasureSpec(MeasureSpec.EXACTLY, mWidth);
            int measuredHeight = MeasureSpec.makeMeasureSpec(MeasureSpec.EXACTLY, mHeight);

            // Log.d(TAG, "measuredWidth: " +
            // MeasureSpec.toString(measuredWidth) + " measuredHeight: " +
            // MeasureSpec.toString(measuredHeight));
            setMeasuredDimension(measuredWidth, measuredHeight);
        }

        public void setWidth(float w) {
            // Log.d(TAG, "setWidth(). w: " + w);
            mWidth = (int) w;

            // updateFocusLayoutparams();
            invalidate();
        }

        public void setHeight(float h) {
            // Log.d(TAG, "setHeight(). h: " + h);
            mHeight = (int) h;

            // updateFocusLayoutparams();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // Log.d(TAG, "onDraw(). view: " + this);
        }

    }
}
