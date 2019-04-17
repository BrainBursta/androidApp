package com.example.joonas.harjoitustyo;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Joonas on 17/02/2016.
 */
public class OnlineChangePassword extends AsyncTask<String,Void,String> {

    protected String oldPW,newPW,tunnus;
    protected boolean tarkistaja = false;
    public AsyncResponse delegate = null;

    public OnlineChangePassword(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    public interface AsyncResponse {
        void processFinish(boolean result);
    }

    @Override
    protected String doInBackground(String... params) {
        String changePW_url = "http://10.0.2.2/mobiiliJava/update_password.php";

        String tunnus = params[0];
        String oldPW = params[1];
        String newPW = params[2];

        try {
            URL url = new URL(changePW_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            OutputStream os = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            String data = URLEncoder.encode("uusiPW", "UTF-8") + "=" + URLEncoder.encode(newPW, "UTF-8") + "&" +
                    URLEncoder.encode("tunnus", "UTF-8") + "=" + URLEncoder.encode(tunnus, "UTF-8") + "&" +
                    URLEncoder.encode("vanhaPW", "UTF-8") + "=" + URLEncoder.encode(oldPW, "UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            os.close();
            InputStream is = httpURLConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            String response = "";
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                response += line + "\n";
            }
            bufferedReader.close();
            is.close();
            return response;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String response) {
        response = response.trim(); //poistaa perästä \n
        if (response.equals("Salasana vaihdettu"))
        {
            tarkistaja = true;
        }
        delegate.processFinish(tarkistaja);



        //super.onPostExecute(s);
    }
}
