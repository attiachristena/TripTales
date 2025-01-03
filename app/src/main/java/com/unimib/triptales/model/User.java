package com.unimib.triptales.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.List;

import androidx.annotation.NonNull;

@Entity
public class User implements Parcelable {
    @ColumnInfo(name = "user_name")
    private String name;
    @ColumnInfo(name = "user_email")
    private String email;
    private String idToken;

    @PrimaryKey
    public int id;

    @ColumnInfo(name = "user_surname")
    public String surname;


    @ColumnInfo(name = "user_passowrd")
    public String password;
 

    public User(String name, String surname, String email, String idToken){
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.idToken = idToken;
    }

    public String getName() {
        return name;
    }

    public String getSurname(){return surname;}

    public void setSurname(String surname){ this.surname = surname;}

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                "surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", idToken='" + idToken + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeString(this.surname);
        parcel.writeString(this.email);
        parcel.writeString(this.idToken);
    }

    protected User(Parcel in){
        this.name = in.readString();
        this.surname = in.readString();
        this.email = in.readString();
        this.idToken = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel parcel) {
            return new User(parcel);
        }

        @Override
        public User[] newArray(int i) {
            return new User[i];
        }
    };
}

