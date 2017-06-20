package com.xman.downloadmanagedemo;

import com.xman.downloadmanagedemo.utils.SDCardHelper;

import java.io.File;

/**
 * Created by nieyunlong on 17/6/12.
 */

public class Config {

    /**
     * 基础包
     */
    public static final String BASE_DIR="download_manage_";
    /**
     * 下载基础路径
     */
    public static final String DOWNLOAD_BASE= SDCardHelper.getSDCardBaseDir()  + File.separator;
}
