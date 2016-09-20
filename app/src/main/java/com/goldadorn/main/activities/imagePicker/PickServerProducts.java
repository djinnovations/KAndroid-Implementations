package com.goldadorn.main.activities.imagePicker;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;

import com.goldadorn.main.activities.productListing.ServerProducts;
import com.goldadorn.main.dj.utils.IntentKeys;
import com.goldadorn.main.model.FilterProductListing;
import com.goldadorn.main.model.ServerFolderObject;
import com.goldadorn.main.views.ColoredSnackbar;

import java.util.ArrayList;

/**
 * Created by BhavinPadhiyar on 19/04/16.
 */
public class PickServerProducts  extends ServerProducts{

    @Override
    public void processItem(ArrayList<FilterProductListing> dataList) {
        if (/*ServerProducts.isCallerPTB*/getIsPTB()){
            if (dataList != null){
                Intent intent = new Intent();
                intent.putParcelableArrayListExtra(IntentKeys.FILTER_OBJ, dataList);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }else {
                final Snackbar snackbar = Snackbar.make(getLayoutParent(), "Please select image", Snackbar.LENGTH_SHORT);
                ColoredSnackbar.alert(snackbar).show();
            }
        }
        else {
            if (dataList != null) {
                FilterProductListing data = dataList.get(0);
                Intent intent = new Intent();
                ////http://demo.eremotus-portal.com/goldadorn_dev/gallery/pendants/gallery.jpg
                String path = /*".." +*/ data.getImage().substring(data.getImage()
                        .indexOf(/*"/product"*/"defaults/"), data.getImage().length());
                intent.putExtra("PATH", path);
                intent.putExtra("PREVIEW", data.getImage());
                intent.putExtra("PRICE", data.getProductPrice());
                intent.putExtra("COLLID", data.getCollId());
                intent.putExtra("DESID", data.getDesgnId());
                intent.putExtra("PRODID", data.getProdId());
                intent.putExtra("DISCOUNT", data.getDiscount());
                intent.putExtra("RANGE", data.getRange());

                setResult(Activity.RESULT_OK, intent);
                finish();
            } else {
                final Snackbar snackbar = Snackbar.make(getLayoutParent(), "Please select image", Snackbar.LENGTH_SHORT);
                ColoredSnackbar.alert(snackbar).show();
            }
        }
    }
}
