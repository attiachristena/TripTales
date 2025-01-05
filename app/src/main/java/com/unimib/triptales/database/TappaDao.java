package com.unimib.triptales.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.unimib.triptales.model.Tappa;

import java.util.List;

@Dao
public interface TappaDao {
    @Query("SELECT * FROM tappe ORDER BY data ASC")
    List<Tappa> getAllTappe();

    @Query("SELECT * FROM tappe WHERE id = :id")
    Tappa getTappaById(int id);

    @Query("SELECT COUNT(*) > 0 FROM tappe WHERE nome = :nome")
    boolean existsByNome(String nome);

    @Query("SELECT * FROM tappe WHERE nome LIKE '%' || :searchQuery || '%'")
    List<Tappa> searchTappe(String searchQuery);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertTappa(Tappa tappa);

    @Update
    void updateTappa(Tappa tappa);

    @Delete
    void deleteTappa(Tappa tappa);

    @Query("DELETE FROM tappe WHERE id IN (:ids)")
    void deleteTappeById(List<Integer> ids);

    @Query("SELECT COUNT(*) FROM tappe")
    int getTappeCount();

    @Query("SELECT * FROM tappe WHERE latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLon AND :maxLon")
    List<Tappa> getTappeInArea(double minLat, double maxLat, double minLon, double maxLon);

    @Query("DELETE FROM tappe")
    void deleteAll();
}
