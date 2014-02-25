package org.bangbang.song.demo.focuslayer;

import org.bangbang.song.focuslayer.AnimationFocusLayout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author bysong
 * @see {@link ViewGroup#setClipChildren(boolean)}
 * @see {@link ViewGroup#setClipToPadding(boolean)}
 *
 */
public class ListViewAnimationAcitivity extends Activity {
    private static final String TAG = ListViewAnimationAcitivity.class.getSimpleName();
    private ListView mListView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_listview);
        mListView = (ListView)findViewById(R.id.listView);
        mListView.setAdapter(new Adapter(this));
        
        mListView.setClipChildren(false);
        mListView.setClipToPadding(false);
    }
    
    class Adapter extends SimpleDataAdapter {

        public Adapter(Context context) {
            super(context);
        }
        
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
//            return super.getView(position, convertView, parent);
            
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.listview_item, null);
            }
            ((TextView)convertView.findViewById(R.id.textView)).setText(getItem(position));
            
            return convertView;
        }
    }
    
    public static class MyListView extends ListView {

        public MyListView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            // TODO Auto-generated constructor stub
        }

        public MyListView(Context context, AttributeSet attrs) {
            super(context, attrs);
            
            
            setChildrenDrawingOrderEnabled(true);
        }

        public MyListView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }
        
        @Override
        protected int getChildDrawingOrder(int childCount, int i) {
            // TODO Auto-generated method stub
            int order = super.getChildDrawingOrder(childCount, i);
            
            int index = getSelectedItemPosition() - getFirstVisiblePosition();
            if (0 <= index && index < childCount) {
                if (i == childCount - 1) {
                    order =index;
                } else if (i >= index) {
                    order = i + 1;
                }
            }
//            Log.d(TAG, "getChildDrawingOrder. childCount: " + childCount + " i: " + i 
//                    + " order: " + order + " index: " + index);
            return order;
        }
        
    }
    
    public static class MyLayout extends AnimationFocusLayout {

        public MyLayout(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            // TODO Auto-generated constructor stub
        }

        public MyLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            
        }

        public MyLayout(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }
        
        @Override
        public void setSelected(boolean selected) {
            // TODO Auto-generated method stub
            super.setSelected(selected);
            Log.d(TAG, "setSelected. selected: " + selected  );;
        }
        
        @Override
        public void dispatchSetSelected(boolean selected) {
            // TODO Auto-generated method stub
            super.dispatchSetSelected(selected);
        }
        
    }
    
    public static class MyButton extends Button {

        private Paint mPaint;


        public MyButton(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            // TODO Auto-generated constructor stub
        }

        public MyButton(Context context, AttributeSet attrs) {
            super(context, attrs);
            mPaint = new Paint();
            mPaint.setColor(Color.RED);
        }

        public MyButton(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }
        
        @Override
        public void draw(Canvas canvas) {
            // TODO Auto-generated method stub
            super.draw(canvas);
            
            if (isSelected())  {
                RectF r = new RectF(2, 2, 180, 180);
                canvas.drawRect(r, mPaint);
            }
        }
    }
}
