package com.unimib.triptales.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.unimib.triptales.ui.diario.fragment.CheckListFragment;
import com.unimib.triptales.ui.diario.fragment.ObiettiviFragment;
import com.unimib.triptales.ui.diario.fragment.SpeseFragment;
import com.unimib.triptales.ui.diario.fragment.TappeFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new TappeFragment();
            case 1:
                return new SpeseFragment();
            case 2:
                return new ObiettiviFragment();
            case 3:
                return new CheckListFragment();
            default:
                return new TappeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
