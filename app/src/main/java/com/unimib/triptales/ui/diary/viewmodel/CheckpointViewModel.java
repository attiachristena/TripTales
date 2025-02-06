package com.unimib.triptales.ui.diary.viewmodel;


import android.app.Application;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.unimib.triptales.R;
import com.unimib.triptales.model.Checkpoint;
import com.unimib.triptales.repository.checkpoint.CheckpointRepository;
import com.unimib.triptales.repository.checkpoint.ICheckpointRepository;
import com.unimib.triptales.source.checkpoint.BaseCheckpointLocalDataSource;
import com.unimib.triptales.source.checkpoint.BaseCheckpointRemoteDataSource;
import com.unimib.triptales.util.SharedPreferencesUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.os.Looper;
import android.util.Pair;


public class CheckpointViewModel extends ViewModel {

}