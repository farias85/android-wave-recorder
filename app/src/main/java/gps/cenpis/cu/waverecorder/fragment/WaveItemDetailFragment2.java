package gps.cenpis.cu.waverecorder.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.newventuresoftware.waveform.WaveformView;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.text.DecimalFormat;

import gps.cenpis.cu.waverecorder.R;
import gps.cenpis.cu.waverecorder.activity.WaveItemDetailActivity;
import gps.cenpis.cu.waverecorder.activity.WaveItemListActivity;
import gps.cenpis.cu.waverecorder.oscilogram.newventures.PlaybackListener;
import gps.cenpis.cu.waverecorder.oscilogram.newventures.PlaybackThread;
import gps.cenpis.cu.waverecorder.oscilogram.semantive.soundfile.CheapWAV;
import gps.cenpis.cu.waverecorder.wave.util.WavContent;
import gps.cenpis.cu.waverecorder.wave.util.WavUtil;

/**
 * A fragment representing a single WaveItem detail screen.
 * This fragment is either contained in a {@link WaveItemListActivity}
 * in two-pane mode (on tablets) or a {@link WaveItemDetailActivity}
 * on handsets.
 */
public class WaveItemDetailFragment2 extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";

    private WavContent.WavItem mItem;
    private PlaybackThread mPlaybackThread;
    private FloatingActionButton playFab;

    public WaveItemDetailFragment2() {
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
        View rootView = inflater.inflate(R.layout.activity_waveitem_detail_fragment2, container, false);

        if (mItem != null) {
            CheapWAV cheapWAV = new CheapWAV();
            try {
                cheapWAV.ReadFile(new File(WavUtil.DIRECTORY_PATH + mItem.wFileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuilder details = new StringBuilder();
            details.append("FileSizeBytes: ");
            double size = cheapWAV.getFileSizeBytes() / 1024;
            DecimalFormat decimalFormat = new DecimalFormat("##.##");
            details.append(decimalFormat.format(size));
            details.append(" KB");
            details.append("\n");
            details.append("SampleRate: ");
            details.append(cheapWAV.getSampleRate());
            details.append(" Hz");
            details.append("\n");
            details.append("BitrateKbps: ");
            details.append(cheapWAV.getAvgBitrateKbps());
            details.append(" kbps");
            details.append("\n");
            ((TextView) rootView.findViewById(R.id.waveitem_detail)).setText(details.toString());

            final WaveformView mPlaybackView = (WaveformView) rootView.findViewById(R.id.playbackWaveformView);

            short[] samples = null;
            try {
                samples = getAudioSample();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (samples != null) {
                playFab = (FloatingActionButton) rootView.findViewById(R.id.playFab);

                mPlaybackThread = new PlaybackThread(samples, new PlaybackListener() {
                    @Override
                    public void onProgress(int progress) {
                        mPlaybackView.setMarkerPosition(progress);
                    }

                    @Override
                    public void onCompletion() {
                        mPlaybackView.setMarkerPosition(mPlaybackView.getAudioLength());
                        playFab.setImageResource(android.R.drawable.ic_media_play);
                    }
                });
                PlaybackThread.SAMPLE_RATE = cheapWAV.getSampleRate();

                mPlaybackView.setChannels(1);
                mPlaybackView.setSampleRate(cheapWAV.getSampleRate());
                mPlaybackView.setSamples(samples);

                playFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mPlaybackThread.playing()) {
                            mPlaybackThread.startPlayback();
                            playFab.setImageResource(android.R.drawable.ic_media_pause);
                        } else {
                            mPlaybackThread.stopPlayback();
                            playFab.setImageResource(android.R.drawable.ic_media_play);
                        }
                    }
                });
            }
        } else {
            Toast.makeText(getActivity(), "Empty mItem", Toast.LENGTH_LONG).show();
        }

        return rootView;
    }

    @Override
    public void onDestroy() {
        if (mPlaybackThread.playing()) {
            mPlaybackThread.stopPlayback();
            playFab.setImageResource(android.R.drawable.ic_media_play);
        }
        mPlaybackThread = null;
        super.onDestroy();
    }

    private short[] getAudioSample() throws IOException {

        InputStream is = null;
        byte[] data = null;
        try {
            is = new FileInputStream(WavUtil.DIRECTORY_PATH + mItem.wFileName);
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
