package ru.illarionovroman.yandexmobilizationhomework.network.responses;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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