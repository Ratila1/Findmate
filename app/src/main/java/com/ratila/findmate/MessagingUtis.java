package com.ratila.findmate;

import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class MessagingUtis {

    // Экземпляр FirebaseMessaging
    private static FirebaseMessaging msgInstance = FirebaseMessaging.getInstance();

    // Статический метод для получения токена
    public static void logToken() {
        // Запрос токена
        msgInstance.getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(Task<String> task) {
                // Проверка на успешность операции
                if (task.isSuccessful()) {
                    // Токен получен успешно
                    String token = task.getResult();
                    if (token != null) {
                        Log.d("FCM token", token);  // Логируем токен
                    } else {
                        Log.d("FCM token", "Token is null");
                    }
                } else {
                    // Ошибка получения токена
                    Log.e("FCM token", "Failed to get token", task.getException());
                }
            }
        });
    }
}
