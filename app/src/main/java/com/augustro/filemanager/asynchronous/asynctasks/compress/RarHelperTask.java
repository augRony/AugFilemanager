

package com.augustro.filemanager.asynchronous.asynctasks.compress;

import android.content.Context;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import com.augustro.filemanager.adapters.data.CompressedObjectParcelable;
import com.augustro.filemanager.filesystem.compressed.CompressedHelper;
import com.augustro.filemanager.filesystem.compressed.showcontents.helpers.RarDecompressor;
import com.augustro.filemanager.utils.OnAsyncTaskFinished;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class RarHelperTask extends CompressedHelperTask {

    private WeakReference<Context> context;
    private String fileLocation;
    private String relativeDirectory;

    public RarHelperTask(Context context, String realFileDirectory, String dir, boolean goBack,
                         OnAsyncTaskFinished<ArrayList<CompressedObjectParcelable>> l) {
        super(goBack, l);
        this.context = new WeakReference<>(context);
        fileLocation = realFileDirectory;
        relativeDirectory = dir;
    }

    @Override
    void addElements(ArrayList<CompressedObjectParcelable> elements) {
        try {
            Archive zipfile = new Archive(new File(fileLocation));
            String relativeDirDiffSeparator = relativeDirectory.replace(CompressedHelper.SEPARATOR, "\\");

            for (FileHeader rarArchive : zipfile.getFileHeaders()) {
                String name = rarArchive.getFileNameString();//This uses \ as separator, not /
                if (!CompressedHelper.isEntryPathValid(name)) {
                    continue;
                }
                boolean isInBaseDir = (relativeDirDiffSeparator == null || relativeDirDiffSeparator.equals("")) && !name.contains("\\");
                boolean isInRelativeDir = relativeDirDiffSeparator != null && name.contains("\\")
                        && name.substring(0, name.lastIndexOf("\\")).equals(relativeDirDiffSeparator);

                if (isInBaseDir || isInRelativeDir) {
                    elements.add(new CompressedObjectParcelable(RarDecompressor.convertName(rarArchive), 0, rarArchive.getDataSize(), rarArchive.isDirectory()));
                }
            }
        } catch (RarException | IOException e) {
            e.printStackTrace();
        }
    }

}

