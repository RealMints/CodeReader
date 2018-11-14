package com.morganabard.mintstv.codereader;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BarcodeReader extends AppCompatActivity {
    private Button scan_btn;
    private Button save_btn;

    private String resultText;
    private String codeType;

    private TextView otext;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootRef;

    DrawerLayout drawer;

    private ArrayAdapter<String> arrayAdapter;
    private ListView listView;
    private List<String> QRArrayList = new ArrayList<>();
    private ClipboardManager clipboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_reader);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar aBar = getSupportActionBar();

        aBar.setDisplayHomeAsUpEnabled(true);
        aBar.setTitle("Barcode Reader");

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        firebaseAuth = firebaseAuth.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        drawer.closeDrawers();

                        if (menuItem.toString().equals("Login")) {
                            sendLogin();
                        } else if (menuItem.toString().equals("QR Code Reader")) {
                            sendQRReader();
                        } else if (menuItem.toString().equals("Saved Codes")) {
                            sendSaved();
                        } else if (menuItem.toString().equals("Barcode Reader")) {
                            sendBarcodeReader();
                        } else if (menuItem.toString().equals("Log Out")) {
                            firebaseAuth.signOut();
                            finish();
                            startActivity(getIntent());
                        }

                        return true;
                    }
                });

        firebaseAuth = firebaseAuth.getInstance();
        View headerView = navView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.userEmail);
        if (firebaseAuth.getCurrentUser() != null) {
            navUsername.setText(firebaseAuth.getCurrentUser().getEmail());
        } else {
            navUsername.setText("Not signed in");
        }

        Menu menu = navView.getMenu();
        MenuItem logIn = menu.findItem(R.id.nav_login);
        if (firebaseAuth.getCurrentUser() != null) {
            logIn.setTitle("Log Out");
        } else {
            logIn.setTitle("Login");
        }

        listView = (ListView) findViewById(R.id.recent_QR);

        resultText = null;
        codeType = null;

        scan_btn = (Button) findViewById(R.id.scan_btn);
        save_btn = (Button) findViewById(R.id.save_btn);


        databaseReference = FirebaseDatabase.getInstance().getReference();
        rootRef = FirebaseDatabase.getInstance().getReference();


        final Activity activity = this;
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
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
                    Toast.makeText(BarcodeReader.this, "This code has NOT been saved. Please login to save codes.", Toast.LENGTH_LONG).show();
                } else if (resultText == null || codeType == null) {
                    Toast.makeText(BarcodeReader.this, "No Code has been scanned. Please scan a barcode.", Toast.LENGTH_LONG).show();
                } else {
                    //String qrType = codeType;
                    //String qrData = otext.getText().toString();

                    //savedQRCodes savedCodes = new savedQRCodes(qrType, qrData);

                    //databaseReference.child(user.getUid()).setValue(savedCodes);

                    String qrType = codeType;
                    String qrData = otext.getText().toString();

                    DatabaseReference user_message_key = rootRef.child(user.getUid()).push();
                    String idd = user_message_key.getKey();
                    Map message = new HashMap();
                    message.put("codeData", qrData);
                    message.put("codeType", qrType);
                    databaseReference.child(user.getUid()).child(idd).setValue(message);


                    Toast.makeText(BarcodeReader.this, "This code has been saved!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1, QRArrayList);

        listView.setAdapter(arrayAdapter);

        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                String selectedItem = (String) parent.getItemAtPosition(position);

                // Display the selected item text on TextView
                ClipData clip = ClipData.newPlainText("Copied Text", selectedItem);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(BarcodeReader.this, "Copied " + selectedItem + " to clipboard.", Toast.LENGTH_LONG).show();
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

        //Set value to output text
        otext = (TextView) findViewById(R.id.output_text);
        otext.setText(result.getContents().toString());
        resultText = otext.getText().toString();
        codeType = "Barcode";

        otext.setFocusableInTouchMode(true);
        if (QRArrayList.size() >= 6) {
            QRArrayList.remove(5);
        }
        QRArrayList.add(result.getContents().toString());
        listView.setAdapter(arrayAdapter);

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
        if (firebaseAuth.getCurrentUser() != null) {
            Intent menuIntent = new Intent(this, QRCodeSavedList.class);
            startActivity(menuIntent);
        } else {
            Toast.makeText(this, "Please Log in first.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}
