package com.example.mynewproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.InputType;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity {

    private static final String TAG = "myNewTag";
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

        checkUserLog();

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

    //?????????? ???????????????????? ?????? ???????????????? ??????????????????
    private void checkUserLog(){
        if(fAuth.getCurrentUser() != null){
            userID = fAuth.getCurrentUser().getUid();
            DocumentReference ref = fStore.collection("users").document(userID);
            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();
                        if (snapshot.exists()) {
                            String Name = snapshot.get("Full Name").toString();
                            String Email = snapshot.get("Email").toString();
                            String Type = snapshot.get("Type").toString();
                            String UserGroup = snapshot.get("UserGroup").toString();
                            User.getUser().create(fAuth.getCurrentUser().getUid(), Name, Email, Type);
                            User.getUser().setUserGroup(UserGroup);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                        startActivity(new Intent(getApplicationContext(), Profile.class));
                        finish();
                    }else{
                        Log.d(TAG, "Error" + task.getException().getMessage());
                    }
                }
            });
        }
    }


    private void registration(){
        final EditText inputName = findViewById(R.id.inputNameReg);
        final EditText inputEmail = findViewById(R.id.inputEmailReg);
        final EditText inputPassword = findViewById(R.id.inputPasswordReg);
        if (TextUtils.isEmpty(inputName.getText().toString())){
            inputName.setError("?????????????? ???????? ??????");
            return;
        }
        if (TextUtils.isEmpty(inputEmail.getText().toString())){
            inputEmail.setError("?????????????? ???????? ??????????");
            return;
        }
        if (inputPassword.getText().toString().length() < 6){
            inputPassword.setError("???????????? ???????????? ???????? ???????????? 6 ????????????????");
            return;
        }
        RadioButton accRBtn;
        accRBtn = findViewById(rGroup.getCheckedRadioButtonId());
        String Type = accRBtn.getText().toString();
        String Email = inputEmail.getText().toString();
        String Name = inputName.getText().toString();
        createUser(Email, inputPassword.getText().toString(), Type, Name);

    }



    private void createUser(String Email, String Password, String Type, String Name){
        fAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    userID = fAuth.getCurrentUser().getUid();
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("Type", Type);
                    userData.put("Email", Email);
                    userData.put("Full Name", Name);
                    userData.put("UserGroup", "");
                    DocumentReference documentReference = fStore.collection("users").document(userID);
                    documentReference.set(userData)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("Registration", "User created" + userID);
                        }
                    })
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {  //?????????? ???????????????????? ???????????????????? ???????? ???????????????? ?? ???? ???????????? ???? ?????????? ???????????????????? + ?????????????????? ??????
                                    User.getUser().create(fAuth.getCurrentUser().getUid(), Name, Email, Type);
                                    if(Type.equals("??????????????")){
                                        createAdmin();
                                    }else{
                                    Snackbar.make(mainElemReg, "?????????????????????? ????????????????????", Snackbar.LENGTH_SHORT).show();
                                    changeAcrivity();
                                    }
                                }
                            });
                }else{
                    Snackbar.make(mainElemReg, "???????????? " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }


    private void createAdmin(){ //?????????????????? ???????????????????????????? ?????????? ???????????????? ?????????? ?????? ????????????
        final String[] nameOfGroup = new String[1];
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("?????????????? ???????????????? ????????????");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("????????????????????????????????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nameOfGroup[0] = input.getText().toString();
                createGroup(User.getUser().getUID(), nameOfGroup[0]); //???????????????? ?????????? ???????? ???? ???? ?????? ???????????? ?? ???????????????????? ????????????
            }
        });
        builder.create().show();
    }



    private void changeAcrivity(){ //???????????? ?????? ???????? ?????????? ???????????????????? ?????????????????? ???????? ?????????????????????? ????????????
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }



    private void createGroup(String UID, String nameOfGroup) {//???????????????? ???????????? + ???????????????? ?????????????????????? ???????????? ?? ???????????? ??????????????????
        if(nameOfGroup.equals("")){
            Snackbar.make(mainElemReg, "?????????????? ???????????????? ????????????", Snackbar.LENGTH_LONG).show();
            fAuth.getCurrentUser().delete();
            fStore.collection("users").document(UID).delete();
            return;
        }
        fStore.collection("groups").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if(document.getId().equals(nameOfGroup)){
                        Snackbar.make(mainElemReg, "???????????? ?? ?????????? ?????????????????? ?????? ????????????????????", Snackbar.LENGTH_LONG).show();
                        fAuth.getCurrentUser().delete();
                        fStore.collection("users").document(UID).delete();
                        return;
                    }
                }
                DocumentReference documentReference = fStore.collection("groups").document(nameOfGroup);
                Map<String, Object> newGroup = new HashMap<>();
                newGroup.put("Name Of Group", nameOfGroup);
                documentReference.set(newGroup).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Snackbar.make(mainElemReg, "???????????? ?????????????? ????????????????", Snackbar.LENGTH_SHORT).show();
                            addUserToGroup(nameOfGroup);
                        }else{
                            Snackbar.make(mainElemReg, "????????????" + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void addUserToGroup(String nameOfGroup){
        DocumentReference userDoc = fStore.collection("users").document(User.getUser().getUID());
        DocumentReference groupDoc = fStore.collection("groups").document(nameOfGroup).collection("groupUsers").document(User.getUser().getUID());
        userDoc.update("UserGroup", nameOfGroup).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Map<String, Object> userDataToGroup = new HashMap<>();
                    userDataToGroup.put("UID", User.getUser().getUID());
                    userDataToGroup.put("UserType", User.getUser().getType());
                    groupDoc.set(userDataToGroup).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                User.getUser().setUserGroup(nameOfGroup);
                                changeAcrivity();
                            }else{
                                userDoc.update("UserGroup", "");
                                Snackbar.make(mainElemReg, "????????????" + task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Snackbar.make(mainElemReg, "????????????" + task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
}