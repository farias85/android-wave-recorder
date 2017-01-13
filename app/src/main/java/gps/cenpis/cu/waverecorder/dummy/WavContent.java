package gps.cenpis.cu.waverecorder.dummy;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gps.cenpis.cu.waverecorder.wave.util.WavUtil;

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
        for (int i = 1; i < witems.size(); i++) {
            addWavItem(new WavItem(String.valueOf(i), witems.get(i), makeDetails(i)));
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
