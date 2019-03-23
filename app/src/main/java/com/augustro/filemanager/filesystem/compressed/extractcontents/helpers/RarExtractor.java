

package com.augustro.filemanager.filesystem.compressed.extractcontents.helpers;

import android.content.Context;
import android.support.annotation.NonNull;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import com.augustro.filemanager.filesystem.FileUtil;
import com.augustro.filemanager.filesystem.compressed.CompressedHelper;
import com.augustro.filemanager.filesystem.compressed.extractcontents.Extractor;
import com.augustro.filemanager.utils.ServiceWatcherUtil;
import com.augustro.filemanager.utils.files.GenericCopyUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class RarExtractor extends Extractor {

    public RarExtractor(Context context, String filePath, String outputPath, OnUpdate listener) {
        super(context, filePath, outputPath, listener);
    }

    @Override
    protected void extractWithFilter(@NonNull Filter filter) throws IOException {
        try {
            long totalBytes = 0;
            Archive rarFile = new Archive(new File(filePath));
            ArrayList<FileHeader> arrayList = new ArrayList<>();

            // iterating archive elements to find file names that are to be extracted
            for (FileHeader header : rarFile.getFileHeaders()) {
                if(CompressedHelper.isEntryPathValid(header.getFileNameString())) {
                    if (filter.shouldExtract(header.getFileNameString(), header.isDirectory())) {
                        // header to be extracted is at least the entry path (may be more, when it is a directory)
                        arrayList.add(header);
                        totalBytes += header.getFullUnpackSize();
                    }
                } else {
                    invalidArchiveEntries.add(header.getFileNameString());
                }
            }

            listener.onStart(totalBytes, arrayList.get(0).getFileNameString());

            for (FileHeader entry : arrayList) {
                if (!listener.isCancelled()) {
                    listener.onUpdate(entry.getFileNameString());
                    extractEntry(context, rarFile, entry, outputPath);
                }
            }
            listener.onFinish();
        } catch (RarException e) {
            throw new IOException(e);
        }
    }

    private void extractEntry(@NonNull final Context context, Archive zipFile, FileHeader entry, String outputDir)
            throws RarException, IOException {
        String name = fixEntryName(entry.getFileNameString()).replaceAll("\\\\", CompressedHelper.SEPARATOR);
        File outputFile = new File(outputDir, name);

        if (!outputFile.getCanonicalPath().startsWith(outputDir)){
            throw new IOException("Incorrect RAR FileHeader path!");
        }

        if (entry.isDirectory()) {
            FileUtil.mkdir(outputFile, context);
            return;
        }

        if (!outputFile.getParentFile().exists()) {
            FileUtil.mkdir(outputFile.getParentFile(), context);
        }
        //	//Log.("Amaze", "Extracting: " + entry);
        BufferedInputStream inputStream = new BufferedInputStream(
                zipFile.getInputStream(entry));
        BufferedOutputStream outputStream = new BufferedOutputStream(
                FileUtil.getOutputStream(outputFile, context));
        try {
            int len;
            byte buf[] = new byte[GenericCopyUtil.DEFAULT_BUFFER_SIZE];
            while ((len = inputStream.read(buf)) != -1) {
                if (!listener.isCancelled()) {
                    outputStream.write(buf, 0, len);
                    ServiceWatcherUtil.position += len;
                } else break;
            }
        } finally {
            outputStream.close();
            inputStream.close();
        }
    }

}
