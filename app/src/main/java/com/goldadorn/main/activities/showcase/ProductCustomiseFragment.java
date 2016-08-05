package com.goldadorn.main.activities.showcase;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.assist.MergeRecycleAdapter;
import com.goldadorn.main.assist.RecyclerAdapter;
import com.goldadorn.main.assist.SingleItemAdapter;
import com.goldadorn.main.assist.ViewHolder;
import com.goldadorn.main.dj.model.CustomizationDisableList;
import com.goldadorn.main.dj.model.Swatches;
import com.goldadorn.main.dj.uiutils.DisplayProperties;
import com.goldadorn.main.dj.uiutils.ResourceReader;
import com.goldadorn.main.dj.uiutils.UiRandomUtils;
import com.goldadorn.main.dj.uiutils.WindowUtils;
import com.goldadorn.main.dj.utils.RandomUtils;
import com.goldadorn.main.model.OptionKey;
import com.goldadorn.main.model.ProductOptions;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
    MergeRecycleAdapter adapterForCustomizeDialog;
    RecyclerView recyclerViewForCustomizeDialog;
    Context mContext;
    private CustomizeMainAdapter mCustomizeAdapter;
    private ProductActivity mProductActivity;
    private SingleItemAdapter mPriceAdapter;
    private ProductOptions mProductOption;
    //private int density = 0;
    //private ArrayList<PriceValueModel> dataForPriceBreakDown;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mContext = getContext();
        mProductActivity = (ProductActivity) getActivity();
        return inflater.inflate(R.layout.fragment_customize, container, false);
    }

    PriceBreakDownAdapter pbda;
    SizeListSpinnerAdapter sizeAdapterDialog;
    SingleItemAdapter sizeTitleDialog;
    SingleItemAdapter customizeTitleDialog;
    private TitleIconAdapter sizeTitleIconAdapter;
    private TitleIconAdapter metalAdapter;
    private TitleIconAdapter stoneAdapter;
    private ResourceReader rsRdr;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerViewForCustomizeDialog = (RecyclerView) LayoutInflater.from(getActivity().getApplicationContext())
                .inflate(R.layout.layout_recyclerview, null);
        mRecyclerView = (RecyclerView) view;
        recyclerViewForCustomizeDialog.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        rsRdr = ResourceReader.getInstance(getActivity().getApplicationContext());

        //density = getResources().getDisplayMetrics().densityDpi;

        adapterForCustomizeDialog = new MergeRecycleAdapter(new ViewHolderFactory(getActivity()));
        mAdapter = new MergeRecycleAdapter(new ViewHolderFactory(getActivity()));
        mAdapter.addAdapter(getTitleAdapter("Price BreakDown"));
        //mAdapter.addAdapter(mPriceAdapter = new SingleItemAdapter(mContext, false, 0, ViewHolderFactory.TYPE.VHT_PB).setViewBinder(mPriceBinder));
        //ArrayList<PriceValueModel> tempList = new ArrayList<>();
        mAdapter.addAdapter(pbda = new PriceBreakDownAdapter(false, ViewHolderFactory.TYPE.VHT_PBCA));

        //mAdapter.addAdapter(sizeTitleAdapter = getTitleAdapter("Size", true));//// TODO: 10-07-2016
        /***************************************************************************/
        mAdapter.addAdapter(getTitleAdapter("Customize"));
        mAdapter.addAdapter(sizeTitleIconAdapter = new TitleIconAdapter("Size", true));
        mAdapter.addAdapter(metalAdapter = new TitleIconAdapter("Metal", true));
        mAdapter.addAdapter(stoneAdapter = new TitleIconAdapter("Stone", true));
        mAdapter.addAdapter(new CustomizeButtonAdapter(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        /***************************************************************************/


        /***************************************************************************/
        adapterForCustomizeDialog.addAdapter(getTitleAdapter("Customization", true, TITLE_CUSTOMIZE_DIALOG));
        /*adapterForCustomizeDialog.addAdapter(sizeTitleAdapter = (SingleItemAdapter)
                getTitleAdapter("Size", true, TITLE_SIZE_DIALOG));*/
        adapterForCustomizeDialog.addAdapter(sizeAdapterDialog = new SizeListSpinnerAdapter(getContext(), true));
        adapterForCustomizeDialog.addAdapter(mCustomizeAdapter = new CustomizeMainAdapter(getActivity()));
        adapterForCustomizeDialog.addAdapter(new PositiveNegativeButtonAdapter(true));
        recyclerViewForCustomizeDialog.setAdapter(adapterForCustomizeDialog);
        /***************************************************************************/

        bindProductOptions(mProductActivity.mProductOptions);
    }


    private class TitleIconAdapter extends RecyclerAdapter<ViewHolder> {

        ViewHolder holder;
        String title;

        public TitleIconAdapter(String title, boolean enabled) {
            super(getActivity(), enabled);
            this.title = title;
        }

        @Override
        public int getItemViewType(int position) {
            return ViewHolderFactory.TYPE.VHT_TITLE_ICON_TV;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            this.holder = holder;
            ((TextView) holder.itemView.findViewById(R.id.tvTitle)).setText(title);
        }

        public void setIconDrawable(Drawable drawable, boolean hideTxtVisiblity) {
            holder.itemView.findViewById(R.id.tvItem).setVisibility(View.INVISIBLE);
            holder.itemView.findViewById(R.id.image).setVisibility(View.VISIBLE);
            if (!hideTxtVisiblity) {
                holder.itemView.findViewById(R.id.tvItem).setVisibility(View.VISIBLE);
                ((TextView) holder.itemView.findViewById(R.id.tvItem)).setText(mProductOption.mCustDefVals.getStoneDescTxt());
            }
            if (drawable != null)
                ((ImageView) holder.itemView.findViewById(R.id.image)).setImageDrawable(drawable);
            else /*((ImageView) holder.itemView.findViewById(R.id.image))
                    .setImageDrawable*/
                UiRandomUtils.drawableFromUrl(((ImageView) holder.itemView.findViewById(R.id.image))
                        , mProductOption.mCustDefVals.getUrlStoneImg());

        }


        public void setExtraText(String text) {
            holder.itemView.findViewById(R.id.image).setVisibility(View.INVISIBLE);
            holder.itemView.findViewById(R.id.tvItem).setVisibility(View.VISIBLE);
            ((TextView) holder.itemView.findViewById(R.id.tvItem)).setText(text);
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }


    private final int TITLE_SIZE_DIALOG = 123;
    private final int TITLE_CUSTOMIZE_DIALOG = 124;

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

    private RecyclerAdapter<ViewHolder> getTitleAdapter(final String title, boolean enabled, final int which) {
        return new SingleItemAdapter(getContext(), enabled, 0, ViewHolderFactory.TYPE.VHT_TITLE).setViewBinder(new SingleItemAdapter.IViewBinder() {
            @Override
            public void onNewView(int id, RecyclerView.ViewHolder holder) {

            }

            @Override
            public void onBindView(int id, RecyclerView.ViewHolder holder) {
                ((TextView) holder.itemView.findViewById(R.id.title)).setText(title);
                /*if (which == TITLE_SIZE_DIALOG) {
                    setTextStyle((TextView) holder.itemView.findViewById(R.id.title), 16,
                            rsRdr.getColorFromResource(R.color.colorBlackDimText), Gravity.LEFT);
                } else */
                if (which == TITLE_CUSTOMIZE_DIALOG) {
                    setTextStyle((TextView) holder.itemView.findViewById(R.id.title), 18,
                            Color.BLACK, Gravity.CENTER);
                }
            }

            public void setTextStyle(TextView tv, float textsize, int textColor, int gravity) {
                //TextView tv = (TextView) holder.itemView.findViewById(R.id.title);
                if (textColor != 0)
                    tv.setTextColor(textColor);
                tv.setTextSize(textsize);
                tv.setGravity(gravity);
            }
        });
    }

    public void bindProductOptions(ProductOptions options) {
        if (options != null) {
            mProductOption = options;
            mCustomizeAdapter.changeData(options.customisationOptions);
            metalAdapter.setIconDrawable(rsRdr.getDrawableFromResId(mProductOption.mCustDefVals.getResIdMetal()), true);
            stoneAdapter.setIconDrawable(null, false);

            if (options.priceBreakDown.size() > 0) {
                //mPriceAdapter.setEnabled(true);
                //mPriceAdapter.notifyDataSetChanged();
                pbda.setEnabled(true);
                pbda.setList(getDataForPriceBreakDown(options));
                //pbda.notifyDataSetChanged();
            } else {
                //mPriceAdapter.setEnabled(false);
                pbda.setEnabled(false);
            }

            int sizeInInt = (int) mProductOption.size;
            if (sizeInInt != -1/*true*/) {
                sizeTitleIconAdapter.setEnabled(true);
                sizeTitleIconAdapter.setExtraText(options.mCustDefVals.getSizeText());
                sizeAdapterDialog.setEnabled(true);
                sizeAdapterDialog.setData(/*getDataForSizeSpinner()*/options.sizeList);//// TODO: 10-07-2016 very important to change
            } else {
                sizeTitleIconAdapter.setExtraText("NA");
                List<String> naList = new ArrayList<>();
                naList.add("NA");
                sizeAdapterDialog.setData(naList);
            }

        }
    }


    public void updateCustomizationData(CustomizationDisableList mCustomizeData) {
        List<Integer> metalDisableList = mCustomizeData.getMetalDisableList();
        List<Integer> stoneDisableList = mCustomizeData.getStoneDisableList();
        List<String> sizeDataList = mCustomizeData.getSizeDataList();
        if (metalDisableList != null) {
            CustomizeMainHolder metal = (CustomizeMainHolder) recyclerViewForCustomizeDialog.findViewHolderForAdapterPosition(1);
            metal.setDisableList(metalDisableList);
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
            double total = 0;
            float vatTotal = 0;
            /*SpannableStringBuilder builder = new SpannableStringBuilder();
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
                float totalTemp = price < 0 ? 0 : price;
                Log.d("djprod","price iteration amount: "+totalTemp);
                total = total + totalTemp;
                //Log.d("djprod","price iteration amount: "+total);

                String spaces="";
                for (int i=(name.length()+1);i<15;i++){
                    spaces=spaces+" ";
                }
                name=name+spaces;

                if(name.contains("Metal")){
                    //tb = tb.addLine(name+ "\t", price == -1 ? NO_DETAILS: priceUnit+String.valueOf(price));
                    // total of tab spaces added = 12
                    tblr.addRow(name+ "\t\t\t\t\t\t\t\t\t\t\t\t",  price < 0 ? NO_DETAILS: priceUnit+" "+dcmf.format(price)
                                    RandomUtils.getIndianCurrencyFormat(Math.round(price), true)
                            String.valueOf(price));
                    float tempVat = price < 0 ? 0 : price;
                    vatTotal=vatTotal+tempVat;
                    //name=name+"\t\t\t\t\t\t\t\t\t\t\t\t\t";
                }
                else if(name.contains("Stone")){
                    //tb = tb.addLine(name+ "\t", price == -1 ? NO_DETAILS: priceUnit+String.valueOf(price));
                    // total of tab spaces added = 12
                    tblr.addRow(name+ "\t\t\t\t\t\t\t\t\t\t\t\t",  price < 0 ? NO_DETAILS: priceUnit+" "+dcmf.format(price)
                            String.valueOf(Math.round(price)) RandomUtils.getIndianCurrencyFormat(Math.round(price), true));
                    float tempVat = price < 0 ? 0 : price;
                    vatTotal=vatTotal+tempVat;
                    vatTotal=vatTotal+priceprice == -1 ? 0 : price;
                    name=name+"\t\t\t\t\t\t\t\t\t\t\t\t\t";
                }else if(name.contains("Making Charges")){
                    //tb = tb.addLine(name+ "   ", price == -1 ? NO_DETAILS: priceUnit+String.valueOf(price));
                    // total of tab spaces added = 3
                    tblr.addRow(name+ "\t\t\t", price < 0 ? NO_DETAILS: priceUnit+" "+ dcmf.format(price)
                            String.valueOf(Math.round(price))RandomUtils.getIndianCurrencyFormat(Math.round(price), true));
                    float tempVat =  price < 0 ? 0 : price;
                    vatTotal=vatTotal+tempVat;
                    vatTotal=vatTotal+priceprice == -1 ? 0 : price;
                    if(density== DisplayMetrics.DENSITY_MEDIUM) {
                        name = name + "\t\t\t\t\t\t\t\t\t";
                    }else if(density== DisplayMetrics.DENSITY_HIGH){
                        name = name + "\t\t\t\t\t\t\t\t\t\t";
                    }
                    else if(density== DisplayMetrics.DENSITY_XHIGH){
                        name = name + "\t\t\t\t\t\t\t\t\t";
                    }else{
                        name = name + "\t\t\t\t\t\t\t\t\t";
                    }
                }else{
                    //price=(float)(vatTotal*0.01);
                    //tb = tb.addLine(name + "\t", price == -1 ? NO_DETAILS: priceUnit+String.valueOf(price));
                    // total of tab spaces added = 8
                    //float vat =(float)(vatTotal - (vatTotal/1.01));
                    double vat;
                    try {
                        double displayPrice = Double.parseDouble(((ProductActivity) getActivity()).getProductDisplayPrice());
                        vat = (displayPrice - (displayPrice/1.01));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        vat = 0.0;
                    }

                    tblr.addRow(name+ "\t\t\t\t\t\t\t\t", *priceUnit+" "+ dcmf.format(vat)
                            String.valueOf(Math.round(vat))RandomUtils.getIndianCurrencyFormat(Math.round(vat), true));
                    total = total + vat;
                    priceValue=String.valueOf(Math.round(price));
                    if(density== DisplayMetrics.DENSITY_MEDIUM) {
                        name = name + "\t\t\t\t\t\t\t\t\t";
                    }else if(density== DisplayMetrics.DENSITY_HIGH){
                        name = name + "\t\t\t\t\t\t\t\t\t\t\t\t";
                    }
                    else if(density== DisplayMetrics.DENSITY_XHIGH){
                        name = name + "\t\t\t\t\t\t\t\t\t\t\t\t";
                    }else{
                        name = name + "\t\t\t\t\t\t\t\t\t\t\t\t";
                    }

                }

                Log.e("iii---",name+"--"+name.length()+"--"+density);
                priceUnit = priceValue.equals(NO_DETAILS) ? "": priceUnit;
                builder.append(name+priceUnit);

                builder.append("\t");
                builder.append(priceValue);
                builder.append("\n");
            }
            //int index = builder.length();

            String mTotal="Total";
            String spaces="";
            for (int i=(mTotal.length()+1);i<15;i++){
                spaces=spaces+" ";
            }
            mTotal=mTotal+spaces;
            builder.append(mTotal);

            //tb = tb.addLine(mTotal + "\t", total == -1 ? NO_DETAILS: priceUnit+String.valueOf(total));
            // total of tab spaces added = 12
            //"<b>" + id + "</b> "
            String totalRowTxt = "Total"+"\t\t\t\t\t\t\t\t\t\t\t\t";

            String totalDisplayPrice = ((ProductActivity) getActivity()).getProductDisplayPrice();
            String totalRowTxtVal = total == -1 ? NO_DETAILS: priceUnit +" "+dcmf.format(total)
                    String.valueOf(Math.round(total)RandomUtils.getIndianCurrencyFormat(totalDisplayPrice, true);
            Log.d("djprod","totalRowTxtVal: "+totalRowTxtVal);

            tblr.addRow("Total"+"\t\t\t\t\t\t\t\t\t\t\t\t"totalRowTxt,
                    total == -1 ? NO_DETAILS: (priceUnit +" "+ String.valueOf(total)) totalRowTxtVal);

            builder.append("\t\t\t\t\t\t\t\t\t\t\t\t\t");
            builder.append(priceUnit);
            String priceValue=String.valueOf(Math.round(total));
            //  String priceValue =String.format(Locale.getDefault(),"%.2f",Math.round(total));
            for(int i = 8-priceValue.length();i>=0;i--){
                builder.append("\t");
            }
            builder.append("\t");
            builder.append(priceValue);
            builder.setSpan(new StyleSpan(Typeface.BOLD),index,builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            //holder.gold.setText(builder);
            //holder.gold.setText(tb.toString());
            String fullText = tblr.toString();
            //String boldTxt = totalRowTxt + totalRowTxtVal;

            holder.gold.setText(*//*tblr.toString()*//* fullText);*/

        }
    };

    public ArrayList<PriceValueModel> getDataForPriceBreakDown(ProductOptions mProductOptions) {

        ArrayList<Map.Entry<String, Float>> p = /*mProductOption.priceBreakDown*/mProductOptions.priceBreakDown;
        //String priceUnit = mProductOption.priceUnit;
        //double total = 0;
        //float vatTotal = 0;
        //SpannableStringBuilder builder = new SpannableStringBuilder();
        //TableFormatter tb = new TableFormatter();
        //TableBuilder tblr = new TableBuilder();
        ArrayList<PriceValueModel> priceBreakDownList = new ArrayList<>();
        for (Map.Entry<String, Float> entry : p) {
            String name = entry.getKey();

            //String priceValue;
            Float price = entry.getValue();
            //price = -1f;
           /* if (price == -1){
                priceValue = NO_DETAILS;
            }
            else priceValue = String.valueOf(Math.round(price));*/
            //float totalTemp = price < 0 ? 0 : price;
            Log.d("djprod", "price from server every iteration: " + price);
            //total = total + totalTemp;
            //Log.d("djprod","price iteration amount: "+total);

            //String spaces="";
                /*for (int i=(name.length()+1);i<15;i++){
                    spaces=spaces+" ";
                }
                name=name+spaces;*/

            if (name.contains("Metal")) {

                //tb = tb.addLine(name+ "\t", price == -1 ? NO_DETAILS: priceUnit+String.valueOf(price));
                // total of tab spaces added = 12
                /*tblr.addRow(name+ "\t\t\t\t\t\t\t\t\t\t\t\t",  price < 0 ? NO_DETAILS: *//*priceUnit+" "+dcmf.format(price)*//*
                                RandomUtils.getIndianCurrencyFormat(Math.round(price), true)
                            *//*String.valueOf(price)*//*);*/

                PriceValueModel pvm = new PriceValueModel(name, price < 0 ? NO_DETAILS :
                        RandomUtils.getIndianCurrencyFormat(Math.round(price), true));
                priceBreakDownList.add(pvm);

               /* float tempVat = price < 0 ? 0 : price;
                vatTotal=vatTotal+tempVat;*/
                //name=name+"\t\t\t\t\t\t\t\t\t\t\t\t\t";
            } else if (name.contains("Stone")) {
                //tb = tb.addLine(name+ "\t", price == -1 ? NO_DETAILS: priceUnit+String.valueOf(price));
                // total of tab spaces added = 12
                /*tblr.addRow(name+ "\t\t\t\t\t\t\t\t\t\t\t\t",  price < 0 ? NO_DETAILS: *//*priceUnit+" "+dcmf.format(price)*//*
                            *//*String.valueOf(Math.round(price))*//* RandomUtils.getIndianCurrencyFormat(Math.round(price), true));*/

                PriceValueModel pvm = new PriceValueModel(name, price < 0 ? NO_DETAILS :
                        RandomUtils.getIndianCurrencyFormat(Math.round(price), true));
                priceBreakDownList.add(pvm);

               /* float tempVat = price < 0 ? 0 : price;
                vatTotal=vatTotal+tempVat;*/
                   /* vatTotal=vatTotal+*//*price*//*price == -1 ? 0 : price;
                    name=name+"\t\t\t\t\t\t\t\t\t\t\t\t\t";*/
            } else if (name.contains("Making Charges")) {
                //tb = tb.addLine(name+ "   ", price == -1 ? NO_DETAILS: priceUnit+String.valueOf(price));
                // total of tab spaces added = 3
               /* tblr.addRow(name+ "\t\t\t", price < 0 ? NO_DETAILS: *//*priceUnit+" "+ dcmf.format(price)*//*
                            *//*String.valueOf(Math.round(price))*//*RandomUtils.getIndianCurrencyFormat(Math.round(price), true));*/

                PriceValueModel pvm = new PriceValueModel(name, price < 0 ? NO_DETAILS :
                        RandomUtils.getIndianCurrencyFormat(Math.round(price), true));
                priceBreakDownList.add(pvm);

                /*float tempVat =  price < 0 ? 0 : price;
                vatTotal=vatTotal+tempVat;*/
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
            } else {
                //price=(float)(vatTotal*0.01);
                //tb = tb.addLine(name + "\t", price == -1 ? NO_DETAILS: priceUnit+String.valueOf(price));
                // total of tab spaces added = 8
                //float vat =(float)(vatTotal - (vatTotal/1.01));
                double vat;
                try {
                    double displayPrice = Double.parseDouble(((ProductActivity) getActivity()).getProductDisplayPrice());
                    vat = (displayPrice - (displayPrice / 1.01));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    vat = 0.0;
                }

               /* tblr.addRow(name+ "\t\t\t\t\t\t\t\t", *//**priceUnit+" "+ dcmf.format(vat)*//*
                            *//*String.valueOf(Math.round(vat))*//*RandomUtils.getIndianCurrencyFormat(Math.round(vat), true));*/
                Log.d("djprod", "VAT - calc from formula: " + vat);
                PriceValueModel pvm = new PriceValueModel(name, RandomUtils.getIndianCurrencyFormat(Math.round(vat), true));
                priceBreakDownList.add(pvm);

                //total = total + vat;
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

        /*String mTotal="Total";
            *//*String spaces="";
            for (int i=(mTotal.length()+1);i<15;i++){
                spaces=spaces+" ";
            }
            mTotal=mTotal+spaces;
            builder.append(mTotal);*//*

        //tb = tb.addLine(mTotal + "\t", total == -1 ? NO_DETAILS: priceUnit+String.valueOf(total));
        // total of tab spaces added = 12
        //"<b>" + id + "</b> "
        String totalRowTxt = "Total"+"\t\t\t\t\t\t\t\t\t\t\t\t";*/

        String totalDisplayPrice = ((ProductActivity) getActivity()).getProductDisplayPrice();
       /* String totalRowTxtVal = total < 0 ? NO_DETAILS: *//*priceUnit +" "+dcmf.format(total)*//*
                    *//*String.valueOf(Math.round(total)*//*RandomUtils.getIndianCurrencyFormat(totalDisplayPrice, true);*/

        Log.d("djprod", "totalRow: " + RandomUtils.getIndianCurrencyFormat(totalDisplayPrice, true));
        PriceValueModel pvm = new PriceValueModel("Total", RandomUtils.getIndianCurrencyFormat(totalDisplayPrice, true));
        priceBreakDownList.add(pvm);

       /* tblr.addRow(*//*"Total"+"\t\t\t\t\t\t\t\t\t\t\t\t"*//*totalRowTxt,
                    *//*total == -1 ? NO_DETAILS: (priceUnit +" "+ String.valueOf(total))*//* totalRowTxtVal);

            *//*builder.append("\t\t\t\t\t\t\t\t\t\t\t\t\t");
            builder.append(priceUnit);
            String priceValue=String.valueOf(Math.round(total));*//*
        //  String priceValue =String.format(Locale.getDefault(),"%.2f",Math.round(total));
            *//*for(int i = 8-priceValue.length();i>=0;i--){
                builder.append("\t");
            }*//*
            *//*builder.append("\t");
            builder.append(priceValue);
            builder.setSpan(new StyleSpan(Typeface.BOLD),index,builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
*//*
        //holder.gold.setText(builder);
        //holder.gold.setText(tb.toString());
        String fullText = tblr.toString();
        //String boldTxt = totalRowTxt + totalRowTxtVal;

        holder.gold.setText(*//*tblr.toString()*//* fullText);*/

        return priceBreakDownList;
    }

    public List<String> getDataForSizeSpinner() {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 10; i++)
            list.add(String.valueOf(i));
        return list;
    }


    private class CustomizeMainAdapter extends RecyclerAdapter<CustomizeMainHolder>
            implements CustomizeMainHolder.IResultListener<Map.Entry<OptionKey, /*OptionValue*/Swatches.MixedSwatch>> {

        //List<Map.Entry<OptionKey, ArrayList<OptionValue>>> options;
        Map<String, List<String>> paramsMap = new HashMap<>();
        List<Map.Entry<OptionKey, ArrayList<Swatches.MixedSwatch>>> options;
        ArrayList<Swatches.MixedSwatch> metalOptions;
        ArrayList<Swatches.MixedSwatch> stoneOptions;

        public CustomizeMainAdapter(Context context) {
            super(context, true);
        }

        @Override
        public int getItemViewType(int position) {
            return ViewHolderFactory.TYPE.VHT_C_MAIN;
        }

        @Override
        public void onBindViewHolder(CustomizeMainHolder holder, int position) {
            //Map.Entry<OptionKey, ArrayList<OptionValue>> option = options.get(position);
            Map.Entry<OptionKey, ArrayList<Swatches.MixedSwatch>> option = options.get(position);
            if (position == 0)
                metalOptions = option.getValue();
            else if (position == 1){
                stoneOptions = option.getValue();
            }
            holder.setOptionSelectedListener(this);
            holder.bindUI(option);
        }

        @Override
        public int getItemCount() {
            return options == null ? 0 : options.size();
        }

        /*public void changeData(List<Map.Entry<OptionKey, ArrayList<OptionValue>>> optionsList) {*/
        public void changeData(List<Map.Entry<OptionKey, ArrayList<Swatches.MixedSwatch>>> optionsList) {
            options = optionsList;
            Log.d("changeData ", "" + optionsList.size());
            notifyDataSetChanged();
        }

        @Override
        public void onResult(Map.Entry<OptionKey, /*OptionValue*/Swatches.MixedSwatch> result) {
            //mProductActivity.addCustomisation(result.getKey(), result.getValue());
            /*if (!customizationDialog.isShowing()) {

            }*/
            if (result.getKey().keyID.equals("Metal")) {
                List<String> tempList = new ArrayList<>();
                tempList.add(ProductOptions.metalListForParam.get(metalOptions.indexOf(result.getValue())));
                paramsMap.put(ProductActivity.METAL, tempList);
            }
            if (result.getKey().keyID.equals("Diamond Quality")){
                List<String> tempList = new ArrayList<>();
                tempList.add(ProductOptions.stoneListForParam.get(stoneOptions.indexOf(result.getValue())));
                paramsMap.put(ProductActivity.STONE, tempList);
            }
            ((ProductActivity) getActivity()).sendCustomizationToServer(paramsMap);
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
                    //((ProductActivity) getActivity()).addToCartNew(v);
                    displayCustomizationDialog();
                }
            });
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }


    private Dialog customizationDialog;

    private void displayCustomizationDialog() {
        if (customizationDialog == null) {
            customizationDialog = WindowUtils.getInstance(getActivity().getApplicationContext())
                    .displayCustomizationDialog(getActivity(), recyclerViewForCustomizeDialog);
        }

        customizationDialog.show();
    }


    /*ViewConstructor.DialogButtonClickListener customizationDialogBtnClick = new ViewConstructor.DialogButtonClickListener() {
        @Override
        public void onPositiveBtnClicked(Dialog dialog, View btn) {

        }

        @Override
        public void onNegativeBtnClicked(Dialog dialog, View btn) {

        }
    };*/


    private class PositiveNegativeButtonAdapter extends RecyclerAdapter<ViewHolder> {

        public PositiveNegativeButtonAdapter(boolean enabled) {
            super(getActivity(), enabled);
        }

        @Override
        public int getItemViewType(int position) {
            return ViewHolderFactory.TYPE.VHT_BTN_POS_NEG;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.itemView.findViewById(R.id.tvNegative).setOnClickListener(negativeClick);
            holder.itemView.findViewById(R.id.tvPositive).setOnClickListener(positiveClick);
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        View.OnClickListener negativeClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// TODO: 01-08-2016
                if (customizationDialog.isShowing())
                    customizationDialog.dismiss();
            }
        };

        View.OnClickListener positiveClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// TODO: 01-08-2016
                List<String> tempList = new ArrayList<>();
                tempList.add(sizeAdapterDialog.sizeList.get(sizeAdapterDialog.mSelectedIndex));
                mCustomizeAdapter.paramsMap.put(ProductActivity.SIZE, tempList);
                ((ProductActivity) getActivity()).sendCustomizationToServer(mCustomizeAdapter.paramsMap);
                customizationDialog = null;
            }
        };
    }


    public class SizeListSpinnerAdapter extends RecyclerAdapter<SizeViewHolder> implements View.OnClickListener {

        private List<String> sizeList = new ArrayList<>();
        SizeViewHolder holder;
        //SimpleAdapterForTv adapterForTv;
        private int mSelectedIndex;

        public SizeListSpinnerAdapter(Context context, boolean enabled) {
            super(context, enabled);
        }

        public void setData(List<String> sizeList) {
            this.sizeList = sizeList;
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            return ViewHolderFactory.TYPE.VHT_SIZE_NEW;
        }

        private Rect rectForMinusSym;
        private Rect defaultRect;

        private void setPaddedRect() {
            DisplayProperties displayProperties = DisplayProperties.getInstance(Application.getInstance(),
                    DisplayProperties.ORIENTATION_PORTRAIT);
            int paddInPx = (int) (1.5 * displayProperties.getXPixelsPerCell());
            rectForMinusSym = new Rect(paddInPx, paddInPx, paddInPx, paddInPx);
        }

        @Override
        public void onBindViewHolder(SizeViewHolder holder, int position) {
            this.holder = holder;
            holder.ivImageExtra.setVisibility(View.GONE);
            holder.tvSelectExtra.setVisibility(View.GONE);
            holder.ivAddRemove.setOnClickListener(ivAddClick);
            holder.tvTitle.setText("Size");
            initExtraLayout();
            defaultRect = new Rect(holder.ivAddRemove.getPaddingLeft(), holder.ivAddRemove.getPaddingTop()
                    , holder.ivAddRemove.getPaddingRight(), holder.ivAddRemove.getPaddingBottom());
            setPaddedRect();
            //Spinner spinner = ((Spinner) holder.itemView.findViewById(R.id.spinnerSize));
            //spinner.setOnItemSelectedListener(this);
            /*spinner.setAdapter(adapterForTv = new SimpleAdapterForTv(getContext(), R.layout.adapter_spinner_size_prod_customization,
                    R.id.tvAdapter, sizeList));*/
        }

        private void initExtraLayout() {
            setDrawablesForArrow();
            holder.extraLayout.setVisibility(View.GONE);
            holder.ivDecrease.setOnClickListener(this);
            holder.ivIncrease.setOnClickListener(this);
            if (sizeList.size() > 0) {
                holder.tvAdapter.setText(sizeList.get(0));
                mSelectedIndex = 0;
            }
        }

        View.OnClickListener ivAddClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int visibility = holder.extraLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;
                if (visibility == View.GONE)
                    changeDrawable(true);
                else changeDrawable(false);
                holder.extraLayout.setVisibility(visibility);
            }
        };

        private void changeDrawable(boolean isAdd) {
            ResourceReader rsrdr = ResourceReader.getInstance(Application.getInstance());
            Drawable drawable;
            if (isAdd) {
                drawable = rsrdr.getDrawableFromResId(R.drawable.add);
                holder.ivAddRemove.setPadding(defaultRect.left, defaultRect.top, defaultRect.right, defaultRect.bottom);
            } else {
                drawable = rsrdr.getDrawableFromResId(R.drawable.minus);
                holder.ivAddRemove.setPadding(rectForMinusSym.left, rectForMinusSym.top, rectForMinusSym.right, rectForMinusSym.bottom);
            }
            drawable.setColorFilter(rsrdr.getColorFromResource(R.color.White),
                    PorterDuff.Mode.SRC_ATOP);
            holder.ivAddRemove.setImageDrawable(drawable);
        }

        private void setDrawablesForArrow() {
            int color = ResourceReader.getInstance(Application.getInstance()).getColorFromResource(R.color.colorPrimary);
            holder.ivDecrease.setImageDrawable(new IconicsDrawable(Application.getInstance())
                    .icon(GoogleMaterial.Icon.gmd_keyboard_arrow_down)
                    .color(color)
                    .sizeDp(20));
            holder.ivIncrease.setImageDrawable(new IconicsDrawable(Application.getInstance())
                    .icon(GoogleMaterial.Icon.gmd_keyboard_arrow_up)
                    .color(color)
                    .sizeDp(20));
        }

        @Override
        public int getItemCount() {
            return 1;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.ivDecrease) {
                if (mSelectedIndex == 0)
                    return;
                holder.tvAdapter.setText(sizeList.get(mSelectedIndex - 1));
                mSelectedIndex--;
            } else if (v.getId() == R.id.ivIncrease) {
                if (mSelectedIndex == (sizeList.size() - 1))
                    return;
                holder.tvAdapter.setText(sizeList.get(mSelectedIndex + 1));
                mSelectedIndex++;
            }
        }


        /*@Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // TODO: 09-07-2016
            mSelectedIndex = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }*/
    }


    private class PriceBreakDownAdapter extends RecyclerAdapter<MyViewHolderNew> {

        private ArrayList<PriceValueModel> ourList = new ArrayList<>();
        private final int mViewType;

        public PriceBreakDownAdapter(boolean enabled, int viewType) {
            super(getActivity(), enabled);
            mViewType = viewType;
        }

        /*public PriceBreakDownAdapter(ArrayList<PriceValueModel> ourList) {
            this.ourList = ourList;
        }*/

        @Override
        public int getItemViewType(int position) {
            return mViewType;
        }


        public void setList(ArrayList<PriceValueModel> ourList) {
            this.ourList = ourList;
            notifyDataSetChanged();
        }

       /* @Override
        public MyViewHolderNew onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_price_breakdown, parent, false);
            view.setEnabled(false);
            return new MyViewHolderNew(view);
        }*/

        @Override
        public int getItemCount() {
            return ourList.size();
        }

        @Override
        public void onBindViewHolder(MyViewHolderNew holder, int position) {
            holder.tvTitle.setText(ourList.get(position).getTvTitle());
            holder.tvValue.setText(ourList.get(position).getTvValue());
        }

        /*class MyViewHolderNew extends RecyclerView.ViewHolder{

            private TextView tvTitle, tvValue;
            public MyViewHolderNew(View itemView) {
                super(itemView);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                tvValue = (TextView) itemView.findViewById(R.id.tvValue);
            }
        }*/
    }


    private class PriceValueModel {
        private String tvTitle;
        private String tvValue;

        public PriceValueModel(String tvTitle, String tvValue) {
            this.tvTitle = tvTitle;
            this.tvValue = tvValue;
        }

        public String getTvTitle() {
            return tvTitle;
        }

        public String getTvValue() {
            return tvValue;
        }
    }

}
