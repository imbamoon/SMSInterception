package com.example.smsinterception;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class WordsActivity extends AppCompatActivity {

    private Button btnAddWords;
    private SQLiteDatabase dbWrite;
    private SQLiteDatabase dbRead;
    private SimpleCursorAdapter wordListAdapter;
    private ListView listViewWord;
    private BlackList blackList;
    private Cursor cursor;
    private AdapterView.OnItemLongClickListener listViewItemLongClickListener=new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            new AlertDialog.Builder(WordsActivity.this).setTitle("提醒").setMessage("您确定要删除该项吗？")
                    .setNegativeButton("取消",null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cursor=wordListAdapter.getCursor();
                            cursor.moveToPosition(position);
                            int itemId=cursor.getInt(cursor.getColumnIndex("_id"));
                            cursor.close();
                            dbWrite.delete("wordList", "_id=?", new String[]{itemId + ""});
                            cursor=dbRead.query("wordList",null,null,null,null,null,null);
                            wordListAdapter.changeCursor(cursor);
                            listViewWord.setAdapter(wordListAdapter);
                        }
                    }).show();
            return true;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_words);

        listViewWord= (ListView) findViewById(R.id.listViewWord);
        btnAddWords= (Button) findViewById(R.id.btnAddWords);
        blackList=new BlackList(this);
        dbWrite=blackList.getWritableDatabase();
        dbRead=blackList.getReadableDatabase();
        cursor=dbRead.query("wordList",null,null,null,null,null,null);
        wordListAdapter=new SimpleCursorAdapter(this,R.layout.word_list_cell,cursor,new String[]{"word"},new int[]{R.id.blackWord});
        listViewWord.setAdapter(wordListAdapter);
        listViewWord.setOnItemLongClickListener(listViewItemLongClickListener);
        //添加按钮点击事件
        btnAddWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View viewAdd;
                final EditText etWord;
                viewAdd= LayoutInflater.from(WordsActivity.this).inflate(R.layout.dialog_word,null);
                etWord=(EditText)viewAdd.findViewById(R.id.editWord);
                AlertDialog.Builder builder = new AlertDialog.Builder(WordsActivity.this);
                builder.setTitle("请输入内容")
                        .setView(viewAdd)
                        .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!etWord.getText().toString().equals("")) {

                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("word", etWord.getText().toString());
                                    dbWrite.insert("wordList", null, contentValues);
                                }
                                cursor = dbRead.query("wordList", null, null, null, null, null, null);
                                wordListAdapter.changeCursor(cursor);
                                listViewWord.setAdapter(wordListAdapter);
                                etWord.setText("");
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                etWord.setText("");
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        dbRead.close();
        dbWrite.close();
        if (cursor!=null){
            cursor.close();
        }
        super.onDestroy();
    }
}

