package com.google.developer.taskmaker;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.developer.taskmaker.data.DatabaseContract;
import com.google.developer.taskmaker.data.TaskAdapter;

public class MainActivity extends AppCompatActivity implements
        TaskAdapter.OnItemClickListener,
        View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    //New code from Andrew Keymolen Feature Tasks 4, 5, 6 and 7
    private static final Uri CONTENT_URI = Uri.parse("content://" + DatabaseContract.CONTENT_AUTHORITY + "/" + DatabaseContract.TABLE_TASKS);
    private static final int REFRESH = 1;
    private TaskAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private String prefSortBy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAdapter = new TaskAdapter(null);
        mAdapter.setOnItemClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        //New code from Andrew Keymolen Testing Tasks 1
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        //New code from Andrew Keymolen Feature Tasks 4, 5 and 6
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        refreshValuesFromContentProvider();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, REFRESH);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* Click events in Floating Action Button */
    @Override
    public void onClick(View v) {
        //New code from Andrew Keymolen Testing Tasks 1
        Intent intent = new Intent(this, AddTaskActivity.class);
        startActivityForResult(intent, REFRESH);
    }

    /* Click events in RecyclerView items */
    @Override
    public void onItemClick(View v, int position) {
        //New code from Andrew Keymolen Feature Tasks 7
        Intent intent = new Intent(this, TaskDetailActivity.class);
        intent.setData(ContentUris.withAppendedId(CONTENT_URI, mAdapter.getItem(position).id));
        startActivityForResult(intent, REFRESH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //New code from Andrew Keymolen Feature Tasks 7, 8 and 10
        if (requestCode == REFRESH) {
            if (resultCode == RESULT_OK) {
                refreshValuesFromContentProvider();
            }
        }
    }

    /* Click events on RecyclerView item checkboxes */
    @Override
    public void onItemToggled(boolean active, int position) {
        //New code from Andrew Keymolen Feature Tasks 6
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContract.TaskColumns.IS_COMPLETE, active ? 1 : 0);
        getContentResolver().update(ContentUris.withAppendedId(CONTENT_URI, mAdapter.getItem(position).id), contentValues, null, null);
    }

    //New code from Andrew Keymolen Feature Tasks 4, 5, 6, 7, 8 and 10
    private void refreshValuesFromContentProvider() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        prefSortBy = "";
        if (sharedPref.getString(getResources().getString(R.string.pref_sortBy_key), getResources().getString(R.string.pref_sortBy_key))
                .equals(getString(R.string.pref_sortBy_due))) {
            prefSortBy = DatabaseContract.DATE_SORT;
        }
        getLoaderManager().initLoader(0, null, this);
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getBaseContext(), CONTENT_URI,
                null, null, null, prefSortBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mAdapter.swapCursor(null);

    }
}
