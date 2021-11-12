package com.lybvinci.learnopengl.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.lybvinci.learnopengl.utils.FileReader;
import com.lybvinci.learnopengl.utils.ShaderHelper;

public class ShaderProgram {
    protected static final String U_MATRIX = "u_Matrix";
    protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String U_COLOR = "u_Color";
    protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    protected final int program;

    protected ShaderProgram(Context context, int vertexShaderResourceId, int fragmentShaderResourceId) {
        program = ShaderHelper.buildProgram(FileReader.readRawFile(context, vertexShaderResourceId), FileReader.readRawFile(context, fragmentShaderResourceId));
    }

    public void useProgram() {
        GLES20.glUseProgram(program);
    }
}
