package com.augustro.filemanager.filesystem.compressed.showcontents;

import android.content.Context;
import android.content.Intent;

import com.augustro.filemanager.adapters.data.CompressedObjectParcelable;
import com.augustro.filemanager.asynchronous.asynctasks.compress.CompressedHelperTask;
import com.augustro.filemanager.asynchronous.services.ExtractService;
import com.augustro.filemanager.utils.OnAsyncTaskFinished;
import com.augustro.filemanager.utils.ServiceWatcherUtil;

import java.util.ArrayList;

public abstract class Decompressor {

    protected Context context;
    protected String filePath;

    public Decompressor(Context context) {
        this.context = context;
    }

    public void setFilePath(String path) {
        filePath = path;
    }

    public abstract CompressedHelperTask changePath(String path, boolean addGoBackItem,
                                                    OnAsyncTaskFinished<ArrayList<CompressedObjectParcelable>> onFinish);

    /**
     * Decompress a file somewhere
     */
    public final void decompress(String whereToDecompress) {
        Intent intent = new Intent(context, ExtractService.class);
        intent.putExtra(ExtractService.KEY_PATH_ZIP, filePath);
        intent.putExtra(ExtractService.KEY_ENTRIES_ZIP, new String[0]);
        intent.putExtra(ExtractService.KEY_PATH_EXTRACT, whereToDecompress);
        ServiceWatcherUtil.runService(context, intent);
    }

    /**
     * Decompress files or dirs inside the compressed file.
     * @param subDirectories separator is "/", ended with "/" if it is a directory, does not if it's a file
     */
    public final void decompress(String whereToDecompress, String[] subDirectories) {
        for (int i = 0; i < subDirectories.length; i++) {
            subDirectories[i] = realRelativeDirectory(subDirectories[i]);
        }

        Intent intent = new Intent(context, ExtractService.class);
        intent.putExtra(ExtractService.KEY_PATH_ZIP, filePath);
        intent.putExtra(ExtractService.KEY_ENTRIES_ZIP, subDirectories);
        intent.putExtra(ExtractService.KEY_PATH_EXTRACT, whereToDecompress);
        ServiceWatcherUtil.runService(context, intent);
    }

    /**
     * Get the real relative directory path (useful if you converted the separator or something)
     */
    protected String realRelativeDirectory(String dir) {
        return dir;
    }

}
