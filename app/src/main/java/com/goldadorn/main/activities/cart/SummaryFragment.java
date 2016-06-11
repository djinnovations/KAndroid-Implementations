package com.goldadorn.main.activities.cart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.dj.utils.IntentKeys;

/**
 * Created by Kiran BH on 10/03/16.
 */
public class SummaryFragment extends Fragment implements View.OnClickListener{

    private boolean isCOD;
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (isCOD)
            view.findViewById(R.id.tvCODNote).setVisibility(View.VISIBLE);
        else view.findViewById(R.id.tvCODNote).setVisibility(View.INVISIBLE);

        view.findViewById(R.id.btnContactUs).setOnClickListener(this);
        //((TextView) view.findViewById(R.id.tvCongo)).setText(Html.fromHtml("Congrats <img> :) <img/>"));
        super.onViewCreated(view, savedInstanceState);
    }


    private View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnContactUs){
            ((CartManagerActivity) getActivity()).contactUs();
        }
        getActivity().finish();
    }
}
