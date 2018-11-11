package com.morganabard.mintstv.codereader;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
    private List<String> QRArrayList = new ArrayList<>();;
    private TextView textView;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_saved_list);

        listView = (ListView) findViewById(R.id.qr_list);
        QRArrayList.clear();

        firebaseAuth = firebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        QRArrayList.add("First");

        databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String value1 = dataSnapshot.child(user.getUid()).child("codeData").getValue(String.class);
                    String value2 = dataSnapshot.child(user.getUid()).child("codeType").getValue(String.class);
                    QRArrayList.add(value1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        //databaseReference.child(user.getUid()).;


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1, QRArrayList );

        listView.setAdapter(arrayAdapter);




    }
}
