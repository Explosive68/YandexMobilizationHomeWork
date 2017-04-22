package ru.illarionovroman.yandexmobilizationhomework.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class DetectLanguageResponse {

    @SerializedName("code")
    @Expose
    private Integer mCode;
    @SerializedName("lang")
    @Expose
    private String mLang;

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

}