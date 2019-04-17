package com.example.joonas.harjoitustyo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.content.Context;
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
 * Created by Joonas on 08/02/2016.
 */
public class RegisterBackground extends AsyncTask<String,Void,String> {
    Context ctx;
    AlertDialog alertDialog;
    boolean online = false;
    int x = 0;
    protected String tunnus,salasana, nimi, info, oldPW;
    RegisterBackground (Context ctx, AsyncResponse delegate)
    {
        this.ctx = ctx;
        this.delegate = delegate;
    }

    public String getTunnus() {
        return tunnus;
    }

    public void setTunnus(String tunnus) {
        this.tunnus = tunnus;
    }
    public String getNimi() {
        return nimi;
    }
    public void setOldPW(String oldPW) {
        this.oldPW = oldPW;
    }
    public String getOldPW() {
        return oldPW;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getSalasana() {
        return salasana;
    }

    public void setSalasana(String salasana) {
        this.salasana = salasana;
    }

    public interface AsyncResponse {
        void processFinish(boolean result);
    }

    public AsyncResponse delegate = null;

    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.setTitle("Kirjautumistiedot");
    }

    @Override
    protected String doInBackground(String... params) {

        String register_url ="http://10.0.2.2/mobiiliJava/register.php";
        String login_url = "http://10.0.2.2/mobiiliJava/login.php";
        String method =  params[0];
        if (method.equals("register")) //******************tunnuksen luontiosa
        {
            String nimi = params[1];
            String tunnus = params[2];
            String salasana = params[3];
            String info = params[4];
            try {
                URL url = new URL(register_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String data = URLEncoder.encode("nimi", "UTF-8") +"="+URLEncoder.encode(nimi,"UTF-8")+"&"+
                        URLEncoder.encode("tunnus", "UTF-8") +"="+URLEncoder.encode(tunnus,"UTF-8")+"&"+
                        URLEncoder.encode("salasana", "UTF-8") +"="+URLEncoder.encode(salasana,"UTF-8")+"&"+
                        URLEncoder.encode("info", "UTF-8") +"="+URLEncoder.encode(info,"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();
                InputStream is = httpURLConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"), 8);
                String response = "";
                String line = null;
                while ((line = bufferedReader.readLine()) != null)
                {
                    response += line + "\n";
                }
                bufferedReader.close();

                is.close();
                return response;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) { // Antaa FileNotFoundExceptionin, jos register_url väärä
                e.printStackTrace();
                return "ERROR";
            }
        }
        else if (method.equals("login")) //***************kirjautumisosa
        {
            try {
                String tunnus = params[1];
                String salasana = params[2];
                String vanhaPW = getOldPW();
                if (!vanhaPW.isEmpty()) {
                    salasana = vanhaPW;
                }
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream os = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String data = URLEncoder.encode("tunnus", "UTF-8") +"="+URLEncoder.encode(tunnus,"UTF-8")+"&"+
                        URLEncoder.encode("salasana", "UTF-8") +"="+URLEncoder.encode(salasana,"UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                os.close();
                InputStream is = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"), 8);
                String response = "";
                String line = null;
                while ((line = bufferedReader.readLine()) != null)
                {
                    response += line;
                }
                bufferedReader.close();
                is.close();
                httpURLConnection.disconnect();

                int f = 1;
                setX(f);
                return response;

            }  catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) { // Antaa FileNotFoundExceptionin, jos login_url väärä
            e.printStackTrace();
                return "ERROR";
        }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
    protected void onPostExecute(String result) {
        result = result.trim();
        if (result.equals("ERROR")) {
            online = false;

        }
        else if (result.equals("Tunnus luotu tietokantaan.."))
        {
                online = true; //Tietokanta yhteys toimii kerrotaan tieto LoginActivitylle..
        }
        else if (result.equals("Kirjautuminen ei onnistunut.."))
        {
            online = false;
        }
        else if (result.equals(nimi+":"+info))
        {
            online = true;
        }
        delegate.processFinish(online);

    }

}
