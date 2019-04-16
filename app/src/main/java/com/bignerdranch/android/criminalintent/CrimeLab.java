package com.bignerdranch.android.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import database.CrimeDbSchema.CrimeBaseHelper;
import database.CrimeDbSchema.CrimeCursorWrapper;
import database.CrimeDbSchema.CrimeDbSchema;

public class CrimeLab {
    private static CrimeLab sCrimeLab;

    //private List<Crime> mCrimes;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context){
        if (sCrimeLab==null){
            sCrimeLab=new CrimeLab(context);
        }
        return sCrimeLab;
    }


    private CrimeLab(Context context){

        mContext=context.getApplicationContext();
        mDatabase=new CrimeBaseHelper(mContext).getWritableDatabase();

       // mCrimes = new ArrayList<>();        //mCrimes实列化
        /*for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setmTitle("Crime #" + i);
            crime.setmSolved(i%2==0);
            //第偶数个Crime设置为True
            if(i % 2 == 0)
                crime.setmRequiresPolice(true);
            else
                crime.setmRequiresPolice(false);
            mCrimes.add(crime);
        }*/
    }

    public void addCrime(Crime c){
      //  mCrimes.add(c);
        ContentValues values=getContentValues(c);
        mDatabase.insert(CrimeDbSchema.CrimeTable.NAME,null,values);
    }
    public void removeCrime(Crime c){

        mDatabase.delete(CrimeDbSchema.CrimeTable.NAME, "uuid = ?", new String[] {String.valueOf(c.getmId())});

    }

    public List<Crime>getmCrimes(){  //返回数据列表
    List<Crime> crimes=new ArrayList<>();
    CrimeCursorWrapper cursor=queryCrimes(null,null);
    try{
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            crimes.add(cursor.getCrime());
            cursor.moveToNext();
        }
    }finally {
        cursor.close();
    }
    return crimes;
    }

    public Crime getCrime(UUID id){  //返回带指定ID的Crime对象
       /* for(Crime crime:mCrimes){
            if (crime.getmId().equals(id)){
                return crime;
            }
        }*/
        CrimeCursorWrapper cursor=queryCrimes(CrimeDbSchema.CrimeTable.Cols.UUID+"=?",new String[]{id.toString()});
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        }
        finally {
            cursor.close();
        }

    }

    public File getPhotoFile(Crime crime){
        File fileDir=mContext.getFilesDir();
        return new File(fileDir,crime.getPhotoFilename());
    }


    public void updateCrime(Crime crime){
        String uuidString=crime.getmId().toString();
        ContentValues values=getContentValues(crime);
        mDatabase.update(CrimeDbSchema.CrimeTable.NAME,values,CrimeDbSchema.CrimeTable.Cols.UUID+"=?",
                new String[]{uuidString});
    }


    //private Cursor queryCrimes(String whereClause,String[] whereArgs){    //没有封装
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){    //封装到CrimeCursorWrapper类中了
        Cursor cursor=mDatabase.query(
            CrimeDbSchema.CrimeTable.NAME,null,whereClause,whereArgs,null,null,null);
        //return cursor;
        return new CrimeCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Crime crime){
        ContentValues values=new ContentValues();
        values.put(CrimeDbSchema.CrimeTable.Cols.UUID,crime.getmId().toString());
        values.put(CrimeDbSchema.CrimeTable.Cols.TITLE,crime.getmTitle());
        values.put(CrimeDbSchema.CrimeTable.Cols.DATE,crime.getmDate().getTime());
        values.put(CrimeDbSchema.CrimeTable.Cols.SOLVED,crime.ismSolved()?1:0);
        values.put(CrimeDbSchema.CrimeTable.Cols.SUSPECT,crime.getmSuspect());


        return values;
    }

}
