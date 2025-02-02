package com.ratila.findmate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Poisk extends AppCompatActivity {

    private List<String> selectedSearchingSpecializations = new ArrayList<>();
    private List<String> selectedSearchingSubSpecializations = new ArrayList<>();
    private Button button28, button29, button27;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poisk);

        db = FirebaseFirestore.getInstance();

        button28 = findViewById(R.id.button28);
        button29 = findViewById(R.id.button29);
        button27 = findViewById(R.id.button27);

        // Загрузка данных из базы данных
        loadUserData();

        button28.setOnClickListener(v -> showSearchingSpecializationDialog());
        button29.setOnClickListener(v -> showSearchingSubSpecializationDialog(selectedSearchingSpecializations));
        button27.setOnClickListener(v -> saveChangesAndExit());
    }

    private void loadUserData() {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Загрузка данных из Firestore для авторизованного пользователя
        db.collection("app")
                .document(currentUserUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fexp1 = documentSnapshot.getString("Fexp1");
                        String fexp2 = documentSnapshot.getString("Fexp2");

                        // Загрузка данных для кнопок
                        button28.setText(fexp1 != null ? fexp1 : "Select Specializations");
                        button29.setText(fexp2 != null ? fexp2 : "Select Sub-specializations");
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(Poisk.this, "Failed to load user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void showSearchingSpecializationDialog() {
        String[] specializations = {"Programming", "Design", "Music"};

        boolean[] checkedItems = new boolean[specializations.length];

        for (int i = 0; i < specializations.length; i++) {
            checkedItems[i] = selectedSearchingSpecializations.contains(specializations[i]);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Specializations for Searching")
                .setMultiChoiceItems(specializations, checkedItems, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        selectedSearchingSpecializations.add(specializations[which]);
                    } else {
                        selectedSearchingSpecializations.remove(specializations[which]);
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    if (!selectedSearchingSpecializations.isEmpty()) {
                        button28.setText("Selected Specializations: " + TextUtils.join(", ", selectedSearchingSpecializations));
                    } else {
                        Toast.makeText(this, "Please select at least one specialization", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showSearchingSubSpecializationDialog(List<String> specializations) {
        List<String> subSpecializationsList = new ArrayList<>();

        if (specializations.contains("Programming")) {
            subSpecializationsList.addAll(Arrays.asList("Cybersecurity", "AI Development", "Web Development"));
        }
        if (specializations.contains("Design")) {
            subSpecializationsList.addAll(Arrays.asList("Web Design", "Banner Design", "App Design"));
        }
        if (specializations.contains("Music")) {
            subSpecializationsList.addAll(Arrays.asList("Guitar", "Piano", "Vocals"));
        }

        String[] subSpecializations = subSpecializationsList.toArray(new String[0]);

        boolean[] checkedItems = new boolean[subSpecializations.length];

        for (int i = 0; i < subSpecializations.length; i++) {
            checkedItems[i] = selectedSearchingSubSpecializations.contains(subSpecializations[i]);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Sub-specializations for Searching")
                .setMultiChoiceItems(subSpecializations, checkedItems, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        selectedSearchingSubSpecializations.add(subSpecializations[which]);
                    } else {
                        selectedSearchingSubSpecializations.remove(subSpecializations[which]);
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    if (!selectedSearchingSubSpecializations.isEmpty()) {
                        button29.setText("Selected Sub-specializations: " + TextUtils.join(", ", selectedSearchingSubSpecializations));
                    } else {
                        Toast.makeText(this, "Please select at least one sub-specialization", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null);
        builder.show();
    }

    private void saveChangesAndExit() {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Преобразование списков в строки с разделителем ", "
        String fexp1 = TextUtils.join(", ", selectedSearchingSpecializations);
        String fexp2 = TextUtils.join(", ", selectedSearchingSubSpecializations);

        // Сохранение данных обратно в базу данных
        db.collection("app")
                .document(currentUserUid)
                .update(
                        "Fexp1", fexp1,
                        "Fexp2", fexp2
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Poisk.this, "Data updated successfully", Toast.LENGTH_SHORT).show();
                    // Переход в экран профиля
                    startActivity(new Intent(Poisk.this, MenuAccount.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Poisk.this, "Failed to update data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("Poisk", "Failed to update data", e); // Логирование ошибки
                });
    }
}
