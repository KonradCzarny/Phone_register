package com.example.student.telefony;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> //Główna aktywność
{
    private SimpleCursorAdapter CursorAdapter;
    private ListView List;

    @Override //Wirtualna
    protected void onCreate(Bundle savedInstanceState) //Tworzenie listy rekordów
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List = (ListView) findViewById(R.id.data_list);
        FillFields();

        List.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        List.setMultiChoiceModeListener(CheckManyElements());

        List.setOnItemClickListener(new AdapterView.OnItemClickListener() //Adapter przy kliknięciu na pole
        {
            @Override //Wirtualna
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra(BDHelper.ID, l);
                startActivityForResult(intent, 0);
            }
        });
    }

    private void FillFields() //Wypełnianie pól
    {
        getLoaderManager().initLoader(0, null, this);
        String[] mappingFrom = new String[] { BDHelper.Producer, BDHelper.MODEL };
        int[] mappingTo = new int[] { R.id.producentText, R.id.modelText };
        CursorAdapter = new SimpleCursorAdapter(this, R.layout.list_row, null, mappingFrom, mappingTo, 0);
        List.setAdapter(CursorAdapter);
    }

    @Override //Wirtualna
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        String[] setSee = { BDHelper.ID, BDHelper.Producer, BDHelper.MODEL };
        CursorLoader CursorLoader = new CursorLoader(this, Provider.ContentUri, setSee, null, null, null);
        return CursorLoader;
    }

    @Override //Wirtualna
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        CursorAdapter.swapCursor(data);
    }

    @Override //Wirtualna
    public void onLoaderReset(Loader<Cursor> loader)
    {
        CursorAdapter.swapCursor(null);
    }

    private void CreateElement()
    {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(BDHelper.ID, (long) -1);
        startActivityForResult(intent, 0);
    }

    @Override //Wirtualna
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override//Wirtualna
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_list, menu);
        return true;
    }

    @Override//Wirtualna
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.add)
        {
            CreateElement();
        }

        return super.onOptionsItemSelected(item);
    }

    private AbsListView.MultiChoiceModeListener CheckManyElements() //Gdy następuje wybór wielu elementów
    {
        return new AbsListView.MultiChoiceModeListener()
        {
            @Override//Wirtualna
            public boolean onPrepareActionMode(ActionMode mode, Menu menu)
            {
                return false;
            }

            @Override//Wirtualna
            public void onDestroyActionMode(ActionMode mode) {}

            @Override//Wirtualna
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {}

            @Override//Wirtualna
            public boolean onCreateActionMode(ActionMode mode, Menu menu)
            {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.kontekst_list, menu);
                return true;
            }

            @Override //Wirtualna
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) //Zaznaczenie wpisu
            {
                if(item.getItemId() == R.id.kasuj_menu)
                {
                    long[] checked = List.getCheckedItemIds();

                    for(int i = 0; i < checked.length; i++)
                    {
                        getContentResolver().delete(ContentUris.withAppendedId(Provider.ContentUri, checked[i]), null, null);
                    }

                    return true;
                }

                return false;
            }
        };
    }
}