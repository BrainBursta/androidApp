package com.example.joonas.harjoitustyo;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

public class JobActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Intent intent;
    private Menu menu;
    ListView listView;
    JobAdapter adapter;
    FloatingActionButton fab;
    List<Job> jobs = null;
    ArrayList jobs_Completed = new ArrayList<Job>();
    String snackbarString, statusString, snackbarReply, suoritteet;
    MenuItem menuItem;
    TextView drawer_nimi, drawer_info;
    String nimi, tunnus, salasana, info, method, vanhaPW;
    NavigationView navigationView;
    View header;
    boolean workSaved = true;
    int selecetedItem;
    boolean bool_valmis,cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        File file = new File(this.getFilesDir(), "Jobs.xml");
        //file.delete(); //Ota tämä käyttöön resetoidaksesi kaikki työt
        if (!file.exists()) {
            try {
                InputStream source = getAssets().open("Jobs.xml");
                copyInputStreamToFile(source, file); //kopioidaan xml muokkausta varten
                source.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        header = LayoutInflater.from(this).inflate(R.layout.nav_header_job, null);
        navigationView.addHeaderView(header);  //Luo drawer headerin

        drawer_nimi = (TextView)header.findViewById(R.id.DrawerTxt_user);
        drawer_info = (TextView)header.findViewById(R.id.DrawerTxt_userEmail);

        nimi = intent.getStringExtra("nimi"); //kirjautuneen käyttäjän tiedot
        tunnus = intent.getStringExtra("tunnus");
        salasana = intent.getStringExtra("salasana");
        info = intent.getStringExtra("info");
        method = intent.getStringExtra("method"); //kertoo onko kyseessä online vai offline
        MenuItem onOffline = menu.findItem(R.id.nav_offline_toggle);
        onOffline.setTitle(method);

        drawer_nimi.setText(nimi);
        drawer_info.setText(info); //Käyttäjän tiedot headerissa
        navigationView.setNavigationItemSelectedListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        listView = (ListView)findViewById(R.id.listview_job);
        listView.setDivider(null); //listviewin ruma jakaja pois

        try {
            XMLPullJobs parser = new XMLPullJobs();
            parser.setUser_id(tunnus);
            parser.setTargetStatus("Uusi");
            //jobs = parser.parse(getAssets().open("Jobs.xml")); //luodaan xml-tiedostosta listaobjekti
            FileInputStream fileInputStream = new FileInputStream(file);
            jobs = parser.parse(fileInputStream);
            adapter = new JobAdapter(getApplicationContext(),R.layout.job_row_layout, jobs); //asetetaan custom adapter lisviewin rivien päälle
            listView.setAdapter(adapter);

            }catch(IOException e) {
                e.printStackTrace();
            }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { //hakee valitun rivinumeron listalta
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) { //kuuntelee klikattua listan riviä
                selecetedItem = pos;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() { // Tekee muutokset listaan
            @Override
            public void onClick(View view) {

                if (jobs.get(selecetedItem).getStatus().equals("Uusi")) //asetetaan snackbar stringit statuksen mukaan
                {
                    statusString = "Aloi-\ntettu";
                    snackbarString = "Haluatko aloittaa työn?";
                    snackbarReply = "Työ aloitettu!";
                } else if (jobs.get(selecetedItem).getStatus().equals("Aloi-\ntettu")) {
                    statusString = "Valmis";
                    snackbarString = "Onko työ valmis?";
                    snackbarReply = "Valmis työ poistettu listalta!";
                }

                Snackbar snackbar = Snackbar
                        .make(view, snackbarString, Snackbar.LENGTH_LONG)
                        .setAction("Kyllä", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (jobs.get(selecetedItem).getStatus().equals("Uusi")) {
                                    jobs.get(selecetedItem).setStatus(statusString);
                                    jobs.get(selecetedItem).setUser_id(tunnus);
                                    jobs_Completed.add(jobs.get(selecetedItem));
                                    adapter.notifyDataSetChanged();
                                    workSaved = false;
                                } else if (jobs.get(selecetedItem).getStatus().equals("Aloi-\ntettu")) //Valmiit työt siiretään valmiiden töiden listalle
                                {
                                    completeAjobDialog();
                                    workSaved = false;
                                }

                                Snackbar snackbar = Snackbar.make(view, snackbarReply, Snackbar.LENGTH_SHORT);
                                snackbar.show();
                            }
                        });

                snackbar.show();
            }
        });


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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.job, menu);
        this.menu = menu;
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
    public boolean onNavigationItemSelected(MenuItem item) {  //menu
        int id = item.getItemId();
        menuItem = item;
        if (id == R.id.nav_logout) {
            if (workSaved) {finish();}
            else {saveJobDialog();}
        } else if (id == R.id.nav_resetPW) {
                displayAlertDialog();
        } else if (id == R.id.nav_completedJobs) {
            intent = new Intent(JobActivity.this, JobCompleteActivity.class);
            intent.putExtra("salasana",salasana);
            intent.putExtra("accountName", tunnus);
            intent.putExtra("method",method);
            startActivity(intent);

        } else if (id == R.id.nav_offline_toggle) {

            if(!item.getTitle().equals("online"))
            {
                final String methodi = "login";
                RegisterBackground rb = new RegisterBackground(JobActivity.this, new RegisterBackground.AsyncResponse()
                {
                    @Override
                    public void processFinish(boolean result) {
                        if (result == true)
                        {
                            menuItem.setTitle("online");
                            Toast.makeText(getApplicationContext(),"Tila: Online",Toast.LENGTH_SHORT).show();
                            //offlineCheck = true;
                            method = "online";


                            if (!vanhaPW.isEmpty()) {

                                OnlineChangePassword ocp = new OnlineChangePassword(new OnlineChangePassword.AsyncResponse() {
                                    @Override
                                    public void processFinish(boolean result) {
                                        if (result) {
                                            Toast.makeText(getBaseContext(), "Uusi salasana päivitetty kantaan: " + salasana, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getBaseContext(), "Virhe vaihtaessa salasanaa", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                ocp.execute(tunnus, vanhaPW, salasana);




                            }

                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Yhteysvirhe. Tarkista serverin hostname. Tila: Offline",Toast.LENGTH_SHORT).show();
                            //offlineCheck = false;
                            menuItem.setTitle("Offline");
                            method = "Offline";
                        }
                    }
                });
                rb.setTunnus(tunnus);
                rb.setSalasana(salasana);
                rb.setOldPW(vanhaPW);
                rb.setNimi(nimi);
                rb.setInfo(info);
                rb.execute(methodi, tunnus, salasana);
            }
            else {
                item.setTitle("Offline");
                Toast.makeText(getApplicationContext(),"Tila: Offline",Toast.LENGTH_SHORT).show();
                method = "Offline";
            }


        } else if (id == R.id.nav_save) {
            XMLCompletedJobs xmlCompletedJobs = new XMLCompletedJobs();
            xmlCompletedJobs.setCtx(this);
            xmlCompletedJobs.setList(jobs_Completed);
            xmlCompletedJobs.setXML();
            workSaved = true;
            Toast.makeText(getBaseContext(), "Työt tallennettu", Toast.LENGTH_SHORT).show();

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
                        user.setAccountName(tunnus);
                        user.setPassword(salasana);
                        user.setCtx(JobActivity.this);
                        user.setNewPW(pw1);
                        user.setOldPW(salasana);
                        vanhaPW = salasana;
                        try {
                            user.OfflineChangePassword();
                            Toast.makeText(getBaseContext(), "Offline tila: Salasana tallennettu puhelimen muistiin!\nUusi salasana: " + pw1, Toast.LENGTH_SHORT).show();
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
                        ocp.execute(tunnus, salasana, newPW1.getText().toString());
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

    public void completeAjobDialog() // valmiin työn dialogi
    {
        bool_valmis = false;
        suoritteet = "";
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.valmisjob_dialog, null);
        final EditText selite_txt = (EditText) alertLayout.findViewById(R.id.txt_selite);
        final NumberPicker tunnitNP = (NumberPicker)alertLayout.findViewById(R.id.npTunnit);
        final CheckBox autoCB = (CheckBox)alertLayout.findViewById(R.id.cbAuto);
        final CheckBox lapioCB = (CheckBox)alertLayout.findViewById(R.id.cbLapio);
        final CheckBox puhelinCB = (CheckBox)alertLayout.findViewById(R.id.cbPuhelin);
        final CheckBox lumikolaCB = (CheckBox)alertLayout.findViewById(R.id.cbLumikola);
        final CheckBox vasaraCB = (CheckBox)alertLayout.findViewById(R.id.cbVasara);
        tunnitNP.setMaxValue(100); //numberpicker asetukset
        tunnitNP.setMinValue(0);
        tunnitNP.setWrapSelectorWheel(false);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Valmiin työn tiedot");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("Peruuta", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getBaseContext(), "Toiminto peruttu!", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        alert.setPositiveButton("Tallenna", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (autoCB.isChecked()) {
                            suoritteet += autoCB.getText().toString() + ",";
                        }
                        if (lapioCB.isChecked()) {
                            suoritteet += lapioCB.getText().toString() + ",";
                        }
                        if (puhelinCB.isChecked()) {
                            suoritteet += puhelinCB.getText().toString() + ",";
                        }
                        if (lumikolaCB.isChecked()) {
                            suoritteet += lumikolaCB.getText().toString() + ",";
                        }
                        if (vasaraCB.isChecked()) {
                            suoritteet += vasaraCB.getText().toString();
                        }
                        if(!vasaraCB.isChecked()) {
                            suoritteet = suoritteet.substring(0,suoritteet.length()-1); //poistaa pilkun perästä
                        }
                        Integer tunti = tunnitNP.getValue(); //valmis työ siirretään toiseen listaan
                        String tunnit = tunti.toString();
                        jobs.get(selecetedItem).setStatus("Valmis");
                        jobs.get(selecetedItem).setTunnit(tunnit);
                        jobs.get(selecetedItem).setSelite(selite_txt.getText().toString());
                        jobs.get(selecetedItem).setSuoritteet(suoritteet);
                        jobs.get(selecetedItem).setUser_id(tunnus);
                        jobs_Completed.add(jobs.get(selecetedItem));
                        jobs.remove(selecetedItem);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(getBaseContext(), "Valmis työ poistettu listalta", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        AlertDialog dialog = alert.create();
        dialog.show();

    }
    public void saveJobDialog()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Tallenna muutokset");
        alert.setMessage("Haluatko tallentaa?");
        alert.setCancelable(false);

        alert.setNegativeButton("Älä tallenna",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getBaseContext(), "Töitä ei tallennettu", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

        alert.setPositiveButton("Tallenna",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        XMLCompletedJobs xmlCompletedJobs = new XMLCompletedJobs();
                        xmlCompletedJobs.setCtx(JobActivity.this);
                        xmlCompletedJobs.setList(jobs_Completed);
                        xmlCompletedJobs.setXML();
                        workSaved = true;
                        Toast.makeText(getBaseContext(), "Työt tallennettu", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                });
        alert.show();
    }
    private void copyInputStreamToFile( InputStream in, File file ) { //kopioi asset/jobs.xml puhelimen sisäiseen muistiin muokkausta varten
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    }





