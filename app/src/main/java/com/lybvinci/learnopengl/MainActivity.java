package com.lybvinci.learnopengl;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "lyb_MainActivity";
    private GLSurfaceView mSurfaceView;
    private boolean renderSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        mSurfaceView = new GLSurfaceView(this);
        final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo deviceConfigurationInfo = am.getDeviceConfigurationInfo();
        Log.i(TAG, "" + deviceConfigurationInfo.reqGlEsVersion);
        final boolean supportsEs2 = deviceConfigurationInfo.reqGlEsVersion >= 0x20000;
        Log.i(TAG, "gl version:" + deviceConfigurationInfo.getGlEsVersion());
        if (supportsEs2) {
            mSurfaceView.setEGLContextClientVersion(2);
            mSurfaceView.setRenderer(new FirstOpenGLRender());
            renderSet = true;
        } else {
            Toast.makeText(this, "不支持es2", Toast.LENGTH_SHORT).show();
        }
        setContentView(mSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (renderSet) {
            mSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (renderSet) {
            mSurfaceView.onResume();
        }
    }

    private class FirstOpenGLRender implements GLSurfaceView.Renderer {
        int i = 0;

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        }

        @Override
        public void onSurfaceChanged(GL10 gl10, int i, int i1) {
            GLES20.glViewport(0, 0, i, i1);
        }

        @Override
        public void onDrawFrame(GL10 gl10) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            if (i % 3000 <= 1000) {
                GLES20.glClearColor(1, 0, 0, 1);
            } else if (i % 3 <= 2000) {
                GLES20.glClearColor(0, 1, 0, 1);
            } else {
                GLES20.glClearColor(0, 0, 1, 1);
            }
            i++;
        }
    }
}