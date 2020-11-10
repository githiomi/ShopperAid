package com.githiomi.onlineshoppingassistant.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.githiomi.onlineshoppingassistant.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JijiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JijiFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public JijiFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static JijiFragment newInstance() {
        JijiFragment fragment = new JijiFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_jiji, container, false);
    }
}