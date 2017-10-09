package cat.xtec.ioc.eac2_2017s1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cat.xtec.ioc.eac2_2017s1.data.Contracte.Noticies;
/**
 * Created by Toni on 08/10/2017.
 */

public class AjudaBD extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "noticies_marca.db";

    public AjudaBD(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREAR_TAULA_NOTICIES = "CREATE TABLE " + Noticies.NOM_TAULA + " (" +
                Noticies._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Noticies.TITOL + " TEXT NOT NULL, " +
                Noticies.AUTOR + " TEXT NOT NULL, " +
                Noticies.DESCRIPCIO + " TEXT NOT NULL, " +
                Noticies.DATA_PUBLICACIO + " TEXT NOT NULL, " +
                Noticies.CATEGORIA + " TEXT NOT NULL, " +
                Noticies.ENLLAC + " TEXT NOT NULL, " +
                Noticies.THUMBNAIL + " TEXT NOT NULL " +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREAR_TAULA_NOTICIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Noticies.NOM_TAULA);
        onCreate(sqLiteDatabase);
    }

}

