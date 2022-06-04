package com.example.mynewproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;


public class Calendar extends AppCompatActivity {

    FirebaseFirestore fStore;
    ConstraintLayout mainElem;
    Button forAdmin;
    String dayOfWeek;
    LinearLayout linLayout;
    TextView dayOfWeekField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        changeActivity();
        dayOfWeek = "Monday";
        mainElem = findViewById(R.id.mainElem);
        fStore = FirebaseFirestore.getInstance();
        forAdmin = findViewById(R.id.forAdmin);
        linLayout = findViewById(R.id.linLayout);
        dayOfWeekField = findViewById(R.id.dayOfWeekField);

        if(!User.getUser().getType().equals("Учитель")){
            forAdmin.setEnabled(false);
        }

        forAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAlertDilog();
            }
        });


    }




    private void startAlertDilog() {
        if(User.getUser().getUserGroup().equals("")){
            Snackbar.make(mainElem, "Вы ещё не состоите в группе", Snackbar.LENGTH_LONG).show();
            return;
        }
        AlertDialog.Builder bilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(mainElem.getContext());
        View view = inflater.inflate(R.layout.add_shablon, null);
        bilder.setView(view);
        bilder.setPositiveButton("Продолжить", new DialogInterface.OnClickListener() { //почему я не могу присовить значение не одной переменной не находящейся в onClick ???????????????????????????????????????????????????????????????????????????????
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText text1 = view.findViewById(R.id.editText1);
                EditText text2 = view.findViewById(R.id.editText2);
                EditText text3 = view.findViewById(R.id.editText3);
                EditText text4 = view.findViewById(R.id.editText4);
                EditText text5 = view.findViewById(R.id.editText5);
                EditText text6 = view.findViewById(R.id.editText6);
                EditText text7 = view.findViewById(R.id.editText7);
                String[] acc = new String[]{text1.getText().toString(), text2.getText().toString(), text3.getText().toString(), text4.getText().toString(), text5.getText().toString(), text6.getText().toString(), text7.getText().toString()};
                addToTable(acc);
            }
        });
        bilder.create().show();

    }
    private void addToTable(String[] acc) {
        DocumentReference documentReference = fStore.collection("groups").document(User.getUser().getUserGroup()).collection("raspisanie").document(dayOfWeek);
        Map<String, Object> data = new HashMap<>();
        for (int i = 0; i < acc.length; i++) {
            Log.d("TAG", String.valueOf(i));
            data.put(String.valueOf(i), acc[i]);
        }
        documentReference.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("TAG", "baldej");
                    showTable(dayOfWeek);
                }else{
                    Snackbar.make(mainElem, "Ошибка" + task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }


    public void switchDay(View view) {

        switch(view.getId()){
            case R.id.btnMonday:
                    dayOfWeek = "Monday";
                    showTable(dayOfWeek);
                    Log.d("TAG", dayOfWeek);
                break;
            case R.id.btnTuesday:
                    dayOfWeek = "Tuesday";
                    showTable(dayOfWeek);
                Log.d("TAG", dayOfWeek);
                break;
            case R.id.btnWednesday:
                    dayOfWeek = "Wednesday";
                    showTable(dayOfWeek);
                    Log.d("TAG", dayOfWeek);
                break;
            case R.id.btnThursday:
                    dayOfWeek = "Thursday";
                    showTable(dayOfWeek);
                    Log.d("TAG", dayOfWeek);
                break;
            case R.id.btnFriday:
                    dayOfWeek = "Friday";
                    showTable(dayOfWeek);
                    Log.d("TAG", dayOfWeek);
                break;
            case R.id.btnSaturday:
                    dayOfWeek = "Saturday";
                    showTable(dayOfWeek);
                    Log.d("TAG", dayOfWeek);
                break;
        }
    }


    public void showTable(String dayOfWeek){
        if(User.getUser().getUserGroup().equals("")){
            Snackbar.make(mainElem, "Вы ещё не состоите в группе", Snackbar.LENGTH_LONG).show();
            return;
        }
        dayOfWeekField.setText(dayOfWeek);
        linLayout.removeAllViews();
        fStore.collection("groups").document(User.getUser().getUserGroup()).collection("raspisanie").document(dayOfWeek).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                int acc;
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()){
                        for (int i = 0; i < 7; i++) {
                            LayoutInflater inflater = LayoutInflater.from(linLayout.getContext());
                            View v = inflater.inflate(R.layout.table_fragment, null);
                            TextView numberLesson = v.findViewById(R.id.numberLesson);
                            TextView lessonName = v.findViewById(R.id.lessonName);
                            acc = i;
                            numberLesson.setText(String.valueOf(acc+1) + " урок: ");
                            lessonName.setText(snapshot.getString(String.valueOf(i)));
                            linLayout.addView(v);
                        }
                    }
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