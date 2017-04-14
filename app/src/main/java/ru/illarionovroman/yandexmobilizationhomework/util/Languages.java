package ru.illarionovroman.yandexmobilizationhomework.util;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.illarionovroman.yandexmobilizationhomework.R;


@Singleton
public class Languages {

    private static final String LANGUAGE_SPLIT_SYMBOL = ":";

    private LinkedHashMap<String,String> mLanguagesMap = new LinkedHashMap<>();

    @Inject
    public Languages(Context appContext) {
        String[] rawLangArray = appContext.getResources()
                .getStringArray(R.array.language_codes_names);

        LinkedHashMap<String, String> languagesMap = new LinkedHashMap<>(rawLangArray.length);

        for (String rawItem : rawLangArray) {
            String[] splittedItem = rawItem.split(LANGUAGE_SPLIT_SYMBOL);
            languagesMap.put(splittedItem[0], splittedItem[1]);
        }

        orderMapByValue(languagesMap, String::compareTo);

        mLanguagesMap = languagesMap;
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

    public String getLangNameByCode(String langCode) {
        return mLanguagesMap.get(langCode);
    }

    public String getLangCodeByName(String langName) {
        if (!TextUtils.isEmpty(langName)) {
            for (Map.Entry<String, String> entry : mLanguagesMap.entrySet()) {
                if (entry.getValue().equals(langName)) {
                    return entry.getKey();
                }
            }
        }
        return "";
    }

    public LinkedHashMap<String, String> getLanguagesMap() {
        return mLanguagesMap;
    }
}
