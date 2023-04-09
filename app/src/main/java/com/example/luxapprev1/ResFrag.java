package com.example.luxapprev1;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ResFrag extends Fragment {
    private TextView luxView;
    private TextView dateView;
    private TextView timeView;
    private TextView locView;

    public ResFrag() {
        // Required empty public constructor
    }

    public static ResFrag newInstance() {
        return new ResFrag();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_res, container, false);
    }

    // 4 method below is used to setting text
    public void changeLuxText(String s) {
        luxView = (TextView) getView().findViewById(R.id.res_lux);
        luxView.setText(s);
    }

    public void changeDateText(String s) {
        dateView = (TextView) getView().findViewById(R.id.res_date);
        dateView.setText(s);
    }

    public void changeTimeText(String s) {
        timeView = (TextView) getView().findViewById(R.id.res_time);
        timeView.setText(s);
    }

    public void changeLocText(String s) {
        locView = (TextView) getView().findViewById(R.id.res_loc);
        locView.setText(s);
    }
}