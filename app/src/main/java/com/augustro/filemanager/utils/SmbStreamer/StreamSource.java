package com.augustro.filemanager.utils.SmbStreamer;

/**
 * Created by Arpit on 06-07-2015.
 */
import android.webkit.MimeTypeMap;

import com.augustro.filemanager.utils.streams.RandomAccessStream;

import java.io.IOException;
import java.io.InputStream;

import jcifs.smb.SmbFile;

public class StreamSource extends RandomAccessStream {

    protected String mime;
    protected long fp;
    protected String name;
    protected SmbFile file;
    InputStream input;

    public StreamSource(SmbFile file,long l) {
        super(l);

        fp = 0;
        mime = MimeTypeMap.getFileExtensionFromUrl(file.getName());
        name = file.getName();
        this.file = file;
    }

    /**
     * You may notice a strange name for the smb input stream.
     * I made some modifications to the original one in the jcifs library for my needs,
     * but streaming required returning to the original one so I renamed it to "old".
     * However, I needed to specify a buffer size in the constructor. It looks now like this:
     *
     *
     public SmbFileInputStreamOld( SmbFile file, int readBuffer, int openFlags) throws SmbException, MalformedURLException, UnknownHostException {
         this.file = file;
         this.openFlags = SmbFile.O_RDONLY & 0xFFFF;
         this.access = (openFlags >>> 16) & 0xFFFF;
         if (file.type != SmbFile.TYPE_NAMED_PIPE) {
            file.open( openFlags, access, SmbFile.ATTR_NORMAL, 0 );
            this.openFlags &= ~(SmbFile.O_CREAT | SmbFile.O_TRUNC);
         } else {
             file.connect0();
         }
         readSize = readBuffer;
         fs = file.length();
     }
     *
     * Setting buffer size by properties didn't work for me so I created this constructor.
     * In the libs folder there is a library modified by me. If you want to use a stock one, you
     * have to set somehow the buffer size to be equal with http server's buffer size which is 8192.
     */
    public void open() throws IOException {
        try {
            input = file.getInputStream();//new SmbFileInputStream(file, bufferSize, 1);
            if(fp>0)
                input.skip(fp);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public int read() throws IOException {
        int read = input.read();
        if(read != -1) fp++;
        return read;
    }

    public int read(byte[] bytes, int start, int offs) throws IOException {
        int read =  input.read(bytes, start, offs);
        fp += read;
        return read;
    }

    @Override
    public void moveTo(long position) throws IllegalArgumentException {
        if(position < 0 || length() < position) {
            throw new IllegalArgumentException("Position out of the bounds of the file!");
        }

        fp = position;
    }

    @Override
    public void close() {
        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getMimeType(){
        return mime;
    }

    public String getName(){
        return name;
    }

    public SmbFile getFile(){
        return file;
    }

    @Override
    protected long getCurrentPosition() {
        return fp;
    }

}

