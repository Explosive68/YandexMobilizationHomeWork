package ru.illarionovroman.yandexmobilizationhomework.ui.fragment.translation;

/**
 * Helper class to unite all necessary translation parameters
 */
public class TranslationParams {

    private String mWordToTranslate;
    private String mLanguageCodeFrom;
    private String mLanguageCodeTo;

    public TranslationParams(String wordToTranslate, String languageCodeFrom, String languageCodeTo) {
        mWordToTranslate = wordToTranslate;
        mLanguageCodeFrom = languageCodeFrom;
        mLanguageCodeTo = languageCodeTo;
    }

    public String getWordToTranslate() {
        return mWordToTranslate;
    }

    public String getLanguageCodeFrom() {
        return mLanguageCodeFrom;
    }

    public String getLanguageCodeTo() {
        return mLanguageCodeTo;
    }
}
