package com.goldadorn.main.activities.imagePicker;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;

import com.goldadorn.main.activities.productListing.ServerProducts;
import com.goldadorn.main.model.ServerFolderObject;
import com.goldadorn.main.views.ColoredSnackbar;

/**
 * Created by BhavinPadhiyar on 19/04/16.
 */
public class PickServerProducts  extends ServerProducts{
    public void processItem(ServerFolderObject data) {
        if(data.getPath()!=null) {
            Intent intent = new Intent();
            intent.putExtra("PATH", data.getPath());
            intent.putExtra("PREVIEW", data.getPreview());
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        else {
            final Snackbar snackbar = Snackbar.make(getLayoutParent(), "Please select image", Snackbar.LENGTH_SHORT);
            ColoredSnackbar.alert(snackbar).show();
        }
    }
}
