package com.unimib.triptales.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.unimib.triptales.model.CountryPolygon;

import java.util.List;

@Dao
public interface CountryPolygonDao {

    @Insert
    void insert(CountryPolygon countryPolygon);

    @Insert
    void insertAll(List<CountryPolygon> countryPolygonList);

    @Update
    void update(CountryPolygon countryPolygon);

    @Delete
    void delete(CountryPolygon countryPolygon);

    @Delete
    void deleteAll(List<CountryPolygon> countryPolygonList);

    @Query("SELECT * FROM CountryPolygon")
    List<CountryPolygon> getAll();

    @Query("SELECT * FROM CountryPolygon WHERE country_name = :countryName")
    CountryPolygon getByName(String countryName);

    /*//Recupero dei paesi colorati di un determinato utente
    @Query("SELECT * FROM CountryPolygon WHERE userId = :userId")
    List<CountryPolygon> getAllByUserId(int userId);*/

}
