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

public class NumberActivity extends AppCompatActivity {

    private SimpleCursorAdapter numListAdapter;
    private SQLiteDatabase dbWrite;
    private SQLiteDatabase dbRead;
    private ListView listViewNum;
    private Button btnAddNumber;
    private BlackList blackList;
    private Cursor cursor;
    private AdapterView.OnItemLongClickListener listViewItemLongClickListener=new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            new AlertDialog.Builder(NumberActivity.this).setTitle("提醒").setMessage("您确定要删除该项吗？")
                    .setNegativeButton("取消",null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cursor=numListAdapter.getCursor();
                            cursor.moveToPosition(position);
                            int itemId=cursor.getInt(cursor.getColumnIndex("_id"));
                            cursor.close();
                            dbWrite.delete("numberList", "_id=?", new String[]{itemId + ""});
                            cursor=dbRead.query("numberList",null,null,null,null,null,null);
                            numListAdapter.changeCursor(cursor);
                            listViewNum.setAdapter(numListAdapter);
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
        blackList=new BlackList(this);//创建新的黑名单数据库
        dbWrite=blackList.getWritableDatabase();
        dbRead=blackList.getReadableDatabase();
        cursor=dbRead.query("numberList",null,null,null,null,null,null);
        numListAdapter=new SimpleCursorAdapter(this,R.layout.num_list_cell,cursor,new String[]{"number"},new int[]{R.id.blackNum});
        listViewNum.setAdapter(numListAdapter);
        //设置长按删除点击事件
        listViewNum.setOnItemLongClickListener(listViewItemLongClickListener);
        //设置添加按钮点击事件
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
                                if (!etNum.getText().toString().equals("")) {

                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put("number", etNum.getText().toString());
                                        dbWrite.insert("numberList", null, contentValues);
                                }
                                cursor = dbRead.query("numberList", null, null, null, null, null, null);
                                numListAdapter.changeCursor(cursor);
                                listViewNum.setAdapter(numListAdapter);
                                etNum.setText("");
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

    @Override
    protected void onDestroy() {
        dbWrite.close();
        dbRead.close();
        if (cursor!=null){
            cursor.close();
        }
        super.onDestroy();
    }
}
