package com.ratila.findmate;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.FirebaseFirestore;

import android.util.Log;
import android.widget.RelativeLayout;

public class Chat extends AppCompatActivity {
    public static final int SIGN_IN_CODE = 1;
    private RelativeLayout chat;
    private FirebaseListAdapter<Message> adapter;
    private Button sendBtn;
    private String chatId;
    private String userName; // Для хранения имени пользователя

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chat = findViewById(R.id.main);
        sendBtn = findViewById(R.id.btnSend);
        TextView userNameTextView = findViewById(R.id.userName);
        Button backButton = findViewById(R.id.backButton); // Добавляем кнопку назад

        // Получаем chat_id и user_name из Intent
        chatId = getIntent().getStringExtra("chat_id");
        userName = getIntent().getStringExtra("user_name");

        if (chatId == null || userName == null) {
            Log.e("Chat", "Chat ID or User Name is null. Exiting.");
            finish();
            return;
        }

        // Устанавливаем имя пользователя в TextView
        userNameTextView.setText(userName);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(Chat.this, Chats.class);
            startActivity(intent);
            finish(); // Закрываем текущий чат
        });

        sendBtn.setOnClickListener(v -> {
            EditText textField = findViewById(R.id.messageField);
            String messageText = textField.getText().toString().trim();
            if (messageText.isEmpty()) {
                Log.d("Chat", "Message is empty. Skipping sending.");
                return;
            }

            final String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Получаем имя пользователя из Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("app").document(senderId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String userName = documentSnapshot.getString("FirstName") + " " + documentSnapshot.getString("LastName");
                            DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId).child("messages");
                            String messageId = chatRef.push().getKey();
                            Message message = new Message(
                                    senderId,
                                    userName,
                                    messageText,
                                    System.currentTimeMillis()
                            );
                            chatRef.child(messageId).setValue(message)
                                    .addOnSuccessListener(aVoid -> Log.d("Chat", "Message sent successfully."))
                                    .addOnFailureListener(e -> Log.e("Chat", "Failed to send message.", e));
                            textField.setText("");
                        } else {
                            Log.e("Chat", "User data not found.");
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Chat", "Failed to get user name.", e));
        });

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_CODE);
        } else {
            Snackbar.make(chat, "Вы авторизованы", Snackbar.LENGTH_LONG).show();
            displayAllMessages(chatId);
        }
    }

    // Обрабатываем системную кнопку "Назад"
    @Override
    public void onBackPressed() {
        // Запускаем активность Chats
        Intent intent = new Intent(Chat.this, Chats.class);
        startActivity(intent);

        // Закрываем текущую активность
        finish();

        // Вызываем super.onBackPressed(), если необходимо сохранить стандартное поведение
        super.onBackPressed();
    }


    private void displayAllMessages(String chatId) {
        Log.d("Chat", "Displaying messages for chat_id: " + chatId);

        ListView listOfMessages = findViewById(R.id.list_of_messages);

        Query query = FirebaseDatabase.getInstance().getReference("chats")
                .child(chatId)
                .child("messages")
                .orderByChild("messageTime");


        FirebaseListOptions<Message> options = new FirebaseListOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLayout(R.layout.list_item)
                .build();

        adapter = new FirebaseListAdapter<>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Message model, int position) {
                TextView mess_time = v.findViewById(R.id.message_time);
                TextView mess_text = v.findViewById(R.id.message_text);
                LinearLayout messageContainer = v.findViewById(R.id.message_container);

                // Контейнер под размер текста
                ViewGroup.LayoutParams params = messageContainer.getLayoutParams();
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                messageContainer.setLayoutParams(params);

                // Определяем, кто отправил сообщение
                if (model.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    messageContainer.setBackgroundResource(R.drawable.message_background_user);
                    ((LinearLayout.LayoutParams) messageContainer.getLayoutParams()).gravity = Gravity.END;
                } else {
                    messageContainer.setBackgroundResource(R.drawable.message_background_other);
                    ((LinearLayout.LayoutParams) messageContainer.getLayoutParams()).gravity = Gravity.START;
                }

                mess_text.setText(model.getTextMessage());

                if (model.getMessageTime() > 0) {
                    mess_time.setText(DateFormat.format("HH:mm", model.getMessageTime()));
                } else {
                    mess_time.setText("Unknown time");
                }
            }


        };

        listOfMessages.setAdapter(adapter);
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
            displayAllMessages(chatId);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Chat", "onStop called. Stopping adapter.");
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_CODE) {
            if (resultCode == RESULT_OK) {
                Snackbar.make(chat, "Вы авторизованы", Snackbar.LENGTH_LONG).show();
                displayAllMessages(chatId);
            } else {
                Snackbar.make(chat, "Вы не авторизованы", Snackbar.LENGTH_LONG).show();
                finish();
            }
        }
    }
}