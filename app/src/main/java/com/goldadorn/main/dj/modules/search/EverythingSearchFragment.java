package com.goldadorn.main.dj.modules.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.dj.uiutils.UiRandomUtils;
import com.goldadorn.main.utils.TypefaceHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by User on 25-10-2016.
 */
public class EverythingSearchFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_everything, container, false);
    }


    View desHolder;
    View collHolder;
    View prodHolder;

    @Bind(R.id.rvDesigner)
    RecyclerView rvDesigner;
    @Bind(R.id.rvCollection)
    RecyclerView rvCollection;
    @Bind(R.id.rvProduct)
    RecyclerView rvProduct;
    @Bind(R.id.tvTitleDes)
    TextView tvTitleDes;
    @Bind(R.id.tvTitleColl)
    TextView tvTitleColl;
    @Bind(R.id.tvTitleProd)
    TextView tvTitleProd;
    @Bind(R.id.tvCollViewAll)
    TextView tvCollViewAll;
    @Bind(R.id.tvDesViewAll)
    TextView tvDesViewAll;
    @Bind(R.id.tvProdViewAll)
    TextView tvProdViewAll;

    View.OnClickListener viewAllColl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((SearchActivity) getActivity()).onPageChange(2);
        }
    };

    View.OnClickListener viewAllDes = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((SearchActivity) getActivity()).onPageChange(1);
        }
    };


    View.OnClickListener viewAllProd = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((SearchActivity) getActivity()).onPageChange(3);
        }
    };


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        desHolder = view.findViewById(R.id.llDesHolder);
        collHolder = view.findViewById(R.id.llCollHolder);
        prodHolder = view.findViewById(R.id.llProdHolder);
        UiRandomUtils.setTypefaceBold(tvTitleColl, tvTitleDes, tvTitleProd);
        TypefaceHelper.setFont(tvCollViewAll, tvDesViewAll, tvProdViewAll);
        setUpRecyclerViews();
        tvCollViewAll.setOnClickListener(viewAllColl);
        tvDesViewAll.setOnClickListener(viewAllDes);
        tvProdViewAll.setOnClickListener(viewAllProd);
    }

     DesignerSearchAdapter designerAdapter;
     CollectionSearchAdapter collAdapter;
     ProductSearchAdapter prodAdapter;

    private void setUpRecyclerViews(){

        collHolder.setVisibility(View.GONE);
        prodHolder.setVisibility(View.GONE);
        desHolder.setVisibility(View.GONE);

        LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvDesigner.setHasFixedSize(false);
        rvDesigner.setLayoutManager(mLayoutManager1);
        rvDesigner.setItemAnimator(new DefaultItemAnimator());
        designerAdapter = new DesignerSearchAdapter();
        rvDesigner.setAdapter(designerAdapter);

        LinearLayoutManager mLayoutManager2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvCollection.setHasFixedSize(false);
        rvCollection.setLayoutManager(mLayoutManager2);
        rvCollection.setItemAnimator(new DefaultItemAnimator());
        collAdapter = new CollectionSearchAdapter();
        rvCollection.setAdapter(collAdapter);

        LinearLayoutManager mLayoutManager3 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvProduct.setHasFixedSize(false);
        rvProduct.setLayoutManager(mLayoutManager3);
        rvProduct.setItemAnimator(new DefaultItemAnimator());
        prodAdapter = new ProductSearchAdapter();
        rvProduct.setAdapter(prodAdapter);
    }

    public void updateEComm(SearchDataObject dataObject){
        if (dataObject.getCollList() != null) {
            if (dataObject.getCollList().size() == 0)
                collHolder.setVisibility(View.GONE);
            else {
                collHolder.setVisibility(View.VISIBLE);
                collAdapter.changeDataColl(dataObject.getCollList());
            }
        }else collHolder.setVisibility(View.GONE);

        if (dataObject.getProdList() != null) {
            if (dataObject.getProdList().size() == 0)
                prodHolder.setVisibility(View.GONE);
            else{
                prodHolder.setVisibility(View.VISIBLE);
                prodAdapter.changeDataProd(dataObject.getProdList());
            }
        }else prodHolder.setVisibility(View.GONE);
    }

    public void updateSocial(SearchDataObject dataObject){
        if (dataObject.getDesigners() != null) {
            if (dataObject.getDesigners().size() == 0)
                desHolder.setVisibility(View.GONE);
            else {
                desHolder.setVisibility(View.VISIBLE);
                designerAdapter.changeData(dataObject.getDesigners());
            }
        }else desHolder.setVisibility(View.GONE);
    }

}
