package it.pgp.sevenzipjbindingandroidexample;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IInStream;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class MainActivity extends Activity {

    private static final String TAG = "Version";

    public static void testExtract(File file, File destDir, boolean testOnly, ArchiveFormat format) {
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            RandomAccessFileInStream inStream = new RandomAccessFileInStream(randomAccessFile);
            ArchiveOpenCallback callback = new ArchiveOpenCallback();
            IInArchive inArchive = SevenZip.openInArchive(format, inStream, callback);

            ArchiveExtractCallback extractCallback = new ArchiveExtractCallback(inArchive, destDir, testOnly);
            inArchive.extract(null, testOnly, extractCallback);

            inArchive.close();
            inStream.close();
            Log.i(TAG, "Extract OK");
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (SevenZipException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static void testExtractRarMultiVolume(File firstVolumePath, File destDir, boolean isTestMode) {

        ArchiveOpenVolumeCallback archiveOpenVolumeCallback = null;
        IInArchive inArchive = null;
        try {

            archiveOpenVolumeCallback = new ArchiveOpenVolumeCallback();
            IInStream inStream = archiveOpenVolumeCallback.getStream(firstVolumePath.getAbsolutePath());
            inArchive = SevenZip.openInArchive(ArchiveFormat.RAR5, inStream, //
                    archiveOpenVolumeCallback);

            System.out.println("   Size   | Compr.Sz. | Filename");
            System.out.println("----------+-----------+---------");
            int itemCount = inArchive.getNumberOfItems();
            for (int i = 0; i < itemCount; i++) {
                System.out.println(String.format("%9s | %9s | %s", //
                        inArchive.getProperty(i, PropID.SIZE), //
                        inArchive.getProperty(i, PropID.PACKED_SIZE), //
                        inArchive.getProperty(i, PropID.PATH)));
            }

            // extract/test archive
            ArchiveExtractCallback extractCallback = new ArchiveExtractCallback(inArchive, destDir, isTestMode);
            inArchive.extract(null, isTestMode, extractCallback);
            Log.i("MultiVolume", "Extract/Test OK");
        } catch (Exception e) {
            System.err.println("Error occurs: " + e);
        } finally {
            if (inArchive != null) {
                try {
                    inArchive.close();
                } catch (SevenZipException e) {
                    System.err.println("Error closing archive: " + e);
                }
            }
            if (archiveOpenVolumeCallback != null) {
                try {
                    archiveOpenVolumeCallback.close();
                } catch (IOException e) {
                    System.err.println("Error closing file: " + e);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SevenZip.Version version = SevenZip.getSevenZipVersion();
        Log.i(TAG, "7-zip version: " + version.major + "." + version.minor + "." + version.build + " (" + version.version + "), " + version.date + version.copyright);
        Log.i(TAG, "7-Zip-JBinding version: " + SevenZip.getSevenZipJBindingVersion());
        Log.i(TAG, "Native library initialized: " + SevenZip.isInitializedSuccessfully());

        File srcFile = new File("/storage/emulated/0/rars/123.rar");
//        File srcFile = new File("/storage/emulated/0/rars/simple.7z");
        File destDir = new File("/storage/emulated/0/rars/extracted123");
//        File destDir = new File("/storage/emulated/0/rars/7zsimple1");
        ArchiveFormat format = ArchiveFormat.RAR5;
//        ArchiveFormat format = ArchiveFormat.SEVEN_ZIP;
        destDir.mkdirs();

        new Thread(()->testExtract(
                srcFile,
                destDir,
                false,
                format
        )).start();

        File srcArchiveMultiVolume = new File("/storage/emulated/0/rars/1234.part1.rar");
        File destDirMultiVolume = new File("/storage/emulated/0/rars/multipartExtracted");
        destDirMultiVolume.mkdirs();

        new Thread(()->testExtractRarMultiVolume(
                srcArchiveMultiVolume,
                destDirMultiVolume,
                true)).start();
    }
}