package com.example.student.telefony;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends Activity //Do edytowania rekordu w bazie
{
    private long RowID;
    private EditText ProducerEdit;
    private EditText ModelEdit;
    private EditText AndroidEdit;
    private EditText WWWEdit;

    @Override //Wirtualna
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        ProducerEdit = (EditText)findViewById(R.id.producerEdit); //Dane rekordu
        ModelEdit = (EditText)findViewById(R.id.modelEdit);
        AndroidEdit = (EditText)findViewById(R.id.androidEdit);
        WWWEdit = (EditText)findViewById(R.id.wwwEdit);

        RowID = -1; //Id rekordu

        if(savedInstanceState != null)
        {
            RowID = savedInstanceState.getLong(BDHelper.ID);
        }

        else
        {
            Bundle Bundle1 = getIntent().getExtras();

            if(Bundle1 != null)
            {
                RowID = Bundle1.getLong(BDHelper.ID);
            }
        }

        if(RowID != -1)
        {
            FillFields();
        }
    }

    public void OnInsert(View view) //Zapisywanie rekordu do bazy
    {
        if(CheckData())
        {
            ContentValues data = new ContentValues(); //Dane rekordu
            data.put(BDHelper.Producer, ProducerEdit.getText().toString());
            data.put(BDHelper.MODEL, ModelEdit.getText().toString());
            data.put(BDHelper.ANDROID, AndroidEdit.getText().toString());
            data.put(BDHelper.WWW, WWWEdit.getText().toString());

            if(RowID == -1)
            {
                Uri NewUri = getContentResolver().insert(Provider.ContentUri, data);
                RowID = Integer.parseInt(NewUri.getLastPathSegment());
            }

            else
            {
                getContentResolver().update(ContentUris.withAppendedId(Provider.ContentUri, RowID), data, null, null);
            }

            setResult(RESULT_OK);
            finish();
        }

        else //Walidacja wypełnienia pól
        {
            Toast.makeText(this, getString(R.string.communicate), Toast.LENGTH_SHORT).show();
        }
    }

    public void Cancel(View view) //Zrezygnowanie z zapisywania
    {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void www(View view) //Przeglądarka
    {
        if(!WWWEdit.getText().toString().equals(""))
        {
            String Adres = WWWEdit.getText().toString();

            if(!Adres.startsWith("http://") && !Adres.startsWith("https://"))
            {
                Adres = "http://" + Adres;
            }

            Intent BrowserIntent = new Intent("android.intent.action.VIEW", Uri.parse(Adres));
            startActivity(BrowserIntent);
        }

        else //Walidacja wypełnienia pól
        {
            Toast.makeText(this, getString(R.string.communicate), Toast.LENGTH_SHORT).show();
        }
    }

    @Override //Wirtualna
    protected void onSaveInstanceState(Bundle outState) //Zapisuję instancję
    {
        super.onSaveInstanceState(outState);
        outState.putLong(BDHelper.ID, RowID);
    }

    private boolean CheckData() //Sprwadzanie danych dane
    {
        return !(ProducerEdit.getText().toString().equals("") || ModelEdit.getText().toString().equals("") || AndroidEdit.getText().toString().equals("") || WWWEdit.getText().toString().equals(""));
    }

    private void FillFields() //Wypełnia pola
    {
        String projekcja[] = { BDHelper.Producer, BDHelper.MODEL, BDHelper.ANDROID, BDHelper.WWW };
        Cursor kursorTel = getContentResolver().query(ContentUris.withAppendedId(Provider.ContentUri, RowID), projekcja, null, null, null);
        kursorTel.moveToFirst();
        int indeksKolumny = kursorTel .getColumnIndexOrThrow(BDHelper.Producer);
        String wartosc = kursorTel.getString(indeksKolumny);
        ProducerEdit.setText(wartosc);
        ModelEdit.setText(kursorTel.getString(kursorTel.getColumnIndexOrThrow(BDHelper.MODEL)));
        AndroidEdit.setText(kursorTel.getString(kursorTel .getColumnIndexOrThrow(BDHelper.ANDROID)));
        WWWEdit.setText(kursorTel.getString(kursorTel .getColumnIndexOrThrow(BDHelper.WWW)));
        kursorTel.close();
    }
}