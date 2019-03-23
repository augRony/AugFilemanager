
package com.augustro.filemanager.filesystem.compressed.extractcontents.helpers;

import android.content.Context;
import android.support.annotation.NonNull;

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
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipExtractor extends Extractor {

    public ZipExtractor(Context context, String filePath, String outputPath, OnUpdate listener) {
        super(context, filePath, outputPath, listener);
    }

    @Override
    protected void extractWithFilter(@NonNull Filter filter) throws IOException {
        long totalBytes = 0;
        List<ZipEntry> entriesToExtract = new ArrayList<>();
        ZipFile zipfile = new ZipFile(filePath);

        // iterating archive elements to find file names that are to be extracted
        for (Enumeration<? extends ZipEntry> e = zipfile.entries(); e.hasMoreElements(); ) {
            ZipEntry zipEntry = e.nextElement();

            if(CompressedHelper.isEntryPathValid(zipEntry.getName())) {
                if (filter.shouldExtract(zipEntry.getName(), zipEntry.isDirectory())) {
                    entriesToExtract.add(zipEntry);
                    totalBytes += zipEntry.getSize();
                }
            } else {
                invalidArchiveEntries.add(zipEntry.getName());
            }
        }

        listener.onStart(totalBytes, entriesToExtract.get(0).getName());

        for (ZipEntry entry : entriesToExtract) {
            if (!listener.isCancelled()) {
                listener.onUpdate(entry.getName());
                extractEntry(context, zipfile, entry, outputPath);
            }
        }
        listener.onFinish();
    }
    
    /**
     * Method extracts {@link ZipEntry} from {@link ZipFile}
     *
     * @param zipFile   zip file from which entriesToExtract are to be extracted
     * @param entry     zip entry that is to be extracted
     * @param outputDir output directory
     */
    private void extractEntry(@NonNull final Context context, ZipFile zipFile, ZipEntry entry,
                              String outputDir) throws IOException {
        final File outputFile = new File(outputDir, fixEntryName(entry.getName()));

        if (!outputFile.getCanonicalPath().startsWith(outputDir)){
            throw new IOException("Incorrect ZipEntry path!");
        }

        if (entry.isDirectory()) {
            // zip entry is a directory, return after creating new directory
            FileUtil.mkdir(outputFile, context);
            return;
        }

        if (!outputFile.getParentFile().exists()) {
            // creating directory if not already exists
            FileUtil.mkdir(outputFile.getParentFile(), context);
        }

        BufferedInputStream inputStream = new BufferedInputStream(zipFile.getInputStream(entry));
        BufferedOutputStream outputStream = new BufferedOutputStream(FileUtil.getOutputStream(outputFile, context));

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
