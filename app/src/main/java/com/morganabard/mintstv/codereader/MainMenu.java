package com.morganabard.mintstv.codereader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.Activity;

import com.google.zxing.integration.android.IntentIntegrator;

public class MainMenu extends AppCompatActivity {
    private Button QR_btn;
    private Button BAR_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        QR_btn = (Button) findViewById(R.id.QR_btn);
        final Activity activity = this;
        QR_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MainMenu.this, QRCodeReader.class));
            }
        });
        BAR_btn = (Button) findViewById(R.id.BAR_btn);
        BAR_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(MainMenu.this, BarcodeReader.class));
            }
        });

    }


}
