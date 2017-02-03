package com.codepath.nytimessearch.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by etast on 2/2/17.
 */

public class SearchSettings implements Parcelable {
    private boolean isArtsFilterOn = false;
    private boolean isFashionFilterOn = false;
    private boolean isSportsFilterOn = false;
    private String mSortOrder;
    private Calendar mCalendar;
    private boolean isCalendarSet;
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    public String getSearchDate () {
        return mSimpleDateFormat.format(mCalendar.getTime());
    }

    public boolean isCalendarSet() {
        return isCalendarSet;
    }

    public void setCalendarSet(boolean calendarSet) {
        isCalendarSet = calendarSet;
    }

    public Calendar getmCalendar() {
        return mCalendar;
    }

    public void setmCalendar(Calendar mCalendar) {
        this.mCalendar = mCalendar;
    }

    public boolean isArtsFilterOn() {
        return isArtsFilterOn;
    }

    public void setArtsFilterOn(boolean artsFilterOn) {
        isArtsFilterOn = artsFilterOn;
    }

    public boolean isFashionFilterOn() {
        return isFashionFilterOn;
    }

    public void setFashionFilterOn(boolean fashionFilterOn) {
        isFashionFilterOn = fashionFilterOn;
    }

    public boolean isSportsFilterOn() {
        return isSportsFilterOn;
    }

    public void setSportsFilterOn(boolean sportsFilterOn) {
        isSportsFilterOn = sportsFilterOn;
    }

    public String getmSortOrder() {
        return mSortOrder;
    }

    public void setmSortOrder(String mSortOrder) {
        this.mSortOrder = mSortOrder;
    }

    public SearchSettings() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isArtsFilterOn ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFashionFilterOn ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isSportsFilterOn ? (byte) 1 : (byte) 0);
        dest.writeString(this.mSortOrder);
        dest.writeSerializable(this.mCalendar);
        dest.writeByte(this.isCalendarSet ? (byte) 1 : (byte) 0);
    }

    protected SearchSettings(Parcel in) {
        this.isArtsFilterOn = in.readByte() != 0;
        this.isFashionFilterOn = in.readByte() != 0;
        this.isSportsFilterOn = in.readByte() != 0;
        this.mSortOrder = in.readString();
        this.mCalendar = (Calendar) in.readSerializable();
        this.isCalendarSet = in.readByte() != 0;
    }

    public static final Creator<SearchSettings> CREATOR = new Creator<SearchSettings>() {
        @Override
        public SearchSettings createFromParcel(Parcel source) {
            return new SearchSettings(source);
        }

        @Override
        public SearchSettings[] newArray(int size) {
            return new SearchSettings[size];
        }
    };
}
