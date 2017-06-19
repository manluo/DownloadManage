package com.xman.downloadmanagedemo;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nieyunlong on 17/6/12.
 * 任务
 */

public class Misson implements Runnable,Parcelable{

    public static int ID = 0;
    private final int MissionID ; //用来通知
    private final static boolean IS_DEBUG = true;
    private final static String TAG = Misson.class.getSimpleName();
    /*下载的url*/
    private String mDownloadUrl;
    /*已经下载的大小,实现断点续传*/
    private long mDownloadedSize;
    /*文件的大小*/
    private long mFileSize;
    /*保存的文件夹*/
    private String mSaveDir;
    /*保存的文件的名称，别忘了后缀*/
    private String mSaveName;
    /*当前下载的进度*/
    private int progressCurrent = 0;
    private List<MissonListener<Misson>> listObserver;
    /*下载通知ui的状态,初始状态等待下载*/
    private DownloadUiStatus downloadUiStatus = DownloadUiStatus.DOWNLOAD_WAIT;
    private final Object o = new Object();
    /*是否暂停*/
    private boolean isPause;
    /**
     * 打断线程
     */
    private boolean isCancel;

    private boolean isDone;


    public Misson(String downloadUrl, long downloadedSize, long fileSize, String saveDir, String saveName) {
        MissionID = ID++;
        this.mDownloadUrl = downloadUrl;
        this.mDownloadedSize = downloadedSize;
        this.mFileSize = fileSize;
        this.mSaveDir = saveDir;
        this.mSaveName = saveName;
        listObserver = new ArrayList<>();
    }

    @Override
    public void run() {
        notifyStartMisson();
        String currentName = Thread.currentThread().getName();
        InputStream in = null;
        RandomAccessFile randomAccessFile = null;
        HttpURLConnection httpUrlConnection = null;
        try {
            LogUtils.e("---->正在等待连接download");
            setDownloadUiStatus(DownloadUiStatus.DOWNLOAD_READ); //准备下载
            URL uri = new URL(mDownloadUrl);
            httpUrlConnection = (HttpURLConnection) uri.openConnection();
            httpUrlConnection.setRequestMethod("GET");
            httpUrlConnection.setConnectTimeout(50000);
            httpUrlConnection.setDoInput(true);
            if (mDownloadedSize > 0) {
                //数据库保存的数据 1.可能服务器把文件删除掉了 需要重新下载
                String originFile = Config.DOWNLOAD_BASE + getSaveDir() + File.separator + getSaveName();
                File file = new File(originFile);
                if (file.exists()) {
                    FileInputStream fis = new FileInputStream(file);
                    mDownloadedSize = fis.available();
                    LogUtils.e("---->用户数据" + fis.available());
                } else {
                    mDownloadedSize = 0;
                }
            }
            httpUrlConnection.setRequestProperty("Range", "bytes=" + mDownloadedSize + "-");
            //当文件没有开始下载，记录文件大小
            if (mFileSize <= 0) {
                mFileSize = httpUrlConnection.getContentLength();
            }
            if (mDownloadedSize >= mFileSize) {
                setDownloadUiStatus(DownloadUiStatus.DOWNLOAD_SUCCESS);
                printLog("---->下载成功了");
                notifySuccess();
                return;
            }
            in = httpUrlConnection.getInputStream();
            setDownloadUiStatus(DownloadUiStatus.DOWNLOADING); //正在下载
            String accessPath = FileUtils.downloadPath(Config.DOWNLOAD_BASE + getSaveDir(), getSaveName());
            randomAccessFile = new RandomAccessFile(accessPath, "rw");
            randomAccessFile.seek(mDownloadedSize);
            byte[] bytes = new byte[1024];
            int count;
            while (!isCancel() && (count = in.read(bytes, 0, 1024)) != -1) { //读   到头是-1
                if (count != 0) {
                    randomAccessFile.write(bytes, 0, count);
                    mDownloadedSize += count;
                    int progress = getPercentage(mDownloadedSize, mFileSize);
                    if (progressCurrent != progress) { //不采用没%2==0 方式进行通知 这样用户看起来进度很怪 数据库更新操作可以以这种方式进行保存进度 这样有一个缓存
                        progressCurrent = progress;
                        notifyPercentageChange();
                    }
                }
            }
            if (isCancel()) { //被打断
                printLog("--->被打断" + currentName);
                notifyCancel();
                return;
            }
            setDownloadUiStatus(DownloadUiStatus.DOWNLOAD_SUCCESS);
            printLog("---->下载成功了--->currentName" + currentName);
            notifySuccess();

        } catch (IOException e) {
            e.printStackTrace();
            printLog(e.getMessage());
            setDownloadUiStatus(DownloadUiStatus.DOWNLOAD_FAILED);
            notifyFailed();
        } finally {
            printLog("--->关闭finally");
            try {
                isDone = true;
                if (in != null) {
                    in.close();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 打断线程
     * 相当于暂停下载 ===>以便资源再利用
     */
    public void cancel() {
        if (isDone())
            return;
        isCancel = true;
        setDownloadUiStatus(DownloadUiStatus.DOWNLOAD_PAUSE);
        notifyPause();
    }

    //通知进度改变
    private final void notifyPercentageChange() {
        if (listObserver != null && listObserver.size() != 0) {
            for (MissonListener<Misson> l : listObserver) {
                l.getProgress(this);
            }
        }
    }

    private final void notifyCancel() {
        if (listObserver != null && listObserver.size() != 0) {
            for (MissonListener<Misson> l : listObserver) {
                l.onCancel(this);
            }
        }
    }

    private final void notifyPause() {
        if (listObserver != null && listObserver.size() != 0) {
            for (MissonListener<Misson> l : listObserver) {
                l.onPause(this);
            }
        }
    }

    private final void notifySuccess() {
        if (listObserver != null && listObserver.size() != 0) {
            for (MissonListener<Misson> l : listObserver) {
                l.onSuccess(this);
            }
        }
    }

    private final void notifyFailed() {
        if (listObserver != null && listObserver.size() != 0) {
            for (MissonListener<Misson> l : listObserver) {
                l.onFailed(this);
            }
        }
    }

    /**
     * 通知添加任务了
     */
    public final void notifyAddMisson() {
        if (listObserver != null && listObserver.size() != 0) {
            for (MissonListener<Misson> l : listObserver) {
                l.addMisson(this);
            }
        }
    }

    public final void notifyStartMisson() {
        if (listObserver != null && listObserver.size() != 0) {
            for (MissonListener<Misson> l : listObserver) {
                l.onStart(this);
            }
        }
    }

    public final int getPercentage(long mDownloaded, long mFileSize) {
        if (mFileSize == 0) {
            return 0;
        } else {
            return (int) (mDownloaded * 100.0f / mFileSize);
        }
    }

    public final int getPercentage() {
        if (mFileSize == 0) {
            return 0;
        } else {
            return (int) (mDownloadedSize * 100.0f / mFileSize);
        }
    }


    public long getmDownloadedSize() {
        return mDownloadedSize;
    }

    public void setmDownloadedSize(long mDownloadedSize) {
        this.mDownloadedSize = mDownloadedSize;
    }

    public long getmFileSize() {
        return mFileSize;
    }

    public void setmFileSize(long mFileSize) {
        this.mFileSize = mFileSize;
    }

    /**
     * 打印日志
     *
     * @param message
     */
    private void printLog(String message) {
        if (IS_DEBUG) {
            Log.e(TAG, message);
        }
    }

    public DownloadUiStatus getDownloadUiStatus() {
        return downloadUiStatus;
    }

    public void setDownloadUiStatus(DownloadUiStatus downloadUiStatus) {
        this.downloadUiStatus = downloadUiStatus;
    }

    public String getSaveDir() {
        return mSaveDir;
    }

    public String getSaveName() {
        return mSaveName;
    }

    public int getProgressCurrent() {
        return progressCurrent;
    }


    public boolean isCancel() {
        return isCancel;
    }

    public void setCancel(boolean cancel) {
        isCancel = cancel;
    }

    public String getmDownloadUrl() {
        return mDownloadUrl;
    }

    public void setmDownloadUrl(String mDownloadUrl) {
        this.mDownloadUrl = mDownloadUrl;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public int getMissionID() {
        return MissionID;
    }



    public void registerMissonListener(MissonListener<Misson> listener) {
        if (listener != null) {
            if (!listObserver.contains(listener)) {
                LogUtils.e("---->不包括这个listener" + listener);
                listObserver.add(listener);
            }

        }
    }

    public void removeMissonListener(MissonListener<Misson> listener) {
        listObserver.remove(listener);
    }


    public interface MissonListener<T extends Misson> {

        /**
         * 用来添加任务缓存 @see ThreadPoolManage.class
         *
         * @param misson
         */
        void addMisson(T misson);

        void onStart(T misson);  //开始下载监听

        void getProgress(T misson); //更新进度 以及数据库存储

        void onSuccess(T misson);

        void onFailed(T misson);

        void onPause(T misson);

        void onCancel(T misson);

    }

    @Override
    public String toString() {
        return "Misson{" +
                "mDownloadUrl='" + mDownloadUrl + '\'' +
                ", mDownloadedSize=" + mDownloadedSize +
                ", mFileSize=" + mFileSize +
                ", mSaveDir='" + mSaveDir + '\'' +
                ", mSaveName='" + mSaveName + '\'' +
                ", progressCurrent=" + progressCurrent +
                ", listObserver=" + listObserver +
                ", downloadUiStatus=" + downloadUiStatus +
                ", o=" + o +
                ", isPause=" + isPause +
                ", isCancel=" + isCancel +
                ", isDone=" + isDone +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.MissionID);
        dest.writeString(this.mDownloadUrl);
        dest.writeLong(this.mDownloadedSize);
        dest.writeLong(this.mFileSize);
        dest.writeString(this.mSaveDir);
        dest.writeString(this.mSaveName);
        dest.writeInt(this.progressCurrent);
        dest.writeInt(this.downloadUiStatus == null ? -1 : this.downloadUiStatus.ordinal());
        dest.writeByte(this.isPause ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCancel ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isDone ? (byte) 1 : (byte) 0);
    }

    protected Misson(Parcel in) {
        this.MissionID = in.readInt();
        this.mDownloadUrl = in.readString();
        this.mDownloadedSize = in.readLong();
        this.mFileSize = in.readLong();
        this.mSaveDir = in.readString();
        this.mSaveName = in.readString();
        this.progressCurrent = in.readInt();
        int tmpDownloadUiStatus = in.readInt();
        this.downloadUiStatus = tmpDownloadUiStatus == -1 ? null : DownloadUiStatus.values()[tmpDownloadUiStatus];
        this.isPause = in.readByte() != 0;
        this.isCancel = in.readByte() != 0;
        this.isDone = in.readByte() != 0;
    }

    public static final Creator<Misson> CREATOR = new Creator<Misson>() {
        @Override
        public Misson createFromParcel(Parcel source) {
            return new Misson(source);
        }

        @Override
        public Misson[] newArray(int size) {
            return new Misson[size];
        }
    };
}
