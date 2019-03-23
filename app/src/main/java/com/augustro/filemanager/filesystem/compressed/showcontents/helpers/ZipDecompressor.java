
package com.augustro.filemanager.filesystem.compressed.showcontents.helpers;

import android.content.Context;

import com.augustro.filemanager.adapters.data.CompressedObjectParcelable;
import com.augustro.filemanager.asynchronous.asynctasks.compress.ZipHelperTask;
import com.augustro.filemanager.filesystem.compressed.showcontents.Decompressor;
import com.augustro.filemanager.utils.OnAsyncTaskFinished;

import java.util.ArrayList;

public class ZipDecompressor extends Decompressor {

    public ZipDecompressor(Context context) {
        super(context);
    }

    @Override
    public ZipHelperTask changePath(String path, boolean addGoBackItem,
                                    OnAsyncTaskFinished<ArrayList<CompressedObjectParcelable>> onFinish) {
        return new ZipHelperTask(context, filePath, path, addGoBackItem, onFinish);
    }

}
