

package com.augustro.filemanager.filesystem.compressed.showcontents.helpers;

import android.content.Context;

import com.github.junrar.rarfile.FileHeader;
import com.augustro.filemanager.adapters.data.CompressedObjectParcelable;
import com.augustro.filemanager.asynchronous.asynctasks.compress.RarHelperTask;
import com.augustro.filemanager.filesystem.compressed.showcontents.Decompressor;
import com.augustro.filemanager.utils.OnAsyncTaskFinished;

import java.util.ArrayList;

import static com.augustro.filemanager.filesystem.compressed.CompressedHelper.SEPARATOR;

public class RarDecompressor extends Decompressor {

    public RarDecompressor(Context context) {
        super(context);
    }

    @Override
    public RarHelperTask changePath(String path, boolean addGoBackItem,
                                    OnAsyncTaskFinished<ArrayList<CompressedObjectParcelable>> onFinish) {
        return new RarHelperTask(context, filePath, path, addGoBackItem, onFinish);
    }

    public static String convertName(FileHeader file) {
        String name = file.getFileNameString().replace('\\', '/');

        if(file.isDirectory()) return name + SEPARATOR;
        else return name;
    }

    @Override
    protected String realRelativeDirectory(String dir) {
        if(dir.endsWith(SEPARATOR)) dir = dir.substring(0, dir.length()-1);
        return dir.replace(SEPARATOR.toCharArray()[0], '\\');
    }

}
