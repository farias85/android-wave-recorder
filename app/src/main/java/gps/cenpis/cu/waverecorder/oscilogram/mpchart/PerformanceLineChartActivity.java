
package gps.cenpis.cu.waverecorder.oscilogram.mpchart;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

import gps.cenpis.cu.waverecorder.R;
import gps.cenpis.cu.waverecorder.wave.util.WavReader;

public class PerformanceLineChartActivity extends Activity implements OnSeekBarChangeListener {

    private LineChart mChart;
    private SeekBar mSeekBarValues;
    private TextView mTvCount;

    private static final String WAV_FILE_NAME = "wav_file_name";
    private String wavFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_performance_linechart);

        wavFileName = getIntent().getExtras().getString(WAV_FILE_NAME);

        mTvCount = (TextView) findViewById(R.id.tvValueCount);
        mSeekBarValues = (SeekBar) findViewById(R.id.seekbarValues);
        mTvCount.setText("500");

        mSeekBarValues.setProgress(500);

        mSeekBarValues.setOnSeekBarChangeListener(this);

        mChart = (LineChart) findViewById(R.id.chart1);
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

        ThisTakesAWhile ttaw = new ThisTakesAWhile();
        ttaw.execute();
    }

    public static void callMe(Context context, String wavFileName) {
        Intent intent = new Intent(context, PerformanceLineChartActivity.class);
        Bundle arguments = new Bundle();
        arguments.putString(PerformanceLineChartActivity.WAV_FILE_NAME, wavFileName);
        intent.putExtras(arguments);
        context.startActivity(intent);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        int count = mSeekBarValues.getProgress() + 1000;
        mTvCount.setText("" + count);

        mChart.resetTracking();

        setData(count, 500f);

        // redraw
        mChart.invalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    private void setData2() {

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        String wavFileName = Environment.getExternalStorageDirectory() + "/wave-recorder/testwave-8000-60000.wav";
        WavReader wr = new WavReader();
        wr.openWav(wavFileName);
        double[] left = wr.getLeft();

        for (int i = 0; i < left.length; i++) {
            if (i % 100 == 0)
                yVals.add(new Entry(i * 1f, (float) left[i]));
        }

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
            wr.openWav(wavFileName);
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
