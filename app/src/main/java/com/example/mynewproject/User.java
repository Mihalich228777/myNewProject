package com.example.mynewproject;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class User {

    private String Uid;
    private String userName;
    private String type;
    private String email;


    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    public User() {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        this.Uid = fAuth.getCurrentUser().getUid();
        DocumentReference documentReference = fStore.collection("users").document(Uid);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                try {
                    throw new Exception(value.getString("Email"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                userName = value.getString("Full Name");
                type = value.getString("Type");

            } //как исправить ошибку
        });

    }

    public String getUserName() {
        return userName;
    }

    public String getType() {
        return type;
    }

    public String getUid() {
        return Uid;
    }

    public String getEmail() {
        return email;
    }
}
