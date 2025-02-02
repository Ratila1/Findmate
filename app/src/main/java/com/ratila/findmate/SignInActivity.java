package com.ratila.findmate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.common.api.ApiException;

public class SignInActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        // Инициализация FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Настройка Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))  // Ваш client ID
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Установка отступов для полноэкранного отображения
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Инициализация элементов
        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button button26 = findViewById(R.id.button26); // Кнопка для Google Sign-In
        CheckBox checkBox1 = findViewById(R.id.checkBox1);
        EditText emailEditText = findViewById(R.id.email);
        TextView errorTextView = findViewById(R.id.textView25);
        TextView textView29 = findViewById(R.id.textView29);

        // Обработчик для button1
        button1.setOnClickListener(v -> {
            // Возвращаемся в RegisterMenuActivity
            Intent intent = new Intent(SignInActivity.this, RegisterMenuActivity.class);
            startActivity(intent);
        });

        button2.setOnClickListener(v -> {
            // Получаем введенную почту
            String email = emailEditText.getText().toString().trim();
            boolean isCheckBoxChecked = checkBox1.isChecked();

            // Проверка: включен ли CheckBox
            if (!isCheckBoxChecked) {
                errorTextView.setTextColor(Color.RED);
                errorTextView.setText("Вы должны принять условия!");
                return;
            }

            // Проверка: корректно ли введена почта
            if (TextUtils.isEmpty(email)) {
                errorTextView.setTextColor(Color.RED);
                errorTextView.setText("Введите почту!");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                errorTextView.setTextColor(Color.RED);
                errorTextView.setText("Введите корректный email!");
                return;
            }

            // Проверка, зарегистрирован ли аккаунт с этой почтой
            mAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Получаем список методов входа для этой почты
                            if (task.getResult().getSignInMethods() != null && !task.getResult().getSignInMethods().isEmpty()) {
                                // Если методы входа есть, значит аккаунт уже зарегистрирован
                                errorTextView.setTextColor(Color.RED);
                                errorTextView.setText("Аккаунт с этой почтой уже зарегистрирован!");
                            } else {
                                // Если аккаунта нет, переходим к регистрации
                                Intent intent = new Intent(SignInActivity.this, registration.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
                            }
                        } else {
                            // Ошибка при проверке
                            errorTextView.setTextColor(Color.RED);
                            errorTextView.setText("Ошибка при проверке почты. Попробуйте снова.");
                        }
                    });
        });

        // Обработчик для button26 (Google Sign-In)
        button26.setOnClickListener(v -> signInWithGoogle());

        checkBox1.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Показать диалоговое окно
                showAgreementDialog(checkBox1);
            }
            return true; // Предотвращает автоматическое изменение состояния чекбокса
        });

        textView29.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, LogInActivity.class);
            startActivity(intent);
        });
    }

    // Метод для Google Sign-In
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 101);  // Код запроса 101
    }

    // Обработка результата от Google Sign-In
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                // Получаем токен авторизации
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, task1 -> {
                            if (task1.isSuccessful()) {
                                // Успешная аутентификация через Google
                                Intent intent = new Intent(SignInActivity.this, registration.class);
                                intent.putExtra("email", account.getEmail());  // Передаем email пользователя
                                startActivity(intent);
                            } else {
                                // Ошибка при аутентификации
                                Toast.makeText(SignInActivity.this, "Ошибка авторизации через Google", Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (ApiException e) {
                // Ошибка
                Toast.makeText(SignInActivity.this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showAgreementDialog(CheckBox checkBox) {
        // Создаем диалог с кастомным стилем
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Пользовательское соглашение");
        builder.setMessage("Вы соглашаетесь с условиями использования?");
        builder.setCancelable(false);

        // Кнопка "Принять"
        builder.setPositiveButton("Принять", (dialog, which) -> {
            checkBox.setChecked(true); // Устанавливаем CheckBox в состояние "включено"
            dialog.dismiss();
        });

        // Кнопка "Отклонить"
        builder.setNegativeButton("Отклонить", (dialog, which) -> {
            checkBox.setChecked(false); // Оставляем CheckBox в состоянии "выключено"
            dialog.dismiss();
        });

        // Показываем диалог
        AlertDialog dialog = builder.create();
        dialog.show();

        // Получаем кнопки диалога и задаем цвета текста
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        if (positiveButton != null) {
            positiveButton.setTextColor(Color.parseColor("#2A7A78"));
        }
        if (negativeButton != null) {
            negativeButton.setTextColor(Color.parseColor("#2A7A78"));
        }
    }
}
