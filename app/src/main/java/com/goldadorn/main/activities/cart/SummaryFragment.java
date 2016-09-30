package com.goldadorn.main.activities.cart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.dj.uiutils.ResourceReader;
import com.goldadorn.main.dj.uiutils.UiRandomUtils;
import com.goldadorn.main.dj.utils.DateTimeUtils;
import com.goldadorn.main.dj.utils.IntentKeys;
import com.goldadorn.main.dj.utils.RandomUtils;
import com.goldadorn.main.utils.TypefaceHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kiran BH on 10/03/16.
 */
public class SummaryFragment extends Fragment implements View.OnClickListener {

    private boolean isCOD;
    @Bind(R.id.btnShare)
    View btnShare;
    @Bind(R.id.tvCODNote)
    View tvCODNote;
    @Bind(R.id.btnContactUs)
    View btnContactUs;
   /* public static final String CODTxt = "";
    public static final String notCODTxt = "Your order has been successfully placed." +
            "\nWe will keep you posted on all updates regarding your order." +
            "\nMeanwhile, please do not hesitate to contact us for any queries." +
            "\nThank you for shopping on GoldAdorn!" +
            "\nWe are delighted to have you as our customer.";*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        isCOD = getArguments().getBoolean(IntentKeys.COD_CALL);
        return inflater.inflate(R.layout.fragment_payment_summary, container, false);
    }

    private void setUpNestedFrag() {
        Fragment frag = new MyCartFragmentReplica();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.myCartContainer, frag).commit();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        rsdr = ResourceReader.getInstance(Application.getInstance());
        setUpNestedFrag();
        setUpFonts();
        setUpSection1();
        setUpSection3and4();
        if (isCOD)
            tvCODNote.setVisibility(View.VISIBLE);
        else tvCODNote.setVisibility(View.INVISIBLE);

        //((CartManagerActivity) getActivity()).getPayInfoView().setVisibility(View.GONE);
        //((CartManagerActivity) getActivity()).getPlaceOrderBtn().setVisibility(View.VISIBLE);
        btnContactUs.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        //((TextView) view.findViewById(R.id.tvCongo)).setText(Html.fromHtml("Congrats <img> :) <img/>"));
    }

    private void setUpFonts() {
        TypefaceHelper.setFont(tvCongo, tvCongoDesc, tvCODNote);
    }

    @Bind(R.id.congratsHolder)
    View congratsHolder;
    @Bind(R.id.section1Holder)
    RelativeLayout section1Holder;
    @Bind(R.id.tvCongo)
    TextView tvCongo;
    @Bind(R.id.tvCongoDesc)
    TextView tvCongoDesc;

    private void setUpSection1() {
        Map<String, String> attr = new HashMap<>();
        attr.put("Order Date", DateTimeUtils.getCurrentDateTime12hr());
        attr.put("Order ID", ((CartManagerActivity) getActivity()).getOrderId());
        attr.put("Order Amount", ((CartManagerActivity) getActivity()).getTvAmount().getText().toString());
        View tempView = getCardView(attr, null, "Manage Orders", true);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
        , ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, congratsHolder.getId());
        tempView.setLayoutParams(layoutParams);
        section1Holder.addView(tempView);
    }

    @Bind(R.id.section3and4Holder)
    LinearLayout section3and4Holder;

    private void setUpSection3and4() {
        Map<String, String> attr = new HashMap<>();
        attr.put("Payment Method", ((CartManagerActivity) getActivity()).getPaymentDone());
        View tempView = getCardView(attr, "Payment Info", null, false);
        section3and4Holder.addView(tempView);
        Map<String, String> attr1 = new HashMap<>();
        attr1.put(RandomUtils.getAddressTxt(((CartManagerActivity) getActivity()).getShippingAddress())
                , /*((CartManagerActivity) getActivity()).getPaymentDone()*/null);
        View tempView1 = getCardView(attr1, "Shipping Address", null, false);
        section3and4Holder.addView(tempView1);
    }

    ResourceReader rsdr;
    private View getCardView(Map<String, String> bodyItems, String headerTxt, String bottomFunc, boolean isSection1) {
        CardItemsViewHolder viewHolder = new CardItemsViewHolder(View.inflate(Application.getInstance()
                , R.layout.title_bottom_function_body_txt, null));
        if (isSection1)
            viewHolder.line1.setVisibility(View.GONE);
        viewHolder.onBind(bodyItems, headerTxt, bottomFunc);
        return viewHolder.itemView;
    }


    class CardItemsViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.titleLabel)
        View titleLabel;
        @Bind(R.id.line1)
        View line1;
        @Bind(R.id.llbodyHolder)
        LinearLayout llbodyHolder;
        @Bind(R.id.bottomFunction)
        View bottomFunction;

        public CardItemsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            bottomFunction.setOnClickListener(btnClick);
        }


        View.OnClickListener btnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(Application.getInstance(), "Feature Coming Soon", Toast.LENGTH_SHORT).show();
                ((CartManagerActivity) getActivity()).launchMyOrderScreen();
            }
        };



        public void onBind(Map<String, String> bodyItems, String headerTxt, String bottomFunc) {
            if (!TextUtils.isEmpty(bottomFunc)) {
                TypefaceHelper.setFont((TextView) bottomFunction.findViewById(R.id.title));
                bottomFunction.setVisibility(View.VISIBLE);
                ((TextView) bottomFunction.findViewById(R.id.title)).setTextColor(Color.BLACK);
                ((TextView) bottomFunction.findViewById(R.id.title)).setText(bottomFunc);
            } else bottomFunction.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(headerTxt)) {
                titleLabel.setVisibility(View.VISIBLE);
                ((TextView) titleLabel.findViewById(R.id.title)).setText(headerTxt);
                UiRandomUtils.setTypefaceBold((TextView) titleLabel.findViewById(R.id.title));
                ((TextView) titleLabel.findViewById(R.id.title)).setTextColor(Color.BLACK);
            } else titleLabel.setVisibility(View.GONE);
            Iterator iterator = bodyItems.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> pair = (Map.Entry<String, String>) iterator.next();
                /*System.out.println(pair.getKey() + " = " + pair.getValue());
                iterator.remove(); // avoids a ConcurrentModificationException*/
                View tempView = LayoutInflater.from(Application.getInstance()).inflate(R.layout.item_extra_cost, null);
                TypefaceHelper.setFont((TextView) tempView.findViewById(R.id.title)
                        , (TextView) tempView.findViewById(R.id.cost));
                ((TextView) tempView.findViewById(R.id.title)).setText(pair.getKey());
                ((TextView) tempView.findViewById(R.id.title)).setTextColor(rsdr.getColorFromResource(R.color.colorBlackDimText));
                ((TextView) tempView.findViewById(R.id.cost)).setTextColor(rsdr.getColorFromResource(R.color.colorBlackDimText));
                if (!TextUtils.isEmpty(pair.getValue())) {
                    tempView.findViewById(R.id.cost).setVisibility(View.VISIBLE);
                    ((TextView) tempView.findViewById(R.id.cost)).setText(pair.getValue());
                } else tempView.findViewById(R.id.cost).setVisibility(View.GONE);
                llbodyHolder.addView(tempView);
            }
        }
    }


    private View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnContactUs) {
            ((CartManagerActivity) getActivity()).contactUs();
        }
        if (v.getId() == R.id.btnShare) {
            Toast.makeText(getContext().getApplicationContext(), "Feature Coming Soon", Toast.LENGTH_SHORT).show();
            return;
        }
        getActivity().finish();
    }
}
