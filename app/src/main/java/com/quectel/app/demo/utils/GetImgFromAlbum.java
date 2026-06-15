package com.quectel.app.demo.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;


public class GetImgFromAlbum {
    /**
     * Get absolute path of an image from URI
     *
     * @param context Application context
     * @param uri     Image URI
     * @return Absolute path if the image exists, otherwise null
     */

    @SuppressLint("ObsoleteSdkInt")
    public static String getRealPathFromUri(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return getRealPathFromUriAboveApiAndroidQ(context, uri);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return getRealPathFromUriAboveApiAndroidK(context, uri);
        } else {
            return getRealPathFromUriBelowApiAndroidK(context, uri);
        }
    }


    public static String getPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Adapted for photo picking on Android 10+
     *
     * @param context Application context
     * @param uri     Image URI
     * @return Image path
     */
    private static String getRealPathFromUriAboveApiAndroidQ(Context context, Uri uri) {
        Cursor cursor = null;
        String path = getRealPathFromUriAboveApiAndroidK(context, uri);
        try {
            cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                    new String[]{path}, null);
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/images/media");
                return String.valueOf(Uri.withAppendedPath(baseUri, "" + id));
            } else {
                // If the image is not in the shared media database, insert it first.
                if (new File(path).exists()) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, path);
                    return String.valueOf(context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values));
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * Adapted for Android below 4.4 (excl. API 19): get absolute path from URI
     *
     * @param context Application context
     * @param uri     Image URI
     * @return Absolute path if the image exists, otherwise null
     */
    private static String getRealPathFromUriBelowApiAndroidK(Context context, Uri uri) {
        return getDataColumn(context, uri, null, null);
    }





    /**
     * Adapted for Android 4.4+ (API 19+): get absolute path from URI
     *
     * @param context Application context
     * @param uri     Image URI
     * @return Absolute path if the image exists, otherwise null
     */
    @SuppressLint("NewApi")
    private static String getRealPathFromUriAboveApiAndroidK(Context context, Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // If URI is a document type, process via document ID
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) {
                // Split by ':'
                String id = documentId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
            } else if (isDownloadsDocument(uri)) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
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
