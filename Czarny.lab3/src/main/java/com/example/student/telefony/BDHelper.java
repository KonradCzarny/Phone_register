package com.example.student.telefony;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BDHelper extends SQLiteOpenHelper //Do zarządzania bazą SQLite
{
    private Context mContext;
    public final static int Database_Version = 1;
    public final static String Database_Name = "baza_telefonow";
    public final static String TABLE_NAME = "telefony";
    public final static String ID = "_id";
    public final static String Producer = "producent";
    public final static String MODEL = "model";
    public final static String ANDROID = "android";
    public final static String WWW = "www";
    public final static String Create_database = "CREATE TABLE " + TABLE_NAME + "(" + ID + " integer primary key autoincrement, " + Producer + " text not null," + MODEL + " text not null," + ANDROID + " text not null," + WWW + " text);";
    private static final String Delete_database = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public BDHelper(Context context)
    {
        super(context, Database_Name, null, Database_Version);
        mContext = context;
    }

    @Override //Wirtualna
    public void onCreate(SQLiteDatabase db) //Tworzenie bazy
    {
        db.execSQL(Create_database);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) //Upgrade bazy do nowej wersji
    {

        db.execSQL(Delete_database); //Usuwa bazę
        onCreate(db); //Tworzy nowa
    }
}