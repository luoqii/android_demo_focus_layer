
package org.bangbang.song.demo.focuslayer;

import org.bangbang.song.android.commonlib.ViewUtil;
import org.bangbang.song.focuslayer.Utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalFocusChangeListener;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalFocusChangeListener(new OnGlobalFocusChangeListener() {
            
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                Utils.onFocusChange(oldFocus, false);
                Utils.onFocusChange(newFocus, true);
                
                if (newFocus != null) {
                    View v = findViewById(R.id.content);
                    Bitmap b = ViewUtil.getBitmapX(newFocus, v.getWidth(), v.getHeight());
                    v.setBackground(new BitmapDrawable(b));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

}
