package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;
    int itemPosition=0;

    private TextView mNoCrimeTextView;
    private Button mNoCrimeButton;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_crime_list,container,false);
        mCrimeRecyclerView=(RecyclerView)view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mNoCrimeTextView=(TextView)view.findViewById(R.id.no_crime_textview);
        mNoCrimeButton=(Button)view.findViewById(R.id.no_crime_add_button);
        mNoCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Crime crime=new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent=CrimePagerActivity.newIntent(getActivity(),crime.getmId());  //已经把CrimePagerActivity中用户输入的信息传送过来了
                startActivity(intent);
            }

        });

        updateUI();                                           //与RecyclerView关联起来
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);

        MenuItem subtitleItem=menu.findItem(R.id.show_subtitle);
        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_crime:
                Crime crime=new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                Intent intent=CrimePagerActivity.newIntent(getActivity(),crime.getmId());  //已经把CrimePagerActivity中用户输入的信息传送过来了
                startActivity(intent);
                return true;  //返回true表示传输完成

            case R.id.show_subtitle:
                mSubtitleVisible=!mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        CrimeLab crimeLab=CrimeLab.get(getActivity());
        int crimeSize=crimeLab.getmCrimes().size();
        String subtitle=getResources().getQuantityString(R.plurals.subtitle_plural,crimeSize,crimeSize);

        if (!mSubtitleVisible){
            subtitle=null;
        }
        AppCompatActivity activity=(AppCompatActivity)getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }


    private void updateUI() {
        CrimeLab crimeLab=CrimeLab.get(getActivity());
        List<Crime> crimes=crimeLab.getmCrimes();
        int crimeSize=crimeLab.getmCrimes().size();

      if (mAdapter==null){
          mAdapter=new CrimeAdapter(crimes);
          mCrimeRecyclerView.setAdapter(mAdapter);
      }else{
          mAdapter.setCrimes(crimes);
          mAdapter.notifyItemChanged(itemPosition);//提交改过的名称
      }

     if (crimeSize==0){
          mNoCrimeTextView.setVisibility(View.VISIBLE);
          mNoCrimeButton.setVisibility(View.VISIBLE);
      }else{
          mNoCrimeTextView.setVisibility(View.GONE);
          mNoCrimeButton.setVisibility(View.GONE);
      }
        updateSubtitle();
    }



    /**
     * 新建一个内部类，类似CrimeHolder
     */
    private class PoliceHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Crime mCrime;
        private Button mCallPolice;

        public PoliceHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.list_item_police_crime, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView)itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView)itemView.findViewById(R.id.crime_date);
            //绑定按钮
            mCallPolice = (Button)itemView.findViewById(R.id.call_police);
            //点击的时候显示一段文字
            mCallPolice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(),"已经联系了警察了",Toast.LENGTH_SHORT).show();
                }

            });
        }


        @Override
        public void onClick(View view) {

            Intent intent=CrimePagerActivity.newIntent(getActivity(),mCrime.getmId());
            itemPosition = getAdapterPosition();
            startActivity(intent);
        }

        public void bind(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getmTitle());
            //mDateTextView.setText(mCrime.getmDate().toString());
          String date=(String) DateFormat.format("EEEE, MMM dd, yyyy", mCrime.getmDate());
            mDateTextView.setText(date);
        }
    }




    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitleTextView;
        private TextView mDataTextView;
        private Crime mCrime;


        public void bind(Crime crime){
            mCrime=crime;
            mTitleTextView.setText(mCrime.getmTitle());
           // mDataTextView.setText(mCrime.getmDate().toString());  //转换成字符串
            String date=(String) DateFormat.format("EEEE, MMM dd, yyyy", mCrime.getmDate());
            mDataTextView.setText(date);
        }


        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView)itemView.findViewById(R.id.crime_title);
            mDataTextView = (TextView)itemView.findViewById(R.id.crime_date);
        }

        @Override
        public void onClick(View view) {
            Intent intent=CrimePagerActivity.newIntent(getActivity(),mCrime.getmId());
            itemPosition = getAdapterPosition();
            startActivity(intent);
        }

    }



    private class CrimeAdapter extends RecyclerView.Adapter{
        private List<Crime>mCrimes;
        public CrimeAdapter(List<Crime>crimes){
            mCrimes=crimes;
        }


        @Override
        public int getItemViewType(int position) {
            if(mCrimes.get(position).ismRequiresPolice() == true){
                return 1;
            }
            else{
                return 0;
            }
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
            //根据getViewType函数里面的设置的viewType的值，调用不同的holder
            if(viewType == 1){
                return new PoliceHolder(layoutInflater, parent);
            }
            else{
                return new CrimeHolder(layoutInflater, parent);
            }
        }



        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Crime crime=mCrimes.get(position);
            if (this.getItemViewType(position)==1){
                ((PoliceHolder)holder).bind(crime);
            }else{
                ((CrimeHolder)holder).bind(crime);
            }

        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes) {
            mCrimes=crimes;
        }
    }

}
