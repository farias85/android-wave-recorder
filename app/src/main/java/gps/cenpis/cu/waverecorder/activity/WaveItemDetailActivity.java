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

package gps.cenpis.cu.waverecorder.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import gps.cenpis.cu.waverecorder.R;
import gps.cenpis.cu.waverecorder.fragment.WaveItemDetailFragment;
import gps.cenpis.cu.waverecorder.fragment.WaveItemDetailFragment2;

/**
 * An activity representing a single WaveItem detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link WaveItemListActivity}.
 */
public class WaveItemDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waveitem_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            //DetailFragment1
//            Bundle arguments = new Bundle();
//            arguments.putString(WaveItemDetailFragment.ARG_ITEM_ID,
//                    getIntent().getStringExtra(WaveItemDetailFragment.ARG_ITEM_ID));
//            WaveItemDetailFragment fragment = new WaveItemDetailFragment();
//            fragment.setArguments(arguments);
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.recorder_container, fragment)
//                    .commit();

            //DetailFragment2
            Bundle arguments = new Bundle();
            arguments.putString(WaveItemDetailFragment2.ARG_ITEM_ID,
                    getIntent().getStringExtra(WaveItemDetailFragment2.ARG_ITEM_ID));
            WaveItemDetailFragment2 fragment = new WaveItemDetailFragment2();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recorder_container, fragment)
                    .commit();
        }
    }

    public static void callMe(Context context, String itemId) {
        Intent intent = new Intent(context, WaveItemDetailActivity.class);
        intent.putExtra(WaveItemDetailFragment.ARG_ITEM_ID, itemId);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, WaveItemListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
