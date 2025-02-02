package com.ratila.findmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterMenuActivity extends AppCompatActivity {

    private Button buttonSignIn, buttonSignUp;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_regis);

        // Инициализация FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        // Проверка, аутентифицирован ли пользователь
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // Если пользователь аутентифицирован, переходим в MainActivity
            Intent intent = new Intent(RegisterMenuActivity.this, MainActivity.class);
            startActivity(intent);
            finish();  // Закрыть текущую активность, чтобы вернуться назад было нельзя
        }

        // Найти кнопки по их ID
        buttonSignIn = findViewById(R.id.buttonSignIn);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        // Установить слушатель для buttonSignIn
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Открыть LoginActivity
                Intent intent = new Intent(RegisterMenuActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        });

        // Установить слушатель для buttonSignUp
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Открыть SignUpActivity
                Intent intent = new Intent(RegisterMenuActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }
}
