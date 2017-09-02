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
 * Created by Felipe Rodriguez Arias <ucifarias@gmail.com> on 19/01/2017
 */

package gps.cenpis.cu.waverecorder.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

import gps.cenpis.cu.waverecorder.R;
import gps.cenpis.cu.waverecorder.activity.WaveItemDetailActivity;
import gps.cenpis.cu.waverecorder.activity.WaveItemListActivity;
import gps.cenpis.cu.waverecorder.wave.util.WavContent;
import gps.cenpis.cu.waverecorder.wave.util.WavReader;
import gps.cenpis.cu.waverecorder.wave.util.WavUtil;

/**
 * A fragment representing a single WaveItem detail screen.
 * This fragment is either contained in a {@link WaveItemListActivity}
 * in two-pane mode (on tablets) or a {@link WaveItemDetailActivity}
 * on handsets.
 */
public class WaveItemDetailFragment extends Fragment {

    private LineChart mChart;
    private String wavFilePath;

    public static final String ARG_ITEM_ID = "item_id";

    private WavContent.WavItem mItem;

    public WaveItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy wFileName specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load wFileName from a wFileName provider.
            mItem = WavContent.getInstance().getMap().get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.wFileName);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_waveitem_detail_fragment, container, false);

        if (mItem != null) {
            wavFilePath = WavUtil.DIRECTORY_PATH + mItem.wFileName;
            mChart = (LineChart) rootView.findViewById(R.id.chart1);
            mChart.setDrawGridBackground(false);

            // no description text
            mChart.getDescription().setEnabled(false);

            // enable touch gestures
            mChart.setTouchEnabled(true);

            // enable scaling and dragging
            mChart.setDragEnabled(true);
            mChart.setScaleEnabled(true);

            // if disabled, scaling can be done on x- and y-axis separately
            mChart.setPinchZoom(false);

            mChart.getAxisLeft().setDrawGridLines(false);
            mChart.getAxisRight().setEnabled(false);
            mChart.getXAxis().setDrawGridLines(true);
            mChart.getXAxis().setDrawAxisLine(false);
            mChart.setBackgroundColor(Color.BLACK);

            LineChartAsyncTask chartAsyncTask = new LineChartAsyncTask();
            chartAsyncTask.execute();
        } else {
            Toast.makeText(getActivity(), "Empty mItem", Toast.LENGTH_LONG).show();
        }

        return rootView;
    }

    private boolean processing;
    private int count;

    class LineChartAsyncTask extends AsyncTask<Integer, Integer, Integer> { //AsyncTask can take any type here arg0, arg1, arg2 all integers

        private ArrayList<Entry> yVals = new ArrayList<Entry>();
        private double[] left;

        @Override
        protected void onPreExecute() { //Executes in UI thread before task begins. Can be used to set things up in UI such as showing progress bar
            processing = true;
            count = 0;

            WavReader wr = new WavReader();
            wr.openWav(wavFilePath);
            left = wr.getLeft();
        }

        @Override
        protected Integer doInBackground(Integer... arg0) { //Runs in a background thread. Used to run code that could block the UI

            for (int i = 0; i < left.length; i++) {
                if (i % 100 == 0)
                    yVals.add(new Entry(i * 1f, (float) left[i]));
            }

            return count; //return value sent to UI via onPostExecute. class arg2 determines result type sent
        }

        @Override
        protected void onProgressUpdate(Integer... arg1) { //called when background task calls publishProgress in doInBackground
            if (isCancelled()) {

            } else {

            }
        }

        @Override
        protected void onPostExecute(Integer result) { //result comes from return value of doInBackground. runs on UI thread, not called if task cancelled
            processing = false;
            // create a dataset and give it a type
            LineDataSet set1 = new LineDataSet(yVals, "Oscilogram");

            set1.setColor(Color.GREEN);
            set1.setLineWidth(0.5f);
            set1.setDrawValues(false);
            set1.setDrawCircles(false);
            set1.setMode(LineDataSet.Mode.LINEAR);
            set1.setDrawFilled(false);

            // create a data object with the datasets
            LineData data = new LineData(set1);

            // set data
            mChart.setData(data);

            // get the legend (only possible after setting data)
            Legend l = mChart.getLegend();
            l.setEnabled(false);
            mChart.invalidate();
        }

        @Override
        protected void onCancelled() { //run on UI thread if task is cancelled
            processing = false;
        }
    }
}
