package com.unimib.triptales.adapters;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.unimib.triptales.ui.settings.fragment.AboutUsFragment;
import com.unimib.triptales.ui.settings.fragment.ChangeEmailFragment;
import com.unimib.triptales.ui.settings.fragment.ChangePasswordFragment;
import com.unimib.triptales.ui.settings.fragment.EditProfileFragment;
import com.unimib.triptales.ui.settings.fragment.PrivacyFragment;
import com.unimib.triptales.ui.settings.fragment.SettingsFragment;

public class SettingsAdapter extends FragmentStateAdapter {

    public SettingsAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        Bundle args = new Bundle();

        switch (position) {
            case 0:
                fragment = new SettingsFragment();
                break;
            case 1:
                fragment = new PrivacyFragment();
                break;
            case 2:
                fragment = new ChangeEmailFragment();
                break;
            case 3:
                fragment = new ChangePasswordFragment();
                break;
            case 4:
                fragment = new AboutUsFragment();
                break;
            case 5:
                fragment = new EditProfileFragment();
                break;
            default:
                fragment = new SettingsFragment();
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 6;
    }
}
