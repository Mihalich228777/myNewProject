package com.example.mynewproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Chat extends AppCompatActivity {

    FirebaseFirestore fStore;
    ImageButton sendBtn;
    TextView textField;
    ConstraintLayout mainElem;
    EditText messageInput;
    int docId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        fStore = FirebaseFirestore.getInstance();
        sendBtn = findViewById(R.id.sendBtn);
        textField = findViewById(R.id.textField);
        mainElem = findViewById(R.id.mainElem);
        messageInput = findViewById(R.id.messageInput);
        changeActivity();
        //predUpdate();
        updateListener();
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(User.getUser().getUserGroup().equals("")){
                    Snackbar.make(mainElem, "Вы ещё не состоите в группе", Snackbar.LENGTH_LONG).show();
                    return;
                }
                String msg = messageInput.getText().toString();
                if(TextUtils.isEmpty(msg)){
                    messageInput.setError("");
                    return;
                }
                sendMsg(msg);
            }
        });
    }




    private void sendMsg(String msg){       //починить (это костыль) //здесь выполнятеся подготовка к отправке
        fStore.collection("groups").document(User.getUser().getUserGroup()).collection("messages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    docId = task.getResult().size();
                    addMsgToBd(msg);
                }else{
                    Log.d("TAG", "Error reading data");
                }
            }
        });
    }


    private void addMsgToBd(String msg){
        DocumentReference documentReference = fStore.collection("groups").document(User.getUser().getUserGroup()).collection("messages").document(String.valueOf(docId));
        Map<String, Object> msgData = new HashMap<>();
        msgData.put("message", msg);
        msgData.put("senderId", User.getUser().getUID());
        msgData.put("senderName", User.getUser().getUserName());
        documentReference.set(msgData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                }else{
                    Snackbar.make(mainElem, "Error " + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateListener(){
        fStore.collection("groups").document(User.getUser().getUserGroup()).collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                textField.setText("");
                for (DocumentSnapshot snapshot : value.getDocuments()) {
                    textField.append(snapshot.getString("senderName").toUpperCase(Locale.ROOT) + ": " + snapshot.getString("message") + "\n");
                }
            }
        });
    }












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