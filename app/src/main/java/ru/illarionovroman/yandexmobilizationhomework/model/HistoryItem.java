package ru.illarionovroman.yandexmobilizationhomework.model;


import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import ru.illarionovroman.yandexmobilizationhomework.db.Contract;


public class HistoryItem implements Parcelable {
    
    public static final long UNSPECIFIED_ID = -1;

    private long mId = UNSPECIFIED_ID;
    private String mWord;
    private String mTranslation;
    private String mLanguageCodeFrom;
    private String mLanguageCodeTo;
    private String mDate = null;
    private int mIsFavorite = -1;
    
    // Specially for RxJava2, because nulls are forbidden
    public HistoryItem() {
    }

    public HistoryItem(String word, String translation, String languageFrom, String languageTo) {
        this.mWord = word;
        this.mTranslation = translation;
        this.mLanguageCodeFrom = languageFrom;
        this.mLanguageCodeTo = languageTo;
    }

    public HistoryItem(Cursor c) {
        this.mId = c.getLong(c.getColumnIndex(Contract.HistoryEntry._ID));
        this.mWord  = c.getString(c.getColumnIndex(Contract.HistoryEntry.WORD));
        this.mTranslation  = c.getString(c.getColumnIndex(Contract.HistoryEntry.TRANSLATION));
        this.mLanguageCodeFrom = c.getString(c.getColumnIndex(Contract.HistoryEntry.LANGUAGE_FROM));
        this.mLanguageCodeTo = c.getString(c.getColumnIndex(Contract.HistoryEntry.LANGUAGE_TO));
        this.mDate  = c.getString(c.getColumnIndex(Contract.HistoryEntry.DATE));
        this.mIsFavorite  = c.getInt(c.getColumnIndex(Contract.HistoryEntry.IS_FAVORITE));
    }

    protected HistoryItem(Parcel in) {
        this.mId = in.readLong();
        this.mWord = in.readString();
        this.mTranslation = in.readString();
        this.mLanguageCodeFrom = in.readString();
        this.mLanguageCodeTo = in.readString();
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
        dest.writeString(this.mLanguageCodeFrom);
        dest.writeString(this.mLanguageCodeTo);
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
        cv.put(Contract.HistoryEntry.LANGUAGE_FROM, this.mLanguageCodeFrom);
        cv.put(Contract.HistoryEntry.LANGUAGE_TO, this.mLanguageCodeTo);
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

    public String getTranslation() {
        return mTranslation;
    }

    public String getLanguageCodeFrom() {
        return mLanguageCodeFrom;
    }

    public String getLanguageCodeTo() {
        return mLanguageCodeTo;
    }

    public String getDate() {
        return mDate;
    }

    public boolean getIsFavorite() {
        return mIsFavorite == 1;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.mIsFavorite = isFavorite ? 1 : 0;
    }
}