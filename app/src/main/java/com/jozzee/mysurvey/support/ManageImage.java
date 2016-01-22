package com.jozzee.mysurvey.support;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.graphics.Palette;
import android.util.Base64;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jozzee on 26/11/2558.
 */
public class ManageImage {
    private static String TAG = ManageImage.class.getSimpleName();



    public void saveBitmapTpFile(Bitmap bitmap, String outputPath){Log.i(TAG, "saveBitmapTpFile");
        OutputStream outputStream = null;
        File file = new File(outputPath);//imagePath = file.getAbsolutePath();
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reduce width, height off a image.
     * @param width width off image in pixel.
     * @param height height off image in pixel.
     * @param max Maximum pixel of width or height.
     * @return reduce, reduce[0] is width after reduce and reduce[1] is height after reduce.
     */
    public int[] reduceWidthHeight(int width,int height,int max){
        int[] reduce = new int[]{width,height};
        float cal = 0;
        if(width > max || height >max){
            if(width>height || width == height){
                cal = (float)width/max;
            }
            else if(height>width){
                cal = (float)height/max;
            }
            reduce[0] = (int)(width/cal);
            reduce[1] = (int)(height/cal);
        }
        return reduce;
    }

    public static void imageViewAnimatedChange(Context context, final ImageView imageView, final Bitmap newBitmap) {
        final Animation anim_out = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.setImageBitmap(newBitmap);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }
                });
                imageView.startAnimation(anim_in);
            }
        });
        imageView.startAnimation(anim_out);
    }
    public Bitmap getBitmapFromURL(String url) { Log.i(TAG, "getBitmapFromURL");
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        InputStream input = null;
        try {
            URL urlLink = new URL(url);
            connection = (HttpURLConnection) urlLink.openConnection();
            connection.setDoInput(true);
            connection.connect();
            input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input); Log.i(TAG, "get Bitmap success");
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return bitmap;
    }
    private void addImageToGallery(String path,Context context) {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File file = new File(path);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
    public String getStringFromImage(Bitmap bmp) {
        Log.i(TAG, "getStringFromImage");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        Log.i(TAG, "encoded image: " + encodedImage);
        return encodedImage;
    }

}
