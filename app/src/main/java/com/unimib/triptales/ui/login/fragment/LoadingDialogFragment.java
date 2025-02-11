package com.unimib.triptales.ui.login.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.unimib.triptales.R;

public class LoadingDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_dialog, container, false);
        VideoView videoView = view.findViewById(R.id.videoView);
        Uri videoUri = Uri.parse("android.resource://" + requireActivity().getPackageName() + "/" + R.raw.caricamento_app);
        videoView.setVideoURI(videoUri);

        videoView.setOnCompletionListener(mp -> videoView.start());
        videoView.start();

        return  view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(requireActivity().getLayoutInflater().inflate(R.layout.custom_dialog, null))
                .setCancelable(false);
        return builder.create();
    }
}
