package com.ratila.findmate;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

public class SettingsMenu extends AppCompatActivity {

    private View button13; // Инициализируем кнопку
    private View button15; // Кнопка для смены темы
    private View button16; // Кнопка для смены языка

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_menu);
        // Инициализация кнопок
        button13 = findViewById(R.id.button13);
        button15 = findViewById(R.id.button15); // Кнопка для смены темы
        button16 = findViewById(R.id.button16); // Кнопка для смены языка

        // Устанавливаем обработчик нажатия для перехода в другой экран
        button13.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsMenu.this, MenuAccount.class);
            startActivity(intent);
            finish(); // Закрываем текущую активность
        });

        // Устанавливаем обработчик нажатия для смены темы
        button15.setOnClickListener(v -> {
            toggleTheme(); // Переключаем тему
            Toast.makeText(SettingsMenu.this, "Theme Changed", Toast.LENGTH_SHORT).show();
        });

        // Устанавливаем обработчик нажатия для смены языка
        button16.setOnClickListener(v -> {
            Toast.makeText(SettingsMenu.this, "Language Changed", Toast.LENGTH_SHORT).show(); // Показать уведомление
        });
    }

    // Метод для смены языка
    private void changeLanguage() {
        Locale currentLocale = getResources().getConfiguration().locale;
        Locale newLocale;

        if (currentLocale.getLanguage().equals("ru")) {
            newLocale = new Locale("en"); // Если текущий язык русский, меняем на английский
        } else {
            newLocale = new Locale("ru"); // Если текущий язык не русский, меняем на русский
        }

        // Устанавливаем новый язык
        Locale.setDefault(newLocale);
        Configuration configuration = getResources().getConfiguration();
        configuration.setLocale(newLocale);

        // Обновляем конфигурацию и ресурсы
        getBaseContext().getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

        // Перезапускаем активность с новым языком
        Intent intent = getIntent();
        finish();
        startActivity(intent); // Перезапускаем активность с новым языком
    }

    // Метод для переключения темы
    private void toggleTheme() {
        // Получаем текущую тему
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        // Переключаем тему в зависимости от текущего режима
        if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            // Если светлая тема, переключаем на тёмную
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            // Если тёмная тема, переключаем на светлую
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Перезапускаем активность, чтобы применить изменения
        recreate();
    }
}
