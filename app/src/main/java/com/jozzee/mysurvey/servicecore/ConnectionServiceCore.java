package com.jozzee.mysurvey.servicecore;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Jozzee on 23/9/2558.
 */
public class ConnectionServiceCore extends OkHttpClient{
    private static final String TAG = ConnectionServiceCore.class.getSimpleName();

    public ConnectionServiceCore(){

    }

    public String doOKHttpPostString(String url,String command,String requestData) {
        Log.i(TAG, "doOKHttpPostString");
        String resultData = "connectionLost";

        MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");//application/json; charset=utf-8 //text/x-markdown; charset=utf-8
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("command", command)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN, requestData))
                .build();
        try {
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                resultData = response.body().string();
            }
            else{
                Log.i(TAG,"Error!! Unexpected code: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultData ;
    }
    public String doOKHttpGetString(String url){
        String resultData = "connectionLost";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                resultData = response.body().string();

            }
            else{
                Log.i(TAG,"Error!! Unexpected code: " + response);
            }

        }catch (IOException e) {
            e.printStackTrace();
        }
        return resultData;
    }
    //-----------------------------------------------------------------------------------------------------------------------------------
    public String uploadImage(String urlLink, String command ,String imageType, String filePath, int id,int surveyVersion,String codeImage) {
        //imageType is "ProfileImage" or "SurveyImage"
        //id is accountID in ProfileImage type or id is surveyID in SurveyImageType
        //use "surveyVersion" have if upload SurveyImage, don't use this when upload ProfileImage set this parameter = ""
        //command UploadImage or UpdateImage
        Log.i(TAG, command);
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        String responseData = "connectionLost";
        int BUFFER_SIZE = 4096;

        File imageToUpload = new File(filePath);
        Log.i(TAG, "path of imageToUpload: " + imageToUpload.getAbsolutePath());

        try {
            URL url = new URL(urlLink);
            connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            //connection.setRequestMethod("POST");

            // set file name as a HTTP header
            connection.setRequestProperty("command", command);
            connection.setRequestProperty("imageType", imageType);
            connection.setRequestProperty("id", String.valueOf(id));
            connection.setRequestProperty("surveyVersion", String.valueOf(surveyVersion));
            connection.setRequestProperty("codeImage", codeImage);
            // opens output stream of the HTTP connection for writing data
            OutputStream outputStream = connection.getOutputStream();
            // Opens input stream of the file for reading data
            FileInputStream fileInputStream = new FileInputStream(imageToUpload);
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;

            Log.i(TAG, "Start upload image...");
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            Log.i(TAG, "Upload a image finish");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.i(TAG, "connection OK ,responseCode: " + connection.getResponseCode());

                inputStream = connection.getInputStream();
                responseData = readIt(inputStream);
                Log.i(TAG, "responseData: " + responseData);
            }
            else{
                responseData = "connectionLost";
            }
            outputStream.flush();
            outputStream.close();
            fileInputStream.close();
            inputStream.close();
            connection.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseData;
    }
    //------------------------------------------------------------------------------------------------------------------------
    public String doOKHttpPostString(String url,String requestData) {
        Log.i(TAG,"use method doOKHttpPostString");
        MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
        String responseData = null;
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MEDIA_TYPE_MARKDOWN,requestData))
                .build();
        try {
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                responseData = response.body().string();
                Log.i(TAG, "responseData from doOKHttpPostString: " + responseData);
            }
            else{
                Log.i(TAG,"Error!! from doOKHttpPostString Unexpected code: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseData ;
    }
    //----------------------------------------------------------------



    //----------------------------------------------------------------------------------------------------------------------------------
    public String doHttpURLConnection(String link,String method,String parameters){
        Log.i(TAG,"Use method doHttpURLConnection");
        final String USER_AGENT = "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/13.0.782.215 Safari/535.1";
        String resultUploadImage = "";
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try{
            URL url = new URL(link);
            connection = (HttpURLConnection)url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            if(method.equals("GET")){
                Log.i(TAG,"methodGET");
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
            }
            else if(method.equals("POST")){
                Log.i(TAG, "methodPOST");
                connection.setDoInput(true);
                connection.setDoOutput(true); // Send to "POST" method auto
                connection.setInstanceFollowRedirects(true);
                connection.setRequestProperty("User-Agent", USER_AGENT);

                //connection.connect();
                outputStream = connection.getOutputStream();
                outputStream.write(parameters.getBytes());
                outputStream.flush();
            }
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                Log.i(TAG, "The connection is Connected : HTTP error code : " + connection.getResponseCode());
                inputStream = connection.getInputStream();
                resultUploadImage = readIt(inputStream);
            }
            else{
                resultUploadImage ="connectionLost";
                Log.i(TAG, "The connection is Failed : HTTP error code : " + connection.getResponseCode());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
            closeOutputStream(outputStream);
            closeInputStream(inputStream);
        }
        return resultUploadImage;
    }



    public String readIt(InputStream inputStream){
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer buffer = new StringBuffer();
        String line;
        try {
            while((line = reader.readLine()) != null){
                buffer.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeBufferedReader(reader);
        }
        Log.i(TAG,"Content result is:" +buffer.toString());
        return buffer.toString();
    }
    public void closeBufferedReader(BufferedReader reader){
        try{
            if(reader != null){
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void closeOutputStream(OutputStream outputStream){
        try{
            if(outputStream != null){
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void closeInputStream(InputStream inputStream){
        try{
            if(inputStream != null){
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public  void closeConnection(HttpURLConnection connection){
        if(connection != null){
            connection.disconnect();
        }
    }

    /*public void checkConnectInternet(){ //for check the connection to internet
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            Toast.makeText(getApplicationContext(),"is Connect",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"No Connect",Toast.LENGTH_SHORT).show();
        }
    }*/

    /*private class ConnectionService extends AsyncTask<String,Void,String>{ //template AsyncTask to connect service
        protected void onPreExecute() {
            // Create Show ProgressBar
            Log.i(TAG, "onPreExecute");
        }
        @Override
        protected String doInBackground(String... params) {
            return null; //something
        }
        protected void onPostExecute(String result) {
            // Dismiss ProgressBarre
            Log.i(TAG, "onPostExecute");
        }
    }*/

}
