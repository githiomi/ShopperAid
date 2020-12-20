package com.githiomi.onlineshoppingassistant.Fragments.App;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.githiomi.onlineshoppingassistant.R;

import butterknife.ButterKnife;

public class PrivacyPolicyFragment extends Fragment {

    public PrivacyPolicyFragment() {
        // Required empty public constructor
    }

    public static PrivacyPolicyFragment newInstance() {
        return new PrivacyPolicyFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View privacyPolicyView = inflater.inflate(R.layout.fragment_privacy_policy, container, false);

        // Butter knife binding widgets
        ButterKnife.bind(this, privacyPolicyView);

        return privacyPolicyView;

    }
}