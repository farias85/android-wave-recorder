package gps.cenpis.cu.waverecorder.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

import gps.cenpis.cu.waverecorder.R;
import gps.cenpis.cu.waverecorder.activity.PerformanceLineChart;
import gps.cenpis.cu.waverecorder.activity.WaveItemDetailActivity;
import gps.cenpis.cu.waverecorder.activity.WaveItemListActivity;
import gps.cenpis.cu.waverecorder.dummy.WavContent;
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
    private SeekBar mSeekBarValues;
    private TextView mTvCount;

    private static final String WAV_FILE_PATH = "wav_file_name";
    private String wavFilePath;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private WavContent.WavItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WaveItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = WavContent.getInstance().getMap().get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_waveitem_detail, container, false);

        // Show the dummy content as text in a TextView.
//        if (mItem != null) {
//            ((TextView) rootView.findViewById(R.id.waveitem_detail)).setText(mItem.details);
//        }

//        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getActivity().setContentView(R.layout.activity_performance_linechart);

        wavFilePath = WavUtil.DIRECTORY_PATH + mItem.content;

        mTvCount = (TextView) rootView.findViewById(R.id.tvValueCount);
        mSeekBarValues = (SeekBar) rootView.findViewById(R.id.seekbarValues);
        mTvCount.setText("500");

        mSeekBarValues.setProgress(500);

        //mSeekBarValues.setOnSeekBarChangeListener(this);

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

        setData(500, 100);
        // dont forget to refresh the drawing
        mChart.invalidate();

//        ThisTakesAWhile ttaw = new ThisTakesAWhile();
//        ttaw.execute();

        return rootView;
    }

    private void setData(int count, float range) {

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult) + 3;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals.add(new Entry(i * 0.001f, val));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");

        set1.setColor(Color.BLACK);
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
    }

    private boolean processing;
    private int count;

    class ThisTakesAWhile extends AsyncTask<Integer, Integer, Integer> { //AsyncTask can take any type here arg0, arg1, arg2 all integers

        private int numcycles; //total number of times to execute process
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
