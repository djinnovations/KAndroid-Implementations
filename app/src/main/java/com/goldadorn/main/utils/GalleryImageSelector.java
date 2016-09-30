package com.goldadorn.main.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.imagePicker.PickServerProducts;
import com.goldadorn.main.activities.post.AbstractPostActivity;
import com.goldadorn.main.activities.post.PostBestOfActivity;
import com.goldadorn.main.activities.post.PostNormalActivity;
import com.goldadorn.main.activities.post.PostPollActivity;
import com.goldadorn.main.activities.post.SelectServerImageActivity;
import com.goldadorn.main.dj.utils.IntentKeys;
import com.goldadorn.main.model.Image;
import com.goldadorn.main.model.People;
import com.goldadorn.main.model.Product;
import com.squareup.picasso.Picasso;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by bhavinpadhiyar on 2/22/16.
 */
public class GalleryImageSelector extends ImageSelector
{
    public static final int PICK_SERVER_GALLERY = 2;
    private String path;
    public int price;
    private int collId;
    private int desId;
    private int productId;
    private float discount;
    private String range;

    public String getRange() {
        return range;
    }

    public int getDiscount() {
        return Math.round(discount);
    }

    public int getProductId(){
        return productId;
    }

    public int getPrice(){
        return price;
    }

    public int getCollId(){
        return collId;
    }

    public int getDesId(){
        return desId;
    }

    public boolean isValid()
    {
        if(selectedMethod == PICK_SERVER_GALLERY && path!=null && path.equals("")==false)
            return true;
        else
            return super.isValid();
    }
    public Item[] getOptions() {
        Item[] items = {
                new Item("Take a picture", android.R.drawable.ic_menu_camera),
                new Item("Select from gallery", android.R.drawable.ic_menu_gallery),
                new Item("Select from our collections", R.drawable.vector_icon_about)
        };
        return items;
    }

    private boolean isPtb = false;
    public void setIsPtbCall(boolean isPtb){
        this.isPtb = isPtb;
    }
    public boolean getIsPtbCall(){
        return isPtb;
    }

    @Override
    protected void openStandardPopup()
    {
        final Item[] items = getOptions();
        String[] list= new String[items.length];
        for (int i = 0; i < items.length; i++) {
            list[i] = items[i].text;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //builder.setTitle("Select Uploading Method");

        View view = activity.getLayoutInflater().inflate(R.layout.image_upload_method,null);
        builder.setView(view);
        final AlertDialog alertDialog = builder.show();

        final View ourCollections =view.findViewById(R.id.collection);
        final View camera =view.findViewById(R.id.camera);
        final View gallery =view.findViewById(R.id.gallery);
        View text =view.findViewById(R.id.text);
        TypefaceHelper.setFont(text);
        View.OnClickListener onClick = new View.OnClickListener(){

            public void onClick(View v) {
                if(v==ourCollections)
                    onOptionSelect(PICK_SERVER_GALLERY);
                else if(v==camera)
                    onOptionSelect(PICK_CAMERA_IMAGE);
                else if(v==gallery)
                    onOptionSelect(PICK_IMAGE);

                alertDialog.dismiss();
            }
        };


        ourCollections.setOnClickListener(onClick);
        camera.setOnClickListener(onClick);
        gallery.setOnClickListener(onClick);

        //float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, activity.getResources().getDisplayMetrics());
        //alertDialog.getWindow().setLayout(Math.round(px),Math.round(px));
    }


    public void onOptionSelect(int which) {
        if (which == PICK_SERVER_GALLERY) {
            registerImageUploadCallBack.registerImageUploadCallBack(this);
            openServerSelecton();
        }
        else
            super.onOptionSelect(which);
    }

    public void openServerSelecton() {

        boolean live=false;
        if(live) {
            Intent intent = new Intent(activity, SelectServerImageActivity.class);
            intent.putExtra("backEnabled", true);
            activity.startActivityForResult(intent, PICK_SERVER_GALLERY);
        }else {
            Intent intent = new Intent(activity, PickServerProducts.class);
            intent.putExtra("backEnabled", true);
            intent.putExtra(IntentKeys.CALLER_PTB, isPtb);
            activity.startActivityForResult(intent, PICK_SERVER_GALLERY);
        }
    }
    public String getLink()
    {
        return path;
    }
    public GalleryImageSelector(AbstractPostActivity activity, RegisterImageUploadCallBack registerImageUploadCallBack)
    {
        super(activity,registerImageUploadCallBack);
    }
    public GalleryImageSelector(AbstractPostActivity activity, RegisterImageUploadCallBack registerImageUploadCallBack,
                                ImageView holder, ImageButton triger)
    {
        super(activity,registerImageUploadCallBack,holder,triger);
        isPtb = false;
    }
    public GalleryImageSelector(AbstractPostActivity activity, RegisterImageUploadCallBack registerImageUploadCallBack, ImageView holder)
    {
        super(activity,registerImageUploadCallBack,holder);
    }
     public void onActivityResult(int requestCode, int resultCode, Intent data)
     {
        if (requestCode==PICK_SERVER_GALLERY && resultCode == Activity.RESULT_OK)
        {
            if (!isPtb) {
                path = data.getStringExtra("PATH");
                price = data.getIntExtra("PRICE", -1);
                collId = data.getIntExtra("COLLID", -1);
                desId = data.getIntExtra("DESID", -1);
                productId = data.getIntExtra("PRODID", -1);
                discount = data.getFloatExtra(KEY_DISCOUNT, 0);
                range = data.getStringExtra(KEY_RANGE);
                file = null;
                selectedMethod = PICK_SERVER_GALLERY;

                showPreview(data.getStringExtra("PREVIEW"));
                registerImageUploadCallBack.unRegisterImageUploadCallBack(this);
            }
        }
        else
            super.onActivityResult(requestCode, resultCode,data);
    }

    public static final String KEY_PATH = "PATH";
    public static final String KEY_PRICE = "PRICE";
    public static final String KEY_COLLID = "COLLID";
    public static final String KEY_DESID = "DESID";
    public static final String KEY_PRODID = "PRODID";
    public static final String KEY_PREVIEW = "PREVIEW";
    public static final String KEY_DISCOUNT = "DISCOUNT";
    public static final String KEY_RANGE = "RANGE";

    public void setDataFromOutside(HashMap<String, Object> dataMap, int requestCode){
        if (requestCode==PICK_SERVER_GALLERY ) {
            path= (String) dataMap.get(KEY_PATH);
            price= (Integer) dataMap.get(KEY_PRICE);
            collId = (Integer) dataMap.get(KEY_COLLID);
            desId = (Integer) dataMap.get(KEY_DESID);
            productId = (Integer) dataMap.get(KEY_PRODID);
            discount = (float) dataMap.get(KEY_DISCOUNT);
            range = (String) dataMap.get(KEY_RANGE);
            file=null;
            selectedMethod = PICK_SERVER_GALLERY;
            showPreview((String) dataMap.get(KEY_PREVIEW));
            registerImageUploadCallBack.unRegisterImageUploadCallBack(this);
        }
    }

    public void imageLoaded(Bitmap bitmap,File file,int method) {
        super.imageLoaded(bitmap,file,method);
        if(method!=PICK_SERVER_GALLERY)
            path=null;

        if(triger!=null)
            triger.setVisibility(View.VISIBLE);
    }

    public void showPreview(String preview) {
        if(holder!=null) {
            String url = Product.getImageUrl(productId, desId, null, false, 400);
            Picasso.with(activity).load(url).into(holder);
        }

        if(triger!=null)
            triger.setVisibility(View.VISIBLE);
    }

    public Bitmap decodeFile(String filePath) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        Bitmap out = BitmapFactory.decodeFile(filePath,o);
        return out;
    }


    public void onRequestPermResult(int requestCode, String permissions[], int[] grantResults){

        super.onRequestPermissionsResultCustom(requestCode, permissions, grantResults);
    }
}
