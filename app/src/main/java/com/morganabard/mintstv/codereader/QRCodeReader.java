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

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class QRCodeReader extends AppCompatActivity {
    private Button scan_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_reader);
        scan_btn = (Button) findViewById(R.id.scan_btn);
        final Activity activity = this;
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt(" ");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
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
        if(result.getContents().toString().contains("http") && result.getContents().toString().contains("MATMSG:") == false)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getContents().toString()));
            startActivity(intent);
        }
        //Opening email app
        if(result.getContents().toString().contains("MATMSG:"))
        {
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
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] {TO});
            intent.putExtra(Intent.EXTRA_SUBJECT, tokenSub[1]);
            intent.putExtra(Intent.EXTRA_TEXT, tokenBody[1]);
            startActivity(Intent.createChooser(intent, TO));
        }
        //Opening text app.
        if(result.getContents().toString().contains("SMSTO:"))
        {
            //3 Tokens will be made, tokens[0], tokens[1], tokens[2]
            String delims1 = ":";
            String[] tokens = result.toString().split(delims1);

            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", tokens[3]);
            smsIntent.putExtra("sms_body",tokens[4]);
            startActivity(smsIntent);
        }
        TextView otext = (TextView)findViewById(R.id.output_text);
        otext.setText(result.getContents().toString());
        super.onActivityResult(requestCode, resultCode, data);
    }
}
