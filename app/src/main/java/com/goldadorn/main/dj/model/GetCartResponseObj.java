package com.goldadorn.main.dj.model;

import android.text.TextUtils;

import com.goldadorn.main.dj.uiutils.UiRandomUtils;
import com.goldadorn.main.model.ProductOptions;

import java.util.List;

/**
 * Created by User on 31-08-2016.
 */
public class GetCartResponseObj {

    public class ProductItem {

        private int transId;
        private int productId;
        private String priceUnits;
        private double pricePaid = -1;
        private double offPrice = -1;
        private double discount = -1;
        private double makingCharges = -1;
        private double metalPrice = -1;
        private double stonePrice = -1;
        private String metalType;
        private int metalPurity;
        private String metalColor;
        private double metalWeight;
        private double prodSize;
        private double VAT;
        private int orderQty;
        private String productName;
        private String metalSel;
        private String stoneSel;
        private int desgnId;
        private String prodType;
        Swatches.MixedSwatch metalSwatch;
        Swatches.MixedSwatch stoneSwatch;
        private String prodImageUrl;
        private String toSendBackSize;

        private void onBindResponse() {
            if (!TextUtils.isEmpty(metalSel))
                metalSwatch = ProductOptions.getParsedMetalSwatch(metalSel);
            if (!TextUtils.isEmpty(stoneSel)) {
                stoneSwatch = ProductOptions.getParsedStoneSwatch(stoneSel);
            }
            prodImageUrl = UiRandomUtils.getVariousProductLooks(desgnId, productId,
                    UiRandomUtils.getFactor(metalSwatch), 1, true).get(0);
            toSendBackSize = ProductOptions.parseSize(prodSize, prodType);
        }

        public String getMetalDisplayText() {
            /*Swatches.MixedSwatch */
            return metalSwatch.getSwatchDisplayTxt();
        }

        public String getDiamondDisplayText() {
            if (stoneSwatch != null) {
                //Swatches.MixedSwatch stoneSwatch = ProductOptions.getParsedMetalSwatch(stoneSel);
                return stoneSwatch.getSwatchDisplayTxt();
            } else return null;
        }

        public String getProdImageUrl() {
            return  /*UiRandomUtils.getVariousProductLooks(desgnId, productId,
                    UiRandomUtils.getFactor(metalSwatch), 1, true).get(0);*/prodImageUrl;
        }

        public int getTransId() {
            return transId;
        }

        public int getProductId() {
            return productId;
        }

        public String getPriceUnits() {
            return priceUnits;
        }

        public double getPricePaid() {
            return (orderQty * pricePaid);
        }

        public double getOffPrice() {
            return (orderQty * offPrice);
        }

        public int getDiscount() {
            return (int) discount;
        }

        public double getMakingCharges() {
            return makingCharges;
        }

        public double getMetalPrice() {
            return metalPrice;
        }

        public double getStonePrice() {
            return stonePrice;
        }

        public String getMetalType() {
            return metalType;
        }

        public int getMetalPurity() {
            return metalPurity;
        }

        public String getMetalColor() {
            return metalColor;
        }

        public double getMetalWeight() {
            return metalWeight;
        }

        public String getProdSize() {
            return toSendBackSize;
        }

        public double getVAT() {
            return VAT;
        }

        public int getOrderQty() {
            return orderQty;
        }

        public String getProductName() {
            return productName;
        }

        public void setOrderQty(int orderQty) {
            this.orderQty = orderQty;
        }
    }

    private List<ProductItem> items;
    private int offset;

    public List<ProductItem> getItems() {
        return items;
    }

    public int getOffset() {
        return offset;
    }

    public void onBindResponse() {
        for (ProductItem prod : items)
            prod.onBindResponse();
    }
}
