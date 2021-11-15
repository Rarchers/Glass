package com.example.glass.database;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HistoryDao {
    @Query("select * from historybean")
    List<HistoryBean> getAllHistory();


    @Insert
    void insert(HistoryBean... historyBeans);

    @Update
    void update(HistoryBean... historyBeans);

    @Delete
    void delete(HistoryBean... historyBeans);

}
