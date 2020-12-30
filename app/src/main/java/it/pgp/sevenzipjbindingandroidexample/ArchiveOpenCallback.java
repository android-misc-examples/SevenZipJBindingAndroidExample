package it.pgp.sevenzipjbindingandroidexample;

import android.util.Log;

import net.sf.sevenzipjbinding.IArchiveOpenCallback;

import static it.pgp.sevenzipjbindingandroidexample.ArchiveExtractCallback.TAG;

public class ArchiveOpenCallback implements IArchiveOpenCallback {
    @Override
    public void setTotal(Long files, Long bytes) {
        Log.i(TAG, "Archive open, total work: " + files + " files, " + bytes + " bytes");
    }

    @Override
    public void setCompleted(Long files, Long bytes) {
        Log.i(TAG, "Archive open, completed: " + files + " files, " + bytes + " bytes");
    }
}
