package gps.cenpis.cu.waverecorder.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gps.cenpis.cu.waverecorder.R;
import gps.cenpis.cu.waverecorder.utility.RecyclerViewEmptySupport;
import gps.cenpis.cu.waverecorder.utility.SimpleDividerItemDecoration;
import gps.cenpis.cu.waverecorder.utility.SimpleItemRecyclerViewAdapter;
import gps.cenpis.cu.waverecorder.wave.util.WavContent;
import gps.cenpis.cu.waverecorder.wave.util.WavUtil;

/**
 * An activity representing a list of WaveItems. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link WaveItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class WaveItemListActivity extends AppCompatActivity {

    private static final int PERMISSION_WRITE_EXTRENAL = 0;
    private static final String LOG_TAG = WaveItemListActivity.class.getSimpleName();
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
//    private SimpleItemRecyclerViewAdapter mAdapter;
//    private List<WavContent.WavItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waveitem_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btnGoRecord);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WaveItemListActivity.this, RecorderActivity.class));
//                startActivity(new Intent(WaveItemListActivity.this, BasicActivity.class));
//                startActivity(new Intent(WaveItemListActivity.this, SheetActivity.class));
//                startActivity(new Intent(WaveItemListActivity.this, NewVentureActivity.class));
//                startActivity(new Intent(WaveItemListActivity.this, SemantiveActivity.class));
            }
        });

        if (findViewById(R.id.recorder_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        if (ContextCompat.checkSelfPermission(WaveItemListActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(WaveItemListActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE_EXTRENAL);
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        RecyclerViewEmptySupport recyclerView = (RecyclerViewEmptySupport) findViewById(R.id.waveitem_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(WaveItemListActivity.this));
        recyclerView.setEmptyView(findViewById(R.id.list_empty));
        setupRecyclerView(recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getResources()));
        List<WavContent.WavItem> items = WavContent.getInstance(true).getItems();
        SimpleItemRecyclerViewAdapter mAdapter = new SimpleItemRecyclerViewAdapter(WaveItemListActivity.this, mTwoPane, items);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_insert:
                startActivity(new Intent(WaveItemListActivity.this, RecorderActivity.class));
//                startActivity(new Intent(WaveItemListActivity.this, LoginActivity.class));
//                startActivity(new Intent(WaveItemListActivity.this, LoginActivity2.class));
                break;
            case R.id.menu_settings:
                startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_WRITE_EXTRENAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    Toast.makeText(this, getBaseContext().getString(R.string.create_dir_ok), Toast.LENGTH_SHORT).show();
                } else {
                    // Permission denied
                    Toast.makeText(this, getBaseContext().getString(R.string.create_dir_fail), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
