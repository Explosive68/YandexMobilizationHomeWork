package ru.illarionovroman.yandexmobilizationhomework.network.response;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * POJO for getLangs query response <br>
 * <br>
 * Example of response:
 * <pre>
 * {
 *    "dirs": [
 *    "ru-en",
 *    "ru-pl",
 *    "ru-hu",
 *    ...
 *    ],
 *    "langs": {
 *    "ru": "русский",
 *    "en": "английский",
 *    "pl": "польский",
 *    ...
 *    }
 * }
 * </pre>
 */
public class SupportedLanguagesResponse {

    @SerializedName("dirs")
    @Expose
    private List<String> mDirs = new ArrayList<>();
    @SerializedName("langs")
    @Expose
    private List<String> mLangs;

    public List<String> getDirs() {
        return mDirs;
    }

    public void setDirs(List<String> mDirs) {
        this.mDirs = mDirs;
    }

    public List<String> getLangs() {
        return mLangs;
    }

    public void setLangs(List<String> langs) {
        this.mLangs = langs;
    }

}