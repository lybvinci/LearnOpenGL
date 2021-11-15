package com.lybvinci.particles;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ParticlesActivity extends AppCompatActivity {

    private static final String TAG = "lyb_MainActivity";
    private GLSurfaceView mSurfaceView;
    private boolean renderSet = false;
    private ParticlesRenderer ariHockeyRenderer;

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
            ariHockeyRenderer = new ParticlesRenderer(this);
            mSurfaceView.setRenderer(ariHockeyRenderer);
            mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
                float previousX, previousY;
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (null != event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            previousX = event.getX();
                            previousY = event.getY();
                        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                            final float deltaX = event.getX() - previousX;
                            final float deltaY = event.getY() - previousY;
                            previousX = event.getX();
                            previousY = event.getY();
                            mSurfaceView.queueEvent(new Runnable() {
                                @Override
                                public void run() {
                                   ariHockeyRenderer.handleTouchDrag(deltaX, deltaY);
                                }
                            });
                        }
                        return true;
                    }
                    return false;
                }
            });
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

}