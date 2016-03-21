package com.example.smsinterception;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.telephony.SmsMessage;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zyw on 2015/11/21.
 */
public class InterceptionReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean flag=false;//是否屏蔽短信，默认为false
        Object[] pdus=(Object[])intent.getExtras().get("pdus");
        //获取短信数据Object[]，其中每个都是一段短信
        for (Object pdu:pdus){
            SmsMessage sms=SmsMessage.createFromPdu((byte[]) pdu);
            //每一段短信是一个字节数组，构建成一个SMSMessage对象
            Date date=new Date(sms.getTimestampMillis());
            //获取发送时间
            String address=sms.getOriginatingAddress();
            //获取号码
            String body=sms.getMessageBody();
            //获取消息内容
            BlackList blackList=new BlackList(context);
            SQLiteDatabase dbRead=blackList.getReadableDatabase();
            Cursor cursorWord = dbRead.query("wordList", new String[]{"word"}, null, null, null, null, null);
            while (cursorWord.moveToNext()){
                String key=cursorWord.getString(cursorWord.getColumnIndex("word"));
                Pattern pattern=Pattern.compile(key);
                Matcher matcher=pattern.matcher(body);
                if (matcher.find()){
                    abortBroadcast();
                    flag=true;
                    break;
                }
            }//屏蔽关键字
            cursorWord.close();

            Cursor cursorNum = dbRead.query("numberList", new String[]{"number"}, null, null, null, null, null);
            while (cursorNum.moveToNext()){
                String key=cursorNum.getString(cursorNum.getColumnIndex("number"));//key就是在黑名单中的电话号
                if (address.equals(key)){
                    abortBroadcast();
                    flag=true;
                    break;
                }
            }//屏蔽电话号
            cursorNum.close();

            if (flag){//如果该短信被屏蔽，则放入blackList
                SQLiteDatabase dbWrite=blackList.getWritableDatabase();
                ContentValues contentValues=new ContentValues();
                contentValues.put("date",date.toString());
                contentValues.put("address",address);
                contentValues.put("body",body);
                dbWrite.insert("blackList",null,contentValues);
                dbWrite.close();
                flag=false;
            }
        }
    }
}
