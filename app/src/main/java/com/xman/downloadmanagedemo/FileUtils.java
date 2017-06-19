package com.xman.downloadmanagedemo;

import java.io.File;
import java.io.IOException;

/**
 * Created by nieyunlong
 * on 2016/5/25.
 */
public class FileUtils {
    public static String downloadPath(String dir,String saveName){
        File file=new File(dir);
        if(!file.exists()){
            file.mkdirs();
        }
        File fileNew=new File(dir+File.separator+saveName);
        if(!fileNew.exists()){
            try {
                fileNew.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.e("--->创建文件失败"+e.getMessage());
            }
        }
        return fileNew.getAbsolutePath();
    }
    public static String getSavePath(String dirName){
        return SDCardHelper.getSDCardBaseDir()+File.separator+dirName;
    }
}
