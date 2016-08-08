package com.goldadorn.main.dj.model;

import com.goldadorn.main.R;

/**
 * Created by User on 11-07-2016.
 */
public class Swatches {

    public class GemStoneSwatch {
    }

    public static final int TYPE_METAL = 2010;
    public static final int TYPE_GEMSTONE = 2020;
    public static final int TYPE_ACCENTSTONE = 2030;
    public static final int TYPE_CENTERSTONE = 2040;

    public static class MixedSwatch {

        private String type;
        private String color;
        private String purity;
        private String weight;
        private String purityUnits;
        private String weightUnits;
        private String costPerUnit;
        private String costUnits;
        private String skuFactor;
        private String factor;
        private String stoneShape;
        private String stoneSize;
        private String settingStone;
        private String stoneSizeUnits;
        private int WHICH_TYPE;
        private String nameForResId;
        private int selectedSwatchIconResId;
        private int defStat;
        private String cutStone;

        private MixedSwatch(String type, String color, String purity, String purityUnits,
                            String weight, String weightUnits, String costPerUnit, String costUnits,
                            String defStat, String skuFactor, int WHICH_TYPE) {
            this.type = type;
            this.color = color;
            this.purity = purity;
            this.weight = weight;
            this.purityUnits = purityUnits;
            this.weightUnits = weightUnits;
            this.costPerUnit = costPerUnit;
            this.costUnits = costUnits;
            this.skuFactor = skuFactor;
            if (WHICH_TYPE == TYPE_METAL)
                setDefStat(defStat);
            this.WHICH_TYPE = WHICH_TYPE;
        }

        public String getSwatchDisplayTxt() {
            if (WHICH_TYPE == TYPE_METAL)
                return purity + purityUnits + " " + color + " " + type;
            else if (WHICH_TYPE == TYPE_GEMSTONE)
                return color + " - " + purity;
            return "";
        }


        public int getSwatchDisplayIconResId() {
            if (WHICH_TYPE == TYPE_METAL) {
                if (type.equalsIgnoreCase("silver"))
                    return R.drawable.silver;
                else if (type.equalsIgnoreCase("platinum"))
                    return R.drawable.platinum;
                else
                    return getResId();
            }
            return 0;
            /*else if (WHICH_TYPE == TYPE_GEMSTONE){

            }*/
        }


        public int getDefStat() {
            return defStat;
        }

        public void setDefStat(String defStat) {
            try {
                this.defStat = Integer.parseInt(defStat);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                this.defStat = 0;
            }
        }

        public void setSkuFactor(String skuFactor){
            this.skuFactor = skuFactor;
        }


        public String getSkuFactor(){
            return skuFactor;
        }

        public void setCutStone(String cutStone){
            this.cutStone = cutStone;
        }

        public String getCutStone(){
            return cutStone;
        }

        public void setFactor(String factor) {
            this.factor = factor;
        }


        public void setStoneShape(String stoneShape) {
            this.stoneShape = stoneShape;
        }

        public void setStoneSize(String stoneSize) {
            this.stoneSize = stoneSize;
        }

        public void setStoneSizeUnits(String stoneSizeUnits) {
            this.stoneSizeUnits = stoneSizeUnits;
        }

        public void setSettingStone(String settingStone) {
            this.settingStone = settingStone;
        }


        @Override
        public String toString() {
            return "MixedSwatch{" +
                    "type='" + type + '\'' +
                    ", color='" + color + '\'' +
                    ", purity='" + purity + '\'' +
                    ", weight='" + weight + '\'' +
                    ", purityUnits='" + purityUnits + '\'' +
                    ", weightUnits='" + weightUnits + '\'' +
                    ", costPerUnit='" + costPerUnit + '\'' +
                    ", costUnits='" + costUnits + '\'' +
                    ", factor='" + factor + '\'' +
                    ", stoneShape='" + stoneShape + '\'' +
                    ", stoneSize='" + stoneSize + '\'' +
                    ", settingStone='" + settingStone + '\'' +
                    ", WHICH_TYPE='" + WHICH_TYPE + '\'' +
                    '}';
        }

        public String getType() {
            return type;
        }

        public String getColor() {
            return color;
        }

        public String getPurity() {
            return purity;
        }

        public String getWeight() {
            return weight;
        }

        public String getPurityUnits() {
            return purityUnits;
        }

        public String getWeightUnits() {
            return weightUnits;
        }

        public String getCostPerUnit() {
            return costPerUnit;
        }

        public String getCostUnits() {
            return costUnits;
        }

        private int getResId() {
            switch (getNameForResId()) {
                case "gold_white_14k":
                    return R.drawable.gold_white_14k;
                case "gold_white_18k":
                    return R.drawable.gold_white_18k;
                case "gold_white_22k":
                    return R.drawable.gold_white_22k;
                case "gold_rose_14k":
                    return R.drawable.gold_rose_14k;
                case "gold_rose_18k":
                    return R.drawable.gold_rose_18k;
                case "gold_rose_22k":
                    return R.drawable.gold_rose_22k;
                case "gold_yellow_14k":
                    return R.drawable.gold_yellow_14k;
                case "gold_yellow_18k":
                    return R.drawable.gold_yellow_18k;
                case "gold_yellow_22k":
                    return R.drawable.gold_yellow_22k;
                case "gold_rose_9k":
                    return R.drawable.gold_rose_9k;
                case "gold_rose_10k":
                    return R.drawable.gold_rose_10k;
                case "gold_white_9k":
                    return R.drawable.gold_white_9k;
                case "gold_white_10k":
                    return R.drawable.gold_rose_10k;
                case "gold_yellow_9k":
                    return R.drawable.gold_yellow_9k;
                case "gold_yellow_10k":
                    return R.drawable.gold_yellow_10k;
                default:
                    return R.drawable.vector_icon_cross_brown;
            }
        }

        private final char UNDERSCORE = '_';

        public String getNameForResId() {
            return (type + UNDERSCORE + color + UNDERSCORE + purity + purityUnits).toLowerCase();
        }
    }


    public static MixedSwatch getMixedSwatch(String dataFromServer, int WHICH_TYPE) {
        String[] arr = dataFromServer.split(":");
        MixedSwatch mixedSwatch = new MixedSwatch(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5], arr[6],
                arr[7], arr[8], arr[9], WHICH_TYPE);
        if (WHICH_TYPE == TYPE_METAL)
            return mixedSwatch;
        else if (WHICH_TYPE == TYPE_GEMSTONE) {
            mixedSwatch.setFactor(arr[8]);
            mixedSwatch.setStoneShape(arr[9]);
            mixedSwatch.setStoneSize(arr[10]);
            mixedSwatch.setSettingStone(arr[11]);
            mixedSwatch.setStoneSizeUnits(arr[12]);
            mixedSwatch.setCutStone(arr[13]);
            mixedSwatch.setDefStat(arr[14]);
            mixedSwatch.setSkuFactor(arr[15]);
            return mixedSwatch;
        }
        return null;
    }


   /* "Diamond:GH:VS: clarityunits : 0.69:Carat:33000:INR/carat:GemStone1:Round:1.3 mm:Prong:mm:"
       "Diamond:GH:VS:0.41:Carat:33000:INR\/carat:GemStone1:Round:2.0 mm:Prong:mm:-1:1"
    "type (inthiscase: stoneName): " +
            "color: " +
            "purity(inthiscase:clarity):" +
            " purityUnits :
    weight :
    weightUnits (And also I didnt find the units for weight (eg 0.69)):
    costPerUnit :
    costUnits :
    factor :
    stoneShape :
    stoneSize :
    settingStone :
    sizeUnits"*/


}
