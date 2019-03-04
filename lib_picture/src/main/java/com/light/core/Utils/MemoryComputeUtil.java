package com.light.core.Utils;

import android.graphics.Bitmap;

import com.light.body.Light;

/**
 * Created by xiaoqi on 2017/11/22
 */

public class MemoryComputeUtil {

	//单位kb
	public static int getMemorySize(Bitmap bitmap){
		Bitmap.Config config = bitmap.getConfig();
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int totalSize;
		if(config.equals(Bitmap.Config.ALPHA_8)){
			totalSize = width * height;
		}else if(config.equals(Bitmap.Config.ARGB_4444)){
			totalSize = width * height * 2;
		}else if(config.equals(Bitmap.Config.RGB_565)){
			totalSize = width * height * 2;
		}else {
			//ARGB_8888
			totalSize = width * height * 4;
		}
		return totalSize / 1024;
	}

	public static int getMemorySize2(Bitmap bitmap){
		return bitmap.getByteCount();
	}
}
