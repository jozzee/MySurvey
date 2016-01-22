package com.jozzee.mysurvey.support;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jozzee.mysurvey.R;
import com.jozzee.mysurvey.bean.SurveyBeanForMySurvey;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by Jozzee on 13/10/2558.
 */
public class Support {
    private static String TAG = Support.class.getSimpleName();
    public Support() {
    }

    public String getURLLink(){
        return "http://192.168.237.148:8080/MySurvey/mysurvey/control";
    }

    public void showSnackBarNotConnectToServer(View viewAnyWhere){
        if(viewAnyWhere != null){
            Snackbar.make(viewAnyWhere, "Can't connect to Server.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }
    public void showSnackBarEmailNotChange(View viewAnyWhere){
        if(viewAnyWhere != null){
            Snackbar.make(viewAnyWhere, "Email not changed.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

    }
    public void showSnackBarNameNotChange(View viewAnyWhere){
        if(viewAnyWhere != null){
            Snackbar.make(viewAnyWhere, "Name not changed.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

    }
    public void showSnackBarNoQuestion(View viewAnyWhere){
        if(viewAnyWhere != null){
            Snackbar.make(viewAnyWhere, "No questions in this survey.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

    }
    public void showSnackBarAllSurvey(View viewAnyWhere){
        if(viewAnyWhere != null){
            Snackbar.make(viewAnyWhere, "All Survey.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }
    public void showSnackBarNoNewSurvey(View viewAnyWhere){
        if(viewAnyWhere != null){
            Snackbar.make(viewAnyWhere, "No new Survey.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }
    public void showSnackBarNotSelectImage(View viewAnyWhere){
        if(viewAnyWhere != null){
            Snackbar.make(viewAnyWhere, "Please select image or skip.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }
    public void showSnackBarMadeSurveyView(View viewAnyWhere){
        if(viewAnyWhere != null){
            Snackbar.make(viewAnyWhere, "You made this survey", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    public boolean checkNetworkConnection(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected())?true:false;

    }

    public String encodeLink(int length){
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder( length );
        for( int i = 0; i < length; i++ ){
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        }
        return sb.toString();
    }
    public String getCodeImage(String imageUrl){
        String[] sub = imageUrl.split("-");
        Log.i(TAG,"codeImage =" +sub[1] );
        return sub[1];
    }
    public int accentClolr(){
        return Color.rgb(255,64,192);
    }
    public int whiteColor(){
        return Color.rgb(255,255,255);
    }
    public int primaryColor(){
        return Color.rgb(33,150,243);
    }
    public int primaryDarkColor(){
        return Color.rgb(25,188,210);
    }
    public static final int[] colorsSet ={
            Color.rgb(244, 67, 54), //Red 1
            Color.rgb(233,30,99),//Pink 2
            Color.rgb(156,39,176),//Purple 3
            Color.rgb(103, 58, 183),//Peep Purple 4
            Color.rgb(63, 81, 181), //Indigo 5
            Color.rgb(33, 150, 243),//Blue 6
            Color.rgb(0, 188, 212),//Cyan 7
            Color.rgb(0, 150, 136),//Teal 8
            Color.rgb(76, 175, 80),//Green 9
            Color.rgb(139, 195, 74),//Light Green 10
            Color.rgb(205, 220, 57),//Lime 11
            Color.rgb(255, 235, 59),//Yellow 12
            Color.rgb(255, 193, 7),//Amber 13
            Color.rgb(255, 152, 0),//Orange 14
            Color.rgb(255, 87, 34),//Deep Orange 15
            Color.rgb(121, 85, 72),//Brown 16
            Color.rgb(158, 158, 158),//Grey 17
    };
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Bitmap blurImage(Context context,Bitmap bitmap, int radius){

        try {
            bitmap = RGB565toARGB888(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bitmap bitmapBlur = Bitmap.createBitmap(
                bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, bitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmapBlur);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));

        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmapBlur);
        renderScript.destroy();
        return bitmapBlur;
    }
    public Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }


    public static void setProgressBarColor(ProgressBar mProgressBar, int mColor){
        mProgressBar.getIndeterminateDrawable().setColorFilter(mColor, android.graphics.PorterDuff.Mode.MULTIPLY);
    }


    public String getNameManageQuestionTitle(String action){
        String title = null;
        if(action.equals("add")){
            title =" Add Question";
        }
        else if(action.equals("update")){
            title = "Edit Question";
        }
        else if(action.equals("delete")){
            title = "Delete Question";
        }
        return title;
    }

    public String getDateTime(){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm",java.util.Locale.US).format(new Date());
    }

    protected String encodeUTF8(String input){ //เข้ารหัส UTF-8 เช่น & ก็จะเป็น %26
        String resData = "";
        try {
            resData = URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return resData;
    }
    protected String DecodeData(String Decode_Data){ //ถอดรหัส UTF-8 เช่น %26 ก็จะเป็น &
        String resData = null;
        try {
            resData = URLDecoder.decode(Decode_Data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return resData;
    }

}
