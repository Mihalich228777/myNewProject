package com.example.mynewproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity {

    Button registrBtn;
    ConstraintLayout mainElemReg;
    TextView btnReplaceToLogin;
    RadioGroup rGroup;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registrBtn = findViewById(R.id.btnRegistr);
        mainElemReg = findViewById(R.id.mainElemReg);
        btnReplaceToLogin = findViewById(R.id.btnReplaceToLogin);
        rGroup = findViewById(R.id.selectType);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        registrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registration();
            }
        });

        btnReplaceToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

    }

    private void registration(){

        final EditText inputName = findViewById(R.id.inputNameReg);
        final EditText inputEmail = findViewById(R.id.inputEmailReg);
        final EditText inputPassword = findViewById(R.id.inputPasswordReg);

        if (TextUtils.isEmpty(inputName.getText().toString())){
            inputName.setError("Ввидите ваше имя");
            return;
        }
        if (TextUtils.isEmpty(inputEmail.getText().toString())){
            inputEmail.setError("Ввидите вашу почту");
            return;
        }
        if (inputPassword.getText().toString().length() < 6){
            inputPassword.setError("Пароль должен быть длинее 6 символов");
            return;
        }





        fAuth.createUserWithEmailAndPassword(inputEmail.getText().toString(), inputPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Snackbar.make(mainElemReg, "Регистрация завершенна", Snackbar.LENGTH_SHORT).show();
                    userID = fAuth.getCurrentUser().getUid();
                    Map<String, Object> user = new HashMap<>();

                    //находить занчение выбранной кнопки
                    RadioButton accRBtn;
                    accRBtn = findViewById(rGroup.getCheckedRadioButtonId());
                    user.put("Type", accRBtn.getText());
                    user.put("Email", inputEmail.getText().toString());
                    user.put("Full Name", inputName.getText().toString());
                    DocumentReference documentReference = fStore.collection("users").document(userID);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("Registration", "User created" + userID);
                        }
                    });
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }else{
                    Snackbar.make(mainElemReg, "Ошибка " + task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
}