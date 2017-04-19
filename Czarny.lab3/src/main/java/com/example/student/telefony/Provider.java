package com.example.student.telefony;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class Provider extends ContentProvider //Provider bazy
{
    private BDHelper mBDHelper;
    private static final String ID = "com.example.student.telefony.Provider";
    public static final Uri ContentUri = Uri.parse("content://" + ID + "/" + BDHelper.TABLE_NAME);
    private static final int TABLE = 1;
    private static final int ROW = 2;
    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static
    {
        URI_MATCHER.addURI(ID, BDHelper.TABLE_NAME, TABLE);
        URI_MATCHER.addURI(ID, BDHelper.TABLE_NAME + "/#", ROW);
    }

    @Override //Wirtualna
    public String getType(@NonNull Uri uri)
    {
        return null;
    }

    @Override//Wirtualna
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) //usuwanie rekordu
    {
        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase database = mBDHelper.getWritableDatabase();
        int deleted = 0;

        switch(uriType)
        {
            case TABLE:
                deleted = database.delete(BDHelper.TABLE_NAME, selection, selectionArgs);
                break;

            case ROW:
                deleted = database.delete(BDHelper.TABLE_NAME, AddSelectionID(selection, uri), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return deleted;
    }

    @Override//Wirtualna
    public Uri insert(@NonNull Uri uri, ContentValues values) //wstawianie rekordu
    {
        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase database = mBDHelper.getWritableDatabase();
        long added = 0;

        switch(uriType)
        {
            case TABLE:
                added = database.insert(BDHelper.TABLE_NAME, null, values);
                break;

            default:
                throw new IllegalArgumentException("Nieznane Uri : "+uri);
        }

        getContext().getContentResolver().notifyChange(	uri, null);
        return Uri.parse(BDHelper.TABLE_NAME + "/" + added);
    }

    @Override//Wirtualna
    public boolean onCreate()
    {
        mBDHelper = new BDHelper(getContext());
        return false;
    }

    @Override//Wirtualna
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) //Kursor
    {
        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase database = mBDHelper.getWritableDatabase();
        Cursor curs = null;

        switch (uriType)
        {
            case TABLE:
                curs = database.query(false, BDHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder, null, null);
                break;

            case ROW:
                curs = database.query(false, BDHelper.TABLE_NAME, projection, AddSelectionID(selection, uri), selectionArgs, null, null, sortOrder, null, null);
                break;

            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }

        curs.setNotificationUri(getContext().getContentResolver(), uri);
        return curs;
    }

    private String AddSelectionID(String select, Uri uri) //Zwraca ID rekordu
    {
        if(select != null && !select.equals(""))
        {
            select = select + " and " + BDHelper.ID + "=" + uri.getLastPathSegment();
        }

        else
        {
            select = BDHelper.ID + "=" + uri.getLastPathSegment();
        }

        return select;
    }

    @Override//Wirtualna
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) //Aktualizacja
    {
        int uriType = URI_MATCHER.match(uri);
        SQLiteDatabase database = mBDHelper.getWritableDatabase();
        int actualised = 0;

        switch (uriType)
        {
            case TABLE:
                actualised = database.update(BDHelper.TABLE_NAME, values, selection, selectionArgs);
                break;

            case ROW:
                actualised = database.update(BDHelper.TABLE_NAME, values, AddSelectionID(selection, uri), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return actualised;
    }
}
