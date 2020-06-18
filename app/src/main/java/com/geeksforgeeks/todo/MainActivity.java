package com.geeksforgeeks.todo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import static android.widget.GridLayout.HORIZONTAL;

public class MainActivity extends AppCompatActivity {
    private DbHelper mHelper;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<ListItem> taskList;
    private RecyclerAdapter mAdapter;
    private ImageView mDone,mUndo,mDelete;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialiseUi();
        changeActionColor();
        updateUi();
    }

    private void initialiseUi()
    {
        mHelper = new DbHelper(this);
        mRecyclerView =findViewById(R.id.rv);
        mDone=findViewById(R.id.done);
        mUndo=findViewById(R.id.undo);
        mDelete=findViewById(R.id.delete);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
    }
    private void changeActionColor()
    {
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#66c2ff"));
        actionBar.setBackgroundDrawable(colorDrawable);
    }

    private void updateUi()
    {
         taskList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(TodoContract.TaskEntry.TABLE,
                new String[]{TodoContract.TaskEntry._ID, TodoContract.TaskEntry.COL_TASK_TITLE, TodoContract.TaskEntry.DONE},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(TodoContract.TaskEntry.COL_TASK_TITLE);
            int s=cursor.getColumnIndex(TodoContract.TaskEntry.DONE);
            ListItem m=new ListItem();
            m.title=cursor.getString(idx);
            m.status=cursor.getString(s);
            taskList.add(m);
        }

        cursor.close();
        if (mAdapter == null) {
           // Collections.reverse(taskList);
            mAdapter = new RecyclerAdapter(this,taskList);
            mRecyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter.notifyChange(taskList);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:
                final EditText taskEditText = new EditText(this);
                taskEditText.setHint("Start Typing..");
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add a new to-do")
                        .setMessage("")
                        .setView(taskEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String task = String.valueOf(taskEditText.getText());
                                SQLiteDatabase db = mHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(TodoContract.TaskEntry.DONE,"0");
                                values.put(TodoContract.TaskEntry.COL_TASK_TITLE, task);

                                db.insertWithOnConflict(TodoContract.TaskEntry.TABLE,
                                        null,
                                        values,
                                        SQLiteDatabase.CONFLICT_REPLACE);
                                db.close();
                                updateUi();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter == null) {
            // Collections.reverse(taskList);
            mAdapter = new RecyclerAdapter(this,taskList);
            mRecyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter.notifyChange(taskList);
        }
    }
}
