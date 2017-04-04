package ru.illarionovroman.yandexmobilizationhomework.util;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import ru.illarionovroman.yandexmobilizationhomework.R;


public class Utils {

    // Use LinkedHashMap to save items order
    private static LinkedHashMap<String,String> sLanguagesMap = new LinkedHashMap<>();

    // Lazy one-time initialization
    public static LinkedHashMap<String, String> getLanguagesMap(Context context) {
        if (sLanguagesMap.isEmpty()) {
            String[] rawLangArray = context.getResources()
                    .getStringArray(R.array.language_codes_names);
            LinkedHashMap<String, String> languagesMap = new LinkedHashMap<>(rawLangArray.length);

            for (String rawItem : rawLangArray) {
                String[] splittedItem = rawItem.split(AppConstants.LANGUAGE_SPLIT_SYMBOL);
                languagesMap.put(splittedItem[0], splittedItem[1]);
            }
            sLanguagesMap = languagesMap;
        }
        return sLanguagesMap;
    }

    public static String getLangNameByCode(Context context, String langCode) {
        LinkedHashMap<String, String> langsMap = getLanguagesMap(context);
        return langsMap.get(langCode);
    }

    public static String getLangCodeByName(Context context, String langName) {
        LinkedHashMap<String, String> langsMap = getLanguagesMap(context);
        if (!TextUtils.isEmpty(langName)) {
            for (Map.Entry<String, String> entry : langsMap.entrySet()) {
                if (entry.getValue().equals(langName)) {
                    return entry.getKey();
                }
            }
        }
        return "";
    }

    public static void parseRawGetLangsResponseToFile(Context context, String rawGetLangsResponse) {
        String[] splittedByComma = rawGetLangsResponse.split(",");
        Map<String, String> langsMap = new ArrayMap<>(splittedByComma.length);

        for (String unsplittedEntry : splittedByComma) {
            String entry[] = unsplittedEntry.split(":");
            String key = entry[0];
            String value = entry[1];
            langsMap.put(key, value);
        }

        try {
            File path = context.getExternalFilesDir(null);
            File file = new File(path, "langNamesAndCodes_Ru.txt");
            FileOutputStream stream = new FileOutputStream(file);

            for (ArrayMap.Entry<String, String> entry : langsMap.entrySet()) {
                String key = entry.getKey() + "\n";
                stream.write(key.getBytes());
            }
            String delimiter = "------" + "\n";
            stream.write(delimiter.getBytes());
            for (ArrayMap.Entry<String, String> entry : langsMap.entrySet()) {
                String val = entry.getValue() + "\n";
                stream.write(val.getBytes());
            }
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
