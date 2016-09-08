package com.goldadorn.main.activities.cart;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.Application;
import com.goldadorn.main.dj.model.GetCartResponseObj;
import com.goldadorn.main.dj.uiutils.UiRandomUtils;
import com.goldadorn.main.dj.utils.RandomUtils;
import com.goldadorn.main.utils.TypefaceHelper;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kiran BH on 10/03/16.
 */
class CartProductsViewHolder extends RecyclerView.ViewHolder {
    public final LinearLayout container;
    private ArrayList<ProductViewHolder> productsVh = new ArrayList<>(5);
    IQuantityChangeListener quatityChangeListener;

    public CartProductsViewHolder(LinearLayout itemView, IQuantityChangeListener quatityChangeListener) {
        super(itemView);
        container = itemView;
        this.quatityChangeListener = quatityChangeListener;
    }

    /*private ProductViewHolder createItem(Product product) {*/
    private ProductViewHolder createItem(GetCartResponseObj.ProductItem product) {
        ProductViewHolder vh = new ProductViewHolder(LayoutInflater.from(itemView.getContext()).inflate(R.layout./*item_product_in_cart*/fragment_my_cart_v2
                , container, false));
        //vh.product = product;
        vh.itemToBind = product;
        container.addView(vh.itemView);
        productsVh.add(vh);
        return vh;
    }

    /*public ProductViewHolder getItem(Product product) {
        for (ProductViewHolder vh : productsVh)
            if (product.equals(vh.product)) return vh;
        return null;
    }*/

    private boolean showBtns;
    private boolean showQty;

    public void bindUI(/*ArrayList<Product> cart*/ ArrayList<GetCartResponseObj.ProductItem> cart, boolean showBtns, boolean showQty) {
        this.showBtns = showBtns;
        this.showQty = showQty;
        for (ProductViewHolder vh : productsVh) {
            vh.remove();
        }
        productsVh.clear();
        for (GetCartResponseObj.ProductItem product : cart) {
            ProductViewHolder pvh = createItem(product);
            //pvh.bindUI(product);
            pvh.bindUI(product, showBtns, showQty);
        }
    }

    public void setVisibility(int visibility) {
        itemView.setVisibility(visibility);
    }


    public interface IQuantityChangeListener {
        void onQuantityChanged(int previousQty, int newQty/*Product product*/);
    }


    class ProductViewHolder extends RecyclerView.ViewHolder implements TextWatcher, View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        @Bind(R.id.ivProd)
        ImageView image;
        @Bind({R.id.tvProdName, R.id.product_cost, R.id.product_price_slash,
        R.id.tvDiscount, R.id.tvMetal, R.id.tvDiamond, R.id.tvSize,
        R.id.tvNegative, R.id.tvPositive})
        List<View> views;
        TextView name, price, strikeThroughPrice, discount, metal, stone, size, tvNegative, tvPositive;
        @Bind(R.id.product_quantity)
        EditText quantityText;
        @Bind(R.id.product_quantity_change)
        View quantityChange;
        @Bind(R.id.bottomBtnHolder)
        View bottomBtnHolder;
        @Bind(R.id.qtyHolder)
        View qtyHolder;
        /*@Bind(R.id.tvPositive)
        ImageView ivRemoveFromCart;*/
        //private Product product;
        private GetCartResponseObj.ProductItem  itemToBind;


        public ProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            name = (TextView) views.get(0);
            price = (TextView) views.get(1);
            strikeThroughPrice = (TextView) views.get(2);
            discount = (TextView) views.get(3);
            metal = (TextView) views.get(4);
            stone = (TextView) views.get(5);
            size = (TextView) views.get(6);
            tvNegative = (TextView) views.get(7);
            tvPositive = (TextView) views.get(8);
            View[] viewsArr = new View[views.size()];
            TypefaceHelper.setFont((views.toArray(viewsArr)));

            //image = (ImageView) itemView.findViewById(R.id.product_image);
            //name = (TextView) itemView.findViewById(R.id.product_name);
            //price = (TextView) itemView.findViewById(R.id.product_price);
            //quantityText = (EditText) itemView.findViewById(R.id.product_quantity);
            //quantityChange = itemView.findViewById(R.id.product_quantity_change);
            quantityText.addTextChangedListener(this);
            //ivRemoveFromCart = (ImageView) itemView.findViewById(R.id.ivRemoveFromCart);
            quantityChange.setOnClickListener(this);
            tvPositive.setOnClickListener(this);
            tvNegative.setOnClickListener(this);
        }

        /*public void bindUI(Product product) {*/
        public void bindUI(GetCartResponseObj.ProductItem itemToBind, boolean showBtns, boolean showQty) {
            Log.d("djcart","product name: "+itemToBind.getProductName());
            //name.setText(product.name);
            name.setText(itemToBind.getProductName());
            quantityText.removeTextChangedListener(this);
            //quantityText.setText(String.valueOf(product.orderQty));
            quantityText.setText(String.valueOf(itemToBind.getOrderQty()));
            quantityText.addTextChangedListener(this);

            Picasso.with(image.getContext()).
                    load(itemToBind.getProdImageUrl()).memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                    .placeholder(R.drawable.vector_image_logo_square_100dp)/*.fit()*/.into(image);

            if (showBtns)
                bottomBtnHolder.setVisibility(View.VISIBLE);
            else bottomBtnHolder.setVisibility(View.GONE);
            if (showQty)
                qtyHolder.setVisibility(View.VISIBLE);
            else qtyHolder.setVisibility(View.GONE);
            //remove 0 at end the end
            /*DecimalFormat format = new DecimalFormat("0.#");
            int pricee= Integer.parseInt(format.format(product.pricePaid));
            long totalCost = pricee * product.orderQty;
            SpannableStringBuilder sbr = new SpannableStringBuilder((RandomUtils.getIndianCurrencyFormat(totalCost, true)));
            //sbr.append("/-");
            int start = sbr.length();
            sbr.append("\n").append("(").append(product.orderQty + " * " + product.pricePaid).append(")");
            int end = sbr.length();
            sbr.setSpan(new RelativeSizeSpan(0.5f), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/
            ;
            price.setText(RandomUtils.getIndianCurrencyFormat(itemToBind.getPricePaid(), true));
            updateComponents();
        }

        public void updateComponents(){
            if (itemToBind.getDiscount() > 0){
                strikeThroughPrice.setVisibility(View.VISIBLE);
                discount.setVisibility(View.VISIBLE);
                strikeThroughPrice.setText(RandomUtils.getIndianCurrencyFormat(itemToBind.getOffPrice(), true));
                UiRandomUtils.strikeThroughText(strikeThroughPrice);
                discount.setText(String.valueOf(itemToBind.getDiscount() + "% off"));
            }else {
                strikeThroughPrice.setVisibility(View.GONE);
                discount.setVisibility(View.GONE);
            }

            String metalTxt = itemToBind.getMetalDisplayText();
            String stoneTxt = itemToBind.getDiamondDisplayText();
            if (metalTxt != null)
                metal.setText("Metal: "+metalTxt);
            if (!TextUtils.isEmpty(stoneTxt)){
                stone.setVisibility(View.VISIBLE);
                stone.setText("Diamond Quality: "+stoneTxt);
            }else stone.setVisibility(View.GONE);
            if (itemToBind.getProdSize().equals("-1")){
                size.setVisibility(View.GONE);
            }else {
                size.setVisibility(View.VISIBLE);
                size.setText("Size: "+itemToBind.getProdSize());
            }
        }

        public void remove() {
            if (popupMenu != null) {
                popupMenu.dismiss();
                popupMenu = null;
            }
            quantityChange.setOnClickListener(null);
            quantityText.removeTextChangedListener(this);
            ((LinearLayout) itemView.getParent()).removeView(itemView);
        }

        PopupMenu popupMenu;

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.tvNegative){
                Toast.makeText(Application.getInstance(), "Feature Coming Soon", Toast.LENGTH_SHORT).show();
            }
            else if (v.getId() == R.id.tvPositive){
                changeQuantity(itemToBind.getOrderQty(), 0);
            }
            else if (quantityChange.getId() == v.getId()){
                if (true){
                    Toast.makeText(Application.getInstance(), "Feature Coming Soon", Toast.LENGTH_SHORT).show();
                    return; //// TODO: 04-09-2016  
                }
                if (popupMenu != null) popupMenu.dismiss();
                popupMenu = new PopupMenu(quantityChange.getContext(), quantityChange);
                Menu menu = popupMenu.getMenu();
                int toadd = 5 - itemToBind.getOrderQty();
                for (int i = 1; i <= itemToBind.getOrderQty() + toadd; i++)
                    menu.add(0, i, i, "" + i);
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            changeQuantity(itemToBind.getOrderQty(), item.getItemId());
            return true;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            changeQuantity(itemToBind.getOrderQty(), TextUtils.isEmpty(s) ? 0 : Integer.parseInt(s.toString()));
        }

        private void changeQuantity(int previousQty, int newQuantity) {
            if (newQuantity != 0){
                Toast.makeText(quantityText.getContext(), "Feature Coming Soon", Toast.LENGTH_SHORT).show();
                return;
            }
            itemToBind.setPreviousQty(previousQty);
            itemToBind.setOrderQty(newQuantity);
            //bindUI(itemToBind, showBtns, showQty);
            quatityChangeListener.onQuantityChanged(previousQty, newQuantity);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
