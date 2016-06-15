package com.goldadorn.main.activities.showcase;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.util.Log;
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
import com.goldadorn.main.dj.utils.TableBuilder;
import com.goldadorn.main.model.OptionKey;
import com.goldadorn.main.model.OptionValue;
import com.goldadorn.main.model.ProductOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Vijith Menon on 18/3/16.
 */
public class ProductCustomiseFragment extends Fragment {
    private final static String TAG = ProductCustomiseFragment.class.getSimpleName();
    private final static boolean DEBUG = true;


    RecyclerView mRecyclerView;
    MergeRecycleAdapter mAdapter;
    Context mContext;
    private CustomizeMainAdapter mCustomizeAdapter;
    private ProductActivity mProductActivity;
    private SingleItemAdapter mPriceAdapter;
    private ProductOptions mProductOption;
    private int density= 0;

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
        density= getResources().getDisplayMetrics().densityDpi;

        mAdapter = new MergeRecycleAdapter(new ViewHolderFactory(getActivity()));
        mAdapter.addAdapter(mPriceAdapter = new SingleItemAdapter(mContext, false, 0, ViewHolderFactory.TYPE.VHT_PB).setViewBinder(mPriceBinder));
        mAdapter.addAdapter(getTitleAdapter("Customize"));
        mAdapter.addAdapter(mCustomizeAdapter = new CustomizeMainAdapter(getActivity()));
//        mAdapter.addAdapter(new CustomizeSpinnerAdapter(getActivity()));
//        mAdapter.addAdapter(new SingleItemAdapter(mContext, true, 0, ViewHolderFactory.TYPE.VHT_C_SEPARATOR));
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
            mProductOption = options;
            mCustomizeAdapter.changeData(options.customisationOptions);
            if (options.priceBreakDown.size() > 0) {
                mPriceAdapter.setEnabled(true);
                mPriceAdapter.notifyDataSetChanged();
            } else {
                mPriceAdapter.setEnabled(false);
            }
        }
    }

    private final String NO_DETAILS = "Detail Not Available";
    DecimalFormat dcmf = new DecimalFormat("0.#");
    private SingleItemAdapter.IViewBinder<PBViewHolder> mPriceBinder = new SingleItemAdapter.IViewBinder<PBViewHolder>() {
        @Override
        public void onNewView(int id, PBViewHolder holder) {

        }

        @Override
        public void onBindView(int id, PBViewHolder holder) {
            ArrayList<Map.Entry<String, Float>> p = mProductOption.priceBreakDown;
            String priceUnit = mProductOption.priceUnit;
            float total = 0;
            float vatTotal = 0;
            SpannableStringBuilder builder = new SpannableStringBuilder();
            //TableFormatter tb = new TableFormatter();
            TableBuilder tblr = new TableBuilder();
            for (Map.Entry<String, Float> entry : p) {
                String name = entry.getKey();

                String priceValue;
                Float price = entry.getValue();
                //price = -1f;
                if (price == -1){
                    priceValue = NO_DETAILS;
                }
                else priceValue = String.valueOf(Math.round(price));
                float totalTemp = price == -1 ? 0 : price;
                Log.d("djprod","price iteration amount: "+totalTemp);
                total = total + totalTemp;
                //Log.d("djprod","price iteration amount: "+total);

                String spaces="";
                /*for (int i=(name.length()+1);i<15;i++){
                    spaces=spaces+" ";
                }
                name=name+spaces;*/

                if(name.contains("Metal")){
                    //tb = tb.addLine(name+ "\t", price == -1 ? NO_DETAILS: priceUnit+String.valueOf(price));
                    // total of tab spaces added = 12
                    tblr.addRow(name+ "\t\t\t\t\t\t\t\t\t\t\t\t", price == -1 ? NO_DETAILS: priceUnit+" "+dcmf.format(price)
                            /*String.valueOf(price)*/);
                    float tempVat = price == -1 ? 0 : price;
                    vatTotal=vatTotal+tempVat;
                    //name=name+"\t\t\t\t\t\t\t\t\t\t\t\t\t";
                }
                else if(name.contains("Stone")){
                    //tb = tb.addLine(name+ "\t", price == -1 ? NO_DETAILS: priceUnit+String.valueOf(price));
                    // total of tab spaces added = 12
                    tblr.addRow(name+ "\t\t\t\t\t\t\t\t\t\t\t\t", price == -1 ? NO_DETAILS: priceUnit+" "+/*dcmf.format(price)*/
                            String.valueOf(Math.round(price)));
                    float tempVat = price == -1 ? 0 : price;
                    vatTotal=vatTotal+tempVat;
                   /* vatTotal=vatTotal+*//*price*//*price == -1 ? 0 : price;
                    name=name+"\t\t\t\t\t\t\t\t\t\t\t\t\t";*/
                }else if(name.contains("Making Charges")){
                    //tb = tb.addLine(name+ "   ", price == -1 ? NO_DETAILS: priceUnit+String.valueOf(price));
                    // total of tab spaces added = 3
                    tblr.addRow(name+ "\t\t\t", price == -1 ? NO_DETAILS: priceUnit+" "+ /*dcmf.format(price)*/
                            String.valueOf(Math.round(price)));
                    float tempVat = price == -1 ? 0 : price;
                    vatTotal=vatTotal+tempVat;
                    /*vatTotal=vatTotal+*//*price*//*price == -1 ? 0 : price;
                    if(density== DisplayMetrics.DENSITY_MEDIUM) {
                        name = name + "\t\t\t\t\t\t\t\t\t";
                    }else if(density== DisplayMetrics.DENSITY_HIGH){
                        name = name + "\t\t\t\t\t\t\t\t\t\t";
                    }
                    else if(density== DisplayMetrics.DENSITY_XHIGH){
                        name = name + "\t\t\t\t\t\t\t\t\t";
                    }else{
                        name = name + "\t\t\t\t\t\t\t\t\t";
                    }*/
                }else{
                    //price=(float)(vatTotal*0.01);
                    //tb = tb.addLine(name + "\t", price == -1 ? NO_DETAILS: priceUnit+String.valueOf(price));
                    // total of tab spaces added = 8
                    float vat =(float)(vatTotal - (vatTotal/1.01));
                    tblr.addRow(name+ "\t\t\t\t\t\t\t\t",priceUnit+" "+ /*dcmf.format(vat)*/
                            String.valueOf(Math.round(vat)));
                    total = total + vat;
                    /*priceValue=String.valueOf(Math.round(price));
                    if(density== DisplayMetrics.DENSITY_MEDIUM) {
                        name = name + "\t\t\t\t\t\t\t\t\t";
                    }else if(density== DisplayMetrics.DENSITY_HIGH){
                        name = name + "\t\t\t\t\t\t\t\t\t\t\t\t";
                    }
                    else if(density== DisplayMetrics.DENSITY_XHIGH){
                        name = name + "\t\t\t\t\t\t\t\t\t\t\t\t";
                    }else{
                        name = name + "\t\t\t\t\t\t\t\t\t\t\t\t";
                    }*/

                }

                /*Log.e("iii---",name+"--"+name.length()+"--"+density);
                priceUnit = priceValue.equals(NO_DETAILS) ? "": priceUnit;
                builder.append(name+priceUnit);

                builder.append("\t");
                builder.append(priceValue);
                builder.append("\n");*/
            }
            //int index = builder.length();

            String mTotal="Total";
            /*String spaces="";
            for (int i=(mTotal.length()+1);i<15;i++){
                spaces=spaces+" ";
            }
            mTotal=mTotal+spaces;
            builder.append(mTotal);*/

            //tb = tb.addLine(mTotal + "\t", total == -1 ? NO_DETAILS: priceUnit+String.valueOf(total));
            // total of tab spaces added = 12
            //"<b>" + id + "</b> "
            String totalRowTxt = "Total"+"\t\t\t\t\t\t\t\t\t\t\t\t";

            String totalRowTxtVal = total == -1 ? NO_DETAILS: priceUnit +" "+/*dcmf.format(total)*/
                    String.valueOf(Math.round(total));
            Log.d("djprod","totalRowTxtVal: "+totalRowTxtVal);

            tblr.addRow(/*"Total"+"\t\t\t\t\t\t\t\t\t\t\t\t"*/totalRowTxt,
                    /*total == -1 ? NO_DETAILS: (priceUnit +" "+ String.valueOf(total))*/ totalRowTxtVal);

            /*builder.append("\t\t\t\t\t\t\t\t\t\t\t\t\t");
            builder.append(priceUnit);
            String priceValue=String.valueOf(Math.round(total));*/
            //  String priceValue =String.format(Locale.getDefault(),"%.2f",Math.round(total));
            /*for(int i = 8-priceValue.length();i>=0;i--){
                builder.append("\t");
            }*/
            /*builder.append("\t");
            builder.append(priceValue);
            builder.setSpan(new StyleSpan(Typeface.BOLD),index,builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
*/
            //holder.gold.setText(builder);
            //holder.gold.setText(tb.toString());
            String fullText = tblr.toString();
            //String boldTxt = totalRowTxt + totalRowTxtVal;

            holder.gold.setText(/*tblr.toString()*/ fullText);

        }
    };


    private class CustomizeMainAdapter extends RecyclerAdapter<CustomizeMainHolder> implements IResultListener<Map.Entry<OptionKey, OptionValue>> {

        List<Map.Entry<OptionKey, ArrayList<OptionValue>>> options;

        public CustomizeMainAdapter(Context context) {
            super(context, true);
        }

        @Override
        public int getItemViewType(int position) {
            return ViewHolderFactory.TYPE.VHT_C_MAIN;
        }

        @Override
        public void onBindViewHolder(CustomizeMainHolder holder, int position) {
            Map.Entry<OptionKey, ArrayList<OptionValue>> option = options.get(position);
            holder.setOptionSelectedListener(this);
            holder.bindUI(option);
        }

        @Override
        public int getItemCount() {
            return options == null ? 0 : options.size();
        }

        public void changeData(List<Map.Entry<OptionKey, ArrayList<OptionValue>>> optionsList) {
            options = optionsList;
            Log.d("changeData ", "" + optionsList.size());
            notifyDataSetChanged();
        }

        @Override
        public void onResult(Map.Entry<OptionKey, OptionValue> result) {
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
                    ((ProductActivity) getActivity()).addToCartNew(v);
                }
            });
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }

}
