package com.example.joonas.harjoitustyo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import static android.Manifest.permission.READ_CONTACTS;


public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Intent intent;
    private Button button_register;

    Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ctx = this;
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        button_register = (Button)findViewById(R.id.btnRegister);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        button_register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) { // kutsuu db kirjautumista

                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Tunnuksen ja salasanan tarkistus. Kutsuu aSyncTaskia jos tarkistus menee läpi
     */
    public  void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_incorrect_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true); //tarkistus meni läpi
            mAuthTask = new UserLoginTask(email, password,ctx);
            mAuthTask.execute((String) null);
        }
    }

    private boolean isEmailValid(String userId) {
        return userId.length() > 3;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 3;
    }

    /**
     * Latausanimaatio
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Online ja Offline Login Task
     */
    public class UserLoginTask extends AsyncTask<String, Void, String> {

        private final String mEmail;
        private final String mPassword;
        Context childCtx;
        File file;
        UserLoginTask(String email, String password, Context ctx) {
            mEmail = email;
            mPassword = password;
            this.childCtx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                Thread.sleep(2000); //lisätään latausaikaa
            } catch (InterruptedException e) {
                return null;
            }
                try {
                    String login_url = "http://10.0.2.2/mobiiliJava/login.php"; //tähän pitää vaihtaa tiedoston osoite
                    String tunnus = mEmail;
                    String salasana = mPassword;
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
                    while ((line = bufferedReader.readLine()) != null) //php vastaus
                    {
                        response += line + "\n";
                    }
                    bufferedReader.close();
                    is.close();
                    httpURLConnection.disconnect();

                    return response;

                }  catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) { // Antaa FileNotFoundExceptionin, jos login_url väärä
                    e.printStackTrace();
                    return "ERROR";
                }
            return null;
        }

        @Override
        protected void onPostExecute(String success) {
            mAuthTask = null;
            showProgress(false);
            success = success.trim();

            if (!success.equals("ERROR")) //online login
            {
                String[] pieces = success.split(":");
                Toast.makeText(LoginActivity.this,"Online kirjautuminen onnistui..Tervetuloa "+pieces[0],Toast.LENGTH_LONG).show();
                intent = new Intent(LoginActivity.this, JobActivity.class);
                String method = "online";
                intent.putExtra("method", method);
                intent.putExtra("nimi", pieces[0]);
                intent.putExtra("tunnus", mEmail);
                intent.putExtra("salasana", mPassword);
                intent.putExtra("info", pieces[1]);
                startActivity(intent);

            }
            else //offline login
            {
                String filu = "users.xml";
                User user = new User();
                file = new File(childCtx.getFilesDir(), filu);
                user.setCtx(childCtx);
                user.setPassword(mPassword);
                user.setAccountName(mEmail);
                boolean authCheck = user.offlineLogin();
                if (authCheck) {
                    String offlineWelcome = "Offline kirjautuminen onnistui. Tervetuloa "+user.getNimi().toString();
                    Toast.makeText(LoginActivity.this,offlineWelcome,Toast.LENGTH_LONG).show();
                    String method = "Offline";
                    intent = new Intent(LoginActivity.this, JobActivity.class);
                    intent.putExtra("method", method);
                    intent.putExtra("nimi",user.getNimi());
                    intent.putExtra("tunnus",user.getAccountName());
                    intent.putExtra("salasana",user.getPassword());
                    intent.putExtra("info",user.getInfo());
                    startActivity(intent);
                }
                else {
                    mEmailView.setError(getString(R.string.error_invalid_email));
                    mEmailView.requestFocus();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

    }
}

