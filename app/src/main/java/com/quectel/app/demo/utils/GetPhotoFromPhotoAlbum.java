package com.quectel.app.demo.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * Utility class for converting photo URIs from the album
 *
 * @author : king.pan
 * @e-mail : king.pan@quectel.com
 * @date : 2020/5/13 18:34
 */
public class GetPhotoFromPhotoAlbum {
    /**
     * Get absolute path of an image from URI
     *
     * @param context Application context
     * @param uri     Image URI
     * @return Absolute path if the image exists, otherwise null
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= 19) {
            return getRealPathFromUriAboveApi19(context, uri);
        } else {
            return getRealPathFromUriBelowAPI19(context, uri);
        }
    }

    /**
     * Adapted for API below 19: get absolute path from URI
     *
     * @param context Application context
     * @param uri     Image URI
     * @return Absolute path if the image exists, otherwise null
     */
    private static String getRealPathFromUriBelowAPI19(Context context, Uri uri) {
        return getDataColumn(context, uri, null, null);
    }

    /**
     * Adapted for API 19+: get absolute path from URI
     *
     * @param context Application context
     * @param uri     Image URI
     * @return Absolute path if the image exists, otherwise null
     */
    @SuppressLint("NewApi")
    private static String getRealPathFromUriAboveApi19(Context context, Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // If URI is a document type, process via document ID
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) {
                // Split by ':'
                String id = documentId.split(":")[1];

                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection,
                        selectionArgs);
            } else if (isDownloadsDocument(uri)) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(documentId));
                filePath = getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // If URI is of content type
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.getScheme())) {
            // If URI is of file type, get the path directly
            filePath = uri.getPath();
        }
        return filePath;
    }

    /**
     * Get the _data column from the media database (i.e. file path for the URI)
     *
     * @param context       Application context
     * @param uri           URI path
     * @param selection     Query selection clause
     * @param selectionArgs Query selection arguments
     * @return File path corresponding to the URI
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;

        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is MediaProvider
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is DownloadsProvider
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

}
