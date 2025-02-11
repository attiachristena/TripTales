package com.unimib.triptales.ui.login.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.unimib.triptales.R;

public class LoadingDialog {

    private FragmentActivity activity;
    private LoadingDialogFragment dialogFragment;

    LoadingDialog(FragmentActivity myActivity){
        activity = myActivity;
        dialogFragment = new LoadingDialogFragment();
    }

    void startLoadingDialog(){
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        dialogFragment.show(fragmentManager, "Loading_dialog");
    }

    void dismissDialog(){
        dialogFragment.dismiss();
    }
}
