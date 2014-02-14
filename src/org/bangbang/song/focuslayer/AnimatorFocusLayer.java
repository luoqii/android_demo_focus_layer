
package org.bangbang.song.focuslayer;

import org.bangbang.song.demo.focuslayer.R;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.AnimatorSet.Builder;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * impl focus layer by {@link Animator}.
 * 
 * @author bysong@tudou.com
 */
public class AnimatorFocusLayer extends BaseAnimationFocusLayer implements AnimatorUpdateListener {
    private static final String TAG = AnimatorFocusLayer.class.getSimpleName();

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
    }

    @Override
    protected void updateFocusView(View focus) {
        super.updateFocusView(focus);

        doAnimation();
    }

    private void doAnimation() {
        // focus rect.
        float fromX = mLastScaledFocusRect.left;
        float toX = mCurrentScaledFocusRect.left;
        float fromY = mLastScaledFocusRect.top;
        float toY = mCurrentScaledFocusRect.top;
        if (DEBUG_TRANSFER_ANIMATION) {
            Log.d(TAG, "mFocusRectView::x-y fromX: " + fromX + " toX: " + toX + " fromY: " + fromY
                    + " toY: " + toY);
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
            Log.d(TAG, "mFocusRectView::w-h fromW: " + fromW + " toW: " + toW + " fromH: " + fromH
                    + " toH: " + toH);
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
                Log.d(TAG, "mLastFocusView::x-y fromX: " + fromX + " toX: " + toX + " fromY: "
                        + fromY + " toY: " + toY);
            }

            fromW = mLastScaledFocusRect.width();
            toW = mLastFocusRect.width();
            fromH = mLastScaledFocusRect.height();
            toH = mLastFocusRect.height();
            ValueAnimator animatorLastW = ObjectAnimator.ofFloat(mLastFocusView, "width", fromW,
                    toW);
            animatorLastW.setDuration(mDuration);
            animatorLastW.addUpdateListener(this);
            ObjectAnimator animatorLastH = ObjectAnimator.ofFloat(mLastFocusView, "height", fromH,
                    toH);
            animatorLastH.setDuration(mDuration);
            animatorLastH.addUpdateListener(this);
            if (DEBUG_SCALE_ANIMATION) {
                Log.d(TAG, "mLastFocusView::w-h fromW: " + fromW + " toW: " + toW + " fromH: "
                        + fromH + " toH: " + toH);
            }

            // current focus view
            fromX = mCurrentFocusRect.left;
            toX = mCurrentScaledFocusRect.left;
            fromY = mCurrentFocusRect.top;
            toY = mCurrentScaledFocusRect.top;
            ObjectAnimator animatorCurrentX = ObjectAnimator.ofFloat(mCurrentFocusView, "x", fromX,
                    toX);
            animatorCurrentX.setDuration(mDuration);
            ObjectAnimator animatorCurrentY = ObjectAnimator.ofFloat(mCurrentFocusView, "y", fromY,
                    toY);
            animatorCurrentY.setDuration(mDuration);
            if (DEBUG_SCALE_ANIMATION) {
                Log.d(TAG, "mCurrentFocusView::x-y fromX: " + fromX + " toX: " + toX + " fromY: "
                        + fromY + " toY: " + toY);
            }

            fromW = mCurrentFocusRect.width();
            toW = mCurrentScaledFocusRect.width();
            fromH = mCurrentFocusRect.height();
            toH = mCurrentScaledFocusRect.height();
            ValueAnimator animatorCurrentW = ObjectAnimator.ofFloat(mCurrentFocusView, "width",
                    fromW, toW);
            animatorCurrentW.setDuration(mDuration);
            animatorCurrentW.addUpdateListener(this);
            ObjectAnimator animatorCurrentH = ObjectAnimator.ofFloat(mCurrentFocusView, "height",
                    fromH, toH);
            animatorCurrentH.setDuration(mDuration);
            if (DEBUG_SCALE_ANIMATION) {
                Log.d(TAG, "mCurrentFocusView::w-h fromW: " + fromW + " toW: " + toW + " fromH: "
                        + fromH + " toH: " + toH);
            }

            animatorBuilder
                    .with(animatorLastX)
                    .with(animatorLastY)
                    .with(animatorLastW)
                    .with(animatorLastH)

                    .with(animatorCurrentX)
                    .with(animatorCurrentY)
                    .with(animatorCurrentW)
                    .with(animatorCurrentH);
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        // XXX why we need this.
        updateFocusLayoutparams();
    }

    private void updateFocusLayoutparams() {
        AbsoluteLayout.LayoutParams layoutParams = (LayoutParams) mFocusRectView.getLayoutParams();
        int w = mFocusRectView.getWidth();
        int h = mFocusRectView.getHeight();
        layoutParams.width = w;
        layoutParams.height = h;
        // Log.d(TAG, "updateFocusLayoutparams. x:" + layoutParams.x + " y:" +
        // layoutParams.y + " w:" + layoutParams.width + " h:" +
        // layoutParams.height);
        updateViewLayout(mFocusRectView, layoutParams);
    }

    @Override
    protected View onInflateScaleAnimationView(LayoutInflater layoutInflater) {
        View v = new FixedSizeView(getContext());
        return v;
    }

    @Override
    protected View onInflateTranslateAnimationView(LayoutInflater layoutInflater) {
        View v = new FixedSizeView(getContext());

        v.setBackgroundResource(R.drawable.search_button_hover);
        return v;
    }

    /**
     * fixed width & height.
     * <p>
     * to incorporate with {@link Animator}, add {@link #setWidth(float)} and
     * {@link #setHeight(float)} method.
     * <p>
     * after construct it, you should explicitly set width & height
     * 
     * @see #setWidth(float)
     * @see #setHeight(float)
     * @author bysong
     */
    public static class FixedSizeView extends View
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

            // must explicitly 1) request layout & 2)redraw
            requestLayout();
            invalidate();
        }

        public void setHeight(float h) {
            // Log.d(TAG, "setHeight(). h: " + h);
            mHeight = (int) h;

            // must explicitly 1) request layout & 2)redraw
            requestLayout();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // Log.d(TAG, "onDraw(). view: " + this);
        }

    }
}
