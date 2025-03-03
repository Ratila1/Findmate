# Findmate

## Описание
Findmate — это мобильное приложение для Android, которое помогает людям находить единомышленников и создавать команды для различных проектов. Независимо от того, хотите ли вы воплотить идею, изобрести что-то новое или создать музыкальный проект — Findmate поможет вам найти людей, с которыми можно работать и развивать идеи.

### Цель проекта
Findmate предоставляет удобный инструмент для поиска единомышленников, которые могут помочь в реализации ваших идей. Если работать в одиночку слишком сложно, приложение позволяет вам легко найти команду для совместной работы.

### Технологии
Проект использует следующие технологии:
- **Java** — основной язык программирования.
- **Android Studio** — среда разработки для создания Android-приложений.
- **Firebase** — для аутентификации пользователей, базы данных, хранения файлов и сообщений.
- **Google Cloud Console** — для управления облачными сервисами.

## Установка
Для установки приложения скачайте APK файл из раздела Релизы.
1. Перейдите на страницу релиза.
2. Скачайте APK файл.
3. Установите его на своём устройстве Android.

## Использование
- **Доработка кода и улучшения**: Вы можете дорабатывать и оптимизировать код для собственного использования. Часть кода можно взять для своих проектов, но использование всего кода для коммерческих целей запрещено.
- **Алгоритмы подбора анкет**: В приложении реализованы алгоритмы для поиска пользователей, которые могут стать вашими единомышленниками.
- **Мессенджер**: Встроенный мессенджер позволяет общаться с другими пользователями приложения.
- **Редактирование анкеты**: Легко обновляйте свои данные и анкеты.
## Возможности
- **Загрузка фотографий на сервер.
- **Развитие мессенджера: Возможности для улучшения обмена сообщениями.
- **Кастомизация приложения: Темы, языки и настройки.
## Лицензия
Проект не имеет официальной лицензии, но вы можете свободно использовать его для личных целей, с учётом условий использования.

## Контакты
- ** Если у вас есть вопросы, предложения или вы хотите внести вклад в проект, свяжитесь со мной через:

1. Telegram: @rat1la
2. Instagram: @ratilaok

## Правила использования
Важно: Люди, которые скачивают и используют данный проект, автоматически соглашаются с следующими условиями:
- **Запрет на перепродажу приложения: Вы не можете скачивать это приложение и выдавать его за свое. Приложение предназначено для личного использования и не может быть распространено или продано без явного разрешения автора.
- **Ограничение коммерческого использования кода: Использование кода из этого проекта в коммерческих целях строго запрещено. Если вы хотите использовать какие-либо части кода для своих проектов, убедитесь, что это не приведет к коммерческому использованию без соответствующего разрешения.
Скачивая и используя данный проект, вы подтверждаете свое согласие с этими условиями. Нарушение данных правил может привести к юридическим последствиям.
### Зависимости

В проекте используются следующие библиотеки:

```gradle
implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
implementation(libs.appcompat)
implementation(libs.material)
implementation(libs.activity)
implementation(libs.constraintlayout)
implementation("com.google.firebase:firebase-auth:23.1.0")
implementation("com.google.firebase:firebase-core:21.1.1")
implementation("com.google.firebase:firebase-database:21.0.0")
implementation("com.google.firebase:firebase-firestore:25.1.1")
implementation("com.firebaseui:firebase-ui-auth:8.0.2")
implementation("com.google.firebase:firebase-storage:21.0.1")
implementation("com.google.firebase:firebase-messaging:24.1.0")
implementation("com.sun.mail:javax.mail:1.6.2")
implementation("com.github.bumptech.glide:glide:4.16.0")
annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
implementation("com.google.android.gms:play-services-auth:20.3.0")
implementation("androidx.recyclerview:recyclerview:1.2.1")
implementation("com.google.android.material:material:1.9.0")
implementation("com.firebaseui:firebase-ui-database:8.0.2")
implementation("com.google.android.gms:play-services-maps:18.1.0")
implementation("com.google.android.gms:play-services-location:21.0.1")
implementation("com.squareup.okhttp3:okhttp:4.9.3")
implementation("com.google.firebase:firebase-messaging:23.1.2")
