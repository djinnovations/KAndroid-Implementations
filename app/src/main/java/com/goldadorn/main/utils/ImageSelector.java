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
import android.view.View;
import android.widget.ImageView;

import com.goldadorn.main.activities.post.AbstractPostActivity;
import com.squareup.picasso.Picasso;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by bhavinpadhiyar on 2/22/16.
 */
public class ImageSelector
{
    public static final int PICK_IMAGE = 1;
    public static final int PICK_CAMERA_IMAGE = 2;
    private RegisterImageUploadCallBack registerImageUploadCallBack;

    private Activity activity;
    private ImageView holder;
    private View triger;
    private File file;
    private Uri imageUri;
    private int maxSize=-1;
    private boolean isCameraActive;

    public int getMaxSize() {
        return maxSize;
    }
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }



    public String getFilePath() {
        if(getFile()!=null)
            return getFile().getAbsolutePath();
        return "";
    }
    public File getFile() {
        return file;
    }
    public ImageSelector(AbstractPostActivity activity, RegisterImageUploadCallBack registerImageUploadCallBack)
    {
        this.activity = activity;
        this.registerImageUploadCallBack =registerImageUploadCallBack;
    }
    public ImageSelector(AbstractPostActivity activity, RegisterImageUploadCallBack registerImageUploadCallBack, ImageView holder, View triger)
    {
        this.activity = activity;
        this.holder = holder;
        this.triger = triger;
        this.registerImageUploadCallBack =registerImageUploadCallBack;
        if(triger!=null)
            triger.setOnClickListener(tigerHandel);

        if(holder!=null)
            holder.setOnClickListener(tigerHandel);
    }
    public ImageSelector(AbstractPostActivity activity, RegisterImageUploadCallBack registerImageUploadCallBack, ImageView holder)
    {
        this.registerImageUploadCallBack =registerImageUploadCallBack;
        this.activity = activity;
        this.holder = holder;
        if(holder!=null)
            holder.setOnClickListener(tigerHandel);
    }
    View.OnClickListener tigerHandel=new View.OnClickListener() {
    @Override
        public void onClick(View v) {
            trigerUpload();
        }
    };

    public void trigerUpload()
    {
        CharSequence colors[] = new CharSequence[] {"Take a picture", "Select from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Select Uploading Method");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0)
                    takeAPicture();
                else
                    selectAPicture();
            }
        });
        builder.show();
    }

    public void selectAPicture() {
        Intent gintent = new Intent();
        gintent.setType("image/*");
        gintent.setAction(Intent.ACTION_GET_CONTENT);
        registerImageUploadCallBack.registerImageUploadCallBack(this);
        activity.startActivityForResult(Intent.createChooser(gintent, "Select Picture"), PICK_IMAGE);
    }

    public void takeAPicture() {
        String fileName = "goldadorn_social_post_"+ IDUtils.generateViewId()+".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");

        File dir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        dir = new File(dir,"GoldAdorn");
        if(!dir.exists())
            dir.mkdir();
        File target = new File(dir,fileName);

        imageUri = Uri.fromFile(target);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        registerImageUploadCallBack.registerImageUploadCallBack(this);
        activity.startActivityForResult(intent, PICK_CAMERA_IMAGE);
    }

    public boolean isValid()
    {
        return file!=null;
    }
    public void imageLoaded(Bitmap bitmap,File file) {
        if(getMaxSize()!=-1)
        {
            if((bitmap.getHeight()>getMaxSize() || bitmap.getWidth()>getMaxSize()))
                this.file = resizeAndSave(bitmap, file,getMaxSize());
            else
                this.file = file;
        }
        else
            this.file = file;

        if(holder!=null)
            Picasso.with(activity).load(this.file).into(holder);

        if(file!=null &&  file.exists() && file.canRead())
        {
            if(triger!=null)
                triger.setVisibility(View.GONE);

            if(holder!=null)
                holder.setVisibility(View.VISIBLE);
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
                isCameraActive=true;
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
                }
                else {
                    String filemanagerstring = selectedImageUri.getPath();
                    filePath = filemanagerstring;
                }

                if (filePath != null) {
                    Bitmap bitmap = decodeFile(filePath);
                    File file = new File(filePath);
                    if(file.exists())
                    {
                        if (bitmap == null)
                            bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), selectedImageUri);
                        imageLoaded(bitmap, file);
                    }
                }

            } catch (Exception e) {

            }
        }
        registerImageUploadCallBack.unRegisterImageUploadCallBack(this);
        isCameraActive=false;
    }



    public Bitmap decodeFile(String filePath) {

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        Bitmap out = BitmapFactory.decodeFile(filePath,o);
        return out;
    }


    private File resizeAndSave(Bitmap bMap,File targetFile,int maxSize)
    {
        File dir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        dir = new File(dir,"GoldAdorn");
        if(!dir.exists())
            dir.mkdir();

        int originalWidth = bMap.getWidth();
        int originalHeight = bMap.getHeight();
        int newWidth = -1;
        int newHeight = -1;
        float multFactor = -1.0F;
        if(originalHeight > originalWidth) {
            newHeight = maxSize;
            multFactor = (float) originalWidth/(float) originalHeight;
            newWidth = (int) (newHeight * multFactor);
        } else if(originalWidth > originalHeight) {
            newWidth = maxSize;
            multFactor = (float) originalHeight/ (float)originalWidth;
            newHeight = (int) (newWidth*multFactor);
        } else if(originalHeight == originalWidth) {
            newHeight = maxSize;
            newWidth = maxSize;
        }



        String fileName = "goldadorn_social_post_"+ IDUtils.generateViewId()+".jpg";
        Bitmap out = Bitmap.createScaledBitmap(bMap, newWidth, newHeight, false);
        File resizedFile = new File(dir,fileName);
        try {
            if(!resizedFile.exists())
                resizedFile.createNewFile();
        }catch(Exception e){}


        OutputStream fOut=null;
        try {
            fOut = new BufferedOutputStream(new FileOutputStream(resizedFile));
            out.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {

        }
        if(isCameraActive)
            targetFile.delete();
        out.recycle();
        bMap.recycle();
        return resizedFile;
    }

    public static interface RegisterImageUploadCallBack
    {
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
    public static String getPath(final Context context, final Uri uri)
    {

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
                final String[] selectionArgs = new String[] {
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
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
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