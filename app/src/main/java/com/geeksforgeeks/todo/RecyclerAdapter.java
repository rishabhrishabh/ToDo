package com.geeksforgeeks.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ExampleViewHolder> {
    private Context mContext;
    private ArrayList<ListItem> mExampleList;
    private int intStatus;
    private DbHelper mHelper;
    private SQLiteDatabase db;
    private String titleTask;

    public RecyclerAdapter(Context context, ArrayList<ListItem> ExampleList)
    {
        super();
        this.mContext = context;
        this.mExampleList = ExampleList;
        mHelper = new DbHelper(mContext);
         db = mHelper.getWritableDatabase();
    }
    public void notifyChange(ArrayList<ListItem> mExampleList)
    {  //Collections.reverse(mExampleList);
        this.mExampleList=mExampleList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RecyclerAdapter.ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_todo, parent, false);
        return new ExampleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ExampleViewHolder holder, final int position) {
     final ListItem current=mExampleList.get(position);
     titleTask =current.title;
     String status=current.status;
     intStatus= Integer.valueOf(status);
     holder.title.setText(titleTask);

     if(holder.done.getVisibility() == View.VISIBLE && intStatus==0)
     {
      holder.done.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              holder.done.setVisibility(View.INVISIBLE);
              holder.delete.setVisibility(View.VISIBLE);
              holder.undo.setVisibility(View.VISIBLE);
              holder.title.setPaintFlags(holder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
              ContentValues cv = new ContentValues();
              String q="UPDATE "+TodoContract.TaskEntry.TABLE+" SET done = "+"'"+1+"' "+ "WHERE title = "+"'"+titleTask+"'";
              db.execSQL(q);
              current.status="1";
              notifyDataSetChanged();
              Toast.makeText(mContext,"Task completed!!",Toast.LENGTH_SHORT).show();
          }
      });
      }
        if( holder.undo.getVisibility() == View.VISIBLE && current.status.equals("1")  )
        {
            holder.undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    holder.done.setVisibility(View.VISIBLE);
                    holder.delete.setVisibility(View.INVISIBLE);
                    holder.undo.setVisibility(View.INVISIBLE);
                    holder.title.setPaintFlags(0);

                    ContentValues cv = new ContentValues();
                    cv.put(TodoContract.TaskEntry.DONE,"0");
                    String q="UPDATE "+TodoContract.TaskEntry.TABLE+" SET done = "+"'"+0+"' "+ "WHERE title = "+"'"+titleTask+"'";
                    db.execSQL(q);
                    current.status="0";
                    notifyDataSetChanged();
                    Toast.makeText(mContext,"Undo!!",Toast.LENGTH_SHORT).show();
                }
            });
        }
        if( holder.delete.getVisibility() == View.VISIBLE && current.status.equals("1"))
        {
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.done.setVisibility(View.VISIBLE);
                    holder.delete.setVisibility(View.INVISIBLE);
                    holder.undo.setVisibility(View.INVISIBLE);
                    holder.title.setPaintFlags(0);

                    mExampleList.remove(position);
                    db.delete(TodoContract.TaskEntry.TABLE,"title=? ",new String[]{titleTask});
                    current.status="0";
                    notifyDataSetChanged();

                    Toast.makeText(mContext,"Deleted!!",Toast.LENGTH_SHORT).show();
                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    public class ExampleViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView done,undo,delete;
        public ExampleViewHolder(@NonNull View itemView) {
            super(itemView);
             title=itemView.findViewById(R.id.task_title);
            done =itemView.findViewById(R.id.done);
            undo =itemView.findViewById(R.id.undo);
            delete =itemView.findViewById(R.id.delete);


        }
    }
}
