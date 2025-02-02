package com.ratila.findmate;

import java.util.HashMap;
import java.util.Map;

public class Synonyms {
    // Словарь синонимов
    private static final Map<String, String> SYNONYMS = new HashMap<>();

    static {
        // Программирование
        SYNONYMS.put("кодирование", "программирование");
        SYNONYMS.put("код", "программирование");
        SYNONYMS.put("разработка", "программирование");
        SYNONYMS.put("прога", "программирование");
        SYNONYMS.put("девелопмент", "программирование");
        SYNONYMS.put("софт", "программирование");
        SYNONYMS.put("алгоритм", "программирование");
        SYNONYMS.put("скрипт", "программирование");
        SYNONYMS.put("программист", "программирование");
        SYNONYMS.put("кодер", "программирование");
        SYNONYMS.put("it", "программирование");
        SYNONYMS.put("айти", "программирование");
        SYNONYMS.put("java", "программирование");
        SYNONYMS.put("python", "программирование");
        SYNONYMS.put("javascript", "программирование");
        SYNONYMS.put("c++", "программирование");
        SYNONYMS.put("html", "программирование");
        SYNONYMS.put("css", "программирование");
        SYNONYMS.put("база данных", "программирование");
        SYNONYMS.put("sql", "программирование");
        SYNONYMS.put("бэкенд", "программирование");
        SYNONYMS.put("фронтенд", "программирование");
        SYNONYMS.put("веб", "программирование");
        SYNONYMS.put("приложение", "программирование");
        SYNONYMS.put("api", "программирование");

        // Дизайн
        SYNONYMS.put("графика", "дизайн");
        SYNONYMS.put("рисование", "дизайн");
        SYNONYMS.put("иллюстрация", "дизайн");
        SYNONYMS.put("ui", "дизайн");
        SYNONYMS.put("ux", "дизайн");
        SYNONYMS.put("интерфейс", "дизайн");
        SYNONYMS.put("веб-дизайн", "дизайн");
        SYNONYMS.put("анимация", "дизайн");
        SYNONYMS.put("фотошоп", "дизайн");
        SYNONYMS.put("figma", "дизайн");
        SYNONYMS.put("sketch", "дизайн");
        SYNONYMS.put("визуал", "дизайн");
        SYNONYMS.put("типографика", "дизайн");
        SYNONYMS.put("логотип", "дизайн");
        SYNONYMS.put("брендинг", "дизайн");
        SYNONYMS.put("арт", "дизайн");
        SYNONYMS.put("креатив", "дизайн");
        SYNONYMS.put("колористика", "дизайн");

        // Музыка
        SYNONYMS.put("муз", "музыка");
        SYNONYMS.put("гитара", "музыка");
        SYNONYMS.put("фортепиано", "музыка");
        SYNONYMS.put("пианино", "музыка");
        SYNONYMS.put("барабаны", "музыка");
        SYNONYMS.put("вокал", "музыка");
        SYNONYMS.put("пение", "музыка");
        SYNONYMS.put("бит", "музыка");
        SYNONYMS.put("трек", "музыка");
        SYNONYMS.put("композиция", "музыка");
        SYNONYMS.put("звук", "музыка");
        SYNONYMS.put("аранжировка", "музыка");
        SYNONYMS.put("сведение", "музыка");
        SYNONYMS.put("микширование", "музыка");
        SYNONYMS.put("диджей", "музыка");
        SYNONYMS.put("рок", "музыка");
        SYNONYMS.put("поп", "музыка");
        SYNONYMS.put("классика", "музыка");
        SYNONYMS.put("джаз", "музыка");
        SYNONYMS.put("электроника", "музыка");
        SYNONYMS.put("синтезатор", "музыка");
        SYNONYMS.put("аккорд", "музыка");
        SYNONYMS.put("ноты", "музыка");
    }

    // Метод для получения синонима
    public static String getSynonym(String word) {
        return SYNONYMS.getOrDefault(word, word); // Возвращает синоним или само слово, если синонима нет
    }
}
