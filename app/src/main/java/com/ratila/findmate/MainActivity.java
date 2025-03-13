package com.ratila.findmate;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.messaging.FirebaseMessaging;


public class MainActivity extends AppCompatActivity {
    private static final String API_KEY = "AIzaSyBZl9EW-XQ0T4rfXMF3_ZUJb8dykBA0LTM";
    private FirebaseFirestore db;
    private List<Map<String, Object>> profiles = new ArrayList<>();
    private int currentProfileIndex = 0;

    private TextView ageName, textView20, textView27, textView26, textView31, textView36;
    private ImageView imageView7, panel, imageChoose1, imageMess1, imageMenu1, imageView13;
    private static final String TAG = "MainActivity";
    private boolean isExpanded = false; // Флаг состояния imageView7
    private ProgressBar loadingProgressBar;
    private FrameLayout overlay;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestNotificationPermission();
        db = FirebaseFirestore.getInstance();

        ageName = findViewById(R.id.age_name);
        textView20 = findViewById(R.id.textView20);
        textView27 = findViewById(R.id.textView27);
        textView26 = findViewById(R.id.textView26);
        imageView7 = findViewById(R.id.imageView7);
        panel = findViewById(R.id.panel);
        imageChoose1 = findViewById(R.id.imageChoose1);
        imageMess1 = findViewById(R.id.imageMess1);
        imageMenu1 = findViewById(R.id.imageMenu1);
        imageView13 = findViewById(R.id.imageView13); // Здесь инициализируем ваш ImageView
        textView31 = findViewById(R.id.textView31);
        textView36 = findViewById(R.id.textView36);
        panel.setZ(7);
        imageChoose1.setZ(7);
        imageMess1.setZ(7);
        imageMenu1.setZ(7);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        overlay = findViewById(R.id.overlay);
        findViewById(R.id.buttonNo).setOnClickListener(v -> showNextProfile());
        findViewById(R.id.buttonYes).setOnClickListener(v -> {
            likeCurrentProfile();
            showNextProfile();


        });
        showLoading();
        setupMenuListeners();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#2A7A78"));
        }

        // Логика для перемещения imageView7
        imageView7.setOnClickListener(v -> toggleImageViewPosition());

        loadProfiles();
        MessagingUtis.logToken();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("MainActivity", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Получаем токен
                    String token = task.getResult();
                    Log.d("MainActivity", "FCM Token: " + token);

                    // Сохраняем токен в Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    db.collection("users").document(userId).set(new UserToken(token));
                });
    }
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Для Android 13+ требуется специальное разрешение POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Разрешение не предоставлено, запрашиваем его
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            } else {
                // Разрешение уже предоставлено, создаем канал уведомлений
                createNotificationChannel();
            }
        } else {
            // Для версий ниже Android 13 разрешение автоматически предоставлено
            createNotificationChannel();
        }
    }

    /**
     * Создает канал уведомлений для Android Oreo и выше.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "findmate_channel",
                    "FindMate Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Channel for FindMate app notifications");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // Подписываем пользователя на топик уведомлений Firebase
        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Notification", "User subscribed to 'general' topic");
                    } else {
                        Log.e("Notification", "Failed to subscribe user to 'general' topic", task.getException());
                    }
                });
    }

    /**
     * Обрабатывает результат запроса разрешений.
     *
     * @param requestCode  Код запроса.
     * @param permissions  Массив запрошенных разрешений.
     * @param grantResults Результаты запросов разрешений.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение предоставлено, создаем канал уведомлений
                createNotificationChannel();
            } else {
                // Разрешение отклонено, показываем сообщение
                Toast.makeText(this, "Уведомления отключены. Некоторые функции могут быть недоступны.", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void sendRegistrationToServer(String token) {
        // Пример отправки токена на сервер через HTTP-запрос
        new Thread(() -> {
            try {
                URL url = new URL("https://yourserver.com/register-token");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                String postData = "token=" + token;
                OutputStream os = connection.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Server response code: " + responseCode);
            } catch (Exception e) {
                Log.e(TAG, "Error sending token to server", e);
            }
        }).start();
    }
    private void loadProfiles() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User is not authenticated. Please log in again.", Toast.LENGTH_SHORT).show();
            hideLoading(); // Скрываем загрузку, если пользователь не авторизован
            return;
        }

        String currentUserUid = currentUser.getUid();
        Log.d("CurrentUser", "Current user UID: " + currentUserUid);

        // Показываем загрузку перед получением данных
        showLoading();

        // Очистка списка профилей
        profiles.clear();

        // Получаем данные текущего пользователя
        FirebaseFirestore.getInstance().collection("app").document(currentUserUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fexp1 = documentSnapshot.getString("Fexp1");
                        String fexp2 = documentSnapshot.getString("Fexp2");
                        String userDopopis = documentSnapshot.getString("Dopopis");
                        String userCity = documentSnapshot.getString("City");
                        String userCountry = documentSnapshot.getString("Country");

                        // Получаем координаты города пользователя
                        double userLat = getLatitude(userCity, userCountry);
                        double userLon = getLongitude(userCity, userCountry);

                        // Получаем все доступные профили
                        db.collection("app")
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    Log.d("LoadProfiles", "Documents loaded: " + queryDocumentSnapshots.size());

                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        String profileUid = document.getString("UID");
                                        String visibility = document.getString("visibility");

                                        // Проверка видимости профиля
                                        if (visibility != null && visibility.equals("no")) {
                                            continue; // Пропускаем скрытые профили
                                        }

                                        Log.d("LoadProfiles", "Profile UID: " + profileUid);

                                        // Проверяем, что профиль соответствует хотя бы одному из Fexp
                                        String exp1 = document.getString("Exp1");
                                        String exp2 = document.getString("Exp2");

                                        if ((exp1 != null && isSimilar(fexp1, exp1)) || (exp2 != null && isSimilar(fexp2, exp2))) {
                                            if (profileUid != null && !profileUid.equals(currentUserUid)) {
                                                profiles.add(document.getData());
                                                Log.d("LoadProfiles", "Profile added: " + document.getData());
                                            }
                                        }
                                    }

                                    // Сортировка профилей
                                    profiles.sort((p1, p2) -> {
                                        double distance1 = calculateDistance(
                                                userLat,
                                                userLon,
                                                getLatitude(p1.get("City").toString(), p1.get("Country").toString()),
                                                getLongitude(p1.get("City").toString(), p1.get("Country").toString())
                                        );
                                        double distance2 = calculateDistance(
                                                userLat,
                                                userLon,
                                                getLatitude(p2.get("City").toString(), p2.get("Country").toString()),
                                                getLongitude(p2.get("City").toString(), p2.get("Country").toString())
                                        );

                                        int matches1 = countKeywordMatches(userDopopis, p1.get("Dopopis").toString());
                                        int matches2 = countKeywordMatches(userDopopis, p2.get("Dopopis").toString());

                                        // Весовые коэффициенты
                                        double weightDistance = 0.7;
                                        double weightMatches = 0.3;

                                        double score1 = weightDistance * distance1 + weightMatches * (1.0 / (matches1 + 1));
                                        double score2 = weightDistance * distance2 + weightMatches * (1.0 / (matches2 + 1));

                                        return Double.compare(score1, score2);
                                    });

                                    // Отображение первого профиля
                                    if (!profiles.isEmpty()) {
                                        Log.d("LoadProfiles", "Profiles available: " + profiles.size());
                                        displayProfile(currentProfileIndex);
                                        imageView13.setVisibility(View.VISIBLE); // Показываем картинку
                                    } else {
                                        Log.d("LoadProfiles", "No profiles found.");
                                        Toast.makeText(this, "No profiles found.", Toast.LENGTH_SHORT).show();
                                        imageView13.setVisibility(View.INVISIBLE); // Скрываем картинку
                                    }

                                    // Скрываем загрузку после завершения
                                    hideLoading();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("LoadProfilesError", "Error loading profiles: ", e);
                                    Toast.makeText(this, "Failed to load profiles: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    hideLoading(); // Скрываем загрузку при ошибке
                                });
                    } else {
                        Toast.makeText(this, "User's Fexp data not found.", Toast.LENGTH_SHORT).show();
                        hideLoading(); // Скрываем загрузку, если данные пользователя не найдены
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("LoadUserError", "Error loading user Fexp: ", e);
                    Toast.makeText(this, "Failed to load user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    hideLoading(); // Скрываем загрузку при ошибке
                });
    }

    // Метод для показа загрузки
    private void showLoading() {
        if (loadingProgressBar != null && overlay != null) {
            loadingProgressBar.setVisibility(View.VISIBLE);
            overlay.setVisibility(View.VISIBLE);
        }
    }

    // Метод для скрытия загрузки
    private void hideLoading() {
        if (loadingProgressBar != null && overlay != null) {
            loadingProgressBar.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
        }
    }
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Радиус Земли в километрах

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Расстояние в километрах
    }

    public static int countKeywordMatches(String userDescription, String profileDescription) {
        if (userDescription == null || profileDescription == null) return 0;

        // Список стоп-слов
        Set<String> stopWords = new HashSet<>(Arrays.asList(
                "и", "в", "на", "с", "по", "к", "а", "но", "или", "у", "за", "о", "от", "до", "не", "для"
        ));

        // Разделение на слова и приведение к нижнему регистру
        Set<String> userKeywords = new HashSet<>(Arrays.asList(userDescription.toLowerCase().split("\\W+")));
        Set<String> profileKeywords = new HashSet<>(Arrays.asList(profileDescription.toLowerCase().split("\\W+")));

        // Замена синонимов
        userKeywords = replaceSynonyms(userKeywords);
        profileKeywords = replaceSynonyms(profileKeywords);

        // Удаление стоп-слов, цифр и коротких слов
        userKeywords.removeAll(stopWords);
        profileKeywords.removeAll(stopWords);
        userKeywords.removeIf(word -> word.matches("\\d+") || word.length() <= 2);
        profileKeywords.removeIf(word -> word.matches("\\d+") || word.length() <= 2);

        // Поиск совпадений
        userKeywords.retainAll(profileKeywords);
        return userKeywords.size();
    }

    // Метод для замены синонимов
    private static Set<String> replaceSynonyms(Set<String> words) {
        return words.stream()
                .map(word -> Synonyms.getSynonym(word)) // Используем синонимы
                .collect(Collectors.toSet());
    }
    private double getLatitude(String city, String country) {
        try {
            String address = city + "," + country;
            String urlString = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + API_KEY;

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray results = jsonResponse.getJSONArray("results");

            if (results.length() > 0) {
                JSONObject location = results.getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONObject("location");
                return location.getDouble("lat");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching latitude: " + e.getMessage()); // Используем ваш TAG
        }
        return 0.0; // Возвращаем 0.0 в случае ошибки
    }

    // Метод для получения долготы
    private double getLongitude(String city, String country) {
        try {
            String address = city + "," + country;
            String urlString = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + API_KEY;

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray results = jsonResponse.getJSONArray("results");

            if (results.length() > 0) {
                JSONObject location = results.getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONObject("location");
                return location.getDouble("lng");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching longitude: " + e.getMessage()); // Используем ваш TAG
        }
        return 0.0; // Возвращаем 0.0 в случае ошибки
    }
    private boolean isSimilar(String fexp, String exp) {
        if (fexp == null || exp == null) return false;

        // Разделяем строки на отдельные навыки
        String[] fexpSkills = fexp.split(",");
        String[] expSkills = exp.split(",");

        // Преобразуем в множества для упрощения поиска совпадений
        Set<String> fexpSet = new HashSet<>(Arrays.asList(fexpSkills));
        Set<String> expSet = new HashSet<>(Arrays.asList(expSkills));

        // Проверяем, есть ли хотя бы одно совпадение
        for (String skill : fexpSet) {
            if (expSet.contains(skill.trim())) {
                return true; // Если есть хотя бы одно совпадение, возвращаем true
            }
        }

        return false; // Если совпадений нет, возвращаем false
    }
    private void likeCurrentProfile() {
        // Проверка, что есть профиль для лайка
        if (profiles.isEmpty() || currentProfileIndex >= profiles.size()) {
            Toast.makeText(this, "No profile to like.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Получение данных текущего профиля
        Map<String, Object> profile = profiles.get(currentProfileIndex);
        String likedProfileUid = profile.get("UID") != null ? profile.get("UID").toString() : null;
        if (likedProfileUid == null) {
            Toast.makeText(this, "Failed to like profile: UID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Получение UID текущего пользователя
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Failed to like profile: Current user is not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserUid = currentUser.getUid();

        // Указание URL базы данных Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://findmate-9bad1-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference ref = database.getReference();

        // Запись данных в Realtime Database
        ref.child("likes").child(likedProfileUid).child(currentUserUid)
                .setValue(true) // Устанавливаем значение `true` для отметки лайка
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Liked profile!", Toast.LENGTH_SHORT).show();
                    Log.d("LikeAction", "Successfully liked profile UID: " + likedProfileUid);
                })
                .addOnFailureListener(e -> {
                    // Обработка ошибки
                    Toast.makeText(this, "Failed to like profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("LikeActionError", "Error liking profile: ", e);
                });
    }

    private void displayProfile(int index) {
        if (index >= 0 && index < profiles.size()) {
            Map<String, Object> profile = profiles.get(index);
            Log.d("DisplayProfile", "Displaying profile at index " + index + ": " + profile);

            // Отображаем данные анкеты
            String firstName = (String) profile.getOrDefault("FirstName", "Unknown");
            String lastName = (String) profile.getOrDefault("LastName", "Unknown");
            Long age = (Long) profile.getOrDefault("Age", 0L);
            String exp1 = (String) profile.getOrDefault("Exp1", "N/A");
            String exp2 = (String) profile.getOrDefault("Exp2", "N/A");
            String city = (String) profile.getOrDefault("City", "Unknown");
            String country = (String) profile.getOrDefault("Country", "Unknown");
            String profileUid = (String) profile.getOrDefault("UID", "");

            // Проверяем, что анкета соответствует специализациям Fexp пользователя
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String currentUserUid = currentUser.getUid();
                Map<String, Object> currentUserProfile = profiles.stream()
                        .filter(p -> p.get("UID").equals(currentUserUid))
                        .findFirst().orElse(null);
                if (currentUserProfile != null) {
                    String userFexp1 = (String) currentUserProfile.getOrDefault("Fexp1", "");
                    String userFexp2 = (String) currentUserProfile.getOrDefault("Fexp2", "");
                    if (!exp1.equals(userFexp1) && !exp2.equals(userFexp2)) {
                        return; // Пропускаем этот профиль, если специализация не совпадает
                    }
                }
            }

            // Обновляем текстовые поля
            ageName.setText(formatName(firstName, lastName));
            textView20.setText(String.format("Возраст: %s", age > 0 ? age.toString() : "Не указан"));
            textView27.setText(String.format("Специальность: %s, %s", exp1, exp2));
            textView26.setText(String.format("Город: %s, %s", city, country));

            // Меняем картинку в зависимости от UID
            switch (profileUid) {
                case "euRGycsEhSRvrJJsd7wUMMeIJE13":
                    imageView13.setImageResource(R.drawable.pers1); // pers1
                    break;
                case "1RpKsjGG1NeIhCl7yPw8XfFLlFw1":
                    imageView13.setImageResource(R.drawable.pers2); // pers2
                    break;
                case "UBFfYtorXlhpL6zJiuU0adYnGn53":
                    imageView13.setImageResource(R.drawable.pers3); // pers3
                    break;
                case "YktbJkBC14WodkHFLylTApS0IYr1":
                    imageView13.setImageResource(R.drawable.pers4); // pers4
                    break;
                default:
                    imageView13.setVisibility(View.INVISIBLE); // Если UID не совпадает, скрываем картинку
                    break;
            }
        } else {
            Log.d("DisplayProfile", "No profile to display at index: " + index);
            Toast.makeText(this, "No more profiles to show.", Toast.LENGTH_SHORT).show();
        }
    }
    private String formatName(String firstName, String lastName) {
        String fullName = firstName + " " + lastName;
        int maxLength = 15; // Максимальная длина в одной строке
        if (fullName.length() > maxLength) {
            return fullName.replace(" ", "\n"); // Заменяет пробел на перенос строки
        }
        return fullName;
    }




    private void showNextProfile() {
        currentProfileIndex++;
        if (currentProfileIndex < profiles.size()) {
            displayProfile(currentProfileIndex);
        } else {
            Toast.makeText(this, "You've viewed all profiles. Restarting from the beginning.", Toast.LENGTH_SHORT).show();
            currentProfileIndex = 0;
            displayProfile(currentProfileIndex);
        }
    }

    private void setupMenuListeners() {
        findViewById(R.id.imageMenu1).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MenuAccount.class);
            startActivity(intent);
        });
        findViewById(R.id.imageMess1).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Chats.class);
            startActivity(intent);
        });
    }

    private void toggleImageViewPosition() {
        if (isExpanded) {
            // Возвращаем элементы на исходную позицию
            animateView(imageView7, 50);  // Оставляем 50dp для imageView
            animateView(textView20, 50);  // Оставляем 50dp для textView20
            animateView(textView27, 50);  // Оставляем 50dp для textView27
            animateView(textView26, 50);  // Оставляем 50dp для textView26
            animateView(textView31, 50);  // Оставляем 50dp для textView31
            animateView(textView36, 50);
            textView31.setVisibility(View.GONE);
            textView36.setVisibility(View.GONE);
        } else {
            // Поднимаем элементы на 400 пикселей
            animateView(imageView7, -400);
            animateView(textView20, -400);
            animateView(textView27, -400);
            animateView(textView26, -400);
            animateView(textView31, -400);  // Поднимаем textView31
            animateView(textView36, -400);
            // Показываем описание
            textView31.setVisibility(View.VISIBLE);
            textView36.setVisibility(View.VISIBLE);
            textView31.setText(getDopopis());  // Загружаем дополнительное описание
        }

        // Увеличиваем Z-индекс для поднятых элементов
        imageView7.setZ(6);
        textView20.setZ(6);
        textView27.setZ(6);
        textView26.setZ(6);
        textView31.setZ(6);
        textView36.setZ(6);

        isExpanded = !isExpanded;  // Переключаем состояние
    }

    private String getDopopis() {
        // Получение дополнительного описания из текущего профиля
        if (profiles.isEmpty() || currentProfileIndex >= profiles.size()) {
            return "Описание отсутствует";
        }

        // Получаем доп. описание из текущего профиля
        Map<String, Object> profile = profiles.get(currentProfileIndex);
        String dopopis = (String) profile.getOrDefault("Dopopis", "Описание отсутствует");

        // Максимальная длина строки и количество строк
        int maxCharsPerLine = 40;
        int maxLines = 7;

        // Разбиваем текст на слова
        String[] words = dopopis.split(" ");
        StringBuilder wrappedText = new StringBuilder();
        StringBuilder currentLine = new StringBuilder();
        int lineCount = 0;

        for (String word : words) {
            // Проверяем, поместится ли слово в текущую строку
            if (currentLine.length() + word.length() + 1 > maxCharsPerLine) {
                // Если достигнуто максимальное количество строк, добавляем "..."
                if (++lineCount >= maxLines) {
                    wrappedText.append("...");
                    break;
                }
                // Добавляем текущую строку в текст и начинаем новую
                wrappedText.append(currentLine.toString().trim()).append("\n");
                currentLine.setLength(0);
            }
            // Добавляем слово в текущую строку
            currentLine.append(word).append(" ");
        }

        // Добавляем последнюю строку
        if (currentLine.length() > 0 && lineCount < maxLines) {
            wrappedText.append(currentLine.toString().trim());
        }

        return "Дополнительное описание:\n" + wrappedText.toString();
    }

    private void animateView(View view, int newMarginTop) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "translationY", newMarginTop);
        animator.setDuration(300);
        animator.start();
    }
    class UserToken {
        private String token;

        public UserToken(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }
    }
}
