package com.jcedar.visinaas.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.jcedar.visinaas.R;
import com.jcedar.visinaas.adapter.SearchResultsCursorAdapter;
import com.jcedar.visinaas.provider.DataContract;

public class RecyclerActivity extends BaseActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = RecyclerActivity.class.getSimpleName();
    RecyclerView recyclerView;
    SearchResultsCursorAdapter resultsCursorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        recyclerView = (RecyclerView) findViewById( R.id.recyclerview );
        resultsCursorAdapter = new SearchResultsCursorAdapter(this);



        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(resultsCursorAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportLoaderManager().restartLoader(1, null, this);

        resultsCursorAdapter.setOnItemClickListener(new SearchResultsCursorAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(Cursor cursor) {
                String dob = cursor.getString(cursor.getColumnIndex(DataContract.Students.DATE_OF_BIRTH));
                Log.e(TAG, dob+" dob");
            }
        });
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new CursorLoader( this,
                DataContract.Students.CONTENT_URI,
                null,
                null,
                null,
                null
                );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        resultsCursorAdapter.swapCursor(data);
        recyclerView.setAdapter(resultsCursorAdapter);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        resultsCursorAdapter.swapCursor(null);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return NavigationDrawerFragment.MenuConstants.NAVDRAWER_ITEM_ONE_PARTICIPANT;
    }
}
