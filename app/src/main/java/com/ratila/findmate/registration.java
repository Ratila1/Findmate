package com.ratila.findmate;

import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.ImageDecoder;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.concurrent.atomic.AtomicInteger;


import android.graphics.Bitmap;

public class registration extends AppCompatActivity {
    private TextView textView2, textView5, textView6, textView3, textView7, textView8, textView10, textView12, textView13,  textView28, textView14, textView15, textView9, textView11, textView16, textView17, textView18, textView19, textView22, textView23, textView24;
    private ImageView imageView8, imageView9, imageView10, imageView11, imageViewProfile, imageViewProfile2, imageViewProfile3, imageViewProfile4, imageViewProfile5, imageViewProfile6, imageView112, imageView12;
    private EditText editTextTextPassword2, editTextTextPassword, editTextEmail, editTextText, editTextText3, editTextDescription;
    private Button buttonNext1, buttonBack, buttonOpenCalendar, button, button3, button4, button5, button6, button7, button8, buttonSelectImage, buttonSelectImage2, button30;
    private CalendarView calendarDialog;
    private int currentGroup = 0;
    private String selectedDate = "";
    private String selectedCountry = "";
    private String selectedCity = "";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int MAX_IMAGES = 3;
    private ArrayList<Uri> imageUris = new ArrayList<>();
    private static final int PICK_IMAGE_REQUEST_2 = 2;
    private ArrayList<Uri> imageUris2 = new ArrayList<>();
    private FirebaseAuth mAuth;
    private Map<String, String[]> countryCitiesMap;
    private List<String> selectedSpecializations = new ArrayList<>();
    private List<String> selectedSearchingSpecializations = new ArrayList<>();
    private List<String> selectedSubSpecializations = new ArrayList<>();
    private List<String> selectedSearchingSubSpecializations = new ArrayList<>();
    private Date birthDate;
    private FirebaseFirestore db;
    private String password;
    private SelectedLocation selectedLocation;
    private boolean isPasswordValid(String password) {
        String passwordPattern = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$";
        return password.matches(passwordPattern);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_registration);
        buttonSelectImage = findViewById(R.id.buttonSelectImage);
        buttonSelectImage2 = findViewById(R.id.buttonSelectImage2);
        editTextDescription = findViewById(R.id.editTextDescription);
        textView2 = findViewById(R.id.textView2);
        textView5 = findViewById(R.id.textView5);
        textView6 = findViewById(R.id.textView6);
        textView3 = findViewById(R.id.textView3);
        textView7 = findViewById(R.id.textView7);
        textView8 = findViewById(R.id.textView8);
        textView12 = findViewById(R.id.textView12);
        textView13 = findViewById(R.id.textView13);
        textView14 = findViewById(R.id.textView14);
        textView15 = findViewById(R.id.textView15);
        textView10 = findViewById(R.id.textView10);
        textView9 = findViewById(R.id.textView9);
        textView11 = findViewById(R.id.textView11);
        imageView8 = findViewById(R.id.imageView4);
        imageView9 = findViewById(R.id.imageView5);
        imageView10 = findViewById(R.id.imageView9);
        imageView11 = findViewById(R.id.imageView10);
        imageViewProfile5 = findViewById(R.id.imageViewProfile5);
        imageViewProfile4 = findViewById(R.id.imageViewProfile4);
        imageViewProfile6 = findViewById(R.id.imageViewProfile6);
        editTextTextPassword2 = findViewById(R.id.editTextTextPassword2);
        editTextTextPassword = findViewById(R.id.editTextTextPassword);
        editTextEmail = findViewById(R.id.editTextText2);  // Added EditText for email
        buttonNext1 = findViewById(R.id.button2);
        buttonBack = findViewById(R.id.buttonBack); // Back Button
        editTextText = findViewById(R.id.editTextText);
        editTextText3 = findViewById(R.id.editTextText3);
        buttonOpenCalendar = findViewById(R.id.buttonOpenCalendar);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);
        imageViewProfile = findViewById(R.id.imageViewProfile1);
        imageViewProfile2 = findViewById(R.id.imageViewProfile2);
        imageViewProfile3 = findViewById(R.id.imageViewProfile3);
        textView16 = findViewById(R.id.textView16);
        textView17 = findViewById(R.id.textView17);
        textView18 = findViewById(R.id.textView18);
        imageView112 = findViewById(R.id.imageView11);
        imageView12 = findViewById(R.id.imageView12);
        textView19 = findViewById(R.id.textView19);
        textView22 = findViewById(R.id.textView22);
        textView23 = findViewById(R.id.textView23);
        textView24 = findViewById(R.id.textView24);
        textView28 = findViewById(R.id.textView28);
        String email = getIntent().getStringExtra("email");
        buttonSelectImage.setOnClickListener(v -> openImageChooser());
        buttonSelectImage2.setOnClickListener(v -> openImageChooser2());
        button30 = findViewById(R.id.button30);
        if (email != null) {
            editTextEmail.setText(email);
        }
        button5.setOnClickListener(v -> {
            showSpecializationDialog();
        });
        button4.setOnClickListener(v -> {
            if (selectedSpecializations.isEmpty()) {
                Toast.makeText(this, "Please select a specialization first", Toast.LENGTH_SHORT).show();
            } else {
                showSubSpecializationDialog(selectedSpecializations);
            }
        });
        button6.setOnClickListener(v -> {
            showSearchingSpecializationDialog();
        });
        button7.setOnClickListener(v -> {
            if (selectedSearchingSpecializations.isEmpty()) {
                Toast.makeText(this, "Please select a specialization for searching first", Toast.LENGTH_SHORT).show();
            } else {
                showSearchingSubSpecializationDialog(selectedSearchingSpecializations);
            }
        });
        editTextEmail.addTextChangedListener(new TextValidator());
        editTextTextPassword.addTextChangedListener(new TextValidator());
        editTextTextPassword2.addTextChangedListener(new TextValidator());
        buttonNext1.setOnClickListener(v -> {
            if (currentGroup == 0) {
                String emailInput = editTextEmail.getText().toString();
                password = editTextTextPassword.getText().toString(); // Используем глобальную переменную
                String confirmPassword = editTextTextPassword2.getText().toString();

                textView28.setText("");

                if (emailInput.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    textView28.setText("Please fill in all fields.");
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    textView28.setText("Passwords do not match.");
                    return;
                }

                if (!isPasswordValid(password)) {
                    textView28.setText("Password must be at least 8 characters long, contain at least one Latin letter and one digit.");
                    return;
                }

                registerUser(emailInput, password);

                currentGroup++;
                updateRegistrationStep();
            } else if (currentGroup == 1) {
                String textInput1 = editTextText.getText().toString();
                String textInput2 = editTextText3.getText().toString();

                textView28.setText("");

                if (textInput1.isEmpty() || textInput2.isEmpty()) {
                    textView28.setText("Please fill in all required fields.");
                    return;
                }

                if (!textInput1.matches("^[a-zA-Zа-яА-Я]+$") || !textInput2.matches("^[a-zA-Zа-яА-Я]+$") ) {
                    textView28.setText("Name and surname must not contain numbers or special characters.");
                    return;
                }

                if (selectedDate.isEmpty()) {
                    textView28.setText("Please select a date before proceeding.");
                    return;
                }

                currentGroup++;
                updateRegistrationStep();
            } else if (currentGroup == 2) {
                textView28.setText("");

                // Проверка, что selectedCountry и selectedCity не пустые
                if (selectedCountry.isEmpty() || selectedCity.isEmpty()) {
                    textView28.setText("Please select both country and city.");
                    return;
                }

                if (selectedSpecializations.isEmpty()) {
                    textView28.setText("Please select at least one specialization.");
                    return;
                }

                currentGroup++;
                updateRegistrationStep();
            } else if (currentGroup == 3) {
                textView28.setText("");

                if (selectedSearchingSpecializations.isEmpty()) {
                    textView28.setText("Please select at least one specialization for searching.");
                    return;
                }

                currentGroup++;
                updateRegistrationStep();
            } else if (currentGroup == 4) {
                textView28.setText("");
                uploadProfileToFirestore(birthDate);
                if (imageUris.isEmpty() && imageUris2.isEmpty()) {
                    textView28.setText("Please upload at least one image before proceeding.");
                    return;
                }
                loadMatchingProfilesCount();
                currentGroup++;
                updateRegistrationStep();
            } else if (currentGroup == 5) {

                Intent intent = new Intent(registration.this, MainActivity.class);
                startActivity(intent);
                finish();
                textView28.setText("Registration completed!");
            } else {
                textView28.setText("You are not in the correct step.");
            }
        });
        buttonBack.setOnClickListener(v -> {
            if (currentGroup == 1) {
                fadeOutGroup1();  // Скрываем элементы текущей группы
                currentGroup--;  // Переходим на предыдущую группу
                updateRegistrationStep();  // Обновляем отображение
            } else if (currentGroup == 2) {
                fadeOutGroup2();
                currentGroup--;
                updateRegistrationStep();
            } else if (currentGroup == 3) {
                fadeOutGroup3();
                currentGroup--;
                updateRegistrationStep();
            } else if (currentGroup == 4) {
                fadeOutGroup4();
                currentGroup--;
                updateRegistrationStep();
            } else if (currentGroup == 5) {
                fadeOutGroup5();
                currentGroup--;
                updateRegistrationStep();
            } else {
                textView28.setText("You are already at the first step.");
            }
        });
        buttonOpenCalendar.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        // Сохраняем выбранную дату в формате "день/месяц/год"
                        selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;

                        // Отображаем выбранную дату на экране
                        TextView textView10 = findViewById(R.id.textView10);
                        textView10.setText("Выбранная дата: " + selectedDate);

                        // Преобразуем строку даты в объект Date
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        try {
                            // Преобразуем строку в объект Date и сохраняем в переменную birthDate
                            birthDate = sdf.parse(selectedDate); // Сохраняем дату в переменную
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Неверный формат даты", Toast.LENGTH_SHORT).show();
                        }
                    },
                    2000, 0, 1 // Год, месяц, день по умолчанию
            );
            datePickerDialog.show();
        });
        button30.setOnClickListener(v -> {
            Intent intent = new Intent(registration.this, LocationPickerActivity.class);
            startActivityForResult(intent, 100);
        });

    };
    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Лог успешной регистрации
                        Log.d("RegisterUser", "User registered successfully.");

                        // Переход к следующему шагу
                        updateRegistrationStep();
                    } else {
                        // Если регистрация не удалась
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Log.e("RegisterUser", "Registration failed: " + errorMessage);
                        Toast.makeText(registration.this, "Registration Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void showSpecializationDialog() {
        String[] specializations = {"Programming", "Design", "Music"};

        // Массив для текущего состояния выбора
        boolean[] checkedItems = new boolean[specializations.length];

        // Заполняем массив checkedItems на основе текущего состояния selectedSpecializations
        for (int i = 0; i < specializations.length; i++) {
            checkedItems[i] = selectedSpecializations.contains(specializations[i]);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Specialization")
                .setMultiChoiceItems(specializations, checkedItems, (dialog, which, isChecked) -> {
                    // Сохраняем или удаляем выбранные специализации
                    if (isChecked) {
                        selectedSpecializations.add(specializations[which]);
                    } else {
                        selectedSpecializations.remove(specializations[which]);
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    if (!selectedSpecializations.isEmpty()) {
                        button5.setText("Selected Specializations: " + TextUtils.join(", ", selectedSpecializations));
                        // После выбора специализаций, открываем диалог для подвидов
                        showSubSpecializationDialog(selectedSpecializations);
                    } else {
                        Toast.makeText(this, "Please select at least one specialization", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null);
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
        builder.setTitle("Select Sub-specialization")
                .setMultiChoiceItems(subSpecializations, checkedItems, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        selectedSubSpecializations.add(subSpecializations[which]);
                    } else {
                        selectedSubSpecializations.remove(subSpecializations[which]);
                    }
                })
                .setPositiveButton("OK", (dialog, which) -> {
                    if (!selectedSubSpecializations.isEmpty()) {
                        button4.setText("Selected Sub-specializations: " + TextUtils.join(", ", selectedSubSpecializations));
                    } else {
                        Toast.makeText(this, "Please select at least one sub-specialization", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null);
        builder.show();
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
                        button6.setText("Selected Specializations: " + TextUtils.join(", ", selectedSearchingSpecializations));
                        showSearchingSubSpecializationDialog(selectedSearchingSpecializations);
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
                        button7.setText("Selected Sub-specializations: " + TextUtils.join(", ", selectedSearchingSubSpecializations));
                    } else {
                        Toast.makeText(this, "Please select at least one sub-specialization", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null);
        builder.show();
    }
    private void openImageChooser() {
        if (imageUris.size() < MAX_IMAGES) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Разрешить выбор нескольких файлов
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), PICK_IMAGE_REQUEST);
        } else {
            Toast.makeText(this, "Вы можете загрузить только 3 изображения", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateImageViews() {
        try {
            if (imageUris.size() > 0) {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUris.get(0));
                imageViewProfile.setImageBitmap(bitmap);
            }
            if (imageUris.size() > 1) {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUris.get(1));
                imageViewProfile2.setImageBitmap(bitmap);
            }
            if (imageUris.size() > 2) {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUris.get(2));
                imageViewProfile3.setImageBitmap(bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void openImageChooser2() {
        if (imageUris2.size() < MAX_IMAGES) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), PICK_IMAGE_REQUEST_2);
        } else {
            Toast.makeText(this, "Вы можете загрузить только 3 изображения", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateImageViews2() {
        try {
            if (imageUris2.size() > 0) {
                Uri uri = imageUris2.get(0);
                Bitmap bitmap = decodeUriToBitmap(uri);
                imageViewProfile4.setImageBitmap(bitmap);
            }
            if (imageUris2.size() > 1) {
                Uri uri = imageUris2.get(1);
                Bitmap bitmap = decodeUriToBitmap(uri);
                imageViewProfile5.setImageBitmap(bitmap);
            }
            if (imageUris2.size() > 2) {
                Uri uri = imageUris2.get(2);
                Bitmap bitmap = decodeUriToBitmap(uri);
                imageViewProfile6.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            Log.e("ImageChooser", "Error updating image views", e);
            Toast.makeText(this, "Ошибка загрузки изображения: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private Bitmap decodeUriToBitmap(Uri uri) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uri);
            return ImageDecoder.decodeBitmap(source);
        } else {
            return MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        }
    }
    private boolean isValidUri(Uri uri) {
        try (InputStream stream = getContentResolver().openInputStream(uri)) {
            return stream != null;
        } catch (Exception e) {
            return false;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            double latitude = data.getDoubleExtra("latitude", 0.0);
            double longitude = data.getDoubleExtra("longitude", 0.0);

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                // Получаем список адресов
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);

                    // Извлекаем город и страну
                    String city = address.getLocality();
                    String country = address.getCountryName();

                    // Обновляем переменные selectedCountry и selectedCity
                    selectedCountry = country;
                    selectedCity = city;

                    // Сохраняем данные в объекте SelectedLocation
                    selectedLocation = new SelectedLocation(latitude, longitude, city, country);

                    // Обновляем текст в textView13
                    if (city != null && country != null) {
                        textView13.setText("Ваш адрес: " + city + ", " + country);
                    } else {
                        textView13.setText("Место не определено");
                    }
                } else {
                    textView13.setText("Место не найдено");
                }
            } catch (IOException e) {
                e.printStackTrace();
                textView13.setText("Ошибка геокодирования");
            }
        }

        // Остальная логика обработки изображений
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();
                int count = clipData.getItemCount();
                for (int i = 0; i < count && imageUris.size() < MAX_IMAGES; i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    if (imageUri != null && isValidUri(imageUri)) {
                        imageUris.add(imageUri);
                    }
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                if (imageUri != null && isValidUri(imageUri) && imageUris.size() < MAX_IMAGES) {
                    imageUris.add(imageUri);
                }
            }
            updateImageViews(); // Обновляем только ImageView для imageUris
        } else if (requestCode == PICK_IMAGE_REQUEST_2 && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();
                int count = clipData.getItemCount();
                for (int i = 0; i < count && imageUris2.size() < MAX_IMAGES; i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    if (imageUri != null && isValidUri(imageUri)) {
                        imageUris2.add(imageUri);
                    }
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                if (imageUri != null && isValidUri(imageUri) && imageUris2.size() < MAX_IMAGES) {
                    imageUris2.add(imageUri);
                }
            }
            updateImageViews2(); // Обновляем только ImageView для imageUris2
        }
    }

    private void updateRegistrationStep() {
        switch (currentGroup) {
            case 0:
                fadeInGroup0();
                break;
            case 1:
                fadeOutGroup0();
                fadeInGroup1();
                break;
            case 2:
                fadeOutGroup1();
                fadeInGroup2();
                break;
            case 3:
                fadeOutGroup2();
                fadeInGroup3();
                break;
            case 4:
                fadeOutGroup3();
                fadeInGroup4();
                break;
            case 5:
                fadeOutGroup4();
                fadeInGroup5();
                break;
            default:
                break;
        }
    }
    private void fadeInGroup0() {
        editTextTextPassword.setVisibility(View.VISIBLE);
        editTextTextPassword2.setVisibility(View.VISIBLE);

        editTextTextPassword.animate().alpha(1).setDuration(500);
        editTextTextPassword2.animate().alpha(1).setDuration(500);

        textView5.setVisibility(View.VISIBLE);
        textView6.setVisibility(View.VISIBLE);
        imageView8.setVisibility(View.VISIBLE);
        imageView9.setVisibility(View.VISIBLE);

        textView5.animate().alpha(1).setDuration(500);
        textView6.animate().alpha(1).setDuration(500);
        imageView8.animate().alpha(1).setDuration(500);
        imageView9.animate().alpha(1).setDuration(500);
    }
    private void fadeOutGroup0() {
        editTextEmail.setVisibility(View.GONE);
        editTextTextPassword.animate().alpha(0).setDuration(500).withEndAction(() -> editTextTextPassword.setVisibility(View.GONE));
        editTextTextPassword2.animate().alpha(0).setDuration(500).withEndAction(() -> editTextTextPassword2.setVisibility(View.GONE));
        textView5.animate().alpha(0).setDuration(500).withEndAction(() -> textView5.setVisibility(View.GONE));
        textView6.animate().alpha(0).setDuration(500).withEndAction(() -> textView6.setVisibility(View.GONE));
        imageView8.animate().alpha(0).setDuration(500).withEndAction(() -> imageView8.setVisibility(View.GONE));
        imageView9.animate().alpha(0).setDuration(500).withEndAction(() -> imageView9.setVisibility(View.GONE));
    }
    private void fadeInGroup1() {
        buttonOpenCalendar.setVisibility(View.VISIBLE);
        textView3.setVisibility(View.VISIBLE);
        textView7.setVisibility(View.VISIBLE);
        textView8.setVisibility(View.VISIBLE);
        textView10.setVisibility(View.VISIBLE);
        imageView10.setVisibility(View.VISIBLE);
        imageView11.setVisibility(View.VISIBLE);
        editTextText3.setVisibility(View.VISIBLE);
        editTextText.setVisibility(View.VISIBLE);
        buttonOpenCalendar.animate().alpha(1).setDuration(500);
        textView3.animate().alpha(1).setDuration(500);
        textView7.animate().alpha(1).setDuration(500);
        textView8.animate().alpha(1).setDuration(500);
        textView10.animate().alpha(1).setDuration(500);
        imageView10.animate().alpha(1).setDuration(500);
        imageView11.animate().alpha(1).setDuration(500);
        editTextText.animate().alpha(1).setDuration(500);
        editTextText3.animate().alpha(1).setDuration(500);
    }
    private void fadeOutGroup1() {
        textView3.animate().alpha(0).setDuration(500).withEndAction(() -> textView3.setVisibility(View.GONE));
        textView7.animate().alpha(0).setDuration(500).withEndAction(() -> textView7.setVisibility(View.GONE));
        textView8.animate().alpha(0).setDuration(500).withEndAction(() -> textView8.setVisibility(View.GONE));
        textView10.animate().alpha(0).setDuration(500).withEndAction(() -> textView10.setVisibility(View.GONE));
        buttonOpenCalendar.animate().alpha(0).setDuration(500).withEndAction(() -> buttonOpenCalendar.setVisibility(View.GONE));
        imageView10.animate().alpha(0).setDuration(500).withEndAction(() -> imageView10.setVisibility(View.GONE));
        imageView11.animate().alpha(0).setDuration(500).withEndAction(() -> imageView11.setVisibility(View.GONE));
        editTextText.animate().alpha(0).setDuration(500).withEndAction(() -> editTextText.setVisibility(View.GONE));
        editTextText3.animate().alpha(0).setDuration(500).withEndAction(() -> editTextText3.setVisibility(View.GONE));
    }
    private void fadeInGroup2() {
        textView12.setVisibility(View.VISIBLE);
        textView13.setVisibility(View.VISIBLE);
        textView14.setVisibility(View.VISIBLE);
        textView15.setVisibility(View.VISIBLE);
        button4.setVisibility(View.VISIBLE);
        button5.setVisibility(View.VISIBLE);
        button30.setVisibility(View.VISIBLE);
        textView12.animate().alpha(1).setDuration(500);
        textView13.animate().alpha(1).setDuration(500);
        textView14.animate().alpha(1).setDuration(500);
        textView15.animate().alpha(1).setDuration(500);
        button4.animate().alpha(1).setDuration(500);
        button5.animate().alpha(1).setDuration(500);
        button30.animate().alpha(1).setDuration(500);
    }
    private void fadeOutGroup2() {
        textView12.animate().alpha(0).setDuration(500).withEndAction(() -> textView12.setVisibility(View.GONE));
        textView13.animate().alpha(0).setDuration(500).withEndAction(() -> textView13.setVisibility(View.GONE));
        textView14.animate().alpha(0).setDuration(500).withEndAction(() -> textView14.setVisibility(View.GONE));
        textView15.animate().alpha(0).setDuration(500).withEndAction(() -> textView15.setVisibility(View.GONE));
        button4.animate().alpha(0).setDuration(500).withEndAction(() -> button4.setVisibility(View.GONE));
        button5.animate().alpha(0).setDuration(500).withEndAction(() -> button5.setVisibility(View.GONE));
        button30.animate().alpha(0).setDuration(500).withEndAction(() -> button30.setVisibility(View.GONE));
    }
    private void fadeInGroup3() {
        textView9.setVisibility(View.VISIBLE);
        textView11.setVisibility(View.VISIBLE);
        button6.setVisibility(View.VISIBLE);
        button7.setVisibility(View.VISIBLE);
        textView9.animate().alpha(1).setDuration(500);
        textView11.animate().alpha(1).setDuration(500);
        button6.animate().alpha(1).setDuration(500);
        button7.animate().alpha(1).setDuration(500);
    }
    private void fadeOutGroup3() {
        textView9.animate().alpha(0).setDuration(500).withEndAction(() -> textView9.setVisibility(View.GONE));
        textView11.animate().alpha(0).setDuration(500).withEndAction(() -> textView11.setVisibility(View.GONE));
        button6.animate().alpha(0).setDuration(500).withEndAction(() -> button6.setVisibility(View.GONE));
        button7.animate().alpha(0).setDuration(500).withEndAction(() -> button7.setVisibility(View.GONE));
    }
    private void fadeInGroup4() {
        imageViewProfile.setVisibility(View.VISIBLE);
        imageViewProfile2.setVisibility(View.VISIBLE);
        imageViewProfile3.setVisibility(View.VISIBLE);
        imageViewProfile4.setVisibility(View.VISIBLE);
        imageViewProfile5.setVisibility(View.VISIBLE);
        imageViewProfile6.setVisibility(View.VISIBLE);
        buttonSelectImage.setVisibility(View.VISIBLE);
        buttonSelectImage2.setVisibility(View.VISIBLE);
        editTextDescription.setVisibility(View.VISIBLE);
        textView16.setVisibility(View.VISIBLE);
        textView17.setVisibility(View.VISIBLE);
        textView18.setVisibility(View.VISIBLE);
        imageView112.setVisibility(View.VISIBLE);
        imageViewProfile.animate().alpha(1).setDuration(500);
        imageViewProfile2.animate().alpha(1).setDuration(500);
        imageViewProfile3.animate().alpha(1).setDuration(500);
        imageViewProfile4.animate().alpha(1).setDuration(500);
        imageViewProfile5.animate().alpha(1).setDuration(500);
        imageViewProfile6.animate().alpha(1).setDuration(500);
        buttonSelectImage.animate().alpha(1).setDuration(500);
        buttonSelectImage2.animate().alpha(1).setDuration(500);
        editTextDescription.animate().alpha(1).setDuration(500);
        textView16.animate().alpha(1).setDuration(500);
        textView17.animate().alpha(1).setDuration(500);
        textView18.animate().alpha(1).setDuration(500);
        imageView112.animate().alpha(1).setDuration(500);
    }
    private void fadeOutGroup4() {
        imageViewProfile.animate().alpha(0).setDuration(500).withEndAction(() -> textView12.setVisibility(View.GONE));
        imageViewProfile2.animate().alpha(0).setDuration(500).withEndAction(() -> textView12.setVisibility(View.GONE));
        imageViewProfile3.animate().alpha(0).setDuration(500).withEndAction(() -> textView12.setVisibility(View.GONE));
        imageViewProfile4.animate().alpha(0).setDuration(500).withEndAction(() -> textView12.setVisibility(View.GONE));
        imageViewProfile5.animate().alpha(0).setDuration(500).withEndAction(() -> textView12.setVisibility(View.GONE));
        imageViewProfile6.animate().alpha(0).setDuration(500).withEndAction(() -> textView12.setVisibility(View.GONE));
        buttonSelectImage.animate().alpha(0).setDuration(500).withEndAction(() -> textView12.setVisibility(View.GONE));
        buttonSelectImage2.animate().alpha(0).setDuration(500).withEndAction(() -> textView12.setVisibility(View.GONE));
        editTextDescription.animate().alpha(0).setDuration(500).withEndAction(() -> textView12.setVisibility(View.GONE));
        textView16.animate().alpha(0).setDuration(500).withEndAction(() -> textView12.setVisibility(View.GONE));
        textView17.animate().alpha(0).setDuration(500).withEndAction(() -> textView12.setVisibility(View.GONE));
        textView18.animate().alpha(0).setDuration(500).withEndAction(() -> textView12.setVisibility(View.GONE));
        imageView112.animate().alpha(0).setDuration(500).withEndAction(() -> textView12.setVisibility(View.GONE));
        textView2.animate().alpha(0).setDuration(500).withEndAction(() -> textView12.setVisibility(View.GONE));

    }
    private void fadeInGroup5() {
        textView19.setVisibility(View.VISIBLE);
        textView22.setVisibility(View.VISIBLE);
        textView23.setVisibility(View.VISIBLE);
        textView24.setVisibility(View.VISIBLE);
        textView19.animate().alpha(1).setDuration(500);
        textView22.animate().alpha(1).setDuration(500);
        textView23.animate().alpha(1).setDuration(500);
        textView24.animate().alpha(1).setDuration(500);

    }
    private void fadeOutGroup5() {
        textView19.animate().alpha(0).setDuration(500).withEndAction(() -> textView12.setVisibility(View.GONE));
        textView22.animate().alpha(0).setDuration(500).withEndAction(() -> textView12.setVisibility(View.GONE));
        textView23.animate().alpha(0).setDuration(500).withEndAction(() -> textView12.setVisibility(View.GONE));
        textView24.animate().alpha(0).setDuration(500).withEndAction(() -> textView12.setVisibility(View.GONE));
    }
    private class TextValidator implements android.text.TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(android.text.Editable editable) {}
    }
    private int calculateAge(Date birthDate) {
        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTime(birthDate);

        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);

        if (today.get(Calendar.MONTH) < birthCalendar.get(Calendar.MONTH) ||
                (today.get(Calendar.MONTH) == birthCalendar.get(Calendar.MONTH) && today.get(Calendar.DAY_OF_MONTH) < birthCalendar.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }

        return age;
    }
    private void uploadProfileToFirestore(Date birthDate) {
        Log.d("FirestoreUpload", "uploadProfileToFirestore called");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Log.e("FirestoreUpload", "User is not authenticated. Cannot upload profile.");
            Toast.makeText(this, "User is not authenticated. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Получение UID текущего пользователя
        String uid = currentUser.getUid();
        Log.d("FirestoreUpload", "Authenticated user UID: " + uid);

        // Данные профиля
        Map<String, Object> profileData = new HashMap<>();

        // Добавление UID пользователя
        profileData.put("UID", uid);

        // Проверка на наличие выбранного местоположения
        if (selectedLocation != null) {
            profileData.put("City", selectedLocation.getCity() != null ? selectedLocation.getCity() : "");
            profileData.put("Country", selectedLocation.getCountry() != null ? selectedLocation.getCountry() : "");
            profileData.put("Coordinates",
                    selectedLocation.getLatitude() + ", " + selectedLocation.getLongitude()); // Хранение координат в виде строки
        } else {
            profileData.put("City", "");
            profileData.put("Country", "");
            profileData.put("Coordinates", "");
        }

        profileData.put("Dopopis", editTextDescription.getText().toString());

        if (!selectedSpecializations.isEmpty() && !selectedSubSpecializations.isEmpty()) {
            profileData.put("Exp1", selectedSpecializations.get(0));
            profileData.put("Exp2", selectedSubSpecializations.get(0));
        } else {
            profileData.put("Exp1", "");
            profileData.put("Exp2", "");
        }

        if (!selectedSearchingSpecializations.isEmpty() && !selectedSearchingSubSpecializations.isEmpty()) {
            profileData.put("Fexp1", selectedSearchingSpecializations.get(0));
            profileData.put("Fexp2", selectedSearchingSubSpecializations.get(0));
        } else {
            profileData.put("Fexp1", "");
            profileData.put("Fexp2", "");
        }

        profileData.put("FirstName", editTextText.getText().toString());
        profileData.put("LastName", editTextText3.getText().toString());
        profileData.put("Age", calculateAge(birthDate));

        Log.d("FirestoreUpload", "Profile data: " + profileData);

        // Использование UID как документа в коллекции "app"
        db.collection("app")
                .document(uid) // Использование UID в качестве идентификатора документа
                .set(profileData) // Установка данных профиля
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirestoreUpload", "Profile successfully uploaded for UID: " + uid);
                    Toast.makeText(this, "Profile successfully uploaded!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreUpload", "Failed to upload profile: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"));
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to upload profile: " + (e.getMessage() != null ? e.getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
                });
    }
    private void loadMatchingProfilesCount() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User is not authenticated. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserUid = currentUser.getUid();
        Log.d("CurrentUser", "Current user UID: " + currentUserUid);

        // Получаем Fexp текущего пользователя
        db.collection("app").document(currentUserUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String fexp1 = documentSnapshot.getString("Fexp1");
                        String fexp2 = documentSnapshot.getString("Fexp2");

                        // Используем массив для хранения счетчика
                        final int[] matchingProfilesCount = {0};

                        // Получаем все доступные профили
                        db.collection("app")
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        String profileUid = document.getId(); // UID текущей анкеты
                                        String exp1 = document.getString("Exp1");
                                        String exp2 = document.getString("Exp2");
                                        String visibility = document.getString("visibility");

                                        // Пропускаем скрытые профили
                                        if (visibility != null && visibility.equals("no")) {
                                            continue;
                                        }

                                        // Исключаем текущего пользователя
                                        if (profileUid.equals(currentUserUid)) {
                                            continue;
                                        }

                                        // Проверка соответствия Fexp
                                        if ((exp1 != null && isSimilar(fexp1, exp1)) || (fexp1 == null)) {
                                            if ((exp2 != null && isSimilar(fexp2, exp2)) || (fexp2 == null)) {
                                                matchingProfilesCount[0]++; // Увеличиваем счетчик
                                            }
                                        }
                                    }

                                    // Отображаем количество найденных анкет
                                    textView23.setText("Мы нашли для вас " + matchingProfilesCount[0] + " анкет. Посмотрите их!");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("LoadProfilesError", "Error loading profiles: ", e);
                                    Toast.makeText(this, "Failed to load profiles: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "User's Fexp data not found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("LoadUserError", "Error loading user Fexp: ", e);
                    Toast.makeText(this, "Failed to load user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private boolean isSimilar(String exp1, String fexp1) {
        return exp1.equalsIgnoreCase(fexp1); // Пример простого сравнения
    }
    }

