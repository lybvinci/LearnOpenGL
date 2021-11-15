package com.lybvinci.learnopengl;

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

public class AirHockeyActivity extends AppCompatActivity {

    private static final String TAG = "lyb_MainActivity";
    private GLSurfaceView mSurfaceView;
    private boolean renderSet = false;
    private AirHockeyRender ariHockeyRenderer;

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
            ariHockeyRenderer = new AirHockeyRender(this);
            mSurfaceView.setRenderer(ariHockeyRenderer);
            renderSet = true;
            mSurfaceView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (null != event) {
                        final float normalizedX = event.getX() / v.getWidth() *2 -1;
                        final float normalizedY = -(event.getY() / v.getHeight() *2 - 1);
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            mSurfaceView.queueEvent(new Runnable() {
                                @Override
                                public void run() {
                                   ariHockeyRenderer.handleTouchPress(normalizedX, normalizedY);
                                }
                            });
                        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                            mSurfaceView.queueEvent(new Runnable() {
                                @Override
                                public void run() {
                                    ariHockeyRenderer.handleTouchDrag(normalizedX, normalizedY);
                                }
                            });
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            });
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