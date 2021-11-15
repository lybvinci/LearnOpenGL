package com.lybvinci.particles;

import android.content.Context;
import static android.opengl.GLES20.*;

import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.lybvinci.particles.objects.ParticleShooter;
import com.lybvinci.particles.objects.ParticleSystem;
import com.lybvinci.particles.objects.Skybox;
import com.lybvinci.particles.programs.ParticleShaderProgram;
import com.lybvinci.particles.programs.SkyboxShaderProgram;
import com.lybvinci.particles.utils.Geometry;
import com.lybvinci.particles.utils.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

class ParticlesRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "ParticlesRenderer";

    private final Context context;
    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];

    private ParticleShaderProgram particleProgram;
    private ParticleSystem particleSystem;
    private ParticleShooter redParticleShooter;
    private ParticleShooter greenParticleShooter;
    private ParticleShooter blueParticleShooter;
    private long globalStartTime;
    private int particleTexture;

    private SkyboxShaderProgram skyboxProgram;
    private Skybox skybox;
    private int skyboxTexture;


    public ParticlesRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
//        glEnable(GL_BLEND);
//        glBlendFunc(GL_ONE, GL_ONE);

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);


        particleProgram = new ParticleShaderProgram(context);
        particleSystem = new ParticleSystem(10000);
        globalStartTime = System.nanoTime();
        final Geometry.Vector particleDirection = new Geometry.Vector(0f, 0.5f, 0f);
        final float angleVarianceInDegrees = 5f;
        final float speedVariance = 1f;

        redParticleShooter = new ParticleShooter(new Geometry.Point(-1f, 0f, 0f), particleDirection,
                Color.rgb(255, 50, 5), angleVarianceInDegrees, speedVariance);
        greenParticleShooter = new ParticleShooter(new Geometry.Point(0f, 0f, 0f),
                particleDirection, Color.rgb(25, 255, 25), angleVarianceInDegrees, speedVariance);
        blueParticleShooter = new ParticleShooter(new Geometry.Point(1f, 0f, 0f), particleDirection,
                Color.rgb(5, 50, 255), angleVarianceInDegrees, speedVariance);
        particleTexture = TextureHelper.loadTexture(context, R.drawable.particle_texture);

        skyboxProgram = new SkyboxShaderProgram(context);
        skybox = new Skybox();
        skyboxTexture = TextureHelper.loadCubeMap(context, new int[]{ R.drawable.left, R.drawable.right,
                R.drawable.bottom, R.drawable.top, R.drawable.front, R.drawable.back});
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        glViewport(0, 0, width, height);

        Matrix.perspectiveM(projectionMatrix, 0, 45, (float)width / (float)height, 1f, 10f);
//        Matrix.setIdentityM(viewMatrix, 0);
//        Matrix.translateM(viewMatrix, 0, 0f, -1.5f, -5f);
//        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT);
        drawSkybox();
        drawParticles();
    }

    private void drawParticles() {
        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;

        redParticleShooter.addParticles(particleSystem, currentTime, 5);
        greenParticleShooter.addParticles(particleSystem, currentTime, 5);
        blueParticleShooter.addParticles(particleSystem, currentTime, 5);

        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.translateM(viewMatrix, 0, 0f, -1.5f, -5f);
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        GLES20.glEnable(GL_BLEND);
        GLES20.glBlendFunc(GL_ONE, GL_ONE);

        particleProgram.useProgram();
        particleProgram.setUniforms(viewProjectionMatrix, currentTime, particleTexture);
        particleSystem.bindData(particleProgram);
        particleSystem.draw();

        GLES20.glDisable(GL_BLEND);
    }

    private void drawSkybox() {
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.rotateM(viewMatrix, 0, -yRotation, 1f, 0f,  0f);
        Matrix.rotateM(viewMatrix, 0, -xRotation, 0f, 1f,  0f);
//        Matrix.translateM(viewMatrix, 0, 0f, -1.5f, -5f);
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        skyboxProgram.useProgram();
        skyboxProgram.setUniforms(viewProjectionMatrix, skyboxTexture);
        skybox.bindData(skyboxProgram);
        skybox.draw();
    }

    private float xRotation, yRotation;

    public void handleTouchDrag(float deltaX, float deltaY) {
        xRotation += deltaX / 16f;
        yRotation += deltaY / 16f;
        if (yRotation < -90) {
            yRotation = -90;
        } else if (yRotation > 90) {
            yRotation = 90;
        }
    }
}
