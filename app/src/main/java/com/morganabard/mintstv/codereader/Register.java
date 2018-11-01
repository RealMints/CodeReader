package com.morganabard.mintstv.codereader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        progress = new ProgressDialog(this);
        firebaseAuth = firebaseAuth.getInstance();
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
}
