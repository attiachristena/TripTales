package com.unimib.triptales.ui.intro.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.unimib.triptales.R;

import org.jetbrains.annotations.Nullable;

public class Intro3Fragment extends Fragment {

    public Intro3Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_intro3,container,false);
        ImageButton nextButtonIntro3=(ImageButton)view.findViewById(R.id.nextButtonIntro3);

        nextButtonIntro3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentTransaction fr=getChildFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_container,new Intro3Fragment());
                fr.commit();
            }
        });

        return view;
    }
}