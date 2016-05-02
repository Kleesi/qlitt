package com.kschenker.qlitt.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kschenker.qlitt.R;

/**
 * Created by Kevin Schenker on 02.05.2016.
 */
public class RoundResultsFragment extends Fragment
{
    public RoundResultsFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment__game_results, container, false);
    }
}