package com.ratila.findmate;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.text.format.DateFormat;

public class Chat extends AppCompatActivity {
    public static int SIGN_IN_CODE = 1;
    private RelativeLayout chat;
    private FirebaseListAdapter<Message> adapter;
    private FloatingActionButton sendBtn;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_CODE){
            if(resultCode == RESULT_OK){
                Snackbar.make(chat, "Вы авторизованы", Snackbar.LENGTH_LONG).show();
                displayAllMessages();
            } else{
                Snackbar.make(chat, "Вы не авторизованы", Snackbar.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chat = findViewById(R.id.main);
        sendBtn = findViewById(R.id.btnSend);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText textField = findViewById(R.id.messageField);

                // Проверяем, чтобы поле ввода не было пустым
                if (textField.getText().toString().trim().isEmpty()) {
                    Log.d("Chat", "Message is empty. Skipping sending.");
                    return;
                }

                // Лог для проверки перед отправкой сообщения
                Log.d("Chat", "Attempting to send message: " + textField.getText().toString().trim());

                // Пытаемся отправить сообщение в Firebase
                FirebaseDatabase.getInstance().getReference("messages").push().setValue(
                        new Message(
                                FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                textField.getText().toString()
                        )
                ).addOnSuccessListener(aVoid -> {
                    // Лог успеха
                    Log.d("Chat", "Message sent successfully.");
                }).addOnFailureListener(e -> {
                    // Лог ошибки
                    Log.e("Chat", "Failed to send message.", e);
                });

                // Очищаем поле ввода
                textField.setText("");
            }
        });
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_CODE);
        else {
            Snackbar.make(chat, "Вы авторизованы", Snackbar.LENGTH_LONG).show();
            displayAllMessages();
        }
    }

    private void displayAllMessages() {
        Log.d("Chat", "Initializing ListView and FirebaseListAdapter.");

        ListView listOfMessages = findViewById(R.id.list_of_messages);

        // Создание Query для Firebase Database
        Query query = FirebaseDatabase.getInstance().getReference("messages").orderByChild("messageTime");
        Log.d("Chat", "Firebase query created: " + query.toString());

        // Настройка FirebaseListOptions
        FirebaseListOptions<Message> options = new FirebaseListOptions.Builder<Message>()
                .setQuery(query, Message.class) // Указываем запрос и класс модели
                .setLayout(R.layout.list_item) // Указываем макет для элемента списка
                .build();

        // Создание адаптера
        adapter = new FirebaseListAdapter<Message>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Message model, int position) {
                TextView mess_user, mess_time, mess_text;

                // Найти элементы в макете
                mess_user = v.findViewById(R.id.message_user);
                mess_time = v.findViewById(R.id.message_time);
                mess_text = v.findViewById(R.id.message_text);

                // Логирование данных
                Log.d("Chat", "Populating view with message: " +
                        "user=" + model.getUserName() +
                        ", text=" + model.getTextMessage() +
                        ", time=" + model.getMessageTime());

                // Установить данные
                mess_user.setText(model.getUserName());
                mess_text.setText(model.getTextMessage());
                mess_time.setText(DateFormat.format("dd-MM-yyyy HH:mm:ss", model.getMessageTime()));
            }
        };

        Log.d("Chat", "Adapter created. Setting adapter to ListView.");
        // Установить адаптер
        listOfMessages.setAdapter(adapter);

        Log.d("Chat", "Adapter set. Starting adapter listening.");
        adapter.startListening();
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Chat", "onStart called. Checking if user is logged in.");
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Log.d("Chat", "User not logged in. Prompting sign-in.");
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_CODE);
        } else {
            Log.d("Chat", "User already logged in.");
            displayAllMessages();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Chat", "onStop called. Stopping adapter.");
        if (adapter != null) {
            adapter.stopListening();  // Stop listening when activity is stopped
        }
    }
}
