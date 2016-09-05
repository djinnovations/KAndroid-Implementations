package com.goldadorn.main.utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.goldadorn.main.server.ApiFactory;

/**
 * Created by bhavinpadhiyar on 2/23/16.
 */
public class ImageFilePath {
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

    public static String getDesignerCertUrl(int desId){
        //http://goldadorn.tuxer5qf9ekl44m.netdna-cdn.com/certs/1661.jpg
        /*return new StringBuilder().append(ApiFactory.IMAGE_URL_COLLECTIONS_HOST).append("certs/")
                .append(String.valueOf(desId)).append(".jpg").toString();*/
        String url = new StringBuilder().append(ApiFactory.IMAGE_URL_COLLECTIONS_HOST).append("certs/")
                .append(String.valueOf(desId)).append(".jpg").toString();
        Log.d("djurl","cert image url: "+ url);
        return url;
    }

    public static String getImageUrlForCollection(int collid) {
        return ApiFactory.IMAGE_URL_COLLECTIONS_HOST + "collections/" + collid + "/" + collid + ".jpg";
    }


    public static String getImageUrlForProduct(int desId, int productid, String defMetal, boolean isDefault) {
        return getNewConvention(desId, productid, defMetal, isDefault);
        //return ApiFactory.IMAGE_URL_COLLECTIONS_HOST+"products/"+productid+"/"+productid+"-1.jpg";
    }


    static String getNewConvention(int desId, int productid, String defMetal, boolean isNotDefault) {
        StringBuilder baseUrl = new StringBuilder(ApiFactory.IMAGE_URL_COLLECTIONS_HOST);
        if (!isNotDefault){
            baseUrl.append("defaults/");
        }
        baseUrl.append("products/");

        //char[] digitsDesId = String.valueOf(desId).toCharArray();
        /*for (char digit : digitsDesId) {*/
            baseUrl.append(desId).append("/");
        //}
        char[] digitsProdId = String.valueOf(productid).toCharArray();
        for (char digit : digitsProdId) {
            baseUrl.append(digit).append("/");
        }

        baseUrl.append(productid);
        if (isNotDefault)
            baseUrl.append("-"+defMetal);
        baseUrl.append("-1.jpg");
        Log.d("djimage", "new image url" + baseUrl.toString());
        return baseUrl.toString();
    }

    //products/2/1/1010-G18Y-1.jpg
    //S3url/products/defaults/2/1/3/3/4/1334-1.jpg

}
