package com.goldadorn.main.activities.showcase;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.goldadorn.main.utils.TypefaceHelper;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

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
    SizeListAdapter sizeAdapterDialog;
    SingleItemAdapter sizeTitleDialog;
    SingleItemAdapter customizeTitleDialog;
    private TitleIconAdapter sizeTitleIconAdapter;
    private TitleIconAdapter metalTitleIconAdapter;
    private TitleIconAdapter stoneTitleIconAdapter;
    private ResourceReader rsRdr;
    private PositiveNegativeButtonAdapter pnbtn;
    private TitleTvAdapter titleTvAdapter;

    RecyclerAdapter<ViewHolder> viewForCustomizeCoach;

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
        mAdapter.addAdapter(viewForCustomizeCoach = getTitleAdapter("Customize"));
        mAdapter.addAdapter( titleTvAdapter = new TitleTvAdapter("Price Range: ", true));
        mAdapter.addAdapter(sizeTitleIconAdapter = new TitleIconAdapter("Size", true));
        mAdapter.addAdapter(metalTitleIconAdapter = new TitleIconAdapter("Metal", true));
        mAdapter.addAdapter(stoneTitleIconAdapter = new TitleIconAdapter("Diamond Quality", true));
        mAdapter.addAdapter(new CustomizeButtonAdapter(getActivity(), "Customize to lowest Price", autoCustomizeClick));
        mAdapter.addAdapter( cba = new CustomizeButtonAdapter(getActivity(), "Explore All Customization Option", customizeBtnClick));
        mRecyclerView.setAdapter(mAdapter);
        /***************************************************************************/


        /***************************************************************************/
        adapterForCustomizeDialog.addAdapter(getTitleAdapter("Customize", true, TITLE_CUSTOMIZE_DIALOG));
        /*adapterForCustomizeDialog.addAdapter(sizeTitleAdapter = (SingleItemAdapter)
                getTitleAdapter("Size", true, TITLE_SIZE_DIALOG));*/
        adapterForCustomizeDialog.addAdapter(sizeAdapterDialog = new SizeListAdapter(getContext(), true));
        adapterForCustomizeDialog.addAdapter(mCustomizeAdapter = new CustomizeMainAdapter(getActivity()));
        adapterForCustomizeDialog.addAdapter(pnbtn = new PositiveNegativeButtonAdapter(true));
        recyclerViewForCustomizeDialog.setAdapter(adapterForCustomizeDialog);
        /***************************************************************************/

        bindProductOptions(mProductActivity.mProductOptions);
    }
    CustomizeButtonAdapter cba;


    //boolean isHolderWasNull = false;

    private class TitleTvAdapter extends RecyclerAdapter<ViewHolder> {

        ViewHolder holder;
        String title;

        public TitleTvAdapter(String title, boolean enabled) {
            super(getActivity(), enabled);
            this.title = title;
        }

        @Override
        public int getItemViewType(int position) {
            return ViewHolderFactory.TYPE.VHT_TITLE_TV;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            this.holder = holder;
            ((TextView) holder.itemView.findViewById(R.id.tvTitle)).setText(title);
            UiRandomUtils.setTypefaceBold(holder.itemView.findViewById(R.id.tvTitle));
            TypefaceHelper.setFont(holder.itemView.findViewById(R.id.tvItem));
            /*if (isHolderWasNull) {
                bindProductOptions(mProductOption);
            }*/
        }

        public void setAmtData(String amtTxt){
            ((TextView) holder.itemView.findViewById(R.id.tvItem)).setText(amtTxt);
        }

        @Override
        public int getItemCount() {
            return 1;
        }

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
            TypefaceHelper.setFont(holder.itemView.findViewById(R.id.tvTitle));
            TypefaceHelper.setFont(holder.itemView.findViewById(R.id.tvItem));
            /*if (isHolderWasNull) {
                bindProductOptions(mProductOption);
            }*/
        }


        public void setIconDrawable(Drawable drawable, boolean hideTxtVisiblity) {
            /*if (holder == null) {
                isHolderWasNull = true;
                return;
            }*/
            holder.itemView.findViewById(R.id.tvItem).setVisibility(View.INVISIBLE);
            holder.itemView.findViewById(R.id.image).setVisibility(View.VISIBLE);
            holder.itemView.findViewById(R.id.tvItem).setVisibility(View.VISIBLE);
            if (!hideTxtVisiblity && drawable == null) {
                ((TextView) holder.itemView.findViewById(R.id.tvItem)).setText(mProductOption.mCustDefVals.getStoneDescTxt());
            }
            if (drawable != null) {
                ((ImageView) holder.itemView.findViewById(R.id.image)).setImageDrawable(drawable);
                try {
                    if (selectedMetalSwatch == null)
                    ((TextView) holder.itemView.findViewById(R.id.tvItem))
                            .setText(mProductOption.mCustDefVals.getDefMetalSwatch().getType());
                    else ((TextView) holder.itemView.findViewById(R.id.tvItem))
                            .setText(selectedMetalSwatch.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                /*UiRandomUtils.drawableFromUrl(((ImageView) holder.itemView.findViewById(R.id.image))
                        , mProductOption.mCustDefVals.getUrlStoneImg());*/
               /* Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        ImageView imageView = (ImageView) holder.itemView.findViewById(R.id.image);
                        imageView.setImageBitmap(bitmap);
                        //Drawable image = imageView.getDrawable();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                };
*/
                if (!TextUtils.isEmpty(mProductOption.mCustDefVals.getUrlStoneImg())) {
                    Picasso.with(getContext().getApplicationContext())
                            //.load(mProductOption.mCustDefVals.getUrlStoneImg())
                            .load(/*R.drawable.ic_diamond_small*/ProductOptions
                                    .getDiaQualityResId(mProductOption.mCustDefVals.getStoneDescTxt()))
                            .centerCrop()
                            .fit().memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                            .into((ImageView) holder.itemView.findViewById(R.id.image));
                }
            }
            //isHolderWasNull = false;

        }


        public void setExtraText(String text) {
           /* if (holder == null){
                isHolderWasNull = true;
                return;
            }*/
            holder.itemView.findViewById(R.id.image).setVisibility(View.INVISIBLE);
            holder.itemView.findViewById(R.id.tvItem).setVisibility(View.VISIBLE);
            ((TextView) holder.itemView.findViewById(R.id.tvItem)).setText(text);
            //isHolderWasNull = false;
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
                TypefaceHelper.setFont(holder.itemView.findViewById(R.id.title));
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
                TypefaceHelper.setFont(holder.itemView.findViewById(R.id.title));
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

    public void setPriceRange(String range){
        titleTvAdapter.setAmtData(range);
    }


    public void bindProductOptions(ProductOptions options) {
        if (options != null) {
            mProductOption = options;
            mCustomizeAdapter.changeData(options.customisationOptions);
            titleTvAdapter.setAmtData(options.range);
            metalTitleIconAdapter.setIconDrawable(rsRdr.getDrawableFromResId(mProductOption.mCustDefVals.getResIdMetal()), false);
            stoneTitleIconAdapter.setIconDrawable(null, false);
            cba.setLighterBg();

            if (options.priceBreakDown.size() > 0) {
                //mPriceAdapter.setEnabled(true);
                //mPriceAdapter.notifyDataSetChanged();
                /*if (!isHolderWasNull) {*/
                pbda.setEnabled(true);
                pbda.setList(getDataForPriceBreakDown(options));
                //}
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
                if (options.parsedSizeList.size() > 0)
                    sizeAdapterDialog.setData(/*getDataForSizeSpinner()*/options.parsedSizeList);
                else {
                    List<String> singleList = new ArrayList<>();
                    singleList.add(options.mCustDefVals.getSizeText());
                    sizeAdapterDialog.setData(singleList);
                }
            } else {
                sizeTitleIconAdapter.setExtraText("NA");
                List<String> naList = new ArrayList<>();
                naList.add("NA");
                sizeAdapterDialog.setData(naList);
            }
        }
    }


    public void updateTitleIconDefValues() {
        metalTitleIconAdapter.setIconDrawable(rsRdr.getDrawableFromResId(mProductOption.mCustDefVals.getResIdMetal()), false);
        stoneTitleIconAdapter.setIconDrawable(null, false);
        sizeTitleIconAdapter.setExtraText(mProductOption.mCustDefVals.getSizeText());
    }


    public void updateCustomizationData(CustomizationDisableList mCustomizeData) {
        List<Integer> metalDisableList = mCustomizeData.getMetalDisableList();
        List<Integer> stoneDisableList = mCustomizeData.getStoneDisableList();
        List<String> sizeDataList = mCustomizeData.getSizeDataList();
        if (metalDisableList != null) {
            //CustomizeMainHolder metal = (CustomizeMainHolder) recyclerViewForCustomizeDialog.findViewHolderForAdapterPosition(2);
            if (metalDisableList.size() > 0) {
                if (mCustomizeAdapter.metalCustHolder != null)
                    mCustomizeAdapter.metalCustHolder.setDisableList(metalDisableList);
            }
        }

        if (stoneDisableList != null) {
            //CustomizeMainHolder stone = (CustomizeMainHolder) recyclerViewForCustomizeDialog.findViewHolderForAdapterPosition(3);
            if (stoneDisableList.size() > 0) {
                if (mCustomizeAdapter.stoneCustHolder != null)
                    mCustomizeAdapter.stoneCustHolder.setDisableList(stoneDisableList);
            }
        }

        if (sizeDataList != null) {
            //CustomizeMainHolder size = (CustomizeMainHolder) recyclerViewForCustomizeDialog.findViewHolderForAdapterPosition(1);
            if (sizeDataList.size() > 0) {
                if (sizeAdapterDialog != null) {
                    sizeAdapterDialog.setData(sizeDataList);
                }
            }
        }
    }

    private Swatches.MixedSwatch selectedMetalSwatch;
    private Swatches.MixedSwatch selectedStoneSwatch;

    public Swatches.MixedSwatch getSelectedMetalSwatch() {
        return selectedMetalSwatch;
    }

    public Swatches.MixedSwatch getSelectedStoneSwatch() {
        return selectedStoneSwatch;
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

    public static final String PBD_total = "Total";
    public static final String PBD_making = "Making Charges";
    public static final String PBD_metal = "Metal";
    public static final String PBD_stone = "Stone";
    public static final String PBD_VAT = "VAT";
    public static final String PBD_Discount = "Discount";
    public static final String PBD_FinalPrice = "Final Price";

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

            if (name.contains(PBD_metal)) {

                //tb = tb.addLine(name+ "\t", price == -1 ? NO_DETAILS: priceUnit+String.valueOf(price));
                // total of tab spaces added = 12
                /*tblr.addRow(name+ "\t\t\t\t\t\t\t\t\t\t\t\t",  price < 0 ? NO_DETAILS: *//*priceUnit+" "+dcmf.format(price)*//*
                                RandomUtils.getIndianCurrencyFormat(Math.round(price), true)
                            *//*String.valueOf(price)*//*);*/

                if (!(price < 0)) {
                    PriceValueModel pvm = new PriceValueModel(name, price < 0 ? NO_DETAILS :
                            RandomUtils.getIndianCurrencyFormat(Math.round(price), true));
                    priceBreakDownList.add(pvm);
                }

               /* float tempVat = price < 0 ? 0 : price;
                vatTotal=vatTotal+tempVat;*/
                //name=name+"\t\t\t\t\t\t\t\t\t\t\t\t\t";
            } else if (name.contains(PBD_stone)) {
                //tb = tb.addLine(name+ "\t", price == -1 ? NO_DETAILS: priceUnit+String.valueOf(price));
                // total of tab spaces added = 12
                /*tblr.addRow(name+ "\t\t\t\t\t\t\t\t\t\t\t\t",  price < 0 ? NO_DETAILS: *//*priceUnit+" "+dcmf.format(price)*//*
                            *//*String.valueOf(Math.round(price))*//* RandomUtils.getIndianCurrencyFormat(Math.round(price), true));*/

                if (!(price < 0)) {
                    PriceValueModel pvm = new PriceValueModel(name, price < 0 ? NO_DETAILS :
                            RandomUtils.getIndianCurrencyFormat(Math.round(price), true));
                    priceBreakDownList.add(pvm);
                }

               /* float tempVat = price < 0 ? 0 : price;
                vatTotal=vatTotal+tempVat;*/
                   /* vatTotal=vatTotal+*//*price*//*price == -1 ? 0 : price;
                    name=name+"\t\t\t\t\t\t\t\t\t\t\t\t\t";*/
            } else if (name.contains(PBD_making)) {
                //tb = tb.addLine(name+ "   ", price == -1 ? NO_DETAILS: priceUnit+String.valueOf(price));
                // total of tab spaces added = 3
               /* tblr.addRow(name+ "\t\t\t", price < 0 ? NO_DETAILS: *//*priceUnit+" "+ dcmf.format(price)*//*
                            *//*String.valueOf(Math.round(price))*//*RandomUtils.getIndianCurrencyFormat(Math.round(price), true));*/

                if (!(price < 0)) {
                    PriceValueModel pvm = new PriceValueModel(name, price < 0 ? NO_DETAILS :
                            RandomUtils.getIndianCurrencyFormat(Math.round(price), true));
                    priceBreakDownList.add(pvm);
                }

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
                PriceValueModel pvm = new PriceValueModel(PBD_VAT, RandomUtils.getIndianCurrencyFormat(Math.round(vat), true));
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

        String totalDisplayPrice = /*((ProductActivity) getActivity()).getProductDisplayPrice()*/
                ((ProductActivity) getActivity()).mOverlayVH.product_price_slash.getText().toString();
       /* String totalRowTxtVal = total < 0 ? NO_DETAILS: *//*priceUnit +" "+dcmf.format(total)*//*
                    *//*String.valueOf(Math.round(total)*//*RandomUtils.getIndianCurrencyFormat(totalDisplayPrice, true);*/

        Log.d("djprod", "totalRow: " + /*RandomUtils.getIndianCurrencyFormat(totalDisplayPrice, true)*/totalDisplayPrice);
        PriceValueModel pvm = new PriceValueModel(PBD_total, /*RandomUtils.getIndianCurrencyFormat(totalDisplayPrice, true)*/totalDisplayPrice);
        priceBreakDownList.add(pvm);

        int discount = mProductOptions.discount;
        if (discount > 0) {
            String totalForCalc = ((ProductActivity) getActivity()).getProductDisplayPrice();
            double offerPrice = RandomUtils.getOfferPrice(discount, totalForCalc);
            double off = Double.parseDouble(totalForCalc) - offerPrice;
            /*String offerPrice = *//*RandomUtils.getOfferPrice(discount, totalDisplayPrice)*//*
                    ((ProductActivity) getActivity()).mOverlayVH.mProductCost.getText().toString();*/
            PriceValueModel pvmDiscount = new PriceValueModel(PBD_Discount, RandomUtils.getIndianCurrencyFormat(off, true));
            priceBreakDownList.add(pvmDiscount);

            //double finalPrice = Double.parseDouble(totalDisplayPrice) - offerPrice;
            String finalPrice = ((ProductActivity) getActivity()).mOverlayVH.mProductCost.getText().toString();
            PriceValueModel pvmFinal = new PriceValueModel(PBD_FinalPrice, finalPrice);
            priceBreakDownList.add(pvmFinal);
        }

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

    boolean canCheckForMetal;
    boolean canCheckForStone;

    private class CustomizeMainAdapter extends RecyclerAdapter<CustomizeMainHolder>
            implements CustomizeMainHolder.IResultListener<Map.Entry<OptionKey, /*OptionValue*/Swatches.MixedSwatch>> {

        //List<Map.Entry<OptionKey, ArrayList<OptionValue>>> options;
        Map<String, List<String>> paramsMap = new HashMap<>();
        List<Map.Entry<OptionKey, ArrayList<Swatches.MixedSwatch>>> options;
        ArrayList<Swatches.MixedSwatch> metalOptions;
        ArrayList<Swatches.MixedSwatch> stoneOptions;
        CustomizeMainHolder metalCustHolder;
        CustomizeMainHolder stoneCustHolder;

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
            if (option.getKey().keyID.equals("Metal")) {
                metalOptions = option.getValue();
                if (metalOptions.size() > 1)
                    canCheckForMetal = true;
                metalCustHolder = holder;
            } else if (option.getKey().keyID.equals("Diamond Quality")) {
                stoneOptions = option.getValue();
                /*if (stoneOptions.size() > 1)*/
                canCheckForStone = true;
                stoneCustHolder = holder;
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
                isMetalSelectionDone = true;
                selectedMetalSwatch = result.getValue();
                List<String> tempList = new ArrayList<>();
                tempList.add(ProductOptions.metalListForParam.get(metalOptions.indexOf(result.getValue())));
                paramsMap.put(ProductActivity.METAL, tempList);
            }
            if (result.getKey().keyID.equals("Diamond Quality")) {
                isStoneSelectionDone = true;
                selectedStoneSwatch = result.getValue();
                List<String> tempList = new ArrayList<>();
                tempList.add(ProductOptions.stoneListForParam.get(stoneOptions.indexOf(result.getValue())));
                paramsMap.put(ProductActivity.STONE, tempList);
            }
            ProductOptions.mCustDefVals.setSizeText(sizeAdapterDialog.sizeList.get(sizeAdapterDialog.mSelectedIndex));
            ((ProductActivity) getActivity()).dontCallNextTime = false;
            enableDoneIfNeeded();
            ((ProductActivity) getActivity()).sendCustomizationToServer(paramsMap, false);
        }
    }

    boolean isMetalSelectionDone;
    boolean isStoneSelectionDone;

    private void clearAllChecksForDone() {
        isMetalSelectionDone = false;
        isStoneSelectionDone = false;
        /*canCheckForMetal = false;
        canCheckForStone = false;*/
    }

    public void clearSelection(boolean onlyParamMap){
        mCustomizeAdapter.paramsMap = new HashMap<>();
        if (onlyParamMap)
            return;
        if (mCustomizeAdapter.metalCustHolder != null)
            mCustomizeAdapter.metalCustHolder.clearCurrentSelection();
        if (mCustomizeAdapter.stoneCustHolder != null)
            mCustomizeAdapter.stoneCustHolder.clearCurrentSelection();
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


        String text;
        View.OnClickListener listener;
        Button btn;
        ViewHolder holder;
        public CustomizeButtonAdapter(Context context, String textOnBtn, View.OnClickListener listener) {
            super(context, true);
            text = textOnBtn;
            this.listener = listener;
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
            TypefaceHelper.setFont(holder.itemView.findViewById(R.id.addToCart));
            this.holder = holder;
            btn = ((Button) holder.itemView.findViewById(R.id.addToCart));
            ((Button) holder.itemView.findViewById(R.id.addToCart)).setText(text);
            holder.itemView.findViewById(R.id.addToCart).setOnClickListener(
                    listener/*new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //((ProductActivity) getActivity()).addToCartNew(v);
                    displayCustomizationDialog();
                }
            }*/);
        }

        public void setLighterBg(){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                btn.setBackground(ResourceReader.getInstance(Application.getInstance())
                        .getDrawableFromResId(R.drawable.button_lighter));
            }
            else btn.setBackgroundDrawable(ResourceReader.getInstance(Application.getInstance())
                    .getDrawableFromResId(R.drawable.button_lighter));
            int temp = holder.itemView.getPaddingTop();
            holder.itemView.setPadding(temp, temp, temp, temp);
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }


    private Dialog customizationDialog;

    View.OnClickListener autoCustomizeClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Toast.makeText(Application.getInstance(), "Feature Coming Soon", Toast.LENGTH_SHORT).show();
            ((ProductActivity) getActivity()).sendMinAutoCustReq();
        }
    };

    View.OnClickListener customizeBtnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /*private void displayCustomizationDialog() {*/
                //clearSelection();
                if (!mProductOption.isCustomizationAvail()){
                    Toast.makeText(getContext(), "Customization is Not Available for this Product", Toast.LENGTH_SHORT).show();
                    return;
                }
                enableDoneIfNeeded();
                if (customizationDialog == null) {
                    customizationDialog = WindowUtils.getInstance(getActivity().getApplicationContext())
                            .displayViewDialog(getActivity(), recyclerViewForCustomizeDialog);
                }

                customizationDialog.show();
            //}
        }
    };

    private void enableDoneIfNeeded() {
        if (canCheckForStone && canCheckForMetal) {
            if (isMetalSelectionDone && isStoneSelectionDone)
                pnbtn.setPositiveBtnEnabled(true);
            else pnbtn.setPositiveBtnEnabled(false);
        } else if (canCheckForMetal) {
            if (isMetalSelectionDone)
                pnbtn.setPositiveBtnEnabled(true);
            else pnbtn.setPositiveBtnEnabled(false);
        } else if (canCheckForStone) {
            if (isStoneSelectionDone)
                pnbtn.setPositiveBtnEnabled(true);
            else pnbtn.setPositiveBtnEnabled(false);
        }
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

        TextView tvPositive;

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
            tvPositive = (TextView) holder.itemView.findViewById(R.id.tvPositive);
            tvPositive.setOnClickListener(positiveClick);
            TypefaceHelper.setFont(tvPositive, holder.itemView.findViewById(R.id.tvNegative));
            enableDoneIfNeeded();
        }

        public void setPositiveBtnEnabled(boolean isEnabled) {
            if (!isEnabled)
                tvPositive.setAlpha(0.3f);
            else tvPositive.setAlpha(1f);
            tvPositive.setEnabled(isEnabled);
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
                if (ProductOptions.rawSizeListVals.size() > 0) {
                    List<String> tempList = new ArrayList<>();
                    ProductOptions.mCustDefVals.setSizeText(sizeAdapterDialog.sizeList.get(sizeAdapterDialog.mSelectedIndex));
                    tempList.add(String.valueOf(ProductOptions.rawSizeListVals.get(sizeAdapterDialog.mSelectedIndex)));
                    mCustomizeAdapter.paramsMap.put(ProductActivity.SIZE, tempList);
                } else
                    ProductOptions.mCustDefVals.setSizeText(sizeAdapterDialog.sizeList.get(sizeAdapterDialog.mSelectedIndex));
                clearAllChecksForDone();
                ((ProductActivity) getActivity()).sendCustomizationToServer(mCustomizeAdapter.paramsMap, true);
                if (customizationDialog.isShowing())
                    customizationDialog.dismiss();
                //customizationDialog = null;
            }
        };
    }


    public class SizeListAdapter extends RecyclerAdapter<SizeViewHolder> implements View.OnClickListener {

        public List<String> sizeList = new ArrayList<>();
        SizeViewHolder holder;
        //SimpleAdapterForTv adapterForTv;
        public int mSelectedIndex;

        public SizeListAdapter(Context context, boolean enabled) {
            super(context, enabled);
        }

        public void setData(List<String> sizeList) {
            try {
                this.sizeList = sizeList;
                //notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            TypefaceHelper.setFont(holder.tvTitle, holder.tvSelectExtra, holder.tvAdapter);
            holder.ivImageExtra.setVisibility(View.GONE);
            //holder.tvSelectExtra.setVisibility(View.GONE);
            holder.ivAddRemove.setOnClickListener(ivAddClick);
            holder.tvTitle.setText("Size");
            initExtraLayout();
            defaultRect = new Rect(holder.ivAddRemove.getPaddingLeft(), holder.ivAddRemove.getPaddingTop()
                    , holder.ivAddRemove.getPaddingRight(), holder.ivAddRemove.getPaddingBottom());
            setPaddedRect();
            //Spinner spinner = ((Spinner) holder.itemView.findViewById(R.id.spinnerSize));
            //spinner.setOnItemSelectedListener(this);
            /*spinner.setAdapter(adapterForTv = new SimpleAdapterForTv(getContext(), R.layout.adapter_spinner_size_prod_customization,
                    R.id.tvAdapter, parsedSizeList));*/
        }

        private void initExtraLayout() {
            setDrawablesForArrow();
            holder.extraLayout.setVisibility(View.GONE);
            holder.ivDecrease.setOnClickListener(this);
            holder.ivIncrease.setOnClickListener(this);
            if (sizeList.size() > 0) {
                holder.tvAdapter.setText(sizeList.get(0));
                //holder.tvSelectExtra.setVisibility(View.VISIBLE);
                holder.tvSelectExtra.setText(sizeList.get(0));
                /*holder.tvSelectExtra.setPadding(0, 0,
                        (int) (5 * DisplayProperties.getInstance(Application.getInstance(),
                                DisplayProperties.ORIENTATION_PORTRAIT).getXPixelsPerCell()), 0 );*/
                mSelectedIndex = 0;
                //new code 16-09-2016  for SZYC issue
                if (ProductOptions.rawSizeListVals.size() > 0) {
                    if (!sizeList.get(0).equals("NA")) {
                    /*List<String> tempList = new ArrayList<>();
                    ProductOptions.mCustDefVals.setSizeText(sizeList.get(mSelectedIndex));
                    tempList.add(String.valueOf(ProductOptions.rawSizeListVals.get(mSelectedIndex)));
                    mCustomizeAdapter.paramsMap.put(ProductActivity.SIZE, tempList);*/
                        ((ProductActivity) getActivity()).getCartReqObj().setSize(String.valueOf(ProductOptions
                                .rawSizeListVals.get(mSelectedIndex)));
                    }
                }
            }/*else holder.tvSelectExtra.setVisibility(View.GONE);*/
        }

        View.OnClickListener ivAddClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sizeList.get(0).equalsIgnoreCase("na")) {
                    Toast.makeText(getContext(), "Size is Not Applicable for this Product", Toast.LENGTH_SHORT).show();
                    return;
                }
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
            if (sizeList.size() == 1) {
                Toast.makeText(getContext(), "Size is fixed for this Product", Toast.LENGTH_SHORT).show();
                return;
            }
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

            ((ProductActivity) getActivity()).getCartReqObj().setSize(String.valueOf(ProductOptions
                    .rawSizeListVals.get(mSelectedIndex)));
            holder.tvSelectExtra.setText(holder.tvAdapter.getText().toString());
            ((ProductActivity) getActivity()).dontCallNextTime = false;
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

    public void setNewTotalPrice(String price) {
        ArrayList<PriceValueModel> tempList = pbda.getOurList();
        //int j = tempList.size() - 1;
        int j = 0;
        for (j = tempList.size() - 1; j >= 0; j--) {
            PriceValueModel pvm = tempList.get(j);
            if (pvm.getTvTitle().equals("Total")) {
                pvm.setTvValue(price);
                tempList.set(j, pvm);
                break;
            }
        }
        pbda.setList(tempList);
    }


    public static String PBD_discountVal = "discountVal";
    public void setPriceBreakDown(HashMap<String, String> mapVals) {
        ArrayList<PriceValueModel> tempList = pbda.getOurList();
        int j = 0;
        for (PriceValueModel pvm : tempList) {
            if (pvm.getTvTitle().equals(PBD_Discount))
                mProductOption.discount = Integer.parseInt(mapVals.get(PBD_discountVal));
            pvm.setTvValue(RandomUtils.getIndianCurrencyFormat(mapVals.get(pvm.getTvTitle()), true));
            tempList.set(j, pvm);
            j++;
        }
        pbda.setList(tempList);
    }


    public String getPriceBreakDownParam(String key) {
        ArrayList<PriceValueModel> tempList = pbda.getOurList();
        for (PriceValueModel pvm : tempList) {
            if (pvm.getTvTitle().equals(key))
                return pvm.getTvValue();
        }
        return null;
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


        public ArrayList<PriceValueModel> getOurList() {
            return ourList;
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
            String title = ourList.get(position).getTvTitle();
            holder.tvTitle.setText(title);
            TypefaceHelper.setFont(holder.tvTitle, holder.tvValue);
            if (/*title.equals(PBD_Discount) || */title.equals(PBD_FinalPrice) || title.equals(PBD_total)) {
                UiRandomUtils.setTypefaceBold(holder.tvValue);
                UiRandomUtils.setTypefaceBold(holder.tvTitle);
                holder.tvTitle.setTextColor(Color.BLACK);
                holder.tvValue.setTextColor(Color.BLACK);
            }
            holder.tvValue.setText(ourList.get(position).getTvValue());
            if (title.equals(PBD_Discount)){
                holder.tvTitle.setText(title + " ("+ mProductOption.discount + "%) ");
                holder.tvValue.setText("- " + ourList.get(position).getTvValue());
            }

            if (title.equals(PBD_Discount) || title.equals(PBD_VAT) || title.equals(PBD_total) || title.equals(PBD_FinalPrice)){
                holder.lineView.setVisibility(View.VISIBLE);
            }else holder.lineView.setVisibility(View.GONE);
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

        public void setTvValue(String tvValue) {
            this.tvValue = tvValue;
        }
    }

}
