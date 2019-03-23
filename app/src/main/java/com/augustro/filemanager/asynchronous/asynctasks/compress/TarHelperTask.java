package com.augustro.filemanager.asynchronous.asynctasks.compress;

import android.content.Context;

import com.augustro.filemanager.adapters.data.CompressedObjectParcelable;
import com.augustro.filemanager.filesystem.compressed.CompressedHelper;
import com.augustro.filemanager.utils.OnAsyncTaskFinished;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.augustro.filemanager.filesystem.compressed.CompressedHelper.SEPARATOR;

public class TarHelperTask extends CompressedHelperTask {

    private WeakReference<Context> context;
    private String filePath, relativePath;

    public TarHelperTask(Context context, String filePath, String relativePath, boolean goBack,
                         OnAsyncTaskFinished<ArrayList<CompressedObjectParcelable>> l) {
        super(goBack, l);
        this.context = new WeakReference<>(context);
        this.filePath = filePath;
        this.relativePath = relativePath;
    }

    @Override
    void addElements(ArrayList<CompressedObjectParcelable> elements) {
        TarArchiveInputStream tarInputStream = null;
        try {
            tarInputStream = new TarArchiveInputStream(new FileInputStream(filePath));

            TarArchiveEntry entry;
            while ((entry = tarInputStream.getNextTarEntry()) != null) {
                String name = entry.getName();
                if (!CompressedHelper.isEntryPathValid(name)) {
                    continue;
                }
                if (name.endsWith(SEPARATOR)) name = name.substring(0, name.length() - 1);

                boolean isInBaseDir = relativePath.equals("") && !name.contains(SEPARATOR);
                boolean isInRelativeDir = name.contains(SEPARATOR)
                        && name.substring(0, name.lastIndexOf(SEPARATOR)).equals(relativePath);

                if (isInBaseDir || isInRelativeDir) {
                    elements.add(new CompressedObjectParcelable(entry.getName(),
                            entry.getLastModifiedDate().getTime(), entry.getSize(), entry.isDirectory()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
