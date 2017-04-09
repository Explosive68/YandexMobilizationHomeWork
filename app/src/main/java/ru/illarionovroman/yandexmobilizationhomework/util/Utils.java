package ru.illarionovroman.yandexmobilizationhomework.util;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
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

            orderMapByValue(languagesMap, String::compareTo);
            sLanguagesMap = languagesMap;
        }
        return sLanguagesMap;
    }

    private static <K,V> void orderMapByValue(LinkedHashMap<K,V> map,
                                              final Comparator<? super V> comp) {
        List<Map.Entry<K,V>> entries = new ArrayList<>(map.entrySet());

        Collections.sort(entries, (o1, o2) -> comp.compare(o1.getValue(), o2.getValue()));

        map.clear();
        for (Map.Entry<K,V> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
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
