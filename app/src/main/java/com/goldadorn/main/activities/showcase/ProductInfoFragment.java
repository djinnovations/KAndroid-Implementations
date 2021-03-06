package com.goldadorn.main.activities.showcase;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.dj.model.TitleDescImageDataObj;
import com.goldadorn.main.dj.uiutils.UiRandomUtils;
import com.goldadorn.main.dj.utils.RandomUtils;
import com.goldadorn.main.model.Collection;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.model.ProductInfo;
import com.goldadorn.main.model.StoneDetail;
import com.goldadorn.main.model.User;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.LikeResponse;
import com.goldadorn.main.utils.ImageFilePath;
import com.goldadorn.main.utils.TypefaceHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Vijith Menon on 18/3/16.
 */
public class ProductInfoFragment extends Fragment {


    public static final String MAKING_CHARGES = "makingcharges";
    public static final String VAT = "VAT";
    public static final String DISCOUNT = "Discount";
    public static final String GRAND_TOTAL = "GrandTotal";

    @Bind(R.id.product_owner_name)
    TextView mProductOwnerName;
    @Bind(R.id.followButton)
    ImageView followButton;
    @Bind(R.id.collection_name)
    TextView mCollectionName;
    /*@Bind(R.id.collection_style_desc)
    TextView mCollectionStyle;*/
    @Bind(R.id.description_desc)
    TextView mdescription;
    @Bind(R.id.product_detail_desc)
    TextView mProductDetail;
    /*@Bind(R.id.paymentModesAvail_desc)
    TextView paymentModesAvail_desc;
    @Bind(R.id.warrantyInfo_desc)
    TextView warrantyInfo_desc;
    @Bind(R.id.moneyBackPolicy_desc)
    TextView moneyBackPolicy_desc;
    @Bind(R.id.certificateType_desc)
    TextView certificateType_desc;
    @Bind(R.id.estimatedDelTime_desc)
    TextView estimatedDelTime_desc;*/

    @Bind(R.id.container_table)
    LinearLayout mTableContainer;


    private User mUser;
    private Product mProduct;
    View mChileview = null;

    private float mTotalPrice = 0;
    ProductActivity mProductActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mProductActivity = (ProductActivity) getActivity();
        mProduct = mProductActivity.mProduct;
        mUser = mProductActivity.mUser;
        return inflater.inflate(R.layout.fragment_product_info, container, false);
    }


    /*public void setAllDescription(ArrayList<String> listOfDescription) {
        if (listOfDescription == null)
            return;
        if (listOfDescription.size() < 5)
            return;
        warrantyInfo_desc.setText(listOfDescription.get(0) == null ? "" : listOfDescription.get(0));
        moneyBackPolicy_desc.setText(listOfDescription.get(1) == null ? "" : listOfDescription.get(1));
        certificateType_desc.setText(listOfDescription.get(2) == null ? "" : listOfDescription.get(2));
        estimatedDelTime_desc.setText(listOfDescription.get(3) == null ? "" : listOfDescription.get(3)*//* + " days"*//*);
        paymentModesAvail_desc.setText(listOfDescription.get(4) == null ? "" : listOfDescription.get(4));
    }*/

    private void setFontTypeface(){
        TypefaceHelper.setFont(mProductOwnerName, mCollectionName, mdescription, mProductDetail);
    }


    private View getTitleMessageView(View child, ViewGroup parent , TitleDescImageDataObj dataObj) {
        TitleTextImageHolder holder;
        if (child == null){
            child = LayoutInflater.from(getContext()).inflate(R.layout.title_text_image, parent, false);
            holder = new TitleTextImageHolder(child);
            child.setTag(holder);
        }
        else holder = (TitleTextImageHolder) child.getTag();
        holder.bindView(dataObj);
        return child;
    }


    class TitleTextImageHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.desc)
        TextView desc;
        @Bind(R.id.ivImage)
        ImageView ivImage;

        public TitleTextImageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            UiRandomUtils.setTypefaceBold(title);
            TypefaceHelper.setFont(desc);
        }

        public void bindView(TitleDescImageDataObj dataObj){
            title.setText(dataObj.getTitle());
            desc.setText(dataObj.getDescription());
            if (!TextUtils.isEmpty(dataObj.getUrl())) {
                Picasso.with(getContext()).load(dataObj.getUrl()).into(ivImage);
            }
            else ivImage.setVisibility(View.GONE);
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        /*((TextView) view.findViewById(R.id.collection_style).findViewById(R.id.title)).setText(
                "Collection Style");*/
        ((TextView) view.findViewById(R.id.description).findViewById(R.id.title)).setText(
                "Description");
        setFontTypeface();
        UiRandomUtils.setTypefaceBold(view.findViewById(R.id.description).findViewById(R.id.title)
                , view.findViewById(R.id.product_detail_style).findViewById(R.id.title));

        //followButton.setVisibility(View.GONE);
        /*((TextView) view.findViewById(R.id.paymentModesAvail).findViewById(R.id.title)).setText(
                "Payment Modes Available");
        ((TextView) view.findViewById(R.id.warrantyInfo).findViewById(R.id.title)).setText(
                "Warranty Information");
        ((TextView) view.findViewById(R.id.moneyBackPolicy).findViewById(R.id.title)).setText(
                "Money Back Policy");
        ((TextView) view.findViewById(R.id.certificateType).findViewById(R.id.title)).setText(
                "Certificate Type");
        ((TextView) view.findViewById(R.id.estimatedDelTime).findViewById(R.id.title)).setText(
                "Estimated Delivery Time");*/

        ((TextView) view.findViewById(R.id.product_detail_style).findViewById(R.id.title)).setText(
                "Product Detail");

        mCollectionName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getActivity().finish();
                ((ProductActivity) getActivity()).launchCollectionScreen();
            }
        });
        mProductOwnerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProductActivity) getActivity()).launchDesignerScreen();
            }
        });

        if (mUser != null) {
            if (!TextUtils.isEmpty(mUser.name)) {
                mProductOwnerName.setText(mUser.name.trim());
                UiRandomUtils.underLineTv(mProductOwnerName, 0, mProductOwnerName.length());
            }
            followButton.setSelected(mUser.isFollowed);
            followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    v.setEnabled(false);
                    final boolean isFollowing = v.isSelected();
                    UIController.follow(getActivity(), mUser, !isFollowing,
                            new IResultListener<LikeResponse>() {
                                @Override
                                public void onResult(LikeResponse result) {
                                    v.setEnabled(true);
                                    v.setSelected(result.success != isFollowing);
                                    if (isFollowing) {
                                        mUser.isFollowed = false;
                                        mUser.followers_cnt = mUser.followers_cnt - 1;
                                    } else {
                                        mUser.isFollowed = true;
                                        mUser.followers_cnt = mUser.followers_cnt + 1;
                                    }
                                    // mProductActivity.mFollower();
                                }
                            });
                }
            });
        } else {
            view.findViewById(R.id.owner_layout).setVisibility(View.GONE);
        }
        bindCollectionUI(mProductActivity.mCollection);
        if (mProductActivity != null && mProductActivity.mProductInfo != null) {
            bindProductInfo(mProductActivity.mProductInfo);

        }
        //mdescription.setText(mProduct.description);

    }

    private final int TYPE_GOLD = 8123;

    @SuppressLint("StringFormatInvalid")
    public void bindProductInfo(ProductInfo summary) {
        if (summary != null) {
            //setAllDescription(summary.new5details);
            //int height = summary.getDisplayHeight() == -1 ? "NA" : ;
            mProductDetail.setText(getString(R.string.product_desc, summary.code,
                    summary.getDisplayHeight(), summary.getDisplayWidth(),
                    summary.getDisplayWeight()));
            mdescription.setText(summary.description);
            List<StoneDetail> rows = summary.stonesDetails;
            if (summary.productmaking_charges < 0) {
                //mTableContainer.setVisibility(View.GONE);
                create2RowTable(summary, rows);
                //// TODO: 05-09-2016  do  nothting for now 
            }//rows = new ArrayList<>();//change
           /* if (rows.size() == 0) {
                mTableContainer.setVisibility(View.GONE);
            }*/
            else {
                mTableContainer.setVisibility(View.VISIBLE);
                //StoneDetail t = rows.get(0);
                TableRowHolder head = new TableRowHolder(mTableContainer.findViewById(R.id.heading));
                TypefaceHelper.setFont(mTableContainer.findViewById(R.id.heading));
                head.setTextColor(Color.WHITE);
                head.setTextSize(getResources().getDimension(R.dimen.ts_primary));
                head.itemView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                head.component.setText("Component");
                TypefaceHelper.setFont(head.component, head.rate, head.weight, head.price, head.offer_price);
                head.rate.setText("Rate\n(" + "INR/weight" + ")");//to be changed from backend
                head.weight.setText("Weight");
                //(" + t.weightunit + ");
                head.price.setText("Value\n(" + /*t.rateunit*/"INR" + ")");//to be changed from backend
                head.offer_price.setText("Offer Price");

                int childPos = 1;
                int pos = 0;
                String type = "";
                /*Add the gold details*/

                mChileview = mTableContainer.getChildAt(childPos);
                View mGoldHeading = getRowView(summary.metalType, mChileview, mTableContainer, false);
                mTableContainer.addView(mGoldHeading);
                childPos++;

                StoneDetail details = new StoneDetail();
                details.color = summary.metalType;
                details.clarity = summary.metalPurity + "" + summary.metalPurityInUnits;
                details.number = /*-1*/TYPE_GOLD;
                details.metalWeightUnits = summary.metalWeightUnits;
                details.price = summary.metalrate;
                details.weight = summary.metalWeight;
                View mGoldDetails = getRowView(details, mChileview, mTableContainer, false);
                mTableContainer.addView(mGoldDetails);
                childPos++;

                 /*Add the remain metal details*/
                if (rows.size() > 0) {
                    do {
                        StoneDetail detail = rows.get(pos);
                        View child = mTableContainer.getChildAt(childPos);
                        View v;
                        if (detail.type.equals(type)) {
                            v = getRowView(detail, child, mTableContainer, false);
                            pos++;
                        } else {
                            v = getRowView(type = detail.type, child, mTableContainer, false);
                        }
                        childPos++;
                        v.setVisibility(View.VISIBLE);
                        if (child == null) {
                            mTableContainer.addView(v);
                        }
                    } while (pos < rows.size());
                }
                /*Caliculate the total,vat,making charges and discount*/
                childPos++;
                mChileview = mTableContainer.getChildAt(childPos);
                View mMakingChanrges = makingVatTotal(MAKING_CHARGES, mChileview, mTableContainer);
                mTableContainer.addView(mMakingChanrges);
                childPos++;
                mChileview = mTableContainer.getChildAt(childPos);
                View mVat = makingVatTotal(VAT, mChileview, mTableContainer);
                mTableContainer.addView(mVat);
                childPos++;
                mChileview = mTableContainer.getChildAt(childPos);
                View mDiscount = makingVatTotal(DISCOUNT, mChileview, mTableContainer);
                mTableContainer.addView(mDiscount);
                childPos++;
                mChileview = mTableContainer.getChildAt(childPos);
                View mGrandTotal = makingVatTotal(GRAND_TOTAL, mChileview, mTableContainer);
                mTableContainer.addView(mGrandTotal);


                if (mTableContainer.getChildCount() > childPos) {
                    for (int i = childPos; i < mTableContainer.getChildCount(); i++) {
                        mTableContainer.getChildAt(i).setVisibility(View.GONE);
                    }
                }
            }
            int moreOptChildCount = 0;
            ArrayList<String> titles = new ArrayList<>();
            if (summary.semiPreciousString != null)
                titles.add("Semi-Precious Stones");
            //titles.add("Collection Style");
            titles.add("Payment Modes Available");
            titles.add("Buyback & Warranty Policy");
            titles.add("Money Back Policy");
            titles.add("Certificate Type");
            if (summary.chainDetail != null)
                titles.add("Chain Included");
            titles.add("Estimated Delivery Time");
            if (titles.contains("Semi-Precious Stones")) {
                summary.new5details.put("Semi-Precious Stones", summary.semiPreciousString);
                summary.new5details.put("Collection Style", mProductActivity.mCollection.category);
            } else summary.new5details.put("Collection Style", mProductActivity.mCollection.category);
            if (titles.contains("Chain Included")){
                summary.new5details.put("Chain Included", summary.chainDetail);
            }
            String url = null;
            for (String txt: titles){
                if (moreOptChildCount == titles.indexOf("Certificate Type")) {
                    if (mProduct.hasCertificate)
                    url = ImageFilePath.getDesignerCertUrl(mProduct.userId);
                }
                else url = null;
                TitleDescImageDataObj dataObj = new TitleDescImageDataObj(txt, summary.new5details.get(txt), url);
                moreOptionContainer.addView(getTitleMessageView(moreOptionContainer.getChildAt(moreOptChildCount), moreOptionContainer, dataObj));
                moreOptChildCount++;
            }
        }
    }

    @Bind(R.id.moreOptionContainer)
    LinearLayout moreOptionContainer;

    private View getRowView(Object obj, View convertView, LinearLayout parent, boolean display2Rows) {
        TableRowHolder holder = null;
        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(getContext());
            convertView = mInflater.inflate(R.layout.table_row, parent, false);
            holder = new TableRowHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (TableRowHolder) convertView.getTag();
        }
        TypefaceHelper.setFont(holder.component, holder.rate, holder.offer_price, holder.price
                , holder.weight, holder.component);
        int grey = getResources().getColor(R.color.cb_dark_grey);
        int gold = getResources().getColor(R.color.colorPrimary);
        if (display2Rows) {
            holder.price.setVisibility(View.INVISIBLE);
            holder.rate.setVisibility(View.INVISIBLE);
        }
        if (obj instanceof StoneDetail) {
            holder.itemView.setBackgroundColor(Color.WHITE);
            holder.setTextColor(grey);
            holder.setTextSize(getResources().getDimensionPixelSize(R.dimen.ts_secondary));
            StoneDetail detail = (StoneDetail) obj;
            if (detail.number == TYPE_GOLD) {
                //for gold
                holder.component.setText(detail.color + " - " + detail.clarity);
                holder.rate.setText(RandomUtils.getIndianCurrencyFormat(detail.price, true) + "/" +/*detail.metalWeightUnits*/"gm");//changed djphy
                holder.weight.setText(" " + detail.weight + " " + /*detail.metalWeightUnits*/"gm");
            } else {
                //for gstone
                String num;
                if (detail.number < 0)
                    num = "";
                else num = " : "+String.valueOf(detail.number)+" nos";
                holder.component.setText(detail.color + "-" + detail.clarity + num);
                holder.rate.setText(RandomUtils.getIndianCurrencyFormat(detail.price, true) + "/" +/*detail.weightunit*/"ct");//djphy
                holder.weight.setText(" " + detail.weight + " " + /*detail.weightunit*/"ct");//changed djphy
            }

            ;//changed djphy
            float mValue = detail.price * detail.weight;
            holder.price.setText(" " + /*Math.round(mValue)*/ RandomUtils.getIndianCurrencyFormat(mValue, true));
            holder.offer_price.setText(" " + ((int) mValue));
            mTotalPrice = mTotalPrice + Math.round(mValue);

           /* holder.rate.setText(detail.rateunit + " " + detail.price);
            holder.weight.setText(detail.weightunit + " " + detail.weight);
            float mValue= detail.price * detail.weight;
            holder.price.setText(detail.rateunit + " " + mValue);
            holder.offer_price.setText(detail.rateunit + " " + 0.00);*/
        } else {
            holder.itemView.setBackgroundColor(grey);
            holder.setTextColor(gold);
            holder.setTextSize(getResources().getDimension(R.dimen.ts_secondary));
            holder.component.setText((String) obj);
            holder.rate.setText("");
            holder.weight.setText("");
            holder.price.setText("");
            holder.offer_price.setText("");
        }
        return holder.itemView;
    }


    private View makingVatTotal(String mType, View convertView, LinearLayout parent) {
        TableRowHolder holder = null;
        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(getContext());
            convertView = mInflater.inflate(R.layout.table_row_discount, parent, false);
            holder = new TableRowHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (TableRowHolder) convertView.getTag();
        }
        TypefaceHelper.setFont(holder.component, holder.rate, holder.weight
        , holder.offer_price, holder.price);
        holder.itemView.setBackgroundColor(Color.WHITE);
        if (mType.equalsIgnoreCase(MAKING_CHARGES)) {
            holder.component.setTextColor(getResources().getColor(R.color.controlColor));
            //holder.component.setTextSize(getResources().getDimension(R.dimen.ts_secondary));
            holder.component.setTypeface(null, Typeface.BOLD);
            mTotalPrice = mTotalPrice + (int) (mProductActivity.mProductInfo.productmaking_charges * mProductActivity.mProductInfo.weight);
            holder.component.setText("Making \nCharges");
            holder.rate.setText("");
            holder.weight.setText(" ");
            //float makingChanges = Math.round(mProductActivity.mProductInfo.productmaking_charges * mProductActivity.mProductInfo.weight);
            String makingChanges = ((ProductActivity) getActivity()).getMakingCharges();
            holder.price.setText(/*" "+(int)makingChanges*//* RandomUtils.getIndianCurrencyFormat(makingChanges, true)*/makingChanges);
            //holder.offer_price.setText(" " + (int) makingChanges);
        } else if (mType.equalsIgnoreCase(VAT)) {
            holder.component.setTextColor(getResources().getColor(R.color.controlColor));
            //holder.component.setTextSize(getResources().getDimension(R.dimen.ts_secondary));
            holder.component.setTypeface(null, Typeface.BOLD);
            Log.e("iii--", mTotalPrice + "");
            /*double mVat;
            try {
                double displayPrice = Double.parseDouble(((ProductActivity) getActivity()).getProductDisplayPrice());
                mVat = displayPrice - (displayPrice / 1.01);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                mVat = 0.0;
            }*/
            String mVat = ((ProductActivity) getActivity()).getVAT();
            //double mVat=/*(mTotalPrice)*0.01*/ RandomUtils.getIndianCurrencyFormat();
            //mTotalPrice=mTotalPrice+((float)(mTotalPrice*0.01));
            holder.component.setText(VAT);
            holder.rate.setText("");
            holder.weight.setText(" ");
            holder.price.setText(/*" "+String.valueOf(((int)mVat))*//* RandomUtils.getIndianCurrencyFormat(mVat, true)*/mVat);
            holder.offer_price.setVisibility(View.GONE);
            //holder.offer_price.setText(" " + String.valueOf(((int) mVat)));
        } else if (mType.equalsIgnoreCase(DISCOUNT)) {
            holder.component.setTextColor(getResources().getColor(R.color.red));
            holder.component.setTypeface(null, Typeface.BOLD);
            holder.offer_price.setTextColor(getResources().getColor(R.color.controlColor));
            holder.offer_price.setTypeface(null, Typeface.BOLD);

            holder.component.setText(DISCOUNT + /*" \n0%"*/"\n" + String.valueOf(mProduct.discount) + "%");
            holder.rate.setText("");
            holder.weight.setText(" ");
            holder.price.setText(" ");
            holder.offer_price.setVisibility(View.VISIBLE);
            holder.offer_price.setText(((ProductActivity) getActivity()).getDiscount());
        } else {
            holder.component.setTextColor(getResources().getColor(R.color.controlColor));
            holder.component.setTypeface(null, Typeface.BOLD);
            holder.offer_price.setTextColor(getResources().getColor(R.color.controlColor));
            holder.offer_price.setTypeface(null, Typeface.BOLD);

            holder.component.setText("Grand \nTotal");
            holder.rate.setText("");
            holder.weight.setText(" ");
            holder.price.setText(" ");
            holder.price.setVisibility(View.GONE);
            holder.offer_price.setVisibility(View.VISIBLE);
            String displayPrice = ((ProductActivity) getActivity()).getGrandTotalForProdInfoFrag();
            holder.offer_price.setText(" " + /*RandomUtils.getIndianCurrencyFormat(displayPrice, true)*/displayPrice + "/-");
        }

        return holder.itemView;
    }

    public void bindCollectionUI(Collection collection) {
        if (collection != null) {
            if (!TextUtils.isEmpty(collection.name)) {
                mCollectionName.setText(collection.name.trim());
                UiRandomUtils.underLineTv(mCollectionName, 0, mCollectionName.length());
            }
            //mCollectionStyle.setText(collection.category);
        } else {
            mCollectionName.setVisibility(View.GONE);
            //mCollectionStyle.setText("");
        }
    }

    private static class TableRowHolder {
        final View itemView;
        final TextView component, rate, weight, price, offer_price;

        public TableRowHolder(View container) {
            itemView = container;
            component = (TextView) container.findViewById(R.id.colom_component);
            rate = (TextView) container.findViewById(R.id.colom_rate);
            weight = (TextView) container.findViewById(R.id.colom_weight);
            price = (TextView) container.findViewById(R.id.colom_price);
            offer_price = (TextView) container.findViewById(R.id.colom_offer_price);
        }

        public void setTextColor(int color) {
            component.setTextColor(color);
            rate.setTextColor(color);
            weight.setTextColor(color);
            price.setTextColor(color);
            offer_price.setTextColor(color);
        }

        public void setTextSize(float size) {
            component.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            rate.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            weight.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            price.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            offer_price.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
    }

    public void mFollower() {

        if (mProductActivity.mUser != null) {
            mUser = mProductActivity.mUser;
            followButton.setSelected(mUser.isFollowed);
        } else {

        }
    }


    private void create2RowTable(ProductInfo summary, List<StoneDetail> rows) {
        TableRowHolder head = new TableRowHolder(mTableContainer.findViewById(R.id.heading));
        head.setTextColor(Color.WHITE);
        head.setTextSize(getResources().getDimension(R.dimen.ts_primary));
        head.itemView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        head.component.setText("Component");
        head.rate.setText("Rate\n(" + "INR/weight" + ")");//to be changed from backend
        head.rate.setVisibility(View.INVISIBLE);
        head.weight.setText("Weight");
        //(" + t.weightunit + ");
        head.price.setText("Value\n(" + /*t.rateunit*/"INR" + ")");//to be changed from backend
        head.price.setVisibility(View.INVISIBLE);
        head.offer_price.setText("Offer Price");

        int childPos = 1;
        int pos = 0;
        String type = "";
                /*Add the gold details*/

        mChileview = mTableContainer.getChildAt(childPos);
        View mGoldHeading = getRowView(summary.metalType, mChileview, mTableContainer, true);
        mTableContainer.addView(mGoldHeading);
        childPos++;

        StoneDetail details = new StoneDetail();
        details.color = summary.metalType;
        details.clarity = summary.metalPurity + "" + summary.metalPurityInUnits;
        details.number = /*-1*/TYPE_GOLD;
        details.metalWeightUnits = summary.metalWeightUnits;
        details.price = summary.metalrate;
        details.weight = summary.metalWeight;
        View mGoldDetails = getRowView(details, mChileview, mTableContainer, true);
        mTableContainer.addView(mGoldDetails);
        childPos++;

        if (rows.size() > 0) {
            do {
                StoneDetail detail = rows.get(pos);
                View child = mTableContainer.getChildAt(childPos);
                View v;
                if (detail.type.equals(type)) {
                    v = getRowView(detail, child, mTableContainer, true);
                    pos++;
                } else {
                    v = getRowView(type = detail.type, child, mTableContainer, true);
                }
                childPos++;
                v.setVisibility(View.VISIBLE);
                if (child == null) {
                    mTableContainer.addView(v);
                }
            } while (pos < rows.size());
        }
                /*Caliculate the total,vat,making charges and discount*/
        childPos++;
        if (mTableContainer.getChildCount() > childPos) {
            for (int i = childPos; i < mTableContainer.getChildCount(); i++) {
                mTableContainer.getChildAt(i).setVisibility(View.GONE);
            }
        }
    }
}
