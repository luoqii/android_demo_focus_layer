package org.bangbang.song.focuslayer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsoluteLayout;

public class AnimationLayer extends AbsoluteLayout implements IFocusAnimationLayer {

    private View mCurrentFocus;
    private View mCurrentUnFocus;

    public AnimationLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onFocusChange(View focusView, boolean hasFocus) {
        if (null == focusView) {
            return;
        }
        
        if (hasFocus) {
            mCurrentFocus = focusView;
        } else {
            mCurrentUnFocus = focusView;
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
        
    }

}
