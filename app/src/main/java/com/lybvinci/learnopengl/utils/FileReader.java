package com.lybvinci.learnopengl.utils;

import android.content.Context;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class FileReader {

    @Nullable
    public static String readRawFile(Context context, int resId) {
        InputStream inputStream = null;
        try {
            inputStream = context.getResources().openRawResource(resId);
            InputStreamReader ir = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(ir);
            String res = "";
            String line;
            while ((line = br.readLine()) != null) {
                res += line;
            }
            return res;
        } catch (IOException e) {

        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
