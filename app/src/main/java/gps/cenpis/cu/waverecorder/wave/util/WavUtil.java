package gps.cenpis.cu.waverecorder.wave.util;

import android.media.MediaPlayer;
import android.os.Environment;

import java.io.File;
import java.io.FileFilter;
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
}
