package com.example.csdfcommunityapp;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadUrl {
    public String readTheUrl(String placeUrl)throws Exception{
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;

        try{
            URL url = new URL(placeUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String line = "";

            while((line=bufferedReader.readLine())!= null){
                stringBuffer.append(line);
            }

            data = stringBuffer.toString();
            bufferedReader.close();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            inputStream.close();
            httpURLConnection.disconnect();
        }

        return data;
    }

}
