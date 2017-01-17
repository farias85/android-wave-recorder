package gps.cenpis.cu.waverecorder.oscilogram.semantive;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import gps.cenpis.cu.waverecorder.oscilogram.mpchart.PerformanceLineChartActivity;
import gps.cenpis.cu.waverecorder.oscilogram.semantive.view.Segment;
import gps.cenpis.cu.waverecorder.oscilogram.semantive.view.WaveformFragment;

import java.util.Arrays;
import java.util.List;

import gps.cenpis.cu.waverecorder.R;

public class SemantiveActivity extends AppCompatActivity {

    private static final int PERMISSION_WRITE_EXTRENAL = 0;
    private static final String WAV_FILE_PATH = "wav_file_path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semantive);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE_EXTRENAL);
        }

        if (savedInstanceState == null) {
            CustomWaveformFragment fragment = new CustomWaveformFragment();
            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    public static void callMe(Context context, String wavFilePath) {
        Intent intent = new Intent(context, SemantiveActivity.class);
        Bundle arguments = new Bundle();
        arguments.putString(WAV_FILE_PATH, wavFilePath);
        intent.putExtras(arguments);
        context.startActivity(intent);
    }

    public static class CustomWaveformFragment extends WaveformFragment {

        private String wavFilePath;

        @Override
        public void onCreate(Bundle bundle) {
            wavFilePath = getArguments().getString(WAV_FILE_PATH);
            super.onCreate(bundle);
        }

        /**
         * Provide path to your audio file.
         *
         * @return
         */
        @Override
        protected String getFileName() {
//            return Environment.getExternalStorageDirectory().toString() + "/wave-recorder" + "/testwave-8000-12000-aeiou.wav";
//            return Environment.getExternalStorageDirectory().toString() + "/wave-recorder" + "/testwave-44100-60000.wav";
            return wavFilePath;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_WRITE_EXTRENAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    Toast.makeText(this, "File Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    // Permission denied
                    Toast.makeText(this, "File Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
