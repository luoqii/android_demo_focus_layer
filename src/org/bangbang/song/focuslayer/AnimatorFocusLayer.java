package org.bangbang.song.focuslayer;

import org.bangbang.song.demo.focuslayer.R;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.AnimatorSet.Builder;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsoluteLayout;

/**
 * 
 * impl focus layer by {@link Animator}.
 * 
 * @author bysong@tudou.com
 */
public class AnimatorFocusLayer extends BaseAnimationFocusLayer implements AnimatorUpdateListener{
    private static final String TAG = AnimatorFocusLayer.class.getSimpleName();
    private static final boolean DEBUG_FOCUS_SESSION = true;

    private Matrix mMatrix;
    private RectF mTmpRectF;
    
    private FixedSizeView mLastFocusView;
    private FixedSizeView mCurrentFocusView;

    private boolean mFirstFocus;
    
    public AnimatorFocusLayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        init();
    }

    public AnimatorFocusLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        init();
    }

    public AnimatorFocusLayer(Context context) {
        super(context);
        
        init();
    }

    private void init() {   
        mMatrix = new Matrix();
        
        mLastFocusView = new FixedSizeView(getContext());
        mLastFocusView.setWidth(0);
        mLastFocusView.setHeight(0);
        mCurrentFocusView  = new FixedSizeView(getContext());
        mCurrentFocusView.setWidth(0);
        mCurrentFocusView.setHeight(0);
        addView(mLastFocusView);
        addView(mCurrentFocusView);
    }
        
    @Override
    public void onViewFocusd(View focus) {
        super.onViewFocusd(focus);
        
        Log.d(TAG, "onViewFocusd(). view: " + focus);
        
        if (!mDisableScale) {
            mMatrix.reset();
            // adjust rect by scale factor.
            mTmpRectF = new RectF(mCurrentFocusRect.left, mCurrentFocusRect.top, mCurrentFocusRect.right, mCurrentFocusRect.bottom);
            mMatrix.setScale(mScaleFactor, mScaleFactor, mTmpRectF.centerX(), mTmpRectF.centerY());
            mMatrix.mapRect(mTmpRectF);
            mCurrentScaledFocusRect = new Rect((int)mTmpRectF.left, (int)mTmpRectF.top, (int)mTmpRectF.right, (int)mTmpRectF.bottom);

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
        
        if (!mDisableScale && mCurrentFocusBitmap != null) {
            mCurrentFocusView.setBackgroundDrawable(new BitmapDrawable(mCurrentFocusBitmap));
        }   
        if (!mDisableScale && mLastFocusBitmap != null) {
            mLastFocusView.setBackgroundDrawable(new BitmapDrawable(mLastFocusBitmap));
        }
        
        doAnimation();
    }

    private void initFocusTarget() {
        // no animation in firstly focus on.
        mLastFocusRect = new Rect(mCurrentFocusRect);
        mLastScaledFocusRect = new Rect(mCurrentScaledFocusRect);

        Log.d(TAG, "first focus: mLastFocusRect: " + mLastFocusRect);
        Log.d(TAG, "first focus: mCurrentFocusRect: " + mCurrentFocusRect);
        Log.d(TAG, "first focus: mLastScaledFocusRect: " + mLastScaledFocusRect);
        Log.d(TAG, "first focus: mCurrentScaledFocusRect: " + mCurrentScaledFocusRect);
        
        int width = mCurrentScaledFocusRect.width();
        int height = mCurrentScaledFocusRect.height();
        int x = mCurrentScaledFocusRect.left;
        int y = mCurrentScaledFocusRect.top;
        Log.d(TAG, "new w: " + width + " h: " + height + " x: " + x + " y: " + y);
        
        mFocusRectView.setVisibility(VISIBLE);
        mFocusRectView.setWidth(width);
        mFocusRectView.setHeight(height);
        updateViewLayout(mFocusRectView, new AbsoluteLayout.LayoutParams(
                width,
                height,
                x,
                y
                ));
//      updateViewLayout(mCurrentFocusView, new AbsoluteLayout.LayoutParams(
//              width,
//              height,
//              x,
//              y
//              ));
//      updateViewLayout(mLastFocusView, new AbsoluteLayout.LayoutParams(
//              width, 
//              height, 
//              x,
//              y
//              ));
    }
    

    private void doAnimation() {    
        // focus rect.
        float fromX = mLastScaledFocusRect.left;
        float toX = mCurrentScaledFocusRect.left;
        float fromY = mLastScaledFocusRect.top;
        float toY = mCurrentScaledFocusRect.top;    
//      Log.d(TAG, "x-y fromX: " + fromX + " toX: " + toX + " fromY: " + fromY + " toY: " + toY);
        ObjectAnimator animatorRectX = ObjectAnimator.ofFloat(mFocusRectView, "x", fromX, toX);
        animatorRectX.setDuration(mDuration);
        ObjectAnimator animatorRectY = ObjectAnimator.ofFloat(mFocusRectView, "y", fromY, toY);
        animatorRectY.setDuration(mDuration);
        
        float fromW = mLastScaledFocusRect.width();
        float toW = mCurrentScaledFocusRect.width();
        float fromH = mLastScaledFocusRect.height();
        float toH = mCurrentScaledFocusRect.height();
//      Log.d(TAG, "w-h fromW: " + fromW + " toW: " + toW + " fromH: " + fromH + " toH: " + toH);
        ValueAnimator animatorRectW = ObjectAnimator.ofFloat(mFocusRectView, "width", fromW, toW);
        animatorRectW.setDuration(mDuration);
        animatorRectW.addUpdateListener(this);
        ObjectAnimator animatorRectH = ObjectAnimator.ofFloat(mFocusRectView, "height", fromH, toH);
        animatorRectH.setDuration(mDuration);
        animatorRectH.addUpdateListener(this);
        
        AnimatorSet set = new AnimatorSet();        
        // XXX do NOT work. bysong@tudou.com
        set.setDuration(mDuration);
        Builder animatorBuilder = set
        .play(animatorRectW)
        .with(animatorRectH)
        .with(animatorRectX)
        .with(animatorRectY);
        
        doScaleAnimation(animatorBuilder);
        
        set.start();
    }

    private void doScaleAnimation(Builder animatorBuilder) {
        float fromX;
        float toX;
        float fromY;
        float toY;
        float fromW;
        float toW;
        float fromH;
        float toH;
        
        if (!mDisableScale) {
            // last focus view
            fromX = mLastScaledFocusRect.left;
            toX = mLastFocusRect.left;
            fromY = mLastScaledFocusRect.top;
            toY = mLastFocusRect.top;
            ObjectAnimator animatorLastX = ObjectAnimator.ofFloat(mLastFocusView, "x", fromX, toX);
            animatorLastX.setDuration(mDuration);
            ObjectAnimator animatorLastY = ObjectAnimator.ofFloat(mLastFocusView, "y", fromY, toY);
            animatorLastY.setDuration(mDuration);

            fromW = mLastScaledFocusRect.width();
            toW = mLastFocusRect.width();
            fromH = mLastScaledFocusRect.height();
            toH = mLastFocusRect.height();
            ValueAnimator animatorLastW = ObjectAnimator.ofFloat(mLastFocusView, "width", fromW, toW);
            animatorLastW.setDuration(mDuration);
            animatorLastW.addUpdateListener(this);
            ObjectAnimator animatorLastH = ObjectAnimator.ofFloat(mLastFocusView, "height", fromH, toH);
            animatorLastH.setDuration(mDuration);
            animatorLastH.addUpdateListener(this);

            // current focus view
            fromX = mCurrentFocusRect.left;
            toX = mCurrentScaledFocusRect.left;
            fromY = mCurrentFocusRect.top;
            toY = mCurrentScaledFocusRect.top;
            ObjectAnimator animatorCurrentX = ObjectAnimator.ofFloat(mCurrentFocusView, "x", fromX, toX);
            animatorCurrentX.setDuration(mDuration);
            ObjectAnimator animatorCurrentY = ObjectAnimator.ofFloat(mCurrentFocusView, "y", fromY, toY);
            animatorCurrentY.setDuration(mDuration);

            fromW = mCurrentFocusRect.width();
            toW = mCurrentScaledFocusRect.width();
            fromH = mCurrentFocusRect.height();
            toH = mCurrentScaledFocusRect.height();
            ValueAnimator animatorCurrentW = ObjectAnimator.ofFloat(mCurrentFocusView, "width", fromW, toW);
            animatorCurrentW.setDuration(mDuration);
            animatorCurrentW.addUpdateListener(this);
            ObjectAnimator animatorCurrentH = ObjectAnimator.ofFloat(mCurrentFocusView, "height", fromH, toH);
            animatorCurrentH.setDuration(mDuration);
            animatorCurrentH.addUpdateListener(this);

            animatorBuilder.with(animatorLastX)
            .with(animatorLastY)
            .with(animatorLastW)
            .with(animatorLastH)

            .with(animatorCurrentX)
            .with(animatorCurrentY)
            .with(animatorCurrentW)
            .with(animatorCurrentH)
            ;   
        }
    }
    
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        updateFocusLayoutparams();
    }

    public void updateFocusLayoutparams() {
        AbsoluteLayout.LayoutParams layoutParams = (LayoutParams) mFocusRectView.getLayoutParams();
        int w = mFocusRectView.getWidth();
        int h = mFocusRectView.getHeight();
        layoutParams.width = w;
        layoutParams.height = h;
//      Log.d(TAG, "updateFocusLayoutparams. x:" + layoutParams.x + " y:" + layoutParams.y + " w:" + layoutParams.width + " h:" + layoutParams.height);
        updateViewLayout(mFocusRectView, layoutParams);
    }

}

