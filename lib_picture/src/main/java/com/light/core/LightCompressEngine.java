package com.light.core;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.light.body.Light;
import com.light.compress.LightCompressCore;
import com.light.compress.NativeCompressCore;
import com.light.core.Utils.SimpleSizeCompute;
import com.light.core.listener.ICompressEngine;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by xiaoqi on 2017/11/21
 */

public class LightCompressEngine implements ICompressEngine {

    @Override
    public Bitmap compress2Bitmap(Bitmap bitmap, int width, int height, Bitmap.Config config) {
        String temp = Light.getInstance().getContext().getCacheDir().getAbsolutePath()
                + UUID.randomUUID().toString() + ".jpg";
        Bitmap resultBitmap = null;
        if (LightCompressCore.compressBitmap(bitmap, 100, temp)) {
            resultBitmap = compress2Bitmap(temp, width, height, config);
            new File(temp).delete();
        }
        return resultBitmap;
    }

    @Override
    public Bitmap compress2Bitmap(String imagePath, int width, int height, Bitmap.Config config) {
        long start = System.currentTimeMillis();
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, options);
            options.inPreferredConfig = config;
            options.inJustDecodeBounds = false;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                options.inPurgeable = true;
                options.inInputShareable = true;
            }
            options.inSampleSize = SimpleSizeCompute.computeSampleSize(options, Math.max(width, height),
                    width * height);
            return BitmapFactory.decodeFile(imagePath, options);
        } finally {
        }
    }

    @Override
    public Bitmap compress2Bitmap(int resId, int width, int height, Bitmap.Config config) {
        long start = System.currentTimeMillis();
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = config;
            BitmapFactory.decodeResource(Light.getInstance().getResources(), resId, options);
            options.inJustDecodeBounds = false;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                options.inPurgeable = true;
                options.inInputShareable = true;
            }
            options.inSampleSize = SimpleSizeCompute.computeSampleSize(options, Math.max(width, height),
                    width * height);
            InputStream is = Light.getInstance().getResources().openRawResource(resId);
            return BitmapFactory.decodeStream(is, null, options);
        } finally {
        }
    }

    @Override
    public Bitmap compress2Bitmap(byte[] bytes, int width, int height, Bitmap.Config config) {
        long start = System.currentTimeMillis();
        InputStream input = null;
        try {
            input = new ByteArrayInputStream(bytes);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, options);
            try {
                input.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = config;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                options.inPurgeable = true;
                options.inInputShareable = true;
            }
            options.inSampleSize = SimpleSizeCompute.computeSampleSize(options, Math.max(width, height),
                    width * height);
            input = new ByteArrayInputStream(bytes);
            return BitmapFactory.decodeStream(input, null, options);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //只会压缩文件大小，不会压缩bitmap大小
    @Override
    public boolean compress2File(Bitmap bitmap, String outputPath, int quality) {
        if (bitmap.hasAlpha()) {
            return NativeCompressCore.compress(bitmap, outputPath, quality, Bitmap.CompressFormat.JPEG);
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                return LightCompressCore.compressBitmap(bitmap, quality, outputPath);
            } else {
                return NativeCompressCore.compress(bitmap, outputPath, 100, Bitmap.CompressFormat.JPEG);
            }
        }
    }

}