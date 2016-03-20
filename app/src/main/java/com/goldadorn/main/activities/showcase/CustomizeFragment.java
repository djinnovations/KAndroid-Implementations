package com.goldadorn.main.activities.showcase;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goldadorn.main.R;

/**
 * Created by Vijith Menon on 18/3/16.
 */
public class CustomizeFragment extends Fragment {
    private final static String TAG = CustomizeFragment.class.getSimpleName();
    private final static boolean DEBUG = true;

    RecyclerView mRecyclerView;
    MergeRecycleAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customize, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new MergeRecycleAdapter(new ViewHolderFactory(getActivity()));

        mAdapter.addAdapter(new PBAdapter(getActivity()));
        mAdapter.addAdapter(new TitleAdapter(getActivity(),"Customize"));
        mAdapter.addAdapter(new CustomizeMainAdapter(getActivity()));
        mAdapter.addAdapter(new SeparatorAdapter(getActivity()));
        mAdapter.addAdapter(new CustomizeMainAdapter(getActivity()));
        mAdapter.addAdapter(new SeparatorAdapter(getActivity()));
        mAdapter.addAdapter(new CustomizeMainAdapter(getActivity()));
        mAdapter.addAdapter(new SeparatorAdapter(getActivity()));
        mAdapter.addAdapter(new CustomizeMainAdapter(getActivity()));
        mAdapter.addAdapter(new CustomizeExtraAdapter(getActivity()));
        mAdapter.addAdapter(new CustomizeExtraAdapter(getActivity()));
        mAdapter.addAdapter(new CustomizeExtraAdapter(getActivity()));
        mAdapter.addAdapter(new CustomizeExtraAdapter(getActivity()));
        mAdapter.addAdapter(new CustomizeExtraAdapter(getActivity()));
        mAdapter.addAdapter(new SeparatorAdapter(getActivity()));
        mAdapter.addAdapter(new CustomizeMainAdapter(getActivity()));
        mAdapter.addAdapter(new SeparatorAdapter(getActivity()));
        mAdapter.addAdapter(new CustomizeSpinnerAdapter(getActivity()));
        mAdapter.addAdapter(new SeparatorAdapter(getActivity()));
        mAdapter.addAdapter(new CustomizeButtonAdapter(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

    }

    class PBAdapter extends RecyclerAdapter<PBViewHolder> {

        private final Context context;

        public PBAdapter(Context context) {
            super(context,true);
            this.context = context;
        }

        @Override
        public void onNewViewHolder(PBViewHolder holder) {

        }

        @Override
        public void onBindViewHolder(PBViewHolder holder, int position) {

        }
        @Override
        public int getItemViewType(int position) {
            return ViewHolderFactory.TYPE.VHT_PB;
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }



    class TitleAdapter extends RecyclerAdapter<ViewHolder> {

        String title;

        public TitleAdapter(Context context,String title) {
            super(context,true);
            this.title = title;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ((TextView)holder.itemView.findViewById(R.id.title)).setText(title);
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public int getItemViewType(int position) {
            return ViewHolderFactory.TYPE.VHT_TITLE;
        }

        @Override
        public void onNewViewHolder(ViewHolder holder) {

        }
    }

    class SeparatorAdapter extends RecyclerAdapter<ViewHolder> {

        private final Context context;

        public SeparatorAdapter(Context context) {
            super(context,true);
            this.context = context;
        }

        @Override
        public void onNewViewHolder(ViewHolder holder) {

        }
        @Override
        public int getItemViewType(int position) {
            return ViewHolderFactory.TYPE.VHT_C_SEPARATOR;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }

    class CustomizeMainAdapter extends RecyclerAdapter<ViewHolder> {

        private final Context context;

        public CustomizeMainAdapter(Context context) {
            super(context,true);
            this.context = context;
        }

        @Override
        public void onNewViewHolder(ViewHolder holder) {

        }
        @Override
        public int getItemViewType(int position) {
            return ViewHolderFactory.TYPE.VHT_C_MAIN;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }

    class CustomizeExtraAdapter extends RecyclerAdapter<ViewHolder> {

        public CustomizeExtraAdapter(Context context) {
            super(context,true);
        }

        @Override
        public void onNewViewHolder(ViewHolder holder) {

        }
        @Override
        public int getItemViewType(int position) {
            return ViewHolderFactory.TYPE.VHT_C_EXTRA;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }
    class CustomizeSpinnerAdapter extends RecyclerAdapter<ViewHolder> {


        public CustomizeSpinnerAdapter(Context context) {
            super(context,true);
        }
        @Override
        public int getItemViewType(int position) {
            return ViewHolderFactory.TYPE.VHT_C_SPINNER;
        }

        @Override
        public void onNewViewHolder(ViewHolder holder) {

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }
    class CustomizeButtonAdapter extends RecyclerAdapter<ViewHolder> {


        public CustomizeButtonAdapter(Context context) {
            super(context,true);
        }

        @Override
        public void onNewViewHolder(ViewHolder holder) {

        }
        @Override
        public int getItemViewType(int position) {
            return ViewHolderFactory.TYPE.VHT_C_BUTTON;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }
}
