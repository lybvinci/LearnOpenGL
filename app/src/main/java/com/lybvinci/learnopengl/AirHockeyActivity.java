package com.lybvinci.learnopengl;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AirHockeyActivity extends AppCompatActivity {

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
            mSurfaceView.setRenderer(new AirHockeyRender(this));
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