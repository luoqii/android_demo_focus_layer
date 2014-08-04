package org.bangbang.song.focuslayer;

import static android.opengl.GLES10.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES10.GL_LINEAR;
import static android.opengl.GLES10.GL_TEXTURE_2D;
import static android.opengl.GLES10.GL_TEXTURE_COORD_ARRAY;
import static android.opengl.GLES10.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES10.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES10.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES10.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES10.glBindTexture;
import static android.opengl.GLES10.glEnableClientState;
import static android.opengl.GLES10.glGenTextures;
import static android.opengl.GLES10.glTexParameterf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.bangbang.song.android.commonlib.GLUtil;
import org.bangbang.song.android.commonlib.LogRender;
import org.bangbang.song.demo.focuslayer.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.View;

public class GLFocusLayer extends GLSurfaceView implements IFocusAnimationLayer {
	public static final boolean DEBUG = true;
	public static final String TAG = GLFocusLayer.class.getSimpleName();

	private AnimationConfigure mConfig;
	private SurfaceHolder mHolder;
	private RectModel mTransfer;

	public GLFocusLayer(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	public GLFocusLayer(Context context) {
		super(context);

		init();
	}

	private void init() {
		setId(Utils.FOCUS_LAYER_ID);
		
		mConfig = new AnimationConfigure();

		// We want an 8888 pixel format because that's required for
		// a translucent window.
		// And we want a depth buffer.
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);		
		mHolder = getHolder();
		// for show what we draw on content.
		mHolder.setFormat(PixelFormat.TRANSLUCENT);
		setZOrderOnTop(true);
		
		setRenderer(new LayerRender());

		setRenderMode(RENDERMODE_WHEN_DIRTY);
//		setRenderMode(RENDERMODE_CONTINUOUSLY);

//		setDebugFlags(DEBUG_CHECK_GL_ERROR);	
//		setDebugFlags(DEBUG_LOG_GL_CALLS);
		
		mTransfer = new RectModel();
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			mConfig.onNewFocus(this, v);
			
			mTransfer.updateRect(mConfig.mCurrentScaledFocusRect);
		}

		requestRender();
	}

	@Override
	public void onFocusSessionEnd(View lastFocus) {
		mConfig.onFocusSessionEnd(lastFocus);
	}

	class LayerRender extends LogRender {

		private int mTextureId;

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			super.onSurfaceCreated(gl, config);

			gl.glClearColor(0f, 0f, .0f, 0f);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	        gl.glEnableClientState(GL_TEXTURE_COORD_ARRAY);
			logError(gl, "glEnableClientState");
			
			int[] textures = new int[1];
			gl.glGenTextures(1, textures, 0);
			logError(gl, "glGenTextures");
			
			mTextureId = textures[0];
			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureId);
			logError(gl, "glBindTexture");
	        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
	        		GL_LINEAR);
	        glTexParameterf(GL_TEXTURE_2D,
	                GL_TEXTURE_MAG_FILTER,
	                GL_LINEAR);

	        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S,
	                GL_CLAMP_TO_EDGE);
	        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T,
	                GL_CLAMP_TO_EDGE);
	        
		}
		@Override
		public void onSurfaceChanged(GL10 gl, int w, int h) {
			super.onSurfaceChanged(gl, w, h);

			gl.glViewport(0, 0, w, h); 
			float ratio = (float) w / h;
			gl.glMatrixMode(GL10.GL_PROJECTION); 
			gl.glLoadIdentity();
			gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7);
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			super.onDrawFrame(gl);

			
			gl.glActiveTexture(GL10.GL_TEXTURE0);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureId);
			
			

			Bitmap bitmap = mConfig.mCurrentFocusBitmap;
			if (null != bitmap){
				GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
			}
			
			logError(gl, "glActiveTexture");
			mTransfer.draw(gl);
			
			 gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		}

	}

	private void logError(GL10 gl, String methodName) {
		GLUtil.logError(gl, TAG, methodName);
	}

	class RectModel {
		private static final int INDEX_COUNT = 6;
		private static final int VERTEX_COUNT = 4;
		private static final int COORDS_PER_VERTEX = 2;
		private static final int TEXTURE_PER_VERTEX = 2;
		
		private static final int BYTE_PER_FLOAT = 4;
		private static final int BYTE_PER_SHORT = 2;

		public RectF mRect;
		public FloatBuffer mVertexBuffer;
		private ShortBuffer mIndexBuffer;
		private FloatBuffer mTexureBuffer;

		public RectModel() {
			ByteBuffer vbb = ByteBuffer.allocateDirect(VERTEX_COUNT
					* COORDS_PER_VERTEX * BYTE_PER_FLOAT);
			vbb.order(ByteOrder.nativeOrder());
			mVertexBuffer = vbb.asFloatBuffer();
			
			ByteBuffer tbb = ByteBuffer.allocateDirect(VERTEX_COUNT
					* TEXTURE_PER_VERTEX * BYTE_PER_FLOAT);
			tbb.order(ByteOrder.nativeOrder());
			mTexureBuffer = tbb.asFloatBuffer();

			ByteBuffer ibb = ByteBuffer.allocateDirect(INDEX_COUNT
					* BYTE_PER_SHORT);
			ibb.order(ByteOrder.nativeOrder());
			mIndexBuffer = ibb.asShortBuffer();
			
			mRect = new RectF();
		}

		public void updateRect(Rect r) {
			mRect = new RectF(r);
		}

		public void draw(GL10 gl) {
			calculate();

			gl.glFrontFace(GL10.GL_CCW);
			logError(gl, "glFrontFace");
			gl.glVertexPointer(COORDS_PER_VERTEX, GL10.GL_FIXED, 0, mVertexBuffer);
			logError(gl, "glVertexPointer");
			
			gl.glEnable(GL10.GL_TEXTURE_2D);
			logError(gl, "glEnable");			
			gl.glTexCoordPointer(TEXTURE_PER_VERTEX, GL10.GL_FIXED, 0, mTexureBuffer);
			logError(gl, "glTexCoordPointer");
			gl.glDrawElements(GL10.GL_TRIANGLES, VERTEX_COUNT,
					GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
			logError(gl, "glDrawElements");
		}

		private void calculate() {

			mVertexBuffer.position(0);
			mVertexBuffer.put(mRect.top);
			mVertexBuffer.put(mRect.left);
			mVertexBuffer.put(mRect.bottom);
			mVertexBuffer.put(mRect.left);
			mVertexBuffer.put(mRect.bottom);
			mVertexBuffer.put(mRect.right);
			mVertexBuffer.put(mRect.top);
			mVertexBuffer.put(mRect.right);
			mVertexBuffer.position(0);

			mTexureBuffer.position(0);
			mTexureBuffer.put(mRect.top);
			mTexureBuffer.put(mRect.left);
			mTexureBuffer.put(mRect.bottom);
			mTexureBuffer.put(mRect.left);
			mTexureBuffer.put(mRect.bottom);
			mTexureBuffer.put(mRect.right);
			mTexureBuffer.put(mRect.top);
			mTexureBuffer.put(mRect.right);
			mTexureBuffer.position(0);

			mIndexBuffer.position(0);
			mIndexBuffer.put((short) 0);
			mIndexBuffer.put((short) 1);
			mIndexBuffer.put((short) 2);
			mIndexBuffer.put((short) 0);
			mIndexBuffer.put((short) 2);
			mIndexBuffer.put((short) 3);
			mIndexBuffer.position(0);
			
		}
	}	
}
