package com.goldadorn.main.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.goldadorn.main.activities.post.AbstractPostActivity;
import com.goldadorn.main.dj.uiutils.WindowUtils;
import com.goldadorn.main.dj.utils.Constants;
import com.squareup.picasso.Picasso;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by bhavinpadhiyar on 2/22/16.
 */
public class ImageSelector {
    public static final int PICK_IMAGE = 1;
    public static final int PICK_CAMERA_IMAGE = 0;

    protected RegisterImageUploadCallBack registerImageUploadCallBack;

    protected Activity activity;
    protected ImageView holder;
    protected ImageButton triger;
    protected File file;
    protected Uri imageUri;
    protected int maxSize = -1;
    protected boolean isCameraActive;
    protected int selectedMethod;

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }


    public String getFilePath() {
        if (getFile() != null)
            return getFile().getAbsolutePath();
        return null;
    }

    public File getFile() {
        return file;
    }

    public ImageSelector(AbstractPostActivity activity, RegisterImageUploadCallBack registerImageUploadCallBack) {
        this.activity = activity;
        this.registerImageUploadCallBack = registerImageUploadCallBack;
    }

    public ImageSelector(AbstractPostActivity activity, RegisterImageUploadCallBack registerImageUploadCallBack, ImageView holder, ImageButton triger) {
        this.activity = activity;
        this.holder = holder;
        this.triger = triger;
        this.registerImageUploadCallBack = registerImageUploadCallBack;
        if (triger != null)
            triger.setOnClickListener(tigerHandel);

        if (holder != null)
            holder.setOnClickListener(tigerHandel);
    }

    public ImageSelector(AbstractPostActivity activity, RegisterImageUploadCallBack registerImageUploadCallBack, ImageView holder) {
        this.registerImageUploadCallBack = registerImageUploadCallBack;
        this.activity = activity;
        this.holder = holder;
        if (holder != null)
            holder.setOnClickListener(tigerHandel);
    }


    View.OnClickListener tigerHandel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //trigerUpload();
           /* if (v instanceof ImageView) {
                Log.d("djgallery", "trigger by new");
                isInEdit = false;
                openStandardPopup(false);
            }
            else if (v instanceof ImageButton){
                Log.d("djgallery", "trigger by edit");
                isInEdit = true;
                openStandardPopup(true);
            }*/
            openStandardPopup();
        }
    };


    public static class Item {
        public final String text;
        public final int icon;

        public Item(String text, int icon) {
            this.text = text;
            this.icon = icon;
        }

        @Override
        public String toString() {
            return text;
        }
    }


    public void trigerUpload() {
        //openStandardPopup();
    }

    protected void openStandardPopup() {
        final Item[] items = getOptions();
        String[] list = new String[items.length];
        for (int i = 0; i < items.length; i++) {
            list[i] = items[i].text;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Select Uploading Method");
        builder.setItems(list, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                onOptionSelect(which);

            }
        });
        builder.show();
    }

    public void onOptionSelect(int which) {
        if (which == PICK_CAMERA_IMAGE) {
            if (Constants.CURRENT_API_LEVEL >= 23){
                checkPermsAndReqIfNeed(Constants.REQUEST_WRITE_EXTERNAL_STORAGE);
            }
            else takeAPicture();
        } else if (which == PICK_IMAGE) {
            if (Constants.CURRENT_API_LEVEL >= 23){
                checkPermsAndReqIfNeed(Constants.REQUEST_READ_EXTERNAL_STORAGE);
            }
            else selectAPicture();
        }
    }

    public void selectAPicture() {
        Intent gintent = new Intent();
        gintent.setType("image/*");
        gintent.setAction(Intent.ACTION_GET_CONTENT);
        registerImageUploadCallBack.registerImageUploadCallBack(this);
        activity.startActivityForResult(Intent.createChooser(gintent, "Select Picture"), PICK_IMAGE);
    }


    public void onRequestPermissionsResultCustom(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case Constants.REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //// TODO: 24-05-2016 read the gallery here
                    Log.d(Constants.TAG, "read perm granted");
                    selectAPicture();

                } else {
                    Toast.makeText(activity.getApplicationContext(), Constants.INFO_MSG_PERM_DENIED, Toast.LENGTH_LONG).show();
                }

                return;
            }

            case Constants.REQUEST_WRITE_EXTERNAL_STORAGE: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //// TODO: 24-05-2016 write to external storage here
                    Log.d(Constants.TAG, "write perm granted");
                    checkPermsAndReqIfNeed(Constants.REQUEST_CAMERA);
                    break;
                } else {
                    Toast.makeText(activity.getApplicationContext(), Constants.INFO_MSG_PERM_DENIED, Toast.LENGTH_LONG).show();
                }

                return;
            }

            case Constants.REQUEST_CAMERA: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //// TODO: 24-05-2016 goto to camera from here
                    Log.d(Constants.TAG, "camera perm granted");
                    takeAPicture();

                } else {
                    Toast.makeText(activity.getApplicationContext(), Constants.INFO_MSG_PERM_DENIED, Toast.LENGTH_LONG).show();
                }

                return;
            }

        }

    }


    private void checkPermsAndReqIfNeed(int requestCode) {

        switch (requestCode) {

            case Constants.REQUEST_READ_EXTERNAL_STORAGE: {

                if (ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        //A dialog to show an explanation
                        Log.d(Constants.TAG, "read perm request, explanation needed");
                        WindowUtils.getInstance(activity.getApplicationContext()).
                                genericPermissionInfoDialog(activity, Constants.INFO_MSG_READ_GALLERY);


                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                Constants.REQUEST_READ_EXTERNAL_STORAGE);

                    } else {
                        // No explanation needed, we can request the permission.
                        Log.d(Constants.TAG, "read perm requested");
                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                Constants.REQUEST_READ_EXTERNAL_STORAGE);
                    }
                } else {
                    // TODO: 24-05-2016
                    Log.d(Constants.TAG, "read perm already granted");
                    selectAPicture();

                }
                return;
            }


            case Constants.REQUEST_WRITE_EXTERNAL_STORAGE: {

                if (ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Log.d(Constants.TAG, "write perm request, explanation needed");
                        //A dialog to show an explanation
                        WindowUtils.getInstance(activity.getApplicationContext()).
                                genericPermissionInfoDialog(activity, Constants.INFO_MSG_WRITE);

                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                Constants.REQUEST_WRITE_EXTERNAL_STORAGE);
                    } else {
                        // No explanation needed, we can request the permission.
                        Log.d(Constants.TAG, "write perm requested");
                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                Constants.REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                } else {
                    //// TODO: 24-05-2016 (this cannot be used as generic; its customized)
                    Log.d(Constants.TAG, "write perm already granted");
                    checkPermsAndReqIfNeed(Constants.REQUEST_CAMERA);
                }

                return;
            }

            case Constants.REQUEST_CAMERA: {

                if (ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                            Manifest.permission.CAMERA)) {
                        //A dialog to show an explanation
                        Log.d(Constants.TAG, "camera perm request, explanation needed");
                        WindowUtils.getInstance(activity.getApplicationContext()).
                                genericPermissionInfoDialog(activity, Constants.INFO_MSG_CAMERA);

                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.CAMERA},
                                Constants.REQUEST_CAMERA);

                    } else {
                        // No explanation needed, we can request the permission.
                        Log.d(Constants.TAG, "camera perm requested");
                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.CAMERA},
                                Constants.REQUEST_CAMERA);
                    }
                } else {
                    //// TODO: 24-05-2016
                    Log.d(Constants.TAG, "camera perm already granted");
                    takeAPicture();
                }

                return;
            }
        }

    }


    public void takeAPicture() {
        String fileName = "goldadorn_social_post_" + IDUtils.generateViewId() + ".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        dir = new File(dir, "GoldAdorn");
        if (!dir.exists())
            dir.mkdir();
        File target = new File(dir, fileName);

        imageUri = Uri.fromFile(target);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        registerImageUploadCallBack.registerImageUploadCallBack(this);
        activity.startActivityForResult(intent, PICK_CAMERA_IMAGE);
    }

    public boolean isValid() {
        return file != null;
    }

    public void imageLoaded(Bitmap bitmap, File file, int method) {
        if (getMaxSize() != -1) {
            if ((bitmap.getHeight() > getMaxSize() || bitmap.getWidth() > getMaxSize()))
                this.file = resizeAndSave(bitmap, file, getMaxSize());
            else
                this.file = file;
        } else
            this.file = file;

        if (holder != null)
            Picasso.with(activity).load(this.file).into(holder);

        if (file != null && file.exists() && file.canRead()) {
            if (triger != null)
                triger.setVisibility(View.GONE);

            if (holder != null)
                holder.setVisibility(View.VISIBLE);

            selectedMethod = method;
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri selectedImageUri = null;

        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK)
                    selectedImageUri = data.getData();
                break;
            case PICK_CAMERA_IMAGE:
                isCameraActive = true;
                if (resultCode == Activity.RESULT_OK)
                    selectedImageUri = imageUri;
                break;
        }


        if (selectedImageUri != null) {
            try {
                String filePath = null;
                String selectedImagePath = getPath(activity, selectedImageUri);
                if (selectedImagePath != null) {
                    filePath = selectedImagePath;
                } else {
                    String filemanagerstring = selectedImageUri.getPath();
                    filePath = filemanagerstring;
                }

                if (filePath != null) {
                    Bitmap bitmap = decodeFile(filePath);
                    File file = new File(filePath);
                    if (file.exists()) {
                        if (bitmap == null)
                            bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), selectedImageUri);
                        imageLoaded(bitmap, file, requestCode);
                    }
                }

            } catch (Exception e) {

            }
        }
        registerImageUploadCallBack.unRegisterImageUploadCallBack(this);
        isCameraActive = false;
    }


    public Bitmap decodeFile(String filePath) {

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        Bitmap out = BitmapFactory.decodeFile(filePath, o);
        return out;
    }


    private File resizeAndSave(Bitmap bMap, File targetFile, int maxSize) {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        dir = new File(dir, "GoldAdorn");
        if (!dir.exists())
            dir.mkdir();

        int originalWidth = bMap.getWidth();
        int originalHeight = bMap.getHeight();
        int newWidth = -1;
        int newHeight = -1;
        float multFactor = -1.0F;
        if (originalHeight > originalWidth) {
            newHeight = maxSize;
            multFactor = (float) originalWidth / (float) originalHeight;
            newWidth = (int) (newHeight * multFactor);
        } else if (originalWidth > originalHeight) {
            newWidth = maxSize;
            multFactor = (float) originalHeight / (float) originalWidth;
            newHeight = (int) (newWidth * multFactor);
        } else if (originalHeight == originalWidth) {
            newHeight = maxSize;
            newWidth = maxSize;
        }


        String fileName = "goldadorn_social_post_" + IDUtils.generateViewId() + ".jpg";
        Bitmap out = Bitmap.createScaledBitmap(bMap, newWidth, newHeight, false);
        File resizedFile = new File(dir, fileName);
        try {
            if (!resizedFile.exists())
                resizedFile.createNewFile();
        } catch (Exception e) {
        }


        OutputStream fOut = null;
        try {
            fOut = new BufferedOutputStream(new FileOutputStream(resizedFile));
            out.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {

        }
        if (isCameraActive)
            targetFile.delete();
        out.recycle();
        bMap.recycle();
        return resizedFile;
    }

    public Item[] getOptions() {
        Item[] items = {
                new Item("Take a picture", android.R.drawable.ic_menu_camera),
                new Item("Select from gallery", android.R.drawable.ic_menu_gallery)
        };
        return items;
    }

    public static interface RegisterImageUploadCallBack {
        void registerImageUploadCallBack(ImageSelector imageSelector);

        void unRegisterImageUploadCallBack(ImageSelector imageSelector);
    }


    /**
     * Method for return file path of Gallery image
     *
     * @param context
     * @param uri
     * @return path of the selected image file from gallery
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        //check here to KITKAT or new version
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is <span id="IL_AD8" class="IL_AD">Google Photos</span>.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
