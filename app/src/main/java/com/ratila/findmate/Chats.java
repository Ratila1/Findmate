package com.ratila.findmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Chats extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<ChatItem> chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chats);

        // Установка отступов для системы
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.chat_list), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Инициализация RecyclerView
        recyclerView = findViewById(R.id.chat_list); // Убедитесь, что этот ID есть в activity_chats.xml
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Создаем данные для списка чатов
        chatList = new ArrayList<>();
        chatList.add(new ChatItem("1", "Chat 1", "Last message in Chat 1", R.drawable.circle_avatar));
        chatList.add(new ChatItem("2", "Chat 2", "Last message in Chat 2", R.drawable.circle_avatar));

        // Установка адаптера
        adapter = new ChatAdapter(chatList, chatId -> {
            // Переход в Chat.java с передачей данных
            Intent intent = new Intent(Chats.this, Chat.class);
            intent.putExtra("chat_id", chatId);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

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
}
