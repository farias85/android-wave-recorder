package gps.cenpis.cu.waverecorder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import gps.cenpis.cu.waverecorder.R;
import gps.cenpis.cu.waverecorder.dummy.WavContent;
import gps.cenpis.cu.waverecorder.utility.RecyclerViewEmptySupport;
import gps.cenpis.cu.waverecorder.utility.SimpleDividerItemDecoration;
import gps.cenpis.cu.waverecorder.utility.SimpleItemRecyclerViewAdapter;
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

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private SimpleItemRecyclerViewAdapter mAdapter;
    private List<WavContent.WavItem> items;

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
            }
        });

        if (findViewById(R.id.recorder_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        RecyclerViewEmptySupport recyclerView = (RecyclerViewEmptySupport) findViewById(R.id.waveitem_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(WaveItemListActivity.this));
        recyclerView.setEmptyView(findViewById(R.id.list_empty));
        assert recyclerView != null;
        setupRecyclerView(recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getResources()));
        items = WavContent.getInstance(true).getItems();
        mAdapter = new SimpleItemRecyclerViewAdapter(WaveItemListActivity.this, mTwoPane, items);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(WaveItemListActivity.this).inflate(R.menu.list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_insert:
                startActivity(new Intent(WaveItemListActivity.this, RecorderActivity.class));
                break;
            case R.id.menu_settings:
                //startActivity(new Intent(getActivity(), TaskPreferences.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int position = mAdapter.getPosition();
        WavContent.WavItem obj = items.get(position);

        switch (item.getItemId()) {
            case R.id.menu_delete:
                items.remove(obj);
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.menu_oscilogram:
                PerformanceLineChart.callMe(WaveItemListActivity.this, WavUtil.DIRECTORY_PATH + obj.content);
                //Toast.makeText(WaveItemListActivity.this, obj.content, Toast.LENGTH_LONG).show();
                break;
        }

        return super.onContextItemSelected(item);
    }

}
