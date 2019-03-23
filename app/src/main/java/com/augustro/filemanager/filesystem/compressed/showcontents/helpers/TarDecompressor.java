
package com.augustro.filemanager.filesystem.compressed.showcontents.helpers;

import android.content.Context;

import com.augustro.filemanager.adapters.data.CompressedObjectParcelable;
import com.augustro.filemanager.asynchronous.asynctasks.compress.TarHelperTask;
import com.augustro.filemanager.filesystem.compressed.showcontents.Decompressor;
import com.augustro.filemanager.utils.OnAsyncTaskFinished;

import java.util.ArrayList;

public class TarDecompressor extends Decompressor {

    public TarDecompressor(Context context) {
        super(context);
    }

    @Override
    public TarHelperTask changePath(String path, boolean addGoBackItem, OnAsyncTaskFinished<ArrayList<CompressedObjectParcelable>> onFinish) {
        return new TarHelperTask(context, filePath, path, addGoBackItem, onFinish);
    }

}
