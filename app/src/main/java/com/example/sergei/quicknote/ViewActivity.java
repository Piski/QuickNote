package com.example.sergei.quicknote;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ViewActivity extends AppCompatActivity {
    private String action;
    private String noteFilter;
    private TextView viewer;
    private static final int EDITOR_REQUEST_CODE = 12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewer = (TextView) findViewById(R.id.textView);
        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);
        if(uri == null) {
            finish();
        } else {
            setTitle(getString(R.string.edit_note));
            action = Intent.ACTION_VIEW;
            noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();

            // get data from db
            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_COLUMNS, noteFilter, null, null);
            cursor.moveToFirst();
            String oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
            viewer.setText(oldText);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(action.equals(Intent.ACTION_VIEW)) {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                finishViewing();
                break;
            case R.id.action_delete:
                deleteNote();
                break;
        }
        return true;
    }

    private void deleteNote() {
        getContentResolver().delete(NotesProvider.CONTENT_URI, noteFilter, null);
        Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void finishViewing() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        finishViewing();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data == null) {
            finishViewing();
        } else if(requestCode == EDITOR_REQUEST_CODE) {
            Intent intent = getIntent();
            Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);
            Cursor cursor = getContentResolver().query(uri, DBOpenHelper.ALL_COLUMNS, noteFilter, null, null);
            cursor.moveToFirst();
            String oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
            viewer.setText(oldText);
        }
    }
    public void openEditorForExistingNote(View view) {
        Intent intent = new Intent(ViewActivity.this, EditorActivity.class);
        Intent i = getIntent();
        Uri u = i.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);
        Uri uri = Uri.parse(NotesProvider.CONTENT_URI + "/" + u.getLastPathSegment());
        intent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);
    }
}
