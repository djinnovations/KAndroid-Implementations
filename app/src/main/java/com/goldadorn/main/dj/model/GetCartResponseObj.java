package com.goldadorn.main.dj.model;

import android.text.TextUtils;

import com.goldadorn.main.dj.uiutils.UiRandomUtils;
import com.goldadorn.main.dj.utils.DateTimeUtils;
import com.goldadorn.main.model.ProductOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        private int previousQty;
        private long timePurchase;
        private String status;
        private long statusChangeTs;
        private String orderId;
        private long timeSLA;

        private String purchaseTime;
        private String statusDateTime;
        private String estimatedDeliveyDateTime;
        private boolean isDisplayOrderId = false;


        public long getTimeSLA() {
            return timeSLA;
        }

        public void setTimeSLA(long timeSLA) {
            this.timeSLA = timeSLA;
        }

        public String getMetalSel() {
            return metalSel;
        }

        public void setMetalSel(String metalSel) {
            this.metalSel = metalSel;
        }

        public String getStoneSel() {
            return stoneSel;
        }

        public void setStoneSel(String stoneSel) {
            this.stoneSel = stoneSel;
        }

        public boolean isDisplayOrderId() {
            return isDisplayOrderId;
        }

        public void setDisplayOrderId(boolean displayOrderId) {
            isDisplayOrderId = displayOrderId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setStatusDateTime(String dateTime){
            statusDateTime = dateTime;
        }

        public String getPurchaseDateTime(){
            return purchaseTime;
        }

        public String getStatusDateTime(){
            return statusDateTime;
        }

        public String getOrderId() {
            return orderId;
        }

        private void onBindResponse() {
            if (!TextUtils.isEmpty(metalSel))
                metalSwatch = ProductOptions.getParsedMetalSwatch(metalSel);
            if (!TextUtils.isEmpty(stoneSel)) {
                stoneSwatch = ProductOptions.getParsedStoneSwatch(stoneSel);
            }
            prodImageUrl = UiRandomUtils.getVariousProductLooks(desgnId, productId,
                    UiRandomUtils.getFactor(metalSwatch), 1, true).get(0);
            toSendBackSize = ProductOptions.parseSize(prodSize, prodType);
            purchaseTime = DateTimeUtils.getFormattedTimestamp("dd/MM/yyyy hh:mm a", (timePurchase * 1000));
            if (timeSLA > 0)
                estimatedDeliveyDateTime = DateTimeUtils.getFormattedTimestamp("dd/MM/yyyy", (timeSLA * 1000));
            else estimatedDeliveyDateTime = "NA";
            if (statusChangeTs > 0)
                statusDateTime = DateTimeUtils.getFormattedTimestamp("dd/MM/yyyy", (statusChangeTs * 1000));
            else statusDateTime = "NA";
            setPreviousQty(orderQty);
        }

        public String getEstimatedDeliveyDateTime() {
            return estimatedDeliveyDateTime;
        }

        public void setEstimatedDeliveyDateTime(String estimatedDeliveyDateTime) {
            this.estimatedDeliveyDateTime = estimatedDeliveyDateTime;
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

        public int getDesgnId() {
            return desgnId;
        }

        public void setDesgnId(int desgnId) {
            this.desgnId = desgnId;
        }

        public String getProdType() {
            return prodType;
        }

        public void setProdType(String prodType) {
            this.prodType = prodType;
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
            return (int) Math.round(discount);
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

        public void setPreviousQty(int prevQty){
            previousQty = prevQty;
        }

        public int getPreviousQty(){
            return previousQty;
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
        HashSet<String> keySet = new HashSet<>();
        for (ProductItem prod : items) {
            prod.onBindResponse();
            keySet.add(prod.getOrderId());

        }
        if (items.get(0).getOrderId() == null)
            return;
        setDisplayStatus(keySet);
    }

    private void setDisplayStatus(HashSet<String> keySet) {
        Map<String, ArrayList<ProductItem>> map = new HashMap<>();

        for (String set: keySet) {
            ArrayList<ProductItem> list = new ArrayList<>();
            for (ProductItem prod : items) {
                if (prod.getOrderId().equals(set)) {
                    list.add(prod);
                }
            }
            map.put(set, list);
        }
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ArrayList<ProductItem>> pair = (Map.Entry) it.next();
            if (pair.getValue().size() > 0)
                pair.getValue().get(0).setDisplayOrderId(true);
        }
    }
}
