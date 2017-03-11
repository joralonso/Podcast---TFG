package es.usal.podcast.Utiles;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Clase para manejar la base de datos dentro del móvil
 * @author Jorge Alonso Merchán
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "Podcast.db";
    private final String SQL_CREATE_1 = "CREATE TABLE descargas (capituloid INTEGER NOT NULL PRIMARY KEY , podcastnombre TEXT, podcastid INTEGER, titulo TEXT, descripcion TEXT, url TEXT, duracion INTEGER, web TEXT, fecha TEXT, programaTitulo TEXT)";
    private final String SQL_CREATE_2 = "CREATE TABLE escuchando (capituloid INTEGER NOT NULL PRIMARY KEY, posicion INTEGER)";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_1);
        db.execSQL(SQL_CREATE_2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE descargas");
        db.execSQL("DROP TABLE escuchando");
        db.execSQL(SQL_CREATE_1);
        db.execSQL(SQL_CREATE_2);

    }

}
