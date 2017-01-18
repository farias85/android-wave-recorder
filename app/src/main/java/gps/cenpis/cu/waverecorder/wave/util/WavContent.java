package gps.cenpis.cu.waverecorder.wave.util;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample wFileName for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class WavContent {

    public final List<WavItem> items = new ArrayList<WavItem>();
    public final Map<String, WavItem> map = new HashMap<String, WavItem>();

    public WavContent() {
        List<String> witems = WavUtil.getFiles();
        for (int i = 0; i < witems.size(); i++) {
            addWavItem(new WavItem(String.valueOf(i), witems.get(i), makeDetails(i)));

//            CheapWAV cheapWAV = new CheapWAV();
//            try {
//                cheapWAV.ReadFile(new File(WavUtil.DIRECTORY_PATH + witems.get(i)));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            addWavItem(new WavItem(String.valueOf(i), witems.get(i), String.valueOf(cheapWAV.getFileSizeBytes())));
        }
    }

    private void addWavItem(WavItem item) {
        items.add(item);
        map.put(item.id, item);
    }

    private static WavContent content;

    public static WavContent getInstance(boolean reload) {
        if (content == null || reload) {
            content = new WavContent();
        }
        return content;
    }

    public static WavContent getInstance() {
        return getInstance(false);
    }

    @NonNull
    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    public List<WavItem> getItems() {
        return items;
    }

    public Map<String, WavItem> getMap() {
        return map;
    }

    public static WavItem findItem(String wFileName) {
        WavContent content = WavContent.getInstance(true);
        List<WavItem> l = content.getItems();
        for (WavItem item : l) {
            if (item.wFileName.equalsIgnoreCase(wFileName)) {
                return item;
            }
        }
        return null;
    }

    /**
     * A dummy item representing a piece of wFileName.
     */
    public static class WavItem {
        public final String id;
        public final String wFileName;
        public final String details;

        public WavItem(String id, String content, String details) {
            this.id = id;
            this.wFileName = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return wFileName;
        }
    }
}
