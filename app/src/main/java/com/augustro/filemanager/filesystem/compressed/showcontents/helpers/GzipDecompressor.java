

package com.augustro.filemanager.filesystem.compressed.showcontents.helpers;

import android.content.Context;

import com.augustro.filemanager.adapters.data.CompressedObjectParcelable;
import com.augustro.filemanager.asynchronous.asynctasks.compress.CompressedHelperTask;
import com.augustro.filemanager.asynchronous.asynctasks.compress.GzipHelperTask;
import com.augustro.filemanager.filesystem.compressed.showcontents.Decompressor;
import com.augustro.filemanager.utils.OnAsyncTaskFinished;

import java.util.ArrayList;

public class GzipDecompressor extends Decompressor {

    public GzipDecompressor(Context context) {
        super(context);
    }

    @Override
    public CompressedHelperTask changePath(String path, boolean addGoBackItem,
                                           OnAsyncTaskFinished<ArrayList<CompressedObjectParcelable>> onFinish) {
        return new GzipHelperTask(context, filePath, path, addGoBackItem, onFinish);
    }

}
