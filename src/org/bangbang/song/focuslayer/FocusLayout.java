package org.bangbang.song.focuslayer;

import org.bangbang.song.android.commonlib.FPSLoger;
import org.bangbang.song.demo.focuslayer.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsoluteLayout;

/**
 * by adjusting view's Z-order(index), to animate. 
 * but animating can only draw in parent's bound.
 * 
 * @author bysong
 *
 */
public abstract class FocusLayout extends AbsoluteLayout implements IFocusAnimationLayer {
    private static final String TAG = FocusLayout.class.getSimpleName();
    
    public static final int ID_RECT = R.id.paste;
    protected AnimationConfigure mConfigure;
    private OnFocusChangeListener mListener;
    private FPSLoger mFPS;

    protected Rect mTmpRect;
    public FocusLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        init();
    }

    public FocusLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        init();
    }

    public FocusLayout(Context context) {
        super(context);
        
        init();
    }
    
    void init() {
        mConfigure = new AnimationConfigure();
        mConfigure.mScaleFactor = 1.1f;
        mConfigure.mDuration = 200;
        mFPS = new FPSLoger(TAG);
        mTmpRect = new Rect();
        mListener = new OnFocusChangeListener() {
            
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                FocusLayout.this.onFocusChange(v, hasFocus);
            }
        };
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getParent() != this) {
            throw new IllegalStateException("view must be my child.");
        }
        
        if (hasFocus) {
            mConfigure.onNewFocus(this, v);
            if (v.getTag(ID_RECT)  == null) {
                v.setTag(ID_RECT, new Rect(v.getLeft(), v.getTop(),
                        v.getWidth() + v.getLeft(), v.getTop() + v.getHeight()));
            }
            v.bringToFront();
            doScalUp(v);
        } else {
            doScalDown(v);
        }
    }

    protected abstract void doScalDown(View v);

    protected abstract void doScalUp(View v);
    
    @Override
    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {
        // TODO Auto-generated method stub
        super.addView(child, index, params);
        
        child.setOnFocusChangeListener(mListener);
    }
    
    @Override
    protected void dispatchDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.dispatchDraw(canvas);
        
        if (mConfigure.TRACK_FPS) {
            mFPS.onDraw();
        }
    }
}