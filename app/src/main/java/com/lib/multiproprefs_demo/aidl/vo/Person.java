package com.lib.multiproprefs_demo.aidl.vo;

import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;

public class Person implements Parcelable {
    public static final int SEX_MALE = 1;
    public static final int SEX_FEMALE = 2;

    public int sno;
    public String name;
    public int sex;
    public int age;

    public Person() {
    }

    public static final Parcelable.Creator<Person> CREATOR = new
            Parcelable.Creator<Person>() {

                public Person createFromParcel(Parcel in) {
                    return new Person(in);
                }

                public Person[] newArray(int size) {
                    return new Person[size];
                }

            };

    private Person(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sno);
        dest.writeString(name);
        dest.writeInt(sex);
        dest.writeInt(age);
    }

    public void readFromParcel(Parcel in) {
        sno = in.readInt();
        name = in.readString();
        sex = in.readInt();
        age = in.readInt();
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH, "Person[ %d, %s, %d, %d ]", sno, name, sex, age);
    }

}