package com.goldadorn.main.dj.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.dj.adapter.AvailedVoucherAdapter;
import com.goldadorn.main.dj.adapter.VoucherAdapter;
import com.goldadorn.main.dj.model.TitlesData;
import com.goldadorn.main.dj.model.UserSession;
import com.goldadorn.main.dj.uiutils.UiRandomUtils;
import com.goldadorn.main.modules.people.DividerDecoration;
import com.goldadorn.main.utils.TypefaceHelper;
import com.kimeeo.library.fragments.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 12-11-2016.
 */
public class RedemptionFragment extends BaseFragment{
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        //return null;
        return layoutInflater.inflate(R.layout.fragment_redemption, viewGroup, false);
    }

    @Override
    protected void garbageCollectorCall() {

    }


    @Bind(R.id.rvTable)
    RecyclerView rvTable;
    VoucherAdapter adapter;
    @Bind(R.id.tvGoldCoinCnt)
    TextView tvGoldCoinCnt;
    @Bind(R.id.tvDiamondCnt)
    TextView tvDiamondCnt;
    @Bind(R.id.labelYourGold)
    TextView labelYourGold;
    @Bind(R.id.labelYourDiamond)
    TextView labelYourDiamond;
    @Bind(R.id.labelYourVoucher)
    TextView labelYourVoucher;
    @Bind(R.id.tvVoucherStat)
    TextView tvVoucherStat;
    @Bind(R.id.rvTableAvailed)
    RecyclerView rvTableAvailed;

    @Bind(R.id.labelGoldCoin)
    TextView labelGoldCoin;
    @Bind(R.id.labelDiamond)
    TextView labelDiamond;
    @Bind(R.id.labelDiscount)
    TextView labelDiscount;

    @Bind(R.id.labelYourRedemptions)
    TextView labelYourRedemptions;
    @Bind(R.id.labelYourRewards)
    TextView labelYourRewards;
    //@Bind(R.id.)

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        TypefaceHelper.setFont(tvGoldCoinCnt, tvDiamondCnt, labelYourDiamond, labelYourGold, tvVoucherStat);
        UiRandomUtils.setTypefaceBold(labelYourVoucher, labelDiamond, labelGoldCoin,
                labelDiscount, labelYourRedemptions, labelYourRewards);
        tvGoldCoinCnt.setText(UserSession.getInstance().getGoldCoinCount());
        tvDiamondCnt.setText(UserSession.getInstance().getDiamondCnt());
        setUpRecycleView();
    }


    private AvailedVoucherAdapter availedVoucherAdapter;
    private void setUpRecycleView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvTable.setHasFixedSize(false);
        rvTable.setLayoutManager(mLayoutManager);
        rvTable.addItemDecoration(new DividerDecoration(getActivity()));
        rvTable.setItemAnimator(new DefaultItemAnimator());
        adapter = new VoucherAdapter();
        rvTable.setAdapter(adapter);
        if (UserSession.getInstance().isVoucherDataAvail()) {
            tvVoucherStat.setVisibility(View.GONE);
            rvTable.setVisibility(View.VISIBLE);
            adapter.changeData(UserSession.getInstance().getVoucherData());
        }else {
            tvVoucherStat.setVisibility(View.VISIBLE);
            rvTable.setVisibility(View.GONE);
        }

        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvTableAvailed.setHasFixedSize(false);
        rvTableAvailed.setLayoutManager(mLayoutManager1);
        rvTableAvailed.addItemDecoration(new DividerDecoration(getActivity()));
        rvTableAvailed.setItemAnimator(new DefaultItemAnimator());
        availedVoucherAdapter = new AvailedVoucherAdapter();
        rvTableAvailed.setAdapter(availedVoucherAdapter);
        if (UserSession.getInstance().isAvailVoucherPresent()){
            rvTableAvailed.setVisibility(View.VISIBLE);
            availedVoucherAdapter.changeData(UserSession.getInstance().getAvailVoucher());
        }else rvTableAvailed.setVisibility(View.GONE);
    }


    List<TitlesData> tempData(){
        List<TitlesData> datas = new ArrayList<>();
        datas.add(new TitlesData("Code1", "Where are you now tat i need you"));
        datas.add(new TitlesData("Code2", "I gave u attention wen nobody was"));
        datas.add(new TitlesData("Code3", "I was on knees when nobody else praying"));
        datas.add(new TitlesData("Code4", "I need u the most"));
        return datas;
    }
}
