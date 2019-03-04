package com.light.core.listener;

import android.graphics.Bitmap;

/**
 * Created by xiaoqi on 2017/11/22
 */

public interface ICompressEngine {

	Bitmap compress2Bitmap(Bitmap bitmap, int width, int height, Bitmap.Config config);

	Bitmap compress2Bitmap(String imagePath, int width, int height, Bitmap.Config config);

	Bitmap compress2Bitmap(int resId, int width, int height, Bitmap.Config config);

	Bitmap compress2Bitmap(byte[] bytes, int width, int height, Bitmap.Config config);

	boolean compress2File(Bitmap bitmap, String outputPath, int quality);

}
