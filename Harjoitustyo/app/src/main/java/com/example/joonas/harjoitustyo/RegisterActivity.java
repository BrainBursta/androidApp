package com.example.joonas.harjoitustyo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.transform.dom.DOMSource;

public class RegisterActivity extends AppCompatActivity {
    EditText nimitxt, tunnustxt, salasanatxt, infotxt;
    String nimi, tunnus, salasana, info;
    Context ctx;

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ctx = this;
        nimitxt = (EditText) findViewById(R.id.txtNimi);
        tunnustxt = (EditText) findViewById(R.id.txtID);
        salasanatxt = (EditText) findViewById(R.id.txtPassword);
        infotxt = (EditText) findViewById(R.id.txtInfo);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar
                        .make(view, "Haluatko tallentaa?", Snackbar.LENGTH_LONG)
                        .setAction("Kyllä", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                nimi = nimitxt.getText().toString();
                                tunnus = tunnustxt.getText().toString();
                                salasana = salasanatxt.getText().toString();
                                info = infotxt.getText().toString();
                                String method = "register";
                                RegisterBackground rb = new RegisterBackground(ctx, new RegisterBackground.AsyncResponse() {
                                    @Override
                                    public void processFinish(boolean result) { //palauttaa false, jos register.php ei saa kantaa kiinni
                                        if (result == false) //OFFLINE TILA
                                        {
                                            Toast.makeText(ctx, "OFFLINE-tila käytössä", Toast.LENGTH_SHORT).show();
                                            User newUser = new User();
                                            newUser.setNimi(nimi);
                                            newUser.setAccountName(tunnus);
                                            newUser.setPassword(salasana);
                                            newUser.setInfo(info);
                                            newUser.setCtx(RegisterActivity.this);
                                            newUser.setXML();
                                            Toast.makeText(ctx, "Tunnus luotu XML-tiedostoon", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                                rb.execute(method, nimi, tunnus, salasana, info);
                                    finish();
                            }
                        });

                snackbar.show();
            }
        });

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


}

