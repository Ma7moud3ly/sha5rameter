package com.ma7moud3ly.sha5rameter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

import androidx.core.app.ActivityCompat;

public class Screenshot {
    private Activity activity;
    private View view;
    public String subject;
    public String text;
    public String path;
    public String name;


    public Screenshot(Activity activity) {
        this.activity = activity;
    }

    public Screenshot(Activity activity, View view) {
        this.activity = activity;
        this.view = view;
        path = activity.getExternalCacheDir().getPath();
        name = "screenshot.png";
    }

    public Screenshot(Activity activity, View view, String subject, String text, String path, String name) {
        this.activity = activity;
        this.subject = subject;
        this.text = text;
        this.view = view;
        this.path = path;
        this.name = name;
    }

    private void Share(File file) {
        Uri uri = Uri.fromFile(file);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        if (!subject.isEmpty())
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (!text.isEmpty())
            intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            activity.startActivity(Intent.createChooser(intent, activity.getResources().getString(R.string.app_name)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "No App Available", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public void CaptureAndShare() {
        File shoot = Capture();
        if (shoot != null) Share(shoot);
        else Toast.makeText(activity, "no screenshot has token", Toast.LENGTH_SHORT).show();
    }

    public File Capture() {
        Bitmap bitmap = captureBitmap();
        return storeScreenShot(bitmap);
    }

    private Bitmap captureBitmap() {
        if (view != null) {
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            return bitmap;
        }
        View rootView = activity.getWindow().getDecorView();
        View screenView = rootView.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private File storeScreenShot(Bitmap bm) {
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();
        File file = new File(path + "/" + name);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
