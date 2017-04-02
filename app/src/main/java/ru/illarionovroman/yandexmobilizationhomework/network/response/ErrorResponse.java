package ru.illarionovroman.yandexmobilizationhomework.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ErrorResponse {

    @SerializedName("code")
    @Expose
    private Integer mCode;

    @SerializedName("message")
    @Expose
    private String mErrorMessage;

    public @ResponseErrorCodes
    Integer getCode() {
        return mCode;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }
}
