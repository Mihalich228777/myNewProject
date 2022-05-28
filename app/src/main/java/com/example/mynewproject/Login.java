package com.example.mynewproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Login extends AppCompatActivity {

    Button btnLogin;
    ConstraintLayout mainElemLog;
    TextView btnReplaceToReg;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        mainElemLog = findViewById(R.id.mainElemLog);
        btnReplaceToReg = findViewById(R.id.btnReplaceToReg);

        fAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        btnReplaceToReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
                finish();
            }
        });
    }

    private void login(){
        final EditText inputEmail = findViewById(R.id.inputEmailLog);
        final EditText inputPassword = findViewById(R.id.inputPasswordLog);

        if(TextUtils.isEmpty(inputEmail.getText().toString())){
            inputEmail.setError("Ввидите вашу почту");
            return;
        }
        if(inputPassword.getText().toString().length() < 6){
            inputPassword.setError("Ввидите пароль");
            return;
        }
        fAuth.signInWithEmailAndPassword(inputEmail.getText().toString(), inputPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Snackbar.make(mainElemLog, "Успешная попытка входа", Snackbar.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }else{
                    Snackbar.make(mainElemLog, "Ошибка " + task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            }
        });


    }
}