package com.kschenker.qlitt.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kschenker.qlitt.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class LeftNumberPadFragment extends Fragment
{


    public LeftNumberPadFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment__left_number_pad, container, false);
    }

}
