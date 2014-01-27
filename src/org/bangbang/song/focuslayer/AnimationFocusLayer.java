package org.bangbang.song.focuslayer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;

public class AnimationFocusLayer extends BaseAnimationFocusLayer{
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
    }
        
    @Override
    public void onViewFocusd(View focus) {
        super.onViewFocusd(focus);
                
        doAnimation();
    }

    private void doAnimation() {
//      removeAllViews();
        updateAnimatedViews();      
        
        animateOldBitmap();
        animateNewView();
        animateRect();      
        
        invalidate();
    }

    private void updateAnimatedViews() {
        LayoutParams params = null;
        
        int width = mCurrentFocusRect.width();
        int height = mCurrentFocusRect.height();
        int x = mCurrentFocusRect.left + OFFSET_X;
        int y = mCurrentFocusRect.top + OFFSET_Y;
        Log.d(TAG, "new layout params x: " + x + " y: " + y + " width: " + width + " height: " + height);
        params = new AbsoluteLayout.LayoutParams(width, height, x, y);
        updateViewLayout(mFocusRectView, params);
    }

    private void animateNewView() {
        
    }

    private void animateRect() {
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.setInterpolator(new LinearInterpolator());
        
        // XXX this do not work. bysong@tudou.com
        animationSet.setDuration(mDuration);
        
        // order is important, scale firstly, then translate.
        addScaleAnimation(animationSet);
        addTranslateAnimation(animationSet);
        
        mFocusRectView.startAnimation(animationSet);
    }

    private void addScaleAnimation(AnimationSet animationSet) {
        float fromX = (float)mLastFocusRect.width() / mCurrentFocusRect.width();
        float toX = 1;
        float fromY = (float)mLastFocusRect.height() / mCurrentFocusRect.height();
        float toY = 1;
        float pivotX = mCurrentFocusRect.exactCenterX();
        float pivotY = mCurrentFocusRect.exactCenterY();
        pivotX = (float)mFocusRectView.getWidth() / 2;
        pivotY = (float)mFocusRectView.getHeight() / 2;
        pivotX = (float)mCurrentFocusRect.width() / 2;
        pivotY = (float)mCurrentFocusRect.height() / 2;
//      Log.d(TAG, "scale fromX: " + fromX + " toX: " + toX + " fromY: " + fromY + " toY: " + toY + " pivotX: " + pivotX + " pivotY: " + pivotY);
        pivotX = (float) 0.5;
        pivotY = (float) 0.5;
        ScaleAnimation s = new ScaleAnimation(fromX, toX, fromY, toY, Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
        s.setDuration(mDuration);
        s.setInterpolator(new LinearInterpolator());
        animationSet.addAnimation(s);
    }

    private void addTranslateAnimation(AnimationSet animationSet) {
        float fromXDelta = mLastFocusRect.centerX() - mCurrentFocusRect.centerX();
        float toXDelta = 0;
        float fromYDelta = mLastFocusRect.centerY() - mCurrentFocusRect.centerY();
        float toYDelta = 0;     
//      Log.d(TAG, "translate fromXDelta: " + fromXDelta + " toXDelta: " + toXDelta + " fromYDelta: " + fromYDelta + " toYDelta: " + toYDelta);
        TranslateAnimation t = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        t.setDuration(mDuration);
        t.setInterpolator(new LinearInterpolator());
        animationSet.addAnimation(t);
    }

    private void animateOldBitmap() {
    }
    
    private void deubgFocuRect() {
        int width = mFocusRectView.getWidth();
        int height = mFocusRectView.getHeight();
        int left = mFocusRectView.getLeft();
        int top = mFocusRectView.getTop();
//      Log.d(TAG, "mFocusRectView left: " + left + " top: " + top + " width: " + width + " height: " + height);
    }

    @Override
    protected FixedSizeView onInflateFocusRectView(LayoutInflater layoutInflater) {
        FixedSizeView v = new FixedSizeView(getContext());
        return v;
    }
}