package com.ratila.findmate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Chats extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<ChatItem> chatList;
    private DatabaseReference likesRef;
    private FirebaseFirestore db;
    private String currentUserId;

    // Добавляем ссылки на overlay и ProgressBar
    private FrameLayout overlay;
    private ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chats);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#2A7A78"));
        }

        // Инициализация overlay и ProgressBar
        overlay = findViewById(R.id.overlay);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        // Установка отступов для системы
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chat_list), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Инициализация RecyclerView
        recyclerView = findViewById(R.id.chat_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatList = new ArrayList<>();
        adapter = new ChatAdapter(chatList, (String chatId) -> {
            Intent intent = new Intent(Chats.this, Chat.class);
            intent.putExtra("chat_id", chatId); // Передача chat_id
            intent.putExtra("user_name", getNameForChatId(chatId)); // Передача имени пользователя
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        // Firebase
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likesRef = FirebaseDatabase.getInstance().getReference("likes");
        db = FirebaseFirestore.getInstance();

        // Показываем прогрессбар перед загрузкой данных
        showLoadingOverlay();

        // Загружаем чаты из Firebase
        loadChatsFromFirebase();

        // Обработка нажатий на imageChoose2
        findViewById(R.id.imageChoose2).setOnClickListener(v -> {
            Intent intent = new Intent(Chats.this, MainActivity.class);
            startActivity(intent);
        });

        // Обработка нажатий на imageMenu2
        findViewById(R.id.imageMenu2).setOnClickListener(v -> {
            Intent intent = new Intent(Chats.this, MenuAccount.class);
            startActivity(intent);
        });
    }

    /**
     * Показывает overlay с ProgressBar
     */
    private void showLoadingOverlay() {
        if (overlay != null) {
            overlay.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Скрывает overlay с ProgressBar
     */
    private void hideLoadingOverlay() {
        if (overlay != null) {
            overlay.setVisibility(View.GONE);
        }
    }

    /**
     * Загружает список пользователей, с которыми есть взаимные лайки
     */
    private void loadChatsFromFirebase() {
        likesRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String likedUserId = snapshot.getKey(); // UID лайкнутого пользователя
                        // Проверяем, лайкнул ли этот пользователь в ответ
                        likesRef.child(likedUserId).child(currentUserId).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult().exists()) {
                                // Взаимный лайк найден, загружаем данные пользователя
                                loadUserData(likedUserId);
                            }
                        });
                    }
                }
                // После завершения загрузки скрываем прогрессбар
                hideLoadingOverlay();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Ошибка загрузки данных
                Log.e("Chats", "Failed to load chats: " + databaseError.getMessage());
                hideLoadingOverlay(); // Скрываем прогрессбар даже при ошибке
            }
        });
    }

    /**
     * Загружает данные пользователя по UID из Firestore
     */
    private void loadUserData(String userId) {
        db.collection("app").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DocumentSnapshot document = task.getResult();
                String firstName = document.getString("FirstName");
                String lastName = document.getString("LastName");
                // Проверка на null
                if (firstName != null && lastName != null) {
                    String name = firstName + " " + lastName;
                    String avatar = document.getString("Avatar");
                    // Генерируем уникальный chatId для пользователей
                    String chatId = generateChatId(currentUserId, userId);
                    // Загружаем последнее сообщение для этого чата
                    loadLastMessage(chatId);
                    // Добавляем элемент в список чатов
                    boolean isUserMessage = userId.equals(currentUserId);
                    chatList.add(new ChatItem(chatId, name, "Loading...", R.drawable.circle_avatar, isUserMessage));
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("Chats", "User data is incomplete for user: " + userId);
                }
            } else {
                Log.e("Chats", "Failed to get user data for user: " + userId);
            }
        });
    }

    /**
     * Загружает последнее сообщение для чата
     */
    private void loadLastMessage(String chatId) {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats").child(chatId).child("messages");
        chatRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot lastMessageSnapshot = dataSnapshot.getChildren().iterator().next();
                    String lastMessage = lastMessageSnapshot.child("textMessage").getValue(String.class);
                    long messageTime = lastMessageSnapshot.child("messageTime").getValue(Long.class);
                    String senderId = lastMessageSnapshot.child("sender").getValue(String.class);
                    // Получаем имя отправителя из Firestore
                    db.collection("app").document(senderId).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            DocumentSnapshot document = task.getResult();
                            String senderName = document.getString("FirstName") + " " + document.getString("LastName");
                            for (ChatItem chatItem : chatList) {
                                if (chatItem.getChatId().equals(chatId)) {
                                    if (senderId.equals(currentUserId)) {
                                        chatItem.setLastMessage("Вы: " + lastMessage);
                                    } else {
                                        chatItem.setLastMessage(senderName + ": " + lastMessage);
                                    }
                                    chatItem.setMessageTime(messageTime); // Установка времени сообщения
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            Log.e("Chats", "Failed to get user name for message in chatId: " + chatId);
                        }
                    });
                } else {
                    Log.d("Chats", "No messages found for chatId: " + chatId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Chats", "Failed to load last message for chatId: " + chatId + ", error: " + databaseError.getMessage());
            }
        });
    }

    /**
     * Генерирует уникальный chatId для пары пользователей
     */
    private String generateChatId(String user1, String user2) {
        return user1.compareTo(user2) < 0 ? user1 + "_" + user2 : user2 + "_" + user1;
    }

    /**
     * Получает имя пользователя для указанного chatId
     */
    private String getNameForChatId(String chatId) {
        for (ChatItem chatItem : chatList) {
            if (chatItem.getChatId().equals(chatId)) {
                return chatItem.getName(); // Возвращаем имя пользователя
            }
        }
        return "Unknown User"; // Если имя не найдено
    }
}