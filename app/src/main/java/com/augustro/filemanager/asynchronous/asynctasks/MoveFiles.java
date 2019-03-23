package com.augustro.filemanager.asynchronous.asynctasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.augustro.filemanager.R;
import com.cloudrail.si.interfaces.CloudStorage;
import com.augustro.filemanager.activities.MainActivity;
import com.augustro.filemanager.asynchronous.services.CopyService;
import com.augustro.filemanager.database.CryptHandler;
import com.augustro.filemanager.database.models.EncryptedEntry;
import com.augustro.filemanager.exceptions.ShellNotRunningException;
import com.augustro.filemanager.filesystem.HybridFile;
import com.augustro.filemanager.filesystem.HybridFileParcelable;
import com.augustro.filemanager.fragments.MainFragment;
import com.augustro.filemanager.utils.DataUtils;
import com.augustro.filemanager.utils.OpenMode;
import com.augustro.filemanager.utils.RootUtils;
import com.augustro.filemanager.utils.ServiceWatcherUtil;
import com.augustro.filemanager.utils.application.AppConfig;
import com.augustro.filemanager.utils.cloud.CloudUtil;
import com.augustro.filemanager.utils.files.CryptUtil;
import com.augustro.filemanager.utils.files.FileUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

/**
 * AsyncTask that moves files from source to destination by trying to rename files first,
 * if they're in the same filesystem, else starting the copy service.
 * Be advised - do not start this AsyncTask directly but use {@link PrepareCopyTask} instead
 */
public class MoveFiles extends AsyncTask<ArrayList<String>, String, Boolean> {

    private ArrayList<ArrayList<HybridFileParcelable>> files;
    private MainFragment mainFrag;
    private ArrayList<String> paths;
    private Context context;
    private OpenMode mode;
    private long totalBytes = 0l;
    private long destinationSize = 0l;

    public MoveFiles(ArrayList<ArrayList<HybridFileParcelable>> files, MainFragment ma, Context context, OpenMode mode) {
        mainFrag = ma;
        this.context = context;
        this.files = files;
        this.mode = mode;
    }

    @Override
    protected Boolean doInBackground(ArrayList<String>... strings) {
        paths = strings[0];

        if (files.size() == 0) return true;

        for (ArrayList<HybridFileParcelable> filesCurrent : files) {
            totalBytes += FileUtils.getTotalBytes(filesCurrent, context);
        }
        HybridFile destination = new HybridFile(mode, paths.get(0));
        destinationSize = destination.getUsableSpace();

        switch (mode) {
            case SMB:
                for (int i = 0; i < paths.size(); i++) {
                    for (HybridFileParcelable f : files.get(i)) {
                        try {
                            SmbFile source = new SmbFile(f.getPath());
                            SmbFile dest = new SmbFile(paths.get(i) + "/" + f.getName());
                            source.renameTo(dest);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            return false;
                        } catch (SmbException e) {
                            e.printStackTrace();
                            return false;
                        }
                    }
                }
                break;
            case FILE:
                for (int i = 0; i < paths.size(); i++) {
                    for (HybridFileParcelable f : files.get(i)) {
                        File dest = new File(paths.get(i) + "/" + f.getName());
                        File source = new File(f.getPath());
                        if (!source.renameTo(dest)) {

                            // check if we have root
                            if (mainFrag.getMainActivity().isRootExplorer()) {
                                try {
                                    if (!RootUtils.rename(f.getPath(), paths.get(i) + "/" + f.getName()))
                                        return false;
                                } catch (ShellNotRunningException e) {
                                    e.printStackTrace();
                                    return false;
                                }
                            } else return false;
                        }
                    }
                }
                break;
            case DROPBOX:
            case BOX:
            case ONEDRIVE:
            case GDRIVE:
                for (int i=0; i<paths.size(); i++) {
                    for (HybridFileParcelable baseFile : files.get(i)) {

                        DataUtils dataUtils = DataUtils.getInstance();

                        CloudStorage cloudStorage = dataUtils.getAccount(mode);
                        String targetPath = paths.get(i) + "/" + baseFile.getName();
                        if (baseFile.getMode() == mode) {
                            // source and target both in same filesystem, use API method
                            try {

                                cloudStorage.move(CloudUtil.stripPath(mode, baseFile.getPath()),
                                        CloudUtil.stripPath(mode, targetPath));
                            } catch (Exception e) {
                                return false;
                            }
                        }  else {
                            // not in same filesystem, execute service
                            return false;
                        }
                    }
                }
            default:
                return false;
        }

        return true;
    }

    @Override
    public void onPostExecute(Boolean movedCorrectly) {
        if (movedCorrectly) {
            if (mainFrag != null && mainFrag.getCurrentPath().equals(paths.get(0))) {
                // mainFrag.updateList();
                Intent intent = new Intent(MainActivity.KEY_INTENT_LOAD_LIST);

                intent.putExtra(MainActivity.KEY_INTENT_LOAD_LIST_FILE, paths.get(0));
                context.sendBroadcast(intent);
            }

            for (int i = 0; i < paths.size(); i++) {
                for (HybridFileParcelable f : files.get(i)) {
                    FileUtils.scanFile(f.getFile(), context);
                    FileUtils.scanFile(new File(paths.get(i) + "/" + f.getName()), context);
                }
            }

            // updating encrypted db entry if any encrypted file was moved
            AppConfig.runInBackground(() -> {
                for (int i = 0; i < paths.size(); i++) {
                    for (HybridFileParcelable file : files.get(i)) {
                        if (file.getName().endsWith(CryptUtil.CRYPT_EXTENSION)) {
                            try {

                                CryptHandler cryptHandler = new CryptHandler(context);
                                EncryptedEntry oldEntry = cryptHandler.findEntry(file.getPath());
                                EncryptedEntry newEntry = new EncryptedEntry();
                                newEntry.setId(oldEntry.getId());
                                newEntry.setPassword(oldEntry.getPassword());
                                newEntry.setPath(paths.get(i) + "/" + file.getName());
                                cryptHandler.updateEntry(oldEntry, newEntry);
                            } catch (Exception e) {
                                e.printStackTrace();
                                // couldn't change the entry, leave it alone
                            }
                        }
                    }
                }
            });

        } else {

            if (destinationSize < totalBytes) {
                // destination don't have enough space; return
                Toast.makeText(context, context.getResources().getString(R.string.in_safe), Toast.LENGTH_LONG).show();
                return;
            }

            for (int i = 0; i < paths.size(); i++) {
                Intent intent = new Intent(context, CopyService.class);
                intent.putExtra(CopyService.TAG_COPY_SOURCES, files.get(i));
                intent.putExtra(CopyService.TAG_COPY_TARGET, paths.get(i));
                intent.putExtra(CopyService.TAG_COPY_MOVE, true);
                intent.putExtra(CopyService.TAG_COPY_OPEN_MODE, mode.ordinal());

                ServiceWatcherUtil.runService(context, intent);
            }
        }
    }

    /**
     * Maintains a list of filesystems supporting the move/rename implementation.
     * Please update to return your {@link OpenMode} type if it is supported here
     * @return
     */
    public static HashSet<OpenMode> getOperationSupportedFileSystem() {
        HashSet<OpenMode> hashSet = new HashSet<>();
        hashSet.add(OpenMode.SMB);
        hashSet.add(OpenMode.FILE);
        hashSet.add(OpenMode.DROPBOX);
        hashSet.add(OpenMode.BOX);
        hashSet.add(OpenMode.GDRIVE);
        hashSet.add(OpenMode.ONEDRIVE);
        return hashSet;
    }
}
