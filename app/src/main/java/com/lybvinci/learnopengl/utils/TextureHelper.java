package com.lybvinci.learnopengl.utils;

import static android.opengl.GLES20.GL_TEXTURE_2D;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public final class TextureHelper {
    private static final String TAG = "TextureHelper";
    public static int loadTexture(Context context, int resId) {
        final int[] textureObjectId = new int[1];
        GLES20.glGenTextures(1, textureObjectId, 0);
        if (textureObjectId[0] == 0) {
            Log.w(TAG, "Could not generate a texture.");
            return 0;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
        if (null == bitmap) {
            Log.w(TAG, "Resource id:" + resId + " couldn't be decoded.");
            GLES20.glDeleteTextures(1, textureObjectId, 0);
            return 0;
        }
        GLES20.glBindTexture(GL_TEXTURE_2D, textureObjectId[0]);
        GLES20.glTexParameteri(GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
        GLES20.glTexParameteri(GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
        GLES20.glGenerateMipmap(GL_TEXTURE_2D);
        GLES20.glBindTexture(GL_TEXTURE_2D, 0);
        return textureObjectId[0];

    }
}
