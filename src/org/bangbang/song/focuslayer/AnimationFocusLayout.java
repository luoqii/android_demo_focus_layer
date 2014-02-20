package org.bangbang.song.focuslayer;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AbsoluteLayout;
import android.widget.AbsoluteLayout.LayoutParams;

public class AnimationFocusLayout extends FocusLayout {
    private static final String TAG = AnimationFocusLayout.class.getSimpleName();
    public AnimationFocusLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public AnimationFocusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public AnimationFocusLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    protected void doScalDown(View v) {
        
        Rect r = (Rect) v.getTag(ID_RECT);
        if (null == r) {
            return;
        }
        
        updateParam(v, r);
        
        Rect rect = r;
        v.getDrawingRect(mTmpRect);
        Rect scaledRect = mTmpRect;
        
        float fromX = (float) scaledRect.width() / rect.width();
        float toX = 1;
        float fromY = (float) scaledRect.height() / rect.height();
        float toY = 1;
        float pivotX = .5f;
        float pivotY = .5f;
        if (mConfigure.DEBUG_SCALE_ANIMATION) {
            Log.d(TAG, "scale fromX: " + fromX + " toX: " + toX + " fromY: " + fromY + " toY: "
                    + toY + " pivotX: " + pivotX + " pivotY: " + pivotY);
        }
        ScaleAnimation s = new ScaleAnimation(fromX, toX, fromY, toY, Animation.RELATIVE_TO_SELF,
                pivotX, Animation.RELATIVE_TO_SELF, pivotY);
        s.setDuration(mConfigure.mDuration);
        s.setInterpolator(new LinearInterpolator());
        v.startAnimation(s);
    }
    
    @Override
    protected void doScalUp(View v) {
        
        Rect rect = mConfigure.mCurrentScaledFocusRect;
        updateParam(v, rect);
        
        rect = (Rect) v.getTag(ID_RECT);
        Rect scaledRect = mConfigure.mCurrentScaledFocusRect;
        
        float fromX = (float) rect.width() / scaledRect.width();
        float toX = 1;
        float fromY = (float) rect.height() / scaledRect.height();
        float toY = 1;
        float pivotX = scaledRect.exactCenterX();
        float pivotY = scaledRect.exactCenterY();
        
        pivotX = 0.5f;
        pivotY = 0.5f;
        if (mConfigure.DEBUG_SCALE_ANIMATION) {
            Log.d(TAG, "scale fromX: " + fromX + " toX: " + toX + " fromY: " + fromY + " toY: "
                    + toY + " pivotX: " + pivotX + " pivotY: " + pivotY);
        }
        ScaleAnimation s = new ScaleAnimation(fromX, toX, fromY, toY, Animation.RELATIVE_TO_SELF,
                pivotX, Animation.RELATIVE_TO_SELF, pivotY);
        s.setDuration(mConfigure.mDuration);
        s.setInterpolator(new LinearInterpolator());
        v.startAnimation(s);
    }

    private void updateParam(View v, Rect r) {
        LayoutParams params = null;

        int width = r.width();
        int height = r.height();
        int x = r.left;
        int y = r.top;
        if (mConfigure.DEBUG_TRANSFER_ANIMATION) {
            Log.d(TAG, "new layout params x: " + x + " y: " + y + " width: " + width + " height: "
                    + height);
        }
        params = new AbsoluteLayout.LayoutParams(width, height, x, y);
        updateViewLayout(v, params);
    }
    
    
}