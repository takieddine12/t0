package com.z.p.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {BitmapModel.class},version = 1,exportSchema = false)
@TypeConverters({BitmapTypeConverter.class})
abstract public class BitmapDatabase extends RoomDatabase {

    private static BitmapDatabase instance;

    public abstract BitmapDao getBitmapDao();

    // Singleton pattern to ensure only one instance of the database is created
    public static synchronized BitmapDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            BitmapDatabase.class,
                            "bitmap.db" // Name of the database
                    )
                    .fallbackToDestructiveMigration() // Recreates the database if no migration strategy is provided
                    .build();
        }
        return instance;
    }
}
