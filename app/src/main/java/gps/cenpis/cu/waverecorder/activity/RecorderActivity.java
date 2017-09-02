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
 * Created by Felipe Rodriguez Arias <ucifarias@gmail.com> on 13/01/2017
 */

package gps.cenpis.cu.waverecorder.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import gps.cenpis.cu.waverecorder.R;
import gps.cenpis.cu.waverecorder.fragment.WaveItemDetailFragment2;
import gps.cenpis.cu.waverecorder.wave.util.WavAudioRecorder;
import gps.cenpis.cu.waverecorder.wave.util.WavContent;
import gps.cenpis.cu.waverecorder.wave.util.WavUtil;

public class RecorderActivity extends AppCompatActivity {

    private static final int PERMISSION_RECORD_AUDIO = 0;
    private static final String LOG_TAG = RecorderActivity.class.getName();

    private Button btnControl, btnStop;
    private TextView textDisplay;
    private EditText editText1;
    private WavAudioRecorder mRecorder;

    private RadioGroup rgDuration;
    private int duration;
    private RadioGroup rgFrequency;
    private int frequency;

    private CountDownTimer timer;
//    private WaveformView waveformView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

        editText1 = (EditText) this.findViewById(R.id.editText1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btnFloatingPlay);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(WavUtil.DIRECTORY_PATH + editText1.getText() + ".wav");
                if (!file.exists()) {
                    Snackbar.make(view, "You don't recorder yet", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "audio/*");
                    startActivity(intent);
                }
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        rgDuration = (RadioGroup) findViewById(R.id.rgDuration);
        rgFrequency = (RadioGroup) findViewById(R.id.rgFrequency);


        rgDurationClicked(null);
        rgFrequencyClicked(null);

        textDisplay = (TextView) this.findViewById(R.id.Textdisplay);

//        waveformView = (WaveformView) this.findViewById(R.id.waveformView);
//        waveformView.setChannels(1);

        btnControl = (Button) this.findViewById(R.id.btnControl);
        btnControl.setText(getBaseContext().getString(R.string.btn_start));
        btnControl.setOnClickListener(new BtnControlOnClickListener());

        btnStop = (Button) this.findViewById(R.id.btnStop);
        btnStop.setText(getBaseContext().getString(R.string.btn_stop));
        btnStop.setEnabled(false);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                mRecorder.stop();
                //mRecorder.reset();
                btnControl.setText(getBaseContext().getString(R.string.btn_start));
                btnControlSetEnable(true);

//                short[] samples = null;
//                try {
//                    samples = WavUtil.getAudioSample(getFilePath());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                waveformView.setSamples(samples);
//                waveformView.setVisibility(View.VISIBLE);
//                waveformView.refreshDrawableState();

                fab.setVisibility(View.INVISIBLE);
                WavContent.WavItem item = WavContent.findItem(editText1.getText().toString() + ".wav");
                Bundle arguments = new Bundle();
                arguments.putString(WaveItemDetailFragment2.ARG_ITEM_ID, item.id);
                WaveItemDetailFragment2 fragment = new WaveItemDetailFragment2();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.recorder_container, fragment)
                        .commit();
            }
        });

        List<Integer> list = getSupportedSampleRate();
        for (Integer sampleRate : list) {
            enableRadioGroupOptions(sampleRate);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    Toast.makeText(this, getBaseContext().getString(R.string.create_dir_ok), Toast.LENGTH_SHORT).show();
                    btnControl.callOnClick();
                } else {
                    // Permission denied
                    Toast.makeText(this, getBaseContext().getString(R.string.create_dir_fail), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private String getFilePath() {
        if (editText1.getText().equals(""))
            editText1.setText("testwave");

        File folder = new File(WavUtil.DIRECTORY_PATH);
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                Log.i(LOG_TAG, getBaseContext().getString(R.string.create_dir_ok) + WavUtil.DIRECTORY_PATH);
                Toast.makeText(this, getBaseContext().getString(R.string.create_dir_ok), Toast.LENGTH_SHORT).show();
            } else {
                Log.i(LOG_TAG, getBaseContext().getString(R.string.create_dir_fail) + WavUtil.DIRECTORY_PATH);
                Toast.makeText(this, getBaseContext().getString(R.string.create_dir_fail), Toast.LENGTH_SHORT).show();
            }
        }

        return WavUtil.DIRECTORY_PATH + editText1.getText() + ".wav";
    }

    public void rgDurationClicked(View view) {

        switch (rgDuration.getCheckedRadioButtonId()) {
            case R.id.radio12000ms:
                duration = 12000;
                break;
            case R.id.radio48000ms:
                duration = 48000;
                break;
            case R.id.radio60000ms:
                duration = 60000;
                break;
            default:
                duration = 12000;
        }
        editText1.setText("testwave" + "-" + frequency + "-" + duration);
    }

    public void rgFrequencyClicked(View view) {

        switch (rgFrequency.getCheckedRadioButtonId()) {
            case R.id.radio8000hz:
                frequency = 8000;
                break;
            case R.id.radio22050hz:
                frequency = 22050;
                break;
            case R.id.radio44100hz:
                frequency = 44100;
                break;
            default:
                frequency = 8000;
        }
        editText1.setText("testwave" + "-" + frequency + "-" + duration);
    }

    private List<Integer> getSupportedSampleRate() {

        final int validSampleRates[] = new int[]{8000, 11025, 16000, 22050,
                32000, 37800, 44056, 44100, 47250, 48000, 50000, 50400, 88200,
                96000, 176400, 192000, 352800, 2822400, 5644800};

        List<Integer> mResult = new ArrayList<>();

        for (int i = 0; i < validSampleRates.length; i++) {
            int result = AudioRecord.getMinBufferSize(validSampleRates[i],
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            if (result != AudioRecord.ERROR
                    && result != AudioRecord.ERROR_BAD_VALUE && result > 0) {
                mResult.add(validSampleRates[i]);
                Log.i(LOG_TAG, "valid sample rate: " + String.valueOf(validSampleRates[i]));
            }
        }
        return mResult;
    }

    private void enableRadioGroupOptions(int sampleRate) {
        RadioButton rb = null;
        switch (sampleRate) {
            case 8000:
                rb = (RadioButton) this.findViewById(R.id.radio8000hz);
                break;
            case 22050:
                rb = (RadioButton) this.findViewById(R.id.radio22050hz);
                break;
            case 44100:
                rb = (RadioButton) this.findViewById(R.id.radio44100hz);
                break;
        }
        if (rb != null) {
            rb.setEnabled(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mRecorder) {
            mRecorder.release();
        }
    }

    private void btnControlSetEnable(boolean enable) {
        btnControl.setEnabled(enable);
        editText1.setEnabled(enable);
        btnStop.setEnabled(!enable);
    }

    private class BtnControlOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            if (ContextCompat.checkSelfPermission(RecorderActivity.this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request permission
                ActivityCompat.requestPermissions(RecorderActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_RECORD_AUDIO);
                return;
            }

            mRecorder = WavAudioRecorder.getInstanse(frequency);
            mRecorder.setOutputFile(getFilePath());
            textDisplay.setText(getBaseContext().getString(R.string.recorder_display) + getFilePath());

            if (WavAudioRecorder.State.INITIALIZING == mRecorder.getState()) {
                mRecorder.prepare();
                mRecorder.start();
            } else if (WavAudioRecorder.State.ERROR == mRecorder.getState()) {
                mRecorder.release();
                Toast.makeText(RecorderActivity.this, getBaseContext().getString(R.string.recorder_error), Toast.LENGTH_LONG).show();
            }

            if (WavAudioRecorder.State.ERROR != mRecorder.getState()) {

                btnControlSetEnable(false);
                Toast.makeText(RecorderActivity.this, frequency + " - " + duration, Toast.LENGTH_LONG).show();
                //waveformView.setVisibility(View.INVISIBLE);

                timer = new CountDownTimer(duration, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long secToFinish = millisUntilFinished / 1000;
                        if (secToFinish == 1) {
                            btnControl.setText(getBaseContext().getString(R.string.recorder_almost));
                        } else {
                            btnControl.setText(getBaseContext().getString(R.string.recorder_remaining) + secToFinish);
                        }
                    }

                    @Override
                    public void onFinish() {
                        try {
                            btnStop.callOnClick();
                        } catch (Exception ex) {
                            Log.e(LOG_TAG, "onFinish error: " + ex);
                        }
                    }
                }.start();
            }
        }
    }
}
