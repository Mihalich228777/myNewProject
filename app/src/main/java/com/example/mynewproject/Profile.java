package com.example.mynewproject;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile extends AppCompatActivity {

    private static final String TAG = "myNewTag";
    Button logoutBtn, joinGroupBtn;
    ConstraintLayout mainElem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        logoutBtn = findViewById(R.id.logoutBtn);
        joinGroupBtn = findViewById(R.id.joinGroupBtn);
        mainElem = findViewById(R.id.mainElem);

        changeActivity();
        showUserData();


        joinGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] nameOfGroup = new String[1];
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Введите название класса");
                final EditText input = new EditText(v.getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("Зарегестрироваться", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        nameOfGroup[0] = input.getText().toString();
                        addUserToGroup(nameOfGroup[0]);
                        Log.d(TAG, nameOfGroup[0] + " asdsadsa");
                    }
                });
                builder.create().show();
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                User.getUser().create("", "", "", "");
                finish();
            }
        });


    }

    private void showUserData() {
        TextView userName = findViewById(R.id.userNameField);
        TextView email = findViewById(R.id.emailField);
        TextView type = findViewById(R.id.userTypeField);
        TextView group = findViewById(R.id.userGroupField);

        userName.setText(User.getUser().getUserName());
        email.setText("Ваша почта: " + User.getUser().getEmail());
        type.setText("Тип учётной записи: " + User.getUser().getType());
        group.setText(User.getUser().getUserGroup());
    }



    private void addUserToGroup(String nameOfGroup){ //не доделано
        FirebaseFirestore fStore; //Инициализируем здесь, тк этот метод будет включаться не всегда (для экономии паияти)
        fStore = FirebaseFirestore.getInstance();
        fStore.collection("groups").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        if(document.getId().equals(nameOfGroup)){ //в будующем заменить на уникельный индефекатор
                            Log.d(TAG, document.getId());
                            DocumentReference documentReference = fStore.collection("groups").document(nameOfGroup).collection("users").document(User.getUser().getUID());
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("UID", User.getUser().getUID());
                            userData.put("UserType", User.getUser().getType());
                            documentReference.set(userData);
                            User.getUser().setUserGroup(nameOfGroup);
                            Snackbar.make(mainElem, "Группа успешно установлена", Snackbar.LENGTH_LONG).show();
                            return;
                        }
                    }
                    Snackbar.make(mainElem, "Не существует группы с таким названием", Snackbar.LENGTH_LONG).show();
                }else{
                    Snackbar.make(mainElem, "Error" + task.getException(), Snackbar.LENGTH_LONG);
                }
            }
        });
    }

//        Map<String, Object> userData = new HashMap<>();
//        userData.put("UID", User.getUser().getUID());
//        userData.put("Type", User.getUser().getType());
//        documentReference.set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    Snackbar.make(mainElem,"Успешно ", Snackbar.LENGTH_LONG).show();
//                    User.setUserGroup(nameOfGroup);
//                }else{
//                    Snackbar.make(mainElem,"Ошибка " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
//                }
//            }
//        });


    private void changeActivity() {
        ImageButton profileBtn = findViewById(R.id.redirectToProfileBtn);
        ImageButton chatBtn = findViewById(R.id.redirectToChatBtn);
        ImageButton calendarBtn = findViewById(R.id.redirectToCalendarBtn);
        ImageButton estimatesBtn = findViewById(R.id.redirectToEstimatesBtn);

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Profile.class));
                finish();
            }
        });
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Chat.class));
                finish();
            }
        });
        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Calendar.class));
                finish();
            }
        });
        estimatesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Estimates.class));
                finish();
            }
        });
    }

}