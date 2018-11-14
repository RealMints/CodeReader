package com.morganabard.mintstv.codereader;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Console;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class QRCodeSavedList extends AppCompatActivity {

    private ListView listView;
    private ProgressDialog progress;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private List<String> QRArrayList = new ArrayList<>();
    private List<String> IDArrayList = new ArrayList<>();
    private TextView textView;
    private FirebaseUser user;
    private DatabaseReference rootRef;
    private ArrayAdapter<String> arrayAdapter;
    DrawerLayout drawer;
    private ClipboardManager clipboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_saved_list);


        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar aBar = getSupportActionBar();

        aBar.setDisplayHomeAsUpEnabled(true);
        aBar.setTitle("Saved Codes");

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
                            sendQRReader();
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

        listView = (ListView) findViewById(R.id.qr_list);
        QRArrayList.clear();

        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        rootRef = FirebaseDatabase.getInstance().getReference();

        FetchMessages();


        arrayAdapter = new ArrayAdapter<>(
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
                Toast.makeText(QRCodeSavedList.this, "Copied " + selectedItem + " to clipboard.", Toast.LENGTH_LONG).show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                databaseReference.child(user.getUid()).child(IDArrayList.get(position)).removeValue();
                                QRArrayList.remove(position);
                                IDArrayList.remove(position);
                                Toast.makeText(QRCodeSavedList.this, "Code Deleted.", Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(getIntent());
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked

                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Do you want to delete " + (String) parent.getItemAtPosition(position) + "?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                return false;
            }
        });
    }


    private void FetchMessages() {
        rootRef.child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String x = dataSnapshot.child("codeData").getValue(String.class);
                String id = dataSnapshot.getKey();
                IDArrayList.add(id);
                QRArrayList.add(x);
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void openDeleteCheck() {
        //START ------------------------------------------------------------------------------------

        //end ------------------------------------------------------------------------------------
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
        Intent menuIntent = new Intent(this, QRCodeSavedList.class);
        startActivity(menuIntent);
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
