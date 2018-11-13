package com.morganabard.mintstv.codereader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Map;

public class QRCodeReader extends AppCompatActivity {
    private Button scan_btn;
    private Button save_btn;
    private String resultText;
    private TextView otext;
    private String codeType;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootRef;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_reader);


        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar aBar = getSupportActionBar();

        aBar.setDisplayHomeAsUpEnabled(true);
        aBar.setTitle("QR Reader");

        drawer = findViewById(R.id.drawer_layout);

        //toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        firebaseAuth = firebaseAuth.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        drawer.closeDrawers();

                        if (menuItem.toString().equals("Login")) {
                                sendLogin();
                        } else if (menuItem.toString().equals("Saved Codes")) {
                            sendSaved();
                        } else if (menuItem.toString().equals("Barcode Reader")) {
                            sendBarcodeReader();
                        }else if(menuItem.toString().equals("Log Out"))
                        {
                            firebaseAuth.signOut();
                            finish();
                            startActivity(getIntent());
                        }

                        return true;
                    }
                });


        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.userEmail);
        if(firebaseAuth.getCurrentUser() != null) {
            navUsername.setText(firebaseAuth.getCurrentUser().getEmail());
        }else{
            navUsername.setText("Not signed in");
        }

        Menu menu = navigationView.getMenu();
        MenuItem logIn = menu.findItem(R.id.nav_login);
        if(firebaseAuth.getCurrentUser() != null) {
            logIn.setTitle("Log Out");
        }else{
            logIn.setTitle("Login");
        }

        resultText = null;
        codeType = null;
        scan_btn = (Button) findViewById(R.id.scan_btn);
        save_btn = (Button) findViewById(R.id.save_btn);

        firebaseAuth = firebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        rootRef = FirebaseDatabase.getInstance().getReference();
        final Activity activity = this;
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt(" ");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Toast.makeText(QRCodeReader.this, "This code has NOT been saved. Please login to save codes.", Toast.LENGTH_LONG).show();
                } else if (resultText == null || codeType == null) {
                    Toast.makeText(QRCodeReader.this, "No Code has been scanned. Please scan a QR-Code.", Toast.LENGTH_LONG).show();
                } else {
                    String qrType = codeType;
                    String qrData = otext.getText().toString();

                    DatabaseReference user_message_key = rootRef.child(user.getUid()).push();
                    String idd = user_message_key.getKey();
                    Map message = new HashMap();
                    message.put("codeData", qrData);
                    message.put("codeType", qrType);
                    databaseReference.child(user.getUid()).child(idd).setValue(message);
                    Toast.makeText(QRCodeReader.this, "This code has been saved!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "You Cancelled the Scanning.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        if (result.getContents().toString().contains("http") && result.getContents().toString().contains("MATMSG:") == false) {
            codeType = "Website";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getContents().toString()));
            startActivity(intent);

        }
        //Opening email app
        if (result.getContents().toString().contains("MATMSG:")) {
            codeType = "Email";

            //3 Tokens will be made, tokens[0], tokens[1], tokens[2]
            String delims1 = ";";
            String[] tokens = result.toString().split(delims1);

            //Split 2.
            String delims2 = ":";

            String[] tokenTo = tokens[0].split(delims2);
            String[] tokenSub = tokens[1].split(delims2);
            String[] tokenBody = tokens[2].split(delims2);

            //To string
            String TO = tokenTo[4];

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("plain/text");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{TO});
            intent.putExtra(Intent.EXTRA_SUBJECT, tokenSub[1]);
            intent.putExtra(Intent.EXTRA_TEXT, tokenBody[1]);
            startActivity(Intent.createChooser(intent, TO));
        }
        //Opening text app.
        if (result.getContents().toString().contains("SMSTO:")) {
            codeType = "Text";

            //3 Tokens will be made, tokens[0], tokens[1], tokens[2]
            String delims1 = ":";
            String[] tokens = result.toString().split(delims1);

            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", tokens[3]);
            smsIntent.putExtra("sms_body", tokens[4]);
            startActivity(smsIntent);
        }
        if (result.getContents().toString().contains("SMSTO:") != true && result.getContents().toString().contains("MATMSG:") != true && result.getContents().toString().contains("http") != true) {
            codeType = "Default";
        }
        otext = (TextView) findViewById(R.id.output_text);
        otext.setText(result.getContents().toString());
        resultText = result.getContents().toString();
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void sendLogin() {
            Intent menuIntent = new Intent(this, Login.class);
            startActivity(menuIntent);
    }

    public void sendQRReader() {
        Intent menuIntent = new Intent(this, QRCodeReader.class);
        startActivity(menuIntent);
    }

    public void sendBarcodeReader() {
        Intent menuIntent = new Intent(this, BarcodeReader.class);
        startActivity(menuIntent);
    }

    public void sendSaved() {
        if(firebaseAuth.getCurrentUser() != null) {
            Intent menuIntent = new Intent(this, QRCodeSavedList.class);
            startActivity(menuIntent);
        }else{
            Toast.makeText(this, "Please Log in first.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }



}
