package com.xman.downloadmanagedemo;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SDCardHelper {

	// 判断SD卡是否被挂载
	public static boolean isSDCardMounted() {
		// return Environment.getExternalStorageState().equals("mounted");
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	// 获取SD卡的根目录
	public static String getSDCardBaseDir() {
		if (isSDCardMounted()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return null;
	}

	// 获取SD卡的完整空间大小，返回MB
	public static long getSDCardSize() {
		if (isSDCardMounted()) {
			StatFs fs = new StatFs(getSDCardBaseDir());
			int count = fs.getBlockCount();
			int size = fs.getBlockSize();
			return count * size / 1024 / 1024;
		}
		return 0;
	}

	// 获取SD卡的剩余空间大小
	public static long getSDCardFreeSize() {
		if (isSDCardMounted()) {
			StatFs fs = new StatFs(getSDCardBaseDir());
			int count = fs.getFreeBlocks();
			int size = fs.getBlockSize();
			return count * size / 1024 / 1024;
		}
		return 0;
	}

	// 获取SD卡的可用空间大小
	public static long getSDCardAvailableSize() {
		if (isSDCardMounted()) {
			StatFs fs = new StatFs(getSDCardBaseDir());
			int count = fs.getAvailableBlocks();
			int size = fs.getBlockSize();
			return count * size / 1024 / 1024;
		}
		return 0;
	}

	// 往SD卡的公有目录下保存文件
	public static boolean saveFileToSDCardPublicDir(byte[] data, String type,
			String fileName) {
		BufferedOutputStream bos = null;
		if (isSDCardMounted()) {
			File file = Environment.getExternalStoragePublicDirectory(type);
			try {
				bos = new BufferedOutputStream(new FileOutputStream(new File(
						file, fileName)));
				bos.write(data);
				bos.flush();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					bos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	// 往SD卡的自定义目录下保存文件
	public static boolean saveFileToSDCardCustomDir(byte[] data, String dir,
			String fileName) {
		BufferedOutputStream bos = null;
		if (isSDCardMounted()) {
			File file = new File(getSDCardBaseDir() + File.separator + dir);
			if (!file.exists()) {
				file.mkdirs();// 递归创建自定义目录
			}
			try {
				bos = new BufferedOutputStream(new FileOutputStream(new File(
						file, fileName)));
				bos.write(data);
				bos.flush();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					bos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	// 往SD卡的私有Files目录下保存文件
	public static boolean saveFileToSDCardPrivateFilesDir(byte[] data,
			String type, String fileName, Context context) {
		BufferedOutputStream bos = null;
		if (isSDCardMounted()) {
			File file = context.getExternalFilesDir(type);
			try {
				bos = new BufferedOutputStream(new FileOutputStream(new File(
						file, fileName)));
				bos.write(data);
				bos.flush();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					bos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	// 往SD卡的私有Cache目录下保存文件
	public static boolean saveFileToSDCardPrivateCacheDir(byte[] data,
			String fileName, Context context) {
		BufferedOutputStream bos = null;
		if (isSDCardMounted()) {
			File file = context.getExternalCacheDir();
			try {
				bos = new BufferedOutputStream(new FileOutputStream(new File(
						file, fileName)));
				bos.write(data);
				bos.flush();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					bos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	// 从SD卡获取文件
	public static byte[] loadFileFromSDCard(String fileDir) {
		BufferedInputStream bis = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			bis = new BufferedInputStream(
					new FileInputStream(new File(fileDir)));
			byte[] buffer = new byte[8 * 1024];
			int c = 0;
			while ((c = bis.read(buffer)) != -1) {
				baos.write(buffer, 0, c);
				baos.flush();
			}
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				baos.close();
				if (bis != null) {
					bis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	// 获取SD卡公有目录的路径
	public static String getSDCardPublicDir(String type) {
		return Environment.getExternalStoragePublicDirectory(type).toString();
	}

	// 获取SD卡私有Cache目录的路径
	public static String getSDCardPrivateCacheDir(Context context) {
		return context.getExternalCacheDir().getAbsolutePath();
	}

	// 获取SD卡私有Files目录的路径
	public static String getSDCardPrivateFilesDir(Context context, String type) {
		return context.getExternalFilesDir(type).getAbsolutePath();
	}

	// 获取SD卡文件目录的路径
	public static String getSDCardPrivateFilesDirOne(Context context,
			String type) {
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator + type;
	}

	/*
	 * 获取sdcard绝对物理路径
	 */
	public static String getSDCardPath() {
		if (isSDCardMounted()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		} else {
			return null;
		}
	}

	/**
	 * 删除文件夹
	 * @param path
	 */
	public   static boolean deleteAllFilesOfDir(File path) {
		if (!path.exists()){
			return false;
		}
		if (path.isFile()) {
			path.delete();
			return true;
		}
		File[] files = path.listFiles();
		for (int i = 0; i < files.length; i++) {
			deleteAllFilesOfDir(files[i]);
		}
		path.delete();
		System.out.println("删除文件夹成功");
		return true;
	}
	/**
	 * 删除文件夹2
	 * @param path
	 */
	public   static void deleteAllFilesOfDir2(File path) {
		if (!path.exists()){
			return ;
		}
		if (path.isFile()) {
			path.delete();
			return ;
		}
		File[] files = path.listFiles();
		for (int i = 0; i < files.length; i++) {
			deleteAllFilesOfDir(files[i]);
		}
		path.delete();
		System.out.println("删除文件夹成功");
	}

}
