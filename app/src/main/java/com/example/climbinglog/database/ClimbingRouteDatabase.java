package com.example.climbinglog.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ClimbingRoute.class, RoutePicture.class, RouteVideo.class}, version = 8, exportSchema = false)
@TypeConverters(DateConverter.class)
public abstract class ClimbingRouteDatabase extends RoomDatabase {

    public abstract RouteDao routeDao();

    private static volatile ClimbingRouteDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                RouteDao dao = INSTANCE.routeDao();
            });
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE routes ADD COLUMN favorite INTEGER NOT NULL DEFAULT 0");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3,4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE routes ADD COLUMN who_climbed TEXT");
        }
    };

    static final Migration MIGRATION_4_5 = new Migration(4,5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE routes_new (id INTEGER NOT NULL DEFAULT 0, title TEXT, rating INTEGER NOT NULL DEFAULT 0, type_of_route INTEGER NOT NULL DEFAULT 0, location TEXT, description TEXT, date_climbed TEXT, photo_path TEXT, favorite INTEGER NOT NULL DEFAULT 0, who_climbed TEXT, PRIMARY KEY(id))");
            database.execSQL("INSERT INTO routes_new (id, title, rating, type_of_route, location, description, date_climbed, photo_path, favorite, who_climbed) SELECT id, title, rating, type_of_route, location, description, date_climbed, photo_path, favorite, who_climbed FROM routes");
            database.execSQL("DROP TABLE routes");
            database.execSQL("ALTER TABLE routes_new RENAME TO routes");
        }
    };

    static final Migration MIGRATION_5_6 = new Migration(5,6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE routes ADD COLUMN route_color TEXT");
        }
    };

    static final Migration MIGRATION_6_7 = new Migration(6,7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE route_pictures (id INTEGER NOT NULL DEFAULT 0, route_id INTEGER NOT NULL DEFAULT 0, photo_path TEXT, PRIMARY KEY(id))");
        }
    };

    static final Migration MIGRATION_7_8 = new Migration(7,8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE route_videos (id INTEGER NOT NULL DEFAULT 0, route_id INTEGER NOT NULL DEFAULT 0, video_path TEXT, PRIMARY KEY(id))");
        }
    };

    public static ClimbingRouteDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ClimbingRouteDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ClimbingRouteDatabase.class, "climbing_route_database")
                            .addCallback(sRoomDatabaseCallback)
                            .addMigrations(MIGRATION_2_3)
                            .addMigrations(MIGRATION_3_4)
                            .addMigrations(MIGRATION_4_5)
                            .addMigrations(MIGRATION_5_6)
                            .addMigrations(MIGRATION_6_7)
                            .addMigrations(MIGRATION_7_8)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
