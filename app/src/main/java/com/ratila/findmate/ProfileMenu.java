package com.ratila.findmate;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ProfileMenu extends AppCompatActivity {

    private EditText editTextFirstName, editTextLastName, editTextDopopis;
    private TextView textViewAge, textView37;
    private Button button18, button20, button21, button22, button23, button24;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String selectedCountry, selectedCity;
    private HashMap<String, String[]> countryCitiesMap;
    private List<String> selectedSpecializations = new ArrayList<>();
    private List<String> selectedSubSpecializations = new ArrayList<>();

    private String firstName, lastName, age, country, city, exp1, exp2, dopopis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_menu);

        // Инициализация элементов
        editTextFirstName = findViewById(R.id.editTextText5);
        editTextLastName = findViewById(R.id.editTextText4);
        textViewAge = findViewById(R.id.textView34);
        textView37 = findViewById(R.id.textView37);
        editTextDopopis = findViewById(R.id.editTextText6);
        button18 = findViewById(R.id.button18);
        button20 = findViewById(R.id.button20);
        button23 = findViewById(R.id.button23);
        button24 = findViewById(R.id.button24);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Обработчик для button3 (открытие LocationPickerActivity)
        Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileMenu.this, LocationPickerActivity.class);
            startActivityForResult(intent, 100); // 100 - код запроса
        });

        // Обработчик для button24 (выбор подспециальностей)
        button24.setOnClickListener(v -> {
            if (selectedSpecializations.isEmpty()) {
                Toast.makeText(this, "Сначала выберите специальность", Toast.LENGTH_SHORT).show();
            } else {
                showSubSpecializationDialog(selectedSpecializations);
            }
        });

        // Обработчик для buttonExp1 (выбор специальностей)
        button23.setOnClickListener(v -> showSpecializationDialog());

        // Обработчик для button18 (сохранение данных)
        button18.setOnClickListener(v -> saveUserData());

        // Обработчик для button20 (выбор даты рождения)
        button20.setOnClickListener(v -> showDatePickerDialog());

        // Устанавливаем отступы для системных панелей
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Загрузка данных пользователя
        loadUserData();
    }

    private void loadUserData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Получаем UID текущего пользователя
            Log.d("ProfileMenu", "Loading data for user: " + userId);

            // Получаем данные пользователя из Firestore
            db.collection("app")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            try {
                                // Извлекаем данные
                                String firstName = documentSnapshot.getString("FirstName");
                                String lastName = documentSnapshot.getString("LastName");
                                Object ageObject = documentSnapshot.get("Age");
                                String age = (ageObject != null) ? ageObject.toString() : "Не указан";
                                String country = documentSnapshot.getString("Country");
                                String city = documentSnapshot.getString("City");
                                String exp1 = documentSnapshot.getString("Exp1");
                                String exp2 = documentSnapshot.getString("Exp2");
                                String dopopis = documentSnapshot.getString("Dopopis");

                                Log.d("ProfileMenu", "Data retrieved: FirstName = " + firstName + ", LastName = " + lastName);

                                // Заполняем поля с проверкой на null
                                editTextFirstName.setText(firstName != null ? firstName : "");
                                editTextLastName.setText(lastName != null ? lastName : "");
                                textViewAge.setText(age != null ? age + " лет" : "Не указан");

                                // Загружаем город и страну в textView37
                                if (city != null && country != null) {
                                    textView37.setText("Город: " + city + ", Страна: " + country);
                                } else {
                                    textView37.setText("Город и страна не указаны");
                                }

                                button23.setText(exp1 != null ? exp1 : "Не выбрана");
                                button24.setText(exp2 != null ? exp2 : "Не выбрана");
                                editTextDopopis.setText(dopopis != null ? dopopis : "");
                            } catch (Exception e) {
                                Log.e("ProfileMenu", "Error parsing data: " + e.getMessage(), e);
                                Toast.makeText(ProfileMenu.this, "Ошибка при обработке данных: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d("ProfileMenu", "Profile not found for user: " + userId);
                            Toast.makeText(ProfileMenu.this, "Профиль не найден", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ProfileMenu", "Ошибка загрузки профиля", e);
                        Toast.makeText(ProfileMenu.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.w("ProfileMenu", "User is not authenticated");
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Сбор данных из полей
            HashMap<String, Object> userData = new HashMap<>();
            userData.put("FirstName", editTextFirstName.getText().toString().trim());
            userData.put("LastName", editTextLastName.getText().toString().trim());
            userData.put("Age", textViewAge.getText().toString().replace(" лет", "").trim());
            userData.put("Country", selectedCountry);
            userData.put("City", selectedCity);
            userData.put("Exp1", button23.getText().toString().replace("Выбранные специальности: ", "").trim());
            userData.put("Exp2", button24.getText().toString().replace("Выбранные подспециальности: ", "").trim());
            userData.put("Dopopis", editTextDopopis.getText().toString().trim());

            // Обновление данных в Firestore
            db.collection("app")
                    .document(userId)
                    .set(userData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ProfileMenu.this, "Данные успешно сохранены", Toast.LENGTH_SHORT).show();
                        // Переход в MenuAccount
                        Intent intent = new Intent(ProfileMenu.this, MenuAccount.class);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ProfileMenu", "Ошибка сохранения данных", e);
                        Toast.makeText(ProfileMenu.this, "Ошибка сохранения данных", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    int age = Calendar.getInstance().get(Calendar.YEAR) - year;
                    if (Calendar.getInstance().get(Calendar.MONTH) < month ||
                            (Calendar.getInstance().get(Calendar.MONTH) == month && Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < dayOfMonth)) {
                        age--;
                    }
                    textViewAge.setText(age + " лет");
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showSpecializationDialog() {
        String[] specializations = {"Programming", "Design", "Music"};

        boolean[] checkedItems = new boolean[specializations.length];

        for (int i = 0; i < specializations.length; i++) {
            checkedItems[i] = selectedSpecializations.contains(specializations[i]);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите специальность")
                .setMultiChoiceItems(specializations, checkedItems, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        selectedSpecializations.add(specializations[which]);
                    } else {
                        selectedSpecializations.remove(specializations[which]);
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    if (!selectedSpecializations.isEmpty()) {
                        button23.setText("Выбранные специальности: " + TextUtils.join(", ", selectedSpecializations));
                        // После выбора специальностей вызываем диалог для подспециальностей
                        showSubSpecializationDialog(selectedSpecializations);
                    } else {
                        Toast.makeText(this, "Пожалуйста, выберите хотя бы одну специальность", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Отмена", null);
        builder.show();
    }

    private void showSubSpecializationDialog(List<String> specializations) {
        List<String> allSubSpecializations = new ArrayList<>();

        if (specializations.contains("Programming")) {
            allSubSpecializations.addAll(Arrays.asList("Cybersecurity", "AI Development", "Web Development"));
        }
        if (specializations.contains("Design")) {
            allSubSpecializations.addAll(Arrays.asList("Web Design", "Banner Design", "App Design"));
        }
        if (specializations.contains("Music")) {
            allSubSpecializations.addAll(Arrays.asList("Guitar", "Piano", "Vocals"));
        }

        String[] subSpecializations = allSubSpecializations.toArray(new String[0]);

        boolean[] checkedItems = new boolean[subSpecializations.length];

        for (int i = 0; i < subSpecializations.length; i++) {
            checkedItems[i] = selectedSubSpecializations.contains(subSpecializations[i]);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите подспециальности")
                .setMultiChoiceItems(subSpecializations, checkedItems, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        selectedSubSpecializations.add(subSpecializations[which]);
                    } else {
                        selectedSubSpecializations.remove(subSpecializations[which]);
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    if (!selectedSubSpecializations.isEmpty()) {
                        button24.setText("Выбранные подспециальности: " + TextUtils.join(", ", selectedSubSpecializations));
                    } else {
                        Toast.makeText(this, "Пожалуйста, выберите хотя бы одну подспециальность", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Отмена", null);
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            // Получаем координаты из LocationPickerActivity
            double latitude = data.getDoubleExtra("latitude", 0.0);
            double longitude = data.getDoubleExtra("longitude", 0.0);

            // Используем Geocoder для получения города и страны по координатам
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String city = address.getLocality(); // Город
                    String country = address.getCountryName(); // Страна

                    // Обновляем textView37
                    if (city != null && country != null) {
                        textView37.setText("Город: " + city + ", Страна: " + country);

                        // Сохраняем город и страну в переменные
                        selectedCity = city;
                        selectedCountry = country;
                    } else {
                        textView37.setText("Место не определено");
                    }
                } else {
                    textView37.setText("Место не найдено");
                }
            } catch (IOException e) {
                e.printStackTrace();
                textView37.setText("Ошибка геокодирования");
            }
        }
    }
}