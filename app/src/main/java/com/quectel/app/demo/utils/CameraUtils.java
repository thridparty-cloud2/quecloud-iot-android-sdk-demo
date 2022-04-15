package com.quectel.app.demo.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraUtils {
    /**
     * 激活相册操作
     *
     * @param context     上下文
     * @param requestCode 请求码
     */
    public static void openPhotoAlbum(@NonNull Activity context, int requestCode) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 激活相机操作
     *
     * @param context        上下文
     * @param requestCode    请求码
     * @param fileProvider   自定义fileProvider
     * @param cameraSavePath 保存的文件路径
     * @return 相片的uri
     */
    public static Uri openCamera(@NonNull Activity context, int requestCode, String fileProvider,
                                 File cameraSavePath) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = createImageUri(context);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(context, fileProvider, cameraSavePath);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                uri = Uri.fromFile(cameraSavePath);
            }
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        context.startActivityForResult(intent, requestCode);
        return uri;
    }

    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     *
     * @param context 上下文
     * @return 图片地址uri
     */
    private static Uri createImageUri(@NonNull Activity context) {
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new ContentValues());
        } else {
            return context.getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                    new ContentValues());
        }
    }

    /**
     * 创建保存图片的文件
     *
     * @param context 上下文
     * @return 文件路径
     */
    public static File createImageFile(@NonNull Activity context) throws IOException {
        String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        File tempFile = new File(storageDir, imageName);
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }
        return tempFile;
    }


}
