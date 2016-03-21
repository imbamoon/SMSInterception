package com.example.smsinterception;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {

    private IntentFilter receiveFilter;
    private InterceptionReceiver interceptionReceiver;
    private SimpleCursorAdapter listAdapter;
    private SQLiteDatabase dbWrite;
    private SQLiteDatabase dbRead;
    private ListView listView;
    private BlackList blackList;
    private Cursor cursor;


    private AdapterView.OnItemLongClickListener listViewItemLongClickListener=new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            new AlertDialog.Builder(MainActivity.this).setTitle("提醒").setMessage("您确定要删除该项吗？")
                    .setNegativeButton("取消",null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cursor=listAdapter.getCursor();
                            cursor.moveToPosition(position);
                            int itemId=cursor.getInt(cursor.getColumnIndex("_id"));
                            cursor.close();
                            dbWrite.delete("blackList","_id=?",new String[]{itemId+""});
                            cursor=dbRead.query("blackList",null,null,null,null,null,null);
                            listAdapter.changeCursor(cursor);
                            listView.setAdapter(listAdapter);
                        }
                    }).show();
            return true;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView=(ListView)findViewById(R.id.listView);
        receiveFilter=new IntentFilter();
        receiveFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        interceptionReceiver =new InterceptionReceiver();
        registerReceiver(interceptionReceiver,receiveFilter);
        blackList=new BlackList(this);
        dbRead=blackList.getReadableDatabase();
        dbWrite=blackList.getWritableDatabase();
        cursor = dbRead.query("blackList", null, null, null, null, null, null);
        listAdapter=new SimpleCursorAdapter(this,R.layout.black_list_cell,cursor,new String[]{"address","date","body"},new int[]{R.id.tvNum,R.id.tvTime,R.id.tvBody});
        listView.setAdapter(listAdapter);
        listView.setOnItemLongClickListener(listViewItemLongClickListener);
    }

    @Override
    protected void onStop() {
        if (cursor!=null){
            cursor.close();
        }
        dbWrite.close();
        dbRead.close();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        dbWrite=blackList.getWritableDatabase();
        dbRead=blackList.getReadableDatabase();
    }

    @Override
    protected void onDestroy(){
        unregisterReceiver(interceptionReceiver);
        dbRead.close();
        dbWrite.close();
        super.onDestroy();
    }

    public boolean onCreateOptionsMenu(Menu menu){
        menu.add(Menu.NONE, Menu.FIRST, 1, "设置");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==Menu.FIRST){
            Intent intent=new Intent(this,EditActivity.class);
            startActivity(intent);
        }
        return false;
    }
}
