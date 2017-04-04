package ru.illarionovroman.yandexmobilizationhomework.network.response;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TranslationResponse {

    @SerializedName("code")
    @Expose
    private Integer mCode;
    @SerializedName("lang")
    @Expose
    private String mLang;
    @SerializedName("text")
    @Expose
    private List<String> mTranslations = new ArrayList<>();

    public Integer getCode() {
        return mCode;
    }

    public void setCode(Integer code) {
        this.mCode = code;
    }

    public String getLang() {
        return mLang;
    }

    public void setLang(String lang) {
        this.mLang = lang;
    }

    public List<String> getTranslations() {
        return mTranslations;
    }

    public void setTranslations(List<String> translations) {
        this.mTranslations = translations;
    }

}