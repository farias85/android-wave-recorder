package gps.cenpis.cu.waverecorder.utility;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import gps.cenpis.cu.waverecorder.R;
import gps.cenpis.cu.waverecorder.activity.WaveItemDetailActivity;
import gps.cenpis.cu.waverecorder.dummy.WavContent;
import gps.cenpis.cu.waverecorder.fragment.WaveItemDetailFragment;

/**
 * Created by farias-i5 on 10/01/2017.
 */

public class SimpleItemRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

    private final List<WavContent.WavItem> mValues;
    private AppCompatActivity mActivity;
    private boolean mTwoPane;

    private Typeface mTypeFaceLight;
    private Typeface mTypeFaceRegular;

    private int position;

    public SimpleItemRecyclerViewAdapter(AppCompatActivity activity, boolean twoPane, List<WavContent.WavItem> items) {
        mValues = items;
        mActivity = activity;
        mTwoPane = twoPane;

        mTypeFaceLight = Typeface.createFromAsset(activity.getAssets(), "OpenSans-Light.ttf");
        mTypeFaceRegular = Typeface.createFromAsset(activity.getAssets(), "OpenSans-Regular.ttf");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.waveitem_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.mIdView.setText(mValues.get(position).id);
        holder.mIdView.setTypeface(mTypeFaceRegular);

        holder.mContentView.setText(mValues.get(position).content);
        holder.mContentView.setTypeface(mTypeFaceLight);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(WaveItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                    WaveItemDetailFragment fragment = new WaveItemDetailFragment();
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
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener {

        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public WavContent.WavItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);

            mView.setOnCreateContextMenuListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuInflater mi = new MenuInflater(mActivity);
            mi.inflate(R.menu.list_menu_item_longpress, menu);
        }
    }
}
