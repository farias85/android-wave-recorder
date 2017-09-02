/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created by Felipe Rodriguez Arias <ucifarias@gmail.com> on 12/01/2017
 */

package gps.cenpis.cu.waverecorder.wave.util;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample wFileName for user interfaces created by
 * Android template wizards.
 */
public class WavContent {

    private final List<WavItem> items = new ArrayList<WavItem>();
    private final Map<String, WavItem> map = new HashMap<String, WavItem>();

    private WavContent() {
        List<String> witems = WavUtil.getFiles();
        for (int i = 0; i < witems.size(); i++) {
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
