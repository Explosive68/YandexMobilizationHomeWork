package ru.illarionovroman.yandexmobilizationhomework.models;


import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import ru.illarionovroman.yandexmobilizationhomework.db.Contract;


public class HistoryItem implements Parcelable {

    private long mId = -1;
    private String mWord;
    private String mTranslation;
    private String mLanguageFrom;
    private String mLanguageTo;
    private String mDate = null;
    private int mIsFavorite = -1;

    public HistoryItem(String word, String translation, String languageFrom, String languageTo) {
        this.mWord = word;
        this.mTranslation = translation;
        this.mLanguageFrom = languageFrom;
        this.mLanguageTo = languageTo;
    }

    public HistoryItem(Cursor c) {
        this.mId = c.getLong(c.getColumnIndex(Contract.HistoryEntry._ID));
        this.mWord  = c.getString(c.getColumnIndex(Contract.HistoryEntry.WORD));
        this.mTranslation  = c.getString(c.getColumnIndex(Contract.HistoryEntry.TRANSLATION));
        this.mLanguageFrom  = c.getString(c.getColumnIndex(Contract.HistoryEntry.LANGUAGE_FROM));
        this.mLanguageTo  = c.getString(c.getColumnIndex(Contract.HistoryEntry.LANGUAGE_TO));
        this.mDate  = c.getString(c.getColumnIndex(Contract.HistoryEntry.DATE));
        this.mIsFavorite  = c.getInt(c.getColumnIndex(Contract.HistoryEntry.IS_FAVORITE));
    }

    protected HistoryItem(Parcel in) {
        this.mId = in.readLong();
        this.mWord = in.readString();
        this.mTranslation = in.readString();
        this.mLanguageFrom = in.readString();
        this.mLanguageTo = in.readString();
        this.mDate = in.readString();
        this.mIsFavorite = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeString(this.mWord);
        dest.writeString(this.mTranslation);
        dest.writeString(this.mLanguageFrom);
        dest.writeString(this.mLanguageTo);
        dest.writeString(this.mDate);
        dest.writeInt(this.mIsFavorite);
    }

    public static final Parcelable.Creator<HistoryItem> CREATOR = new Parcelable.Creator<HistoryItem>() {
        @Override
        public HistoryItem createFromParcel(Parcel in) {
            return new HistoryItem(in);
        }

        @Override
        public HistoryItem[] newArray(int size) {
            return new HistoryItem[size];
        }
    };

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        if (this.mId != -1) {
            cv.put(Contract.HistoryEntry._ID, this.mId);
        }
        cv.put(Contract.HistoryEntry.WORD, this.mWord);
        cv.put(Contract.HistoryEntry.TRANSLATION, this.mTranslation);
        cv.put(Contract.HistoryEntry.LANGUAGE_FROM, this.mLanguageFrom);
        cv.put(Contract.HistoryEntry.LANGUAGE_TO, this.mLanguageTo);
        if (this.mDate != null) {
            cv.put(Contract.HistoryEntry.DATE, this.mDate);
        }
        if (this.mIsFavorite != -1) {
            cv.put(Contract.HistoryEntry.IS_FAVORITE, this.mIsFavorite);
        }
        return cv;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getWord() {
        return mWord;
    }

    public void setWord(String mWord) {
        this.mWord = mWord;
    }

    public String getTranslation() {
        return mTranslation;
    }

    public void setTranslation(String mTranslation) {
        this.mTranslation = mTranslation;
    }

    public String getLanguageFrom() {
        return mLanguageFrom;
    }

    public void setLanguageFrom(String mLanguageFrom) {
        this.mLanguageFrom = mLanguageFrom;
    }

    public String getLanguageTo() {
        return mLanguageTo;
    }

    public void setLanguageTo(String mLanguageTo) {
        this.mLanguageTo = mLanguageTo;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public boolean getIsFavorite() {
        return mIsFavorite == 1;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.mIsFavorite = isFavorite ? 1 : 0;
    }
}