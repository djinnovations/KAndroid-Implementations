package com.goldadorn.main.activities.cart;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.assist.ILoadingProgress;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.dj.model.GetCartResponseObj;
import com.goldadorn.main.dj.uiutils.ResourceReader;
import com.goldadorn.main.dj.uiutils.WindowUtils;
import com.goldadorn.main.dj.utils.RandomUtils;
import com.goldadorn.main.model.Product;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.ProductResponse;
import com.goldadorn.main.utils.TypefaceHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


/**
 * Created by Kiran BH on 10/03/16.
 */
public class MyCartFragment extends Fragment implements CartProductsViewHolder.IQuantityChangeListener {
    CartProductsViewHolder mCartProductsViewHolder;
    private View mCartEmptyView;
    ArrayList<GetCartResponseObj.ProductItem> mCart = new ArrayList<>();
    View mShippingContainer, mPriceLabelContainer, mTotalContainer, mPriceItems;
    //LinearLayout mContainer_header_row;
    long mCostShipping = 0, mCostTax = 0, mCostTotal;
    View card_view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mycart, container, false);
    }

    HashMap<Integer, Integer> mapOfProdsToQuantity;
    Typeface typeface;

    protected boolean displayBottomBtns(){
        return true;
    }

    protected boolean displayQtyHolder(){
        return true;
    }

    /*private boolean isUseCart;
    protected void setIsUseCart(boolean isUseCart){
     this.isUseCart = isUseCart;
    }*/

    protected boolean getIsUseCart(){
        return true;
    }

    /*private String orderId;
     public void setOrderId(String orderId){
        this.orderId = orderId;
    }*/

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCartEmptyView = view.findViewById(R.id.emptyview_cart);
        card_view = view.findViewById(R.id.card_view);
        mapOfProdsToQuantity = new HashMap<>();

        //((CartManagerActivity) getActivity()).getPayInfoView().setVisibility(View.GONE);
        ((CartManagerActivity) getActivity()).getPlaceOrderBtn().setVisibility(View.VISIBLE);
        typeface = TypefaceHelper.getTypeFace(Application.getInstance(),
                ResourceReader.getInstance(Application.getInstance()).getStringFromResource(R.string.font_name_text_normal));

        mShippingContainer = view.findViewById(R.id.container_shipping);
        mPriceLabelContainer = view.findViewById(R.id.priceDetailsLabel);
        //mTaxContainer.setVisibility(View.VISIBLE);
        mTotalContainer = view.findViewById(R.id.container_total);
        mPriceItems = view.findViewById(R.id.pricesOfItems);

        ((TextView) mPriceLabelContainer.findViewById(R.id.title)).setTypeface(typeface, Typeface.BOLD);
        ((TextView) mTotalContainer.findViewById(R.id.title)).setTypeface(typeface, Typeface.BOLD);
        ResourceReader rsd = ResourceReader.getInstance(Application.getInstance());
        ((TextView) mTotalContainer.findViewById(R.id.title)).setTextColor(Color.BLACK);
        ((TextView) mTotalContainer.findViewById(R.id.cost)).setTextColor(Color.BLACK);
        ((TextView) mPriceLabelContainer.findViewById(R.id.title)).setTextColor(Color.BLACK);

        TypefaceHelper.setFont(((TextView) mShippingContainer.findViewById(R.id.title))
        ,((TextView) mPriceItems.findViewById(R.id.title)), ((TextView) mPriceItems.findViewById(R.id.cost))
        , ((TextView) mShippingContainer.findViewById(R.id.cost))
        ,mCartEmptyView);

        ((TextView) mTotalContainer.findViewById(R.id.cost)).setTypeface(typeface, Typeface.BOLD);

        //mContainer_header_row = (LinearLayout) view.findViewById(R.id.container_header_row);
        ((TextView) mPriceLabelContainer.findViewById(R.id.title)).setText("Price Details");
        //((TextView) mPriceItems.findViewById(R.id.title)).setText("Price");
        ((TextView) mShippingContainer.findViewById(R.id.title)).setText("Shipping (Free Delivery)");
        ((TextView) mTotalContainer.findViewById(R.id.title)).setText("Amount Payable");
        //((TextView) mContainer_header_row.getChildAt(0)).setText(R.string.Product);
        //((TextView) mContainer_header_row.getChildAt(1)).setText(R.string.Quantity);
        SpannableStringBuilder sbr = new SpannableStringBuilder(getString(R.string.Price));
        int start = sbr.length();
        sbr.append("\n").append("(Quantity * Unit price)");
        int end = sbr.length();
//        sbr.setSpan(new RelativeSizeSpan(0.5f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //((TextView) mContainer_header_row.getChildAt(2)).setText(sbr);


        mCartProductsViewHolder = new CartProductsViewHolder((LinearLayout) view.findViewById(R.id.container_cart), this);
        String orderId = ((CartManagerActivity) getActivity()).getOrderId();
        ((ILoadingProgress) getActivity()).showLoading(true);
        UIController.getCartDetails(getContext(), getIsUseCart(), orderId,0
                ,new IResultListener<ProductResponse>() {
            @Override
            public void onResult(ProductResponse result) {
                try {
                    if (result.success && result.productArray != null) {
                        for (int i = 0; i < result.productArray.size(); i++) {
                            GetCartResponseObj.ProductItem prod = result.productArrayNew.get(i);
                            //checkAndEntry(prod);
                        }
                        mCart.addAll(/*getUniqueProdList(*/result.productArrayNew/*)*/);
                        mapTransIdOrderQty(result.productArrayNew);
                        //getUniqueProdList();
                        onCartChanged();
                    }
                    ((ILoadingProgress) getActivity()).showLoading(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void checkAndEntry(Product prod) {

        if (mapOfProdsToQuantity.containsKey(prod.id)) {
            int oldVal = mapOfProdsToQuantity.get(prod.id);
            mapOfProdsToQuantity.put(prod.id, (oldVal + 1));
        } else
            mapOfProdsToQuantity.put(prod.id, prod.quantity);
    }

    private void logEventsAnalytics(String eventName) {
        ((Application) getActivity().getApplication()).getFbAnalyticsInstance().logCustomEvent(getActivity(), eventName);
    }

    HashMap<Integer, Integer> mapTransIdQty = new HashMap<>();

    private void mapTransIdOrderQty(List<GetCartResponseObj.ProductItem> prodList) {
        for (GetCartResponseObj.ProductItem product : prodList) {
            mapTransIdQty.put(product.getTransId(), product.getOrderQty());
        }
    }

    public int getTotalQty(List<GetCartResponseObj.ProductItem> prodList){
       /* int totalQty = 0;
        for (GetCartResponseObj.ProductItem product : prodList) {
            totalQty = totalQty + product.getOrderQty();
        }
        return totalQty;*/
        return ((CartManagerActivity) getActivity()).getTotalQty(prodList);
    }


    private int getOrderQtyToRemove(int transId, int currentSelectedCount) {
        int intial = mapTransIdQty.get(transId);
        int toremove = intial - currentSelectedCount;
        mapTransIdQty.put(transId, currentSelectedCount);
        return toremove;
    }


    private boolean isFirst = true;

    private void bindCostUi() {
        mCostTotal = 0;
        int totalUnits = 0;
        String currency = null;
        for (final GetCartResponseObj.ProductItem p : mCart) {
            mCostTotal = mCostTotal + (long) p.getPricePaid();
            totalUnits = totalUnits + p.getOrderQty();
            currency = p.getPriceUnits();
            Log.e("iii---", p.getTransId() + "--" + mCostTotal + "--" + p.getOrderQty());

            if (p.getOrderQty() == 0) {
                int reduceqty = p.getPreviousQty() - p.getOrderQty();
                UIController.removeFromCart(getContext(), p.getTransId(), /*p.getOrderQty()*/ reduceqty, getOrderQtyToRemove(p.getTransId(), p.getOrderQty()),
                        new IResultListener<ProductResponse>() {
                            @Override
                            public void onResult(ProductResponse result) {
                                ((CartManagerActivity) getActivity()).dismissOverLay();
                                if (result.success) {
                                    mCart.remove(p);
                                    //   notifyDataSetChanged();
                                    Log.e("REMOVED ITEM---", "REMOVED " + mCart.size());
                                    onCartChanged();
                                    //result.productArray.remove(mCart);
                                    Toast.makeText(getContext(), "Item removed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }); //// TODO: 01-09-2016  remove cart implementation
            }

        }
        //((CartManagerActivity) getActivity()).dismissOverLay();
        isFirst = false;
        mCostTotal = mCostTotal + mCostShipping + mCostTax;
        int t = totalUnits > 0 ? 1 : 0;
        Log.e("iii---", t + "--" + mCostTotal);
        /*Madhu*/
        float taxCost = (mCostTax * t);
        float shipinCost = (mCostShipping * t);
        float totalCost = (mCostTotal * t);

        if (getTotalQty(mCart) <= 0){
            card_view.setVisibility(View.GONE);
            ((CartManagerActivity) getActivity()).removeStaticBottomBar();
            return;
        }
        ((TextView) mPriceItems.findViewById(R.id.title)).setText("Price" + " (" +getTotalQty(mCart) + " items)");
        ((TextView) mPriceItems.findViewById(R.id.cost)).setText(RandomUtils.getIndianCurrencyFormat(totalCost, true));
        //((TextView) mPriceLabelContainer.findViewById(R.id.cost)).setText(RandomUtils.getIndianCurrencyFormat(taxCost, true)/*"Rs " + (mCostTax * t)*/ + "/-");
        ((TextView) mShippingContainer.findViewById(R.id.cost)).setText(RandomUtils.getIndianCurrencyFormat(shipinCost, true)/*"Rs " + (mCostShipping * t)*/ + "/-");
        ((TextView) mTotalContainer.findViewById(R.id.cost)).setText(RandomUtils.getIndianCurrencyFormat(totalCost, true)/*"Rs " + (mCostTotal * t)*/ + "/-");
        ((CartManagerActivity) getActivity()).getTvAmount().setText(RandomUtils.getIndianCurrencyFormat(totalCost, true));
        /*  ((TextView) mTaxContainer.findViewById(R.id.cost)).setText(currency + ". " + (mCostTax * t) + "/-");
        ((TextView) mShippingContainer.findViewById(R.id.cost)).setText(currency + ". " + (mCostShipping * t) + "/-");
        ((TextView) mTotalContainer.findViewById(R.id.cost)).setText(currency + ". " + (mCostTotal * t) + "/-");*/
    }

    private void onCartChanged() {
        if (mCart.size() > 0) {
            mCartEmptyView.setVisibility(View.GONE);
            mCartProductsViewHolder.setVisibility(View.VISIBLE);
            mCartProductsViewHolder.bindUI(mCart, displayBottomBtns(), displayQtyHolder());
        } else {
            mCartEmptyView.setVisibility(View.VISIBLE);
            mCartProductsViewHolder.setVisibility(View.GONE);
        }
        bindCostUi();
        ((CartManagerActivity) getActivity()).storeCartData(mCart, mCostTotal);
    }

    @Override
    public void onQuantityChanged(int prevQty, int newQty/*Product product*/) {
        if (!isFirst) {
            WindowUtils.marginForProgressViewInGrid = 25;
            ((CartManagerActivity) getActivity()).showOverLay(null, R.color.colorPrimary,
                    WindowUtils.PROGRESS_FRAME_GRAVITY_BOTTOM);
        }
        Log.d("djcart", "onQuantityChanged");
        bindCostUi();
    }

    /*private Product find(int id) {
        Product t = new Product(id);
        int index = mCart.indexOf(t);
        return mCart.get(index);
    }*/

    public ArrayList<Product> getUniqueProdList(ArrayList<Product> fullList) {
        ArrayList<Product> templist = new ArrayList<>();
        Set<Integer> keysList = mapOfProdsToQuantity.keySet();
        for (int key : keysList) {
            for (Product prod : fullList) {
                if (key == prod.id) {
                    prod.orderQty = mapOfProdsToQuantity.get(key);
                    templist.add(prod);
                    break;
                }
            }
        }
        Log.d("djcart", "unique productList size: " + templist.size());
        return templist;
    }

}
