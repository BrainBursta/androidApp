package com.example.joonas.harjoitustyo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class JobCompleteActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ListView listView;
    JobCompleteAdapter adapter;
    private String accountName,salasana,method;
    private boolean cancel;
    MenuItem menuItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_complete);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        accountName = intent.getStringExtra("accountName");
        salasana = intent.getStringExtra("salasana");
        method = intent.getStringExtra("method");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        listView =(ListView)findViewById(R.id.listview_job_complete);
        listView.setDivider(null);
        List<Job> jobs = null;
        try {
            XMLPullJobs parser = new XMLPullJobs();
            File file = new File(this.getFilesDir(), "Jobs.xml");
            FileInputStream fileInputStream = new FileInputStream(file);
            parser.setUser_id(accountName);
            parser.setTargetStatus("Valmis");
            jobs = parser.parse(fileInputStream);

            adapter = new JobCompleteAdapter(getApplicationContext(),R.layout.job_complete_row_layout, jobs);

            listView.setAdapter(adapter);

        }catch(IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.job_complete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        menuItem = item;
        if (id == R.id.nav_logout) {
             finish();
        } else if (id == R.id.nav_resetPW) {
            displayAlertDialog();
        } else if (id == R.id.nav_completedJobs) {
            Toast.makeText(getApplicationContext(), "Valmiit työt ovat jo näkyvissä. Valitse takaisin nuoli palataksesi vapaisiin töihin", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_offline_toggle) {

            if(item.getTitle() != "Online")
            {
                String method = "login";
                RegisterBackground rb = new RegisterBackground(JobCompleteActivity.this, new RegisterBackground.AsyncResponse()
                {
                    @Override
                    public void processFinish(boolean result) {
                        if (result == true)
                        {
                            menuItem.setTitle("Online");
                            Toast.makeText(getApplicationContext(), "Tila: Online", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Yhteysvirhe. Tarkista serverin hostname. Tila: Offline",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                rb.setTunnus(accountName);
                rb.setSalasana(salasana);
                rb.execute(method, accountName, salasana);
            }
            else {
                item.setTitle("Offline");
                Toast.makeText(getApplicationContext(),"Tila: Offline",Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_save) {
            Toast.makeText(getApplicationContext(),"Ei tallennettavia töitä..",Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void displayAlertDialog() { //Salasanan resetointi dialogi
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.password_dialog, null);
        final EditText oldPW = (EditText) alertLayout.findViewById(R.id.txt_oldPW);
        final EditText newPW1 = (EditText) alertLayout.findViewById(R.id.txt_newPW1);
        final EditText newPW2 = (EditText) alertLayout.findViewById(R.id.txt_newPW2);

        newPW1.setError(null);
        newPW2.setError(null);
        oldPW.setError(null);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Resetoi salasana");
        alert.setView(alertLayout);
        alert.setCancelable(false);

        alert.setNegativeButton("Peruuta",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Dialogi ei sulkeudu automaattisesti
                    }
                });

        alert.setPositiveButton("Vaihda",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Dialogi ei sulkeudu automaattisesti
                    }
                });

        final AlertDialog dialog = alert.create(); //Nappien toiminnot alkaa tästä
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Toiminto peruttu..", Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        });

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pw1 = newPW1.getText().toString();
                String pw2 = newPW2.getText().toString();
                String pwOld = oldPW.getText().toString();
                cancel = false;

                if (!isPasswordValid(pwOld)) {
                    oldPW.setError(getString(R.string.error_incorrect_password)); //tarkistetaan syötteet
                    cancel = true;
                }
                if (!(isPasswordValidLength(pwOld))) {
                    oldPW.setError(getString(R.string.error_short_password));
                    cancel = true;
                }
                if (!isPasswordValidLength(pw1)) {
                    newPW1.setError(getString(R.string.error_short_password));
                    cancel = true;
                }
                if (!areNewPWSsame(pw1, pw2)) {
                    newPW1.setError(getString(R.string.error_passwords_dont_match));
                    cancel = true;
                }
                if (pwOld.isEmpty()) {
                    oldPW.setError(getString(R.string.error_field_required));
                    cancel = true;
                }
                if (pw1.isEmpty()) {
                    newPW1.setError(getString(R.string.error_field_required));
                    cancel = true;
                }
                if (!cancel) {


                    if (method.equals("Offline"))
                    {
                        User user = new User();
                        user.setAccountName(accountName);
                        user.setPassword(salasana);
                        user.setCtx(JobCompleteActivity.this);
                        user.setNewPW(pw1);
                        try {
                            user.OfflineChangePassword();
                            Toast.makeText(getBaseContext(), "Salasana vaihdettu!\nUusi salasana: " + pw1, Toast.LENGTH_SHORT).show();
                            salasana = pw1;
                            dialog.dismiss();
                        } catch (ParserConfigurationException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (SAXException e) {
                            e.printStackTrace();
                        } catch (TransformerException e) {
                            e.printStackTrace();
                        }
                    }
                    else //online
                    {
                        OnlineChangePassword ocp = new OnlineChangePassword(new OnlineChangePassword.AsyncResponse() {
                            @Override
                            public void processFinish(boolean result) {
                                if (result) {
                                    String pw1 = newPW1.getText().toString();
                                    salasana = pw1;
                                    Toast.makeText(getBaseContext(), "Salasana vaihdettu!\nUusi salasana: " + pw1, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getBaseContext(), "Virhe vaihtaessa salasanaa", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        ocp.execute(accountName, salasana, newPW1.getText().toString());
                    }
                } else {
                    Toast.makeText(getBaseContext(), "Virheelliset tiedot..Salasanaa ei vaihdettu", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean isPasswordValidLength(String password) { //salasanan tarkistus
        return password.length() > 3;
    }
    private boolean isPasswordValid(String password) {
        return salasana.equals(password);
    }
    private boolean areNewPWSsame(String pw1,String pw2) {
        return pw1.equals(pw2);
    }
}
