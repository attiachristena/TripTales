package com.unimib.triptales.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.unimib.triptales.ui.diario.fragment.TasksFragment;
import com.unimib.triptales.ui.diario.fragment.GoalsFragment;
import com.unimib.triptales.ui.diario.fragment.ExpensesFragment;
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
                return new ExpensesFragment();
            case 2:
                return new GoalsFragment();
            case 3:
                return new TasksFragment();
            default:
                return new TappeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
