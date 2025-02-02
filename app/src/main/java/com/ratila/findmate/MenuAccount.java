package com.ratila.findmate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MenuAccount extends AppCompatActivity {
    private ImageView imageChoose, imageMess, imageView15; // Добавляем imageView15
    private View buttonExit, button12, button9, button10, button11; // Добавляем кнопку button10
    private TextView nameAccount; // TextView для отображения имени пользователя
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_account);

        // Устанавливаем отступы для системных панелей
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Инициализация элементов
        imageMess = findViewById(R.id.imageMess);
        imageChoose = findViewById(R.id.imageChoose);
        imageView15 = findViewById(R.id.imageView15); // Инициализируем imageView15
        buttonExit = findViewById(R.id.buttonExit);
        button12 = findViewById(R.id.button12); // Инициализируем button12
        button9 = findViewById(R.id.button9); // Инициализируем button9
        button10 = findViewById(R.id.button10); // Инициализируем button10
        button11 = findViewById(R.id.button11);
        nameAccount = findViewById(R.id.nameAccount); // Привязываем TextView

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Загружаем данные пользователя
        loadUserName();
        updateProfileImage(); // Метод для обновления изображения профиля

        // Обработчики нажатий
        setupListeners();

        button11.setOnClickListener(v -> {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                // Обновляем или добавляем поле "visibility" в базе данных Firestore
                db.collection("app").document(userId)
                        .update("visibility", "no")  // Устанавливаем значение "no" или "yes"
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(MenuAccount.this, "Profile visibility updated", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("MenuAccount", "Error updating visibility", e);
                            Toast.makeText(MenuAccount.this, "Error updating visibility", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private void loadUserName() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Получаем UID текущего пользователя
            // Получаем данные пользователя из Firestore
            db.collection("app")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Извлекаем имя и фамилию
                            String firstName = documentSnapshot.getString("FirstName");
                            String lastName = documentSnapshot.getString("LastName");
                            if (firstName != null && lastName != null) {
                                // Отображаем имя и фамилию в nameAccount
                                nameAccount.setText(firstName + " " + lastName);
                            } else {
                                nameAccount.setText("Имя не указано");
                            }
                        } else {
                            Toast.makeText(MenuAccount.this, "Профиль не найден", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("MenuAccount", "Ошибка загрузки профиля", e);
                        Toast.makeText(MenuAccount.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        // Обработчик для imageChoose (возвращает в MainActivity)
        imageChoose.setOnClickListener(v -> {
            Intent intent = new Intent(MenuAccount.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Закрываем текущую активность
        });

        // Обработчик для buttonExit (выход из аккаунта и переход в RegisterMenuActivity)
        buttonExit.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut(); // Выход из аккаунта
            Intent intent = new Intent(MenuAccount.this, RegisterMenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Закрываем текущую активность
        });

        // Обработчик для button12 (переход в SettingsMenu)
        button12.setOnClickListener(v -> {
            Intent intent = new Intent(MenuAccount.this, SettingsMenu.class);
            startActivity(intent);
        });

        // Обработчик для button9 (переход в ProfileMenu)
        button9.setOnClickListener(v -> {
            Intent intent = new Intent(MenuAccount.this, ProfileMenu.class);
            startActivity(intent);
        });

        // Обработчик для button10 (переход в Poisk)
        button10.setOnClickListener(v -> {
            Intent intent = new Intent(MenuAccount.this, Poisk.class);  // Указываем активность Poisk
            startActivity(intent);
        });

        // Обработчик для imageMess1 (переход в Chat)
        imageMess.setOnClickListener(v -> {
            Intent intent = new Intent(MenuAccount.this, Chats.class);  // Переход в Chat
            startActivity(intent);
        });
    }

    private void updateProfileImage() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String profileUid = currentUser.getUid();
            switch (profileUid) {
                case "euRGycsEhSRvrJJsd7wUMMeIJE13":
                    imageView15.setImageResource(R.drawable.pers1); // pers1
                    break;
                case "1RpKsjGG1NeIhCl7yPw8XfFLlFw1":
                    imageView15.setImageResource(R.drawable.pers2); // pers2
                    break;
                case "UBFfYtorXlhpL6zJiuU0adYnGn53":
                    imageView15.setImageResource(R.drawable.pers3); // pers3
                    break;
                case "YktbJkBC14WodkHFLylTApS0IYr1":
                    imageView15.setImageResource(R.drawable.pers4); // pers4
                    break;
                default:
                    imageView15.setVisibility(View.INVISIBLE); // Если UID не совпадает, скрываем картинку
                    break;
            }
        } else {
            imageView15.setVisibility(View.INVISIBLE); // Если пользователь не авторизован, скрываем картинку
        }
    }
}