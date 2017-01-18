package gps.cenpis.cu.waverecorder.wave.util;

import android.media.MediaPlayer;
import android.os.Environment;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by farias on 28/12/2016.
 */
public class WavUtil {

    public static final String STORAGE_DIR = Environment.getExternalStorageDirectory().toString();
    public static final String FOLDER_NAME = "wave-recorder";
    public static final String DIRECTORY_PATH = STORAGE_DIR + "/" + FOLDER_NAME + "/";

    public static List<String> getFiles() {

        List<String> list = new ArrayList<>();
        File files = new File(DIRECTORY_PATH);

        if (files.exists()) {
            FileFilter filter = new FileFilter() {
                private final List<String> exts = Arrays.asList("wav");

                public boolean accept(File pathname) {
                    String ext;
                    String path = pathname.getPath();
                    ext = path.substring(path.lastIndexOf(".") + 1);
                    return exts.contains(ext);
                }
            };
            final File[] filesFound = files.listFiles(filter);
            if (filesFound != null && filesFound.length > 0) {
                for (File file : filesFound) {
                    list.add(file.getName());
                }
            }
        }

        return list;
    }

    public static void playAudioFile(String fileName) { //set up MediaPlayer

        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(fileName);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static short[] getAudioSample(String wFileName) throws IOException {

        InputStream is = null;
        byte[] data = null;
        try {
            is = new FileInputStream(DIRECTORY_PATH + wFileName);
            data = IOUtils.toByteArray(is);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        ShortBuffer sb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        short[] samples = new short[sb.limit()];
        sb.get(samples);
        return samples;
    }
}
