package it.pgp.sevenzipjbindingandroidexample;

import android.util.Log;

import net.sf.sevenzipjbinding.ExtractAskMode;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IArchiveExtractCallback;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZipException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ArchiveExtractCallback implements IArchiveExtractCallback {

    public static final String TAG = "Extract";

    private final File destDir;
    private final IInArchive inArchive;
    private final boolean testMode;

    private OutputStream uos;

    public ArchiveExtractCallback(IInArchive inArchive, File destDir, boolean testMode) {
        this.inArchive = inArchive;
        this.destDir = destDir;
        this.testMode = testMode;
    }

    @Override
    public ISequentialOutStream getStream(int index, ExtractAskMode extractAskMode) throws SevenZipException {
        Log.i(TAG, "Extract archive, get stream: " + index + " to: " + extractAskMode);
        if(testMode) {
            return new SequentialOutStream();
        }

        String path = inArchive.getStringProperty(index, PropID.PATH);
        Boolean isDir = (Boolean) inArchive.getProperty(index, PropID.IS_FOLDER);
        File destPath = new File(destDir,path);

        try {
            if(isDir)
                destPath.mkdirs();
            else {
                destPath.getParentFile().mkdirs();
                destPath.createNewFile();
                uos = new FileOutputStream(destPath);
            }
            return bytes -> {
                try {
                    uos.write(bytes);
                    return bytes.length;
                } catch (IOException e) {
                    throw new SevenZipException(e);
                }
            };

        } catch (IOException e) {
            throw new SevenZipException(e);
        }
    }

    @Override
    public void prepareOperation(ExtractAskMode extractAskMode) throws SevenZipException {
        Log.i(TAG, "Extract archive, prepare to: " + extractAskMode);
    }

    @Override
    public void setOperationResult(ExtractOperationResult extractOperationResult) throws SevenZipException {
        Log.i(TAG, "Extract archive, completed with: " + extractOperationResult);
        if (extractOperationResult != ExtractOperationResult.OK) {
            throw new SevenZipException(extractOperationResult.toString());
        }
    }

    @Override
    public void setTotal(long total) throws SevenZipException {
        Log.i(TAG, "Extract archive, work planned: " + total);
    }

    @Override
    public void setCompleted(long complete) throws SevenZipException {
        Log.i(TAG, "Extract archive, work completed: " + complete);
    }

    public static class SequentialOutStream implements ISequentialOutStream {
        @Override
        public int write(byte[] data) throws SevenZipException {
            if (data == null || data.length == 0) {
                throw new SevenZipException("null data");
            }
            Log.i(TAG, "Data to write: " + data.length);
            return data.length;
        }
    }
}