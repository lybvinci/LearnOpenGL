package com.lybvinci.learnopengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.google.android.material.tabs.TabLayout;
import com.lybvinci.learnopengl.objects.Mallet;
import com.lybvinci.learnopengl.objects.Table;
import com.lybvinci.learnopengl.programs.ColorShaderProgram;
import com.lybvinci.learnopengl.programs.TextureShaderProgram;
import com.lybvinci.learnopengl.utils.FileReader;
import com.lybvinci.learnopengl.utils.ShaderHelper;
import com.lybvinci.learnopengl.utils.TextureHelper;

import org.w3c.dom.Text;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class AirHockeyRender implements GLSurfaceView.Renderer {

    private final Context mContext;
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private Table table;
    private Mallet mallet;

    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;

    private int texture;

    public AirHockeyRender(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f, 0f, 0f, 0f);
        table = new Table();
        mallet = new Mallet();
        textureProgram = new TextureShaderProgram(mContext);
        colorProgram = new ColorShaderProgram(mContext);
        texture = TextureHelper.loadTexture(mContext, R.drawable.air_hockey_surface);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        Matrix.perspectiveM(projectionMatrix, 0, 45, (float)width / (float)height, 1f, 10f);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -3f);
        Matrix.rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
        final float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        textureProgram.useProgram();
        textureProgram.setUniforms(projectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        colorProgram.useProgram();
        colorProgram.setUniforms(projectionMatrix);
        mallet.bindData(colorProgram);
        mallet.draw();
    }
}
