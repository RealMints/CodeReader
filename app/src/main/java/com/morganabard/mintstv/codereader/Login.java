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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private TextView reg_txt;
    private Button LOGIN_btn;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private ProgressDialog progress;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        reg_txt = (TextView) findViewById(R.id.reg_txt);
        progress = new ProgressDialog(this);
        firebaseAuth = firebaseAuth.getInstance();
        LOGIN_btn = (Button) findViewById(R.id.LOGIN_btn);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);


        reg_txt.setOnClickListener(this);
        LOGIN_btn.setOnClickListener(this);
    }

    private void loginUser()
    {
        String Email = editTextEmail.getText().toString().trim();
        String Password = editTextPassword.getText().toString().trim();

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

        progress.setMessage("Logging in User, Please wait one moment");
        progress.show();

        firebaseAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_LONG).show();
                    progress.dismiss();
                    startActivity(new Intent(Login.this, MainMenu.class));
                }else{
                    Toast.makeText(Login.this, "Could not log in, please check your Email and password are correct.", Toast.LENGTH_LONG).show();
                    progress.dismiss();
                }
            }

        });

    }


    @Override
    public void onClick(View v) {
        if(v == reg_txt)
        {
            startActivity(new Intent(Login.this, Register.class));
        }

        if(v == LOGIN_btn)
        {
            loginUser();
        }
    }
}
