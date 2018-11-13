package com.morganabard.mintstv.codereader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity implements View.OnClickListener{

    private Button REG_btn;
    private EditText emailTxt;
    private EditText passwordTxt;
    private TextView loginTxt;

    private ProgressDialog progress;
    private FirebaseAuth firebaseAuth;

    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar aBar = getSupportActionBar();

        aBar.setDisplayHomeAsUpEnabled(true);
        aBar.setTitle("Register Account");

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        firebaseAuth = firebaseAuth.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(
        new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawer.closeDrawers();

                if(menuItem.toString().equals("Login"))
                {
                    sendLogin();
                }else if(menuItem.toString().equals("QR Code Reader")){
                    sendQRReader();
                }else if(menuItem.toString().equals("Saved Codes")){
                    sendSaved();
                }else if(menuItem.toString().equals("Barcode Reader")){
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

        firebaseAuth = firebaseAuth.getInstance();
        View headerView = navView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.userEmail);
        if(firebaseAuth.getCurrentUser() != null) {
            navUsername.setText(firebaseAuth.getCurrentUser().getEmail());
        }else{
            navUsername.setText("Not signed in");
        }

        Menu menu = navView.getMenu();
        MenuItem logIn = menu.findItem(R.id.nav_login);
        if(firebaseAuth.getCurrentUser() != null) {
            logIn.setTitle("Log Out");
        }else{
            logIn.setTitle("Login");
        }

        progress = new ProgressDialog(this);
        REG_btn = (Button) findViewById(R.id.REG_btn);
        emailTxt = (EditText) findViewById(R.id.editTextEmail);
        passwordTxt = (EditText) findViewById(R.id.editTextPassword);
        loginTxt = (TextView) findViewById(R.id.loginTxt);

        REG_btn.setOnClickListener(this);
        loginTxt.setOnClickListener(this);
    }

    private void registerUser()
    {
        String Email = emailTxt.getText().toString().trim();
        String Password = passwordTxt.getText().toString().trim();

        if(TextUtils.isEmpty(Email))
        {
            //email is empty
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(Password))
        {
            //Password is empty
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        progress.setMessage("Registering User, Please wait one moment");
        progress.show();

        firebaseAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    //User is created and loged in
                    Toast.makeText(Register.this, "Registered Successfully, Please log in.", Toast.LENGTH_LONG).show();
                    progress.dismiss();
                    startActivity(new Intent(Register.this, Login.class));
                }else
                {
                    Toast.makeText(Register.this, "Could not register. Please try again.", Toast.LENGTH_LONG).show();
                    progress.dismiss();

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == REG_btn)
        {
            registerUser();
        }else if(v == loginTxt)
        {
            startActivity(new Intent(Register.this, Login.class));
        }

    }

    public void sendLogin()
    {
        Intent menuIntent = new Intent(this, Login.class);
        startActivity(menuIntent);
    }

    public void sendQRReader()
    {
        Intent menuIntent = new Intent(this, QRCodeReader.class);
        startActivity(menuIntent);
    }

    public void sendBarcodeReader()
    {
        Intent menuIntent = new Intent(this, BarcodeReader.class);
        startActivity(menuIntent);
    }

    public void sendSaved()
    {
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
