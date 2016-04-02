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
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.assist.MergeRecycleAdapter;
import com.goldadorn.main.assist.RecyclerAdapter;
import com.goldadorn.main.assist.SingleItemAdapter;
import com.goldadorn.main.assist.ViewHolder;
import com.goldadorn.main.model.ProductOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Vijith Menon on 18/3/16.
 */
public class ProductOptionsFragment extends Fragment {
    private final static String TAG = ProductOptionsFragment.class.getSimpleName();
    private final static boolean DEBUG = true;


    RecyclerView mRecyclerView;
    MergeRecycleAdapter mAdapter;
    Context mContext;
    private CustomizeMainAdapter mCustomizeAdapter;
    private ProductActivity mProductActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        mProductActivity = (ProductActivity) getActivity();
        return inflater.inflate(R.layout.fragment_customize, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new MergeRecycleAdapter(new ViewHolderFactory(getActivity()));
        mAdapter.addAdapter(new PBAdapter(getActivity()));
        mAdapter.addAdapter(getTitleAdapter("Customize"));
        mAdapter.addAdapter(mCustomizeAdapter = new CustomizeMainAdapter(getActivity()));
        mAdapter.addAdapter(new CustomizeSpinnerAdapter(getActivity()));
        mAdapter.addAdapter(new SingleItemAdapter(mContext, true, 0, ViewHolderFactory.TYPE.VHT_C_SEPARATOR));
        mAdapter.addAdapter(new CustomizeButtonAdapter(getActivity()));
        mRecyclerView.setAdapter(mAdapter);

        bindProductOptions(mProductActivity.mProductOptions);

    }

    private RecyclerAdapter<ViewHolder> getTitleAdapter(final String title) {
        return new SingleItemAdapter(getContext(), true, 0, ViewHolderFactory.TYPE.VHT_TITLE).setViewBinder(new SingleItemAdapter.IViewBinder() {
            @Override
            public void onNewView(int id, RecyclerView.ViewHolder holder) {

            }

            @Override
            public void onBindView(int id, RecyclerView.ViewHolder holder) {
                ((TextView) holder.itemView.findViewById(R.id.title)).setText(title);
            }
        });
    }

    public void bindProductOptions(ProductOptions options) {
        if (options != null) {
            mCustomizeAdapter.changeData(options.customisationOptions);
        }
    }

    class PBAdapter extends RecyclerAdapter<PBViewHolder> {

        private final Context context;

        public PBAdapter(Context context) {
            super(context, true);
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


    private class CustomizeMainAdapter extends RecyclerAdapter<CustomizeMainHolder> implements IResultListener<Map.Entry<String, String>> {

        List<Map.Entry<String, ArrayList<String>>> options;

        public CustomizeMainAdapter(Context context) {
            super(context, true);
        }

        @Override
        public int getItemViewType(int position) {
            return ViewHolderFactory.TYPE.VHT_C_MAIN;
        }

        @Override
        public void onBindViewHolder(CustomizeMainHolder holder, int position) {
            Map.Entry<String, ArrayList<String>> option = options.get(position);
            holder.setOptionSelectedListener(this);
            holder.bindUI(option);
        }

        @Override
        public int getItemCount() {
            return options == null ? 0 : options.size();
        }

        public void changeData(List<Map.Entry<String, ArrayList<String>>> optionsList) {
            options = optionsList;
            notifyDataSetChanged();
        }

        @Override
        public void onResult(Map.Entry<String, String> result) {
            mProductActivity.addCustomisation(result.getKey(), result.getValue());
        }
    }

    class CustomizeSpinnerAdapter extends RecyclerAdapter<ViewHolder> {


        public CustomizeSpinnerAdapter(Context context) {
            super(context, true);
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
            super(context, true);
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

            holder.itemView.findViewById(R.id.addToCart).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    ((ProductActivity) getActivity()).addToCart();
                    //// TODO: 30/3/16 kiran add to cart click
                }
            });
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }

}
