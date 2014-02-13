package org.bangbang.song.focuslayer;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.AnimatorSet.Builder;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageView.ScaleType;

/**
 * 
 * impl focus layer by {@link Animator}.
 * 
 * @author bysong@tudou.com
 */
public class AnimatorFocusLayer extends BaseAnimationFocusLayer implements AnimatorUpdateListener{
    private static final String TAG = AnimatorFocusLayer.class.getSimpleName();
    private static final boolean DEBUG_FOCUS_SESSION = true;
    private static final boolean DEBUG_TRANSFER_ANIMATION = true;
    private static final boolean DEBUG_SCALE_ANIMATION = true;

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
    protected void updateFocusView(View focus) {
        Log.d(TAG, "updateFocusView(). view: " + focus);
        super.updateFocusView(focus);
                
        if (!mDisableScaleAnimation && mCurrentFocusBitmap != null) {
            mCurrentFocusView.setBackgroundColor(Color.BLACK);
//            mCurrentFocusView.setBackgroundDrawable(new BitmapDrawable(mCurrentFocusBitmap));
            mCurrentFocusView.setScaleType(ScaleType.FIT_XY);
            mCurrentFocusView.setImageBitmap(mCurrentFocusBitmap);
        }
        if (!mDisableScaleAnimation && mLastFocusBitmap != null) {
            mLastFocusView.setBackgroundColor(Color.BLACK);
//            mLastFocusView.setBackgroundDrawable(new BitmapDrawable(mLastFocusBitmap));
            mLastFocusView.setImageBitmap(mLastFocusBitmap);
            mLastFocusView.setScaleType(ScaleType.FIT_XY);
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
        if (DEBUG_TRANSFER_ANIMATION) {
            Log.d(TAG, "mFocusRectView::x-y fromX: " + fromX + " toX: " + toX + " fromY: " + fromY + " toY: " + toY);
        }
        ObjectAnimator animatorRectX = ObjectAnimator.ofFloat(mFocusRectView, "x", fromX, toX);
        animatorRectX.setDuration(mDuration);
        ObjectAnimator animatorRectY = ObjectAnimator.ofFloat(mFocusRectView, "y", fromY, toY);
        animatorRectY.setDuration(mDuration);
        
        float fromW = mLastScaledFocusRect.width();
        float toW = mCurrentScaledFocusRect.width();
        float fromH = mLastScaledFocusRect.height();
        float toH = mCurrentScaledFocusRect.height();
        if (DEBUG_TRANSFER_ANIMATION) {
            Log.d(TAG, "mFocusRectView::w-h fromW: " + fromW + " toW: " + toW + " fromH: " + fromH + " toH: " + toH);
        }
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
        if (!mDisableScaleAnimation) {
            float fromX, toX;
            float fromY, toY;
            float fromW, toW;
            float fromH, toH;
            
            // last focus view
            fromX = mLastScaledFocusRect.left;
            toX = mLastFocusRect.left;
            fromY = mLastScaledFocusRect.top;
            toY = mLastFocusRect.top;
            ObjectAnimator animatorLastX = ObjectAnimator.ofFloat(mLastFocusView, "x", fromX, toX);
            animatorLastX.setDuration(mDuration);
            ObjectAnimator animatorLastY = ObjectAnimator.ofFloat(mLastFocusView, "y", fromY, toY);
            animatorLastY.setDuration(mDuration);
            if (DEBUG_SCALE_ANIMATION) {
                Log.d(TAG, "mLastFocusView::x-y fromX: " + fromX + " toX: " + toX + " fromY: " + fromY + " toY: " + toY);
            }

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
            if (DEBUG_SCALE_ANIMATION) {
                Log.d(TAG, "mLastFocusView::w-h fromW: " + fromW + " toW: " + toW + " fromH: " + fromH + " toH: " + toH);
            }
            
            // current focus view
            fromX = mCurrentFocusRect.left;
            toX = mCurrentScaledFocusRect.left;
            fromY = mCurrentFocusRect.top;
            toY = mCurrentScaledFocusRect.top;
            ObjectAnimator animatorCurrentX = ObjectAnimator.ofFloat(mCurrentFocusView, "x", fromX, toX);
            animatorCurrentX.setDuration(mDuration);
            ObjectAnimator animatorCurrentY = ObjectAnimator.ofFloat(mCurrentFocusView, "y", fromY, toY);
            animatorCurrentY.setDuration(mDuration);
            if (DEBUG_SCALE_ANIMATION) {
                Log.d(TAG, "mCurrentFocusView::x-y fromX: " + fromX + " toX: " + toX + " fromY: " + fromY + " toY: " + toY);
            }

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
            if (DEBUG_SCALE_ANIMATION) {
                Log.d(TAG, "mCurrentFocusView::w-h fromW: " + fromW + " toW: " + toW + " fromH: " + fromH + " toH: " + toH);
            }

            animatorBuilder
            .with(animatorLastX)
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

