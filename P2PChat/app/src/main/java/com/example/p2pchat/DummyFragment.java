package com.example.p2pchat;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


//TODO: remove this class with its xml
/**
 * A simple {@link Fragment} subclass.
 */
public class DummyFragment extends Fragment {


    public DummyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dummy, container, false);
    }

}
