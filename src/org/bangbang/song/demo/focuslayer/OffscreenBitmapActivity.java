package org.bangbang.song.demo.focuslayer;

import org.bangbang.song.android.commonlib.ViewUtil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;

/**
 * get offscreen bitmap from arbitary view.
 * 
 * to easy debugging, turn debug-layout on.
 * 
 * @author bysong
 * @see http://stackoverflow.com/questions/2801116/converting-a-view-to-bitmap-without-displaying-it-in-android
 */
public class OffscreenBitmapActivity extends Activity {
    private View mPaste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_offscreen);
        
        mPaste = findViewById(R.id.paste);
    }
    
    public void onClick(View view) {
        int id = 0;
        switch (view.getId()){
            case R.id.go1:
                id = R.id.copy1;
                break;
            case R.id.go2:
                id = R.id.copy2;
                break;
            case R.id.go3:
                id = R.id.copy3;
                break;
        }
        View copy = findViewById(id);
        int w = mPaste.getWidth();
        int h = mPaste.getHeight();
        Bitmap bitmap = ViewUtil.getBitmap(copy, w, h);
        
//        mPaste.setBackground(new BitmapDrawable(bitmap));
        mPaste.setBackgroundDrawable(new BitmapDrawable(bitmap));
    }
    
}
