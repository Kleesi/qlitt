package com.kschenker.qlitt.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kschenker.qlitt.R;
import com.kschenker.views.DonutChartView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SpawnedItemFragment extends Fragment
{

    public SpawnedItemFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        DonutChartView donutChart = (DonutChartView)getActivity().findViewById(R.id.donut_chart_view__spawned_item_fragment__time_ring);
        if (donutChart != null)
        {
            donutChart.addSegment(new DonutChartView.Segment(0.4f, getResources().getColor(R.color.lightReddishGray), Color.BLACK));
            donutChart.addSegment(new DonutChartView.Segment(0.6f, getResources().getColor(R.color.extraLightReddishGray), Color.BLACK));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment__spawned_item, container, false);
    }


}
