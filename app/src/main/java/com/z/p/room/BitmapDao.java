package com.z.p.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;

@Dao
public interface BitmapDao {


    @Query("SELECT * FROM images ORDER BY imageID desc")
    Observable<List<BitmapModel>> getBitmapList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertBitmap(BitmapModel bitmapModel);

    @Query("DELETE FROM images")
    Completable deleteRecords();


}
