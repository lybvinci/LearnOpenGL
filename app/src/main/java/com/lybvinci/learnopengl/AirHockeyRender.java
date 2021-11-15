package com.lybvinci.learnopengl;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.lybvinci.learnopengl.objects.Mallet;
import com.lybvinci.learnopengl.objects.Puck;
import com.lybvinci.learnopengl.objects.Table;
import com.lybvinci.learnopengl.programs.ColorShaderProgram;
import com.lybvinci.learnopengl.programs.TextureShaderProgram;
import com.lybvinci.learnopengl.utils.Geometry;
import com.lybvinci.learnopengl.utils.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class AirHockeyRender implements GLSurfaceView.Renderer {
    private static final String TAG = "AirHockeyRender";
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];
    private final float[] invertedViewProjectionMatrix = new float[16];
    private final float leftBound = -0.5f;
    private final float rightBound = 0.5f;
    private final float farBound = -0.8f;
    private final float nearBound = 0.8f;

    private final Context mContext;
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    private Table table;
    private Mallet mallet;
    private Puck puck;

    private TextureShaderProgram textureProgram;
    private ColorShaderProgram colorProgram;

    private int texture;
    private Geometry.Point previousBlueMalletPosition;
    private Geometry.Point puckPosition;
    private Geometry.Vector puckVector;

    public AirHockeyRender(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f, 0f, 0f, 0f);
        table = new Table();
        mallet = new Mallet(0.08f, 0.15f, 32);
        puck = new Puck(0.06f, 0.02f, 32);
        textureProgram = new TextureShaderProgram(mContext);
        colorProgram = new ColorShaderProgram(mContext);
        texture = TextureHelper.loadTexture(mContext, R.drawable.air_hockey_surface);
        blueMalletPosition = new Geometry.Point(0f, mallet.height / 2f, 0.4f);
        puckPosition = new Geometry.Point(0f, puck.height / 2f, 0f);
        puckVector = new Geometry.Vector(0f, 0f, 0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        Matrix.setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);
        Matrix.perspectiveM(projectionMatrix, 0, 45, (float)width / (float)height, 1f, 10f);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);
        positionTableInScene();
        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        // draw the mallets
        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
        colorProgram.useProgram();
        colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f);
        mallet.bindData(colorProgram);
        mallet.draw();

        positionObjectInScene(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
        mallet.draw();

//        positionObjectInScene(0f, puck.height / 2f, 0f);
        puckPosition = puckPosition.translate(puckVector);
        if (puckPosition.x < leftBound + puck.radius || puckPosition.x > rightBound - puck.radius) {
            puckVector = new Geometry.Vector(-puckVector.x, puckVector.y, puckVector.z);
            puckVector = puckVector.scale(0.9f);
        }
        if (puckPosition.z <farBound + puck.radius || puckPosition.z >nearBound - puck.radius) {
            puckVector = new Geometry.Vector(puckVector.x, puckVector.y, -puckVector.z);
            puckVector = puckVector.scale(0.9f);
        }
        puckPosition = new Geometry.Point(clamp(puckPosition.x, leftBound + puck.radius, rightBound - puck.radius),
                puckPosition.y,
                clamp(puckPosition.z, farBound + puck.radius, nearBound - puck.radius));
        positionObjectInScene(puckPosition.x, puckPosition.y, puckPosition.z);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorProgram);
        puck.draw();
        puckVector = puckVector.scale(0.99f);
    }

    private void positionTableInScene() {
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }

    private void positionObjectInScene(float x, float y, float z) {
       Matrix.setIdentityM(modelMatrix, 0);
       Matrix.translateM(modelMatrix, 0, x, y, z);
       Matrix.multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }


    private boolean malletPressed = false;
    private Geometry.Point blueMalletPosition;

    public void handleTouchPress(float normalizedX, float normalizedY) {
        Log.d(TAG, "handleTouchPress: x:" + normalizedX + " y:" + normalizedY);
        Geometry.Ray ray = convertNormalizedf2DPointToRay(normalizedX, normalizedY);
        Geometry.Sphere malletBoundingSphere = new Geometry.Sphere(new Geometry.Point(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z), mallet.height / 2f);
        malletPressed = Geometry.intersects(malletBoundingSphere, ray);
        Log.i(TAG, "handleTouchPress: malletPressed:" + malletPressed);
    }

    private Geometry.Ray convertNormalizedf2DPointToRay(float normalizedX, float normalizedY) {
        final float[]  nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[]  farPointNdc = {normalizedX, normalizedY, 1, 1};

        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];

        Matrix.multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        Matrix.multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);
        divideByW(nearPointWorld);
        divideByW(farPointWorld);
        Geometry.Point nearPointRay = new Geometry.Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        Geometry.Point farPointRay = new Geometry.Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);

        return new Geometry.Ray(nearPointRay, Geometry.vectorBetween(nearPointRay, farPointRay));
    }

    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];

    }


    public void handleTouchDrag(float normalizedX, float normalizedY) {
        Log.d(TAG, "handleTouchDrag: x:" + normalizedX + " y:" + normalizedY);
        if (malletPressed) {
            Geometry.Ray ray = convertNormalizedf2DPointToRay(normalizedX, normalizedY);
            Geometry.Plane plane = new Geometry.Plane(new Geometry.Point(0, 0, 0), new Geometry.Vector(0, 1, 0));
            Geometry.Point touchedPoint = Geometry.intersectionPoint(ray, plane);
            previousBlueMalletPosition = blueMalletPosition;
            blueMalletPosition = new Geometry.Point(clamp(touchedPoint.x, leftBound + mallet.radius, rightBound - mallet.radius),
                    mallet.height / 2f,
                    clamp(touchedPoint.z, 0f + mallet.radius, nearBound - mallet.radius));
            float distance = Geometry.vectorBetween(blueMalletPosition, puckPosition).length();
            if (distance < (puck.radius + mallet.radius)) {
                puckVector = Geometry.vectorBetween(previousBlueMalletPosition, blueMalletPosition);
            }
        }
    }

    private float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
    }


}
