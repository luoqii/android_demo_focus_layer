package org.bangbang.song.focuslayer;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.AbsoluteLayout;

public class AnimationLayer extends AbsoluteLayout implements IFocusAnimationLayer {

    private View mCurrentFocus;
    private View mCurrentUnFocus;  
    private WeakReference<View> mCurrentFocusRef;
    private WeakReference<View> mCurrentUnFocusRef;

    public AnimationLayer(Context context, AttributeSet attrs) {
        super(context, attrs);

        mCurrentFocusRef = new WeakReference<View>(null);
        mCurrentUnFocusRef = new WeakReference<View>(null);
        
        mCurrentFocus = new View(context);
        addView(mCurrentFocus);
        
        setId(Utils.FOCUS_LAYER_ID);
    }

    @Override
    public void onFocusChange(View focusView, boolean hasFocus) {
        if (null == focusView) {
            return;
        }
        
        if (hasFocus) {
            mCurrentFocusRef = new WeakReference<View>(focusView);
        } else {
            mCurrentUnFocusRef = new WeakReference<View>(focusView);
        }
        
        doAnimation();
    }

    private void doAnimation() {
        doFocusAnimation();
        doUnFocusAnimation();
        dotransferAnimation();
    }

    private void dotransferAnimation() {
        
    }

    private void doUnFocusAnimation() {
    }

    private void doFocusAnimation() {
        View target = mCurrentUnFocusRef.get();
        if (null == target) {
            return;
        }
        
        Rect r = transferCoord(this, target);
        Bitmap b = mCurrentUnFocus.getDrawingCache();
        
        LayoutParams lp = (LayoutParams) mCurrentFocus.getLayoutParams();
        lp.x = r.left;
        lp.y = r.top;
        lp.width = r.width();
        lp.height = r.height();
        mCurrentFocus.setBackground(new BitmapDrawable(b));
        ScaleAnimation a = new ScaleAnimation(1, 1, 1.2f, 1.2f);
        mCurrentFocus.startAnimation(a);
        updateViewLayout(mCurrentFocus, lp);
    }
    
    public static Rect transferCoord(View currentView, View targetView) {
        Rect r = new Rect();
        targetView.getFocusedRect(r);
        ViewGroup root = (ViewGroup) currentView.getRootView();
        root.offsetDescendantRectToMyCoords(targetView, r);
        root.offsetRectIntoDescendantCoords(currentView, r);
        
        return r;
    }

}
