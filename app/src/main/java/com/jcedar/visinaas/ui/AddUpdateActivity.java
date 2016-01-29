package com.jcedar.visinaas.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jcedar.visinaas.R;
import com.jcedar.visinaas.helper.UIUtils;
import com.jcedar.visinaas.io.adapters.UpdateCursorAdapter;
import com.jcedar.visinaas.provider.DataContract;

public class AddUpdateActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    UpdateCursorAdapter updateCursorAdapter;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update);

        listView = (ListView) findViewById(android.R.id.list);
        listView.setItemsCanFocus(true);
        listView.setDividerHeight(0);
        listView.setVerticalScrollBarEnabled(false);
        updateCursorAdapter = new UpdateCursorAdapter(this, null, R.layout.list_item_update);
        listView.setAdapter( updateCursorAdapter );

        getSupportLoaderManager().initLoader(0, null, this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UIUtils.showToast(AddUpdateActivity.this, "Clicked on item with id "+id );
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_update, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if ( id == R.id.action_update ){
            startActivity( new Intent( this, AddActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return NavigationDrawerFragment.MenuConstants.NAVDRAWER_ITEM_DASHBOARD;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DataContract.StudentsChapter.CONTENT_URI;
        return new CursorLoader(
                this,
                uri,
                DataContract.StudentsChapter.PROJECTION_ALL,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        updateCursorAdapter.swapCursor( data );
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        updateCursorAdapter.swapCursor(null);
    }
}
