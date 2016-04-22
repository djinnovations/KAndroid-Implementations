package com.goldadorn.main.activities.imagePicker;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;

import com.goldadorn.main.activities.productListing.ServerProducts;
import com.goldadorn.main.model.FilterProductListing;
import com.goldadorn.main.model.ServerFolderObject;
import com.goldadorn.main.views.ColoredSnackbar;

/**
 * Created by BhavinPadhiyar on 19/04/16.
 */
public class PickServerProducts  extends ServerProducts{

    @Override
    public void processItem(FilterProductListing data) {
        if(data!=null) {
            Intent intent = new Intent();
            ////http://demo.eremotus-portal.com/goldadorn_dev/gallery/pendants/gallery.jpg
            String path=".."+data.getImage().substring(data.getImage().indexOf("/product"),data.getImage().length());
            intent.putExtra("PATH",path);
            intent.putExtra("PREVIEW", data.getImage());
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        else {
            final Snackbar snackbar = Snackbar.make(getLayoutParent(), "Please select image", Snackbar.LENGTH_SHORT);
            ColoredSnackbar.alert(snackbar).show();
        }
    }
}
