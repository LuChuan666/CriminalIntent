package com.bignerdranch.android.criminalintent;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

public class GlancePictureFragment extends DialogFragment {

    private static final String ARG_FILE = "file";

    private ImageView mImage;

    // 由于文件比较大，所以将文件路径传入即可
    public static GlancePictureFragment newInstance(File file) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FILE, file);

        GlancePictureFragment fragment = new GlancePictureFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        File file = (File) getArguments().getSerializable(ARG_FILE);

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.glancepicture_fragment, null);
        mImage = (ImageView) v.findViewById(R.id.bigger_photo);

        Bitmap bitmap = PictureUtils.getScaledBitmap(
                file.getPath(), getActivity());
        mImage.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                //.setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
}
