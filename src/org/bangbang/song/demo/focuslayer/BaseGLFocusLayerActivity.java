
package org.bangbang.song.demo.focuslayer;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;

public abstract class BaseGLFocusLayerActivity extends Activity {
	private GLSurfaceView mGlView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_glfocuslayer);
		
		mGlView = (GLSurfaceView)findViewById(R.id.focusLayer);
		onInitGLView(mGlView);
	}

	protected abstract void onInitGLView(GLSurfaceView view);

	@Override
	protected void onResume() {
		super.onResume();
		mGlView.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mGlView.onPause();
	}
	
	
}
