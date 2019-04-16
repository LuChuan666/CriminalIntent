package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity  extends AppCompatActivity {


    private static final String EXTRA_CRIME_ID="com.bignerdranch.android.criminalintent.crime_id";


    private ViewPager mViewPager;
    private Button mTofirst;
    private Button mTolast;
    private List<Crime> mCrimes;

    public static Intent newIntent(Context packageContext, UUID crimeId){    //与CrimeListActivity联系
        Intent intent =new Intent(packageContext,CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID,crimeId);
        return  intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID crimeId=(UUID)getIntent().getSerializableExtra(EXTRA_CRIME_ID);



        mViewPager=(ViewPager)findViewById(R.id.crime_view_pager);                    //和SingleFragmentActivity一样的
        mCrimes=CrimeLab.get(this).getmCrimes();
        FragmentManager fragmentManager=getSupportFragmentManager();//动态添加碎片

        mTofirst=(Button)findViewById(R.id.To_frist);
        mTolast=(Button)findViewById(R.id.To_last);

        mTofirst.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                mViewPager.setCurrentItem(0);
            }
        });
        mTolast.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                mViewPager.setCurrentItem(mCrimes.size() - 1);
            }
        });
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
               Crime crime=mCrimes.get(position);
               setButtonView(mViewPager.getCurrentItem());
               return CrimeFragment.newInstance(crime.getmId());
            }



            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {   //这个方法会在屏幕滚动过程中不断被调用。

            }

            @Override
            public void onPageSelected(int position) {  //这个方法有一个参数，代表哪个页面被选中。
                setButtonView(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {  //这个方法在手指操作屏幕的时候发生变化


            }
        });

        for (int i=0;i<mCrimes.size();i++){
            if (mCrimes.get(i).getmId().equals(crimeId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }

    }

    private void setButtonView(int position) {
        if (position == 0){
            mTofirst.setVisibility(View.INVISIBLE);
            mTolast.setVisibility(View.VISIBLE);
        }
        if (position == mCrimes.size() - 1){
            mTolast.setVisibility(View.INVISIBLE);
            mTofirst.setVisibility(View.VISIBLE);
        }
        if (position != 0 && position != mCrimes.size() - 1) {
            mTofirst.setVisibility(View.VISIBLE);
            mTolast.setVisibility(View.VISIBLE);
        }
    }
}
