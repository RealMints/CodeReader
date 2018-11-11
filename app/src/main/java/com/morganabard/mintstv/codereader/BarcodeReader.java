package com.morganabard.mintstv.codereader;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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




public class BarcodeReader extends AppCompatActivity {
    private Button scan_btn;
    private Button save_btn;

    private String resultText;
    private String codeType;

    private TextView otext;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_reader);

        resultText = null;
        codeType = null;

        scan_btn = (Button) findViewById(R.id.scan_btn);
        save_btn = (Button) findViewById(R.id.save_btn);

        firebaseAuth = firebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();


        final Activity activity = this;
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
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
            public void onClick(View view)
            {


                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null )
                {
                    Toast.makeText(BarcodeReader.this, "This code has NOT been saved. Please login to save codes.", Toast.LENGTH_LONG).show();
                }else if(resultText == null || codeType == null)
                {
                    Toast.makeText(BarcodeReader.this, "No Code has been scanned. Please scan a barcode.", Toast.LENGTH_LONG).show();
                }else
                {
                    String qrType = codeType;
                    String qrData = otext.getText().toString();

                    savedQRCodes savedCodes = new savedQRCodes(qrType, qrData);

                    databaseReference.child(user.getUid()).setValue(savedCodes);
                    Toast.makeText(BarcodeReader.this, "This code has been saved!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
        {
            if(result.getContents() == null)
            {
                Toast.makeText(this, "You Cancelled the Scanning.", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }

        //Set value to output text
        otext = (TextView)findViewById(R.id.output_text);
        otext.setText(result.getContents().toString());
        resultText = otext.getText().toString();
        codeType = "Barcode";
        super.onActivityResult(requestCode, resultCode, data);
    }
}
