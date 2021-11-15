package com.lybvinci.particles.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.lybvinci.particles.utils.FileReader;
import com.lybvinci.particles.utils.ShaderHelper;

public class ShaderProgram {
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String U_COLOR = "u_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
    protected static final String U_TIME = "u_Time";
    protected static final String A_PARTICLE_START_TIME = "a_ParticleStartTime";
    protected static final String A_DIRECTION_VECTOR = "a_DirectionVector";

    protected final int program;

    protected ShaderProgram(Context context, int vertexShaderResourceId, int fragmentShaderResourceId) {
        program = ShaderHelper.buildProgram(FileReader.readRawFile(context, vertexShaderResourceId), FileReader.readRawFile(context, fragmentShaderResourceId));
    }

    public void useProgram() {
        GLES20.glUseProgram(program);
    }
}
