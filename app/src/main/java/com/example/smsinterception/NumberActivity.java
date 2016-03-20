package com.example.smsinterception;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class NumberActivity extends AppCompatActivity {

    private Button btnAddNumber;

    private SimpleCursorAdapter numListAdapter;
    private SQLiteDatabase dbWrite;
    private SQLiteDatabase dbRead;
    private ListView listViewNum;
    private AdapterView.OnItemLongClickListener listViewItemLongClickListener=new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            new AlertDialog.Builder(NumberActivity.this).setTitle("提醒").setMessage("您确定要删除该项吗？")
                    .setNegativeButton("取消",null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Cursor c=numListAdapter.getCursor();
                            c.moveToPosition(position);
                            int itemId=c.getInt(c.getColumnIndex("_id"));
                            dbWrite.delete("numberList","_id=?",new String[]{itemId+""});
                            Cursor cursor=dbRead.query("numberList",null,null,null,null,null,null);
                            numListAdapter.changeCursor(cursor);
                        }
                    }).show();
            return true;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number);

        listViewNum= (ListView) findViewById(R.id.listViewNum);
        btnAddNumber= (Button) findViewById(R.id.btnAddNumber);
        BlackList blackList=new BlackList(this);
        dbWrite=blackList.getWritableDatabase();
        dbRead=blackList.getReadableDatabase();
        numListAdapter=new SimpleCursorAdapter(this,R.layout.num_list_cell,null,new String[]{"number"},new int[]{R.id.blackNum});
        Cursor cursor=dbRead.query("numberList",null,null,null,null,null,null);
        numListAdapter.changeCursor(cursor);
        listViewNum.setOnItemLongClickListener(listViewItemLongClickListener);
        listViewNum.setAdapter(numListAdapter);

        btnAddNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View viewAdd;
                final EditText etNum;
                viewAdd=LayoutInflater.from(NumberActivity.this).inflate(R.layout.dialog_num,null);
                etNum=(EditText)viewAdd.findViewById(R.id.editNum);
                AlertDialog.Builder builder=new AlertDialog.Builder(NumberActivity.this);
                builder.setTitle("请输入内容")
                        .setView(viewAdd)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BlackList blackList = new BlackList(getApplicationContext());
                                SQLiteDatabase dbWrite = blackList.getWritableDatabase();
                                if (!etNum.getText().toString().equals("")) {
                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put("number", etNum.getText().toString());
                                    dbWrite.insert("numberList", null, contentValues);
                                    dbWrite.close();
                                }
                                SQLiteDatabase dbRead = blackList.getReadableDatabase();
                                Cursor cursor = dbRead.query("numberList", null, null, null, null, null, null);
                                numListAdapter.changeCursor(cursor);
                                listViewNum.setAdapter(numListAdapter);
                                etNum.setText("");
                                dbRead.close();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                etNum.setText("");
                            }
                        })
                        .show();
            }
        });
    }
}
