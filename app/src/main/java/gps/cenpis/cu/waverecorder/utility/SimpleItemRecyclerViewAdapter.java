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
 * Created by Felipe Rodriguez Arias <ucifarias@gmail.com> on 10/01/2017
 */

package gps.cenpis.cu.waverecorder.utility;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kennyc.bottomsheet.BottomSheet;
import com.kennyc.bottomsheet.BottomSheetListener;

import java.io.File;
import java.util.List;

import gps.cenpis.cu.waverecorder.R;
import gps.cenpis.cu.waverecorder.activity.WaveItemDetailActivity;
import gps.cenpis.cu.waverecorder.fragment.WaveItemDetailFragment2;
import gps.cenpis.cu.waverecorder.oscilogram.semantive.SemantiveActivity;
import gps.cenpis.cu.waverecorder.wave.util.WavContent;
import gps.cenpis.cu.waverecorder.wave.util.WavUtil;

public class SimpleItemRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

    private final List<WavContent.WavItem> items;
    private AppCompatActivity mActivity;
    private boolean mTwoPane;

    private Typeface mTypeFaceLight;
    private Typeface mTypeFaceRegular;

    private int position;

    public SimpleItemRecyclerViewAdapter(AppCompatActivity activity, boolean twoPane, List<WavContent.WavItem> items) {
        this.items = items;
        mActivity = activity;
        mTwoPane = twoPane;

        mTypeFaceLight = Typeface.createFromAsset(activity.getAssets(), "OpenSans-Light.ttf");
        mTypeFaceRegular = Typeface.createFromAsset(activity.getAssets(), "OpenSans-Regular.ttf");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_waveitem_list_content_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = items.get(position);

        holder.mIdView.setText(holder.mItem.wFileName);
        holder.mIdView.setTypeface(mTypeFaceRegular);

        holder.mContentView.setText("");
        holder.mContentView.setTypeface(mTypeFaceLight);

        int id = Integer.parseInt(holder.mItem.id) + 1;
        holder.mDetails.setText(String.valueOf(id));
        holder.mDetails.setTypeface(mTypeFaceLight);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTwoPane) {
//                    Bundle arguments = new Bundle();
//                    arguments.putString(WaveItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
//                    WaveItemDetailFragment fragment = new WaveItemDetailFragment();
//                    fragment.setArguments(arguments);
//                    mActivity.getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.recorder_container, fragment)
//                            .commit();
                    Bundle arguments = new Bundle();
                    arguments.putString(WaveItemDetailFragment2.ARG_ITEM_ID, holder.mItem.id);
                    WaveItemDetailFragment2 fragment = new WaveItemDetailFragment2();
                    fragment.setArguments(arguments);
                    mActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recorder_container, fragment)
                            .commit();
                } else {
                    WaveItemDetailActivity.callMe(v.getContext(), holder.mItem.id);
                }
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getAdapterPosition());
                return false;
            }
        });
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.mView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener, BottomSheetListener {

        final View mView;
        final TextView mIdView;
        final TextView mContentView;
        final TextView mDetails;
        WavContent.WavItem mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mDetails = (TextView) view.findViewById(R.id.details);

            mView.setOnCreateContextMenuListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//            MenuInflater mi = new MenuInflater(mActivity);
//            mi.inflate(R.menu.list_menu_item_longpress, menu);

            new BottomSheet.Builder(mActivity)
                    .setSheet(R.menu.list_menu_item_longpress)
                    .grid()
                    .setTitle("Options")
                    .setListener(ViewHolder.this).show();
        }

        @Override
        public void onSheetShown(@NonNull BottomSheet bottomSheet) {

        }

        @Override
        public void onSheetItemSelected(@NonNull BottomSheet bottomSheet, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    new BottomSheet.Builder(mActivity)
                            .setTitle("BottomSheet")
                            .setMessage("With bottom sheet you can also display a simple message dialog")
                            .setPositiveButton("Okay")
                            .setNegativeButton("Close")
                            .setListener(ViewHolder.this).show();
                    break;
                case R.id.oscilogram:
                    //PerformanceLineChartActivity.callMe(mActivity, WavUtil.DIRECTORY_PATH + mItem.wFileName);
                    SemantiveActivity.callMe(mActivity, WavUtil.DIRECTORY_PATH + mItem.wFileName);
                    break;
                case R.id.play:
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    File file = new File(WavUtil.DIRECTORY_PATH + mItem.wFileName);
                    intent.setDataAndType(Uri.fromFile(file), "audio/*");
                    mActivity.startActivity(intent);
                    break;
            }
            Toast.makeText(mActivity, item.getTitle() + " Clicked", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onSheetDismissed(@NonNull BottomSheet bottomSheet, @DismissEvent int dismissEvent) {
            switch (dismissEvent) {
                case BottomSheetListener.DISMISS_EVENT_BUTTON_POSITIVE:
                    SimpleItemRecyclerViewAdapter that = SimpleItemRecyclerViewAdapter.this;
                    that.items.remove(mItem);
                    that.notifyDataSetChanged();
                    Toast.makeText(mActivity, "Positive Button Clicked", Toast.LENGTH_SHORT).show();
                    break;

                case BottomSheetListener.DISMISS_EVENT_BUTTON_NEGATIVE:
                    Toast.makeText(mActivity, "Negative Button Clicked", Toast.LENGTH_SHORT).show();
                    break;
                case BottomSheetListener.DISMISS_EVENT_BUTTON_NEUTRAL:
                    break;
                case BottomSheetListener.DISMISS_EVENT_MANUAL:
                    break;
                case BottomSheetListener.DISMISS_EVENT_SWIPE:
                    break;
            }
        }
    }
}
