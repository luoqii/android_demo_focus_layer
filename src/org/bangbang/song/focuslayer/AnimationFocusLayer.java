
package org.bangbang.song.focuslayer;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;

/**
 * 
 * impl by {@link Animation}.
 * @author bysong
 *
 */
public class AnimationFocusLayer extends BaseAnimationFocusLayer {
    protected static final String TAG = AnimationFocusLayer.class.getSimpleName();

    public AnimationFocusLayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    public AnimationFocusLayer(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public AnimationFocusLayer(Context context) {
        super(context);

        init();
    }

    private void init() {
//        mFocusRectView.setBackgroundColor(Color.RED);
    }

    @Override
    public void updateFocusView(View focus) {
        super.updateFocusView(focus);

        doAnimation();
    }

    private void doAnimation() {
        animateTranslateView();
        
        animateLastFocusView();
        animateCurrentFocusView();
    }

    private void animateTranslateView() {
        LayoutParams params = null;

        int width = mCurrentScaledFocusRect.width();
        int height = mCurrentScaledFocusRect.height();
        int x = mCurrentScaledFocusRect.left + OFFSET_X;
        int y = mCurrentScaledFocusRect.top + OFFSET_Y;
        if (DEBUG_TRANSFER_ANIMATION) {
            Log.d(TAG, "new layout params x: " + x + " y: " + y + " width: " + width + " height: "
                    + height);
        }
        params = new AbsoluteLayout.LayoutParams(width, height, x, y);
        updateViewLayout(mFocusRectView, params);
        
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setInterpolator(new LinearInterpolator());

        // XXX this do not work. bysong@tudou.com
        animationSet.setDuration(mDuration);

        float fromX = (float) mLastScaledFocusRect.width() / mCurrentScaledFocusRect.width();
        float toX = 1;
        float fromY = (float) mLastScaledFocusRect.height() / mCurrentScaledFocusRect.height();
        float toY = 1;
        float pivotX = mCurrentScaledFocusRect.exactCenterX();
        float pivotY = mCurrentScaledFocusRect.exactCenterY();
        pivotX = (float) mFocusRectView.getWidth() / 2;
        pivotY = (float) mFocusRectView.getHeight() / 2;
        pivotX = (float) mCurrentScaledFocusRect.width() / 2;
        pivotY = (float) mCurrentScaledFocusRect.height() / 2;
        
        pivotX = (float) 0.5;
        pivotY = (float) 0.5;
        if (DEBUG_SCALE_ANIMATION) {
            Log.d(TAG, "scale fromX: " + fromX + " toX: " + toX + " fromY: " + fromY + " toY: "
                    + toY + " pivotX: " + pivotX + " pivotY: " + pivotY);
        }
        ScaleAnimation s = new ScaleAnimation(fromX, toX, fromY, toY, Animation.RELATIVE_TO_SELF,
                pivotX, Animation.RELATIVE_TO_SELF, pivotY);
        s.setDuration(mDuration);
        s.setInterpolator(new LinearInterpolator());
        
        float fromXDelta = mLastScaledFocusRect.centerX() - mCurrentScaledFocusRect.centerX();
        float toXDelta = 0;
        float fromYDelta = mLastScaledFocusRect.centerY() - mCurrentScaledFocusRect.centerY();
        float toYDelta = 0;
        if (DEBUG_TRANSFER_ANIMATION) {
            Log.d(TAG, "translate fromXDelta: " + fromXDelta + " toXDelta: " + toXDelta
                    + " fromYDelta: " + fromYDelta + " toYDelta: " + toYDelta);
        }
        TranslateAnimation t = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        t.setDuration(mDuration);
        t.setInterpolator(new LinearInterpolator());
        
        // order is important, scale firstly, then translate.
        animationSet.addAnimation(s);
        animationSet.addAnimation(t);
        
        mFocusRectView.startAnimation(animationSet);
    }

    private void animateLastFocusView() {
        Rect r = mLastFocusRect;
        LayoutParams params = null;

        int width = r.width();
        int height = r.height();
        int x = r.left + OFFSET_X;
        int y = r.top + OFFSET_Y;
        if (DEBUG_TRANSFER_ANIMATION) {
            Log.d(TAG, "new layout params x: " + x + " y: " + y + " width: " + width + " height: "
                    + height);
        }
        params = new AbsoluteLayout.LayoutParams(width, height, x, y);
        updateViewLayout(mLastFocusView, params);
        
        float fromX = (float) mLastScaledFocusRect.width() / mLastFocusRect.width();
        float toX = 1;
        float fromY = (float) mLastScaledFocusRect.height() / mLastFocusRect.height();
        float toY = 1;
        float pivotX = .5f;
        float pivotY = .5f;
        if (DEBUG_SCALE_ANIMATION) {
            Log.d(TAG, "scale fromX: " + fromX + " toX: " + toX + " fromY: " + fromY + " toY: "
                    + toY + " pivotX: " + pivotX + " pivotY: " + pivotY);
        }
        ScaleAnimation s = new ScaleAnimation(fromX, toX, fromY, toY, Animation.RELATIVE_TO_SELF,
                pivotX, Animation.RELATIVE_TO_SELF, pivotY);
        s.setDuration(mDuration);
        s.setInterpolator(new LinearInterpolator());
        mLastFocusView.startAnimation(s);
    }

    private void animateCurrentFocusView() {
        Rect r = mCurrentScaledFocusRect;
        LayoutParams params = null;

        int width = r.width();
        int height = r.height();
        int x = r.left + OFFSET_X;
        int y = r.top + OFFSET_Y;
        if (DEBUG_TRANSFER_ANIMATION) {
            Log.d(TAG, "new layout params x: " + x + " y: " + y + " width: " + width + " height: "
                    + height);
        }
        params = new AbsoluteLayout.LayoutParams(width, height, x, y);
        updateViewLayout(mCurrentFocusView, params);
        
        float fromX = (float) mCurrentFocusRect.width() / mCurrentScaledFocusRect.width();
        float toX = 1;
        float fromY = (float) mCurrentFocusRect.height() / mCurrentScaledFocusRect.height();
        float toY = 1;
        float pivotX = mCurrentScaledFocusRect.exactCenterX();
        float pivotY = mCurrentScaledFocusRect.exactCenterY();
        pivotX = (float) mFocusRectView.getWidth() / 2;
        pivotY = (float) mFocusRectView.getHeight() / 2;
        pivotX = (float) mCurrentScaledFocusRect.width() / 2;
        pivotY = (float) mCurrentScaledFocusRect.height() / 2;
        
        pivotX = (float) 0.5;
        pivotY = (float) 0.5;
        if (DEBUG_SCALE_ANIMATION) {
            Log.d(TAG, "scale fromX: " + fromX + " toX: " + toX + " fromY: " + fromY + " toY: "
                    + toY + " pivotX: " + pivotX + " pivotY: " + pivotY);
        }
        ScaleAnimation s = new ScaleAnimation(fromX, toX, fromY, toY, Animation.RELATIVE_TO_SELF,
                pivotX, Animation.RELATIVE_TO_SELF, pivotY);
        s.setDuration(mDuration);
        s.setInterpolator(new LinearInterpolator());
        mCurrentFocusView.startAnimation(s);
    }    
}
