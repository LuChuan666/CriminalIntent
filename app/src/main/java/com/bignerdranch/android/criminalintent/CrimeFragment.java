package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CrimeFragment extends Fragment {
    private Crime mCrime;
    private File mPhotoFile;

   // private static final String ARG_DELETE="delete_menu";

    private static final String ARG_CRIME_ID="crime_id";
    private static final String DIALOG_DATE="DialogDate";
    private static final String ADD_PICTURE="Add_picture";
    private static final int REQUEST_DATE=0;
    private static final int REQUSET_CONTACT=1;
    private static final int REQUSET_PHOTO=2;
    private static final int ADD_PHOTO = 3;
    /*添加组件*/

    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private ImageView mPhotoView;
    private ImageButton mPhotoButton;



    public static CrimeFragment newInstance(UUID crimeId){    //与CrimePagerActivity的数据传递
        Bundle args=new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId);

        CrimeFragment fragment=new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     UUID crimeId = (UUID)getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile=CrimeLab.get(getActivity()).getPhotoFile(mCrime);

        setHasOptionsMenu(true);

    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View v=inflater.inflate(R.layout.fragment_crime,container,false);

       mTitleField=(EditText)v.findViewById(R.id.crime_title);
       mTitleField.setText(mCrime.getmTitle());
       mTitleField.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setmTitle(s.toString());
           }

           @Override
           public void afterTextChanged(Editable s) {

           }
       });

        mDateButton=(Button)v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               FragmentManager manager=getFragmentManager();
              // DatePickerFragment dialog=new DatePickerFragment();
               DatePickerFragment dialog=DatePickerFragment.newInstance(mCrime.getmDate());//此目标为要接收数据的目标，即数据从DatePickerFragment发给CrimeFragment
               dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
               dialog.show(manager,DIALOG_DATE);
           }
       });


        mSolvedCheckBox=(CheckBox)v.findViewById(R.id.crime_solved);
       mSolvedCheckBox.setChecked(mCrime.ismSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setmSolved(isChecked);
            }
        });


        mReportButton=(Button)v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* Intent i =new Intent(Intent.ACTION_SEND);                       //隐式Intent
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT,getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.crime_report_suspect));
                i=Intent.createChooser(i,getString(R.string.send_report));
                startActivity(i);*/
                ShareCompat.IntentBuilder i = ShareCompat.IntentBuilder.from(getActivity());
                i.setType("text/plain");
                i.setText(getCrimeReport());
                i.setSubject(getString(R.string.send_report));
                i.startChooser();
            }
        });

        final Intent pickContact=new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton=(Button)v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact,REQUSET_CONTACT);
            }
        });
        if (mCrime.getmSuspect()!=null){
            mSuspectButton.setText(mCrime.getmSuspect());
        }


        PackageManager packageManager=getActivity().getPackageManager();             //检查有没有相机功能
        if (packageManager.resolveActivity(pickContact,PackageManager.MATCH_DEFAULT_ONLY)==null){
            mPhotoButton.setEnabled(false);
        }


        mPhotoButton=(ImageButton)v.findViewById(R.id.crime_camera);
        final Intent captureImage=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto=mPhotoFile!=null && captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri=FileProvider.getUriForFile(getActivity(),"com.bignerdranch.android.criminalintent.fileprovider",mPhotoFile);
                //调用FileProvider.getUriForFile 把本地文件路径转换为相机看的Uri形式。
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                List<ResolveInfo> cameraActivities=getActivity().getPackageManager().
                        queryIntentActivities(captureImage,PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity:cameraActivities){
                    getActivity().grantUriPermission(activity.activityInfo.packageName,uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivityForResult(captureImage,REQUSET_PHOTO);
                }
            }
        });
        mPhotoView=(ImageView)v.findViewById(R.id.crime_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPhotoFile == null || !mPhotoFile.exists()) {
                    mPhotoView.setImageDrawable(null);
                }else{
                FragmentManager manager=getFragmentManager();
                GlancePictureFragment dialog=GlancePictureFragment.newInstance(mPhotoFile);
                    dialog.setTargetFragment(CrimeFragment.this,ADD_PHOTO);
                    dialog.show(manager,ADD_PICTURE);
                }
            }
        });
        updatePhotoView();


        return v;

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode!= Activity.RESULT_OK){
            return;
        }
        if (requestCode==REQUEST_DATE){
            Date date=(Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setmDate(date);
            updateDate();
        }else if (requestCode == REQUSET_CONTACT && data != null) {
            Uri contactUri = data.getData();
            // Specify which fields you want your query to return values for
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME,

            };
            // Perform your query - the contactUri is like a "where" clause here
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);

            try {
                // Double-check that you actually got results
                if (c.getCount() == 0) {
                    return;
                }

                // Pull out the first column of the first row of data that is your suspect's name
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setmSuspect(suspect);
                mSuspectButton.setText(suspect);
            } finally {
                c.close();
            }
        }else if (requestCode==REQUSET_PHOTO){
            Uri uri=FileProvider.getUriForFile(getActivity(),"com.bignerdranch.android.criminalintent.fileprovider",mPhotoFile);
            getActivity().revokeUriPermission(uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }


    }



    private void updateDate() {
        mDateButton.setText(mCrime.getmDate().toString());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_remove, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_crime:
                CrimeLab.get(getActivity()).removeCrime(mCrime);
                if(getActivity().getSupportFragmentManager().getBackStackEntryCount() <= 0) {
                   getActivity().finish();
                 } else {
                 getActivity().getSupportFragmentManager().popBackStack();
                  }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    private String getCrimeReport(){
        String solvedString=null;
        if (mCrime.ismSolved()){
            solvedString=getString(R.string.crime_report_solved);
        }else {
            solvedString=getString(R.string.crime_report_unsolved);
        }
        String dateFormat="EEE,MMM dd";
        String dateString= DateFormat.format(dateFormat,mCrime.getmDate()).toString();

        String suspect=mCrime.getmSuspect();
        if (suspect==null){
            suspect=getString(R.string.crime_report_no_suspect);
        }else {
            suspect=getString(R.string.crime_report_suspect,suspect);
        }

        String report=getString(R.string.crime_report,mCrime.getmTitle(),dateString,solvedString,suspect);

        return report;
    }

    private void updatePhotoView(){
        if (mPhotoFile==null||!mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        }else {
            Bitmap bitmap=PictureUtils.getScaledBitmap(mPhotoFile.getPath(),getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    }
