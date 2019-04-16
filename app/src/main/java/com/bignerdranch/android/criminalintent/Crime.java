package com.bignerdranch.android.criminalintent;

import java.time.Year;
import java.util.Date;
import java.util.UUID;

public class Crime {
     private UUID mId;
     private  String mTitle;
     private Date mDate;
     private boolean mSolved;
     private boolean mRequiresPolice;
     private String mSuspect;



    public String getmSuspect() {
        return mSuspect;
    }

    public void setmSuspect(String mSuspect) {
        this.mSuspect = mSuspect;
    }
    public String getPhotoFilename(){                //添加获取文件名方法
        return "IMG_"+getmId().toString()+".jpg";
    }

    public void setmRequiresPolice(boolean mRequiresPolice) {
        this.mRequiresPolice = mRequiresPolice;
    }

    public boolean ismRequiresPolice() {
        return mRequiresPolice;
    }



    public Crime(UUID id ){
        mId=id;
        mDate=new Date();
    }

    public Crime(){
        this(UUID.randomUUID());
      /*   mId=UUID.randomUUID();
         mDate=new Date();*/
    }


    public UUID getmId() {
        return mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Date getmDate() {
        return mDate;
    }

    public void setmDate(Date mDate) {
        this.mDate = mDate;
    }

    public boolean ismSolved() {
        return mSolved;
    }

    public void setmSolved(boolean mSolved) {
        this.mSolved = mSolved;
    }


}
