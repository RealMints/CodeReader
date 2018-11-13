package com.morganabard.mintstv.codereader;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.integration.android.IntentIntegrator;

public class MainMenu extends AppCompatActivity {
    private Button QR_btn;
    private Button BAR_btn;
    private Button QRList_btn;
    private Button SIGNIN_btn;
    private FirebaseAuth firebaseAuth;
    private TextView currentUserText;
    private Button signout_txt;

    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();



        /*QR_btn = (Button) findViewById(R.id.QR_btn);
        SIGNIN_btn = (Button) findViewById(R.id.SIGNIN_btn);
        BAR_btn = (Button) findViewById(R.id.BAR_btn);
        QRList_btn = (Button) findViewById(R.id.QR_List_btn);
        signout_txt = (Button) findViewById(R.id.signout_txt);

        firebaseAuth = firebaseAuth.getInstance();

        currentUserText = (TextView) findViewById(R.id.currentUserText);
        String currentUser;

        updateUserTxt();



        final Activity activity = this;
        QR_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MainMenu.this, QRCodeReader.class));
            }
        });
        BAR_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MainMenu.this, BarcodeReader.class));
            }
        });
        SIGNIN_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MainMenu.this, Login.class));
            }
        });

        QRList_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MainMenu.this, QRCodeSavedList.class));
            }
        });

        signout_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                firebaseAuth.signOut();
                Toast.makeText(MainMenu.this, "You have logged out.", Toast.LENGTH_SHORT).show();
                updateUserTxt();
            }
        });*/

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

    /*private void updateUserTxt()
    {
        firebaseAuth = firebaseAuth.getInstance();
        String currentUser;
        currentUserText = (TextView) findViewById(R.id.currentUserText);
        if(firebaseAuth.getCurrentUser() != null)
        {
            currentUser = firebaseAuth.getCurrentUser().getEmail();
        }else
        {
            currentUser = "Empty";
        }
        if (currentUser == "Empty")
        {
            currentUserText.setText("No User Signed in.");
            SIGNIN_btn.setText("Login");
        }else
        {
            currentUserText.setText(currentUser);
            SIGNIN_btn.setText("Switch Account");
        }
        return;
    }*/


}
