package com.scanlibrary;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

/**
 * Created by jhansi on 05/04/15.
 */
public class Utils {
	private static String PATH = "images";

	private Utils() {
	}

	public static Uri getUri(Context context, Bitmap bitmap) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = MediaStore.Images.Media.insertImage(
				context.getContentResolver(), bitmap, "Title", null);
		return Uri.parse(path);
	}

	public static Bitmap getBitmap(Context context, Uri uri) throws IOException {
		Bitmap bitmap = MediaStore.Images.Media.getBitmap(
				context.getContentResolver(), uri);
		return bitmap;
	}

	public static Uri getUri(String path) {
		 return Uri.fromFile(new File(path));
//		return Uri.parse(new File(path).toString());
	}

	public static void saveImageToGallery(Context context, Bitmap bmp) {
		// 首先保存图片
		File appDir = new File(Environment.getExternalStorageDirectory(),PATH);
		if (!appDir.exists()) {
			appDir.mkdir();
		}
		String fileName = System.currentTimeMillis() + ".jpg";
		File file = new File(appDir, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 其次把文件插入到系统图库
		try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(),
					file.getAbsolutePath(), fileName, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// 最后通知图库更新
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				Uri.fromFile(new File(appDir,fileName))));
		saveImageToSdcard(context, file.getAbsolutePath());
	}
	
	private static void saveImageToSdcard(Context context,String path) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // 判断SDK版本是不是4.4或者高于4.4
			String[] paths = new String[]{path};
			MediaScannerConnection.scanFile(context, paths, null, null);
		} else {
			context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri
					.parse("file://"+path)));
		}
	}
}