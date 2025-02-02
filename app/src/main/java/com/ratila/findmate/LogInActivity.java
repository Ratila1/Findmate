package com.ratila.findmate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LogInActivity extends AppCompatActivity {

    private EditText emailEditText, passEditText;
    private TextView textView21, textView30; // Добавлено textView30
    private Button button1, button2;
    private FirebaseAuth mAuth;
    private CheckBox checkBox; // Используем checkBox

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Инициализация FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Инициализация элементов
        emailEditText = findViewById(R.id.email);
        passEditText = findViewById(R.id.pass);
        textView21 = findViewById(R.id.textView21);
        textView30 = findViewById(R.id.textView30); // Инициализация textView30
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        checkBox = findViewById(R.id.checkBox); // Инициализация checkBox

        // Обработчик кнопки для перехода в RegisterMenuActivity
        button1.setOnClickListener(view -> {
            Intent intent = new Intent(LogInActivity.this, RegisterMenuActivity.class);
            startActivity(intent);
        });

        // Обработчик для перехода в SignInActivity через textView30
        textView30.setOnClickListener(view -> {
            Intent intent = new Intent(LogInActivity.this, SignInActivity.class);
            startActivity(intent);
        });

        // Обработчик кнопки для проверки почты и пароля через Firebase Authentication
        button2.setOnClickListener(view -> {
            String email = emailEditText.getText().toString().trim();
            String password = passEditText.getText().toString().trim();

            // Очистка предыдущих ошибок
            textView21.setText("");
            textView21.setTextColor(getResources().getColor(android.R.color.black));

            // Валидация входных данных
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                textView21.setText("Пожалуйста, заполните все поля");
                textView21.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                textView21.setText("Неверный формат email");
                textView21.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            } else {
                // Проверка почты и пароля через Firebase
                signInWithEmailPassword(email, password);
            }
        });

        // Обработчик для CheckBox, который переключает видимость пароля
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Показать пароль
                passEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // Скрыть пароль
                passEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
    }

    private void signInWithEmailPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Переход в MainActivity, если данные верны
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Закрыть текущую активность, чтобы вернуться назад нельзя было
                    } else {
                        // Обработка ошибок
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            // Если пользователь не найден
                            textView21.setText("Аккаунта с такой почтой нет, зарегистрируйтесь!");
                            textView21.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        } else {
                            // Если ошибка авторизации, выводим сообщение об ошибке
                            textView21.setText("Неверные данные, попробуйте снова");
                            textView21.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        }
                        emailEditText.setText("");
                        passEditText.setText("");
                    }
                });
    }

    // Для выхода из аккаунта (например, если нужно в LogoutActivity)
    private void signOut() {
        mAuth.signOut();
    }
}
